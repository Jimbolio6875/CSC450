package edu.missouristate.service;


import com.querydsl.core.Tuple;
import edu.missouristate.domain.RedditPosts;
import edu.missouristate.domain.reddit.RedditResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RedditPostsService {

//    public RedditPosts saveRedditPost(RedditPosts post);

    String postToReddit(String accessToken, String subreddit, String title, String text);

    Mono<RedditPosts> fetchAndSaveRedditPost(String fullName);

    List<RedditPosts> getAllRedditPosts();

    List<String> getAllRedditPostIds();

    RedditPosts saveRedditPost(RedditPosts post);

    RedditPosts transformToRedditPosts(RedditResponse response);

    Mono<RedditPosts> transformAndSaveRedditPost(RedditResponse response);

    List<RedditPosts> fetchRedditPostDetails(List<String> postIds);

    Tuple getLatestUser();

    public void updatePostIdWhereNull(String postId);

    void updateOrCreateRedditPost(String redditAccessToken, String subreddit, String title, String message, String fullName);

}
