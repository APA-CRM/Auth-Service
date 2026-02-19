package com.crm.auth.service.operation;

import com.crm.auth.dto.request.RefreshTokenRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.enums.TokenType;
import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.JwtService;
import com.crm.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenProvider {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthenticationResponse refreshJwtToken(RefreshTokenRequest request, String deviceInfo) {

        RefreshToken refreshToken = refreshTokenService
                .validateAndRecreateRefreshToken(request.getRefreshToken(), deviceInfo);

        User user = refreshToken.getUser();

        String token = jwtService.generateToken(user.getId(), user.getLogin());

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

}
