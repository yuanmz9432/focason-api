package com.lemonico.core.utils;



import com.google.common.base.Splitter;
import com.lemonico.core.exception.LcBadRequestException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * ZIPツール
 *
 * @since 1.0.0
 */
public class ZipUtil
{

    /**
     * ファイルを解凍する
     *
     * @param zipFile 対象ファイル
     * @param path パス
     * @return 出力パス
     * @throws IOException IO異常
     */
    public static List<String> unzipFile(File zipFile, String path) throws IOException {
        long singleFileMaxSize = 2000000L;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        ArrayList<String> paths = new ArrayList<>();
        // 文字コードを指定して、ZIPファイルを作成する
        ZipFile zip = new ZipFile(zipFile, Charset.forName("SJIS"));
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry zipEntry = entries.nextElement();
            // 判断文件大小是否符合要求
            if (zipEntry.getSize() >= singleFileMaxSize) {
                throw new LcBadRequestException("画像サイズが2MB以上にはアプロードできません。");
            }
            String zipEntryName = zipEntry.getName();
            List<String> list = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(zipEntryName);
            String name = list.get(list.size() - 1);
            if (name != null) {
                InputStream inputStream = zip.getInputStream(zipEntry);
                String outPath = (path + name).replaceAll("\\*", "/");
                File outFile = new File(outPath.substring(0, outPath.lastIndexOf("/")));
                // 判断路径是否存在
                if (!outFile.exists()) {
                    outFile.mkdirs();
                }
                // 判断文件全路径是否为文件夹，如果已经上传则不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                List<String> split = Splitter.on(".").omitEmptyStrings().trimResults().splitToList(outPath);
                // 如果解压后不包含图片，则不保存
                if (split.size() > 1) {
                    paths.add(outPath);
                }
                FileOutputStream out = new FileOutputStream(outPath);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) > 0) {
                    out.write(bytes, 0, len);
                }
                inputStream.close();
                out.close();
            }
        }
        zip.close();
        return paths;
    }
}
