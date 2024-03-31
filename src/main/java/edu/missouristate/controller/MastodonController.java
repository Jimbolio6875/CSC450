package edu.missouristate.controller;

import edu.missouristate.dao.MastodonRepository;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.service.MastodonService;
import edu.missouristate.service.SocialMediaAccountService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
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
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller
public class MastodonController {

    private static final String CLIENT_ID = "0DIHKu7BCcGb9wuPLXIa-y4E7I9-TefyM9X5Q0Xym7w";
    private static final String CLIENT_SECRET = "gT1Hyha5yI2ZHRk3BmUA3YkiuW2UFCC_e-JVDaM8rHE";
    private static final String REDIRECT_URI = "http://localhost:8080/callback";
    private static final String SCOPE = "read write";

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Autowired
    private MastodonService mastodonService;

//    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"; // will prob need to change

    // prob should move a lot of this into the service but it can wait for now

    @GetMapping("/mastodon/auth")
    public RedirectView startMastodonAuth() {
        String authUrl = "https://mastodon.social/oauth/authorize" +
                "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);

        return new RedirectView(authUrl);
    }

    // set session access token and redirect to post-message after authorization
    @GetMapping("/callback")
    public String handleCallback(@RequestParam("code") String code, HttpSession session) {
        String accessToken = getAccessToken(code);

        session.setAttribute("accessToken", accessToken);

        return "redirect:/post-message";
    }

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


    // set client_id/secret/auth to get token
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

        System.out.println(formData);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenEndpoint, request, String.class);
            System.out.println(response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String accessToken = jsonResponse.getString("access_token");
            System.out.println("access token: " + accessToken);
            return accessToken;

        } catch (Exception e) {
            System.out.println("Error during request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

//    @RequestMapping("/post-message")
//    public String showPostMessageForm() {
//        return "mastodon/post-message";
//
//    }

    // load posting form and show post history
    @RequestMapping("/post-message")
    public ModelAndView showPostMessageForm(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("mastodon/post-message");
        String accessToken = (String) session.getAttribute("accessToken");

        if (accessToken != null) {
            try {
                String userId = getUserId(accessToken);
//                List<String> userPosts = getUserPosts(userId, accessToken);
//                List<String> userPosts = mastodonService.getPostContent();
//                List<Integer> userPostFavourites = getUserPostFavourites(userId, accessToken);
//                List<Integer> userPostFavourites = mastodonService.getPostFavourites();

                List<Mastodon> post = mastodonService.getPosts();
                Collections.reverse(post);
//                modelAndView.addObject("posts", userPosts);
//                modelAndView.addObject("postFavourites", userPostFavourites);

                modelAndView.addObject("posts", post);
            } catch (Exception e) {
                modelAndView.addObject("error", "Failed to get posts: " + e.getMessage());
            }
        } else {
            modelAndView.addObject("error", "No access token");
        }

        return modelAndView;
    }

    // submit post to mastodon
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


    // post message and add message attributes to database
    private void postMessageToMastodon(String message, String accessToken) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(accessToken);

        String postParams = "status=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        HttpEntity<String> request = new HttpEntity<>(postParams, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("https://mastodon.social/api/v1/statuses", request, String.class);
        System.out.println(response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {

            JSONObject postObject = new JSONObject(response.getBody());
            String id = postObject.getString("id");
            String content = postObject.getString("content");
            String url = postObject.getString("url");
            Integer favourites = postObject.getInt("favourites_count");

            Mastodon post = new Mastodon();
            post.setPostId(id);
            post.setContent(content);
            post.setPostUrl(url);
            post.setFavouriteCount(favourites);

            mastodonService.savePost(post);

//            System.out.println(id.concat(" ").concat(content).concat(" ").concat(url).concat(" ").concat(String.valueOf(favourites)));
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to post to Mastodon, response code: " + response.getStatusCode());
        }
    }

    // get json status of user and get post content
    // this might be useless now but im keeping it just in case
//    private List<String> getUserPosts(String userId, String accessToken) {
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.setBearerAuth(accessToken);
//        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                "https://mastodon.social/api/v1/accounts/" + userId + "/statuses",
//                HttpMethod.GET, entity, String.class
//        );
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            JSONArray postsArray = new JSONArray(response.getBody());
//            List<String> postsList = new ArrayList<>();
//
//            for (int i = 0; i < postsArray.length(); i++) {
//
//                System.out.println(postsArray.getJSONObject(i));
//
//                JSONObject postObject = postsArray.getJSONObject(i);
//                String postContent = postObject.getString("content");
//                postsList.add(postContent);
//            }
//
//            return postsList;
//        } else {
//            throw new RuntimeException("Failed to get user posts: " + response.getStatusCode());
//        }
//    }

    // get json status of user and get post favourites
    // also prob useless
//    private List<Integer> getUserPostFavourites(String userId, String accessToken) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//
//        headers.setBearerAuth(accessToken);
//        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                "https://mastodon.social/api/v1/accounts/" + userId + "/statuses",
//                HttpMethod.GET, entity, String.class
//        );
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            JSONArray postsArray = new JSONArray(response.getBody());
//            List<Integer> postFavouritesCountList = new ArrayList<>();
//
//            for (int i = 0; i < postsArray.length(); i++) {
//
//                System.out.println(postsArray.getJSONObject(i));
//
//                JSONObject postObject = postsArray.getJSONObject(i);
//                int postContent = postObject.getInt("favourites_count");
//                postFavouritesCountList.add(postContent);
//            }
//
//            return postFavouritesCountList;
//        } else {
//            throw new RuntimeException("Failed to get user posts: " + response.getStatusCode());
//        }
//    }

    // gets user id
    private String getUserId(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://mastodon.social/api/v1/accounts/verify_credentials",
                HttpMethod.GET, entity, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject userObject = new JSONObject(response.getBody());
            return userObject.getString("id");
        } else {
            throw new RuntimeException("Failed to get user id: " + response.getStatusCode());
        }
    }


}
