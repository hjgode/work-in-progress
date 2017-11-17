package com.mobile.app.zpl;

import java.io.IOException;

import com.mobile.app.assist.ZPLSample2;
import com.sewoo.app.zpl.R;
import com.mobile.app.zpl.FileViewer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

public class ZPL2Menu extends ListActivity
{
	private final String TAG = "ZPL2Menu";
	
	private Context context;
	private static final String fKey = "FILE_PATH";

	// Menu
	final static String[] arr = 
	{
		"Text",								// 0
		"Geometry",							// 1
		"Image",							// 2
		"1D Barcode",						// 3
		"2D Barcode",						// 4
		"UTF-8 Printing",					// 5
		"Send File to test",				// 6
		"Image object(140x121)"				// 7
	};
	
	private String strCount;
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "OnDestroy");
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
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
		final LinearLayout linear = (LinearLayout)
			View.inflate(ZPL2Menu.this, R.layout.input_popup, null);
		
		final EditText number = (EditText)linear.findViewById(R.id.EditTextPopup);
		if(strCount == null)
			strCount = "1";
		number.setText(strCount);
		
		if(arg2 == 6)
		{
			Intent ni = new Intent(context, FileViewer.class);
			ni.putExtra(fKey, "//sdcard//temp//test//ZPL_UTF8_FONT.txt");
			startActivity(ni);
		} else {
			// count popup.
			new AlertDialog.Builder(ZPL2Menu.this)
			.setTitle("Test Count.")
			.setIcon(R.drawable.icon)
			.setView(linear)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					try
					{
						ZPLSample2 sample = new ZPLSample2();
						strCount = number.getText().toString();
						int count = Integer.parseInt(strCount);
						Log.d(TAG,"NUM "+count);
					
						switch(arg2)
						{
							case 0:
								sample.textTest(count);
								break;
							case 1:
								sample.geometryTest(count);
								break;
							case 2:
								sample.imageTest(count);
								break;
							case 3:
								sample.barcode1DTest(count);
								break;
							case 4:
								sample.barcode2DTest(count);
								break;
							case 5:
								sample.printUTF8(count);
								break;
							case 7:
								sample.imageObject(count);
								break;
							default:
						}
					
					}
					catch(NumberFormatException e)
					{
						Log.e(TAG, "Invalid Input Nubmer. : "+e.getMessage(), e);
					}
					catch(IOException e)
					{
						Log.e(TAG, "IO Error. : "+e.getMessage(), e);	
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) 
				{
					Log.d(TAG, "Cancel Button Clicked.");
				}
			})
			.show();
		}
	}
}