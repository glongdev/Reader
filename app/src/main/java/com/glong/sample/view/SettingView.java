package com.glong.sample.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.glong.sample.R;

/**
 * Created by Garrett on 2018/12/5.
 * contact me krouky@outlook.com
 */
public class SettingView extends FrameLayout {

    private boolean isShowing;
    private LinearLayout mSettingContainer;

    public SettingView(@NonNull Context context) {
        this(context, null);
    }

    public SettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingView(@NonNull final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.reader_setting_layout, this);
        mSettingContainer = findViewById(R.id.setting_container);

        ((Activity) context).getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ((Activity) context).getWindow().getDecorView().removeOnLayoutChangeListener(this);
                mSettingContainer.setTranslationY(mSettingContainer.getHeight());
                setVisibility(GONE);
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void dismiss() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mSettingContainer, "translationY", mSettingContainer.getHeight());
        anim.setDuration(200).start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.GONE);
                isShowing = false;
            }
        });
    }

    public void show() {
        setVisibility(View.VISIBLE);
        isShowing = true;
        post(new Runnable() {
            @Override
            public void run() {
                Log.d(SettingView.class.getSimpleName(), "showAnim translationY:" + mSettingContainer.getTranslationY());
                ObjectAnimator.ofFloat(mSettingContainer, "translationY", 0)
                        .setDuration(200).start();
            }
        });
    }

    public boolean isShowing() {
        return isShowing;
    }
}
