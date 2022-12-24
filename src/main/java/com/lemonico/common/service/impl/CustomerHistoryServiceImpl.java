package com.lemonico.common.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms200_customer;
import com.lemonico.common.bean.Ms205_customer_history;
import com.lemonico.common.dao.CommonFunctionDao;
import com.lemonico.common.dao.CustomerHistoryDao;
import com.lemonico.common.service.CustomerHistoryService;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @program: sunlogi
 * @description: 顧客別作業履歴
 * @create: 2020-07-13 11:09
 **/
@Service
public class CustomerHistoryServiceImpl implements CustomerHistoryService
{

    @Resource
    private CustomerHistoryDao customerHistoryDao;
    @Resource
    private CommonFunctionDao commonFunctionDao;

    /**
     * @Description: 顧客別作業履歴
     * @Param:
     * @return:
     * @Date: 2020/7/13
     */
    @Override
    public List<Ms205_customer_history> getCustomerHistory(String plan_id) {
        return customerHistoryDao.getCustomerHistory(plan_id);
    };

    @Override
    @Transactional
    public Integer insertCustomerHistory(HttpServletRequest servletRequest, String[] shipment_plan_id,
        String operation_cd, String user_id) {
        JSONObject jsonObject = new JSONObject();
        int result = 0;
        if (StringTools.isNullOrEmpty(user_id)) {
            try {
                user_id = CommonUtils.getToken("user_id", servletRequest);
                ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();
                StringBuffer apiUrl = request.getRequestURL();
                jsonObject.put("api_url", apiUrl.toString());
            } catch (Exception e) {
                user_id = "99999";
                jsonObject.put("api_url", "Batch処理");
            }
        }
        // ユーザーIDが空値の場合、99999として処理
        if (StringTools.isNullOrEmpty(user_id)) {
            user_id = "99999";
            jsonObject.put("biko", "Batch処理");
        } else {
            jsonObject.put("biko", null);
        }

        Date nowTime = DateUtils.getDate();
        jsonObject.put("user_id", user_id);
        jsonObject.put("operation_date", nowTime);
        jsonObject.put("operation_cd", operation_cd);

        List<Ms200_customer> list = commonFunctionDao.getClientInfo(null, user_id);
        if (list != null && list.size() > 0) {
            String user = list.get(0).getLogin_nm();
            jsonObject.put("ins_usr", user);
            jsonObject.put("upd_usr", user);
        } else {
            jsonObject.put("ins_usr", "システム自動");
            jsonObject.put("upd_usr", "システム自動");
        }
        jsonObject.put("ins_date", nowTime);
        jsonObject.put("upd_date", nowTime);

        for (String plan_id : shipment_plan_id) {
            jsonObject.put("plan_id", plan_id);
            // 查询最新一条操作履历
            Ms205_customer_history customerInfo = customerHistoryDao.getCustomerInfo(plan_id);
            if (!StringTools.isNullOrEmpty(customerInfo) && !StringTools.isNullOrEmpty(operation_cd)
                && operation_cd.equals(customerInfo.getOperation_cd())) {
                continue;
            }
            String operation_id = plan_id + setOperationId();
            jsonObject.put("operation_id", operation_id);
            result = customerHistoryDao.insertCustomerHistory(jsonObject);
        }
        return result;
    }

    /**
     * @Description: 出庫依頼ID生成
     * @return: String
     * @Date: 2020/5/14
     */
    public String setOperationId() {
        int i = CommonUtils.getRandomNum(100000000);
        String timeMillis = String.valueOf(i + (System.currentTimeMillis() / 1000L));

        // 获取随机数
        Random random = new Random();
        String ranNum = "";
        for (int j = 0; j < 4; j++) {
            ranNum += random.nextInt(100);
        }

        Long newId = Long.valueOf(timeMillis) + (Long.valueOf(ranNum) * random.nextInt(100));
        return newId.toString();
    }
}
