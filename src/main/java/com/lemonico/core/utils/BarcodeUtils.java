package com.lemonico.core.utils;



import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

/**
 * Barcode生成ツール
 *
 * @since 1.0.0
 */
public class BarcodeUtils
{

    /**
     * バーコードを生成する
     *
     * @param barcode バーコード
     * @param path バーコード生成パス
     * @param width バーコード幅
     * @since 1.0.0
     */
    public static void generateCode128Barcode(String barcode, String path, Double width) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            Code128Bean bean = new Code128Bean();
            // Set the width
            bean.setModuleWidth(width);
            bean.doQuietZone(false);
            // The output stream
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                Files.newOutputStream(file.toPath()), "image/png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            // To generate the barcode
            bean.generateBarcode(canvas, barcode);
            canvas.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
