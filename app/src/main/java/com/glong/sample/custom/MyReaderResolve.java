package com.glong.sample.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;

import com.glong.reader.textconvert.TextBreakUtils;
import com.glong.reader.widget.ReaderResolve;
import com.glong.sample.MyApplication;
import com.glong.sample.R;

/**
 * Created by Garrett on 2018/12/6.
 * contact me krouky@outlook.com
 */
public class MyReaderResolve extends ReaderResolve {

    private int mChapterTitleWidth;

    @Override
    public void calculateChapterParameter() {
        int paddingLeft = mReaderConfig.getPadding()[0];
        int paddingTop = mReaderConfig.getPadding()[1];
        int paddingRight = mReaderConfig.getPadding()[2];
        int paddingBottom = mReaderConfig.getPadding()[3];
        //除边缘区域外的区域宽度
        int usableWidth = mAreaWidth - paddingLeft - paddingRight;
        //除边缘区域外的区域高度
        int usableHeight = mAreaHeight - paddingTop - paddingBottom;

        //计算大标题转换为行List
        mChapterNameLines = TextBreakUtils.breakToLineList(mTitle, usableHeight, 0, mChapterPaint);

        //计算第0页大章节标题所占宽度
        float titleWidth = mChapterPaint.measureText("正");
        float bodyWidth = mMainBodyPaint.measureText("正");
        mChapterTitleWidth = (int) (mChapterNameLines.size() * (titleWidth + mReaderConfig.getLineSpace()) * 1.5f);

        //第0页 能展示多少行正文
        mLineNumPerPageInFirstPage = (int) ((usableWidth - mChapterTitleWidth) / (bodyWidth + mReaderConfig.getLineSpace()));

        //正常情况下（除第0页）一页能展示多少行
        mLineNumPerPageWithoutFirstPage = (int) (usableWidth / (bodyWidth + mReaderConfig.getLineSpace()));

        //正文所占行数
        if (!android.text.TextUtils.isEmpty(mContent)) {
            mShowLines = TextBreakUtils.breakToLineList(mContent, usableHeight, 0, mMainBodyPaint);
        }
        //计算总页数
        if (mShowLines != null) {
            mPageSum = mShowLines.size() - mLineNumPerPageInFirstPage <= 0 ?
                    1 : ((mShowLines.size() - mLineNumPerPageInFirstPage) - 1) / Math.max(1, mLineNumPerPageWithoutFirstPage) + 1 + 1;
            calculatePageIndex();
        } else {
            mPageSum = -1;
        }
    }

    @Override
    protected void maybeDrawChapterTitle(Canvas canvas) {
        if (mPageIndex == 0) {
            mChapterPaint.setFakeBoldText(true);
            Paint.FontMetrics pfm = mChapterPaint.getFontMetrics();

            float x = mAreaWidth - (mReaderConfig.getPadding()[2] + mChapterPaint.measureText("正") + mReaderConfig.getLineSpace() / 2);
            for (int i = 0; i < mChapterNameLines.size(); i++) {
                float y = mReaderConfig.getPadding()[1] + pfm.bottom - pfm.top;
                String lineData = mChapterNameLines.get(i).getLineData();
                for (int j = 0; j < lineData.length(); j++) {
                    canvas.drawText(lineData.substring(j, j + 1), x, y, mChapterPaint);
                    y += (pfm.bottom - pfm.top);
                }
                x -= (mChapterPaint.measureText("正") + mReaderConfig.getLineSpace());
            }
        }
    }

    @Override
    protected void drawMainBodyArea(Canvas canvas) {
        //画正文
        if (mShowLines == null || mShowLines.size() <= 0)
            return;

        Paint.FontMetrics fm = mMainBodyPaint.getFontMetrics();
        float textHeight = fm.bottom - fm.top;

        float x = mAreaWidth - (mReaderConfig.getPadding()[2] + mMainBodyPaint.measureText("正") + mReaderConfig.getLineSpace() / 2);
        if (mPageIndex == 0) {
            x -= mChapterTitleWidth;
        }

        for (int i = 0; i < (mPageIndex == 0 ? mLineNumPerPageInFirstPage : mLineNumPerPageWithoutFirstPage); i++) {
            //要画的这行在整章中的索引（那么在整章中前面就有 索引数 行数）
            float y = mReaderConfig.getPadding()[1] + textHeight;

            int lineInChapter;
            if (mPageIndex == 0) {
                lineInChapter = i;
            } else {
                lineInChapter = mLineNumPerPageWithoutFirstPage * (mPageIndex - 1) + mLineNumPerPageInFirstPage + i;
            }
            //如果是最后一页可能当前行数超过总行数
            if (lineInChapter > mShowLines.size() - 1) {
                break;
            }

            String lineData = mShowLines.get(lineInChapter).getLineData();
            for (int j = 0; j < lineData.length(); j++) {
                canvas.drawText(lineData.substring(j, j + 1), x, y, mMainBodyPaint);
                y += textHeight;
            }
            x -= (mReaderConfig.getLineSpace() + mMainBodyPaint.measureText("正"));
        }
    }

    @Override
    protected void drawMarginTitle(Canvas canvas, @Nullable String title, int x, int y, Paint paint) {
//        super.drawMarginTitle(canvas, title, x, y, paint);
    }

    @Override
    protected void drawPercentage(Canvas canvas, float percent, float x, float y, Paint marginPaint) {
//        super.drawPercentage(canvas, percent, x, y, marginPaint);
        // 不画页码只需要注掉super.drawPagination()既可.
    }

    @Override
    protected void drawBattery(Canvas canvas, Paint batteryPaint, int battery, Rect outRect, Rect innerRect) {
        Bitmap batteryBp = BitmapFactory.decodeResource(MyApplication.getApplication().getResources(), R.drawable.battery);
        int center = outRect.top + outRect.height() / 2;
        Rect rect = new Rect(outRect.left, center - 30, outRect.left + 60, center + 30);
        canvas.drawBitmap(batteryBp, null, rect, null);
    }
}
