package ru.omdroid.DebtCalc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WorkDateDebt {

    AppData appData = new AppData();
    Long dateNewLong = (long) 0;
    int countMonth[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public Long createNextDatePayment(Long dateNewLong, Long dateDebtStartLong){
        if (this.dateNewLong == 0)
            this.dateNewLong = dateNewLong;

        Calendar calendarDateStart = Calendar.getInstance();
        calendarDateStart.setTimeInMillis(dateDebtStartLong);

        Calendar calendarDateNew = Calendar.getInstance();
        calendarDateNew.setTimeInMillis(this.dateNewLong);
        appData.setCountDayOfMonth(countMonth[calendarDateNew.get(Calendar.MONTH)], 0);

        int preMonth = calendarDateNew.get(Calendar.MONTH);
        calendarDateNew.set(calendarDateNew.get(Calendar.YEAR), calendarDateNew.get(Calendar.MONTH) + 1, calendarDateStart.get(Calendar.DATE));
        int postMonth = calendarDateNew.get(Calendar.MONTH);
        while (postMonth - preMonth > 1){
            calendarDateNew.set(calendarDateNew.get(Calendar.YEAR), calendarDateNew.get(Calendar.MONTH), calendarDateNew.get(Calendar.DATE)-1);
            postMonth = calendarDateNew.get(Calendar.MONTH);
        }

        //appData.setCountDayOfMonth((int) ((calendarDateNew.getTimeInMillis() - this.dateNewLong) / 86400000), 0);

        this.dateNewLong = calendarDateNew.getTimeInMillis();
        return calendarDateNew.getTimeInMillis();
    }

    public String getDate(Calendar calendar) {
        String date;
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd");
        date = format.format(calendar.getTime());
        format.applyPattern("MM");
        date = date + "." + format.format(calendar.getTime()) + "." + String.valueOf(calendar.get(Calendar.YEAR));
        return date;
    }

    public void getCountDayInMonth(long datePaymentLong){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datePaymentLong);
        int month1 = calendar.get(Calendar.MONTH);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 15);
        int month2 = calendar.get(Calendar.MONTH);
        int test = countMonth[calendar.get(Calendar.MONTH)];
        appData.setCountDayOfMonth(countMonth[calendar.get(Calendar.MONTH)], 0);
    }
}
