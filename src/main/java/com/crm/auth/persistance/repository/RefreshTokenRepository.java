package com.crm.auth.persistance.repository;

import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @EntityGraph(attributePaths = "user")
    Optional<RefreshToken> findByToken(String refreshToken);

    List<RefreshToken> findByUser(User user);

    @Modifying
    void removeByUserAndExpiredAtBefore(User user, Instant beforeInstant);

}
