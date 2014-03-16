package com.fdl.mangaz;

import java.io.IOException;

import com.fdl.mangaz.utils.Manga;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MangaDescriptionActivity extends Activity {
	
	private Manga selected_manga;
	private ImageView imageview;
	private TextView textview;
	private MangaDescriptionActivity me;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		me = this;
		setContentView(R.layout.manga_selected);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		(new MangaDescriptionAsync()).execute();
				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_favorite, menu);
		if(Manga.private_library.getManga(selected_manga.getTitle()) != null)
		{
			// Turn star on
			MenuItem star = menu.findItem(R.id.action_add_to_favorite);
			star.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
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
	        		item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_add));
	        		Manga.private_library.removeManga(selected_manga.getTitle());
	        		MainActivity.mainadapter.notifyDataSetChanged();
	        		Toast.makeText(getApplicationContext(),selected_manga.getTitle()+ " removed from library",Toast.LENGTH_SHORT).show();
	        	}
	        	else
	        	{
	        		// add this manga to the library
	        		item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete));
	        		Manga.private_library.addManga(selected_manga,false);
	        		MainActivity.mainadapter.notifyDataSetChanged();
	        		Toast.makeText(getApplicationContext(),selected_manga.getTitle()+ " added to library",Toast.LENGTH_SHORT).show();
	        	}
	        		
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private class MangaDescriptionAsync extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog mDialog;
		protected Void doInBackground(Void[] params) {
			Intent intent = getIntent();
			String manga_name = intent.getStringExtra("manga_name");

			Manga current_manga = Manga.web_library.getManga(manga_name);
			
			selected_manga = current_manga;
			
			imageview = (ImageView)findViewById(R.id.manga_cover);
			textview = (TextView)findViewById(R.id.manga_title);
			
			if(selected_manga.getCover() == null)
				try {
					selected_manga.setCover(Manga.mra.getImage(selected_manga.getMainlink().toString()));
					selected_manga.setInformations(Manga.mra.getDescription(selected_manga.getMainlink().toString()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			return null;
			
		}
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
	    	mDialog = new ProgressDialog(me);
	        mDialog.setMessage("Please wait...");
	        mDialog.show();
	        
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			imageview.setImageBitmap(selected_manga.getCover());
			
			textview.setText(selected_manga.getInformations());
			
			mDialog.dismiss();
		}
	}
}
