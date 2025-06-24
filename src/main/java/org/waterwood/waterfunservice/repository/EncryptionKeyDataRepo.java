package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.User.EncryptionDataKey;

public interface EncryptionKeyDataRepo extends JpaRepository<EncryptionDataKey,String> {
}
