package com.glong.reader.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.glong.reader.TurnStatus;
import com.glong.reader.util.Log;

/**
 * Created by Garrett on 2018/11/20.
 * contact me krouky@outlook.com
 */
public class EffectOfCover extends Effect {

    private static final int ANIM_DURATION = 1200;
    private float mMoveVector;
    private Paint mShadowPaint;
    private int mShadowWidth;

    public EffectOfCover(Context context) {
        super(context);
        initShadowPaint();
    }

    private void initShadowPaint() {
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowWidth = 35;
        LinearGradient linearGradient = new LinearGradient(0, 0, mShadowWidth, 0, 0x00111111, 0x55111111, LinearGradient.TileMode.MIRROR);
        mShadowPaint.setShader(linearGradient);
    }

    private float mDownX;
    private long mDownTime;
    private float mStartMoveX;
    private TurnStatus mTurnStatus = TurnStatus.IDLE;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果是多点触控 ，不处理
        if (event.getPointerCount() > 1) {
            return true;
        }

        float x = event.getX();
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTurnStatus = TurnStatus.IDLE;
                this.mDownX = x;
                this.mDownTime = System.currentTimeMillis();

                abortAnimation();
                mPageDrawingCallback.drawCurrPage();
                mPageDrawingCallback.invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                float dxFromDown = x - mDownX;

                // 如果滑动需要处理翻页
                if (Math.abs(dxFromDown) >= mTouchSlop) {
                    if (mTurnStatus == TurnStatus.IDLE) {
                        mStartMoveX = x;
                        if (dxFromDown > 0) {
                            mTurnStatus = mPageChangedCallback.toPrevPage();
                        } else {
                            mTurnStatus = mPageChangedCallback.toNextPage();
                        }
                        if (mTurnStatus == TurnStatus.LOAD_SUCCESS) {
                            Log.d(getClass().getSimpleName(), "drawNextPage -- ");
                            mPageDrawingCallback.drawNextPage();
                        }
                    }
                }

                //如果已经翻页成功，需要处理移动时页面的刷新
                if (mTurnStatus == TurnStatus.LOAD_SUCCESS) {
                    float dxMove;
                    if (mStartMoveX - mDownX < 0) { // 向左滑 --> 下一页
                        dxMove = x - mStartMoveX;
                    } else {
                        dxMove = x;
                    }
                    if (dxMove * (mStartMoveX - mDownX) >= 0) {
                        mMoveVector = dxMove;
                        mPageDrawingCallback.invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                // 单击 事件
                long upTime = System.currentTimeMillis();
                if (mTurnStatus == TurnStatus.IDLE && upTime - mDownTime <= mLongClickTime) {
                    if (x > mEffectWidth / 2) {
                        //向左滑 --> 下一页
                        if (mPageChangedCallback.toNextPage() == TurnStatus.LOAD_SUCCESS) {
                            Log.d(getClass().getSimpleName(), "start scroll to next page!");
                            mPageDrawingCallback.drawNextPage();
                            mScroller.startScroll(0, 0, -mEffectWidth - mShadowWidth, 0, ANIM_DURATION);
                            mPageDrawingCallback.invalidate();
                        }
                    } else {
                        // 向右滑 --> 上一页
                        if (mPageChangedCallback.toPrevPage() == TurnStatus.LOAD_SUCCESS) {
                            mPageDrawingCallback.drawNextPage();
                            mScroller.startScroll(0, 0, mEffectWidth, 0, ANIM_DURATION);
                            mPageDrawingCallback.invalidate();
                        }
                    }
                }

                // 翻页成功，手指抬起来时，处理自动翻页
                else if (mTurnStatus == TurnStatus.LOAD_SUCCESS) {
                    mVelocityTracker.computeCurrentVelocity(1000);
                    Log.d(getClass().getSimpleName(), "xVelocity == " + mVelocityTracker.getXVelocity());
                    if (mMoveVector <= 0) {
                        // 向左滑 --> 下一页
                        // 分两种情况，顺利到达下一页 and 取消到下一页
                        if (mVelocityTracker.getXVelocity() >= 500) {// 取消至下一页
                            mScroller.startScroll((int) mMoveVector, 0, (int) -mMoveVector, 0, ANIM_DURATION);
                            mPageChangedCallback.toPrevPage();// 数据回滚
                        } else {
                            mScroller.startScroll((int) mMoveVector, 0, (int) (-mEffectWidth - mMoveVector - mShadowWidth), 0, ANIM_DURATION);
                        }
                        mPageDrawingCallback.invalidate();
                    } else {
                        // 向右滑 --> 上一页
                        // 分两种情况，顺利到达上一页 and 取消到上一页
                        if (mVelocityTracker.getXVelocity() <= -500) {// 取消至上一页
                            mScroller.startScroll((int) mMoveVector, 0, (int) -mMoveVector, 0, ANIM_DURATION / 4);
                            mPageChangedCallback.toNextPage();// 数据回滚
                        } else {
                            mScroller.startScroll((int) mMoveVector, 0, (int) (mEffectWidth - mMoveVector), 0, ANIM_DURATION);
                        }
                        mPageDrawingCallback.invalidate();
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            mMoveVector = mScroller.getCurrX();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mMoveVector = mScroller.getCurrX();
            mPageDrawingCallback.invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawCurrPage(canvas);
        drawNextPage(canvas);
        drawShadow(canvas);
    }

    private void drawCurrPage(Canvas canvas) {
        canvas.save();
        // 向左滑 --> 下一页
        if (mMoveVector <= 0) {
            canvas.translate(mMoveVector, 0);
        } else {
            canvas.clipRect(mMoveVector, 0, mEffectWidth, mEffectHeight);
        }
        canvas.drawBitmap(mCurrPageBitmap, 0, 0, null);
        canvas.restore();
    }

    private void drawNextPage(Canvas canvas) {
        canvas.save();
        // 向左滑 --> 下一页
        if (mMoveVector <= 0) {
            canvas.clipRect(mEffectWidth + mMoveVector, 0, mEffectWidth, mEffectHeight);
        } else {
            canvas.translate(mMoveVector - mEffectWidth, 0);
        }
        canvas.drawBitmap(mNextPageBitmap, 0, 0, null);
        canvas.restore();
    }

    private void drawShadow(Canvas canvas) {
        canvas.save();
        if (mMoveVector <= 0) {
            canvas.translate(mMoveVector, 0);
        } else {
            canvas.translate(mMoveVector - mEffectWidth, 0);
        }
        canvas.drawRect(mEffectWidth, 0, mEffectWidth + mShadowWidth, mEffectHeight, mShadowPaint);
        canvas.restore();
    }
}
