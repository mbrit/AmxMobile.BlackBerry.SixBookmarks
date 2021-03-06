package com.amxmobile.SixBookmarks.Database;

import javax.microedition.global.Formatter;

import com.amxmobile.SixBookmarks.Entities.EntityField;

public class SqlFieldConstraint extends SqlConstraint
{
	private EntityField _field;
	private Object _value;
	private int _operator;
	
	public SqlFieldConstraint(EntityField field, Object value)
	{
		this(field, SqlFilter.OPERATOR_EQUALTO, value);
	}
	
	public SqlFieldConstraint(EntityField field, int op, Object value)
	{
		_field = field;
		_value = value;
		_operator = op;
	}
	
	public EntityField getField()
	{
		return _field;
	}
	
	public Object getValue()
	{
		return _value;
	}
	
	public int getOperator()
	{
		return _operator;
	}
	
	public void Append(SqlStatement sql, StringBuffer builder) throws Exception 
	{
		// add the snippet...
		EntityField field = getField();
		builder.append(field.getNativeName());
		
		// what operator?
		if(_operator == SqlFilter.OPERATOR_EQUALTO)
			builder.append("=");
		else if(_operator == SqlFilter.OPERATOR_NOTEQUALTO)
			builder.append(" <> ");
		else
			throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { new Integer(_operator).toString() }));
		
		// add the parameter and its value value...
		builder.append("?");
		sql.AddParameterValue(getValue());
	}
}
