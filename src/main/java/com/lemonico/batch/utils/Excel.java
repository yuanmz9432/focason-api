package com.lemonico.batch.utils;



import com.lemonico.batch.bean.*;
import com.lemonico.common.bean.Tw200_shipment;
import com.lemonico.common.bean.Tw300_stock;
import com.lemonico.common.bean.Tw301_stock_history;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.StringTools;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

/**
 * @className: Excel
 * @description: 生成错误数据的excel
 * @date: 2021/12/10 10:50
 **/
public class Excel
{

    private static final List<String> heads;
    private static final List<String> sheetName;

    static {
        sheetName = new ArrayList<>();
        heads = new ArrayList<>();
        sheetName.add("⑥ 300,405实际数确认");
        sheetName.add("⑦ 理论在库数确认");
        sheetName.add("⑧ 在库数为负数");
        sheetName.add("② 未作业TW212存在");
        sheetName.add("③ 作业中TW212不存在");
        sheetName.add("④ 405依赖中数统计");
        sheetName.add("⑨ 出库商品删除");
        sheetName.add("① tw201 del_flg确认");
        sheetName.add("⑤ 300依赖中数统计");
        sheetName.add("⑩ 301重复错误数据");
        heads.add("倉庫コード, 店舗ID, 商品ID, tw300_实际在库数, mw405_实际在库数, 作成者, 作成日時, 更新者, 更新日時");
        heads.add("店舗ID, 商品ID, mw405_実在庫数, mw405_理論在庫数, mw405_出庫依頼中数, mw405_不可配送数");
        heads.add("店舗ID, 商品ID, tw300_実在庫数, tw300_理論在庫数, tw300_出庫依頼中数, tw300_不可配送数");
        heads.add("出庫依頼ID, 店舗ID, 出庫依頼日, 出庫予定日, 出庫ステータス");
        heads.add("出庫依頼ID, 店舗ID, 出庫依頼日, 出庫予定日, 出庫ステータス");
        heads.add("ロケーションID, 店舗ID, 商品ID, mw405_在庫数, mw405_出庫依頼中数, tw212_引当数, mw405_不可配送数");
        heads.add("店舗ID, 商品ID, 出庫依頼ID, 出庫ステータス, mc100削除フラグ, tw201削除フラグ");
        heads.add("出庫依頼ID, 出庫ステータス, 削除フラグ, 店铺CD, 商品ID, 出庫依頼数, tw201削除フラグ");
        heads.add("店舗ID, 商品ID, 実在庫数, tw300出庫依頼中数, tw200出庫依頼数");
        heads.add("在庫履歴ID, 依頼ID, 店舗ID, 商品ID, タイプ, 数量, 変更前のアイテム数, 変更後のアイテム数, 作成者, 作成日時");
    }

