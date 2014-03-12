package com.fdl.mangaz;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * An unofficial API of MangaReader written by the developers of this program.
 * @author Aaron Gokaslan
 */
public class MangaReaderAPI implements MangaEngine{

	/**
	 * Current URL for future look ups
	 */
	private String currentURL;

	/**
	 * Base-URL
	 */
	private final static String MANGA_READER_URL = "http://www.mangareader.net/";
	private ArrayList<ArrayList<String>> mangaList = new ArrayList<ArrayList<String>>(); //List of Manga

	/**
	 * URLs
	 */
	public String[] pageURLs = new String[0];
	private String[] chapterURLs = new String[0];

	/**
	 * Names
	 */
	public static ArrayList<String> chapterNames = new ArrayList<String>();

	public static final int MANGA_LIST_NAME_INDEX = 0; 
	public static final int MANGA_LIST_URL_INDEX = 1;
	
	public static final String MANGA_LIST_SETTINGS = "manga_list";
	/**
	 * Constructor
	 */
	public MangaReaderAPI(){
	}
	/**
	 * Constructor
	 * @param currentURL
	 */
	public MangaReaderAPI(String currentURL){
		this.currentURL = currentURL;
	}

	/**
	 * Returns currentURL
	 */
	@Override
	public String getCurrentURL() {
		return currentURL;
	}

	/**
	 * Sets currentURL
	 */
	@Override
	public void setCurrentURL(String url) {
		if((currentURL == null) || (!currentURL.equals(url)))
		{
			chapterNames = new ArrayList<String>();
			chapterURLs = new String[0];
			pageURLs = new String[0];
		}
		
		currentURL = url;
		
	}

	/**
	 * Loads Image AND sets currentURL to loadedImg
	 * See getImage for simple image grabbing.
	 */
	@Override
	public Bitmap loadImg(String url) throws Exception {
		if(url==null || url.equals("")){
			System.out.println(url);
			throw new Exception("INVALID PARAMETER");
		}
		//Detects whether it is loading a new Manga 
		boolean hasMangaChanged = !getMangaName(currentURL).equals(getMangaName(url));
		boolean hasChapterChanged = getCurrentChapNum()!= getChapNum(url);
		Bitmap image = BitmapFactory.decodeStream(new URL(url).openStream());
		currentURL = url;
		if(hasMangaChanged || hasChapterChanged){
			refreshLists(); //Refreshes Chapter & Page URLs
		}
		return image;
	}

	/**
	 * Simply grabs the image of the specified URL
	 * @param URL The URL you want to grab the image from
	 * @return The BufferedImage of the page of the URL
	 * @throws IOException If URL is not valid or unable to complete request
	 */
	public Bitmap getImage(String URL) throws IOException{
		Document doc = Jsoup.connect(URL).timeout(5*1000).get();
		Elements e = doc.getElementsByTag("img");
		String imgUrl = e.get(0).absUrl("src");
		return BitmapFactory.decodeStream(new URL(imgUrl).openStream());
	}

	public String getDescription(String URL) throws IOException{
		Document doc = Jsoup.connect(URL).timeout(5*1000).get();
		Elements e = doc.select("div#readmangasum");
		String description = e.get(0).text();
		return description;
	}

