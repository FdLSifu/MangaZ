package com.fdl.mangaz;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class ChapterSlider extends Activity {
	
	public static ChapterSliderAdapter adapter;
	public static int nbPages = 0;
	public static String current_chapter;
	public static String current_manga_name;
	public static ArrayList<String> path = new ArrayList<String>();
	private ChapterSliderAsync mAsyncTask;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chapter_slider);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Create list view
		ViewPager vp = (ViewPager)findViewById(R.id.pager);
		
		
		Manga current_manga = Manga.private_library.getManga(ChapterView.current_manga_name);
		
		ChapterSlider.path = current_manga.getPath();
		ChapterSlider.path.clear();
		
		if(ChapterSlider.adapter == null)
			ChapterSlider.adapter = new ChapterSliderAdapter(this, path);
		ChapterSlider.adapter.notifyDataSetChanged();
		
		String [] sequences = current_chapter.split(" ");
		int chapter_number = 0;
		for (int i = 0; i < sequences.length ; i ++)
			if(sequences[i].equals(":"))
			{
				chapter_number = Integer.parseInt(sequences[i-1]);
				chapter_number --; // to match index of array
				break;
			}
		
		try {
			current_manga.setMainlink(new URL(Manga.mra.getChapterList()[chapter_number]));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Manga.mra.setCurrentURL(Manga.mra.getChapterList()[chapter_number]);

		vp.setAdapter(ChapterSlider.adapter);
		
		// execute population of images in background
		mAsyncTask = new ChapterSliderAsync(getApplicationContext());
		mAsyncTask.execute(current_manga,Integer.toString(chapter_number));
		
	}
	
	@Override
	protected void onPause() {
		mAsyncTask.cancel(true);
		super.onPause();
	}
	

}
