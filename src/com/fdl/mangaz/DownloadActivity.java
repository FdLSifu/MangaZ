package com.fdl.mangaz;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class DownloadActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.library_grid);
	
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

}
