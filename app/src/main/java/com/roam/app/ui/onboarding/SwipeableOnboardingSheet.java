package com.roam.app.ui.onboarding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class SwipeableOnboardingSheet extends LinearLayout {
    public interface OnSwipeListener {
        void onSwipe(int direction);
    }

    private final int touchSlop;
    private float downX;
    private float downY;
    private boolean horizontalSwipe;
    private OnSwipeListener onSwipeListener;

    public SwipeableOnboardingSheet(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setOnSwipeListener(@Nullable OnSwipeListener listener) {
        onSwipeListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                horizontalSwipe = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - downX;
                float deltaY = event.getY() - downY;
                if (Math.abs(deltaX) > touchSlop && Math.abs(deltaX) > Math.abs(deltaY)) {
                    horizontalSwipe = true;
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            float deltaX = event.getX() - downX;
            int swipeThreshold = Math.max(touchSlop, Math.round(48 * getResources().getDisplayMetrics().density));
            if (horizontalSwipe && Math.abs(deltaX) >= swipeThreshold && onSwipeListener != null) {
                onSwipeListener.onSwipe(deltaX < 0 ? 1 : -1);
            }
            horizontalSwipe = false;
            return true;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            horizontalSwipe = false;
            return true;
        }
        return horizontalSwipe || super.onTouchEvent(event);
    }
}
