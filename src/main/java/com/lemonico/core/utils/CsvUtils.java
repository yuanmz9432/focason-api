package com.lemonico.core.utils;



import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * CSVツール
 *
 * @since 1.0.0
 */
public class CsvUtils
{

    public static File read(HttpServletRequest req, MultipartFile file) throws IllegalStateException, IOException {
        String fileName = System.currentTimeMillis() + file.getOriginalFilename();
        // a获取当前项目的真实路径
        String path = req.getServletContext().getRealPath("");
        String realPath = (String) path.subSequence(0, path.length() - 7);
        // a获取当前的年份+月份
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        String datePath = year + "" + month;
        // a拼接图片保存路径
        String destFileName = realPath + "resources" + File.separator + "static" + File.separator + "csv"
            + File.separator + datePath + File.separator + fileName;
        // a第一次运行的时候创建文件夹
        File destFile = new File(destFileName);
        destFile.getParentFile().mkdirs();
        // a把浏览器上传的文件复制到目标路径
        file.transferTo(destFile);
        return destFile;
    }

    public static void write(String filePath, String csvHeader, List<String[]> csvData, Boolean headerFlag) {
        try {
            // a输出的CSV文件
            File outFile = new File(filePath);

            OutputStreamWriter write = new OutputStreamWriter(Files.newOutputStream(outFile.toPath()), "SJIS");
            BufferedWriter writer = new BufferedWriter(write);
            if (headerFlag) {
                writer.write(csvHeader);
                writer.newLine();
            }
            if (csvData.size() > 0) {
                for (String[] csvDatum : csvData) {
                    StringBuilder tmp = new StringBuilder();
                    int arrLength = csvDatum.length;
                    for (int j = 0; j < arrLength; j++) {
                        if (StringTools.isNullOrEmpty(csvDatum[j])) {
                            tmp.append("\"\"");
                        } else {
                            tmp.append("\"").append(csvDatum[j]).append("\"");
                        }
                        if (j != arrLength - 1) {
                            tmp.append(",");
                        }
                    }
                    writer.write(tmp.toString());
                    writer.newLine();
                }
            }
            writer.flush();
            write.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
