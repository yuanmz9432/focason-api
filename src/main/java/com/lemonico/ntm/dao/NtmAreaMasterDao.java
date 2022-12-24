package com.lemonico.ntm.dao;



import com.lemonico.common.bean.Mc108_ntm_area_master;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: NtmAreaMasterDao
 * @description: NtmAreaMaster dao
 * @date: 2021/6/18
 **/
@Mapper
public interface NtmAreaMasterDao
{

    /**
     * @description 根据区域code获取area_master数据
     * @param code 区域code
     * @return Mc108_ntm_area_master
     * @date 2021/7/15
     **/
    Mc108_ntm_area_master getNtmAreaMasterByCode(@Param("code") String code);
}
