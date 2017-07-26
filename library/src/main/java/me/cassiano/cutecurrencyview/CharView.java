package me.cassiano.cutecurrencyview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

class CharView extends View {

    private StaticLayout layout;
    private char letter;
    private TextPaint originalPaint;
    Rect textBounds = new Rect();

    public CharView(Context context) {
        super(context);
    }

    public CharView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CharView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CharView(Context context, TextPaint originalPaint, char letter) {
        super(context);
        this.letter = letter;
        this.originalPaint = originalPaint;
        layout = new StaticLayout(String.valueOf(letter),
                0, 1, originalPaint, 0, Layout.Alignment.ALIGN_NORMAL, 0f, 0f, false);
        originalPaint.getTextBounds(String.valueOf(letter), 0, 1, textBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float baseline = -originalPaint.ascent();
        int width = (int) (originalPaint.measureText(String.valueOf(letter)) + 0.5f); // round
        int height = (int) (baseline + originalPaint.descent() + 0.5f);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        layout.draw(canvas);
    }
}
