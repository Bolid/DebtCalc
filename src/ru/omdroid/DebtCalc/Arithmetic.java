package ru.omdroid.DebtCalc;


import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Arithmetic {
    final String TAG = "ru.omdroid.DebtCalc.Arithmetic";

    private HashMap<Integer, Double> hmPaymentMonth;

    public static ArrayList <String> allResult;
    public static ArrayList <String> listDefaultPayment;
    public static ArrayList <HashMap<String, String>> listResult = null;


    int termCredit = 0;
    Double sumCredit = 0.0;
    Double percend = 0.0;
    Double dopPlatej;

    public Arithmetic(Double sumCredit, Integer termCredit, Double percend){
        this.sumCredit = sumCredit;
        this.termCredit = termCredit;
        this.percend = percend;

        allResult = new ArrayList<String>();
        listDefaultPayment = new ArrayList<String>();
        hmPaymentMonth = new HashMap<Integer, Double>();

        allResult.add(0, String.valueOf(dopPlatej)); //исходные данные Дополнительный платеж
        allResult.add(1, String.valueOf(sumCredit)); //исходные данные Сумма кредита
        allResult.add(2, String.valueOf(termCredit)); //исходные данные Срок кредита
        allResult.add(3, String.valueOf(percend)); //исходные данные Процентная ставка
        //данные для вывода
        allResult.add(4, String.valueOf(getPayment(sumCredit, termCredit)));
        allResult.add(5, "");
        allResult.set(5, String.valueOf(getDeltaDefault(getPayment(sumCredit, termCredit), termCredit)));
        allResult.add(6, String.valueOf(termCredit));
        allResult.add(7, "");
        allResult.add(8, "");
        allResult.add(9, "");
    }


    public Double getPayment(Double sumCredit, int termCredit){
        Double result = Rounding(sumCredit * ((percend / 100. / 12) * Math.pow((1 + (percend / 100. / 12)), termCredit)) / (Math.pow((1 + (percend / 100. / 12)), termCredit) - 1));
        return result;
    }

    public Double getDeltaDefault(Double payment, int termCredit){
        Double delta = Rounding(payment * termCredit - sumCredit);
        allResult.set(5, String.valueOf(delta));
        return delta;
    }

   public void getOverpaymentAllMonth(Double addPayment, boolean overPayment){
       hmPaymentMonth.clear();

       Double sumCredit = Double.valueOf(allResult.get(1));
       Double allPer = 0.0;
       int i = 0;
       String[] from = new String[]{"Number", "Payment", "Image", "Dolg", "Delta"};

       listResult = new ArrayList<HashMap<String, String>>();
       HashMap<String, String> hm;
        while (sumCredit > 0.0){
            listDefaultPayment.add(i, String.valueOf(getPayment(sumCredit, Integer.valueOf(allResult.get(2)) - i)));
            i++;
            hm = new HashMap<String, String>();
            if (overPayment)
                hm.put(from[2], String.valueOf(1));
            if (i < 10)
                hm.put(from[0], " "+String.valueOf(i)+" ");
            else if (i > 9 & i < 100)
                hm.put(from[0], String.valueOf(i)+" ");
            else
                hm.put(from[0], String.valueOf(i));

            allPer = allPer + (sumCredit * (percend/100.) / 12);
            if (sumCredit < (addPayment)){
                hm.put(from[1], setMask(Rounding(addPayment)));
                hm.put(from[3], setMask(Rounding(addPayment - (sumCredit * (percend/100.) / 12))));
                hm.put(from[4], setMask(Rounding((sumCredit * (percend/100.) / 12))));
                addPayment = sumCredit + (sumCredit * (percend/100.) / 12);
                sumCredit = sumCredit - addPayment;
            }
            else{
                hm.put(from[1], setMask(Rounding(addPayment)));
                hm.put(from[3], setMask(Rounding(addPayment - (sumCredit * (percend/100.) / 12))));
                hm.put(from[4], setMask(Rounding((sumCredit * (percend/100.) / 12))));
                sumCredit = Rounding(sumCredit - (addPayment - (sumCredit * (percend/100.) / 12)));
            }
            listResult.add(hm);
            Log.d(TAG, "========");
            Log.d(TAG, "Сумма долга: " + sumCredit);
            Log.d(TAG, "Доп. платеж: " + addPayment);
            Log.d(TAG, "Переплата: " + (sumCredit * (percend/100.) / 12));
            Log.d(TAG, "________");
        }
        Log.d(TAG, "Общая переплата: " + allPer);
        allPer = Rounding(allPer);
        allResult.set(8, String.valueOf(allPer)); //Общая переплата
        allResult.set(9, String.valueOf(i)); //Срок погашения


        hm = new HashMap<String, String>();
        hm.put(from[0], "Всего");
        hm.put(from[1], setMask(Double.valueOf(allResult.get(1)) + Double.valueOf(allResult.get(5))));
        hm.put(from[3], setMask(Double.valueOf(allResult.get(1))));
        hm.put(from[4], setMask(allPer));
        listResult.add(hm);
    }

    public void getOverpaymentSomeMonth(Double addPaymentSomeMonth, Double addPayment, int j){
        Double sumCredit = Double.valueOf(allResult.get(1));
        int termCredit = Integer.valueOf(allResult.get(9));
        int i = 0;
        Double allPer = 0.0;

        if (j != 0 & !hmPaymentMonth.containsKey(j))
            hmPaymentMonth.put(j, addPaymentSomeMonth);
        else if (hmPaymentMonth.containsKey(j)){
            hmPaymentMonth.remove(j);
            hmPaymentMonth.put(j, addPaymentSomeMonth);
        }

        listResult = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hm;
        String[] from = new String[]{"Number", "Payment", "Image", "Dolg", "Delta"};

        while (sumCredit > 0.0){
            listDefaultPayment.set(i, String.valueOf(getPayment(sumCredit, Integer.valueOf(allResult.get(2)) - i)));
            Log.v(TAG, "Пересчитанный платеж: " + getPayment(sumCredit, termCredit));
            Log.v(TAG, "Сумма: " + sumCredit + ". Срок: " + termCredit);
            Log.v(TAG, "В счет долга: " + (addPayment - (sumCredit * (percend/100.) / 12)));
            Log.v(TAG, "Остаток: " + String.valueOf(sumCredit - (addPayment - (sumCredit * (percend/100.) / 12))));
            i++;
            allPer = allPer + (sumCredit * (percend/100.) / 12);
            hm = new HashMap<String, String>();
            if (i < 10)
                hm.put(from[0], String.valueOf(i)+"  ");
            else if (i > 9 & i < 100)
                hm.put(from[0], String.valueOf(i)+" ");
            else
                hm.put(from[0], String.valueOf(i));
            if (!hmPaymentMonth.containsKey(i)){
                if (sumCredit < addPayment)
                    addPayment = sumCredit + (sumCredit * (percend/100.) / 12);
                hm.put(from[1], setMask(addPayment));
                hm.put(from[3], setMask(Rounding(addPayment - (sumCredit * (percend / 100.) / 12))));
                hm.put(from[4], setMask(Rounding((sumCredit * (percend / 100.) / 12))));
                sumCredit = Rounding(sumCredit - (getPayment(sumCredit, termCredit) - (sumCredit * (percend/100.) / 12)));
            }
            else{
                if (sumCredit < hmPaymentMonth.get(i)){
                    hmPaymentMonth.remove(i);
                    hmPaymentMonth.put(i, sumCredit + (sumCredit * (percend/100.) / 12));
                }
                hm.put(from[1], setMask(Rounding(hmPaymentMonth.get(i))));
                hm.put(from[2], String.valueOf(1));
                hm.put(from[3], setMask(Rounding(hmPaymentMonth.get(i) - (sumCredit * (percend / 100.) / 12))));
                hm.put(from[4], setMask(Rounding((sumCredit * (percend / 100.) / 12))));
                sumCredit = sumCredit - (Rounding(hmPaymentMonth.get(i) - (sumCredit * (percend/100.) / 12)));
                if (termCredit != 1)
                    addPayment = getPayment(sumCredit, termCredit - 1);
            }
            termCredit--;
            listResult.add(hm);
        }

        hm = new HashMap<String, String>();
        hm.put(from[0], "Всего");
        hm.put(from[1], setMask(Double.valueOf(allResult.get(1)) + Double.valueOf(allResult.get(5))));
        hm.put(from[3], setMask(Double.valueOf(allResult.get(1))));
        hm.put(from[4], setMask(allPer));
        listResult.add(hm);

    }

    public Double Rounding(Double value){
        BigDecimal roundValue = BigDecimal.valueOf(value);
        roundValue = roundValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
        return Double.valueOf(roundValue.toString());
    }

    public String setMask(Double value){
        NumberFormat numberFormat = new DecimalFormat("###,###,###,###.00");
    return String.valueOf(numberFormat.format(value));
    }

}
