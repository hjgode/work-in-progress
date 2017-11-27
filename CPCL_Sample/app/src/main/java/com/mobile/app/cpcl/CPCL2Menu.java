package com.mobile.app.cpcl;

import java.io.IOException;

import com.mobile.app.assist.AlertView;
import com.mobile.app.assist.CPCLSample2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

public class CPCL2Menu extends Activity
{
	// Menu
	final static String[] menuArray = 
	{
		"Barcode",
		"Sewoo Tech.",
		"2D Barcode",
		"Denmark Stamp",
		"Font Test",
		"Font Type Test",
		"Setting Test1",
		"Setting Test2",
		"MULTILINE",
		"Print Android Font",
		"Print Multilingual",
		"Printer Status",
		"Print GS1 1",
		"Print GS1 2"
	};
	
	private String strCount;
	private Spinner paperSpinner;
//	private ListView sampleList;
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d("CPCL2Menu", "OnDestroy");
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.label_sample_menu);
		
		ArrayAdapter<CharSequence> paperAdapter = ArrayAdapter.createFromResource(this, R.array.label_paper, android.R.layout.simple_spinner_item);
        paperAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paperSpinner = (Spinner) findViewById(R.id.paper_spinner);
        paperSpinner.setAdapter(paperAdapter);
        paperSpinner.setSelection(2);
        
		ListView sampleList = (ListView) findViewById(android.R.id.list);
		sampleList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray));
		sampleList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				dialog(arg2);
			}
		});
	}
	
	// Dialog
	private void dialog(final int index)
	{
		final LinearLayout linear = (LinearLayout)
			View.inflate(CPCL2Menu.this, R.layout.input_popup, null);
		
		final EditText number = (EditText)linear.findViewById(R.id.EditTextPopup);
		if(strCount == null)
			strCount = "1";
		number.setText(strCount);
		
		new AlertDialog.Builder(CPCL2Menu.this)
		.setTitle("Test Count.")
		.setIcon(R.drawable.ic_launcher)
		.setView(linear)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				try
				{
					strCount = number.getText().toString();
					int count = Integer.parseInt(strCount);
					Log.d("NUM",count+"");
					CPCLSample2 sample = new CPCLSample2();
					int paperType = paperSpinner.getSelectedItemPosition();
					// R.array.label_paper [GAP, BM, CONT]
					switch(paperType)
					{
						case 0:
							sample.selectGapPaper();
							break;
						case 1:
							sample.selectBlackMarkPaper();
							break;
						case 2:
							sample.selectContinuousPaper();
							break;
						default:
					}
					switch(index)
					{
						case 0:
							sample.barcodeTest(count);
							break;
						case 1:
							sample.profile2(count);
							break;
						case 2:
							sample.barcode2DTest(count);
							break;
						case 3:
							sample.dmStamp(count);
							break;
						case 4:
							sample.fontTest(count);
							break;
						case 5:
							sample.fontTypeTest(count);
							break;
						case 6:
							sample.settingTest1(count);
							break;
						case 7:
							sample.settingTest2(count);
							break;
						case 8:
							sample.multiLineTest(count);
							break;
						case 9:
							sample.printAndroidFont(count);
							break;
						case 10:
							sample.printMultilingualFont(count);
							break;	
						case 11:
							String strresult = sample.statusCheck();
							AlertView.showAlert("Status Error",
									strresult+" : ", CPCL2Menu.this);
							break;	
						case 12:
							sample.RSS1(count);
							break;	
						case 13:
							sample.RSS2(count);
							break;	
						default:
					}
				}
				catch(NumberFormatException e)
				{
					Log.e("NumberFormatException","Invalid Input Nubmer.",e);
				}
				catch(IOException e)
				{
					Log.e("IO Exception", "IO Error",e);	
				}
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				Log.d("Cancel", "Cancel Button Clicked.");
			}
		})
		.show();
	}
}