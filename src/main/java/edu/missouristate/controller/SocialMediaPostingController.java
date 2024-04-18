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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

// this controller will be used to post from a single text box
@Controller
public class SocialMediaPostingController {

    // userInfo needed for tumblr usage
    String userInfo;

    @Autowired
    TumblrService tumblrService;

    @Autowired
    MastodonService mastodonService;

    @Autowired
    TwitterService twitterService;

    @Autowired
    RedditPostsService redditPostsService;


    //    ---------------------------MASTODON--------------------------------

    // when mastodon icon clicked redirect to auth url
    @GetMapping("/mastodon/auth")
    public RedirectView startMastodonAuth() {
        return new RedirectView(mastodonService.getAuthorizationUrl());
    }

    // after auth redirect to post message after setting session access token
    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String code, HttpSession session) {

        session.setAttribute("accessToken", mastodonService.getAccessToken(code));

        return "redirect:/login";
    }

    // gets mastodon posts for post history page
    // todo same thing with tumblr it doesn't update correctly
    // todo should rename the mapping if we fix all bugs because it's not accurate anymore
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

    // when tumblr icon clicked redirect to auth url
    @GetMapping("/tumblr/start-oauth")
    public String startOAuthProcess(RedirectAttributes redirectAttributes) {
        String authorizationUrl = tumblrService.getAuthorizationUrl();
        return "redirect:" + authorizationUrl;
    }

    // after authentication redirect to post message endpoint which updates posts and gets posts for post history
    // todo update feature for tumblr doesn't work meaning if you like something on the site it won't update on post history yet
    // todo should be easy to fix
    @GetMapping("/tumblr/oauth-callback")
    public String oauthCallback(@RequestParam("oauth_verifier") String oauthVerifier, HttpSession session) throws Exception {
        String userInfo = tumblrService.getUserInfo(oauthVerifier);
        session.setAttribute("userInfo", userInfo);

        tumblrService.updatePosts();
        session.setAttribute("postsUpdated", true);

        return "redirect:/landing";
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

    //    -----------------------------------------------------------------------

//    -----------------------POST MESSAGE TO ALL SITES--------------------------

    // handles submits for every social media site
    @PostMapping("/submit-post")
    public ModelAndView submitPost(@RequestParam("message") String message,
                                   @RequestParam("subreddit") String subreddit,
                                   @RequestParam("title") String title,
                                   @RequestParam(required = false) Boolean twitter,
                                   @RequestParam(required = false) Boolean tumblr,
                                   @RequestParam(required = false) Boolean reddit,
                                   @RequestParam(required = false) Boolean mastodon,
                                   HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();

        // Checks if sent over value is null and assigns true or false
        boolean isTwitterChecked = twitter != null && twitter;
        boolean isTumblrChecked = tumblr != null && tumblr;
        boolean isRedditChecked = reddit != null && reddit;
        boolean isMastodonChecked = mastodon != null && mastodon;
//        boolean existingMastodonToken = mastodonService.hasToken();
//        boolean existingTwitterToken = twitterService.hasToken();


        boolean success = false;
        Mastodon mastodonPost = null;


        if (isMastodonChecked) {
            Tuple mastodonAccessToken = mastodonService.getLatestAccessToken();
            String mastroAccessToken = mastodonAccessToken.get(0, String.class);

            // hanldes posting for mastodon
            // todo will put this in try catch after second sprint presentation don't wanna break anything rn
            if (mastodonAccessToken != null) {

//            mastodonPost.updateInfo(message);

                // database
                mastodonService.updateOrCreateMastodonPost(mastroAccessToken, message);

                // api POST
                mastodonPost = mastodonService.postMessageToMastodon(message, mastroAccessToken);

                if (mastodonPost != null) {
                    success = true;
                    mastodonService.cleanTable();
                } else {
                    modelAndView.setViewName("error");
                    modelAndView.addObject("message", "Failed to post your message to Mastodon.");
                    return modelAndView;
                }
            } else {
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "No access token available for Mastodon.");
                return modelAndView;
            }

        }

        if (isRedditChecked) {
            // handles posting for reddit
            try {
                Tuple redditAccessTokenTuple = redditPostsService.getLatestUser();
                if (redditAccessTokenTuple != null) {
                    String redditAccessToken = redditAccessTokenTuple.get(0, String.class);
                    String fullName = redditPostsService.postToReddit(redditAccessToken, subreddit, title, message);
                    if (fullName == null || fullName.isEmpty()) {
                        modelAndView.setViewName("error");
                        modelAndView.addObject("message", "Failed to post your message to Reddit.");
                        return modelAndView;
                    }

                    redditPostsService.updateOrCreateRedditPost(redditAccessToken, subreddit, title, message, fullName);
                    redditPostsService.cleanTable();

                }
            } catch (Exception e) {
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "Failed to post your message to Reddit: " + e.getMessage());
                return modelAndView;
            }
        }

        if (isTwitterChecked) {
            // handles posting for twiiter
            try {
                Tuple twitterToken = twitterService.getLatestUser();
                if (twitterToken != null) {
                    String accessToken = twitterToken.get(0, String.class);
                    String accessTokenSecret = twitterToken.get(1, String.class);
                    twitterService.updateContent(accessToken, message, LocalDateTime.now(), accessTokenSecret);
                    success = twitterService.postTweet(message, accessToken, accessTokenSecret);
                    if (!success) {
                        modelAndView.setViewName("error");
                        modelAndView.addObject("message", "Failed to post your message to Twitter.");
                        return modelAndView;
                    }
                    // Basically removes the rows that don't have any associated post information
                    // this would happen if they authorize many times
                    twitterService.cleanTable();
                }
            } catch (Exception e) {
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "Failed to post your message to Twitter: " + e.getMessage());
                return modelAndView;
            }
        }

        if (isTumblrChecked) {
            try {

                Tuple tumblrCredentialsTuple = tumblrService.getLatestUser();
                if (tumblrCredentialsTuple != null) {
                    String accessToken = tumblrCredentialsTuple.get(0, String.class);
                    String tokenSecret = tumblrCredentialsTuple.get(1, String.class);
                    String blogIdentifier = tumblrCredentialsTuple.get(2, String.class);

                    // api post
                    String postId = tumblrService.postToBlog(message);
                    if (postId != null && !postId.isEmpty()) {

                        // database
                        tumblrService.updateOrCreateTumblrPost(accessToken, tokenSecret, blogIdentifier, postId, message);
                        tumblrService.cleanTable();
                        success = true;
                    } else {

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
        }


//         handles posting for tumblr


        // check if every social media was successfully posted to
        if (success) {
            modelAndView.setViewName("success");
            modelAndView.addObject("message", "Your message has been posted successfully to all platforms!");
        } else {
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "No access tokens available for posting.");
        }

        // redirect back to landing after making post
        return new ModelAndView("redirect:/landing");
    }

}

