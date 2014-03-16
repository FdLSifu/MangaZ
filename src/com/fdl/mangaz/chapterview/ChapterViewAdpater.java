package com.fdl.mangaz.chapterview;

import java.util.Comparator;
import java.util.List;

import com.fdl.mangaz.utils.StringUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChapterViewAdpater extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private Context mContext;
    private int mViewResourceId;
    
	public ChapterViewAdpater(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		mContext = context;
		mViewResourceId = resource;
		mInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void sort(Comparator<? super String> comparator) {
		Comparator<String> compar = new Comparator<String>() {
 			 public int compare(String e1, String e2) {
 				 	int ret = 0;
 				 	
 				 	int a = StringUtil.getChapterNumber(e1);
 				 	int b = StringUtil.getChapterNumber(e2);
 				 	if( a>b)
 				 		ret = 1;
 				 	else if(a<b)
 				 		ret = -1;
 				 	else ret = 0;
 				 	if (ChapterViewActivity.ascending_sort)
 				 		return ret;
 				 	else return -ret;
 				 		
 			    }
 		};
 		
 		super.sort(compar);
 		
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);
        convertView = super.getView(position, convertView, parent);
        TextView tv = (TextView)convertView.findViewById(android.R.id.text1); //Give Id to your textview
        String s = tv.getText().toString();
        int chap_index = StringUtil.getChapterNumber(s)-1;
        

        if(ChapterViewActivity.isChapterRead(ChapterViewActivity.current_manga_name,chap_index))
        		tv.setTextColor(Color.DKGRAY);

		return convertView;
	}
}
