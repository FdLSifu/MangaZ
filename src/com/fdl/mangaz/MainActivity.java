package com.fdl.mangaz;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.fdl.mangaz.chapterslider.ChapterSliderActivity;
import com.fdl.mangaz.chapterview.ChapterViewActivity;
import com.fdl.mangaz.mangareaderlist.MangaReaderListActivity;
import com.fdl.mangaz.utils.Constants;
import com.fdl.mangaz.utils.LibraryAdapter;
import com.fdl.mangaz.utils.Manga;
import com.fdl.mangaz.utils.StringUtil;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class MainActivity extends Activity implements OnItemClickListener,OnItemLongClickListener {

	private ActionMode mActionMode;
	private MainActivity me;
	public static LibraryAdapter mainadapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		me=this;
		setContentView(R.layout.library_grid);
		
		GridView gridview = (GridView)findViewById(R.id.library_grid);
		
		if (mainadapter == null)
			mainadapter = new LibraryAdapter(getApplicationContext());
		gridview.setAdapter(mainadapter);
		
		gridview.setOnItemClickListener(this);
		
		gridview.setOnItemLongClickListener(this);
		gridview.setLongClickable(true);
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
	        	intent = new Intent(this,MangaReaderListActivity.class);
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
			Intent intent = new Intent(this,MangaReaderListActivity.class);
        	startActivity(intent);
            return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void onItemClick(AdapterView<?> parent, View v,
             int position, long id) {
		
			if (mActionMode != null)
			{
				mainadapter.setSelectItem(position);
		        mainadapter.notifyDataSetChanged();
			}
			else
			{
				Intent intent = new Intent(this,ChapterViewActivity.class);
				ChapterViewActivity.current_manga_name = ((Manga)parent.getItemAtPosition(position)).getTitle();
				startActivity(intent);
			}
	 }
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


		// Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.library_context_menu, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.delete:
	                dialog_deleteSelectItem();
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	                return false;
	        }
	    }

	    private void dialog_deleteSelectItem() {
	    	
	    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    	    

				@Override
	    	    public void onClick(DialogInterface dialog, int which) {
	    	        switch (which){
	    	        case DialogInterface.BUTTON_POSITIVE:
	    	        	deleteSelectItem(true);
	    	            break;

	    	        case DialogInterface.BUTTON_NEGATIVE:
	    	        	deleteSelectItem(false);
	    	            break;
	    	        }
	    	    }
	    	};

	    	AlertDialog.Builder builder = new AlertDialog.Builder(me);
	    	builder.setMessage("Are you sure, it will delete files also?").setPositiveButton("Yes", dialogClickListener)
	    	    .setNegativeButton("No", dialogClickListener).show(); 	
	    				
		}

	    private void deleteSelectItem(boolean flag) {
	    	if(flag)
	    	{
				ArrayList<Integer> selecteditems = mainadapter.getSelectItems();
				for(Integer index : selecteditems)
				{
					Manga m = mainadapter.getItem(index.intValue());
					//Remove preferences associated to this manga
					SharedPreferences settings = getSharedPreferences(StringUtil.sanitizeFilename(m.getTitle()), 0);
					if(settings != null)
					{
						Editor editor = settings.edit();
						editor.clear().commit();
					}
					//Remove manga if any
					String dir = Constants.DEFAULT_SD_CARD_PATH + "/" + m.getTitle();
					File manga_dir = new File(dir);
					DeleteRecursive(manga_dir);
					//Remove from privatelibrary
					Manga.private_library.removeManga(m.getTitle());
					
				}
	    	}
	    	mainadapter.clearSelect();
		    	
	    }
	    
	    
	    void DeleteRecursive(File fileOrDirectory) {
		    if (fileOrDirectory.isDirectory())
		        for (File child : fileOrDirectory.listFiles())
		            DeleteRecursive(child);

		    fileOrDirectory.delete();
		}
	    
		// Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
	
	public boolean onItemLongClick(android.widget.AdapterView<?> parent, View view, int position, long id)
	{
        if (mActionMode != null) {
            return false;
        }

        // Start the CAB using the ActionMode.Callback defined above
        mActionMode = me.startActionMode(mActionModeCallback);
        mainadapter.setSelectItem(position);
        mainadapter.notifyDataSetChanged();
        return true;
    }

}
