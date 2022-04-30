/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.controller;



import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import api.lemonico.auth.config.JWTGenerator;
import api.lemonico.auth.config.LoginUser;
import api.lemonico.auth.resource.JWTResource;
import api.lemonico.controller.UserController;
import api.lemonico.core.exception.LcEntityNotFoundException;
import api.lemonico.core.exception.LcIllegalUserException;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcValidationErrorException;
import api.lemonico.core.utils.BCryptEncoder;
import api.lemonico.domain.UserType;
import api.lemonico.entity.UserEntity;
import api.lemonico.resource.UserResource;
import api.lemonico.service.UserService;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
    /**
     * ログインURI
     */
    private static final String LOGIN_URI = "/login";

    /**
     * 登録URI
     */
    private static final String REGISTER_URI = "/register";

    private final UserService userService;

    private final JWTGenerator generator;

    /**
     * ログイン
     *
     * @return JWTトークン
     */
    @PostMapping(LOGIN_URI)
    @CrossOrigin
    public ResponseEntity<JWTResource> login(@RequestBody LoginUser loginUser) {
        final var user = userService.getResourceByEmail(loginUser.getUsername());
        if (user.isEmpty()) {
            throw new LcEntityNotFoundException(UserEntity.class, loginUser.getUsername());
        }
        this.checkLoginUser(loginUser, user);
        final var expirationTime = generator.generateExpirationTime();
        final var accessToken = generator.generateAccessToken(user.get().getEmail(), expirationTime);
        return ResponseEntity.ok().body(
            JWTResource.builder()
                .accessToken(accessToken)
                .expirationTime(expirationTime.getTime())
                .build());
    }

    /**
     * 登録
     *
     * @param resource クライアントリソース
     * @return クライアントリソース作成APIレスポンス
     */
    @PostMapping(REGISTER_URI)
    public ResponseEntity<Void> register(
        @Valid @RequestBody UserResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = userService.createResource(
            resource.withUuid(UUID.randomUUID().toString())).getId();
        var uri = relativeTo(uriBuilder).withMethodCall(on(UserController.class).getUser(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * ログインクライアント有効性チェック
     *
     * @param loginUser ログインクライアント
     * @param user クライアント
     */
    private void checkLoginUser(LoginUser loginUser, Optional<UserResource> user) {
        if (loginUser == null || user.isEmpty()) {
            throw new LcResourceNotFoundException(UserResource.class, null);
        }
        switch (UserType.of(user.get().getType())) {
            case SILVER:
            case GOLD:
            case PREMIUM:
                // パスワード一致性チェック
                var isMatched =
                    BCryptEncoder.getInstance().matches(loginUser.getPassword(), user.get().getPassword());
                if (!isMatched) {
                    throw new LcValidationErrorException("Password was not matched, please check again.");
                }
                break;
            case LOGOUT:
                throw new LcIllegalUserException(user.get().getEmail(), UserType.LOGOUT.name());
        }
    }
}
