package com.lemonico.core.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils
{

    /**
     * @throws ParseException
     * @Description: 現在の時間を取得してフォーマットする yyyy-MM-dd HH:mm:ss
     * @return: date
     * @Date: 2020/6/9
     */
    public static Date getDate() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sf.format(date);
        try {
            date = sf.parse(dateTime);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }

    /**
     * @description: 获取String -> yyyy-MM
     * @return: java.lang.String
     * @date: 2020/7/30
     */
    public static String getDateMonth() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        return sf.format(date);
    }

    /**
     * @description: 获取String -> yyyyMMdd
     * @return: java.lang.String
     * @date: 2020/7/30
     */
    public static String getDateDay() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String dateTime = sf.format(date);
        return dateTime;
    }

    /**
     * @Param: params : string日期
     * @description: 日期格式化(不带时间戳的)
     * @return: java.util.Date
     * @date: 2020/8/11
     */
    public static Date getNowTime(String params) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringTools.isNullOrEmpty(params)) {
            Date date = new Date();
            String dateTime = simpleDateFormat.format(date);
            params = dateTime;
        }
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(params);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    public static Date getNowTimeWithOutTimeStamp(String params, String tmp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(tmp);
        if (StringTools.isNullOrEmpty(params)) {
            Date date = new Date();
            String dateTime = simpleDateFormat.format(date);
            params = dateTime;
        }
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(params);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    /**
     * @throws ParseException
     * @Description: String → Date
     * @return: date
     * @Date: 2020/07/16
     */
    public static Date stringToDate(String time) {
        Date date;
        if (time == null || time == "") {
            return null;
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }
}
