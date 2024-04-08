package edu.missouristate.dao.impl;

import com.querydsl.core.Tuple;
import edu.missouristate.dao.custom.TumblrRepositoryCustom;
import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.QTumblr;
import edu.missouristate.domain.Tumblr;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                .limit(20)
                .orderBy(tumblrTable.date.desc())
                .fetch();
    }

    @Transactional
    @Override
    public void updatePost(Tumblr post) {
        update(tumblrTable)
                .where(tumblrTable.postId.eq(post.getPostId()))
                .set(tumblrTable.content, post.getContent())
                .set(tumblrTable.noteCount, post.getNoteCount())
                .execute();
    }

    @Override
    public List<Tumblr> findTumblrsByCentralLogin(CentralLogin centralLogin){
    	return from(tumblrTable)
    			.where(tumblrTable.centralLogin.eq(centralLogin))
    			.fetch();
    }

    @Override
    public Tuple getLatestUser() {
        return from(tumblrTable)
                .select(tumblrTable.accessToken, tumblrTable.tokenSecret, tumblrTable.blogIdentifier, tumblrTable.id.max())
                .groupBy(tumblrTable.accessToken, tumblrTable.tokenSecret, tumblrTable.blogIdentifier)
                .fetchOne();
    }

    @Override
    public void updateWherePostIdIsNull(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message) {
        update(tumblrTable).where(tumblrTable.postId.isNull())
                .set(tumblrTable.accessToken, accessToken)
                .set(tumblrTable.tokenSecret, tokenSecret)
                .set(tumblrTable.blogIdentifier, blogIdentifier)
                .set(tumblrTable.postId, postId)
                .set(tumblrTable.content, message)
                .execute();

    }

    @Override
    public Tumblr findExistingPostByTokenAndNoText(String accessToken) {
        return from(tumblrTable)
                .where(tumblrTable.accessToken.eq(accessToken).and(tumblrTable.content.isNull())).fetchOne();
    }


}
