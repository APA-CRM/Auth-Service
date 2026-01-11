package com.crm.auth.persistance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PasswordRestoreRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer verificationCode;

    @OneToOne(optional = false)
    private User user;

    @Setter(AccessLevel.NONE)
    private Integer attemptsCount = 0;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updateAt;

    public void incrementAttemptsCount() {
        attemptsCount += 1;
    }

}
