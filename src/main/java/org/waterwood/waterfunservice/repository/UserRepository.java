package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.waterwood.waterfunservice.entity.User.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // Custom query to find a user by email or phone number
    @Query("SELECT u FROM User u JOIN UserDatum ud ON u.id = ud.user.id WHERE ud.email = :email")
    Optional<User> findUserByEmail(String email);
    // Custom query to find a user by phone number
    @Query("SELECT u FROM User u JOIN UserDatum ud ON u.id = ud.user.id WHERE ud.phone = :phone")
    Optional<User> findUserByPhone(String phone);
}
