package com.lemonico.auth.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.auth.resource.LoginUserResource;
import com.lemonico.auth.service.AuthorityService;
import com.lemonico.common.service.LoginService;
import com.lemonico.core.utils.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 認証認可コントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "認証認可")
public class AuthorityController
{

    private final AuthorityService authorityService;
    private final LoginService loginService;
    private final HttpServletResponse response;

    /**
     * ログイン
     *
     * @param loginUserResource ログインユーザー情報
     * @return 処理結果
     * @since 1.0.0
     */
    @PostMapping("/auth/login")
    @ApiOperation(value = "ログイン", notes = "必ずJSON格式を入力してください")
    public ResponseEntity<Void> login(@RequestBody LoginUserResource loginUserResource) {
        response.addHeader(HttpHeaders.AUTHORIZATION, authorityService.login(loginUserResource));
        return ResponseEntity.ok().build();
    }

    /**
     * 新規登録
     *
     * @param jsonObject 登録情報
     * @return 処理結果
     * @since 1.0.0
     */
    @PostMapping("/auth/register")
    @ApiOperation(value = "登録", notes = "必ずJSON格式を入力してください")
    public JSONObject register(
        @ApiParam(name = "jsonObject", value = "login_pw,login_id") @RequestBody JSONObject jsonObject) {
        CommonUtils.hashAllRequired(jsonObject, "login_pw,login_id");
        return authorityService.register(jsonObject);
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

    /**
     * 店鋪側ログイン処理
     *
     * @param jsonObject ログイン情報
     * @param response {@link HttpServletResponse}
     * @return 処理結果情報
     */
    @RequestMapping(value = "/auth/login/store", method = RequestMethod.POST)
    @ApiOperation(value = "店铺侧登陆")
    public JSONObject getClientToken(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        CommonUtils.hashAllRequired(jsonObject, "client_id,user_id");
        return loginService.getClientToken(jsonObject, response);
    }

    /**
     * 倉庫側ログイン処理
     *
     * @param jsonObject ログイン情報
     * @param response {@link HttpServletResponse}
     * @return 処理結果情報
     */
    @RequestMapping(value = "/auth/login/warehouse", method = RequestMethod.POST)
    @ApiOperation(value = "仓库侧登陆")
    public JSONObject getWarehouseToken(@RequestBody JSONObject jsonObject, HttpServletResponse response) {
        CommonUtils.hashAllRequired(jsonObject, "user_id,warehouse_id");
        return loginService.getWarehouseToken(jsonObject, response);
    }

}
