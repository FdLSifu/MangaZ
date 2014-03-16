package com.fdl.mangaz.chapterslider;

import java.util.ArrayList;

import com.fdl.mangaz.R;
import com.fdl.mangaz.chapterview.ChapterViewActivity;
import com.fdl.mangaz.utils.Manga;
import com.fdl.mangaz.utils.MangaReaderAPI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;
import android.widget.ProgressBar;

public class ChapterSliderActivity extends Activity implements OnPageChangeListener {
	
	public static ChapterSliderAdapter adapter;
	public static int nbPages = 0;
	public static int chapter_number;
	public static Manga current_manga;
	public static ArrayList<String> path = new ArrayList<String>();
	public static ProgressBar progress_bar;
	private ChapterSliderAsync mAsyncTask;
    private int selectedIndex;
    private boolean callHappened;
	private boolean mPageEnd;
	private Bundle bundleinstance;
	public static int orientation;
	public static ChapterSliderPager vp;
	protected void onCreate(Bundle savedInstanceState)
	{
		if(!callHappened)
		{
			super.onCreate(savedInstanceState);
			bundleinstance = savedInstanceState;
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			setContentView(R.layout.activity_chapter_slider);
		}
		else
			callHappened = false;
		
		orientation = getResources().getConfiguration().orientation;
		
		// Create list view
		vp = (ChapterSliderPager)findViewById(R.id.pager);
		
		progress_bar = (ProgressBar)findViewById(R.id.progressBar);
        
		current_manga = Manga.private_library.getManga(ChapterViewActivity.current_manga_name);
		
		ChapterSliderActivity.path = current_manga.getPath();
		ChapterSliderActivity.path.clear();
		

		ChapterSliderActivity.adapter = new ChapterSliderAdapter(this, path);
		ChapterSliderActivity.adapter.notifyDataSetChanged();
	
		vp.setAdapter(ChapterSliderActivity.adapter);

		vp.setOnPageChangeListener(this);
		
		
		// execute population of images in background
		mAsyncTask = new ChapterSliderAsync(getApplicationContext());
		mAsyncTask.execute(current_manga,Integer.toString(chapter_number));
		
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
	    return ChapterSliderActivity.adapter.getItems();
	}
	
	@Override
	protected void onPause() {
		callHappened = false;
		mAsyncTask.cancel(true);
		super.onPause();
	}

	@Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        selectedIndex = arg0;

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if( mPageEnd && arg0 == selectedIndex && !callHappened)
        {
            mPageEnd = false;//To avoid multiple calls. 
            callHappened = true;
            if((MangaReaderAPI.nbChapters - 1) != ChapterSliderActivity.chapter_number)
            {
            	ChapterSliderActivity.chapter_number = ChapterSliderActivity.chapter_number + 1 ;
            	this.onCreate(bundleinstance);
            }
        }
        else
            mPageEnd = false;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if(selectedIndex == ChapterSliderActivity.nbPages - 1)
        {
            mPageEnd = true;
            // Mark this chapter as read
            Activity ac = this.getParent();
            ChapterViewActivity.MarkChapterAsRead(ChapterSliderActivity.current_manga.getTitle(),ChapterSliderActivity.chapter_number);
            ChapterViewActivity.adapter.notifyDataSetChanged();
        }
    }
}
