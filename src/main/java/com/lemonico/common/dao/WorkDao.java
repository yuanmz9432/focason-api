package com.lemonico.common.dao;



import com.lemonico.common.bean.Ms003_work;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogi
 * @description: 作業マスタ
 * @create: 2020-07-13 10:13
 **/
public interface WorkDao
{

    public List<Ms003_work> getWorkList(@Param("operation_cd") String operation_cd);
}
