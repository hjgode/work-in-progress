package com.mobile.sewoonfc.escpos;

import com.mobile.sewoonfc.assist.AlertView;
import com.mobile.sewoonfc.assist.ESCPSample2;
import com.sewoo.jpos.command.ESCPOSConst;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ESCP2Menu extends ListActivity
{
	private final String TAG = "Sample2";
	// Menu
	final static String[] arr = 
	{
		"Sample 1",
		"Sample 2",
		"Sample 3",
		"Image Test",
		"e-Invoice",
		"Print DataMatrix",
		"Print Android Font",
		"Print Multilingual"
	};
	
	@Override
	protected void onDestroy()
	{
//		Log.d(TAG,"onDestroy()");
		super.onDestroy();
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , arr));
		getListView().setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				dialog(arg2);
			}
		});
	}
	
	// Dialog
	private void dialog(int arg1)
	{
		final int arg2 = arg1;
		int retval = 0;
		ESCPSample2 sample = new ESCPSample2();
		try
		{
			switch(arg2)
			{
				case 0:
					retval = sample.sample1();
					break;
				case 1:
					retval = sample.sample2();
					break;
				case 2:
					retval = sample.sample3();
					break;
				case 3:
					retval = sample.imageTest();
					break;
				case 4:
					retval = sample.invoice();
					break;	
				case 5:
					retval = sample.printDataMatrix();
					break;	
				case 6:
					retval = sample.printAndroidFont();
					break;
				case 7:
					retval = sample.printMultilingualFont();
					break;	
				default:
			}
		}
		catch(Exception e)
		{
			AlertView.showAlert(e.toString(), e.getMessage(), ESCP2Menu.this);
			Log.e(TAG,e.toString() +" "+ e.getMessage());
		}
		if(retval != ESCPOSConst.LK_STS_NORMAL)
		{
			String msg = "";	
			if((ESCPOSConst.LK_STS_BATTERY_LOW & retval)> 0)
				msg = "Battery Low\r\n";
			if((ESCPOSConst.LK_STS_COVER_OPEN & retval) > 0)
				msg = msg + "Cover Open\r\n";
			if((ESCPOSConst.LK_STS_MSR_READ & retval) > 0)
				msg = msg + "MSR Read status\r\n";
			if((ESCPOSConst.LK_STS_PAPER_EMPTY & retval) > 0)
				msg = msg + "Paper Empty\r\n";
			// Show Status Error.
			AlertView.showAlert("Status Error",
					msg+" : "+retval, ESCP2Menu.this);
		}
	}
}