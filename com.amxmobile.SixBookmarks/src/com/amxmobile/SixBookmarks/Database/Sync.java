package com.amxmobile.SixBookmarks.Database;

import java.util.Vector;

import com.amxmobile.SixBookmarks.Entities.Entity;
import com.amxmobile.SixBookmarks.Entities.EntityField;
import com.amxmobile.SixBookmarks.Entities.EntityType;
import com.amxmobile.SixBookmarks.Services.BookmarksService;

public class Sync 
{
	private DatabaseHelper _database;
	private EntityType _entityType;
	
	public void DoSync() throws Exception
	{
		_database = new DatabaseHelper();
	
		// make sure we have a table...
		_entityType = EntityType.GetEntityType(Bookmark.class);
		_database.EnsureTableExists(_entityType);
		
		// push changes, then get the latest...
		PushChanges();
		GetLatest();
	}

	private EntityType getEntityType()
	{
		return _entityType;
	}
	
	private DatabaseHelper getDatabase()
	{
		return _database;
	}
	
	private void GetLatest() throws Exception
	{
		// clear the bookmarks table...
		getDatabase().ExecuteNonQuery(new SqlStatement("delete from bookmarks"), true);
		
		// get the bookmarks from the server...
		BookmarksService service = new BookmarksService();
		BookmarkCollection bookmarks = service.GetAll();
		for(int index = 0; index < bookmarks.size(); index++)
		{
			Bookmark bookmark = (Bookmark)bookmarks.elementAt(index);
			
			// the bookmarks we get from the server have server ids populated.  we need to
			// remove the server id and save locally by creating a new item...
			
			// create and copy all of the fields except the key field...
			Bookmark newBookmark = new Bookmark();
			Vector fields = getEntityType().getFields();
			for(int j = 0; j < fields.size(); j++)
			{
				EntityField field = (EntityField)fields.elementAt(index);
				if(!(field.getIsKey()) && bookmark.getIsLoaded(field))
					newBookmark.SetValue(field, bookmark.GetValue(field), Entity.SETREASON_USER);
			}
			
			// set local modified and deleted...
			newBookmark.setLocalModified(false);
			newBookmark.setLocalDeleted(false);
		
			// save...
			newBookmark.SaveChanges();
		}
	}
	
	private void PushChanges() throws Exception
	{
		BookmarkCollection updates = Bookmark.GetBookmarksForServerUpdate();
		BookmarkCollection deletes = Bookmark.GetBookmarksForServerDelete();
		if(updates.size() == 0 && deletes.size() == 0)
			return;

		// et...
		EntityType et = EntityType.GetEntityType(Bookmark.class);
		
		// get the server ones...
		BookmarksService service = new BookmarksService();
		BookmarkCollection fromServer = service.GetAll();

		// walk the locally updated items…
		for(int i = 0; i < updates.size(); i++)
		{
			Bookmark local = (Bookmark)updates.elementAt(i);
			
			// find it in our server set...
			Bookmark toUpdate = null;
			for(int j = 0; j < fromServer.size(); j++)
			{
				Bookmark server = (Bookmark)fromServer.elementAt(j);
				
				// what happened?
				if(local.getOrdinal() == server.getOrdinal())
				{
					toUpdate = server;
					break;
				}
			}
			
			// did we have one to change?
			if(toUpdate != null)
			{
				// walk the fields...
				int serverId = 0;
				Vector fields = et.getFields();
				for(int k = 0; k < fields.size(); k++)
				{
					EntityField field = (EntityField)fields.elementAt(k);
						
					// what now?
					if(!(field.getIsKey()))
						toUpdate.SetValue(field, local.GetValue(field), Entity.SETREASON_USER);
					else
						serverId = toUpdate.getBookmarkId();
				}
				
				// send that up...
				service.PushUpdate(toUpdate, serverId);
			}
			else
			{
				// we need to insert it...
				service.PushInsert(local); 
			}
		}
		
		// what about ones to delete?
		for(int i = 0; i < deletes.size(); i++)
		{
			Bookmark local = (Bookmark)deletes.elementAt(i);
				
			// find a matching ordinal on the server...
			for(int j = 0; j < fromServer.size(); j++)
			{
				Bookmark server = (Bookmark)fromServer.elementAt(j);
				
				// delete?
				if(local.getOrdinal() ==  server.getOrdinal())
					service.PushDelete(server, server.getBookmarkId());
			}
		}
	}
}
