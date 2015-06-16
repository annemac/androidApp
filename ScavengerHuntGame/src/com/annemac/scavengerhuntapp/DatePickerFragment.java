package com.annemac.scavengerhuntapp;
import java.util.Calendar;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

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
        StringBuilder task = new StringBuilder();
        task.append(month + 1).append("-").append(day).append("-").append(year)
                .append(" ");
        if (getTag() == "startDatePicker") {
            ((TextView) getActivity().findViewById(R.id.editStartDate))
                    .setText(task);
        }
        if (getTag() == "endDatePicker") {
            ((TextView) getActivity().findViewById(R.id.editEndDate))
                    .setText(task);
        }

    }
}