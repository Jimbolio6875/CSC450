package edu.missouristate.repository.Impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;

import edu.missouristate.domain.CentralLogin;
import edu.missouristate.domain.QTwitter;
import edu.missouristate.domain.Twitter;
import edu.missouristate.repository.custom.TwitterRepositoryCustom;

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
                .select(twitterTable.accessToken, twitterTable.accessTokenSecret, twitterTable.id.max())
                .groupBy(twitterTable.accessToken, twitterTable.accessTokenSecret)
                .fetchOne();
    }

    @Override
    public List<Twitter> findTweetsByCentralLogin(CentralLogin centralLogin){
    	return from(twitterTable)
    			.where(twitterTable.centralLogin.eq(centralLogin))
    			.fetch();
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
                .where(twitterTable.accessToken.eq(accessToken).and(twitterTable.tweetText.isNull())).fetchOne();
    }


}
