package edu.missouristate.dao.custom;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Tumblr;

import java.util.List;

public interface TumblrRepositoryCustom {

    Tumblr findExistingPostByTokenAndNoTextAndCentralLoginId(String accessToken, Integer centralLoginId);

    void updatePost(Tumblr post);

    Tuple getLatestUser();

    List<Tumblr> getAllPostsWhereCreationIsNotNullAndSameUserid(Integer userId);

    void cleanTable(Integer userId);

    boolean hasToken(Integer userId);

    void updateByPostId(String postId, Integer noteCount, String content);

    void updateDeletedPost(String postId, String str);
}
