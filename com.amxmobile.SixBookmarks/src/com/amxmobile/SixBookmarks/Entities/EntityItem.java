package com.amxmobile.SixBookmarks.Entities;

public abstract class EntityItem 
{
	private String _name;
	private String _lowerName;
	private String _nativeName;
	
	protected EntityItem(String name, String nativeName)
	{
		_name = name;
		_lowerName = name.toLowerCase();
		
		if(nativeName != null)
			_nativeName = nativeName;
		else
			_nativeName = name;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getLowerName()
	{
		return _lowerName;
	}

	public String getNativeName()
	{
		return _nativeName;
	}
}
