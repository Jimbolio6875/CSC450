package edu.missouristate.dao.impl;

import com.querydsl.core.Tuple;
import edu.missouristate.dao.custom.TumblrRepositoryCustom;
import edu.missouristate.domain.QTumblr;
import edu.missouristate.domain.Tumblr;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Repository
public class TumblrRepositoryImpl extends QuerydslRepositorySupport implements TumblrRepositoryCustom {

    QTumblr tumblrTable = QTumblr.tumblr;

    public TumblrRepositoryImpl() {
        super(Tumblr.class);
    }

    /**
     * check if content is null for existing userId and accessToken
     * @param accessToken mastodon access token
     * @param centralLoginId login id
     * @return Tumblr object
     */
    @Override
    public Tumblr findExistingPostByTokenAndNoTextAndCentralLoginId(String accessToken, Integer centralLoginId) {
        return from(tumblrTable)
                .where(tumblrTable.accessToken.eq(accessToken).and(tumblrTable.content.isNull())
                        .and(tumblrTable.centralLogin.centralLoginId.eq(centralLoginId)))
                .orderBy(tumblrTable.id.desc())
                .limit(1)
                .fetchOne();
    }

    /**
     * updates tumblr post content and note count
     * @param post tumblr post
     */
    @Transactional
    @Override
    public void updatePost(Tumblr post) {
        update(tumblrTable)
                .where(tumblrTable.postId.eq(post.getPostId()))
                .set(tumblrTable.content, post.getContent())
                .set(tumblrTable.noteCount, post.getNoteCount())
                .execute();
    }

    /**
     * gets latest user
     * @return latest user
     */
    @Override
    public Tuple getLatestUser() {
        return from(tumblrTable)
                .select(tumblrTable.accessToken, tumblrTable.tokenSecret, tumblrTable.blogIdentifier, tumblrTable.id.max())
                .groupBy(tumblrTable.accessToken, tumblrTable.tokenSecret, tumblrTable.blogIdentifier)
                .limit(1)
                .fetchOne();
    }

    /**
     * get posts that have not been deleted
     * @param userId login id
     * @return list of tumblr posts
     */
    @Override
    public List<Tumblr> getAllPostsWhereCreationIsNotNullAndSameUserid(Integer userId) {
        return from(tumblrTable).where(tumblrTable.postId.isNotNull()
                        .and(tumblrTable.centralLogin.centralLoginId.eq(userId).and(tumblrTable.content.ne("[deleted]"))))
                .fetch();
    }

    /**
     * gets rid of posts that have been deleted
     * @param userId login id
     */
    @Override
    public void cleanTable(Integer userId) {
        delete(tumblrTable).where(tumblrTable.postId.isNull()
                .and(tumblrTable.centralLogin.centralLoginId.eq(userId))).execute();
    }

    /**
     * check if user has tumblr access token
     * @param userId login id
     * @return bool
     */
    @Override
    public boolean hasToken(Integer userId) {
        long count = from(tumblrTable)
                .where(tumblrTable.accessToken.isNotNull()
                        .and(tumblrTable.centralLogin.centralLoginId.eq(userId)))
                .fetchCount();

        return count > 0;
    }

    /**
     * update post given note count and content
     * @param postId post's id
     * @param noteCount new note count (if liked or disliked)
     * @param content new content (if edited)
     */
    @Override
    public void updateByPostId(String postId, Integer noteCount, String content) {
        update(tumblrTable)
                .where(tumblrTable.postId.eq(postId))
                .set(tumblrTable.noteCount, noteCount).set(tumblrTable.content, content)
                .execute();
    }

    /**
     * update post content to [deleted]
     * @param postId post's id
     * @param str always [deleted]
     */
    @Override
    public void updateDeletedPost(String postId, String str) {
        update(tumblrTable)
                .where(tumblrTable.postId.eq(postId))
                .set(tumblrTable.content, str)
                .execute();
    }


}
