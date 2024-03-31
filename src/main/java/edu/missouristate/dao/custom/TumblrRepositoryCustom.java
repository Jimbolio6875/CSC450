package edu.missouristate.dao.custom;

import edu.missouristate.domain.Tumblr;

import java.util.List;

public interface TumblrRepositoryCustom {
    List<Tumblr> getPostsByBlogIdentifier(String blog);
}
