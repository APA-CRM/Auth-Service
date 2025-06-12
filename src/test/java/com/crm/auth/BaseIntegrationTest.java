package com.crm.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @LocalServerPort
    private int localServerPort;

    @PostConstruct
    public void init() {
        RestAssured.port = localServerPort;
        objectMapper.findAndRegisterModules();
    }

}
