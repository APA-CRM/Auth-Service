package com.crm.auth.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Base64;

@UtilityClass
public class SecureStringGenerator {

    public static String generateSecureString(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[byteLength];

        secureRandom.nextBytes(tokenBytes);

        return Base64.getEncoder().withoutPadding().encodeToString(tokenBytes);
    }

}
