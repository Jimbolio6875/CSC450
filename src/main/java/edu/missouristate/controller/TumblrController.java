package edu.missouristate.controller;

import edu.missouristate.service.TumblrService;
import edu.missouristate.domain.Tumblr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


// TODO when we get the post controller working the separate social media controllers won't be needed (probably)
@Controller
public class TumblrController {


    private final TumblrService tumblrService;

    String userInfo;

    @Autowired
    public TumblrController(TumblrService tumblrService) {
        this.tumblrService = tumblrService;
    }

//    @GetMapping("/tumblr/start-oauth")
//    public String startOAuthProcess(RedirectAttributes redirectAttributes) {
//        String authorizationUrl = tumblrService.getAuthorizationUrl();
//        return "redirect:" + authorizationUrl;
//    }
//
//    @GetMapping("/tumblr/oauth-callback")
//    public String oauthCallback(@RequestParam("oauth_verifier") String oauthVerifier, Model model) throws Exception {
//        userInfo = tumblrService.getUserInfo(oauthVerifier);
//        model.addAttribute("userInfo", userInfo);
//        return "redirect:/tumblr/create-post";
//    }

//    @PostMapping("/tumblr/post-to-tumblr")
//    public String postToTumblr(@RequestParam("postContent") String postContent, Model model) throws Exception {
//        tumblrService.postToBlog(postContent);
//        model.addAttribute("message", "Successfully posted to Tumblr!");
//        return "tumblr/post-success";
//    }

    // bad one
//    @GetMapping("/tumblr/create-post")
//    public String showPostCreationPage() {
//        return "/tumblr/create-post";
//    }

//    @RequestMapping("/tumblr/create-post")
//    public ModelAndView showPostCreationPage() {
//        ModelAndView modelAndView = new ModelAndView("tumblr/create-post");
//
//        try {
//            tumblrService.updatePosts();
//            List<Tumblr> userPosts = tumblrService.getPostsByBlog();
////            Collections.reverse(userPosts);
//            modelAndView.addObject("posts", userPosts);
//        } catch (Exception e) {
//            modelAndView.addObject("error", "Failed to get posts" + e.getMessage());
//        }
//
//        return modelAndView;
//    }

}
