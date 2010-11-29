package com.amxmobile.SixBookmarks.Runtime;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.w3c.dom.Document;

import com.amxmobile.SixBookmarks.Services.DownloadSettings;

public class HttpHelper 
{
	public static String Download(String url, DownloadSettings settings) throws Exception
	{
		if(settings == null)
			settings  = new DownloadSettings();
		
    	HttpConnection conn = null;
    	InputStream stream = null;
        try 
        {
            conn = (HttpConnection)Connector.open(url);

            // headers...
            Hashtable headers = settings.getExtraHeaders();
            Enumeration keys = headers.keys();
            while(keys.hasMoreElements())
            {
            	String key = (String)keys.nextElement();
            	conn.setRequestProperty(key, (String)headers.get(key));
            }
            
            // open...
            stream = conn.openInputStream();

            // walk...
            final int bufLen = 10240;
            byte[] buf = new byte[bufLen];
            StringBuffer raw = new StringBuffer();
            while(true)
            {
            	int len = stream.read(buf, 0, bufLen);
            	if(len == -1)
            		break;
            	
            	// append...
                raw.append(new String(buf, 0, len));
            }

            // return...
            String html = raw.toString();
            return html;
        }
        finally
        {
        	if(stream != null)
        		stream.close();
        	if(conn != null)
        		conn.close();
        } 
	}
	
	public static Document DownloadXml(String url, DownloadSettings settings) throws Exception 
	{
		// get the plain content...
		String xml = Download(url, settings);

		// turn that into some XML...
		Document doc = XmlHelper.LoadXml(xml);
		return doc;
	}

	public static String BuildQueryString(Hashtable values)
	{
		StringBuffer builder = new StringBuffer();
		Enumeration keys = values.keys();
		while(keys.hasMoreElements())
		{
			String key = (String)keys.nextElement();
			
			// ok...
			if(builder.length() > 0)
				builder.append("&");
			builder.append(key);
			builder.append("=");
			builder.append(values.get(key));
		}
		
		// return...
		String qs = builder.toString();
		return qs;
	}

	public static String BuildUrl(String url, Hashtable values)
	{
		int index = url.indexOf("?");
		if(index != -1) 
			url = url.substring(0, index);
		
		// add...
		url = url + "?" + BuildQueryString(values);
		return url;
	}
}
