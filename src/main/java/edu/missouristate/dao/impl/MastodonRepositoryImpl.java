package edu.missouristate.dao.impl;

import edu.missouristate.dao.custom.MastodonRepositoryCustom;
import edu.missouristate.domain.Mastodon;
import edu.missouristate.domain.QMastodon;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class MastodonRepositoryImpl extends QuerydslRepositorySupport implements MastodonRepositoryCustom {

    QMastodon mastodonTable = QMastodon.mastodon;

    public MastodonRepositoryImpl() {
        super(Mastodon.class);
    }


}
