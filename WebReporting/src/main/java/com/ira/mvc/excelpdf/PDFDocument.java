package com.ira.mvc.excelpdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created for JavaStudy.ru on 23.02.2016.
 */
public class PDFDocument extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(
            Map<String, Object> model,
            Document document,
            PdfWriter writer,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {


        PdfPTable table = new PdfPTable(3);
        PdfPCell header1 = new PdfPCell(new Phrase("Name"));
        PdfPCell header2 = new PdfPCell(new Phrase("Weight"));
        PdfPCell header3 = new PdfPCell(new Phrase("Color"));
        header1.setHorizontalAlignment(Element.ALIGN_LEFT);
        header2.setHorizontalAlignment(Element.ALIGN_LEFT);
        header3.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(header1);
        table.addCell(header2);
        table.addCell(header3);

        //Get data from model
        List<Cat> cats = (List<Cat>) model.get("modelObject");
        for (Cat cat : cats) {
            table.addCell(cat.getName());
            table.addCell(String.valueOf(cat.getWeight()));
            table.addCell(cat.getColor());
        }
        document.add(table);
    }
}
