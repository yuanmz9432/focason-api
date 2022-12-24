package com.lemonico.wms.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mw406_wh_smartcat;
import com.lemonico.common.bean.Mw407_smart_file;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * @className: WarehouseCustomerService
 * @description: 仓库侧设定处理
 * @date: 2020/07/08 10:36
 **/
public interface WarehouseCustomerService
{

    /**
     * @Param: client_id : 店铺Id
     * @description: 获取该仓库下所有店铺的信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/08
     */
    JSONObject getClientInfoList(String warehouse_cd);

    /**
     * @Param: warehouse_id : 仓库 id
     * @description: 获取到该仓库下所有店铺的权限
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/08
     */
    JSONObject getCustomerAuthority(String warehouse_id);

    /**
     * @param: client_id : 店铺Id
     * @description: 根据店铺Id 查询店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/09
     */
    JSONObject getClientInfo(String client_id);

    /**
     * @Param: jsonObject : 店铺的所有信息
     * @description: 根据店铺Id修改店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/10
     */
    JSONObject updateClientInfo(JSONObject jsonObject);

    /**
     * @param: warehouseId : 仓库Id
     * @description: 根据仓库Id 获取仓库信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    JSONObject getWarehouseInfo(String warehouseId);

    /**
     * @Param: jsonObject : 仓库id
     * @description: 根据仓库Id更改仓库信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    JSONObject updateWarehouseInfoByWarehouseId(JSONObject jsonObject);

    /**
     * @Param: user_id : 用户Id
     * @description: 根据用户Id查询用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    JSONObject getUserInfo(String user_id);

    /**
     * @Param: jsonObject : 多个用户Id
     * @description: 根据用户Id删除用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    JSONObject deleteUserInfo(JSONObject jsonObject);

    /**
     * @param: login_id : 邮箱
     * @description: 根据邮箱查询用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    JSONObject getUserInfoByLoginId(String login_id);

    /**
     * @param: jsonObject : login_id,login_nm
     * @description: 新增属于该仓库的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    JSONObject insertUserInfo(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @Param: jsonObject
     * @description: 新增属于该仓库的店铺
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/6
     */
    JSONObject insertClientInfo(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @Description: スマートCatリスト
     * @Param: warehouse_id
     * @return: JSONObject
     * @Date: 2020/11/10
     */
    List<Mw406_wh_smartcat> getSmartCatList(String warehouse_cd, Integer id);

    /**
     * @Description: スマートCat更新
     * @Param: warehouse_id
     * @return: JSONObject
     * @Date: 2020/11/10
     */
    JSONObject updateSmartCatList(String warehouse_cd, Integer id, JSONObject jsonObject, HttpServletRequest request);

    /**
     * @Description: CSV windows アプロード
     * @Param: warehouse_id
     * @return: Mw407_smart_file
     * @Date: 2020/11/13
     */
    JSONObject getSmartCatListWindow(String warehouse_cd);

    /**
     * @Description: スマートCAT ファイル 挿入
     * @Param: warehouse_id
     * @return: Mw407_smart_file
     * @Date: 2020/11/13
     */
    Integer inSmartCatListWindow(Mw407_smart_file mw407SmartFile);

    /**
     * @Description: スマートCAT ファイル 更新
     * @Param: warehouse_id
     * @return: Mw407_smart_file
     * @Date: 2020/11/13
     */
    Integer upSmartCatListWindow(String warehouse_cd, HttpServletRequest httpServletRequest, String shipment_plan_id);

    /**
     * @Description:
     * @Param: warehouse_id
     * @return: Mw407_smart_file
     * @Author: Liocng
     * @Date: 2020/11/13
     */
    JSONObject updateDeliveryClientInfo(JSONObject jsonObject);
}
