package ru.omdroid.DebtCalc.Widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;
import ru.omdroid.DebtCalc.DB.DebtCalcDB;
import ru.omdroid.DebtCalc.DB.WorkDB;
import ru.omdroid.DebtCalc.R;
import ru.omdroid.DebtCalc.WorkDateDebt;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class WidgetAdapter implements RemoteViewsFactory {
    Context context;
    Cursor cursor;
    ArrayList<String> alIdDebt;
    NumberFormat numberFormat = new DecimalFormat("###,###,###,###.00");
    HashMap<String, String> hmIdDebt;
    HashMap<String, Double> hmPayment;
    HashMap<String, String> hmGoal;
    HashMap<String, Integer> hmPaidPay;
    HashMap<String, Long> hmDatePay;

    WorkDateDebt workDateDebt = new WorkDateDebt();

    Long currentDate;
    int countDayByPayment;

    WidgetAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        alIdDebt = new ArrayList<String>();
        hmIdDebt = new HashMap<String, String>();
        hmPayment = new HashMap<String, Double>();
        hmGoal = new HashMap<String, String>();
        hmPaidPay = new HashMap<String, Integer>();
        hmDatePay = new HashMap<String, Long>();

        int pos = 0;
        Calendar currentDay = Calendar.getInstance();
        currentDate = currentDay.getTimeInMillis();
        Log.i("Дата платежа текущая", String.valueOf(currentDay.getTimeInMillis()));
        WorkDB workDB = new WorkDB(context);
        cursor = workDB.readValueFromDataBase("SELECT " + DebtCalcDB.FIELD_TYPE_DEBT + ", " + DebtCalcDB.FIELD_ID_DEBT + " FROM " + DebtCalcDB.TABLE_CREDITS + " ORDER BY " + DebtCalcDB.FIELD_DATE_LONG_START_DEBT + " DESC");
        while (cursor.moveToNext()){
            hmGoal.put(cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT)), cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_TYPE_DEBT)));
        }
        cursor.close();

        cursor = workDB.readValueFromDataBase("SELECT " +
                DebtCalcDB.FIELD_PAYMENT_PAYMENTS + ", " +
                DebtCalcDB.FIELD_PAID_PAYMENTS + ", " +
                DebtCalcDB.FIELD_DATE_LONG_PAYMENTS + ", " +
                DebtCalcDB.FIELD_ID_DEBT_PAYMENTS + " FROM " + DebtCalcDB.TABLE_PAYMENTS +
                " WHERE " + DebtCalcDB.FIELD_PAID_PAYMENTS + " = '0' ORDER BY " + DebtCalcDB.FIELD_DATE_LONG_PAYMENTS);
        while (cursor.moveToNext()){
            Log.i("Дата платежа ", cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT_PAYMENTS)) + "  " + cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));

            hmDatePay.put(cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT_PAYMENTS)), cursor.getLong(cursor.getColumnIndex(DebtCalcDB.FIELD_DATE_LONG_PAYMENTS)));
            hmPayment.put(cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT_PAYMENTS)), cursor.getDouble(cursor.getColumnIndex(DebtCalcDB.FIELD_PAYMENT_PAYMENTS)));
            hmPaidPay.put(cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT_PAYMENTS)), cursor.getInt(cursor.getColumnIndex(DebtCalcDB.FIELD_PAID_PAYMENTS)));
            alIdDebt.add(pos, cursor.getString(cursor.getColumnIndex(DebtCalcDB.FIELD_ID_DEBT_PAYMENTS))); //заполняем список целей кредитов
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
        rView.setTextViewText(R.id.tvDatePayment, workDateDebt.getDate(hmDatePay.get(alIdDebt.get(i))));
        rView.setTextColor(R.id.tvDatePayment, Color.parseColor(getColor(hmDatePay.get(alIdDebt.get(i)))));

        Intent intent = new Intent();
        intent.putExtra("POSITION", i);
        rView.setOnClickFillInIntent(R.id.rlItemWidget, intent);
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

    private String getColor(Long date){
        if (date < currentDate)
            return "#"+Integer.toHexString(context.getResources().getColor(R.color.itemWidget));
        countDayByPayment = (int) ((date - currentDate) / 86400000);
        if (countDayByPayment > 30)
            return "#"+Integer.toHexString(context.getResources().getColor(R.color.itemWidget) - 5 * 3329280);
        return "#"+Integer.toHexString(context.getResources().getColor(R.color.itemWidget) - countDayByPayment / 6 * 3329280);
    }
}
