package com.clase.schoollife;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {
	private static final int ACTIVITY_EDIT=0;
	private static final int ACTIVITY_SUBJECT=1;
	private static final int ACTIVITY_OPTIONS=2;
	private static final int ACTIVITY_HELP=3;
	
	private static final int INSERT_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST + 1;
	private static final int DELETE_ID = Menu.FIRST + 2;
	 
	private static final String SUBJECTS="subjects";
	
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
	
	@SuppressWarnings("deprecation")
	private void fillData() {
		Cursor subjectsCursor = mDbHelper.fetchAllSubjects();
        startManagingCursor(subjectsCursor);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{SLDbAdapter.KEY_NAME};
        // and an array of the fields we want to bind those fields to (in this case just name_entry)
        int[] to = new int[]{R.id.name_entry};
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter subjects= new SimpleCursorAdapter(this, R.layout.items_row, subjectsCursor, from, to);
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
		Intent i;
	    switch (item.getItemId()) {
	        case R.id.menu_add:
	        	i = new Intent(this, CreateSubject.class);
	            startActivityForResult(i, ACTIVITY_EDIT);
	            return true;
	        case R.id.menu_settings:
	        	i=new Intent(this, OptionsActivity.class);
	        	//maybe
	        	i.putExtra(OptionsActivity.FROM,SUBJECTS);
	        	startActivityForResult(i,ACTIVITY_OPTIONS);
	        	return true;
	        case R.id.menu_help:
	        	i= new Intent(this, HelpActivity.class);
	        	startActivityForResult(i,ACTIVITY_HELP);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, SubjectActivity.class);
        i.putExtra(SLDbAdapter.KEY_SUBJECTID, id);
        startActivityForResult(i, ACTIVITY_SUBJECT);
    }
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_ID, 0, R.string.edit);
        menu.add(0, DELETE_ID, 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info;
        switch(item.getItemId()) {
        case EDIT_ID:
        	info = (AdapterContextMenuInfo) item.getMenuInfo();
        	Intent i = new Intent(this, CreateSubject.class);
            i.putExtra(SLDbAdapter.KEY_SUBJECTID, info.id);
            startActivityForResult(i, ACTIVITY_EDIT);
        	return true;
        case DELETE_ID:
        	info = (AdapterContextMenuInfo) item.getMenuInfo();
        	mDbHelper.deleteSubject(info.id);
        	fillData();
        	return true;
        }
        return super.onContextItemSelected(item);
    }
}