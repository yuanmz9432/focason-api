// =====================================================
// Copyright 2022 Lemonico Co.,Ltd. AllRights Reserved.
// =====================================================
package com.lemonico;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Lemonico Spring Boot アプリケーション
 */
@SpringBootApplication
@OpenAPIDefinition
@EnableTransactionManagement
@EnableAsync
@MapperScan(value = {
    "com.lemonico.admin.dao",
    "com.lemonico.store.dao",
    "com.lemonico.wms.dao",
    "com.lemonico.common.dao",
    "com.lemonico.ntm.dao",
    "com.lemonico.batch.dao"
})
public class LemonicoAPIServer
{
    public static void main(String[] args) {
        SpringApplication.run(LemonicoAPIServer.class, args);
    }
}
