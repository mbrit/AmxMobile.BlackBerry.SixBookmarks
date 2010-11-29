package com.amxmobile.SixBookmarks.Services;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.global.Formatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amxmobile.SixBookmarks.Database.Bookmark;
import com.amxmobile.SixBookmarks.Entities.Entity;
import com.amxmobile.SixBookmarks.Entities.EntityField;
import com.amxmobile.SixBookmarks.Entities.EntityType;
import com.amxmobile.SixBookmarks.Runtime.HttpHelper;
import com.amxmobile.SixBookmarks.Runtime.XmlHelper;

public class ODataServiceProxy extends ServiceProxy
{
	private final String AtomNamespace = "http://www.w3.org/2005/Atom"; 
	private final String MsMetadataNamespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";
	private final String MsDataNamespace = "http://schemas.microsoft.com/ado/2007/08/dataservices";

	private final int ODATAOPERATION_INSERT = 0;
	private final int ODATAOPERATION_UPDATE = 1;
	private final int ODATAOPERATION_DELETE = 2;
	
	public ODataServiceProxy(String serviceName) 
	{
		super(serviceName);
	}
	
	public String GetServiceUrl(EntityType et)
	{
		return getResolvedServiceUrl() + "/" + et.getShortName();
	}
	
	protected Vector LoadEntities(Document doc, EntityType et) throws Exception
	{
        // parse...
        NodeList feedElements = doc.getElementsByTagNameNS(AtomNamespace, "feed");
        if(feedElements.getLength() == 0)
        	throw new Exception("A 'feed' element was not found.");
        
        // feed...
        Element feed = (Element)feedElements.item(0);
        NodeList entryElements = feed.getElementsByTagNameNS(AtomNamespace, "entry");

		// walk...
        Vector results = et.CreateCollectionInstance();
		for(int i = 0; i < entryElements.getLength(); i++)
		{
			Element entry = (Element)entryElements.item(i);

			// get the content item...
			Element content = XmlHelper.GetElement(entry, AtomNamespace, "content", false);
			if(content == null)
				throw new Exception(Formatter.formatMessage("A content element not found on item '{0}'.", new String[] { new Integer(i).toString() }));
			
			// then get the properties element...
			Element properties = XmlHelper.GetElement(content, MsMetadataNamespace, "properties", false);
			if(properties == null)
				throw new Exception(Formatter.formatMessage("A properties element not found on item '{0}'.", new String[] { new Integer(i).toString() }));

			// then get the fields...
			NodeList fields = properties.getElementsByTagNameNS(MsDataNamespace, "*");
			Hashtable values = new Hashtable(); 
			for(int j = 0; j < fields.getLength(); j++)
			{
				Element field = (Element)fields.item(j);
				
				// value...
				Object value = GetValue(field);
				
				// is it a field?
				if(et.IsField(field.getLocalName()))
					values.put(field.getLocalName(), value);
			}
			
			// create...
			Bookmark bookmark = new Bookmark(values);
			results.addElement(bookmark);
		}
		// return...
		return results;
	}

