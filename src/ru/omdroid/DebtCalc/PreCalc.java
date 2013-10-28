package ru.omdroid.DebtCalc;


public class PreCalc {

    public boolean preCalc(){
        return !AppData.DEBT.equals("") & AppData.TERM != 0 & AppData.PERCENT != 0.0;
    }
}
