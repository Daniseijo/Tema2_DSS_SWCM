package com.clase.schoollife;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.database.Cursor;
import android.widget.Button;
import android.widget.EditText;

public class CreateSubject extends Activity {
	
	private Long mSubjectId;
	private EditText mNameText;
    private EditText mAbbreviationText;
    private EditText mProfessorText;
    private EditText mClassroomText;
    private SLDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new SLDbAdapter(this);
		mDbHelper.open();
		
		setContentView(R.layout.activity_create);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.new_subject_title);
		
		mNameText= (EditText) findViewById(R.id.edit_subject);
		mAbbreviationText= (EditText) findViewById(R.id.edit_abbreviation);
		mProfessorText= (EditText) findViewById(R.id.edit_professor);
		mClassroomText= (EditText) findViewById(R.id.edit_classroom);
		
		Button confirmButton=(Button) findViewById(R.id.button1);

		mSubjectId= (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(SLDbAdapter.KEY_SUBJECTID);
		if(mSubjectId == null){
			Bundle extras = getIntent().getExtras();
			mSubjectId = extras != null ? extras.getLong(SLDbAdapter.KEY_SUBJECTID) : null;
		}
		
		populateFields();
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
            Cursor subject = mDbHelper.fetchSubject(mSubjectId);
            startManagingCursor(subject);
            mNameText.setText(subject.getString(subject.getColumnIndexOrThrow(SLDbAdapter.KEY_NAME)));
            mAbbreviationText.setText(subject.getString(subject.getColumnIndexOrThrow(SLDbAdapter.KEY_ABBREVIATION)));
            mProfessorText.setText(subject.getString(subject.getColumnIndexOrThrow(SLDbAdapter.KEY_PROFESSOR)));
            mClassroomText.setText(subject.getString(subject.getColumnIndexOrThrow(SLDbAdapter.KEY_CLASSROOM)));
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(SLDbAdapter.KEY_SUBJECTID, mSubjectId);
    }
	
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

	private void saveState() {
        String name = mNameText.getText().toString();
        String abbreviation = mAbbreviationText.getText().toString();
        String professor= mProfessorText.getText().toString();
        String classroom= mClassroomText.getText().toString();
        if(name!=null){
	        if (mSubjectId == null) {
	            long id = mDbHelper.createSubject(name, abbreviation, professor, classroom);
	            if (id > 0) {
	                mSubjectId = id;
	            }
	        } else {
	            mDbHelper.updateSubject(mSubjectId, name, abbreviation, professor, classroom);
	        }
        }
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
