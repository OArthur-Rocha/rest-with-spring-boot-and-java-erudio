package com.erudio.services;

import com.erudio.data.vo.v1.security.AccountCredentialsVo;
import com.erudio.data.vo.v1.security.TokenVo;
import com.erudio.data.vo.v1.security.jwt.JwtTokenProvider;
import com.erudio.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServices {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository repository;

    public ResponseEntity signin(AccountCredentialsVo data){
        try{
            var userName = data.getUserName();
            var password = data.getPassWord();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userName, password));

            var user = repository.findByUserName(userName);

            var tokenResponse = new TokenVo();
            if(user != null){
                tokenResponse = tokenProvider.createAccessToken(userName, user.getRoles());
            }else {
                throw new UsernameNotFoundException("Username "+userName+ " not found!");
            }
            return ResponseEntity.ok(tokenResponse);
        }catch (Exception e){
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }

    public ResponseEntity refreshToken(String username, String refreshToken){
        var user = repository.findByUserName(username);

        var tokenResponse = new TokenVo();
        if(user != null){
            tokenResponse = tokenProvider.refreshToken(refreshToken);
        }else {
            throw new UsernameNotFoundException("Username "+username+ " not found!");
        }
        return ResponseEntity.ok(tokenResponse);
        }


}
