package com.lemonico.api.bean;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms012_sponsor_master;
import com.lemonico.common.bean.Tc203_order_client;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import lombok.Data;

/**
 * 店舗情報
 */
@Data
@ApiModel(value = "店舗情報")
public class ClientInfo
{
    @ApiModelProperty(value = "倉庫CD")
    private String warehouseCd;
    @ApiModelProperty(value = "店舗ID")
    private String clientId;
    @ApiModelProperty(value = "受注履歴ID")
    private Integer historyId;
    @ApiModelProperty(value = "ディフォルト配送方法")
    private String defaultDeliveryMethod;
    @ApiModelProperty(value = "出庫依頼ステータス")
    private Integer shipmentStatus;
    @ApiModelProperty(value = "依頼主ID")
    private String sponsorId;
    @ApiModelProperty(value = "識別番号")
    private String identification;
    @ApiModelProperty(value = "受注子番号")
    private Integer subNo;
    @ApiModelProperty(value = "API番号")
    private Integer apiId;
    @ApiModelProperty(value = "API名称")
    private String apiName;
    @ApiModelProperty(value = "店舗設定TBL希望時間帯情報(MAP)")
    private Map<String, String> ms007SettingTimeMap;
    @ApiModelProperty(value = "店舗設定TBL支払情報(MAP)")
    private Map<String, String> ms007SettingPaymentMap;
    @ApiModelProperty(value = "店舗設定TBL配送方法情報(MAP)")
    private Map<String, String> ms007SettingDeliveryMethodMap;
    @ApiModelProperty(value = "店舗API情報")
    private Tc203_order_client tc203Order;
    @ApiModelProperty(value = "依頼主マスタ")
    private Ms012_sponsor_master ms012sponsor;
    @ApiModelProperty(value = "カラーミークライアント定義配送情報")
    private JSONArray deliveries;
    @ApiModelProperty(value = "カラーミークライアント定義支払情報")
    private JSONArray payments;
    @ApiModelProperty(value = "カラーミークライアント定義店舗情報")
    private JSONObject shop;
}
