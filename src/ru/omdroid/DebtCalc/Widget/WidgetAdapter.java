package ru.omdroid.DebtCalc.Widget;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class WidgetAdapter implements RemoteViewsFactory {
    Context context;
    Cursor cursor;
    ArrayList<String> alIdDebt;
    ArrayList<Double> alPayment;
    ArrayList<Integer> alPaymentPaid;
    NumberFormat numberFormat = new DecimalFormat("###,###,###,###.##");
    HashMap<String, String> hmIdDebt;
    HashMap<String, Double> hmPayment;
    HashMap<String, String> hmGoal;
    HashMap<String, Integer> hmPaidPay;


    WidgetAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onCreate() {
        alIdDebt = new ArrayList<String>();
        alPayment = new ArrayList<Double>();
        alPaymentPaid = new ArrayList<Integer>();
    }

    @Override
    public void onDataSetChanged() {
        hmIdDebt = new HashMap<String, String>();
        hmPayment = new HashMap<String, Double>();
        hmGoal = new HashMap<String, String>();
        hmPaidPay = new HashMap<String, Integer>();

        int pos = 0;
        Calendar currentDay = Calendar.getInstance();
        String currentDayAndYear = String.valueOf(currentDay.get(Calendar.MONTH)) +"."+ String.valueOf(currentDay.get(Calendar.YEAR));
        Calendar firstDayPostMouth = Calendar.getInstance();
        Calendar lastDayPreMouth = Calendar.getInstance();

        currentDay.set(Calendar.DATE, 1);
        firstDayPostMouth.set(Calendar.DATE, 1);
        firstDayPostMouth.set(Calendar.MONTH, currentDay.get(Calendar.MONTH) + 1);
        lastDayPreMouth.set(Calendar.DATE, currentDay.get(Calendar.DATE) - 1);
        WorkDB workDB = new WorkDB(context);
        cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_TYPE_DEBT + ", " + DebtCalcDB.FIELD_ID_DEBT + " FROM " + DebtCalcDB.TABLE_CREDITS);
        while (cursor.moveToNext()){
            hmGoal.put(cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)), cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)));
            alIdDebt.add(pos, cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT))); //заполняем список целей кредитов
            pos++;
        }
        pos = 0;
        cursor.close();

        cursor = workDB.readValueFromDataBase("SELECT " +
                DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +
                DebtCalcDB.FIELD_PAID_PAYMENTS + ", " +
                DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + ", " +
                DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                " WHERE " + DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + " > " + lastDayPreMouth.getTimeInMillis()/*currentDayAndYear*/ +
                " AND " + DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + " < " + firstDayPostMouth.getTimeInMillis());
        while (cursor.moveToNext()){

            hmPayment.put(cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT_PAYMENTS)), cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
            hmPaidPay.put(cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT_PAYMENTS)), cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_PAID_PAYMENTS)));
            alPayment.add(pos, cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));  //заполняем список платежей за текущий месяц
            alPaymentPaid.add(pos, cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_PAID_PAYMENTS)));    //заполняем список отметок по оплате
            Log.d("Виджет lastDayPreMouth ", String.valueOf(lastDayPreMouth));
            Log.d("Виджет currentDay ", String.valueOf(currentDay));
            Log.d("Виджет firstDayPostMouth ", String.valueOf(firstDayPostMouth));
            Log.d("Виджет в платеже ", String.valueOf(cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS))));
            pos++;
        }
        cursor.close();
        workDB.disconnectDataBase();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return alIdDebt.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rView = new RemoteViews(context.getPackageName(), R.layout.item_for_widget);
        rView.setTextViewText(R.id.tvPymentInWidget, numberFormat.format(hmPayment.get(alIdDebt.get(i))));
        rView.setTextViewText(R.id.tvGoalInWidget, hmGoal.get(alIdDebt.get(i)));
        if (hmPaidPay.get(alIdDebt.get(i)) == 1)
            rView.setImageViewResource(R.id.ivPaidPayInWidget, R.drawable.pay_paid);
        else
            rView.setImageViewResource(R.id.ivPaidPayInWidget, R.drawable.pay_no_paid);
        return rView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
