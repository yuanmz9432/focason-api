package com.lemonico.common.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.lemonico.common.bean.Ms001_function;
import com.lemonico.common.bean.Ms201_client;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.service.ClientService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @program: sunlogic
 * @description: 顧客グループマスタ
 * @create: 2020-07-06 15:13
 **/
@Service
public class ClientServiceImpl implements ClientService
{

    private final static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Resource
    private ClientDao clientDao;

    /**
     * @Param: store_id : 店铺Id
     * @description: 获取店铺信息
     * @return: com.lemonico.common.bean.Ms201_customer_group
     * @date: 2020/07/09
     */
    @Override
    public Ms201_client getClientInfo(String client_id) {
        return clientDao.getClientInfo(client_id);
    }

    /**
     * @Description: 获取登录用户的信息
     * @Param: 登录邮箱
     * @return: 店铺名，用户名
     * @Date: 2020/8/24
     */
    @Override
    public Ms201_client getLoginUserInfo(String client_id, String user_id) {
        return clientDao.getLoginUserInfo(client_id, user_id);
    }

    /**
     * @Param: client_id
     * @Param: function_cd
     * @description: 获取登录店铺或仓库的機能
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/25
     */
    @Override
    public JSONObject getFunctions(String warehouse_cd, String client_id, String function_cd) {
        List<String> functionCdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(function_cd);
        List<Ms001_function> ms001FunctionList = null;
        try {
            ms001FunctionList = clientDao.function_info(client_id, functionCdList, warehouse_cd);
        } catch (Exception e) {
            logger.error("获取登录店铺的機能失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success(ms001FunctionList);
    }

    /**
     * @Description: 获取登录用户的clientid
     * @Param: user_id
     * @return: 用户名
     * @Author: Liocng
     * @Date: 2020/12/3
     */
    @Override
    public String getClientIdByUserId(String user_id) {
        return clientDao.getClientIdByUserId(user_id);
    }

    /**
     * @Param: jsonObject
     * @description: 更改店铺品名
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/06
     */
    @Override
    public JSONObject updateLabelNote(JSONObject jsonObject) {
        // clientDao.updateLabelNote(jsonObject);
        return CommonUtils.success();
    }
}
