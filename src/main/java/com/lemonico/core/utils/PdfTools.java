package com.lemonico.core.utils;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.primitives.Ints;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.lemonico.common.bean.*;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.DottedLine.BottomDottedLine;
import com.lemonico.core.utils.DottedLine.LeftDottedLine;
import com.lemonico.core.utils.DottedLine.RightDottedLine;
import com.lemonico.core.utils.DottedLine.TopDottedLine;
import com.lemonico.core.utils.constants.Constants;
import java.io.*;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @className: PdfTools
 * @description: TODO類記述
 * @date: 2020/05/28 14:04
 **/
public class PdfTools
{

    // private static final String FONT_PATH = "~/font/YuGothR.ttc,1";
    private static final String FONT_PATH = System.getProperty("user.dir") + "/font/YuGothR.ttc,1";
    // 最大幅
    private static final int MAX_WIDTH = 520;
    // フォント
    private static Font titlefont;
    private static Font headfont;
    private static Font headBoldfont;
    private static Font smallfont;
    private static Font keyfont;
    private static Font textfont;
    private static Font textBoldFont;
    private static Font textUnderlineFont;
    private static Font textUnderlineFont12;
    private static Font smalltextUnderlineFont10;
    private static Font smalltextUnderlineFont8;
    private static Font smalltextfont;
    private static Font midFont;
    private static Font midBlodFont;
    private static Font littleBoldFont;
    private static Font overSize;
    private static Font bigSize;
    private static Font superSize;
    private static Font overBigSize;
    private static Font titleFont20;
    private static Font smallBoldFont;

    static {
        try {
            BaseFont baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            baseFont.setSubset(true);
            titleFont20 = new Font(baseFont, 20, Font.NORMAL);
            titlefont = new Font(baseFont, 16, Font.NORMAL);
            headfont = new Font(baseFont, 14, Font.NORMAL);
            headBoldfont = new Font(baseFont, 14, Font.BOLD);
            smallfont = new Font(baseFont, 12, Font.NORMAL);
            keyfont = new Font(baseFont, 10, Font.NORMAL);
            textfont = new Font(baseFont, 10, Font.NORMAL);
            textBoldFont = new Font(baseFont, 10, Font.BOLD);
            textUnderlineFont = new Font(baseFont, 10, Font.UNDERLINE);
            textUnderlineFont12 = new Font(baseFont, 12, Font.UNDERLINE);
            smalltextfont = new Font(baseFont, 8, Font.NORMAL);
            smalltextUnderlineFont8 = new Font(baseFont, 8, Font.UNDERLINE);
            smalltextUnderlineFont10 = new Font(baseFont, 10, Font.UNDERLINE);
            midFont = new Font(baseFont, 30, Font.NORMAL);
            midBlodFont = new Font(baseFont, 30, Font.BOLD);
            overSize = new Font(baseFont, 50, Font.NORMAL);
            bigSize = new Font(baseFont, 80, Font.NORMAL);
            superSize = new Font(baseFont, 140, Font.NORMAL);
            overBigSize = new Font(baseFont, 160, Font.NORMAL);
            smallBoldFont = new Font(baseFont, 12, Font.BOLD);
            littleBoldFont = new Font(baseFont, 20, Font.BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void format(Paragraph p) {
        // 文字の位置は真ん中、0 左 1 真ん中 2 右
        p.setAlignment(1);
        // 左インデントをセットする
        p.setIndentationLeft(12);
        // 右インデントをセットする
        p.setIndentationRight(12);
        // 行首インデントをセットする
        p.setFirstLineIndent(24);
        // 行間
        p.setLeading(20f);
        // 段落上に空白を設ける
        p.setSpacingBefore(5f);
        // 段落下に空白を設ける
        p.setSpacingAfter(10f);

    }

    // 表を作り、列幅を指定する
    public static PdfPTable createTable(float[] widths) {
        PdfPTable table = new PdfPTable(widths);
        try {
            table.setTotalWidth(MAX_WIDTH);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            // 枠線幅
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    /**
     * @param widths : 各个行款
     * @param width ： 总宽度
     * @description: 生成表格
     * @return: com.itextpdf.text.pdf.PdfPTable
     * @date: 2020/12/28 10:50
     */
    public static PdfPTable createTable(float[] widths, int width) {
        PdfPTable table = new PdfPTable(widths);
        try {
            table.setTotalWidth(width);
            table.setLockedWidth(true);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            // 枠線幅
            table.getDefaultCell().setBorder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    // セルを作り、フォントを指定する
    public static PdfPCell createCell(String value, Font font, int align, int border) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setBorder(border);
        cell.setPaddingLeft(35f);
        return cell;
    }

    public static PdfPCell createCell(String value, Font font, int align, int border, float paddingLeft) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setBorder(border);
        cell.setPaddingLeft(paddingLeft);
        return cell;
    }

    // セルに画像を挿入する
    public static PdfPCell createCell(Image image, Font font, int align, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(rowspan * 25f);
        cell.setImage(image);
        cell.setBorder(0);
        cell.setPaddingLeft(20f);
        return cell;
    }

    public static PdfPCell createCell(Image image, int align, int colspan, int rowspan, int border) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(rowspan * 40f);
        cell.setImage(image);
        cell.setBorder(border);
        cell.setPaddingLeft(20f);
        return cell;
    }

    public static PdfPCell createCell(Image image, Font font, int align, int colspan, int rowspan, int border) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(rowspan * 10f);
        cell.setImage(image);
        cell.setBorder(0);
        cell.setPaddingLeft(20f);
        return cell;
    }

    public static PdfPCell createCell(String value, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    public static PdfPCell createCell(String value, Font font, String element) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    // 设置行高
    public static PdfPCell createCell(String text, Font font, int align, int colspan, int rowspan, float height) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(height);
        cell.setPhrase(new Phrase(text, font));
        return cell;
    }

    public static PdfPCell createCell(String text, Font font, int align, int colspan, int rowspan, float height,
        int border) {
        PdfPCell cell = new PdfPCell();
        cell.setPaddingLeft(25f);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(height);
        cell.setBorder(border);
        cell.setPhrase(new Phrase(text, font));
        return cell;
    }

    public static PdfPCell createCell(String text, Font font, int align, int colspan, int rowspan, float height,
        int border, Integer paddingLeft) {
        PdfPCell cell = new PdfPCell();
        cell.setPaddingLeft(25f);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(height);
        cell.setBorder(border);
        cell.setPhrase(new Phrase(text, font));
        cell.setPaddingLeft(paddingLeft);
        return cell;
    }

    // 隐藏边框线
    public static PdfPCell createCell(String value, Font font, int disable) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        // 4 隐藏左边框线 8 隐藏右边框线
        cell.disableBorderSide(disable);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    public static PdfPCell createCell(String value, Font font, int align, int border, int status) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setBorder(border);
        cell.setPaddingLeft(10f);
        return cell;
    }

    /**
     * @param image ： 图片
     * @param align ： 对齐
     * @param colspan ： 合并
     * @param rowspan ： 行跨
     * @param border ： 边框
     * @param paddingLeft ： 向左填充
     * @description: 納品書PDF 图片格式
     * @return: com.itextpdf.text.pdf.PdfPCell
     * @date: 2020/12/28 10:48
     */
    public static PdfPCell createShipmentImgCell(Image image, int align, int colspan, int rowspan, int border,
        float paddingLeft) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(rowspan * 30f);
        cell.setImage(image);
        cell.setBorder(border);
        cell.setPaddingLeft(paddingLeft);
        cell.setPhrase(new Phrase("", titlefont));
        return cell;
    }

    /**
     * @param image : 图片
     * @param align ： 水平对齐的方式
     * @param rowspan ： 行跨
     * @param disableBorder ： 隐藏哪条边框线
     * @param paddingLeft ： 左偏移
     * @description: 图片隐藏具体边框线
     * @return: com.itextpdf.text.pdf.PdfPCell
     * @date: 2021/1/18 10:52
     */
    public static PdfPCell createShipmentImgCell(Image image, int align, int rowspan, int disableBorder,
        float paddingLeft) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setHorizontalAlignment(align);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(rowspan * 30f);
        cell.setImage(image);
        cell.disableBorderSide(disableBorder);
        cell.setPaddingLeft(paddingLeft);
        return cell;
    }

