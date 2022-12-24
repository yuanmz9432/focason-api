package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * @className: ms016_macro
 * @description: ms016_macro
 * @author: HZM
 * @date: 2021/02/25
 **/
@ApiModel(value = "ms016_macro", description = "ms016_macro")
public class Ms016_macro implements Comparable<Ms016_macro>
{
    @ApiModelProperty(value = "管理ID", required = true)
    private Integer id;
    @ApiModelProperty(value = "店舗ID")
    private String client_id;
    @ApiModelProperty(value = "マクロ名")
    private String macro_name;
    @ApiModelProperty(value = "イベント名")
    private String event_name;
    @ApiModelProperty(value = "条件区分コード")
    private Integer conditions_kube;
    @ApiModelProperty(value = "条件項目コード1")
    private Integer conditions_item01;
    @ApiModelProperty(value = "条件内容01")
    private String conditions01;
    @ApiModelProperty(value = "比較フラグ1")
    private Integer conditions_flg01;

    @ApiModelProperty(value = "条件項目コード2")
    private Integer conditions_item02;
    @ApiModelProperty(value = "条件内容02")
    private String conditions02;
    @ApiModelProperty(value = "比較フラグ2")
    private Integer conditions_flg02;

    @ApiModelProperty(value = "条件項目コード3")
    private Integer conditions_item03;
    @ApiModelProperty(value = "条件内容03")
    private String conditions03;
    @ApiModelProperty(value = "比較フラグ3")
    private Integer conditions_flg03;

    @ApiModelProperty(value = "条件項目コード4")
    private Integer conditions_item04;
    @ApiModelProperty(value = "条件内容04")
    private String conditions04;
    @ApiModelProperty(value = "比較フラグ4")
    private Integer conditions_flg04;

    @ApiModelProperty(value = "条件項目コード5")
    private Integer conditions_item05;
    @ApiModelProperty(value = "条件内容05")
    private String conditions05;
    @ApiModelProperty(value = "比較フラグ5")
    private Integer conditions_flg05;

    @ApiModelProperty(value = "条件項目コード6")
    private Integer conditions_item06;
    @ApiModelProperty(value = "条件内容06")
    private String conditions06;
    @ApiModelProperty(value = "比較フラグ6")
    private Integer conditions_flg06;

    @ApiModelProperty(value = "条件項目コード7")
    private Integer conditions_item07;
    @ApiModelProperty(value = "条件内容07")
    private String conditions07;
    @ApiModelProperty(value = "比較フラグ7")
    private Integer conditions_flg07;

    @ApiModelProperty(value = "条件項目コード8")
    private Integer conditions_item08;
    @ApiModelProperty(value = "条件内容08")
    private String conditions08;
    @ApiModelProperty(value = "比較フラグ8")
    private Integer conditions_flg08;

    @ApiModelProperty(value = "条件項目コード9")
    private Integer conditions_item09;
    @ApiModelProperty(value = "条件内容09")
    private String conditions09;
    @ApiModelProperty(value = "比較フラグ9")
    private Integer conditions_flg09;

    @ApiModelProperty(value = "条件項目コード10")
    private Integer conditions_item10;
    @ApiModelProperty(value = "条件内容10")
    private String conditions10;
    @ApiModelProperty(value = "比較フラグ10")
    private Integer conditions_flg10;

    @ApiModelProperty(value = "条件項目コード11")
    private Integer conditions_item11;
    @ApiModelProperty(value = "条件内容11")
    private String conditions11;
    @ApiModelProperty(value = "比較フラグ11")
    private Integer conditions_flg11;

    @ApiModelProperty(value = "条件項目コード12")
    private Integer conditions_item12;
    @ApiModelProperty(value = "条件内容12")
    private String conditions12;
    @ApiModelProperty(value = "比較フラグ12")
    private Integer conditions_flg12;

    @ApiModelProperty(value = "条件項目コード13")
    private Integer conditions_item13;
    @ApiModelProperty(value = "条件内容13")
    private String conditions13;
    @ApiModelProperty(value = "比較フラグ13")
    private Integer conditions_flg13;

    @ApiModelProperty(value = "条件項目コード14")
    private Integer conditions_item14;
    @ApiModelProperty(value = "条件内容14")
    private String conditions14;
    @ApiModelProperty(value = "比較フラグ14")
    private Integer conditions_flg14;

    @ApiModelProperty(value = "条件項目コード15")
    private Integer conditions_item15;
    @ApiModelProperty(value = "条件内容15")
    private String conditions15;
    @ApiModelProperty(value = "比較フラグ15")
    private Integer conditions_flg15;

