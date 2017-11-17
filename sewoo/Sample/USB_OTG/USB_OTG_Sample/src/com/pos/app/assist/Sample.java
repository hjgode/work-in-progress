package com.pos.app.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;
import com.sewoo.port.android.DeviceConnection;

import android.graphics.Typeface;
import android.util.Log;

public class Sample
{
	ESCPOSPrinter posPtr;
	final char ESC = ESCPOS.ESC;
	final char LF = ESCPOS.LF;
	
	public Sample(DeviceConnection connection)
	{
		posPtr = new ESCPOSPrinter(connection);
	}
	
	public void Sample1() throws UnsupportedEncodingException
	{

		posPtr.printNormal(ESC + "|1F" + ESC + "|cA" + ESC + "|bC" + ESC + "|2C" + "Receipt" + LF + LF);

		posPtr.printNormal(ESC + "|rA" + ESC + "|bC" + "TEL (123)-456-7890" + LF);
        posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + "Thank you for coming to our shop!" + LF + LF);

        posPtr.printNormal("Chicken                   $10.00" + LF);
        posPtr.printNormal("Hamburger                 $20.00" + LF);
        posPtr.printNormal("Pizza                     $30.00" + LF);
        posPtr.printNormal("Lemons                    $40.00" + LF);
        posPtr.printNormal("Drink                     $50.00" + LF + LF);
        posPtr.printNormal("Excluded tax             $150.00" + LF);

        posPtr.printNormal( ESC + "|2F" + ESC + "|uC" + "Tax(5%)                    $7.50" + LF);
        posPtr.printNormal( ESC + "|bC" + ESC + "|2C" + "Total   $157.50" + LF + LF);
        posPtr.printNormal( ESC + "|bC" + "Payment                  $200.00" + LF);

        posPtr.printNormal( ESC + "|bC" + "Change                    $42.50" + LF);
//        posPtr.printBarCode("1234567890", ESCPOSConst.LK_BCS_Code39, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);         
        posPtr.printNormal( ESC + "|cA" + ESC + "|1fB" + ESC + "|2wB" + ESC + "|80hB" + ESC + "|2rB" + ESC + "|4tB" + ESC + "|10dB" + "1234567890" + LF);
        posPtr.printNormal(ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "Thank you" + LF);
        
//        posPtr.lineFeed(3);
        // POSPrinter Only.
		posPtr.printNormal(ESC + "|fP");
//        posPtr.cutPaper();
	}
	
	public void Sample2() throws UnsupportedEncodingException
	{
		posPtr.printBarCode("1234567890", ESCPOSConst.LK_BCS_Code39, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("0123498765", ESCPOSConst.LK_BCS_Code93, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("0987654321", ESCPOSConst.LK_BCS_ITF, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("{ACODE 128", ESCPOSConst.LK_BCS_Code128, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("{BCode 128", ESCPOSConst.LK_BCS_Code128, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("{C12345", ESCPOSConst.LK_BCS_Code128, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
        posPtr.printBarCode("A1029384756A", ESCPOSConst.LK_BCS_Codabar, 40, 2, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);

        posPtr.lineFeed(3);
        // POSPrinter Only.
        posPtr.cutPaper();

	}
	
	public void Sample3() throws IOException
	{
		posPtr.printBitmap("//sdcard//temp//test//logo_s.jpg", ESCPOSConst.LK_ALIGNMENT_CENTER);
    	posPtr.printBitmap("//sdcard//temp//test//danmark_windmill.jpg", ESCPOSConst.LK_ALIGNMENT_LEFT);
    	posPtr.printBitmap("//sdcard//temp//test//sample_2.jpg", ESCPOSConst.LK_ALIGNMENT_RIGHT);
    	posPtr.printBitmap("//sdcard//temp//test//sample_3.jpg", ESCPOSConst.LK_ALIGNMENT_CENTER);
    	posPtr.printBitmap("//sdcard//temp//test//sample_4.jpg", ESCPOSConst.LK_ALIGNMENT_LEFT);
    
        posPtr.lineFeed(3);
        // POSPrinter Only.
        posPtr.cutPaper();
	}
	
	public int Sample4() throws IOException
	{
		int check = posPtr.printerCheck();
    	if(check == ESCPOSConst.LK_SUCCESS)
		{
    		Log.i("Sample","sts= "+posPtr.status());
    		return posPtr.status();
    	}
    	else
    	{
    		Log.i("Sample","Retrieve Status Failed");
    		return -1;
    	}
    }
	
    public void printAndroidFont() throws IOException
	{
    	int nLineWidth = 384;
    	String data = "Receipt";
//    	String data = "영수증";
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
	}	

    public void printMultilingualFont() throws IOException
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
	}

	public int Sample5() throws IOException, InterruptedException
	{
		return posPtr.printerSts();
	}
}
