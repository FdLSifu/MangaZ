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
    public static boolean isRunning = false;
    public static boolean wasCalled = false;
    public static String REFRESH = "refresh_chapter";
    
	private Context context;
	private ProgressDialog mDialog;
	private Gson GSON = new Gson();
    
    public ChapterViewAsync(Context context) {
		// TODO Auto-generated constructor stub
    	this.context = context;
	}

    @Override
    protected void onPostExecute(Void arg) {
    	isRunning = false;
    	wasCalled = true;

    	if (ChapterView.adapter != null)
    	{
    		ChapterView.adapter.clear();
    		ArrayList<String> chap_names = Manga.mra.getChapterNames();
    		for (int i = 0; i < chap_names.size(); i ++)
    			ChapterView.adapter.add(chap_names.get(i));
    		ChapterView.adapter.notifyDataSetChanged();
    	}
    
    	mDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
    	isRunning = true;
    	
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
   		
		
		ArrayList<String> chapter_names = new ArrayList<String>();
		chapter_names = GSON.fromJson(settings.getString(manga_first_chapter_url[0],null),chapter_names.getClass());
		if( chapter_names !=null )
		{
			Manga.mra.getChapterNames().clear();
			Manga.mra.getChapterNames().addAll(chapter_names);
		}
		if(Manga.mra.getChapterNames().size() == 0)
		{
			Manga.mra.refreshLists();
			// Write setting
			Editor editor = settings.edit();
			editor.putString(manga_first_chapter_url[0],GSON.toJson(Manga.mra.getChapterNames()));
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
