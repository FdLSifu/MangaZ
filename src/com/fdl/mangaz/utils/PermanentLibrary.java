package com.fdl.mangaz.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class PermanentLibrary extends Library {

	private Context mContext;

	public PermanentLibrary() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public PermanentLibrary(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public int addManga(Manga m,boolean fromSettings) {
		// TODO Auto-generated method stub
		
		if(!fromSettings)
		{
			try {
				m.setMainlink(new URL(Manga.mra.getFirstChapterURL(m.getTitle()).toString()));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	        SharedPreferences settings = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
	        SharedPreferences.Editor editor = settings.edit();
	       
	        int nb_mangas = settings.getInt("nb_mangas",0);
	        editor.putInt("nb_mangas", nb_mangas+1);
	                
			editor.putString("manga"+"_"+nb_mangas, m.getTitle());
			editor.putString("manga_url"+"_"+nb_mangas, m.getMainlink().toString());
	
			editor.commit();
		}
		File manga_dir = new File(mContext.getFilesDir() + "/" + m.getTitle());
		manga_dir.mkdirs();
		File fCover = new File(manga_dir,"cover.png");
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(fCover);
	
			if(m.getCover() != null)
				m.getCover().compress(Bitmap.CompressFormat.PNG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return super.addManga(m);
	}
	
	@Override
	public void removeManga(String manga_name) {
		int manga_index = 0;
		
		SharedPreferences settings = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
       
        int nb_mangas = settings.getInt("nb_mangas",0);
        ArrayList<String> manganame = new ArrayList<String>();
        ArrayList<String> mangaurl = new ArrayList<String>();
        
        for (int index = 0; index < nb_mangas ; index++)
        {
        	String current_setting = settings.getString("manga"+"_"+index, ""); 
        	
			if(current_setting.equals(manga_name))
        		manga_index = index;
			manganame.add(current_setting);
			mangaurl.add(settings.getString("manga_url"+"_"+index,""));
			
        }
        manganame.remove(manga_index);
        mangaurl.remove(manga_index);
        
        for (int index = 0; index < nb_mangas-1 ; index++)
        {
        	editor.putString("manga"+"_"+index, manganame.get(index));
        	editor.putString("manga_url"+"_"+index, mangaurl.get(index));
			
        }
        
        editor.putInt("nb_mangas", settings.getInt("nb_mangas",0)-1);
		editor.commit();


		super.removeManga(manga_name);
	}
}
