package com.fdl.mangaz.utils;

import java.util.ArrayList;

import android.os.Environment;

public class Constants {
	public static final String CHAPTER_READ = "chapter_read";
	public static final String NB_PAGE = "nbPage";
	public static final String CHAPTER_LIST = "chapter_list";
	public static final String CHAPTER_URL = "chapter_url";
	public static final String MANGA_LIST_SETTINGS = "manga_list_settings";
	public static final String MANGA_LIST = "manga_list";
    static final String PREFS_NAME = "Manga_Library";

	
	public static String DEFAULT_SD_CARD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/../MangaZ";
}
