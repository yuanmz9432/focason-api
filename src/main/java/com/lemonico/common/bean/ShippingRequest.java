package com.lemonico.common.bean;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.Data;

/**
 * @class ShippingRequest
 * @description 送り状発行 请求参数
 * @date 2021/6/3
 **/
@Data
@JacksonXmlRootElement(localName = "shippingRequest")
public class ShippingRequest
{

    // 用户认证（必需）
    @JacksonXmlProperty(localName = "customerAuth")
    private CustomerAuth customerAuth;

    // 运输公司代码, 0001 / null / 空白 --佐川急便, DELIVERYCODE_XXX
    @JacksonXmlProperty(localName = "deliveryCode")
    private String deliveryCode;

    // 打印输出标志（必需）
    // 0-（确认功能）仅执行错误审查。
    // 1-（发出请求功能）检查错误后，所有发货信息均正常
    @JacksonXmlProperty(localName = "printOutFlg")
    private String printOutFlg;

    // 发票代码, 发票定义类,（半必需） OKURICODE_A501
    @JacksonXmlProperty(localName = "okuriCode")
    private String okuriCode;

    // 输出电平,（必需） OUTPUTLEVEL_XXX
    @JacksonXmlProperty(localName = "outputLevel")
    private String outputLevel;

    // 底层图像显示标志
    // 0 / null / 空白隐藏
    // 1-显示器
    @JacksonXmlProperty(localName = "backLayerFlg")
    private String backLayerFlg;

    // 发货信息列表
    @JacksonXmlElementWrapper(localName = "printDataList")
    @JacksonXmlProperty(localName = "printDataDetail")
    private List<PrintDataDetail> printDataList;

    @Data
    public class CustomerAuth
    {
        // 客户ID（必需）
        String customerId;
        // 登录密码（必需）
        String loginPassword;
    }

    // 送货信息详情
    @Data
    public class PrintDataDetail
    {
        // 発送個口数，指定分送り状発行 交货单位数、 出货单位数（必需）
        String haisoKosu;
        // 控制序列号、由用户管理的唯一代码（必需）
        String userManageNumber;
        // 顧客コード 客户代码（必需）
        String kokyakuCode;
        // 届先住所1 收货地址 1（必需）
        String otodokeAdd1;
        // 收货地址 2
        String otodokeAdd2;
        // 收货地址 3
        String otodokeAdd3;
        // 届先氏名1 通知名称 1（必需）
        String otodokeNm1;
        // 通知名称 2
        String otodokeNm2;
        // 届先郵便番号 送货邮政编码（必需）
        String otodokeYubin;
        // 届先電話番号 送货电话（必需）
        String otodokeTel;
        // 送货电子邮件地址
        String otodokeMailAddress;
        // 依頼主指定フラグ 客户指定标志（必需）
        // 0 - 顧客コードに紐づく出荷場情報を印字
        // 1 - 下記の依頼主情報を参照
        String iraiPrintFlg;
        // 依頼主住所 客户地址 1（半必需）
        String iraiAdd1;
        // 客户地址 2
        String iraiAdd2;
        // 客户地址 3
        String iraiAdd3;
        // 客户名称 1（半必需）
        String iraiNm1;
        // 客户名称 2
        String iraiNm2;
        // 客户邮编（半必需）
        String iraiYubin;
        // 客户电话号码（半必需）
        String iraiTel;
        // 请求者电子邮件地址
        String iraiMailAddress;
        // 发货日期 yyyyMMdd格式
        String shippingDate;
        // 第1条
        String kiji1;
        // 第2条
        String kiji2;
        // 第3条
        String kiji3;
        // 第4条
        String kiji4;
        // 第5条
        String kiji5;
        // 第6条
        String kiji6;
        // 航班类型代码（必需） BINSYUCODE_XXX
        String binsyuCode;
        // 货到付款标志（必需）
        // 0 - 通常出荷の送り状を発行
        // 1 - 代金引換の送り状を発行
        String daibikiFlg;
        // 货到付款付款方式分类 DAIBIKITYPE_XXX
        String daibikiType;
        // 指定交货日期 yyyyMMdd格式
        String shiteiDate;
        // 交货时间规格代码 SHITEITIMECODE_XXX
        String shiteiTimeCode;
        // 货到付款金额（半必需），如果 代金引換フラグが1则需要，如果代金引換フラグが0/null/空则不需要
        String daibikiKingaku;
        // 货到付款消费税（半必需），如果 代金引換フラグが1则需要，如果代金引換フラグが0/null/空则不需要
        String daibikiTax;
        // 重量 1 WEIGHT1_XX
        String weight1;
        // 重量 2 WEIGHT2_XX
        String weight2;
        // 封印1 CARESEAL1_XXX
        String careSeal1;
        // 封印2 CARESEAL1_XXX
        String careSeal2;
        // 封印3 CARESEAL1_XXX
        String careSeal3;
        // 保险金额
        String hokenKingaku;
        // 停止标志
        // 0/null/空白 - 営業所止めを行いません
        // 1 - 営業所止めにします
        String eidomeFlg;
        // 售楼处代码
        String depotCode;
        // 标记
        String mark;
        // 交货单信息
        NouhinshoData nouhinshoData;
        // 收货信息
        UketoriData uketoriData;
        // 元着コード（半必需）
        String motoChakuCode;
        // 集荷依頼情報
        ShukaIraiData shukaIraiData;
    }

