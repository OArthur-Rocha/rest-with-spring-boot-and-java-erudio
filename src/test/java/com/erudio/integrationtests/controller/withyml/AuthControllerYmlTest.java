package com.erudio.integrationtests.controller.withyml;

import com.erudio.configs.TestConfigs;
import com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import com.erudio.integrationtests.vo.AccountCredentialsVo;
import com.erudio.integrationtests.vo.TokenVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerYmlTest extends AbstractIntegrationTest {

    private static YMLMapper obecjtMapper;
    private static TokenVo tokenVo;

    @BeforeAll
    public static void setup(){
        obecjtMapper = new YMLMapper();
    }

    @Test
    @Order(1)
    public void testSignin() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVo user = new AccountCredentialsVo("arthur", "admin123");

        RequestSpecification specification = new RequestSpecBuilder()
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        tokenVo = given().spec(specification)
                .config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_YML)
                .body(user, obecjtMapper)
                    .when()
                .post()
                    .then()
                        .statusCode(200)
                            .extract()
                            .body()
                                .as(TokenVo.class, obecjtMapper);

        assertNotNull(tokenVo.getAccessToken());
        assertNotNull(tokenVo.getRefreshToken());
    }

    @Test
    @Order(2)
    public void testRefresh() throws JsonMappingException, JsonProcessingException {
        AccountCredentialsVo user = new AccountCredentialsVo("arthur", "admin123");

        var newTokenVo = given().config(RestAssuredConfig
                        .config()
                        .encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                .accept(TestConfigs.CONTENT_TYPE_YML)
                .basePath("/auth/refresh")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_YML)
                .pathParam("username", tokenVo.getUserName())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVo.getRefreshToken())
                    .when()
                .put("{username}")
                    .then()
                .statusCode(200)
                    .extract()
                        .body()
                            .as(TokenVo.class, obecjtMapper);

        assertNotNull(newTokenVo.getAccessToken());
        assertNotNull(newTokenVo.getRefreshToken());
    }

}
