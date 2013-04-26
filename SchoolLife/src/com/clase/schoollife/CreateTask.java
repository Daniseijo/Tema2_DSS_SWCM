package com.clase.schoollife;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateTask extends Activity implements OnItemSelectedListener {
	
	private Long mTaskId;
	private int mTypeInt;
	private EditText mTitleText;
    private EditText mExplanationText;
    private EditText mDateText;
    private double mMarkInt;
    private boolean mRevision;
    private EditText mRevisionDateText;
    private boolean mCompleted;
    private int mFeelingsStarsInt;
    private EditText mFeelingsText;
    
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
		
		mTaskId= (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(SLDbAdapter.KEY_TASKID);
		if(mTaskId == null){
			Bundle extras = getIntent().getExtras();
			mTaskId = extras != null ? extras.getLong(SLDbAdapter.KEY_TASKID) : null;
			Cursor task = mDbHelper.fetchTask(mTaskId);
			actionBar.setTitle(task.getString(task.getColumnIndexOrThrow(SLDbAdapter.KEY_TITLE)));
		}
		
		//R.id. de prueba
		mTitleText= (EditText) findViewById(R.id.edit_subject);
		mExplanationText= (EditText) findViewById(R.id.edit_abbreviation);
		mDateText= (EditText) findViewById(R.id.edit_professor);
		mRevisionDateText= (EditText) findViewById(R.id.edit_classroom);
		mFeelingsText= (EditText) findViewById(R.id.edit_abbreviation);
		Spinner spinner = (Spinner) findViewById(R.id.task_type);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.tasks_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // Dependiendo de lo seleccionado se activan unos u otros.
		Log.i("Posición ID", ""+ pos);
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
