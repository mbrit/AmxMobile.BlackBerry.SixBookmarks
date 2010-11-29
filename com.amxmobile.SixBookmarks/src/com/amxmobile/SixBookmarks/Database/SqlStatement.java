package com.amxmobile.SixBookmarks.Database;

import java.util.Vector;

public class SqlStatement implements ISqlStatementSource 
{
	private String _commandText;
	private Vector _parameterValues = new Vector();
	
	public SqlStatement()
	{
	}
	
	public SqlStatement(String commandText)
	{
		_commandText = commandText;
	}
	
	public String getCommandText()
	{
		return _commandText;
	}
	
	public void setCommandText(String commandText)
	{
		_commandText = commandText;
	}
	
	public SqlStatement GetSqlStatement()
	{
		return this;
	}
	
	public void AddParameterValue(Object value)
	{
		_parameterValues.addElement(value);
	}
	
	public Object[] getParameterValues()
	{
		Object[] results = new Object[_parameterValues.size()];
		for(int index = 0; index < _parameterValues.size(); index++)
			results[index] = _parameterValues.elementAt(index);
		
		// return...
		return results;
	}
}
