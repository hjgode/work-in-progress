package com.mobile.app.cpcl;

import java.io.IOException;

import com.mobile.app.assist.CPCLSample4;

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

public class CPCL4Menu extends Activity
{
	// Menu
	final static String[] menuArray = 
	{
		"Barcode",
		"Sewoo Tech.",
		"Image Test",
		"Korean Font Test",
		"KFDA",
		"Print Android Font",
		"Print Multilingual"
	};
	
	private String strCount;
	private Spinner paperSpinner;
//	private ListView sampleList;
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d("CPCL4Menu", "OnDestroy");
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
			View.inflate(CPCL4Menu.this, R.layout.input_popup, null);
		
		final EditText number = (EditText)linear.findViewById(R.id.EditTextPopup);
		if(strCount == null)
			strCount = "1";
		number.setText(strCount);
		
		new AlertDialog.Builder(CPCL4Menu.this)
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
					CPCLSample4 sample = new CPCLSample4();
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
							sample.barcode4(count);
							break;
						case 1:
							sample.sewooTech4(count);
							break;
						case 2:
							sample.image3(count);
							break;
						case 3:
							sample.koreanFontTest4(count);
							break;
						case 4:
							sample.KFDA(count);
							break;
						case 5:
							sample.printAndroidFont(count);
							break;
						case 6:
							sample.printMultilingualFont(count);
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