    @ApiModelProperty(value = "条件項目コード16")
    private Integer conditions_item16;
    @ApiModelProperty(value = "条件内容16")
    private String conditions16;
    @ApiModelProperty(value = "比較フラグ16")
    private Integer conditions_flg16;

    @ApiModelProperty(value = "条件項目コード17")
    private Integer conditions_item17;
    @ApiModelProperty(value = "条件内容17")
    private String conditions17;
    @ApiModelProperty(value = "比較フラグ17")
    private Integer conditions_flg17;

    @ApiModelProperty(value = "条件項目コード18")
    private Integer conditions_item18;
    @ApiModelProperty(value = "条件内容18")
    private String conditions18;
    @ApiModelProperty(value = "比較フラグ18")
    private Integer conditions_flg18;

    @ApiModelProperty(value = "条件項目コード19")
    private Integer conditions_item19;
    @ApiModelProperty(value = "条件内容19")
    private String conditions19;
    @ApiModelProperty(value = "比較フラグ19")
    private Integer conditions_flg19;

    @ApiModelProperty(value = "条件項目コード20")
    private Integer conditions_item20;
    @ApiModelProperty(value = "条件内容20")
    private String conditions20;
    @ApiModelProperty(value = "比較フラグ20")
    private Integer conditions_flg20;

    @ApiModelProperty(value = "自動処理フラグ")
    private Integer action_code;
    @ApiModelProperty(value = "自動処理内容")
    private String action_content;

    @ApiModelProperty(value = "マクロステータス")
    private Integer macro_status;
    @ApiModelProperty(value = "作成者")
    private String ins_usr;
    @ApiModelProperty(value = "作成日時")
    private Date ins_date;
    @ApiModelProperty(value = "更新者")
    private String upd_usr;
    @ApiModelProperty(value = "更新日時")
    private Date upd_date;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;
    @ApiModelProperty(value = "优先级别")
    private Integer priority;
    @ApiModelProperty(value = "开始时间")
    private Date start_time;
    @ApiModelProperty(value = "结束时间")
    private Date end_time;

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getMacro_name() {
        return macro_name;
    }

