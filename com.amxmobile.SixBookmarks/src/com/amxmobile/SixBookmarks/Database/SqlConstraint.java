package com.amxmobile.SixBookmarks.Database;

public abstract class SqlConstraint 
{
	public SqlConstraint()
	{
	}
	
	public abstract void Append(SqlStatement sql, StringBuffer builder) throws Exception;
}