	private Object GetValue(Element field) throws Exception 
	{
		// fields are provided with a data element, like this....
		// <d:BookmarkId m:type="Edm.Int32">1002</d:BookmarkId>
		String asString = field.getAttributeNS(MsMetadataNamespace, "type");
		
		// nothing?
		if(asString == null || asString.length() == 0)
			return XmlHelper.GetStringValue(field);
		else if(asString.compareTo("Edm.Int32") == 0)
			return new Integer(XmlHelper.GetInt32Value(field));
		else
			throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { asString }));
	}
	
	private void ExecuteODataOperation(int opType, String url, String xml) throws Exception
	{
		// make sure we're authenticated...
		EnsureApiAuthenticated();

//		// show...
//		HttpRequestBase op = null;
//		if(opType == ODATAOPERATION_UPDATE)
//			op = new HttpMerge(url);
//		else if(opType == ODATAOPERATION_INSERT)
//			op = new HttpPost(url);
//		else if(opType == ODATAOPERATION_DELETE)
//			op = new HttpDelete(url);
//		else
//			throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { new Integer(opType).toString() }));
//		
//		// add our headers...
//		Hashtable extraHeaders = GetDownloadSettings().getExtraHeaders();
//		Enumeration keys = extraHeaders.keys();
//		while(keys.hasMoreElements())
//		{
//			String name = (String)keys.nextElement();
//			op.addHeader(name, extraHeaders.get(name));
//		}
//		
//		// create an entity...
//		if(xml != null)
//			((HttpEntityEnclosingRequestBase)op).setEntity(new ODataEntity(xml));
//		
//		// request...
//		HttpClient client = new DefaultHttpClient();
//		HttpResponse response = client.execute(op);
//
//		// what happened...  200 is OK, but 204 also is (which means no content)...
//		int code = response.getStatusLine().getStatusCode(); 
//		if(code != 200 && code != 204)
//		{
//			// get...
//			String error = HttpHelper.GetHttpEntityContent(response.getEntity());
//			
//			// throw...
//			throw new Exception(String.format("Download of '%s' via operation '%s' returned HTTP '%d' (%s).\nResponse: %s\nXML: %s", 
//					url, opType, code, response.getStatusLine().getReasonPhrase(), error, xml));
//		}
		
		throw new Exception("Not implemented.");
	}
		
	public void PushInsert(Entity entity) throws Exception 
	{
		// an insert is an update but with a different url...
		PushUpdate(entity, 0);
	}

	public void PushDelete(Entity entity, int serverId) throws Exception
	{
		// get the entity URL...
		String url = GetEntityUrlForPush(entity, serverId);	
		ExecuteODataOperation(ODATAOPERATION_DELETE, url, null);
	}

	public void PushUpdate(Entity entity, int serverId) throws Exception 
	{
//		// update...
//		XmlSerializer xml = Xml.newSerializer();
//		StringWriter writer = new StringWriter();
//		xml.setOutput(writer);
//		
//		// start...
//		xml.startDocument("UTF-8", true);
//		xml.setPrefix("", AtomNamespace);
//		xml.setPrefix("m", MsMetadataNamespace);
//		xml.setPrefix("d", MsDataNamespace);
//		
//		// start entry and content and properties...
//		xml.startTag(AtomNamespace, "entry");
//		xml.startTag(AtomNamespace, "content");
//		xml.attribute("", "type", "application/xml");
//		xml.startTag(MsMetadataNamespace, "properties");
//		
//		// fields...
//		EntityType et = entity.getEntityType();
//		for(int index = 0; index < et.getFields().size(); index++)
//		{
//			EntityField field = (EntityField)et.getFields().elementAt(index);
//			if(!(field.getIsKey()) && field.getIsOnServer())
//			{
//				xml.startTag(MsDataNamespace, field.getName());
//				xml.text(entity.GetValue(field).toString());
//				xml.endTag(MsDataNamespace, field.getName());
//			}
//		}
//		
//		// end content and entry...
//		xml.endTag(MsMetadataNamespace, "properties");
//		xml.endTag(AtomNamespace, "content");
//		xml.endTag(AtomNamespace, "entry");
//		
//		// end...
//		xml.endDocument();
//		
//		// run...
//		String url = null; 
//		int op = ODATAOPERATION_UPDATE;
//		String xmlAsString = writer.toString();
//		if(serverId != 0)
//			url = GetEntityUrlForPush(entity, serverId);
//		else
//		{
//			url = this.GetServiceUrl(et);
//			op = ODATAOPERATION_INSERT;
//		}
//		
//		// run...
//		ExecuteODataOperation(op, url, xmlAsString);
		
		throw new Exception("Not implemented.");
	}

	private String GetEntityUrlForPush(Entity entity, int serverId)
	{
		return Formatter.formatMessage("{0}({1})", new String[] { GetServiceUrl(entity.getEntityType()), 
				new Integer(serverId).toString() });
	}
}
