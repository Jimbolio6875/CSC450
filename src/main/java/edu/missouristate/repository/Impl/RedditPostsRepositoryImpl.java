package edu.missouristate.repository.Impl;


import edu.missouristate.domain.RedditPosts;
import edu.missouristate.repository.custom.RedditPostsRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class RedditPostsRepositoryImpl extends QuerydslRepositorySupport implements RedditPostsRepositoryCustom {

    public RedditPostsRepositoryImpl() {
        super(RedditPosts.class);
    }
}
