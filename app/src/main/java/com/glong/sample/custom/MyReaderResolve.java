package com.glong.sample.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.glong.reader.textconvert.ShowLine;
import com.glong.reader.textconvert.TextBreakUtils;
import com.glong.reader.widget.ReaderResolve;
import com.glong.sample.MyApplication;
import com.glong.sample.R;

/**
 * Created by Garrett on 2018/12/6.
 * contact me krouky@outlook.com
 */
public class MyReaderResolve extends ReaderResolve {

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

        //计算大标题所占行数
        mChapterNameLines = TextBreakUtils.breakToLineList(mTitle, usableHeight, 5, mChapterPaint);

        //计算第0页大章节标题所占宽度
        Paint.FontMetrics chapterFM = mChapterPaint.getFontMetrics();
        mChapterTitleHeight = (int) (mChapterNameLines.size() * (chapterFM.bottom - chapterFM.top + mReaderConfig.getLineSpace()) * 1.5f);

        //第0页 能展示多少行正文
        mLineNumPerPageInFirstPage = TextBreakUtils.measureLines(usableHeight - mChapterTitleHeight, mReaderConfig.getLineSpace(), mMainBodyPaint);

        //正常情况下（除第0页）一页能展示多少行
        mLineNumPerPageWithoutFirstPage = TextBreakUtils.measureLines(usableHeight, mReaderConfig.getLineSpace(), mMainBodyPaint);

        //正文所占行数
        if (!android.text.TextUtils.isEmpty(mContent)) {
            mShowLines = TextBreakUtils.breakToLineList(mContent, usableWidth, 0, mMainBodyPaint);
        }

    }

    @Override
    protected void drawLineText(Canvas canvas, ShowLine showLine, float x, float y, float textHeight) {
        super.drawLineText(canvas, showLine, x, y, textHeight);
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
