package com.clase.schoollife;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OptionsActivity extends Activity {
	public static final String FROM="from";
	private static final String SUBJECTS="subjects";
	private static final String TASKS="tasks";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.settings);
		
		Bundle extras = getIntent().getExtras();
		String extra_from= extras.getString(FROM);
		ListView list = (ListView) findViewById(R.id.options_list);
		ArrayList<String> myList = new ArrayList<String>();
		
		if(extra_from.equals(SUBJECTS)){
			myList.add(getString(R.string.delete_database));
		}else if(extra_from.equals(TASKS)){
			myList.add(getString(R.string.delete_tasks));
		}
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.items_row, myList);
		list.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	finish(); 
		        return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
