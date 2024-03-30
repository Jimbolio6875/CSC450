package edu.missouristate.service.impl;

import edu.missouristate.dao.MastodonRepository;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.service.MastodonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MastodonServiceImpl implements MastodonService {

    @Autowired
    MastodonRepository mastodonRepository;

    @Override
    public void savePost(Mastodon post) {
        mastodonRepository.save(post);
    }
}
