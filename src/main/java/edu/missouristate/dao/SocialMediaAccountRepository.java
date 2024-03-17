package edu.missouristate.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import edu.missouristate.domain.SocialMediaAccount;


public interface SocialMediaAccountRepository extends JpaRepository<SocialMediaAccount, Integer> {
    // Custom query methods can be added here
}
