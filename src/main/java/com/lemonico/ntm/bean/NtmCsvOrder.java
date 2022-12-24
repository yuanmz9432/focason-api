package com.lemonico.ntm.bean;



import com.lemonico.common.bean.Mc105_product_setting;
import com.lemonico.common.bean.Mc107_ntm_product_master;
import com.lemonico.common.bean.Mc108_ntm_area_master;
import com.lemonico.common.bean.Ms012_sponsor_master;
import com.opencsv.bean.CsvBindByName;
import java.util.List;
import lombok.Data;

/**
 * @class NtmCsvOrder
 * @description Ntm个人受注CSV实体
 * @date 2021/6/16
 **/
@Data
public class NtmCsvOrder
{

    @CsvBindByName(column = "通し番号")
    private String serialNum;

    @CsvBindByName(column = "支店コード")
    private String branchCode;

    @CsvBindByName(column = "カタログ1")
    private String catalog1;

    @CsvBindByName(column = "カタログ2")
    private String catalog2;

    @CsvBindByName(column = "カタログ3")
    private String catalog3;

    @CsvBindByName(column = "カタログ4")
    private String catalog4;

    @CsvBindByName(column = "カタログ5")
    private String catalog5;

    @CsvBindByName(column = "カタログ6")
    private String catalog6;

    @CsvBindByName(column = "カタログ7")
    private String catalog7;

    @CsvBindByName(column = "カタログ8")
    private String catalog8;

    @CsvBindByName(column = "カタログ9")
    private String catalog9;

    @CsvBindByName(column = "カタログ10")
    private String catalog10;

    @CsvBindByName(column = "姓名")
    private String name;

    @CsvBindByName(column = "郵便番号")
    private String yubin;

    @CsvBindByName(column = "都道府県")
    private String prefectures;

    @CsvBindByName(column = "住所市区町村名")
    private String addressCity;

    @CsvBindByName(column = "住所町名番地")
    private String addressTown;

    @CsvBindByName(column = "住所ビル名")
    private String addressBuilding;

    @CsvBindByName(column = "TEL")
    private String tel;

    private Mc108_ntm_area_master ntmAreaMaster;

    private Mc105_product_setting productSetting;

    private Ms012_sponsor_master sponsorMaster;

    private List<Mc107_ntm_product_master> ntmProductMasterList;

}
