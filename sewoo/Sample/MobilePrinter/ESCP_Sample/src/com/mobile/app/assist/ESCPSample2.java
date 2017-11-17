package com.mobile.app.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSImage;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

public class ESCPSample2
{
	private ESCPOSPrinter posPtr;
	private ESCPOSImage posImg;
	private final char ESC = ESCPOS.ESC;
	private final char LF = ESCPOS.LF;
	
	public ESCPSample2()
	{
		posPtr = new ESCPOSPrinter();
//		posPtr = new ESCPOSPrinter("EUC-KR"); // Korean.
//		posPtr = new ESCPOSPrinter("BIG5"); // Big5
		posImg = new ESCPOSImage();
	}
	
	public void receipt() throws UnsupportedEncodingException
    {
		posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|2C" + "Receipt" + LF + LF);
        posPtr.printNormal(ESC + "|rA" + ESC + "|bC" + "TEL (123)-456-7890" + LF);
        posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + "Thank you for coming to our shop!" + LF + LF);

        posPtr.printNormal(ESC + "|cA" +"Chicken                   $10.00" + LF);
        posPtr.printNormal(ESC + "|cA" +"Hamburger                 $20.00" + LF);
        posPtr.printNormal(ESC + "|cA" +"Pizza                     $30.00" + LF);
        posPtr.printNormal(ESC + "|cA" +"Lemons                    $40.00" + LF);
        posPtr.printNormal(ESC + "|cA" +"Drink                     $50.00" + LF + LF);
        posPtr.printNormal(ESC + "|cA" +"Excluded tax             $150.00" + LF);

