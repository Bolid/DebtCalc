package ru.omdroid.DebtCalc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Receiver.Receiver;

import java.util.Calendar;
import java.util.Date;


public class WorkNotification {
    Context context;
    final String ADD_NOTIFY = "ADD_NOTIFY";
    public WorkNotification(Context context){
       this.context = context;
    }

    public void addNotify(String idDebt, int countDay, Date date){

        WorkDB workDB = new WorkDB(context);
        workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_NOTIFY + " (" +
                                            DebtCalcDB.F_ID_DEBT_NOTIFY + ", " +
                                            DebtCalcDB.F_COUNT_DAY_NOTIFY +
                                            ") VALUES ('" + idDebt + "', '"+ countDay + "')");
        workDB.disconnectDataBase();

        Intent intent = new Intent(context, Receiver.class);
        intent.setAction(ADD_NOTIFY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(), 86400 * 1000, pendingIntent);
        Toast.makeText(context, "Напоминание установлено!", Toast.LENGTH_LONG).show();
    }

    public void delNotify(String idDebt){
        WorkDB workDB = new WorkDB(context);

        workDB.deleteData("DELETE FROM " + DebtCalcDB.TABLE_NOTIFY + " WHERE " + DebtCalcDB.F_ID_DEBT_NOTIFY + " = '" + idDebt + "'");
        int countNotification = workDB.countDataInDataBase("SELECT * FROM " + DebtCalcDB.TABLE_NOTIFY);
        workDB.disconnectDataBase();

        if (countNotification == 0){
            Intent intent = new Intent(context, Receiver.class);
            intent.setAction(ADD_NOTIFY);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            alarmManager.cancel(PendingIntent.getBroadcast(context, 0, intent, 0));
        }
        Toast.makeText(context, "Напоминание удалено!", Toast.LENGTH_LONG).show();
    }
}
