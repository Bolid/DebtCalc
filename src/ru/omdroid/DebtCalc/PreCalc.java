package ru.omdroid.DebtCalc;


public class PreCalc {

    public boolean preCalc(){
        return !AppData.DEBT_BALANCE.equals("") & AppData.TERM_BALANCE != 0 & AppData.PERCENT != 0.0;
    }
}
