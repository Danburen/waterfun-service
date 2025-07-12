package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.user.UserDatum;

import java.util.Optional;

public interface UserDatumRepo extends JpaRepository<UserDatum, Long> {
    Optional<UserDatum> findByPhonePrefixAndPhoneHash(String phonePrefix, String phoneHash);

    Optional<UserDatum> findByEmailDisplayAndEmailHash(String emailDisplay, String emailHash);
}
