package ru.omdroid.DebtCalc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Receiver.Receiver;

import java.util.Calendar;


public class WorkNotification {
    Context context;

    public WorkNotification(Context context){
       this.context = context;
    }

    public void addNotify(String idDebt){

        WorkDB workDB = new WorkDB(context);
        workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_NOTIFY + " (" + DebtCalcDB.F_ID_DEBT_NOTIFY + ") VALUES ('" + idDebt + "')");
        workDB.disconnectDataBase();

        String ADD_NOTIFY = "ADD_NOTIFY";
        Intent intent = new Intent(context, Receiver.class);
        intent.setAction(ADD_NOTIFY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 3600 * 720 * 1000, pendingIntent);
        Log.v("WorkNotification. ", "Напоминание добавлено!");
    }

    public void delNotify(){
        /*Intent intent = new Intent(context, test.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);*/
    }
}
