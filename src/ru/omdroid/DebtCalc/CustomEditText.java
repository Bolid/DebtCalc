package ru.omdroid.DebtCalc;

import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;

/**
 * TextView that draws a bubble behind the text. We cannot use a LineBackgroundSpan
 * because we want to make the bubble taller than the text and TextView's clip is
 * too aggressive.
 */
public class CustomEditText extends TextView {
    private static final float CORNER_RADIUS = 14.0f;
    private static final float PADDING_H = 10.0f;
    private static final float PADDING_V = 5.0f;

    private final RectF mRect = new RectF();
    private Paint mPaint;
    private Drawable mDrawableBottom;

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getContext().getResources().getColor(R.color.buttonNextStroke));
    }

    @Override
    public void onDraw(Canvas canvas) {
        final Layout layout = getLayout();
        final RectF rect = mRect;
        final int left = getCompoundPaddingLeft();
        final int top = getExtendedPaddingTop();

        rect.set(left + layout.getLineLeft(0) - PADDING_H,
                top + layout.getLineTop(0) - PADDING_V,
                Math.min(left + layout.getLineRight(0) + PADDING_H, getScrollX() + getWidth()),
                top + layout.getLineBottom(0) + PADDING_V);
        canvas.drawRoundRect(rect, 25, 25, mPaint);

        super.onDraw(canvas);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top,
                                                        Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        mDrawableBottom = bottom;
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        if (mDrawableBottom == drawable) {
            final Rect dirty = drawable.getBounds();

            // Assume paddingLeft == paddingRight
            final int drawableWidth = dirty.right - dirty.left;
            final int left = (getWidth() - drawableWidth) / 2 + getScrollX();

            // Assume we draw the bottom drawable at the bottom
            final int drawableHeight = dirty.bottom - dirty.top;
            final int top = getHeight() - getPaddingBottom() - drawableHeight + getScrollY();

            invalidate(left, top, left + drawableWidth, top + drawableHeight);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return mDrawableBottom == who || super.verifyDrawable(who);
    }
}
