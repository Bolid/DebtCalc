package ru.omdroid.DebtCalc.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Forms.ListDebt;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.WorkNotification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Receiver extends BroadcastReceiver {
    final String ADD_NOTIFY = "ADD_NOTIFY";
    final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";
    final String TAG = "ru.omdroid.DebtCalc.Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "Сработал!");

        if (action.equals(ADD_NOTIFY)){
            runNotification(context);
        }

        if (action.equalsIgnoreCase(BOOT_COMPLETE)) {
            checkNotifyAfterRunDevice(context);
        }
    }

    /*++Запуск напоминания*/
    private void runNotification(Context context){
        String idDebt;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String currentTime = simpleDateFormat.format(date);
        String iterationTime;
        ArrayList <String> goal = new ArrayList<String>();
        ArrayList <String> payment = new ArrayList<String>();
        ArrayList <Long> datePayment = new ArrayList<Long>();
        int countDay;
        int countNotify = 0;
        Calendar currentDate = Calendar.getInstance();
        Calendar paymentDate = Calendar.getInstance();
        paymentDate.set(Calendar.YEAR, 1980);

        WorkDB workDB = new WorkDB(context);
        Cursor cursorNotify = workDB.readValueFromDataBase("SELECT * FROM " + DebtCalcDB.TABLE_NOTIFY);    //Получаем записи из таблицы notification
        while (cursorNotify.moveToNext()){                                                                 //Пробегаемся по полученному курсору
            idDebt = cursorNotify.getString(cursorNotify.getColumnIndex(DebtCalcDB.F_ID_DEBT_NOTIFY));
            countDay = cursorNotify.getInt(cursorNotify.getColumnIndex(DebtCalcDB.F_COUNT_DAY_NOTIFY));
            Log.v(TAG, "Кредит: " + idDebt +  ". Количество дней: " + countDay);
            Cursor cursorPayment = workDB.readValueFromDataBase("SELECT " +                                 //Получаем платеж и дату по idDebt
                                                                    DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +
                                                                    DebtCalcDB.FIELD_DATE_LONG_PAYMENTS  +" FROM " +
                                                                    DebtCalcDB.TABLE_PAYMENTS + " WHERE (" +
                                                                    DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + idDebt + "' AND " +
                                                                    DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");

            if (cursorPayment.moveToNext()){
                paymentDate.setTimeInMillis(cursorPayment.getLong(cursorPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
            }
            paymentDate.set(Calendar.DATE, paymentDate.get(Calendar.DATE) - countDay);
           // date.setTime(paymentDate.getTimeInMillis());
            iterationTime = cursorNotify.getString(cursorNotify.getColumnIndex(DebtCalcDB.F_TIME_NOTIFY));

            Cursor cursorGoal = workDB.readValueFromDataBase("SELECT " +                                   //Получаем цель кредита
                    DebtCalcDB.FIELD_TYPE_DEBT +
                    " FROM " +
                    DebtCalcDB.TABLE_CREDITS + " WHERE (" +
                    DebtCalcDB.FIELD_ID_DEBT + " = '" + idDebt + "' AND " +
                    DebtCalcDB.FIELD_PAID_DEBT + " = '0')");
            cursorGoal.moveToNext();

            Log.v(TAG, "currentDate!" + currentDate.get(Calendar.DATE) + "." + currentDate.get(Calendar.MONTH) + "." + currentDate.get(Calendar.YEAR));
            Log.v(TAG, "paymentDate!" + paymentDate.get(Calendar.DATE) + "." + paymentDate.get(Calendar.MONTH) + "." + paymentDate.get(Calendar.YEAR));
            if ((currentDate.get(Calendar.DATE) == paymentDate.get(Calendar.DATE)) & (currentDate.get(Calendar.MONTH) == paymentDate.get(Calendar.MONTH)) & (currentDate.get(Calendar.YEAR) == paymentDate.get(Calendar.YEAR)) & currentTime.equals(iterationTime))
            {
                date.setTime(cursorPayment.getLong(cursorPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
                payment.add(countNotify, cursorPayment.getString(cursorPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));       //Записываем платеж в лист
                goal.add(countNotify, cursorGoal.getString(cursorGoal.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)));          //Записываем цель в лист
                datePayment.add(countNotify, cursorPayment.getLong(cursorPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
                countNotify++;
            }
            cursorPayment.close();
            cursorGoal.close();
        }

        if (payment.size() != 0)
            if (payment.size() > 1)
                    createNotification(context, createSeveralNotify(payment, goal, datePayment));
                //Toast.makeText(context, createSeveralNotify(payment, goal, date), Toast.LENGTH_LONG).show();
            else
                createNotification(context, createOneNotify(payment, goal, datePayment));
                //Toast.makeText(context, createOneNotify(payment, goal, date), Toast.LENGTH_LONG).show();;
    }

    private String createSeveralNotify(ArrayList<String> payment, ArrayList<String> goal, ArrayList<Long> datePayment){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy");
        Date date = new Date();
        String textNotify = "Напоминаем о предстоящих платежах по кредитам:";
        for (int i = 0; i < payment.size(); i++){
            date.setTime(datePayment.get(i));
            textNotify = textNotify + "\n";
            textNotify = textNotify + Integer.valueOf(i+1) + ") " + goal.get(i) + ". " + simpleDateFormat.format(date) + ", в размере " + payment.get(i);
        }
        Log.v(TAG, textNotify);
        return textNotify;
    }

    private String createOneNotify(ArrayList<String> payment, ArrayList<String> goal, ArrayList<Long> datePayment){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy");
        Date date = new Date();
        date.setTime(datePayment.get(0));
        String textNotify = "Напоминаем о предстоящем " + simpleDateFormat.format(date) + " платеже по кредиту за ";
        for (int i = 0; i < payment.size(); i++){
            textNotify = textNotify + " " +goal.get(i) + " в размере " + payment.get(i);
        }
        Log.v(TAG, textNotify);
        return textNotify;
    }

    private void createNotification(Context context, String textNotify){
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "Внимание! Оплата кредита";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.notify_layout);
        remoteViews.setTextViewText(R.id.notifyText, textNotify);
        CharSequence contentTitle = "DebtCalc";
        Intent notificationIntent = new Intent(context, ListDebt.class);

        notification.contentIntent = PendingIntent.getActivity(context, 2908, notificationIntent, 0);
        notification.contentView = remoteViews;
        //notification.setLatestEventInfo(context, contentTitle, textNotify, contentIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(0, notification);
    }
    /*--Запуск напоминания*/

    /*++Проверка после запуска телефона*/
    private void checkNotifyAfterRunDevice(Context context){
        WorkDB workDB = new WorkDB(context);
        Cursor cursor = workDB.readValueFromDataBase("SELECT * FROM " + DebtCalcDB.TABLE_NOTIFY);
        long date;
        int numberAlarm;
        while (cursor.moveToNext()){
            date = cursor.getLong(cursor.getColumnIndex(DebtCalcDB.F_TIME_START_NOTIFY));
            numberAlarm = cursor.getInt(cursor.getColumnIndex(DebtCalcDB.F_ID_ALARM_NOTIFY));
            WorkNotification workNotification = new WorkNotification(context);
            workNotification.createAlarm(date, numberAlarm);
        }

    }
}   /*--Проверка после запуска телефона*/
