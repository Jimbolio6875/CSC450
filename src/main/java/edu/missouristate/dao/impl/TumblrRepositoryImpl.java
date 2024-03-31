package edu.missouristate.dao.impl;

import edu.missouristate.dao.custom.TumblrRepositoryCustom;
import edu.missouristate.domain.QTumblr;
import edu.missouristate.domain.Tumblr;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TumblrRepositoryImpl extends QuerydslRepositorySupport implements TumblrRepositoryCustom {

    QTumblr tumblrTable = QTumblr.tumblr;

    public TumblrRepositoryImpl() {
        super(Tumblr.class);
    }

    @Override
    public List<Tumblr> getPostsByBlogIdentifier(String blog) {
        return from(tumblrTable)
                .where(tumblrTable.blogIdentifier.eq(blog))
                .limit(50)
                .fetch();
    }
}
