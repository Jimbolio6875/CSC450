package edu.missouristate.dao.custom;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Mastodon;

import java.util.List;

public interface MastodonRepositoryCustom {
//    List<String> getPostContent();
//
//    List<Integer> getPostFavourites();

    List<Mastodon> getPostsByUserId(String userId);

    Tuple getLatestAccessToken();

    Mastodon findExistingPostByTokenAndNoText(String accessToken, Integer userId);

    void updateWherePostIdIsNull(String accessToken, String id, String userId, String content, String url, Integer favourites);

    List<Mastodon> getAllMasterpostsWherePostIsNotNullAndSameUserId(Integer userId);

    void cleanTable(Integer userId);

    boolean hasToken(Integer userId);

    void updateByPostId(String postId, int favouritesCount);
}
