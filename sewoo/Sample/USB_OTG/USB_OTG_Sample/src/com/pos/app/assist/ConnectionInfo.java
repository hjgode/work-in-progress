package com.pos.app.assist;

import com.sewoo.port.android.DeviceConnection;
import com.sewoo.request.android.AndroidMSR;

public class ConnectionInfo
{
	private static ConnectionInfo connectionInfo;
	private DeviceConnection connection;
	private AndroidMSR androidMSR;
	
	public static ConnectionInfo getInstance()
	{
		if(connectionInfo == null)
			connectionInfo = new ConnectionInfo();
		return connectionInfo;
	}
	
	private ConnectionInfo()
	{}

	public void setConnection(DeviceConnection connection)
	{
		this.connection = connection;
	}

	public DeviceConnection getConnection()
	{
		return connection;
	}
	
	public AndroidMSR getAndroidMSR()
	{
		if((androidMSR == null) && (connection != null))
		{
			androidMSR = new AndroidMSR(connection);
		}
		return androidMSR;
	}
}
