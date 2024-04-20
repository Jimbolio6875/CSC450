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

    @Override
    public Tumblr findExistingPostByTokenAndNoTextAndCentralLoginId(String accessToken, Integer centralLoginId) {
        return from(tumblrTable)
                .where(tumblrTable.accessToken.eq(accessToken).and(tumblrTable.content.isNull())
                        .and(tumblrTable.centralLogin.centralLoginId.eq(centralLoginId)))
                .orderBy(tumblrTable.id.desc())
                .limit(1)
                .fetchOne();
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
    public Tuple getLatestUser() {
        return from(tumblrTable)
                .select(tumblrTable.accessToken, tumblrTable.tokenSecret, tumblrTable.blogIdentifier, tumblrTable.id.max())
                .groupBy(tumblrTable.accessToken, tumblrTable.tokenSecret, tumblrTable.blogIdentifier)
                .limit(1)
                .fetchOne();
    }

    @Override
    public void updateWherePostIdIsNull(String accessToken, String tokenSecret, String blogIdentifier, String postId, String message) {

        int mostRecentId = Objects.requireNonNull(getQuerydsl()).createQuery().select(tumblrTable.id.max())
                .from(tumblrTable).fetchOne();

        update(tumblrTable).where(tumblrTable.id.eq(mostRecentId))
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
                .where(tumblrTable.accessToken.eq(accessToken).and(tumblrTable.content.isNull()))
                .orderBy(tumblrTable.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public List<Tumblr> getAllPostsWhereCreationIsNotNullAndSameUserid(Integer userId) {
        return from(tumblrTable).where(tumblrTable.postId.isNotNull().and(tumblrTable.centralLogin.centralLoginId.eq(userId)))
                .fetch();
    }

    @Override
    public void cleanTable(Integer userId) {
        delete(tumblrTable).where(tumblrTable.postId.isNull()
                .and(tumblrTable.centralLogin.centralLoginId.eq(userId))).execute();
    }

    @Override
    public boolean hasToken(Integer userId) {
        long count = from(tumblrTable)
                .where(tumblrTable.accessToken.isNotNull()
                        .and(tumblrTable.centralLogin.centralLoginId.eq(userId)))
                .fetchCount();

        return count > 0;
    }


}
