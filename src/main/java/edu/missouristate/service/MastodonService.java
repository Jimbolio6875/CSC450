package edu.missouristate.service;

import edu.missouristate.domain.Mastodon;

import java.util.List;

public interface MastodonService {
    void savePost(Mastodon post);

    List<Mastodon> getPostsByUserId(String userId);
}
