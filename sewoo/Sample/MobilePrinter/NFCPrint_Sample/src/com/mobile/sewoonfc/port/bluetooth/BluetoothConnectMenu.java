package com.mobile.sewoonfc.port.bluetooth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import com.mobile.sewoonfc.escpos.R;
import com.mobile.sewoonfc.assist.AlertView;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import android.app.PendingIntent;
//import android.widget.TextView;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.NFCPrinter;

/**
 * BluetoothConnectMenu
 * @author Sung-Keun Lee
 * @version 2011. 12. 21.
 */
public class BluetoothConnectMenu extends Activity
{
	private static final String TAG = "BluetoothConnectMenu";
	// Intent request codes
	// private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

	ArrayAdapter<String> adapter;
	private BluetoothAdapter mBluetoothAdapter;
	private Vector<BluetoothDevice> remoteDevices;
	private BroadcastReceiver searchFinish;
	private BroadcastReceiver searchStart;
	private BroadcastReceiver discoveryResult;
	private Thread hThread;
	private Context context;
	// UI
	private EditText btAddrBox;
	private Button connectButton;
	private Button searchButton;
	private ListView list;
	// BT
	private BluetoothPort bluetoothPort;

	private NFCPrinter nfc = new NFCPrinter();
	private ESCPOSPrinter posPtr;
	private final char ESC = ESCPOS.ESC;
	private final char LF = ESCPOS.LF;
	private String nfcdata;
	
