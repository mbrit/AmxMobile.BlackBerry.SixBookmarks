package com.amxmobile.SixBookmarks.Entities;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.global.Formatter;

public abstract class Entity 
{
	private EntityType _entityType;
	
	private Object[] _values;
	private int[] _flags;
	
	private static final int FIELDFLAGS_NOTLOADED = 0;
	private static final int FIELDFLAGS_LOADED = 1;
	private static final int FIELDFLAGS_MODIFIED = 2;
	
	public static final int SETREASON_LOAD = 0;
	public static final int SETREASON_USER = 1;
	
	protected Entity() throws Exception
	{
		_entityType = EntityType.GetEntityType(this.getClass());
		
		// slots...
		int count = _entityType.getFields().size();
		_values = new Object[count];
		_flags = new int[count];
	}
	
	protected Entity(Hashtable values) throws Exception
	{
		// make sure the original initialization code runs – v. important…
		this();
		
		// set...
		Enumeration keys = values.keys();
		while(keys.hasMoreElements())
		{
			String key = (String)keys.nextElement();
			EntityField field = getEntityType().GetField(key, true);
			SetValue(field, values.get(key), SETREASON_LOAD);
		}
	}

	public EntityType getEntityType()
	{
		return _entityType;
	}
	
	protected void SetValue(String name, Object value, int setReason) throws Exception
	{
		EntityField field = getEntityType().GetField(name, true);
		SetValue(field, value, setReason);
	}
	
	public void SetValue(EntityField field, Object value, int setReason) throws Exception
	{
		int ordinal = field.getOrdinal();
		SetValue(ordinal, value, setReason);
	}
	
	private void SetValue(int ordinal, Object value, int setReason) throws Exception
	{
		_values[ordinal] = value;
		
		// if...
		SetFlag(ordinal, FIELDFLAGS_LOADED);
		if(setReason == SETREASON_USER)
			SetFlag(ordinal, FIELDFLAGS_MODIFIED);
	}
	
	private void SetFlag(int ordinal, int flag)
	{
		_flags[ordinal] = _flags[ordinal] | flag;
	} 
	
	public boolean getIsLoaded(EntityField field)
	{
		return getIsLoaded(field.getOrdinal()); 
	}
	
	private boolean getIsLoaded(int index)
	{
		return IsFlagSet(index, FIELDFLAGS_LOADED);
	}
	
	public boolean getIsModified(EntityField field)
	{
		return getIsModified(field.getOrdinal()); 
	}
	
	private boolean getIsModified(int index)
	{
		return IsFlagSet(index, FIELDFLAGS_MODIFIED); 
	}
	
	private boolean IsFlagSet(int index, int flag)
	{
		if((_flags[index] & flag) == flag)
			return true;
		else
			return false;
	}
	
	public boolean getIsNew() throws Exception
	{
		EntityField key = getEntityType().GetKeyField();
		
		// state...
		if(!(getIsLoaded(key)) && !(getIsModified(key))) 
			return true;
		else
			return false;
	}
	
	public boolean getIsDeleted()
	{
		return false;
	}
	
	public boolean getIsModified()
	{
		for(int index = 0; index < _flags.length; index++)
		{
			if(getIsModified(index))
				return true;
		}
		
		// nope...
		return false;
	} 
	
	public Object GetValue(String name) throws Exception
	{
		EntityField field = getEntityType().GetField(name, true);
		return GetValue(field.getOrdinal());
	}

	public Object GetValue(EntityField field) throws Exception
	{
		return GetValue(field.getOrdinal());
	}
	
	private Object GetValue(int index) throws Exception
	{
		// do we need to demand load?
		if(!(getIsLoaded(index)) && !(getIsNew()))
			throw new Exception("Demand loading is not implemented.");
		
		// return...
		return _values[index]; 
	}
	
	public String GetStringValue(String name) throws Exception
	{
		EntityField field = getEntityType().GetField(name, true);
		return GetStringValue(field);
	}

	public String GetStringValue(EntityField field) throws Exception
	{
		Object value = GetValue(field);
		if(value != null)
			return value.toString();
		else
			return null;
	}
	
	public int GetInt32Value(String name) throws Exception
	{
		EntityField field = getEntityType().GetField(name, true);
		return GetInt32Value(field);
	}

	public int GetInt32Value(EntityField field) throws Exception
	{
		Object value = GetValue(field);
		if(value == null)
			return 0;
		else if(value instanceof Integer)
			return ((Integer)value).intValue();
		else
			throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { value.getClass().getName() }));
	}

	public boolean GetBooleanValue(String name) throws Exception
	{
		EntityField field = getEntityType().GetField(name, true);
		return GetBooleanValue(field);
	}

	public boolean GetBooleanValue(EntityField field) throws Exception
	{
		Object value = GetValue(field);
		if(value == null)
			return false;
		else if(value instanceof Integer)
		{
			int asInt = ((Integer)value).intValue();
			if(asInt == 0)
				return false;
			else
				return true;
		}
		else
			throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { value.getClass().getName() }));
	}
	
	public void SaveChanges() throws Exception
	{
		EntityChangeProcessor processor = new EntityChangeProcessor(this.getEntityType());
		processor.SaveChanges(this);
	}
}
