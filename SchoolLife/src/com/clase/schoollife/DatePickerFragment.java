package com.clase.schoollife;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		showTimePickerDialog(view, year, month, day);
	}
	
	public void showTimePickerDialog(View v, int year, int month, int day) {
		int[] date = {year , month, day};
	    DialogFragment newFragment = new TimePickerFragment();
	    Bundle newBundle = new Bundle();
	    newBundle.putIntArray("date", date);
	    newFragment.setArguments(newBundle);
	    newFragment.show(getFragmentManager(), "timePicker");
	}
}