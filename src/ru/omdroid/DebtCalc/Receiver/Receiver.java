package ru.omdroid.DebtCalc.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.Forms.ListDebt;
import ru.omdroid.DebtCalc.Forms.MainNew;
import ru.omdroid.DebtCalc.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Receiver extends BroadcastReceiver {
    final String ADD_NOTIFY = "ADD_NOTIFY";
    final String TAG = "ru.omdroid.DebtCalc.Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ADD_NOTIFY)){
            checkNotification(context);
        }
    }

    private void checkNotification(Context context){
        String idDebt;
        Date date = new Date();
        ArrayList <String> goal = new ArrayList<String>();
        ArrayList <String> payment = new ArrayList<String>();

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

            cursorPayment.moveToNext();
            paymentDate.setTimeInMillis(cursorPayment.getLong(cursorPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
            date.setTime(cursorPayment.getLong(cursorPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
            paymentDate.set(Calendar.DATE, paymentDate.get(Calendar.DATE) - countDay);

            Cursor cursorGoal = workDB.readValueFromDataBase("SELECT " +                                   //Получаем цель кредита
                    DebtCalcDB.FIELD_TYPE_DEBT +
                    " FROM " +
                    DebtCalcDB.TABLE_CREDITS + " WHERE (" +
                    DebtCalcDB.FIELD_ID_DEBT + " = '" + idDebt + "' AND " +
                    DebtCalcDB.FIELD_PAID_DEBT + " = '0')");
            cursorGoal.moveToNext();

            Log.v(TAG, "currentDate!" + currentDate.get(Calendar.DATE) + "." + currentDate.get(Calendar.MONTH) + "." + currentDate.get(Calendar.YEAR));
            Log.v(TAG, "paymentDate!" + paymentDate.get(Calendar.DATE) + "." + paymentDate.get(Calendar.MONTH) + "." + paymentDate.get(Calendar.YEAR));
            if ((currentDate.get(Calendar.DATE) == paymentDate.get(Calendar.DATE)) & (currentDate.get(Calendar.MONTH) == paymentDate.get(Calendar.MONTH)) & (currentDate.get(Calendar.YEAR) == paymentDate.get(Calendar.YEAR)))
            {
                payment.add(countNotify, cursorPayment.getString(cursorPayment.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));       //Записываем платеж в лист
                goal.add(countNotify, cursorGoal.getString(cursorGoal.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)));          //Записываем цель в лист
                countNotify++;
            }
            cursorPayment.close();
            cursorGoal.close();
        }

        if (payment.size() != 0)
            if (payment.size() > 1)
                    createNotification(context, createSeveralNotify(payment, goal, date));
                //Toast.makeText(context, createSeveralNotify(payment, goal, date), Toast.LENGTH_LONG).show();
            else
                createNotification(context, createOneNotify(payment, goal, date));
                //Toast.makeText(context, createOneNotify(payment, goal, date), Toast.LENGTH_LONG).show();;
    }

    private String createSeveralNotify(ArrayList<String> payment, ArrayList<String> goal, Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy");
        String textNotify = "Напоминаем о предстоящих  " + simpleDateFormat.format(date) + "  платежах по кредитам за:";
        for (int i = 0; i < payment.size(); i++){
            textNotify = textNotify + "\n";
            textNotify = textNotify + Integer.valueOf(i+1) + ") " + goal.get(i) + " в размере " + payment.get(i);
        }
        Log.v(TAG, textNotify);
        return textNotify;
    }

    private String createOneNotify(ArrayList<String> payment, ArrayList<String> goal, Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy");
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
        CharSequence contentTitle = "DebtCalc";
        CharSequence contentText = textNotify;
        Intent notificationIntent = new Intent(context, ListDebt.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        mNotificationManager.notify(0, notification);
    }
}
