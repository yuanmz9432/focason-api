package com.lemonico.common.bean;



import java.util.Date;

/**
 * @className: Ms013_api_template
 * @description:
 * @date: 2020/12/21
 **/

public class Ms013_api_template
{
    private Integer id;
    private String template;
    private String identification;
    private Date ins_date;
    private String ins_usr;
    private String biko;
    private Integer del_flg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public Date getIns_date() {
        return ins_date;
    }

    public void setIns_date(Date ins_date) {
        this.ins_date = ins_date;
    }

    public String getIns_usr() {
        return ins_usr;
    }

    public void setIns_usr(String ins_usr) {
        this.ins_usr = ins_usr;
    }

    public String getBiko() {
        return biko;
    }

    public void setBiko(String biko) {
        this.biko = biko;
    }

    public Integer getDel_flg() {
        return del_flg;
    }

    public void setDel_flg(Integer del_flg) {
        this.del_flg = del_flg;
    }

    @Override
    public String toString() {
        return "Ms013_api_template{" +
            "id=" + id +
            ", template='" + template + '\'' +
            ", identification='" + identification + '\'' +
            ", ins_date=" + ins_date +
            ", ins_usr='" + ins_usr + '\'' +
            ", biko='" + biko + '\'' +
            ", del_flg=" + del_flg +
            '}';
    }
}
