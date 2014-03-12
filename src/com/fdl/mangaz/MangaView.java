package com.fdl.mangaz;

import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MangaView extends Activity {
	
	private Manga selected_manga;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manga_selected);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		String manga_name = intent.getStringExtra("manga_name");
		
		Manga current_manga = Manga.web_library.getManga(manga_name);
		
		this.selected_manga = current_manga;
		
		if(current_manga.getCover() == null)
			try {
				current_manga.setCover(Manga.mra.getImage(current_manga.getMainlink().toString()));
				current_manga.setInformations(Manga.mra.getDescription(current_manga.getMainlink().toString()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		ImageView imageview = (ImageView)findViewById(R.id.manga_cover);
		imageview.setImageBitmap(current_manga.getCover());
		
		TextView textview = (TextView)findViewById(R.id.manga_title);
		textview.setText(current_manga.getInformations());
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_favorite, menu);
		if(Manga.private_library.getManga(selected_manga.getTitle()) != null)
		{
			// Turn star on
			MenuItem star = menu.findItem(R.id.action_add_to_favorite);
			star.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_add_to_favorite:
	        	if(Manga.private_library.getManga(selected_manga.getTitle()) != null)
	        	{
	        		// already in library so unselected and remove from library
	        		item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_off));
	        		Manga.private_library.removeManga(selected_manga.getTitle());
	        		MainActivity.mainadapter.notifyDataSetChanged();
	        		Toast.makeText(getApplicationContext(),selected_manga.getTitle()+ " removed from library",Toast.LENGTH_SHORT).show();
	        	}
	        	else
	        	{
	        		// add this manga to the library
	        		item.setIcon(getResources().getDrawable(android.R.drawable.btn_star_big_on));
	        		Manga.private_library.addManga(selected_manga,false);
	        		MainActivity.mainadapter.notifyDataSetChanged();
	        		Toast.makeText(getApplicationContext(),selected_manga.getTitle()+ " added to library",Toast.LENGTH_SHORT).show();
	        	}
	        		
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
