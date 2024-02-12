package edu.missouristate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//TODO: Potentially remove this dependency
import edu.missouristate.domain.CentralLogin;
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
	public String getLogin() {
		CentralLogin example = new CentralLogin();
		example.setUsername("Testerino");
		example.setPassword("Testeroo");
		centralLoginService.saveCentralLogin(example);
		
		return "login";
	}

}
