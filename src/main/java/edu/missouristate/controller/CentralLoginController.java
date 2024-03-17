package edu.missouristate.controller;

import edu.missouristate.domain.CentralLogin;
import edu.missouristate.dto.GenericResponse;
import edu.missouristate.dto.LoginResponse;
import edu.missouristate.service.CentralLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class CentralLoginController {

    @Autowired
    CentralLoginService centralLoginService;

    @GetMapping(value = {"/", "/home"})
    public String getIndex() {
        return "home";
    }

    @GetMapping("/socials")
    public String getSocial() {
        return "socials";
    }
    

    @GetMapping("/login")
    public String getLoginPage(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "login";
        } else {
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
    public String getLandingPage(HttpSession session) {
    	System.out.println(session == null);
        if (session.getAttribute("username") == null || session.getAttribute("username").equals("")) {
            return "redirect:/login";
        } else {
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
    public LoginResponse login(@RequestBody CentralLogin login, HttpSession session) {
        LoginResponse loginResponse = centralLoginService.login(login);
        if (loginResponse.isLoggedIn()) {
            session.setAttribute("username", loginResponse.getUsername());
            session.setAttribute("firstName", loginResponse.getFirstName());
            session.setAttribute("lastName", loginResponse.getLastName());
            return loginResponse;
        } else {
            session.setAttribute("message", loginResponse.getMessage());
            session.setAttribute("messageType", loginResponse.getMessageType());
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
