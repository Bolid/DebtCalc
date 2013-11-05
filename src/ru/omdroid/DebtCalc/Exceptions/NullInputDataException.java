package ru.omdroid.DebtCalc.Exceptions;


import ru.omdroid.DebtCalc.AppData;

public class NullInputDataException extends Exception {
    private String detail = null;
    public NullInputDataException(String a){
        detail = a;
    }
    public String toString(){
        String exception = "";
        if (AppData.DEBT_BALANCE.equals("0")) exception = exception + "Сумму\n";
        if (AppData.TERM_BALANCE == 0) exception = exception + "Срок\n";
        if (AppData.PERCENT == 0.0) exception = exception + "Ставку\n";
        if (AppData.GOAL.equals("")) exception = exception + "Цель";
        return "Внимание! Введите следующие данные:\n" + exception;
    }
}
