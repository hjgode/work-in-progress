package com.mobile.app.zpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sewoo.app.zpl.R;
import com.sewoo.jpos.request.RequestQueue;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FileViewer extends Activity
{
	private static final String fKey = "FILE_PATH";	
	private RequestQueue rq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_viewer);
		// Using Filepath for open Image file.
		Bundle extra = getIntent().getExtras();
		final String filePath = extra.getString(fKey);
		File f = new File(filePath);
		rq = RequestQueue.getInstance();
		TextView tv = (TextView) findViewById(R.id.TextView01);
		tv.setText("File : "+f.getName()+"\r\nFile size : "+f.length()+"\r\nFile Path : "+f.getPath());
		
	    // Button Listener
	    Button sendButton  = (Button) findViewById(R.id.Button01);
		sendButton.setOnClickListener(new OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
				int rin = 0;
				FileInputStream fis = null;
				byte buffer[] = new byte[1024];
				// Send
				try
				{
					fis = new FileInputStream(filePath);
					while((rin = (fis.read(buffer))) > 0)
					{
						byte temp [] = new byte[rin];
						System.arraycopy(buffer, 0, temp, 0, rin);
						rq.addRequest(temp);									
					}
					fis.close();
				}
				catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					Log.d("ERROR",e.getMessage());
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					Log.d("ERROR",e.getMessage());
					if(fis != null)
					{
						try
						{
							fis.close();
						}
						catch(Exception ex){}
					}
				}
			}
		});
	}
}
