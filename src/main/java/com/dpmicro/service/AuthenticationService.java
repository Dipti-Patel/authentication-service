package com.dpmicro.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpmicro.security.JwtTokenProvider;

@Service
public class AuthenticationService {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
	@Autowired
    JwtTokenProvider jwtTokenProvider;

	public String generateToken(Map<String, Object> authMap) {
		logger.info("in method : generateToken");
		String userName = null;
		String name = String.valueOf(authMap.get("name"));
		String mobileNo =String.valueOf(authMap.get("mobileNo"));
		String  email = String.valueOf(authMap.get("email"));
		logger.info("name : " + name);
		logger.info("mobileNo ::" + mobileNo);
		userName = name + mobileNo + email;
		Set<String> appUserRoleNames = Stream.of(String.valueOf(authMap.get("appUserRoleNames")).trim().split("\\s*,\\s*"))
                 .collect(Collectors.toSet());
		System.out.println("userName ::  " + userName);
		
		String token = jwtTokenProvider.createToken(userName, appUserRoleNames);
		return token;
	}

	public String validateToken(String token) {
		return jwtTokenProvider.validateToken(token);
	}

}
