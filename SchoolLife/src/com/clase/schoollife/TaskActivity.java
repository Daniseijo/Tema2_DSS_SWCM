package com.clase.schoollife;

import java.text.DateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class TaskActivity extends Activity {
	
	private SLDbAdapter mDbHelper;
	private Long mTaskId;
	private Long mSubjectId;
	
	private long mTime;
	private int mTypeInt;
	private TextView mTypeText;
	private String mTitleText;
	//private TextView mTitleText;
    private TextView mExplanationText;
    private TextView mDateText;
	private EditText mMarkText;
	private boolean mRevision;
	private TextView mRevisionDate;
	private boolean mCompleted;
	private int mFeelingsStars;
	private EditText mFeelingsText;
	
	
	private static final int ACTIVITY_EDIT=0;
	private static final int ACTIVITY_HELP=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    mDbHelper= new SLDbAdapter(this);
	    mDbHelper.open();
	    //Lo ponemos en el t�tulo de la Task
	    mTypeText= (TextView) findViewById(R.id.view_type);
		mExplanationText= (TextView) findViewById(R.id.view_explanation);
		mDateText= (TextView) findViewById(R.id.view_date);
		mMarkText=(EditText) findViewById(R.id.edit_mark);
		mRevisionDate = (TextView) findViewById(R.id.view_revision_date);
		mFeelingsText = (EditText) findViewById(R.id.edit_feelings);
		
	    mTaskId= (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(SLDbAdapter.KEY_TASKID);
		if(mTaskId == null){
			Bundle extras = getIntent().getExtras();
			mTaskId = extras != null ? extras.getLong(SLDbAdapter.KEY_TASKID) : null;
			mSubjectId= extras != null ? extras.getLong(SLDbAdapter.KEY_TASKSUBJECT) : null;
			Cursor task = mDbHelper.fetchTask(mTaskId);
			mTitleText = task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_TITLE));
			actionBar.setTitle(mTitleText);
			
		}
		
		populateFields();
	}
	
	@SuppressWarnings("deprecation")
	private void populateFields() {
        if (mTaskId != null) {
            Cursor task = mDbHelper.fetchTask(mTaskId);
            startManagingCursor(task);
            mTypeInt = task.getColumnIndexOrThrow(SLDbAdapter.KEY_TYPE);
            if(mTypeInt==0){
            	mTypeText.setText(R.string.exam);
            } else{
            	mTypeText.setText(R.string.exercise);
            }
            mExplanationText.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_EXPLANATION)));
            
            mTime = task.getLong(task.getColumnIndexOrThrow(SLDbAdapter.KEY_DATE));
            mDateText.setText(DateFormat.getDateInstance().format(new Date(mTime)));
            
    		mMarkText.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_MARK)));
    		mRevisionDate.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_REVISIONDATE)));
    		mFeelingsText.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_FEELINGS)));
        }
    }
	
	private void saveState() {
		int type=mTypeInt;
        String title = mTitleText;
        String explanation= mExplanationText.getText().toString();
        Long date= mTime;
        Long taskSubject= mSubjectId;
        int mark = Integer.parseInt(mMarkText.getText().toString());
        String revisionDate= mRevisionDate.getText().toString();
        String feelings = mFeelingsText.getText().toString();
        
        mDbHelper.updateTask(mTaskId, type, title, explanation, date, mark, true, revisionDate, true, 5, feelings, taskSubject);
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