	/**
	 * Gets the URL of the nextPage and returns it as a string.
	 */
	@Override
	public String getNextPage() {
		try {
			Document doc = Jsoup.connect(currentURL).get();
			Element navi = doc.getElementById("navi");
			String html = navi.getElementsByClass("next").html();//Manual Parsing Required
			html = html.substring(html.indexOf('"')+2, html.lastIndexOf('"'));
			return MANGA_READER_URL + html;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Gets URL of previous page
	 */
	@Override
	public String getPreviousPage() {
		try {
			Document doc = Jsoup.connect(currentURL).get();
			Element navi = doc.getElementById("navi");
			String html = navi.getElementsByClass("prev").html();//Manual Parsing Required
			if(html.contains("href=\"\"")){//Signifies that page does not exist
				return currentURL;
			}
			html = html.substring(html.indexOf('"')+2, html.lastIndexOf('"'));
			return MANGA_READER_URL + html;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Determines whether or not a URL is valid.
	 */
	@Override
	public boolean isValidPage(String url) {
		try{
			Document doc = Jsoup.connect(url).get();
			return doc.text().contains("404 Not Found");
		}
		catch(IOException ex){
			return false;
		}
	}

	/**
	 * Returns a list of all manga on site for selector
	 */
	@Override
	public List<String> getMangaList() {
		return mangaList.get(MANGA_LIST_NAME_INDEX);
	}

	public ArrayList<ArrayList<String>> getRawMangaList()
	{
		return mangaList;
	}
	/**
	 * Generates the name of the manga from the current URL
	 */
	@Override
	public String getMangaName(){
		return getMangaName(currentURL);
	}

	/**
	 * Generates the name of the manga from the URL
	 */
	private String getMangaName(String URL) {
		String name = URL.replace(MANGA_READER_URL, "");
		name = name.substring(0, name.indexOf("/"));
		if(isMangaHash(name)){//If it's a MangaHash, we have to parse the next / 
			name = URL.replace(MANGA_READER_URL, "");
			name = name.substring(0, name.lastIndexOf('/'));
			name = name.substring(name.lastIndexOf("/")+1);
		}
		name = name.replace('-', ' ');
		assert(!name.contains("."));
		return name;
	}

	/**
	 * Returns the list of chapters from current manga
	 */
	@Override
	public String[] getChapterList(){
		if (chapterURLs.length == 0)
			refreshLists();
		return chapterURLs;
	}

	/**
	 * Returns the list of pages from current manga
	 */
	@Override
	public String[] getPageList(){
		return pageURLs;
	}

	/**
	 * Gets the URL of the Manga with the specified name from the site.
	 */
	@Override
	public String getMangaURL(String mangaName) {
		String mangaURL = "";
		mangaName = StringUtil.removeTrailingWhiteSpaces(mangaName);
		try {
			int index = mangaList.get(MANGA_LIST_NAME_INDEX).indexOf(mangaName);
			if (index != -1)
				mangaURL = mangaList.get(MANGA_LIST_URL_INDEX).get(index);

			//System.out.println(mangaURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mangaURL;
	}
	
	/**
	 * Gets the URL of the Manga with the specified name from the site.
	 */
	@Override
	public String getFirstChapterURL(String mangaName) {
		String mangaURL = "";
		mangaName = StringUtil.removeTrailingWhiteSpaces(mangaName);
		try {
			int index = mangaList.get(MANGA_LIST_NAME_INDEX).indexOf(mangaName);
			if (index != -1)
			{
				mangaURL = mangaList.get(MANGA_LIST_URL_INDEX).get(index);
				mangaURL = getFirstChapter(mangaURL);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mangaURL;
	}


	/**
	 * Gets the first chapter listed on the MangaURL
	 * @param mangaURL The URL of the manga 
	 * @return The URL of the first chapter of the manga
	 * @throws Exception If cannot complete request
	 */
	private String getFirstChapter(String mangaURL) throws Exception{
		Document doc = Jsoup.connect(mangaURL).timeout(10*1000).get();
		Element list = doc.getElementById("listing");
		Elements names = list.select("a");
		return names.first().absUrl("href");
	}

	/**
	 * Gets the currentPageNum calculated from the URL
	 */
	@Override
	public int getCurrentPageNum() {
		String check = currentURL.replace(MANGA_READER_URL, "");
		check = check.substring(0,check.indexOf('/'));
		if(isMangaHash(check)){//There are two types of ways of delineating Manga on the site
			String page = check.substring(check.lastIndexOf('-')+1);
			return (int)Double.parseDouble(page);
		}
		else{
			String number = currentURL.substring(currentURL.lastIndexOf('/'));
			return (int)Double.parseDouble(number);
		}
	}

	/**
	 * Gets the currentChapterNum parsed from the URL
	 */
	@Override
	public int getCurrentChapNum() {
		return (int)getChapNum(currentURL);
	}

	/**
	 * Generates a list of the URLs and names of all Manga on the site
	 * @return
	 */
	public void initializeMangaList(){
		try{
			Document doc = Jsoup.connect("http://www.mangareader.net/alphabetical").timeout(10*1000)
					.maxBodySize(0).get();
			Elements bigList = doc.getElementsByClass("series_alpha");
			Elements names = new Elements();
			for(Element miniList: bigList){
				 names.addAll(miniList.select("li"));
			}
			
			ArrayList<ArrayList<String>>mangaList = new ArrayList<ArrayList<String>>();
			mangaList.add(0,new ArrayList<String>());
			mangaList.add(1,new ArrayList<String>());
			
			for(int i = 0; i<names.size(); i++){
				Element e = names.get(i).select("a").first();
				if(!mangaList.get(0).contains(e.text().replace("[Completed]","")))
				{
					mangaList.get(0).add(e.text().replace("[Completed]",""));
					mangaList.get(1).add(e.absUrl("href"));
				}
			}

			this.mangaList = mangaList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.mangaList = null;
		}

	}

	/**
	 * Generates a chapter number from the URL
	 * @param URL the URL you want to generate the number of
	 * @return The calculated number
	 */
	private double getChapNum(String URL){
		if(URL.contains("chapter")){
			String test = URL.substring(URL.lastIndexOf('-')+1);
			if(test.indexOf('.')!=-1){
				test = test.substring(0, test.indexOf('.'));
			}
			return Double.parseDouble(test);
		}
		if(hasMangaHash(URL)){//There are two types of ways of delineating Manga on the site
			String chapter = URL.replace(MANGA_READER_URL, "");
			chapter = chapter.substring(0,chapter.indexOf("/"));
			chapter = chapter.substring(chapter.indexOf('-')+1, chapter.lastIndexOf('-'));
			return Double.parseDouble(chapter);
		}
		else{
			String number = URL.substring(0,URL.lastIndexOf('/'));
			number = number.substring(number.lastIndexOf('/')+1);
			if(!StringUtil.isNum(number)){//In case it grabs the name instead
				number = URL.substring(URL.lastIndexOf('/')+1);
			}
			return Double.parseDouble(number);
		}
	}

	/**
	 * Checks if the number contains MangaHashes
	 * @param URL
	 * @return
	 */
	private boolean hasMangaHash(String URL){
		String end = URL.replace(MANGA_READER_URL, "");
		String[] pieces = end.split("/");
		for(String s: pieces){
			if(isMangaHash(s)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if it's uses a manga hash to store data about the manga.
	 * @param input The String you want to check
	 * @return True if it is, false otherwise
	 */
	private boolean isMangaHash(String input){
		for(char c: input.toCharArray()){
			if(!(Character.isDigit(c) || c=='-')){
				return false;
			}
		}
		if(!input.contains("-")){//In case it got the chapter number at the end
			return false;
		}
		return true;
	}

	/**
	 * Refreshes the list of chapters and pages based off of the currentURL
	 */
	public void refreshLists(){
		this.chapterURLs = initializeChapterList();
		//this.pageURLs = initializePageList();
		chapterNames = initializeChapterNames();
	}

	/**
	 * Generates a list of all the pages in the current chapter of the current manga
	 * @return A list of all pages in the current chapter of the current manga
	 */
	public void initializePageList() {
		try {
			Document doc = Jsoup.connect(currentURL).timeout(10*1000).get();
			Elements items = doc.getElementById("pageMenu").children();
			String[] out = new String[items.size()];
			for(int i = 0; i<items.size(); i++){
				out[i] = items.get(i).absUrl("value");
			}
			this.pageURLs = out;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.pageURLs = null;
		}
	}


	/**
	 * Generates a list of all the current chapters in the current Manga
	 * @return A String[] of all current chapters in the manga.
	 */
	private String[] initializeChapterList() {
		String baseURL = MANGA_READER_URL + getMangaName().replace(' ', '-');
		try{
			List<String> outList = new ArrayList<String>();
			Document doc = Jsoup.connect(baseURL).timeout(10*1000).get();
			Element list = doc.getElementById("listing");
			Elements names = list.select("tr");
			names = names.select("a");
			for(Element e: names){
				outList.add(e.absUrl("href"));
			}
			String[] out = new String[outList.size()];
			outList.toArray(out);
			return out;
		}
		catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<String> getChapterNames(){
		if (chapterNames == null)
			refreshLists();
		return chapterNames;
	}

	private ArrayList<String> initializeChapterNames(){
		String baseURL = MANGA_READER_URL + getMangaName().replace(' ', '-');
		try{
			Document doc = Jsoup.connect(baseURL).timeout(10*1000).maxBodySize(0).get();
			Element list = doc.getElementById("listing");
			Elements names = list.select("tr");
			ArrayList<String> out = new ArrayList<String>();
			for(int i = 0; i<names.size()-1; i++){
				out.add(names.get(i+1).select("td").text());
			}
			out = StringUtil.formatChapterNames(out);
			return out;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	public void setRawMangaList(ArrayList<ArrayList<String>> manga_list) {
		this.mangaList = manga_list;
		
	}
	public void setChapterNames(ArrayList<String> chapter_names) {
		chapterNames = chapter_names;
	}

}