package edu.missouristate.controller;

import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.RedditPosts;
import edu.missouristate.domain.Twitter;
import edu.missouristate.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Controller
public class RedditController {

    private static final Logger log = LoggerFactory.getLogger(RedditController.class);

    @Autowired
    MastodonService mastodonService;

    @Autowired
    TumblrService tumblrService;

    @Autowired
    RedditPostsService redditPostsService;

    @Autowired
    TwitterService twitterService;

    String CLIENT_ID = "6aK_iXozHqB7AlJY3aF6ZA";
    String CLIENT_SECRET = "6bEXPVk7tYpAAFj4fbH9Vj-XSKzGag";
    String REDIRECT_URI = "http://localhost:8080/reddit/callback";

    @Autowired
    CentralLoginService centralLoginService;

    @Autowired
    private SocialMediaAccountService socialMediaAccountService;

    @Value("${python.path}")
    private String pythonPath;


    /**
     * Initiates the Reddit OAuth flow, constructs the authentication URL, and redirects the user
     *
     * @param session The HTTP session that stores unique state tokens
     * @param model   The model object to pass information to the view
     * @return A redirect URL to Reddit OAuth page
     */
    @GetMapping("/reddit/auth")
    public String redditAuth(HttpSession session, Model model) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("REDDIT_STATE", state);

        String encodedRedirectUri = URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        String authUrl = "https://www.reddit.com/api/v1/authorize?" +
                "client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&state=" + state +
                "&redirect_uri=" + encodedRedirectUri +
                "&duration=permanent" +
                "&scope=identity submit read";

