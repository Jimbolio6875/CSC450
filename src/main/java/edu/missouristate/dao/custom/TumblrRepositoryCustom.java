package edu.missouristate.dao.custom;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Tumblr;

import java.util.List;

public interface TumblrRepositoryCustom {

    Tumblr findExistingPostByTokenAndNoTextAndCentralLoginId(String accessToken, Integer centralLoginId);

    List<Tumblr> getPostsByBlogIdentifier(String blog);

    void updatePost(Tumblr post);

    Tuple getLatestUser();

    void updateWherePostIdIsNull(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message);

    Tumblr findExistingPostByTokenAndNoText(String accessToken);

    List<Tumblr> getAllPostsWhereCreationIsNotNullAndSameUserid(Integer userId);

    void cleanTable(Integer userId);

    boolean hasToken(Integer userId);
}
