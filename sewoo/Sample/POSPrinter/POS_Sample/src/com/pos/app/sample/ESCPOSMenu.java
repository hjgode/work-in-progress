package com.pos.app.sample;

import java.io.IOException;

import com.pos.app.assist.AlertView;
import com.pos.app.assist.ESCPOSSample;
import com.pos.app.sample.R;
import com.sewoo.jpos.command.ESCPOSConst;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;

public class ESCPOSMenu extends ListActivity
{
	// Menu
	final static String[] arr = 
	{
		"Sample 1",
		"Sample 2",
		"Image Test",
		"Character Test",
		"Barcode 1D",
		"Barcode 2D",
		"Print Android Font",
		"Print Multilingual"
	};
	
	private String strCount;
	private static Context context;

	@Override
	protected void onDestroy()
	{
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
		final LinearLayout linear = (LinearLayout)
			View.inflate(ESCPOSMenu.this, R.layout.input_popup, null);
		
		final EditText number = (EditText)linear.findViewById(R.id.EditTextPopup);
		if(strCount == null)
			strCount = "1";
		number.setText(strCount);
		
		new AlertDialog.Builder(ESCPOSMenu.this)
		.setTitle("Test Count.")
		.setIcon(R.drawable.ic_launcher)
		.setView(linear)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				int results = 0;
				try
				{
					ESCPOSSample sample = new ESCPOSSample();					
					strCount = number.getText().toString();
					int count = Integer.parseInt(strCount);
					Log.d("NUM",count+"");
					for(int i=0;i<count;i++)
					{
						switch(arg2)
						{
							case 0:
								try
								{
									results = sample.sample1();
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 1:
								try
								{
									results = sample.sample2();
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 2:
								try
								{
									results = sample.imageTest();
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 3:
								try
								{
									results = sample.westernLatinCharTest();
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 4:
								try
								{
									results = sample.barcode1DTest();
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 5:
								try
								{
									results = sample.barcode2DTest();
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case 6:
								try
								{
									results = sample.printAndroidFont();
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;	
							case 7:
								try
								{
									results = sample.printMultilingualFont();
								}
								catch (InterruptedException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;	
							default:
						}
					}
					if( results != 0 )
					{
						Context ctx = POSTester.getContext();
						switch(results)
						{
						case ESCPOSConst.STS_PAPEREMPTY:
							AlertView.showAlert("Error","Paper Empty",ctx);
							break;
						case ESCPOSConst.STS_COVEROPEN:
							AlertView.showAlert("Error","Cover Open",ctx);
							break;
						case ESCPOSConst.STS_PAPERNEAREMPTY:
							AlertView.showAlert("Warning","Paper Near Empty",ctx);
							break;
						}
					}
				}
				catch(NumberFormatException e)
				{
					Log.d("NumberFormatException","Invalid Input Nubmer.");
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