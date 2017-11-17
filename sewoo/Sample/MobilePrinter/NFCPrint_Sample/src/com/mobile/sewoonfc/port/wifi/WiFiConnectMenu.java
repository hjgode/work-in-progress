package com.mobile.sewoonfc.port.wifi;

import com.mobile.sewoonfc.escpos.R;
import com.mobile.sewoonfc.assist.AlertView;
import com.sewoo.port.android.WiFiPort;
import com.sewoo.request.android.RequestHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

public class WiFiConnectMenu extends Activity
{
	private static final String TAG = "WiFiConnectMenu";
	private Thread hThread;
	private Context context;
	private EditText ipAddrBox;
	private Button connectButton;
	private WiFiPort wifiPort;
	private ListView list;
	
//	private boolean delete;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_menu);
		context = this;
		wifiPort = WiFiPort.getInstance();
		ipAddrBox = (EditText) findViewById(R.id.EditTextAddressIP);		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		list = (ListView) findViewById(R.id.ipList);
		list.setAdapter(adapter);
		loadSettingFile();
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				// TODO Auto-generated method stub
				// Connect
				String ip = ipAddrVector.elementAt(arg2);
				try
				{
					if(!wifiPort.isConnected())
					{	
						ipAddrBox.setText(ip);
						wifiConn(ip);
					}
				}
				catch (IOException e)
				{
					Log.e(TAG,e.getMessage(),e);
				}
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			//boolean delete = false;
			String clicked;
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				clicked = ipAddrVector.elementAt(arg2);
				new AlertDialog.Builder(context)
				.setTitle("Wi-Fi connection history")
				.setMessage("Delete '"+clicked+"' ?")
				.setPositiveButton("YES", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Log.i(TAG,"YES Click "+clicked);
						deleteIpList(clicked);
					}
				})
				.setNegativeButton("NO", new DialogInterface.OnClickListener()
				{		
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Log.i(TAG,"NO Click"+clicked);
						//delete = false;
					}
				})
				.show();
				return true;
			}
		});
		
		connectButton = (Button) findViewById(R.id.ButtonConnectIP);
		connectButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					if(wifiPort.isConnected())
					{
						wifiDisConn();
					}
					else
					{
						String ip = ipAddrBox.getText().toString();
						wifiConn(ip);
					}
				}
				catch (IOException e)
				{
					Log.e(TAG,e.getMessage(),e);
				}
				catch (InterruptedException e)
				{
					Log.e(TAG,e.getMessage(),e);
				}
			}
		});
	}
	
	@Override
	protected void onDestroy()
	{
		saveSettingFile();
		try
		{
			if(wifiPort != null)
				wifiPort.disconnect();
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
		super.onDestroy();
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu,menu);	
    	return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
    	switch (item.getItemId()) 
    	{
			case R.id.deleteall:
				// YES NO
				if(IpListCount() > 0)
					new AlertDialog.Builder(context)
					.setTitle("Wi-Fi connection history")
					.setMessage("Delete All?")
					.setPositiveButton("YES", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							deleteAllIpList();
						}
					})
					.setNegativeButton("NO", new DialogInterface.OnClickListener()
					{		
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
						}
					})
					.show();
				return true;
			case R.id.cancel:
				closeOptionsMenu();
				return true;
    	}
    	return false;
	}
	
	ArrayAdapter<String> adapter;
	private Vector<String> ipAddrVector;
	private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//temp";
	private static final String fileName = dir + "//WFPrinter";
	private String lastConnAddr;
	
	private void loadSettingFile()
	{
		String line;
		ipAddrVector = new Vector<String>();
		try
		{	
			// Retrieve the connection history from the file.
			BufferedReader fReader = new BufferedReader(new FileReader(fileName));
			while((line = fReader.readLine()) != null)
			{
				ipAddrVector.addElement(line);
				adapter.add(line);
			}
			fReader.close();
			if(ipAddrVector.size() > 0)
			{
				lastConnAddr = ipAddrVector.firstElement();
				ipAddrBox.setText(lastConnAddr);	
			}
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
			BufferedWriter fWriter = new BufferedWriter(new FileWriter(fileName));
			Iterator<String> iter = ipAddrVector.iterator();
			while(iter.hasNext())
			{
				fWriter.write(iter.next());
				fWriter.newLine();
			}
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
	
	// if address already exists in list, it would inserted LIFO.
	private void addIpList(String addr)
	{
		deleteIpList(addr);
		ipAddrVector.insertElementAt(addr, 0);
		adapter.insert(addr, 0);
	}
	
	// Delete Wi-Fi connection.
	private void deleteIpList(String addr)
	{
		if(ipAddrVector.contains(addr))
		{
			ipAddrVector.remove(addr);
		}
		if(adapter.getPosition(addr) >= 0)
		{
			adapter.remove(addr);
		}
	}
	
	// Delete all Wi-Fi connection history.
	private void deleteAllIpList()
	{
		ListIterator<String> listIter = ipAddrVector.listIterator();
		while(listIter.hasNext())
		{
			adapter.remove(listIter.next());
		}
		ipAddrVector = new Vector<String>();
	}
	
	private int IpListCount()
	{
		int ac = adapter.getCount();
		int vc = ipAddrVector.size();
		if(ac == vc)
			return ac;
		else
			return -1;
	}
	
	// WiFi Connection method.
	private void wifiConn(String ipAddr) throws IOException
	{
		new connTask().execute(ipAddr);
	}
	// WiFi Disconnection method.
	private void wifiDisConn() throws IOException, InterruptedException
	{
		wifiPort.disconnect();
		connectButton.setText(getResources().getString(R.string.dev_conn_btn));
		ipAddrBox.setEnabled(true);
		hThread.interrupt();
		Toast toast = Toast.makeText(context, getResources().getString(R.string.wifi_disconn_msg), Toast.LENGTH_SHORT);
		toast.show();	
	}
	
	// WiFi Connection Task.
	class connTask extends AsyncTask<String, Void, Integer>
	{
		private final ProgressDialog dialog = new ProgressDialog(WiFiConnectMenu.this);
		
		@Override
		protected void onPreExecute()
		{
			dialog.setTitle(getResources().getString(R.string.wifi_tab));
			dialog.setMessage(getResources().getString(R.string.connecting_msg));
			dialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Integer doInBackground(String... params)
		{
			Integer retVal = null;
			try
			{
				// ip 
				wifiPort.connect(params[0]);
				lastConnAddr = params[0];
				retVal = new Integer(0);
			}
			catch (IOException e)
			{
				Log.e(TAG,e.getMessage(),e);
				retVal = new Integer(-1);
			}
			return retVal;
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			if(result.intValue() == 0)
			{
				RequestHandler rh = new RequestHandler();				
				hThread = new Thread(rh);
				hThread.start();
				addIpList(lastConnAddr);
				connectButton.setText(getResources().getString(R.string.dev_disconn_btn));
				ipAddrBox.setEnabled(false);				
				if(dialog.isShowing())
					dialog.dismiss();
				Toast toast = Toast.makeText(context, getResources().getString(R.string.wifi_conn_msg), Toast.LENGTH_SHORT);
				toast.show();	
			}
			else
			{	
				if(dialog.isShowing())
					dialog.dismiss();
				AlertView.showAlert(getResources().getString(R.string.wifi_conn_fail_msg),
						getResources().getString(R.string.dev_check_msg), context);
			}
			super.onPostExecute(result);
		}
	}
}