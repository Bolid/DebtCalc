package ru.omdroid.DebtCalc;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WorkDateDebt {

    AppData appData = new AppData();
    Long dateNewLong = (long) 0;

    public Long createNextDatePayment(Long dateNewLong, Long dateDebtStartLong){
        if (this.dateNewLong == 0)
            this.dateNewLong = dateNewLong;

        Calendar calendarDateStart = Calendar.getInstance();
        calendarDateStart.setTimeInMillis(dateDebtStartLong);

        Calendar calendarDateNew = Calendar.getInstance();
        calendarDateNew.setTimeInMillis(this.dateNewLong);

        int preMonth = calendarDateNew.get(Calendar.MONTH);
        calendarDateNew.set(calendarDateNew.get(Calendar.YEAR), calendarDateNew.get(Calendar.MONTH) + 1, calendarDateStart.get(Calendar.DATE));
        int postMonth = calendarDateNew.get(Calendar.MONTH);
        while (postMonth - preMonth > 1){
            calendarDateNew.set(calendarDateNew.get(Calendar.YEAR), calendarDateNew.get(Calendar.MONTH), calendarDateNew.get(Calendar.DATE)-1);
            postMonth = calendarDateNew.get(Calendar.MONTH);
        }

        appData.setCountDayOfMonth((int) ((calendarDateNew.getTimeInMillis() - this.dateNewLong) / 86400000), 0);
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
}
