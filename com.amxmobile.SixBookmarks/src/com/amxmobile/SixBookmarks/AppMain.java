package com.amxmobile.SixBookmarks;

public class AppMain extends net.rim.device.api.ui.UiApplication
{
	public static void main(String[] args) 
	{
    	AppMain instance = new AppMain();
        instance.enterEventDispatcher();
	}
	
    public AppMain() 
    {
    	pushScreen(new LogonForm());
    }
}
