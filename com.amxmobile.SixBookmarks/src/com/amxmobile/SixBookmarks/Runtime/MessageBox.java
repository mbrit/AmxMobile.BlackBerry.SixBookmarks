package com.amxmobile.SixBookmarks.Runtime;

import net.rim.device.api.ui.component.Dialog;

public class MessageBox
{
	public static void Show(Exception ex)
	{
		String message = ex.toString();
		Show(message);
	}

	public static void Show(String message)
	{
		Dialog.alert(message);
	}
}
