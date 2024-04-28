package edu.missouristate.dao.impl;

import com.querydsl.core.Tuple;
import edu.missouristate.dao.custom.MastodonRepositoryCustom;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.domain.QMastodon;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class MastodonRepositoryImpl extends QuerydslRepositorySupport implements MastodonRepositoryCustom {

    QMastodon mastodonTable = QMastodon.mastodon;

    public MastodonRepositoryImpl() {
        super(Mastodon.class);
    }

    @Override
    public List<Mastodon> getPostsByUserId(String userId) {
        return from(mastodonTable)
                .where(mastodonTable.userId.eq(userId))
                .limit(50)
                .fetch();
    }

    @Override
    public Tuple getLatestAccessToken() {
        return from(mastodonTable).select(mastodonTable.accessToken, mastodonTable.id.max())
                .groupBy(mastodonTable.accessToken).fetchOne();
    }

    @Override
    public void updateWherePostIdIsNull(String accessToken, String id, String userId, String content, String url, Integer favourites) {

        int mostRecentId = Objects.requireNonNull(getQuerydsl()).createQuery().select(mastodonTable.id.max())
                .from(mastodonTable).fetchOne();

        update(mastodonTable).where(mastodonTable.id.eq(mostRecentId))
                .set(mastodonTable.accessToken, accessToken)
                .set(mastodonTable.postId, id)
                .set(mastodonTable.userId, userId)
                .set(mastodonTable.content, content)
                .set(mastodonTable.postUrl, url)
                .set(mastodonTable.favouriteCount, favourites)
                .execute();
    }

    @Override
    public List<Mastodon> getAllMasterpostsWherePostIsNotNullAndSameUserId(Integer userId) {
        return from(mastodonTable)
                .where(mastodonTable.postId.isNotNull().and(mastodonTable.centralLogin.centralLoginId.eq(userId)))
                .fetch();
    }

    @Override
    public void cleanTable(Integer userId) {
        delete(mastodonTable)
                .where(mastodonTable.postId.isNull()
                        .and(mastodonTable.centralLogin.centralLoginId.eq(userId)))
                .execute();
    }

    @Override
    public boolean hasToken(Integer userId) {
        long fetchCountAmount = from(mastodonTable)
                .where(mastodonTable.accessToken.isNotNull().and(mastodonTable.centralLogin.centralLoginId.eq(userId)))
                .fetchCount();

        return fetchCountAmount > 0;

    }

    @Override
    public Mastodon findExistingPostByTokenAndNoText(String accessToken, Integer userId) {
        return from(mastodonTable)
                .where(mastodonTable.accessToken.eq(accessToken).and(mastodonTable.content.isNull()
                        .and(mastodonTable.centralLogin.centralLoginId.eq(userId))))
                .limit(1)
                .fetchOne();
    }

    @Override
    public void updateByPostId(String postId, int favouritesCount) {
        update(mastodonTable)
                .where(mastodonTable.postId.eq(postId))
                .set(mastodonTable.favouriteCount, favouritesCount)
                .execute();
    }

    @Override
    public void updateDeletedPost(String postId, String str) {
        update(mastodonTable)
                .where(mastodonTable.postId.eq(postId))
                .set(mastodonTable.content, str)
                .execute();
    }

//    @Override
//    public List<String> getPostContent() {
//        return from(mastodonTable)
//                .select(mastodonTable.content)
//                .limit(50)
//                .fetch();
//    }
//
//    @Override
//    public List<Integer> getPostFavourites() {
//        return from(mastodonTable)
//                .select(mastodonTable.favouriteCount)
//                .limit(50)
//                .fetch();
//    }


}
