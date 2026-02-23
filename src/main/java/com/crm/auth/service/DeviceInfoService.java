package com.crm.auth.service;

import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceInfoService {

    private final UserAgentAnalyzer userAgentAnalyzer;

    public String getDeviceInfoFromUserAgent(String userAgentString) {
        UserAgent.ImmutableUserAgent userAgent = userAgentAnalyzer.parse(userAgentString);

        String agentName = userAgent.getValue("AgentName");
        String agentVersion = userAgent.getValue("AgentVersion");
        String operatingSystemName = userAgent.getValue("OperatingSystemName");
        String deviceName = userAgent.getValue("DeviceName");

        return "%s %s %s %s".formatted(
                agentName, agentVersion, operatingSystemName, deviceName
        );
    }
}
