package com.crm.auth.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeviceInfoService {

    private final UserAgentAnalyzer userAgentAnalyzer;

    public String getDeviceInfoFromUserAgent(String userAgentString) {
        UserAgent.ImmutableUserAgent userAgent = userAgentAnalyzer.parse(userAgentString);

        DeviceInfo deviceInfo = getDeviceInfo(userAgent);

        return deviceInfo.toString();
    }

    public boolean isTheSameDevice(String userAgentString, String deviceInfo) {
        UserAgent.ImmutableUserAgent userAgent = userAgentAnalyzer.parse(userAgentString);

        DeviceInfo deviceInfoFromUserAgent = getDeviceInfo(userAgent);
        DeviceInfo deviceInfoFromString = new DeviceInfo(deviceInfo);

        return Objects.equals(deviceInfoFromString, deviceInfoFromUserAgent);
    }

    private DeviceInfo getDeviceInfo(UserAgent.ImmutableUserAgent userAgent) {
        String operatingSystemName = userAgent.getValue("OperatingSystemName");
        String agentName = userAgent.getValue("AgentName");
        String agentVersion = userAgent.getValue("AgentVersion");

        return new DeviceInfo(operatingSystemName, agentName, agentVersion);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    private static class DeviceInfo {

        String operatingSystemName;
        String agentName;
        @EqualsAndHashCode.Exclude
        String agentVersion;

        DeviceInfo(String deviceInfoString) {
            String[] strings = deviceInfoString.split(", ");

            if (strings.length < 3) {
                throw new IllegalArgumentException("Device info is too short");
            }

            this.operatingSystemName = strings[0];
            this.agentName = strings[1];
            this.agentVersion = strings[2];
        }

        @Override
        public String toString() {
            return "%s, %s, %s".formatted(operatingSystemName, agentName, agentVersion);
        }

    }

}
