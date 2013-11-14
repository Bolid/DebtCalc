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
        allResult.add(6, String.valueOf(termCredit));
        allResult.add(7, "");
        allResult.add(8, "");
        allResult.add(9, "");

        dataForGraph.setSum(sumCredit);
        dataForGraph.setParamOlr(termCredit);
        dataForGraph.setParamNew(termCredit);
    }


    public Double getPayment(Double sumCredit, int termCredit){

        return Rounding(sumCredit * ((percent / 100. / 12) + ((percent / 100. / 12) / (Math.pow((1 + (percent / 100. / 12)), termCredit) - 1))));
        //Rounding(sumCredit * ((percent / 100. / 12) * Math.pow((1 + (percent / 100. / 12)), termCredit)) / (Math.pow((1 + (percent / 100. / 12)), termCredit) - 1));
    }

    public Double getDeltaDefault(Double payment, int termCredit){
        Double delta = Rounding(payment * termCredit - sumCredit);
        allResult.set(5, String.valueOf(delta));
        dataForGraph.setOver(delta);
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

   public void getOverpaymentAllMonth(Double sumDebt, Double addPayment, boolean overPayment){
       hmPaymentMonth.clear();
       Double sumCredit = sumDebt;
       Double allPer = 0.0;
       int i = 0;
       WorkDateDebt workDateDebt = new WorkDateDebt();
       workDateDebt.getCountDayInMonth(AppData.DATE_PAY);
       listResult = new ArrayList<HashMap<String, String>>();
        while (sumCredit > 0.0){
            listDefaultPayment.add(i, String.valueOf(getPayment(sumCredit, Integer.valueOf(allResult.get(2)) - i)));
            i++;
            Double perLocal = (getPaymentInPercent(sumCredit, AppData.COUNT_DAY_OF_MONTH));
            allPer = allPer + (getPaymentInPercent(sumCredit, AppData.COUNT_DAY_OF_MONTH));
            if (sumCredit < (addPayment)){
                addPayment = sumCredit + (getPaymentInPercent(sumCredit, AppData.COUNT_DAY_OF_MONTH));
                sumCredit = sumCredit - addPayment;
            }
            else{
                sumCredit = Rounding(sumCredit - (getPaymentInDebt(addPayment, sumCredit)));
            }
            workDateDebt.createNextDatePayment(AppData.DATE_PAY, AppData.DATE_DEBT_START);
        }
        allPer = Rounding(allPer);
        allResult.set(5, String.valueOf(allPer)); //Общая переплата
        allResult.set(6, String.valueOf(i)); //Срок погашения
        dataForGraph.setParamNew(i);
        dataForGraph.setOver(allPer);
    }

    public void getOverpaymentSomeMonth(Double addPaymentSomeMonth, Double addPayment, int j){
        Double sumCredit = Double.valueOf(allResult.get(1));
        int termCredit = Integer.valueOf(allResult.get(6));
        int i = 0;
        Double allPer = 0.0;

        if (j != 0 & !hmPaymentMonth.containsKey(j))
            hmPaymentMonth.put(j, addPaymentSomeMonth);
        else if (hmPaymentMonth.containsKey(j)){
            hmPaymentMonth.remove(j);
            hmPaymentMonth.put(j, addPaymentSomeMonth);
        }

        while (sumCredit > 0.0){
            listDefaultPayment.set(i, String.valueOf(getPayment(sumCredit, Integer.valueOf(allResult.get(2)) - i)));
            i++;
            allPer = allPer + (sumCredit * (percent /100.) / 12);
            if (!hmPaymentMonth.containsKey(i)){
                if (sumCredit < addPayment)
                    addPayment = sumCredit + (sumCredit * (percent /100.) / 12);
                sumCredit = Rounding(sumCredit - (getPayment(sumCredit, termCredit) - (sumCredit * (percent /100.) / 12)));
            }
            else{
                sumCredit = sumCredit - (Rounding(hmPaymentMonth.get(i) - (sumCredit * (percent /100.) / 12)));
                if (termCredit != 1)
                    addPayment = getPayment(sumCredit, termCredit - 1);
            }
            termCredit--;
        }

    }

    public Double Rounding(Double value){
        BigDecimal roundValue = BigDecimal.valueOf(value);
        roundValue = roundValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        return /*value;//*/Double.valueOf(roundValue.toString());
    }

    public String setMask(Double value){
        NumberFormat numberFormat = new DecimalFormat("###,###,###,###.00");
    return String.valueOf(numberFormat.format(value));
    }

}
