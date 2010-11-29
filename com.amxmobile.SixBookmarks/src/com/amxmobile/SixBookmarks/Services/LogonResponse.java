package com.amxmobile.SixBookmarks.Services;

import javax.microedition.global.Formatter;

public class LogonResponse 
{
	public final static int RESULT_OK = 0;
	public final static int RESULT_INVALID_USERNAME = 1;
	public final static int RESULT_INVALID_PASSWORD = 2;
	public final static int RESULT_USER_INACTIVE = 3;
	
	private String _token;
	private String _message;
	private int _result;
	
	public LogonResponse(int result, String message, String token)
	{
		_result = result;
		_message = message;
		_token = token;
	}
	
	public String getMessage()
	{		
		return _message;
	}
	
	public String getToken()
	{		
		return _token;
	}
	
	public int getResult()
	{
		return _result;
	}
	
	public static int ParseLogonResult(String asString) throws Exception
	{
		if(asString.compareTo("LogonOk") == 0)
			return RESULT_OK;
		else if(asString.compareTo("InvalidUsername") == 0)
			return RESULT_INVALID_USERNAME;
		else if(asString.compareTo("InvalidPassword") == 0)
			return RESULT_INVALID_PASSWORD;
		else if(asString.compareTo("AccountInactive") == 0)
			return RESULT_USER_INACTIVE;
		else
			throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { asString }));
	}
}
