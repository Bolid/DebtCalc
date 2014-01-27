package ru.omdroid.DebtCalc.Arithmetic;


import android.util.Log;
import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.CustomView.DataForGraph;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Arithmetic {


    public static ArrayList <String> allResult;
    public static ArrayList <String> listDefaultPayment;


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
        return (int) Math.round(Math.log((payment) / ((payment) - (percent/100./12) * sumCredit)) / Math.log(1 + (percent/100./12)));
    }

    public Double getDeltaDefault(Double payment, int termCredit){
        Double delta = Rounding(payment * termCredit - sumCredit);
        allResult.set(5, String.valueOf(delta));
        return delta;
    }

    public int getOverInPercent(Double overDebt, Double sumCredit){
        return (int) (overDebt * 100  / sumCredit);
    }

    public Double getDeltaNew(int termCredit, Double balance, Double payment){
        return Rounding(payment * termCredit - balance);
    }

    public Double getBalance (Double payment, Double balance){

        Double a1 = 0.0;
        try{a1 = Rounding(balance - Rounding(payment - getOverpaymentOneMonth(balance))); }
        catch (Exception e){
            Log.v("Error", "Error");
        }
        return a1;
        //return Rounding(balance - Rounding(payment - Rounding(balance * (percent /100.) / 12)));
    }

    public Double getOverpaymentOneMonth(Double balance){
        if (AppData.END_YEAR)
            return ((balance * AppData.DAY_IN_DEC * percent)/(AppData.DAY_IN_YEAR_DEC * 100)) + ((balance * AppData.DAY_IN_JAN * percent)/(AppData.DAY_IN_YEAR_JAN * 100));
        return (balance * AppData.COUNT_DAY_OF_MONTH * percent)/(AppData.COUNT_DAY_OF_YEAR * 100);
    }

    public Double getPaymentInDebt(Double payment, Double balance){
        return payment - getOverpaymentOneMonth(balance);
    }



    public Double Rounding(Double value){
        BigDecimal roundValue = BigDecimal.valueOf(value);
        roundValue = roundValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        return /*value;//*/Double.valueOf(roundValue.toString());
    }
}
