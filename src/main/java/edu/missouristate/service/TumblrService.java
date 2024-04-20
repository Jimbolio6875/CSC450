package edu.missouristate.service;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Tumblr;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface TumblrService {
    String getAuthorizationUrl();

    String getUserInfo(String oauthVerifier, HttpSession session) throws Exception;

    String postToBlog(String postContent) throws Exception;

    List<Tumblr> getPostsByBlog(Integer userId);

    void updatePosts() throws IOException, ExecutionException, InterruptedException;

    Tuple getLatestUser();

    void updateOrCreateTumblrPost(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message, Integer userId);

    List<Tumblr> getAllPosts();

    List<Tumblr> getAllPostsWhereCreationIsNotNullAndSameUserid(Integer userId);

    void cleanTable(Integer userId);

    Tumblr findExistingPostByTokenAndNoTextAndCentralLoginId(String accessToken, Integer centralLoginId);

    boolean hasToken(Integer userId);

//    void updateContent(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message, Integer userId);
}
