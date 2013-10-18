package ru.omdroid.DebtCalc;

public class AppData {
    public static String[] param = new String[4];
    public static String PAYMENT, DEBT, PAYMENT_DEFAULT;
    public static String ID_DEBT;
    public static Long DATE;

    public void addSumCredit(String sumCredit){
        param[0] = sumCredit;
    }

    public void addPercentCredit(String percentCredit){
        param[1] = percentCredit;
    }

    public void addTermCredit(String termCredit){
        param[2] = termCredit;
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

    public void setIdDebt(String s){
        ID_DEBT = s;
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
}
