package edu.missouristate.dao.impl;

import com.querydsl.core.Tuple;
import edu.missouristate.dao.custom.MastodonRepositoryCustom;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.domain.QMastodon;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MastodonRepositoryImpl extends QuerydslRepositorySupport implements MastodonRepositoryCustom {

    QMastodon mastodonTable = QMastodon.mastodon;

    public MastodonRepositoryImpl() {
        super(Mastodon.class);
    }

    /**
     * gets mastodon posts by user id
     *
     * @param userId login id
     * @return list of mastodon posts
     */
    @Override
    public List<Mastodon> getPostsByUserId(String userId) {
        return from(mastodonTable)
                .where(mastodonTable.userId.eq(userId))
                .limit(50)
                .fetch();
    }

    /**
     * get latest access token
     *
     * @return tuple of latest access token
     */
    @Override
    public Tuple getLatestAccessToken() {
        return from(mastodonTable).select(mastodonTable.accessToken, mastodonTable.id.max())
                .groupBy(mastodonTable.accessToken).fetchOne();
    }

    /**
     * get posts that have not been deleted
     *
     * @param userId login id
     * @return list of tumblr posts
     */
    @Override
    public List<Mastodon> getAllMasterpostsWherePostIsNotNullAndSameUserId(Integer userId) {
        return from(mastodonTable)
                .where(mastodonTable.postId.isNotNull()
                        .and(mastodonTable.centralLogin.centralLoginId.eq(userId)
                                .and(mastodonTable.content.ne("[deleted]"))))
                .fetch();
    }

    /**
     * gets rid of posts that have been deleted
     *
     * @param userId login id
     */
    @Override
    public void cleanTable(Integer userId) {
        delete(mastodonTable)
                .where(mastodonTable.postId.isNull()
                        .and(mastodonTable.centralLogin.centralLoginId.eq(userId)))
                .execute();
    }

    /**
     * check if user has tumblr access token
     *
     * @param userId login id
     * @return bool
     */
    @Override
    public boolean hasToken(Integer userId) {
        long fetchCountAmount = from(mastodonTable)
                .where(mastodonTable.accessToken.isNotNull().and(mastodonTable.centralLogin.centralLoginId.eq(userId)))
                .fetchCount();

        return fetchCountAmount > 0;

    }

    /**
     * check if content is null for existing userId and accessToken
     *
     * @param accessToken mastodon access token
     * @param userId      login id
     * @return Mastodon object
     */
    @Override
    public Mastodon findExistingPostByTokenAndNoText(String accessToken, Integer userId) {
        return from(mastodonTable)
                .where(mastodonTable.accessToken.eq(accessToken).and(mastodonTable.content.isNull()
                        .and(mastodonTable.centralLogin.centralLoginId.eq(userId))))
                .limit(1)
                .fetchOne();
    }

    /**
     * update post given note count and content
     *
     * @param postId          post's id
     * @param favouritesCount new note count (if liked or disliked)
     * @param content         new content (if edited)
     */
    @Override
    public void updateByPostId(String postId, int favouritesCount, String content) {
        update(mastodonTable)
                .where(mastodonTable.postId.eq(postId))
                .set(mastodonTable.favouriteCount, favouritesCount).set(mastodonTable.content, content)
                .execute();
    }

    /**
     * update post content to [deleted]
     *
     * @param postId post's id
     * @param str    always [deleted]
     */
    @Override
    public void updateDeletedPost(String postId, String str) {
        update(mastodonTable)
                .where(mastodonTable.postId.eq(postId))
                .set(mastodonTable.content, str)
                .execute();
    }
}
