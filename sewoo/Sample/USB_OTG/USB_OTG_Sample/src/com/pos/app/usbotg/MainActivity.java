package com.pos.app.usbotg;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Action Bar
		ActionBar ab = getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Fragment Tab
		Tab tab = ab.newTab().setText(R.string.usb_tab)
				.setTabListener(new MyTabListener(this, USBFragment.class.getName()));
		ab.addTab(tab);

		tab = ab.newTab().setText(R.string.msr_tab)
				.setTabListener(new MyTabListener(this, MSRFragment.class.getName()));
		ab.addTab(tab);
	}

	// TabListener
	private class MyTabListener implements ActionBar.TabListener
	{
		private static final String TAG = "MyTabListener";
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mFragName;

		public MyTabListener(Activity activity, String fragName)
		{
			mActivity = activity;
			mFragName = fragName;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft)
		{
			// Do nothing.
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft)
		{
			if(mFragment == null)
			{
				mFragment = Fragment.instantiate(mActivity, mFragName);
				ft.add(android.R.id.content, mFragment);
			}
			else
			{
				ft.attach(mFragment);
			}
			Log.d(TAG,"mytab select");
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft)
		{
			Log.d(TAG,"mytab hide");
			if(mFragment != null)
				ft.detach(mFragment);
		}
	}
}
