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

    @Override
    public Tuple getLatestUser() {
        return from(redditPostsTable)
                .select(redditPostsTable.accessToken, redditPostsTable.id)
                .orderBy(redditPostsTable.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public void updatePostId(String accessToken, String subreddit, String title, String text, String postId) {
        update(redditPostsTable)
                .where(redditPostsTable.accessToken.eq(accessToken)
                        .and(redditPostsTable.subreddit.eq(subreddit)
                                .and(redditPostsTable.title.eq(title))
                                .and(redditPostsTable.content.eq(text)))).set(redditPostsTable.postId, postId)
                .execute();
    }

    @Override
    public void updatePostIdWhereNull(String postId) {
        update(redditPostsTable).where(redditPostsTable.postId.isNull()).set(redditPostsTable.postId, postId).execute();
    }

    @Override
    public RedditPosts findByAccessToken(String accessToken) {
        return from(redditPostsTable).where(redditPostsTable.accessToken.eq(accessToken)).fetchOne();
    }

    @Override
    public RedditPosts updateOrCreateRedditPost(String redditAccessToken, Integer userId) {

        return from(redditPostsTable).where(redditPostsTable.accessToken.eq(redditAccessToken)
                .and(redditPostsTable.content.isNull().and(redditPostsTable.centralLogin.centralLoginId.eq(userId)))).fetchOne();

    }

    @Override
    public List<String> getAllRedditPostIdsWhereNotNullAndSameUserid(Integer userId) {
        return from(redditPostsTable).select(redditPostsTable.postId).where(redditPostsTable.postId.isNotNull().and(redditPostsTable.centralLogin.centralLoginId.eq(userId))).fetch();
    }

    @Override
    public void cleanTable(Integer userId) {
        delete(redditPostsTable)
                .where(redditPostsTable.postId.isNull().and(redditPostsTable.centralLogin.centralLoginId.eq(userId)))
                .execute();
    }

    @Override
    public boolean hasToken(Integer userId) {

        long tokenAmount = from(redditPostsTable).where(redditPostsTable.accessToken.isNotNull()
                .and(redditPostsTable.centralLogin.centralLoginId.eq(userId))).fetchCount();

        return tokenAmount > 0;
    }
}
