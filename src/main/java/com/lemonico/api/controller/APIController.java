package com.lemonico.api.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.api.service.ApiErrorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * APIコントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "api报错信息处理")
public class APIController
{

    private final ApiErrorService apiErrorService;

    /**
     * APIエラー件数を更新する
     *
     * @param jsonObject 更新情報
     * @param request {@link HttpServletRequest}
     * @return 処理結果情報
     */
    @ApiOperation(value = "更改api报错信息次数", notes = "更改api报错信息次数")
    @PostMapping("/store/api/error/update/count/")
    public JSONObject updateErrorCount(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        return apiErrorService.updateErrorCount(jsonObject, request);
    }

    /**
     * APIの認証情報の有効期間をチェックする
     *
     * @param client_id 店鋪ID
     * @return 処理結果情報
     */
    @ApiOperation(value = "判断是否有api快要过期", notes = "判断是否有api快要过期")
    @GetMapping("/store/api/expired/{client_id}")
    public JSONObject getApiExpired(@PathVariable("client_id") String client_id) {
        return apiErrorService.getApiExpired(client_id);
    }
}
