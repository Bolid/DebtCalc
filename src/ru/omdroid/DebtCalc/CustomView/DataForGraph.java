package ru.omdroid.DebtCalc.CustomView;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.text.NumberFormat;


class DrawingBar extends View {
    static  String TAG = "ru.omdroid.DebtCalc.DrawingBar";
    Context context;
    Double sizeWightBar = 150.;
    int paddingBar = 18;
    int w, h;
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if (DataForGraph.CREATE_GRAPH_OVER)
            drawingCreditBar(canvas);
        if (DataForGraph.CREATE_GRAPH_TERM)
            drawingTermBar(canvas);
        canvas.restore();
    }

    private String setMaskText(Double s, String type){
        NumberFormat patternMoney = new DecimalFormat("###,###,###,###,###,###,##0.00");
        NumberFormat patternMounth = new DecimalFormat("###,###");
        if (type.equals("money"))
            return String.valueOf(patternMoney.format(s));
        else
            return String.valueOf(patternMounth.format(s));
    }

    private void drawingCreditBar(Canvas canvas){
        int height = 50/*DataForGraph.HEIGHT_OVER*/;


        sizeWightBar = (canvas.getWidth() - paddingBar) * (DataForGraph.SUM)/(DataForGraph.SUM + DataForGraph.OVER);
        //sizeWightBar = (canvas.getWidth() - paddingBar) * (DataForGraph.SUM)/(DataForGraph.OVER);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        RectF rectF = new RectF();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set((float) (paddingBar), height, canvas.getWidth()-paddingBar, height + 30);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        rectF.set((float) (paddingBar), height, canvas.getWidth()-paddingBar, height + 30);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set(paddingBar, height, Float.valueOf(sizeWightBar.toString()), height + 30);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        rectF.set(paddingBar, height, Float.valueOf(sizeWightBar.toString()), height + 30);
        canvas.drawRoundRect(rectF, 15, 15, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(25);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);

        if ((getWidth() - sizeWightBar) / 2 < 100){
            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);
            paint.setShadowLayer(15, 0, 0, Color.rgb(95, 112, 95));
            canvas.drawLine(getWidth() - 100, height+15, (float) (getWidth() - (getWidth() - sizeWightBar) / 2 - 7), height+15, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(25);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
            canvas.drawText(setMaskText(DataForGraph.OVER, "money"), (float) (getWidth() - 100), height+23, paint);
        }
        else
            canvas.drawText(setMaskText(DataForGraph.OVER, "money"), (float) (sizeWightBar + (getWidth() - sizeWightBar) / 2), height+23, paint);



    }

    private void drawingTermBar(Canvas canvas){
        int upperBound = DataForGraph.HEIGHT_TERM;
        int lowerBound = DataForGraph.HEIGHT_TERM + 30;
        sizeWightBar = (double) ((canvas.getWidth() - paddingBar) * DataForGraph.PARAM_NEW / DataForGraph.PARAM_OLD);

        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        RectF rectF = new RectF();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        /*if (sizeWightBar > 50)
            rectF.set((float) (paddingBar + sizeWightBar - 50), upperBound, canvas.getWidth()-paddingBar, lowerBound);
        else
            rectF.set((float) (paddingBar), upperBound, canvas.getWidth()-paddingBar, lowerBound);*/
        rectF.set((float) (paddingBar), upperBound, canvas.getWidth()-paddingBar, lowerBound);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
       /* if (sizeWightBar > 50)
            rectF.set((float) (paddingBar + sizeWightBar - 50), upperBound, canvas.getWidth()-paddingBar, lowerBound);
        else
            rectF.set((float) (paddingBar), upperBound, canvas.getWidth()-paddingBar, lowerBound);*/
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
            canvas.drawLine((float) paddingBar, upperBound + 15, 100, upperBound + 15, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(25);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(2);
            canvas.drawText(setMaskText((double) DataForGraph.PARAM_NEW, "mouth"), (float) (100), upperBound + 23, paint);
        }
        else
            canvas.drawText(setMaskText((double) DataForGraph.PARAM_NEW, "mouth"), (float) (sizeWightBar / 2), upperBound + 23, paint);

    }
}


public class DataForGraph{
    public static Double SUM = 1.0;
    public static Double OVER = 1.0;

    public static Double TERM = 1.;
    public static Double NEW_TERM = 1.;

    public static int PARAM_OLD = 1;
    public static int PARAM_NEW = 1;

    public static int HEIGHT_OVER = 50;
    public static int HEIGHT_TERM = 100;

    public static boolean CREATE_GRAPH_OVER;
    public static boolean CREATE_GRAPH_TERM;
    public static boolean CREATE_GRAPH_OVER_INFO;

    public void setSum(Double sum){
        SUM = sum;
    }

    public void setOver(Double over){
        OVER = over;
    }

    public void setParamOlr(int term){
        PARAM_OLD = term;
    }

    public void setParamNew(int newTerm){
        PARAM_NEW = newTerm;
    }

    public void setHeightTerm(int height){
        HEIGHT_TERM = height;
    }

    public void setHeightOver(int height){
        HEIGHT_OVER= height;
    }

    public void createOver(boolean bool){
        CREATE_GRAPH_OVER = bool;
    }

    public void createTerm(boolean bool){
        CREATE_GRAPH_TERM = bool;
    }

}