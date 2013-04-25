package com.clase.schoollife;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
		setTitle(R.string.new_task_title);
		
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
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
