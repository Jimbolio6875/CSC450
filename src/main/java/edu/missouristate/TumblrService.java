package edu.missouristate;

import edu.missouristate.domain.Tumblr;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface TumblrService {
    String getAuthorizationUrl();

    String getUserInfo(String oauthVerifier) throws Exception;

    void postToBlog(String postContent) throws Exception;

    List<Tumblr> getPostsByBlog();

    void updatePosts() throws IOException, ExecutionException, InterruptedException;
}
