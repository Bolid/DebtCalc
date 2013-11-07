package ru.omdroid.DebtCalc.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import ru.omdroid.DebtCalc.AppData;

import java.util.Calendar;
import java.util.Date;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    TextView textView;
    Calendar calendarConst;
    Calendar calendar;
    public DatePickerFragment(TextView textView, Calendar calendarConst, Calendar calendar){
        this.textView = textView;
        this.calendarConst = calendarConst;
        this.calendar = calendar;
    }
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

    public void onDateSet(DatePicker view, int year, int month, int day) {
        textView.setText(String.valueOf(day)+"."+String.valueOf(month + 1)+"."+String.valueOf(year));
        calendar.set(year, month, day);
        AppData appData = new AppData();
        appData.setDate(calendar.getTimeInMillis());
        calendarConst.set(year, month, day);
    }
}
