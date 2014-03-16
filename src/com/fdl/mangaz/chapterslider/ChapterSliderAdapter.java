package com.fdl.mangaz.chapterslider;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

import com.fdl.mangaz.R;
import com.fdl.mangaz.utils.TouchImageView;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ChapterSliderAdapter extends PagerAdapter {
    private ArrayList<String> _imagePaths;
	private Activity _activity;
	private LayoutInflater inflater;
	
	// constructor
    public ChapterSliderAdapter(Activity activity,
            ArrayList<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
    }
 
    @Override
    public int getCount() {
        return this._imagePaths.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	PhotoView imgDisplay;
        TextView txtDisplay;
  
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.fullscreen_image, container,
                false);
		
		viewLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
	            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
	            | View.SYSTEM_UI_FLAG_IMMERSIVE
	            );
		
        imgDisplay = (PhotoView) viewLayout.findViewById(R.id.imgDisplay);
        txtDisplay = (TextView)viewLayout.findViewById(R.id.chap_over_nb);
        		
        		
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        imgDisplay.setImageBitmap(bitmap);
        
        imgDisplay.setAdjustViewBounds(true);
        
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        if(ChapterSliderActivity.orientation == Configuration.ORIENTATION_PORTRAIT)
        	params.addRule(RelativeLayout.CENTER_IN_PARENT);
        else
        	params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imgDisplay.setLayoutParams(params);
        
        txtDisplay.setText((position+1)+"/"+ChapterSliderActivity.nbPages);
        
        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }

	public Object getItems() {
		// TODO Auto-generated method stub
		return _imagePaths;
	}
}
