package edu.missouristate.repository.custom;

import com.querydsl.core.Tuple;
import edu.missouristate.domain.Twitter;

import java.time.LocalDateTime;
import java.util.List;

public interface TwitterRepositoryCustom {

    public Tuple getLatestUser();

    public void updateTextWithAccessToken(String accessToken, String message, LocalDateTime date);

    Twitter findExistingPostByTokenAndNoText(String accessToken);

    List<Twitter> getAllTweetsWhereCreationIsNotNull();

    void cleanTable();
}
