package ru.omdroid.DebtCalc;

public class AppData {
    public static String[] param = new String[4];
    public static String PAYMENT, GOAL = "", DEBT = "", PAYMENT_DEFAULT, ID_DEBT = "";
    public static Long DATE;
    public static Double OVER_PAYMENT, PERCENT = 0.0;
    public static int TERM = 0;

    public void addSumCredit(String sumCredit){
        param[0] = sumCredit;
    }

    public void setPercent(String percentCredit){
        param[1] = percentCredit;
        PERCENT = Double.valueOf(percentCredit);
    }

    public void setTerm(String termCredit){
        param[2] = termCredit;
        TERM = Integer.parseInt(termCredit);
    }

    public void addTypeCredit(String typeCredit){
        param[3] = String.valueOf(typeCredit);
    }

    public void setPayment(String s, String sDefault){
        PAYMENT = s;
        PAYMENT_DEFAULT = sDefault;
    }

    public void setDate(Long s){
        DATE = s;
    }

    public void setDebt(String s){
        DEBT = s;
    }

    public void setGoal(String s){
        GOAL = s;
    }

    public void setIdDebt(String s){
        ID_DEBT = s;
    }

    public void setOverPayment(Double overPayment){
        OVER_PAYMENT = overPayment;
    }

    public String getSumCredit(){
        return param[0];
    }

    public String getPercentCredit(){
        return param[1];
    }

    public String getTermCredit(){
        return param[2];
    }

    public void allRemove(){
        DEBT = "0";
        TERM = 0;
        PERCENT = 0.0;
        GOAL = "";
        DATE = (long) 0;
    }
}
