package com.lemonico.common.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms201_client;
import com.lemonico.common.bean.Ms204_customer_func;
import com.lemonico.common.bean.Tc209_setting_template;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.CommonFunctionDao;
import com.lemonico.common.service.ClientService;
import com.lemonico.common.service.CommonService;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.apiLimit.GetIp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.UnknownHostException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店舗管理コントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "店舗管理")
public class ClientController
{

    private final ClientService clientService;
    private final ClientDao clientDao;
    private final CommonService commonService;
    private final CommonFunctionDao comFunctionDao;

    /**
     * 倉庫所属の店舗リストを取得する
     * TODO
     *
     * @param warehouse_cd 倉庫コード
     * @param request {@link HttpServletRequest}
     * @return 処理結果情報
     */
    @ApiOperation(value = "倉庫所属の店舗リスト取得")
    @GetMapping(value = "/client/info/{warehouse_cd}")
    public JSONObject getClientsByWarehouseCd(@PathVariable("warehouse_cd") String warehouse_cd,
        HttpServletRequest request) {
        return CommonUtils.success(commonService.getClientsByWarehouseCd(warehouse_cd, request));
    }

    /**
     * 倉庫所属の店鋪IDリストを取得する
     *
     * @param warehouse_cd 倉庫コード
     * @return 店鋪IDリスト
     * @since 1.0.0
     */
    @ApiOperation(value = "倉庫所属の店鋪IDリスト取得", notes = "倉庫所属の店鋪IDリスト取得")
    @GetMapping("/client/getAllClientIdByWarehouseId")
    public List<String> getClientIdsByWarehouseCd(String warehouse_cd) {
        return clientDao.getAllClientIdByWarehouseId(warehouse_cd);
    }

    /**
     * 根据店铺ID获取仓库ID
     *
     * @param client_id 店舗ID
     * @return 処理結果情報
     */
    @ApiOperation(value = "根据店铺ID获取仓库ID", notes = "根据店铺ID获取仓库ID")
    @GetMapping(value = "/getWarehouseIdByClientId")
    public JSONObject getWarehouseIdByClientId(String client_id) {
        String id = comFunctionDao.getWarehouseIdByClientId(client_id);
        return CommonUtils.success(id);
    }

    /**
     * 获取用户功能权限
     *
     * @param userId ユーザーID
     * @param functionCd 機能ID
     * @return {@link Ms204_customer_func}
     */
    @ApiOperation(value = "获取用户功能权限", notes = "获取用户功能权限")
    @GetMapping(value = "/getUserFunction")
    public JSONObject getUserFunction(String userId, String functionCd) {
        return CommonUtils.success(comFunctionDao.getUserFunction(userId, functionCd));
    }

    /**
     * 获取客户制定的csv出力模板数据
     *
     * @param client_id 店舗ID
     * @param warehouse_cd 倉庫コード
     * @param template_nm テンプレート名称
     * @param yoto_id 用途ID
     * @return {@link Tc209_setting_template}
     */
    @ApiOperation(value = "获取客户制定的csv出力模板数据", notes = "获取客户制定的csv出力模板数据")
    @GetMapping(value = "/client/setting/template")
    public JSONObject getClientCsvTemplate(String client_id, String warehouse_cd, String template_nm, String yoto_id) {
        List<Tc209_setting_template> tc209 =
            comFunctionDao.getClientCsvTemplate(client_id, warehouse_cd, template_nm, yoto_id);
        return CommonUtils.success(tc209);
    }

    /**
     * 店鋪情報を取得する
     *
     * @param clientId 店鋪ID
     * @return {@link Ms201_client}
     * @since 1.0.0
     */
    @ApiOperation(value = "店鋪詳細取得", notes = "店鋪詳細取得")
    @GetMapping("/client/{client_id}")
    public Ms201_client getClientInfo(@PathVariable("client_id") String clientId) {
        return clientService.getClientInfo(clientId);
    }

    /**
     * @Description: 获取登录用户的信息
     * @Param: 登录邮箱
     * @return: 店铺名，用户名
     * @Date: 2020/8/24
     */
    @ApiOperation(value = "获取登录用户的信息", notes = "获取登录用户的信息")
    @GetMapping("/client/login_info/{client_id}")
    public JSONObject getLoginUserInfo(@PathVariable("client_id") String client_id, HttpServletRequest servletRequest)
        throws UnknownHostException {
        String user_id = CommonUtils.getToken("user_id", servletRequest);
        System.err.println(GetIp.getRequestHeadersInMap(servletRequest));
        Ms201_client loginUserInfo = clientService.getLoginUserInfo(client_id, user_id);
        return CommonUtils.success(loginUserInfo);
    }

    /**
     * 店鋪や倉庫の持ち機能を取得する
     *
     * @param client_id 店鋪ID
     * @param warehouse_cd 倉庫コード
     * @param function_cd 機能コード
     * @return 機能リスト
     * @since 1.0.0
     */
    @ApiOperation(value = "获取登录用户或仓库的機能")
    @GetMapping("/client/function_info")
    public JSONObject getFunctions(String client_id, String warehouse_cd, String function_cd) {
        return clientService.getFunctions(warehouse_cd, client_id, function_cd);
    }

    /**
     * @Description: 获取登录用户的clientid
     * @Param: user_id
     * @return: 用户名
     * @Author: Liocng
     * @Date: 2020/12/3
     */
    @ApiOperation(value = "获取登录用户的clientid", notes = "获取登录用户的clientid")
    @GetMapping("/client/get_client_id/{user_id}")
    public String getClientIdByUserId(@PathVariable("user_id") String user_id) {
        return clientService.getClientIdByUserId(user_id);
    }
}
