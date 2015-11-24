package com.report.bin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.report.data.TDataList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.report.book.ExcelBook;
import com.report.book.TF1Book6;
import com.report.data.TDataSet;
import com.report.work.TfrmF1Container;

/**
 * Hello world!
 * 
 */
public class App {
	
	public static void addData(Workbook book){
		Cell c = null;
		 
        //Cell style for header row
        CellStyle cs = book.createCellStyle();
        cs.setFillForegroundColor(IndexedColors.LIME.getIndex());
        cs.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        Font f = book.createFont();
        f.setBoldweight(Font.BOLDWEIGHT_BOLD);
        f.setFontHeightInPoints((short) 12);
        cs.setFont(f);

        //New Sheet
        Sheet sheet1 = book.createSheet("myData");
	}

	public static TDataSet initDataSetHeader(){
		List<String> head = new ArrayList<String>();
		head.add("d1");

		List<List<Object>> data = new ArrayList<List<Object>>();

		List<Object> row1 = new ArrayList<Object>();
		row1.add(new Date());
		data.add(row1);

		return new TDataList("dataSetMaster1" , head, data);
	}

	public static TDataSet initDataSetMaster(){
		List<String> head = new ArrayList<String>();
		head.add("data");
		head.add("name");
		head.add("cant");
		head.add("suma");

		List<List<Object>> data = new ArrayList<List<Object>>();

		List<Object> row1 = new ArrayList<Object>();
		row1.add(new Date());
		row1.add("name1");
		row1.add(17);
		row1.add(285);
		data.add(row1);

		List<Object> row2 = new ArrayList<Object>();
		row2.add(new Date());
		row2.add("name asdasd asd asd asdasd");
		row2.add(2);
		row2.add(170);
		data.add(row2);

		return new TDataList("dataSetMaster1" , head, data);
	}

	public static void main(String[] args) throws Exception {
		File from = new File("D:/tmp/rep1.xlsx");
		String folder = from.getParent();
		String tempName = java.util.UUID.randomUUID().toString();

		TF1Book6 book1 = new ExcelBook("D:/tmp/rep1.xlsx");
		TF1Book6 book2 = new ExcelBook(folder + "/" + tempName + ".xlsx");
		TDataSet ds0 = initDataSetHeader();
		TDataSet ds1 = initDataSetMaster();
		
		TfrmF1Container container = new TfrmF1Container();
		container.OpenF1Rep(book1, book2, ds1, null, "pReportTitle", ds0, null);

		book2.save();
		

	}
}
