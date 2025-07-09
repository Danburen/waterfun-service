package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.security.EncryptionDataKey;

public interface EncryptionKeyDataRepo extends JpaRepository<EncryptionDataKey,String> {
}
