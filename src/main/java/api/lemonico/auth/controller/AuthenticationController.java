/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.controller;



import api.lemonico.auth.config.JWTGenerator;
import api.lemonico.auth.config.LoginUser;
import api.lemonico.auth.resource.JWTResource;
import api.lemonico.controller.ClientController;
import api.lemonico.core.exception.LcEntityNotFoundException;
import api.lemonico.core.exception.LcIllegalUserException;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcValidationErrorException;
import api.lemonico.core.utils.BCryptEncoder;
import api.lemonico.domain.ClientStatus;
import api.lemonico.entity.Client;
import api.lemonico.resource.ClientResource;
import api.lemonico.service.ClientService;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

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

    private final ClientService clientService;

    private final JWTGenerator generator;

    private final ClientController clientController;

    /**
     * ログイン
     *
     * @return JWTトークン
     */
    @PostMapping(LOGIN_URI)
    public ResponseEntity<JWTResource> login(@RequestBody LoginUser loginUser) {
        final var client = clientService.getResourceByEmail(loginUser.getUsername());
        if (client.isEmpty()) {
            throw new LcEntityNotFoundException(Client.class, loginUser.getUsername());
        }
        this.checkLoginUser(loginUser, client);
        final var expirationTime = generator.generateExpirationTime();
        final var accessToken = generator.generateAccessToken(client.get().getEmail(), expirationTime);
        return ResponseEntity.ok().body(
            JWTResource.builder()
                .accessToken(accessToken)
                .expirationTime(expirationTime)
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
        @Valid @RequestBody ClientResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = clientService.createResource(
                resource.withClientCode(UUID.randomUUID().toString().substring(0, 8))).getId();
        var uri = relativeTo(uriBuilder)
                .withMethodCall(clientService.getResource(id))
                .build()
                .encode()
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * ログインクライアント有効性チェック
     *
     * @param loginUser ログインクライアント
     * @param client クライアント
     */
    private void checkLoginUser(LoginUser loginUser, Optional<ClientResource> client) {
        if (loginUser == null || client.isEmpty()) {
            throw new LcResourceNotFoundException(ClientResource.class, null);
        }
        switch (ClientStatus.of(client.get().getStatus())) {
            case NORMAL:
                // パスワード一致性チェック
                var isMatched =
                    BCryptEncoder.getInstance().matches(loginUser.getPassword(), client.get().getPassword());
                if (!isMatched) {
                    throw new LcValidationErrorException("Password was not matched, please check again.");
                }
                break;
            case BLOCKED:
                throw new LcIllegalUserException(client.get().getEmail(), ClientStatus.BLOCKED.name());
            case LOGOUT:
                throw new LcIllegalUserException(client.get().getEmail(), ClientStatus.LOGOUT.name());
        }
    }
}
