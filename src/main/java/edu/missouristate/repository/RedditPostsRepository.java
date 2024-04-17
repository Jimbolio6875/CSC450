package edu.missouristate.repository;


import edu.missouristate.domain.RedditPosts;
import edu.missouristate.repository.custom.RedditPostsRepositoryCustom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedditPostsRepository extends CrudRepository<RedditPosts, Integer>, RedditPostsRepositoryCustom {

    @Query("SELECT rp.postId FROM RedditPosts rp")
    List<String> findAllPostIds();

    RedditPosts findByPostId(String postId);

}
