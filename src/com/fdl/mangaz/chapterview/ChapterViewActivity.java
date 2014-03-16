package com.fdl.mangaz.chapterview;


import com.fdl.mangaz.R;
import com.fdl.mangaz.chapterslider.ChapterSliderActivity;
import com.fdl.mangaz.utils.Constants;
import com.fdl.mangaz.utils.Manga;
import com.fdl.mangaz.utils.MangaReaderAPI;
import com.fdl.mangaz.utils.StringUtil;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChapterViewActivity extends Activity implements OnItemClickListener {
	public ListView lv = null;
	public static ChapterViewAdpater adapter = null;
	public static String current_manga_name;
	private ChapterViewAsync mAsyncTask;
	public static boolean ascending_sort = false;
	public static ChapterViewActivity cva;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cva = this;
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.chapter_list);
		// Create list view
		lv = (ListView)findViewById(R.id.chapter_list);
		Manga current_manga = Manga.private_library.getManga(current_manga_name);
		
		if (mAsyncTask == null)
			mAsyncTask = new ChapterViewAsync(this);
		else
			mAsyncTask.setContext(this);
		
		if(Manga.mra == null)
			mAsyncTask.execute(current_manga.getMainlink().toString());
		else
		{
			Manga.mra.setCurrentURL(current_manga.getMainlink().toString());
			if(current_manga.getChapterlist().length == 0)
				mAsyncTask.execute(current_manga.getMainlink().toString());
		}
		if (adapter == null)
		{
			adapter = new ChapterViewAdpater(this,android.R.layout.simple_list_item_1,MangaReaderAPI.chapterNames);

		}

		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this,ChapterSliderActivity.class);
		String current_chapter = (String)parent.getItemAtPosition(position);
		ChapterSliderActivity.chapter_number = Manga.chapter_names.indexOf(current_chapter); //StringUtil.getChapterNumber(current_chapter)-1;
    	startActivity(intent);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chapter_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_change_sort:
	        	ascending_sort = !ascending_sort;
	        	adapter.sort(null);
	        	adapter.notifyDataSetChanged();
	        	return true;
	        default:
	        	return super.onOptionsItemSelected(item);
	            
	    }
	}
	
	@Override
	protected void onPause() {
		mAsyncTask.cancel(true);
		super.onPause();
	}
	
	public static void MarkChapterAsRead(String manga_name, int chapter_num) {
		SharedPreferences settings = cva.getSharedPreferences(StringUtil.sanitizeFilename(manga_name), 0);
		boolean wasRead = settings.getBoolean(Constants.CHAPTER_READ+chapter_num, false); 
		if(!wasRead)
		{
			Editor editor = settings.edit();
			editor.putBoolean(Constants.CHAPTER_READ+chapter_num, true);
			editor.commit();
		}
	}

	public static void MarkChapterAsUnread(String manga_name, int chapter_num) {
		SharedPreferences settings = cva.getSharedPreferences(StringUtil.sanitizeFilename(manga_name), 0);
		if(settings.getBoolean(Constants.CHAPTER_READ+chapter_num, false))
		{
			Editor editor = settings.edit();
			editor.remove(Constants.CHAPTER_READ+chapter_num);
			editor.commit();
		}
	}
	
	public static boolean isChapterRead(String manga_name, int chapter_num)
	{
		SharedPreferences settings = cva.getSharedPreferences(StringUtil.sanitizeFilename(manga_name), 0);
		
		
		return settings.getBoolean(Constants.CHAPTER_READ+chapter_num, false);
	}

}
