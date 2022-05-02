/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ヘルスチェックコントローラ
 *
 * @since 1.1.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HealthCheckController
{
    @RequestMapping(method = RequestMethod.GET, path = "/heartbeat")
    public ResponseEntity<Void> heartbeat() {

        return ResponseEntity.noContent().build();
    }
}
