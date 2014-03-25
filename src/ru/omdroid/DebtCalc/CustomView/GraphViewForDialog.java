package ru.omdroid.DebtCalc.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class GraphViewForDialog extends View {

    public GraphViewForDialog(Context context) {
        super(context);
    }
    public GraphViewForDialog(Context context, AttributeSet attrs) {
    super(context, attrs);
}
    public GraphViewForDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        RectF rectF = new RectF();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        rectF.set((float) (50), 50, canvas.getWidth()-50, 80);
        canvas.drawRoundRect(rectF, 15, 15, paint);
        canvas.restore();
    }

}
