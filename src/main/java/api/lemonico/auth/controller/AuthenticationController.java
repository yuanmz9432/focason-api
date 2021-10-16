/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.controller;



import api.lemonico.auth.config.JWTGenerator;
import api.lemonico.auth.config.LoginUser;
import api.lemonico.auth.resource.JWTResource;
import api.lemonico.user.service.UserService;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 認証コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/auth")
public class AuthenticationController
{

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    /**
     * ログインURI
     */
    private static final String LOGIN_URI = "/login";

    private final UserService service;

    private final JWTGenerator generator;

    /**
     * ログイン
     *
     * @return JWTトークン
     */
    @PostMapping(LOGIN_URI)
    public ResponseEntity<JWTResource> login(@RequestBody LoginUser loginUser) {
        logger.info("username: {}, password: {}", loginUser.getUsername(), loginUser.getPassword());

        final LoginUser user = service.getLoginUserByEmail(loginUser.getEmail());
        final Date expirationTime = generator.generateExpirationTime();
        final String accessToken = generator.generateAccessToken(user.getUsername(), expirationTime);
        return ResponseEntity.ok().body(
            JWTResource.builder()
                .accessToken(accessToken)
                .expirationTime(expirationTime)
                .build());
    }
}
