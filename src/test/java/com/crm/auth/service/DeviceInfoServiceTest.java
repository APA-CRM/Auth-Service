package com.crm.auth.service;

import com.crm.auth.config.UserAgentAnalyzerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {UserAgentAnalyzerConfig.class, DeviceInfoService.class})
@Slf4j
class DeviceInfoServiceTest {

    @Autowired
    private DeviceInfoService deviceInfoService;

    private static Stream<Arguments> validUserAgentsWithCorrectDeviceInfo() {
        return Stream.of(
                Arguments.of(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0",
                        "Firefox 122.0 Windows NT Desktop"
                ),
                Arguments.of(
                        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36",
                        "Chrome 121 Android Google Pixel 8"
                ),
                Arguments.of(
                        "PostmanRuntime/7.36.0", "Postman Runtime 7.36.0 Cloud Postman Runtime"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("validUserAgentsWithCorrectDeviceInfo")
    public void getDeviceInfoFromUserAgentExpectedCorrectDeviceInfo(String userAgent, String expectedDeviceInfo) {

        String deviceInfo = deviceInfoService.getDeviceInfoFromUserAgent(userAgent);

        assertNotNull(deviceInfo);

        assertEquals(expectedDeviceInfo, deviceInfo);
    }

}