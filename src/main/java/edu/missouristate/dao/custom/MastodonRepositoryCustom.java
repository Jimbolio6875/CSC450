package edu.missouristate.dao.custom;

import com.querydsl.core.Tuple;

import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.Mastodon;

import java.util.List;

public interface MastodonRepositoryCustom {
//    List<String> getPostContent();
//
//    List<Integer> getPostFavourites();

	public List<Mastodon> findMastodonsByCentralLogin(CentralLogin centralLogin);
	
    List<Mastodon> getPostsByUserId(String userId);

    Tuple getLatestAccessToken();

    Mastodon findExistingPostByTokenAndNoText(String accessToken);

    void updateWherePostIdIsNull(String accessToken, String id, String userId, String content, String url, Integer favourites);
}