	private BluetoothDevice connectedDevice;
	// Set up Bluetooth.
	private void bluetoothSetup()
	{
		// Initialize
		clearBtDevData();
		bluetoothPort = BluetoothPort.getInstance();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) 
		{
		    // Device does not support Bluetooth
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) 
		{
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); 
		}
	}
	
	private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//temp";
	private static final String fileName = dir + "//BTPrinter";
	private String lastConnAddr;
	private void loadSettingFile()
	{
		int rin = 0;
		char [] buf = new char[128];
		try
		{	
			FileReader fReader = new FileReader(fileName);
			rin = fReader.read(buf);
			if(rin > 0)
			{
				lastConnAddr = new String(buf,0,rin);
				btAddrBox.setText(lastConnAddr);
			}
			fReader.close();
		}
		catch (FileNotFoundException e)
		{
			Log.i(TAG, "Connection history not exists.");
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}	
	}
	
	private void saveSettingFile()
	{
		try
		{
			File tempDir = new File(dir);
			if(!tempDir.exists())
			{
				tempDir.mkdir();
			}
			FileWriter fWriter = new FileWriter(fileName);
			if(lastConnAddr != null)
				fWriter.write(lastConnAddr);
			fWriter.close();
		}
		catch (FileNotFoundException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}	
	}
	
	// clear device data used list.
	private void clearBtDevData()
	{
		remoteDevices = new Vector<BluetoothDevice>();
	}	
	// add paired device to list
	private void addPairedDevices()
	{
		BluetoothDevice pairedDevice;
		Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();
		while(iter.hasNext())
		{
			pairedDevice = iter.next();
			if(bluetoothPort.isValidAddress(pairedDevice.getAddress()))
			{
				remoteDevices.add(pairedDevice);
				adapter.add(pairedDevice.getName() +"\n["+pairedDevice.getAddress()+"] [Paired]");			
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_menu);
		// Setting
		btAddrBox = (EditText) findViewById(R.id.EditTextAddressBT);
		connectButton = (Button) findViewById(R.id.ButtonConnectBT);
		searchButton = (Button) findViewById(R.id.ButtonSearchBT);
		list = (ListView) findViewById(R.id.ListView01);

		context = this;
		// Setting
		loadSettingFile();
		bluetoothSetup();

		// Connect, Disconnect -- Button
		connectButton.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				if(!bluetoothPort.isConnected()) // Connect routine.
				{
					try
					{
						btConn(mBluetoothAdapter.getRemoteDevice(btAddrBox.getText().toString()));
						connectedDevice = mBluetoothAdapter.getRemoteDevice(btAddrBox.getText().toString());
					}
					catch(IllegalArgumentException e)
					{
						// Bluetooth Address Format [OO:OO:OO:OO:OO:OO]
						Log.e(TAG,e.getMessage(),e);
						AlertView.showAlert(e.getMessage(), context);
						return;	
					}
					catch (IOException e)
					{
						Log.e(TAG,e.getMessage(),e);
						AlertView.showAlert(e.getMessage(), context);
						return;
					}
				}
				else // Disconnect routine.
				{
					// Always run. 
					btDisconn();
				}
			}
		});		
		// Search Button
		searchButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!mBluetoothAdapter.isDiscovering())
				{	
					clearBtDevData();
					adapter.clear();
					mBluetoothAdapter.startDiscovery();	
				}
				else
				{	
					mBluetoothAdapter.cancelDiscovery();
				}
			}
		});				
		// Bluetooth Device List
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		list.setAdapter(adapter);
		addPairedDevices();
		// Connect - click the List item.
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				BluetoothDevice btDev = remoteDevices.elementAt(arg2);
				try
				{
					if(mBluetoothAdapter.isDiscovering())
					{
						mBluetoothAdapter.cancelDiscovery();
					}
					btAddrBox.setText(btDev.getAddress());
					btConn(btDev);
					connectedDevice = btDev;
				}
				catch (IOException e)
				{
					AlertView.showAlert(e.getMessage(), context);
					return;
				}
			}
		});
		
		// UI - Event Handler.
		// Search device, then add List.
		discoveryResult = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				String key;
				BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(remoteDevice != null)
				{
					if(remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED)
					{
						key = remoteDevice.getName() +"\n["+remoteDevice.getAddress()+"]";
					}
					else
					{
						key = remoteDevice.getName() +"\n["+remoteDevice.getAddress()+"] [Paired]";
					}
					if(bluetoothPort.isValidAddress(remoteDevice.getAddress()))
					{
						remoteDevices.add(remoteDevice);
						adapter.add(key);
					}
				}
			}
		};
		registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		searchStart = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				connectButton.setEnabled(false);
				btAddrBox.setEnabled(false);
				searchButton.setText(getResources().getString(R.string.bt_stop_search_btn));
			}
		};
		registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
		searchFinish = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				connectButton.setEnabled(true);
				btAddrBox.setEnabled(true);
				searchButton.setText(getResources().getString(R.string.bt_search_btn));				
			}
		};
		registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

		
		Intent intent = getIntent();   
		String action = intent.getAction();   

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
		{
			NdefMessage[] messages = getNdefMessages(getIntent());
			byte[] payload = messages[0].getRecords()[0].getPayload();

			nfcdata = new String(payload);
			CharSequence cs = nfcdata;
			btAddrBox.setText(cs);

			if (nfcdata.matches ("^[\\da-fA-F]+$"))
			{
				// Valid Hexadecimal number
				Log.e(TAG, "[BT] Valid: " + nfcdata);
				nfcdata = nfc.getBT(nfcdata);
				btAddrBox.setText(nfcdata);
			} else { 
				// Invalid 
				Log.e(TAG, "[BT] Invalid: " + nfcdata);
				Toast toast = Toast.makeText(context, "Invalid address", Toast.LENGTH_SHORT);
				toast.show();
				return;
			} 

			if( nfc.isBT(nfcdata) )
			{
				Log.e(TAG, "isBT is true");
				nfcdata = nfc.getBT(nfcdata);
				btAddrBox.setText(nfcdata);
			} else {
				Log.e(TAG, "isBT is false");
				return;
			}

			try
			{
				Log.e(TAG, "start btConnNfc");

				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				connectedDevice = mBluetoothAdapter.getRemoteDevice(nfcdata);

				btConnNfc(nfcdata);
				Log.e(TAG, "End btConnNfc");
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction(); 
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
		{
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			
			if (rawMsgs != null) {

				msgs = new NdefMessage[rawMsgs.length];

				for (int i = 0; i < rawMsgs.length; i++)
				{
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			}
		}
		return msgs;
	}

	
	@Override
	protected void onDestroy()
	{
		try
		{
			saveSettingFile();
			bluetoothPort.disconnect();
	        try {
	            Method method = connectedDevice.getClass().getMethod("removeBond", (Class[]) null);
	            method.invoke(connectedDevice, (Object[]) null);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
		catch (InterruptedException e)
		{
			Log.e(TAG, e.getMessage(), e);
		}


		if((hThread != null) && (hThread.isAlive()))
		{
			hThread.interrupt();
			hThread = null;
		}	

		unregisterReceiver(searchFinish);
		unregisterReceiver(searchStart);
		unregisterReceiver(discoveryResult);
		super.onDestroy();
	}
	
	// Nfc Bluetooth Connection method.
	private void btConnNfc(final String btDev) throws IOException
	{
		Log.e(TAG, "start connTaskNfc" + btDev);
		new connTaskNfc().execute(btDev);
	}
	// Bluetooth Connection method.
	private void btConn(final BluetoothDevice btDev) throws IOException
	{
		new connTask().execute(btDev);
	}
	// Bluetooth Disconnection method.
	private void btDisconn()
	{
		try
		{
			bluetoothPort.disconnect();
	        try {
	            Method method = connectedDevice.getClass().getMethod("removeBond", (Class[]) null);
	            method.invoke(connectedDevice, (Object[]) null);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
		if((hThread != null) && (hThread.isAlive()))
			hThread.interrupt();
		// UI
		connectButton.setText(getResources().getString(R.string.dev_conn_btn));
		list.setEnabled(true);
		btAddrBox.setEnabled(true);
		searchButton.setEnabled(true);
		Toast toast = Toast.makeText(context, getResources().getString(R.string.bt_disconn_msg), Toast.LENGTH_SHORT);
		toast.show();
	}
	
	// Nfc Bluetooth Connection Task.
	class connTaskNfc extends AsyncTask<String, Void, Integer>
	{
		private final ProgressDialog dialog = new ProgressDialog(BluetoothConnectMenu.this);
		
		@Override
		protected void onPreExecute()
		{
			Log.e(TAG, "start onPreExecute");
			dialog.setTitle(getResources().getString(R.string.bt_tab));
			dialog.setMessage(getResources().getString(R.string.connecting_msg));
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Integer doInBackground(String... params)
		{
			Integer retVal = null;
			int retrycnt = 0;


			for(retrycnt=0; retrycnt < 4; retrycnt++)
			{
				try
				{
					Log.e(TAG, "[BT] start connect=" + params[0]);
					bluetoothPort.connect(params[0]);
					Log.e(TAG, "[BT] end connect=" + params[0]);

					retVal = Integer.valueOf(0);
					return retVal;
				}
				catch (IOException e)
				{
					retVal = new Integer(-1);
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

			return retVal;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			if(result.intValue() == 0)	// Connection success.
			{
				RequestHandler rh = new RequestHandler();				
				hThread = new Thread(rh);
				hThread.start();
				// UI
				if(dialog.isShowing())
					dialog.dismiss();				

				Log.e(TAG, "start ESCPOSPrinter");

				posPtr = new ESCPOSPrinter();
				
				try
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

//			        posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|4C" + "Thank you" + LF);
			        posPtr.printNormal(ESC + "|cA" + ESC + "|bC" + ESC + "|3C" + "Thank you for visiting" + LF);
			    	posPtr.lineFeed(3);
				}
				catch (UnsupportedEncodingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try
				{
					Thread.sleep(2500);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(bluetoothPort.isConnected())
				{
					try
					{
						bluetoothPort.disconnect();
				        try {
				            Method method = connectedDevice.getClass().getMethod("removeBond", (Class[]) null);
				            method.invoke(connectedDevice, (Object[]) null);
					            
				        } catch (Exception e) {
				            e.printStackTrace();
				        }
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				finish();
			}
			else	// Connection failed.
			{
				if(dialog.isShowing())
					dialog.dismiss();				
				AlertView.showAlert(getResources().getString(R.string.bt_conn_fail_msg),
									getResources().getString(R.string.dev_check_msg), context);
			}
			super.onPostExecute(result);
		}
	}

	// Bluetooth Connection Task.
	class connTask extends AsyncTask<BluetoothDevice, Void, Integer>
	{
		private final ProgressDialog dialog = new ProgressDialog(BluetoothConnectMenu.this);
		
		@Override
		protected void onPreExecute()
		{
			dialog.setTitle(getResources().getString(R.string.bt_tab));
			dialog.setMessage(getResources().getString(R.string.connecting_msg));
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Integer doInBackground(BluetoothDevice... params)
		{
			Integer retVal = null;
			try
			{
				bluetoothPort.connect(params[0]);
				
				lastConnAddr = params[0].getAddress();
				retVal = new Integer(0);
			}
			catch (IOException e)
			{
				Log.e(TAG, e.getMessage());
				retVal = new Integer(-1);
			}
			return retVal;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			if(result.intValue() == 0)	// Connection success.
			{
				RequestHandler rh = new RequestHandler();				
				hThread = new Thread(rh);
				hThread.start();
				// UI
				connectButton.setText(getResources().getString(R.string.dev_disconn_btn));
				list.setEnabled(false);
				btAddrBox.setEnabled(false);
				searchButton.setEnabled(false);
				if(dialog.isShowing())
					dialog.dismiss();				
				Toast toast = Toast.makeText(context, getResources().getString(R.string.bt_conn_msg), Toast.LENGTH_SHORT);
				toast.show();
			}
			else	// Connection failed.
			{
				if(dialog.isShowing())
					dialog.dismiss();				
				AlertView.showAlert(getResources().getString(R.string.bt_conn_fail_msg),
									getResources().getString(R.string.dev_check_msg), context);
			}
			super.onPostExecute(result);
		}
	}
}