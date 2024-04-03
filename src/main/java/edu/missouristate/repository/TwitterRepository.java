package edu.missouristate.repository;


import edu.missouristate.domain.Twitter;
import edu.missouristate.repository.custom.TwitterRepositoryCustom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TwitterRepository extends CrudRepository<Twitter, Long>, TwitterRepositoryCustom {

    List<Twitter> findByTweetId(Long userId);


}
