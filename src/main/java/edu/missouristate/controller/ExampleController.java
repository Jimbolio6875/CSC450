package edu.missouristate.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

//TODO: Potentially remove this dependency
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.dto.LoginResponse;
import edu.missouristate.service.CentralLoginService;

@Controller
public class ExampleController {

	@Autowired
	CentralLoginService centralLoginService;
	
	@GetMapping("/")
	public String getIndex() {
		return "login";
	}
	
	@GetMapping("/login")
	public String getLoginPage() {
		return "login";
	}
	
	@GetMapping("/register")
	public String getRegisterPage() {
		return "register";
	}
	
	//TODO: remove
	@GetMapping("/landing")
	public String getLandingPage() {
		return "landing";
	}
	
	//TODO: Use responses and dto's instead of loose objects being passed everywhere
	@ResponseBody
	@PostMapping("/register")
	public String register(@RequestBody CentralLogin login, HttpSession session) {
		boolean registered = centralLoginService.register(login);
		
		if (registered) {
			session.setAttribute("username", login.getUsername());
			return "landing";
		}
		else {
			session.setAttribute("message", "Username unavailable");
			session.setAttribute("messageType", "danger");
			return "redirect:/register";
		}
	}
	
	@ResponseBody
	@PostMapping("/login")
	public String login(@RequestBody CentralLogin login, HttpSession session) {
		LoginResponse loginResponse = centralLoginService.login(login);
		if (loginResponse.isLoggedIn()) {
			session.setAttribute("username", loginResponse.getUsername());
			return "redirect:/landing";
		}
		else {
			session.setAttribute("username", "");
			session.setAttribute("message", loginResponse.getErrorMessage());
			session.setAttribute("messageType", loginResponse.getErrorType());
			return "redirect:/login";
		}
	}
	

}
