package edu.missouristate.service;

import edu.missouristate.domain.Mastodon;

public interface MastodonService {
    void savePost(Mastodon post);
}
