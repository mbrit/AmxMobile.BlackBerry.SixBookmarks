package com.amxmobile.SixBookmarks.Entities;

import com.amxmobile.SixBookmarks.Database.DatabaseHelper;
import com.amxmobile.SixBookmarks.Database.SqlStatement;

public class EntityChangeProcessor 
{
	private EntityType _entityType;
	
	public EntityChangeProcessor(EntityType et)
	{
		_entityType = et;
	}
	
	public EntityType getEntityType()
	{
		return _entityType;
	}
	
	public void SaveChanges(Entity entity) throws Exception
	{
		//  do we need to do anything?
		if(entity.getIsNew())
			this.Insert(entity);
		else if(entity.getIsModified())
			this.Update(entity);
		else if(entity.getIsDeleted())
			this.Delete(entity);
		
		// nothing to do...
	}

	private void Delete(Entity entity) throws Exception
	{
		// get the statement...
		SqlStatement sql = this.GetDeleteStatement(entity);
		
		// run the statement...
		DatabaseHelper db = new DatabaseHelper();
		db.EnsureTableExists(getEntityType());
		db.ExecuteNonQuery(sql, true);
	}
	
	private SqlStatement GetDeleteStatement(Entity entity) throws Exception
	{
		StringBuffer builder = new StringBuffer();
		SqlStatement sql = new SqlStatement();
		
		// et...
		EntityType et = getEntityType();
		
		// update...
		builder.append("DELETE FROM ");
		builder.append(et.getNativeName());
		
		// key...
		EntityField key = et.GetKeyField();
		AppendIdConstraint(builder, sql, key, entity);
		
		// return...
		sql.setCommandText(builder.toString());
		return sql;
	}
	
	private void Update(Entity entity) throws Exception
	{
		// get the statement...
		SqlStatement sql = this.GetUpdateStatement(entity);
		
		// run the statement...
		DatabaseHelper db = new DatabaseHelper();
		db.EnsureTableExists(getEntityType());
		db.ExecuteNonQuery(sql, true);
	}
	
	private SqlStatement GetUpdateStatement(Entity entity) throws Exception
	{
		StringBuffer builder = new StringBuffer();
		SqlStatement sql = new SqlStatement();
		
		// et...
		EntityType et = getEntityType();
		
		// update...
		builder.append("UPDATE ");
		builder.append(et.getNativeName());
		builder.append(" SET ");
		
		// walk...
		boolean first = true;
		EntityField key = null;
		for(int index = 0; index < et.getFields().size(); index++)
		{
			EntityField field = (EntityField)et.getFields().elementAt(index);
			if(field.getIsKey())
				key = field;
			else if(entity.getIsModified(field))
			{
				if(first)
					first = false;
				else
					builder.append(", ");
				
				// add the snippet...
				builder.append(field.getNativeName());
				builder.append("=?");
				
				// add the parameter...
				Object value = entity.GetValue(field);
				sql.AddParameterValue(value);
			}
		}
		
		// append...
		AppendIdConstraint(builder, sql, key, entity);
		
		// return...
		sql.setCommandText(builder.toString());
		return sql;
	}
	
	private void AppendIdConstraint(StringBuffer builder, SqlStatement sql, EntityField key, Entity entity) throws Exception
	{
		// constraint by ID...
		builder.append(" WHERE ");
		builder.append(key.getNativeName());
		builder.append("=?");
		sql.AddParameterValue(entity.GetValue(key));
	}
	
	private SqlStatement GetInsertStatement(Entity entity) throws Exception
	{
		StringBuffer builder = new StringBuffer();
		SqlStatement sql = new SqlStatement();
		
		// et...
		EntityType et = getEntityType();
		
		// create...
		builder.append("INSERT INTO ");
		builder.append(et.getNativeName());
		builder.append(" (");
		boolean first = true;
		for(int index = 0; index < et.getFields().size(); index++)
		{
			EntityField field = (EntityField)et.getFields().elementAt(index);
			if(entity.getIsModified(field))
			{
				if(first)
					first = false;
				else
					builder.append(", ");
				builder.append(field.getNativeName());
			}
		}
		builder.append(") VALUES (");
		first = true;
		for(int index = 0; index < et.getFields().size(); index++)
		{
			EntityField field = (EntityField)et.getFields().elementAt(index);
			if(entity.getIsModified(field))
			{
				if(first)
					first = false;
				else
					builder.append(", ");
				builder.append("?");
				
				// add in the parameter....
				sql.AddParameterValue(entity.GetValue(field));
			}
		}
		builder.append(")");
		
		// return...
		sql.setCommandText(builder.toString());
		return sql;
	}

	protected void Insert(Entity entity) throws Exception
	{
		// get the statement...
		SqlStatement sql = this.GetInsertStatement(entity);
		
		// run the statement...
		DatabaseHelper db = new DatabaseHelper();
		db.EnsureTableExists(getEntityType());
		db.ExecuteNonQuery(sql, true);
	}
}
