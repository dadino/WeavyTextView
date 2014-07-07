package com.dadino.android.animatingtextsample;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;



public class AnimatedTextView extends TextView {
    //Frame per seconds
    private static final int FPS = 60;

    //Animation Speed
    private static final float SPEED_MULTIPLIER = 1f;
    private static final float INCREMENT_PER_SECOND =  4.2f * SPEED_MULTIPLIER;
    private static final float SPEED = INCREMENT_PER_SECOND/FPS;
    private static final int UPDATE_TIMER_MILLIS = 1000/FPS;

    //Text modifications params
    private static final float MAX_TEXTSIZE_MULTIPLIER = 1.5f;
    private static final float MIN_TEXTSIZE_MULTIPLIER = 1.0f;
    private static final float DELTA_TEXTSIZE_MULTIPLIER = MAX_TEXTSIZE_MULTIPLIER - MIN_TEXTSIZE_MULTIPLIER;

    private Handler handler;
    private int length;
    private SpannableString spannableText;


    private float originalSize;
    private float timer = 0;
    private float delta;
    private int newSize;

    private FastAbsoluteSizeSpan[] sizeSpans;
    private boolean isAnimatingSpan;

    public AnimatedTextView(Context context) {
        this(context, null);
    }

    public AnimatedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //Click listener for testing purpose
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnimatingSpan()) {
                    stopAnimatingSpan();
                } else {
                    startAnimatingSpan();
                }
            }
        });
        handler = new Handler();

        startAnimatingSpan();
    }

    public void startAnimatingSpan() {
        String text = this.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            length = this.getText().length();
            spannableText = new SpannableString(text);
            originalSize = this.getTextSize();
            sizeSpans = new FastAbsoluteSizeSpan[length];
            for (int i = 0; i < sizeSpans.length; i++) {
                sizeSpans[i] = new FastAbsoluteSizeSpan(1);
            }

            handler.postDelayed(spanUpdater, UPDATE_TIMER_MILLIS);
            setAnimatingSpan(true);
        }
    }


    private Runnable spanUpdater = new Runnable() {
        @Override
        public void run() {
            setTextAnim();
            handler.postDelayed(this, UPDATE_TIMER_MILLIS);
        }
    };

    public void stopAnimatingSpan() {
        setAnimatingSpan(false);
        handler.removeCallbacks(spanUpdater);
    }


    private void setTextAnim() {
        if (timer >= Float.MAX_VALUE) timer = 0;
        timer += SPEED;
        for (int i = 0; i < length; i++) {
            delta = (DELTA_TEXTSIZE_MULTIPLIER * (float) Math.sin((2 * Math.PI * (i + timer)) / 5)) + 1;
            newSize = Math.round(originalSize * delta);
            sizeSpans[i].setSize(newSize);
            spannableText.setSpan(sizeSpans[i], i, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        this.setText(spannableText);
    }

    public boolean isAnimatingSpan() {
        return isAnimatingSpan;
    }

    public void setAnimatingSpan(boolean isAnimatingSpan) {
        this.isAnimatingSpan = isAnimatingSpan;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimatingSpan();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimatingSpan();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            stopAnimatingSpan();
        } else {
            startAnimatingSpan();
        }
    }
}
