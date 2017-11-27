package com.mobile.app.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.graphics.Typeface;

import com.sewoo.jpos.command.CPCLConst;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.CPCLPrinter;

public class CPCLSample2
{
	private CPCLPrinter cpclPrinter;
	private int paperType;
	
	public CPCLSample2()
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
	
	public void barcodeTest(int count) throws UnsupportedEncodingException
    {
		cpclPrinter.setForm(0, 200, 200, 406, count);
		cpclPrinter.setMedia(paperType);
//		cpclPrinter.userString("BACKFEED", true); // Back feed.
		
		// CODABAR
		cpclPrinter.setCPCLBarcode(0, 0, 0);
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_CODABAR, 2, CPCLConst.LK_CPCL_BCS_0RATIO, 30, 19, 45, "A37859B", 0);
		cpclPrinter.printCPCLText(0, 7, 0, 19, 18, "CODABAR", 0);
		// Code 39
		cpclPrinter.setCPCLBarcode(0, 0, 0);
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_39, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 130, "0123456", 0);
		cpclPrinter.printCPCLText(0, 7, 0, 21, 103, "CODE 39", 0);
		// Code 93
		cpclPrinter.setCPCLBarcode(0, 0, 0);
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_93, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 215, "0123456", 0);
		cpclPrinter.printCPCLText(0, 7, 0, 21, 180, "CODE 93", 0);
		// BARCODE 128 1 1 50 150 10 HORIZ.
		cpclPrinter.setCPCLBarcode(0, 0, 0);
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 300, "A37859B", 0);
		cpclPrinter.printCPCLText(0, 7, 0, 21, 270, "CODE 128", 0);		
		// Print
		cpclPrinter.printForm();
    }
    
    public void profile2(int count) throws UnsupportedEncodingException
    {
    	cpclPrinter.setForm(0, 200, 200, 406, count);
		cpclPrinter.setMedia(paperType);
		
		cpclPrinter.printCPCLText(0, 5, 1, 1, 1, "SEWOO TECH CO.,LTD.", 0);
    	cpclPrinter.printCPCLText(0, 0, 2, 1, 70, "Global leader in the mini-printer industry.", 0);
    	cpclPrinter.printCPCLText(0, 0, 2, 1, 110, "Total Printing Solution", 0);
    	cpclPrinter.printCPCLText(0, 0, 2, 1, 150, "Diverse innovative and reliable products", 0);
    	// Telephone
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 0, 1, 200, "TEL : 82-31-459-8200", 0);
    	// Homepage
		cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_QRCODE, 0, 250, 4, 0, 1, 0, "http://www.miniprinter.com");
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 7, 0, 130, 250, "www.miniprinter.com", 0);
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 1, 0, 130, 300, "<-- Check This.", 0);    	
		cpclPrinter.printForm();
    }
    
    public void barcode2DTest(int count) throws IOException
    {
    	cpclPrinter.setForm(0, 200, 200, 406, count);    	
		cpclPrinter.setMedia(paperType);
		
    	cpclPrinter.printBitmap("//sdcard//temp//test//logo_s.jpg", 1, 1);
    	cpclPrinter.setCPCLBarcode(0, 0, 0);
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 125, "12345690AB", 0);		
		cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_PDF417, 80, 180, 2, 7, 2, 1, "SEWOO TECH\r\nLK-P11");
		cpclPrinter.printCPCL2DBarCode(0, CPCLConst.LK_CPCL_BCS_QRCODE, 30, 260, 4, 0, 1, 0, "LK-P11");
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 0, 2, 130, 280, "SEWOO TECH", 0);
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, 0, 2, 130, 300, "LK-P11", 0);    	    	
		cpclPrinter.printForm();
    }
    
    public void imageTest(int count) throws IOException
    {
    	cpclPrinter.setForm(0, 200, 200, 406, count);    	
		cpclPrinter.setMedia(paperType);
		
    	cpclPrinter.printBitmap("//sdcard//temp//test//sample_2.jpg", 1, 200);    	
    	cpclPrinter.printBitmap("//sdcard//temp//test//sample_3.jpg", 100, 200);    	
    	cpclPrinter.printBitmap("//sdcard//temp//test//sample_4.jpg", 120, 245);      	
    	cpclPrinter.printForm();    	
    }
        
    public void dmStamp(int count) throws IOException
    {
    	cpclPrinter.setForm(0, 200, 200, 406, count);
		cpclPrinter.setMedia(paperType);
		
		cpclPrinter.printBitmap("//sdcard//temp//test//danmark_windmill.jpg", 10, 10);		
		cpclPrinter.printBitmap("//sdcard//temp//test//denmark_flag.jpg", 222, 55);		
		cpclPrinter.setCPCLBarcode(0, 0, 0);
		cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 2, CPCLConst.LK_CPCL_BCS_1RATIO, 30, 19, 290, "0123456", 1);
		cpclPrinter.printCPCLText(0, 0, 1, 21, 345, "Quantity 001", 1);
		cpclPrinter.printForm();
    }
   
	public void fontTest(int count) throws UnsupportedEncodingException
    {
		cpclPrinter.setForm(0, 200, 200, 406, count);
		cpclPrinter.setMedia(paperType);
		
		cpclPrinter.printCPCLText(0, 0, 0, 1, 1,   "FONT-0-0", 2);
		cpclPrinter.printCPCLText(0, 0, 1, 1, 50,  "FONT-0-1", 0);
		cpclPrinter.printCPCLText(0, 4, 0, 1, 100, "FONT-4-0", 0);
		cpclPrinter.printCPCLText(0, 4, 1, 1, 150,  "FONT-4-1", 0);
		cpclPrinter.printCPCLText(0, 4, 2, 1, 260,  "4-2", 0);
		// Print
		cpclPrinter.printForm();
    }
	    
	public void fontTypeTest(int count) throws UnsupportedEncodingException
    {
		cpclPrinter.setForm(0, 200, 200, 406, count);
		cpclPrinter.setMedia(paperType);
		
		// FONT 0,1,2,4,5,6,7
    	cpclPrinter.printCPCLText(0, 0, 0, 1, 1,   "ABCD1234", 0);
		cpclPrinter.printCPCLText(0, 1, 0, 1, 20,  "ABCD1234", 0);
		cpclPrinter.printCPCLText(0, 2, 0, 1, 70, "ABCD1234", 0);
		cpclPrinter.printCPCLText(0, 4, 0, 1, 100, "ABCD1234", 0);
		cpclPrinter.printCPCLText(0, 5, 0, 1, 150, "ABCD1234", 0);
		cpclPrinter.printCPCLText(0, 6, 0, 1, 200,  "ABCD1234", 0);
		cpclPrinter.printCPCLText(0, 7, 0, 1, 250,  "ABCD1234", 0);
		// Print
		cpclPrinter.printForm();
    }	
	
	public void settingTest1(int count) throws UnsupportedEncodingException
	{
		cpclPrinter.setForm(0, 200, 200, 406, count);
		cpclPrinter.setMedia(paperType);
		
		cpclPrinter.setMagnify(CPCLConst.LK_CPCL_TXT_2WIDTH, CPCLConst.LK_CPCL_TXT_2HEIGHT);		
		cpclPrinter.setJustification(CPCLConst.LK_CPCL_LEFT);
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_0, 0, 1, 1, "FONT-0-0", 2);
		cpclPrinter.setMagnify(CPCLConst.LK_CPCL_TXT_1WIDTH, CPCLConst.LK_CPCL_TXT_1HEIGHT);		
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_0, 1, 1, 50,  "FONT-0-1", 0);
		cpclPrinter.setJustification(CPCLConst.LK_CPCL_CENTER);
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_4, 0, 1, 100, "FONT-4-0", 0);
		cpclPrinter.setMagnify(CPCLConst.LK_CPCL_TXT_2WIDTH, CPCLConst.LK_CPCL_TXT_1HEIGHT);		
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_4, 1, 1, 150,  "FONT-4-1", 0);
		cpclPrinter.setMagnify(CPCLConst.LK_CPCL_TXT_1WIDTH, CPCLConst.LK_CPCL_TXT_1HEIGHT);		
		cpclPrinter.setJustification(CPCLConst.LK_CPCL_RIGHT);
		cpclPrinter.printCPCLText(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_FONT_4, 2, 1, 260,  "4-2", 0);
		cpclPrinter.resetMagnify();
		// Print
		cpclPrinter.printForm();
	}
	
	public void settingTest2(int count) throws UnsupportedEncodingException
	{
		cpclPrinter.setForm(0, 200, 200, 406, count);
		cpclPrinter.setMedia(paperType);
		
		cpclPrinter.setConcat(CPCLConst.LK_CPCL_CONCAT, 75, 75);
		cpclPrinter.concatText(4, 2, 5, "$");
		cpclPrinter.concatText(4, 3, 0, "12");
		cpclPrinter.concatText(4, 2, 5, "34");
		cpclPrinter.resetConcat();
		// Print
		cpclPrinter.printForm();
	}
	
	public void multiLineTest(int count) throws UnsupportedEncodingException
	{
		String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ;0123456789!@#%^&*\r\n";
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<16;i++)
		{
			sb.append(data);
		}
		
		cpclPrinter.setForm(0, 200, 200, 406, count);
		cpclPrinter.setMedia(paperType);
    	
		// MultiLine mode.
		cpclPrinter.setMultiLine(15);
		cpclPrinter.multiLineText(0, 0, 0, 10, 20);
		cpclPrinter.multiLineData(sb.toString());
		cpclPrinter.resetMultiLine();
		// Print
		cpclPrinter.printForm();		
	}
	
	public String statusCheck()
	{
		String result = "";
		if(!(cpclPrinter.printerCheck() < 0))
		{
			int sts = cpclPrinter.status();
			if(sts == CPCLConst.LK_STS_CPCL_NORMAL)
				return "Normal";
			if((sts & CPCLConst.LK_STS_CPCL_BUSY) > 0)
				result = result + "Busy\r\n";
			if((sts & CPCLConst.LK_STS_CPCL_PAPER_EMPTY) > 0)
				result = result + "Paper empty\r\n";
			if((sts & CPCLConst.LK_STS_CPCL_COVER_OPEN) > 0)
				result = result + "Cover open\r\n";
			if((sts & CPCLConst.LK_STS_CPCL_BATTERY_LOW) > 0)
				result = result + "Battery low\r\n";
//			result = result + cpclPrinter.voltage()+ "\r\n";
//			result = result + cpclPrinter.temperature();
		}
		else
		{
			result = "Check the printer\r\nNo response";
		}
		return result;
	}

    public void printAndroidFont(int count) throws UnsupportedEncodingException
	{
    	int nLineWidth = 384;
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
    	int nLineWidth = 384;
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
    
    public void RSS1(int count) throws UnsupportedEncodingException
	{
    	String rsscommand = "B RSS-EXPSTACK 2 40 10 10 [01]98898765432106[3202]012345\r\n";

    	try
		{
    		cpclPrinter.setForm(0, 200, 200, 1000, count);
    		cpclPrinter.setMedia(paperType);

    		cpclPrinter.userString(rsscommand);
    		// Print
    		cpclPrinter.printForm();		
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void RSS2(int count) throws UnsupportedEncodingException
	{
    	String rsscommand = "B RSS-EXPSTACK 2 40 10 10 [01]98898765432106[3920]15000[3101]000015[17]160210[3202]012345[15]991231\r\n";

    	try
		{
    		cpclPrinter.setForm(0, 200, 200, 1000, count);
    		cpclPrinter.setMedia(paperType);

    		cpclPrinter.userString(rsscommand);
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
