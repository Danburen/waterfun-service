package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.user.UserDatum;

public interface UserDatumRepo extends JpaRepository<UserDatum, Integer> {
}
