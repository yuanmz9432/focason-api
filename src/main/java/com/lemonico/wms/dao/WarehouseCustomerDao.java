package com.lemonico.wms.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: WarehouseCustomerDao
 * @description: 仓库侧设定处理
 * @date: 2020/07/08 10:35
 **/
@Mapper
public interface WarehouseCustomerDao
{


    /**
     * @Param: user_id : 用户id
     * @Param: warehouse_cd: 仓库Id
     * @description: user_id warehouse_cd查询所属仓库Id
     * @return: java.lang.String
     * @date: 2020/07/08
     */
    String getWarehouseId(@Param("user_id") String user_id,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @Param: warehouse_cd : 仓库id
     * @description: 根据仓库Id查询所有的全部店铺Id
     * @return: java.util.List<java.lang.String>
     * @date: 2020/07/08
     */
    List<String> getClientIdList(@Param("warehouse_cd") String warehouse_cd);

    /**
     * @Param: clientIdList : 该仓库所对应的所有店铺Id
     * @description: 查询该仓库对应所有的店铺信息
     * @return: java.util.List<com.lemonico.common.bean.Ms201_customer_group>
     * @date: 2020/07/09
     */
    List<Ms201_client> getClientInfoListByClientId(@Param("clientIdList") List<String> clientIdList);

    /**
     * @Param: warehouse_id : 仓库id
     * @description: 获取该仓库下所有用户Id
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/08
     */
    List<String> getUserIdByWarehouseId(@Param("warehouse_cd") String warehouse_id);

    /**
     * @Param: userIdList : 属于该仓库的所有用户Id
     * @description: 根据userId，获取user信息
     * @return: java.util.List<com.lemonico.common.bean.Ms200_customer>
     * @date: 2020/07/09
     */
    List<Ms200_customer> getUserInfoByUserId(@Param("userIdList") List<String> userIdList);

    /**
     * @param: client_id : 店铺Id
     * @description: 根据店铺Id 查询店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/09
     */
    Ms201_client getClientInfo(String client_id);

    /**
     * @Param: jsonObject : 店铺的所有信息 , birthday : 生日 , permonth ：出荷件数
     * @description: 根据店铺Id修改店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/10
     */
    Integer updateClientInfo(@Param("jsonObject") JSONObject jsonObject,
        @Param("birthday") Date birthday,
        @Param("permonth") Integer permonth);

    /**
     * @param: warehouseId : 仓库Id
     * @description: 根据仓库Id 获取仓库信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    Mw400_warehouse getWarehouseInfo(String warehouseId);

    /**
     * @Param: jsonObject : 仓库id
     * @description: 根据仓库Id更改仓库信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    Integer updateWarehouseInfoByWarehouseId(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: userIdList
     * @description: 根据用户Id删除用户信息
     * @return: java.lang.Integer
     * @date: 2020/07/13
     */
    Integer deleteUserInfo(@Param("userId") String userId);

    /**
     * @param: userIdList : 多个用户Id
     * @param: warehouseId : 仓库Id
     * @description: 根据用户和仓库的Id 删除用户和仓库的关系
     * @return: java.lang.Integer
     * @date: 2020/07/13
     */
    Integer deleteWharehouseCustomerByUserId(@Param("userIdList") ArrayList<String> userIdList,
        @Param("warehouseId") String warehouseId);

    /**
     * @Param: jsonObject : user_id,warehouse_cd
     * @description: 新增该仓库的员工
     * @return: java.lang.Integer
     * @date: 2020/07/13
     */
    Integer insertWarehouseCustomer(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: jsonObject : user_id , authority_cd
     * @description: 给用户添加权限
     * @return: java.lang.Integer
     * @date: 2020/07/14
     */
    Integer insertCustomerAuth(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: userIdList
     * @description: 根据用户Id删除顧客別権限マスタ的信息
     * @return: java.lang.Integer
     * @date: 2020/8/27
     */
    Integer deleteCustomerAuthByUserId(@Param("user_id") String userId);

    /**
     * @Description: スマートCatリスト
     * @Param: warehouse_id
     * @return: JSONObject
     * @Date: 2020/11/10
     */
    List<Mw406_wh_smartcat> getSmartCatList(@Param("warehouse_cd") String warehouse_cd,
        @Param("id") Integer id);

    /**
     * @Description: スマートCat更新
     * @Param: warehouse_id
     * @return: JSONObject
     * @Date: 2020/11/10
     */
    Integer updateSmartCatList(@Param("warehouse_cd") String warehouse_cd,
        @Param("id") Integer id,
        @Param("jsonObject") JSONObject jsonObject);

    /**
     * @Description: CSV windows アプロード
     * @Param: warehouse_id
     * @return: Mw407_smart_file
     * @Date: 2020/11/13
     */
    List<Mw407_smart_file> getSmartCatListWindow(@Param("warehouse_cd") String warehouse_cd);

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
    Integer upSmartCatListWindow(Mw407_smart_file mw407SmartFile);

    Integer updateDeliveryClientInfo(@Param("jsonObject") JSONObject jsonObject);
}
