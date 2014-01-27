package ru.omdroid.DebtCalc;


import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import ru.omdroid.DebtCalc.CustomView.DataForGraph;

public class Arithmetic {
    final String TAG = "ru.omdroid.DebtCalc.Arithmetic";

    private HashMap<Integer, Double> hmPaymentMonth;

    public static ArrayList <String> allResult;
    public static ArrayList <String> listDefaultPayment;
    public static ArrayList <HashMap<String, String>> listResult = null;


    int termCredit = 0;
    Double sumCredit = 0.0, percent = 0.0, dopPlatej;

    DataForGraph dataForGraph = new DataForGraph();

    public Arithmetic(Double percent){
        this.percent = percent;
    }

    public Arithmetic(Double sumCredit, Double percent, Integer termCredit){
        this.sumCredit = sumCredit;
        this.percent = percent;
        this.termCredit = termCredit;

        allResult = new ArrayList<String>();
        listDefaultPayment = new ArrayList<String>();
        hmPaymentMonth = new HashMap<Integer, Double>();

        allResult.add(0, String.valueOf(dopPlatej)); //исходные данные Дополнительный платеж
        allResult.add(1, String.valueOf(sumCredit)); //исходные данные Сумма кредита
        allResult.add(2, String.valueOf(termCredit)); //исходные данные Срок кредита
        allResult.add(3, String.valueOf(percent)); //исходные данные Процентная ставка
        //данные для вывода
        allResult.add(4, String.valueOf(getPayment(sumCredit, termCredit)));
        allResult.add(5, "");
        allResult.set(5, String.valueOf(getDeltaDefault(getPayment(sumCredit, termCredit), termCredit)));
        //getOverpaymentAllMonth(sumCredit, getPayment(sumCredit, termCredit), true);
        allResult.add(6, String.valueOf(termCredit));
        allResult.add(7, "");
        allResult.add(8, "");
        allResult.add(9, "");

        /*dataForGraph.setSum(sumCredit);
        dataForGraph.setOldTerm(termCredit);
        dataForGraph.setNewTerm(termCredit);*/
    }


    public Double getPayment(Double sumCredit, int termCredit){

        return Rounding(sumCredit * ((percent / 100. / 12) + ((percent / 100. / 12) / (Math.pow((1 + (percent / 100. / 12)), termCredit) - 1))));
        //Rounding(sumCredit * ((percent / 100. / 12) * Math.pow((1 + (percent / 100. / 12)), termCredit)) / (Math.pow((1 + (percent / 100. / 12)), termCredit) - 1));
    }

    public int getTerm(Double payment, Double sumCredit){
        int n = (int) Math.round(Math.log((payment) / ((payment) - (percent/100./12) * sumCredit)) / Math.log(1 + (percent/100./12)));
        return n;
    }

    public Double getDeltaDefault(Double payment, int termCredit){
        Double delta = Rounding(payment * termCredit - sumCredit);
        allResult.set(5, String.valueOf(delta));
        return delta;
    }

    public int getOverInPercent(Double overDebt, Double sumCredit, int termCredit){
        return (int) (overDebt * 100  / sumCredit);
    }

    public Double getDeltaNew(int termCredit, Double balance, Double payment){
        return Rounding(payment * termCredit - balance);
    }

    public Double getBalance (Double payment, Double balance, int termCredit){

        Double a1 = 0.0;
        try{a1 = Rounding(balance - Rounding(payment - getPaymentInPercent(balance, AppData.COUNT_DAY_OF_MONTH))); }
        catch (Exception e){
            Log.v("Error", "Error");
        }
        return a1;
        //return Rounding(balance - Rounding(payment - Rounding(balance * (percent /100.) / 12)));
    }

    public Double getPaymentInPercent(Double balance, int countDay){
        if (AppData.END_YEAR)
            return ((balance * AppData.DAY_IN_DEC * percent)/(AppData.DAY_IN_YEAR_DEC * 100)) + ((balance * AppData.DAY_IN_JAN * percent)/(AppData.DAY_IN_YEAR_JAN * 100));
        return (balance * AppData.COUNT_DAY_OF_MONTH * percent)/(AppData.COUNT_DAY_OF_YEAR * 100);
    }

    public Double getPaymentInDebt(Double payment, Double balance){
        return payment -getPaymentInPercent(balance, AppData.COUNT_DAY_OF_MONTH);
    }

   public void getOverpaymentAllMonth(Double sumDebt, Double addPayment, long date, int dec){
       Double sumCredit = sumDebt;
       Double allPer = 0.0;
       int i = 0;
       WorkDateDebt workDateDebt = new WorkDateDebt();
       workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);
        while (sumCredit > 0.0){
//            listDefaultPayment.add(i, String.valueOf(getPayment(sumCredit, Integer.valueOf(allResult.get(2)) - i)));
            Double perLocal = (getPaymentInPercent(sumCredit, AppData.COUNT_DAY_OF_MONTH));
            i++;
            allPer = allPer + (getPaymentInPercent(sumCredit, AppData.COUNT_DAY_OF_MONTH));
            if (i == AppData.TERM_BALANCE - dec){
                addPayment = sumCredit + perLocal;
                sumCredit = sumCredit - addPayment;
            }
            else{
                sumCredit = Rounding(sumCredit - (addPayment - perLocal));
            }
            workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);
            //workDateDebt.getCountDayInMonth(datePayment);
        }
        allPer = Rounding(allPer);
        allResult.set(5, String.valueOf(allPer)); //Общая переплата
        allResult.set(6, String.valueOf(i)); //Срок погашения
        dataForGraph.setNewTerm(i);
        dataForGraph.setOver(allPer);
    }

    public Double Rounding(Double value){
        BigDecimal roundValue = BigDecimal.valueOf(value);
        roundValue = roundValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        return /*value;//*/Double.valueOf(roundValue.toString());
    }
}
