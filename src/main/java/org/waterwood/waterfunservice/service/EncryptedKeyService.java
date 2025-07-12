package org.waterwood.waterfunservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.entity.security.EncryptionDataKey;
import org.waterwood.waterfunservice.entity.security.KeyStatus;
import org.waterwood.waterfunservice.repository.EncryptionKeyDataRepo;
import org.waterwood.waterfunservice.service.dto.OpResult;
import org.waterwood.waterfunservice.utils.security.EncryptionHelper;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class EncryptedKeyService {
    private static final int MIN_KEY_COUNT = 2;
    @Autowired
    EncryptionKeyDataRepo encryptionKeyDataRepo;

    @PostConstruct
    public void init(){
        ensureMinKeysExists();
    }

    private OpResult<EncryptionDataKey> createEncryptedKey(String encryptedKey, String algorithm, Integer keyLength, String description){
        EncryptionDataKey key = new EncryptionDataKey();
        key.setId(generateKeyId());
        key.setEncryptedKey(encryptedKey);
        key.setAlgorithm(algorithm != null ? algorithm : "AES");
        key.setKeyLength(keyLength != null ? keyLength : 256);
        key.setCreatedAt(Instant.now());
        key.setDescription(description);
        key.setKeyStatus(KeyStatus.PENDING_ACTIVATION);
        encryptionKeyDataRepo.save(key);
        return OpResult.success(key);
    }

    private void ensureMinKeysExists(){
        long activeKeyCount = encryptionKeyDataRepo.countEncryptionDataKeysByKeyStatus(KeyStatus.ACTIVE);
        if(activeKeyCount < MIN_KEY_COUNT){
            try{
                int KeysToGenerateCount = (int)(MIN_KEY_COUNT-activeKeyCount);
                List<EncryptionDataKey> newKeys = EncryptionHelper.generateAndEncryptDEKs(KeysToGenerateCount);
                newKeys.forEach(key-> key.setKeyStatus(KeyStatus.ACTIVE));
                encryptionKeyDataRepo.saveAll(newKeys);
                log.info("Generate {} new encrypted Keys",KeysToGenerateCount);
            }catch (Exception e){
                log.error("Error occurred when generate Encrypted key. {}", String.valueOf(e));
            }
        }
    }

    public List<EncryptionDataKey> getAllKeys() {
        return encryptionKeyDataRepo.findAll();
    }

    public Optional<EncryptionDataKey> randomPickEncryptionKey(){
        List<EncryptionDataKey> keys = getAllKeys();
        if(keys.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(keys.get(ThreadLocalRandom.current().nextInt(keys.size())));
        }
    }

    public Optional<EncryptionDataKey> pickEncryptionKey(int keyInd){
        List<EncryptionDataKey> keys = getAllKeys();
        if(keys.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(keys.stream()
                    .filter(key -> key.getKeyStatus() == KeyStatus.ACTIVE)
                    .toList()
                    .get(keyInd));
        }
    }

    public Optional<List<EncryptionDataKey>> pickEncryptionKeys(int... keysInd){
        List<EncryptionDataKey> keys = getAllKeys();
        if(keys.isEmpty()){
            return Optional.empty();
        }
        List<EncryptionDataKey> activeKeys = keys.stream()
                .filter(key -> key.getKeyStatus() == KeyStatus.ACTIVE)
                .toList();

        if (activeKeys.isEmpty()) {
            return Optional.empty();
        }
        if (keysInd == null || keysInd.length == 0) {
            return Optional.of(activeKeys);
        }
        List<EncryptionDataKey> selectedKeys = new ArrayList<>();
        for (int index : keysInd) {
            if (index >= 0 && index < activeKeys.size()) {
                selectedKeys.add(activeKeys.get(index));
            }
        }
        return selectedKeys.isEmpty() ? Optional.empty() : Optional.of(selectedKeys);
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
