package edu.missouristate.repository.Impl;


import com.querydsl.core.Tuple;
import edu.missouristate.domain.QRedditPosts;
import edu.missouristate.domain.RedditPosts;
import edu.missouristate.repository.custom.RedditPostsRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RedditPostsRepositoryImpl extends QuerydslRepositorySupport implements RedditPostsRepositoryCustom {

    QRedditPosts redditPostsTable = QRedditPosts.redditPosts;

    public RedditPostsRepositoryImpl() {
        super(RedditPosts.class);
    }

    /**
     * Fetches the latest Reddit access token and associated user ID as a tuple
     *
     * @return The latest tuple of access token and user ID
     */
    @Override
    public Tuple getLatestUser() {
        return from(redditPostsTable)
                .select(redditPostsTable.accessToken, redditPostsTable.id)
                .orderBy(redditPostsTable.id.desc())
                .limit(1)
                .fetchOne();
    }

    /**
     * Updates the post ID for a specific Reddit post based on access token, subreddit, title, and content
     *
     * @param accessToken Access token of the Reddit post
     * @param subreddit   Subreddit of the post
     * @param title       Title of the post
     * @param text        Content of the post
     * @param postId      New post ID to update
     */
    @Override
    public void updatePostId(String accessToken, String subreddit, String title, String text, String postId) {
        update(redditPostsTable)
                .where(redditPostsTable.accessToken.eq(accessToken)
                        .and(redditPostsTable.subreddit.eq(subreddit)
                                .and(redditPostsTable.title.eq(title))
                                .and(redditPostsTable.content.eq(text)))).set(redditPostsTable.postId, postId)
                .execute();
    }

    /**
     * Updates the post ID where it is currently null
     *
     * @param postId New post ID to set where current is null
     */
    @Override
    public void updatePostIdWhereNull(String postId) {
        update(redditPostsTable).where(redditPostsTable.postId.isNull()).set(redditPostsTable.postId, postId).execute();
    }

    /**
     * Finds a Reddit post by its access token
     *
     * @param accessToken Access token of the Reddit post
     * @return The Reddit post or null if not found
     */
    @Override
    public RedditPosts findByAccessToken(String accessToken) {
        return from(redditPostsTable).where(redditPostsTable.accessToken.eq(accessToken)).fetchOne();
    }

    /**
     * Finds or creates a Reddit post based on access token and user ID. Used for updating posts without an existing ID
     *
     * @param redditAccessToken Access token associated with the Reddit post
     * @param userId            User ID associated with the post
     * @return The existing or new Reddit post
     */
    @Override
    public RedditPosts updateOrCreateRedditPost(String redditAccessToken, Integer userId) {

        return from(redditPostsTable).where(redditPostsTable.accessToken.eq(redditAccessToken)
                .and(redditPostsTable.content.isNull().and(redditPostsTable.centralLogin.centralLoginId.eq(userId)))).fetchOne();

    }

    /**
     * Retrieves all Reddit post IDs associated with a specific user ID where the post IDs are not null
     *
     * @param userId User ID to filter posts
     * @return List of post IDs
     */
    @Override
    public List<String> getAllRedditPostIdsWhereNotNullAndSameUserid(Integer userId) {
        return from(redditPostsTable).select(redditPostsTable.postId).where(redditPostsTable.postId.isNotNull().and(redditPostsTable.centralLogin.centralLoginId.eq(userId))).fetch();
    }

    /**
     * Cleans up entries in the table where the post ID is null for a specific user
     *
     * @param userId User ID associated with the cleanup operation
     */
    @Override
    public void cleanTable(Integer userId) {
        delete(redditPostsTable)
                .where(redditPostsTable.postId.isNull().and(redditPostsTable.centralLogin.centralLoginId.eq(userId)))
                .execute();
    }

    /**
     * Checks if a user has any associated access tokens
     *
     * @param userId User ID to check
     * @return true if at least one token exists, otherwise false
     */
    @Override
    public boolean hasToken(Integer userId) {

        long tokenAmount = from(redditPostsTable).where(redditPostsTable.accessToken.isNotNull()
                .and(redditPostsTable.centralLogin.centralLoginId.eq(userId))).fetchCount();

        return tokenAmount > 0;
    }

    /**
     * Checks if a post is ready based on its post ID. A post is considered not ready if the author field is null
     *
     * @param postId The ID of the post to check
     * @return true if the post is ready, otherwise false
     */
    @Override
    public boolean isPostReady(String postId) {
        long counter = from(redditPostsTable).where(redditPostsTable.postId.eq(postId).and(redditPostsTable.author.isNull())).fetchCount();

        if (counter > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Fetches all Reddit post IDs by a specific user that have a non-null, non-deleted author
     *
     * @param userId User ID to filter posts
     * @return List of valid post IDs
     */
    @Override
    public List<String> getAllRedditPostIdsByUserIdWithNonNullAuthor(Integer userId) {
        return from(redditPostsTable).select(redditPostsTable.postId)
                .where(redditPostsTable.centralLogin.centralLoginId.eq(userId)
                        .and(redditPostsTable.author.isNotNull())
                        .and(redditPostsTable.author.ne("[deleted]")))
                .fetch();
    }
}
