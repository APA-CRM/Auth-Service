package com.crm.auth.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class VerificationCodeGenerator {

    public static Integer generateRandom4DigitVerificationCode() {
        Random random = new Random();

        return random.nextInt(1000, 9999);
    }

}
