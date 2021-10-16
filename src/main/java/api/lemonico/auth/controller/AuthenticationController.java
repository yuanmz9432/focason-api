package api.lemonico.auth.controller;

import api.lemonico.auth.resource.JWTResource;
import api.lemonico.core.annotation.LcConditionParam;
import api.lemonico.core.annotation.LcPaginationParam;
import api.lemonico.core.annotation.LcSortParam;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.attribute.LcSort;
import api.lemonico.core.auth.JWTGenerator;
import api.lemonico.core.auth.LoginUser;
import api.lemonico.customer.repository.CustomerRepository;
import api.lemonico.customer.resource.CustomerResource;
import api.lemonico.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 認証コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    /**
     * ログインURI
     */
    private static final String LOGIN_URI = "/login";

    private final CustomerService service;

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
                        .build()
        );
    }
}
