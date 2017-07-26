package me.cassiano.cutecurrencyview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class CuteCurrencyView extends FrameLayout {

    private EditText actualEditText;
    private LinearLayout animatedPlaceholder;

    private boolean autoStart;

    private CharSequence text;
    private CharSequence hint;

    @ColorInt
    private int textColor;
    @ColorInt
    private int hintColor;
    @Dimension
    private float textSize;


    public CuteCurrencyView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CuteCurrencyView(@NonNull Context context,
                            @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
        init(context);
    }

    public CuteCurrencyView(@NonNull Context context,
                            @Nullable AttributeSet attrs,
                            @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(context, attrs);
        init(context);
    }

    public void startAnimation() {

        if (TextUtils.isEmpty(actualEditText.getText())
                && TextUtils.isEmpty(actualEditText.getHint())) {
            // don't do anything if everything is empty
            // but make the EditText visible and takes the placeholder
            // out of the hierarchy
            actualEditText.setVisibility(VISIBLE);
            animatedPlaceholder.setVisibility(GONE);
            return;
        }

        TextPaint paint = new TextPaint();
        paint.set(actualEditText.getPaint());

        // decides what is going to be animated
        if (TextUtils.isEmpty(actualEditText.getText())) {
            // animate hint
            paint.setColor(actualEditText.getCurrentHintTextColor());
            animateCharSequence(actualEditText.getHint(), paint);
        } else {
            // animate text
            paint.setColor(actualEditText.getCurrentTextColor());
            animateCharSequence(actualEditText.getText(), paint);
        }

    }

    private void animateCharSequence(CharSequence charSequence, TextPaint paint) {
        final View[] viewsToAnimate = new View[charSequence.length()];

        for (int i = 0; i < charSequence.length(); i++) {
            View view = new CharView(getContext(), paint, charSequence.charAt(i));
            viewsToAnimate[i] = view;
            animatedPlaceholder.addView(view);
        }

        viewsToAnimate[0]
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        viewsToAnimate[0].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        animateCharViews(viewsToAnimate, animatedPlaceholder, actualEditText);
                    }
                });
    }

    private void animateCharViews(View[] viewsToAnimate,
                                  final LinearLayout parentView, final EditText editText) {

        // first, make sure the items are out of sight
        // by moving them by their height (maybe use parent's height?)

        for (View viewToAnimate : viewsToAnimate) {
            int height = viewToAnimate.getHeight();
            float y = viewToAnimate.getY();
            viewToAnimate.setY(y + height);
        }

        // now the whole container can be visible since the views are clipped
        parentView.setVisibility(View.VISIBLE);

        for (int i = 0; i < viewsToAnimate.length; i++) {

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(viewsToAnimate[i], "translationY", 0));
            animatorSet.setInterpolator(new SimpleSpringInterpolator());
            animatorSet.setDuration(700);
            animatorSet.setStartDelay(i * 100);

            if (i == viewsToAnimate.length - 1) {
                // when the last char finishes animating,
                // hide parent container and show the EditText

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parentView.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(editText.getText()))
                            editText.setSelection(editText.getText().length());
                        editText.setVisibility(View.VISIBLE);
                    }
                });
            }

            animatorSet.start();
        }
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.CuteCurrencyView);

        text = attributes.getText(R.styleable.CuteCurrencyView_android_text);
        hint = attributes.getText(R.styleable.CuteCurrencyView_android_hint);
        textColor = attributes.getColor(R.styleable.CuteCurrencyView_android_textColor, -1);
        hintColor = attributes.getColor(R.styleable.CuteCurrencyView_android_textColorHint, -1);
        textSize = attributes.getDimensionPixelSize(R.styleable.CuteCurrencyView_android_textSize, -1);
        autoStart = attributes.getBoolean(R.styleable.CuteCurrencyView_autoStart, true);

        attributes.recycle();

    }

    private void init(Context context) {

        FrameLayout.LayoutParams linearLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.gravity = Gravity.CENTER;

        FrameLayout.LayoutParams editTextLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editTextLayoutParams.gravity = Gravity.CENTER;

        animatedPlaceholder = new LinearLayout(context);
        animatedPlaceholder.setVisibility(INVISIBLE);
        animatedPlaceholder.setOrientation(LinearLayout.HORIZONTAL);

        actualEditText = new EditText(context);
        actualEditText.setBackground(null);
        actualEditText.setGravity(Gravity.CENTER);
        actualEditText.setIncludeFontPadding(false);
        actualEditText.setVisibility(INVISIBLE);
        if (!TextUtils.isEmpty(text))
            actualEditText.setText(text);
        if (!TextUtils.isEmpty(hint))
            actualEditText.setHint(hint);
        if (textColor != -1)
            actualEditText.setTextColor(textColor);
        if (hintColor != -1)
            actualEditText.setHintTextColor(hintColor);
        if (textSize != -1)
            actualEditText.setTextSize(textSize);

        addView(animatedPlaceholder, linearLayoutParams);
        addView(actualEditText, editTextLayoutParams);

        if (autoStart) startAnimation();
    }
}
