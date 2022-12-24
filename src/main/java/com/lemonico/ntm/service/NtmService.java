package com.lemonico.ntm.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Tw216_delivery_fare;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @className: NtmService
 * @description: ntm service interface
 * @date: 2021/4/15
 **/
public interface NtmService
{

    /**
     * @description: 获取ntm top页数据
     * @return: JSONObject
     * @param clientId 店铺ID
     * @date: 2021/4/15
     **/
    public JSONObject getTopData(String clientId);

    /**
     * @description 通过ntm的配送運賃数据
     * @param list 更新が必要です配送運賃
     * @return: boolean
     * @date 2021/6/11
     **/
    public boolean syncDeliveryFare(List<Tw216_delivery_fare> list);

    /**
     * @description ntm个人受注csv导入
     * @param file 文件
     * @param client_id 店铺ID
     * @param wharehouse_cd 仓库CD
     * @param status 状态
     * @param request http请求
     * @return JSONObject
     * @date 2021/6/16
     **/
    public JSONObject importOrderCsv(MultipartFile file, String client_id, String wharehouse_cd, String status,
        HttpServletRequest request);
}
