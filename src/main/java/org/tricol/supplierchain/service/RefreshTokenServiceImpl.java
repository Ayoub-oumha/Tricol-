package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.entity.RefreshToken;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.exception.ResourceNotFoundException;
import org.tricol.supplierchain.repository.RefreshTokenRepository;
import org.tricol.supplierchain.service.inter.RefreshTokenService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${security.refresh-token.max-active-tokens:5}")
    private int maxActiveTokens;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(UserApp user) {
        List<RefreshToken> activeTokens = refreshTokenRepository
                .findActiveTokensByUser(user, LocalDateTime.now());
        
        if (activeTokens.size() >= maxActiveTokens) {
            RefreshToken oldestToken = activeTokens.get(activeTokens.size() - 1);
            oldestToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(oldestToken);
        }

        RefreshToken refreshToken = RefreshToken
                .builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken validateAndRotate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
        if (refreshToken.isRevoked()) {
            revokeTokenFamily(refreshToken);
            throw new IllegalStateException("Token has been revoked. Possible token theft detected.");
        }

        if (refreshToken.isExpired()) {
            throw new IllegalStateException("Refresh token has expired");
        }

        RefreshToken newRefreshToken = createRefreshToken(refreshToken.getUser());

        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshToken.setReplacedByToken(newRefreshToken.getToken());
        refreshTokenRepository.save(refreshToken);

        return newRefreshToken;
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (!refreshToken.isRevoked()) {
            refreshToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);
        }
    }

    private void revokeTokenFamily(RefreshToken token) {
        RefreshToken current = token;
        while (current != null) {
            if (!current.isRevoked()) {
                current.setRevokedAt(LocalDateTime.now());
                refreshTokenRepository.save(current);
            }

            if (current.getReplacedByToken() != null) {
                current = refreshTokenRepository.findByToken(current.getReplacedByToken())
                        .orElse(null);
            } else {
                current = null;
            }
        }
    }
}
