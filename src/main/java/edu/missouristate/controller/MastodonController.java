package edu.missouristate.controller;

import edu.missouristate.service.SocialMediaAccountService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Controller
public class MastodonController {

    private static final String CLIENT_ID = "0DIHKu7BCcGb9wuPLXIa-y4E7I9-TefyM9X5Q0Xym7w";
    private static final String CLIENT_SECRET = "gT1Hyha5yI2ZHRk3BmUA3YkiuW2UFCC_e-JVDaM8rHE";
    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    private static final String SCOPE = "read write";
    @Autowired
    private SocialMediaAccountService socialMediaAccountService;
//    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"; // will prob need to change

    @GetMapping("/mastodon/auth")
    public RedirectView startMastodonAuth() {
        String authUrl = "https://mastodon.social/oauth/authorize" +
                "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);

        return new RedirectView(authUrl);
    }

    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String code, HttpSession session) {
        String accessToken = getAccessToken(code);
        session.setAttribute("accessToken", accessToken);
        return "redirect:/post-message";
    }


    private String getAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenEndpoint = "https://mastodon.social/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formData = "client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET +
                "&grant_type=authorization_code" +
                "&code=" + authorizationCode +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(formData, headers);

        System.out.println("Sending access token request with data: " + formData);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenEndpoint, request, String.class);
            System.out.println("Received response: " + response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String accessToken = jsonResponse.getString("access_token");
            System.out.println("Extracted access token: " + accessToken);
            return accessToken;
        } catch (Exception e) {
            System.out.println("Error during token request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/post-message")
    public String showPostMessageForm() {
        return "mastodon/post-message";

    }

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
            postMessageToMastodon(message, accessToken);
            modelAndView.setViewName("result");
            modelAndView.addObject("message", "Message posted to Mastodon successfully!");
            socialMediaAccountService.saveSocialMediaAccount(3, "Mastodon", accessToken);
        } catch (Exception e) {
            modelAndView.setViewName("error");
            modelAndView.addObject("error", "Failed to post message to Mastodon: " + e.getMessage());
        }

        return modelAndView;
    }


    private void postMessageToMastodon(String message, String accessToken) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        String postParams = "status=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(postParams, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://mastodon.social/api/v1/statuses", request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to post to Mastodon, response code: " + response.getStatusCode());
        }
    }


}
