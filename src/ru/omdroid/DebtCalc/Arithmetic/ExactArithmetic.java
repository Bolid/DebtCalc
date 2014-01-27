package ru.omdroid.DebtCalc.Arithmetic;

import ru.omdroid.DebtCalc.AppData;
import ru.omdroid.DebtCalc.WorkDateDebt;


public class ExactArithmetic extends Arithmetic {
    public int totalTerm = 0;

    public ExactArithmetic(Double percent) {
        super(percent);
    }

    public Double getOverpaymentAllMonth(Double sumDebt, Double addPayment, long date, int dec){
        Double totalOver = 0.0;
        totalTerm = 0;
        Double sumCredit = sumDebt;
        int i = 0;
        WorkDateDebt workDateDebt = new WorkDateDebt();
        workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);
        while (sumCredit > 0.0){
            Double perLocal = (getOverpaymentOneMonth(sumCredit));
            i++;
            totalOver = totalOver + (getOverpaymentOneMonth(sumCredit));
            if (i == AppData.TERM_BALANCE - dec){
                addPayment = sumCredit + perLocal;
                sumCredit = sumCredit - addPayment;
            }
            else{
                sumCredit = Rounding(sumCredit - (addPayment - perLocal));
            }
            workDateDebt.createNextDatePayment(date, AppData.DATE_DEBT_START);
        }
        totalOver = Rounding(totalOver);
        totalTerm = i;
        allResult.set(5, String.valueOf(totalOver)); //Общая переплата
        allResult.set(6, String.valueOf(i)); //Срок погашения
        dataForGraph.setNewTerm(i);
        dataForGraph.setOver(totalOver);
        return totalOver;
    }
}
