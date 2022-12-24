package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;

/**
 * @className: SettingService
 * @description: SettingService
 * @author: wang
 * @date: 2020/07/03
 **/
public interface MacroSettingService
{

    /**
     * @Description: 店舗に関するマクロ機能の情報取得
     * @Param: client_id 店舗ID
     * @return: String
     * @Author: wang
     * @Date: 2021/3/22
     */
    JSONObject getMacroList(String client_id);


    /**
     * @Description: マクロ設定の情報取得
     * @Param: id マクロ管理ID
     * @return: String
     * @Author: wang
     * @Date: 2021/3/22
     */
    JSONObject getMacroInfoById(String id);

    /**
     * @Description: 店舗に関するマクロ機能の情報保存
     * @Param: JSONObject マクロ情報
     * @return: JSONObject
     * @Date: 2021/3/22
     */
    Integer saveMacroInfo(JSONObject jsonObject) throws NoSuchFieldException;

    /**
     * @Description: //TODO 批量删除macro设定
     *               @Date： 2021/3/26
     * @Param：ids ,client_id
     * @return：
     */
    Integer delMacro(String client_id, String ids);

    /**
     * @Param:
     * @description: 检验macro优先顺序是否重复
     * @return: boolean
     * @date: 2021/04/06
     */
    boolean checkMacroPriorityExists(Integer priority, String client_id);

    /**
     * @Description: //TODO macro信息修改
     *               @Date： 2021/4/6
     *               @Param：
     *               @return： JSONObject
     */
    JSONObject updateMacroInfo(String client_id, Integer priority, Integer macro_status, String id,
        Integer old_priority, String repeat_id);

    /**
     * @Param:
     * @description: 检验macro名称是否重复
     * @return: boolean
     * @date: 2021/04/06
     */
    boolean nameCheck(String macro_name, String client_id);
}
