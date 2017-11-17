package com.pos.app.assist;

import java.io.InputStream;

import com.sewoo.port.PortMediator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class StatusChecker
{
	private static final String TAG = "StatusChecker";
	private static final String key = "status";
	private StatusReader task;
	private Handler handler;
	private InputStream is;
	
	public StatusChecker()
	{
		is = PortMediator.getInstance().getIs();
	}
	
	public void start()
	{
		task = new StatusReader();
		task.setStopFlag(false);
		task.execute();
	}
	
	public void stop()
	{
		task.setStopFlag(true);
		task.cancel(true);
	}
	
	/**
	 * Set the Handler instance to use in this class.
	 * @param handler 
	 */
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}
	/**
	 * Get the Handler instance.
	 * @return handler
	 */
	public Handler getHandler()
	{
		return handler;
	}
	
	class StatusReader extends AsyncTask<Void, Void, Void>
	{
		private boolean stopFlag;
		
		public void setStopFlag(boolean stop)
		{
			this.stopFlag = stop;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
		}

/**		
	case 0x00: // 에러 복구(Error recovery).
	case 0x01: // 인쇄 중(Now Printing).
	case 0x02: // 용지 없음(Paper Empty).
	case 0x04: // 커버 열림(Cover Open).
	case 0x06: // 커버 열림(Cover Open).
	case 0x08: // Battery Low.
	case 0x10: // 블랙 마크 검색 에러(Black Mark sensor error).
	case 0x20: // 인쇄 완료(Complete Printing).
*/
		
		@Override
		protected Void doInBackground(Void... params)
		{
			byte[] buffer = new byte[4];
			int rin = 0;

			byte[] Totalbuffer = new byte[16];
			int Totalrin = 0;

			Message msg;
			Bundle bundle = new Bundle();
			try
			{
				while(true)
				{	
					if(stopFlag)
					{
						break;
					}
					rin = is.read(buffer);
					if(rin < 4)
					{
						System.arraycopy(buffer, 0, Totalbuffer, 0, rin);
						Thread.sleep(500);
						Totalrin = is.read(buffer);
						System.arraycopy(buffer, 0, Totalbuffer, rin, Totalrin);
						rin = rin + Totalrin;
					} else {
						System.arraycopy(buffer, 0, Totalbuffer, 0, rin);
					}
					if(handler != null)
					{
						// TODO
						msg = Message.obtain();
						msg.what = 0;
						bundle.putByteArray(key, Totalbuffer);
						msg.setData(bundle);
						handler.sendMessage(msg);
					}
					Log.d(TAG,"Read "+Totalbuffer[0]+" "+Totalbuffer[1]+" "+Totalbuffer[2]+" "+Totalbuffer[3]+" "+rin);
					Thread.sleep(1000);
				}
			}
			catch(Exception e)
			{
				handler.sendEmptyMessage(-1);
				Log.e(TAG + e.toString(), e.getMessage());
			}
			return null;
		}
	}
}
