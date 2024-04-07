package edu.missouristate.dao.custom;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Tumblr;

import java.util.List;

public interface TumblrRepositoryCustom {
    List<Tumblr> getPostsByBlogIdentifier(String blog);

    void updatePost(Tumblr post);

    Tuple getLatestUser();

    void updateWherePostIdIsNull(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message);

    Tumblr findExistingPostByTokenAndNoText(String accessToken);
}
