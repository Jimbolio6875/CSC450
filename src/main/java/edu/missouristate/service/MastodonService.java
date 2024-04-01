package edu.missouristate.service;

import edu.missouristate.domain.Mastodon;

import java.util.List;

public interface MastodonService {

    String getAuthorizationUrl();

    String getAccessToken(String authorizationCode);

    String getUserId(String accessToken);

    Mastodon postMessageToMastodon(String message, String accessToken);

    void savePost(Mastodon post);

    List<Mastodon> getPostsByUserId(String userId);
}
