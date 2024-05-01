package edu.missouristate.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.Twitter;
import edu.missouristate.service.CentralLoginService;
import edu.missouristate.service.SocialMediaAccountService;
import edu.missouristate.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.stream.Collectors;


@Controller
public class TwitterController {

    private static final Logger log = LoggerFactory.getLogger(TwitterController.class);

    @Autowired
    TwitterService twitterService;

    @Autowired
    CentralLoginService centralLoginService;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;
    @Value("${python.path}")
    private String pythonPath;

    /**
     * Posts a tweet using the stored Twitter credentials from the user's session.
     * It uses a Python script to interact with the Twitter API
     *
     * @param tweetText The content of the tweet to post
     * @param session   The HTTP session containing Twitter credentials
     * @return ModelAndView for displaying the result or error message
     */
    @PostMapping("/tweet")
    public ModelAndView postTweet(@RequestParam String tweetText, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            String accessToken = (String) session.getAttribute("access_token");
            String accessTokenSecret = (String) session.getAttribute("access_token_secret");

            log.debug("Attempting to post a tweet with text: {}", tweetText);
            log.debug("Using Access Token: {}", accessToken);
            log.debug("Using Access Token Secret: {}", accessTokenSecret);

            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/TwitterPythonScripts/twitter_post_manager.py", accessToken, accessTokenSecret, tweetText);
            log.debug("Executing command: {}", String.join(" ", processBuilder.command()));

            Process process = processBuilder.start();
            String scriptOutput = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));
            int exitCode = process.waitFor();

            socialMediaAccountService.saveSocialMediaAccount(2, "Twitter", accessToken);

            ObjectMapper objectMapper = new ObjectMapper();

            if (exitCode == 0) {

                String json = scriptOutput.substring(scriptOutput.indexOf("{")).replace('\'', '\"');
                JsonNode rootNode = objectMapper.readTree(json);
                Long postId = rootNode.path("id").asLong();
                String textTweet = rootNode.path("text").asText();

                Twitter newTweet = new Twitter();

                newTweet.setCreationDate(LocalDateTime.now());
                newTweet.setTweetText(textTweet);
                newTweet.setAccessToken(accessToken);
                newTweet.setAccessTokenSecret(accessTokenSecret);

                twitterService.saveTweet(newTweet);


                log.debug("Tweet posted successfully. Script output: {}", scriptOutput);
                modelAndView.setViewName("result");
                modelAndView.addObject("message", "Tweet posted successfully!");
                modelAndView.addObject("scriptOutput", scriptOutput);
            } else {
                log.error("Failed to post tweet, exit code: {}", exitCode);
                throw new Exception("Failed to post tweet, exit code " + exitCode);
            }
        } catch (Exception e) {
            log.error("Exception occurred while posting tweet: {}", e.getMessage(), e);
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Failed to post tweet: " + e.getMessage());
        }

        return modelAndView;
    }


    /**
     * Exchanges a provided PIN for Twitter access tokens using a Python script
     *
     * @param pin     The PIN received from Twitter after user authorization
     * @param session The HTTP session to store the exchanged access tokens
     * @return A redirection string to the tweet posting form or error page
     */
    @PostMapping("/submit-pin")
    public String submitPin(@RequestParam("pin") String pin, HttpSession session) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/TwitterPythonScripts/exchange_pin_for_tokens.py", pin);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            System.out.println("STOP");
            String tokens = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();

            String[] parts = tokens.split(",");
            session.setAttribute("access_token", parts[0]);
            session.setAttribute("access_token_secret", parts[1]);

            return "redirect:/"; //<-- Redirect to the tweet posting form
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * Displays the form for posting a tweet if the user has an access token in session
     *
     * @param session The HTTP session to check for existing access token
     * @return The name of the view for posting a tweet or a redirection command
     */
    @GetMapping("/post-tweet")
    public String showTweetForm(HttpSession session) {
        if (session.getAttribute("access_token") != null) {
            return "post-tweet";
        } else {
            return "redirect:/";
        }
    }

    /**
     * Handles the OAuth callback from Twitter, exchanging the OAuth verifier for access tokens.
     * Stores the new tokens in session and associates them with the user's account
     *
     * @param oauthToken    The OAuth token received from Twitter
     * @param oauthVerifier The OAuth verifier received from Twitter
     * @param session       The HTTP session to store tokens and user information
     * @return A redirection command based on the result of token exchange
     */
    @GetMapping("/oauth-callback")
    public String oauthCallback(@RequestParam("oauth_token") String oauthToken, @RequestParam("oauth_verifier") String oauthVerifier, HttpSession session) {
        try {


            System.out.println("Entering oauthCallback method.");
            System.out.println("OAuth Token: " + oauthToken);
            System.out.println("OAuth Verifier: " + oauthVerifier);

            String requestToken = (String) session.getAttribute("request_token");
            String requestTokenSecret = (String) session.getAttribute("request_token_secret");
            System.out.println("Request Token from session: " + requestToken);
            System.out.println("Request Token Secret from session: " + requestTokenSecret);

            if (requestToken == null || requestTokenSecret == null) {
                System.err.println("Request token or secret is null. Redirecting to error.");
                return "error";
            }

            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/TwitterPythonScripts/exchange_verifier_for_tokens.py", oauthVerifier, requestToken, requestTokenSecret);
            processBuilder.redirectErrorStream(true);
            System.out.println("Starting process to exchange verifier for tokens.");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            System.out.println("Response from script: " + line);

            if (line == null || line.isEmpty()) {
                System.err.println("Script response is null or empty. Redirecting to error.");
                return "error";
            }

            String[] tokens = line.split(",");
            if (tokens.length < 2) {
                System.err.println("Invalid tokens format: " + line);
                throw new RuntimeException("Invalid tokens format: " + line);
            }

            // Get user ID from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";  // Handle missing userId
            }

            // Fetch the CentralLogin using userId
            CentralLogin user = centralLoginService.getUserById(userId);
            if (user == null) {
                return "redirect:/login";  // Handle user not found
            }

            // Create new Twitter credentials and associate with the user
            Twitter newTwitterCredentials = new Twitter();
            newTwitterCredentials.setAccessToken(tokens[0]);
            newTwitterCredentials.setAccessTokenSecret(tokens[1]);
            newTwitterCredentials.setCentralLogin(user);
            twitterService.saveTweet(newTwitterCredentials);

            // Set tokens in the session
            session.setAttribute("access_token", tokens[0]);
            session.setAttribute("access_token_secret", tokens[1]);


            return "redirect:/login";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception in oauthCallback: " + e.getMessage());
            return "error";
        }
    }


    /**
     * Initiates the Twitter authentication process by generating an authorization URL using a Python script.
     * It retrieves and stores request tokens in the user's session before redirecting to Twitter's auth URL
     *
     * @param session The HTTP session to store the request tokens
     * @return ModelAndView with redirection to Twitter's auth URL or an error page
     */
    @GetMapping("/start-auth")
    public ModelAndView startAuth(HttpSession session) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/TwitterPythonScripts/generate_auth_url.py");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // Reads the authorization URL directly
            String authUrl = reader.readLine();
            String tokensLine = reader.readLine();
            String[] tokens = tokensLine.split(",");

            if (tokens.length >= 2) {
                String requestToken = tokens[0];
                String requestTokenSecret = tokens[1];

                session.setAttribute("request_token", requestToken);
                session.setAttribute("request_token_secret", requestTokenSecret);

                return new ModelAndView("redirect:" + authUrl);
            } else {
                throw new IOException("Invalid token response from script");
            }
        } catch (IOException e) {
            e.printStackTrace();
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("message", "Error generating authorization URL.");
            return modelAndView;
        }
    }


}
