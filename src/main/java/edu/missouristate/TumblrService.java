package edu.missouristate;

public interface TumblrService {
    String getAuthorizationUrl();

    String getUserInfo(String oauthVerifier) throws Exception;

    void postToBlog(String postContent) throws Exception;
}
