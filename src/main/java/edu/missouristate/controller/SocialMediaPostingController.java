package edu.missouristate.controller;

import edu.missouristate.domain.Tumblr;
import edu.missouristate.service.MastodonService;
import edu.missouristate.service.RedditPostsService;
import edu.missouristate.service.TumblrService;
import edu.missouristate.service.TwitterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import edu.missouristate.domain.Mastodon;

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

//    ---------------------SUBMIT POSTS------------------------
    // has to be its own thing cause a form can only submit to one endpoint at a time
    // this means all service methods that post will go in here somewhere

    @PostMapping("/submit-post")
    public ModelAndView submitPostToMastodon(@RequestParam("message") String message, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken == null) {
            modelAndView.setViewName("redirect:/");
            modelAndView.addObject("error", "No access token available. Please authorize first.");
            return modelAndView;
        }

        try {
//            postMessageToMastodon(message, accessToken);
            mastodonService.savePost(mastodonService.postMessageToMastodon(message, accessToken));
            tumblrService.postToBlog(message);
            modelAndView.setViewName("result");
            modelAndView.addObject("message", "Message posted to Mastodon successfully!");
//            socialMediaAccountService.saveSocialMediaAccount(3, "Mastodon", accessToken);
        } catch (Exception e) {
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "Failed to post message to Mastodon: " + e.getMessage());
        }

        return modelAndView;
    }
    
}
