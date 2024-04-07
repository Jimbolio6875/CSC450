package edu.missouristate.controller;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.service.MastodonService;
import edu.missouristate.service.RedditPostsService;
import edu.missouristate.service.TumblrService;
import edu.missouristate.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

// this controller will be used to post from a single text box
@Controller
public class SocialMediaPostingController {

    String userInfo;

    @Autowired
    TumblrService tumblrService;

    @Autowired
    MastodonService mastodonService;

    @Autowired
    TwitterService twitterService;

    @Autowired
    RedditPostsService redditPostsService;


    // TODO this looks terrible clean this up
    // TODO add reddit and twitter

    //    ---------------------------MASTODON--------------------------------
    @GetMapping("/mastodon/auth")
    public RedirectView startMastodonAuth() {
        return new RedirectView(mastodonService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String code, HttpSession session) {

        session.setAttribute("accessToken", mastodonService.getAccessToken(code));

        return "redirect:/post-message";
    }

    @RequestMapping("/post-message")
    public ModelAndView showPostMessageForm(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("landing");
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken != null) {
            try {
                String userId = mastodonService.getUserId(accessToken);
                System.out.println("user id from access token: " + userId);

                List<Mastodon> post = mastodonService.getPostsByUserId(userId);
                Collections.reverse(post);

                modelAndView.addObject("posts", post);
            } catch (Exception e) {
                modelAndView.addObject("error", "Failed to get posts: " + e.getMessage());
            }
        } else {
            modelAndView.addObject("error", "No access token");
        }

        return modelAndView;
    }

//    -----------------------------------------------------------------------

//    -----------------------TUMBLR--------------------------

    @GetMapping("/tumblr/start-oauth")
    public String startOAuthProcess(RedirectAttributes redirectAttributes) {
        String authorizationUrl = tumblrService.getAuthorizationUrl();
        return "redirect:" + authorizationUrl;
    }

    @GetMapping("/tumblr/oauth-callback")
    public String oauthCallback(@RequestParam("oauth_verifier") String oauthVerifier, Model model) throws Exception {
        userInfo = tumblrService.getUserInfo(oauthVerifier);
        model.addAttribute("userInfo", userInfo);
        return "redirect:/tumblr/post-message";
    }

    @RequestMapping("/tumblr/post-message")
    public ModelAndView showPostCreationPage() {
        ModelAndView modelAndView = new ModelAndView("landing");

        try {
            tumblrService.updatePosts();
            List<Tumblr> userPosts = tumblrService.getPostsByBlog();
            modelAndView.addObject("posts", userPosts);
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to get posts" + e.getMessage());
        }

        return modelAndView;
    }


    @PostMapping("/submit-post")
    public ModelAndView submitPost(@RequestParam("message") String message,
                                   @RequestParam("subreddit") String subreddit,
                                   @RequestParam("title") String title,
                                   HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        boolean success = false;
        Mastodon mastodonPost = null;
        Tuple mastodonAccessToken = mastodonService.getLatestAccessToken();
        String mastroAccessToken = mastodonAccessToken.get(0, String.class);

//        if (mastodonAccessToken != null) {
//            // Attempt to post the message to Mastodon
//
////            mastodonPost.updateInfo(message);
//
//            mastodonService.updateOrCreateMastodonPost(mastroAccessToken, message);
//            mastodonPost = mastodonService.postMessageToMastodon(message, mastroAccessToken);
//
//            // Check if the post was successfully created and returned
//            if (mastodonPost != null) {
//                success = true; // Indicate success if we have a Mastodon post
//            } else {
//                // Handle the failure case
//                modelAndView.setViewName("error");
//                modelAndView.addObject("message", "Failed to post your message to Mastodon.");
//                return modelAndView;
//            }
//        } else {
//            // Handle the case where there's no access token available
//            modelAndView.setViewName("error");
//            modelAndView.addObject("message", "No access token available for Mastodon.");
//            return modelAndView;
//        }


//        // Handling Reddit posting
//        try {
//            Tuple redditAccessTokenTuple = redditPostsService.getLatestUser(); // Fetch the latest Reddit access token for the user
//            if (redditAccessTokenTuple != null) {
//                String redditAccessToken = redditAccessTokenTuple.get(0, String.class);
//                String fullName = redditPostsService.postToReddit(redditAccessToken, subreddit, title, message);
//                if (fullName == null || fullName.isEmpty()) {
//                    // Handle failure to post to Reddit.
//                    modelAndView.setViewName("error");
//                    modelAndView.addObject("message", "Failed to post your message to Reddit.");
//                    return modelAndView;
//                }
//
//                redditPostsService.updateOrCreateRedditPost(redditAccessToken, subreddit, title, message, fullName);
//
//            }
//        } catch (Exception e) {
//            modelAndView.setViewName("error");
//            modelAndView.addObject("message", "Failed to post your message to Reddit: " + e.getMessage());
//            return modelAndView;
//        }

        // Handling Twitter posting
//        try {
//            Tuple twitterToken = twitterService.getLatestUser();
//            if (twitterToken != null) {
//                String accessToken = twitterToken.get(0, String.class);
//                String accessTokenSecret = twitterToken.get(1, String.class);
//                twitterService.updateContent(accessToken, message, LocalDateTime.now(), accessTokenSecret);
//                success = twitterService.postTweet(message, accessToken, accessTokenSecret);
//                if (!success) {
//                    modelAndView.setViewName("error");
//                    modelAndView.addObject("message", "Failed to post your message to Twitter.");
//                    return modelAndView;
//                }
//            }
//        } catch (Exception e) {
//            modelAndView.setViewName("error");
//            modelAndView.addObject("message", "Failed to post your message to Twitter: " + e.getMessage());
//            return modelAndView;
//        }

        // Handling Tumblr posting
        try {
            // Assuming you have a method to fetch the latest Tumblr credentials
            Tuple tumblrCredentialsTuple = tumblrService.getLatestUser();
            if (tumblrCredentialsTuple != null) {
                String accessToken = tumblrCredentialsTuple.get(0, String.class);
                String tokenSecret = tumblrCredentialsTuple.get(1, String.class);
                String blogIdentifier = tumblrCredentialsTuple.get(2, String.class);

                // Assuming you have a method to post to Tumblr and it returns the post ID on success
                String postId = tumblrService.postToBlog(message);
                if (postId != null && !postId.isEmpty()) {
                    // Success! Update or create the Tumblr post record in the database
                    tumblrService.updateOrCreateTumblrPost(accessToken, tokenSecret, blogIdentifier, postId, message);
                    success = true; // Indicate success
                } else {
                    // Handle failure to post to Tumblr
                    modelAndView.setViewName("error");
                    modelAndView.addObject("message", "Failed to post your message to Tumblr.");
                    return modelAndView;
                }
            }
        } catch (Exception e) {
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Failed to post your message to Tumblr: " + e.getMessage());
            return modelAndView;
        }


        // If all posts succeed
        if (success) {
            modelAndView.setViewName("success");
            modelAndView.addObject("message", "Your message has been posted successfully to all platforms!");
        } else {
            // Handle the case where no tokens were found for any platform
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "No access tokens available for posting.");
        }

        return modelAndView;
    }

}
