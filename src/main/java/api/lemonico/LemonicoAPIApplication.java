/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Lemonico API アプリケーション
 */
@SpringBootApplication
@EnableWebMvc
public class LemonicoAPIApplication
{
    public static void main(String[] args) {
        SpringApplication.run(LemonicoAPIApplication.class, args);
    }
}
