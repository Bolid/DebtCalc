package ru.omdroid.DebtCalc.Dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeNotifyPickerFragment  extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    TextView tvTimeNot;
    Date date;

    public TimeNotifyPickerFragment(TextView tvCountDay, Date date){
        this.tvTimeNot = tvCountDay;
        this.date = date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int hour = date.getHours();
        int minute = date.getMinutes();

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        date.setHours(hour);
        date.setMinutes(minute);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        tvTimeNot.setText(format.format(date));
    }
}
