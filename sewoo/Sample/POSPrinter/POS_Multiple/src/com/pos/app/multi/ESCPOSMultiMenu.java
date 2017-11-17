package com.pos.app.multi;

import java.io.IOException;
import java.util.Iterator;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.pos.app.assist.AddressRepo;
import com.pos.app.assist.ESCPOSSample;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.port.android.WiFiPortConnection;
import com.sewoo.request.android.RequestHandler;

public class ESCPOSMultiMenu extends ListActivity
{
	final String TAG = "ESCPOSMenu";
	private Context context;
	
	private BluetoothTask bluetoothTask;
	private WiFiTask [] wifiTasks;
	
	// Menu
	final static String[] arr = 
	{
		"Sample 1",
		"Sample 2",
		"Image Test",
		"Character Test",
		"Barcode 1D",
		"Barcode 2D",
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , arr));
		context = this;
		getListView().setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// single connection Blue tooth.
				bluetoothTask = new BluetoothTask();
				bluetoothTask.execute(AddressRepo.getInstance().getBluetoothAddress(), String.valueOf(arg2));
				// connect IP address in the list, then print and disconnect. -- iterative.
				int i = 0;
				wifiTasks = new WiFiTask[AddressRepo.getInstance().getIPCount()];
				Iterator<String> iter = AddressRepo.getInstance().getIterator();
				while(iter.hasNext())
				{
					wifiTasks[i] = new WiFiTask();
					wifiTasks[i].execute(iter.next(), String.valueOf(arg2));
					i++;
				}
			}
		});
	}
	
	@Override
	protected void onDestroy()
	{
		if((bluetoothTask != null) && (bluetoothTask.getStatus() == AsyncTask.Status.RUNNING))
		{
			try
			{
				bluetoothTask.closeConnection();
				bluetoothTask.cancel(true);
				bluetoothTask = null;
			}
			catch(Exception e)
			{
				Log.e(TAG,e.getMessage(),e);
			}
		}
		if(wifiTasks != null)
		{
			for(int i=0;i<wifiTasks.length;i++)
			{
				if((wifiTasks[i] != null) && (wifiTasks[i].getStatus() == AsyncTask.Status.RUNNING))
				{
					try
					{
						wifiTasks[i].closeConnection();
						wifiTasks[i].cancel(true);
						wifiTasks[i] = null;
					}
					catch(Exception e)
					{
						Log.e(TAG,e.getMessage(),e);
					}
				}
			}
			wifiTasks = null;
		}
		super.onDestroy();
	}
	
	private void methods(String index, ESCPOSPrinter escp) throws IOException
	{
		ESCPOSSample sample = new ESCPOSSample();
		int m_index = Integer.valueOf(index);
		switch(m_index)
		{
			case 0:
				sample.sample1(escp);
				break;
			case 1:
				sample.sample2(escp);
				break;
			case 2:
				sample.imageTest(escp);
				break;
			case 3:
				sample.westernLatinCharTest(escp);
				break;
			case 4:
				sample.barcode1DTest(escp);
				break;
			case 5:
				sample.barcode2DTest(escp);
				break;
			default:
		}
	}

	// Bluetooth Task.
	class BluetoothTask extends AsyncTask<String, Void, Integer>
	{
		private BluetoothPort bluetoothPort;
		private Thread rThread;
		
		public void closeConnection() throws InterruptedException, IOException
		{
			if((bluetoothPort != null) && (bluetoothPort.isConnected()))
			{
				bluetoothPort.disconnect();
			}
			if((rThread != null) && (rThread.isAlive()))
			{
				rThread.interrupt();
				rThread = null;
			}
		}
		
		@Override
		protected Integer doInBackground(String... params)
		{
			Integer result = Integer.valueOf(0);
			bluetoothPort = BluetoothPort.getInstance();
			if(bluetoothPort.isValidAddress(params[0]))
			{
				try
				{
					// Connection
					bluetoothPort.connect(params[0]);
					rThread = new Thread(new RequestHandler());
					rThread.start();
					// Printing
					methods(params[1], new ESCPOSPrinter());
					Thread.sleep(1000);
					// Disconnection
					bluetoothPort.disconnect();
					if((rThread != null) && (rThread.isAlive()))
					{
						rThread.interrupt();
						rThread = null;
					}
				}
				catch (IOException e)
				{
					Log.e(TAG,e.getMessage(),e);
					result = Integer.valueOf(-1);
				}
				catch (InterruptedException e)
				{
					Log.e(TAG,e.getMessage(),e);
					result = Integer.valueOf(-2);
				}
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			int retVal = result.intValue();
			if(retVal < 0)
			{
				Toast.makeText(context, "Bluetooth Print Failed.", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	// WiFi Task.
	class WiFiTask extends AsyncTask<String, Void, Integer>
	{
		private WiFiPortConnection connection;
		private String address;
		
		public void closeConnection() throws InterruptedException, IOException
		{
			if(connection != null)
				connection.close();
		}
		
		@Override
		protected Integer doInBackground(String... params)
		{
			Integer result = Integer.valueOf(0);
			address = params[0];
			try
			{
				// Connection
				connection = WiFiPort.getInstance().open(params[0]);
				// Printing
				methods(params[1], new ESCPOSPrinter(connection));
				Thread.sleep(1000);
				// Disconnection
				connection.close();
			}
			catch (IOException e)
			{
				Log.e(TAG,e.getMessage(),e);
				result = Integer.valueOf(-1);
			}
			catch (InterruptedException e)
			{
				Log.e(TAG,e.getMessage(),e);
				result = Integer.valueOf(-2);
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			int retVal = result.intValue();
			if(retVal < 0)
			{
				Toast.makeText(context, "["+address+"] Connection Failed.", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
}