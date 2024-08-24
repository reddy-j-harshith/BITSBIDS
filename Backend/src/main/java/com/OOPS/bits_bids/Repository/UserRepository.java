package com.OOPS.bits_bids.Repository;

import com.OOPS.bits_bids.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByBitsId(String bitsId);
    Optional<User> findByMail(String mail);
}