    public void setMacro_name(String macro_name) {
        this.macro_name = macro_name;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public Integer getConditions_kube() {
        return conditions_kube;
    }

    public void setConditions_kube(Integer conditions_kube) {
        this.conditions_kube = conditions_kube;
    }

    public Integer getConditions_item01() {
        return conditions_item01;
    }

    public void setConditions_item01(Integer conditions_item01) {
        this.conditions_item01 = conditions_item01;
    }

    public String getConditions01() {
        return conditions01;
    }

    public void setConditions01(String conditions01) {
        this.conditions01 = conditions01;
    }

    public Integer getConditions_flg01() {
        return conditions_flg01;
    }

    public void setConditions_flg01(Integer conditions_flg01) {
        this.conditions_flg01 = conditions_flg01;
    }

    public Integer getConditions_item02() {
        return conditions_item02;
    }

    public void setConditions_item02(Integer conditions_item02) {
        this.conditions_item02 = conditions_item02;
    }

    public String getConditions02() {
        return conditions02;
    }

    public void setConditions02(String conditions02) {
        this.conditions02 = conditions02;
    }

    public Integer getConditions_flg02() {
        return conditions_flg02;
    }

    public void setConditions_flg02(Integer conditions_flg02) {
        this.conditions_flg02 = conditions_flg02;
    }

    public Integer getConditions_item03() {
        return conditions_item03;
    }

    public void setConditions_item03(Integer conditions_item03) {
        this.conditions_item03 = conditions_item03;
    }

    public String getConditions03() {
        return conditions03;
    }

    public void setConditions03(String conditions03) {
        this.conditions03 = conditions03;
    }

    public Integer getConditions_flg03() {
        return conditions_flg03;
    }

    public void setConditions_flg03(Integer conditions_flg03) {
        this.conditions_flg03 = conditions_flg03;
    }

    public Integer getConditions_item04() {
        return conditions_item04;
    }

    public void setConditions_item04(Integer conditions_item04) {
        this.conditions_item04 = conditions_item04;
    }

    public String getConditions04() {
        return conditions04;
    }

    public void setConditions04(String conditions04) {
        this.conditions04 = conditions04;
    }

    public Integer getConditions_flg04() {
        return conditions_flg04;
    }

    public void setConditions_flg04(Integer conditions_flg04) {
        this.conditions_flg04 = conditions_flg04;
    }

    public Integer getConditions_item05() {
        return conditions_item05;
    }

    public void setConditions_item05(Integer conditions_item05) {
        this.conditions_item05 = conditions_item05;
    }

    public String getConditions05() {
        return conditions05;
    }

    public void setConditions05(String conditions05) {
        this.conditions05 = conditions05;
    }

    public Integer getConditions_flg05() {
        return conditions_flg05;
    }

    public void setConditions_flg05(Integer conditions_flg05) {
        this.conditions_flg05 = conditions_flg05;
    }

    public Integer getConditions_item06() {
        return conditions_item06;
    }

    public void setConditions_item06(Integer conditions_item06) {
        this.conditions_item06 = conditions_item06;
    }

    public String getConditions06() {
        return conditions06;
    }

    public void setConditions06(String conditions06) {
        this.conditions06 = conditions06;
    }

    public Integer getConditions_flg06() {
        return conditions_flg06;
    }

    public void setConditions_flg06(Integer conditions_flg06) {
        this.conditions_flg06 = conditions_flg06;
    }

    public Integer getConditions_item07() {
        return conditions_item07;
    }

    public void setConditions_item07(Integer conditions_item07) {
        this.conditions_item07 = conditions_item07;
    }

    public String getConditions07() {
        return conditions07;
    }

    public void setConditions07(String conditions07) {
        this.conditions07 = conditions07;
    }

    public Integer getConditions_flg07() {
        return conditions_flg07;
    }

    public void setConditions_flg07(Integer conditions_flg07) {
        this.conditions_flg07 = conditions_flg07;
    }

    public Integer getConditions_item08() {
        return conditions_item08;
    }

    public void setConditions_item08(Integer conditions_item08) {
        this.conditions_item08 = conditions_item08;
    }

    public String getConditions08() {
        return conditions08;
    }

    public void setConditions08(String conditions08) {
        this.conditions08 = conditions08;
    }

    public Integer getConditions_flg08() {
        return conditions_flg08;
    }

    public void setConditions_flg08(Integer conditions_flg08) {
        this.conditions_flg08 = conditions_flg08;
    }

    public Integer getConditions_item09() {
        return conditions_item09;
    }

    public void setConditions_item09(Integer conditions_item09) {
        this.conditions_item09 = conditions_item09;
    }

    public String getConditions09() {
        return conditions09;
    }

    public void setConditions09(String conditions09) {
        this.conditions09 = conditions09;
    }

    public Integer getConditions_flg09() {
        return conditions_flg09;
    }

    public void setConditions_flg09(Integer conditions_flg09) {
        this.conditions_flg09 = conditions_flg09;
    }

    public Integer getConditions_item10() {
        return conditions_item10;
    }

    public void setConditions_item10(Integer conditions_item10) {
        this.conditions_item10 = conditions_item10;
    }

    public String getConditions10() {
        return conditions10;
    }

    public void setConditions10(String conditions10) {
        this.conditions10 = conditions10;
    }

    public Integer getConditions_flg10() {
        return conditions_flg10;
    }

    public void setConditions_flg10(Integer conditions_flg10) {
        this.conditions_flg10 = conditions_flg10;
    }


    public Integer getConditions_item11() {
        return conditions_item11;
    }

    public void setConditions_item11(Integer conditions_item11) {
        this.conditions_item11 = conditions_item11;
    }

    public String getConditions11() {
        return conditions11;
    }

    public void setConditions11(String conditions11) {
        this.conditions11 = conditions11;
    }

    public Integer getConditions_flg11() {
        return conditions_flg11;
    }

    public void setConditions_flg11(Integer conditions_flg11) {
        this.conditions_flg11 = conditions_flg11;
    }

    public Integer getConditions_item12() {
        return conditions_item12;
    }

    public void setConditions_item12(Integer conditions_item12) {
        this.conditions_item12 = conditions_item12;
    }

    public String getConditions12() {
        return conditions12;
    }

    public void setConditions12(String conditions12) {
        this.conditions12 = conditions12;
    }

    public Integer getConditions_flg12() {
        return conditions_flg12;
    }

    public void setConditions_flg12(Integer conditions_flg12) {
        this.conditions_flg12 = conditions_flg12;
    }

    public Integer getConditions_item13() {
        return conditions_item13;
    }

    public void setConditions_item13(Integer conditions_item13) {
        this.conditions_item13 = conditions_item13;
    }

    public String getConditions13() {
        return conditions13;
    }

    public void setConditions13(String conditions13) {
        this.conditions13 = conditions13;
    }

    public Integer getConditions_flg13() {
        return conditions_flg13;
    }

    public void setConditions_flg13(Integer conditions_flg13) {
        this.conditions_flg13 = conditions_flg13;
    }

    public Integer getConditions_item14() {
        return conditions_item14;
    }

    public void setConditions_item14(Integer conditions_item14) {
        this.conditions_item14 = conditions_item14;
    }

    public String getConditions14() {
        return conditions14;
    }

    public void setConditions14(String conditions14) {
        this.conditions14 = conditions14;
    }

    public Integer getConditions_flg14() {
        return conditions_flg14;
    }

    public void setConditions_flg14(Integer conditions_flg14) {
        this.conditions_flg14 = conditions_flg14;
    }

    public Integer getConditions_item15() {
        return conditions_item15;
    }

    public void setConditions_item15(Integer conditions_item15) {
        this.conditions_item15 = conditions_item15;
    }

    public String getConditions15() {
        return conditions15;
    }

    public void setConditions15(String conditions15) {
        this.conditions15 = conditions15;
    }

    public Integer getConditions_flg15() {
        return conditions_flg15;
    }

    public void setConditions_flg15(Integer conditions_flg15) {
        this.conditions_flg15 = conditions_flg15;
    }

    public Integer getConditions_item16() {
        return conditions_item16;
    }

    public void setConditions_item16(Integer conditions_item16) {
        this.conditions_item16 = conditions_item16;
    }

    public String getConditions16() {
        return conditions16;
    }

    public void setConditions16(String conditions16) {
        this.conditions16 = conditions16;
    }

    public Integer getConditions_flg16() {
        return conditions_flg16;
    }

    public void setConditions_flg16(Integer conditions_flg16) {
        this.conditions_flg16 = conditions_flg16;
    }

    public Integer getConditions_item17() {
        return conditions_item17;
    }

    public void setConditions_item17(Integer conditions_item17) {
        this.conditions_item17 = conditions_item17;
    }

    public String getConditions17() {
        return conditions17;
    }

    public void setConditions17(String conditions17) {
        this.conditions17 = conditions17;
    }

    public Integer getConditions_flg17() {
        return conditions_flg17;
    }

    public void setConditions_flg17(Integer conditions_flg17) {
        this.conditions_flg17 = conditions_flg17;
    }

    public Integer getConditions_item18() {
        return conditions_item18;
    }

    public void setConditions_item18(Integer conditions_item18) {
        this.conditions_item18 = conditions_item18;
    }

    public String getConditions18() {
        return conditions18;
    }

    public void setConditions18(String conditions18) {
        this.conditions18 = conditions18;
    }

    public Integer getConditions_flg18() {
        return conditions_flg18;
    }

    public void setConditions_flg18(Integer conditions_flg18) {
        this.conditions_flg18 = conditions_flg18;
    }

    public Integer getConditions_item19() {
        return conditions_item19;
    }

    public void setConditions_item19(Integer conditions_item19) {
        this.conditions_item19 = conditions_item19;
    }

    public String getConditions19() {
        return conditions19;
    }

    public void setConditions19(String conditions19) {
        this.conditions19 = conditions19;
    }

    public Integer getConditions_flg19() {
        return conditions_flg19;
    }

    public void setConditions_flg19(Integer conditions_flg19) {
        this.conditions_flg19 = conditions_flg19;
    }

    public Integer getConditions_item20() {
        return conditions_item20;
    }

    public void setConditions_item20(Integer conditions_item20) {
        this.conditions_item20 = conditions_item20;
    }

    public String getConditions20() {
        return conditions20;
    }

    public void setConditions20(String conditions20) {
        this.conditions20 = conditions20;
    }

    public Integer getConditions_flg20() {
        return conditions_flg20;
    }

    public void setConditions_flg20(Integer conditions_flg20) {
        this.conditions_flg20 = conditions_flg20;
    }

    public Integer getAction_code() {
        return action_code;
    }

    public void setAction_code(Integer action_code) {
        this.action_code = action_code;
    }

    public String getAction_content() {
        return action_content;
    }

    public void setAction_content(String action_content) {
        this.action_content = action_content;
    }

    public Integer getMacro_status() {
        return macro_status;
    }

    public void setMacro_status(Integer macro_status) {
        this.macro_status = macro_status;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public String getUpd_usr() {
        return upd_usr;
    }

    public void setUpd_usr(String upd_usr) {
        this.upd_usr = upd_usr;
    }

    public Date getUpd_date() {
        return upd_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    @Override
    public String toString() {
        return "Ms016_macro{" +
            "id=" + id +
            ", client_id='" + client_id + '\'' +
            ", macro_name='" + macro_name + '\'' +
            ", event_name='" + event_name + '\'' +
            ", conditions_kube=" + conditions_kube +
            ", conditions_item01=" + conditions_item01 +
            ", conditions01='" + conditions01 + '\'' +
            ", conditions_flg01=" + conditions_flg01 +
            ", conditions_item02=" + conditions_item02 +
            ", conditions02='" + conditions02 + '\'' +
            ", conditions_flg02=" + conditions_flg02 +
            ", conditions_item03=" + conditions_item03 +
            ", conditions03='" + conditions03 + '\'' +
            ", conditions_flg03=" + conditions_flg03 +
            ", conditions_item04=" + conditions_item04 +
            ", conditions04='" + conditions04 + '\'' +
            ", conditions_flg04=" + conditions_flg04 +
            ", conditions_item05=" + conditions_item05 +
            ", conditions05='" + conditions05 + '\'' +
            ", conditions_flg05=" + conditions_flg05 +
            ", conditions_item06=" + conditions_item06 +
            ", conditions06='" + conditions06 + '\'' +
            ", conditions_flg06=" + conditions_flg06 +
            ", conditions_item07=" + conditions_item07 +
            ", conditions07='" + conditions07 + '\'' +
            ", conditions_flg07=" + conditions_flg07 +
            ", conditions_item08=" + conditions_item08 +
            ", conditions08='" + conditions08 + '\'' +
            ", conditions_flg08=" + conditions_flg08 +
            ", conditions_item09=" + conditions_item09 +
            ", conditions09='" + conditions09 + '\'' +
            ", conditions_flg09=" + conditions_flg09 +
            ", conditions_item10=" + conditions_item10 +
            ", conditions10='" + conditions10 + '\'' +
            ", conditions_flg10=" + conditions_flg10 +
            ", conditions_item11=" + conditions_item11 +
            ", conditions11='" + conditions11 + '\'' +
            ", conditions_flg11=" + conditions_flg11 +
            ", conditions_item12=" + conditions_item12 +
            ", conditions12='" + conditions12 + '\'' +
            ", conditions_flg12=" + conditions_flg12 +
            ", conditions_item13=" + conditions_item13 +
            ", conditions13='" + conditions13 + '\'' +
            ", conditions_flg13=" + conditions_flg13 +
            ", conditions_item14=" + conditions_item14 +
            ", conditions14='" + conditions14 + '\'' +
            ", conditions_flg14=" + conditions_flg14 +
            ", conditions_item15=" + conditions_item15 +
            ", conditions15='" + conditions15 + '\'' +
            ", conditions_flg15=" + conditions_flg15 +
            ", conditions_item16=" + conditions_item16 +
            ", conditions16='" + conditions16 + '\'' +
            ", conditions_flg16=" + conditions_flg16 +
            ", conditions_item17=" + conditions_item17 +
            ", conditions17='" + conditions17 + '\'' +
            ", conditions_flg17=" + conditions_flg17 +
            ", conditions_item18=" + conditions_item18 +
            ", conditions18='" + conditions18 + '\'' +
            ", conditions_flg18=" + conditions_flg18 +
            ", conditions_item19=" + conditions_item19 +
            ", conditions19='" + conditions19 + '\'' +
            ", conditions_flg19=" + conditions_flg19 +
            ", conditions_item20=" + conditions_item20 +
            ", conditions20='" + conditions20 + '\'' +
            ", conditions_flg20=" + conditions_flg20 +
            ", action_code=" + action_code +
            ", action_content='" + action_content + '\'' +
            ", macro_status=" + macro_status +
            ", ins_usr='" + ins_usr + '\'' +
            ", ins_date=" + ins_date +
            ", upd_usr='" + upd_usr + '\'' +
            ", upd_date=" + upd_date +
            ", del_flg=" + del_flg +
            ", priority=" + priority +
            ", start_time=" + start_time +
            ", end_time=" + end_time +
            '}';
    }

    // 重写CompareTo 按照ms016表的优先级顺序 升序排序
    @Override
    public int compareTo(Ms016_macro o) {
        if (this.getPriority() > o.getPriority()) {
            return 1;
        } else if (this.getPriority() < o.getPriority()) {
            return -1;
        } else {
            return 0;
        }
    }
}
