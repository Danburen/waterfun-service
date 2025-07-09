package org.waterwood.waterfunservice.entity.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "encryption_data_key")
public class EncryptionDataKey {
    @Id
    @Column(name = "id", nullable = false, length = 50)
    private String id;

    @Column(name = "encrypted_key", nullable = false, length = 512)
    private String encryptedKey;

    @ColumnDefault("'AES'")
    @Column(name = "algorithm", nullable = false, length = 20)
    private String algorithm;

    @ColumnDefault("256")
    @Column(name = "key_length", nullable = false)
    private Integer keyLength;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "description")
    private String description;

}