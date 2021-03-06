package com.amxmobile.SixBookmarks.Services;

import javax.microedition.global.Formatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amxmobile.SixBookmarks.Runtime.HttpHelper;
import com.amxmobile.SixBookmarks.Runtime.XmlHelper;

public class RestServiceProxy extends ServiceProxy 
{
    protected RestServiceProxy(String serviceName) 
    {
		super(serviceName);
	}

	protected Element SendRequest(RestRequestArgs args) throws Exception
    {
        // ensure that we have an authenticated API...
        this.EnsureApiAuthenticated();

        // get the URL...
        String url = this.getResolvedServiceUrl();
        url = HttpHelper.BuildUrl(url, args);

        // download...
        Document doc = HttpHelper.DownloadXml(url, GetDownloadSettings());
		try
		{
			// find the response...
			Element root = doc.getDocumentElement();
			if(root.getNodeName().compareTo("AmxResponse") != 0)
				throw new Exception(Formatter.formatMessage("The root element had an invalid name of '%s'.", new String[] { root.getNodeName() }));
			
			// get...
			boolean hasException = XmlHelper.GetElementBoolean(root, "HasException", true);
			if(!(hasException))
				return root;
			else 
			{
				// get the error...
				String error = XmlHelper.GetElementString(root, "Error", true);
				throw new Exception(Formatter.formatMessage("The server returned an error: {0}", new String[] { error }));
			}
		}
		catch(Exception ex)
		{
			throw new Exception(Formatter.formatMessage("An error occurred when processing a response returned from a REST request.\nURL: {0}\r\nInner: {1}", 
					new String[] { url, ex.toString() }));
		}
    }
}
