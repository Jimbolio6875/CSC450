package edu.missouristate.controller;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.dto.MastodonPostDTO;
import edu.missouristate.service.*;
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

    @Autowired
    CentralLoginService centralLoginService;


    //    ---------------------------MASTODON--------------------------------

    // when mastodon icon clicked redirect to auth url
    @GetMapping("/mastodon/auth")
    public RedirectView startMastodonAuth() {
        return new RedirectView(mastodonService.getAuthorizationUrl());
    }

    // after auth redirect to post message after setting session access token
    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String code, HttpSession session) {

        session.setAttribute("accessToken", mastodonService.getAccessToken(code, session));

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
        String userInfo = tumblrService.getUserInfo(oauthVerifier, session);
        session.setAttribute("userInfo", userInfo);

        tumblrService.updatePosts();
        session.setAttribute("postsUpdated", true);

        return "redirect:/landing";
    }

    @RequestMapping("/tumblr/post-message")
    public ModelAndView showPostCreationPage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("landing");
        Integer userId = (Integer) session.getAttribute("userId");  // Get user ID from session

        if (userId == null) {
            modelAndView.setViewName("redirect:/login");  // Redirect to login if userID is not found
            return modelAndView;
        }

        try {
            tumblrService.updatePosts();  // Assuming updatePosts method also needs to be user-specific eventually
            List<Tumblr> userPosts = tumblrService.getPostsByBlog(userId);  // Pass userId to the method
            modelAndView.addObject("posts", userPosts);
        } catch (Exception e) {
            modelAndView.addObject("error", "Failed to get posts: " + e.getMessage());
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
                                   HttpSession session, RedirectAttributes redirectAttrs, Integer userId) {


        StringBuilder statusMessage = new StringBuilder();
        ModelAndView modelAndView = new ModelAndView();

        // Checks if sent over value is null and assigns true or false
        boolean isTwitterChecked = twitter != null && twitter;
        boolean isTumblrChecked = tumblr != null && tumblr;
        boolean isRedditChecked = reddit != null && reddit;
        boolean isMastodonChecked = mastodon != null && mastodon;

        boolean twitterSuccess = true;
        boolean tumblrSuccess = true;
        boolean redditSuccess = true;
        boolean mastodonSuccess = true;

        if (isMastodonChecked) {
            try {
                Tuple mastodonAccessTokenTuple = mastodonService.getLatestAccessToken();
                if (mastodonAccessTokenTuple == null) {
                    modelAndView.setViewName("error");
                    modelAndView.addObject("message", "No access token available for Mastodon.");
                    statusMessage.append("Failed to post to Mastodon due to missing access token. ");
                    mastodonSuccess = false;
                } else {
                    userId = (Integer) session.getAttribute("userId");
                    String mastroAccessToken = mastodonAccessTokenTuple.get(0, String.class);

                    // API POST
                    MastodonPostDTO mastodonPost = mastodonService.postMessageToMastodon(message, mastroAccessToken);
                    if (!mastodonPost.getId().isEmpty()) {
                        // Database update
                        mastodonService.updateOrCreateMastodonPost(mastroAccessToken, message, userId, mastodonPost);
                        mastodonService.cleanTable(userId); // add userId
                        statusMessage.append("Successfully posted to Mastodon. ");
                    } else {
                        modelAndView.setViewName("error");
                        modelAndView.addObject("message", "Failed to post your message to Mastodon.");
                        statusMessage.append("Failed to post to Mastodon. ");
                        mastodonSuccess = false;
                    }
                }
            } catch (Exception e) {
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "Error posting to Mastodon: " + e.getMessage());
                statusMessage.append("Exception while posting to Mastodon: ").append(e.getMessage()).append(" ");
                mastodonSuccess = false;
            }
        }


        if (isRedditChecked) {
            try {
                Tuple redditAccessTokenTuple = redditPostsService.getLatestUser();
                if (redditAccessTokenTuple == null) {
                    modelAndView.setViewName("error");
                    modelAndView.addObject("message", "No Reddit access token available.");
                    statusMessage.append("Failed to post to Reddit due to missing access token. ");
                    redditSuccess = false;
                } else {
                    userId = (Integer) session.getAttribute("userId");
                    String redditAccessToken = redditAccessTokenTuple.get(0, String.class);
                    String fullName = redditPostsService.postToReddit(redditAccessToken, subreddit, title, message);
                    if (fullName == null || fullName.isEmpty() || fullName.equals("No ID found")) {
                        modelAndView.setViewName("error");
                        modelAndView.addObject("message", "Failed to post your message to Reddit: Invalid Subreddit.");
                        statusMessage.append("Failed to post to Reddit due to invalid Subreddit. ");
                        redditSuccess = false;
                    } else {
                        redditPostsService.updateContent(redditAccessToken, subreddit, title, message, fullName, userId);
                        redditPostsService.cleanTable(userId);
                        statusMessage.append("Successfully posted to Reddit. ");

                    }
                }
            } catch (Exception e) {
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "Failed to post your message to Reddit: " + e.getMessage());
                statusMessage.append("Exception while posting to Reddit: ").append(e.getMessage()).append(" ");
                redditSuccess = false;
            }
        }


        if (isTwitterChecked) {
            try {
                Tuple twitterToken = twitterService.getLatestUser();
                if (twitterToken == null) {
                    modelAndView.setViewName("error");
                    modelAndView.addObject("message", "No access token available for Twitter.");
                    twitterSuccess = false;
                } else {
                    userId = (Integer) session.getAttribute("userId");
                    String accessToken = twitterToken.get(0, String.class);
                    String accessTokenSecret = twitterToken.get(1, String.class);
                    boolean success = twitterService.postTweet(message, accessToken, accessTokenSecret);

                    if (success) {
                        twitterService.updateContent(accessToken, message, LocalDateTime.now(), accessTokenSecret, userId);
                        twitterService.cleanTable(userId);
                        twitterSuccess = true;
                    } else {
                        modelAndView.setViewName("error");
                        modelAndView.addObject("message", "Failed to post your message to Twitter.");
                        twitterSuccess = false;
                    }
                }
            } catch (Exception e) {
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "Error posting to Twitter: " + e.getMessage());
                twitterSuccess = false;
            }
        }


        if (isTumblrChecked) {
            try {
                Tuple tumblrCredentialsTuple = tumblrService.getLatestUser();
                if (tumblrCredentialsTuple == null) {
                    modelAndView.setViewName("error");
                    modelAndView.addObject("message", "No access token available for Tumblr.");
                    statusMessage.append("Failed to post to Tumblr due to missing access token. ");
                    tumblrSuccess = false;
                } else {
                    userId = (Integer) session.getAttribute("userId");
                    String accessToken = tumblrCredentialsTuple.get(0, String.class);
                    String tokenSecret = tumblrCredentialsTuple.get(1, String.class);
                    String blogIdentifier = tumblrCredentialsTuple.get(2, String.class);

                    // API post
                    String postId = tumblrService.postToBlog(message);
                    if (postId != null && !postId.isEmpty()) {
                        // Database update
                        tumblrService.updateOrCreateTumblrPost(accessToken, tokenSecret, blogIdentifier, postId, message, userId);
                        tumblrService.cleanTable(userId);
                        statusMessage.append("Successfully posted to Tumblr. ");
                    } else {
                        modelAndView.setViewName("error");
                        modelAndView.addObject("message", "Failed to post your message to Tumblr.");
                        statusMessage.append("Failed to post to Tumblr. ");
                        tumblrSuccess = false;
                    }
                }
            } catch (Exception e) {
                modelAndView.setViewName("error");
                modelAndView.addObject("message", "Error posting to Tumblr: " + e.getMessage());
                statusMessage.append("Exception while posting to Tumblr: ").append(e.getMessage()).append(" ");
                tumblrSuccess = false;
            }
        }


        // checks if every social media was successfully posted to
        if (twitterSuccess && tumblrSuccess && mastodonSuccess && redditSuccess) {
            redirectAttrs.addFlashAttribute("message", "Your message has been posted successfully!");
            modelAndView.setViewName("redirect:/landing");
        } else {
            redirectAttrs.addFlashAttribute("message", statusMessage.toString());
            modelAndView.setViewName("redirect:/landing");
        }

        // redirect back to landing after making post
        return modelAndView;
    }

}

