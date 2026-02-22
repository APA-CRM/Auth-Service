package com.crm.auth.service;

import com.crm.auth.config.UserAgentAnalyzerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {UserAgentAnalyzerConfig.class, DeviceInfoService.class})
@Slf4j
class DeviceInfoServiceTest {

    @Autowired
    private DeviceInfoService deviceInfoService;

    private static Stream<Arguments> validUserAgentsWithCorrectDeviceInfo() {
        return Stream.of(
                Arguments.of(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0",
                        "Windows NT, Firefox, 122.0"
                ),
                Arguments.of(
                        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36",
                        "Android, Chrome, 121"
                ),
                Arguments.of(
                        "PostmanRuntime/7.36.0", "Cloud, Postman Runtime, 7.36.0"
                )
        );
    }

    private static Stream<Arguments> validUserAgentsWithIncorrectDeviceInfo() {
        return Stream.of(
                Arguments.of(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0",
                        "Windows NT, Chrome, 121"
                ),
                Arguments.of(
                        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Mobile Safari/537.36",
                        "Android, Firefox, 122.0"
                ),
                Arguments.of(
                        "PostmanRuntime/7.36.0", "Cloud, Oracle Java-Http-Client, 21"
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

    @ParameterizedTest
    @MethodSource("validUserAgentsWithCorrectDeviceInfo")
    void isTheSameDeviceExpectedTrue(String userAgent, String correctDeviceInfo) {

        boolean theSameDevice = deviceInfoService.isTheSameDevice(userAgent, correctDeviceInfo);

        assertTrue(theSameDevice);
    }

    @ParameterizedTest
    @MethodSource("validUserAgentsWithIncorrectDeviceInfo")
    void isTheSameDeviceExpectedFalse(String userAgent, String incorrectDeviceInfo) {

        boolean theSameDevice = deviceInfoService.isTheSameDevice(userAgent, incorrectDeviceInfo);

        assertFalse(theSameDevice);
    }

}