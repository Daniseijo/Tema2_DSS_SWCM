package com.clase.schoollife;

import android.app.ActionBar;
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
import android.widget.TextView;

public class SubjectActivity extends ListActivity {
	
	private SLDbAdapter mDbHelper;
	private Long mSubjectId;
	
	private TextView mNameText;
    private TextView mProfessorText;
    private TextView mClassroomText;
	
	private static final int ACTIVITY_EDIT=0;
	private static final int ACTIVITY_TASK=1;
	private static final int ACTIVITY_OPTIONS=2;
	private static final int ACTIVITY_HELP=3;
	
	//private static final int INSERT_ID = Menu.FIRST;
	//private static final int EDIT_ID = Menu.FIRST +1;
	private static final int DELETE_ID = Menu.FIRST + 2;
	private static final int EDIT_TASK_ID= Menu.FIRST + 3;
	
	private static final String TASKS="tasks";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subject);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
		mDbHelper = new SLDbAdapter(this);
		mDbHelper.open();
		
		mNameText= (TextView) findViewById(R.id.view_subject);
		mProfessorText= (TextView) findViewById(R.id.view_professor);
		mClassroomText= (TextView) findViewById(R.id.view_classroom);
		
		mSubjectId= (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(SLDbAdapter.KEY_SUBJECTID);
		if(mSubjectId == null){
			Bundle extras = getIntent().getExtras();
			mSubjectId = extras != null ? extras.getLong(SLDbAdapter.KEY_SUBJECTID) : null;
			Cursor subject = mDbHelper.fetchSubject(mSubjectId);
			actionBar.setTitle(subject.getString(subject.getColumnIndexOrThrow(SLDbAdapter.KEY_ABBREVIATION)));
		}
		populateFields();
	    fillData();
	    registerForContextMenu(getListView());
	}
	
	@SuppressWarnings("deprecation")
	private void populateFields() {
        if (mSubjectId != null) {
            Cursor subject = mDbHelper.fetchSubject(mSubjectId);
            startManagingCursor(subject);
            mNameText.setText(subject.getString(subject.getColumnIndexOrThrow(SLDbAdapter.KEY_NAME)));
            mProfessorText.setText(subject.getString(subject.getColumnIndexOrThrow(SLDbAdapter.KEY_PROFESSOR)));
            mClassroomText.setText(subject.getString(subject.getColumnIndexOrThrow(SLDbAdapter.KEY_CLASSROOM)));
        }
    }
	
	@SuppressWarnings("deprecation")
	private void fillData() {
		Cursor tasksCursor = mDbHelper.fetchAllTasksOfSubject(mSubjectId);
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent i;
	    switch (item.getItemId()) {
	        case R.id.menu_add:
	        	i = new Intent(this, CreateTask.class);
	            startActivityForResult(i, ACTIVITY_TASK);
	            return true;
	        case R.id.menu_settings:
	        	i= new Intent(this, OptionsActivity.class);
	        	i.putExtra(OptionsActivity.FROM, TASKS);
	        	i.putExtra(SLDbAdapter.KEY_SUBJECTID, mSubjectId);
	        	startActivityForResult(i, ACTIVITY_OPTIONS);
	        	return true;
	        case R.id.menu_edit:
	        	i = new Intent(this, CreateSubject.class);
	        	i.putExtra(SLDbAdapter.KEY_SUBJECTID, mSubjectId);
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
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_TASK_ID, 0, R.string.edit);
        menu.add(0, DELETE_ID, 0, R.string.delete);
    }
	
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info;
        switch(item.getItemId()) {
        case EDIT_TASK_ID:
        	info = (AdapterContextMenuInfo) item.getMenuInfo();
        	Intent i = new Intent(this, CreateTask.class);
            i.putExtra(SLDbAdapter.KEY_TASKID, info.id);
            startActivityForResult(i, ACTIVITY_EDIT);
        	return true;
        case DELETE_ID:
        	info = (AdapterContextMenuInfo) item.getMenuInfo();
        	mDbHelper.deleteTask(info.id);
        	fillData();
        	return true;
        }
        return super.onContextItemSelected(item);
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, TaskActivity.class);
        i.putExtra(SLDbAdapter.KEY_TASKID, id);
        startActivityForResult(i, ACTIVITY_TASK);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
