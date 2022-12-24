package com.lemonico.core.utils.DottedLine;



import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * @className: DottedTop
 * @description: 顶部实线虚化
 * @date: 2021/1/18 13:02
 **/
public class TopDottedLine implements PdfPCellEvent
{

    @Override
    public void cellLayout(PdfPCell pdfPCell, Rectangle rectangle, PdfContentByte[] pdfContentBytes) {
        PdfContentByte pdfContentByte = pdfContentBytes[PdfPTable.LINECANVAS];
        pdfContentByte.saveState();
        pdfContentByte.setLineWidth(0.1f);
        pdfContentByte.setLineDash(new float[] {
            2.0f, 2.0f
        }, 0);
        pdfContentByte.moveTo(rectangle.getLeft(), rectangle.getTop());
        pdfContentByte.lineTo(rectangle.getRight(), rectangle.getTop());
        pdfContentByte.stroke();
        pdfContentByte.restoreState();
    }
}
