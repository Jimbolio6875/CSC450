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

    /**
     * Retrieves the latest Twitter access token and secret for the most recently added Twitter account
     *
     * @return Tuple containing the access token and secret
     */
    @Override
    public Tuple getLatestUser() {
        return from(twitterTable)
                .select(twitterTable.accessToken, twitterTable.accessTokenSecret)
                .orderBy(twitterTable.id.desc())
                .limit(1)
                .fetchOne();
    }

    /**
     * Updates the text and creation date of a tweet based on the given access token
     *
     * @param accessToken The access token associated with the Twitter account
     * @param message     The new tweet text to set
     * @param date        The creation date to set for the tweet
     */
    @Override
    public void updateTextWithAccessToken(String accessToken, String message, LocalDateTime date) {
        update(twitterTable)
                .where(twitterTable.accessToken.eq(accessToken))
                .set(twitterTable.tweetText, message)
                .set(twitterTable.creationDate, date)
                .execute();
    }

    /**
     * Finds an existing Twitter post by access token where the tweet text is null
     *
     * @param accessToken The access token associated with the Twitter account
     * @return The Twitter post or null if no matching post is found
     */
    @Override
    public Twitter findExistingPostByTokenAndNoText(String accessToken) {
        return from(twitterTable)
                .where(twitterTable.accessToken.eq(accessToken).and(twitterTable.tweetText.isNull()))
                .orderBy(twitterTable.id.desc())
                .limit(1)
                .fetchOne();
    }

    /**
     * Retrieves all tweets that have a non-null creation date and are associated with the specified user ID
     *
     * @param userId The user ID to filter the tweets
     * @return List of Twitter posts
     */
    @Override
    public List<Twitter> getAllTweetsWhereCreationIsNotNullAndSameUserid(Integer userId) {
        return from(twitterTable).where(twitterTable.creationDate.isNotNull()
                .and(twitterTable.centralLogin.centralLoginId.eq(userId))).fetch();
    }

    /**
     * Deletes entries from the Twitter table where the creation date is null for a specific user
     *
     * @param userId The user ID associated with the cleanup operation
     */
    @Override
    public void cleanTable(Integer userId) {
        delete(twitterTable).where(twitterTable.creationDate.isNull()
                        .and(twitterTable.centralLogin.centralLoginId.eq(userId)))
                .execute();
    }

    /**
     * Checks if there are any stored access tokens for a specific user in the Twitter table
     *
     * @param userId The user ID to check
     * @return true if at least one token exists, otherwise false
     */
    @Override
    public boolean hasToken(Integer userId) {

        long hasToken = from(twitterTable).where(twitterTable.accessToken.isNotNull()
                .and(twitterTable.centralLogin.centralLoginId.eq(userId))).fetchCount();

        return hasToken > 0;
    }


}
