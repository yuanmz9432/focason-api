package com.lemonico.core.utils;



import java.util.regex.Pattern;

/**
 * 文字列ツール
 *
 * @since 1.0.0
 */
public class StringTools
{

    public static boolean isNullOrEmpty(String str) {
        return null == str || "".equals(str) || "null".equals(str) || " ".equals(str);
    }

    public static boolean isNullOrEmpty(Object obj) {
        return null == obj || "".equals(obj);
    }

    public static boolean isInteger(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        return regular(str, "^[-\\+]?[\\d\\.]*$");
    }

    /**
     * 正規表現をマッチする
     *
     * @param str 文字列
     * @param reg 正規表現ルール
     * @return マッチ結果
     * @since 1.0.0
     */
    public static boolean regular(String str, String reg) {
        return Pattern.compile(reg).matcher(str).matches();
    }

    /**
     * 判断字符串是否仅包含半角字符, 注：只能判断因为字符串和数字，不能判断日文
     *
     * @param str 判断子
     * @return true: 含む false: 含まない
     */
    public static boolean isIncludeHalfWidth(String str) {
        if (null == str)
            return false;
        byte[] byteArr;
        for (int i = 0; i < str.length(); i++) {
            try {
                byteArr = (Character.toString(str.charAt(i))).getBytes("MS932");
            } catch (Exception e) {
                return true;
            }
            if (byteArr.length == 1) {
                return false;
            }
        }
        return true;
    }
}
