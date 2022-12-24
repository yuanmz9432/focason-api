package com.lemonico.common.bean;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class DataOutputResponse
 * @description
 * @date 2021/10/28
 **/
@Data
@JacksonXmlRootElement(localName = "dataOutputResponse")
public class DataOutputResponse
{

    @JacksonXmlProperty(localName = "resultCode")
    private String resultCode;

    @JacksonXmlElementWrapper(localName = "printRequestIdList")
    @JacksonXmlProperty(localName = "printRequestIdDetail")
    private List<PrintRequestIdDetail> printRequestIdList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrintRequestIdDetail
    {

        @JacksonXmlProperty(localName = "printRequestId")
        String printRequestId;
        @JacksonXmlProperty(localName = "createDate")
        String createDate;
        @JacksonXmlProperty(localName = "resultCode")
        String resultCode;

        @JacksonXmlElementWrapper(localName = "dataOutputList")
        @JacksonXmlProperty(localName = "dataOutputDetail")
        List<DataOutputDetail> dataOutputList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataOutputDetail
    {
        @JacksonXmlProperty(localName = "pickupDepotNm")
        String pickupDepotNm;
        @JacksonXmlProperty(localName = "pickupDepotTel")
        String pickupDepotTel;
        @JacksonXmlProperty(localName = "pickupDepotFax")
        String pickupDepotFax;
        @JacksonXmlProperty(localName = "deliveryDepotNm")
        String deliveryDepotNm;
        @JacksonXmlProperty(localName = "deliveryDepotTel")
        String deliveryDepotTel;
        @JacksonXmlProperty(localName = "deliveryDepotCode")
        String deliveryDepotCode;
        @JacksonXmlProperty(localName = "deliveryDepotCodeEda")
        String deliveryDepotCodeEda;
        @JacksonXmlProperty(localName = "deliveryDepotBarcode")
        String deliveryDepotBarcode;
        @JacksonXmlProperty(localName = "shippingNumber")
        String shippingNumber;
        @JacksonXmlProperty(localName = "shippingNumberBarcode")
        String shippingNumberBarcode;
        @JacksonXmlProperty(localName = "shippingCode")
        Integer shippingCode;
        @JacksonXmlProperty(localName = "expirationDate")
        String expirationDate;
        @JacksonXmlProperty(localName = "shiteiTimeSealBarcode")
        String shiteiTimeSealBarcode;
        @JacksonXmlProperty(localName = "weightNm1")
        String weightNm1;
        @JacksonXmlProperty(localName = "weightBarcode1")
        String weightBarcode1;
        @JacksonXmlProperty(localName = "weightNm2")
        String weightNm2;
        @JacksonXmlProperty(localName = "weightBarcode2")
        String weightBarcode2;
        @JacksonXmlProperty(localName = "sealNm1")
        String sealNm1;
        @JacksonXmlProperty(localName = "sealBarcode1")
        String sealBarcode1;
        @JacksonXmlProperty(localName = "sealNm2")
        String sealNm2;
        @JacksonXmlProperty(localName = "sealBarcode2")
        String sealBarcode2;
        @JacksonXmlProperty(localName = "sealNm3")
        String sealNm3;
        @JacksonXmlProperty(localName = "sealBarcode3")
        String sealBarcode3;
        @JacksonXmlProperty(localName = "chukeiFlg")
        Integer chukeiFlg;
        @JacksonXmlProperty(localName = "systemVersion")
        String systemVersion;
        @JacksonXmlProperty(localName = "printDataDetail")
        PrintDataDetail printDataDetail;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrintDataDetail
    {
        @JacksonXmlProperty(localName = "haisoKosu")
        Integer haisoKosu;
        @JacksonXmlProperty(localName = "userManageNumber")
        String userManageNumber;
        @JacksonXmlProperty(localName = "kokyakuCode")
        Integer kokyakuCode;
        @JacksonXmlProperty(localName = "otodokeAdd1")
        String otodokeAdd1;
        @JacksonXmlProperty(localName = "otodokeAdd2")
        String otodokeAdd2;
        @JacksonXmlProperty(localName = "otodokeAdd3")
        String otodokeAdd3;
        @JacksonXmlProperty(localName = "otodokeNm1")
        String otodokeNm1;
        @JacksonXmlProperty(localName = "otodokeNm2")
        String otodokeNm2;
        @JacksonXmlProperty(localName = "otodokeYubin")
        Integer otodokeYubin;
        @JacksonXmlProperty(localName = "otodokeTel")
        String otodokeTel;
        @JacksonXmlProperty(localName = "otodokeMailAddress")
        String otodokeMailAddress;
        @JacksonXmlProperty(localName = "iraiPrintFlg")
        Integer iraiPrintFlg;
        @JacksonXmlProperty(localName = "iraiAdd1")
        String iraiAdd1;
        @JacksonXmlProperty(localName = "iraiAdd2")
        String iraiAdd2;
        @JacksonXmlProperty(localName = "iraiAdd3")
        String iraiAdd3;
        @JacksonXmlProperty(localName = "iraiNm1")
        String iraiNm1;
        @JacksonXmlProperty(localName = "iraiNm2")
        String iraiNm2;
        @JacksonXmlProperty(localName = "iraiYubin")
        Integer iraiYubin;
        @JacksonXmlProperty(localName = "iraiTel")
        String iraiTel;
        @JacksonXmlProperty(localName = "iraiMailAddress")
        String iraiMailAddress;
        @JacksonXmlProperty(localName = "shippingDate")
        String shippingDate;
        @JacksonXmlProperty(localName = "kiji1")
        String kiji1;
        @JacksonXmlProperty(localName = "kiji2")
        String kiji2;
        @JacksonXmlProperty(localName = "kiji3")
        String kiji3;
        @JacksonXmlProperty(localName = "kiji4")
        String kiji4;
        @JacksonXmlProperty(localName = "kiji5")
        String kiji5;
        @JacksonXmlProperty(localName = "kiji6")
        String kiji6;
        @JacksonXmlProperty(localName = "binsyuCode")
        Integer binsyuCode;
        @JacksonXmlProperty(localName = "daibikiFlg")
        Integer daibikiFlg;
        @JacksonXmlProperty(localName = "daibikiType")
        Integer daibikiType;
        @JacksonXmlProperty(localName = "shiteiDate")
        String shiteiDate;
        @JacksonXmlProperty(localName = "shiteiTimeCode")
        String shiteiTimeCode;
        @JacksonXmlProperty(localName = "daibikiKingaku")
        Integer daibikiKingaku;
        @JacksonXmlProperty(localName = "daibikiTax")
        Integer daibikiTax;
        @JacksonXmlProperty(localName = "weight1")
        String weight1;
        @JacksonXmlProperty(localName = "weight2")
        String weight2;
        @JacksonXmlProperty(localName = "careSeal1")
        String careSeal1;
        @JacksonXmlProperty(localName = "careSeal2")
        String careSeal2;
        @JacksonXmlProperty(localName = "careSeal3")
        String careSeal3;
        @JacksonXmlProperty(localName = "hokenKingaku")
        Integer hokenKingaku;
        @JacksonXmlProperty(localName = "eidomeFlg")
        Integer eidomeFlg;
        @JacksonXmlProperty(localName = "depotCode")
        String depotCode;
        @JacksonXmlProperty(localName = "mark")
        String mark;
        @JacksonXmlProperty(localName = "nouhinshoData")
        NouhinshoData nouhinshoData;
        @JacksonXmlProperty(localName = "uketoriData")
        UketoriData uketoriData;
        @JacksonXmlProperty(localName = "motoChakuCode")
        Integer motoChakuCode;
        @JacksonXmlProperty(localName = "shukaIraiData")
        ShukaIraiData shukaIraiData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShukaIraiData
    {
        @JacksonXmlProperty(localName = "shukaIraiCode")
        Integer shukaIraiCode;
        @JacksonXmlProperty(localName = "shukaIraiShiteiDate")
        String shukaIraiShiteiDate;
        @JacksonXmlProperty(localName = "shukaIraiShiteiTimeCode")
        String shukaIraiShiteiTimeCode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UketoriData
    {
        @JacksonXmlProperty(localName = "uketoriCode")
        String uketoriCode;
        @JacksonXmlProperty(localName = "uketoriShopCode")
        String uketoriShopCode;
        @JacksonXmlProperty(localName = "uketoriOrderId")
        String uketoriOrderId;
        @JacksonXmlProperty(localName = "uketoriPersonNm1")
        String uketoriPersonNm1;
        @JacksonXmlProperty(localName = "uketoriPersonNm2")
        String uketoriPersonNm2;
        @JacksonXmlProperty(localName = "henpinAdd1")
        String henpinAdd1;
        @JacksonXmlProperty(localName = "henpinAdd2")
        String henpinAdd2;
        @JacksonXmlProperty(localName = "henpinAdd3")
        String henpinAdd3;
        @JacksonXmlProperty(localName = "henpinNm1")
        String henpinNm1;
        @JacksonXmlProperty(localName = "henpinNm2")
        String henpinNm2;
        @JacksonXmlProperty(localName = "henpinYubin")
        Integer henpinYubin;
        @JacksonXmlProperty(localName = "henpinTel")
        String henpinTel;
        @JacksonXmlProperty(localName = "certificationNumber")
        Integer certificationNumber;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NouhinshoData
    {
        @JacksonXmlProperty(localName = "title")
        String title;
        @JacksonXmlProperty(localName = "orderId")
        String orderId;
        @JacksonXmlProperty(localName = "gift")
        String gift;
        @JacksonXmlProperty(localName = "noshi")
        String noshi;
        @JacksonXmlProperty(localName = "shopTel")
        String shopTel;
        @JacksonXmlProperty(localName = "shopFax")
        String shopFax;
        @JacksonXmlProperty(localName = "shopYubin")
        Integer shopYubin;
        @JacksonXmlProperty(localName = "shopAdd1")
        String shopAdd1;
        @JacksonXmlProperty(localName = "shopAdd2")
        String shopAdd2;
        @JacksonXmlProperty(localName = "shopAdd3")
        String shopAdd3;
        @JacksonXmlProperty(localName = "shopNm1")
        String shopNm1;
        @JacksonXmlProperty(localName = "shopNm2")
        String shopNm2;
        @JacksonXmlProperty(localName = "shopMailAddress")
        String shopMailAddress;
        @JacksonXmlProperty(localName = "taxRegisterNo")
        String taxRegisterNo;
        @JacksonXmlProperty(localName = "teikei1")
        String teikei1;
        @JacksonXmlProperty(localName = "teikei2")
        String teikei2;
        @JacksonXmlProperty(localName = "teikei3")
        String teikei3;
        @JacksonXmlProperty(localName = "teikei4")
        String teikei4;
        @JacksonXmlProperty(localName = "teikei5")
        String teikei5;
        @JacksonXmlProperty(localName = "teikei6")
        String teikei6;
        @JacksonXmlProperty(localName = "teikei7")
        String teikei7;
        @JacksonXmlProperty(localName = "teikei8")
        String teikei8;
        @JacksonXmlProperty(localName = "teikei9")
        String teikei9;
        @JacksonXmlProperty(localName = "teikei10")
        String teikei10;
        @JacksonXmlProperty(localName = "biko")
        String biko;
        @JacksonXmlProperty(localName = "orderDate")
        String orderDate;
        @JacksonXmlProperty(localName = "shopNm")
        String shopNm;
        @JacksonXmlProperty(localName = "paymentNm")
        String paymentNm;
        @JacksonXmlProperty(localName = "itemTotalTax")
        Integer itemTotalTax;
        @JacksonXmlProperty(localName = "soryo")
        Integer soryo;
        @JacksonXmlProperty(localName = "tesuryo")
        Integer tesuryo;
        @JacksonXmlProperty(localName = "couponPrice")
        Integer couponPrice;
        @JacksonXmlProperty(localName = "usedPoint")
        Integer usedPoint;
        @JacksonXmlProperty(localName = "totalPrice")
        Integer totalPrice;
        @JacksonXmlElementWrapper(localName = "taxList")
        @JacksonXmlProperty(localName = "taxDetail")
        List<TaxDetail> taxList;
        @JacksonXmlProperty(localName = "itemList")
        List<ItemList> itemList;
        @JacksonXmlProperty(localName = "freeEntry1")
        String freeEntry1;
        @JacksonXmlProperty(localName = "freeEntry2")
        String freeEntry2;
        @JacksonXmlProperty(localName = "freeEntry3")
        String freeEntry3;
        @JacksonXmlProperty(localName = "freeEntry4")
        String freeEntry4;
        @JacksonXmlProperty(localName = "freeEntry5")
        String freeEntry5;
        @JacksonXmlProperty(localName = "freeEntry6")
        String freeEntry6;
        @JacksonXmlProperty(localName = "freeEntry7")
        String freeEntry7;
        @JacksonXmlProperty(localName = "freeEntry8")
        String freeEntry8;
        @JacksonXmlProperty(localName = "freeEntry9")
        String freeEntry9;
        @JacksonXmlProperty(localName = "freeEntry10")
        String freeEntry10;
        @JacksonXmlProperty(localName = "freeEntry11")
        String freeEntry11;
        @JacksonXmlProperty(localName = "freeEntry12")
        String freeEntry12;
        @JacksonXmlProperty(localName = "freeEntry13")
        String freeEntry13;
        @JacksonXmlProperty(localName = "freeEntry14")
        String freeEntry14;
        @JacksonXmlProperty(localName = "freeEntry15")
        String freeEntry15;
        @JacksonXmlProperty(localName = "freeEntry16")
        String freeEntry16;
        @JacksonXmlProperty(localName = "freeEntry17")
        String freeEntry17;
        @JacksonXmlProperty(localName = "freeEntry18")
        String freeEntry18;
        @JacksonXmlProperty(localName = "freeEntry19")
        String freeEntry19;
        @JacksonXmlProperty(localName = "freeEntry20")
        String freeEntry20;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaxDetail
    {
        @JacksonXmlProperty(localName = "taxRate")
        Integer taxRate;
        @JacksonXmlProperty(localName = "taxPrice")
        Integer taxPrice;
        @JacksonXmlProperty(localName = "taxValue")
        Integer taxValue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemList
    {
        @JacksonXmlProperty(localName = "itemDetail")
        ItemDetail itemDetail;
        @JacksonXmlProperty(localName = "itemTotalPrice")
        Integer itemTotalPrice;
        @JacksonXmlProperty(localName = "totalKosu")
        Integer totalKosu;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDetail
    {
        @JacksonXmlProperty(localName = "itemCode")
        String itemCode;
        @JacksonXmlProperty(localName = "itemPickingCode")
        String itemPickingCode;
        @JacksonXmlProperty(localName = "itemKosu")
        Integer itemKosu;
        @JacksonXmlProperty(localName = "itemUnitNm")
        String itemUnitNm;
        @JacksonXmlProperty(localName = "itemName")
        String itemName;
        @JacksonXmlProperty(localName = "itemShosai")
        String itemShosai;
        @JacksonXmlProperty(localName = "itemPrice")
        Integer itemPrice;
        @JacksonXmlProperty(localName = "subTotalPrice")
        Integer subTotalPrice;
        @JacksonXmlProperty(localName = "taxReduceMark")
        String taxReduceMark;
    }
}
