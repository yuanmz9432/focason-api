package com.lemonico.core.utils;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms006_delivery_time;
import com.lemonico.common.bean.Ms007_setting;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.LcValidationErrorException;
import com.lemonico.core.utils.constants.Constants;
import info.monitorenter.cpdetector.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 共通ツール
 *
 * @since 1.0.0
 */
public class CommonUtils
{

    private final static Logger logger = LoggerFactory.getLogger(CommonUtils.class);
    private static final char DBC_SPACE = ' '; // 半角空格
    private static final char SBC_SPACE = 12288; // 全角空格 12288
    private static final char DBC_CHAR_START = 33; // 半角!
    private static final char DBC_CHAR_END = 126; // 半角~
    // ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
    private static final int CONVERT_STEP = 65248;
    // 全角对应于ASCII表的可见字符从！开始，偏移值为65281
    private static final char SBC_CHAR_START = 65281;
    // 全角对应于ASCII表的可见字符到～结束，偏移值为65374
    private static final char SBC_CHAR_END = 65374;

    /**
     * Returns json for a success message with info as an empty object
     */
    public static JSONObject success() {
        return success(new JSONObject());
    }

    /**
     * Returns a json with a return code of 200
     */
    public static JSONObject success(Object info) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", Constants.SUCCESS_CODE);
        jsonObject.put("msg", Constants.SUCCESS_MSG);
        jsonObject.put("info", info);
        return jsonObject;
    }

    /**
     * 処理失敗
     *
     * @param errorCode {@link ErrorCode}
     * @return 失敗情報
     * @since 1.0.0
     */
    public static JSONObject failure(ErrorCode errorCode) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("code", errorCode.getValue());
        resultJson.put("msg", errorCode.getDetail());
        resultJson.put("info", new JSONObject());
        return resultJson;
    }

    /**
     * 処理失敗
     *
     * @param errorCode {@link ErrorCode}
     * @param errMsg 異常メッセージ
     * @return 失敗情報
     * @since 1.0.0
     */
    public static JSONObject failure(ErrorCode errorCode, String errMsg) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("code", errorCode.getValue());
        resultJson.put("msg", errorCode.getDetail());
        resultJson.put("info", errMsg);
        return resultJson;
    }

    /**
     * 処理失敗
     *
     * @param errorCode {@link ErrorCode}
     * @param object 異常対象
     * @return 失敗情報
     * @since 1.0.0
     */
    public static JSONObject failure(ErrorCode errorCode, Object object) {
        JSONObject resultJson = new JSONObject();
        resultJson.put("code", errorCode.getValue());
        resultJson.put("msg", errorCode.getDetail());
        resultJson.put("info", object);
        return resultJson;
    }

    /**
     * ファイル文字コードチェック
     *
     * @param url ファイルURL
     * @param charset 文字コード
     * @return チェック結果
     * @since 1.0.0
     */
    public static boolean determineEncoding(URL url, String[] charset) {
        /*
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
         * JChardetFacade、ASCIIDetector、UnicodeDetector。
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
         * cpDetector是基于统计学原理的，不保证完全正确。
         */
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
         * 指示是否显示探测过程的详细信息，为false不显示。
         */
        detector.add(new ParsingDetector(false));
        /*
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());
        Charset fileCharset = null;
        try {
            fileCharset = detector.detectCodepage(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileCharset != null) {
            for (String s : charset) {
                if ("SHIFT_JIS".equals(s) || "Windows-31J".equals(s)) {
                    s = "Shift_JIS";
                }
                return s.equals(fileCharset.name());
            }
        }
        return false;
    }

    /**
     * ファイル文字コードチェック
     *
     * @param inputStream ファイル
     * @param charset 文字コード
     * @return チェック結果
     * @since 1.0.0
     */
    public static boolean determineEncoding(InputStream inputStream, String[] charset) {
        /*
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、
         * JChardetFacade、ASCIIDetector、UnicodeDetector。
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar
         * cpDetector是基于统计学原理的，不保证完全正确。
         */
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        /*
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于
         * 指示是否显示探测过程的详细信息，为false不显示。
         */
        detector.add(new ParsingDetector(false));
        /*
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
         */
        detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());
        Charset fileCharset = null;
        try {
            fileCharset = detector.detectCodepage(inputStream, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fileCharset != null) {
            for (String s : charset) {
                // 受注放到数据库的编码格式与detectCodepage出来的编码格式不一致，修改后再判断
                if ("SHIFT-JIS".equals(s) || "Windows-31J".equals(s)) {
                    s = "Shift_JIS";
                }
                return s.equals(fileCharset.name());
            }
        }
        return false;
    }

    /**
     * Verify that all required fields are present
     */
    public static void hashAllRequired(final JSONObject jsonObject, String requiredColumns) {
        if (!StringTools.isNullOrEmpty(requiredColumns)) {
            String[] columns = requiredColumns.split(",");
            StringBuilder missCol = new StringBuilder();
            for (String column : columns) {
                Object var = jsonObject.get(column.trim());
                if (StringTools.isNullOrEmpty(var)) {
                    missCol.append(column).append("  ");
                }
            }
            if (!StringTools.isNullOrEmpty(missCol.toString())) {
                jsonObject.clear();
                throw new LcValidationErrorException("必須パラメーターが存在するので、ご確認お願いします。");
            }
        }
    }

    /**
     * 文字列を数字に変更する
     *
     * @param param 文字列
     * @return 変更された数字
     * @since 1.0.0
     */
    public static Integer toInteger(String param) {
        if (param == null || "".equals(param) || "null".equals(param)) {
            return 0;
        }
        return Integer.parseInt(param);
    }

    /**
     * 文字列を数字に変更する
     *
     * @param param 文字列
     * @return 変更された数字
     * @since 1.0.0
     */
    public static Long toLong(String param) {
        if (param == null || "".equals(param)) {
            return 0L;
        }
        return Long.parseLong(param);
    }

    /**
     * 全角を半角に変更する
     *
     * @param param 文字列
     * @return 変更された文字列
     * @since 1.0.0
     */
    public static String conversionCharacter(String param) {
        if (param == null) {
            return null;
        }
        StringBuilder newString = new StringBuilder(param.length() + 1);
        char[] chars = param.toCharArray();
        for (int i = 0; i < param.length(); i++) {
            if (chars[i] >= SBC_CHAR_START && chars[i] <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内
                newString.append((char) (chars[i] - CONVERT_STEP));
            } else if (chars[i] == SBC_SPACE) { // 如果是全角空格
                newString.append(DBC_SPACE);
            } else { // 不处理全角空格，全角！到全角～区间外的字符
                newString.append(chars[i]);
            }
        }
        return newString.toString();
    }

    // 获取token里面的信息
    public static String getToken(String param, HttpServletRequest httpServletRequest) {
        // String authorization = httpServletRequest.getHeader(JwtUtils.AUTH_HEADER);
        // String requestParam = "";
        // JwtToken token = new JwtToken(authorization);
        // if (!StringTools.isNullOrEmpty(token)) {
        // requestParam = JwtUtils.getClaimFiled((String) token.getCredentials(), param);
        // }
        return "";
    }

    /**
     * @description: 获取本月所属的代号
     * @return: java.lang.String
     * @date: 2020/06/17
     */
    public static String getLastClientIdStr() {
        Calendar date = Calendar.getInstance();
        String account_id = "";
        int year = Integer.valueOf(date.get(Calendar.YEAR));
        int month = date.get(Calendar.MONTH) + 1;
        // 18 代表R
        int result = Constants.START_ASC + year % 26 - 18;
        if (result == 64) {
            result = 90;
        }
        if (result > 90) {
            result = 65;
        }
        account_id = String.valueOf((char) result);
        switch (month) {
            case 1:
                account_id += "J";
                break;
            case 2:
                account_id += "F";
                break;
            case 3:
                account_id += "M";
                break;
            case 4:
                account_id += "A";
                break;
            case 5:
                account_id += "W";
                break;
            case 6:
                account_id += "U";
                break;
            case 7:
                account_id += "L";
                break;
            case 8:
                account_id += "G";
                break;
            case 9:
                account_id += "S";
                break;
            case 10:
                account_id += "O";
                break;
            case 11:
                account_id += "N";
                break;
            case 12:
                account_id += "D";
                break;
            default:
                break;
        }
        return account_id;
    }

    /**
     * @Param: lastClientId: 当前最大Id
     * @param: account_id ： 本月代号
     * @description: 获取最大店铺Id
     * @return: java.lang.String
     * @date: 2020/8/6
     */
    public static String getMaxClientId(String lastClientId, String account_id) {
        String newId = null;
        int num;
        if (lastClientId != null) {
            num = Integer.parseInt(lastClientId.substring(2));
            num++;
        } else {
            num = 1;
        }
        String str = String.format("%03d", num);
        newId = account_id + str;
        return newId;
    }

    /**
     * @Param: zip
     * @description: 截取邮编番号
     * @return: java.lang.String
     * @date: 2020/8/29
     */
    public static String getZip(String zip) {
        if (zip.length() == 7) {
            String substring = zip.substring(0, 3);
            String substringEnd = zip.substring(3, 7);
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder append = stringBuilder.append(substring).append("-").append(substringEnd);
            zip = append.toString();
        }
        return zip;
    }

    public static String getNewDate(Date param) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = "";
        if (!StringTools.isNullOrEmpty(param)) {
            format = simpleDateFormat.format(param);
        } else {
            format = simpleDateFormat.format(new Date());
        }
        return format;
    }

    public static String transformDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = "";
        if (!StringTools.isNullOrEmpty(date)) {
            format = simpleDateFormat.format(date);
        }
        return format;
    }

    // 格式化 邮编番号
    public static String formatZip(String zip) {
        if (zip.length() == 7) {
            StringBuilder stringBuilder = new StringBuilder();
            String zip1 = zip.substring(0, 3);
            String zip2 = zip.substring(3, 7);
            stringBuilder.append(zip1).append("-").append(zip2);
            return stringBuilder.toString();
        }
        return zip;
    }

    /**
     * @Param: file : 上传的文件
     * @param: path : 文件上传的路径
     * @param: keepPath : 文件在数据库里面保存的路径
     * @description: 上传文件公共方法
     * @return: java.lang.String
     * @date: 2020/9/27
     */
    public static String uploadFile(MultipartFile file, String path, String keepPath) {
        File uploadDirectory = new File(path);
        if (uploadDirectory.exists()) {
            if (!uploadDirectory.isDirectory()) {
                uploadDirectory.delete();
            }
        } else {
            uploadDirectory.mkdirs();
        }
        String filename = file.getOriginalFilename();
        String filePath = path + filename;
        File outFile = new File(filePath);
        try {
            file.transferTo(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keepPath + filename;
    }

    public static Timestamp dealDateFormat(String oldDateStr) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        // 日付を取得
        String datetime = dateTransfer(oldDateStr);
        if (!StringTools.isNullOrEmpty(datetime)) {
            ts = Timestamp.valueOf(datetime);
        }
        return ts;
    }

    /**
     * @Param: 日付(yyyy - mm - dd)
     * @description: 日付時間帯(23 : 59 : 59)を取得
     * @return: Date
     * @author: wang
     * @date: 2021/4/20
     */
    public static Date getDateEnd(Date date) {
        if (!StringTools.isNullOrEmpty(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date d = calendar.getTime();
            return d;
        }
        return null;
    }

    /**
     * @Param: 日付(yyyy - mm - dd)
     * @description: 日付時間帯(23 : 59 : 59)を取得
     * @return: Date
     * @author: wang
     * @date: 2021/4/20
     */
    public static Date getDateEnd(String str) {
        // 文字列を日付に変更
        Date date = DateUtils.stringToDate(str);
        if (!StringTools.isNullOrEmpty(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date d = calendar.getTime();
            return d;
        }
        return null;
    }

    /**
     * @Param: 日付(yyyy - mm - dd)
     * @description: 日付時間帯(00 : 00 : 00)を取得
     * @return: Date
     * @author: wang
     * @date: 2021/4/20
     */
    public static Date getDateStar(Date date) {
        if (!StringTools.isNullOrEmpty(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            Date d = calendar.getTime();
            return d;
        }
        return null;
    }

    public static String getContact(Integer contact) {
        String value = String.valueOf(contact);
        if (contact == 10) {
            value = "010";
        }
        if (contact == 1) {
            value = "001";
        }
        if (contact == 11) {
            value = "011";
        }
        return value;
    }

    public static String trimSpace(String str) {
        if (str != null && !"".equals(str)) {
            str = str.trim();
        }
        return str;
    }

    public static String dateToStr(Date dateDate) {
        String dateString = null;
        if (!StringTools.isNullOrEmpty(dateDate)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            dateString = formatter.format(dateDate);
        }
        return dateString;
    }

    /**
     * 文字列(yyyy / mm / dd)
     *
     * @param strDate 時間文字列
     * @return java.lang.String
     * @author wang
     * @date 2021/1/16
     */
    public static Date stringToData(String strDate) {
        Date date;
        if (!StringTools.isNullOrEmpty(strDate)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            ParsePosition pos = new ParsePosition(0);
            date = formatter.parse(strDate, pos);
            return date;
        } else {
            return null;
        }
    }

    public static String dateTransfer(String str) {
        if (!StringTools.isNullOrEmpty(str)) {
            str = str.replaceAll("/", "-");
            // @時間改善 wang 2021/4/16
            str = str.replaceAll("T", " ");
            str = str.replaceAll("\\+", " ");
            String[] tem = str.split(" ");
            if (tem.length == 1) {
                str = tem[0] + " 00:00:00";
            } else {
                String[] hourArr = tem[1].split(":");
                switch (hourArr.length) {
                    case 1:
                        if (Integer.parseInt(hourArr[0]) < 10) {
                            hourArr[0] = "0" + hourArr[0];
                        }
                        str = hourArr[0] + ":00:00";
                        break;
                    case 2:
                        if (Integer.parseInt(hourArr[0]) < 10) {
                            hourArr[0] = "0" + hourArr[0];
                        }
                        if (Integer.parseInt(hourArr[1]) < 10) {
                            hourArr[1] = "0" + hourArr[1];
                        }
                        str = hourArr[0] + ":" + hourArr[1] + ":00";
                        break;
                    case 3:
                        if (Integer.parseInt(hourArr[0]) < 10) {
                            hourArr[0] = "0" + hourArr[0];
                        }
                        if (Integer.parseInt(hourArr[1]) < 10) {
                            hourArr[1] = "0" + hourArr[1];
                        }
                        if (Integer.parseInt(hourArr[2]) < 10) {
                            hourArr[2] = "0" + hourArr[2];
                        }

                        str = hourArr[0] + ":" + hourArr[1] + ":" + hourArr[2];
                        break;
                    default:
                        str = tem[0] + " 00:00:00";
                        break;
                }
                str = tem[0] + " " + str;
            }
        } else {
            str = "";
        }
        return str;
    }

    /**
     * @description: 获取最大 受注取込履歴ID
     * @return: java.lang.String
     * @date: 2020/9/9
     */
    public static Integer getMaxHistoryId(String lastOrderHistoryNo) {
        Integer lastHistoryId = 0;
        if (!StringTools.isNullOrEmpty(lastOrderHistoryNo)) {
            lastHistoryId = Integer.valueOf(lastOrderHistoryNo);
        }
        String historyid = String.valueOf(lastHistoryId);
        Integer historyId = Integer.parseInt(historyid) + 1;
        return historyId;
    }

    /**
     * @description: 获取最大在庫履歴ID
     * @return: java.lang.String
     * @date: 2020/07/03
     */
    public static String getMaxStockHistoryId(String stockHistoryId) {
        String str;
        if (stockHistoryId != null) {
            int num = Integer.parseInt(stockHistoryId);
            num++;
            str = String.format("%010d", num);
        } else {
            str = String.format("%010d", 1);
        }
        return str;
    }

    /**
     * @Param * @param: num
     * @description: 获取受注番号
     * @return: java.lang.String
     * @date: 2020/9/9
     */
    public static String getOrderNo(int num, String identification) {
        String tmp = "SL000";
        if (!StringTools.isNullOrEmpty(identification)) {
            tmp = identification;
        }
        // 受注
        String orderNo = tmp + "-" + new SimpleDateFormat("yyyyMMddHHmmss-").format(new Date())
            + String.format("%05d", num);
        return orderNo;
    }

    public static List<String> checkZipToList(String number) {
        List<String> result = new ArrayList<String>();
        // 置換前, 置換後
        Map<String, String> map = new HashMap<>();
        map.put("〒", "");
        map.put("(", "");
        map.put(")", "");
        map.put("-", "");
        if (!StringTools.isNullOrEmpty(number)) {
            for (String key : map.keySet()) {
                number = number.replace(key, map.get(key));
            }
            result = checkZipNumber(number, "^[0-9]{7}$");// 固定
            if (result != null && result.size() > 0) {
                return result;
            }
        }
        return result;
    }

    public static List<String> checkZipNumber(String number, String type) {// [50]
        Pattern pattern = Pattern.compile(type);
        Matcher matcher = pattern.matcher(number);
        List<String> list = new ArrayList<String>();
        if (matcher.find()) {
            String telStr = matcher.group();
            if (telStr != null) {
                list.add(0, number.substring(0, 3));
                list.add(1, number.substring(3, 7));
            }
        }
        return list;
    }

    public static List<String> checkPhoneToList(String number) {
        // 戻り値
        List<String> result = new ArrayList<String>();
        // 置換前, 置換後
        Map<String, String> map = new HashMap<>();
        map.put("+81", "0");
        map.put("(", "");
        map.put(")", "");
        map.put("-", "");
        map.put(" ", "");
        map.put("　", "");
        if (!StringTools.isNullOrEmpty(number)) {
            for (String key : map.keySet()) {
                number = number.replace(key, map.get(key));
            }
            result = checkTelephoneNumber(number, "^0\\d\\d{4}\\d{4}$");// 固定
            if (result != null && result.size() > 0) {
                return result;
            }
            result = checkTelephoneNumber(number, "^\\(0\\d\\)\\d{4}\\d{4}$");// 固定
            if (result != null && result.size() > 0) {
                return result;
            }
            result = checkTelephoneNumber(number, "^(070|080|090)\\d{4}\\d{4}$");// 携帯
            if (result != null && result.size() > 0) {
                return result;
            }
            result = checkTelephoneNumber(number, "^050\\d{4}\\d{4}$");// 携帯IP
            if (result != null && result.size() > 0) {
                return result;
            }
            result = checkTelephoneNumber(number, "^0120\\d{3}\\d{3}$");// 0120
            if (result != null && result.size() > 0) {
                return result;
            }
            result = checkTelephoneNumber(number, "^[1-9][0-9].*");// 数字
            if (result != null && result.size() > 0) {
                return result;
            }
        }
        return result;
    }

    public static List<String> checkTelephoneNumber(String number, String type) {// [50]
        Pattern pattern = Pattern.compile(type);
        Matcher matcher = pattern.matcher(number);
        List<String> list = new ArrayList<String>();
        if (matcher.find()) {
            String telStr = matcher.group();
            if (telStr != null) {
                int len = telStr.length();
                // 不正データがある場合、０埋め
                if (len <= 8) {
                    number = String.format("%10s", number).replace(" ", "0");
                    len = 10;
                }
                list.add(0, number.substring(0, len - 8));
                list.add(1, number.substring(len - 8).substring(0, 4));
                list.add(2, number.substring(len - 4));
            }
        }
        return list;
    }

    /**
     * @param tw200
     * @param i (处理file后缀名)
     * @return
     * @description: 循环时根据i获取不同的fileName
     * @return: String
     */
    public static String savePath(Tw200_shipment tw200, int i) {
        String name;
        switch (i) {
            case 0:
                name = tw200.getFile();
                break;
            case 1:
                name = tw200.getFile2();
                break;
            case 2:
                name = tw200.getFile3();
                break;
            case 3:
                name = tw200.getFile4();
                break;
            case 4:
                name = tw200.getFile5();
                break;
            default:
                name = null;
        }
        return name;
    }

    /**
     * @Description: //TODO 根据不同功能生成不同的pdfName
     *               @Date： 2021/3/22
     *               @Param： client_id
     *               @Param： function 一览 明细书 作业书 '-ラベル印刷 区分
     *               @Param： different 出库 入库 在库 区分
     *               @Param： content 相关出库 入库 在库 id
     * @return：String
     */
    public static String getPdfName(String client_id, String different, String function, String content) {
        StringBuilder sb = new StringBuilder();
        boolean add = false;
        sb.append(client_id);
        // 仓库侧 出库相关
        if ("shipment".equals(different)) {
            switch (function) {
                // ・同梱明細書
                case "glance":
                    sb.append("-LS0000001-");
                    break;
                // ・作業指示書
                case "glance_work":
                    sb.append("-LW0000001-");
                    break;
                // ・明細・作業指示書 A3
                case "glance_work_detail":
                    sb.append("-LD0000001-A3-");
                    break;
                // ・明細・作業指示書 A4
                case "glance_work_detail_A4":
                    sb.append("-LD0000001-A4-");
                    break;
                // ・トータルピッキングリスト
                case "orderList":
                    sb.append("-LT0000001-");
                    break;
                // 同梱明細書 作業指示書 出荷済み详细页的同梱明細書 生成规则相同
                case "detail":
                    sb.append("-").append(content).append("-");
                    break;
                // ・明細・作業指示書 A3
                case "work_detail":
                    sb.append("-").append(content).append("-A3-");
                    break;
                // ・商品ラベル印刷
                case "lable":
                    sb.append("-").append(content).append("-PL-");
                    break;
                // ・明細・作業指示書 A3
                case "work_detail_A4":
                    sb.append("-").append(content).append("-A4-");
            }
        }
        // 仓库侧 入库相关
        if ("warehousing".equals(different)) {
            // 商品明细
            if ("product".equals(function)) {
                return sb.append("-" + content + ".pdf").toString();
                // 临时条形码文件名
            } else if ("code".equals(function)) {
                return sb.append("-" + content + "-" + System.currentTimeMillis()).toString();
            } else {
                sb.append("-" + content + "-");
            }
        }
        // 在库
        if ("stock".equals(different)) {
            switch (function) {
                // '-ロケーション-ロケラベル
                case "location":
                    sb.append("-LC00000001-");
                    break;
                // '-ロケーション-商品ラベル
                case "product_count":
                    sb.append("-LP00000001-");
                    break;
                // '-ロケーション-商品ラベル(在庫数付け)
                case "product":
                    sb.append("-LPZ0000001-");
                    break;
                // ・商品マスタ-箱ラベルPDF表示 / 商品マスタ-箱ラベルPDF表示
                case "box":
                    sb.append("-PB00000001-");
                    break;
                // ・商品マスタ-商品ラベル印刷
                case "glance_lable":
                    sb.append("-PA00000001-");
                    break;
                // ・商品マスタ-商品ラベル印刷
                case "lable":
                    add = true;
                    sb.append("-" + content + ".pdf");
                    break;
            }
            if (add) {
                return sb.toString();
            }
        }
        return sb.append(System.currentTimeMillis()).append(".pdf").toString();
    }

    /**
     * @param number : 数字
     * @param accordion : 计算方式
     * @description: 根据计算方式 获取结果
     * @return: int
     * @author: wang
     * @date: 2021/4/15 16:07
     */
    public static int calculateTheNumbers(double number, int accordion) {
        int unitPrice = 0;
        switch (accordion) {
            case 0:
                // 切り捨て
                unitPrice = (int) Math.floor(number);
                break;
            case 1:
                // 切り上げ
                unitPrice = (int) Math.ceil(number);
                break;
            case 2:
                // 四捨五入
                unitPrice = (int) Math.round(number);
                break;
            default:
                // 切り捨て(Default値)
                unitPrice = (int) Math.floor(number);
                break;
        }
        return unitPrice;
    }

    /**
     * @param unitPrice : 税抜単価
     * @param reduceRax : 税率
     * @param accordion : 計算方法
     * @description: 税込価格(消費税 + 税抜単価)
     * @return: int
     * @date: 2021/3/31 16:07
     */
    public static int getTaxIncluded(int unitPrice, int reduceRax, int accordion) {
        int currentTax = 0;
        try {
            // 税込= 消費税+単価(税抜)
            double tax = getTax(unitPrice, reduceRax);
            currentTax = calculateTheNumbers(tax, accordion) + unitPrice;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        // 戻り値
        return currentTax;
    }

    /**
     * @param price : 価格
     * @param reduced : 税率
     * @description : 消費税取得
     * @return: double
     * @date: 2021/4/2 16:18
     */
    public static double getTax(double price, int reduced) {
        double tax = price * reduced;
        return tax / (100 + reduced);
    }

    /**
     * @param price : 価格
     * @param reduced : 税率
     * @description : 消費税取得
     * @return: double
     * @date: 2021/4/2 16:18
     */
    public static double getTax(int price, int reduced) {
        double tax = price * reduced;
        return tax / (100 + reduced);
    }

    /**
     * @param number: 電話番号
     * @description : 電話番号を取得
     * @return: double
     * @author: wang
     * @date: 2021/4/16 16:18
     */
    public static String toTelNumber(String number) {
        // 戻り値
        String result = number;
        // 置換前, 置換後
        Map<String, String> map = new HashMap<>();
        map.put("+81", "0");
        map.put("(", "");
        map.put(")", "");
        map.put("-", "");
        map.put(" ", "");
        map.put("　", "");
        map.put("－", "");
        if (!StringTools.isNullOrEmpty(result)) {
            //
            for (String key : map.keySet()) {
                result = result.replace(key, map.get(key));
            }
        }
        return result;
    }

    /**
     * @param times: 配送時間
     * @description : 配送時間の文字を取得
     * @return: String
     * @author: wang
     * @date: 2021/4/16 16:18
     */
    public static String timeToString(String times) {
        String result = "指定なし";
        // 置換前, 置換後
        Map<String, String> map = new HashMap<>();
        map.put("午前", "午前中");
        map.put("AM指定", "午前中");
        map.put("AM", "午前中");
        map.put("0812", "午前中");
        map.put("1214", "12時～14時");
        map.put("1416", "14時～16時");
        map.put("1618", "16時～18時");
        map.put("1820", "18時～20時");
        map.put("1921", "19時～21時");
        map.put("2021", "19時～21時");
        map.put("8～12", "午前中");
        map.put("08～12", "午前中");
        map.put("09～12", "午前中");
        map.put("12～14", "12時～14時");
        map.put("14～16", "14時～16時");
        map.put("16～18", "16時～18時");
        map.put("18～20", "18時～20時");
        map.put("18～21", "19時～21時");
        map.put("19～21", "19時～21時");
        map.put("20～21", "19時～21時");
        // ない場合、「指定なし」とする
        if (!StringTools.isNullOrEmpty(times)) {
            times = times.replace("-", "～");
            times = times.replace("ー", "～");
            times = times.replace("~", "～");
            times = times.replace(":00", "");
            times = times.replace("時", "");
            times = times.replace("中", "");
            for (String key : map.keySet()) {
                times = times.replace(key, map.get(key));
            }
            if (times.indexOf("午前中") != -1) {
                times = "午前中";
            }
            if ("1".equals(times)) {
                times = times.replace("1", "午前中");
            }
            if ("2".equals(times)) {
                times = times.replace("2", "午後");
            }
            if ("0".equals(times)) {
                times = times.replace("0", "指定なし");
            }
            result = times;
        }
        return result;
    }

    /**
     * 文字列切り出し（Byte単位）<br />
     * <br />
     * 先頭から指定バイト数分文字列を切り出す。<br />
     * 切り出し終了部分が日本語の途中にかかる場合は<br />
     * 直前の文字までを切り出す
     *
     * @param str String 切り出し対象文字列
     * @param len Integer 切り出しバイト数
     * @param charset String 文字コード
     * @return String 切り出し後の文字列
     * @Date 2021/6/28
     */
    public static String bytesSubstr(String str, Integer len, String charset) {
        StringBuffer sb = new StringBuffer();
        int cnt = 0;

        try {
            for (int i = 0; i < str.length(); i++) {
                String tmpStr = str.substring(i, i + 1);
                byte[] b = tmpStr.getBytes(charset);
                if (cnt + b.length > len) {
                    return sb.toString();
                } else {
                    sb.append(tmpStr);
                    cnt += b.length;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * @param str
     * @return
     */
    public static HashMap<String, String> sagawaToAdderss(String str, int standard) {
        HashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("add1", "");
        hashMap.put("add2", "");
        hashMap.put("add3", "");

        str = str.replaceAll("\\s*", "").replaceAll("－", "-");

        String add1 = "", add2 = "", tmpStr = "";
        if (!StringTools.isNullOrEmpty(str) && str.getBytes().length > standard) {
            add1 = bytesSubstr(str, standard, "UTF-8");
            hashMap.put("add1", add1);
            tmpStr = str.substring(add1.length());
        } else {
            hashMap.put("add1", str);
        }

        if (!StringTools.isNullOrEmpty(tmpStr) && tmpStr.getBytes().length > standard) {
            add2 = bytesSubstr(tmpStr, standard, "UTF-8");
            hashMap.put("add2", add2);
            tmpStr = tmpStr.substring(add2.length());
        } else {
            hashMap.put("add2", tmpStr);
            tmpStr = "";
        }

        if (!StringTools.isNullOrEmpty(tmpStr) && tmpStr.getBytes().length > 0) {
            hashMap.put("add3", tmpStr);
        }

        return hashMap;
    }

    /**
     * @param str : 需要分割的字符
     * @param norm : 分割的标准
     * @description: 分割字符
     * @return: java.util.HashMap<java.lang.String, java.lang.String>
     * @date: 2021/6/29 20:43
     */
    public static HashMap<String, String> cuttingAddress(String str, int norm) {
        HashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("add1", "");
        hashMap.put("add2", "");
        hashMap.put("add3", "");
        if (StringTools.isNullOrEmpty(str)) {
            return hashMap;
        }
        str = str.replaceAll("\\s*", "").replaceAll("　", "");
        String address = changeAlpHalfToFull(str);
        int length = address.length();
        String add1 = "", add2 = "", add3 = "";
        if (length <= norm) {
            add1 = address;
        } else {
            add1 = address.substring(0, norm);
            // 如果为ヤマト運輸 不需要 add3
            if (norm == 32) {
                add2 = address.substring(norm, length);
            } else {
                if (length <= (norm * 2)) {
                    add2 = address.substring(norm, length);
                } else {
                    add2 = address.substring(norm, norm * 2);
                    add3 = address.substring((norm * 2), length);
                }
            }
        }
        add1 = add1.replaceAll("－", "ー");
        hashMap.put("add1", add1);
        if (!StringTools.isNullOrEmpty(add2)) {
            add2 = add2.replaceAll("－", "ー");
        }
        hashMap.put("add2", add2);
        if (!StringTools.isNullOrEmpty(add3)) {
            add3 = add3.replaceAll("－", "ー");
        }
        hashMap.put("add3", add3);
        return hashMap;
    }

    /**
     * ヤマト運輸
     *
     * @param str
     * @return
     */
    public static HashMap<String, String> yamatoToAdderss(String str) {
        HashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("add1", "");
        hashMap.put("add2", "");

        str = str.replaceAll("\\s*", "").replaceAll("－", "-");

        String add1 = "", tmpStr = "";
        if (!StringTools.isNullOrEmpty(str) && str.getBytes().length > 64) {
            add1 = bytesSubstr(str, 64, "UTF-8");
            hashMap.put("add1", add1);
            tmpStr = str.substring(add1.length());
        } else {
            hashMap.put("add1", str);
            tmpStr = "";
        }

        if (!StringTools.isNullOrEmpty(tmpStr) && tmpStr.getBytes().length > 0) {
            hashMap.put("add2", tmpStr);
        }

        return hashMap;
    }

    /**
     * ヤマト運輸 お届け先住所
     *
     * @param prefecture,yamato_addr1,yamato_addr2
     * @return hashMap
     */
    public static HashMap<String, String> yamatoGetAdderss(String prefecture, String yamato_addr1,
        String yamato_addr2) {
        HashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("add1", "");
        hashMap.put("add2", "");
        String address1 = "";
        String address2 = "";
        String prefecture_tpm = "";
        if (!StringTools.isNullOrEmpty(prefecture)) {
            prefecture_tpm = prefecture.replaceAll("\\s*", "").replaceAll("－", "-");
        }
        if (!StringTools.isNullOrEmpty(yamato_addr1)) {
            address1 = yamato_addr1.replaceAll("\\s*", "").replaceAll("－", "-");
        }
        if (!StringTools.isNullOrEmpty(yamato_addr2)) {
            address2 = yamato_addr2.replaceAll("\\s*", "").replaceAll("－", "-");
        }
        String address_sum = prefecture_tpm + address1;
        int prefecture_length = getLength(prefecture_tpm);
        int addr1_length = getLength(address1);
        int addr2_length = getLength(address2);
        int sum_length = prefecture_length + addr1_length;
        String addr_tmp = "";
        String interregional = "市郡区町村";
        String addr_intercept = address1.substring(address1.length() - 1);
        if (sum_length < 64) {
            if (addr2_length <= 32) {
                // 如果住所1最后一个在【市郡区町村】中
                if (interregional.indexOf(addr_intercept) >= 0) {
                    if (addr1_length <= 24) {
                        hashMap.put("add1", address_sum + address2);
                        hashMap.put("add2", "");
                        return hashMap;
                    } else {
                        hashMap.put("add1", address_sum);
                        hashMap.put("add2", address2);
                    }
                } else {
                    hashMap.put("add1", address_sum);
                    hashMap.put("add2", address2);
                }
            } else {
                if ((sum_length + addr2_length) <= 96) {
                    // 如果住所1最后一个在【市郡区町村】中
                    if (interregional.indexOf(addr_intercept) >= 0) {
                        if (addr1_length <= 24) {
                            addr_tmp = new StringBuffer(cutStr(new StringBuffer(address2).reverse().toString(), 32))
                                .reverse().toString();
                            String addr = address_sum + address2.replace(addr_tmp, "");
                            if (getLength(addr) <= 44) {
                                hashMap.put("add1", addr);
                                hashMap.put("add2", addr_tmp);
                                return hashMap;
                            } else {
                                hashMap.put("add1", cutStr(addr, 44));
                                hashMap.put("add2", addr.replace(cutStr(addr, 44), "") + addr_tmp);
                                return hashMap;
                            }
                        } else {
                            addr_tmp = new StringBuffer(cutStr(new StringBuffer(address2).reverse().toString(), 32))
                                .reverse().toString();
                            hashMap.put("add1", address_sum + address2.replace(addr_tmp, ""));
                            hashMap.put("add2", addr_tmp);
                        }
                    } else {
                        addr_tmp = new StringBuffer(cutStr(new StringBuffer(address2).reverse().toString(), 32))
                            .reverse().toString();
                        hashMap.put("add1", address_sum + address2.replace(addr_tmp, ""));
                        hashMap.put("add2", addr_tmp);
                    }
                } else {
                    addr_tmp = cutStr(address_sum + address2, 64);
                    hashMap.put("add1", addr_tmp);
                    hashMap.put("add2", (address_sum + address2).replace(addr_tmp, ""));
                }
            }
        } else if (sum_length == 64) {
            hashMap.put("add1", address_sum);
            hashMap.put("add2", address2);
        } else {
            addr_tmp = cutStr(address_sum, 64);
            hashMap.put("add1", addr_tmp);
            hashMap.put("add2", address_sum.replace(addr_tmp, "") + address2);
        }
        return hashMap;
    }

    /**
     * <p>
     * [概 要] 半角英字⇒全角英字への変換
     * </p>
     * <p>
     * [詳 細]
     * </p>
     * <p>
     * [備 考]
     * </p>
     *
     * @param str 変換対象文字列
     * @return 変換後文字列
     */
    public static String changeAlpHalfToFull(String str) {
        if (str == null) {
            return str;
        }
        StringBuilder buf = new StringBuilder(str.length() + 1);
        char[] ca = str.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE);
            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
                buf.append((char) (ca[i] + CONVERT_STEP));
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

    public static int checkIntNull(int param) {
        int value = 0;
        if (!StringTools.isNullOrEmpty(param)) {
            value = param;
        }
        return value;
    }

    /**
     * @description: 配送時間帯IDを取得
     * @Param: client_id 配送時間名称
     * @param: deliverys 配送会社名
     * @param: ms007Maps 店舗側設定リスト
     * @return: String 配送時間帯ID
     * @author: wang
     * @date: 2021/6/26
     */
    public static String getDeliveryTimeId(String deliveryTime, String deliveryName,
        Map<String, List<Ms007_setting>> ms007Maps,
        DeliveryDao deliveryDao, String user) {

        // 配送時間帯を取得
        List<Ms006_delivery_time> ms006list = deliveryDao.getDeliveryTimeAllList();

        // 初期化
        String delivery_time_id = "";
        String converted_value = "";
        if (!StringTools.isNullOrEmpty(deliveryTime)) {
            // 删除配送时间带前后的全角和半角空格
            deliveryTime = deliveryTime.replaceAll("^　|　$", "").trim();
        }
        // 時間文字を変換
        converted_value = CommonUtils.timeToString(deliveryTime);
        if (ms007Maps.size() > 0) {
            // 配送連携設定(ms700)から支払方法を取得した場合、処理
            if (ms007Maps.containsKey(deliveryTime)) {
                List<Ms007_setting> ms007Settings = ms007Maps.get(deliveryTime);
                List<Ms007_setting> ms007SettingList =
                    ms007Settings.stream().filter(x -> x.getKubun() == 2).collect(Collectors.toList());
                if (!StringTools.isNullOrEmpty(ms007SettingList) && !ms007SettingList.isEmpty()) {
                    // converted_value = ms007Maps.get(deliveryTime);
                    converted_value = ms007SettingList.get(0).getConverted_value();
                }
            }
        }

        if (!StringTools.isNullOrEmpty(converted_value)) {
            deliveryTime = converted_value;
        }

        // 支払方法設定(ms014)から支払方法を取得した場合、処理
        for (Ms006_delivery_time ms006 : ms006list) {
            if (deliveryName.equals(ms006.getDelivery_nm()) && deliveryTime.equals(ms006.getDelivery_time_name())) {
                delivery_time_id = ms006.getDelivery_time_id().toString();
                break;
            }
        }

        if (StringTools.isNullOrEmpty(delivery_time_id)) {
            // 支払方法を追加
            Ms006_delivery_time ms006 = new Ms006_delivery_time();
            ms006.setKubu(99999);
            ms006.setDelivery_nm(deliveryName);
            ms006.setDelivery_time_name(deliveryTime);
            ms006.setInfo("API連携により、新規追加");
            ms006.setIns_usr(user);
            ms006.setDelivery_time_csv(deliveryTime);
            // 新期登録
            deliveryDao.insertMs006DeliveryTime(ms006);

            List<Ms006_delivery_time> ms006DeliveryTimes = deliveryDao.getDeliveryTimeName(deliveryTime, deliveryName);
            if (ms006DeliveryTimes != null && ms006DeliveryTimes.size() > 0) {
                delivery_time_id = ms006DeliveryTimes.get(0).getDelivery_time_id().toString();
                // ms007Maps.put(deliveryTime, delivery_time_id);
            }
        }
        return delivery_time_id;
    }

    /**
     * 获取随机数
     *
     * @return
     * @date 2021/8/24 21:48
     */
    public static int getRandomNum(int max) {
        // 1 - 100000000
        int min = 1;
        long randomNum = System.currentTimeMillis();
        return (int) (randomNum % (max - min) + min);
    }

    /**
     * @param path : 地址
     * @param multipartFile : 文件
     * @description: 上传文件
     * @return: void
     * @date: 2021/9/23 10:15
     */
    public static void uploadFile(String path, MultipartFile multipartFile) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
            byte[] bytes = new byte[1024];
            File file = new File(path);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            fileOutputStream = new FileOutputStream(path);
            int length = 0;
            while ((length = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, length);
            }
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (!StringTools.isNullOrEmpty(fileOutputStream)) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!StringTools.isNullOrEmpty(inputStream)) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param s,length : 字符串，截取长度
     * @description: 按字节截取字符串
     * @return: String
     * @date: 2021/10/22
     */
    public static String cutStr(String s, int length) {
        try {
            byte[] bytes = s.getBytes("Unicode");
            int n = 0; // 表示当前的字节数
            int i = 2; // 要截取的字节数，从第3个字节开始
            for (; i < bytes.length && n < length; i++) {
                // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
                if (i % 2 == 1) {
                    n++; // 在UCS2第二个字节时n加1
                } else {
                    // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                    if (bytes[i] != 0) {
                        n++;
                    }
                }
            }
            // 如果i为奇数时，处理成偶数
            if (i % 2 == 1) {
                // 该UCS2字符是汉字时，去掉这个截一半的汉字
                if (bytes[i - 1] != 0) {
                    i = i - 1;
                } else {
                    // 该UCS2字符是字母或数字，则保留该字符
                    i = i + 1;
                }
            }
            return new String(bytes, 0, i, "Unicode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * @param s : 字符串
     * @description: 按字节计算字符串长度
     * @return: int
     * @date: 2021/10/22
     */
    public static int getLength(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255) {
                length++;
            } else {
                length += 2;
            }
        }
        return length;
    }
}
