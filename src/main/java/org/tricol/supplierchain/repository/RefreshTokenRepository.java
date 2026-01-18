package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.RefreshToken;
import org.tricol.supplierchain.entity.UserApp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    List<RefreshToken> findByUserAndRevokedAtIsNull(UserApp user);
    
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revokedAt IS NULL AND rt.expiresAt > :now ORDER BY rt.createdAt DESC")
    List<RefreshToken> findActiveTokensByUser(UserApp user, LocalDateTime now);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    
    void deleteByUser(UserApp user);
}
