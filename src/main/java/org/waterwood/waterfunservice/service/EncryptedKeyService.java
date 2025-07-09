package org.waterwood.waterfunservice.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.entity.security.EncryptionDataKey;
import org.waterwood.waterfunservice.repository.EncryptionKeyDataRepo;
import org.waterwood.waterfunservice.service.dto.OpResult;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EncryptedKeyService {
    @Autowired
    EncryptionKeyDataRepo encryptionKeyDataRepo;
    private OpResult<EncryptionDataKey> createEncryptedKey(String encryptedKey, String algorithm, Integer keyLength, String description){
        EncryptionDataKey key = new EncryptionDataKey();
        key.setId(generateKeyId());
        key.setEncryptedKey(encryptedKey);
        key.setAlgorithm(algorithm != null ? algorithm : "AES");
        key.setKeyLength(keyLength != null ? keyLength : 256);
        key.setCreatedAt(Instant.now());
        key.setDescription(description);
        encryptionKeyDataRepo.save(key);
        return OpResult.success(key);
    }

    public List<EncryptionDataKey> getAllKeys() {
        return encryptionKeyDataRepo.findAll();
    }

    @Transactional
    public Optional<EncryptionDataKey> updateKeyDescription(String id, String description) {
        return encryptionKeyDataRepo.findById(id)
                .map(key -> {
                    key.setDescription(description);
                    return encryptionKeyDataRepo.save(key);
                });
    }

    private String generateKeyId() {
        return UUID.randomUUID().toString();
    }

    private OpResult<Void> deleteEncryptedKey(String keyId){
        encryptionKeyDataRepo.deleteById(keyId);
        return OpResult.success();
    }
}
