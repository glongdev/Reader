package com.glong.sample.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.glong.sample.R;

/**
 * Created by Garrett on 2018/11/29.
 * contact me krouky@outlook.com
 */
public class MenuView extends FrameLayout {

    private boolean isShowing;
    private static final int ANIMATION_DURATION = 200;

    private Toolbar mToolbar;
    private FrameLayout mBottomMenu;

    public MenuView(@NonNull Context context) {
        this(context, null);
    }

    public MenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuView(@NonNull final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.reader_menu_layout, this);
        mToolbar = findViewById(R.id.tool_bar);
        mBottomMenu = findViewById(R.id.bottom_menu);

        ((Activity) context).getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ((Activity) context).getWindow().getDecorView().removeOnLayoutChangeListener(this);
                mToolbar.setTranslationY(-mToolbar.getHeight());
                mBottomMenu.setTranslationY(mBottomMenu.getHeight());
                setVisibility(GONE);
            }
        });
        setClickable(true);
    }

    private AnimatorSet mShowAnim;

    public void show() {
        setVisibility(View.VISIBLE);
        post(new Runnable() {
            @Override
            public void run() {
                showAnim();
            }
        });
    }

    private void showAnim() {
        if (mShowAnim == null) {
            ObjectAnimator toolbarAnim = ObjectAnimator.ofFloat(mToolbar, "translationY", -mToolbar.getHeight(), 0);
            ObjectAnimator bottomMenuAnim = ObjectAnimator.ofFloat(mBottomMenu, "translationY", mBottomMenu.getHeight(), 0);
            mShowAnim = new AnimatorSet();
            mShowAnim.play(toolbarAnim).with(bottomMenuAnim);
            mShowAnim.setDuration(ANIMATION_DURATION);
            mShowAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isShowing = true;
                }
            });
        }
        if (!mShowAnim.isRunning()) {
            mShowAnim.start();
        }
    }

    private AnimatorSet mDismissAnim;

    public void dismiss() {
        if (mDismissAnim == null) {
            ObjectAnimator toolbarAnim = ObjectAnimator.ofFloat(mToolbar, "translationY", -mToolbar.getHeight());
            ObjectAnimator bottomMenuAnim = ObjectAnimator.ofFloat(mBottomMenu, "translationY", mBottomMenu.getHeight());
            mDismissAnim = new AnimatorSet();
            mDismissAnim.play(toolbarAnim).with(bottomMenuAnim);
            mDismissAnim.setDuration(ANIMATION_DURATION);
            mDismissAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.GONE);
                    isShowing = false;
                }
            });
        }
        if (!mDismissAnim.isRunning()) {
            mDismissAnim.start();
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    private boolean tempShowing;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tempShowing = isShowing;
                break;
            case MotionEvent.ACTION_UP:
                if (tempShowing && isShowing)
                    dismiss();
                break;
        }
        return super.onTouchEvent(event);
    }
}
