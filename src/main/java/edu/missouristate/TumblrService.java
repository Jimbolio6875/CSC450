package edu.missouristate;

import edu.missouristate.domain.Tumblr;

import java.util.List;

public interface TumblrService {
    String getAuthorizationUrl();

    String getUserInfo(String oauthVerifier) throws Exception;

    void postToBlog(String postContent) throws Exception;

    List<Tumblr> getPostsByBlog();
}
