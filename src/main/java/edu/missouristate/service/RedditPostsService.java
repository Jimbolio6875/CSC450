package edu.missouristate.service;


import com.querydsl.core.Tuple;
import edu.missouristate.domain.RedditPosts;
import edu.missouristate.domain.reddit.RedditResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface RedditPostsService {

//    public RedditPosts saveRedditPost(RedditPosts post);

    boolean isPostReady(String postId);

    String postToReddit(String accessToken, String subreddit, String title, String text, HttpSession session);

    Mono<RedditPosts> fetchAndSaveRedditPost(String fullName);

    List<RedditPosts> getAllRedditPosts();

    void checkPostStatusAndUpdateEmitter(SseEmitter emitter, String postId);

    List<String> getAllRedditPostIds();

    RedditPosts saveRedditPost(RedditPosts post);

    RedditPosts transformToRedditPosts(RedditResponse response);

    Mono<RedditPosts> transformAndSaveRedditPost(RedditResponse response);

    List<RedditPosts> fetchRedditPostDetails(List<String> postIds);

    Tuple getLatestUser();

    public void updatePostIdWhereNull(String postId);

    void updateContent(String redditAccessToken, String subreddit, String title, String message, String fullName, Integer userId);

    void cleanTable(Integer userId);

    boolean hasToken(Integer userId);

    List<String> getAllRedditPostIdsWhereNotNullAndSameUserid(Integer userId);

    List<String> getAllRedditPostIdsByUserIdWithNonNullAuthor(Integer userId);
}
