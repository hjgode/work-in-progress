package com.pos.app.port;

import com.pos.app.assist.AddressRepo;
import com.pos.app.multi.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

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

public class MultiConnectMenu extends Activity
{
	private static final String TAG = "WiFiConnectMenu";
	private final int sendRequestCode = 2345;
	private Context context;
	private EditText ipAddrBox;
	private Button AddButton;
	private ListView list;
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multi_conn_menu);
		context = this;
		ipAddrBox = (EditText) findViewById(R.id.EditTextAddressIP);		
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice);		
		list = (ListView) findViewById(R.id.ipList);
		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		loadSettingFile();
		
		Button bluetoothButton = (Button) findViewById(R.id.btListButton);
		bluetoothButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, DeviceListActivity.class);
				startActivityForResult(intent, sendRequestCode);
			}
		});
		Button bluetoothRemoveButton = (Button) findViewById(R.id.btRemoveButton);
		bluetoothRemoveButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				TextView t = (TextView) findViewById(R.id.btAddr);
				t.setText("Bluetooth");
				AddressRepo.getInstance().setBluetoothAddress("");
			}
		});
		
		// delete address.
		list.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			String clicked;
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{				
				boolean checked = list.isItemChecked(arg2);
				clicked = adapter.getItem(arg2);
				
				if(!checked)
				{
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
						}
					})
					.show();
				}
				return true;
			}
		});
		
		// Connect or Disconnect
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				String ipAddr = (String) list.getItemAtPosition(arg2);
				boolean checked = list.isItemChecked(arg2);
				
				if(!checked)
				{
					AddressRepo.getInstance().removeIP(ipAddr);
				}
				else
				{
					AddressRepo.getInstance().addIP(ipAddr);
				}
			}
		});
		
		// Add IP Address.
		AddButton = (Button) findViewById(R.id.ButtonAddIP);
		AddButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String ip = ipAddrBox.getText().toString();		
				addIpList(ip);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Bundle b = new Bundle();
		if (requestCode == sendRequestCode)
		{
			if (resultCode == RESULT_OK)
			{
				b = data.getExtras();
				String arr = b.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// TODO Address saved.
				AddressRepo.getInstance().setBluetoothAddress(arr);
				TextView t = (TextView) findViewById(R.id.btAddr);
				t.setText(arr);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onDestroy()
	{
		saveSettingFile();
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
	
	// IP list.
	public static Vector<String> ipAddrVector;	
	private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//temp";
	private static final String fileName = dir + "//WFPrinter";
	
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
		// UI
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
		int pos = 0;
		String item = null;
		while(!adapter.isEmpty())
		{
			item = adapter.getItem(pos);
			adapter.remove(item);
			pos++;
		}
		
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
	
	/* TODO DELETE
	class MultiConnectorTask extends AsyncTask<String, Void, Integer>
	{
		final String TAG = "MultiConnectorTask";
		
		@Override
		protected Integer doInBackground(String... params)
		{
			try
			{
				MultiConnectionRepo.getInstance().add(params[0]);
			}
			catch (InterruptedException e)
			{
				Log.e(TAG,e.getMessage(),e);
				return Integer.valueOf(params[1]);
			}
			catch (IOException e)
			{
				Log.e(TAG,e.getMessage(),e);
				return Integer.valueOf(params[1]);
			}
			return Integer.valueOf(-1);
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			int retVal = result.intValue();
			if(retVal != -1)
			{
				Toast.makeText(context, "Connection Failed.", Toast.LENGTH_SHORT).show();
				if(retVal >= 0)
				{
					list.setItemChecked(retVal, false);
				}
			}
			else
			{
				Toast.makeText(context, "Connected.", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	*/
}