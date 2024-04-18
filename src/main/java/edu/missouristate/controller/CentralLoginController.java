package edu.missouristate.controller;

import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.Tumblr;
import edu.missouristate.dto.GenericResponse;
import edu.missouristate.dto.LoginResponse;
import edu.missouristate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CentralLoginController {

    @Autowired
    CentralLoginService centralLoginService;

    @Autowired
    TwitterService twitterService;
    @Autowired
    RedditPostsService redditPostsService;
    @Autowired
    MastodonService mastodonService;
    @Autowired
    TumblrService tumblrService;


    @GetMapping(value = {"/", "/home"})
    public String getIndex() {
        return "home";
    }

    @GetMapping("/socials")
    public String getSocial() {
        return "socials";
    }


    @GetMapping("/login")
    public String getLoginPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) {
            return "login";
        } else {
            model.addAttribute("hasTwitterToken", twitterService.hasToken());
            model.addAttribute("hasTumblrToken", tumblrService.hasToken());
            model.addAttribute("hasRedditToken", redditPostsService.hasToken());
            model.addAttribute("hasMastodonToken", mastodonService.hasToken());
            return "landing";
        }
    }

    @GetMapping("/register")
    public String getRegisterPage(HttpSession session) {
        if (session.getAttribute("username") == null || session.getAttribute("username").equals("")) {
            return "register";
        } else {
            return "landing";
        }
    }

    //TODO: secure, actually check userId
    @GetMapping("/landing")
    public String getLandingPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null || session.getAttribute("username").equals("")) {
            return "redirect:/login";
        } else {
            model.addAttribute("hasTwitterToken", twitterService.hasToken());
            model.addAttribute("hasTumblrToken", tumblrService.hasToken());
            model.addAttribute("hasRedditToken", redditPostsService.hasToken());
            model.addAttribute("hasMastodonToken", mastodonService.hasToken());

            if (Boolean.TRUE.equals(session.getAttribute("postsUpdated"))) {
                try {
                    List<Tumblr> userPosts = tumblrService.getPostsByBlog();
                    model.addAttribute("posts", userPosts);
                    session.removeAttribute("postsUpdated");
                } catch (Exception e) {
                    model.addAttribute("error", "Failed to get posts: " + e.getMessage());
                }
            }
            
            if (session.getAttribute("userInfo") != null) {
                model.addAttribute("userInfo", session.getAttribute("userInfo"));
                session.removeAttribute("userInfo");
            }

            return "landing";
        }
    }


    //TODO: Use responses and dto's instead of loose objects being passed everywhere
    @ResponseBody
    @PostMapping("/register")
    public GenericResponse register(@RequestBody CentralLogin login, HttpSession session) {
        boolean registered = centralLoginService.register(login);
        GenericResponse response = new GenericResponse();
        if (registered) {
            response.setMessage("Registration success");
            response.setMessageType("success");
            return response;
        } else {
            response.setMessage("Registration failed. Please select a new username");
            response.setMessageType("danger");
            return response;
        }
    }

    @ResponseBody
    @PostMapping("/login")
    public LoginResponse login(@RequestBody CentralLogin login, HttpSession session, Model model) {
        LoginResponse loginResponse = centralLoginService.login(login);
        if (loginResponse.isLoggedIn()) {
            session.setAttribute("username", loginResponse.getUsername());// Assume tokenService can check by username
            session.setAttribute("firstName", loginResponse.getFirstName());
            session.setAttribute("lastName", loginResponse.getLastName());
//            model.addAttribute("hasTwitterToken", twitterService.hasToken());
            return loginResponse;
        } else {
            session.setAttribute("message", loginResponse.getMessage());
            session.setAttribute("messageType", loginResponse.getMessageType());
//            model.addAttribute("hasTwitterToken", twitterService.hasToken());
            return loginResponse;
        }
    }

    //TODO: Change this to POST
    @GetMapping("/signOut")
    public String signOut(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
