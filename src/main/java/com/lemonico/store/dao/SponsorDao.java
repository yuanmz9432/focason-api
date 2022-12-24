package com.lemonico.store.dao;



import com.lemonico.common.bean.Ms012_sponsor_master;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogic
 * @description: 依頼主マスタ
 * @create: 2020-05-27 14:58
 **/
@Mapper
public interface SponsorDao
{

    /**
     * @Description: 依頼主マスタ取得
     * @Param: 顧客CD
     * @return: Json
     * @Date: 2020/5/27
     */
    public List<Ms012_sponsor_master> getSponsorList(@Param("client_id") String client_id,
        @Param("sponsor_default") boolean sponsor_default,
        @Param("sponsor_id") String sponsor_id);

    /**
     * @param sponsorIds : 依赖主Id集合
     * @description: 根据依赖主id获取依赖主信息
     * @return: java.util.List<com.lemonico.common.bean.Ms012_sponsor_master>
     * @date: 2021/8/12 13:31
     */
    List<Ms012_sponsor_master> getSponsorListById(@Param("sponsorIds") List<String> sponsorIds);

    /**
     * 获取店铺的所有依赖主信息
     * 
     * @param client_id
     * @return
     */
    List<Ms012_sponsor_master> getClientSponsorList(@Param("client_id") List<String> client_id);
}
