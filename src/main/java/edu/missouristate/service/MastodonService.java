package edu.missouristate.service;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.dto.MastodonPostDTO;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface MastodonService {

    String getAuthorizationUrl();

    String getAccessToken(String authorizationCode, HttpSession session);

    String getUserId(String accessToken);

    MastodonPostDTO postMessageToMastodon(String message, String accessToken);


    List<Mastodon> getPostsByUserId(String userId);

    Tuple getLatestAccessToken();


    void updateOrCreateMastodonPost(String mastroAccessToken, String message, Integer userId, MastodonPostDTO mastodonPostDTO);

    List<Mastodon> getAllMasterpostsWherePostIsNotNullAndSameUserId(Integer userId);

    void cleanTable(Integer userId);

    boolean hasToken(Integer userId);

    void updateAllPosts(HttpSession session, List<Mastodon> mastodonPosts);
}
