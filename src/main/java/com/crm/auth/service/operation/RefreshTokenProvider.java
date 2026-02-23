package com.crm.auth.service.operation;

import com.crm.auth.dto.request.RefreshTokenRequest;
import com.crm.auth.dto.response.JwtAuthenticationResponse;
import com.crm.auth.enums.TokenType;
import com.crm.auth.persistance.entity.RefreshToken;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.DeviceInfoService;
import com.crm.auth.service.JwtService;
import com.crm.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenProvider {

    private final JwtService jwtService;
    private final DeviceInfoService deviceInfoService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public JwtAuthenticationResponse refreshJwtToken(RefreshTokenRequest request, String userAgent) {
        RefreshToken refreshToken = refreshTokenService.getRefreshTokenOfThrowUnauthorizedException(request.getRefreshToken());

        String deviceInfo = deviceInfoService.getDeviceInfoFromUserAgent(userAgent);

        refreshToken = refreshTokenService.updateRefreshToken(refreshToken, deviceInfo);

        User user = refreshToken.getUser();

        String token = jwtService.generateToken(user.getId(), user.getLogin(), deviceInfo);

        return new JwtAuthenticationResponse(token, TokenType.BEARER, refreshToken.getToken());
    }

}