        posPtr.printNormal( ESC + "|cA" +ESC + "|uC" + "Tax(5%)                    $7.50" + LF);
        posPtr.printNormal( ESC + "|cA" +ESC + "|bC" + ESC + "|2C" + "Total   $157.50" + LF + LF);
        posPtr.printNormal( ESC + "|cA" +ESC + "|bC" + "Payment                  $200.00" + LF);
        posPtr.printNormal( ESC + "|cA" +ESC + "|bC" + "Change                    $42.50" + LF);
    }
    
    public int sample() throws UnsupportedEncodingException
    {
    	int sts;
    	int iSize = 0;
    	
    	sts = posPtr.printerCheck();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

    	sts = posPtr.status();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}
    	
		posPtr.setAsync(false);

		receipt();
		try {
			iSize = posImg.printBitmap("//sdcard//temp//test//sign.bmp");
		    if(iSize > 0)
	    	{
		    	byte [] iImageBuffer = new byte[iSize];
		    	int iLen = posImg.getImage(iImageBuffer);
		    	if(iLen > 0)
	    		{
		    		posPtr.sendByte(iImageBuffer);
	    		}
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|4C" + "Thank you" + LF);
//    	posPtr.printNormal("測試");
    	posPtr.lineFeed(3);
        return 0;
    }

    public int barcode2d() throws UnsupportedEncodingException
    {
    	String data = "ABCDEFGHIJKLMN";
    	int sts;
    	
    	sts = posPtr.printerCheck();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

    	sts = posPtr.status();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

		posPtr.setAsync(false);

    	posPtr.printString("PDF417\r\n");
    	posPtr.printPDF417(data, data.length(), 0, 10, ESCPOSConst.LK_ALIGNMENT_CENTER);
    	posPtr.printPDF417(data, data.length(), 4, 3, ESCPOSConst.LK_ALIGNMENT_CENTER);
    	posPtr.printString("QRCode\r\n");
    	posPtr.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);

	    posPtr.printString("DDDD\r\n");
//        receipt();
        posPtr.printBarCode("1234567890", LKPrint.LK_BCS_Code39, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);         
//        posPtr.printNormal(ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "Thank you" + LF);

        posPtr.lineFeed(4);
//	    posPtr.cutPaper();
        return 0;
    }
    public int barcodesample() throws UnsupportedEncodingException
    {
    	int sts;
    	
    	sts = posPtr.printerCheck();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

    	sts = posPtr.status();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

		posPtr.setAsync(false);

		posPtr.printBarCode("1234567890", LKPrint.LK_BCS_Code39, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("0123498765", LKPrint.LK_BCS_Code93, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("0987654321", LKPrint.LK_BCS_ITF, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("{ACODE 128", LKPrint.LK_BCS_Code128, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("{BCode 128", LKPrint.LK_BCS_Code128, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("{C12345", LKPrint.LK_BCS_Code128, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("A1029384756A", LKPrint.LK_BCS_Codabar, 40, 2, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);
        posPtr.printNormal(ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "Thank you" + LF);
        posPtr.lineFeed(3);
        return 0;
    }
    
    public int imageTest() throws IOException
    {
    	int sts;
    	
    	sts = posPtr.printerCheck();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

    	sts = posPtr.status();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

		posPtr.setAsync(false);

		posPtr.printBitmap("//sdcard//temp//test//logo_s.jpg", LKPrint.LK_ALIGNMENT_CENTER);
    	posPtr.printBitmap("//sdcard//temp//test//sample_2.jpg", LKPrint.LK_ALIGNMENT_CENTER);
    	posPtr.printBitmap("//sdcard//temp//test//sample_3.jpg", LKPrint.LK_ALIGNMENT_LEFT);
    	posPtr.printBitmap("//sdcard//temp//test//sample_4.jpg", LKPrint.LK_ALIGNMENT_RIGHT);
    	
    	Bitmap _bitmap = BitmapFactory.decodeFile("//sdcard//temp//test//logo_s.jpg");
    	posPtr.printBitmap(_bitmap, LKPrint.LK_ALIGNMENT_CENTER);
    	posPtr.printBitmap(_bitmap, LKPrint.LK_ALIGNMENT_CENTER, 0, 1);

    	posPtr.printBitmap(_bitmap, LKPrint.LK_ALIGNMENT_LEFT, 1);
    	posPtr.printBitmap(_bitmap, LKPrint.LK_ALIGNMENT_LEFT, 2);
    	posPtr.printBitmap(_bitmap, LKPrint.LK_ALIGNMENT_LEFT, 3);

    	posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|4C" + "Thank you" + LF);

    	posPtr.lineFeed(3);
    	return 0;
    }
    
    public int imageTest1() throws IOException
    {
    	int sts;
    	
    	sts = posPtr.printerCheck();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

    	sts = posPtr.status();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

		posPtr.setAsync(false);

		posPtr.printBitmap("//sdcard//temp//test//1512301638.png", LKPrint.LK_ALIGNMENT_CENTER);
    	posPtr.lineFeed(3);
    	return 0;
    }

    public int invoice() throws UnsupportedEncodingException
	{	
/*
    	posPtr.setCharSet("UTF-8");
		
		// Setting PageMode
		posPtr.setPageMode(true);
    	// 180 DPI or 203 DPI
		// 180 DPI - 7 dot per 1mm
    	// 203 DPI - 8 dot per 1mm
    	posPtr.setDPI(203);
    	// Print direction.
		posPtr.setPrintDirection(ESCPOSConst.DIRECTION_LEFT_RIGHT);
    	// 399 dot x 630 dot.
		posPtr.setPrintingArea(399, 630);

    	// Data
    	// Medium Text (20, 20)
    	posPtr.setAbsoluteVertical(20);
    	posPtr.setAbsoluteHorizontal(20);
	    posPtr.printText("丟並乾亂佔佪亙", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH | LKPrint.LK_TXT_2HEIGHT);

    	// Large Text
	    posPtr.setAbsoluteVertical(90);
    	posPtr.setAbsoluteHorizontal(20);
	    posPtr.printText("伋伕佇佈", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_3WIDTH | LKPrint.LK_TXT_3HEIGHT);
    	
	    // Must be Off Unicode when print Alphabet or print barcode.
	    posPtr.setCharSet("Big5");
		
	    posPtr.setAbsoluteVertical(190);
    	posPtr.setAbsoluteHorizontal(20);
	    posPtr.printText("ABCDE", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_3WIDTH | LKPrint.LK_TXT_3HEIGHT);
    	
	    posPtr.setCharSet("UTF-8");
		
    	// Small Text
	    posPtr.setAbsoluteVertical(300);
    	posPtr.setAbsoluteHorizontal(20);
	    posPtr.printText("壓壘壙壚壞壟壢壩壯壺", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH | LKPrint.LK_TXT_1HEIGHT);

	    // Must be Off Unicode when print Alphabet or print barcode.
    	posPtr.setCharSet("Big5");

    	// 1D Barcode
    	posPtr.setAbsoluteVertical(380);
    	posPtr.setAbsoluteHorizontal(0);
    	posPtr.printBarCode("0123456789012345678901", ESCPOSConst.LK_BCS_Code39, 40, 1, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_NONE);
//    	    	posPtr.printBarCode("0123498765", ESCPOSConst.LK_BCS_Code93, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_NONE);
        		
    	// QRCODE
    	String data = "12345678901234567890123456789012345678901234567890123456789012345678901234567890";
    	posPtr.setAbsoluteVertical(450);
    	posPtr.setAbsoluteHorizontal(40);
    	posPtr.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
    	posPtr.setAbsoluteVertical(450);
    	posPtr.setAbsoluteHorizontal(240);
    	posPtr.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
    	
    	// Data
	    posPtr.printPageModeData();
    	posPtr.setPageMode(false);
    	posPtr.lineFeed(4);
    	return 0;
*/
    	int sts;
    	
    	sts = posPtr.printerCheck();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

    	sts = posPtr.status();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

		posPtr.setAsync(false);

    	posPtr.setCharSet("Big5");
		
		// Setting PageMode
		posPtr.setPageMode(true);
    	// 180 DPI or 203 DPI
		// 180 DPI - 7 dot per 1mm
    	// 203 DPI - 8 dot per 1mm
    	posPtr.setDPI(203);
    	// Print direction.
		posPtr.setPrintDirection(ESCPOSConst.DIRECTION_LEFT_RIGHT);
    	// 399 dot x 630 dot.
		posPtr.setPrintingArea(399, 730); // al

    	// Data
    	// Medium Text (20, 20)
    	posPtr.setAbsoluteVertical(20);
    	posPtr.setAbsoluteHorizontal(20);
	    posPtr.printText("丟並乾亂佔佪亙", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_2WIDTH | LKPrint.LK_TXT_2HEIGHT);

    	// Large Text
	    posPtr.setAbsoluteVertical(90);
    	posPtr.setAbsoluteHorizontal(20);
	    posPtr.printText("伋伕佇佈", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_3WIDTH | LKPrint.LK_TXT_3HEIGHT);
    	
	    // Must be Off Unicode when print Alphabet or print barcode.
	    posPtr.setCharSet("Big5");
		
	    posPtr.setAbsoluteVertical(190);
    	posPtr.setAbsoluteHorizontal(20);
	    posPtr.printText("ABCDE", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_3WIDTH | LKPrint.LK_TXT_3HEIGHT);
    	
	    posPtr.setCharSet("Big5");
		
    	// Small Text
	    posPtr.setAbsoluteVertical(300);
    	posPtr.setAbsoluteHorizontal(20);
	    posPtr.printText("壓壘壙壚壞壟壢壩壯壺桌號菇", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH | LKPrint.LK_TXT_1HEIGHT);

	    // Must be Off Unicode when print Alphabet or print barcode.
    	//posPtr.setCharSet("Big5");

    	// 1D Barcode
    	posPtr.setAbsoluteVertical(380);
    	posPtr.setAbsoluteHorizontal(0);
    	posPtr.printBarCode("0123456789012345678901", ESCPOSConst.LK_BCS_Code39, 40, 1, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_NONE);
//    	    	posPtr.printBarCode("0123498765", ESCPOSConst.LK_BCS_Code93, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_NONE);
        		
    	// QRCODE
    	String data = "中華民國萬萬稅1234567890123456789012345678901234567890123456789012345678";
    	posPtr.setAbsoluteVertical(450);
    	posPtr.setAbsoluteHorizontal(40);
    	posPtr.printQRCode(data, data.length(), 5, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
    	posPtr.setAbsoluteVertical(450);
    	posPtr.setAbsoluteHorizontal(240);
    	posPtr.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
    	
    	// Data
	    posPtr.printPageModeData();
    	posPtr.setPageMode(false);
    	posPtr.lineFeed(4);
    	return 0;
	
	}
    
    public int printDataMatrix() throws UnsupportedEncodingException
	{	
    	// DataMatrix
    	int sts;
    	
    	sts = posPtr.printerCheck();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

    	sts = posPtr.status();
		if(sts != ESCPOSConst.LK_STS_NORMAL)
		{
			return sts;
		}

		posPtr.setAsync(false);

		String data = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	posPtr.printDataMatrix(data, data.length(), 6, ESCPOSConst.LK_ALIGNMENT_CENTER);

    	posPtr.lineFeed(4);
    	return 0;
	}	

    public int printAndroidFont() throws UnsupportedEncodingException
	{
    	int nLineWidth = 384;
    	String data = "Receipt";
//    	String data = "영수증";
    	Typeface typeface = null;

    	try
		{
        	int sts;
        	
        	sts = posPtr.printerCheck();
    		if(sts != ESCPOSConst.LK_STS_NORMAL)
    		{
    			return sts;
    		}

        	sts = posPtr.status();
    		if(sts != ESCPOSConst.LK_STS_NORMAL)
    		{
    			return sts;
    		}

    		posPtr.setAsync(false);

    		posPtr.printAndroidFont(data, nLineWidth, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);
			posPtr.lineFeed(2);
			posPtr.printAndroidFont("Left Alignment", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont("Center Alignment", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_CENTER);
			posPtr.printAndroidFont("Right Alignment", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_RIGHT);

			posPtr.lineFeed(2);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, "SANS_SERIF : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, "SERIF : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(typeface.MONOSPACE, "MONOSPACE : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

			posPtr.lineFeed(2);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, "SANS : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, true, "SANS BOLD : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, true, false, "SANS BOLD : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, false, true, "SANS ITALIC : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, true, true, "SANS BOLD ITALIC : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SANS_SERIF, true, true, true, "SANS B/I/U : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

			posPtr.lineFeed(2);
			posPtr.printAndroidFont(Typeface.SERIF, "SERIF : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, true, "SERIF BOLD : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, true, false, "SERIF BOLD : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, false, true, "SERIF ITALIC : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, true, true, "SERIF BOLD ITALIC : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.SERIF, true, true, true, "SERIF B/I/U : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

			posPtr.lineFeed(2);
			posPtr.printAndroidFont(Typeface.MONOSPACE, "MONOSPACE : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, true, "MONO BOLD : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, true, false, "MONO BOLD : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, false, true, "MONO ITALIC : 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, true, true, "MONO BOLD ITALIC: 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
			posPtr.printAndroidFont(Typeface.MONOSPACE, true, true, true, "MONO B/I/U: 1234iwIW", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);

			posPtr.lineFeed(4);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return 0;
	}	

    public int printMultilingualFont() throws UnsupportedEncodingException
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
        	int sts;
        	
        	sts = posPtr.printerCheck();
    		if(sts != ESCPOSConst.LK_STS_NORMAL)
    		{
    			return sts;
    		}

        	sts = posPtr.status();
    		if(sts != ESCPOSConst.LK_STS_NORMAL)
    		{
    			return sts;
    		}

    		posPtr.setAsync(false);

    		posPtr.printAndroidFont("Korean Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Korean 100-dot size font in android device.
        	posPtr.printAndroidFont(Koreandata, nLineWidth, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Turkish Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Turkish 50-dot size font in android device.
        	posPtr.printAndroidFont(Turkishdata, nLineWidth, 50, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Russian Font", 384, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Russian 60-dot size font in android device.
        	posPtr.printAndroidFont(Russiandata, nLineWidth, 60, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Arabic Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Arabic 100-dot size font in android device.
        	posPtr.printAndroidFont(Arabicdata, nLineWidth, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Greek Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Greek 60-dot size font in android device.
        	posPtr.printAndroidFont(Greekdata, nLineWidth, 60, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Japanese Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
        	// Japanese 100-dot size font in android device.
        	posPtr.printAndroidFont(Japanesedata, nLineWidth, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("GB2312 Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// GB2312 100-dot size font in android device.
        	posPtr.printAndroidFont(GB2312data, nLineWidth, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("BIG5 Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// BIG5 100-dot size font in android device.
        	posPtr.printAndroidFont(BIG5data, nLineWidth, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

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
