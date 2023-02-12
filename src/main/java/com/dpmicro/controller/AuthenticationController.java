package com.dpmicro.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpmicro.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
	
	@Autowired
    AuthenticationService authenticationService;

	@PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(@RequestBody Map<String,Object> authMap) {
    	return authenticationService.generateToken(authMap);
    }
	
	@GetMapping(value = "/validateToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public String validateToken(@RequestParam String token) {
    	return authenticationService.validateToken(token);
    }

	
}
