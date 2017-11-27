package com.mobile.app.cpcl;

import com.mobile.app.assist.AlertView;
import com.mobile.app.assist.CONSTANTS;
import com.mobile.app.assist.PDFprint;
import com.mobile.app.assist.ResourceInstaller;
import com.mobile.app.assist.myIntentService;
import com.mobile.app.port.bluetooth.BluetoothConnectMenu;
import com.mobile.app.port.wifi.WiFiConnectMenu;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.WiFiPort;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;

public class CPCLTester extends TabActivity implements OnTabChangeListener
{
	static String TAG="CPCLTester";

	private TabHost mTabHost;
	private String lastTabID;

	File pdfFile=null;
    public static String BitmapFilename=null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// copy image file to sd-card.
		ResourceInstaller ri = new ResourceInstaller();
		ri.copyAssets(getAssets(),"temp/test");
		
		mTabHost = getTabHost();			
		//mTabHost.addTab(mTabHost.newTabSpec("sample2").setIndicator("2\" CPCL").setContent(new Intent(this, CPCL2Menu.class)));
		mTabHost.addTab(mTabHost.newTabSpec("sample3").setIndicator("PDF print").setContent(new Intent(this, CPCL3Menu.class)));
		//mTabHost.addTab(mTabHost.newTabSpec("sample4").setIndicator("4\" CPCL").setContent(new Intent(this, CPCL4Menu.class)));
		mTabHost.addTab(mTabHost.newTabSpec("bluetoothMenu").setIndicator("Bluetooth").setContent(new Intent(this, BluetoothConnectMenu.class)));
		//mTabHost.addTab(mTabHost.newTabSpec("wifiMenu").setIndicator("Wi-Fi").setContent(new Intent(this, WiFiConnectMenu.class)));
	
		// 0,1,2,3,4
		//mTabHost.setCurrentTabByTag("wifiMenu");
		mTabHost.setCurrentTabByTag("bluetoothMenu");
		mTabHost.setOnTabChangedListener(this);
		lastTabID = mTabHost.getCurrentTabTag();

		enableCPCLtab(false);

		//have we launched by an Intent?
		Uri data = getIntent().getData();
		if (data != null) {
			getIntent().setData(null);
			try {
				pdfFile = new File(data.getPath());
				String fileName = pdfFile.getPath();
				//txtFile.setText(fileName);
				Toast.makeText(this, TAG + " " + fileName, Toast.LENGTH_LONG);
				Log.d(TAG, "data=" + data);
				updateStatus("Launched by Intent: "+fileName);
				//start IntentService to convert PDF and print
				float scale=(float)3.0;
				launchIntentService(fileName, scale );
			} catch (Exception e) {
				// warn user about bad data here
				Log.d(TAG, "Exception: " + e.getMessage());
				finish();
//                return;
			}
		}
	}

	void enableCPCLtab(boolean bEnable){
        mTabHost.getTabWidget().getChildTabViewAt(0).setEnabled(bEnable);
    }

    public void launchIntentService(String sFile, float scale) {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, myIntentService.class);
        // Add extras to the bundle
        i.putExtra(CONSTANTS.IntentServiceData_PdfFilename, sFile);
        // Start the service
        updateStatus("Starting Print...");
        startService(i);
    }
    @Override
    protected void onResume(){
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(CONSTANTS.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, filter);
    }

    public static String getBitmapFilename(){
        return BitmapFilename;
    }
    // Define the callback for what to do when data is received
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra(CONSTANTS.IntentServiceData_RESULT_BITMAP_OK, RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                String _BitmapFilename = intent.getStringExtra(CONSTANTS.IntentServiceData_RESULT_BITMAP_FILE);
                updateStatus("Print completed OK: " + _BitmapFilename);
                BitmapFilename=_BitmapFilename;
                saveDataForPrint(BitmapFilename);
                enableCPCLtab(true);
            }
            else if(resultCode==RESULT_CANCELED){
                String resultValue = intent.getStringExtra(CONSTANTS.IntentServiceData_RESULT_MESSAGE);
                updateStatus("Print FAILED: "+resultValue);
            }
        }
    };
    @Override
	protected void onDestroy()
	{
		super.onDestroy();
		finish();
	}
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }

	public static Handler handler = new Handler() 
	{
        @Override
        public void handleMessage(Message msg) 
        {
        	// Just Dummy Message.
        }
	};

	@Override
	public void onTabChanged(String tabId)
	{
		Log.d("onTabChanged",tabId);
		if(BitmapFilename==null)
        {
            if(mTabHost.getCurrentTab()==0){
                AlertView.showAlert("No PDF loaded.", this);
                mTabHost.setCurrentTabByTag(lastTabID);
            }
        }

		if( (!BluetoothPort.getInstance().isConnected() && !WiFiPort.getInstance().isConnected()))
		{
			int index = mTabHost.getCurrentTab();
			if(index < 3)
			{
				mTabHost.setCurrentTabByTag(lastTabID);
				AlertView.showAlert("Interface not connected.", this);
			}
			else
			{
				lastTabID = tabId;
			}
		}
		else if(BluetoothPort.getInstance().isConnected() && (tabId.compareTo("wifiMenu") == 0))
		{
			mTabHost.setCurrentTabByTag(lastTabID);
			AlertView.showAlert("Bluetooth already connected.", this);
		}
		else if(WiFiPort.getInstance().isConnected() && (tabId.compareTo("bluetoothMenu") == 0))
		{
			mTabHost.setCurrentTabByTag(lastTabID);
			AlertView.showAlert("Wi-Fi already connected.", this);
		}
		else
		{
			lastTabID = tabId;
		}
	}

	void saveDataForPrint(String sFilename){
        // Create object of SharedPreferences.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //now get Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        //put your value
        editor.putString(CONSTANTS.PrefBitmapfilename, sFilename);
        //commits your edits
        editor.commit();
    }
	void updateStatus(final String status){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//txtLog.setText(status);
				Log.d(TAG,status);
			}
		});
	}
}