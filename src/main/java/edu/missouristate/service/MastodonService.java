package edu.missouristate.service;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Mastodon;

import java.util.List;

public interface MastodonService {

    String getAuthorizationUrl();

    String getAccessToken(String authorizationCode);

    String getUserId(String accessToken);

    Mastodon postMessageToMastodon(String message, String accessToken);

    void savePost(Mastodon post);

    List<Mastodon> getPostsByUserId(String userId);

    Tuple getLatestAccessToken();

    Mastodon findExistingPostByTokenAndNoText(String accessToken);

    void updateOrCreateMastodonPost(String mastroAccessToken, String message);

    List<Mastodon> getAllPosts();

    List<Mastodon> getAllPostsWherePostIsNotNull();

    void cleanTable();

    boolean hasToken();
}
