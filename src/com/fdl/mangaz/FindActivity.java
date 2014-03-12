package com.fdl.mangaz;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class FindActivity extends Activity implements OnItemClickListener, OnQueryTextListener{
	public ListView lv = null;
	public static ArrayAdapter<String> adapter = null;
	private MangaDownloader mAsyncTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_mangas);
	
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create list view
		lv = (ListView)findViewById(R.id.listview);
		
		if (Manga.web_library != null)
			Manga.manga_names = Manga.web_library.toStringArray();
		
		// create adapter to display items
		if (adapter == null)
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, Manga.manga_names);
		else
		{
			adapter.clear();
			for (int i = 0; i < Manga.manga_names.size(); i ++)
				adapter.add(Manga.manga_names.get(i));
			adapter.notifyDataSetChanged();
		}

		// Create if not already running
		if (Manga.manga_names.size() == 0)
		{
			mAsyncTask = new MangaDownloader(this);
			mAsyncTask.execute("");
		}
	
		// Initialize adapter
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);

	}
	
	public ArrayAdapter<String> getAdapter()
	{
		return adapter;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,MangaView.class);
		intent.putExtra("manga_name",(String)parent.getItemAtPosition(position));
    	startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_mangas).getActionView();
        searchView.setOnQueryTextListener(this);
		return true;
	}
	
	@Override
	public boolean onQueryTextChange(String DynamicText) {
		// TODO Auto-generated method stub
		FindActivity.adapter.getFilter().filter(DynamicText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String FinalText) {
		// TODO Auto-generated method stub
		FindActivity.adapter.getFilter().filter(FinalText);
		return false;
	}

	
	@Override
	protected void onPause() {
		if(mAsyncTask != null)
			mAsyncTask.cancel(true);
		super.onPause();
	}
}