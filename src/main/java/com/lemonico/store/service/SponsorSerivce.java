package com.lemonico.store.service;



import com.lemonico.common.bean.Ms012_sponsor_master;
import java.util.List;

/**
 * @program: sunlogic
 * @description: 依頼主マスタ
 * @create: 2020-05-27 14:54
 **/
public interface SponsorSerivce
{

    /**
     * @Description: 依頼主マスタ取得
     * @Param: 顧客CD
     * @return: Json
     * @Date: 2020/5/27
     */
    public List<Ms012_sponsor_master> getSponsorList(String client_id, boolean sponsor_default, String sponsor_id);
}