    /**
     * @param inconsistentInventories : 在库表和货架表在库数不一致的数据
     * @param numberInStock : 在库表的实际在库，理论在库，依赖中，不可配送在库数对不上
     * @param inventoryTable : 实际在库，依赖中，不可配送在库数出现负数
     * @param noWorkStarted : 没有出荷作业开始，但是tw212状态为出荷作业中的数据
     * @param noDataInTheJob : 出荷作业中，货架履历表没有数据
     * @param mw405ErrorDataList : 出库货架详细表里面的引当数总和和相对应的货架上面的依赖数不一致
     * @param abnormalProducts : 还在出库的商品被删除
     * @param errorRequestCnts : 查询在库表依赖中数和出库详细表统计出来的依赖数不一致失败
     * @param tw200Tw201DifferentDelFlgs : 查询出库依赖和出库依赖明细的删除状态不一致失败
     * @param shipmentErrorHistory : 在库履历重复的错误数据
     * @param filePath : 生成文件路径
     * @description: 生成错误数据csv
     * @return: void
     * @date: 2021/12/13 15:31
     */
    public static void createCheckDataExcel(List<Tw300Mw405InconsistentInventory> inconsistentInventories,
        List<Tw300_stock> numberInStock,
        List<Tw300_stock> inventoryTable, List<Tw200_shipment> noWorkStarted,
        List<Tw200_shipment> noDataInTheJob, List<Mw405ErrorData> mw405ErrorDataList,
        List<AbnormalProducts> abnormalProducts,
        List<Tw200Tw201DifferentDelFlg> tw200Tw201DifferentDelFlgs,
        List<Tw300ErrorRequestCnt> errorRequestCnts,
        List<Tw301_stock_history> shipmentErrorHistory,
        String filePath) {
        try {
            // 创建工作簿
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFCellStyle cellStyle = cellStyle(workbook);

            for (int i = 0; i < sheetName.size(); i++) {
                String name = sheetName.get(i);
                // 创建工作表
                XSSFSheet sheet = workbook.createSheet(name);
                sheet.setForceFormulaRecalculation(true);
                // 创建表头
                XSSFRow xssfRow = sheet.createRow(0);
                String[] headArray = heads.get(i).split(",");
                for (int j = 0; j < headArray.length; j++) {
                    createCell(xssfRow, j, headArray[j], cellStyle);
                }
                int index = 1;
                switch (i) {
                    case 0:
                        for (Tw300Mw405InconsistentInventory data : inconsistentInventories) {
                            XSSFRow sheetRow = sheet.createRow(index);
                            int cellIndex = 0;
                            List<String> list =
                                Arrays.asList(data.getWarehouse_cd(), data.getClient_id(), data.getProduct_id(),
                                    toString(data.getInventory_cnt()), toString(data.getMw405_stock_cnt()),
                                    data.getIns_usr(),
                                    data.getIns_date(), data.getUpd_usr(), data.getUpd_date());
                            for (String value : list) {
                                createCell(sheetRow, cellIndex, value, cellStyle);
                                cellIndex++;
                            }
                            index++;
                        }
                        break;
                    case 1:
                        stockCreateCell(numberInStock, cellStyle, sheet);
                        break;
                    case 2:
                        stockCreateCell(inventoryTable, cellStyle, sheet);
                        break;
                    case 3:
                        shipmentCreateCell(noWorkStarted, cellStyle, sheet);
                        break;
                    case 4:
                        shipmentCreateCell(noDataInTheJob, cellStyle, sheet);
                        break;
                    case 5:
                        for (Mw405ErrorData mw405ErrorData : mw405ErrorDataList) {
                            XSSFRow sheetRow = sheet.createRow(index);
                            int cellIndex = 0;
                            List<String> list = Arrays.asList(mw405ErrorData.getLocation_id(),
                                mw405ErrorData.getClient_id(),
                                mw405ErrorData.getProduct_id(), toString(mw405ErrorData.getStock_cnt()),
                                toString(mw405ErrorData.getRequesting_cnt()), toString(mw405ErrorData.getReserve_cnt()),
                                toString(mw405ErrorData.getNot_delivery()));
                            for (String value : list) {
                                createCell(sheetRow, cellIndex, value, cellStyle);
                                cellIndex++;
                            }
                            index++;
                        }
                        break;
                    case 6:
                        for (AbnormalProducts product : abnormalProducts) {
                            XSSFRow sheetRow = sheet.createRow(index);
                            int cellIndex = 0;
                            List<String> list = Arrays.asList(product.getClient_id(), product.getProduct_id(),
                                product.getShipment_plan_id(),
                                toString(product.getShipment_status()), toString(product.getMc100_del()),
                                toString(product.getTw201_del()));
                            for (String value : list) {
                                createCell(sheetRow, cellIndex, value, cellStyle);
                                cellIndex++;
                            }
                            index++;
                        }
                        break;
                    case 7:
                        for (Tw200Tw201DifferentDelFlg data : tw200Tw201DifferentDelFlgs) {
                            XSSFRow sheetRow = sheet.createRow(index);
                            int cellIndex = 0;
                            List<String> list =
                                Arrays.asList(data.getShipment_plan_id(), toString(data.getShipment_status()),
                                    toString(data.getDel_flg()), data.getClient_id(), data.getProduct_id(),
                                    toString(data.getProduct_plan_cnt()), toString(data.getTw201_del_flg()));
                            for (String value : list) {
                                createCell(sheetRow, cellIndex, value, cellStyle);
                                cellIndex++;
                            }
                            index++;
                        }
                        break;
                    case 8:
                        for (Tw300ErrorRequestCnt data : errorRequestCnts) {
                            XSSFRow sheetRow = sheet.createRow(index);
                            int cellIndex = 0;
                            List<String> list = Arrays.asList(data.getClient_id(), data.getProduct_id(),
                                toString(data.getInventory_cnt()),
                                toString(data.getRequesting_cnt()), toString(data.getProduct_plan_cnt()));
                            for (String value : list) {
                                createCell(sheetRow, cellIndex, value, cellStyle);
                                cellIndex++;
                            }
                            index++;
                        }
                        break;
                    case 9:
                        for (Tw301_stock_history history : shipmentErrorHistory) {
                            XSSFRow sheetRow = sheet.createRow(index);
                            int cellIndex = 0;
                            Date insDate = history.getIns_date();
                            String date = CommonUtils.transformDate(insDate);
                            List<String> list = Arrays.asList(history.getHistory_id(), history.getPlan_id(),
                                history.getClient_id(),
                                history.getProduct_id(), toString(history.getType()), toString(history.getQuantity()),
                                toString(history.getBefore_num()), toString(history.getAfter_num()),
                                toString(history.getIns_usr()), date);
                            for (String value : list) {
                                createCell(sheetRow, cellIndex, value, cellStyle);
                                cellIndex++;
                            }
                            index++;
                        }
                        break;
                    default:
                        break;
                }
                setSizeColumn(sheet, 10);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @description: 出库数据填入单元格
     * @return: void
     * @date: 2021/12/13 15:31
     */
    public static void shipmentCreateCell(List<Tw200_shipment> noDataInTheJob, XSSFCellStyle cellStyle,
        XSSFSheet sheet) {
        int index = 1;
        for (Tw200_shipment shipment : noDataInTheJob) {
            XSSFRow sheetRow = sheet.createRow(index);
            int cellIndex = 0;
            List<String> list = Arrays.asList(shipment.getShipment_plan_id(), shipment.getClient_id(),
                toString(shipment.getRequest_date()),
                toString(shipment.getShipment_plan_date()), toString(shipment.getShipment_status()));
            for (String value : list) {
                createCell(sheetRow, cellIndex, value, cellStyle);
                cellIndex++;
            }
            index++;
        }
    }

    /**
     * @description: 在库数据填入单元格
     * @return: void
     * @date: 2021/12/13 15:30
     */
    public static void stockCreateCell(List<Tw300_stock> inventoryTable, XSSFCellStyle cellStyle, XSSFSheet sheet) {
        int index = 1;
        for (Tw300_stock stock : inventoryTable) {
            XSSFRow sheetRow = sheet.createRow(index);
            int cellIndex = 0;
            List<String> list =
                Arrays.asList(stock.getClient_id(), stock.getProduct_id(), toString(stock.getInventory_cnt()),
                    toString(stock.getAvailable_cnt()), toString(stock.getRequesting_cnt()),
                    toString(stock.getNot_delivery()));
            for (String value : list) {
                createCell(sheetRow, cellIndex, value, cellStyle);
                cellIndex++;
            }
            index++;
        }
    }

    /**
     * @description: 转化为string
     * @return: java.lang.String
     * @date: 2021/12/13 15:30
     */
    public static String toString(Object object) {
        String result = "";
        if (!StringTools.isNullOrEmpty(object)) {
            result = String.valueOf(object);
        }
        return result;
    }

    /**
     * @description: 设置单元格样式
     * @return: org.apache.poi.xssf.usermodel.XSSFCellStyle
     * @date: 2021/12/13 15:30
     */
    public static XSSFCellStyle cellStyle(XSSFWorkbook xssfWorkbook) {
        XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
        // 下边框线
        cellStyle.setBorderBottom(BorderStyle.THIN);
        // 上边框线
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 左边框线
        cellStyle.setBorderLeft(BorderStyle.THIN);
        // 右边框线
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }

    /**
     * @description: 创建单元格
     * @return: void
     * @date: 2021/12/13 15:30
     */
    public static void createCell(XSSFRow sheetRow, int column, String value, XSSFCellStyle cellStyle) {
        XSSFCell cell = sheetRow.createCell(column);
        if (!StringTools.isNullOrEmpty(cellStyle)) {
            cell.setCellStyle(cellStyle);
        }
        cell.setCellValue(value);
    }

    /**
     * @description: 自动适应列宽
     * @return: void
     * @date: 2021/12/13 15:29
     */
    private static void setSizeColumn(XSSFSheet sheet, int size) {
        for (int columnNum = 0; columnNum < size; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                XSSFRow currentRow;
                // 当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                    XSSFCell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }

}
