package com.mobile.app.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sewoo.jpos.command.ZPLConst;
import com.sewoo.jpos.printer.LKPrint;
//import com.sewoo.jpos.printer.ZPLPrinter;
import com.sewoo.jpos.printer.ZPLRFIDPrinter;

public class ZPLSample2
{
	private ZPLRFIDPrinter zplPrinter;
	
	public ZPLSample2()
	{	
		zplPrinter = new ZPLRFIDPrinter();
//		zplPrinter = new ZPLRFIDPrinter("EUC-KR");
	}
	
	protected void blackMarkPaper()
	{
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_BLACKMARK, 384, 400);
	}
	
	protected void gapLabelPaper()
	{
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_GAP, 384, 400);		
	}
	
	protected void continuousPaper()
	{
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 400);
	}
	
	public void textTest(int count) throws UnsupportedEncodingException
	{
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 480);

		zplPrinter.startPage();
		zplPrinter.setInternationalFont(0);

		zplPrinter.printText(ZPLConst.FONT_A, ZPLConst.ROTATION_0, 15, 12, 0, 0, "FontA 0123");
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 15, 12, 0, 30, "FontB 0123");
		zplPrinter.printText(ZPLConst.FONT_C, ZPLConst.ROTATION_0, 15, 12, 0, 60, "FontC 0123");
		zplPrinter.printText(ZPLConst.FONT_D, ZPLConst.ROTATION_0, 15, 12, 0, 90, "FontD 0123");
		zplPrinter.printText(ZPLConst.FONT_E, ZPLConst.ROTATION_0, 15, 12, 0, 120, "FontE 0123");
		zplPrinter.printText(ZPLConst.FONT_F, ZPLConst.ROTATION_0, 15, 12, 0, 160, "FontF 0123");
		zplPrinter.printText(ZPLConst.FONT_G, ZPLConst.ROTATION_0, 15, 12, 0, 210, "FontG 01");
		zplPrinter.printText(ZPLConst.FONT_H, ZPLConst.ROTATION_0, 15, 12, 0, 300, "FontH 01234567");

/*
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 200);
		zplPrinter.printText('2', ZPLConst.ROTATION_0, 60, 60, 0, 40, "ABC가나다abc");
*/
		zplPrinter.endPage(count);
	}
	
	public void imageTest(int count) throws IOException
	{
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 340);

		zplPrinter.startPage();
		zplPrinter.setInternationalFont(0);
