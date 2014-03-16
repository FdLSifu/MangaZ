package com.fdl.mangaz.chapterview;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.fdl.mangaz.utils.Constants;
import com.fdl.mangaz.utils.Manga;
import com.fdl.mangaz.utils.MangaReaderAPI;
import com.fdl.mangaz.utils.StringUtil;

public class ChapterViewAsync extends AsyncTask<String, Void, Void> {
    public static String REFRESH = "refresh_chapter";
    
	private Context context;
	private ProgressDialog mDialog;
	private Gson GSON = new Gson();
    
    public ChapterViewAsync(Context context) {
    	this.context = context;
	}


	public void setContext(Context context) {
		this.context = context;
	}
	
    @Override
    protected void onPostExecute(Void arg) {
    	if (ChapterViewActivity.adapter != null)
    	{
    		ChapterViewActivity.adapter.clear();
    		ChapterViewActivity.adapter.addAll(Manga.chapter_names);
    		MangaReaderAPI.nbChapters = Manga.chapter_names.size();
    		MangaReaderAPI.chapterNames.clear();
    		MangaReaderAPI.chapterNames.addAll(Manga.chapter_names);
    		ChapterViewActivity.adapter.sort(null);
    		ChapterViewActivity.adapter.notifyDataSetChanged();

    	}
    	mDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
    	super.onCancelled();
    	if (ChapterViewActivity.adapter != null)
    	{
    		ChapterViewActivity.adapter.clear();
    		ChapterViewActivity.adapter.addAll(Manga.chapter_names);
    		MangaReaderAPI.nbChapters = Manga.chapter_names.size();
    		MangaReaderAPI.chapterNames.clear();
    		MangaReaderAPI.chapterNames.addAll(Manga.chapter_names);
    		ChapterViewActivity.adapter.sort(null);
    		ChapterViewActivity.adapter.notifyDataSetChanged();
    	}
    }
    
    @Override
    protected void onPreExecute() {
       	if (ChapterViewActivity.adapter != null)
       	{
    		ChapterViewActivity.adapter.clear();
    		ChapterViewActivity.adapter.notifyDataSetChanged();
       	}
       	
    	mDialog = new ProgressDialog(context);
        mDialog.setMessage("Please wait...");
        mDialog.show();
        
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
    
	@SuppressWarnings("unchecked")
	@Override
	protected Void doInBackground(String... manga_first_chapter_url) {
		
	   	if (Manga.mra == null)
	   		Manga.mra = new MangaReaderAPI(manga_first_chapter_url[0]);
	   	else
	   		Manga.mra.setCurrentURL(manga_first_chapter_url[0]);
	   	
		SharedPreferences settings = context.getSharedPreferences(StringUtil.sanitizeFilename(ChapterViewActivity.current_manga_name), 0);
   		
		Manga.chapter_names = GSON.fromJson(settings.getString(Constants.CHAPTER_LIST,null),Manga.chapter_names.getClass());
		if( Manga.chapter_names !=null )
		{
			MangaReaderAPI.chapterNames.clear();
			MangaReaderAPI.chapterNames.addAll(Manga.chapter_names);
		}
		if((MangaReaderAPI.chapterNames == null) || (MangaReaderAPI.chapterNames.size() == 0))
		{
			//Manga.mra.setCurrentURL(Manga.mra.getFirstChapterURL(ChapterViewActivity.current_manga_name));
			Manga.mra.refreshLists();

			Manga.chapter_names = new ArrayList<String>();
			Manga.chapter_names.addAll(MangaReaderAPI.chapterNames);
			Manga.chapter_url.addAll(Manga.mra.getChapterList());
			// Write setting
			Editor editor = settings.edit();
			editor.putString(Constants.CHAPTER_LIST,GSON.toJson(MangaReaderAPI.chapterNames));
			editor.putString(Constants.CHAPTER_URL,GSON.toJson(Manga.chapter_url));
			editor.commit();
		}
		
		if (manga_first_chapter_url.length > 1)
		{
			if(manga_first_chapter_url[1].equals(REFRESH))
				Manga.mra.refreshLists();
		}
		
    	return null;
    }

}
