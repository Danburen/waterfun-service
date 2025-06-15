package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.User.UserDatum;

public interface UserDatumRepository  extends JpaRepository<UserDatum, Integer> {
}
