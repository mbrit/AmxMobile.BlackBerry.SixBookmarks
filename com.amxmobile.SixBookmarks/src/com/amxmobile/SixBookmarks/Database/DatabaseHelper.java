package com.amxmobile.SixBookmarks.Database;

import java.util.Vector;

import javax.microedition.global.Formatter;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.Row;
import net.rim.device.api.database.Statement;

import com.amxmobile.SixBookmarks.Entities.Entity;
import com.amxmobile.SixBookmarks.Entities.EntityField;
import com.amxmobile.SixBookmarks.Entities.EntityType;

public class DatabaseHelper
{
	private static Vector _loadMap = new Vector();
	
	private static String DatabaseName = null;
	
	public DatabaseHelper()
	{
	}

	public static void setUser(String username)
	{
		DatabaseName = "SixBookmarks-" + username;
	}

	private SqlStatement GetCreateScript(EntityType et) throws Exception
	{
		StringBuffer builder = new StringBuffer();
		builder.append("CREATE TABLE IF NOT EXISTS ");
		builder.append(et.getNativeName());	// now the columns...
		builder.append(" (");
		Vector fields = et.getFields(); 
		for(int index = 0; index < fields.size(); index++)
		{
			if(index > 0)
				builder.append(", ");
			
			// defer...
			AppendCreateSnippet(builder, (EntityField)fields.elementAt(index)); 
		}
		builder.append(")");
		
		// return...
		return new SqlStatement(builder.toString());
	}
	
	private void AppendCreateSnippet(StringBuffer builder, EntityField field) throws Exception
	{
		builder.append(field.getNativeName());
		builder.append(" ");
		
		// switch...
		int type = field.getDataType();
		if(type == EntityField.DATATYPE_STRING)
		{
			builder.append("varchar(");
			builder.append(field.getSize());
			builder.append(")");
		}
		else if(type == EntityField.DATATYPE_INT32)
		{
			builder.append("integer");
			
			// key?
			if(field.getIsKey())
				builder.append(" primary key autoincrement");
		}
		else
			throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { new Integer(field.getDataType()).toString() }));
	}

	public void EnsureTableExists(EntityType et) throws Exception
	{
		// have we already called it?
		String name = et.getName();
		if(_loadMap.contains(name))
			return;
		
		// create...
		SqlStatement sql = GetCreateScript(et);
		ExecuteNonQuery(sql, true);
		
		// add...
		_loadMap.addElement(name);
	}
	
	private Database GetDatabase() throws Exception
	{
		if(DatabaseFactory.exists(DatabaseName))
			return DatabaseFactory.open(DatabaseName);
		else
			return DatabaseFactory.create(DatabaseName);
	}
	
	public void ExecuteNonQuery(ISqlStatementSource sql, boolean writable) throws Exception
	{
		// open...
		Database db = GetDatabase();
		Statement statement = null;
		try
		{
			statement = GetRealSqlStatement(db, sql);
			statement.execute();
		}
		finally
		{
			if(statement != null)
				statement.close();
			if(db != null)
				db.close();
		}
	}

	private Statement GetRealSqlStatement(Database db, ISqlStatementSource sql) throws Exception
	{
		SqlStatement wrapped = sql.GetSqlStatement();
		return GetRealSqlStatement(db, wrapped);
	}	
	
	private Statement GetRealSqlStatement(Database db, SqlStatement sql) throws Exception
	{
		Statement real = db.createStatement(sql.getCommandText());
		
		// prep...
		real.prepare();
		
		// populate params...
		Object[] params = sql.getParameterValues();
		for(int index = 0; index < params.length; index++)
		{
			Object param = params[index];
			if(param instanceof String)
				real.bind(index + 1, (String)param);
			else if(param instanceof Integer)
				real.bind(index + 1, ((Integer)param).intValue());			
			else if(param instanceof Boolean)
				real.bind(index + 1, ((Boolean)param).booleanValue());			
			else
				throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", new String[] { param.getClass().getName() }));
		}
	
		// return..
		return real;
	}

	public Vector ExecuteEntityCollection(ISqlStatementSource sql, EntityType et) throws Exception
	{
		EnsureTableExists(et);
		
		// get...
		Database db = GetDatabase();
		Statement statement = null;
		Cursor cursor = null;
		try
		{
			// set...
			statement = GetRealSqlStatement(db, sql);
			cursor = statement.getCursor();
			
			// execute a cursor...
			Vector results = et.CreateCollectionInstance();
			
			// cursor...
			while(cursor.next())
			{
				// load...
				Entity entity = LoadEntity(cursor, et);
				results.addElement(entity);
			}
			
			// return...
			return results;
		}
		finally
		{
			if(cursor != null)
				cursor.close();
			if(statement != null)
				statement.close();
			if(db != null)
				db.close();
		}
	}
	
	private Entity LoadEntity(Cursor c, EntityType et) throws Exception
	{
		// create a new instance...
		Entity entity = et.CreateInstance();
		
		// row...
		Row row = c.getRow();
		
		// load data...
		for(int index = 0; index < et.getFields().size(); index++)
		{
			EntityField field = (EntityField)et.getFields().elementAt(index);
			int type = field.getDataType();
			if(type == EntityField.DATATYPE_STRING)
				entity.SetValue(field, row.getString(index), Entity.SETREASON_LOAD);
			else if(type == EntityField.DATATYPE_INT32)
				entity.SetValue(field, new Integer(row.getInteger(index)), Entity.SETREASON_LOAD);
			else
			{
				throw new Exception(Formatter.formatMessage("Cannot handle '{0}'.", 
						new String[] { new Integer(field.getDataType()).toString() }));
			}
		}
		
		// return...
		return entity;
	}
}
