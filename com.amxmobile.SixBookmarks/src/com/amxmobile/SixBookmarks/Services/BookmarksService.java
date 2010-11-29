package com.amxmobile.SixBookmarks.Services;

import org.w3c.dom.Document;

import com.amxmobile.SixBookmarks.Database.Bookmark;
import com.amxmobile.SixBookmarks.Database.BookmarkCollection;
import com.amxmobile.SixBookmarks.Entities.EntityType;
import com.amxmobile.SixBookmarks.Runtime.HttpHelper;

public class BookmarksService extends ODataServiceProxy 
{
	public BookmarksService()
	{
		super("Bookmarks.svc");
	}

	public BookmarkCollection GetAll() throws Exception
	{
		EntityType et = EntityType.GetEntityType(Bookmark.class);
		
		// run...
		String url = GetServiceUrl(et);
        Document doc = HttpHelper.DownloadXml(url, GetDownloadSettings());
        
        // load...
        return (BookmarkCollection)LoadEntities(doc, et);
	}
}
