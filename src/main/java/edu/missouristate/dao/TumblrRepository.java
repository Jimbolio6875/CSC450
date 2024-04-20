package edu.missouristate.dao;

import edu.missouristate.dao.custom.TumblrRepositoryCustom;
import edu.missouristate.domain.Tumblr;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TumblrRepository extends CrudRepository<Tumblr, String>, TumblrRepositoryCustom {

    List<Tumblr> findByCentralLogin_CentralLoginId(Integer centralLoginId);


}
