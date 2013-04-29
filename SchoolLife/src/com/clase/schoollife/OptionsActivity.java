package com.clase.schoollife;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class OptionsActivity extends ListActivity {
	public static final String FROM="from";
	private static final String SUBJECTS="subjects";
	private static final String TASKS="tasks";
	
	private String extra_from;
	private SLDbAdapter mDbHelper;
	private Long mSubjectId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.settings);
		mDbHelper = new SLDbAdapter(this);
        mDbHelper.open();
		
		Bundle extras = getIntent().getExtras();
		extra_from= extras.getString(FROM);
		ListView list = (ListView) findViewById(android.R.id.list);
		ArrayList<String> myList = new ArrayList<String>();
		
		if(extra_from.equals(SUBJECTS)){
			myList.add(getString(R.string.delete_database));
		}else if(extra_from.equals(TASKS)){
			myList.add(getString(R.string.delete_tasks));
			mSubjectId=extras.getLong(SLDbAdapter.KEY_SUBJECTID);
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
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	Toast toast;
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		        	if(extra_from.equals(SUBJECTS)){
		            	mDbHelper.deleteAllSubjects();
		            	toast = Toast.makeText(getApplicationContext(), R.string.database_deleted, Toast.LENGTH_SHORT);
		            	toast.show();
		            } else if(extra_from.equals(TASKS)){
		            	mDbHelper.deleteAllTasksOfSubject(mSubjectId);
		            	toast = Toast.makeText(getApplicationContext(), R.string.tasks_deleted, Toast.LENGTH_SHORT);
		            	toast.show();
		            }
		        	finish();
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		        	finish();
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.are_you_sure).setNegativeButton(R.string.no, dialogClickListener).setPositiveButton(R.string.yes, dialogClickListener).show();
	}
}
