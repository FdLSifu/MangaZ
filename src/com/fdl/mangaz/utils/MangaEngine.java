package com.fdl.mangaz.utils;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Bitmap;

/**
 * An interface used for Manga Scrapers
 * @author Skylion
 *
 */
public interface MangaEngine {

	public String getCurrentURL();
	public void setCurrentURL(String url);
	public Bitmap loadImg(String url)throws Exception;
	public Bitmap getImage(String url) throws Exception;


	public String getNextPage();
	public String getPreviousPage();

	public boolean isValidPage(String url);

	public List<String> getMangaList();

	public String getMangaName();

	public ArrayList<String> getChapterList();
	public ArrayList<String> getPageList();
	public ArrayList<String> getChapterNames();

	public String getMangaURL(String mangaName);

	public int getCurrentPageNum();
	public int getCurrentChapNum();
	String getFirstChapterURL(String mangaName);

}