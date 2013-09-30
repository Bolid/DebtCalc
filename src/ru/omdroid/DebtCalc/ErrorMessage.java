package ru.omdroid.DebtCalc;


public class ErrorMessage {
    static public String nullSumCredit = "";
    static public String nullTermCredit = "";
    static public String nullPercentCredit = "";
    static public String errorTitle = "";

    public void readErrorMessageSumCredit(){
        errorTitle = "Внимание! Введите:\n";
        nullSumCredit = "Сумму кредита. Расчет выполнен для суммы по умолчанию - 900 000 р.\n";
    }

    public void readErrorMessageTermCredit(){
        errorTitle = "Внимание! Введите:\n";
        nullTermCredit = "Срок кредита. Расчет выполнен для срока по умолчанию - 120 месяцев.\n";
    }

    public void readErrorMessagePercendCredit(){
        errorTitle = "Внимание! Введите:\n";
        nullPercentCredit = "Процентную ставку. Расчет выполнен для ставки по умолчанию - 12 %.\n";
    }

    public void clearErrorMessageSumCredit(){
        errorTitle = "";
        nullSumCredit = "";
    }

    public void clearErrorMessageTermCredit(){
        errorTitle = "";
        nullTermCredit = "";
    }

    public void clearErrorMessagePercendCredit(){
        errorTitle = "";
        nullPercentCredit = "";
    }

    public void downPercentCredit(){
        nullPercentCredit = "Процентная ставка более 100 %. Расчет произведен для ставки по умолчанию - 12%\n";
    }
}
