package com.mobile.app.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sewoo.jpos.command.CPCLConst;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.image.ImageLoader;
import com.sewoo.jpos.image.MobileImageConverter;
import com.sewoo.jpos.printer.CPCLPrinter;

public class CPCLSample3
{
	private final String TAG = "StatusTimeChecker";

	private CPCLPrinter cpclPrinter;
	private int paperType;
	
	public CPCLSample3()
	{
		cpclPrinter = new CPCLPrinter();
		paperType = CPCLConst.LK_CPCL_CONTINUOUS;
	}

	public void selectGapPaper()
	{
		paperType = CPCLConst.LK_CPCL_LABEL;
	}
	
	public void selectBlackMarkPaper()
	{
		paperType = CPCLConst.LK_CPCL_BLACKMARK;
	}
	
	public void selectContinuousPaper()
	{
		paperType = CPCLConst.LK_CPCL_CONTINUOUS;
	}
	
    public void Profile3(int count) throws UnsupportedEncodingException
    {    
		cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.printCPCLText(0, 5, 2, 30, 1, "SEWOO TECH CO.,LTD.", 0);
    	cpclPrinter.printCPCLText(0, 0, 3, 30, 70, "Global leader in the mini-printer industry.", 0);
    	cpclPrinter.printCPCLText(0, 0, 3, 30, 110, "Total Printing Solution", 0);
    	cpclPrinter.printCPCLText(0, 0, 3, 30, 150, "Diverse innovative and reliable products", 0);
    	// Telephone
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 1, 30, 220, "TEL : 82-31-459-8200", 0);
    	// Homepage
		cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_QRCODE, 30, 300, 6, 0, 1, 0, "http://www.miniprinter.com");
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 1, 210, 300, "www.miniprinter.com", 0);
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 1, 1, 210, 390, "<-- Check This.", 0);    	
    	cpclPrinter.printForm();
    }
    
    public void barcode3(int count) throws UnsupportedEncodingException
    {
    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.setCPCLBarcode(0, 2, 0);
		// CODABAR
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_CODABAR, 2, CPCLConst.LK_CPCL_BCS_0RATIO, 50, 109, 45, "A1234567890B", 0);
		// Code 39
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_39, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 50, 19, 150, "01234567890", 0);
		// Code 93
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_93, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 50, 79, 255, "01234567890", 0);
		// Code 128
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 50, 109, 360, "01234567890", 0);
		// Print
		cpclPrinter.printForm();
    }
    
    public void image3(int count, String sBitmapFilename) throws IOException
    {
		cpclPrinter.setForm(0, 200, 200, 800, count);
		cpclPrinter.setMedia(paperType);

//		cpclPrinter.printBitmap("//sdcard//temp//test//car_s.jpg", 100, 140);
		String filePath=sBitmapFilename;
		com.sewoo.jpos.image.ImageLoader imageLoader=new ImageLoader();
		MobileImageConverter mobileImageConverter=new MobileImageConverter();
		int[][] img = imageLoader.imageLoad(filePath);
		if(img != null) {
			byte[] bimg = mobileImageConverter.convertBitImage(img, imageLoader.getThresHoldValue());
			int printX=0, printY=0;
			cpclPrinter.printGraphic(mobileImageConverter.getByteWidth(img.length), img[0].length, printX, printY, bimg);
		}
//		cpclPrinter.printBitmap(filePath, 1, 1);

		//		cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_PDF417, 150, 440, 2, 7, 2, 1, "http://www.miniprinter.com");
//		cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_QRCODE, 150, 400, 5, 0, 1, 0, "http://www.miniprinter.com");
		// Print
		cpclPrinter.printForm();

/*
		//#### original stuff ####
    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.setCPCLBarcode(0, 2, 0);
		cpclPrinter.printBitmap("//sdcard//temp//test//logo_m.jpg", 1, 1);
//		Print saved image in Printer Memory. (NV LOGO Image)
//    	cpclPrinter.printCPCLImage("LOGO1.PCX", 1, 50);
		cpclPrinter.printBitmap("//sdcard//temp//test//car_s.jpg", 168, 140);
		cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_PDF417, 150, 440, 2, 7, 2, 1, "http://miniprinter.com");
    	// Print
		cpclPrinter.printForm();
*/
/*
    	int cstatus, pstatus;
    	cstatus = cpclPrinter.printerCheck();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"printerCheck error");
    		return;
   		}
    	cstatus = cpclPrinter.status();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"Printer is error");
    		return;
   		}

    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.printBitmap("//sdcard//temp//test//test4407989.png", 1, 1);
//		cpclPrinter.printBitmap("//sdcard//temp//test//logo_s.jpg", 1, 1);
    	// Print
		cpclPrinter.printForm();
*/
/*
		Log.e(TAG,"Start printerResults(1)");
		pstatus = cpclPrinter.printerResults();
		Log.e(TAG,"End printerResults(1)");
		if(pstatus != 0)
		{
			Log.e(TAG,"printerResults error");
			return;
		}

		
    	cstatus = cpclPrinter.printerCheck();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"printerCheck error");
    		return;
   		}
    	cstatus = cpclPrinter.status();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"Printer is error");
    		return;
   		}
*/
/*    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.printBitmap("//sdcard//temp//test//test4408259.png", 1, 1);
//		cpclPrinter.printBitmap("//sdcard//temp//test//logo_s.jpg", 1, 1);
    	// Print
		cpclPrinter.printForm();
*/
/*
		Log.e(TAG,"Start printerResults(2)");
		pstatus = cpclPrinter.printerResults();
		Log.e(TAG,"End printerResults(2)");
		if(pstatus != 0)
		{
			Log.e(TAG,"printerResults error");
			return;
		}

    	cstatus = cpclPrinter.printerCheck();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"printerCheck error");
    		return;
   		}
    	cstatus = cpclPrinter.status();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"Printer is error");
    		return;
   		}

    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.printBitmap("//sdcard//temp//test//test4409022.png", 1, 1);
//		cpclPrinter.printBitmap("//sdcard//temp//test//logo_s.jpg", 1, 1);
    	// Print
		cpclPrinter.printForm();

		Log.e(TAG,"Start printerResults(3)");
		pstatus = cpclPrinter.printerResults();
		Log.e(TAG,"End printerResults(3)");
		if(pstatus != 0)
		{
			Log.e(TAG,"printerResults error");
			return;
		}

    	cstatus = cpclPrinter.printerCheck();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"printerCheck error");
    		return;
   		}
    	cstatus = cpclPrinter.status();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"Printer is error");
    		return;
   		}

    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		cpclPrinter.printBitmap("//sdcard//temp//test//test4409025.png", 1, 1);
//		cpclPrinter.printBitmap("//sdcard//temp//test//logo_s.jpg", 1, 1);
    	// Print
		cpclPrinter.printForm();

		Log.e(TAG,"Start printerResults(4)");
		pstatus = cpclPrinter.printerResults();
		Log.e(TAG,"End printerResults(4)");
		if(pstatus != 0)
		{
			Log.e(TAG,"printerResults error");
			return;
		}
*/
    }
    
    public void fontTypeTest(int count) throws UnsupportedEncodingException
    {

		String test = "ABCDEFGHI;1234567890";
		cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
		// FONT 0,1,2,4,5,6,7
    	cpclPrinter.printCPCLText(0, 0, 1, 1, 1, "FONT 0", 0);
		cpclPrinter.printCPCLText(0, 0, 1, 1, 20, test, 0);
		cpclPrinter.printCPCLText(0, 0, 1, 1, 70, "FONT 1", 0);
		cpclPrinter.printCPCLText(0, 1, 0, 1, 90, test, 0);
		cpclPrinter.printCPCLText(0, 0, 1, 1, 140, "FONT 2", 0);
		cpclPrinter.printCPCLText(0, 2, 0, 1, 160, test, 0);
		cpclPrinter.printCPCLText(0, 0, 1, 1, 210, "FONT 4", 0);
		cpclPrinter.printCPCLText(0, 4, 0, 1, 230, test, 0);
		cpclPrinter.printCPCLText(0, 0, 1, 1, 280, "FONT 5", 0);
		cpclPrinter.printCPCLText(0, 5, 0, 1, 300, test, 0);
		cpclPrinter.printCPCLText(0, 0, 1, 1, 350, "FONT 6", 0);
		cpclPrinter.printCPCLText(0, 6, 0, 1, 370, test, 0);
		cpclPrinter.printCPCLText(0, 0, 1, 1, 420, "FONT 7", 0);
		cpclPrinter.printCPCLText(0, 7, 0, 1, 440, test, 0);
		// Print
		cpclPrinter.printForm();

/*
    	int cstatus, pstatus;
    	cstatus = cpclPrinter.printerCheck();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"printerCheck error");
    		return;
   		}
    	cstatus = cpclPrinter.status();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"Printer is error");
    		return;
   		}

    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
    	cpclPrinter.printCPCLText(0, 0, 1, 1, 1, "FONT 0(Page 1)", 0);
    	// Print
		cpclPrinter.printForm();
		pstatus = cpclPrinter.printerResults();
		if(pstatus != 0)
		{
			Log.e(TAG,"printerResults error");
			return;
		}
		
		
    	cstatus = cpclPrinter.printerCheck();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"printerCheck error");
    		return;
   		}
    	cstatus = cpclPrinter.status();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"Printer is error");
    		return;
   		}

    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
    	cpclPrinter.printCPCLText(0, 0, 1, 1, 1, "FONT 0(Page 2)", 0);
    	// Print
		cpclPrinter.printForm();
		pstatus = cpclPrinter.printerResults();
		if(pstatus != 0)
		{
			Log.e(TAG,"printerResults error");
			return;
		}

    	cstatus = cpclPrinter.printerCheck();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"printerCheck error");
    		return;
   		}
    	cstatus = cpclPrinter.status();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"Printer is error");
    		return;
   		}

    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
    	cpclPrinter.printCPCLText(0, 0, 1, 1, 1, "FONT 0(Page 3)", 0);
    	// Print
		cpclPrinter.printForm();
		pstatus = cpclPrinter.printerResults();
		if(pstatus != 0)
		{
			Log.e(TAG,"printerResults error");
			return;
		}

    	cstatus = cpclPrinter.printerCheck();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"printerCheck error");
    		return;
   		}
    	cstatus = cpclPrinter.status();
    	if(cstatus != 0)
   		{
			Log.e(TAG,"Printer is error");
    		return;
   		}

    	cpclPrinter.setForm(0, 200, 200, 576, count);
		cpclPrinter.setMedia(paperType);
    	cpclPrinter.printCPCLText(0, 0, 1, 1, 1, "FONT 0(Page 4)", 0);
    	// Print
		cpclPrinter.printForm();
		pstatus = cpclPrinter.printerResults();
		if(pstatus != 0)
		{
			Log.e(TAG,"printerResults error");
			return;
		}
*/
	}
    
    public void multiLineTest(int count) throws UnsupportedEncodingException
	{
		String data = "ABCDEFGHIJKLMNOPQRSTUV\r\n";
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<9;i++)
		{
			sb.append(data);
		}		
		cpclPrinter.setForm(0, 0, 0, 1990, 0);
		cpclPrinter.setMedia(paperType);
		// MultiLine mode.
    	cpclPrinter.setMultiLine(45);
		cpclPrinter.multiLineText(0, 1, 0, 10, 20);
		cpclPrinter.multiLineData(sb.toString());
		cpclPrinter.resetMultiLine();
		// Print
		cpclPrinter.printForm();
	}
    
    public void countryTest(int count) throws UnsupportedEncodingException
    {
    	final String [] country = {"USA","GERMANY","FRANCE","SWEDEN","SPAIN",
			  						 "NORWAY","ITALY","UK","CP850","LATIN9"};	
    	for(int i=0;i<country.length;i++)
    		countryTestForm(country[i]);
    }
    
    private void countryTestForm(String country) throws UnsupportedEncodingException
    {
    	final char [] diff = {0x23,0x24,0x40,0x5B,0x5C,0x5D,0x5E,0x6C,0x7B,0x7C,0x7D,0x7E,
			 0xA4,0xA6,0xA8,0xB4,0xB8,0xBC,0xBD,0xBE};
    	
    	cpclPrinter.setForm(0, 200, 200, 30, 1);
    	cpclPrinter.setMedia(paperType);
		cpclPrinter.setCountry(country);
    	cpclPrinter.printCPCLText(0, 0, 1, 10, 10, new String(diff)+"   "+country, 0);
		cpclPrinter.resetCountry();
    	cpclPrinter.printForm();
    }

    public void printAndroidFont(int count) throws UnsupportedEncodingException
	{
    	int nLineWidth = 576;
    	String data = "Receipt";
//    	String data = "영수증";
    	Typeface typeface = null;

    	try
		{
    		cpclPrinter.setForm(0, 200, 200, 406, count);
    		cpclPrinter.setMedia(paperType);

    		cpclPrinter.printAndroidFont(data, nLineWidth, 100, 0, ESCPOSConst.LK_ALIGNMENT_CENTER);
    		cpclPrinter.printAndroidFont("Left Alignment", nLineWidth, 24, 120, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		cpclPrinter.printAndroidFont("Center Alignment", nLineWidth, 24, 150, ESCPOSConst.LK_ALIGNMENT_CENTER);
    		cpclPrinter.printAndroidFont("Right Alignment", nLineWidth, 24, 180, ESCPOSConst.LK_ALIGNMENT_RIGHT);

    		cpclPrinter.printAndroidFont(Typeface.SANS_SERIF, "SANS_SERIF : 1234iwIW", nLineWidth, 24, 210, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		cpclPrinter.printAndroidFont(Typeface.SERIF, "SERIF : 1234iwIW", nLineWidth, 24, 240, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		cpclPrinter.printAndroidFont(typeface.MONOSPACE, "MONOSPACE : 1234iwIW", nLineWidth, 24, 270, ESCPOSConst.LK_ALIGNMENT_LEFT);

    		// Print
    		cpclPrinter.printForm();		
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

    public void printMultilingualFont(int count) throws UnsupportedEncodingException
	{
    	int nLineWidth = 576;
    	String Koreandata = "영수증";
    	String Turkishdata = "Turkish(İ,Ş,Ğ)";
    	String Russiandata = "Получение";
    	String Arabicdata = "الإيصال";
    	String Greekdata = "Παραλαβή";
    	String Japanesedata = "領収書";
    	String GB2312data = "收据";
    	String BIG5data = "收據";

    	try
		{
    		cpclPrinter.setForm(0, 200, 200, 1000, count);
    		cpclPrinter.setMedia(paperType);

    		cpclPrinter.printAndroidFont("Korean Font", nLineWidth, 24, 0, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Korean 100-dot size font in android device.
    		cpclPrinter.printAndroidFont(Koreandata, nLineWidth, 100, 30, ESCPOSConst.LK_ALIGNMENT_CENTER);

    		cpclPrinter.printAndroidFont("Turkish Font", nLineWidth, 24, 140, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Turkish 50-dot size font in android device.
    		cpclPrinter.printAndroidFont(Turkishdata, nLineWidth, 50, 170, ESCPOSConst.LK_ALIGNMENT_CENTER);

    		cpclPrinter.printAndroidFont("Russian Font", nLineWidth, 24, 230, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Russian 60-dot size font in android device.
    		cpclPrinter.printAndroidFont(Russiandata, nLineWidth, 60, 260, ESCPOSConst.LK_ALIGNMENT_CENTER);

    		cpclPrinter.printAndroidFont("Arabic Font", nLineWidth, 24, 330, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Arabic 100-dot size font in android device.
    		cpclPrinter.printAndroidFont(Arabicdata, nLineWidth, 100, 360, ESCPOSConst.LK_ALIGNMENT_CENTER);

    		cpclPrinter.printAndroidFont("Greek Font", nLineWidth, 24, 470, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Greek 60-dot size font in android device.
    		cpclPrinter.printAndroidFont(Greekdata, nLineWidth, 60, 500, ESCPOSConst.LK_ALIGNMENT_CENTER);

    		cpclPrinter.printAndroidFont("Japanese Font", nLineWidth, 24, 570, ESCPOSConst.LK_ALIGNMENT_LEFT);
        	// Japanese 100-dot size font in android device.
    		cpclPrinter.printAndroidFont(Japanesedata, nLineWidth, 100, 600, ESCPOSConst.LK_ALIGNMENT_CENTER);

    		cpclPrinter.printAndroidFont("GB2312 Font", nLineWidth, 24, 710, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// GB2312 100-dot size font in android device.
    		cpclPrinter.printAndroidFont(GB2312data, nLineWidth, 100, 740, ESCPOSConst.LK_ALIGNMENT_CENTER);

    		cpclPrinter.printAndroidFont("BIG5 Font", nLineWidth, 24, 850, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// BIG5 100-dot size font in android device.
    		cpclPrinter.printAndroidFont(BIG5data, nLineWidth, 100, 880, ESCPOSConst.LK_ALIGNMENT_CENTER);

    		// Print
    		cpclPrinter.printForm();		
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
