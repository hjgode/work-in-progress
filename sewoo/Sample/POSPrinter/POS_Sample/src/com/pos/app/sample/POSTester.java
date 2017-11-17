package com.pos.app.sample;

import com.pos.app.assist.AlertView;
import com.pos.app.assist.ResourceInstaller;
import com.pos.app.port.wifi.WiFiConnectMenu;
import com.pos.app.port.bluetooth.BluetoothConnectMenu;

import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.WiFiPort;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class POSTester extends TabActivity implements OnTabChangeListener
{
	private TabHost mTabHost;
	private String lastTabID;
	private static Context context;
	private static boolean errConn;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// copy image file to sd-card.
		ResourceInstaller ri = new ResourceInstaller();
		ri.copyAssets(getAssets(),"temp/test");
		// Context
		context = this;
		
		mTabHost = getTabHost();
		mTabHost.addTab(mTabHost.newTabSpec("sample").setIndicator("POS Sample").setContent(
				new Intent(this, ESCPOSMenu.class)));
		
		mTabHost.addTab(mTabHost.newTabSpec("status").setIndicator("Status").setContent(
				new Intent(this, StatusMonitorMenu.class)));
				
		mTabHost.addTab(mTabHost.newTabSpec("bluetoothMenu").setIndicator("Bluetooth").setContent(
				new Intent(this, BluetoothConnectMenu.class)));		

		mTabHost.addTab(mTabHost.newTabSpec("wifiMenu").setIndicator("Wi-Fi").setContent(
				new Intent(this, WiFiConnectMenu.class)));		
		// 01234
//		mTabHost.setCurrentTabByTag("wifiMenu");
		mTabHost.setCurrentTabByTag("bluetoothMenu");
		mTabHost.setOnTabChangedListener(this);
		lastTabID = mTabHost.getCurrentTabTag();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		finish();
	}
	
	@Override
	public void onTabChanged(String tabId)
	{
		Log.d("onTabChanged",tabId);
		if((!BluetoothPort.getInstance().isConnected()) && (!WiFiPort.getInstance().isConnected()))
		{
			int index = mTabHost.getCurrentTab();
			if(index < 2)
			{
				mTabHost.setCurrentTabByTag(lastTabID);
				AlertView.showAlert("Interface not connected.", this);
			}
			else
			{
				lastTabID = tabId;
			}
		}
//		else if(BluetoothPort.getInstance().isConnected() && (tabId.compareTo("wifiMenu") == 0))
//		{
//			mTabHost.setCurrentTabByTag(lastTabID);
//			AlertView.showAlert("Bluetooth already connected.", this);
//		}
//		else if(WiFiPort.getInstance().isConnected() && (tabId.compareTo("bluetoothMenu") == 0))
//		{
//			mTabHost.setCurrentTabByTag(lastTabID);
//			AlertView.showAlert("Wi-Fi already connected.", this);
//		}
		else
		{
			lastTabID = tabId;
		}
	}

	// 연결 끊김에 따른 에러 표시를 위해 쓰임
	public static Context getContext()
	{
		return context;
	}
	
	public static void setConnErrOccur(boolean errconn)
	{
		errConn = errconn;
	}

	public static boolean isConnErrOccur()
	{
		return errConn;
	}
}