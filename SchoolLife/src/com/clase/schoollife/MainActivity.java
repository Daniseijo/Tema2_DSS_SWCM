package com.clase.schoollife;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {
	private static final int ACTIVITY_CREATE=0;
	
	private SLDbAdapter mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDbHelper = new SLDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
	}
	
	private void fillData() {
		Cursor subjectsCursor = mDbHelper.fetchAllSubjects();
        startManagingCursor(subjectsCursor);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{SLDbAdapter.KEY_NAME};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter subjects= new SimpleCursorAdapter(this, R.layout.activity_main, subjectsCursor, from, to);
        setListAdapter(subjects);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_add:
	            newSubject();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void newSubject() {
        Intent i = new Intent(this, CreateSubject.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

}
