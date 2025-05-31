package com.crm.auth.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(columnList = "token"),
        @Index(columnList = "deviceInfo")
})
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String token;

    private String deviceInfo;

    @Column(nullable = false)
    private Instant expiredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updateAt;

}
