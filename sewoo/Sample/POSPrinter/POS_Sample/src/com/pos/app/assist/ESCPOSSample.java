package com.pos.app.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
//import java.io.UnsupportedEncodingException;

//import android.util.Log;

import android.graphics.Typeface;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

public class ESCPOSSample
{
	private ESCPOSPrinter posPtr;
	// 0x1B
	private final char ESC = ESCPOS.ESC;
	
	public ESCPOSSample()
	{
		posPtr = new ESCPOSPrinter();
	}
	
//	private final String TAG = "PrinterStsChecker";
	private int rtn;
	
    public int sample1() throws InterruptedException
    {
		try
		{
			rtn = posPtr.printerSts();
			// Do not check the paper near empty.
			// if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
			// check the paper near empty.
			if( rtn != 0 )  return rtn;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return rtn;
		}
    	
		try
		{
			posPtr.printNormal(ESC+"|cA"+ESC+"|2CReceipt\r\n\r\n\r\n");
			posPtr.printNormal(ESC+"|rATEL (123)-456-7890\n\n\n");
			posPtr.printNormal(ESC+"|cAThank you for coming to our shop!\n");
			posPtr.printNormal(ESC+"|cADate\n\n");
			posPtr.printNormal("Chicken                             $10.00\n");
			posPtr.printNormal("Hamburger                           $20.00\n");
			posPtr.printNormal("Pizza                               $30.00\n");
			posPtr.printNormal("Lemons                              $40.00\n");
			posPtr.printNormal("Drink                               $50.00\n");
			posPtr.printNormal("Excluded tax                       $150.00\n");
			posPtr.printNormal(ESC+"|uCTax(5%)                              $7.50\n");
			posPtr.printNormal(ESC+"|bC"+ESC+"|2CTotal         $157.50\n\n");
			posPtr.printNormal("Payment                            $200.00\n");
			posPtr.printNormal("Change                              $42.50\n\n");
			posPtr.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode
			posPtr.lineFeed(4);
			posPtr.cutPaper();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return 0;
    }
    
    public int sample2() throws InterruptedException
    {
		try
		{
			rtn = posPtr.printerSts();
			// Do not check the paper near empty.
			// if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
			// check the paper near empty.
			if( rtn != 0 )  return rtn;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return rtn;
		}
    	
		try
		{
			posPtr.printText("Receipt\r\n\r\n\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH);
			posPtr.printText("TEL (123)-456-7890\r\n", LKPrint.LK_ALIGNMENT_RIGHT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Thank you for coming to our shop!\r\n", LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Chicken                             $10.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Hamburger                           $20.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Pizza                               $30.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Lemons                              $40.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Drink                               $50.00\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Excluded tax                       $150.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Tax(5%)                              $7.50\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_UNDERLINE, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Total         $157.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH);
			posPtr.printText("Payment                            $200.00\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			posPtr.printText("Change                              $42.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);
			// Reverse print
			//posPtr.printText("Change                              $42.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT | LKPrint.LK_FNT_REVERSE, LKPrint.LK_TXT_1WIDTH);
			posPtr.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode
			posPtr.lineFeed(4);
			posPtr.cutPaper();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return 0;
    }
    
    public int imageTest() throws InterruptedException
    {
		try
		{
			rtn = posPtr.printerSts();
			// Do not check the paper near empty.
			// if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
			// check the paper near empty.
			if( rtn != 0 )  return rtn;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return rtn;
		}
    	
		try
		{
			posPtr.printBitmap("//sdcard//temp//test//car_s.jpg", LKPrint.LK_ALIGNMENT_CENTER);
			posPtr.printBitmap("//sdcard//temp//test//danmark_windmill.jpg", LKPrint.LK_ALIGNMENT_LEFT);
			posPtr.printBitmap("//sdcard//temp//test//denmark_flag.jpg", LKPrint.LK_ALIGNMENT_RIGHT);
			posPtr.lineFeed(4);
			posPtr.cutPaper();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return 0;
    }
    
    public int westernLatinCharTest() throws InterruptedException
    {    	
    	final char [] diff = {0x23,0x24,0x40,0x5B,0x5C,0x5D,0x5E,0x6C,0x7B,0x7C,0x7D,0x7E,
   			 0xA4,0xA6,0xA8,0xB4,0xB8,0xBC,0xBD,0xBE};
		try
		{
			rtn = posPtr.printerSts();
			// Do not check the paper near empty.
			// if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
			// check the paper near empty.
			if( rtn != 0 )  return rtn;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return rtn;
		}
    	
		try
		{
			String ad = new String(diff);
			posPtr.printText(ad+"\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);    		
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return 0;
    }
    
    public int barcode1DTest() throws InterruptedException
    {
    	String barCodeData = "123456789012";
    	
		try
		{
			rtn = posPtr.printerSts();
			// Do not check the paper near empty.
			// if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
			// check the paper near empty.
			if( rtn != 0 )  return rtn;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return rtn;
		}
    	
		try
		{
			posPtr.printString("UPCA\r\n");
			posPtr.printBarCode(barCodeData, ESCPOSConst.LK_BCS_UPCA, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.printString("UPCE\r\n");
			posPtr.printBarCode(barCodeData, ESCPOSConst.LK_BCS_UPCE, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.printString("EAN8\r\n");
			posPtr.printBarCode("1234567", ESCPOSConst.LK_BCS_EAN8, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.printString("EAN13\r\n");
			posPtr.printBarCode(barCodeData, ESCPOSConst.LK_BCS_EAN13, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.printString("CODE39\r\n");
			posPtr.printBarCode("ABCDEFGHI", ESCPOSConst.LK_BCS_Code39, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.printString("ITF\r\n");
			posPtr.printBarCode(barCodeData, ESCPOSConst.LK_BCS_ITF, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.printString("CODABAR\r\n");
			posPtr.printBarCode(barCodeData, ESCPOSConst.LK_BCS_Codabar, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.printString("CODE93\r\n");
			posPtr.printBarCode(barCodeData, ESCPOSConst.LK_BCS_Code93, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.printString("CODE128\r\n");
			posPtr.printBarCode("{BNo.{C4567890120", ESCPOSConst.LK_BCS_Code128, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			posPtr.lineFeed(4);
			posPtr.cutPaper();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return 0;
    }
    
    public int barcode2DTest() throws InterruptedException
    {
    	String data = "ABCDEFGHIJKLMN";
		try
		{
			rtn = posPtr.printerSts();
			// Do not check the paper near empty.
			// if( (rtn != 0) && (rtn != ESCPOSConst.STS_PAPERNEAREMPTY)) return rtn;
			// check the paper near empty.
			if( rtn != 0 )  return rtn;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return rtn;
		}
    	
		try
		{
			posPtr.printString("PDF417\r\n");
			posPtr.printPDF417(data, data.length(), 0, 10, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printString("QRCode\r\n");
			posPtr.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
			posPtr.lineFeed(4);
			posPtr.cutPaper();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return 0;
    }

    public int printAndroidFont() throws InterruptedException
	{
    	String data = "Receipt";
//    	String data = "영수증";
    	Typeface typeface = null;

    	try
		{
			posPtr.printAndroidFont(data, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);
			posPtr.lineFeed(2);
			posPtr.printAndroidFont("Left Alignment", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont("Center Alignment", 512, 24, ESCPOSConst.LK_ALIGNMENT_CENTER);
			posPtr.printAndroidFont("Right Alignment", 512, 24, ESCPOSConst.LK_ALIGNMENT_RIGHT);

			posPtr.lineFeed(2);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, "SANS_SERIF : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, "SERIF : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(typeface.MONOSPACE, "MONOSPACE : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

			posPtr.lineFeed(2);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, "SANS : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, true, "SANS BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, true, false, "SANS BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, false, true, "SANS ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, true, true, "SANS BOLD ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, true, true, true, "SANS B/I/U : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

			posPtr.lineFeed(2);
			posPtr.printAndroidFont(Typeface.SERIF, "SERIF : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, true, "SERIF BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, true, false, "SERIF BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, false, true, "SERIF ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, true, true, "SERIF BOLD ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, true, true, true, "SERIF B/I/U : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

			posPtr.lineFeed(2);
			posPtr.printAndroidFont(Typeface.MONOSPACE, "MONOSPACE : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, true, "MONO BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, true, false, "MONO BOLD : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, false, true, "MONO ITALIC : 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, true, true, "MONO BOLD ITALIC: 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, true, true, true, "MONO B/I/U: 1234iwIW", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

			posPtr.lineFeed(4);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return 0;
	}

    public int printMultilingualFont() throws InterruptedException
	{
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
			posPtr.printAndroidFont("Korean Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Korean 100-dot size font in android device.
        	posPtr.printAndroidFont(Koreandata, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Turkish Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Turkish 50-dot size font in android device.
        	posPtr.printAndroidFont(Turkishdata, 512, 50, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Russian Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Russian 60-dot size font in android device.
        	posPtr.printAndroidFont(Russiandata, 512, 60, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Arabic Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Arabic 100-dot size font in android device.
        	posPtr.printAndroidFont(Arabicdata, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Greek Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Greek 60-dot size font in android device.
        	posPtr.printAndroidFont(Greekdata, 512, 60, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Japanese Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
        	// Japanese 100-dot size font in android device.
        	posPtr.printAndroidFont(Japanesedata, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("GB2312 Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// GB2312 100-dot size font in android device.
        	posPtr.printAndroidFont(GB2312data, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("BIG5 Font", 512, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// BIG5 100-dot size font in android device.
        	posPtr.printAndroidFont(BIG5data, 512, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.lineFeed(4);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return 0;
	}
}