package com.crm.auth.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class VerificationCodeGenerator {

    public static String generateRandom4DigitVerificationCode() {
        Random random = new Random();

        int randomInt = random.nextInt(0, 9999);

        return String.format("%04d", randomInt);
    }

}
