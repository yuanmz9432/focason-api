package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @description csv出力科目表
 * @date 2021/03/19
 **/
@ApiModel(value = "Tc210_setting_yoto", description = "csv出力科目表")
public class Tc210_setting_yoto
{
    @ApiModelProperty(value = "用途ID", required = true)
    private String yoto_id;
    @ApiModelProperty(value = "模板")
    private String name;
    @ApiModelProperty(value = "模板")
    private String template;
    @ApiModelProperty(value = "削除フラグ")
    private Integer del_flg;

    public String getYoto_id() {
        return yoto_id;
    }

    public void setYoto_id(String yoto_id) {
        this.yoto_id = yoto_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    @Override
    public String toString() {
        return "Tc210_setting_yoto{" +
            "yoto_id='" + yoto_id + '\'' +
            ", name='" + name + '\'' +
            ", template='" + template + '\'' +
            ", del_flg=" + del_flg +
            '}';
    }
}
