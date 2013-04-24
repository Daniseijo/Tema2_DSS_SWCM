package com.clase.schoollife;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.SimpleCursorAdapter;
import android.app.ListActivity;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SubjectActivity extends ListActivity {
	
	private SLDbAdapter mDbHelper;
	private static final int INSERT_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST + 1;
	private static final int DELETE_ID = Menu.FIRST + 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subject);
		setTitle(R.string.app_name);
		Bundle extras = getIntent().getExtras();
		long subjectId=extras.getLong(SLDbAdapter.KEY_SUBJECTID);
		mDbHelper = new SLDbAdapter(this);
	    mDbHelper.open();
	    fillData();
	    registerForContextMenu(getListView());
	}
	
	private void fillData() {
		Cursor tasksCursor = mDbHelper.fetchAllTasks();
        startManagingCursor(tasksCursor);
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{SLDbAdapter.KEY_TITLE};
        // and an array of the fields we want to bind those fields to (in this case just name_entry)
        int[] to = new int[]{R.id.name_entry};
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter tasks= new SimpleCursorAdapter(this, R.layout.items_row, tasksCursor, from, to);
        setListAdapter(tasks);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.subject, menu);
		return true;
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_ID, 0, R.string.edit);
        menu.add(0, DELETE_ID, 0, R.string.delete);
    }

}
