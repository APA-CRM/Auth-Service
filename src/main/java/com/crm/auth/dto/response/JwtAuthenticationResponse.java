package com.crm.auth.dto.response;

import com.crm.auth.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JwtAuthenticationResponse {

    private String token;

    private TokenType tokenType;

    private String refreshToken;

}
