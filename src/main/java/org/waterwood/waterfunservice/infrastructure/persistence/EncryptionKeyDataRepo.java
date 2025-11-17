package org.waterwood.waterfunservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.infrastructure.security.EncryptionDataKey;
import org.waterwood.waterfunservice.infrastructure.security.KeyStatus;

public interface EncryptionKeyDataRepo extends JpaRepository<EncryptionDataKey,String> {
    long countEncryptionDataKeysByKeyStatus(KeyStatus keyStatus);
}
