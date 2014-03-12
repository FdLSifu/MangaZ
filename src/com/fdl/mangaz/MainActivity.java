package com.fdl.mangaz;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends Activity implements OnItemClickListener {

	public static LibraryAdapter mainadapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library_grid);
		
		GridView gridview = (GridView)findViewById(R.id.library_grid);
		if (mainadapter == null)
			mainadapter = new LibraryAdapter(getApplicationContext());
		gridview.setAdapter(mainadapter);
		
		gridview.setOnItemClickListener(this);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		Intent intent;
	    switch (item.getItemId()) {
	        case R.id.action_search:
	        	intent = new Intent(this,FindActivity.class);
	        	startActivity(intent);
	            return true;
	        case R.id.action_download:
	        	 intent = new Intent(this,DownloadActivity.class);
	        	startActivity(intent);
	            return true;
	        case R.id.action_settings:
	        	intent = new Intent(this,SettingsActivity.class);
	        	startActivity(intent);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_SEARCH)
		{
			Intent intent = new Intent(this,FindActivity.class);
        	startActivity(intent);
            return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void onItemClick(AdapterView<?> parent, View v,
             int position, long id) {
			Intent intent = new Intent(this,ChapterView.class);
			ChapterView.current_manga_name = ((Manga)parent.getItemAtPosition(position)).getTitle();
	    	startActivity(intent);
	 }

}
