package com.pos.app.multi;

import com.pos.app.assist.ResourceInstaller;
import com.pos.app.port.MultiConnectMenu;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class POSMultiTester extends TabActivity implements OnTabChangeListener
{
	private TabHost mTabHost;
	private static Context context;
	private static boolean errConn;
	private final String TAG = "POSMultiTesterTab";
	
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
				new Intent(this, ESCPOSMultiMenu.class)));
		
		mTabHost.addTab(mTabHost.newTabSpec("connection").setIndicator("Connection").setContent(
				new Intent(this, MultiConnectMenu.class)));
		
		// 01234
		mTabHost.setCurrentTabByTag("connection");
		mTabHost.setOnTabChangedListener(this);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		finish();
	}

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

	@Override
	public void onTabChanged(String tabId)
	{
		// TODO Auto-generated method stub
	}
}