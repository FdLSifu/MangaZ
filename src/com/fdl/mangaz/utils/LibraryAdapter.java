package com.fdl.mangaz.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.fdl.mangaz.R;


import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LibraryAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Integer> mItemIndex = new ArrayList<Integer>();
    
    public LibraryAdapter(Context context)
    {
    	this.context = context;
    	
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        int nb_mangas = settings.getInt("nb_mangas",0);
        

        Manga.private_library =  new PermanentLibrary(context);
        Manga m;
        
        for (int i = 0 ; i < nb_mangas; i++)
        {
        	// Get manga name and manga url from prefs
        	 try {
				m = new Manga(settings.getString("manga"+"_"+i, null),new URL(settings.getString("manga_url"+"_"+i, null)));
				m.setCover(BitmapFactory.decodeFile(context.getFilesDir() + "/" + m.getTitle() + "/cover.png"));
				Manga.private_library.addManga(m,true);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Manga.private_library.length();
		
	}
	
	@Override
	public Manga getItem(int arg0) {
		// TODO Auto-generated method stub
		return Manga.private_library.getManga(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.manga_library, null);
        }
        
        TextView txtTitle = (TextView) convertView.findViewById(R.id.icon_text);
		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon_image);
	        
	   imgIcon.setImageBitmap(Manga.private_library.getManga(position).getCover());
	   txtTitle.setText(Manga.private_library.getManga(position).getTitle());
	   
	   if(mItemIndex.contains(Integer.valueOf(position)))
	   {
		   convertView.setSelected(true);
           convertView.setPressed(true);
           convertView.setBackgroundColor(Color.parseColor("#38c0f4"));
	   }
	   else
	   {
		   convertView.setSelected(false);
           convertView.setPressed(false);
           convertView.setBackgroundResource(0);
	   }
       return convertView;
    }
    

    public void setSelectItem(int index) {
    	if(!mItemIndex.contains(Integer.valueOf(index)))
    		mItemIndex.add(Integer.valueOf(index));
    	else
    		mItemIndex.remove(Integer.valueOf(index));
    	
    }
    
    public ArrayList<Integer> getSelectItems()
    {
    	return mItemIndex;
    }
    
	public void clearSelect() {
		mItemIndex.clear();
		this.notifyDataSetChanged();
		
	}

    
}