        return "redirect:" + authUrl;
    }


    /**
     * Handles the OAuth callback, exchanges the code for an access token, and redirects on success or error
     *
     * @param state   The state token returned from Reddit
     * @param code    The authorization code returned from Reddit
     * @param session The HTTP session for storing the retrieved access token
     * @return A ModelAndView object that either redirects to the login page or displays an error message
     */
    @GetMapping("/reddit/callback")
    public ModelAndView redditCallback(@RequestParam("state") String state, @RequestParam("code") String code, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        String sessionState = (String) session.getAttribute("REDDIT_STATE");

        if (sessionState == null || !sessionState.equals(state)) {
            log.debug("State mismatch. sessionState: {}, state: {}", sessionState, state);
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Invalid state parameter");
            return modelAndView;
        }

        // Max tries
        final int maxRetries = 5;

        // Backoff milli sec
        final long backoffMillis = 1000;
        int retryCount = 0;
        boolean success = false;
        String scriptOutput = "";
        String errorOutput = "";

        while (!success && retryCount < maxRetries) {
            try {
                String pythonScriptPath = "scripts/RedditPythonScripts/exchange_auth_code_for_access_token.py";
                ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, pythonScriptPath, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, code);
                log.debug("Executing Python script: {}", String.join(" ", processBuilder.command()));
                Process process = processBuilder.start();
                scriptOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
                        .lines().collect(Collectors.joining(System.lineSeparator()));
                errorOutput = new BufferedReader(new InputStreamReader(process.getErrorStream()))
                        .lines().collect(Collectors.joining(System.lineSeparator()));
                int exitCode = process.waitFor();
                log.info("Script Exit Code: " + exitCode);
                log.info("Script Output: " + scriptOutput);
                log.info("Error Output: " + errorOutput);

                if (scriptOutput.contains("ACCESS_TOKEN:")) {
                    success = true;
                } else {
                    throw new IllegalArgumentException("Access token not found in script output");
                }
            } catch (Exception e) {
                log.error("Attempt {} failed to exchange code for token", retryCount + 1, e);
                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(backoffMillis * (long) Math.pow(2, retryCount));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        if (!success) {
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Failed to exchange code for token after " + maxRetries + " attempts.");
            return modelAndView;
        }

        String accessToken = extractAccessToken(scriptOutput);
        log.error("Outputted string<?> : " + accessToken);

        if (accessToken.isEmpty()) {
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Extracted access token is empty.");
            return modelAndView;
        }

        // Get user ID from session
        Integer userId = (Integer) session.getAttribute("userId");
        CentralLogin user = centralLoginService.getUserById(userId);
        RedditPosts redditPosts = new RedditPosts();
        redditPosts.setAccessToken(accessToken);
        redditPosts.setCentralLogin(user);
        redditPostsService.saveRedditPost(redditPosts);
        session.setAttribute("REDDIT_ACCESS_TOKEN", accessToken);
        modelAndView.setViewName("redirect:/login");
        return modelAndView;
    }


    /**
     * Extracts the access token from the output of the Python script
     *
     * @param scriptOutput The output from the Python script used to exchange the authorization code for an access token
     * @return The extracted access token as a String
     * @throws IllegalArgumentException if the access token is not found in the script output
     */
    private String extractAccessToken(String scriptOutput) throws IllegalArgumentException {
        return Arrays.stream(scriptOutput.split(System.lineSeparator()))
                .filter(line -> line.startsWith("ACCESS_TOKEN:"))
                .findFirst()
                .map(line -> line.substring("ACCESS_TOKEN:".length()))
                .orElseThrow(() -> new IllegalArgumentException("Access token not found in script output"));
    }


    /**
     * Processes form submissions for Reddit posts, submits the post via a Python script, and handles the result
     *
     * @param subreddit The subreddit where the post is to be submitted
     * @param title     The title of the post
     * @param text      The text content of the post
     * @param session   The HTTP session which contains the Reddit access token
     * @return A ModelAndView object that either shows the result of the post submission or displays an error message
     */
    @PostMapping("/reddit/submitPost")
    public ModelAndView submitPost(@RequestParam("subreddit") String subreddit,
                                   @RequestParam("title") String title,
                                   @RequestParam("text") String text,
                                   HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("reddit/postResult");

        // Directly use session-stored accessToken
        String accessToken = (String) session.getAttribute("REDDIT_ACCESS_TOKEN");
        log.debug("Using Access Token from session: {}", accessToken);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            log.error("Access Token is missing or empty.");
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Access Token is missing or invalid.");
            return modelAndView;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, "scripts/RedditPythonScripts/reddit_submit_post.py", accessToken, subreddit, title, text);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String result = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            System.out.println("->>" + result);
            log.debug("Result from post submission: {}", result);
            modelAndView.addObject("result", result);
            System.out.println("!->>" + result);
            String jsonExtract = extractJsonPart(result);
            String fullname = extractPostIdFromJson(jsonExtract);
            redditPostsService.fetchAndSaveRedditPost(fullname).subscribe(
                    post -> log.info("Reddit post saved: {}", post),
                    error -> log.error("Error fetching and saving Reddit post {}", fullname, error)
            );

        } catch (Exception e) {
            log.error("Failed to submit post", e);
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Failed to submit post: " + e.getMessage());
        }
        return modelAndView;
    }


    /**
     * Displays the form for submitting a Reddit post, checks for a valid access token
     *
     * @param session The HTTP session which should contain a valid Reddit access token
     * @return A ModelAndView object either containing the form or an error message
     */
    @GetMapping("/reddit/submitRedditPost")
    public ModelAndView showSubmitRedditPost(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("reddit/submitRedditPost");
        String accessToken = (String) session.getAttribute("REDDIT_ACCESS_TOKEN");
        log.debug("Retrieved Access Token from session: {}", accessToken);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            modelAndView.setViewName("error");
            modelAndView.addObject("message", "Access token is missing or invalid.");
            return modelAndView;
        }

        return modelAndView;
    }

    /**
     * Retrieves and displays the history of posts made by the user
     *
     * @return A ModelAndView object that contains lists of Reddit posts and Twitter tweets to be displayed
     * @throws IOException          If an input or output exception occurred
     * @throws ExecutionException   If the computation threw an exception
     * @throws InterruptedException If the current thread was interrupted while waiting
     */
    @GetMapping("/post-history")
    public ModelAndView getPostHistory() throws IOException, ExecutionException, InterruptedException {
        ModelAndView modelAndView = new ModelAndView("post-history");
        List<String> redditPostIds = redditPostsService.getAllRedditPostIds();
        List<RedditPosts> redditPosts = redditPostsService.fetchRedditPostDetails(redditPostIds);
        List<Twitter> tweets = twitterService.getAllTweets();
        modelAndView.addObject("redditPosts", redditPosts);
        modelAndView.addObject("tweets", tweets);
        return modelAndView;
    }

    /**
     * Extracts the JSON portion from a mixed-format response
     *
     * @param fullResponse The full response which represents the JSON
     * @return The extracted JSON part as a string
     */
    public String extractJsonPart(String fullResponse) {
        String jsonPart = "";
        String identifier = "Response body:";
        int startIndex = fullResponse.indexOf(identifier);

        if (startIndex != -1) {
            jsonPart = fullResponse.substring(startIndex + identifier.length()).trim();
        }

        return jsonPart;
    }

    /**
     * Parses JSON to extract a Reddit post ID
     *
     * @param jsonResponse The JSON part of a response that potentially contains the post ID
     * @return The extracted post ID or an indication that no ID was found
     */
    private String extractPostIdFromJson(String jsonResponse) {
        String regexPattern = "comments/([a-zA-Z0-9]+)";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(jsonResponse);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "No ID found";
        }
    }

    /**
     * Checks if a Reddit post is ready to be viewed
     *
     * @param postId The ID of the post whose status is being checked
     * @return A ResponseEntity with a string indicating if the post is "ready" or "not ready"
     */
    @GetMapping("/post-status/{postId}")
    public ResponseEntity<String> checkPostStatus(@PathVariable String postId) {
        boolean isReady = redditPostsService.isPostReady(postId);
        return ResponseEntity.ok(isReady ? "ready" : "not ready");
    }


}

