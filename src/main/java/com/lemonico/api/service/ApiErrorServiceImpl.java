package com.lemonico.api.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms018_api_error;
import com.lemonico.common.bean.Tc203_order_client;
import com.lemonico.common.dao.ApiErrorDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.constants.Constants;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @className: ApiErrorServiceImpl
 * @description: API报错信息记录实现类
 * @date: 2021/8/12 9:56
 **/
@Service
public class ApiErrorServiceImpl implements ApiErrorService
{

    private final static Logger logger = LoggerFactory.getLogger(ApiErrorServiceImpl.class);

    @Resource
    private ApiErrorDao apiErrorDao;

    /**
     * @param errOrderClients : 报错信息的api设定信息
     * @description: 保存api报错次数
     * @return: void
     * @date: 2021/8/12 17:01
     */
    @Override
    @Transactional
    public JSONObject insertApiErrorCount(List<Tc203_order_client> errOrderClients) {

        Date nowTime = DateUtils.getNowTime(null);

        // 获取到tc203 的map key为 id_client_id
        Map<String, Tc203_order_client> orderClientMap = errOrderClients.stream().distinct()
            .collect(Collectors.toMap(x -> x.getId() + "_" + x.getClient_id(), o -> o));

        // 获取所有的报错信息
        List<Ms018_api_error> allApiError = apiErrorDao.getAllApiError();

        // 转为map key order_id_client_id
        Map<String, Ms018_api_error> apiErrorMap =
            allApiError.stream().collect(Collectors.toMap(x -> x.getOrder_id() + "_" + x.getClient_id(), o -> o));

        // 保存之前存在的 报错信息对象
        ArrayList<Ms018_api_error> existApiErrors = new ArrayList<>();
        // 保存之前不存在的保存信息对象
        ArrayList<Ms018_api_error> notExistApiErrors = new ArrayList<>();
        orderClientMap.forEach((key, value) -> {
            // 遍历本次报错集合
            if (apiErrorMap.containsKey(key)) {
                // 证明之前已经登录过 只需要该其报错次数
                Ms018_api_error apiError = apiErrorMap.get(key);
                apiError.setError_count(apiError.getError_count() + 1);
                apiError.setUpd_date(nowTime);
                existApiErrors.add(apiError);
            } else {
                // key值不存在 证明第一次记录报错信息 需要新规
                Ms018_api_error apiError = new Ms018_api_error();
                apiError.setOrder_id(value.getId());
                apiError.setClient_id(value.getClient_id());
                apiError.setTemplate(value.getTemplate());
                apiError.setError_count(1);
                apiError.setIns_usr("admin");
                apiError.setIns_date(nowTime);
                apiError.setUpd_usr("admin");
                apiError.setUpd_date(nowTime);
                apiError.setDel_flg(0);
                notExistApiErrors.add(apiError);
            }
        });

        if (existApiErrors.size() != 0) {
            // 批量修改之前存在的 报错次数
            apiErrorDao.updateErrorCount(existApiErrors);
        }
        if (notExistApiErrors.size() != 0) {
            // 批量新规 报错信息
            apiErrorDao.insertApiError(notExistApiErrors);
        }
        return CommonUtils.success();
    }

    /**
     * @param client_id : 店铺ID
     * @description: 获取店铺报错信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/12 12:43
     */
    @Override
    public JSONObject getClientApiError(String client_id) {
        int count = 5;
        List<Ms018_api_error> clientApiError = apiErrorDao.getClientApiError(client_id, count);
        if (!StringTools.isNullOrEmpty(clientApiError) && clientApiError.size() != 0) {
            return CommonUtils.success();
        } else {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param jsonObject : client_id：店铺Id order_id：api设定Id template：模板 count：错误次数
     * @param request : 请求
     * @description: 更改api报错信息次数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/12 14:36
     */
    @Override
    @Transactional
    public JSONObject updateErrorCount(JSONObject jsonObject, HttpServletRequest request) {
        String loginNm = CommonUtils.getToken("login_nm", request);
        Date nowTime = DateUtils.getNowTime(null);
        // 修改单个api报错信息次数
        try {
            apiErrorDao.updateApiErrorCount(jsonObject, loginNm, nowTime);
            return CommonUtils.success();
        } catch (Exception e) {
            logger.error("修改api报错信息失败, 店铺Id={}, apiId={}", jsonObject.getString("client_id"),
                jsonObject.getString("order_id"));
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param client_id : 店铺Id
     * @description: 判断是否有api快要过期
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/16 14:52
     */
    @Override
    public JSONObject getApiExpired(String client_id) {

        // 目前拥有过期时间 功能的只有 雅虎和乐天
        List<String> templateList = Arrays.asList(Constants.YAHOO, Constants.RAKUTEN);

        // 获取到乐天和雅虎的 api设定信息
        List<Tc203_order_client> apiExpired = apiErrorDao.getApiExpired(client_id, templateList);
        // 记录过期的件数
        int count = 0;
        // 获取当前的时间
        LocalDate now = LocalDate.now();
        for (Tc203_order_client orderClient : apiExpired) {
            Date expire_date = orderClient.getExpire_date();
            if (StringTools.isNullOrEmpty(expire_date)) {
                continue;
            }
            // 获取到 过期时间和当前时间的差的天数
            LocalDate future = expire_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long between = ChronoUnit.DAYS.between(now, future);
            if (between <= 7.0) {
                count++;
            }
        }

        if (count != 0) {
            return CommonUtils.success();
        } else {
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
