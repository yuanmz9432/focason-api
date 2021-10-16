/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.controller;



import api.lemonico.auth.config.JWTGenerator;
import api.lemonico.auth.config.LoginUser;
import api.lemonico.auth.domain.UserStatus;
import api.lemonico.auth.resource.JWTResource;
import api.lemonico.common.BCryptEncoder;
import api.lemonico.core.exception.LcEntityNotFoundException;
import api.lemonico.core.exception.LcIllegalUserException;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcValidationErrorException;
import api.lemonico.user.entity.User;
import api.lemonico.user.resource.UserResource;
import api.lemonico.user.service.UserService;
import java.util.Optional;
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

        final var user = service.getResourceByEmail(loginUser.getUsername());
        if (user.isEmpty()) {
            throw new LcEntityNotFoundException(User.class, loginUser.getUsername());
        }
        this.checkLoginUser(loginUser, user);
        final var expirationTime = generator.generateExpirationTime();
        final var accessToken = generator.generateAccessToken(user.get().getEmail(), expirationTime);
        return ResponseEntity.ok().body(
            JWTResource.builder()
                .accessToken(accessToken)
                .expirationTime(expirationTime)
                .build());
    }

    /**
     * ログインユーザー有効性チェック
     *
     * @param loginUser ログインユーザー
     * @param user ユーザー
     */
    private void checkLoginUser(LoginUser loginUser, Optional<UserResource> user) {
        if (loginUser == null || user.isEmpty()) {
            throw new LcResourceNotFoundException(UserResource.class, null);
        }
        switch (UserStatus.of(user.get().getStatus())) {
            case NORMAL:
                // パスワード一致性チェック
                var isMatched = BCryptEncoder.getInstance().matches(loginUser.getPassword(), user.get().getPassword());
                if (!isMatched) {
                    throw new LcValidationErrorException("Password was not matched, please check again.");
                }
                break;
            case BLOCKED:
                throw new LcIllegalUserException(user.get().getEmail(), UserStatus.BLOCKED.name());
            case LOGOUT:
                throw new LcIllegalUserException(user.get().getEmail(), UserStatus.LOGOUT.name());
        }
    }
}
