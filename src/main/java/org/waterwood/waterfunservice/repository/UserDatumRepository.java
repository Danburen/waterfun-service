package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.User.UserDatum;

import java.util.Optional;

public interface UserDatumRepository extends JpaRepository<UserDatum, Long> {
    Optional<UserDatum> findByEmail(String email);
    Optional<UserDatum> findByPhone(String phone);
}
