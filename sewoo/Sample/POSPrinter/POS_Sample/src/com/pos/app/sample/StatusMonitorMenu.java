package com.pos.app.sample;

import java.io.UnsupportedEncodingException;

import com.pos.app.assist.AlertView;
import com.pos.app.assist.StatusChecker;
import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;
import com.sewoo.port.android.BluetoothPort;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class StatusMonitorMenu extends Activity
{
	private static final String TAG = "StatusMonitorMenu";
	private static final String key = "status";
	
	private static CheckBox cashDrawerOpen;
	private static CheckBox coverOpen;
	private static CheckBox paperNearEnd;
	private static CheckBox paperEmpty;
	
	private StatusChecker checker;
	private ESCPOSPrinter escposPrinter;
	
	// 0x1B
	private final char ESC = ESCPOS.ESC;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_monitor);
		
		escposPrinter = new ESCPOSPrinter();
		ToggleButton asbToggleButton = (ToggleButton) findViewById(R.id.toggleButtonASB);
		
		asbToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				// TODO Auto-generated method stub
				if(isChecked)
				{
					checker = new StatusChecker();
					checker.setHandler(statusHandler);
					checker.start();
					escposPrinter.asbOn();
				}
				else
				{
					escposPrinter.asbOff();
					// need a dummy status.
					checker.stop();
				}
			
			}
		});
		Button cdOpenButton = (Button) findViewById(R.id.buttonCDOpen);
		cdOpenButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				escposPrinter.openDrawer(ESCPOSConst.CD_PIN_TWO, 100, 200);
			}
		});
		Button textPrintButton = (Button) findViewById(R.id.text_print);
		textPrintButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					escposPrinter.printNormal(ESC+"|cA"+ESC+"|2CReceipt\r\n\r\n\r\n");
					escposPrinter.printNormal(ESC+"|rATEL (123)-456-7890\n\n\n");
					escposPrinter.printNormal(ESC+"|cAThank you for coming to our shop!\n");
					escposPrinter.printNormal(ESC+"|cADate\n\n");
					escposPrinter.printNormal("Chicken                             $10.00\n");
					escposPrinter.printNormal("Hamburger                           $20.00\n");
					escposPrinter.printNormal("Pizza                               $30.00\n");
					escposPrinter.printNormal("Lemons                              $40.00\n");
					escposPrinter.printNormal("Drink                               $50.00\n");
					escposPrinter.printNormal("Excluded tax                       $150.00\n");
					escposPrinter.printNormal(ESC+"|uCTax(5%)                              $7.50\n");
					escposPrinter.printNormal(ESC+"|bC"+ESC+"|2CTotal         $157.50\n\n");
					escposPrinter.printNormal("Payment                            $200.00\n");
					escposPrinter.printNormal("Change                              $42.50\n\n");
					escposPrinter.printBarCode("{Babc456789012", LKPrint.LK_BCS_Code128, 40, 512, LKPrint.LK_ALIGNMENT_CENTER, LKPrint.LK_HRI_TEXT_BELOW); // Print Barcode
					escposPrinter.lineFeed(4);
					escposPrinter.cutPaper();
				}
				catch( UnsupportedEncodingException e)
				{
				}
			}
		});
		Button bar1PrintButton = (Button) findViewById(R.id.bar1_print);
		bar1PrintButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
			    	String barCodeData = "123456789012";
			    	
			    	escposPrinter.printString("UPCA\r\n");
			    	escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_UPCA, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.printString("UPCE\r\n");
			    	escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_UPCE, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.printString("EAN8\r\n");
			    	escposPrinter.printBarCode("1234567", ESCPOSConst.LK_BCS_EAN8, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.printString("EAN13\r\n");
			    	escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_EAN13, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.printString("CODE39\r\n");
			    	escposPrinter.printBarCode("ABCDEFGHI", ESCPOSConst.LK_BCS_Code39, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.printString("ITF\r\n");
			    	escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_ITF, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.printString("CODABAR\r\n");
			    	escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_Codabar, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.printString("CODE93\r\n");
			    	escposPrinter.printBarCode(barCodeData, ESCPOSConst.LK_BCS_Code93, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.printString("CODE128\r\n");
			    	escposPrinter.printBarCode("{BNo.{C4567890120", ESCPOSConst.LK_BCS_Code128, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW);
			    	escposPrinter.lineFeed(4);
			    	escposPrinter.cutPaper();
				}
				catch( UnsupportedEncodingException e)
				{
				}
			}
		});
		Button bar2PrintButton = (Button) findViewById(R.id.bar2_print);
		bar2PrintButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
			    	String data = "ABCDEFGHIJKLMN";
			    	escposPrinter.printString("PDF417\r\n");
			    	escposPrinter.printPDF417(data, data.length(), 0, 10, ESCPOSConst.LK_ALIGNMENT_LEFT);
			    	escposPrinter.printString("QRCode\r\n");
			    	escposPrinter.printQRCode(data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
			    	escposPrinter.lineFeed(4);
			    	escposPrinter.cutPaper();
				}
				catch( UnsupportedEncodingException e)
				{
				}
			}
		});

		cashDrawerOpen = (CheckBox) findViewById(R.id.checkCashDrawerOpen);
		paperEmpty = (CheckBox) findViewById(R.id.checkPaperEmpty);
		coverOpen = (CheckBox) findViewById(R.id.checkCoverOpen);
		paperNearEnd = (CheckBox) findViewById(R.id.checkPaperNearEnd);
	}
		
	@Override
	protected void onDestroy()
	{
		if(checker != null)
			checker.stop();
		Log.d(TAG,"onDestroy()");
		super.onDestroy();
	}
	
	public static final Handler statusHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == -1)	// 비정상적인 연결 종료 (abnormal disconnection)
			{
				if(BluetoothPort.getInstance().isConnected())
				{
					Context ctx = POSTester.getContext();
					AlertView.showAlert("Error",ctx.getString(R.string.bt_disconn_msg),ctx);
					POSTester.setConnErrOccur(true);
					try
					{
						BluetoothPort.getInstance().disconnect();
					}
					catch (Exception e)
					{
						Log.e("StatusMonitor", e.getMessage());
					}
				}
				Log.d("Status","disconnected");
				return;
			}

			Bundle bundle = msg.getData();
			byte [] sts = bundle.getByteArray(key);
			
			if(cashDrawerOpen != null && paperEmpty != null && 
			   coverOpen != null && paperNearEnd != null)
			{
				if((sts[0] & 0x04) > 0) // 금전등록기 열림(Cash Drawer Open).
					cashDrawerOpen.setChecked(false);
				else
					cashDrawerOpen.setChecked(true);
				
				if((sts[0] & 0x20) > 0) // 커버 열림(Cover Open).
					coverOpen.setChecked(true);
				else
					coverOpen.setChecked(false);

				if((sts[2] & 0x03) > 0) // 용지 적음 (Paper Near End)
					paperNearEnd.setChecked(true);
				else
					paperNearEnd.setChecked(false);				
				
				if((sts[2] & 0x0C) > 0) // 용지 없음(Paper Empty).
					paperEmpty.setChecked(true);
				else
					paperEmpty.setChecked(false);
			}
		}
	};
}
