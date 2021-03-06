package com.amxmobile.SixBookmarks.Services;

import org.w3c.dom.Element;

import com.amxmobile.SixBookmarks.Runtime.XmlHelper;

public class UsersService extends RestServiceProxy 
{
	public UsersService()
	{
		super("usersrest.aspx");
	}
	
    public LogonResponse Logon(String username, String password) throws Exception
    {
        // create the request...
        RestRequestArgs args = new RestRequestArgs("logon");

        // add the username and password...
        args.put("username", username);
        args.put("password", password);

        // send the request...
        Element element = SendRequest(args);
        
        String asString = XmlHelper.GetElementString(element, "Result", true);
        int result = LogonResponse.ParseLogonResult(asString);
        
        // message...
        String message = XmlHelper.GetElementString(element, "Message", false);
        
        // return...
    	return new LogonResponse(result, message, "");
    }
}
