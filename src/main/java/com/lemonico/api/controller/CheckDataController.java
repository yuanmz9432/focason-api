package com.lemonico.api.controller;



import com.lemonico.batch.ScheduleTasks;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 異常データ検索コントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "異常データ検索")
public class CheckDataController
{

    private final ScheduleTasks tasks;

    /**
     * 異常データを集計する
     *
     * @since 1.0.0
     */
    @GetMapping("/error-data/excel")
    public ResponseEntity<Void> getErrorDataExcel() {
        tasks.searchErrorData();
        return ResponseEntity.ok().build();
    }
}
