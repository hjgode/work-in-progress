package com.mobile.app.cpcl;

import java.io.IOException;

import com.mobile.app.assist.AlertView;
import com.mobile.app.assist.CONSTANTS;
import com.mobile.app.assist.CPCLSample3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class CPCL3Menu extends Activity
{
	// Menu
	final static String[] menuArray = 
	{
//		"Barcode",
//		"Sewoo Tech.",
		"Print PDF", //"Image Test",
//		"Font Type Test",
//		"MULTILINE",
//		"COUNTRY",
//		"Print Android Font",
//		"Print Multilingual"
	};
	
	private String strCount;
	private Spinner paperSpinner;
//	private ListView sampleList;
	String bitmapFilename=null;
    static final String TAG = "CPCL3Menu";
    Context context;
    Object _printBitmapTask=null;

    @Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d("CPCL3Menu", "OnDestroy");
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.label_sample_menu);
		//bitmapFilename=CPCLTester.Bit
        context=CPCL3Menu.this;

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
        Log.d(TAG, "OnCreate done.");
	}

	String getBitmapFilename(){
	    Log.d(TAG, "load Bitmap filename");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String bitmapfilename = sharedPref.getString(CONSTANTS.PrefBitmapfilename, "");
        return bitmapfilename;
    }

	// Dialog
	private void dialog(final int index)
	{
        if(_printBitmapTask!=null){
            Toast.makeText(context, "Already printing, please wait", Toast.LENGTH_LONG);
        }

        final LinearLayout linear = (LinearLayout)
			View.inflate(CPCL3Menu.this, R.layout.input_popup, null);
		
		final EditText number = (EditText)linear.findViewById(R.id.EditTextPopup);
		if(strCount == null)
			strCount = "1";
		number.setText(strCount);
		
		new AlertDialog.Builder(CPCL3Menu.this)
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
					CPCLSample3 sample = new CPCLSample3();
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
/*						case 0:
							sample.barcode3(count);
							break;
						case 1:
							sample.Profile3(count);
							break;
						case 2:
							sample.image3(count);
							break;
						case 3:
							sample.fontTypeTest(count);
							break;
						case 4:
							sample.multiLineTest(count);
							break;
						case 5:
							sample.countryTest(count);
							break;
						case 6:
							sample.printAndroidFont(count);
							break;
						case 7:
							sample.printMultilingualFont(count);
							break;	
*/						case 0:
                        //bitmapFilename=CPCLTester.getBitmapFilename();
                        bitmapFilename=getBitmapFilename();
                        if(bitmapFilename!=null && bitmapFilename.length()>0){
                            if(_printBitmapTask==null){
                                new printBitmapFiles().execute(bitmapFilename);
                            }
                            else{
                                Toast.makeText(context, "Already printing, please wait", Toast.LENGTH_LONG);
                            }
						    //sample.image3(count, bitmapFilename);
                        }
                        else {
                            AlertView.showAlert("Error with Bitmapfile!", context);
                        }
						break;
						default:
					}
				}
				catch(NumberFormatException e)
				{
					Log.e("NumberFormatException","Invalid Input Nubmer.",e);
				}
/*				catch(IOException e)
				{
					Log.e("IO Exception", "IO Error",e);	
				}
*/			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				Log.d("Cancel", "Cancel Button Clicked.");
			}
		})
		.show();
	}

	void setProgressPercent(int percent){
        Toast.makeText(context, "Printing done: "+percent, Toast.LENGTH_SHORT);
    }

	// usage: new printBitmapFiles().execute(string, int, long);
    //                                      <Parameters, Progress, Result>
	class printBitmapFiles extends AsyncTask<String , Integer, Long >{
        @Override
        protected void onPreExecute() {
            _printBitmapTask=this;
            Log.d(TAG, "_printBitmapTask=this;");
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... strings){ //Parameters
            long count=strings.length;
            publishProgress((int)count);
            String sFileBitmap=strings[0];
            CPCLSample3 sample = new CPCLSample3();
            try {
                Log.d(TAG, "AsyncTask starting printBitmap");
                sample.image3(1, sFileBitmap);
                Log.d(TAG, "AsyncTask printBitmap DONE");
            }catch (IOException ex){
                Log.d(TAG, "doInBackground: sample.image3 filed for "+sFileBitmap);
            }
            return count;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            new AlertDialog.Builder(context)
                    .setTitle("PDF Print done")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                        }
                    })
            .show();
//            showDialog("Downloaded " + result + " bytes");
            Log.d(TAG, "_printBitmapTask=null;");
            _printBitmapTask=null;
        }
	}
}