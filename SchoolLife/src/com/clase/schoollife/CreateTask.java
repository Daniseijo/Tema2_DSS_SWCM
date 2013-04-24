package com.clase.schoollife;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;

public class CreateTask extends Activity {
	
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
		setTitle(R.string.app_name);
		
		//R.id. de prueba
		mTitleText= (EditText) findViewById(R.id.edit_subject);
		mExplanationText= (EditText) findViewById(R.id.edit_abbreviation);
		mDateText= (EditText) findViewById(R.id.edit_professor);
		mRevisionDateText= (EditText) findViewById(R.id.edit_classroom);
		mFeelingsText= (EditText) findViewById(R.id.edit_abbreviation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
