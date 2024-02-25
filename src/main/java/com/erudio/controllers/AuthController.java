package com.erudio.controllers;

import com.erudio.data.vo.v1.security.AccountCredentialsVo;
import com.erudio.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServices authServices;

    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin")
    public ResponseEntity signin(@RequestBody AccountCredentialsVo data){
        if(checkIfParamsIsNotNull(data)){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        }
        var token = authServices.signin(data);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        }
        return token;
    }

    @Operation(summary = "")
    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity refreshToken(@PathVariable(name = "username") String username,
                                       @RequestHeader(name = "Authorization") String refreshToken) {
        if(checkIfParamsIsNotNull(username, refreshToken)){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        }
        var token = authServices.refreshToken(username, refreshToken);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        }
        return token;
    }

    private static boolean checkIfParamsIsNotNull(String username, String refreshToken) {
        return refreshToken == null || refreshToken.isBlank() ||
                username == null || username.isBlank();
    }

    private static boolean checkIfParamsIsNotNull(AccountCredentialsVo data) {
        return data == null
                || data.getUserName() == null || data.getUserName().isBlank()
                || data.getPassWord() == null || data.getPassWord().isBlank();
    }
}
