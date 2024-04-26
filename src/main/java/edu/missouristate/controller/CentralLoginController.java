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
        Integer userId = (Integer) session.getAttribute("userId");  // Retrieve userId from the session

        if (userId == null) {
            return "login";
        } else {
            // Pass userId to the service methods to check tokens specific to the logged-in user
            model.addAttribute("hasTwitterToken", twitterService.hasToken(userId));
            model.addAttribute("hasTumblrToken", tumblrService.hasToken(userId));
            model.addAttribute("hasRedditToken", redditPostsService.hasToken(userId));
            model.addAttribute("hasMastodonToken", mastodonService.hasToken(userId));
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

    @GetMapping("/landing")
    public String getLandingPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null || session.getAttribute("username").equals("")) {
            return "redirect:/login";
        } else {
            Integer userId = (Integer) session.getAttribute("userId"); // Get user ID from session
            if (userId == null) {
                return "redirect:/login"; // Redirect if user ID is not found
            }

            model.addAttribute("hasTwitterToken", twitterService.hasToken(userId));
            model.addAttribute("hasTumblrToken", tumblrService.hasToken(userId));
            model.addAttribute("hasRedditToken", redditPostsService.hasToken(userId));
            model.addAttribute("hasMastodonToken", mastodonService.hasToken(userId));

            if (Boolean.TRUE.equals(session.getAttribute("postsUpdated"))) {
                try {
                    List<Tumblr> userPosts = tumblrService.getPostsByBlog(userId);
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


    @ResponseBody
    @PostMapping("/register")
    public GenericResponse register(@RequestBody CentralLogin login, HttpSession session) {
    	try {
    		GenericResponse registered = centralLoginService.register(login);
            return registered;
    	}
        catch(Exception e) {
        	GenericResponse response = new GenericResponse();
        	response.setMessage("Registration failed.");
            response.setMessageType("danger");
            return response;
        }
    }

    @ResponseBody
    @PostMapping("/login")
    public LoginResponse login(@RequestBody CentralLogin login, HttpSession session, Model model) {
        LoginResponse loginResponse = centralLoginService.login(login);
        if (loginResponse.isLoggedIn()) {
            session.setAttribute("username", loginResponse.getUsername());
            session.setAttribute("firstName", loginResponse.getFirstName());
            session.setAttribute("lastName", loginResponse.getLastName());
            session.setAttribute("userId", loginResponse.getUserId());
            return loginResponse;
        } else {
            session.setAttribute("message", loginResponse.getMessage());
            session.setAttribute("messageType", loginResponse.getMessageType());
            return loginResponse;
        }
    }

    @GetMapping("/signOut")
    public String signOut(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

//    @RequestMapping("/error")
//    public String handleError(HttpServletRequest request) {
//        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//
//        if (status != null) {
//            int statusCode = Integer.parseInt(status.toString());
//
//            // You can add more handling logic based on the status code
//            if (statusCode == HttpStatus.NOT_FOUND.value()) {
//                return "error-404"; // custom 404 page
//            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
//                return "error-500"; // custom 500 page
//            }
//        }
//        return "error"; // general error page
//    }
}
