package edu.missouristate.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.Tuple;
import edu.missouristate.domain.RedditPosts;
import edu.missouristate.domain.reddit.RedditResponse;
import edu.missouristate.repository.RedditPostsRepository;
import edu.missouristate.service.RedditPostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class RedditPostsServiceImpl implements RedditPostsService {

    private final RedditPostsRepository redditPostsRepository;
    private final WebClient webClient;

    @Value("${python.path}")
    private String pythonPath;

    @Autowired
    public RedditPostsServiceImpl(RedditPostsRepository redditPostsRepository, WebClient webClient) {
        this.redditPostsRepository = redditPostsRepository;
        this.webClient = webClient;
    }


    public String postToReddit(String accessToken, String subreddit, String title, String text) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonPath,
                    "scripts/RedditPythonScripts/reddit_submit_post.py",
                    accessToken, subreddit, title, text);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            String scriptOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // Assuming the script output needs parsing to extract the postId
                String jsonExtract = extractJsonPart(scriptOutput);
                String fullname = extractPostIdFromJson(jsonExtract); // Implement this method based on your script's output format
                if (fullname != null && !fullname.isEmpty()) {
                    return fullname;
                }
            }
        } catch (Exception e) {
            // Consider logging the exception
        }
        return null;
    }

    public String extractJsonPart(String fullResponse) {
        String jsonPart = "";

        String identifier = "Response body:";
        int startIndex = fullResponse.indexOf(identifier);

        if (startIndex != -1) {
            jsonPart = fullResponse.substring(startIndex + identifier.length()).trim();
        }

        return jsonPart;
    }


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


    @Override
    public Mono<RedditPosts> fetchAndSaveRedditPost(String fullName) {
        String fullId = fullName.startsWith("t3_") ? fullName : "t3_" + fullName;
        String url = "https://www.reddit.com/by_id/" + fullId + ".json";
        System.out.println("Fetching Reddit post from URL: " + url);

        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(jsonString -> System.out.println("Raw JSON response: " + jsonString))
                .flatMap(jsonString -> {
                    try {
                        RedditResponse response = new ObjectMapper().readValue(jsonString, RedditResponse.class);
                        if (response.getData().getChildren().isEmpty()) {
                            return Mono.error(new RuntimeException("Empty children array"));
                        }
                        return Mono.just(response);
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("Error parsing JSON", e));
                    }
                })
                .flatMap(this::transformAndSaveRedditPost)
                .doOnSuccess(post -> System.out.println("Post saved successfully: " + post))
                .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(2))
                        .filter(e -> e instanceof RuntimeException && e.getMessage().contains("Empty children array")))
                .onErrorResume(e -> {
                    System.err.println("Error fetching Reddit post after retries: " + e.getMessage());
                    return Mono.empty();
                });
    }

    @Override
    public List<RedditPosts> getAllRedditPosts() {
        return (List<RedditPosts>) redditPostsRepository.findAll();
    }


    @Override
    public List<String> getAllRedditPostIds() {
        return StreamSupport.stream(redditPostsRepository.findAll().spliterator(), false)
                .map(RedditPosts::getPostId)
                .collect(Collectors.toList());
    }

    @Override
    public List<RedditPosts> fetchRedditPostDetails(List<String> postIds) {
        List<RedditPosts> posts = new ArrayList<>();
        for (String id : postIds) {
            String url = "https://www.reddit.com/by_id/" + "t3_" + id + ".json";
            RedditResponse response = webClient.get().uri(url)
                    .retrieve()
                    .bodyToMono(RedditResponse.class)
                    .block();
            RedditPosts post = transformToRedditPosts(response);
            posts.add(post);
        }
        return posts;
    }

    @Override
    public Tuple getLatestUser() {
        return redditPostsRepository.getLatestUser();
    }

    @Override
    public void updatePostIdWhereNull(String postId) {
        redditPostsRepository.updatePostIdWhereNull(postId);
    }

    @Override
    public void updateOrCreateRedditPost(String redditAccessToken, String subreddit, String title, String message, String fullName) {

        RedditPosts exists = redditPostsRepository.updateOrCreateRedditPost(redditAccessToken);

        if (exists != null) {
            exists.setSubreddit(subreddit);
            exists.setPostId(fullName);
            exists.setContent(message);
            exists.setTitle(title);
            exists.setAccessToken(redditAccessToken);
            redditPostsRepository.save(exists);
        } else {
            RedditPosts redditPosts = new RedditPosts();
            redditPosts.setSubreddit(subreddit);
            redditPosts.setPostId(fullName);
            redditPosts.setContent(message);
            redditPosts.setTitle(title);
            redditPosts.setAccessToken(redditAccessToken);
            redditPostsRepository.save(redditPosts);
        }

    }

    @Override
    public List<String> getAllRedditPostIdsWhereNotNull() {
        return redditPostsRepository.getAllRedditPostIdsWhereNotNull();
    }

    @Override
    public void cleanTable() {
        redditPostsRepository.cleanTable();
    }

    @Override
    public Mono<RedditPosts> transformAndSaveRedditPost(RedditResponse response) {
        try {
            RedditPosts post = transformToRedditPosts(response);
            return Mono.justOrEmpty(saveRedditPost(post));
        } catch (Exception e) {
            System.err.println("Error transforming Reddit response: " + e.getMessage());
            return Mono.empty();
        }
    }

    // This function transforms the API response to the RedditPosts entity
    @Override
    public RedditPosts transformToRedditPosts(RedditResponse response) {
        String postId = response.getData().getChildren().get(0).getData().getId();

        // Try to find an existing post by postId
        RedditPosts post = redditPostsRepository.findByPostId(postId);

        // If the post doesn't exist, create a new one
        if (post == null) {
            post = new RedditPosts();
            post.setPostId(postId);
        }

        // Update fields with the latest data
        post.setSubreddit(response.getData().getChildren().get(0).getData().getSubreddit());
        post.setTitle(response.getData().getChildren().get(0).getData().getTitle());
        post.setContent(response.getData().getChildren().get(0).getData().getSelftext());
        post.setAuthor(response.getData().getChildren().get(0).getData().getAuthor());
        post.setScore(response.getData().getChildren().get(0).getData().getScore());
        post.setNumComments(response.getData().getChildren().get(0).getData().getNumComments());

        double timestampDouble = response.getData().getChildren().get(0).getData().getCreated();
        long timestamp = (long) timestampDouble;
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        post.setCreationDate(date);

        post.setUpVotes(response.getData().getChildren().get(0).getData().getUps());
        post.setDownVotes(response.getData().getChildren().get(0).getData().getDowns());
        post.setUrl(response.getData().getChildren().get(0).getData().getUrl());

        // Save the updated or new post
        return redditPostsRepository.save(post);
    }


    @Override
    public RedditPosts saveRedditPost(RedditPosts post) {
        return redditPostsRepository.save(post);
    }

}
