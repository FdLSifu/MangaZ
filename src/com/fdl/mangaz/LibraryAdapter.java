package com.fdl.mangaz;

import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LibraryAdapter extends BaseAdapter {
    static final String PREFS_NAME = "Manga_Library";
	private Context context;
    
    public LibraryAdapter(Context context)
    {
    	this.context = context;
    	
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
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
	public Object getItem(int arg0) {
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
   
        return convertView;
    }
}

