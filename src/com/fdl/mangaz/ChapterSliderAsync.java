package com.fdl.mangaz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class ChapterSliderAsync extends AsyncTask<Object, Void, Void> {

	private Context mContext;
	private ArrayList<String> asyncPath = new ArrayList<String>();
	
	public ChapterSliderAsync(Context context) {
		
		mContext = context;
	}
	@Override
	protected Void doInBackground(Object... manga) 
	{
		Manga current_manga = (Manga)manga[0];
		
		SharedPreferences settings = mContext.getSharedPreferences(current_manga.getTitle()+((String)manga[1]), 0);
		int nbPage = settings.getInt("nbPage", 0);
		if (nbPage == 0)
		{
			Manga.mra.initializePageList();
			// write settings
			Editor editor = settings.edit();
			nbPage = Manga.mra.getPageList().length;
			editor.putInt("nbPage", nbPage);
			// write PageList tab
			for(int i = 0 ; i < nbPage; i ++)
				editor.putString(current_manga.getTitle()+((String)manga[1])+i, Manga.mra.getPageList()[i]);
			editor.commit();
			
		}
		else
		{
			// read settings back
			String [] pageList = new String[nbPage];
			for(int i = 0 ; i < nbPage; i ++)
				pageList[i] = settings.getString(current_manga.getTitle()+((String)manga[1])+i, "");
			Manga.mra.pageURLs = pageList;
		}
		
		
		ChapterSlider.nbPages = (Manga.mra.getPageList()).length;
		
		for(int i = 0; i < ChapterSlider.nbPages ; i ++)
		{
			try {
				String dir = mContext.getFilesDir() + "/" + current_manga.getTitle() + "/" + ((String)manga[1]);
				File manga_dir = new File(dir);
				manga_dir.mkdirs();
				String file_name = i+".jpg";
				File fImage = new File(manga_dir,file_name);
				if(!fImage.exists())
				{
					Bitmap manga_image = Manga.mra.getImage(Manga.mra.getPageList()[i]);
					FileOutputStream fOut = new FileOutputStream(fImage);
					manga_image.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
					fOut.flush();
					fOut.close();
				}
				// update path and slider
				if (i == asyncPath.size())
					asyncPath.add(i, dir +"/" + file_name);
				else 
					asyncPath.set(i, dir +"/" + file_name);
				
				publishProgress((Void[])null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		ChapterSlider.path.clear();
		ChapterSlider.path.addAll(asyncPath);
		ChapterSlider.adapter.notifyDataSetChanged();
		super.onPostExecute(result);
	}
	
	
	@Override
	protected void onPreExecute() {
		asyncPath.clear();
		
		ChapterSlider.adapter.notifyDataSetChanged();
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		ChapterSlider.path.clear();
		ChapterSlider.path.addAll(asyncPath);
		ChapterSlider.adapter.notifyDataSetChanged();
		super.onProgressUpdate(values);
	}

}
