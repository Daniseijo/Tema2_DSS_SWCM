package com.clase.schoollife;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class TaskActivity extends Activity {
	
	private SLDbAdapter mDbHelper;
	private Long mTaskId;
	private Long mSubjectId;
	
	private long mTime;
	private long mRevisionTime;
	private int mTypeInt;
	private TextView mTypeText;
	private String mTitleText;
    private TextView mExplanationText;
    private TextView mDateText;
	private EditText mMarkText;
	private CheckBox mRevisionCheck;
	private TextView mRevisionDate;
	private CheckBox mCompletedCheck;
	private RatingBar mFeelingsStarsBar;
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
	    mTypeText= (TextView) findViewById(R.id.view_type);
		mExplanationText= (TextView) findViewById(R.id.view_explanation);
		mDateText= (TextView) findViewById(R.id.view_date);
		mMarkText=(EditText) findViewById(R.id.edit_mark);
		mRevisionCheck=(CheckBox) findViewById(R.id.edit_revision);
		mCompletedCheck=(CheckBox) findViewById(R.id.edit_completed);
		mRevisionDate = (TextView) findViewById(R.id.view_revision_date);
		mFeelingsText = (EditText) findViewById(R.id.edit_feelings);
		mFeelingsStarsBar= (RatingBar) findViewById(R.id.feelingsStars);
		mFeelingsStarsBar.setNumStars(5);
		
		Button changeDate = (Button) findViewById(R.id.button2);
		changeDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
			}
		});
		
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
            int completed= task.getInt(task.getColumnIndexOrThrow(SLDbAdapter.KEY_COMPLETED));
            if(completed==1){mCompletedCheck.setChecked(true);} else{mCompletedCheck.setChecked(false);}
            int revision= task.getInt(task.getColumnIndexOrThrow(SLDbAdapter.KEY_REVISION));
            if(revision==1){mRevisionCheck.setChecked(true);} else{mRevisionCheck.setChecked(false);}
    		mMarkText.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_MARK)));
    		mRevisionDate.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_REVISIONDATE)));
    		mFeelingsText.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_FEELINGS)));
    		float stars = task.getInt(task.getColumnIndexOrThrow(SLDbAdapter.KEY_FEELINGSSTARS));
    		mFeelingsStarsBar.setRating(stars);
    }
	
	@Override
	protected void onStop() {
		super.onStop();
		saveState();
	}
	
	public void setTimeMili(long time){
		mRevisionTime=time;
	}
	
    public void setDateText (Calendar cal) {
    	mRevisionDate.setText(DateFormat.getDateTimeInstance().format(cal.getTime()));
    }
    
    private void showDatePickerDialog(View v){
		DialogFragment newFragment= new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
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
        boolean revision=mRevisionCheck.isChecked();
        boolean completed=mCompletedCheck.isChecked();
        float feelingsStars= mFeelingsStarsBar.getRating();
        mDbHelper.updateTask(mTaskId, type, title, explanation, date, mark, revision, revisionDate, completed, feelingsStars, feelings, taskSubject);
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
