package com.clase.schoollife;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateTask extends Activity implements OnItemSelectedListener {
	
	private Long mTaskId;
	private int mTypeInt;
	private EditText mTitleText;
    private EditText mExplanationText;
    private TextView mDateText;
    private Long mSubjectId;
    private boolean created;
    
    private SLDbAdapter mDbHelper;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new SLDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.activity_create_task);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.new_task_title);
		
		Spinner spinner = (Spinner) findViewById(R.id.task_type);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.tasks_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		
		Button changeDate = (Button) findViewById(R.id.button2);
		changeDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
			}
		});
		created= true;
		mTitleText= (EditText) findViewById(R.id.edit_title);
		mExplanationText= (EditText) findViewById(R.id.edit_explanation);
		mDateText= (TextView) findViewById(R.id.edit_date);
		mTypeInt=0;
		mDateText.setText(DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));
		
		mTaskId= (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(SLDbAdapter.KEY_TASKID);
		if(mTaskId == null){
			Bundle extras = getIntent().getExtras();
			try{
				Long tryLong= extras.getLong(SLDbAdapter.KEY_TASKSUBJECT);
			} catch(Exception e){
				created =false;
			}
			if(!created){
				mSubjectId= extras != null ? extras.getLong(SLDbAdapter.KEY_SUBJECTID): null;
			} else{
				mTaskId= extras.getLong(SLDbAdapter.KEY_TASKID);
				mSubjectId=extras.getLong(SLDbAdapter.KEY_TASKSUBJECT);
				actionBar.setTitle(R.string.edit_task);
				populateFields();
			}
		}
		
		Button confirmButton=(Button) findViewById(R.id.button1);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				setResult(RESULT_OK);
				saveState();
				finish();
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private void populateFields() {
        if (mSubjectId != null) {
            Cursor task = mDbHelper.fetchTask(mTaskId);
            startManagingCursor(task);
            mTitleText.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_TITLE)));
            mExplanationText.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_EXPLANATION)));
            mDateText.setText(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_DATE)));
            //poner dato en Spinner
            mTypeInt=task.getColumnIndexOrThrow(SLDbAdapter.KEY_TYPE);
        }
    }
	
	private void showDatePickerDialog(View v){
		DialogFragment newFragment= new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}
	@SuppressWarnings("deprecation")
	private void saveState() {
		int type=mTypeInt;
        String title = mTitleText.getText().toString();
        String explanation= mExplanationText.getText().toString();
        String date= mDateText.getText().toString();
        Long taskSubject= mSubjectId;
        if(!created){
	        if(title!=null){
	        	long id = mDbHelper.createTask(type, title, explanation, date, -1, false, null, false, -1,null, taskSubject);
	        	if (id > 0) {
	        		mTaskId = id;
	        	}
	        }
        } else{
        	Cursor task = mDbHelper.fetchTask(mTaskId);
            startManagingCursor(task);
            double mark = task.getColumnIndexOrThrow(SLDbAdapter.KEY_MARK);
            boolean rev = false;
            if(task.getColumnIndexOrThrow(SLDbAdapter.KEY_REVISION)==1) rev = true;
            String revisionDate= task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_REVISIONDATE));
            boolean comp = false;
            if(task.getColumnIndexOrThrow(SLDbAdapter.KEY_COMPLETED)==1) comp = true;
            int feelingsStars = task.getColumnIndexOrThrow(SLDbAdapter.KEY_FEELINGSSTARS);
            String feelings = task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_FEELINGS));
            mDbHelper.updateTask(mTaskId, type,title, explanation, date,mark,rev,revisionDate, comp, feelingsStars, feelings, taskSubject);
        }
    }
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(SLDbAdapter.KEY_TASKID, mTaskId);
    }
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mTypeInt=pos;
    }

	@Override
    public void onNothingSelected(AdapterView<?> parent) {
		// Nothing
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task, menu);
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
