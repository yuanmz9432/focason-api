package com.lemonico.common.bean;



import com.opencsv.bean.CsvBindByName;
import java.io.Serializable;

/**
 * @className: Rk145_csv_img
 * @description: 商品画像CSV entity
 * @date: 2020/9/15 13:35
 **/
public class Rk145_csv_img implements Serializable
{

    @CsvBindByName(column = "商品ID（必須）", required = true)
    private String item001;

    @CsvBindByName(column = "商品名", required = true)
    private String item002;

    @CsvBindByName(column = "商品コード")
    private String item003;

    @CsvBindByName(column = "画像1")
    private String item004;

    @CsvBindByName(column = "画像2")
    private String item005;

    @CsvBindByName(column = "画像3")
    private String item006;

    public String getItem001() {
        return item001;
    }

    public void setItem001(String item001) {
        this.item001 = item001;
    }

    public String getItem002() {
        return item002;
    }

    public void setItem002(String item002) {
        this.item002 = item002;
    }

    public String getItem003() {
        return item003;
    }

    public void setItem003(String item003) {
        this.item003 = item003;
    }

    public String getItem004() {
        return item004;
    }

    public void setItem004(String item004) {
        this.item004 = item004;
    }

    public String getItem005() {
        return item005;
    }

    public void setItem005(String item005) {
        this.item005 = item005;
    }

    public String getItem006() {
        return item006;
    }

    public void setItem006(String item006) {
        this.item006 = item006;
    }

    @Override
    public String toString() {
        return "Rk145_csv_img{" +
            "item001='" + item001 + '\'' +
            ", item002='" + item002 + '\'' +
            ", item003='" + item003 + '\'' +
            ", item004='" + item004 + '\'' +
            ", item005='" + item005 + '\'' +
            ", item006='" + item006 + '\'' +
            '}';
    }
}
