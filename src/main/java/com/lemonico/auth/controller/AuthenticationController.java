/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.auth.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.auth.config.JWTGenerator;
import com.lemonico.auth.config.LoginUser;
import com.lemonico.auth.resource.JWTResource;
import com.lemonico.auth.service.AuthorityService;
import com.lemonico.core.exception.LcEntityNotFoundException;
import com.lemonico.core.exception.LcIllegalUserException;
import com.lemonico.core.exception.LcResourceNotFoundException;
import com.lemonico.core.exception.LcValidationErrorException;
import com.lemonico.core.utils.BCryptEncoder;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    /**
     * ログインURI
     */
    private static final String LOGIN_URI = "/login";

    /**
     * 登録URI
     */
    private static final String REGISTER_URI = "/register";

    private final JWTGenerator generator;

    private final UserDetailsService userDetailsService;

    private final AuthorityService authorityService;

    /**
     * ログイン
     *
     * @return JWTトークン
     */
    @PostMapping(LOGIN_URI)
    @CrossOrigin
    public ResponseEntity<JWTResource> login(@RequestBody LoginUser loginUser) {
        var userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
        if (userDetails == null) {
             throw new LcEntityNotFoundException(LoginUser.class, loginUser.getUsername());
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
    // @PostMapping(REGISTER_URI)
    // public ResponseEntity<Void> register(
    // @Valid @RequestBody UserResource resource,
    // UriComponentsBuilder uriBuilder) {
    // var id = userService.createResource(
    // resource.withUuid(UUID.randomUUID().toString())).getId();
    // var uri = relativeTo(uriBuilder).withMethodCall(on(UserController.class).getUser(id))
    // .build()
    // .encode()
    // .toUri();
    // return ResponseEntity.created(uri).build();
    // }

    /**
     * ログインクライアント有効性チェック
     *
     * @param loginUser ログインクライアント
     * @param userDetails ユーザー
     */
    private void checkLoginUser(LoginUser loginUser, UserDetails userDetails) {
        if (loginUser == null || userDetails == null) {
             throw new LcResourceNotFoundException(LoginUser.class, null);
        }
        if (userDetails.isEnabled()) {
            // パスワード一致性チェック
            var isMatched =
                BCryptEncoder.getInstance().matches(loginUser.getPassword(), userDetails.getPassword());
            if (!isMatched) {
                 throw new LcValidationErrorException("Password was not matched, please check again.");
            }
        } else {
             throw new LcIllegalUserException(userDetails.getUsername());
        }
    }

    /**
     * ログアウト
     *
     * @return 処理結果
     * @since 1.0.0
     */
    @GetMapping("/auth/logout")
    @ApiOperation(value = "ログアウト")
    public JSONObject logout() {
        return authorityService.logout();
    }

    // /**
    // * 店鋪側ログイン処理
    // *
    // * @param jsonObject ログイン情報
    // * @param response {@link HttpServletResponse}
    // * @return 処理結果情報
    // */
    // @RequestMapping(value = "/auth/login/store", method = RequestMethod.POST)
    // @ApiOperation(value = "店铺侧登陆")
    // public JSONObject getClientToken(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
    // CommonUtils.hashAllRequired(jsonObject, "client_id,user_id");
    // return loginService.getClientToken(jsonObject, response);
    // }
    //
    // /**
    // * 倉庫側ログイン処理
    // *
    // * @param jsonObject ログイン情報
    // * @param response {@link HttpServletResponse}
    // * @return 処理結果情報
    // */
    // @RequestMapping(value = "/auth/login/warehouse", method = RequestMethod.POST)
    // @ApiOperation(value = "仓库侧登陆")
    // public JSONObject getWarehouseToken(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
    // CommonUtils.hashAllRequired(jsonObject, "user_id,warehouse_id");
    // return loginService.getWarehouseToken(jsonObject, response);
    // }
}
