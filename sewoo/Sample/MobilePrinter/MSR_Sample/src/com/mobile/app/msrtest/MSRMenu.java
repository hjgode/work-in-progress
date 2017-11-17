package com.mobile.app.msrtest;

import java.io.IOException;

import com.mobile.app.assist.AlertView;
import com.mobile.app.assist.ReceiptSample;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.LKPrint;
import com.sewoo.request.android.AndroidMSR;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MSRMenu extends Activity 
{    
	private static final String TAG = "MSRMenu";
	private static final boolean D = true;
	
	private static EditText trackView1;
	private static EditText trackView2;
	private static EditText trackView3;
	private static Button msrButton;
	private static Button paymentButton;
	private static Spinner trackSpinner;
	private static Context context;
	private AndroidMSR androidMSR;
	private int mode;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msr_menu);
        
        context = this;
        trackView1 = (EditText) findViewById(R.id.EditTextTrack1);
        trackView2 = (EditText) findViewById(R.id.EditTextTrack2);
        trackView3 = (EditText) findViewById(R.id.EditTextTrack3);
        
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.msr_track, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trackSpinner = (Spinner) findViewById(R.id.Spinner01);
        trackSpinner.setAdapter(adapter);
        trackSpinner.setSelection(1);
        
        msrButton = (Button) findViewById(R.id.ButtonMSR);      
        msrButton.setOnClickListener(msrTestListener);
        
        paymentButton = (Button) findViewById(R.id.paymentButton);
        paymentButton.setOnClickListener(paymentCardListener);
    }
    
	OnClickListener msrTestListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if (msrButton.getText().equals("Read MSR"))
			{
				// MSR mode command.
				mode = trackSpinner.getSelectedItemPosition(); // + 49;
				switch (mode)
				{
					case 0: // Track 1
						mode = ESCPOSConst.LK_MSR_TRACK_1;
						break;
					case 1: // Track 2
						mode = ESCPOSConst.LK_MSR_TRACK_2;
						break;
					case 2: // Track 12
						mode = ESCPOSConst.LK_MSR_TRACK_12;
						break;
					case 3: // Track 3
						mode = ESCPOSConst.LK_MSR_TRACK_3;
						break;
					case 4: // Track 23
						mode = ESCPOSConst.LK_MSR_TRACK_23;
						break;
				}		
				androidMSR = AndroidMSR.getInstance();
				androidMSR.setHandler(msrHandler);
				try
				{
					if(androidMSR.readMSR(mode) == LKPrint.LK_STS_MSR_READ)
					{
						trackSpinner.setEnabled(false);
						paymentButton.setEnabled(false);
						msrButton.setText("Cancel MSR");
						trackView1.setText("Track 1");
						trackView2.setText("Track 2");
						trackView3.setText("Track 3");
					}
					else
					{
						AlertView.showError("Fail to change MSR mode.", context);
					}
				}
				catch(InterruptedException e1)
				{
					Log.e(TAG, "msrTestListener "+e1.getMessage());
				}
				catch(IOException e2)
				{
					Log.e(TAG, "msrTestListener "+e2.getMessage());
				}
			}
			else
			{
				trackSpinner.setEnabled(true);
				paymentButton.setEnabled(true);
				msrButton.setText("Read MSR");
				trackView1.setText("Track 1");
				trackView2.setText("Track 2");
				trackView3.setText("Track 3");
				// Normal mode restore command.
				if (androidMSR != null)
				{
					try
					{
						androidMSR.cancelMSR();
					}
					catch (InterruptedException e)
					{
						Log.e(TAG, "msrTestListener "+e.getMessage());
					}
				}
			}
		}
	};
    
    OnClickListener paymentCardListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			androidMSR = AndroidMSR.getInstance();
			androidMSR.setHandler(paymentHandler);
			if(!paymentButton.getText().equals("Cancel MSR"))
			{
				try
				{
					if(androidMSR.readMSR(ESCPOSConst.LK_MSR_TRACK_2) == LKPrint.LK_STS_MSR_READ)
					{
//						boolean s = androidMSR.isMSRStatus();
//						Log.d(TAG,"status "+s);
						
						paymentButton.setText("Cancel MSR");
						msrButton.setEnabled(false);
						trackSpinner.setEnabled(false);
					}
					else
					{	
						AlertView.showError("Fail to change MSR mode.", context);
					}
				}
				catch(Exception e)
				{
					AlertView.showError(e.getMessage(), context);
					Log.e(e.toString(), e.getMessage());
				}
			}
			else
			{
				try
				{
					androidMSR.cancelMSR();
				}
				catch (Exception e)
				{
					Log.e(TAG,e.getMessage());
				}
				paymentButton.setText(getResources().getString(R.string.card_btn));
				msrButton.setEnabled(false);
				trackSpinner.setEnabled(false);
			}
		}
	};
    
	
	
    @Override
    protected void onDestroy()
    {
    	Log.w(TAG,"onDestroy");
    	try
		{
    		if(androidMSR != null)
    			androidMSR.releaseInstance();
		}
		catch (InterruptedException e)
		{
			Log.e(TAG,e.getMessage(),e);
		}
    	super.onDestroy();
    }
    
    // ================================================================================================ //
    // Track Data Update Handler. - Read Complete.
    // ================================================================================================ //
    final static Handler msrHandler = new MSRHandler();
    final static Handler paymentHandler = new PaymentHandler();
     
	static class PaymentHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			Bundle bundle;
			super.handleMessage(msg);
			bundle = (Bundle) msg.obj;
			if (bundle.size() > 1)
			{
				byte [] rawData = bundle.getByteArray("RawData");
    			Log.i(TAG,"RawDATA == "+ new String(rawData));
				String [] track = parsingMSRData(rawData);	
				
				
				if (track.length >= 3)
				{
					ReceiptSample msrSample = new ReceiptSample();
					try
					{
						// Print track2
						msrSample.cardPrint(track[1]);
						trackView2.setText(track[1]);
						paymentButton.setText(context.getResources().getString(R.string.card_btn));
						trackSpinner.setEnabled(true);
						msrButton.setEnabled(true);
					}
					catch (Exception e)
					{
						Log.e(TAG, e.getMessage());
						AlertView.showError(e.getMessage(), context);
					}
				}
			}
			else
			{
				// Fail to read MSR.
				Log.e(TAG,"RawDATA == "+ new String(bundle.getByteArray("RawData")));
				AlertView.showError("Invalid MSR Data.", context);
			}
		}
	}
    
    static class MSRHandler extends Handler
	{
    	@Override
		public void handleMessage(Message msg)
		{
    		super.handleMessage(msg);
    		Bundle bundle = (Bundle) msg.obj;
			if(D)
				Log.d(TAG, "bundle.size() = "+bundle.size());
    		if(bundle.size() > 1)
    		{
    			// rawData - MSR Data.
    			byte [] rawData = bundle.getByteArray("RawData");
    			int rawLength = bundle.getInt("RawDataSize");
    			String [] track = parsingMSRData(rawData);
    			if(track.length >= 3)
    			{
    				trackView1.setText(track[0]);
    				trackView2.setText(track[1]);
    				trackView3.setText(track[2]);    				
    			}
    			Log.i(TAG, "RawDATA == "+new String(rawData));
    			Log.i(TAG, "RawDATA Buffer Size == "+rawData.length);
        		Log.i(TAG, "RawDATA Size == "+ rawLength);
        		Toast toast = Toast.makeText(context, "RawData Size : "+rawLength+"\r\n"
        				+new String(rawData,0,rawLength), Toast.LENGTH_SHORT);
				toast.show();
    			trackSpinner.setEnabled(true);
    			paymentButton.setEnabled(true);
				msrButton.setText("Read MSR");
    		}
    		else
    		{
    			// Fail to read MSR.
    			Log.e(TAG,"RawDATA == "+new String(bundle.getByteArray("RawData")));
        		AlertView.showError("Invalid MSR Data.", context);
    		}	
    	}
	}
    
	/**
	 * STX FS [Track1] FS [Track2] FS [Track3] ETX DATAEND 0x02 0x1C [0-76
	 * Bytes] 0x1C [0-37 Bytes] 0x1C [0-106 Bytes] 0x03 0x0D 0x0A 0x00
	 */
	private static String[] parsingMSRData(byte[] rawData)
	{
		final byte[] FS = { (byte) 0x1C };
		final byte[] ETX = { (byte) 0x03 };

		String temp = new String(rawData);
		String trackData[] = new String[3];

		// ETX , FS
		String[] rData = temp.split(new String(ETX));
		temp = rData[0];
		String[] tData = temp.split(new String(FS));

		switch (tData.length)
		{
			case 1:
				break;
			case 2:
				trackData[0] = tData[1];
				break;
			case 3:
				trackData[0] = tData[1];
				trackData[1] = tData[2];
				break;
			case 4:
				trackData[0] = tData[1];
				trackData[1] = tData[2];
				trackData[2] = tData[3];
				break;
		}
		return trackData;
	}
}