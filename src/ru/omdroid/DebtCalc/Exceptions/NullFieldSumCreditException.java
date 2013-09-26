package ru.omdroid.DebtCalc.Exceptions;


public abstract class NullFieldSumCreditException extends Exception {
    private String detail = null;
    NullFieldSumCreditException(String a){
        detail = a;
    }
    public String toString(){
        return "NullFieldSumCreditException:";
    }
}
