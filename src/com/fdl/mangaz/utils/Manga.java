package com.fdl.mangaz.utils;

import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class Manga {
	private String title;
	private int id;
	private int nbChapters;
	private String informations;
	private Bitmap cover = null;
	private URL mainlink;
	private String[] chapterlist = new String[0];
	private String author;
	private ArrayList<String> path = new ArrayList<String>();
	public static PermanentLibrary private_library = null;
	public static Library web_library = null;
	public static MangaReaderAPI mra = null;
	public static ArrayList<String> manga_names = new ArrayList<String>();
	
	public Manga(String name, URL mainlink)
	{
		setTitle(name);
		setMainlink(mainlink);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNbChapters() {
		return nbChapters;
	}

	public void setNbChapters(int nbChapters) {
		this.nbChapters = nbChapters;
	}

	public String getInformations() {
		return informations;
	}

	public void setInformations(String informations) {
		this.informations = informations;
	}

	public Bitmap getCover() {
		return cover;
	}

	public void setCover(Bitmap cover) {
		this.cover = cover;
	}

	public String[] getChapterlist() {
		return chapterlist;
	}

	public void setChapterlist(String[] chapterlist) {
		this.chapterlist = chapterlist;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public URL getMainlink() {
		return mainlink;
	}

	public void setMainlink(URL mainlink) {
		this.mainlink = mainlink;
	}

	public ArrayList<String> getPath() {
		return path ;
	}

}
