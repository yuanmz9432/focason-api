package com.lemonico.core.utils;



import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Excelツール
 *
 * @since 1.0.0
 */
public class ExcelUtils
{

    public static String getCellValue(Cell cell) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        NumberFormat numberFormat = NumberFormat.getInstance();
        String value = "";
        if (null == cell) {
            return value;
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                boolean cellDateFormatted = HSSFDateUtil.isCellDateFormatted(cell);
                if (cellDateFormatted) {
                    value = dateFormat.format(cell.getDateCellValue());
                } else {
                    String format = numberFormat.format(cell.getNumericCellValue());
                    value = format;
                    if (format.indexOf(",") > 0) {
                        value = format.replace(",", "");
                    }
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue() + "";
                break;
            default:
                break;
        }
        return value;
    }
}
