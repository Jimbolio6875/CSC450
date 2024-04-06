package edu.missouristate.dao.custom;

import edu.missouristate.domain.Tumblr;

import java.util.List;

public interface TumblrRepositoryCustom {
    List<Tumblr> getPostsByBlogIdentifier(String blog);

    void updatePost(Tumblr post);

    List<String> getAllTumblrIds();

    List<Tumblr> tumblrPosts(List<String> tublrIds);

//    TumblrAccessToken getTumblrAccessToken(String blogIdentifier);
}
