package edu.missouristate.dao;

import edu.missouristate.dao.custom.MastodonRepositoryCustom;
import edu.missouristate.domain.Mastodon;
import org.springframework.data.repository.CrudRepository;

public interface MastodonRepository extends CrudRepository<Mastodon, String>, MastodonRepositoryCustom {
    
}