    @Data
    // 交货单信息
    public class NouhinshoData
    {
        // 交货单标题
        String title;
        // 订单号
        String orderId;
        // 礼物
        String gift;
        // 能志
        String noshi;
        // 店铺联系电话
        String shopTel;
        // 商店联系传真号码
        String shopFax;
        // 店铺联系邮编
        String shopYubin;
        // 店铺联系地址 1
        String shopAdd1;
        // 店铺联系地址 2
        String shopAdd2;
        // 店铺联系地址 3
        String shopAdd3;
        // 店铺联系人姓名 1
        String shopNm1;
        // 店铺联系人姓名 2
        String shopNm2;
        // 商店联系电子邮件地址
        String shopMailAddress;
        // 税务登记号
        String taxRegisterNo;
        // 固定短语 1
        String teikei1;
        // 固定短语 2
        String teikei2;
        // 固定短语 3
        String teikei3;
        // 固定短语 4
        String teikei4;
        // 固定短语 5
        String teikei5;
        // 固定短语 6
        String teikei6;
        // 固定短语 7
        String teikei7;
        // 固定短语 8
        String teikei8;
        // 固定短语 9
        String teikei9;
        // 评论
        String biko;
        // 订购日期
        String orderDate;
        // 店铺名称
        String shopNm;
        // 付款方法
        String paymentNm;
        // 产品总消费税
        String itemTotalTax;
        // 船运
        String soryo;
        // 费用
        String tesuryo;
        // 优惠券折扣金额
        String couponPrice;
        // 使用ポイント
        String usedPoint;
        // 总价
        String totalPrice;
        List<TaxDetail> taxList;
        // 商品master
        List<ItemDetail> itemList;
        // フリー項目1
        String freeEntry1;
        // フリー項目2
        String freeEntry2;
        // フリー項目3
        String freeEntry3;
        // フリー項目4
        String freeEntry4;
        // フリー項目5
        String freeEntry5;
        // フリー項目6
        String freeEntry6;
        // フリー項目7
        String freeEntry7;
        // フリー項目8
        String freeEntry8;
        // フリー項目9
        String freeEntry9;
        // フリー項目10
        String freeEntry10;
        // フリー項目11
        String freeEntry11;
        // フリー項目12
        String freeEntry12;
        // フリー項目13
        String freeEntry13;
        // フリー項目14
        String freeEntry14;
        // フリー項目15
        String freeEntry15;
        // フリー項目16
        String freeEntry16;
        // フリー項目17
        String freeEntry17;
        // フリー項目18
        String freeEntry18;
        // フリー項目19
        String freeEntry19;
        // フリー項目20
        String freeEntry20;
    }
    @Data
    // 税明細
    public class TaxDetail
    {
        // 税率
        String taxRate;
        // 税金額
        String taxPrice;
        // 対象額
        String taxValue;
    }
    @Data
    // 商品详情
    public class ItemDetail
    {
        // 商品code
        String itemCode;
        // 产品拣货代码
        String itemPickingCode;
        // 商品数量
        String itemKosu;
        // 商品単位名
        String itemUnitNm;
        // 商品名
        String itemName;
        // 商品詳細
        String itemShosai;
        // 商品単価
        String itemPrice;
        // 商品小計
        String subTotalPrice;
        // 軽減税率対象マーク
        String taxReduceMark;
    }

    @Data
    public class UketoriData
    {
        // 收款码（半必需）
        String uketoriCode;
        // 接收商店代码（半必需）
        String uketoriShopCode;
        // 收到的订单号（半必需）
        String uketoriOrderId;
        // 收件人姓名 1（半必需）
        String uketoriPersonNm1;
        // 收件人姓名 2
        String uketoriPersonNm2;
        // 退货地址 1（半必需）
        String henpinAdd1;
        // 退货地址 2
        String henpinAdd2;
        // 退货地址 3
        String henpinAdd3;
        // 退货地址名称 1（半必需）
        String henpinNm1;
        // 退货地址名称 2
        String henpinNm2;
        // 返回邮政编码（半必需）
        String henpinYubin;
        // 返回电话号码（半必需）
        String henpinTel;
        // 認証番号（半必需）
        String certificationNumber;
    }

    @Data
    // 取件请求信息
    public class ShukaIraiData
    {
        // 取件请求者代码（半必需）
        String shukaIraiCode;
        // 取件请求指定日期（半必需）
        String shukaIraiShiteiDate;
        // 取件请求时间指定代码（半必需）
        String shukaIraiShiteiTimeCode;
    }
}
