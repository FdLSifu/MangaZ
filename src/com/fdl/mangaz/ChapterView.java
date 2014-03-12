package com.fdl.mangaz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChapterView extends Activity implements OnItemClickListener {
	public ListView lv = null;
	public static ArrayAdapter<String> adapter = null;
	public static String current_manga_name;
	private ChapterViewAsync mAsyncTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chapter_list);
		// Create list view
		lv = (ListView)findViewById(R.id.chapter_list);
		Manga current_manga = Manga.private_library.getManga(current_manga_name);
		
		if (mAsyncTask == null)
			mAsyncTask = new ChapterViewAsync(this);
		else
			mAsyncTask.setContext(this);
		
		if(Manga.mra == null)
			(new ChapterViewAsync(this)).execute(current_manga.getMainlink().toString());
		else
		{
			Manga.mra.setCurrentURL(current_manga.getMainlink().toString());
			if(current_manga.getChapterlist().length == 0)
				mAsyncTask.execute(current_manga.getMainlink().toString());
		}
		if (adapter == null)
		{
			adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,MangaReaderAPI.chapterNames);

		}

		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,ChapterSlider.class);
		ChapterSlider.current_chapter = (String)parent.getItemAtPosition(position);
    	startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		mAsyncTask.cancel(true);
		super.onPause();
	}
}
