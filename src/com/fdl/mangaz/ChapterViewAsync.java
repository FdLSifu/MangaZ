package com.fdl.mangaz;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.fdl.mangaz.MangaReaderAPI;

public class ChapterViewAsync extends AsyncTask<String, Void, Void> {
    public static String REFRESH = "refresh_chapter";
    
	private Context context;
	private ProgressDialog mDialog;
	private Gson GSON = new Gson();
	ArrayList<String> chapter_names = new ArrayList<String>();
    
    public ChapterViewAsync(Context context) {
    	this.context = context;
	}


	public void setContext(Context context) {
		this.context = context;
	}
	
    @Override
    protected void onPostExecute(Void arg) {
    	if (ChapterView.adapter != null)
    	{
    		ChapterView.adapter.addAll(MangaReaderAPI.chapterNames);
    		ChapterView.adapter.notifyDataSetChanged();
    	}
    	mDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
       	if (ChapterView.adapter != null)
       	{
    		ChapterView.adapter.clear();
    		ChapterView.adapter.notifyDataSetChanged();
       	}
       	
    	mDialog = new ProgressDialog(context);
        mDialog.setMessage("Please wait...");
        mDialog.show();
        
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
	@Override
	protected Void doInBackground(String... manga_first_chapter_url) {
		
	   	if (Manga.mra == null)
	   		Manga.mra = new MangaReaderAPI(manga_first_chapter_url[0]);
	   	else
	   		Manga.mra.setCurrentURL(manga_first_chapter_url[0]);
	   	
		SharedPreferences settings = context.getSharedPreferences(MangaReaderAPI.MANGA_LIST_SETTINGS, 0);
   		
		chapter_names = GSON.fromJson(settings.getString(manga_first_chapter_url[0],null),chapter_names.getClass());
		if( chapter_names !=null )
		{
			MangaReaderAPI.chapterNames.clear();
			MangaReaderAPI.chapterNames.addAll(chapter_names);
		}
		if((MangaReaderAPI.chapterNames == null) || (MangaReaderAPI.chapterNames.size() == 0))
		{
			Manga.mra.setCurrentURL(Manga.mra.getFirstChapterURL(ChapterView.current_manga_name));
			Manga.mra.refreshLists();
			// Write setting
			Editor editor = settings.edit();
			editor.putString(manga_first_chapter_url[0],GSON.toJson(MangaReaderAPI.chapterNames));
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