    /**
     * @param text ： 内容
     * @param font ： 字体
     * @param align : 对齐
     * @param colspan : 合并
     * @param rowspan : 行跨
     * @param height : 高度
     * @param border ： 边框
     * @param paddingLeft :向左填充
     * @description: 生成出库纳品书单元格
     * @return: com.itextpdf.text.pdf.PdfPCell
     * @date: 2020/12/28 14:54
     */
    public static PdfPCell createShipmentCell(String text, Font font, int align, int colspan, int rowspan, float height,
        int border, float paddingLeft) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(align);
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        if (height != 0) {
            cell.setFixedHeight(height);
        }
        if (border != 0 && border != 1) {
            cell.disableBorderSide(border);
        } else {
            cell.setBorder(border);
        }
        cell.setPhrase(new Phrase(text, font));
        cell.setPaddingLeft(paddingLeft);
        return cell;
    }

    /**
     * @param value : 文本
     * @param font ： 字体
     * @param disableBorder ： 隐藏哪条边框线
     * @description: 生成隐藏边框线的单元格
     * @return: com.itextpdf.text.pdf.PdfPCell
     * @date: 2020/12/28 15:41
     */
    public static PdfPCell createShipmentCell(String value, Font font, int disableBorder) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPhrase(new Phrase(value, font));
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        return cell;
    }

    public static PdfPCell createShipmentCell(String value, Font font, int disableBorder, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        return cell;
    }

    public static PdfPCell createShipmentCell(String value, Font font, float height, int disableBorder, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setFixedHeight(height);
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        return cell;
    }

    /**
     * @param value : value
     * @param font ： 字体
     * @param disableBorder ： 隐藏边框线
     * @param align ： 水平对齐
     * @param topDottedLine ： 上虚线
     * @param bottomDottedLine ： 下虚线
     * @param leftDottedLine ： 左虚线
     * @param rightDottedLine ： 右虚线
     * @description: 虚化边框线
     * @return: com.itextpdf.text.pdf.PdfPCell
     * @date: 2021/1/18 13:22
     */
    public static PdfPCell createShipmentCell(String value, Font font, int disableBorder, int align,
        TopDottedLine topDottedLine,
        BottomDottedLine bottomDottedLine, LeftDottedLine leftDottedLine, RightDottedLine rightDottedLine) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        if (!StringTools.isNullOrEmpty(bottomDottedLine)) {
            cell.setCellEvent(bottomDottedLine);
        }
        if (!StringTools.isNullOrEmpty(topDottedLine)) {
            cell.setCellEvent(topDottedLine);
        }
        if (!StringTools.isNullOrEmpty(leftDottedLine)) {
            cell.setCellEvent(leftDottedLine);
        }
        if (!StringTools.isNullOrEmpty(rightDottedLine)) {
            cell.setCellEvent(rightDottedLine);
        }
        return cell;
    }

    /**
     * @param value ： 文本
     * @param font ： 字体
     * @param disableBorder ： 隐藏哪条边框线
     * @param flg ： 加粗哪条边框线
     * @param borderWidth ： 加粗的值
     * @param height ： 高度
     * @description: 生成单元格
     * @return: com.itextpdf.text.pdf.PdfPCell
     * @date: 2020/12/28 15:43
     */
    public static PdfPCell createShipmentCell(String value, Font font, int disableBorder, int flg, float borderWidth,
        float height) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setFixedHeight(height);
        cell.setPhrase(new Phrase(value, font));
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        switch (flg) {
            case 1:
                // 上
                cell.setBorderWidthTop(borderWidth);
                break;
            case 2:
                // 右
                cell.setBorderWidthRight(borderWidth);
                break;
            case 3:
                // 左
                cell.setBorderWidthLeft(borderWidth);
                break;
            case 4:
                // 下
                cell.setBorderWidthBottom(borderWidth);
                break;
            default:
                break;
        }
        return cell;
    }

    public static PdfPCell createShipmentCell(String value, Font font, int disableBorder, int flg, float borderWidth,
        float height, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(align);
        cell.setHorizontalAlignment(align);
        cell.setFixedHeight(height);
        cell.setPhrase(new Phrase(value, font));
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        switch (flg) {
            case 1:
                // 上
                cell.setBorderWidthTop(borderWidth);
                break;
            case 2:
                // 右
                cell.setBorderWidthRight(borderWidth);
                break;
            case 3:
                // 左
                cell.setBorderWidthLeft(borderWidth);
                break;
            case 4:
                // 下
                cell.setBorderWidthBottom(borderWidth);
                break;
            default:
                break;
        }
        return cell;
    }

    /**
     * @param value : 文本
     * @param font ： 字体
     * @param align ： 对齐
     * @param disableBorder ： 选择隐藏哪条边框
     * @param paddingLeft ： 左偏移距离
     * @description: 生成单元格
     * @return: com.itextpdf.text.pdf.PdfPCell
     * @date: 2020/12/28 17:44
     */
    public static PdfPCell createShipmentCell(String value, Font font, int align, int disableBorder,
        float paddingLeft) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(align);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        cell.setPaddingLeft(paddingLeft);
        return cell;
    }

    public static PdfPCell createShipmentCell2(String value, Font font, int align, int disableBorder, float paddingLeft,
        float paddingTop) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(align);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        cell.setPaddingLeft(paddingLeft);
        cell.setPaddingTop(paddingTop);
        return cell;
    }

    public static PdfPCell createShipmentCell(String value, Font font, int align, int disableBorder, int rowspan,
        int height, float paddingLeft, int flg, float borderWidth) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(align);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setRowspan(rowspan);
        cell.setFixedHeight(height);
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        cell.setPaddingLeft(paddingLeft);
        switch (flg) {
            case 1:
                // 上
                cell.setBorderWidthTop(borderWidth);
                break;
            case 2:
                // 右
                cell.setBorderWidthRight(borderWidth);
                break;
            case 3:
                // 左
                cell.setBorderWidthLeft(borderWidth);
                break;
            case 4:
                // 下
                cell.setBorderWidthBottom(borderWidth);
                break;
            case 5:
                // 上下
                cell.setBorderWidthTop(borderWidth);
                cell.setBorderWidthBottom(borderWidth);
            default:
                break;
        }
        return cell;
    }

    /**
     * @param value : 数据
     * @param font : 字体
     * @param align : 对齐
     * @param disableBorder : 选择禁用哪条边框
     * @param rowspan : 行跨
     * @param height : 高度
     * @param paddingLeft : 向左填充
     * @param flg : 加粗哪条边框线
     * @param borderWidth : 边框线加粗
     * @param leading : 行间距
     * @description: 添加了行间距的设置
     * @return: com.itextpdf.text.pdf.PdfPCell
     * @date: 2021/4/19 17:19
     */
    public static PdfPCell createShipmentCell(String value, Font font, int align, int disableBorder, int rowspan,
        int height, float paddingLeft, int flg, float borderWidth,
        float leading) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(align);
        cell.setHorizontalAlignment(align);
        cell.setPhrase(new Phrase(value, font));
        cell.setLeading(leading, 1f);
        cell.setRowspan(rowspan);
        cell.setFixedHeight(height);
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        cell.setPaddingLeft(paddingLeft);
        switch (flg) {
            case 1:
                // 上
                cell.setBorderWidthTop(borderWidth);
                break;
            case 2:
                // 右
                cell.setBorderWidthRight(borderWidth);
                break;
            case 3:
                // 左
                cell.setBorderWidthLeft(borderWidth);
                break;
            case 4:
                // 下
                cell.setBorderWidthBottom(borderWidth);
                break;
            case 5:
                // 上下
                cell.setBorderWidthTop(borderWidth);
                cell.setBorderWidthBottom(borderWidth);
            default:
                break;
        }
        return cell;
    }

    public static PdfPCell createShipmentCell(String value, Font font, int disableBorder, int flg, float borderWidth,
        float height, int vertical, int horizontal) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(vertical);
        cell.setHorizontalAlignment(horizontal);
        cell.setFixedHeight(height);
        cell.setPhrase(new Phrase(value, font));
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        switch (flg) {
            case 1:
                // 上
                cell.setBorderWidthTop(borderWidth);
                break;
            case 2:
                // 右
                cell.setBorderWidthRight(borderWidth);
                break;
            case 3:
                // 左
                cell.setBorderWidthLeft(borderWidth);
                break;
            case 4:
                // 下
                cell.setBorderWidthBottom(borderWidth);
                break;
            default:
                break;
        }
        return cell;
    }

    public static PdfPCell createShipmentCell(String value, Font font, int disableBorder, int flg, float borderWidth,
        float height, int vertical, int horizontal, int rowSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(vertical);
        cell.setHorizontalAlignment(horizontal);
        cell.setFixedHeight(height);
        cell.setPhrase(new Phrase(value, font));
        cell.setRowspan(rowSpan);
        if (disableBorder != 0) {
            cell.disableBorderSide(disableBorder);
        }
        switch (flg) {
            case 1:
                // 上
                cell.setBorderWidthTop(borderWidth);
                break;
            case 2:
                // 右
                cell.setBorderWidthRight(borderWidth);
                break;
            case 3:
                // 左
                cell.setBorderWidthLeft(borderWidth);
                break;
            case 4:
                // 下
                cell.setBorderWidthBottom(borderWidth);
                break;
            default:
                break;
        }
        return cell;
    }

    /**
     * @Param * @param: client_id : 用户ID
     * @param: shipment_plan_id ： 出库依赖ID
     * @description: 生成PDF地址和条形码地址
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/01
     */
    public static JSONObject creatPath(String client_id, String shipment_plan_id, String rootPath, String storePath) {
        String codePath = rootPath + storePath + DateUtils.getDateMonth() + "/code/" + shipment_plan_id;
        String pdfName = client_id + "-" + shipment_plan_id + "-" + System.currentTimeMillis() + ".pdf";
        String webPath = storePath + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = rootPath + webPath;
        BarcodeUtils.generateCode128Barcode(shipment_plan_id, codePath, Constants.WIDTH_3);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("codePath", codePath);
        jsonObject.put("pdfPath", pdfPath);
        jsonObject.put("webPath", webPath);
        return jsonObject;
    }

    /**
     * 入庫ラベル 兼 入庫明細書
     *
     * @param productList 入庫详细信息
     * @param mw400Warehouse 仓库信息
     * @param ms201Client 店铺信息
     * @param clientId 店铺Id
     * @param warehousingId 入库依赖Id
     * @param barcodePath 条形码路径
     * @param pdfPath pdf路径
     * @since 1.0.0
     */
    public static void createWarehousingPDF(List<Tc101_warehousing_plan_detail> productList,
        Mw400_warehouse mw400Warehouse,
        Ms201_client ms201Client, String clientId, String warehousingId,
        String barcodePath, String pdfPath) throws Exception {

        // PDFファイルを新規作成する
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        Document doc = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(doc, Files.newOutputStream(file.toPath()));
        TableFooter footer = new TableFooter();
        writer.setPageEvent(footer);
        doc.open();

        // 做虚线
        LeftDottedLine leftDottedLine = new LeftDottedLine();
        // 上虚线
        TopDottedLine topDottedLine = new TopDottedLine();
        // 下虚线
        BottomDottedLine bottomDottedLine = new BottomDottedLine();
        // title
        PdfPTable table = createTable(new float[] {
            320, 220
        }, 540);
        PdfPCell cell = createCell(" ", titleFont20, Element.ALIGN_LEFT, 0);
        cell.setPaddingTop(-25f);
        cell.setPaddingLeft(-3f);
        table.addCell(cell);
        Image image = Image.getInstance(barcodePath);
        image.setAlignment(Image.ALIGN_CENTER);
        image.scalePercent(40);
        PdfPCell shipmentImgCell = createShipmentImgCell(image, 0, 1, 1, 0, 20f);
        shipmentImgCell.setPaddingTop(-15f);
        shipmentImgCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(shipmentImgCell);
        PdfPCell cell1 = createCell("入庫明細書", titleFont20, Element.ALIGN_CENTER, 0);
        cell1.setPaddingRight(-160f);
        cell1.setPaddingTop(-60f);
        table.addCell(cell1);
        table.addCell(createCell(" ", titlefont, Element.ALIGN_LEFT, 0));
        // 店铺信息 和 仓库信息
        PdfPTable table1 = createTable(new float[] {
            300, 330
        }, 580);
        table1.addCell(createCell("(入庫先) ", textfont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell("店舗ID：" + judgmentNull(clientId), textfont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell("〒" + judgmentNull(mw400Warehouse.getZip()), textfont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell("店舗名：" + ms201Client.getClient_nm(), textfont, Element.ALIGN_LEFT, 0));
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringTools.isNullOrEmpty(mw400Warehouse.getTodoufuken())) {
            stringBuilder.append(mw400Warehouse.getTodoufuken());
        }
        if (!StringTools.isNullOrEmpty(mw400Warehouse.getAddress1())) {
            stringBuilder.append(mw400Warehouse.getAddress1());
        }
        if (!StringTools.isNullOrEmpty(mw400Warehouse.getAddress2())) {
            stringBuilder.append(mw400Warehouse.getAddress2());
        }

        table1.addCell(createCell(stringBuilder.toString(), textfont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell("〒" + judgmentNull(ms201Client.getZip()), textfont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell(mw400Warehouse.getWarehouse_nm(), textfont, Element.ALIGN_LEFT, 0));
        stringBuilder.delete(0, stringBuilder.length());
        if (!StringTools.isNullOrEmpty(ms201Client.getTdfk())) {
            stringBuilder.append(ms201Client.getTdfk());
        }
        if (!StringTools.isNullOrEmpty(ms201Client.getAdd1())) {
            stringBuilder.append(ms201Client.getAdd1());
        }
        if (!StringTools.isNullOrEmpty(ms201Client.getAdd2())) {
            stringBuilder.append(ms201Client.getAdd2());
        }
        table1.addCell(createCell(stringBuilder.toString(), textfont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell("TEL:" + mw400Warehouse.getTel(), textfont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell("ご担当者：" + judgmentNull(ms201Client.getTnnm()), textfont, Element.ALIGN_LEFT, 0));
        // 出库数据
        PdfPTable table2 = createTable(new float[] {
            80, 140, 270, 50
        }, 540);
        table2.setHeaderRows(1);
        table2.addCell(createShipmentCell("商品ID", keyfont, 12, 1, 1.5f, 30f));
        table2.addCell(createShipmentCell("商品コード／管理バーコード", keyfont, 8, 1, 1.5f, 30f));
        table2.addCell(createShipmentCell("商品名", keyfont, 8, 1, 1.5f, 30f));
        table2.addCell(createShipmentCell("数量", keyfont, 8, 1, 1.5f, 30f));
        for (Tc101_warehousing_plan_detail warehousingPlanDetail : productList) {
            table2.addCell(
                createShipmentCell(warehousingPlanDetail.getProduct_id(), smalltextfont, 13, Element.ALIGN_LEFT));
            String value = "";
            String code = warehousingPlanDetail.getMc100_productList().getCode();
            value = !StringTools.isNullOrEmpty(code) ? (code + "\n") : "  \n";
            String barcode = warehousingPlanDetail.getMc100_productList().getBarcode();
            value += !StringTools.isNullOrEmpty(barcode) ? barcode : " ";
            table2.addCell(createShipmentCell(value, smalltextfont, 9, Element.ALIGN_LEFT));
            table2.addCell(createShipmentCell(warehousingPlanDetail.getMc100_productList().getName(), smalltextfont, 9,
                Element.ALIGN_LEFT));
            table2.addCell(createShipmentCell(String.valueOf(warehousingPlanDetail.getQuantity()), smalltextfont, 9,
                Element.ALIGN_RIGHT));

        }
        if (productList.size() < 10) {
            for (int j = 0; j < (10 - productList.size()); j++) {
                table2.addCell(createShipmentCell(" \n" + " ", smalltextfont, 13, Element.ALIGN_LEFT));
                table2.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                table2.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                table2.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
            }
        }
        doc.add(table);
        doc.add(table1);
        doc.add(table2);
        doc.newPage();
        PdfPTable table3 = createTable(new float[] {
            580
        }, 580);
        PdfPCell cell2 = createCell("入庫依頼番号：" + warehousingId, textfont, Element.ALIGN_RIGHT, 0);
        cell2.setPaddingTop(-35f);
        table3.addCell(cell2);
        table3.addCell(createCell("入庫箱ラベル", keyfont, Element.ALIGN_LEFT, 0));
        table3.addCell(createCell("1. 入庫ラベルには、今回入庫依頼をいただいた、入庫依頼番号が記載されています。", smalltextfont, Element.ALIGN_LEFT, 0));
        PdfPTable table4 = createTable(new float[] {
            260, 260
        }, 520);
        table4.addCell(
            createShipmentCell("入庫ラベル（上面）", textfont, 15, Element.ALIGN_CENTER, topDottedLine, null, null, null));
        table4.addCell(createShipmentCell("入庫ラベル（側面）", textfont, 15, Element.ALIGN_CENTER, topDottedLine, null,
            leftDottedLine, null));
        table4.addCell(createShipmentCell("※配送箱の上面に貼付けてください。", textfont, 15, Element.ALIGN_CENTER));
        table4.addCell(createShipmentCell("※配送箱の側面に貼付けてください。", textfont, 15, Element.ALIGN_CENTER, null, null,
            leftDottedLine, null));
        PdfPCell imgCell = createShipmentImgCell(image, Element.ALIGN_CENTER, 2, 15, 0);
        imgCell.setCellEvent(bottomDottedLine);
        table4.addCell(imgCell);
        imgCell.setCellEvent(leftDottedLine);
        table4.addCell(imgCell);
        Paragraph paragraph = new Paragraph("仕分けラベル", keyfont);
        Paragraph paragraph1 =
            new Paragraph("1. 仕分けラベルには、今回入庫依頼をいただいた、店舗ID - 商品ID・商品コード・管理バーコード・商品名・数量が記載されています。", smalltextfont);
        Paragraph paragraph2 = new Paragraph("2. 商品ごとにOPP袋などで小分けにしていただき、ラベルを袋の中に同梱または袋に貼付します。", smalltextfont);
        Paragraph paragraph3 = new Paragraph("3. 仕分けが複数になる場合、仕分けラベルはコピーして数量を訂正して利用します。", smalltextfont);
        PdfPTable table5 = createTable(new float[] {
            260, 260
        }, 520);
        table5.setSpacingBefore(4f);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < productList.size(); i++) {
            Tc101_warehousing_plan_detail planDetail = productList.get(i);
            builder.append(warehousingId).append("\n").append(judgmentNull(planDetail.getMc100_productList().getCode()))
                .append("\n")
                .append(judgmentNull(planDetail.getMc100_productList().getBarcode())).append("\n")
                .append(planDetail.getMc100_productList().getName())
                .append("\n").append("\n")
                .append("                                                                        (")
                .append(planDetail.getQuantity()).append(")");
            PdfPCell shipmentCell = createShipmentCell(builder.toString(), textfont, 15, Element.ALIGN_LEFT);
            shipmentCell.setPaddingLeft(5f);
            shipmentCell.setCellEvent(topDottedLine);
            if ((i % 2) == 0) {
                if ((productList.size() - i) < 3) {
                    // 左右
                    shipmentCell.setCellEvent(topDottedLine);
                    shipmentCell.setCellEvent(bottomDottedLine);
                }
            } else {
                shipmentCell.setCellEvent(leftDottedLine);
                if ((productList.size() - i) == 1) {
                    // 右
                    shipmentCell.setCellEvent(topDottedLine);
                    shipmentCell.setCellEvent(bottomDottedLine);
                }
            }
            table5.addCell(shipmentCell);
            builder.delete(0, builder.length());
            if (productList.size() % 2 != 0 && (productList.size() - i) == 1) {
                table5.addCell(createShipmentCell(" ", textfont, 15, Element.ALIGN_LEFT,
                    topDottedLine, bottomDottedLine, leftDottedLine, null));
            }
        }
        doc.add(table3);
        doc.add(table4);
        doc.add(paragraph);
        doc.add(paragraph1);
        doc.add(paragraph2);
        doc.add(paragraph3);
        doc.add(table5);
        doc.close();
        writer.close();
    }

    /**
     * @param jsonParam : 数据
     * @param codePath ： 条形码地址
     * @param pdfPath ： pdf地址
     * @description: 納品書 兼 同梱明細書(領収書) （新版）
     * @return: void
     * @date: 2020/12/29 12:31
     */
    public static void createNewShipmentPricePdf(JSONObject jsonParam, String codePath, String pdfPath)
        throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        TableFooter footer = new TableFooter();
        writer.setPageEvent(footer);
        doc.open();
        PdfPTable table = createTable(new float[] {
            130, 260, 190
        }, 580);

        String logo = jsonParam.getString("detail_logo");
        try {
            Image image = Image.getInstance(logo);
            image.setAlignment(Image.ALIGN_CENTER);
            image.scalePercent(40);
            PdfPCell shipmentImgCell = createShipmentImgCell(image, 0, 1, 2, 0, 20f);
            table.addCell(shipmentImgCell);
        } catch (Exception e) {
            table.addCell(createShipmentCell(" ", titlefont, Element.ALIGN_MIDDLE, 15, 30f));
        }
        PdfPCell cell1 = createCell("        納品書      ", titlefont, Element.ALIGN_LEFT, 0);
        cell1.setPaddingLeft(75f);
        cell1.setPaddingTop(-30f);
        table.addCell(cell1);
        Image picture = Image.getInstance(codePath);
        picture.setAlignment(Image.ALIGN_CENTER);
        picture.scalePercent(40);
        PdfPCell shipmentImgCell = createShipmentImgCell(picture, 0, 1, 2, 0, 0f);
        table.addCell(shipmentImgCell);
        PdfPTable table1 = createTable(new float[] {
            300, 330
        }, 580);
        table1.setSpacingAfter(5f);
        String company = jsonParam.getString("company");
        String surname = jsonParam.getString("surname");
        String division = jsonParam.getString("division");
        // 部署null判断
        if (StringTools.isNullOrEmpty(division)) {
            division = " ";
        }
        // 拼接部署
        if (!StringTools.isNullOrEmpty(company)) {
            company += " " + division;
        } else {
            company = division;
        }
        if (StringTools.isNullOrEmpty(surname) && !StringTools.isNullOrEmpty(company)) {
            company += "  御中";
        }
        if (!StringTools.isNullOrEmpty(surname)) {
            surname += " 様";
        }
        table1.addCell(createCell(company, smallBoldFont, Element.ALIGN_LEFT, 0));
        String orderNo = jsonParam.getString("order_no");
        if (StringTools.isNullOrEmpty(orderNo)) {
            orderNo = jsonParam.getString("shipment_plan_id");
        }
        if (StringTools.isNullOrEmpty(orderNo)) {
            orderNo = " ";
        }
        table1.addCell(createCell("注文日付：" + isNullOrEmpty(jsonParam.getString("order_datetime")), smalltextfont,
            Element.ALIGN_LEFT, 0));
        table1.addCell(createCell(judgmentNull(surname), smallBoldFont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell("注文番号：" + orderNo, smalltextfont, Element.ALIGN_LEFT, 0));
        PdfPTable table2 = createTable(new float[] {
            300, 425
        }, 520);
        if (!StringTools.isNullOrEmpty(jsonParam.getString("gift_sender_name"))) {
            table2.addCell(createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 2, 25, 1, 5f));
            table2.addCell(createShipmentCell("贈 り 主 ：" + isNullOrEmpty(jsonParam.getString("gift_sender_name")),
                smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
        } else {
            table2.addCell(createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 1, 5f));
        }
        table2.addCell(createShipmentCell(isNullOrEmpty(jsonParam.getString("name")), textfont, Element.ALIGN_LEFT, 0,
            1, 15, 0, 65f));
        table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
        table2.addCell(createShipmentCell("〒" + isNullOrEmpty(jsonParam.getString("postcode")), textfont,
            Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
        table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
        String add = "";
        if (!StringTools.isNullOrEmpty(jsonParam.getString("prefecture"))) {
            add += jsonParam.getString("prefecture")
                + " ";
        }
        if (!StringTools.isNullOrEmpty(jsonParam.getString("address1"))) {
            add += jsonParam.getString("address1");
        }
        table2.addCell(createShipmentCell(add, textfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
        table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
        table2.addCell(createShipmentCell(judgmentNull(jsonParam.getString("address2")), textfont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 65f));
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(0);
        cell.setVerticalAlignment(0);
        cell.setColspan(0);
        cell.setRowspan(1);
        cell.setFixedHeight(20);
        String string = jsonParam.getString("total_amount");
        String param = "合計金額" + "    " + "¥" + formatrPrict(jsonParam.getString("total_amount"));
        if (string.length() > 7) {
            param += "(税込)";
        } else {
            param += "            (税込)";
        }
        cell.setPhrase(new Phrase(param, smallfont));
        cell.setPaddingLeft(10f);
        cell.disableBorderSide(13);
        String contact = jsonParam.getString("contact");
        if (!"0".equals(contact)) {
            // 电话
            String substring2 = contact.substring(0, 1);
            String sponsorPhone = ("1".equals(substring2)) ? "TEL : " + jsonParam.getString("sponsorPhone") : "";
            // fax
            String substring1 = contact.substring(1, 2);
            String sponsorFax = ("1".equals(substring1)) ? "FAX : " + jsonParam.getString("sponsorFax") : "";
            // 邮箱
            String substring = contact.substring(2, 3);
            String sponsorEmail = ("1".equals(substring)) ? "Mail : " + jsonParam.getString("sponsorEmail") : "";
            List<String> list = Stream.of(sponsorPhone, sponsorFax, sponsorEmail).filter(String -> !String.isEmpty())
                .collect(Collectors.toList());
            if (list.size() == 0) {
                table2.addCell(cell);
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            } else {
                table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                table2.addCell(createShipmentCell("購入内容に関するお問い合わせ先", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                int count = 0;
                for (String value : list) {
                    if (count == list.size() - 1) {
                        table2.addCell(cell);
                    } else {
                        table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    }
                    table2.addCell(createShipmentCell(value, textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    count++;
                }
            }
        } else {
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            table2.addCell(cell);
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
        }
        doc.add(table);
        doc.add(table1);
        doc.add(table2);
        PdfPTable table3 = createTable(new float[] {
            80, 200, 40, 80, 90, 30
        }, 520);
        table3.setSpacingBefore(2f);
        table3.setHeaderRows(1);
        table3.setSplitRows(false);
        table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
        table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
        table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
        String productTax = jsonParam.getString("product_tax");
        String value = "";
        if (!StringTools.isNullOrEmpty(productTax)) {
            value = "(" + productTax + ")";
        }
        table3.addCell(createShipmentCell("単価 " + value, textfont, 8, 1, 1.5f, 30f));
        table3.addCell(createShipmentCell("金額 (税込)", textfont, 8, 1, 1.5f, 30f));
        table3.addCell(createShipmentCell("税率", textfont, 8, 1, 1.5f, 30f));
        JSONArray items = jsonParam.getJSONArray("items");
        int size = items.size();
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemsJSONObject = items.getJSONObject(i);
            // 同捆物
            if (itemsJSONObject.getInteger("kubun") == Constants.BUNDLED) {
                continue;
            }
            String is_reduced_tax = itemsJSONObject.getString("is_reduced_tax");
            String isReducedTax = "10%";
            String mark = "";
            if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                    : "10%";
                if ("1".equals(is_reduced_tax)) {
                    mark = "※ ";
                }
            }
            String taxFlag = itemsJSONObject.getString("tax_flag");
            if ("3".equals(taxFlag) || "非課税".equals(taxFlag)) {
                isReducedTax = "0%";
                mark = "";
            }
            table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")), smalltextfont, 20f, 13,
                Element.ALIGN_LEFT));
            String name = isNullOrEmpty(itemsJSONObject.getString("name"));
            size += (int) Math.ceil((double) name.length() / (double) 24) - 1;
            table3.addCell(createShipmentCell(mark + isNullOrEmpty(itemsJSONObject.getString("name")), smalltextfont, 9,
                Element.ALIGN_LEFT));
            table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                smalltextfont, 9, Element.ALIGN_RIGHT));
            table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("unit_price")),
                smalltextfont, 9, Element.ALIGN_RIGHT));
            table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("price")), smalltextfont, 9,
                Element.ALIGN_RIGHT));
            table3.addCell(createShipmentCell(isReducedTax, smalltextfont, 9));
        }
        if (size < 8) {
            for (int i = 0; i < 9 - size; i++) {
                table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
            }
        }
        if (size > 33 && size < 44) {
            for (int i = 0; i < 45 - size; i++) {
                table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
            }
        }
        if (size > 44) {
            int num = size - 43;
            if (num > 66 && num < 77) {
                for (int i = 0; i < 78 - num; i++) {
                    table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                }
            }
        }
        table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
        table3.addCell(createShipmentCell("　※は軽減税率対象", smalltextfont, 9, Element.ALIGN_LEFT));
        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
        doc.add(table3);
        PdfPTable table4 = createTable(new float[] {
            320, 80, 90, 30
        }, 520);
        table4.setKeepTogether(true);
        String payment_method = jsonParam.getString("payment_method");
        if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
            table4.addCell(createShipmentCell(judgmentNull(jsonParam.getString("detail_message")), smalltextfont,
                Element.ALIGN_LEFT, 13, 10, 220, 0f, 4, 1f, 5f));
        } else {
            table4.addCell(createShipmentCell(judgmentNull(jsonParam.getString("detail_message")), smalltextfont,
                Element.ALIGN_LEFT, 13, 9, 195, 0f, 4, 1f, 5f));
        }
        table4.addCell(
            createShipmentCell("商品合計 (税込)", textfont, 13, 1, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
        table4.addCell(createShipmentCell("¥" + formatrPrict(jsonParam.getString("subtotal_amount")), smalltextfont, 9,
            1, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
        table4.addCell(createShipmentCell(" ", smalltextfont, 14, 1, 1f, 25f));

        table4
            .addCell(createShipmentCell("送料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
        String deliveryCharge = jsonParam.getString("delivery_charge");
        if (StringTools.isNullOrEmpty(deliveryCharge)) {
            deliveryCharge = "0";
        }
        table4.addCell(createShipmentCell("¥" + formatrPrict(deliveryCharge), smalltextfont, 9, 0, 0f, 25f,
            Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
        table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

        table4.addCell(
            createShipmentCell("手数料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
        String handlingCharge = jsonParam.getString("handling_charge");
        if (StringTools.isNullOrEmpty(handlingCharge)) {
            handlingCharge = "0";
        }
        table4.addCell(createShipmentCell("¥" + formatrPrict(handlingCharge), smalltextfont, 9, 0, 0f, 25f,
            Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
        table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

        table4
            .addCell(createShipmentCell("割引額", textfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT, 1));

        String discount_amount = jsonParam.getString("discount_amount");
        if (StringTools.isNullOrEmpty(discount_amount)) {
            discount_amount = "0";
        }
        table4.addCell(createShipmentCell("¥" + formatrPrict(discount_amount), smalltextfont, 9, 0, 0f, 15f,
            Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT, 2));

        table4.addCell(
            createShipmentCell(" ", smalltextfont, 15, 0, 0f, 15f, Element.ALIGN_BOTTOM, Element.ALIGN_RIGHT, 1));

        PdfPCell cell2 = createShipmentCell("(ポイント、クーポン含む) ", smalltextfont, 13, 0, 1f, 10f, Element.ALIGN_MIDDLE,
            Element.ALIGN_LEFT, 1);
        cell2.setPaddingTop(-5);
        table4.addCell(cell2);

        table4.addCell(
            createShipmentCell(" ", smalltextfont, 15, 0, 0f, 10f, Element.ALIGN_BOTTOM, Element.ALIGN_RIGHT, 1));

        table4
            .addCell(createShipmentCell("合計 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
        table4.addCell(createShipmentCell("¥" + formatrPrict(jsonParam.getString("total_amount")), smalltextfont, 9, 0,
            1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
        table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));

        // 代金引換
        if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
            table4.addCell(
                createShipmentCell("支払方法", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4.addCell(createShipmentCell(isNullOrEmpty(jsonParam.getString("payment_method_name")), smalltextfont,
                9, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
            table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));

            table4.addCell(
                createShipmentCell("支払総計 (税込)", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4.addCell(createShipmentCell("¥" + formatrPrict(jsonParam.getString("total_for_cash_on_delivery")),
                smalltextfont, 9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
            table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
        } else {
            table4.addCell(
                createShipmentCell("支払方法", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4.addCell(createShipmentCell(isNullOrEmpty(jsonParam.getString("payment_method_name")), smalltextfont,
                9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
            table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
        }
        table4.addCell(createShipmentCell("10%対象   " + "¥" + formatrPrict(jsonParam.getString("total_with_normal_tax")),
            smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
        table4
            .addCell(createShipmentCell("うち消費税  " + "¥" + formatrPrict(jsonParam.getString("totalWithNormalTaxPrice")),
                smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
        table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_LEFT));

        table4
            .addCell(createShipmentCell(" 8%対象   " + "¥" + formatrPrict(jsonParam.getString("total_with_reduced_tax")),
                smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
        table4
            .addCell(createShipmentCell("うち消費税  " + "¥" + formatrPrict(jsonParam.getString("totalWithReducedTaxPrice")),
                smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
        table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_LEFT));

        doc.add(table4);
        doc.close();
        footer.onCloseDocument(writer, doc);
    }

    /**
     * @param jsonParam ：数据
     * @param codePath ： 条形码路径
     * @param pdfPath ： pdf生成路径
     * @description: 納品明細書 (新版)
     * @return: void
     * @date: 2020/12/29 9:16
     */
    public static void createNewShipmentsPDF(JSONObject jsonParam, String codePath, String pdfPath) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        TableFooter footer = new TableFooter();
        writer.setPageEvent(footer);
        doc.open();
        PdfPTable table = createTable(new float[] {
            150, 230, 190
        }, 580);
        String logo = jsonParam.getString("detail_logo");
        try {
            Image image = Image.getInstance(logo);
            image.setAlignment(Image.ALIGN_CENTER);
            image.scalePercent(40);
            PdfPCell shipmentImgCell = createShipmentImgCell(image, 0, 1, 2, 0, 20f);
            table.addCell(shipmentImgCell);
        } catch (Exception e) {
            table.addCell(createShipmentCell(" ", titlefont, Element.ALIGN_MIDDLE, 15, 30f));
        }
        PdfPCell shipmentCell = createShipmentCell("  納品書", titlefont, Element.ALIGN_MIDDLE, 15, 90f);
        shipmentCell.setPaddingTop(-30f);
        table.addCell(shipmentCell);
        Image picture = Image.getInstance(codePath);
        picture.setAlignment(Image.ALIGN_CENTER);
        picture.scalePercent(40);
        PdfPCell shipmentImgCell = createShipmentImgCell(picture, 0, 1, 2, 0, 0f);
        table.addCell(shipmentImgCell);
        // table.addCell(createShipmentImgCell(picture, 0, 1, 2, 0, 0f));
        PdfPTable table1 = createTable(new float[] {
            300, 280
        }, 580);
        table1.setSpacingAfter(5f);
        String company = jsonParam.getString("company");
        String surname = jsonParam.getString("surname");
        String division = jsonParam.getString("division");
        // 部署null判断
        if (StringTools.isNullOrEmpty(division)) {
            division = " ";
        }
        // 拼接部署
        if (!StringTools.isNullOrEmpty(company)) {
            company += " " + division;
        } else {
            company = division;
        }
        if (StringTools.isNullOrEmpty(surname) && !StringTools.isNullOrEmpty(company)) {
            company += "  御中";
        }
        if (!StringTools.isNullOrEmpty(surname)) {
            surname += " 様";
        }
        table1.addCell(createCell(company, smallBoldFont, Element.ALIGN_LEFT, 0));
        String orderNo = jsonParam.getString("order_no");
        if (StringTools.isNullOrEmpty(orderNo)) {
            orderNo = jsonParam.getString("shipment_plan_id");
        }
        table1.addCell(createCell("注文日付：" + isNullOrEmpty(jsonParam.getString("order_datetime")), textfont,
            Element.ALIGN_LEFT, 0));
        table1.addCell(createCell(judgmentNull(surname), smallBoldFont, Element.ALIGN_LEFT, 0));
        table1.addCell(createCell("注文番号：" + orderNo, textfont, Element.ALIGN_LEFT, 0));
        PdfPTable table2 = createTable(new float[] {
            240, 280,
        }, 520);

        if (!StringTools.isNullOrEmpty(jsonParam.getString("gift_sender_name"))) {
            table2.addCell(createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 2, 30, 1, 5f));
            table2.addCell(createShipmentCell("贈 り 主 ：" + isNullOrEmpty(jsonParam.getString("gift_sender_name")),
                textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
        } else {
            table2.addCell(createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 1, 5f));
        }
        table2.addCell(createShipmentCell(isNullOrEmpty(jsonParam.getString("name")), textfont, Element.ALIGN_LEFT, 0,
            1, 15, 0, 65f));
        table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
        table2.addCell(createShipmentCell("〒" + isNullOrEmpty(jsonParam.getString("postcode")), textfont,
            Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));

        String bundledText = "";
        if (!"同梱する".equals(jsonParam.getString("delivery_note_type"))) {
            bundledText = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
        }
        table2.addCell(createShipmentCell(bundledText, littleBoldFont, Element.ALIGN_LEFT, 0, 2, 40, 0, 20f));

        String add = "";
        if (!StringTools.isNullOrEmpty(jsonParam.getString("prefecture"))) {
            add += jsonParam.getString("prefecture")
                + " ";
        }
        if (!StringTools.isNullOrEmpty(jsonParam.getString("address1"))) {
            add += jsonParam.getString("address1");
        }
        table2.addCell(createShipmentCell(add, textfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
        table2.addCell(createShipmentCell(judgmentNull(jsonParam.getString("address2")), textfont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 65f));
        String contact = jsonParam.getString("contact");
        if (!contact.equals("0")) {
            table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 10f));
            table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 10f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 10f));
            table2.addCell(createShipmentCell("購入内容に関するお問い合わせ先", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            // 电话
            String substring2 = contact.substring(0, 1);
            String sponsorPhone = (substring2.equals("1")) ? "TEL : " + jsonParam.getString("sponsorPhone") : "";
            // fax
            String substring1 = contact.substring(1, 2);
            String sponsorFax = (substring1.equals("1")) ? "FAX : " + jsonParam.getString("sponsorFax") : "";
            // 邮箱
            String substring = contact.substring(2, 3);
            String sponsorEmail = (substring.equals("1")) ? "Mail : " + jsonParam.getString("sponsorEmail") : "";
            // 集合中去掉空的字符串
            List<String> list = Stream.of(sponsorPhone, sponsorFax, sponsorEmail).filter(String -> !String.isEmpty())
                .collect(Collectors.toList());

            for (String value : list) {
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 10f));
                table2.addCell(createShipmentCell(value, textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            }
        } else {
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 10f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 10f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 10f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 10f));
        }
        PdfPTable table3 = createTable(new float[] {
            100, 380, 40
        }, 520);
        table3.setHeaderRows(1);
        table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
        table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
        table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
        JSONArray items = jsonParam.getJSONArray("items");
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemsJSONObject = items.getJSONObject(i);
            // 同捆物
            if (itemsJSONObject.getInteger("kubun") == Constants.BUNDLED) {
                continue;
            }
            table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")), smalltextfont, 20f, 13,
                Element.ALIGN_LEFT));
            table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("name")), smalltextfont, 9,
                Element.ALIGN_LEFT));
            table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                smalltextfont, 9, Element.ALIGN_RIGHT));
        }
        if (items.size() < 10) {
            for (int i = 0; i < (10 - items.size()); i++) {
                table3.addCell(createShipmentCell(" ", smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
            }
        }
        PdfPTable table4 = createTable(new float[] {
            520
        }, 520);
        table4.setKeepTogether(true);
        PdfPCell detail_message = createShipmentCell(judgmentNull(jsonParam.getString("detail_message")), smalltextfont,
            12, Element.ALIGN_LEFT);
        detail_message.setBorderWidthTop(1f);
        detail_message.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        detail_message.setVerticalAlignment(Element.ALIGN_LEFT);
        detail_message.setBorderWidthBottom(1.5f);
        table4.addCell(detail_message);
        doc.add(table);
        doc.add(table1);
        doc.add(table2);
        doc.add(table3);
        if (!StringTools.isNullOrEmpty(jsonParam.getString("detail_message"))) {
            doc.add(table4);
        }
        doc.close();
        footer.onCloseDocument(writer, doc);
    }

    /**
     * @param jsonObject : 数据
     * @param pdfPath ： pdf地址
     * @description: 同梱明細書PDF仓库侧working页面 （新版）
     * @return: void
     * @date: 2020/12/29 13:44
     */
    public static void createNewShipmentsPriceWorking(JSONObject jsonObject, String pdfPath) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        for (int l = 0; l < jsonArray.size(); l++) {
            JSONObject jsonParam = jsonArray.getJSONObject(l);
            Integer price_on_delivery_note = jsonParam.getInteger("price_on_delivery_note");
            PdfPTable table = createTable(new float[] {
                130, 260, 190
            }, 580);

            String logo = jsonParam.getString("detail_logo");
            try {
                Image image = Image.getInstance(logo);
                image.setAlignment(Image.ALIGN_CENTER);
                image.scalePercent(40);
                PdfPCell shipmentImgCell = createShipmentImgCell(image, 0, 1, 2, 0, 20f);
                table.addCell(shipmentImgCell);
            } catch (Exception e) {
                table.addCell(createShipmentCell(" ", titlefont, Element.ALIGN_MIDDLE, 15, 30f));
            }
            PdfPCell cell1 = createCell("                  納品書", titlefont, Element.ALIGN_LEFT, 0);
            cell1.setPaddingTop(-30);
            table.addCell(cell1);
            Image picture = Image.getInstance(jsonParam.getString("codePath"));
            picture.setAlignment(Image.ALIGN_CENTER);
            picture.scalePercent(40);
            PdfPCell shipmentImgCell = createShipmentImgCell(picture, 0, 1, 2, 0, 0f);
            table.addCell(shipmentImgCell);
            PdfPTable table1 = createTable(new float[] {
                300, 330
            }, 580);
            table1.setSpacingAfter(5f);
            String company = jsonParam.getString("company");
            String surname = jsonParam.getString("surname");
            String division = jsonParam.getString("division");
            // 部署null判断
            if (StringTools.isNullOrEmpty(division)) {
                division = " ";
            }
            // 拼接部署
            if (!StringTools.isNullOrEmpty(company)) {
                company += " " + division;
            } else {
                company = division;
            }
            if (StringTools.isNullOrEmpty(surname) && !StringTools.isNullOrEmpty(company)) {
                company += "  御中";
            }
            if (!StringTools.isNullOrEmpty(surname)) {
                surname += " 様";
            }
            table1.addCell(createCell(judgmentNull(company), smallBoldFont, Element.ALIGN_LEFT, 0));
            String orderNo = jsonParam.getString("order_no");
            if (StringTools.isNullOrEmpty(orderNo)) {
                orderNo = jsonParam.getString("shipment_plan_id");
            }
            table1.addCell(createCell("注文日付：" + isNullOrEmpty(jsonParam.getString("order_datetime")), smalltextfont,
                Element.ALIGN_LEFT, 0));
            table1.addCell(createCell(judgmentNull(surname), smallBoldFont, Element.ALIGN_LEFT, 0));
            table1.addCell(createCell("注文番号：" + orderNo, smalltextfont, Element.ALIGN_LEFT, 0));
            PdfPTable table2 = createTable(new float[] {
                240, 338
            }, 520);

            if (!StringTools.isNullOrEmpty(jsonParam.getString("gift_sender_name"))) {
                table2
                    .addCell(createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 2, 30, 1, 5f));
                table2.addCell(createShipmentCell("贈 り 主 ：" + isNullOrEmpty(jsonParam.getString("gift_sender_name")),
                    smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            } else {
                table2
                    .addCell(createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 1, 5f));
            }
            table2.addCell(createShipmentCell(isNullOrEmpty(jsonParam.getString("name")), textfont, Element.ALIGN_LEFT,
                0, 1, 15, 0, 65f));
            table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
            table2.addCell(createShipmentCell("〒" + isNullOrEmpty(jsonParam.getString("postcode")), textfont,
                Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));

            String noteType = "";
            if ("同梱しない".equals(jsonParam.getString("delivery_note_type"))) {
                noteType = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
            }
            table2.addCell(createShipmentCell(noteType, littleBoldFont, Element.ALIGN_LEFT, 0, 2, 30, 0, 20f));
            // table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
            String add = "";
            if (!StringTools.isNullOrEmpty(jsonParam.getString("prefecture"))) {
                add += jsonParam.getString("prefecture")
                    + " ";
            }
            if (!StringTools.isNullOrEmpty(jsonParam.getString("address1"))) {
                add += jsonParam.getString("address1");
            }
            table2.addCell(createShipmentCell(add, textfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
            // table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
            table2.addCell(createShipmentCell(judgmentNull(jsonParam.getString("address2")), textfont,
                Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
            PdfPCell cell = new PdfPCell();
            cell.setHorizontalAlignment(0);
            cell.setVerticalAlignment(0);
            cell.setColspan(0);
            cell.setRowspan(1);
            cell.setFixedHeight(20);
            String string = jsonParam.getString("total_amount");
            String param = "合計金額" + "    " + "¥" + formatrPrict(jsonParam.getString("total_amount"));
            if (string.length() > 7) {
                param += "       (税込)";
            } else {
                param += "            (税込)";
            }
            cell.setPhrase(new Phrase(param, smallfont));
            cell.setPaddingLeft(10f);
            cell.disableBorderSide(13);
            String contact = jsonParam.getString("contact");
            if (!contact.equals("0")) {
                // 电话
                String substring2 = contact.substring(0, 1);
                String sponsorPhone = (substring2.equals("1")) ? "TEL : " + jsonParam.getString("sponsorPhone") : "";
                // fax
                String substring1 = contact.substring(1, 2);
                String sponsorFax = (substring1.equals("1")) ? "FAX : " + jsonParam.getString("sponsorFax") : "";
                // 邮箱
                String substring = contact.substring(2, 3);
                String sponsorEmail = (substring.equals("1")) ? "Mail : " + jsonParam.getString("sponsorEmail") : "";
                // 集合中去掉空的字符串
                List<String> list = Stream.of(sponsorPhone, sponsorFax, sponsorEmail)
                    .filter(String -> !String.isEmpty()).collect(Collectors.toList());
                if (list.size() == 0) {
                    if (price_on_delivery_note == 1) {
                        table2.addCell(cell);
                        table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    }
                } else {
                    table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                    table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    table2
                        .addCell(createShipmentCell("購入内容に関するお問い合わせ先", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    int count = 0;
                    for (String value : list) {
                        if (count == list.size() - 1) {
                            if (price_on_delivery_note == 1) {
                                table2.addCell(cell);
                            } else {
                                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                            }
                        } else {
                            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        }
                        table2.addCell(createShipmentCell(value, textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        count++;
                    }
                }
            } else {
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                if (price_on_delivery_note == 1) {
                    table2.addCell(cell);
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                }
            }
            doc.add(table);
            doc.add(table1);
            doc.add(table2);
            if (price_on_delivery_note == 1) {
                PdfPTable table3 = createTable(new float[] {
                    80, 200, 40, 80, 90, 30
                }, 520);
                table3.setSpacingBefore(2f);
                table3.setHeaderRows(1);
                table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
                table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
                table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                String productTax = jsonParam.getString("product_tax");
                String value = "";
                if (!StringTools.isNullOrEmpty(productTax)) {
                    value = "(" + productTax + ")";
                }
                table3.addCell(createShipmentCell("単価" + value, textfont, 8, 1, 1.5f, 30f));
                table3.addCell(createShipmentCell("金額 (税込)", textfont, 8, 1, 1.5f, 30f));
                table3.addCell(createShipmentCell("税率", textfont, 8, 1, 1.5f, 30f));
                JSONArray items = jsonParam.getJSONArray("items");
                int size = items.size();
                for (int i = 0; i < items.size(); i++) {
                    JSONObject itemsJSONObject = items.getJSONObject(i);
                    // 同捆物
                    if (itemsJSONObject.getInteger("kubun") == Constants.BUNDLED) {
                        continue;
                    }
                    table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")), smalltextfont,
                        20f, 13, Element.ALIGN_LEFT));
                    String is_reduced_tax = itemsJSONObject.getString("is_reduced_tax");
                    String isReducedTax = "10%";
                    String mark = "";
                    if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                        isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                            : "10%";
                        if ("1".equals(is_reduced_tax)) {
                            mark = "※ ";
                        }
                    }
                    String taxFlag = itemsJSONObject.getString("tax_flag");
                    if ("3".equals(taxFlag) || "非課税".equals(taxFlag)) {
                        isReducedTax = "0%";
                        mark = "";
                    }

                    String name = isNullOrEmpty(itemsJSONObject.getString("name"));
                    size += (int) Math.ceil((double) name.length() / (double) 24) - 1;
                    table3.addCell(createShipmentCell(mark + name, smalltextfont, 9, Element.ALIGN_LEFT));
                    table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                        smalltextfont, 9, Element.ALIGN_RIGHT));
                    table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("unit_price")),
                        smalltextfont, 9, Element.ALIGN_RIGHT));
                    table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("price")),
                        smalltextfont, 9, Element.ALIGN_RIGHT));
                    if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                        isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                            : "10%";
                    }
                    String taxFlag1 = itemsJSONObject.getString("tax_flag");
                    if ("3".equals(taxFlag1) || "非課税".equals(taxFlag1)) {
                        isReducedTax = "0%";
                    }
                    table3.addCell(createShipmentCell(isReducedTax, smalltextfont, 9));
                }
                if (size < 8) {
                    for (int i = 0; i < 10 - size; i++) {
                        table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    }
                }
                if (size > 33 && size < 45) {
                    for (int i = 0; i < 46 - size; i++) {
                        table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    }
                }
                if (size > 45) {
                    int num = size - 43;
                    if (num > 66 && num < 78) {
                        for (int i = 0; i < 79 - num; i++) {
                            table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        }
                    }
                }
                table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                table3.addCell(createShipmentCell("　※は軽減税率対象", smalltextfont, 9, Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                PdfPTable table4 = createTable(new float[] {
                    320, 80, 90, 30
                }, 520);
                table4.setKeepTogether(true);
                String payment_method = jsonParam.getString("payment_method");
                if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
                    table4.addCell(createShipmentCell(judgmentNull(jsonParam.getString("detail_message")),
                        smalltextfont, Element.ALIGN_LEFT, 13, 10, 220, 0f, 4, 1f, 5f));
                } else {
                    table4.addCell(createShipmentCell(judgmentNull(jsonParam.getString("detail_message")),
                        smalltextfont, Element.ALIGN_LEFT, 13, 9, 195, 0f, 4, 1f, 5f));
                }
                table4.addCell(createShipmentCell("商品合計 (税込)", textfont, 13, 1, 1f, 25f, Element.ALIGN_MIDDLE,
                    Element.ALIGN_LEFT));
                table4.addCell(createShipmentCell("¥" + formatrPrict(jsonParam.getString("subtotal_amount")),
                    smalltextfont, 9, 1, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                table4.addCell(createShipmentCell(" ", smalltextfont, 14, 1, 1f, 25f));
                table4.addCell(
                    createShipmentCell("送料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                String deliveryCharge = jsonParam.getString("delivery_charge");
                if (StringTools.isNullOrEmpty(deliveryCharge)) {
                    deliveryCharge = "0";
                }
                table4.addCell(createShipmentCell("¥" + formatrPrict(deliveryCharge), smalltextfont, 9, 0, 0f, 25f,
                    Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

                table4.addCell(
                    createShipmentCell("手数料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                String handlingCharge = jsonParam.getString("handling_charge");
                if (StringTools.isNullOrEmpty(handlingCharge)) {
                    handlingCharge = "0";
                }
                table4.addCell(createShipmentCell("¥" + formatrPrict(handlingCharge), smalltextfont, 9, 0, 0f, 25f,
                    Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));


                table4.addCell(
                    createShipmentCell("割引額", textfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT, 1));

                String discount_amount = jsonParam.getString("discount_amount");
                if (StringTools.isNullOrEmpty(discount_amount)) {
                    discount_amount = "0";
                }
                table4.addCell(createShipmentCell("¥" + formatrPrict(discount_amount), smalltextfont, 9, 0, 0f, 15f,
                    Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT, 2));

                table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 0f, 15f, Element.ALIGN_BOTTOM,
                    Element.ALIGN_RIGHT, 1));

                PdfPCell cell2 = createShipmentCell("(ポイント、クーポン含む) ", smalltextfont, 13, 0, 1f, 10f,
                    Element.ALIGN_MIDDLE, Element.ALIGN_LEFT, 1);
                cell2.setPaddingTop(-5);
                table4.addCell(cell2);

                table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 0f, 10f, Element.ALIGN_BOTTOM,
                    Element.ALIGN_RIGHT, 1));


                table4.addCell(
                    createShipmentCell("合計 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                table4.addCell(createShipmentCell("¥" + formatrPrict(jsonParam.getString("total_amount")),
                    smalltextfont, 9, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));

                if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
                    table4.addCell(
                        createShipmentCell("支払方法", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell(isNullOrEmpty(jsonParam.getString("payment_method_name")),
                        smalltextfont, 9, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));

                    table4.addCell(createShipmentCell("支払総計 (税込)", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    table4.addCell(
                        createShipmentCell("¥" + formatrPrict(jsonParam.getString("total_for_cash_on_delivery")),
                            smalltextfont, 9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
                } else {
                    table4.addCell(
                        createShipmentCell("支払方法", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell(isNullOrEmpty(jsonParam.getString("payment_method_name")),
                        smalltextfont, 9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));

                }
                table4.addCell(
                    createShipmentCell("10%対象   " + "¥" + formatrPrict(jsonParam.getString("total_with_normal_tax")),
                        smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                table4.addCell(
                    createShipmentCell("うち消費税  " + "¥" + formatrPrict(jsonParam.getString("totalWithNormalTaxPrice")),
                        smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_LEFT));

                table4.addCell(
                    createShipmentCell(" 8%対象   " + "¥" + formatrPrict(jsonParam.getString("total_with_reduced_tax")),
                        smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                table4.addCell(
                    createShipmentCell("うち消費税  " + "¥" + formatrPrict(jsonParam.getString("totalWithReducedTaxPrice")),
                        smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_LEFT));

                doc.add(table3);
                doc.add(table4);
            } else {
                PdfPTable table3 = createTable(new float[] {
                    100, 360, 60
                }, 520);
                table3.setHeaderRows(1);
                table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
                table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
                table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                JSONArray items = jsonParam.getJSONArray("items");
                for (int i = 0; i < items.size(); i++) {
                    JSONObject itemsJSONObject = items.getJSONObject(i);
                    table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")), smalltextfont,
                        20f, 13, Element.ALIGN_LEFT));
                    table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("name")), smalltextfont,
                        9, Element.ALIGN_LEFT));
                    table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                        smalltextfont, 9, Element.ALIGN_RIGHT));
                }
                if (items.size() < 10) {
                    for (int i = 0; i < (10 - items.size()); i++) {
                        table3.addCell(createShipmentCell(" ", smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    }
                }
                PdfPTable table4 = createTable(new float[] {
                    520
                }, 520);
                table4.setKeepTogether(true);
                PdfPCell detail_message = createShipmentCell(judgmentNull(jsonParam.getString("detail_message")),
                    smalltextfont, 12, Element.ALIGN_LEFT);
                detail_message.setBorderWidthTop(1f);
                detail_message.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                detail_message.setVerticalAlignment(Element.ALIGN_LEFT);
                detail_message.setBorderWidthBottom(1.5f);
                table4.addCell(detail_message);
                doc.add(table3);
                if (!StringTools.isNullOrEmpty(jsonParam.getString("detail_message"))) {
                    doc.add(table4);
                }
            }
            doc.newPage();
        }
        doc.close();

    }

    /**
     * @description: 箱ラベル印刷
     * @return: void
     * @date: 2020/06/23
     */
    public static void itemsBoxLabelPDF(JSONArray jsonObject, String pdfPath) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        for (int i = 0; i < jsonObject.size(); i++) {
            JSONObject json = jsonObject.getJSONObject(i);
            Paragraph paragraph = new Paragraph(isNullOrEmpty(json.getString("client_id")), bigSize);
            paragraph.setAlignment(1);
            Paragraph blankRow1 = new Paragraph(16f, " ", titlefont);
            Paragraph p1 = new Paragraph();
            p1.add(new Chunk(new LineSeparator()));
            Paragraph blankRow2 = new Paragraph(16f, " ", titlefont);
            Paragraph p2 = new Paragraph(isNullOrEmpty(json.getString("num")), superSize);
            p2.setAlignment(1);
            Paragraph blankRow3 = new Paragraph(150f, " ", overSize);
            Image image = Image.getInstance(json.getString("codePath"));
            image.setAlignment(Image.ANCHOR);
            image.scalePercent(100);
            image.setAbsolutePosition(100, 150);
            PdfPTable table = createTable(new float[] {
                200, 360
            });
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setHorizontalAlignment(1);
            cell.setColspan(1);
            cell.setRowspan(1);
            cell.setImage(image);
            cell.setBorder(0);
            cell.setPaddingLeft(20f);
            table.addCell(cell);
            PdfPCell cell1 = new PdfPCell();
            cell1.setVerticalAlignment(Element.ALIGN_LEFT);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setPhrase(new Phrase(isNullOrEmpty(json.getString("name")), titlefont));
            cell1.setBorder(0);
            cell1.setPaddingLeft(35f);
            cell1.setFixedHeight(10f);
            table.addCell(cell1);
            doc.add(paragraph);
            doc.add(blankRow1);
            doc.add(p1);
            doc.add(blankRow2);
            doc.add(p2);
            doc.add(blankRow3);
            doc.add(table);
            doc.newPage();
        }
        doc.close();
    }

    // a仓库侧入库PDF生成
    public static void createWarehouseInfoPDF(JSONObject jsonObject, String codePath, String pdfPath) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        PdfPTable table = createTable(new float[] {
            80, 480
        });
        Image image = Image.getInstance(codePath);
        image.scalePercent(40);
        image.setAbsolutePosition(40, 60);
        table.addCell(image);
        PdfPCell pdfPCell = new PdfPCell(new Paragraph("入庫確認帳票", titlefont));
        pdfPCell.setPaddingLeft(150f);
        pdfPCell.setBorder(0);
        table.addCell(pdfPCell);
        PdfPTable p1 = createTable(new float[] {
            120, 280, 40, 40, 40, 40
        });
        p1.addCell(createCell("商品ID / BARCODE", keyfont));
        p1.addCell(createCell("商品名(商品コード)", keyfont));
        p1.addCell(createCell("サイズ", keyfont));
        p1.addCell(createCell("重量(g)", keyfont));
        p1.addCell(createCell("依頼数", keyfont));
        p1.addCell(createCell("実績数", keyfont));
        JSONArray item = jsonObject.getJSONArray("item");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < item.size(); i++) {
            JSONObject object = item.getJSONObject(i);
            p1.addCell(createCell(isNullOrEmpty(object.getString("product_id")), keyfont, 1, 1, 1, 40f));
            stringBuilder.delete(0, stringBuilder.length());

            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setHorizontalAlignment(0);
            cell.setColspan(1);
            cell.setRowspan(1);
            cell.setFixedHeight(40f);
            cell.setPhrase(new Phrase(isNullOrEmpty(object.getString("name")), keyfont));
            p1.addCell(cell);

            p1.addCell(createCell(isNullOrEmpty(object.getString("sizeName")), keyfont, 1, 1, 2, 50f));
            p1.addCell(createCell(isNullOrEmpty(object.getString("weight")), keyfont, 1, 1, 2, 50f));
            p1.addCell(createCell(isNullOrEmpty(object.getString("quantity")), keyfont, 1, 1, 2, 50f));
            p1.addCell(createCell(" ", keyfont, 1, 1, 2, 50f));
            p1.addCell(createCell(isNullOrEmpty(object.getString("barcode")), keyfont, 1, 1, 2, 10f));
            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append("口ケ:")
                .append(isNullOrEmpty(object.getString("wh_location_nm")));
            p1.addCell(createCell(stringBuilder.toString(), keyfont, 1, 1, 2, 10f));
            p1.addCell(createCell("", keyfont, 8));
            p1.addCell(createCell("", keyfont, 4));
            p1.addCell(createCell("在庫数", keyfont));
            Integer stockCnt = 0;
            if (!StringTools.isNullOrEmpty(object.getString("stock_cnt"))) {
                stockCnt = Integer.valueOf(object.getString("stock_cnt"));
            }
            p1.addCell(createCell(String.valueOf(stockCnt), keyfont));
        }
        doc.add(table);
        doc.add(p1);
        doc.close();
    }

    /**
     * @param jsonObject : 数据
     * @param codePath : 条形码地址
     * @param pdfPath : pdf地址
     * @description: 仓库侧入库PDF生成
     * @return: void
     * @date: 2021/1/15 16:53
     */
    public static void createNewWarehouseInfoPDF(JSONObject jsonObject, String codePath, String pdfPath)
        throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        TableFooter footer = new TableFooter();
        writer.setPageEvent(footer);
        doc.open();
        PdfPTable table = createTable(new float[] {
            265, 265
        }, 530);
        PdfPCell cell = createCell(jsonObject.getString("warehouse_nm"), smallfont, Element.ALIGN_LEFT, 0);
        cell.setPaddingTop(-25f);
        cell.setPaddingLeft(-3f);
        table.addCell(cell);
        String logo = codePath;
        Image image = Image.getInstance(logo);
        image.setAlignment(Image.ALIGN_CENTER);
        image.scalePercent(40);
        PdfPCell shipmentImgCell = createShipmentImgCell(image, 0, 1, 2, 0, 10f);
        shipmentImgCell.setPaddingTop(-1f);
        shipmentImgCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(shipmentImgCell);
        PdfPCell cell1 = createCell("入庫確認帳票", titlefont, Element.ALIGN_RIGHT, 0);
        cell1.setPaddingRight(-40f);
        table.addCell(cell1);
        PdfPTable table1 = createTable(new float[] {
            70, 235, 70, 30, 35, 45, 45
        }, 530);
        table1.setHeaderRows(1);
        table1.addCell(createShipmentCell("UID" + "\n" + "BARCODE", keyfont, 12, 1, 1.5f, 30f));
        table1.addCell(createShipmentCell("商品コード" + "\n" + "商品名", keyfont, 8, 1, 1.5f, 30f));
        table1.addCell(createShipmentCell("ロケーション" + "\n" + "入庫前実在庫数", keyfont, 8, 1, 1.5f, 30f));
        table1.addCell(createShipmentCell("サイズ", keyfont, 8, 1, 1.5f, 30f));
        table1.addCell(createShipmentCell("重量(g)", keyfont, 8, 1, 1.5f, 30f));
        table1.addCell(createShipmentCell("依頼数", keyfont, 8, 1, 1.5f, 30f));
        table1.addCell(createShipmentCell("実績数", keyfont, 8, 1, 1.5f, 30f));
        JSONArray item = jsonObject.getJSONArray("item");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < item.size(); i++) {
            JSONObject object = item.getJSONObject(i);
            String barcode =
                !StringTools.isNullOrEmpty(object.getString("barcode")) ? object.getString("barcode") : "-";
            stringBuilder.append(object.getString("client_id")).append("-").append(object.getString("product_id"))
                .append("\n").append(barcode);
            table1.addCell(createShipmentCell(stringBuilder.toString(), smalltextfont, 13, Element.ALIGN_LEFT));
            stringBuilder.delete(0, stringBuilder.length());
            if (!StringTools.isNullOrEmpty(object.getString("code"))) {
                stringBuilder.append(object.getString("code")).append("\n");
            }
            stringBuilder.append(object.getString("name"));
            table1.addCell(createShipmentCell(stringBuilder.toString(), smalltextfont, 9, Element.ALIGN_LEFT));
            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append(isNullOrEmpty(object.getString("wh_location_nm"))).append("\n           ")
                .append(isNullOrEmpty(object.getString("stock_cnt")));
            table1.addCell(createShipmentCell(stringBuilder.toString(), smalltextfont, 9, Element.ALIGN_LEFT));
            stringBuilder.delete(0, stringBuilder.length());
            table1.addCell(
                createShipmentCell(judgmentNull(object.getString("sizeName")), smalltextfont, 9, Element.ALIGN_RIGHT));
            table1.addCell(
                createShipmentCell(judgmentNull(object.getString("weight")), smalltextfont, 9, Element.ALIGN_RIGHT));
            table1.addCell(
                createShipmentCell(judgmentNull(object.getString("quantity")), keyfont, 9, Element.ALIGN_RIGHT));
            table1.addCell(
                createShipmentCell(judgmentNull(object.getString("product_cnt")), keyfont, 9, Element.ALIGN_RIGHT));
        }
        if (item.size() < 10) {
            for (int i = 0; i < (10 - item.size()); i++) {
                table1.addCell(createShipmentCell(" \n ", smalltextfont, 13, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
            }
        }
        table1.addCell(createShipmentCell(" ", keyfont, 14, 1, 1.2f, 30f));
        table1.addCell(createShipmentCell(" ", keyfont, 14, 1, 1.2f, 30f));
        table1.addCell(createShipmentCell(" ", keyfont, 14, 1, 1.2f, 30f));
        table1.addCell(createShipmentCell(" ", keyfont, 14, 1, 1.2f, 30f));
        table1.addCell(createShipmentCell(" ", keyfont, 14, 1, 1.2f, 30f));
        table1.addCell(createShipmentCell(" ", keyfont, 14, 1, 1.2f, 30f));
        table1.addCell(createShipmentCell(" ", keyfont, 14, 1, 1.2f, 30f));
        doc.add(table);
        doc.add(table1);
        doc.close();
        writer.close();
    }

    /**
     * @description: 仓库侧在库 商品PDF生成
     * @return: void
     * @date: 2020/07/14
     */
    public static void createStockProductPDF(JSONArray jsonObject, String pdfPath) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        for (int i = 0; i < jsonObject.size(); i++) {
            JSONObject json = jsonObject.getJSONObject(i);

            Font size = new Font();
            size = bigSize;
            if (!StringTools.isNullOrEmpty(json.getString("locationNm"))) {
                if (json.getString("locationNm").length() > 10) {
                    size = overSize;
                }
            }
            Paragraph paragraph = new Paragraph(isNullOrEmpty(json.getString("locationNm")), size);
            paragraph.setAlignment(1);
            Paragraph blankRow1 = new Paragraph(16f, " ", titlefont);
            Paragraph p1 = new Paragraph();
            p1.add(new Chunk(new LineSeparator()));
            Paragraph blankRow2 = new Paragraph(16f, " ", titlefont);
            Paragraph p2 = new Paragraph(isNullOrEmpty(json.getString("stock_cnt")), overBigSize);
            p2.setAlignment(1);
            Paragraph blankRow3 = new Paragraph(150f, " ", overSize);
            Image image = Image.getInstance(json.getString("codePath"));
            image.setAlignment(Image.ANCHOR);
            image.scalePercent(100);
            image.setAbsolutePosition(100, 150);
            PdfPTable table = createTable(new float[] {
                200, 360
            });
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setHorizontalAlignment(1);
            cell.setColspan(1);
            cell.setRowspan(1);
            cell.setImage(image);
            cell.setBorder(0);
            cell.setPaddingLeft(20f);
            table.addCell(cell);
            PdfPCell cell1 = new PdfPCell();
            cell1.setVerticalAlignment(Element.ALIGN_LEFT);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setPhrase(new Phrase(isNullOrEmpty(json.getString("name")), titlefont));
            cell1.setBorder(0);
            cell1.setPaddingLeft(35f);
            cell1.setFixedHeight(10f);
            table.addCell(cell1);
            doc.add(paragraph);
            doc.add(blankRow1);
            doc.add(p1);
            doc.add(blankRow2);
            doc.add(p2);
            doc.add(blankRow3);
            doc.add(table);
            doc.newPage();
        }
        doc.close();
    }

    /**
     * @description: 仓库侧在库商品明细PDF生成
     * @return: void
     * @date: 2020/07/14
     */
    public static void createStockProductTablePDF(JSONObject jsonObject, String pdfPath, String locationCodePath)
        throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        Image image = Image.getInstance(locationCodePath);
        image.setAlignment(Image.ANCHOR);
        image.setAbsolutePosition(40, 200);
        PdfPTable table = createTable(new float[] {
            180, 180, 200
        });
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = DateUtils.getDate();
        String newDate = format.format(date);
        table.addCell(createCell(newDate, headfont, Element.ALIGN_LEFT, 0));
        table.addCell(createCell(" ", headfont, Element.ALIGN_LEFT, 0));
        table.addCell(createCell(image, 1, 1, 2, 0));
        Font size = titlefont;
        if (!StringTools.isNullOrEmpty(jsonObject.getString("wh_location_nm"))) {
            if (jsonObject.getString("wh_location_nm").length() > 10) {
                size = headfont;
            }
        }
        table.addCell(createCell(isNullOrEmpty(jsonObject.getString("wh_location_nm")), size, Element.ALIGN_LEFT, 0));
        table.addCell(createCell(" ", titlefont, Element.ALIGN_LEFT, 0));

        PdfPTable p2 = createTable(new float[] {
            140, 80, 120, 80, 140
        });
        p2.addCell(createCell("商品ID", textfont));
        p2.addCell(createCell("番号", textfont));
        p2.addCell(createCell("商品名", textfont));
        p2.addCell(createCell("数量", textfont));
        p2.addCell(createCell("備考", textfont));
        Integer status = Integer.valueOf(jsonObject.getString("status"));
        JSONArray stockInfo = jsonObject.getJSONArray("stockInfo");
        for (int i = 0; i < stockInfo.size(); i++) {
            JSONObject stock = stockInfo.getJSONObject(i);
            Image image1 = Image.getInstance(stock.getString("codePath"));
            PdfPCell cell = new PdfPCell();
            cell.setImage(image1);
            p2.addCell(cell);
            p2.addCell(createCell(isNullOrEmpty(stock.getString("identifier")), keyfont, Element.ALIGN_CENTER));
            p2.addCell(createCell(isNullOrEmpty(stock.getString("name")), textfont, Element.ALIGN_CENTER));
            if (status == 1) {
                p2.addCell(createCell(isNullOrEmpty(stock.getString("stock_cnt")), textfont, Element.ALIGN_CENTER));
            } else {
                p2.addCell(createCell(" ", textfont, Element.ALIGN_CENTER));
            }
            p2.addCell(createCell(" ", textfont, Element.ALIGN_CENTER));
        }
        doc.add(table);
        doc.add(p2);
        doc.close();
    }

    /**
     * @description: 棚卸し在庫商品明細
     * @return: void
     * @date: 2020/07/14
     */
    public static void createStockManagementPDF(JSONObject jsonObject, String codePath, String pdfPath)
        throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        PdfPTable table = createTable(new float[] {
            80, 480
        });
        Image image = Image.getInstance(codePath);
        image.scalePercent(40);
        image.setAbsolutePosition(40, 60);
        table.addCell(image);
        PdfPCell pdfPCell = new PdfPCell(new Paragraph("棚卸し在庫商品明細", titlefont));
        pdfPCell.setPaddingLeft(150f);
        pdfPCell.setBorder(0);
        table.addCell(pdfPCell);
        PdfPTable p1 = createTable(new float[] {
            120, 280, 80, 80
        });
        p1.addCell(createCell("商品ID / BARCODE", keyfont));
        p1.addCell(createCell("商品名(商品コード)", keyfont));
        p1.addCell(createCell("在庫数", keyfont));
        p1.addCell(createCell("実績数", keyfont));
        StringBuilder stringBuilder = new StringBuilder();
        JSONArray jsonArray = jsonObject.getJSONArray("stockInfo");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject stockInfo = jsonArray.getJSONObject(i);
            p1.addCell(createCell(isNullOrEmpty(stockInfo.getString("product_id")), keyfont, 1, 1, 1, 35f));
            stringBuilder.append(isNullOrEmpty(stockInfo.getString("name"))).append("(")
                .append(isNullOrEmpty(stockInfo.getString("code"))).append(")");
            p1.addCell(createCell(stringBuilder.toString(), keyfont, 1, 1, 1, 35f));
            p1.addCell(createCell(isNullOrEmpty(stockInfo.getString("stock_count")), keyfont, 1, 1, 2, 50f));
            p1.addCell(createCell(stockInfo.getString("count") != null ? stockInfo.getString("count") : "-", keyfont, 1,
                1, 2, 50f));
            p1.addCell(createCell(isNullOrEmpty(stockInfo.getString("barcode")), keyfont, 1, 1, 1, 15f));
            // p1.addCell(createCell("口ケ"+":"+ isNullOrEmpty(stockInfo.getString("locationNm")),keyfont,1,1,1,15f));
            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append("口ケ:")
                .append(isNullOrEmpty(stockInfo.getString("locationNm")));
            p1.addCell(createCell(stringBuilder.toString(), keyfont, 1, 1, 1, 15f));
        }
        doc.add(table);
        doc.add(p1);
        doc.close();
    }

    /**
     * @description: 商品ラベル印刷
     * @return: void
     * @date: 2020/07/14
     */
    public static void itemsLabelPDF(JSONArray item, String pdfPath, String num) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        JSONArray objects = new JSONArray();
        if (StringTools.isNullOrEmpty(num)) {
            num = "1";
        }
        Integer number = Ints.tryParse(num);
        for (int i = 0; i < item.size(); i++) {
            for (int j = 0; j < number; j++) {
                objects.add(item.get(i));
            }
        }
        int objects_size = objects.size();
        int excess_size = objects.size() % 4;
        if (excess_size != 0) {
            for (int i = 1; i <= (4 - excess_size); i++) {
                objects.add(item.get(0));
            }
        }

        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        PdfPTable p1 = createTable(new float[] {
            50, 190, 160, 190, 160, 190, 160, 190, 60
        });
        PdfPCell addCell = createCell("", smalltextfont, Element.ALIGN_LEFT, 0);
        for (int i = 0; i < objects.size(); i++) {
            // 每行第一个条形码前插入一个空cell
            if (i % 4 == 0) {
                p1.addCell(addCell);
            }
            JSONObject json = objects.getJSONObject(i);
            Image image = Image.getInstance(json.getString("codePath"));
            PdfPCell cell = new PdfPCell();
            cell.setFixedHeight(45);
            if (i < objects_size) {
                cell.setImage(image);
            }
            cell.setBorder(0);
            p1.addCell(cell);
            p1.addCell(createCell("    ", keyfont, Element.ALIGN_LEFT, 0));
            if ((i + 1) % 4 == 0) {
                p1.addCell(addCell);
                PdfPCell cell1 = createCell(objects.getJSONObject(i - 3).getString("product_name"), smalltextfont,
                    Element.ALIGN_LEFT, 0);
                PdfPCell cell2 = createCell(objects.getJSONObject(i - 2).getString("product_name"), smalltextfont,
                    Element.ALIGN_LEFT, 0);
                PdfPCell cell3 = createCell(objects.getJSONObject(i - 1).getString("product_name"), smalltextfont,
                    Element.ALIGN_LEFT, 0);
                PdfPCell cell4 = createCell(objects.getJSONObject(i).getString("product_name"), smalltextfont,
                    Element.ALIGN_LEFT, 0);
                cell1.setPaddingLeft(-15f);
                cell1.setFixedHeight(30);
                cell1.setPaddingTop(-20);
                cell2.setPaddingLeft(-15f);
                cell2.setFixedHeight(30);
                cell2.setPaddingTop(-20);
                cell3.setPaddingLeft(-15f);
                cell3.setFixedHeight(30);
                cell3.setPaddingTop(-20);
                cell4.setPaddingLeft(-15f);
                cell4.setFixedHeight(30);
                cell4.setPaddingTop(-20);
                p1.addCell(cell1);
                p1.addCell(createCell("", smalltextfont, Element.ALIGN_LEFT, 0));
                if (i - 2 < objects_size) {
                    p1.addCell(cell2);
                } else {
                    p1.addCell(createCell("", smalltextfont, Element.ALIGN_LEFT, 0));
                }
                p1.addCell(createCell("", smalltextfont, Element.ALIGN_LEFT, 0));
                if (i - 1 < objects_size) {
                    p1.addCell(cell3);
                } else {
                    p1.addCell(createCell("", smalltextfont, Element.ALIGN_LEFT, 0));
                }
                p1.addCell(createCell("", smalltextfont, Element.ALIGN_LEFT, 0));
                if (i < objects_size) {
                    p1.addCell(cell4);
                } else {
                    p1.addCell(createCell("", smalltextfont, Element.ALIGN_LEFT, 0));
                }
                p1.addCell(createCell("", smalltextfont, Element.ALIGN_LEFT, 0));
            }
        }
        doc.add(p1);
        doc.close();
    }

    /**
     * @param jsonObject : 数据
     * @param pdfPath ： pdf路径
     * @param workName ： 作业id
     * @description: トータルピッキングリストPDF
     * @return: void
     * @date: 2021/1/15 15:11
     */
    public static void createNewOrderListPDF(JSONObject jsonObject, String pdfPath, String workName) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        TableFooter footer = new TableFooter();
        writer.setPageEvent(footer);
        doc.open();
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        // NTM店铺标识
        Boolean isNtmFlg = jsonObject.getBoolean("isNtmFlg");
        PdfPTable table = createTable(new float[] {
            530
        }, 530);
        PdfPCell shipmentCell =
            createShipmentCell(jsonObject.getString("warehouse_nm"), smallfont, 15, Element.ALIGN_LEFT);
        shipmentCell.setPaddingTop(-25f);
        table.addCell(shipmentCell);
        table.addCell(createShipmentCell("トータルピッキングリスト", titlefont, 15, Element.ALIGN_CENTER));
        PdfPCell shipmentCell1 = createShipmentCell("WorkId：" + workName, textfont, 15, Element.ALIGN_LEFT);
        shipmentCell1.setPaddingLeft(35f);
        table.addCell(shipmentCell1);

        if (isNtmFlg) {
            PdfPTable table1 = createTable(new float[] {
                140, 70, 70, 220, 30
            }, 530);
            table1.setHeaderRows(1);
            table1.addCell(createShipmentCell("ロケーション", smalltextfont, 12, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("ロット番号", smalltextfont, 8, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("商品コード", smalltextfont, 8, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
            // a记录商品种类数量
            int productNum = 0;
            JSONArray setItems = jsonObject.getJSONArray("setItems");
            for (int i = 0; i < setItems.size(); i++) {
                JSONObject object = setItems.getJSONObject(i);
                table1.addCell(createShipmentCell(isNullOrEmpty(object.getString("locationName")), smalltextfont, 13,
                    Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(object.getString("lot_no"), smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(object.getString("code"), smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(object.getString("name"), smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(
                    createShipmentCell(object.getString("product_plan_cnt"), smalltextfont, 9, Element.ALIGN_LEFT));
                productNum += Integer.parseInt(object.getString("product_plan_cnt"));
            }
            if (setItems.size() < 10) {
                for (int i = 0; i < (10 - setItems.size()); i++) {
                    table1.addCell(createShipmentCell(" \n ", smalltextfont, 13, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                }
            }
            table1.addCell(createShipmentCell("", smalltextfont, 0, 12, 1, 20, 0f, 5, 1f));
            table1.addCell(createShipmentCell("", smalltextfont, 0, 12, 1, 20, 0f, 5, 1f));
            table1.addCell(createShipmentCell("", smalltextfont, 0, 12, 1, 20, 0f, 5, 1f));
            PdfPCell cell = createShipmentCell("数量合計", smalltextfont, Element.ALIGN_RIGHT, 12, 1, 20, 0f, 5, 1f);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table1.addCell(cell);
            PdfPCell cell1 =
                createShipmentCell(Integer.toString(productNum), keyfont, Element.ALIGN_RIGHT, 8, 1, 20, 0f, 5, 1f);
            cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table1.addCell(cell1);
            doc.add(table);
            doc.add(table1);
        } else {
            PdfPTable table1 = createTable(new float[] {
                60, 70, 70, 80, 160, 60, 30
            }, 530);
            table1.setHeaderRows(1);
            table1.addCell(createShipmentCell("ロケーション", smalltextfont, 12, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("ロット番号", smalltextfont, 8, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("商品コード", smalltextfont, 8, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("管理バーコード", smalltextfont, 8, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("備考欄", textfont, 8, 1, 1.5f, 30f));
            table1.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
            // a记录商品种类数量
            int productNum = 0;
            JSONArray setItems = jsonObject.getJSONArray("setItems");
            for (int i = 0; i < setItems.size(); i++) {
                JSONObject object = setItems.getJSONObject(i);
                table1.addCell(createShipmentCell(isNullOrEmpty(object.getString("locationName")), smalltextfont, 13,
                    Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(object.getString("lot_no"), smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(object.getString("code"), smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(object.getString("barcode"), smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(object.getString("name"), smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(createShipmentCell(object.getString("bikou"), smalltextfont, 9, Element.ALIGN_LEFT));
                table1.addCell(
                    createShipmentCell(object.getString("product_plan_cnt"), smalltextfont, 9, Element.ALIGN_LEFT));
                productNum += Integer.parseInt(object.getString("product_plan_cnt"));
            }
            if (setItems.size() < 10) {
                for (int i = 0; i < (10 - setItems.size()); i++) {
                    table1.addCell(createShipmentCell(" \n ", smalltextfont, 13, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table1.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                }
            }
            table1.addCell(createShipmentCell("", smalltextfont, 0, 12, 1, 20, 0f, 5, 1f));
            table1.addCell(createShipmentCell("", smalltextfont, 0, 12, 1, 20, 0f, 5, 1f));
            table1.addCell(createShipmentCell("", smalltextfont, 0, 12, 1, 20, 0f, 5, 1f));
            table1.addCell(createShipmentCell("", smalltextfont, 0, 12, 1, 20, 0f, 5, 1f));
            table1.addCell(createShipmentCell("", smalltextfont, 0, 12, 1, 20, 0f, 5, 1f));
            PdfPCell cell = createShipmentCell("数量合計", smalltextfont, Element.ALIGN_RIGHT, 12, 1, 20, 0f, 5, 1f);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table1.addCell(cell);
            PdfPCell cell1 =
                createShipmentCell(Integer.toString(productNum), keyfont, Element.ALIGN_RIGHT, 8, 1, 20, 0f, 5, 1f);
            cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table1.addCell(cell1);
            doc.add(table);
            doc.add(table1);
        }

        doc.close();
        writer.close();
    }

    /**
     * @param jsonParam : 数据
     * @param pdfPath ： pdf地址
     * @param shipments ：出库依赖信息
     * @description: 作業指示書PDF仓库侧working页面 （新版）
     * @return: void
     * @date: 2020/12/30 13:16
     */
    public static void createNewInstructionPDFWorking(JSONObject jsonParam, String pdfPath,
        List<Tw200_shipment> shipments) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();
        JSONArray jsonArray = jsonParam.getJSONArray("data");
        // NTM店铺标识
        Boolean isNtmFlg = jsonParam.getBoolean("isNtmFlg");
        for (int l = 0; l < jsonArray.size(); l++) {
            // a获取当前出库依赖的详细信息
            JSONObject jsonObject = jsonArray.getJSONObject(l);
            Tw200_shipment shipment = shipments.get(l);

            PdfPTable table = createTable(new float[] {
                330, 190
            }, 520);
            PdfPTable table1 = createTable(new float[] {
                260, 320
            }, 580);

            if (isNtmFlg) {

                table.addCell(createShipmentCell("作業指示書", titlefont, Element.ALIGN_CENTER, 15, 180f));
                Image picture = Image.getInstance(jsonObject.getString("codePath"));
                picture.setAlignment(Image.ALIGN_CENTER);
                picture.scalePercent(40);
                PdfPCell shipmentImgCell = createShipmentImgCell(picture, 0, 1, 2, 0, 0f);
                shipmentImgCell.setPaddingTop(-20f);
                table.addCell(shipmentImgCell);
                String noteType = "";
                if ("同梱しない".equals(shipment.getDelivery_note_type())) {
                    noteType = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
                }
                table.addCell(createShipmentCell(noteType, littleBoldFont, Element.ALIGN_LEFT, 15, 25f));

                String surname = shipment.getSurname();
                if (StringTools.isNullOrEmpty(surname)) {
                    surname = shipment.getCompany();
                }
                table1
                    .addCell(createShipmentCell("配送先：" + surname, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

                table1.addCell(createShipmentCell("店舗名：" + shipment.getClient_nm(), smalltextUnderlineFont8,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 114f));
                // 住所1

                String sendAdd = "";
                if (!StringTools.isNullOrEmpty(shipment.getPrefecture())) {
                    sendAdd += shipment.getPrefecture()
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(shipment.getAddress1())) {
                    sendAdd += shipment.getAddress1();
                }
                table1.addCell(createShipmentCell("〒" + shipment.getPostcode(), smalltextfont, Element.ALIGN_LEFT, 0, 1,
                    0, 0, 45f));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Timestamp shipping_date = shipment.getShipping_date();
                String shippingDate = "";
                if (!StringTools.isNullOrEmpty(shipping_date)) {
                    shippingDate = simpleDateFormat.format(shipping_date);
                }

                table1.addCell(createShipmentCell("出荷予定日(納品日)：" + replaceNull(shippingDate), smalltextUnderlineFont8,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                table1.addCell(createShipmentCell(sendAdd, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

                String order_no = shipment.getOrder_no();
                if (StringTools.isNullOrEmpty(order_no)) {
                    order_no = shipment.getShipment_plan_id();
                }
                table1.addCell(createShipmentCell("注文番号：" + order_no, smalltextUnderlineFont10, Element.ALIGN_LEFT, 0,
                    1, 0, 0, 107f));

                // 住所2
                table1.addCell(createShipmentCell(" " + judgmentNull(shipment.getAddress2()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                table1.addCell(createShipmentCell("配送方法：" + replaceNull(jsonObject.getString("method")),
                    smalltextUnderlineFont8, Element.ALIGN_LEFT, 0, 1, 0, 0, 107f));
                table1.addCell(createShipmentCell("注文者：" + jsonObject.getString("order_name"), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

                // 住所１
                String masterAdd = "";
                if (!StringTools.isNullOrEmpty(jsonObject.getString("order_todoufuken"))) {
                    masterAdd += jsonObject.getString("order_todoufuken")
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(jsonObject.getString("order_address1"))) {
                    masterAdd += jsonObject.getString("order_address1");
                }
                String order_zip = "";
                if (!StringTools.isNullOrEmpty(jsonObject.getString("order_zip_code1"))) {
                    order_zip += jsonObject.getString("order_zip_code1");
                }
                if (!StringTools.isNullOrEmpty(jsonObject.getString("order_zip_code2"))) {
                    order_zip += jsonObject.getString("order_zip_code2");
                }
                if (!StringTools.isNullOrEmpty(order_zip)) {
                    order_zip = "〒" + order_zip;
                }


                Timestamp delivery_plan_date = shipment.getDelivery_date();
                String deliveryPlanDate = "ー";
                if (!StringTools.isNullOrEmpty(delivery_plan_date)) {
                    deliveryPlanDate = simpleDateFormat.format(delivery_plan_date);
                }
                table1.addCell(createShipmentCell("お届け希望日：" + deliveryPlanDate, smalltextUnderlineFont8,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 94f));
                table1.addCell(createShipmentCell(order_zip, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                table1.addCell(createShipmentCell("お届け時間帯：" + replaceNull(jsonObject.getString("delivery_time_slot")),
                    smalltextUnderlineFont8, Element.ALIGN_LEFT, 0, 1, 0, 0, 94f));

                table1.addCell(createShipmentCell(masterAdd, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                table1.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                // 住所２
                table1.addCell(createShipmentCell(" " + judgmentNull(jsonObject.getString("order_address2")),
                    smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                table1.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

                PdfPTable table2 = createTable(new float[] {
                    90, 90, 210, 90, 40
                }, 520);
                table2.setHeaderRows(1);
                table2.addCell(createShipmentCell("ロケーション", textfont, 12, 1, 1.5f, 30f));
                table2.addCell(createShipmentCell("商品コード", textfont, 8, 1, 1.5f, 30f));
                table2.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
                table2.addCell(createShipmentCell("備考", textfont, 8, 1, 1.5f, 30f));
                table2.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                JSONArray items = jsonObject.getJSONArray("items");
                int total = 0;
                // for (int i = 0; i < items.size(); i++) {
                // JSONObject itemsJSONObject = items.getJSONObject(i);
                // table2.addCell(createShipmentCell(itemsJSONObject.getString("locationName"), smallBoldFont, 13,
                // Element.ALIGN_LEFT));
                // table2.addCell(createShipmentCell(itemsJSONObject.getString("code"), textfont, 9,
                // Element.ALIGN_LEFT));
                // table2.addCell(createShipmentCell(itemsJSONObject.getString("name"), smalltextfont, 9,
                // Element.ALIGN_LEFT));
                // table2.addCell(createShipmentCell(itemsJSONObject.getString("ntm_memo"), textfont, 9,
                // Element.ALIGN_LEFT));
                // table2.addCell(createShipmentCell(itemsJSONObject.getString("product_plan_cnt"), smallBoldFont, 9,
                // Element.ALIGN_CENTER));
                // total += Integer.parseInt(itemsJSONObject.getString("product_plan_cnt"));
                // }
                table2.addCell(createShipmentCell(" ", textfont, 12));
                table2.addCell(createShipmentCell(" ", textfont, 12));
                table2.addCell(createShipmentCell(" ", textfont, 12));
                PdfPCell cell = createShipmentCell("数量合計", smalltextfont, 12);
                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table2.addCell(cell);
                table2.addCell(createShipmentCell(total + "", textfont, 8, Element.ALIGN_RIGHT));
                doc.add(table);
                doc.add(table1);
                doc.add(table2);
                // a判断是否有同捆物
                JSONArray bundled = jsonObject.getJSONArray("bundled");
                if (!StringTools.isNullOrEmpty(bundled) && bundled.size() != 0) {
                    Paragraph p2 = new Paragraph("同梱物", headfont);
                    format(p2);
                    Paragraph p3 = new Paragraph(" ", textfont);
                    doc.add(p2);
                    doc.add(p3);
                    PdfPTable table3 = createTable(new float[] {
                        90, 90, 210, 90, 40
                    }, 520);
                    // table3.setSpacingBefore(30f);
                    table3.setHeaderRows(1);
                    table3.addCell(createShipmentCell("ロケーション", textfont, 12, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("商品コード", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("同梱物名", textfont, 8, 1, 1.5f, 30f));
                    table2.addCell(createShipmentCell("備考", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                    int bundledTotal = 0;
                    for (int i = 0; i < bundled.size(); i++) {
                        JSONObject json = bundled.getJSONObject(i);
                        table3.addCell(
                            createShipmentCell(json.getString("locationName"), smallBoldFont, 13, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(json.getString("code"), textfont, 9, Element.ALIGN_LEFT));
                        table3
                            .addCell(createShipmentCell(json.getString("name"), smalltextfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(json.getString("ntm_memo"), textfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(json.getString("product_plan_cnt"), smallBoldFont, 9,
                            Element.ALIGN_RIGHT));
                        bundledTotal += json.getInteger("product_plan_cnt");
                    }
                    table3.addCell(createShipmentCell(" ", textfont, 12));
                    table3.addCell(createShipmentCell(" ", textfont, 12));
                    table3.addCell(createShipmentCell(" ", textfont, 12));
                    PdfPCell cell1 = createShipmentCell("数量合計", smalltextfont, 12);
                    cell1.setVerticalAlignment(Element.ALIGN_RIGHT);
                    cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table3.addCell(cell1);
                    table3.addCell(createShipmentCell(bundledTotal + "", textfont, 8, Element.ALIGN_RIGHT));
                    doc.add(table3);
                }
                // 判断是否含有ご請求コード
                String billBarcode = jsonObject.getString("billBarcode");
                if (!StringTools.isNullOrEmpty(billBarcode)) {
                    PdfPTable table4 = createTable(new float[] {
                        100, 420
                    }, 520);
                    table4.setSpacingBefore(30f);
                    table4.addCell(createShipmentCell("GMO後払い\nご請求コード", textfont, 12, 1, 1.5f, 30f));
                    table4.addCell(createShipmentCell(billBarcode, keyfont, 8, 1, 1.5f, 30f));
                    doc.add(table4);
                }


                PdfPTable table5 = createTable(new float[] {
                    240, 30, 250
                }, 520);
                table5.setSpacingBefore(20f);
                table5.addCell(createShipmentCell("■特記事項", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 13, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5
                    .addCell(createShipmentCell("■購入者備考欄 （梱包メモ）", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 13, 15f));


                String gift_wrapping_unit = shipment.getGift_wrapping_unit();
                String giftWrappingUnit = "";
                if (!StringTools.isNullOrEmpty(gift_wrapping_unit)) {
                    switch (gift_wrapping_unit) {
                        case "1":
                            giftWrappingUnit = "注文単位 ";
                            String giftWrappingType =
                                shipment.getTw201_shipment_detail().get(0).getGift_wrapping_type();
                            if (!StringTools.isNullOrEmpty(giftWrappingType)) {
                                giftWrappingUnit += " " + giftWrappingType;
                            }
                            break;
                        case "2":
                            giftWrappingUnit = "商品単位";
                            break;
                        default:
                            break;
                    }
                }
                if (!StringTools.isNullOrEmpty(giftWrappingUnit)) {
                    giftWrappingUnit = "・ギフト：" + giftWrappingUnit;
                }
                table5
                    .addCell(createShipmentCell(giftWrappingUnit, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));

                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                PdfPCell shipmentCell =
                    createShipmentCell(shipment.getMemo(), smalltextfont, Element.ALIGN_LEFT, 0, 10, 0, 0, 20f);
                shipmentCell.setLeading(5f, 1f);
                table5.addCell(shipmentCell);
                String cushioning_unit = shipment.getCushioning_unit();
                String cushioningType = "";
                if (!StringTools.isNullOrEmpty(cushioning_unit)) {
                    switch (cushioning_unit) {
                        case "1":
                            cushioningType = "注文単位";
                            String cushioning_type = shipment.getTw201_shipment_detail().get(0).getCushioning_type();
                            if (!StringTools.isNullOrEmpty(cushioning_type)) {
                                cushioningType += " " + cushioning_type;
                            }
                            break;
                        case "2":
                            cushioningType = "商品単位";
                            break;
                        default:
                            break;
                    }
                }
                if (!StringTools.isNullOrEmpty(cushioningType)) {
                    cushioningType = "・緩衝材：" + cushioningType;
                }
                table5.addCell(createShipmentCell(cushioningType, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getInstructions_special_notes()),
                    smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou1()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou2()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                // table10.addCell(createShipmentCell(json.getString("memo"), smalltextfont, Element.ALIGN_LEFT, 0, 6,
                // 0, 0, 20f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou3()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou4()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou5()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou6()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou7()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                doc.add(table5);
            } else {
                table.addCell(createShipmentCell("作業指示書", titlefont, Element.ALIGN_CENTER, 15, 180f));
                Image picture = Image.getInstance(jsonObject.getString("codePath"));
                picture.setAlignment(Image.ALIGN_CENTER);
                picture.scalePercent(40);
                PdfPCell shipmentImgCell = createShipmentImgCell(picture, 0, 1, 2, 0, 0f);
                shipmentImgCell.setPaddingTop(-20f);
                table.addCell(shipmentImgCell);
                String noteType = "";
                if ("同梱しない".equals(shipment.getDelivery_note_type())) {
                    noteType = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
                }
                table.addCell(createShipmentCell(noteType, littleBoldFont, Element.ALIGN_LEFT, 15, 25f));

                String surname = shipment.getSurname();
                if (StringTools.isNullOrEmpty(surname)) {
                    surname = shipment.getCompany();
                }
                table1
                    .addCell(createShipmentCell("配送先：" + surname, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

                table1.addCell(createShipmentCell("店舗名：" + shipment.getClient_nm(), smalltextfont, Element.ALIGN_LEFT,
                    0, 1, 0, 0, 114f));
                // 住所1

                String sendAdd = "";
                if (!StringTools.isNullOrEmpty(shipment.getPrefecture())) {
                    sendAdd += shipment.getPrefecture()
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(shipment.getAddress1())) {
                    sendAdd += shipment.getAddress1();
                }
                table1.addCell(createShipmentCell("〒" + shipment.getPostcode() + " " + sendAdd, smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Timestamp shipping_date = shipment.getShipping_date();
                String shippingDate = "";
                if (!StringTools.isNullOrEmpty(shipping_date)) {
                    shippingDate = simpleDateFormat.format(shipping_date);
                }

                table1.addCell(createShipmentCell("出荷予定日(納品日)：" + replaceNull(shippingDate), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                // 住所2
                table1.addCell(createShipmentCell(" " + judgmentNull(shipment.getAddress2()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                String order_no = shipment.getOrder_no();
                if (StringTools.isNullOrEmpty(order_no)) {
                    order_no = shipment.getShipment_plan_id();
                }
                table1.addCell(
                    createShipmentCell("注文番号：" + order_no, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 107f));
                table1.addCell(createShipmentCell("注文者：" + jsonObject.getString("order_name"), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                table1.addCell(createShipmentCell("配送方法：" + replaceNull(jsonObject.getString("method")), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 107f));

                // 住所１
                String masterAdd = "";
                if (!StringTools.isNullOrEmpty(jsonObject.getString("order_todoufuken"))) {
                    masterAdd += jsonObject.getString("order_todoufuken")
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(jsonObject.getString("order_address1"))) {
                    masterAdd += jsonObject.getString("order_address1");
                }
                String order_zip = "";
                if (!StringTools.isNullOrEmpty(jsonObject.getString("order_zip_code1"))) {
                    order_zip += jsonObject.getString("order_zip_code1");
                }
                if (!StringTools.isNullOrEmpty(jsonObject.getString("order_zip_code2"))) {
                    order_zip += jsonObject.getString("order_zip_code2");
                }
                if (!StringTools.isNullOrEmpty(order_zip)) {
                    order_zip = "〒" + order_zip;
                }
                table1.addCell(createShipmentCell(order_zip + " " + masterAdd, smalltextfont, Element.ALIGN_LEFT, 0, 1,
                    0, 0, 45f));


                Timestamp delivery_plan_date = shipment.getDelivery_date();
                String deliveryPlanDate = "ー";
                if (!StringTools.isNullOrEmpty(delivery_plan_date)) {
                    deliveryPlanDate = simpleDateFormat.format(delivery_plan_date);
                }
                table1.addCell(createShipmentCell("お届け希望日：" + deliveryPlanDate, smalltextfont, Element.ALIGN_LEFT, 0, 1,
                    0, 0, 94f));

                // 住所２
                table1.addCell(createShipmentCell(" " + judgmentNull(jsonObject.getString("order_address2")),
                    smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                table1.addCell(createShipmentCell("お届け時間帯：" + replaceNull(jsonObject.getString("delivery_time_slot")),
                    smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 94f));

                PdfPTable table2 = createTable(new float[] {
                    90, 90, 90, 210, 40
                }, 520);
                table2.setHeaderRows(1);
                table2.addCell(createShipmentCell("ロケーション", textfont, 12, 1, 1.5f, 30f));
                table2.addCell(createShipmentCell("商品コード", textfont, 8, 1, 1.5f, 30f));
                table2.addCell(createShipmentCell("管理バーコード", textfont, 8, 1, 1.5f, 30f));
                table2.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
                table2.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                JSONArray items = jsonObject.getJSONArray("items");
                int total = 0;
                for (int i = 0; i < items.size(); i++) {
                    JSONObject itemsJSONObject = items.getJSONObject(i);
                    table2.addCell(createShipmentCell(itemsJSONObject.getString("locationName"), smallBoldFont, 13,
                        Element.ALIGN_LEFT));
                    table2.addCell(
                        createShipmentCell(itemsJSONObject.getString("code"), textfont, 9, Element.ALIGN_LEFT));
                    table2.addCell(
                        createShipmentCell(itemsJSONObject.getString("barcode"), textfont, 9, Element.ALIGN_LEFT));
                    table2.addCell(
                        createShipmentCell(itemsJSONObject.getString("name"), smalltextfont, 9, Element.ALIGN_LEFT));
                    table2.addCell(createShipmentCell(itemsJSONObject.getString("product_plan_cnt"), smallBoldFont, 9,
                        Element.ALIGN_CENTER));
                    total += Integer.parseInt(itemsJSONObject.getString("product_plan_cnt"));
                }
                table2.addCell(createShipmentCell(" ", textfont, 12));
                table2.addCell(createShipmentCell(" ", textfont, 12));
                table2.addCell(createShipmentCell(" ", textfont, 12));
                PdfPCell cell = createShipmentCell("数量合計", smalltextfont, 12);
                cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table2.addCell(cell);
                table2.addCell(createShipmentCell(total + "", textfont, 8, Element.ALIGN_RIGHT));
                doc.add(table);
                doc.add(table1);
                doc.add(table2);
                // a判断是否有同捆物
                JSONArray bundled = jsonObject.getJSONArray("bundled");
                if (!StringTools.isNullOrEmpty(bundled) && bundled.size() != 0) {
                    Paragraph p2 = new Paragraph("同梱物", headfont);
                    format(p2);
                    Paragraph p3 = new Paragraph(" ", textfont);
                    doc.add(p2);
                    doc.add(p3);
                    PdfPTable table3 = createTable(new float[] {
                        90, 90, 90, 210, 40
                    }, 520);
                    // table3.setSpacingBefore(30f);
                    table3.setHeaderRows(1);
                    table3.addCell(createShipmentCell("ロケーション", textfont, 12, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("商品コード", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("管理バーコード", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("同梱物名", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                    int bundledTotal = 0;
                    for (int i = 0; i < bundled.size(); i++) {
                        JSONObject json = bundled.getJSONObject(i);
                        table3.addCell(
                            createShipmentCell(json.getString("locationName"), smallBoldFont, 13, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(json.getString("code"), textfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(json.getString("barcode"), textfont, 9, Element.ALIGN_LEFT));
                        table3
                            .addCell(createShipmentCell(json.getString("name"), smalltextfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(json.getString("product_plan_cnt"), smallBoldFont, 9,
                            Element.ALIGN_RIGHT));
                        bundledTotal += json.getInteger("product_plan_cnt");
                    }
                    table3.addCell(createShipmentCell(" ", textfont, 12));
                    table3.addCell(createShipmentCell(" ", textfont, 12));
                    table3.addCell(createShipmentCell(" ", textfont, 12));
                    PdfPCell cell1 = createShipmentCell("数量合計", smalltextfont, 12);
                    cell1.setVerticalAlignment(Element.ALIGN_RIGHT);
                    cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table3.addCell(cell1);
                    table3.addCell(createShipmentCell(bundledTotal + "", textfont, 8, Element.ALIGN_RIGHT));
                    doc.add(table3);
                }
                // 判断是否含有ご請求コード
                String billBarcode = jsonObject.getString("billBarcode");
                if (!StringTools.isNullOrEmpty(billBarcode)) {
                    PdfPTable table4 = createTable(new float[] {
                        100, 420
                    }, 520);
                    table4.setSpacingBefore(30f);
                    table4.addCell(createShipmentCell("GMO後払い\nご請求コード", textfont, 12, 1, 1.5f, 30f));
                    table4.addCell(createShipmentCell(billBarcode, keyfont, 8, 1, 1.5f, 30f));
                    doc.add(table4);
                }


                PdfPTable table5 = createTable(new float[] {
                    240, 30, 250
                }, 520);
                table5.setSpacingBefore(20f);
                table5.addCell(createShipmentCell("■特記事項", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 13, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5
                    .addCell(createShipmentCell("■購入者備考欄 （梱包メモ）", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 13, 15f));


                String gift_wrapping_unit = shipment.getGift_wrapping_unit();
                String giftWrappingUnit = "";
                if (!StringTools.isNullOrEmpty(gift_wrapping_unit)) {
                    switch (gift_wrapping_unit) {
                        case "1":
                            giftWrappingUnit = "注文単位 ";
                            String giftWrappingType =
                                shipment.getTw201_shipment_detail().get(0).getGift_wrapping_type();
                            if (!StringTools.isNullOrEmpty(giftWrappingType)) {
                                giftWrappingUnit += " " + giftWrappingType;
                            }
                            break;
                        case "2":
                            giftWrappingUnit = "商品単位";
                            break;
                        default:
                            break;
                    }
                }
                if (!StringTools.isNullOrEmpty(giftWrappingUnit)) {
                    giftWrappingUnit = "・ギフト：" + giftWrappingUnit;
                }
                table5
                    .addCell(createShipmentCell(giftWrappingUnit, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));

                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                PdfPCell shipmentCell =
                    createShipmentCell(shipment.getMemo(), smalltextfont, Element.ALIGN_LEFT, 0, 10, 0, 0, 20f);
                shipmentCell.setLeading(5f, 1f);
                table5.addCell(shipmentCell);
                String cushioning_unit = shipment.getCushioning_unit();
                String cushioningType = "";
                if (!StringTools.isNullOrEmpty(cushioning_unit)) {
                    switch (cushioning_unit) {
                        case "1":
                            cushioningType = "注文単位";
                            String cushioning_type = shipment.getTw201_shipment_detail().get(0).getCushioning_type();
                            if (!StringTools.isNullOrEmpty(cushioning_type)) {
                                cushioningType += " " + cushioning_type;
                            }
                            break;
                        case "2":
                            cushioningType = "商品単位";
                            break;
                        default:
                            break;
                    }
                }
                if (!StringTools.isNullOrEmpty(cushioningType)) {
                    cushioningType = "・緩衝材：" + cushioningType;
                }
                table5.addCell(createShipmentCell(cushioningType, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getInstructions_special_notes()),
                    smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou1()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou2()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                // table10.addCell(createShipmentCell(json.getString("memo"), smalltextfont, Element.ALIGN_LEFT, 0, 6,
                // 0, 0, 20f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou3()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou4()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou5()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou6()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou7()), smalltextfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                doc.add(table5);

            }

            doc.newPage();
        }
        doc.close();
    }

    /**
     * @param jsonObject : 数据
     * @param pdfPath ： pdf地址
     * @param codePath ： 条形码地址
     * @param shipments ： 出库依赖数据
     * @description: 作業指示書PDF （新版）
     * @return: void
     * @date: 2020/12/30 9:46
     */
    public static void createNewInstructionPDF(JSONObject jsonObject, String pdfPath, String codePath,
        Tw200_shipment shipment) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        TableFooter footer = new TableFooter();
        writer.setPageEvent(footer);
        doc.open();
        PdfPTable table = createTable(new float[] {
            330, 190
        }, 520);
        table.addCell(createShipmentCell("作業指示書", titlefont, Element.ALIGN_CENTER, 15, 180f));
        Image picture = Image.getInstance(codePath);
        picture.setAlignment(Image.ALIGN_CENTER);
        picture.scalePercent(40);
        PdfPCell shipmentImgCell = createShipmentImgCell(picture, 0, 1, 2, 0, 0f);
        shipmentImgCell.setPaddingTop(-20f);
        table.addCell(shipmentImgCell);

        String noteType = "";
        if ("同梱しない".equals(shipment.getDelivery_note_type())) {
            noteType = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
        }
        table.addCell(createShipmentCell(noteType, littleBoldFont, Element.ALIGN_LEFT, 15, 25f));

        PdfPTable table1 = createTable(new float[] {
            290, 290
        }, 580);

        String surname = shipment.getSurname();
        if (StringTools.isNullOrEmpty(surname)) {
            surname = shipment.getCompany();
        }
        table1.addCell(createShipmentCell("配送先：" + surname, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

        table1.addCell(createShipmentCell("店舗名：" + jsonObject.getString("client_nm"), smalltextfont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 85f));
        // 住所1

        String sendAdd = "";
        if (!StringTools.isNullOrEmpty(shipment.getPrefecture())) {
            sendAdd += shipment.getPrefecture()
                + " ";
        }
        if (!StringTools.isNullOrEmpty(shipment.getAddress1())) {
            sendAdd += shipment.getAddress1();
        }
        table1.addCell(createShipmentCell("〒" + shipment.getPostcode() + " " + sendAdd, smalltextfont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp shipping_date = shipment.getShipping_date();
        String shippingDate = "";
        if (!StringTools.isNullOrEmpty(shipping_date)) {
            shippingDate = simpleDateFormat.format(shipping_date);
        }

        table1.addCell(createShipmentCell("出荷予定日(納品日)：" + replaceNull(shippingDate), smalltextfont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 40f));

        // 住所2
        table1.addCell(createShipmentCell(" " + judgmentNull(shipment.getAddress2()), smalltextfont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 45f));
        String order_no = shipment.getOrder_no();
        if (StringTools.isNullOrEmpty(order_no)) {
            order_no = shipment.getShipment_plan_id();
        }
        table1.addCell(createShipmentCell("注文番号：" + order_no, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 77f));


        table1.addCell(createShipmentCell("注文者：" + jsonObject.getString("order_name"), smalltextfont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
        table1.addCell(createShipmentCell("配送方法：" + replaceNull(jsonObject.getString("method")), smalltextfont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 77f));

        // 住所１
        String masterAdd = "";
        if (!StringTools.isNullOrEmpty(jsonObject.getString("order_todoufuken"))) {
            masterAdd += jsonObject.getString("order_todoufuken")
                + " ";
        }
        if (!StringTools.isNullOrEmpty(jsonObject.getString("order_address1"))) {
            masterAdd += jsonObject.getString("order_address1");
        }
        String order_zip = "";
        if (!StringTools.isNullOrEmpty(jsonObject.getString("order_zip_code1"))) {
            order_zip += jsonObject.getString("order_zip_code1");
        }
        if (!StringTools.isNullOrEmpty(jsonObject.getString("order_zip_code2"))) {
            order_zip += jsonObject.getString("order_zip_code2");
        }
        if (!StringTools.isNullOrEmpty(order_zip)) {
            order_zip = "〒" + order_zip;
        }
        table1.addCell(
            createShipmentCell(order_zip + " " + masterAdd, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));


        Timestamp delivery_plan_date = shipment.getDelivery_date();
        String deliveryPlanDate = "ー";
        if (!StringTools.isNullOrEmpty(delivery_plan_date)) {
            deliveryPlanDate = simpleDateFormat.format(delivery_plan_date);
        }
        table1.addCell(
            createShipmentCell("お届け希望日：" + deliveryPlanDate, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 64f));

        // 住所２
        table1.addCell(createShipmentCell(" " + judgmentNull(jsonObject.getString("order_address2")), smalltextfont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
        table1.addCell(createShipmentCell("お届け時間帯：" + replaceNull(jsonObject.getString("delivery_time_slot")),
            smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 64f));

        PdfPTable table2 = createTable(new float[] {
            90, 90, 90, 210, 40
        }, 520);
        table2.setHeaderRows(1);
        table2.addCell(createShipmentCell("ロケーション", textfont, 12, 1, 1.5f, 30f));
        table2.addCell(createShipmentCell("商品コード", textfont, 8, 1, 1.5f, 30f));
        table2.addCell(createShipmentCell("管理バーコード", textfont, 8, 1, 1.5f, 30f));
        table2.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
        table2.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
        JSONArray items = jsonObject.getJSONArray("items");
        int total = 0;
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemsJSONObject = items.getJSONObject(i);
            table2.addCell(
                createShipmentCell(itemsJSONObject.getString("locationName"), smallBoldFont, 13, Element.ALIGN_LEFT));
            table2.addCell(createShipmentCell(itemsJSONObject.getString("code"), textfont, 9, Element.ALIGN_LEFT));
            table2.addCell(createShipmentCell(itemsJSONObject.getString("barcode"), textfont, 9, Element.ALIGN_LEFT));
            table2.addCell(createShipmentCell(itemsJSONObject.getString("name"), smalltextfont, 9, Element.ALIGN_LEFT));
            table2.addCell(createShipmentCell(itemsJSONObject.getString("product_plan_cnt"), smallBoldFont, 9,
                Element.ALIGN_CENTER));
            total += Integer.parseInt(itemsJSONObject.getString("product_plan_cnt"));
        }
        table2.addCell(createShipmentCell(" ", textfont, 12));
        table2.addCell(createShipmentCell(" ", textfont, 12));
        table2.addCell(createShipmentCell(" ", textfont, 12));
        PdfPCell cell = createShipmentCell("数量合計", smalltextfont, 12);
        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table2.addCell(cell);
        table2.addCell(createShipmentCell(total + "", textfont, 8, Element.ALIGN_RIGHT));
        doc.add(table);
        doc.add(table1);
        doc.add(table2);
        // a判断是否有同捆物
        JSONArray bundled = jsonObject.getJSONArray("bundled");
        if (!StringTools.isNullOrEmpty(bundled) && bundled.size() != 0) {
            Paragraph p2 = new Paragraph("同梱物", headfont);
            format(p2);
            Paragraph p3 = new Paragraph(" ", textfont);
            doc.add(p2);
            doc.add(p3);
            PdfPTable table3 = createTable(new float[] {
                90, 90, 90, 210, 40
            }, 520);
            // table3.setSpacingBefore(30f);
            table3.setHeaderRows(1);
            table3.addCell(createShipmentCell("ロケーション", textfont, 12, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("商品コード", textfont, 8, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("管理バーコード", textfont, 8, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("同梱物名", textfont, 8, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));

            int bundledTotal = 0;
            for (int i = 0; i < bundled.size(); i++) {
                JSONObject json = bundled.getJSONObject(i);
                table3
                    .addCell(createShipmentCell(json.getString("locationName"), smalltextfont, 13, Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(json.getString("code"), textfont, 9, Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(json.getString("barcode"), textfont, 9, Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(json.getString("name"), smalltextfont, 9, Element.ALIGN_LEFT));
                table3.addCell(
                    createShipmentCell(json.getString("product_plan_cnt"), smalltextfont, 9, Element.ALIGN_RIGHT));
                bundledTotal += json.getInteger("product_plan_cnt");
            }
            table3.addCell(createShipmentCell(" ", textfont, 12));
            table3.addCell(createShipmentCell(" ", textfont, 12));
            table3.addCell(createShipmentCell(" ", textfont, 12));
            PdfPCell cell1 = createShipmentCell("数量合計", smalltextfont, 12);
            cell1.setVerticalAlignment(Element.ALIGN_RIGHT);
            cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table3.addCell(cell1);
            table3.addCell(createShipmentCell(bundledTotal + "", textfont, 8, Element.ALIGN_RIGHT));
            doc.add(table3);
        }
        // 判断是否含有ご請求コード
        String billBarcode = jsonObject.getString("billBarcode");
        if (!StringTools.isNullOrEmpty(billBarcode)) {
            PdfPTable table4 = createTable(new float[] {
                100, 420
            }, 520);
            table4.setSpacingBefore(30f);
            table4.addCell(createShipmentCell("GMO後払い\nご請求コード", textfont, 12, 1, 1.5f, 30f));
            table4.addCell(createShipmentCell(billBarcode, keyfont, 8, 1, 1.5f, 30f));
            doc.add(table4);
        }

        PdfPTable table5 = createTable(new float[] {
            240, 30, 250
        }, 520);
        table5.setSpacingBefore(20f);
        table5.addCell(createShipmentCell("■特記事項", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 13, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table5.addCell(createShipmentCell("■購入者備考欄 （梱包メモ）", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 13, 15f));


        String gift_wrapping_unit = shipment.getGift_wrapping_unit();
        String giftWrappingUnit = "";
        if (!StringTools.isNullOrEmpty(gift_wrapping_unit)) {
            switch (gift_wrapping_unit) {
                case "1":
                    giftWrappingUnit = "注文単位 ";
                    String giftWrappingType = shipment.getTw201_shipment_detail().get(0).getGift_wrapping_type();
                    if (!StringTools.isNullOrEmpty(giftWrappingType)) {
                        giftWrappingUnit += " " + giftWrappingType;
                    }
                    break;
                case "2":
                    giftWrappingUnit = "商品単位";
                    break;
                default:
                    break;
            }
        }
        if (!StringTools.isNullOrEmpty(giftWrappingUnit)) {
            giftWrappingUnit = "・ギフト：" + giftWrappingUnit;
        }
        table5.addCell(createShipmentCell(giftWrappingUnit, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));

        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        PdfPCell shipmentCell =
            createShipmentCell(shipment.getMemo(), smalltextfont, Element.ALIGN_LEFT, 0, 10, 0, 0, 20f);
        shipmentCell.setLeading(5f, 1f);
        table5.addCell(shipmentCell);
        String cushioning_unit = shipment.getCushioning_unit();
        String cushioningType = "";
        if (!StringTools.isNullOrEmpty(cushioning_unit)) {
            switch (cushioning_unit) {
                case "1":
                    cushioningType = "注文単位";
                    String cushioning_type = shipment.getTw201_shipment_detail().get(0).getCushioning_type();
                    if (!StringTools.isNullOrEmpty(cushioning_type)) {
                        cushioningType += " " + cushioning_type;
                    }
                    break;
                case "2":
                    cushioningType = "商品単位";
                    break;
                default:
                    break;
            }
        }
        if (!StringTools.isNullOrEmpty(cushioningType)) {
            cushioningType = "・緩衝材：" + cushioningType;
        }
        table5.addCell(createShipmentCell(cushioningType, smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

        table5.addCell(createShipmentCell(pointReplaceNull(shipment.getInstructions_special_notes()), smalltextfont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

        table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou1()), smalltextfont, Element.ALIGN_LEFT, 0,
            1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

        table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou2()), smalltextfont, Element.ALIGN_LEFT, 0,
            1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        // table10.addCell(createShipmentCell(json.getString("memo"), smalltextfont, Element.ALIGN_LEFT, 0, 6, 0, 0,
        // 20f));
        table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou3()), smalltextfont, Element.ALIGN_LEFT, 0,
            1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou4()), smalltextfont, Element.ALIGN_LEFT, 0,
            1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou5()), smalltextfont, Element.ALIGN_LEFT, 0,
            1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou6()), smalltextfont, Element.ALIGN_LEFT, 0,
            1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table5.addCell(createShipmentCell(pointReplaceNull(shipment.getBikou7()), smalltextfont, Element.ALIGN_LEFT, 0,
            1, 0, 0, 20f));
        table5.addCell(createShipmentCell("", smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        doc.add(table5);

        doc.close();
    }

    /**
     * @param jsonObject ： 数据
     * @param pdfPath ： pdf地址
     * @description: 明細・作業指示書 (新版)
     * @return: void
     * @date: 2020/12/29 15:40
     */
    public static void createNewShipmentsDetailPDF(JSONObject jsonObject, String pdfPath) throws Exception {

        StringBuilder stringBuilder = new StringBuilder();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        Document doc = new Document(PageSize.A4);
        String type = jsonObject.getString("type");
        String path = pdfPath;
        // 如果type为0 则生成A3的pdf
        if (StringTools.isNullOrEmpty(type) || "0".equals(type)) {
            path = pdfPath + "A4";
        }
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        JSONArray pageArray = new JSONArray();
        // NTM店铺标识
        Boolean isNtmFlg = jsonObject.getBoolean("isNtmFlg");

        // 如果生成的是A3的pdf(type=0)，则按原先 左边纳品书、右边作业指示书 展示
        // 如果生成的是A4的pdf(type=1)，则按照先 作业指示书、后纳品书逻辑
        if (StringTools.isNullOrEmpty(type) || "1".equals(type)) {
            for (int l = 0; l < jsonArray.size(); l++) {
                JSONObject pageJson = new JSONObject();
                stringBuilder.delete(0, stringBuilder.length());
                JSONObject json = jsonArray.getJSONObject(l);
                Integer price_on_delivery_note = json.getInteger("price_on_delivery_note");
                Tw200_shipment shipments = json.getJSONObject("shipments").toJavaObject(Tw200_shipment.class);
                // a获取当前出库依赖的详细信息
                List<Tw201_shipment_detail> detailList = shipments.getTw201_shipment_detail();

                // 作業指示書
                PdfPTable table5 = createTable(new float[] {
                    330, 190
                }, 520);
                table5.addCell(createShipmentCell("作業指示書", titlefont, Element.ALIGN_CENTER, 15, 180f));
                table5.addCell(createCell(" ", keyfont, Element.ALIGN_RIGHT, 0));
                String noteType = "";
                if ("同梱しない".equals(json.getString("delivery_note_type"))) {
                    noteType = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
                }
                table5.addCell(createCell(noteType, littleBoldFont, Element.ALIGN_LEFT, 0));
                Image picture1 = Image.getInstance(json.getString("codePath"));
                picture1.setAlignment(Image.ALIGN_CENTER);
                picture1.scalePercent(40);
                PdfPCell shipmentImgCell = createShipmentImgCell(picture1, 0, 1, 2, 0, 0f);
                shipmentImgCell.setPaddingTop(-20f);
                shipmentImgCell.setPaddingRight(10f);
                table5.addCell(shipmentImgCell);
                PdfPTable table6 = createTable(new float[] {
                    290, 290
                }, 580);
                String surname1 = json.getString("surname");
                if (StringTools.isNullOrEmpty(surname1)) {
                    surname1 = json.getString("company");
                }
                table6
                    .addCell(createShipmentCell("配送先：" + surname1, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("店舗名：" + json.getString("client_nm"), textUnderlineFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 95f));
                } else {
                    table6.addCell(createShipmentCell("店舗名：" + json.getString("client_nm"), textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 95f));
                }
                // 住所1

                String sendAdd = "";
                if (!StringTools.isNullOrEmpty(json.getString("prefecture"))) {
                    sendAdd += json.getString("prefecture")
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(json.getString("address1"))) {
                    sendAdd += json.getString("address1");
                }
                // 如果是NTM店铺，则将邮编番号换行
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("〒" + json.getString("postcode"), textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                } else {
                    table6.addCell(createShipmentCell("〒" + json.getString("postcode") + " " + sendAdd, textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                }

                String shipping_date = json.getString("shipping_date");
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("出荷予定日(納品日)：" + replaceNull(shipping_date), textUnderlineFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 40f));
                } else {
                    table6.addCell(createShipmentCell("出荷予定日(納品日)：" + replaceNull(shipping_date), textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 40f));
                }

                // 如果是NTM店铺，则将邮编番号换行
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell(sendAdd, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                    table6.addCell(createShipmentCell(" ", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 40f));
                }

                // 住所2
                table6.addCell(createShipmentCell(" " + judgmentNull(json.getString("address2")), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                String order_no = json.getString("order_no");
                if (StringTools.isNullOrEmpty(order_no)) {
                    order_no = json.getString("shipment_plan_id");
                }
                // NTM的受注番号变大加下划线
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("注文番号：" + order_no, textUnderlineFont12, Element.ALIGN_LEFT, 0, 1,
                        0, 0, 86f));
                } else {
                    table6.addCell(
                        createShipmentCell("注文番号：" + order_no, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 86f));
                }


                table6.addCell(createShipmentCell("注文者：" + json.getString("order_name"), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("配送方法：" + replaceNull(json.getString("method")),
                        textUnderlineFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 86f));
                } else {
                    table6.addCell(createShipmentCell("配送方法：" + replaceNull(json.getString("method")), textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 86f));
                }

                // 住所１
                String masterAdd = "";
                if (!StringTools.isNullOrEmpty(json.getString("order_todoufuken"))) {
                    masterAdd += json.getString("order_todoufuken")
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(json.getString("order_address1"))) {
                    masterAdd += json.getString("order_address1");
                }
                String order_zip_code = "";
                if (!StringTools.isNullOrEmpty(json.getString("order_zip_code1"))) {
                    order_zip_code += json.getString("order_zip_code1");
                }
                if (!StringTools.isNullOrEmpty(json.getString("order_zip_code2"))) {
                    order_zip_code += json.getString("order_zip_code2");
                }
                if (!StringTools.isNullOrEmpty(order_zip_code)) {
                    order_zip_code = "〒" + order_zip_code;
                }
                if (isNtmFlg) {
                    table6
                        .addCell(createShipmentCell(order_zip_code, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                } else {
                    table6.addCell(createShipmentCell(order_zip_code + " " + masterAdd, textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                }

                String deliveryPlanDate = json.getString("delivery_date");
                if (StringTools.isNullOrEmpty(deliveryPlanDate)) {
                    deliveryPlanDate = "ー";
                }
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("お届け希望日：" + deliveryPlanDate, textUnderlineFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                } else {
                    table6.addCell(createShipmentCell("お届け希望日：" + deliveryPlanDate, textBoldFont, Element.ALIGN_LEFT, 0,
                        1, 0, 0, 70f));
                }

                // 住所２
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell(masterAdd, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                } else {
                    table6.addCell(createShipmentCell(" " + judgmentNull(json.getString("order_address2")),
                        textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                }

                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("お届け時間帯：" + replaceNull(json.getString("delivery_time_slot")),
                        textUnderlineFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                } else {
                    table6.addCell(createShipmentCell("お届け時間帯：" + replaceNull(json.getString("delivery_time_slot")),
                        textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                }

                if (isNtmFlg) {
                    table6.addCell(createShipmentCell(" " + judgmentNull(json.getString("order_address2")),
                        textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                    table6.addCell(createShipmentCell(" ", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                }

                PdfPTable table7 = createTable(new float[] {
                    90, 90, 210, 90, 40
                }, 520);

                table7.setHeaderRows(1);

                table7.addCell(createShipmentCell("ロケーション", textBoldFont, 12, 1, 1.5f, 30f));
                table7.addCell(createShipmentCell("商品コード", textBoldFont, 8, 1, 1.5f, 30f));
                table7.addCell(createShipmentCell("商品名", textBoldFont, 8, 1, 1.5f, 30f));
                table7.addCell(createShipmentCell("備考", textBoldFont, 8, 1, 1.5f, 30f));
                table7.addCell(createShipmentCell("数量", textBoldFont, 8, 1, 1.5f, 30f));
                JSONArray setItems = json.getJSONArray("setItems");
                Integer sku = 0;
                for (int i = 0; i < setItems.size(); i++) {
                    JSONObject itemsJSONObject = setItems.getJSONObject(i);

                    table7.addCell(createShipmentCell(itemsJSONObject.getString("locationName"), smallBoldFont, 13,
                        Element.ALIGN_LEFT));
                    table7.addCell(
                        createShipmentCell(itemsJSONObject.getString("code"), textBoldFont, 9, Element.ALIGN_LEFT));
                    table7.addCell(
                        createShipmentCell(itemsJSONObject.getString("name"), textBoldFont, 9, Element.ALIGN_LEFT));
                    table7.addCell(
                        createShipmentCell(itemsJSONObject.getString("ntm_memo"), textBoldFont, 9, Element.ALIGN_LEFT));
                    table7.addCell(createShipmentCell(itemsJSONObject.getString("product_plan_cnt"), textBoldFont, 9, 5,
                        1.5f, 30f));
                    sku += itemsJSONObject.getInteger("product_plan_cnt");

                }
                table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                PdfPCell pdfPCell = createShipmentCell("数量合計", textBoldFont, 12);
                pdfPCell.setVerticalAlignment(Element.ALIGN_RIGHT);
                pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table7.addCell(pdfPCell);
                table7.addCell(createShipmentCell(sku + "", textBoldFont, 8, Element.ALIGN_RIGHT));
                doc.add(table5);
                doc.add(table6);
                doc.add(table7);
                // a判断是否有同捆物
                JSONArray bundled = json.getJSONArray("bundled");
                if (bundled.size() != 0) {
                    Paragraph p2 = new Paragraph("同梱物", headBoldfont);
                    format(p2);
                    Paragraph p3 = new Paragraph(" ", textfont);
                    doc.add(p2);
                    doc.add(p3);
                    PdfPTable table8 = createTable(new float[] {
                        90, 90, 210, 90, 40
                    }, 520);
                    // table8.setSpacingBefore(30f);
                    table8.setHeaderRows(1);
                    table8.addCell(createShipmentCell("ロケーション", textBoldFont, 12, 1, 1.5f, 30f));
                    table8.addCell(createShipmentCell("商品コード", textBoldFont, 8, 1, 1.5f, 30f));
                    table8.addCell(createShipmentCell("同梱物名", textBoldFont, 8, 1, 1.5f, 30f));
                    table8.addCell(createShipmentCell("備考", textBoldFont, 8, 1, 1.5f, 30f));
                    table8.addCell(createShipmentCell("数量", textBoldFont, 8, 1, 1.5f, 30f));
                    int bundledTotal = 0;
                    for (int i = 0; i < bundled.size(); i++) {
                        JSONObject jsonParam = bundled.getJSONObject(i);
                        table8.addCell(createShipmentCell(jsonParam.getString("locationName"), smallBoldFont, 13,
                            Element.ALIGN_LEFT));
                        table8.addCell(
                            createShipmentCell(jsonParam.getString("code"), textBoldFont, 9, Element.ALIGN_LEFT));
                        table8.addCell(
                            createShipmentCell(jsonParam.getString("name"), textBoldFont, 9, Element.ALIGN_LEFT));
                        table8.addCell(
                            createShipmentCell(jsonParam.getString("ntm_memo"), textBoldFont, 9, Element.ALIGN_LEFT));
                        table8.addCell(createShipmentCell(jsonParam.getString("product_plan_cnt"), textBoldFont, 9,
                            Element.ALIGN_RIGHT));

                        bundledTotal += jsonParam.getInteger("product_plan_cnt");
                    }
                    table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                    table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                    table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                    PdfPCell cell1 = createShipmentCell("数量合計", textBoldFont, 12);
                    cell1.setVerticalAlignment(Element.ALIGN_RIGHT);
                    cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table8.addCell(cell1);
                    table8.addCell(createShipmentCell(bundledTotal + "", textBoldFont, 8, Element.ALIGN_RIGHT));
                    doc.add(table8);
                }
                // 判断是否有ご請求コード
                String billBarcode = json.getString("billBarcode");
                if (!StringTools.isNullOrEmpty(billBarcode)) {
                    PdfPTable table9 = createTable(new float[] {
                        100, 420
                    }, 520);
                    table9.setSpacingBefore(30f);
                    table9.addCell(createShipmentCell("GMO後払い\nご請求コード", textBoldFont, 12, 1, 1.5f, 30f));
                    table9.addCell(createShipmentCell(billBarcode, textBoldFont, 8, 1, 1.5f, 30f));
                    doc.add(table9);
                }

                PdfPTable table10 = createTable(new float[] {
                    240, 30, 250
                }, 520);
                table10.setSpacingBefore(20f);
                table10.addCell(createShipmentCell("■特記事項", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 13, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10
                    .addCell(createShipmentCell("■購入者備考欄 （梱包メモ）", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 13, 15f));


                String gift_wrapping_unit = shipments.getGift_wrapping_unit();
                String giftWrappingUnit = "";
                if (!StringTools.isNullOrEmpty(gift_wrapping_unit)) {
                    switch (gift_wrapping_unit) {
                        case "1":
                            giftWrappingUnit = "注文単位 ";
                            String giftWrappingType =
                                shipments.getTw201_shipment_detail().get(0).getGift_wrapping_type();
                            if (!StringTools.isNullOrEmpty(giftWrappingType)) {
                                giftWrappingUnit += " " + giftWrappingType;
                            }
                            break;
                        case "2":
                            giftWrappingUnit = "商品単位";
                            break;
                        default:
                            break;
                    }
                }
                if (!StringTools.isNullOrEmpty(giftWrappingUnit)) {
                    giftWrappingUnit = "・ギフト：" + giftWrappingUnit;
                }
                table10
                    .addCell(createShipmentCell(giftWrappingUnit, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));

                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                PdfPCell shipmentCell =
                    createShipmentCell(shipments.getMemo(), textBoldFont, Element.ALIGN_LEFT, 0, 10, 0, 0, 20f);
                shipmentCell.setLeading(5f, 1f);
                table10.addCell(shipmentCell);
                String cushioning_unit = shipments.getCushioning_unit();
                String cushioningType = "";
                if (!StringTools.isNullOrEmpty(cushioning_unit)) {
                    switch (cushioning_unit) {
                        case "1":
                            cushioningType = "注文単位";
                            String cushioning_type = shipments.getTw201_shipment_detail().get(0).getCushioning_type();
                            if (!StringTools.isNullOrEmpty(cushioning_type)) {
                                cushioningType += " " + cushioning_type;
                            }
                            break;
                        case "2":
                            cushioningType = "商品単位";
                            break;
                        default:
                            break;
                    }
                }
                if (!StringTools.isNullOrEmpty(cushioningType)) {
                    cushioningType = "・緩衝材：" + cushioningType;
                }
                table10.addCell(createShipmentCell(cushioningType, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getInstructions_special_notes()),
                    textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou1()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou2()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                // table10.addCell(createShipmentCell(json.getString("memo"), smalltextfont, Element.ALIGN_LEFT, 0, 6,
                // 0, 0, 20f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou3()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou4()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou5()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou6()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou7()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                doc.add(table10);

                int pageNumber = writer.getPageNumber();

                // 作業指示書 的页数
                int pageNumber2 = writer.getPageNumber();

                pageJson.put("pageNumber2", pageNumber2);
                pageArray.add(pageJson);
                doc.newPage();

                PdfPTable table = createTable(new float[] {
                    135, 245, 190
                }, 580);

                String logo = json.getString("detail_logo");
                try {
                    Image image = Image.getInstance(logo);
                    image.setAlignment(Image.ALIGN_CENTER);
                    image.scalePercent(40);
                    table.addCell(createShipmentImgCell(image, 0, 1, 2, 0, 30f));
                } catch (Exception e) {
                    table.addCell(createShipmentCell(" ", titlefont, Element.ALIGN_MIDDLE, 15, 30f));
                }
                String value = "                   納品書";
                PdfPCell c = createCell(value, titlefont, Element.ALIGN_LEFT, 0);
                c.setPaddingTop(-30f);
                table.addCell(c);
                Image picture = Image.getInstance(json.getString("codePath"));
                picture.setAlignment(Image.ALIGN_CENTER);
                picture.scalePercent(40);
                table.addCell(createShipmentImgCell(picture, 0, 1, 2, 0, 0f));
                PdfPTable table1 = createTable(new float[] {
                    300, 330
                }, 580);
                table1.setSpacingAfter(5f);
                String company = json.getString("company");
                String surname = json.getString("surname");
                String division = json.getString("division");
                // 部署null判断
                if (StringTools.isNullOrEmpty(division)) {
                    division = " ";
                }
                // 拼接部署
                if (!StringTools.isNullOrEmpty(company)) {
                    company += " " + division;
                } else {
                    company = division;
                }
                if (StringTools.isNullOrEmpty(surname) && !StringTools.isNullOrEmpty(company)) {
                    company += "  御中";
                }
                if (!StringTools.isNullOrEmpty(surname)) {
                    surname += " 様";
                }
                table1.addCell(createCell(judgmentNull(company), smallBoldFont, Element.ALIGN_LEFT, 0));
                String orderNo = json.getString("order_no");
                if (StringTools.isNullOrEmpty(orderNo)) {
                    orderNo = json.getString("shipment_plan_id");
                }
                table1.addCell(createCell("注文日付：" + isNullOrEmpty(json.getString("order_datetime")), smalltextfont,
                    Element.ALIGN_LEFT, 0));
                table1.addCell(createCell(judgmentNull(surname), smallBoldFont, Element.ALIGN_LEFT, 0));
                table1.addCell(createCell("注文番号：" + orderNo, smalltextfont, Element.ALIGN_LEFT, 0));

                PdfPTable table2 = createTable(new float[] {
                    240, 338
                }, 520);

                if (!StringTools.isNullOrEmpty(json.getString("gift_sender_name"))) {
                    table2.addCell(
                        createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 2, 30, 1, 5f));
                    table2.addCell(createShipmentCell("贈 り 主 ：" + isNullOrEmpty(json.getString("gift_sender_name")),
                        smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                } else {
                    table2.addCell(
                        createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 1, 5f));
                }
                table2.addCell(createShipmentCell(isNullOrEmpty(json.getString("name")), textfont, Element.ALIGN_LEFT,
                    0, 1, 15, 0, 65f));
                table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
                table2.addCell(createShipmentCell("〒" + isNullOrEmpty(json.getString("masterPostcode")), textfont,
                    Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));

                String delivery_note_type = "";
                if ("同梱しない".equals(json.getString("delivery_note_type"))) {
                    delivery_note_type = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
                }
                table2.addCell(
                    createShipmentCell(delivery_note_type, littleBoldFont, Element.ALIGN_LEFT, 0, 2, 30, 0, 5f));

                // table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
                String add = "";
                if (!StringTools.isNullOrEmpty(json.getString("masterPrefecture"))) {
                    add += json.getString("masterPrefecture")
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(json.getString("masterAddress1"))) {
                    add += json.getString("masterAddress1");
                }
                table2.addCell(createShipmentCell(add, textfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
                // table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
                table2.addCell(createShipmentCell(judgmentNull(json.getString("masterAddress2")), textfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(0);
                cell.setVerticalAlignment(0);
                cell.setColspan(0);
                cell.setRowspan(1);
                cell.setFixedHeight(20);
                String string = json.getString("total_amount");
                String param1 = "合計金額" + "    " + "¥" + formatrPrict(json.getString("total_amount"));
                if (string.length() > 7) {
                    param1 += "       (税込)";
                } else {
                    param1 += "            (税込)";
                }
                cell.setPhrase(new Phrase(param1, smallfont));
                cell.setPaddingLeft(10f);
                cell.disableBorderSide(13);
                String contact = json.getString("contact");
                if (!contact.equals("0")) {
                    // 电话
                    String substring2 = contact.substring(0, 1);
                    String sponsorPhone = (substring2.equals("1")) ? "TEL : " + json.getString("sponsorPhone") : "";
                    // fax
                    String substring1 = contact.substring(1, 2);
                    String sponsorFax = (substring1.equals("1")) ? "FAX : " + json.getString("sponsorFax") : "";
                    // 邮箱
                    String substring = contact.substring(2, 3);
                    String sponsorEmail = (substring.equals("1")) ? "Mail : " + json.getString("sponsorEmail") : "";
                    // 集合中去掉空的字符串
                    List<String> list = Stream.of(sponsorPhone, sponsorFax, sponsorEmail)
                        .filter(String -> !String.isEmpty()).collect(Collectors.toList());
                    if (list.size() == 0) {
                        if (price_on_delivery_note == 1) {
                            table2.addCell(cell);
                            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        }
                    } else {
                        table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                        table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                        table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        table2.addCell(
                            createShipmentCell("購入内容に関するお問い合わせ先", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        int count = 0;
                        for (String param : list) {
                            if (count == list.size() - 1) {
                                if (price_on_delivery_note == 1) {
                                    table2.addCell(cell);
                                } else {
                                    table2.addCell(
                                        createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                                }
                            } else {
                                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                            }
                            table2.addCell(createShipmentCell(param, textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                            count++;
                        }
                    }
                } else {
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    if (price_on_delivery_note == 1) {
                        table2.addCell(cell);
                        table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    }
                }
                doc.add(table);
                doc.add(table1);
                doc.add(table2);
                if (price_on_delivery_note == 1) {
                    PdfPTable table3 = createTable(new float[] {
                        80, 200, 40, 80, 90, 30
                    }, 520);
                    table3.setSpacingBefore(2f);
                    table3.setHeaderRows(1);
                    table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                    String productTax = json.getString("product_tax");
                    String param = "";
                    if (!StringTools.isNullOrEmpty(productTax)) {
                        param = "(" + productTax + ")";
                    }
                    table3.addCell(createShipmentCell("単価" + param, textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("金額 (税込)", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("税率", textfont, 8, 1, 1.5f, 30f));
                    JSONArray setProductJson = json.getJSONArray("setProductJson");
                    int size = setProductJson.size();
                    for (int i = 0; i < setProductJson.size(); i++) {
                        JSONObject itemsJSONObject = setProductJson.getJSONObject(i);
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")),
                            smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                        String is_reduced_tax = itemsJSONObject.getString("is_reduced_tax");
                        String isReducedTax = "10%";
                        String mark = "";
                        if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                            isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                                : "10%";
                            if ("1".equals(is_reduced_tax)) {
                                mark = "※ ";
                            }
                        }
                        String taxFlag = itemsJSONObject.getString("tax_flag");
                        if ("3".equals(taxFlag) || "非課税".equals(taxFlag)) {
                            isReducedTax = "0%";
                            mark = "";
                        }
                        String name = isNullOrEmpty(itemsJSONObject.getString("name"));
                        size += (int) Math.ceil((double) name.length() / (double) 24) - 1;
                        table3.addCell(createShipmentCell(mark + isNullOrEmpty(itemsJSONObject.getString("name")),
                            smalltextfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                            smalltextfont, 9, Element.ALIGN_RIGHT));
                        table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("unit_price")),
                            smalltextfont, 9, Element.ALIGN_RIGHT));
                        table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("price")),
                            smalltextfont, 9, Element.ALIGN_RIGHT));

                        if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                            isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                                : "10%";
                        }
                        String taxFlag1 = itemsJSONObject.getString("tax_flag");
                        if ("3".equals(taxFlag1) || "非課税".equals(taxFlag1)) {
                            isReducedTax = "0%";
                        }
                        table3.addCell(createShipmentCell(isReducedTax, smalltextfont, 9));
                    }
                    if (size < 8) {
                        for (int i = 0; i < 10 - size; i++) {
                            table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        }
                    }
                    if (size > 33 && size < 45) {
                        for (int i = 0; i < 46 - size; i++) {
                            table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        }
                    }
                    if (size > 45) {
                        int num = size - 43;
                        if (num > 66 && num < 78) {
                            for (int i = 0; i < 79 - num; i++) {
                                table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            }
                        }
                    }
                    table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                    table3.addCell(createShipmentCell("　※は軽減税率対象", smalltextfont, 9, Element.ALIGN_LEFT));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    PdfPTable table4 = createTable(new float[] {
                        320, 80, 90, 30
                    }, 520);
                    table4.setKeepTogether(true);
                    String payment_method = json.getString("payment_method");
                    if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
                        table4.addCell(createShipmentCell(judgmentNull(json.getString("detail_message")), smalltextfont,
                            Element.ALIGN_LEFT, 13, 10, 220, 0f, 4, 1f, 5f));
                    } else {
                        table4.addCell(createShipmentCell(judgmentNull(json.getString("detail_message")), smalltextfont,
                            Element.ALIGN_LEFT, 13, 9, 195, 0f, 4, 1f, 5f));
                    }
                    table4.addCell(createShipmentCell("商品合計 (税込)", textfont, 13, 1, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell("¥" + formatrPrict(json.getString("subtotal_amount")),
                        smalltextfont, 9, 1, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 14, 1, 1f, 25f));
                    table4.addCell(createShipmentCell("送料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    String deliveryCharge = json.getString("delivery_charge");
                    if (StringTools.isNullOrEmpty(deliveryCharge)) {
                        deliveryCharge = "0";
                    }
                    table4.addCell(createShipmentCell("¥" + formatrPrict(deliveryCharge), smalltextfont, 9, 0, 0f, 25f,
                        Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

                    table4.addCell(createShipmentCell("手数料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    String handlingCharge = json.getString("handling_charge");
                    if (StringTools.isNullOrEmpty(handlingCharge)) {
                        handlingCharge = "0";
                    }
                    table4.addCell(createShipmentCell("¥" + formatrPrict(handlingCharge), smalltextfont, 9, 0, 0f, 25f,
                        Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

                    table4.addCell(createShipmentCell("割引額", textfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT, 1));

                    String discount_amount = json.getString("discount_amount");
                    if (StringTools.isNullOrEmpty(discount_amount)) {
                        discount_amount = "0";
                    }
                    table4.addCell(createShipmentCell("¥" + formatrPrict(discount_amount), smalltextfont, 9, 0, 0f, 15f,
                        Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT, 2));

                    table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 0f, 15f, Element.ALIGN_BOTTOM,
                        Element.ALIGN_RIGHT, 1));

                    PdfPCell cell2 = createShipmentCell("(ポイント、クーポン含む) ", smalltextfont, 13, 0, 1f, 10f,
                        Element.ALIGN_MIDDLE, Element.ALIGN_LEFT, 1);
                    cell2.setPaddingTop(-5);
                    table4.addCell(cell2);

                    table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 0f, 10f, Element.ALIGN_BOTTOM,
                        Element.ALIGN_RIGHT, 1));

                    table4.addCell(createShipmentCell("合計 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell("¥" + formatrPrict(json.getString("total_amount")), smalltextfont,
                        9, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));
                    if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
                        table4.addCell(createShipmentCell("支払方法", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE,
                            Element.ALIGN_LEFT));
                        table4.addCell(createShipmentCell(isNullOrEmpty(json.getString("payment_method_name")),
                            smalltextfont, 9, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                        table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));

                        table4.addCell(createShipmentCell("支払総計 (税込)", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE,
                            Element.ALIGN_LEFT));
                        table4.addCell(
                            createShipmentCell("¥" + formatrPrict(json.getString("total_for_cash_on_delivery")),
                                smalltextfont, 9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                        table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
                    } else {
                        table4.addCell(createShipmentCell("支払方法", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE,
                            Element.ALIGN_LEFT));
                        table4.addCell(createShipmentCell(isNullOrEmpty(json.getString("payment_method_name")),
                            smalltextfont, 9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                        table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
                    }
                    table4.addCell(
                        createShipmentCell("10%対象   " + "¥" + formatrPrict(json.getString("total_with_normal_tax")),
                            smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(
                        createShipmentCell("うち消費税  " + "¥" + formatrPrict(json.getString("totalWithNormalTaxPrice")),
                            smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_LEFT));

                    table4.addCell(
                        createShipmentCell(" 8%対象   " + "¥" + formatrPrict(json.getString("total_with_reduced_tax")),
                            smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(
                        createShipmentCell("うち消費税  " + "¥" + formatrPrict(json.getString("totalWithReducedTaxPrice")),
                            smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_LEFT));
                    doc.add(table3);
                    doc.add(table4);
                } else {
                    PdfPTable table3 = createTable(new float[] {
                        100, 360, 60
                    }, 520);
                    table3.setHeaderRows(1);
                    table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                    JSONArray items = json.getJSONArray("setProductJson");
                    for (int i = 0; i < items.size(); i++) {
                        JSONObject itemsJSONObject = items.getJSONObject(i);
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")),
                            smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("name")),
                            smalltextfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                            smalltextfont, 9, Element.ALIGN_RIGHT));
                    }
                    if (items.size() < 10) {
                        for (int i = 0; i < (10 - items.size()); i++) {
                            table3.addCell(createShipmentCell(" ", smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                        }
                    }
                    PdfPTable table4 = createTable(new float[] {
                        520
                    }, 520);
                    table4.setKeepTogether(true);
                    PdfPCell detail_message = createShipmentCell(judgmentNull(json.getString("detail_message")),
                        smalltextfont, 12, Element.ALIGN_LEFT);
                    detail_message.setBorderWidthTop(1f);
                    detail_message.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    detail_message.setVerticalAlignment(Element.ALIGN_LEFT);
                    detail_message.setBorderWidthBottom(1.5f);
                    table4.addCell(detail_message);
                    doc.add(table3);
                    if (!StringTools.isNullOrEmpty(json.getString("detail_message"))) {
                        doc.add(table4);
                    }
                }
                // 纳品书的页数
                int pageNumber1 = pageNumber - pageNumber2;
                pageJson.put("pageNumber1", pageNumber1);

                doc.newPage();

            }
        } else {
            for (int l = 0; l < jsonArray.size(); l++) {
                JSONObject pageJson = new JSONObject();

                stringBuilder.delete(0, stringBuilder.length());
                JSONObject json = jsonArray.getJSONObject(l);
                Integer price_on_delivery_note = json.getInteger("price_on_delivery_note");
                Tw200_shipment shipments = json.getJSONObject("shipments").toJavaObject(Tw200_shipment.class);
                // a获取当前出库依赖的详细信息
                List<Tw201_shipment_detail> detailList = shipments.getTw201_shipment_detail();

                PdfPTable table = createTable(new float[] {
                    135, 245, 190
                }, 580);

                String logo = json.getString("detail_logo");
                try {
                    Image image = Image.getInstance(logo);
                    image.setAlignment(Image.ALIGN_CENTER);
                    image.scalePercent(40);
                    table.addCell(createShipmentImgCell(image, 0, 1, 2, 0, 30f));
                } catch (Exception e) {
                    table.addCell(createShipmentCell(" ", titlefont, Element.ALIGN_MIDDLE, 15, 30f));
                }
                String value = "                   納品書";
                PdfPCell c = createCell(value, titlefont, Element.ALIGN_LEFT, 0);
                c.setPaddingTop(-30f);
                table.addCell(c);
                Image picture = Image.getInstance(json.getString("codePath"));
                picture.setAlignment(Image.ALIGN_CENTER);
                picture.scalePercent(40);
                table.addCell(createShipmentImgCell(picture, 0, 1, 2, 0, 0f));
                PdfPTable table1 = createTable(new float[] {
                    300, 330
                }, 580);
                table1.setSpacingAfter(5f);
                String company = json.getString("company");
                String surname = json.getString("surname");
                String division = json.getString("division");
                // 部署null判断
                if (StringTools.isNullOrEmpty(division)) {
                    division = " ";
                }
                // 拼接部署
                if (!StringTools.isNullOrEmpty(company)) {
                    company += " " + division;
                } else {
                    company = division;
                }
                if (StringTools.isNullOrEmpty(surname) && !StringTools.isNullOrEmpty(company)) {
                    company += "  御中";
                }
                if (!StringTools.isNullOrEmpty(surname)) {
                    surname += " 様";
                }
                table1.addCell(createCell(judgmentNull(company), smallBoldFont, Element.ALIGN_LEFT, 0));
                String orderNo = json.getString("order_no");
                if (StringTools.isNullOrEmpty(orderNo)) {
                    orderNo = json.getString("shipment_plan_id");
                }
                table1.addCell(createCell("注文日付：" + isNullOrEmpty(json.getString("order_datetime")), smalltextfont,
                    Element.ALIGN_LEFT, 0));
                table1.addCell(createCell(judgmentNull(surname), smallBoldFont, Element.ALIGN_LEFT, 0));
                table1.addCell(createCell("注文番号：" + orderNo, smalltextfont, Element.ALIGN_LEFT, 0));

                PdfPTable table2 = createTable(new float[] {
                    240, 338
                }, 520);

                if (!StringTools.isNullOrEmpty(json.getString("gift_sender_name"))) {
                    table2.addCell(
                        createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 2, 30, 1, 5f));
                    table2.addCell(createShipmentCell("贈 り 主 ：" + isNullOrEmpty(json.getString("gift_sender_name")),
                        smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                } else {
                    table2.addCell(
                        createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 1, 5f));
                }
                table2.addCell(createShipmentCell(isNullOrEmpty(json.getString("name")), textfont, Element.ALIGN_LEFT,
                    0, 1, 15, 0, 65f));
                table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
                table2.addCell(createShipmentCell("〒" + isNullOrEmpty(json.getString("masterPostcode")), textfont,
                    Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));

                String delivery_note_type = "";
                if ("同梱しない".equals(json.getString("delivery_note_type"))) {
                    delivery_note_type = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
                }
                table2.addCell(
                    createShipmentCell(delivery_note_type, littleBoldFont, Element.ALIGN_LEFT, 0, 2, 30, 0, 5f));

                // table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
                String add = "";
                if (!StringTools.isNullOrEmpty(json.getString("masterPrefecture"))) {
                    add += json.getString("masterPrefecture")
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(json.getString("masterAddress1"))) {
                    add += json.getString("masterAddress1");
                }
                table2.addCell(createShipmentCell(add, textfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
                // table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
                table2.addCell(createShipmentCell(judgmentNull(json.getString("masterAddress2")), textfont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(0);
                cell.setVerticalAlignment(0);
                cell.setColspan(0);
                cell.setRowspan(1);
                cell.setFixedHeight(20);
                String string = json.getString("total_amount");
                String param1 = "合計金額" + "    " + "¥" + formatrPrict(json.getString("total_amount"));
                if (string.length() > 7) {
                    param1 += "       (税込)";
                } else {
                    param1 += "            (税込)";
                }
                cell.setPhrase(new Phrase(param1, smallfont));
                cell.setPaddingLeft(10f);
                cell.disableBorderSide(13);
                String contact = json.getString("contact");
                if (!contact.equals("0")) {
                    // 电话
                    String substring2 = contact.substring(0, 1);
                    String sponsorPhone = (substring2.equals("1")) ? "TEL : " + json.getString("sponsorPhone") : "";
                    // fax
                    String substring1 = contact.substring(1, 2);
                    String sponsorFax = (substring1.equals("1")) ? "FAX : " + json.getString("sponsorFax") : "";
                    // 邮箱
                    String substring = contact.substring(2, 3);
                    String sponsorEmail = (substring.equals("1")) ? "Mail : " + json.getString("sponsorEmail") : "";
                    // 集合中去掉空的字符串
                    List<String> list = Stream.of(sponsorPhone, sponsorFax, sponsorEmail)
                        .filter(String -> !String.isEmpty()).collect(Collectors.toList());
                    if (list.size() == 0) {
                        if (price_on_delivery_note == 1) {
                            table2.addCell(cell);
                            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        }
                    } else {
                        table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                        table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                        table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        table2.addCell(
                            createShipmentCell("購入内容に関するお問い合わせ先", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        int count = 0;
                        for (String param : list) {
                            if (count == list.size() - 1) {
                                if (price_on_delivery_note == 1) {
                                    table2.addCell(cell);
                                } else {
                                    table2.addCell(
                                        createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                                }
                            } else {
                                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                            }
                            table2.addCell(createShipmentCell(param, textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                            count++;
                        }
                    }
                } else {
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    if (price_on_delivery_note == 1) {
                        table2.addCell(cell);
                        table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    }
                }
                doc.add(table);
                doc.add(table1);
                doc.add(table2);
                if (price_on_delivery_note == 1) {
                    PdfPTable table3 = createTable(new float[] {
                        80, 200, 40, 80, 90, 30
                    }, 520);
                    table3.setSpacingBefore(2f);
                    table3.setHeaderRows(1);
                    table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                    String productTax = json.getString("product_tax");
                    String param = "";
                    if (!StringTools.isNullOrEmpty(productTax)) {
                        param = "(" + productTax + ")";
                    }
                    table3.addCell(createShipmentCell("単価" + param, textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("金額 (税込)", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("税率", textfont, 8, 1, 1.5f, 30f));
                    JSONArray setProductJson = json.getJSONArray("setProductJson");
                    int size = setProductJson.size();
                    for (int i = 0; i < setProductJson.size(); i++) {
                        JSONObject itemsJSONObject = setProductJson.getJSONObject(i);
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")),
                            smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                        String is_reduced_tax = itemsJSONObject.getString("is_reduced_tax");
                        String isReducedTax = "10%";
                        String mark = "";
                        if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                            isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                                : "10%";
                            if ("1".equals(is_reduced_tax)) {
                                mark = "※ ";
                            }
                        }
                        String taxFlag = itemsJSONObject.getString("tax_flag");
                        if ("3".equals(taxFlag) || "非課税".equals(taxFlag)) {
                            isReducedTax = "0%";
                            mark = "";
                        }
                        String name = isNullOrEmpty(itemsJSONObject.getString("name"));
                        size += (int) Math.ceil((double) name.length() / (double) 24) - 1;
                        table3.addCell(createShipmentCell(mark + isNullOrEmpty(itemsJSONObject.getString("name")),
                            smalltextfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                            smalltextfont, 9, Element.ALIGN_RIGHT));
                        table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("unit_price")),
                            smalltextfont, 9, Element.ALIGN_RIGHT));
                        table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("price")),
                            smalltextfont, 9, Element.ALIGN_RIGHT));

                        if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                            isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                                : "10%";
                        }
                        String taxFlag1 = itemsJSONObject.getString("tax_flag");
                        if ("3".equals(taxFlag1) || "非課税".equals(taxFlag1)) {
                            isReducedTax = "0%";
                        }
                        table3.addCell(createShipmentCell(isReducedTax, smalltextfont, 9));
                    }
                    if (size < 8) {
                        for (int i = 0; i < 10 - size; i++) {
                            table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        }
                    }
                    if (size > 33 && size < 45) {
                        for (int i = 0; i < 46 - size; i++) {
                            table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        }
                    }
                    if (size > 45) {
                        int num = size - 43;
                        if (num > 66 && num < 78) {
                            for (int i = 0; i < 79 - num; i++) {
                                table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                                table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                            }
                        }
                    }
                    table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                    table3.addCell(createShipmentCell("　※は軽減税率対象", smalltextfont, 9, Element.ALIGN_LEFT));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    PdfPTable table4 = createTable(new float[] {
                        320, 80, 90, 30
                    }, 520);
                    table4.setKeepTogether(true);
                    String payment_method = json.getString("payment_method");
                    if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
                        table4.addCell(createShipmentCell(judgmentNull(json.getString("detail_message")), smalltextfont,
                            Element.ALIGN_LEFT, 13, 9, 210, 0f, 4, 1f, 5f));
                    } else {
                        table4.addCell(createShipmentCell(judgmentNull(json.getString("detail_message")), smalltextfont,
                            Element.ALIGN_LEFT, 13, 8, 185, 0f, 4, 1f, 5f));
                    }
                    table4.addCell(createShipmentCell("商品合計 (税込)", textfont, 13, 1, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell("¥" + formatrPrict(json.getString("subtotal_amount")),
                        smalltextfont, 9, 1, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 14, 1, 1f, 25f));
                    table4.addCell(createShipmentCell("送料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    String deliveryCharge = json.getString("delivery_charge");
                    if (StringTools.isNullOrEmpty(deliveryCharge)) {
                        deliveryCharge = "0";
                    }
                    table4.addCell(createShipmentCell("¥" + formatrPrict(deliveryCharge), smalltextfont, 9, 0, 0f, 25f,
                        Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

                    table4.addCell(createShipmentCell("手数料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    String handlingCharge = json.getString("handling_charge");
                    if (StringTools.isNullOrEmpty(handlingCharge)) {
                        handlingCharge = "0";
                    }
                    table4.addCell(createShipmentCell("¥" + formatrPrict(handlingCharge), smalltextfont, 9, 0, 0f, 25f,
                        Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));


                    table4.addCell(
                        createShipmentCell("割引額", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    String discount_amount = json.getString("discount_amount");
                    if (StringTools.isNullOrEmpty(discount_amount)) {
                        discount_amount = "0";
                    }
                    table4.addCell(createShipmentCell("¥" + formatrPrict(discount_amount), smalltextfont, 9, 0, 0f, 25f,
                        Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

                    table4.addCell(createShipmentCell("合計 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE,
                        Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell("¥" + formatrPrict(json.getString("total_amount")), smalltextfont,
                        9, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));
                    if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
                        table4.addCell(createShipmentCell("支払方法", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE,
                            Element.ALIGN_LEFT));
                        table4.addCell(createShipmentCell(isNullOrEmpty(json.getString("payment_method_name")),
                            smalltextfont, 9, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                        table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));

                        table4.addCell(createShipmentCell("支払総計 (税込)", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE,
                            Element.ALIGN_LEFT));
                        table4.addCell(
                            createShipmentCell("¥" + formatrPrict(json.getString("total_for_cash_on_delivery")),
                                smalltextfont, 9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                        table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
                    } else {
                        table4.addCell(createShipmentCell("支払方法", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE,
                            Element.ALIGN_LEFT));
                        table4.addCell(createShipmentCell(isNullOrEmpty(json.getString("payment_method_name")),
                            smalltextfont, 9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                        table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
                    }
                    table4.addCell(
                        createShipmentCell("10%対象   " + "¥" + formatrPrict(json.getString("total_with_normal_tax")),
                            smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(
                        createShipmentCell("うち消費税  " + "¥" + formatrPrict(json.getString("totalWithNormalTaxPrice")),
                            smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_LEFT));

                    table4.addCell(
                        createShipmentCell(" 8%対象   " + "¥" + formatrPrict(json.getString("total_with_reduced_tax")),
                            smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(
                        createShipmentCell("うち消費税  " + "¥" + formatrPrict(json.getString("totalWithReducedTaxPrice")),
                            smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                    table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_LEFT));
                    doc.add(table3);
                    doc.add(table4);
                } else {
                    PdfPTable table3 = createTable(new float[] {
                        100, 360, 60
                    }, 520);
                    table3.setHeaderRows(1);
                    table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
                    table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
                    JSONArray items = json.getJSONArray("setProductJson");
                    for (int i = 0; i < items.size(); i++) {
                        JSONObject itemsJSONObject = items.getJSONObject(i);
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")),
                            smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("name")),
                            smalltextfont, 9, Element.ALIGN_LEFT));
                        table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                            smalltextfont, 9, Element.ALIGN_RIGHT));
                    }
                    if (items.size() < 10) {
                        for (int i = 0; i < (10 - items.size()); i++) {
                            table3.addCell(createShipmentCell(" ", smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                            table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                        }
                    }
                    PdfPTable table4 = createTable(new float[] {
                        520
                    }, 520);
                    table4.setKeepTogether(true);
                    PdfPCell detail_message = createShipmentCell(judgmentNull(json.getString("detail_message")),
                        smalltextfont, 12, Element.ALIGN_LEFT);
                    detail_message.setBorderWidthTop(1f);
                    detail_message.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    detail_message.setVerticalAlignment(Element.ALIGN_LEFT);
                    detail_message.setBorderWidthBottom(1.5f);
                    table4.addCell(detail_message);
                    doc.add(table3);
                    if (!StringTools.isNullOrEmpty(json.getString("detail_message"))) {
                        doc.add(table4);
                    }
                }
                // 纳品书的页数
                int pageNumber1 = writer.getPageNumber();
                pageJson.put("pageNumber1", pageNumber1);

                doc.newPage();


                // 作業指示書
                PdfPTable table5 = createTable(new float[] {
                    330, 190
                }, 520);
                table5.addCell(createShipmentCell("作業指示書", titlefont, Element.ALIGN_CENTER, 15, 180f));
                table5.addCell(createCell(" ", keyfont, Element.ALIGN_RIGHT, 0));
                String noteType = "";
                if ("同梱しない".equals(json.getString("delivery_note_type"))) {
                    noteType = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
                }
                table5.addCell(createCell(noteType, littleBoldFont, Element.ALIGN_LEFT, 0));
                Image picture1 = Image.getInstance(json.getString("codePath"));
                picture1.setAlignment(Image.ALIGN_CENTER);
                picture1.scalePercent(40);
                PdfPCell shipmentImgCell = createShipmentImgCell(picture1, 0, 1, 2, 0, 0f);
                shipmentImgCell.setPaddingTop(-20f);
                shipmentImgCell.setPaddingRight(10f);
                table5.addCell(shipmentImgCell);
                PdfPTable table6 = createTable(new float[] {
                    290, 290
                }, 580);
                String surname1 = json.getString("surname");
                if (StringTools.isNullOrEmpty(surname1)) {
                    surname1 = json.getString("company");
                }
                table6
                    .addCell(createShipmentCell("配送先：" + surname1, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("店舗名：" + json.getString("client_nm"), textUnderlineFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 95f));
                } else {
                    table6.addCell(createShipmentCell("店舗名：" + json.getString("client_nm"), textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 95f));
                }
                // 住所1

                String sendAdd = "";
                if (!StringTools.isNullOrEmpty(json.getString("prefecture"))) {
                    sendAdd += json.getString("prefecture")
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(json.getString("address1"))) {
                    sendAdd += json.getString("address1");
                }
                // 如果是NTM店铺，则将邮编番号换行
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("〒" + json.getString("postcode"), textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                } else {
                    table6.addCell(createShipmentCell("〒" + json.getString("postcode") + " " + sendAdd, textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                }

                String shipping_date = json.getString("shipping_date");
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("出荷予定日(納品日)：" + replaceNull(shipping_date), textUnderlineFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 40f));
                } else {
                    table6.addCell(createShipmentCell("出荷予定日(納品日)：" + replaceNull(shipping_date), textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 40f));
                }

                // 如果是NTM店铺，则将邮编番号换行
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell(sendAdd, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                    table6.addCell(createShipmentCell(" ", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 40f));
                }

                // 住所2
                table6.addCell(createShipmentCell(" " + judgmentNull(json.getString("address2")), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                String order_no = json.getString("order_no");
                if (StringTools.isNullOrEmpty(order_no)) {
                    order_no = json.getString("shipment_plan_id");
                }
                // NTM的受注番号变大加下划线
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("注文番号：" + order_no, textUnderlineFont12, Element.ALIGN_LEFT, 0, 1,
                        0, 0, 86f));
                } else {
                    table6.addCell(
                        createShipmentCell("注文番号：" + order_no, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 86f));
                }

                table6.addCell(createShipmentCell("注文者：" + json.getString("order_name"), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("配送方法：" + replaceNull(json.getString("method")),
                        textUnderlineFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 86f));
                } else {
                    table6.addCell(createShipmentCell("配送方法：" + replaceNull(json.getString("method")), textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 86f));
                }
                // 住所１
                String masterAdd = "";
                if (!StringTools.isNullOrEmpty(json.getString("order_todoufuken"))) {
                    masterAdd += json.getString("order_todoufuken")
                        + " ";
                }
                if (!StringTools.isNullOrEmpty(json.getString("order_address1"))) {
                    masterAdd += json.getString("order_address1");
                }
                String order_zip_code = "";
                if (!StringTools.isNullOrEmpty(json.getString("order_zip_code1"))) {
                    order_zip_code += json.getString("order_zip_code1");
                }
                if (!StringTools.isNullOrEmpty(json.getString("order_zip_code2"))) {
                    order_zip_code += json.getString("order_zip_code2");
                }
                if (!StringTools.isNullOrEmpty(order_zip_code)) {
                    order_zip_code = "〒" + order_zip_code;
                }
                if (isNtmFlg) {
                    table6
                        .addCell(createShipmentCell(order_zip_code, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                } else {
                    table6.addCell(createShipmentCell(order_zip_code + " " + masterAdd, textBoldFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                }

                String deliveryPlanDate = json.getString("delivery_date");
                if (StringTools.isNullOrEmpty(deliveryPlanDate)) {
                    deliveryPlanDate = "ー";
                }
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("お届け希望日：" + deliveryPlanDate, textUnderlineFont,
                        Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                } else {
                    table6.addCell(createShipmentCell("お届け希望日：" + deliveryPlanDate, textBoldFont, Element.ALIGN_LEFT, 0,
                        1, 0, 0, 70f));
                }

                // 住所２
                if (isNtmFlg) {
                    table6.addCell(createShipmentCell(masterAdd, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                } else {
                    table6.addCell(createShipmentCell(" " + judgmentNull(json.getString("order_address2")),
                        textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                }

                if (isNtmFlg) {
                    table6.addCell(createShipmentCell("お届け時間帯：" + replaceNull(json.getString("delivery_time_slot")),
                        textUnderlineFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                } else {
                    table6.addCell(createShipmentCell("お届け時間帯：" + replaceNull(json.getString("delivery_time_slot")),
                        textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                }

                if (isNtmFlg) {
                    table6.addCell(createShipmentCell(" " + judgmentNull(json.getString("order_address2")),
                        textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
                    table6.addCell(createShipmentCell(" ", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));
                }

                if (isNtmFlg) {
                    PdfPTable table7 = createTable(new float[] {
                        90, 90, 210, 90, 40
                    }, 520);

                    table7.setHeaderRows(1);

                    table7.addCell(createShipmentCell("ロケーション", textBoldFont, 12, 1, 1.5f, 30f));
                    table7.addCell(createShipmentCell("商品コード", textBoldFont, 8, 1, 1.5f, 30f));
                    table7.addCell(createShipmentCell("商品名", textBoldFont, 8, 1, 1.5f, 30f));
                    table7.addCell(createShipmentCell("備考", textBoldFont, 8, 1, 1.5f, 30f));
                    table7.addCell(createShipmentCell("数量", textBoldFont, 8, 1, 1.5f, 30f));
                    JSONArray items = json.getJSONArray("setItems");
                    Integer sku = 0;
                    for (int i = 0; i < items.size(); i++) {
                        JSONObject itemsJSONObject = items.getJSONObject(i);


                        table7.addCell(createShipmentCell(itemsJSONObject.getString("locationName"), smallBoldFont, 13,
                            Element.ALIGN_LEFT));
                        table7.addCell(
                            createShipmentCell(itemsJSONObject.getString("code"), textBoldFont, 9, Element.ALIGN_LEFT));
                        table7.addCell(
                            createShipmentCell(itemsJSONObject.getString("name"), textBoldFont, 9, Element.ALIGN_LEFT));
                        table7.addCell(createShipmentCell(itemsJSONObject.getString("ntm_memo"), textBoldFont, 9,
                            Element.ALIGN_LEFT));
                        table7.addCell(createShipmentCell(itemsJSONObject.getString("product_plan_cnt"), textBoldFont,
                            9, 5, 1.5f, 30f));
                        sku += itemsJSONObject.getInteger("product_plan_cnt");

                    }
                    table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                    table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                    table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                    PdfPCell pdfPCell = createShipmentCell("数量合計", textBoldFont, 12);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_RIGHT);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table7.addCell(pdfPCell);
                    table7.addCell(createShipmentCell(sku + "", textBoldFont, 8, Element.ALIGN_RIGHT));
                    doc.add(table5);
                    doc.add(table6);
                    doc.add(table7);
                    // a判断是否有同捆物
                    JSONArray bundled = json.getJSONArray("bundled");
                    if (bundled.size() != 0) {
                        Paragraph p2 = new Paragraph("同梱物", headBoldfont);
                        format(p2);
                        Paragraph p3 = new Paragraph(" ", textfont);
                        doc.add(p2);
                        doc.add(p3);
                        PdfPTable table8 = createTable(new float[] {
                            90, 90, 210, 90, 40
                        }, 520);
                        // table8.setSpacingBefore(30f);
                        table8.setHeaderRows(1);
                        table8.addCell(createShipmentCell("ロケーション", textBoldFont, 12, 1, 1.5f, 30f));
                        table8.addCell(createShipmentCell("商品コード", textBoldFont, 8, 1, 1.5f, 30f));
                        table8.addCell(createShipmentCell("同梱物名", textBoldFont, 8, 1, 1.5f, 30f));
                        table8.addCell(createShipmentCell("備考", textBoldFont, 8, 1, 1.5f, 30f));
                        table8.addCell(createShipmentCell("数量", textBoldFont, 8, 1, 1.5f, 30f));
                        int bundledTotal = 0;
                        for (int i = 0; i < bundled.size(); i++) {
                            JSONObject jsonParam = bundled.getJSONObject(i);
                            table8.addCell(createShipmentCell(jsonParam.getString("locationName"), smallBoldFont, 13,
                                Element.ALIGN_LEFT));
                            table8.addCell(
                                createShipmentCell(jsonParam.getString("code"), textBoldFont, 9, Element.ALIGN_LEFT));
                            table8.addCell(
                                createShipmentCell(jsonParam.getString("name"), textBoldFont, 9, Element.ALIGN_LEFT));
                            table8.addCell(createShipmentCell(jsonParam.getString("ntm_memo"), textBoldFont, 9,
                                Element.ALIGN_LEFT));
                            table8.addCell(createShipmentCell(jsonParam.getString("product_plan_cnt"), textBoldFont, 9,
                                Element.ALIGN_RIGHT));

                            bundledTotal += jsonParam.getInteger("product_plan_cnt");
                        }
                        table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                        table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                        table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                        PdfPCell cell1 = createShipmentCell("数量合計", textBoldFont, 12);
                        cell1.setVerticalAlignment(Element.ALIGN_RIGHT);
                        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table8.addCell(cell1);
                        table8.addCell(createShipmentCell(bundledTotal + "", textBoldFont, 8, Element.ALIGN_RIGHT));
                        doc.add(table8);
                    }
                } else {
                    PdfPTable table7 = createTable(new float[] {
                        90, 90, 90, 210, 40
                    }, 520);

                    table7.setHeaderRows(1);

                    table7.addCell(createShipmentCell("ロケーション", textBoldFont, 12, 1, 1.5f, 30f));
                    table7.addCell(createShipmentCell("商品コード", textBoldFont, 8, 1, 1.5f, 30f));
                    table7.addCell(createShipmentCell("管理バーコード", textBoldFont, 8, 1, 1.5f, 30f));
                    table7.addCell(createShipmentCell("商品名", textBoldFont, 8, 1, 1.5f, 30f));
                    table7.addCell(createShipmentCell("数量", textBoldFont, 8, 1, 1.5f, 30f));
                    JSONArray items = json.getJSONArray("setItems");
                    Integer sku = 0;
                    for (int i = 0; i < items.size(); i++) {
                        JSONObject itemsJSONObject = items.getJSONObject(i);
                        table7.addCell(createShipmentCell(itemsJSONObject.getString("locationName"), smallBoldFont, 13,
                            Element.ALIGN_LEFT));
                        table7.addCell(
                            createShipmentCell(itemsJSONObject.getString("code"), textBoldFont, 9, Element.ALIGN_LEFT));
                        table7.addCell(createShipmentCell(itemsJSONObject.getString("barcode"), textBoldFont, 9,
                            Element.ALIGN_LEFT));
                        table7.addCell(
                            createShipmentCell(itemsJSONObject.getString("name"), textBoldFont, 9, Element.ALIGN_LEFT));
                        table7.addCell(createShipmentCell(itemsJSONObject.getString("product_plan_cnt"), textBoldFont,
                            9, 5, 1.5f, 30f));
                        sku += itemsJSONObject.getInteger("product_plan_cnt");

                    }
                    table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                    table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                    table7.addCell(createShipmentCell(" ", textBoldFont, 12));
                    PdfPCell pdfPCell = createShipmentCell("数量合計", textBoldFont, 12);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_RIGHT);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table7.addCell(pdfPCell);
                    table7.addCell(createShipmentCell(sku + "", textBoldFont, 8, Element.ALIGN_RIGHT));
                    doc.add(table5);
                    doc.add(table6);
                    doc.add(table7);
                    // a判断是否有同捆物
                    JSONArray bundled = json.getJSONArray("bundled");
                    if (bundled.size() != 0) {
                        Paragraph p2 = new Paragraph("同梱物", headBoldfont);
                        format(p2);
                        Paragraph p3 = new Paragraph(" ", textfont);
                        doc.add(p2);
                        doc.add(p3);
                        PdfPTable table8 = createTable(new float[] {
                            90, 90, 90, 210, 40
                        }, 520);
                        // table8.setSpacingBefore(30f);
                        table8.setHeaderRows(1);
                        table8.addCell(createShipmentCell("ロケーション", textBoldFont, 12, 1, 1.5f, 30f));
                        table8.addCell(createShipmentCell("商品コード", textBoldFont, 8, 1, 1.5f, 30f));
                        table8.addCell(createShipmentCell("管理バーコード", textBoldFont, 8, 1, 1.5f, 30f));
                        table8.addCell(createShipmentCell("同梱物名", textBoldFont, 8, 1, 1.5f, 30f));
                        table8.addCell(createShipmentCell("数量", textBoldFont, 8, 1, 1.5f, 30f));
                        int bundledTotal = 0;
                        for (int i = 0; i < bundled.size(); i++) {
                            JSONObject jsonParam = bundled.getJSONObject(i);
                            table8.addCell(createShipmentCell(jsonParam.getString("locationName"), smallBoldFont, 13,
                                Element.ALIGN_LEFT));
                            table8.addCell(
                                createShipmentCell(jsonParam.getString("code"), textBoldFont, 9, Element.ALIGN_LEFT));
                            table8.addCell(createShipmentCell(jsonParam.getString("barcode"), textBoldFont, 9,
                                Element.ALIGN_LEFT));
                            table8.addCell(
                                createShipmentCell(jsonParam.getString("name"), textBoldFont, 9, Element.ALIGN_LEFT));
                            table8.addCell(createShipmentCell(jsonParam.getString("product_plan_cnt"), textBoldFont, 9,
                                Element.ALIGN_RIGHT));

                            bundledTotal += jsonParam.getInteger("product_plan_cnt");
                        }
                        table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                        table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                        table8.addCell(createShipmentCell(" ", textBoldFont, 12));
                        PdfPCell cell1 = createShipmentCell("数量合計", textBoldFont, 12);
                        cell1.setVerticalAlignment(Element.ALIGN_RIGHT);
                        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        table8.addCell(cell1);
                        table8.addCell(createShipmentCell(bundledTotal + "", textBoldFont, 8, Element.ALIGN_RIGHT));
                        doc.add(table8);
                    }
                }

                // 判断是否有ご請求コード
                String billBarcode = json.getString("billBarcode");
                if (!StringTools.isNullOrEmpty(billBarcode)) {
                    PdfPTable table9 = createTable(new float[] {
                        100, 420
                    }, 520);
                    table9.setSpacingBefore(30f);
                    table9.addCell(createShipmentCell("GMO後払い\nご請求コード", textBoldFont, 12, 1, 1.5f, 30f));
                    table9.addCell(createShipmentCell(billBarcode, textBoldFont, 8, 1, 1.5f, 30f));
                    doc.add(table9);
                }

                PdfPTable table10 = createTable(new float[] {
                    240, 30, 250
                }, 520);
                table10.setSpacingBefore(20f);
                table10.addCell(createShipmentCell("■特記事項", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 13, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10
                    .addCell(createShipmentCell("■購入者備考欄 （梱包メモ）", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 13, 15f));


                String gift_wrapping_unit = shipments.getGift_wrapping_unit();
                String giftWrappingUnit = "";
                if (!StringTools.isNullOrEmpty(gift_wrapping_unit)) {
                    switch (gift_wrapping_unit) {
                        case "1":
                            giftWrappingUnit = "注文単位 ";
                            String giftWrappingType =
                                shipments.getTw201_shipment_detail().get(0).getGift_wrapping_type();
                            if (!StringTools.isNullOrEmpty(giftWrappingType)) {
                                giftWrappingUnit += " " + giftWrappingType;
                            }
                            break;
                        case "2":
                            giftWrappingUnit = "商品単位";
                            break;
                        default:
                            break;
                    }
                }
                if (!StringTools.isNullOrEmpty(giftWrappingUnit)) {
                    giftWrappingUnit = "・ギフト：" + giftWrappingUnit;
                }
                table10
                    .addCell(createShipmentCell(giftWrappingUnit, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));

                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                PdfPCell shipmentCell =
                    createShipmentCell(shipments.getMemo(), textBoldFont, Element.ALIGN_LEFT, 0, 10, 0, 0, 20f);
                shipmentCell.setLeading(5f, 1f);
                table10.addCell(shipmentCell);
                String cushioning_unit = shipments.getCushioning_unit();
                String cushioningType = "";
                if (!StringTools.isNullOrEmpty(cushioning_unit)) {
                    switch (cushioning_unit) {
                        case "1":
                            cushioningType = "注文単位";
                            String cushioning_type = shipments.getTw201_shipment_detail().get(0).getCushioning_type();
                            if (!StringTools.isNullOrEmpty(cushioning_type)) {
                                cushioningType += " " + cushioning_type;
                            }
                            break;
                        case "2":
                            cushioningType = "商品単位";
                            break;
                        default:
                            break;
                    }
                }
                if (!StringTools.isNullOrEmpty(cushioningType)) {
                    cushioningType = "・緩衝材：" + cushioningType;
                }
                table10.addCell(createShipmentCell(cushioningType, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getInstructions_special_notes()),
                    textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou1()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou2()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                // table10.addCell(createShipmentCell(json.getString("memo"), smalltextfont, Element.ALIGN_LEFT, 0, 6,
                // 0, 0, 20f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou3()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou4()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou5()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou6()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                table10.addCell(createShipmentCell(pointReplaceNull(shipments.getBikou7()), textBoldFont,
                    Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
                table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
                doc.add(table10);

                int pageNumber = writer.getPageNumber();

                int pageNumber2 = pageNumber - pageNumber1;
                pageJson.put("pageNumber2", pageNumber2);
                pageArray.add(pageJson);
                doc.newPage();
            }
        }


        doc.close();
        if (StringTools.isNullOrEmpty(type) || "0".equals(type)) {
            // a将A4输出变为A3格式(需要恢复A4删除下面两行和pdfPath后的"+A4"值即可)
            File f = new File(pdfPath);
            concatPDFs(new FileInputStream(file), new FileOutputStream(f), true, pageArray);
        }
    }

    /**
     * @param json
     * @param pdfPath
     * @description: 生成店铺侧的明細・作業指示書
     * @return: void
     * @date: 2021/6/8 15:51
     */
    public static void createStoreShipmentsDetailPDF(JSONObject json, String pdfPath) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath + "A4");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        Integer price_on_delivery_note = json.getInteger("price_on_delivery_note");

        PdfPTable table = createTable(new float[] {
            135, 245, 190
        }, 580);

        String logo = json.getString("detail_logo");
        try {
            Image image = Image.getInstance(logo);
            image.setAlignment(Image.ALIGN_CENTER);
            image.scalePercent(40);
            table.addCell(createShipmentImgCell(image, 0, 1, 2, 0, 30f));
        } catch (Exception e) {
            table.addCell(createShipmentCell(" ", titlefont, Element.ALIGN_MIDDLE, 15, 30f));
        }
        String value = "                   納品書";
        PdfPCell c = createCell(value, titlefont, Element.ALIGN_LEFT, 0);
        c.setPaddingTop(-30f);
        table.addCell(c);
        Image picture = Image.getInstance(json.getString("codePath"));
        picture.setAlignment(Image.ALIGN_CENTER);
        picture.scalePercent(40);
        table.addCell(createShipmentImgCell(picture, 0, 1, 2, 0, 0f));
        PdfPTable table1 = createTable(new float[] {
            300, 330
        }, 580);
        table1.setSpacingAfter(5f);
        String company = json.getString("company");
        String surname = json.getString("surname");
        String division = json.getString("division");
        // 部署null判断
        if (StringTools.isNullOrEmpty(division)) {
            division = " ";
        }
        // 拼接部署
        if (!StringTools.isNullOrEmpty(company)) {
            company += " " + division;
        } else {
            company = division;
        }
        if (StringTools.isNullOrEmpty(surname) && !StringTools.isNullOrEmpty(company)) {
            company += "  御中";
        }
        if (!StringTools.isNullOrEmpty(surname)) {
            surname += " 様";
        }
        table1.addCell(createCell(judgmentNull(company), smallBoldFont, Element.ALIGN_LEFT, 0));
        String orderNo = json.getString("order_no");
        if (StringTools.isNullOrEmpty(orderNo)) {
            orderNo = json.getString("shipment_plan_id");
        }
        table1.addCell(createCell("注文日付：" + isNullOrEmpty(json.getString("order_datetime")), smalltextfont,
            Element.ALIGN_LEFT, 0));
        table1.addCell(createCell(judgmentNull(surname), smallBoldFont, Element.ALIGN_LEFT, 0));

        table1.addCell(createCell("注文番号：" + orderNo, smalltextfont, Element.ALIGN_LEFT, 0));
        PdfPTable table2 = createTable(new float[] {
            240, 338
        }, 520);

        if (!StringTools.isNullOrEmpty(json.getString("gift_sender_name"))) {
            table2.addCell(createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 2, 30, 1, 5f));
            table2.addCell(createShipmentCell("贈 り 主 ：" + isNullOrEmpty(json.getString("gift_sender_name")),
                smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
        } else {
            table2.addCell(createShipmentCell("下記の通り納品いたしました。", smalltextfont, Element.ALIGN_LEFT, 0, 1, 15, 1, 5f));
        }
        table2.addCell(
            createShipmentCell(isNullOrEmpty(json.getString("name")), textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
        table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
        table2.addCell(createShipmentCell("〒" + isNullOrEmpty(json.getString("masterPostcode")), textfont,
            Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));

        if (Constants.DON_T_WANT_TO_SHARE_THE_BOOK.equals(json.getString("delivery_note_type"))) {
            table2.addCell(createShipmentCell(Constants.DON_T_WANT_TO_SHARE_THE_BOOK, littleBoldFont,
                Element.ALIGN_LEFT, 0, 2, 30, 0, 5f));
        } else {
            table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
        }
        String add = "";
        if (!StringTools.isNullOrEmpty(json.getString("masterPrefecture"))) {
            add += json.getString("masterPrefecture")
                + " ";
        }
        if (!StringTools.isNullOrEmpty(json.getString("masterAddress1"))) {
            add += json.getString("masterAddress1");
        }
        table2.addCell(createShipmentCell(add, textfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 65f));
        if (!Constants.DON_T_WANT_TO_SHARE_THE_BOOK.equals(json.getString("delivery_note_type"))) {
            table2.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0));
        }
        table2.addCell(createShipmentCell(judgmentNull(json.getString("masterAddress2")), textfont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 65f));
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(0);
        cell.setVerticalAlignment(0);
        cell.setColspan(0);
        cell.setRowspan(1);
        cell.setFixedHeight(20);
        String string = json.getString("total_amount");
        String param1 = "合計金額" + "    " + "¥" + formatrPrict(json.getString("total_amount"));
        if (string.length() > 7) {
            param1 += "       (税込)";
        } else {
            param1 += "            (税込)";
        }
        cell.setPhrase(new Phrase(param1, smallfont));
        cell.setPaddingLeft(10f);
        cell.disableBorderSide(13);
        String contact = json.getString("contact");
        if (!contact.equals("0")) {
            // 电话
            String substring2 = contact.substring(0, 1);
            String sponsorPhone = (substring2.equals("1")) ? "TEL : " + json.getString("sponsorPhone") : "";
            // fax
            String substring1 = contact.substring(1, 2);
            String sponsorFax = (substring1.equals("1")) ? "FAX : " + json.getString("sponsorFax") : "";
            // 邮箱
            String substring = contact.substring(2, 3);
            String sponsorEmail = (substring.equals("1")) ? "Mail : " + json.getString("sponsorEmail") : "";
            // 集合中去掉空的字符串
            List<String> list = Stream.of(sponsorPhone, sponsorFax, sponsorEmail).filter(String -> !String.isEmpty())
                .collect(Collectors.toList());
            if (list.size() == 0) {
                if (price_on_delivery_note == 1) {
                    table2.addCell(cell);
                    table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                }
            } else {
                table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                table2.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 1, 8, 0, 65f));
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                table2.addCell(createShipmentCell("購入内容に関するお問い合わせ先", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                int count = 0;
                for (String param : list) {
                    if (count == list.size() - 1) {
                        if (price_on_delivery_note == 1) {
                            table2.addCell(cell);
                        } else {
                            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                        }
                    } else {
                        table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    }
                    table2.addCell(createShipmentCell(param, textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
                    count++;
                }
            }
        } else {
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            if (StringTools.isNullOrEmpty(price_on_delivery_note)) {
                price_on_delivery_note = 1;
            }
            if (price_on_delivery_note == 1) {
                table2.addCell(cell);
                table2.addCell(createShipmentCell(" ", textfont, Element.ALIGN_LEFT, 0, 1, 15, 0, 65f));
            }
        }
        doc.add(table);
        doc.add(table1);
        doc.add(table2);
        if (price_on_delivery_note == 1) {
            PdfPTable table3 = createTable(new float[] {
                80, 200, 40, 80, 90, 30
            }, 520);
            table3.setSpacingBefore(2f);
            table3.setHeaderRows(1);
            table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
            String productTax = json.getString("product_tax");
            String param = "";
            if (!StringTools.isNullOrEmpty(productTax)) {
                param = "(" + productTax + ")";
            }
            table3.addCell(createShipmentCell("単価" + param, textfont, 8, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("金額 (税込)", textfont, 8, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("税率", textfont, 8, 1, 1.5f, 30f));
            JSONArray setProductJson = json.getJSONArray("items");
            int size = setProductJson.size();
            for (int i = 0; i < setProductJson.size(); i++) {
                JSONObject itemsJSONObject = setProductJson.getJSONObject(i);
                table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")), smalltextfont, 20f,
                    13, Element.ALIGN_LEFT));
                String is_reduced_tax = itemsJSONObject.getString("is_reduced_tax");
                String isReducedTax = "10%";
                String mark = "";
                if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                    isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                        : "10%";
                    if ("1".equals(is_reduced_tax)) {
                        mark = "※ ";
                    }
                }
                String taxFlag = itemsJSONObject.getString("tax_flag");
                if ("3".equals(taxFlag) || "非課税".equals(taxFlag)) {
                    isReducedTax = "0%";
                    mark = "";
                }
                String name = isNullOrEmpty(itemsJSONObject.getString("name"));
                size += (int) Math.ceil((double) name.length() / (double) 24) - 1;
                table3.addCell(createShipmentCell(mark + isNullOrEmpty(itemsJSONObject.getString("name")),
                    smalltextfont, 9, Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                    smalltextfont, 9, Element.ALIGN_RIGHT));
                table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("unit_price")),
                    smalltextfont, 9, Element.ALIGN_RIGHT));
                table3.addCell(createShipmentCell("¥" + formatrPrict(itemsJSONObject.getString("price")), smalltextfont,
                    9, Element.ALIGN_RIGHT));

                if (!StringTools.isNullOrEmpty(is_reduced_tax)) {
                    isReducedTax = (Integer.parseInt(is_reduced_tax) != 0) ? "8%"
                        : "10%";
                }
                String taxFlag1 = itemsJSONObject.getString("tax_flag");
                if ("3".equals(taxFlag1) || "非課税".equals(taxFlag1)) {
                    isReducedTax = "0%";
                }
                table3.addCell(createShipmentCell(isReducedTax, smalltextfont, 9));
            }
            if (size < 8) {
                for (int i = 0; i < 10 - size; i++) {
                    table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                }
            }
            if (size > 33 && size < 45) {
                for (int i = 0; i < 46 - size; i++) {
                    table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                }
            }
            if (size > 45) {
                int num = size - 43;
                if (num > 66 && num < 78) {
                    for (int i = 0; i < 79 - num; i++) {
                        table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                        table3.addCell(createShipmentCell(" ", smalltextfont, 9));
                    }
                }
            }
            table3.addCell(createShipmentCell(" \n ", smalltextfont, 13));
            table3.addCell(createShipmentCell("　※は軽減税率対象", smalltextfont, 9, Element.ALIGN_LEFT));
            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
            table3.addCell(createShipmentCell(" ", smalltextfont, 9));
            PdfPTable table4 = createTable(new float[] {
                320, 80, 90, 30
            }, 520);
            table4.setKeepTogether(true);
            String payment_method = json.getString("payment_method");
            if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
                table4.addCell(createShipmentCell(judgmentNull(json.getString("detail_message")), smalltextfont,
                    Element.ALIGN_LEFT, 13, 10, 220, 0f, 4, 1f, 5f));
            } else {
                table4.addCell(createShipmentCell(judgmentNull(json.getString("detail_message")), smalltextfont,
                    Element.ALIGN_LEFT, 13, 9, 195, 0f, 4, 1f, 5f));
            }
            table4.addCell(
                createShipmentCell("商品合計 (税込)", textfont, 13, 1, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4.addCell(createShipmentCell("¥" + formatrPrict(json.getString("subtotal_amount")), smalltextfont, 9,
                1, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
            table4.addCell(createShipmentCell(" ", smalltextfont, 14, 1, 1f, 25f));
            table4.addCell(
                createShipmentCell("送料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            String deliveryCharge = json.getString("delivery_charge");
            if (StringTools.isNullOrEmpty(deliveryCharge)) {
                deliveryCharge = "0";
            }
            table4.addCell(createShipmentCell("¥" + formatrPrict(deliveryCharge), smalltextfont, 9, 0, 0f, 25f,
                Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
            table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

            table4.addCell(
                createShipmentCell("手数料 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            String handlingCharge = json.getString("handling_charge");
            if (StringTools.isNullOrEmpty(handlingCharge)) {
                handlingCharge = "0";
            }
            table4.addCell(createShipmentCell("¥" + formatrPrict(handlingCharge), smalltextfont, 9, 0, 0f, 25f,
                Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
            table4.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 5f));

            table4.addCell(
                createShipmentCell("割引額", textfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT, 1));

            String discount_amount = json.getString("discount_amount");
            if (StringTools.isNullOrEmpty(discount_amount)) {
                discount_amount = "0";
            }
            table4.addCell(createShipmentCell("¥" + formatrPrict(discount_amount), smalltextfont, 9, 0, 0f, 15f,
                Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT, 2));

            table4.addCell(
                createShipmentCell(" ", smalltextfont, 15, 0, 0f, 15f, Element.ALIGN_BOTTOM, Element.ALIGN_RIGHT, 1));

            PdfPCell cell2 = createShipmentCell("(ポイント、クーポン含む) ", smalltextfont, 13, 0, 1f, 10f, Element.ALIGN_MIDDLE,
                Element.ALIGN_LEFT, 1);
            cell2.setPaddingTop(-5);
            table4.addCell(cell2);

            table4.addCell(
                createShipmentCell(" ", smalltextfont, 15, 0, 0f, 10f, Element.ALIGN_BOTTOM, Element.ALIGN_RIGHT, 1));

            table4.addCell(
                createShipmentCell("合計 (税込)", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4.addCell(createShipmentCell("¥" + formatrPrict(json.getString("total_amount")), smalltextfont, 9, 0,
                1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
            table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));
            if (!StringTools.isNullOrEmpty(payment_method) && (Integer.parseInt(payment_method) == 2)) {
                table4.addCell(
                    createShipmentCell("支払方法", textfont, 13, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                table4.addCell(createShipmentCell(isNullOrEmpty(json.getString("payment_method_name")), smalltextfont,
                    9, 0, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 25f, Element.ALIGN_LEFT));

                table4.addCell(createShipmentCell("支払総計 (税込)", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE,
                    Element.ALIGN_LEFT));
                table4.addCell(createShipmentCell("¥" + formatrPrict(json.getString("total_for_cash_on_delivery")),
                    smalltextfont, 9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
            } else {
                table4.addCell(
                    createShipmentCell("支払方法", textfont, 13, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
                table4.addCell(createShipmentCell(isNullOrEmpty(json.getString("payment_method_name")), smalltextfont,
                    9, 4, 1f, 25f, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT));
                table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 25f, Element.ALIGN_LEFT));
            }
            table4.addCell(createShipmentCell("10%対象   " + "¥" + formatrPrict(json.getString("total_with_normal_tax")),
                smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4.addCell(createShipmentCell("うち消費税  " + "¥" + formatrPrict(json.getString("totalWithNormalTaxPrice")),
                smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4.addCell(createShipmentCell(" ", smalltextfont, 15, 0, 1f, 15f, Element.ALIGN_LEFT));

            table4.addCell(createShipmentCell(" 8%対象   " + "¥" + formatrPrict(json.getString("total_with_reduced_tax")),
                smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4
                .addCell(createShipmentCell("うち消費税  " + "¥" + formatrPrict(json.getString("totalWithReducedTaxPrice")),
                    smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_MIDDLE, Element.ALIGN_LEFT));
            table4.addCell(createShipmentCell(" ", smalltextfont, 13, 4, 1f, 15f, Element.ALIGN_LEFT));
            doc.add(table3);
            doc.add(table4);
        } else {
            PdfPTable table3 = createTable(new float[] {
                100, 360, 60
            }, 520);
            table3.setHeaderRows(1);
            table3.addCell(createShipmentCell("商品コード", textfont, 12, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("商品名", textfont, 8, 1, 1.5f, 30f));
            table3.addCell(createShipmentCell("数量", textfont, 8, 1, 1.5f, 30f));
            JSONArray items = json.getJSONArray("items");
            for (int i = 0; i < items.size(); i++) {
                JSONObject itemsJSONObject = items.getJSONObject(i);
                table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("code")), smalltextfont, 20f,
                    13, Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("name")), smalltextfont, 9,
                    Element.ALIGN_LEFT));
                table3.addCell(createShipmentCell(isNullOrEmpty(itemsJSONObject.getString("product_plan_cnt")),
                    smalltextfont, 9, Element.ALIGN_RIGHT));
            }
            if (items.size() < 10) {
                for (int i = 0; i < (10 - items.size()); i++) {
                    table3.addCell(createShipmentCell(" ", smalltextfont, 20f, 13, Element.ALIGN_LEFT));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                    table3.addCell(createShipmentCell(" ", smalltextfont, 9, Element.ALIGN_LEFT));
                }
            }
            PdfPTable table4 = createTable(new float[] {
                520
            }, 520);
            table4.setKeepTogether(true);
            PdfPCell detail_message = createShipmentCell(judgmentNull(json.getString("detail_message")), smalltextfont,
                12, Element.ALIGN_LEFT);
            detail_message.setBorderWidthTop(1f);
            detail_message.setHorizontalAlignment(Element.ALIGN_MIDDLE);
            detail_message.setVerticalAlignment(Element.ALIGN_LEFT);
            detail_message.setBorderWidthBottom(1.5f);
            table4.addCell(detail_message);
            doc.add(table3);
            if (!StringTools.isNullOrEmpty(json.getString("detail_message"))) {
                doc.add(table4);
            }
        }
        // 纳品书生成的页数
        int pageNumber1 = writer.getPageNumber();

        doc.newPage();

        // 作業指示書
        PdfPTable table5 = createTable(new float[] {
            330, 190
        }, 520);
        table5.addCell(createShipmentCell("作業指示書", titlefont, Element.ALIGN_CENTER, 15, 180f));
        table5.addCell(createCell(" ", keyfont, Element.ALIGN_RIGHT, 0));
        String noteType = "";
        if (Constants.DON_T_WANT_TO_SHARE_THE_BOOK.equals(json.getString("delivery_note_type"))) {
            noteType = Constants.DON_T_WANT_TO_SHARE_THE_BOOK;
        }
        table5.addCell(createCell(noteType, littleBoldFont, Element.ALIGN_LEFT, 0));
        Image picture1 = Image.getInstance(json.getString("codePath"));
        picture1.setAlignment(Image.ALIGN_CENTER);
        picture1.scalePercent(40);
        PdfPCell shipmentImgCell = createShipmentImgCell(picture1, 0, 1, 2, 0, 0f);
        shipmentImgCell.setPaddingTop(-20f);
        shipmentImgCell.setPaddingRight(10f);
        table5.addCell(shipmentImgCell);
        PdfPTable table6 = createTable(new float[] {
            290, 290
        }, 580);
        String surname1 = json.getString("surname");
        if (StringTools.isNullOrEmpty(surname1)) {
            surname1 = json.getString("company");
        }
        table6.addCell(createShipmentCell("配送先：" + surname1, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

        table6.addCell(createShipmentCell("店舗名：" + json.getString("client_nm"), textBoldFont, Element.ALIGN_LEFT, 0, 1,
            0, 0, 95f));
        // 住所1

        String sendAdd = "";
        if (!StringTools.isNullOrEmpty(json.getString("prefecture"))) {
            sendAdd += json.getString("prefecture")
                + " ";
        }
        if (!StringTools.isNullOrEmpty(json.getString("address1"))) {
            sendAdd += json.getString("address1");
        }
        table6.addCell(createShipmentCell("〒" + json.getString("postcode") + " " + sendAdd, textBoldFont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

        String shipping_date = json.getString("shipping_date");
        table6.addCell(createShipmentCell("出荷予定日(納品日)：" + replaceNull(shipping_date), textBoldFont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 40f));

        // 住所2
        String address2 = "";
        if (!StringTools.isNullOrEmpty(json.getString("address2"))) {
            address2 = json.getString("address2");
        }

        table6.addCell(
            createShipmentCell(" " + judgmentNull(address2), textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
        String order_no = json.getString("order_no");
        if (StringTools.isNullOrEmpty(order_no)) {
            order_no = json.getString("shipment_plan_id");
        }
        table6.addCell(createShipmentCell("注文番号：" + order_no, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 86f));


        table6.addCell(createShipmentCell("注文者：" + json.getString("order_name"), textBoldFont, Element.ALIGN_LEFT, 0, 1,
            0, 0, 45f));
        table6.addCell(createShipmentCell("配送方法：" + replaceNull(json.getString("method")), textBoldFont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 86f));

        // 住所１
        String masterAdd = "";
        if (!StringTools.isNullOrEmpty(json.getString("order_todoufuken"))) {
            masterAdd += json.getString("order_todoufuken")
                + " ";
        }
        if (!StringTools.isNullOrEmpty(json.getString("order_address1"))) {
            masterAdd += json.getString("order_address1");
        }
        String order_zip = "";
        if (!StringTools.isNullOrEmpty(json.getString("order_zip_code1"))) {
            order_zip += json.getString("order_zip_code1");
        }
        if (!StringTools.isNullOrEmpty(json.getString("order_zip_code2"))) {
            order_zip += json.getString("order_zip_code2");
        }
        if (!StringTools.isNullOrEmpty(order_zip)) {
            order_zip = "〒" + order_zip;
        }
        table6.addCell(
            createShipmentCell(order_zip + " " + masterAdd, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));

        String deliveryPlanDate = json.getString("delivery_date");
        if (StringTools.isNullOrEmpty(deliveryPlanDate)) {
            deliveryPlanDate = "ー";
        }
        table6.addCell(
            createShipmentCell("お届け希望日：" + deliveryPlanDate, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));

        // 住所２
        table6.addCell(createShipmentCell(" " + judgmentNull(json.getString("order_address2")), textBoldFont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 45f));
        table6.addCell(createShipmentCell("お届け時間帯：" + replaceNull(json.getString("delivery_time_slot")), textBoldFont,
            Element.ALIGN_LEFT, 0, 1, 0, 0, 70f));

        PdfPTable table7 = createTable(new float[] {
            90, 90, 90, 210, 40
        }, 520);
        table7.setHeaderRows(1);
        table7.addCell(createShipmentCell("ロケーション", textBoldFont, 12, 1, 1.5f, 30f));

        table7.addCell(createShipmentCell("商品コード", textBoldFont, 8, 1, 1.5f, 30f));
        table7.addCell(createShipmentCell("管理バーコード", textBoldFont, 8, 1, 1.5f, 30f));
        table7.addCell(createShipmentCell("商品名", textBoldFont, 8, 1, 1.5f, 30f));
        table7.addCell(createShipmentCell("数量", textBoldFont, 8, 1, 1.5f, 30f));

        JSONArray items = json.getJSONArray("setItems");
        Integer sku = 0;
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemsJSONObject = items.getJSONObject(i);
            sku += itemsJSONObject.getInteger("product_plan_cnt");


            table7.addCell(
                createShipmentCell(itemsJSONObject.getString("locationName"), smallBoldFont, 13, Element.ALIGN_LEFT));
            table7.addCell(createShipmentCell(itemsJSONObject.getString("code"), textBoldFont, 9, Element.ALIGN_LEFT));
            table7
                .addCell(createShipmentCell(itemsJSONObject.getString("barcode"), textBoldFont, 9, Element.ALIGN_LEFT));
            table7.addCell(createShipmentCell(itemsJSONObject.getString("name"), textBoldFont, 9, Element.ALIGN_LEFT));
            table7.addCell(
                createShipmentCell(itemsJSONObject.getString("product_plan_cnt"), smallBoldFont, 9, 5, 1.5f, 30f));
        }
        table7.addCell(createShipmentCell(" ", textBoldFont, 12));
        table7.addCell(createShipmentCell(" ", textBoldFont, 12));
        table7.addCell(createShipmentCell(" ", textBoldFont, 12));
        PdfPCell pdfPCell = createShipmentCell("数量合計", textBoldFont, 12);
        pdfPCell.setVerticalAlignment(Element.ALIGN_RIGHT);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table7.addCell(pdfPCell);
        table7.addCell(createShipmentCell(sku + "", textBoldFont, 8, Element.ALIGN_RIGHT));
        doc.add(table5);
        doc.add(table6);
        doc.add(table7);
        // a判断是否有同捆物
        JSONArray bundled = json.getJSONArray("bundled");
        if (bundled.size() != 0) {
            Paragraph p2 = new Paragraph("同梱物", headBoldfont);
            format(p2);
            Paragraph p3 = new Paragraph(" ", textfont);
            doc.add(p2);
            doc.add(p3);
            PdfPTable table8 = createTable(new float[] {
                90, 90, 90, 210, 40
            }, 520);
            // table8.setSpacingBefore(30f);
            table8.setHeaderRows(1);
            table8.addCell(createShipmentCell("ロケーション", textBoldFont, 12, 1, 1.5f, 30f));
            table8.addCell(createShipmentCell("商品コード", textBoldFont, 8, 1, 1.5f, 30f));
            table8.addCell(createShipmentCell("管理バーコード", textBoldFont, 8, 1, 1.5f, 30f));
            table8.addCell(createShipmentCell("同梱物名", textBoldFont, 8, 1, 1.5f, 30f));
            table8.addCell(createShipmentCell("数量", textBoldFont, 8, 1, 1.5f, 30f));
            int bundledTotal = 0;
            for (int i = 0; i < bundled.size(); i++) {
                JSONObject jsonParam = bundled.getJSONObject(i);
                String num = jsonParam.getString("product_plan_cnt");
                bundledTotal += Integer.parseInt(num);
                table8.addCell(
                    createShipmentCell(jsonParam.getString("locationName"), smallBoldFont, 13, Element.ALIGN_LEFT));
                table8.addCell(createShipmentCell(jsonParam.getString("code"), textBoldFont, 9, Element.ALIGN_LEFT));
                table8.addCell(createShipmentCell(jsonParam.getString("barcode"), textBoldFont, 9, Element.ALIGN_LEFT));
                table8.addCell(createShipmentCell(jsonParam.getString("name"), textBoldFont, 9, Element.ALIGN_LEFT));
                table8.addCell(createShipmentCell(num, smallBoldFont, 9, Element.ALIGN_RIGHT));
            }
            table8.addCell(createShipmentCell(" ", textBoldFont, 12));
            table8.addCell(createShipmentCell(" ", textBoldFont, 12));
            table8.addCell(createShipmentCell(" ", textBoldFont, 12));
            PdfPCell cell1 = createShipmentCell("数量合計", textBoldFont, 12);
            cell1.setVerticalAlignment(Element.ALIGN_RIGHT);
            cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table8.addCell(cell1);
            table8.addCell(createShipmentCell(bundledTotal + "", textBoldFont, 8, Element.ALIGN_RIGHT));
            doc.add(table8);
        }

        // 判断是否有ご請求コード
        String billBarcode = json.getString("billBarcode");
        if (!StringTools.isNullOrEmpty(billBarcode)) {
            PdfPTable table9 = createTable(new float[] {
                100, 420
            }, 520);
            table9.setSpacingBefore(30f);
            table9.addCell(createShipmentCell("GMO後払い\nご請求コード", textBoldFont, 12, 1, 1.5f, 30f));
            table9.addCell(createShipmentCell(billBarcode, keyfont, 8, 1, 1.5f, 30f));
            doc.add(table9);
        }

        PdfPTable table10 = createTable(new float[] {
            240, 30, 250
        }, 520);
        table10.setSpacingBefore(20f);
        table10.addCell(createShipmentCell("■特記事項", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 13, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table10.addCell(createShipmentCell("■購入者備考欄 （梱包メモ）", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 13, 15f));

        String gift_wrapping_unit = json.getString("gift_wrapping_unit");
        String giftWrappingUnit = "";
        if (!StringTools.isNullOrEmpty(gift_wrapping_unit)) {
            switch (gift_wrapping_unit) {
                case "1":
                    giftWrappingUnit = "注文単位 ";
                    if (!StringTools.isNullOrEmpty(json.getString("gift_wrapping_type"))) {
                        String gift_wrapping_type = " " + json.getString("gift_wrapping_type");
                        giftWrappingUnit += gift_wrapping_type;
                    }
                    break;
                case "2":
                    giftWrappingUnit = "商品単位";
                    break;
                default:
                    break;
            }
        }
        if (!StringTools.isNullOrEmpty(giftWrappingUnit)) {
            giftWrappingUnit = "・ギフト：" + giftWrappingUnit;
        }
        table10.addCell(createShipmentCell(giftWrappingUnit, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));

        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

        PdfPCell shipmentCell =
            createShipmentCell(json.getString("memo"), textBoldFont, Element.ALIGN_LEFT, 0, 10, 0, 0, 20f);
        shipmentCell.setLeading(5f, 1f);
        table10.addCell(shipmentCell);

        // table10.addCell(createShipmentCell(json.getString("memo"), textBoldFont, Element.ALIGN_LEFT, 0, 10, 0, 0,
        // 20f));
        String cushioning_unit = json.getString("cushioning_unit");
        String cushioningType = "";
        if (!StringTools.isNullOrEmpty(cushioning_unit)) {
            switch (cushioning_unit) {
                case "1":
                    cushioningType = "注文単位";
                    if (!StringTools.isNullOrEmpty(json.getString("cushioning_type"))) {
                        cushioningType += " " + json.getString("cushioning_type");
                    }
                    break;
                case "2":
                    cushioningType = "商品単位";
                    break;
                default:
                    break;
            }
        }
        if (!StringTools.isNullOrEmpty(cushioningType)) {
            cushioningType = "・緩衝材：" + cushioningType;
        }
        table10.addCell(createShipmentCell(cushioningType, textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

        table10.addCell(createShipmentCell(pointReplaceNull(json.getString("instructions_special_notes")),
            smalltextfont, Element.ALIGN_LEFT, 0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

        table10.addCell(createShipmentCell(pointReplaceNull(json.getString("bikou1")), textBoldFont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

        table10.addCell(createShipmentCell(pointReplaceNull(json.getString("bikou2")), textBoldFont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        // table10.addCell(createShipmentCell(json.getString("memo"), smalltextfont, Element.ALIGN_LEFT, 0, 6, 0, 0,
        // 20f));
        table10.addCell(createShipmentCell(pointReplaceNull(json.getString("bikou3")), textBoldFont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table10.addCell(createShipmentCell(pointReplaceNull(json.getString("bikou4")), textBoldFont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table10.addCell(createShipmentCell(pointReplaceNull(json.getString("bikou5")), textBoldFont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table10.addCell(createShipmentCell(pointReplaceNull(json.getString("bikou6")), textBoldFont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));
        table10.addCell(createShipmentCell(pointReplaceNull(json.getString("bikou7")), textBoldFont, Element.ALIGN_LEFT,
            0, 1, 0, 0, 20f));
        table10.addCell(createShipmentCell("", textBoldFont, Element.ALIGN_LEFT, 0, 1, 0, 0, 50f));

        doc.add(table10);

        // doc.newPage();
        // 总页数
        int pageNumber = writer.getPageNumber();
        // 作业指示书生成的页数
        int pageNumber2 = pageNumber - pageNumber1;


        doc.close();
        // a将A4输出变为A3格式(需要恢复A4删除下面两行和pdfPath后的"+A4"值即可)
        File f = new File(pdfPath);
        JSONObject pageJson = new JSONObject();
        pageJson.put("pageNumber2", pageNumber2);
        pageJson.put("pageNumber1", pageNumber1);
        JSONArray pageArray = new JSONArray();
        pageArray.add(pageJson);
        concatPDFs(new FileInputStream(file), new FileOutputStream(f), true, pageArray);
    }

    /**
     * @description: 出荷検品サイズPDF
     * @return: void
     * @date: 2020/09/14
     */
    public static void shipmentSizePdf(List<Ms010_product_size> list, String pdfPath, String path, String codeNull)
        throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        for (int i = 0; i < list.size(); i++) {
            // 将显示数据分为两列
            PdfPTable table = new PdfPTable(2);

            // 条形码构造
            Image image = Image.getInstance(path + codeNull + list.get(i).getName() + codeNull);
            PdfPCell barCodeCell = new PdfPCell();
            barCodeCell.setVerticalAlignment(Element.ALIGN_CENTER);
            barCodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            barCodeCell.setImage(image);
            barCodeCell.setBorder(0);
            barCodeCell.setFixedHeight(70);

            // 商品名称构造
            PdfPCell productNameCell = new PdfPCell();
            productNameCell.setVerticalAlignment(Element.ALIGN_CENTER);
            productNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            productNameCell.setBorder(0);
            productNameCell.setFixedHeight(70);
            productNameCell.setPhrase(new Phrase(list.get(i).getName(), midBlodFont));

            // 左右错行显示
            if (i % 2 != 0) {
                table.addCell(barCodeCell);
                table.addCell(productNameCell);
            } else {
                table.addCell(productNameCell);
                table.addCell(barCodeCell);
            }

            doc.add(table);

            // 添加横线
            Paragraph paragraph = new Paragraph();
            paragraph.setSpacingBefore(-5f);
            paragraph.setSpacingAfter(15f);
            paragraph.add(new Chunk(new LineSeparator()));
            doc.add(paragraph);
        }

        doc.close();
    }

    // 領収書
    public static void receiptPdf(String pdfPath, Tw200_shipment shipment, String imgPath) throws Exception {
        Document doc = new Document(PageSize.A4);
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        PdfPTable table = createTable(new float[] {
            330, 40, 150
        }, 520);
        table.addCell(createShipmentCell(" ", keyfont, Element.ALIGN_LEFT, 15, 0f));
        table.addCell(createShipmentCell(" ", keyfont, Element.ALIGN_LEFT, 15, 0f));
        String order_no = shipment.getOrder_no();
        if (StringTools.isNullOrEmpty(order_no)) {
            order_no = shipment.getShipment_plan_id();
        }
        table.addCell(createShipmentCell("No. " + order_no, textfont, Element.ALIGN_RIGHT, 13, 0f));
        table.addCell(createShipmentCell("領収書", headBoldfont, Element.ALIGN_LEFT, 15, 0f));
        table.addCell(createShipmentCell(" ", keyfont, Element.ALIGN_LEFT, 15, 0f));

        // 注文日時
        Timestamp shipmentDate = shipment.getOrder_datetime();
        if (StringTools.isNullOrEmpty(shipmentDate)) {
            // 若注文日时为空 选择 出庫予定日
            shipmentDate = shipment.getShipment_plan_date();
        }
        String date = "";
        if (!StringTools.isNullOrEmpty(shipmentDate)) {
            LocalDate localDate = shipmentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int monthValue = localDate.getMonthValue();
            int dayOfMonth = localDate.getDayOfMonth();
            String year = localDate.getYear() + "";
            String month = monthValue < 10 ? "0" + monthValue : monthValue + "";
            String day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
            date = year + "年" + month + "月" + day + "日";
        }

        table.addCell(createShipmentCell(date, textfont, Element.ALIGN_RIGHT, 15, 0f));
        table.addCell(createShipmentCell(" ", titleFont20, Element.ALIGN_LEFT, 15, 0f));
        table.addCell(createShipmentCell(" ", titleFont20, Element.ALIGN_LEFT, 15, 0f));
        table.addCell(createShipmentCell(" ", titleFont20, Element.ALIGN_RIGHT, 15, 0f));
        String name = "";
        // 如果注文者名不为空
        if (!StringTools.isNullOrEmpty(shipment.getOrder_family_name())) {
            name = shipment.getOrder_family_name();
            if (!StringTools.isNullOrEmpty(shipment.getOrder_first_name())) {
                name += shipment.getOrder_first_name();
            }
            name += " 様";
        } else if (!StringTools.isNullOrEmpty(shipment.getOrder_company())) {
            // 注文者名为空 判断注文者会社
            name = shipment.getOrder_company() + " 御中";
        } else if (!StringTools.isNullOrEmpty(shipment.getSurname())) {
            // 注文者名和 注文者会社 为空 判断配送先名
            name = shipment.getSurname() + " 様";
        } else if (!StringTools.isNullOrEmpty(shipment.getCompany())) {
            // 判断配送先会社名
            name = shipment.getCompany() + " 御中";
        }
        table.addCell(createShipmentCell(name, keyfont, Element.ALIGN_LEFT, 13, 0f));
        table.addCell(createShipmentCell(" ", keyfont, Element.ALIGN_LEFT, 15, 0f));
        table.addCell(createShipmentCell(" ", textfont, Element.ALIGN_RIGHT, 15, 0f));

        PdfPCell shipmentCell = createShipmentCell("￥ " + formatrPrict(shipment.getTotal_price() + "") + " -", keyfont,
            Element.ALIGN_LEFT, 13, 0f);
        shipmentCell.setFixedHeight(40f);
        table.addCell(shipmentCell);
        table.addCell(createShipmentCell(" ", keyfont, Element.ALIGN_LEFT, 15, 0f));
        table.addCell(createShipmentCell(" ", textfont, Element.ALIGN_RIGHT, 15, 0f));

        table.addCell(createShipmentCell("うち、消費税額: ￥ "
            + formatrPrict(shipment.getTotal_with_normal_tax() + "")
            + " -", smalltextfont, Element.ALIGN_LEFT, 15, 0f));
        table.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_LEFT, 15, 0f));
        table.addCell(createShipmentCell(" ", smalltextfont, Element.ALIGN_RIGHT, 15, 0f));

        PdfPTable table1 = createTable(new float[] {
            120, 150, 150, 100
        }, 520);
        table1.setSpacingBefore(30f);
        Image picture = Image.getInstance(imgPath);
        picture.setAlignment(Image.ALIGN_CENTER);
        picture.scalePercent(40);
        PdfPCell shipmentImgCell = createShipmentImgCell(picture, 0, 1, 5, 0, 0f);
        table1.addCell(createCell("但し、 ディフューザー代として", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell("株式会社Dr. Vranjes JAPAN", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(shipmentImgCell);
        table1.addCell(createCell("（クレジットカードでお支払）", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell("〒150-0021", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell("上記正に領収いたしました", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell("東京都東京都渋谷区恵比寿西2-11-9", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell("東光ホワイトビル4F", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        table1.addCell(createCell(" ", smalltextfont, Element.ALIGN_LEFT, 0, 0f));
        doc.add(table);
        doc.add(table1);
        doc.close();
    }

    public static String isNullOrEmpty(String value) {
        if (null == value || "".equals(value) || "null".equals(value)) {
            return "-";
        } else {
            return value;
        }
    }

    public static String judgmentNull(String value) {
        if (null == value || "".equals(value) || "null".equals(value)) {
            return " ";
        } else {
            return value;
        }
    }

    public static String pointReplaceNull(String value) {
        if (!StringTools.isNullOrEmpty(value)) {
            value = "・" + value;
        }
        return value;
    }

    public static String replaceNull(String value) {
        if (null == value || "".equals(value) || "null".equals(value)) {
            return "なし";
        } else {
            return value;
        }
    }

    public static String formatrPrict(String value) {
        if (!StringTools.isNullOrEmpty(value)) {
            return new DecimalFormat(",###.##").format(Long.valueOf(value));
        } else {
            return "0";
        }

    }

    /**
     * 两页A4打印到一页A3
     *
     * @param streamOfPDFFile
     * @param outputStream
     * @param paginate
     */
    public static void concatPDFs(InputStream streamOfPDFFile, OutputStream outputStream, boolean paginate,
        JSONArray pageArray) {

        Document document = new Document();
        try {

            // a读取PDF
            InputStream pdf = streamOfPDFFile;
            PdfReader pdfReader = new PdfReader(pdf);
            int totalPages = pdfReader.getNumberOfPages();

            // a创建PDF Writer
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte contentByte = writer.getDirectContent();

            Rectangle rec = pdfReader.getPageSize(1);
            // a新pdf为两个宽度
            Rectangle newRec = new Rectangle(0, 0, rec.getWidth() * 2, rec.getHeight());
            document.setPageSize(newRec);

            PdfImportedPage page;
            PdfImportedPage page2;
            int currentPageNumber = 0;

            int sum = 0;
            for (int i = 0; i < pageArray.size(); i++) {
                JSONObject jsonObject = pageArray.getJSONObject(i);
                // 纳品书的页数
                Integer number1 = jsonObject.getInteger("pageNumber1");
                // 作业指示书的页数
                Integer number2 = jsonObject.getInteger("pageNumber2");
                if (i == 0) {
                    // 如果为第一次循环
                    // 总数 = 纳品书的页数 + 作业指示书的页数
                    sum += number1 + number2;
                    // 计算出 两个pdf 哪个页数较多 ,按照较多的进行循环
                    int size = Math.max(number1, number2);
                    // 作业指示书开始计算的位置 （纳品书完的页数）
                    int pageCnt1 = number1;
                    for (int j = 0; j < size; j++) {
                        // 页数
                        currentPageNumber++;
                        document.newPage();
                        // 如果 纳品书页数 已经为0 则用空白页代替，
                        if (number1 != 0) {
                            // 原始第一页设置到左边
                            page = writer.getImportedPage(pdfReader, j + 1);
                            contentByte.addTemplate(page, 0, 0);
                            // 纳品书的页数--
                            number1--;
                        }
                        // 如果 作业指示书 已经为0 则用空白页代替，
                        if (number2 != 0) {
                            // 作业指示书 从 纳品书总页数 + 1 的开始
                            page2 = writer.getImportedPage(pdfReader, pageCnt1 + 1);
                            contentByte.addTemplate(page2, rec.getWidth(), 0);
                            // 下一次 作业指示书开始的位置
                            pageCnt1++;
                            // 作业指示书的页数--
                            number2--;
                        }
                        // a设置页码
                        if (paginate) {
                            contentByte.beginText();
                            contentByte.setFontAndSize(baseFont, 13);
                            contentByte.showTextAligned(PdfContentByte.ALIGN_CENTER,
                                "" + currentPageNumber + "/" + (totalPages / 2 + (totalPages % 2 == 0 ? 0 : 1)),
                                newRec.getWidth() / 2, 17, 0);
                            contentByte.endText();
                        }
                    }
                } else {
                    // 纳品书开始的位置
                    int pageCnt1 = sum;
                    // 作业指示书开始的位置
                    int pageCnt = number1;
                    // 纳品书的页数
                    number1 -= sum;
                    // 保持 纳品书 和 作业指示书 的页数不会变化 （因为上面的 都会经过计算减少， 所以需要保留原始值用在下面计算总页数）
                    int finalNum1 = number1;
                    int finalNum2 = number2;
                    // 选择纳品书 和 作业指示书 较大的一个 进行循环
                    int size = Math.max(number1, number2);
                    for (int j = 0; j < size; j++) {
                        // 页码
                        currentPageNumber++;
                        document.newPage();
                        // 如果纳品书页数为0 用空白页代替
                        if (number1 != 0) {
                            // 纳品书第一页 放到左边
                            page = writer.getImportedPage(pdfReader, pageCnt1 + 1);
                            contentByte.addTemplate(page, 0, 0);
                            // 纳品书页数--
                            number1--;
                            // 下一次纳品书开始的位置
                            pageCnt1++;
                        }
                        // 如果作业指示书页数为0 用空白页代替
                        if (number2 != 0) {
                            // 作业指示书第一页放到 右边
                            page2 = writer.getImportedPage(pdfReader, pageCnt + 1);
                            contentByte.addTemplate(page2, rec.getWidth(), 0);
                            // 下一次作业指示书开始的位置
                            pageCnt++;
                            // 作业指示书的页数--
                            number2--;
                        }
                        // a设置页码
                        if (paginate) {
                            contentByte.beginText();
                            contentByte.setFontAndSize(baseFont, 13);
                            contentByte.showTextAligned(PdfContentByte.ALIGN_CENTER,
                                "" + currentPageNumber + "/" + (totalPages / 2 + (totalPages % 2 == 0 ? 0 : 1)),
                                newRec.getWidth() / 2, 17, 0);
                            contentByte.endText();
                        }
                    }
                    sum += (finalNum1 + finalNum2);

                }
            }

            // 原始写法 如果上面的出错 可以紧急对应 将上面的注释掉用这段代码
            // a舍弃奇数的最后一页, 删除该设置后面不受影响
            /*
             * for (int pageIndex = 0; pageIndex < totalPages; pageIndex += 2) {
             * document.newPage();
             * currentPageNumber++;
             * 
             * // a原始第一页设置到左边
             * page = writer.getImportedPage(pdfReader, pageIndex + 1);
             * contentByte.addTemplate(page, 0, 0);
             * 
             * // a第二页设置到右边
             * if (pageIndex + 2 <= totalPages) {
             * page2 = writer.getImportedPage(pdfReader, pageIndex + 2);
             * contentByte.addTemplate(page2, rec.getWidth(), 0);
             * }
             * 
             * // a设置页码
             * if (paginate) {
             * contentByte.beginText();
             * contentByte.setFontAndSize(baseFont, 13);
             * contentByte.showTextAligned(PdfContentByte.ALIGN_CENTER,
             * "" + currentPageNumber + "/" + (totalPages / 2 + (totalPages % 2 == 0 ? 0 : 1)),
             * newRec.getWidth() / 2, 17, 0);
             * contentByte.endText();
             * }
             * }
             */
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static class Footer extends PdfPageEventHelper
    {
        public static PdfPTable footer;

        @SuppressWarnings("static-access")
        public Footer(PdfPTable footer) {
            this.footer = footer;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // 把页脚表格定位
            footer.writeSelectedRows(0, -1, 38, 50, writer.getDirectContent());
        }
    }
}
