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
        update(mastodonTable).where(mastodonTable.postId.isNull())
                .set(mastodonTable.accessToken, accessToken)
                .set(mastodonTable.postId, id)
                .set(mastodonTable.userId, userId)
                .set(mastodonTable.content, content)
                .set(mastodonTable.postUrl, url)
                .set(mastodonTable.favouriteCount, favourites)
                .execute();

    }

    @Override
    public Mastodon findExistingPostByTokenAndNoText(String accessToken) {
        return from(mastodonTable)
                .where(mastodonTable.accessToken.eq(accessToken).and(mastodonTable.content.isNull())).fetchOne();
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
