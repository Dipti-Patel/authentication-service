package com.dpmicro.security;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.dpmicro.service.AuthenticationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {

	private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${security.jwt.token.secret-key:secret-key}")
	private String secretKey;

	@Value("${security.jwt.token.expire-length:3600000}")
	private long validityInMilliseconds = 3600000; // 1h

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	public String createToken(String username, Set<String> appUserRoleNames) {

		try {
//            String encUserName = AES.encrypt(username, secretKey);
			Claims claims = Jwts.claims().setSubject(username);
			claims.put("auth", appUserRoleNames.stream().map(s -> new SimpleGrantedAuthority(s))
					.filter(Objects::nonNull).collect(Collectors.toList()));

			Date now = new Date();
			Date validity = new Date(now.getTime() + validityInMilliseconds);

			Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secretKey),
					SignatureAlgorithm.HS512.getJcaName());

//            Jwts.parser().setSigningKey("secret").parseClaimsJws(token).getBody();

			return "Bearer " + Jwts.builder().setClaims(claims).setSubject(username)
//					.setSubject(encUserName)
//					.setId(UUID.randomUUID().toString())
					.setIssuedAt(now).setExpiration(validity).signWith(hmacKey).compact();

		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public String validateToken(String token) {
		try {
			Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secretKey),
					SignatureAlgorithm.HS256.getJcaName());

			return AES.decrypt(
					Jwts.parserBuilder().setSigningKey(hmacKey).build().parseClaimsJws(token).getBody().getSubject(),
					secretKey);
		} catch (JwtException | IllegalArgumentException e) {
			logger.error("Invalid or Expired Token ::   ");
			throw e;
		}
	}

}
