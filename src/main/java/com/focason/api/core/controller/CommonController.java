/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.controller;



import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
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
public class CommonController
{
    /**
     * ヘルスチェック
     *
     * @return Void
     */
    @RequestMapping(method = RequestMethod.GET, path = "/heartbeat")
    public ResponseEntity<Void> heartbeat() {
        return ResponseEntity.noContent().build();
    }

    /**
     * 文字列エンコーダー
     *
     * @param jsonObject 転換文字列
     * @return 転換された文字列
     */
    @RequestMapping(method = RequestMethod.POST, path = "/base64")
    public ResponseEntity<String> base64Generator(@RequestBody String jsonObject) {
        String result = null;
        if (jsonObject != null) {
            result = Base64.getEncoder().encodeToString(jsonObject.getBytes());
        }
        return ResponseEntity.ok(result);
    }
}
