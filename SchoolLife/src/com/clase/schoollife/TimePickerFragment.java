package com.clase.schoollife;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public  class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		int [] date = getArguments().getIntArray("date");
		Calendar myCalendar = new GregorianCalendar(date[0], date[1], date[2], hourOfDay, minute);
		if (getActivity().getClass().equals(CreateTask.class)) {
			CreateTask ct= (CreateTask) getActivity();
			ct.setDateText(myCalendar);
			ct.setTimeMili(myCalendar.getTime().getTime());
		} else if (getActivity().getClass().equals(TaskActivity.class)) {
			TaskActivity ta= (TaskActivity) getActivity();
			ta.setDateText(myCalendar);
			ta.setTimeMili(myCalendar.getTime().getTime());
		}
	}
}
