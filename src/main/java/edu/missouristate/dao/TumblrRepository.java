package edu.missouristate.dao;

import edu.missouristate.dao.custom.TumblrRepositoryCustom;
import edu.missouristate.domain.Tumblr;
import org.springframework.data.repository.CrudRepository;

public interface TumblrRepository extends CrudRepository<Tumblr, String>, TumblrRepositoryCustom {
}
