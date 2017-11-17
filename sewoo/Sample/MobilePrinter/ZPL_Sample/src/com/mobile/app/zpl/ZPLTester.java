package com.mobile.app.zpl;

import com.mobile.app.assist.AlertView;
import com.mobile.app.assist.ResourceInstaller;
import com.mobile.app.port.bluetooth.BluetoothConnectMenu;
import com.mobile.app.port.wifi.WiFiConnectMenu;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.WiFiPort;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class ZPLTester extends TabActivity implements OnTabChangeListener
{
	private TabHost mTabHost;
	private String lastTabID;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// copy image file to sd-card.
		ResourceInstaller ri = new ResourceInstaller();
		ri.copyAssets(getAssets(),"temp/test");
		
		mTabHost = getTabHost();
		mTabHost.addTab(mTabHost.newTabSpec("sample2").setIndicator("ZPL Sample").setContent(
				new Intent(this, ZPL2Menu.class)));
		mTabHost.addTab(mTabHost.newTabSpec("bluetoothMenu").setIndicator("Bluetooth").setContent(
				new Intent(this, BluetoothConnectMenu.class)));
		mTabHost.addTab(mTabHost.newTabSpec("wifiMenu").setIndicator("Wi-Fi").setContent(
				new Intent(this, WiFiConnectMenu.class)));
	
		// 0,1,2,3,4
		mTabHost.setCurrentTabByTag("wifiMenu");
//		mTabHost.setCurrentTabByTag("bluetoothMenu");
		mTabHost.setOnTabChangedListener(this);
		lastTabID = mTabHost.getCurrentTabTag();
		
		Log.i("com.sewoo",Build.VERSION.CODENAME+" "+Build.VERSION.SDK_INT);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		finish();
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
		if((!BluetoothPort.getInstance().isConnected()) && (!WiFiPort.getInstance().isConnected()))
		{
			int index = mTabHost.getCurrentTab();
			if(index < 1)
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
}