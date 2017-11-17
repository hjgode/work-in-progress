package com.mobile.sewoonfc.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.graphics.Typeface;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

public class ESCPSample3
{
	private ESCPOSPrinter posPtr;
	// 0x1B
	private final char ESC = ESCPOS.ESC;
	private final char LF = ESCPOS.LF;
	
	public ESCPSample3()
	{
		posPtr = new ESCPOSPrinter();
	}
	
    
    public int sample1() throws UnsupportedEncodingException
    {
    	posPtr.printerCheck();
    	if(posPtr.status() != 0)
    		return posPtr.status();
    	posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|2C" + "Receipt" + LF + LF);
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
	    return 0;
    }
    
    public int sample2() throws UnsupportedEncodingException
    {
    	posPtr.printerCheck();
    	if(posPtr.status() != 0)
    		return posPtr.status();
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
	    // Reverse print.
	    posPtr.printText("Change                              $42.50\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT | LKPrint.LK_FNT_REVERSE, LKPrint.LK_TXT_1WIDTH);
	    posPtr.printBarCode("0123456789", LKPrint.LK_BCS_Code39, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW);
	    return 0;
    }
    
    public int imageTest() throws IOException
    {
    	posPtr.printerCheck();
    	if(posPtr.status() != 0)
    		return posPtr.status();
    	posPtr.printBitmap("//sdcard//temp//test//car_s.jpg", LKPrint.LK_ALIGNMENT_CENTER);
    	posPtr.printBitmap("//sdcard//temp//test//danmark_windmill.jpg", LKPrint.LK_ALIGNMENT_LEFT);
    	posPtr.printBitmap("//sdcard//temp//test//denmark_flag.jpg", LKPrint.LK_ALIGNMENT_RIGHT);
    	return 0;
    }
    
    public int westernLatinCharTest() throws UnsupportedEncodingException
    {    	
    	final char [] diff = {0x23,0x24,0x40,0x5B,0x5C,0x5D,0x5E,0x6C,0x7B,0x7C,0x7D,0x7E,
   			 0xA4,0xA6,0xA8,0xB4,0xB8,0xBC,0xBD,0xBE};
    	String ad = new String(diff);
    	posPtr.printerCheck();
    	if(posPtr.status() != 0)
    		return posPtr.status();
    	posPtr.printText(ad+"\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);    		
    	return 0;
    }

    public int printAndroidFont() throws UnsupportedEncodingException
	{
    	int nLineWidth = 576;
    	String data = "Receipt";
//    	String data = "?òÅ?àòÏ¶?";
    	Typeface typeface = null;

    	try
		{
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
    	int nLineWidth = 576;
    	String Koreandata = "?òÅ?àòÏ¶?";
    	String Turkishdata = "Turkish(ƒ∞,≈û,ƒû)";
    	String Russiandata = "?ü–æ–ª—É—á–µ–Ω–∏–?";
    	String Arabicdata = "ÿß?Ñÿ??äÿµÿßŸ?";
    	String Greekdata = "?†Œ±œÅŒ±ŒªŒ±Œ≤Œ?";
    	String Japanesedata = "?†ò?èé?õ∏";
    	String GB2312data = "?î∂?çÆ";
    	String BIG5data = "?î∂?ìö";

    	try
		{
			posPtr.printAndroidFont("Korean Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Korean 100-dot size font in android device.
        	posPtr.printAndroidFont(Koreandata, nLineWidth, 100, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Turkish Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
    		// Turkish 50-dot size font in android device.
        	posPtr.printAndroidFont(Turkishdata, nLineWidth, 50, ESCPOSConst.LK_ALIGNMENT_CENTER);

			posPtr.printAndroidFont("Russian Font", nLineWidth, 24, ESCPOSConst.LK_ALIGNMENT_LEFT);
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
