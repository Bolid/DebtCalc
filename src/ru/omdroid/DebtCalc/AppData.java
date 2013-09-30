package ru.omdroid.DebtCalc;

public class AppData {
    public static String[] param = new String[3];

    public void addSumCredit(String sumCredit){
        param[0] = sumCredit;
    }

    public void addTermCredit(String termCredit){
        param[1] = termCredit;
    }

    public void addPercentCredit(String percentCredit){
        param[2] = percentCredit;
    }
}
