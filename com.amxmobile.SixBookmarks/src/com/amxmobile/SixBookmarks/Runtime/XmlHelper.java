package com.amxmobile.SixBookmarks.Runtime;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.microedition.global.Formatter;

import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHelper 
{
	public final static int DATATYPE_STRING = 0;
	public final static int DATATYPE_INT32 = 1;
	public final static int DATATYPE_BOOLEAN = 2;	
	
	public static Document LoadXml(String xml) throws Exception
	{
		// create...
		ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
		try
		{
			return LoadXml(stream);
		}
		finally
		{
			if(stream != null)
				stream.close();
		}
	}
	
	private static Document LoadXml(InputStream stream) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		// builder...
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(stream);
		
		// return...
		return doc;
	}

	public static String GetElementString(Element element, String name, boolean throwIfNotFound) throws Exception
	{
		return (String)GetElementValue(element, name, DATATYPE_STRING, throwIfNotFound);
	}
	
	public static boolean GetElementBoolean(Element element, String name, boolean throwIfNotFound) throws Exception
	{
		return ((Boolean)GetElementValue(element, name, DATATYPE_BOOLEAN, throwIfNotFound)).booleanValue();
	}
	
	public static int GetElementInt32(Element element, String name, boolean throwIfNotFound) throws Exception
	{
		return ((Integer)GetElementValue(element, name, DATATYPE_INT32, throwIfNotFound)).intValue();
	}
	
	private static Object GetElementValue(Element element, String name, int dataType, boolean throwIfNotFound) throws Exception
	{
		// find it...
		NodeList nodes = element.getElementsByTagName(name);
		if(nodes.getLength() == 1)
		{
			if(dataType == DATATYPE_STRING)
				return GetStringValue(nodes.item(0));
			else if(dataType == DATATYPE_BOOLEAN)
				return new Boolean(GetBooleanValue(nodes.item(0)));
			else if(dataType == DATATYPE_INT32)
				return new Integer(GetInt32Value(nodes.item(0)));
			else
				throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { new Integer(dataType).toString() }));
		}
		else if(nodes.getLength() == 0)
		{
			if(throwIfNotFound)
			{
				throw new Exception(Formatter.formatMessage("An element with name '{0}' was not found within an element with name '{1}'.", 
						new String[] { name, element.getNodeName() }));
			}
			else
				return null;
		}
		else
		{
			throw new Exception(Formatter.formatMessage("Too many ({0}) child elements were found.", 
					new String[] { new Integer(nodes.getLength()).toString() } ));
		}
	}
	
	public static String GetStringValue(Node item) throws Exception
	{
		if(item instanceof Element)
		{
			Node node = item.getFirstChild();
			if(node != null)
				return node.getNodeValue();
			else
				return "";
		}
		else
			throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { item.getClass().getName() }));
	}

	public static int GetInt32Value(Node item) throws Exception
	{
		String asString = GetStringValue(item);
		return Integer.parseInt(asString);
	}

	public static boolean GetBooleanValue(Node item) throws Exception
	{
		String asString = GetStringValue(item);
		if(asString.compareTo("0") == 0 || asString.toLowerCase().compareTo("false") == 0)
			return false;
		else if(asString.compareTo("1") == 0 || asString.toLowerCase().compareTo("true") == 0)
			return true;
		else
		{
			throw new Exception(Formatter.formatMessage("The value '{0}' could not be recognised as valid Boolean value.", 
					new String[] { asString }));
		}
	}
	
	public static Element GetElement(Element root, String namespaceUri, String name, boolean throwIfNotFound) throws Exception
	{
		NodeList nodes = root.getElementsByTagNameNS(namespaceUri, name);
		if(nodes.getLength() > 0)
			return (Element)nodes.item(0);
		else
		{
			if(throwIfNotFound)
			{
				throw new Exception(Formatter.formatMessage("A node with name '{0}' in namespace '{1}' was not found.", 
						new String[] { name, namespaceUri }));
			}
			else
				return null;
		}
	}
}