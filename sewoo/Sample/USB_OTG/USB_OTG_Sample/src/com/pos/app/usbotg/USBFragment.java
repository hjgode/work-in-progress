package com.pos.app.usbotg;

import java.util.ArrayList;

import com.pos.app.assist.ConnectionInfo;
import com.pos.app.assist.Sample;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.port.android.USBPort;
import com.sewoo.port.android.USBPortConnection;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class USBFragment extends Fragment
{
	private static final String TAG = "SEWOO USB";

	// USB
	private UsbManager mUsbManager;
	private USBPort port;
	private USBPortConnection connection;
	private int prn;

	// UI 
	private ListView sampleListView;
	private Button connectButton;
	private Button disconnectButton;
	private Context context;
	private Spinner printerSpinner;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.usb_flag, container, false);
		context = container.getContext();
		mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		port = new USBPort(mUsbManager, context);
		// Receiver
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		context.registerReceiver(mUsbReceiver, filter);		
		// UI
		sampleListView = (ListView) view.findViewById(R.id.Sample_listView);
		connectButton = (Button) view.findViewById(R.id.connect_button);
		disconnectButton = (Button) view.findViewById(R.id.disconnect_button);
		
		// Track select.
        printerSpinner = (Spinner) view.findViewById(R.id.spinner_printer);
        ArrayAdapter<?> adapterP = ArrayAdapter.createFromResource(context, R.array.printer, android.R.layout.simple_spinner_item);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        printerSpinner.setAdapter(adapterP);
        printerSpinner.setSelection(0);
		
        // Sample List.
		ArrayList<String> arrayList = new ArrayList<String>(); 
		arrayList.add("Sample 1");
		arrayList.add("Sample 2");
		arrayList.add("Sample 3");
		arrayList.add("Print Android Font");
		arrayList.add("Print Multilingual");
		arrayList.add("Status Check");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, arrayList);
		sampleListView.setAdapter(adapter);
		sampleListView.setOnItemClickListener(sampleListListener);				
		connectButton.setOnClickListener(connectButtonListener);
		disconnectButton.setOnClickListener(disconnectButtonListener);
		return view;
	}
	
	private OnItemClickListener sampleListListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			if(ConnectionInfo.getInstance().getConnection() == null)
				return;
			Sample sample = new Sample(ConnectionInfo.getInstance().getConnection());
			try
			{
				switch(arg2)
				{
					case 0:
						sample.Sample1();
						break;
					case 1:
						sample.Sample2();
						break;
					case 2:
						sample.Sample3();
						break;
					case 3:
						sample.printAndroidFont();
						break;
					case 4:
						sample.printMultilingualFont();
						break;	
					case 5:
						int sts = 0;
						if(prn == USBPort.MOBILE_PRINTER)
							sts = sample.Sample4();
						else if(prn == USBPort.POS_PRINTER)
							sts = sample.Sample5();
						String statusMsg;
						if(sts == ESCPOSConst.LK_STS_NORMAL)
							statusMsg = new String("Normal");
						else
						{
							statusMsg = new String();
							if((sts & ESCPOSConst.LK_STS_COVER_OPEN) > 0)
								statusMsg = statusMsg + "Cover Open\r\n";
							if((sts & ESCPOSConst.STS_PAPERNEAREMPTY) > 0)
								statusMsg = statusMsg + "Paper Near Empty\r\n";
							if((sts & ESCPOSConst.LK_STS_PAPER_EMPTY) > 0)
								statusMsg = statusMsg + "Paper Empty\r\n";
							if((sts & ESCPOSConst.LK_STS_BATTERY_LOW) > 0)
								statusMsg = statusMsg + "Battery Low";
						}
						Toast toast = Toast.makeText(context, statusMsg, Toast.LENGTH_SHORT);
						toast.show();
						break;
				}
			}
			catch(Exception e)
			{
				Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
				toast.show();
				Log.e(TAG, e.getMessage(), e);
			}
		}
	};
			 
	private OnClickListener connectButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			prn = printerSpinner.getSelectedItemPosition();
				switch(prn)
				{
					case 0:
						prn = USBPort.POS_PRINTER;
						break;
					case 1:
						prn = USBPort.MOBILE_PRINTER;
						break;
				}
				// Retry
				for(int i=0;(i<10) && (connection == null);i++)
				{
					connection = port.connect_device(prn);
				}
				if(connection != null)
				{
					(ConnectionInfo.getInstance()).setConnection(connection);
					connectButton.setEnabled(false);
					disconnectButton.setEnabled(true);
					sampleListView.setEnabled(true);
					printerSpinner.setEnabled(false);
				}
				else
				{
					// Error
					Toast toast = Toast.makeText(context, "Could not connect device.", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
	};
	
	private OnClickListener disconnectButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(connection != null)
			{
				try
				{
					connection.close();
					connectButton.setEnabled(true);
					disconnectButton.setEnabled(false);
					sampleListView.setEnabled(false);
					connection = null;
					ConnectionInfo.getInstance().setConnection(null);
					printerSpinner.setEnabled(true);	
				}
				catch (InterruptedException e)
				{
					Toast toast = Toast.makeText(context, e.toString()+" : "+e.getMessage(), Toast.LENGTH_SHORT);
					toast.show();
					Log.e(TAG,e.getMessage(),e);
				}
			}
		}
	};
	
	@Override
	public void onResume() 
	{
		super.onResume();
		if(ConnectionInfo.getInstance().getConnection() == null)
		{
			connectButton.setEnabled(true);
			disconnectButton.setEnabled(false);
		}
		else
		{
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);			
		}
	}
	
	@Override
	public void onDestroy()
	{
		Log.d(TAG,"mytab usb destroy");
		if(connection != null)
		{	
			try
			{
				connection.close();
			}
			catch (InterruptedException e)
			{
				Log.e(TAG,e.getMessage(),e);
			}
		}
		context.unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}
	
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action))
			{
				synchronized (this)
				{
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
					{
						if (device != null)
						{
							// call method to set up device communication
							Log.d(TAG,"connected "+ device);
						}
					}
					else
					{
						Log.d(TAG, "permission denied for device " + device);
					}
				}
			}
			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) 
			{
	            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
	            if (device != null) 
	            {
	                // call your method that cleans up and closes communication with the device
	            	Log.d(TAG,"disconnected "+ device);
	            }
	        }
		}
	};
}
