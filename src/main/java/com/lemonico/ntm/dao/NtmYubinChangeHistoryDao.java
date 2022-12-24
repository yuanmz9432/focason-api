package com.lemonico.ntm.dao;



import com.lemonico.common.bean.Mc109_ntm_yubin_change_history;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: NtmAreaMasterDao
 * @description: NtmAreaMaster dao
 * @date: 2021/6/18
 **/
@Mapper
public interface NtmYubinChangeHistoryDao
{

    /**
     * @description 获取邮编更改记录
     * @return: List
     * @date 2021/7/15
     **/
    List<Mc109_ntm_yubin_change_history> getAllYubinChangeHistory();
}
