package com.pos.app.assist;

import java.util.Iterator;
import java.util.Vector;

public class AddressRepo
{
	private String bluetoothAddress;
	private Vector<String> ipAddressVector;
	private static AddressRepo addressRepo;
	
	public static AddressRepo getInstance()
	{
		if(addressRepo == null)
			addressRepo = new AddressRepo();
		return addressRepo;
	}
	private AddressRepo()
	{
		ipAddressVector = new Vector<String>();
	}
	// Bluetooth Address
	public String getBluetoothAddress()
	{
		return bluetoothAddress;
	}
	public void setBluetoothAddress(String bluetoothAddress)
	{
		this.bluetoothAddress = bluetoothAddress;
	}
	// IP Address
	public Iterator<String> getIterator()
	{
		return ipAddressVector.iterator();
	}
	public void addIP(String ipAddress)
	{
		ipAddressVector.addElement(ipAddress);
	}
	public void removeIP(String ipAddress)
	{
		ipAddressVector.remove(ipAddress);
	}
	public void removeAllIP()
	{
		ipAddressVector.removeAllElements();
	}
	public int getIPCount()
	{
		return ipAddressVector.size();
	}
}
