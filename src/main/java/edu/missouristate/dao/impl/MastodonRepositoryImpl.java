package edu.missouristate.dao.impl;

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
