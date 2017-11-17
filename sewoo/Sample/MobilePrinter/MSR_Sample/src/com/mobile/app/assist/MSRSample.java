package com.mobile.app.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;

public class MSRSample
{
	private ESCPOSPrinter posPtr;
	private char ESC = 0x1B;
	private char LF = 0x0A;
	
	public MSRSample()
	{
		posPtr = new ESCPOSPrinter();
	}
	
	public void samplePrint(String trackData) throws IOException
	{
		String cardData;
		// Logo
		posPtr.printBitmap("//sdcard//temp//test//logo_s.jpg", ESCPOSConst.LK_ALIGNMENT_CENTER);
		posPtr.lineFeed(2);
		// List
		receipt();		
		// Card
		trackData = trackData.substring(0, 16);
		cardData = trackData.substring(0,4) +"-";
		cardData = cardData + trackData.substring(4,8)+"-****-";
		cardData = cardData + trackData.substring(12,16);
		posPtr.lineFeed(2);
		posPtr.printNormal("Card : "+cardData+ LF + LF);
		// sign
		posPtr.printNormal(ESC + "|rA" + ESC + "|bC" + "Signature :" + LF);
		posPtr.printBitmap("//sdcard//temp//test//card_sign.jpg", ESCPOSConst.LK_ALIGNMENT_RIGHT);
		posPtr.lineFeed(3);
	}
	
	public void receipt() throws UnsupportedEncodingException
    {
		posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|2C" + "Receipt" + LF + LF);
        posPtr.printNormal(ESC + "|rA" + ESC + "|bC" + "TEL (123)-456-7890" + LF);
        posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + "Thank you for coming to our shop" + LF + LF);

        posPtr.printNormal("Chicken                   $10.00" + LF);
        posPtr.printNormal("Hamburger                 $20.00" + LF);
        posPtr.printNormal("Pizza                     $30.00" + LF);
        posPtr.printNormal("Lemons                    $40.00" + LF);
        posPtr.printNormal("Drink                     $50.00" + LF + LF);
        posPtr.printNormal("Excluded tax             $150.00" + LF);

        posPtr.printNormal( ESC + "|uC" + "Tax(5%)                    $7.50" + LF);
        posPtr.printNormal( ESC + "|bC" + ESC + "|2C" + "Total   $157.50" + LF + LF);
        posPtr.printNormal( ESC + "|bC" + "Payment                  $200.00" + LF);
        posPtr.printNormal( ESC + "|bC" + "Change                    $42.50" + LF);
    }
}
