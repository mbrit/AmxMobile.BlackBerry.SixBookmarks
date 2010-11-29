package com.amxmobile.SixBookmarks.Services;

import org.w3c.dom.Element;

import com.amxmobile.SixBookmarks.Runtime.XmlHelper;

public class ApiService extends RestServiceProxy 
{
	public ApiService()
	{
		super("apirest.aspx");
	}
	
	public LogonResponse Logon(String password) throws Exception
	{
        // create the request...
        RestRequestArgs args = new RestRequestArgs("logon");
        args.put("password", password);

        // send the request...
        Element element = SendRequest(args);
        
        // what happened?
        String asString = XmlHelper.GetElementString(element, "Result", true);
        int result = LogonResponse.ParseLogonResult(asString);
        
        // message...
        String message = XmlHelper.GetElementString(element, "Message", false);
        
        // then what?
        if(result == LogonResponse.RESULT_OK)
        {
        	String token = XmlHelper.GetElementString(element, "Token", true);
        	return new LogonResponse(result, message, token);
        }
        else
        	return new LogonResponse(result, message, "");
	}
	
	protected void EnsureApiAuthenticated()
    {
    	// no-op...
    }
}
