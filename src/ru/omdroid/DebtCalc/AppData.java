package ru.omdroid.DebtCalc;

public class AppData {
    public static String[] param = new String[4];
    public static String PAYMENT, GOAL = "", DEBT_BALANCE = "", PAYMENT_DEFAULT, ID_DEBT = "", DEBT = "";
    public static Long DATE;
    public static Double OVER_PAYMENT, PERCENT = 0.0, DELTA;
    public static int TERM_BALANCE = 0, TERM = 0;

    public void addSumCredit(String sumCredit){
        param[0] = sumCredit;
    }

    public void setPercent(String percentCredit){
        param[1] = percentCredit;
        PERCENT = Double.valueOf(percentCredit);
    }

    public void setTermBalance(String termCredit){
        param[2] = termCredit;
        TERM_BALANCE = Integer.parseInt(termCredit);
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

    public void setDebtBalance(String s){
        DEBT_BALANCE = s;
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

    public void setDebt(String debt){
        DEBT = debt;
    }

    public void setTerm(int term){
        TERM = term;
    }

    public void setDelta(Double delta){
        DELTA = delta;
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
        DEBT_BALANCE = "0";
        TERM_BALANCE = 0;
        PERCENT = 0.0;
        GOAL = "";
        DATE = (long) 0;
    }
}
