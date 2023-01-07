/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.auth.controller;



import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import com.focason.api.auth.config.JWTGenerator;
import com.focason.api.auth.config.LoginUser;
import com.focason.api.auth.resource.JWTResource;
import com.focason.api.core.controller.AbstractController;
import com.focason.api.core.exception.FsEntityNotFoundException;
import com.focason.api.core.exception.FsIllegalUserException;
import com.focason.api.core.exception.FsResourceNotFoundException;
import com.focason.api.core.exception.FsValidationErrorException;
import com.focason.api.core.utils.FsBCryptEncoder;
import com.focason.api.user.controller.UserController;
import com.focason.api.user.entity.UserEntity;
import com.focason.api.user.resource.UserResource;
import com.focason.api.user.service.UserService;
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
    public ResponseEntity<JWTResource> login(@Valid @RequestBody LoginUser loginUser) {
        var userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
        if (userDetails == null) {
            throw new FsEntityNotFoundException(UserEntity.class, loginUser.getUsername());
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
            throw new FsResourceNotFoundException(UserResource.class, null);
        }
        if (userDetails.isEnabled()) {
            // パスワード一致性チェック
            var isMatched =
                FsBCryptEncoder.getInstance().matches(loginUser.getPassword(), userDetails.getPassword());
            if (!isMatched) {
                throw new FsValidationErrorException("Password was not matched, please check again.");
            }
        } else {
            throw new FsIllegalUserException(userDetails.getUsername());
        }
    }
}
