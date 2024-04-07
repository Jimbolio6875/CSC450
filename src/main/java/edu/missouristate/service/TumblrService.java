package edu.missouristate.service;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Tumblr;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface TumblrService {
    String getAuthorizationUrl();

    String getUserInfo(String oauthVerifier) throws Exception;

    String postToBlog(String postContent) throws Exception;

    List<Tumblr> getPostsByBlog();

    void updatePosts() throws IOException, ExecutionException, InterruptedException;

    Tuple getLatestUser();

    void updateOrCreateTumblrPost(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message);
}
