package com.fdl.mangaz.chapterslider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.fdl.mangaz.utils.Constants;
import com.fdl.mangaz.utils.Manga;
import com.fdl.mangaz.utils.MangaReaderAPI;
import com.fdl.mangaz.utils.StringUtil;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class ChapterSliderAsync extends AsyncTask<Object, Integer, Void> {

	private Context mContext;
	private ArrayList<String> asyncPath = new ArrayList<String>();
	
	public ChapterSliderAsync(Context context) {
		
		mContext = context;
	}
	@Override
	protected Void doInBackground(Object... manga) 
	{

		Gson GSON = new Gson();
		SharedPreferences settings = mContext.getSharedPreferences(StringUtil.sanitizeFilename(ChapterSliderActivity.current_manga.getTitle()), 0);
		int nbPage = settings.getInt(Constants.NB_PAGE+((String)manga[1]), 0);
		ArrayList<String> chapter_url = new ArrayList<String>();
		
		if (nbPage == 0)
		{
			chapter_url = GSON.fromJson(settings.getString(StringUtil.sanitizeFilename(ChapterSliderActivity.current_manga.getTitle()) + Constants.CHAPTER_URL,null),chapter_url.getClass());
			if( (chapter_url == null) || (chapter_url.size() ==0))
				chapter_url = Manga.mra.getChapterList();
			
			try {
				ChapterSliderActivity.current_manga.setMainlink(new URL(chapter_url.get(ChapterSliderActivity.chapter_number)));				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Manga.mra.setCurrentURL(chapter_url.get(ChapterSliderActivity.chapter_number));
			
			Manga.mra.initializePageList();
			// write settings
			Editor editor = settings.edit();
			nbPage = Manga.mra.getPageList().size();
			editor.putInt(Constants.NB_PAGE+((String)manga[1]), nbPage);
			// write PageList tab
			for(int i = 0 ; i < nbPage; i ++)
				editor.putString(((String)manga[1])+i, Manga.mra.getPageList().get(i));
			editor.commit();
			
		}
		else
		{
			// read settings back
			ArrayList<String> pageList = new ArrayList<String>(nbPage);
			for(int i = 0 ; i < nbPage; i ++)
				pageList.add(settings.getString(((String)manga[1])+i, ""));
			Manga.mra.pageURLs = pageList;
		}
		
		
		ChapterSliderActivity.nbPages = (Manga.mra.getPageList()).size();
		
		for(int i = 0; i < ChapterSliderActivity.nbPages ; i ++)
		{
			try {
				String dir = com.fdl.mangaz.utils.Constants.DEFAULT_SD_CARD_PATH + "/" + ChapterSliderActivity.current_manga.getTitle() + "/" + (Integer.parseInt((String)manga[1]) + 1);
				File manga_dir = new File(dir);
				manga_dir.mkdirs();
				String file_name = i+".jpg";
				File fImage = new File(manga_dir,file_name);
				if(!fImage.exists())
				{
					Bitmap manga_image = Manga.mra.getImage(Manga.mra.getPageList().get(i));
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
				
				publishProgress(Integer.valueOf(i));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(isCancelled())
				break;
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		ChapterSliderActivity.path.clear();
		ChapterSliderActivity.path.addAll(asyncPath);
		ChapterSliderActivity.adapter.notifyDataSetChanged();
		
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		ChapterSliderActivity.path.clear();
		ChapterSliderActivity.path.addAll(asyncPath);
		ChapterSliderActivity.adapter.notifyDataSetChanged();
		
		super.onCancelled();
	}
	
	@Override
	protected void onPreExecute() {
		asyncPath.clear();
		ChapterSliderActivity.path.clear();
		ChapterSliderActivity.adapter.notifyDataSetChanged();
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		
		ChapterSliderActivity.progress_bar.setMax(ChapterSliderActivity.nbPages);
		
        if((values[0].intValue()+1) == ChapterSliderActivity.nbPages)
        	ChapterSliderActivity.progress_bar.setProgress(0);
        else
        	ChapterSliderActivity.progress_bar.setProgress(values[0].intValue()+1);
		
		ChapterSliderActivity.path.clear();
		ChapterSliderActivity.path.addAll(asyncPath);
		ChapterSliderActivity.adapter.notifyDataSetChanged();
		super.onProgressUpdate(values);
	}

}
