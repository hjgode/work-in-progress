package com.pos.app.assist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

public class ESCPOSSample
{
	// 0x1B
	private final char ESC = ESCPOS.ESC;
	
	public ESCPOSSample()
	{
	}
	
    public void sample1(ESCPOSPrinter posPtr) throws UnsupportedEncodingException
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
    
    public void sample2(ESCPOSPrinter posPtr) throws UnsupportedEncodingException
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
    
    public void imageTest(ESCPOSPrinter posPtr) throws IOException
    {
    	posPtr.printBitmap("//sdcard//temp//test//car_s.jpg", LKPrint.LK_ALIGNMENT_CENTER);
    	posPtr.printBitmap("//sdcard//temp//test//danmark_windmill.jpg", LKPrint.LK_ALIGNMENT_LEFT);
    	posPtr.printBitmap("//sdcard//temp//test//denmark_flag.jpg", LKPrint.LK_ALIGNMENT_RIGHT);
    }
    
    public void westernLatinCharTest(ESCPOSPrinter posPtr) throws UnsupportedEncodingException
    {    	
    	final char [] diff = {0x23,0x24,0x40,0x5B,0x5C,0x5D,0x5E,0x6C,0x7B,0x7C,0x7D,0x7E,
   			 0xA4,0xA6,0xA8,0xB4,0xB8,0xBC,0xBD,0xBE};
    	String ad = new String(diff);
    	posPtr.printText(ad+"\r\n\r\n", LKPrint.LK_ALIGNMENT_LEFT, LKPrint.LK_FNT_DEFAULT, LKPrint.LK_TXT_1WIDTH);    		
    }
    
    public void barcode1DTest(ESCPOSPrinter posPtr) throws UnsupportedEncodingException
    {
    	String barCodeData = "123456789012";
    	
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
    
    public void barcode2DTest(ESCPOSPrinter posPtr) throws UnsupportedEncodingException
    {
    	String data = "ABCDEFGHIJKLMN";
    	posPtr.printString("PDF417\r\n");
    	posPtr.printPDF417(data, data.length(), 0, 10, ESCPOSConst.LK_ALIGNMENT_LEFT);
    	posPtr.printString("QRCode\r\n");
    	posPtr.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
    	posPtr.lineFeed(4);
	    posPtr.cutPaper();
    }
}