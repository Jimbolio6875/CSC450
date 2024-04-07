package edu.missouristate.controller;

import edu.missouristate.service.MastodonService;
import edu.missouristate.service.SocialMediaAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

// TODO when we get the post controller working the separate social media controllers won't be needed (probably)
@Controller
public class MastodonController {

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private MastodonService mastodonService;

//    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"; // will prob need to change


    // logic has been moved to service layer

//    @GetMapping("/mastodon/auth")
//    public RedirectView startMastodonAuth() {
//
//        return new RedirectView(mastodonService.getAuthorizationUrl());
//    }

    // set session access token and redirect to post-message after authorization
//    @GetMapping("/callback")
//    public String handleCallback(@RequestParam("code") String code, HttpSession session) {
//
//        session.setAttribute("accessToken", mastodonService.getAccessToken(code));
//
//        return "redirect:/post-message";
//    }

    // bad one
//    @GetMapping("/callback")
//    public String handleCallback(@RequestParam("code") String code, HttpSession session) {
//        String accessToken = getAccessToken(code);
//
//        String userId = getUserId(accessToken);
//        getUserPosts(userId, accessToken);
//
//        session.setAttribute("accessToken", accessToken);
//
//        return "redirect:/post-message";
//    }


    // load posting form and show post history
//    @RequestMapping("/post-message")
//    public ModelAndView showPostMessageForm(HttpSession session) {
//        ModelAndView modelAndView = new ModelAndView("mastodon/post-message");
//        String accessToken = (String) session.getAttribute("accessToken");
//
//        if (accessToken != null) {
//            try {
//                String userId = mastodonService.getUserId(accessToken);
//                System.out.println("user id from access token: " + userId);
//
////                List<String> userPosts = getUserPosts(userId, accessToken);
////                List<String> userPosts = mastodonService.getPostContent();
////                List<Integer> userPostFavourites = getUserPostFavourites(userId, accessToken);
////                List<Integer> userPostFavourites = mastodonService.getPostFavourites();
//
//                List<Mastodon> post = mastodonService.getPostsByUserId(userId);
//                Collections.reverse(post);
////                modelAndView.addObject("posts", userPosts);
////                modelAndView.addObject("postFavourites", userPostFavourites);
//
//                modelAndView.addObject("posts", post);
//            } catch (Exception e) {
//                modelAndView.addObject("error", "Failed to get posts: " + e.getMessage());
//            }
//        } else {
//            modelAndView.addObject("error", "No access token");
//        }
//
//        return modelAndView;
//    }

    // submit post to mastodon
//    @PostMapping("/submit-post")
//    public ModelAndView submitPostToMastodon(@RequestParam("message") String message, HttpSession session) {
//        ModelAndView modelAndView = new ModelAndView();
//        String accessToken = (String) session.getAttribute("accessToken");
//
//        if (accessToken == null) {
//            modelAndView.setViewName("redirect:/");
//            modelAndView.addObject("error", "No access token available. Please authorize first.");
//            return modelAndView;
//        }
//
//        try {
////            postMessageToMastodon(message, accessToken);
//            mastodonService.savePost(mastodonService.postMessageToMastodon(message, accessToken));
//            modelAndView.setViewName("result");
//            modelAndView.addObject("message", "Message posted to Mastodon successfully!");
//            socialMediaAccountService.saveSocialMediaAccount(3, "Mastodon", accessToken);
//        } catch (Exception e) {
//            modelAndView.setViewName("error");
//            modelAndView.addObject("error", "Failed to post message to Mastodon: " + e.getMessage());
//        }
//
//        return modelAndView;
//    }
}
