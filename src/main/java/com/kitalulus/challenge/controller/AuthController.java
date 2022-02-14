package com.kitalulus.challenge.controller;

import com.kitalulus.challenge.util.TokenHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value ="/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getToken(@RequestParam String user) throws Exception {
        return TokenHelper.generateToken(user, 60);
    }
}
