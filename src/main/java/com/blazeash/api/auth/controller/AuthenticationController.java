/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.auth.controller;



import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import com.blazeash.api.auth.config.JWTGenerator;
import com.blazeash.api.auth.config.LoginUser;
import com.blazeash.api.auth.resource.JWTResource;
import com.blazeash.api.core.controller.AbstractController;
import com.blazeash.api.core.exception.BaEntityNotFoundException;
import com.blazeash.api.core.exception.BaIllegalUserException;
import com.blazeash.api.core.exception.BaResourceNotFoundException;
import com.blazeash.api.core.exception.BaValidationErrorException;
import com.blazeash.api.core.utils.BCryptEncoder;
import com.blazeash.api.user.controller.UserController;
import com.blazeash.api.user.entity.UserEntity;
import com.blazeash.api.user.resource.UserResource;
import com.blazeash.api.user.service.UserService;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
public class AuthenticationController extends AbstractController
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

    private final UserDetailsService userDetailsService;

    /**
     * ログイン
     *
     * @return JWTトークン
     */
    @PostMapping(LOGIN_URI)
    @CrossOrigin
    public ResponseEntity<JWTResource> login(@Valid @RequestBody LoginUser loginUser) {
        var userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
        if (userDetails == null) {
            throw new BaEntityNotFoundException(UserEntity.class, loginUser.getUsername());
        }
        this.checkLoginUser(loginUser, userDetails);
        final var expirationTime = generator.generateExpirationTime();
        loginUser = (LoginUser) userDetails;
        final var accessToken = generator.generateAccessToken(loginUser.getUuid(), expirationTime);
        return ResponseEntity.ok().body(
            JWTResource.builder()
                .accessToken(accessToken)
                .expiresIn(expirationTime.getTime())
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
     * @param userDetails ユーザー
     */
    private void checkLoginUser(LoginUser loginUser, UserDetails userDetails) {
        if (loginUser == null || userDetails == null) {
            throw new BaResourceNotFoundException(UserResource.class, null);
        }
        if (userDetails.isEnabled()) {
            // パスワード一致性チェック
            var isMatched =
                BCryptEncoder.getInstance().matches(loginUser.getPassword(), userDetails.getPassword());
            if (!isMatched) {
                throw new BaValidationErrorException("Password was not matched, please check again.");
            }
        } else {
            throw new BaIllegalUserException(userDetails.getUsername());
        }
    }
}
