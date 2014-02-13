package ru.omdroid.DebtCalc;


import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface TablePaymentManager {

    void manageData();
    Cursor readDataFromDB();
    View createDataFromDB(Double payment, Double feePayment, String date, String upPayment, int numPayment, final Double balanceDebt, final Double paymentDebt, final Double paymentPercent, Drawable color);
    void createDataFromArithmetic();
    void insertField(View view);
}