/*
		zplPrinter.printImage("//sdcard//temp//test//sample_2.jpg", 1, 1);
    	zplPrinter.printImage("//sdcard//temp//test//sample_3.jpg", 100, 20);
    	zplPrinter.printImage("//sdcard//temp//test//sample_4.jpg", 120, 155);
*/
    	zplPrinter.printImage("//sdcard//temp//test//sample_4.jpg", 0, 0);
    	zplPrinter.endPage(count);
	}
	
	public void geometryTest(int count)
	{
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 300);

		zplPrinter.startPage();
		zplPrinter.setInternationalFont(0);

		zplPrinter.printCircle(40,40, 70, 2, ZPLConst.LINE_COLOR_B);
		zplPrinter.printDiagonalLine(30, 30, 100, 100, 7, ZPLConst.LINE_COLOR_B, ZPLConst.DIAGONAL_L);
		zplPrinter.printEllipse(10, 10, 300, 200, 2, ZPLConst.LINE_COLOR_B);
		zplPrinter.printRectangle(120, 10, 120, 80, 10, ZPLConst.LINE_COLOR_B, 0);
		zplPrinter.printRectangle(120, 100, 120, 80, 10, ZPLConst.LINE_COLOR_B, 8);
		zplPrinter.endPage(1);
	}
	
	public void barcode1DTest(int count) throws UnsupportedEncodingException
    {
		String data = "123456789012";
		ArrayList<String> byParam = new ArrayList<String>();
		byParam.add("2");
		byParam.add("3");
		byParam.add("20");
		
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 1600);

		zplPrinter.startPage();
		zplPrinter.setInternationalFont(0);

		zplPrinter.setBarcodeField(byParam);
				
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 70, "Code11");
		zplPrinter.printBarcode(ZPLConst.BARCODE_Code11, null, 10, 10, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 150, "Code128");
		zplPrinter.printBarcode(ZPLConst.BARCODE_Code128, null, 10, 100, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 250, "Code39");
		zplPrinter.printBarcode(ZPLConst.BARCODE_Code39, null, 10, 200, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 350, "Code93");
		zplPrinter.printBarcode(ZPLConst.BARCODE_Code93, null, 10, 300, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 450, "EAN13");
		zplPrinter.printBarcode(ZPLConst.BARCODE_EAN13, null, 10, 400, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 550, "EAN8");
		zplPrinter.printBarcode(ZPLConst.BARCODE_EAN8, null, 10, 500, "12345");
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 650, "Industrial 2OF5");
		zplPrinter.printBarcode(ZPLConst.BARCODE_Industrial_2OF5, null, 10, 600, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 750, "Interleaved 2OF5");
		zplPrinter.printBarcode(ZPLConst.BARCODE_Interleaved_2OF5, null, 10, 700, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 850, "LOGMARS");
		zplPrinter.printBarcode(ZPLConst.BARCODE_LOGMARS, null, 10, 800, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 950, "MSI");
		zplPrinter.printBarcode(ZPLConst.BARCODE_MSI, null, 10, 900, data);
				
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 1050, "PlanetCode");
		zplPrinter.printBarcode(ZPLConst.BARCODE_PlanetCode, null, 10, 1000, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 1150, "Plessey");
		zplPrinter.printBarcode(ZPLConst.BARCODE_Plessey, null, 10, 1100, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 1250, "POSTNET");
		zplPrinter.printBarcode(ZPLConst.BARCODE_POSTNET, null, 10, 1200, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 1350, "Standard 2OF5");
		zplPrinter.printBarcode(ZPLConst.BARCODE_Standard_2OF5, null, 10, 1300, data);
				
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 1450, "UPCA");
		zplPrinter.printBarcode(ZPLConst.BARCODE_UPCA, null, 10, 1400, data);
		
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 10, 10, 0, 1550, "UPCE");
		zplPrinter.printBarcode(ZPLConst.BARCODE_UPCE, null, 10, 1500, data);

		zplPrinter.endPage(count);
    }
    
	public void barcode2DTest(int count) throws UnsupportedEncodingException
	{
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 400);

		zplPrinter.startPage();
		zplPrinter.setInternationalFont(0);
		
		// QRCode
		// Barcode Properties		
		ArrayList<String> barParam = new ArrayList<String>();
		barParam.add("N");
		barParam.add("2");
		barParam.add("5");
		// Print Barcode
		zplPrinter.printBarcode(ZPLConst.BARCODE_QRCode, barParam, 10, 220, "MM,AAC-42");
		// PDF-417
		String data = "ABCDEFGHIJKLMNOPQRSTUVWXTZ";
		ArrayList<String> byParam = new ArrayList<String>();
		byParam.add("2");
		byParam.add("3");
		barParam = new ArrayList<String>();
		barParam.add("N");
		barParam.add("5");
		barParam.add("5");
		barParam.add("");
		barParam.add("23");
		barParam.add("N");
		zplPrinter.setBarcodeField(byParam);
		zplPrinter.printBarcode(ZPLConst.BARCODE_PDF417, barParam, 10, 10, data);
		// DataMatrix
		// Barcode Properties		
		barParam = new ArrayList<String>();
		barParam.add("N");
		barParam.add("10");
		barParam.add("200");
		// Print Barcode
		zplPrinter.printBarcode(ZPLConst.BARCODE_DataMatrix, barParam, 200, 220, "ABDFFEWGSERSHSRGRR");
		
		zplPrinter.endPage(count);
	}

	public void printUTF8(int count) throws UnsupportedEncodingException
	{
		zplPrinter.setCharSet("UTF-8");
		
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 460);
		zplPrinter.startPage();
		zplPrinter.setInternationalFont(24);
		
		zplPrinter.printText(ZPLConst.FONT_1, ZPLConst.ROTATION_0, 16, 16, 0, 0, "汉语 漢語");
		zplPrinter.printText(ZPLConst.FONT_1, ZPLConst.ROTATION_0, 20, 20, 0, 30, "汉语 漢語");
		zplPrinter.printText(ZPLConst.FONT_1, ZPLConst.ROTATION_0, 24, 24, 0, 60, "汉语 漢語");
		zplPrinter.printText(ZPLConst.FONT_1, ZPLConst.ROTATION_0, 32, 32, 0, 90, "汉语 漢語");
		zplPrinter.printText(ZPLConst.FONT_1, ZPLConst.ROTATION_0, 40, 40, 0, 130, "汉语 漢語");
		zplPrinter.printText(ZPLConst.FONT_1, ZPLConst.ROTATION_0, 48, 48, 0, 180, "汉语 漢語");
		zplPrinter.printText(ZPLConst.FONT_1, ZPLConst.ROTATION_0, 60, 60, 0, 240, "汉语 漢語");
		zplPrinter.printText(ZPLConst.FONT_1, ZPLConst.ROTATION_0, 80, 80, 0, 320, "汉语 漢語");

		zplPrinter.endPage(count);

		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 480);
		zplPrinter.startPage();
		zplPrinter.setInternationalFont(0);

		zplPrinter.printText(ZPLConst.FONT_A, ZPLConst.ROTATION_0, 15, 12, 0, 0, "FontA 0123");
		zplPrinter.printText(ZPLConst.FONT_B, ZPLConst.ROTATION_0, 15, 12, 0, 30, "FontB 0123");
		zplPrinter.printText(ZPLConst.FONT_C, ZPLConst.ROTATION_0, 15, 12, 0, 60, "FontC 0123");
		zplPrinter.printText(ZPLConst.FONT_D, ZPLConst.ROTATION_0, 15, 12, 0, 90, "FontD 0123");
		zplPrinter.printText(ZPLConst.FONT_E, ZPLConst.ROTATION_0, 15, 12, 0, 120, "FontE 0123");
		zplPrinter.printText(ZPLConst.FONT_F, ZPLConst.ROTATION_0, 15, 12, 0, 160, "FontF 0123");
		zplPrinter.printText(ZPLConst.FONT_G, ZPLConst.ROTATION_0, 15, 12, 0, 210, "FontG 01");
		zplPrinter.printText(ZPLConst.FONT_H, ZPLConst.ROTATION_0, 15, 12, 0, 300, "FontH 01234567");

		zplPrinter.endPage(count);
	}
	
	public void imageObject(int count) throws IOException
	{
		zplPrinter.setupPrinter(ZPLConst.ROTATION_180, ZPLConst.SENSE_CONTINUOUS, 384, 340);

		zplPrinter.startPage();
		zplPrinter.setInternationalFont(0);

		Bitmap _bitmap = BitmapFactory.decodeFile("//sdcard//temp//test//sample_4.jpg");
		zplPrinter.printImage(_bitmap, 0, 0);
    	zplPrinter.endPage(count);
	}
}
