package ru.omdroid.DebtCalc.Arithmetic;

import android.util.Log;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.WorkDateDebt;

import java.util.Calendar;


public class ExactArithmetic extends Arithmetic {
    private int totalTerm = 0;

    public ExactArithmetic(Double percent) {
        super(percent);
    }

    public Double getOverpaymentAllMonth(Double sumDebt, Double addPayment, int balanceTerm, long date, int dec){

        Calendar dateD = Calendar.getInstance();
        dateD.setTimeInMillis(date);
        long date1 = 0;

        Double totalOver = 0.0;
        totalTerm = 0;
        Double sumCredit = sumDebt;
        int i = 0;
        WorkDateDebt workDateDebt = new WorkDateDebt();
        //date1 = workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);
        Log.v("Дата и время: ", workDateDebt.getDate(date));
        while (sumCredit > 0.0){
            date1 = workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);
            Log.v("Дата и время: ", workDateDebt.getDate(date1));
            Double perLocal = (getOverpaymentOneMonth(sumCredit));
            i++;
            totalOver = totalOver + (getOverpaymentOneMonth(sumCredit));
            if (i == balanceTerm - dec){
                addPayment = sumCredit + perLocal;
                sumCredit = sumCredit - addPayment;
            }
            else{
                sumCredit = Rounding(sumCredit - (addPayment - perLocal));
            }
        }
        totalOver = Rounding(totalOver);
        totalTerm = i;
        allResult.set(5, String.valueOf(totalOver)); //Общая переплата
        allResult.set(6, String.valueOf(i)); //Срок погашения
        dataForGraph.setNewTerm(i);
        dataForGraph.setOver(totalOver);
        return totalOver;
    }

    public double getOverpaymentAllMouthAtOneRepayment(Double sumDebt, Double addPayment, int balanceTerm, long date, int dec){
        WorkDateDebt workDateDebt = new WorkDateDebt();
        workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);
        //Double sumDebt = sumDebt;
        double totalOverpayment = getOverpaymentOneMonth(sumDebt);
        sumDebt = sumDebt - (addPayment - totalOverpayment);
        addPayment = getPayment(sumDebt, balanceTerm - 1);
        int i = 0;
        Double overpaymentOneMonth;
        workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);

        while (sumDebt > 0.0){
            overpaymentOneMonth = (getOverpaymentOneMonth(sumDebt));
            i++;
            totalOverpayment = totalOverpayment + overpaymentOneMonth;
            if (i == balanceTerm - dec){
                addPayment = sumDebt + overpaymentOneMonth;
                sumDebt = sumDebt - addPayment;
            }
            else{
                sumDebt = Rounding(sumDebt - (addPayment - overpaymentOneMonth));
            }
            workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);
        }
        return totalOverpayment;
    }

    public String getTotalTerm(){
        return String.valueOf(totalTerm);
    }
}
