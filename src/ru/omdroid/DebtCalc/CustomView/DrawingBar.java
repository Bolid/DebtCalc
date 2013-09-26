package ru.omdroid.DebtCalc.CustomView;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import ru.omdroid.DebtCalc.Arithmetic;
import ru.omdroid.DebtCalc.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;


class DrawingBar extends View {
    static  String TAG = "ru.omdroid.DebtCalc.DrawingBar";
    Context context;
    Double sizeWightBar = 150.;
    int paddingBar = 18;
    public DrawingBar(Context context) {
        super(context);
        this.context = context;
    }

    public DrawingBar(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;

    }

    public DrawingBar(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        this.context = context;
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        drawingCreditBar(canvas);
        drawingTermBar(canvas);
        canvas.restore();
    }

    private String setMaskText(String s, String type){
        NumberFormat patternMoney = new DecimalFormat("###,###,###,###,###,###,##0.00");
        NumberFormat patternMounth = new DecimalFormat("###,###");
        if (type.equals("money"))
            return String.valueOf(patternMoney.format(Double.valueOf(s)));
        else
            return String.valueOf(patternMounth.format(Double.valueOf(s)));
    }

    private void drawingCreditBar(Canvas canvas){
        //sizeWightBar = canvas.getWidth()/((Float.valueOf(Arithmetic.allResult.get(1)) + Double.valueOf(Arithmetic.allResult.get(5)))/Float.valueOf(Arithmetic.allResult.get(1)));
        sizeWightBar = (canvas.getWidth() - paddingBar) * (Double.valueOf(Arithmetic.allResult.get(1)))/(Double.valueOf(Arithmetic.allResult.get(1)) + Double.valueOf(Arithmetic.allResult.get(5)));
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        RectF rectF = new RectF();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set((float) (paddingBar + sizeWightBar - 50), 50, canvas.getWidth()-paddingBar, 80);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        rectF.set((float) (paddingBar + sizeWightBar - 50), 50, canvas.getWidth()-paddingBar, 80);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set(paddingBar, 50, Float.valueOf(sizeWightBar.toString()), 80);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        rectF.set(paddingBar, 50, Float.valueOf(sizeWightBar.toString()), 80);
        canvas.drawRoundRect(rectF, 15, 15, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(25);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);
        //canvas.drawText(setMaskText(Arithmetic.allResult.get(5), "money"), (float) (sizeWightBar + (getWidth() - sizeWightBar) / 2), 73, paint);

        if ((getWidth() - sizeWightBar) / 2 < 100){
            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);
            paint.setShadowLayer(15, 0, 0, Color.rgb(95, 112, 95));
            canvas.drawLine(getWidth() - 100, 65, (float) (getWidth() - (getWidth() - sizeWightBar) / 2 - 7), 65, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(25);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
            canvas.drawText(setMaskText(Arithmetic.allResult.get(5), "money"), (float) (getWidth() - 100), 73, paint);
        }
        else
            canvas.drawText(setMaskText(Arithmetic.allResult.get(5), "money"), (float) (sizeWightBar + (getWidth() - sizeWightBar) / 2), 73, paint);



    }

    private void drawingTermBar(Canvas canvas){
        int upperBound = 100;
        int lowerBound = 130;
        sizeWightBar = (canvas.getWidth() - paddingBar) * Double.valueOf(Arithmetic.allResult.get(6))/Double.valueOf(Arithmetic.allResult.get(2));
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        RectF rectF = new RectF();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        if (sizeWightBar > 50)
            rectF.set((float) (paddingBar + sizeWightBar - 50), upperBound, canvas.getWidth()-paddingBar, lowerBound);
        else rectF.set((float) (paddingBar), upperBound, canvas.getWidth()-paddingBar, lowerBound);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        if (sizeWightBar > 50)
            rectF.set((float) (paddingBar + sizeWightBar - 50), upperBound, canvas.getWidth()-paddingBar, lowerBound);
        else
            rectF.set((float) (paddingBar), upperBound, canvas.getWidth()-paddingBar, lowerBound);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set(paddingBar, upperBound, Float.valueOf(sizeWightBar.toString()), lowerBound);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        rectF.set(paddingBar, upperBound, Float.valueOf(sizeWightBar.toString()), lowerBound);
        canvas.drawRoundRect(rectF, 15, 15, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(25);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);

        if (sizeWightBar / 2 < 100) {
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(10);
            paint.setShadowLayer(15, 0, 0, Color.rgb(95, 112, 95));
            canvas.drawLine((float) paddingBar, 115, 100, 115, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(25);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
            canvas.drawText(setMaskText(Arithmetic.allResult.get(6), "mouth"), (float) (100), 123, paint);
        }
        else
            canvas.drawText(setMaskText(Arithmetic.allResult.get(6), "mouth"), (float) (sizeWightBar / 2), 123, paint);
    }


}