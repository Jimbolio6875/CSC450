package edu.missouristate.dao;

import edu.missouristate.domain.CentralLogin;
import edu.missouristate.dto.LoginResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CentralLoginRepository extends CrudRepository<CentralLogin, Integer> {

    public List<CentralLogin> findUsername(String username);

    public LoginResponse authenticate(String username, String password);

    Optional<CentralLogin> findById(Integer userId);
}
