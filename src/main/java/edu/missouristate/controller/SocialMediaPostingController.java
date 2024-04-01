package edu.missouristate.controller;

import edu.missouristate.service.TumblrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

// this controller will be used to post from a single text box
@RestController
public class SocialMediaPostingController {

    @Autowired
    TumblrService tumblrService;
}
