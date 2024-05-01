package edu.missouristate.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Handles requests to the error route and displays a custom error page
     *
     * @return The name of the view that renders the error page in this case 'errorPage'
     */
    @RequestMapping("/error")
    public String error() {
        return "errorPage";
    }
}
