package com.lemonico.ntm.dao;



import com.lemonico.common.bean.Tw216_delivery_fare;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: DeliveryFareDao
 * @description: DeliveryFare dao
 * @date: 2021/6/11
 **/
@Mapper
public interface DeliveryFareDao
{

    /**
     * @description 清空配送運賃表
     * @return: null
     * @date 2021/5/31
     **/
    public void truncateDeliveryFare();

    /**
     * @description 一括挿入配送運賃
     * @param deliveryFareList ロット配送運賃
     * @return: null
     * @date 2021/4/27
     **/
    public void insertDeliveryFareBatch(@Param("deliveryFareList") List<Tw216_delivery_fare> deliveryFareList);
}
