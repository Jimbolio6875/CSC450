package edu.missouristate.dao.custom;

import edu.missouristate.domain.Mastodon;

import java.util.List;

public interface MastodonRepositoryCustom {
//    List<String> getPostContent();
//
//    List<Integer> getPostFavourites();

    List<Mastodon> getPostsByUserId(String userId);
}
