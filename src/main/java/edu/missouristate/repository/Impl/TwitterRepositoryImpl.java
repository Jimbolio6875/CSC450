package edu.missouristate.repository.Impl;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.QTwitter;
import edu.missouristate.domain.Twitter;
import edu.missouristate.repository.custom.TwitterRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TwitterRepositoryImpl extends QuerydslRepositorySupport implements TwitterRepositoryCustom {

    QTwitter twitterTable = QTwitter.twitter;

    public TwitterRepositoryImpl() {
        super(Twitter.class);
    }

//    @Override
//    public Tuple getLatestUser() {
//        return from(twitterTable).select(twitterTable.accessToken, twitterTable.accessTokenSecret).orderBy(twitterTable.id.desc()).fetchOne();
//    }

    @Override
    public Tuple getLatestUser() {
        return from(twitterTable)
                .select(twitterTable.accessToken, twitterTable.accessTokenSecret)
                .orderBy(twitterTable.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public void updateTextWithAccessToken(String accessToken, String message, LocalDateTime date) {
        update(twitterTable)
                .where(twitterTable.accessToken.eq(accessToken))
                .set(twitterTable.tweetText, message)
                .set(twitterTable.creationDate, date)
                .execute();
    }

    @Override
    public Twitter findExistingPostByTokenAndNoText(String accessToken) {
        return from(twitterTable)
                .where(twitterTable.accessToken.eq(accessToken).and(twitterTable.tweetText.isNull()))
                .orderBy(twitterTable.id.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public List<Twitter> getAllTweetsWhereCreationIsNotNull() {
        return from(twitterTable).where(twitterTable.creationDate.isNotNull()).fetch();
    }

    @Override
    public void cleanTable() {
        delete(twitterTable).where(twitterTable.creationDate.isNull()).execute();
    }

    @Override
    public boolean hasToken() {

        long hasToken = from(twitterTable).where(twitterTable.accessToken.isNotNull()).fetchCount();

        return hasToken > 0;
    }


}
