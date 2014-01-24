package ru.omdroid.DebtCalc.Dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.WorkNotification;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DialogDelNotify  extends DialogFragment implements View.OnClickListener {

    private Context context;
    public DialogDelNotify(Context context) {
        this.context = context;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getResources().getString(R.string.dialog_title));
        View view = inflater.inflate(R.layout.dialog_del_notify, null);
        view.findViewById(R.id.butDel).setOnClickListener(this);
        view.findViewById(R.id.butCancel).setOnClickListener(this);

        WorkDB workDB = new WorkDB(context);
        Cursor cursor = workDB.readValueFromDataBase("SELECT * FROM " + DebtCalcDB.TABLE_NOTIFY + " WHERE " + DebtCalcDB.F_ID_DEBT_NOTIFY + " = " + AppData.ID_DEBT);
        cursor.moveToNext();

        TextView tvCountDay = (TextView)view.findViewById(R.id.tvCountDayNot);
        tvCountDay.setText(cursor.getString(cursor.getColumnIndex(DebtCalcDB.F_COUNT_DAY_NOTIFY)));

        Date date = new Date();
        date.setTime(cursor.getLong(cursor.getColumnIndex(DebtCalcDB.F_TIME_START_NOTIFY)));
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("HH:mm");
        TextView tvTimeNotify = (TextView)view.findViewById(R.id.tvTimeNot);
        tvTimeNotify.setText(format.format(date));
        cursor.close();
        workDB.disconnectDataBase();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.butDel:
                WorkNotification workNotification = new WorkNotification(context);
                workNotification.delNotify(AppData.ID_DEBT);
                Toast.makeText(context, context.getResources().getString(R.string.delNotify), Toast.LENGTH_LONG).show();
                dismiss();
                break;
            case R.id.butCancel:
                dismiss();
                break;
            default:
                break;
        }

    }
}
