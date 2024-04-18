package edu.missouristate.repository.custom;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.RedditPosts;

import java.util.List;

public interface RedditPostsRepositoryCustom {

    public Tuple getLatestUser();

    public void updatePostId(String accessToken, String subreddit, String title, String text, String postId);

    public void updatePostIdWhereNull(String postId);

    RedditPosts findByAccessToken(String accessToken);

    RedditPosts updateOrCreateRedditPost(String redditAccessToken);

    List<String> getAllRedditPostIdsWhereNotNull();

    void cleanTable();

    boolean hasToken();
}
