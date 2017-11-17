package com.pos.app.usbotg;

import java.io.IOException;

import com.pos.app.assist.AlertView;
import com.pos.app.assist.ConnectionInfo;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.port.android.DeviceConnection;
import com.sewoo.request.android.AndroidMSR;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MSRFragment extends Fragment
{
	private AndroidMSR androidMSR;
	private int mode;
	private static final String TAG = "MSRFragment"; 
	private static EditText trackView1;
	private static EditText trackView2;
	private static EditText trackView3;
	private static Button msrButton;
	private static Spinner trackSpinner;

	private static Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.msr_flag, container, false);
		context = container.getContext();
		trackView1 = (EditText) view.findViewById(R.id.EditTextTrack1);
        trackView2 = (EditText) view.findViewById(R.id.EditTextTrack2);
        trackView3 = (EditText) view.findViewById(R.id.EditTextTrack3);
        
        // Track select.
        trackSpinner = (Spinner) view.findViewById(R.id.Spinner01);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(context, R.array.msr_track, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trackSpinner.setAdapter(adapter);
        trackSpinner.setSelection(1);
        
        // MSR Button
        msrButton = (Button) view.findViewById(R.id.ButtonMSR);
        msrButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					if(msrButton.getText().equals("Read MSR"))
					{
						// MSR mode command.
						mode = trackSpinner.getSelectedItemPosition();	// + 49;
						switch(mode)
						{
							case 0:		// Track 1
								mode = ESCPOSConst.LK_MSR_TRACK_1;
								break;
							case 1:		// Track 2
								mode = ESCPOSConst.LK_MSR_TRACK_2;
								break;
							case 2:		// Track 12
								mode = ESCPOSConst.LK_MSR_TRACK_12;
								break;
							case 3:		// Track 3
								mode = ESCPOSConst.LK_MSR_TRACK_3;
								break;
							case 4:		// Track 23
								mode = ESCPOSConst.LK_MSR_TRACK_23;
								break;
						}
						if(androidMSR.readMSR(mode) == ESCPOSConst.LK_STS_MSR_READ)
						{
							trackSpinner.setEnabled(false);
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
					else
					{
						trackSpinner.setEnabled(true);
						msrButton.setText("Read MSR");
						trackView1.setText("Track 1");
						trackView2.setText("Track 2");
						trackView3.setText("Track 3");
						// Normal mode restore command.
						androidMSR.cancelMSR();
					}
				}
				catch(InterruptedException e)
				{
					Log.e(TAG, e.toString()+" : "+e.getMessage(), e);
				}
				catch (IOException e)
				{
					Log.e(TAG, e.toString()+" : "+e.getMessage(), e);
				}
				catch (Exception e)
				{
					Log.e(TAG, e.toString()+" : "+e.getMessage(), e);
				}
			}
		});
		return view;
	}
	
	@Override
	public void onPause()
	{
		Log.w(TAG,"mytab msr onPause");
    	trackSpinner.setEnabled(true);
		msrButton.setText("Read MSR");
		trackView1.setText("Track 1");
		trackView2.setText("Track 2");
		trackView3.setText("Track 3");
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		// Android MSR
        DeviceConnection connection = ConnectionInfo.getInstance().getConnection();
        if(connection != null)
        {
        	androidMSR = new AndroidMSR(ConnectionInfo.getInstance().getConnection());
        	androidMSR = ConnectionInfo.getInstance().getAndroidMSR();
        	androidMSR.setHandler(msrHandler);
        	msrButton.setEnabled(true);
        }
        else
        {
        	Toast toast = Toast.makeText(context, "Printer is not connected.", Toast.LENGTH_SHORT);
        	toast.show();	
        	msrButton.setEnabled(false);
        }
		Log.w(TAG,"mytab msr onResume");
	}
	
	@Override
	public void onDestroy()
	{
		Log.w(TAG,"mytab msr onDestroy");
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
	
	/**
	 *  STX   FS   [Track1]    FS    [Track2]     FS    [Track3]     ETX     DATAEND
	 * 0x02 0x1C [0-76 Bytes] 0x1C [0-37 Bytes] 0x1C [0-106 Bytes]  0x03  0x0D 0x0A 0x00 
	 */
	private static String [] parsingMSRData(byte [] rawData)
	{
		final byte [] FS = {(byte) 0x1C};
		final byte [] ETX = {(byte) 0x03};
		final byte [] DATAEND = {0x0D, 0x0A, 0x00}; 
		
		String temp = new String(rawData);
		String trackData [] = new String[3];
	    String [] rdata = temp.split(new String(DATAEND));
		int lastDataIndex = rdata.length - 2;
		if(lastDataIndex >= 0)
		{
			temp = rdata[lastDataIndex];
			String [] tData = temp.split(new String(FS));
			if(tData.length == 4)
			{
				int etxI = tData[3].indexOf(new String(ETX));
				if(etxI >= 0)
				{
					temp = tData[3].substring(0, etxI);	
				}
				trackData[0] = tData[1];
				trackData[1] = tData[2];
				trackData[2] = temp;
			}
		}
		return trackData;
	}
	
	static Handler msrHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Log.e(TAG,"Handler sewoo msr");
			Bundle bundle;
			super.handleMessage(msg);
			bundle = (Bundle) msg.obj;
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
				msrButton.setText("Read MSR");
			}
			else
			{
				// Fail to read MSR.
				Log.e(TAG,"RawDATA == "+new String(bundle.getByteArray("RawData")));
	    		AlertView.showError("Invalid MSR Data.", context);
			}
		}
	};
}