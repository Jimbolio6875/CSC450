package edu.missouristate.repository.Impl;

import edu.missouristate.domain.Twitter;
import edu.missouristate.repository.custom.TwitterRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class TwitterRepositoryImpl extends QuerydslRepositorySupport implements TwitterRepositoryCustom {

    public TwitterRepositoryImpl() {
        super(Twitter.class);
    }

}
