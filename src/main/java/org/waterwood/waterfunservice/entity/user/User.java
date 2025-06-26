package org.waterwood.waterfunservice.entity.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.waterwood.waterfunservice.utils.PasswordUtil;


import java.time.Instant;
@Data
@Entity
@NoArgsConstructor
@Table(name = "user", schema = "waterfun")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;


    @ColumnDefault("'ACTIVE'")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus;

    @Column(name = "status_changed_at")
    private Instant statusChangedAt;

    @Column(name = "status_change_reason")
    private String statusChangeReason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.accountStatus = AccountStatus.ACTIVE;
    }

    public boolean checkPassword(String password) {
        return PasswordUtil.matchPassword(password, passwordHash);
    }
}


