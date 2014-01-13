package ru.omdroid.DebtCalc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Receiver.Receiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class WorkNotification {
    Context context;
    final String ADD_NOTIFY = "ADD_NOTIFY";
    public WorkNotification(Context context){
       this.context = context;
    }

    public void addNotify(String idDebt, int countDay, long dateLong){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime(dateLong);
        String timeIteration = simpleDateFormat.format(date);

        WorkDB workDB = new WorkDB(context);
        Cursor countAlarm = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.F_ID_ALARM_NOTIFY + " FROM " + DebtCalcDB.TABLE_NOTIFY + " WHERE " + DebtCalcDB.F_TIME_NOTIFY + " = '" + timeIteration + "'");
        int numberAlarm;
        if (countAlarm.getCount() == 0)
            numberAlarm = generateNumCredit();
        else{
            countAlarm.moveToNext();
            numberAlarm = countAlarm.getInt(countAlarm.getColumnIndex(DebtCalcDB.F_ID_ALARM_NOTIFY));
        }
        workDB.insertValueToTablePayment("INSERT INTO " + DebtCalcDB.TABLE_NOTIFY + " (" +
                                            DebtCalcDB.F_ID_DEBT_NOTIFY + ", " +
                                            DebtCalcDB.F_ID_ALARM_NOTIFY + ", " +
                                            DebtCalcDB.F_COUNT_DAY_NOTIFY + ", " +
                                            DebtCalcDB.F_TIME_NOTIFY + ", " +
                                            DebtCalcDB.F_TIME_START_NOTIFY +
                                            ") VALUES ('" + idDebt +  "', '" + numberAlarm + "', '" + countDay + "', '" + timeIteration + "', '" + dateLong + "')");
        workDB.disconnectDataBase();
        createAlarm(dateLong, numberAlarm);
        /*Intent intent = new Intent(context, Receiver.class);
        intent.setAction(ADD_NOTIFY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(), 86400 * 1000, pendingIntent);
        Toast.makeText(context, "Напоминание установлено!", Toast.LENGTH_LONG).show();*/
    }

    public void delNotify(String idDebt){
        WorkDB workDB = new WorkDB(context);
        Cursor cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.F_ID_ALARM_NOTIFY + " FROM " + DebtCalcDB.TABLE_NOTIFY + " WHERE " + DebtCalcDB.F_ID_DEBT_NOTIFY + " = '" + idDebt + "'");
        cursor.moveToNext();
        int numberAlarm = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_ID_ALARM_NOTIFY));
        cursor.close();
        workDB.deleteData("DELETE FROM " + DebtCalcDB.TABLE_NOTIFY + " WHERE " + DebtCalcDB.F_ID_DEBT_NOTIFY + " = '" + idDebt + "'");
        int countNotification = workDB.countDataInDataBase("SELECT * FROM " + DebtCalcDB.TABLE_NOTIFY + " WHERE " + DebtCalcDB.F_ID_ALARM_NOTIFY + " = '" + numberAlarm + "'");
        workDB.disconnectDataBase();

        if (countNotification == 0){
            Intent intent = new Intent(context, Receiver.class);
            intent.setAction(ADD_NOTIFY);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            alarmManager.cancel(PendingIntent.getBroadcast(context, numberAlarm, intent, 0));
        }
        Toast.makeText(context, "Напоминание удалено!", Toast.LENGTH_LONG).show();
    }

    public void createAlarm(long date, int numberAlarm){
        Intent intent = new Intent(context, Receiver.class);
        intent.setAction(ADD_NOTIFY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, numberAlarm, intent, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date, 86400 * 1000, pendingIntent);
        Toast.makeText(context, "Напоминание установлено!", Toast.LENGTH_LONG).show();
    }

    private int generateNumCredit(){
        Random random = new Random();
        return 100 + random.nextInt(899);
    }
}
