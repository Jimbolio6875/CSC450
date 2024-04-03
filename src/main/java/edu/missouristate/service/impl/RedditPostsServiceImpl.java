package edu.missouristate.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.missouristate.domain.RedditPosts;
import edu.missouristate.domain.reddit.RedditResponse;
import edu.missouristate.repository.RedditPostsRepository;
import edu.missouristate.service.RedditPostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RedditPostsServiceImpl implements RedditPostsService {

    private final RedditPostsRepository redditPostsRepository;
    private final WebClient webClient;

    @Autowired
    public RedditPostsServiceImpl(RedditPostsRepository redditPostsRepository, WebClient webClient) {
        this.redditPostsRepository = redditPostsRepository;
        this.webClient = webClient;
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
        RedditPosts post = new RedditPosts();

        post.setPostId(response.getData().getChildren().get(0).getData().getId());
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

        return post;
    }

    @Override
    public RedditPosts saveRedditPost(RedditPosts post) {
        return redditPostsRepository.save(post);
    }

}
