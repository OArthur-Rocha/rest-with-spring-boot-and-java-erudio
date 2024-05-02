package com.erudio.integrationtests.controller.withxml;

import com.erudio.configs.TestConfigs;
import com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import com.erudio.integrationtests.vo.AccountCredentialsVo;
import com.erudio.integrationtests.vo.TokenVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.annotation.XmlRootElement;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerXmlTest extends AbstractIntegrationTest {

    private static TokenVo tokenVo;

    @Test
    @Order(1)

    public void testSignin() {
        AccountCredentialsVo user = new AccountCredentialsVo("arthur", "admin123");

        tokenVo = given()
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_XML)
                .body(user)
                    .when()
                .post()
                    .then()
                        .statusCode(200)
                            .extract()
                            .body()
                                .as(TokenVo.class);

        assertNotNull(tokenVo.getAccessToken());
        assertNotNull(tokenVo.getRefreshToken());
    }

    @Test
    @Order(2)
    public void testRefresh() {
        AccountCredentialsVo user = new AccountCredentialsVo("arthur", "admin123");

        var newTokenVo = given()
                .basePath("/auth/refresh")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("username", tokenVo.getUserName())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVo.getRefreshToken())
                    .when()
                .put("{username}")
                    .then()
                .statusCode(200)
                    .extract()
                        .body()
                            .as(TokenVo.class);

        assertNotNull(newTokenVo.getAccessToken());
        assertNotNull(newTokenVo.getRefreshToken());
    }

}
