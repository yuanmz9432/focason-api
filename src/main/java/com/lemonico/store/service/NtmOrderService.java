package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @className: NtmOrderService
 * @description: Ntm 受注接口
 * @date: 2021/3/31 13:30
 **/
public interface NtmOrderService
{

    /**
     * @param client_id : 店铺Id
     * @param warehouse_cd : 仓库Id
     * @param file : 上传的excel文件
     * @param shipmentStatus : 是否出库的flg， 1：出庫依頼する 2：出庫依頼しない
     * @param request : 请求
     * @description: NTM受注Excel取込
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/23 14:02
     */
    JSONObject importOrderExcel(MultipartFile file, String client_id, String warehouse_cd,
        String shipmentStatus, String eccubeFLg, HttpServletRequest request);
}
