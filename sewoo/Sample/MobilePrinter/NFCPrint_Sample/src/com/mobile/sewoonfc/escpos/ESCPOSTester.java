package com.mobile.sewoonfc.escpos;

import com.mobile.sewoonfc.assist.AlertView;
import com.mobile.sewoonfc.assist.ResourceInstaller;
import com.mobile.sewoonfc.port.bluetooth.BluetoothConnectMenu;
import com.mobile.sewoonfc.port.wifi.WiFiConnectMenu;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.port.android.WiFiPort;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class ESCPOSTester extends TabActivity implements OnTabChangeListener
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
		mTabHost.addTab(mTabHost.newTabSpec("sample2").setIndicator("2 \"").setContent(
				new Intent(this, ESCP2Menu.class)));
		
		mTabHost.addTab(mTabHost.newTabSpec("sample3").setIndicator("3 \"").setContent(
				new Intent(this, ESCP3Menu.class)));
		
		mTabHost.addTab(mTabHost.newTabSpec("sample4").setIndicator("4 \"").setContent(
				new Intent(this, ESCP3Menu.class)));

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
}