package com.fdl.mangaz;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Library {
	private ArrayList<Manga> allManga = null;
	
	public Library()
	{
		this.allManga = new ArrayList<Manga>();
	}
	
	public Library(ArrayList<ArrayList<String>> manga_list) {
		// TODO Auto-generated constructor stub
		if(Manga.mra == null)
			Manga.mra = new MangaReaderAPI();
		
		this.allManga = new ArrayList<Manga>();
		
		if(manga_list.size() != 0)
		{
			for(int i = 0; i < manga_list.get(MangaReaderAPI.MANGA_LIST_NAME_INDEX).size(); i ++)
			{
			
				String manga_name = manga_list.get(MangaReaderAPI.MANGA_LIST_NAME_INDEX).get(i);
				String manga_url = manga_list.get(MangaReaderAPI.MANGA_LIST_URL_INDEX).get(i);
				try {
					addManga(new Manga(manga_name,new URL(manga_url)));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			
			}
		}
	}

	// Return position of the manga added
	public int addManga(Manga m)
	{
		allManga.add(m);
		
		return allManga.size() - 1;
		
	}
	
	public Manga getManga(int index)
	{
		return allManga.get(index);
	}
	
	public int length()
	{
		return allManga.size();
	}

	public static Library getLibrary()
	{
		return new Library();
	}
	
	public ArrayList<Manga> getAllManga()
	{
		return allManga;
	}
	
	public ArrayList<String> toStringArray()
	{
		if (allManga == null)
			return new ArrayList<String>();
		
		ArrayList<String> s = new ArrayList<String>();
		for (int i = 0 ; i < allManga.size() ; i ++)
			s.add(allManga.get(i).getTitle());
		return s;
	}

	public Manga getManga(String manga_name) {
		
		if (allManga == null)
			return null;
		for(Manga m  : allManga)
		{ 
			if(m.getTitle().equals(manga_name))
				return m;
		}
		return null;
	}

	public void removeManga(String manga_name) {
		
		if (allManga == null)
			return;
		for(Manga m  : allManga)
		{ 
			if(m.getTitle().equals(manga_name))
			{
				allManga.remove(m);
				return;
			}
		}
	}

	public int addManga(Manga m, boolean fromSettings) {
		return addManga(m);
	}
	
}
