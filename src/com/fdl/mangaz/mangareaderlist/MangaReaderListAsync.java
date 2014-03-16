package com.fdl.mangaz.mangareaderlist;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.fdl.mangaz.utils.Constants;
import com.fdl.mangaz.utils.Library;
import com.fdl.mangaz.utils.Manga;
import com.fdl.mangaz.utils.MangaReaderAPI;

public class MangaReaderListAsync extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {
    public static boolean isRunning = false;
    public static boolean wasCalled = false;
	private Context context;
	private ProgressDialog mDialog;
	private Gson GSON = new Gson();
    
    public MangaReaderListAsync(Context context) {
		// TODO Auto-generated constructor stub
    	this.context = context;
	}

    @Override
    protected void onPostExecute(ArrayList<ArrayList<String>> manga_list) {
    	isRunning = false;
    	wasCalled = true;
    	Manga.web_library = new Library(manga_list);
    	Manga.manga_names = Manga.web_library.toStringArray();

    
    	if(MangaReaderListActivity.adapter!=null)
    	{
    		MangaReaderListActivity.adapter.clear();
    		for (int i = 0; i < Manga.manga_names.size(); i ++)
    			MangaReaderListActivity.adapter.add(Manga.manga_names.get(i));
    		MangaReaderListActivity.adapter.notifyDataSetChanged();
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
    
	@SuppressWarnings("unchecked")
	@Override
	protected ArrayList<ArrayList<String>> doInBackground(String... manga_first_chapter_url) {
		
	   	if (Manga.mra == null)
	   		Manga.mra = new MangaReaderAPI();
	   	
		SharedPreferences settings = context.getSharedPreferences(Constants.MANGA_LIST_SETTINGS, 0);
		
		ArrayList<ArrayList<String>> manga_list = new ArrayList<ArrayList<String>>(); 
   		manga_list = GSON.fromJson(settings.getString(Constants.MANGA_LIST, null), manga_list.getClass());
   		if (manga_list != null)
   			Manga.mra.setRawMangaList(manga_list);
   		if((Manga.mra.getRawMangaList() == null) || (Manga.mra.getRawMangaList().size() == 0))
   		{
   			Manga.mra.initializeMangaList();
   			Editor editor = settings.edit();
   			editor.putString(Constants.MANGA_LIST,GSON.toJson(Manga.mra.getRawMangaList()));
   			editor.commit();
   		}

		
    	return Manga.mra.getRawMangaList();
    }
}
