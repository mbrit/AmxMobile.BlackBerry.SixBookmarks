package com.amxmobile.SixBookmarks.Services;

import java.util.Hashtable;

public class DownloadSettings 
{
	private Hashtable _extraHeaders = new Hashtable(); 
	
	public DownloadSettings()
	{
	}
	
	public Hashtable getExtraHeaders()
	{
		return _extraHeaders;
	}
	
	public void AddHeader(String name, String value) 
	{
		if(value == null)
			value = "";
		
		getExtraHeaders().put(name, value);
	}
}
