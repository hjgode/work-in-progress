package com.mobile.app.port.bluetooth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.mobile.app.assist.AlertView;
import com.sewoo.app.zpl.R;
import com.sewoo.port.android.BluetoothPort;
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
	}
	
	@Override
	protected void onDestroy()
	{
		try
		{
			saveSettingFile();
			bluetoothPort.disconnect();
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
//				bluetoothPort.connect(params[0]);
				lastConnAddr = params[0].getAddress();
				bluetoothPort.connect(lastConnAddr);
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