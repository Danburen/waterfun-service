package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.security.EncryptionDataKey;
import org.waterwood.waterfunservice.entity.security.KeyStatus;

public interface EncryptionKeyDataRepo extends JpaRepository<EncryptionDataKey,String> {
    long countEncryptionDataKeysByKeyStatus(KeyStatus keyStatus);
}
