package com.lemonico.store.dao;



import com.lemonico.common.bean.Ms009_definition;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 名称区分マスタ
 ** @Date: 2020/5/29
 */
@Mapper
public interface DefinitionDao
{

    /**
     * @Description:
     * @Param:
     * @return:
     * @Date: 2020/5/29
     */
    List<Ms009_definition> getDefinitionList(@Param("warehouse_cd") String warehouse_cd,
        @Param("sys_kind") Integer[] sys_kind,
        @Param("sys_cd") String sys_cd);
}
