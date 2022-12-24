package com.lemonico.store.dao;



import com.lemonico.common.bean.Mw410_delivery_takes_days;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @classname DeliveryTakesDaysDao
 * @description DeliveryTakesDays Dao
 * @date 2021/6/23
 **/
@Mapper
public interface DeliveryTakesDaysDao
{

    /**
     * @description 根据仓库都道府县获取所有配送区域时长数据
     * @param sender 仓库都道府县
     * @return List
     * @date 2021/6/23
     **/
    public List<Mw410_delivery_takes_days> getDeliveryTakesDaysListBySender(@Param("list") List<String> list);
}
