package com.clase.schoollife;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;

public class TaskActivity extends Activity {
	
	private SLDbAdapter mDbHelper;
	private Long mTaskId;
	
	private static final int ACTIVITY_EDIT=0;
	private static final int ACTIVITY_HELP=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    mTaskId= (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(SLDbAdapter.KEY_TASKID);
		if(mTaskId == null){
			Bundle extras = getIntent().getExtras();
			mTaskId = extras != null ? extras.getLong(SLDbAdapter.KEY_TASKID) : null;
			Cursor task = mDbHelper.fetchTask(mTaskId);
			actionBar.setTitle(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_TITLE)));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent i;
	    switch (item.getItemId()) {
	        case R.id.menu_edit:
	        	i = new Intent(this, CreateTask.class);
	        	i.putExtra(SLDbAdapter.KEY_TASKID, mTaskId);
	            startActivityForResult(i, ACTIVITY_EDIT);
	            return true;
	        case R.id.menu_help:
	        	i= new Intent(this, HelpActivity.class);
	        	startActivityForResult(i,ACTIVITY_HELP);
	        	return true;
	        case android.R.id.home:
	        	finish(); 
		        return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
