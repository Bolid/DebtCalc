package ru.omdroid.DebtCalc;

public class AppData {
    public static String[] param = new String[4];
    public static String PAYMENT, GOAL = "", DEBT_BALANCE = "", PAYMENT_DEFAULT, ID_DEBT = "", DEBT = "";
    public static Long DATE_PAY, DATE_DEBT_START;
    public static Double OVER_PAYMENT, PERCENT = 0.0, DELTA;
    public static int TERM_BALANCE = 0, TERM = 0, COUNT_DAY_OF_MONTH = 0, COUNT_DAY_OF_YEAR = 0, UP_PAYMENT = 0;
    public static int DAY_IN_JAN, DAY_IN_DEC, DAY_IN_YEAR_JAN, DAY_IN_YEAR_DEC;
    public static boolean END_YEAR = false;

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
        DATE_PAY = s;
    }

    public void setUpPayment(int i){
        UP_PAYMENT = i;
    }

    public void setDateDebtStart(Long dateStart){
        DATE_DEBT_START = dateStart;
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

    public void setCountDayOfMonth(int countDayInMonth, int countDayInYear){
        COUNT_DAY_OF_MONTH = countDayInMonth;
        COUNT_DAY_OF_YEAR = countDayInYear;
        END_YEAR = false;
    }

    public void setEndYear(boolean endYear, int dayInJan, int dayInDec, int dayInYearJan, int dayInYearDec){
        END_YEAR = endYear; DAY_IN_JAN = dayInJan; DAY_IN_DEC = dayInDec; DAY_IN_YEAR_JAN = dayInYearJan; DAY_IN_YEAR_DEC = dayInYearDec;
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
        DATE_PAY = (long) 0;
        PAYMENT = "";
    }
}
