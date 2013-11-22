package ru.omdroid.DebtCalc.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;

import java.util.Calendar;
import java.util.Date;


public class Receiver extends BroadcastReceiver {
    final String ADD_NOTIFY = "ADD_NOTIFY";
    final String TAG = "ru.omdroid.DebtCalc.Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ADD_NOTIFY)){
            createNotification(context);
        }
    }

    private void createNotification(Context context){
        String idDebt;
        int countDay;
        Calendar currentDate = Calendar.getInstance();
        Calendar paymentDate = Calendar.getInstance();
        paymentDate.set(Calendar.YEAR, 1980);

        WorkDB workDB = new WorkDB(context);
        Cursor cursorNotify = workDB.readValueFromDataBase("SELECT * FROM " + DebtCalcDB.TABLE_NOTIFY);
        while (cursorNotify.moveToNext()){
            idDebt = cursorNotify.getString(cursorNotify.getColumnIndex(DebtCalcDB.F_ID_DEBT_NOTIFY));
            countDay = cursorNotify.getInt(cursorNotify.getColumnIndex(DebtCalcDB.F_COUNT_DAY_NOTIFY));
            Log.v(TAG, "Кредит: " + idDebt +  ". Количество дней: " + countDay);
            Cursor cursorPayment = workDB.readValueFromDataBase("SELECT " +
                                                                    DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +
                                                                    DebtCalcDB.FIELD_DATE_LONG_PAYMENTS  +" FROM " +
                                                                    DebtCalcDB.TABLE_PAYMENTS + " WHERE (" +
                                                                    DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " = '" + idDebt + "' AND " +
                                                                    DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0')");
            if (cursorPayment.moveToNext()){
                Log.v(TAG, "Условии 1");
                paymentDate.setTimeInMillis(cursorPayment.getLong(cursorPayment.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
                paymentDate.set(Calendar.DATE, paymentDate.get(Calendar.DATE) - countDay);
            }
            Log.v(TAG, "currentDate!" + currentDate.get(Calendar.DATE) + "." + currentDate.get(Calendar.MONTH) + "." + currentDate.get(Calendar.YEAR));
            Log.v(TAG, "paymentDate!" + paymentDate.get(Calendar.DATE) + "." + paymentDate.get(Calendar.MONTH) + "." + paymentDate.get(Calendar.YEAR));
            if ((currentDate.get(Calendar.DATE) == paymentDate.get(Calendar.DATE)) & (currentDate.get(Calendar.MONTH) == paymentDate.get(Calendar.MONTH)) & (currentDate.get(Calendar.YEAR) == paymentDate.get(Calendar.YEAR)))
            {
                Toast.makeText(context, "Напоминание удалено!", Toast.LENGTH_LONG).show();
                Log.v(TAG, "Круто!");
            }
        }
    }
}
