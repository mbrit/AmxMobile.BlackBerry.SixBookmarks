package com.amxmobile.SixBookmarks.Entities;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.global.Formatter;

public class EntityType extends EntityItem
{
	private static Hashtable _entityTypes = new Hashtable();
	
	private Vector _fields = new Vector();
	private Class _instanceType = null;
	private Class _collectionType = null;
	
	public EntityType(Class instanceType, Class collectionType, String nativeName)
	{
		super(instanceType.getName(), nativeName);
		_instanceType = instanceType;
		_collectionType = collectionType;		
	}

	public Vector getFields()
	{
		return _fields;
	}
	
	public EntityField AddField(String name, String nativeName, int type, int size)
	{
		EntityField field = new EntityField(name, nativeName, type, size, _fields.size());
		_fields.addElement(field);
		
		// return...
		return field;
	}
	
	public Class getInstanceType()
	{
		return _instanceType;
	}
	
	public Class getCollectionType()
	{
		return _collectionType;
	}

	public Entity CreateInstance() throws Exception
	{
		return (Entity)getInstanceType().newInstance();
	}

	public Vector CreateCollectionInstance() throws Exception
	{
		return (Vector)getCollectionType().newInstance();
	}
	
	public EntityField GetField(String name, boolean throwIfNotFound) throws Exception	
	{
		name = name.toLowerCase();
		for(int index = 0; index < _fields.size(); index++)
		{
			EntityField field = (EntityField)_fields.elementAt(index);
			if(field.getLowerName().compareTo(name) == 0)
				return field;
		}
		
		// throw...
		if(throwIfNotFound)
			throw new Exception(Formatter.formatMessage("Failed to find a field with name '{0}'.", new String[] { name }));
		else
			return null;
	}
	
	public EntityField GetKeyField() throws Exception
	{
		for(int index = 0; index < _fields.size(); index++)
		{
			EntityField field = (EntityField)_fields.elementAt(index);
			if(field.getIsKey())
				return field;
		}
		
		// throw...
		throw new Exception("Failed to find a key field.");
	}
	
	public boolean IsField(String name) throws Exception
	{
		EntityField field = this.GetField(name, false);
		if(field != null)
			return true;
		else
			return false;
	}
	
	public static void RegisterEntityType(EntityType entityType)
	{
		_entityTypes.put(entityType.getInstanceType().getName(), entityType);
	}
	
	public static EntityType GetEntityType(Class type) throws Exception
	{
		String name = type.getName();
		Enumeration keys = _entityTypes.keys();
		while(keys.hasMoreElements())
		{
			String key = (String)keys.nextElement();
			if(key.compareTo(name) == 0)
				return (EntityType)_entityTypes.get(key);
		}
		
		// throw...
		throw new Exception(Formatter.formatMessage("Failed to get entity type for '{0}'.", new String[] { type.getName() }));
	}
	
	public String getShortName()
	{
		String name = getName();
		int index = name.lastIndexOf((char)'.');
		return name.substring(index + 1);
	}
}
