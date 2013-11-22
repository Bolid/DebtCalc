package ru.omdroid.DebtCalc.Dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.WorkNotification;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogNotify  extends DialogFragment implements View.OnClickListener {
    private Date date;
    private int countDay = 1;
    private Context context;

    public DialogNotify(Context context) {
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.app_name));
        View view = inflater.inflate(R.layout.dialog_notify, null);
        view.findViewById(R.id.butNotOK).setOnClickListener(this);
        view.findViewById(R.id.butNotCancel).setOnClickListener(this);
        view.findViewById(R.id.tvTimeNot).setOnClickListener(this);
        date = new Date();
        final TextView tvCountDay = (TextView)view.findViewById(R.id.tvCountDayNot);
        final TextView tvTimeNot = (TextView)view.findViewById(R.id.tvTimeNot);

        SeekBar seekBar = (SeekBar)view.findViewById(R.id.sbCountDay);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                countDay = i + 1;
                tvCountDay.setText(String.valueOf(i+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        tvTimeNot.setText(format.format(date));
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvTimeNot:
                final DialogFragment dialogFragment = new TimeNotifyPickerFragment((TextView)view, date);
                dialogFragment.show(getFragmentManager(), "");
                break;
        }
        switch (view.getId()){
            case R.id.butNotOK:
                WorkNotification workNotification = new WorkNotification(context);
                workNotification.addNotify(AppData.ID_DEBT, countDay, date);
                break;
        }
    }
}
