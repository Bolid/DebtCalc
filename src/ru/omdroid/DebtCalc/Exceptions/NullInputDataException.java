package ru.omdroid.DebtCalc.Exceptions;


import ru.omdroid.DebtCalc.AppData;

public class NullInputDataException extends Exception {
    private String detail = null;
    public NullInputDataException(String a){
        detail = a;
    }
    public String toString(){
        String exception = "";
        if (AppData.DEBT.equals("")) exception = exception + "Сумму\n";
        if (AppData.TERM == 0) exception = exception + "Срок\n";
        if (AppData.PERCENT == 0.0) exception = exception + "Ставку\n";
        if (AppData.GOAL.equals("")) exception = exception + "Цель";
        return "Кредит не сохранен. Введите следующие данные:\n" + exception;
    }
}
