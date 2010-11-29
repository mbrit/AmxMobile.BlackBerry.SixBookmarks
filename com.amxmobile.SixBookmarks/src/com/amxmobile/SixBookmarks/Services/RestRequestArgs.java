package com.amxmobile.SixBookmarks.Services;

import java.util.Hashtable;

public class RestRequestArgs extends Hashtable
{
	public RestRequestArgs(String operation)
	{
		this.put("operation", operation);
	}
}
