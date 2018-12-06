package com.glong.reader.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.glong.reader.config.ColorsConfig;
import com.glong.reader.config.ReaderConfig;
import com.glong.reader.textconvert.ShowChar;
import com.glong.reader.textconvert.ShowLine;
import com.glong.reader.textconvert.TextBreakUtils;
import com.glong.reader.util.DLog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public class ReaderResolve {
    private static final String TAG = ReaderResolve.class.getSimpleName();

    public static final int LAST_INDEX = -1;//表示最后一个
    public static final int FIRST_INDEX = 0;//表示第一个
    public static final int UNKNOWN = -2;//未知，在实际翻页时动态计算

    //本章在总章节中的索引
    protected int mChapterIndex;

    //当前页第一个字符在本章中
    protected int mCharIndex;


    //当前章节的文字内容(正文)
    protected String mContent = "";

    //当前章节标题
    protected String mTitle = "";

    // 总章节数
    protected int mChapterSum;

    protected int mAreaWidth;
    protected int mAreaHeight;

    // 电池电量
    private int mBattery = 50;
    /***************************人工智能分割线 外部设置的属性end************************/

    //当前章节总页数，页数根据文字间距，字体大小而变化
    protected int mPageSum;

    //本章中页码
    protected int mPageIndex;

    protected List<ShowLine> mShowLines;
    protected List<ShowLine> mChapterNameLines;

    protected int mLineNumPerPageWithoutFirstPage;//一页能展示多少行（除第0页）
    protected int mLineNumPerPageInFirstPage;//一页能展示多少行（第0页）
    protected int mChapterTitleHeight;//第0页大章节名称所占高度

    /****************************通过计算的属性end*************************************/

    protected Paint mMainBodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//正文的画笔
    protected Paint mMarginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//画边缘的画笔，如：页码、章节名称、电池等
    protected Paint mChapterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画章节名称的Paint
    protected Paint mBatteryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画电池的画笔

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);

    protected DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    /**
     * 界面布局配置，此处依赖{@link ReaderConfig}是因为需要使用TextSize计算总页数
     */
    protected ReaderConfig mReaderConfig;

    public ReaderResolve() {
        // set a default ReaderConfig
        mReaderConfig = new ReaderConfig.Builder().build();
        initPaints();
    }

    public ReaderResolve(@NonNull ReaderConfig readerConfig) {
        this.mReaderConfig = readerConfig;
        initPaints();
    }

    private void initPaints() {
        mMainBodyPaint.setColor(mReaderConfig.getColorsConfig().getTextColor());
        mMainBodyPaint.setTextSize(mReaderConfig.getTextSize());

        mChapterPaint.setTextSize(mReaderConfig.getTextSize() * 1.4f);
        mChapterPaint.setColor(mReaderConfig.getColorsConfig().getTextColor());

        mMarginPaint.setColor(mReaderConfig.getColorsConfig().getTextColor());
        mMarginPaint.setTextSize(40);

        mBatteryPaint.setColor(mReaderConfig.getColorsConfig().getBatteryColor());
        mBatteryPaint.setStrokeWidth(2);
    }


    /**
     * 计算当前章节的相关参数
     * eg: mPageSum，总行数等等。。
     */
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
        mChapterNameLines = TextBreakUtils.breakToLineList(mTitle, usableWidth, 0, mChapterPaint);

        //计算第0页大章节标题所占高度
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

        //计算总页数
        if (mShowLines != null) {
            mPageSum = mShowLines.size() - mLineNumPerPageInFirstPage <= 0 ?
                    1 : ((mShowLines.size() - mLineNumPerPageInFirstPage) - 1) / Math.max(1, mLineNumPerPageWithoutFirstPage) + 1 + 1;
            calculatePageIndex();
        } else {
            mPageSum = -1;
        }
    }

    /**
     * 根据字符索引计算页码
     */
    protected void calculatePageIndex() {
        DLog.d(TAG, " start calculatePageIndex --- charIndex: " + mCharIndex + " showLines.size == " + mShowLines.size());
        if (mCharIndex == FIRST_INDEX) {
            mPageIndex = 0;
        } else if (mCharIndex == LAST_INDEX) {
            mPageIndex = mPageSum - 1;
        } else if (mCharIndex >= mContent.length()) {
            mPageIndex = mPageSum - 1;
        } else {
            //step 1.计算当前字符索引在哪一行

            // 为了提升效率，如果字符索引在mContent.length()后半部分，则从后半部分遍历
            int lineIndex = 0;
            if (mCharIndex >= mContent.length() / 2) {
                for (int i = mShowLines.size() - 1; i >= 0; --i) {
                    ShowLine showLine = mShowLines.get(i);
                    if (mCharIndex >= showLine.getLineFirstIndexInChapter() && mCharIndex <= showLine.getLineLastIndexInChapter()) {
                        lineIndex = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i <= mShowLines.size() - 1; i++) {
                    ShowLine showLine = mShowLines.get(i);
                    if (mCharIndex >= showLine.getLineFirstIndexInChapter() && mCharIndex <= showLine.getLineLastIndexInChapter()) {
                        lineIndex = i;
                        break;
                    }
                }
            }
            //step 2.计算lineIndex在哪一页
            if (lineIndex <= mLineNumPerPageInFirstPage - 1) {
                mPageIndex = 0;
            } else {
                mPageIndex = (lineIndex - mLineNumPerPageInFirstPage) / mLineNumPerPageWithoutFirstPage + 1;
            }
        }
        DLog.d(TAG, "pageIndex : " + mPageIndex);
    }

    public void drawPage(Canvas canvas) {
//        DLog.d(TAG, "start drawPage,title:" + mTitle + " ,content:" + mContent);
        drawBackground(canvas);
        drawMarginArea(canvas);
        if (mTitle != null) {
            maybeDrawChapterTitle(canvas);
        }
        if (mShowLines != null) {
            drawMainBodyArea(canvas);
        }
    }

    /**
     * 清空画布
     */
    private void drawBackground(Canvas canvas) {
        //step1.清空画布
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    /**
     * 画边缘区域内容，分为5步
     *
     * @param canvas 画布
     */
    protected void drawMarginArea(Canvas canvas) {
        //step1.在边缘区域画章节名称
        drawMarginTitle(canvas, mTitle, mReaderConfig.getPadding()[0], mReaderConfig.getPadding()[1], mMarginPaint);

        //step3.在边缘区域画页码
        if (mPageSum != -1) {//mPageSum == -1 代表当前页的内容为空
            String pageNumber = String.valueOf((mPageIndex + 1) + "/" + mPageSum);
            drawPagination(canvas, mPageIndex, mPageSum, mAreaWidth - mReaderConfig.getPadding()[2] - mMarginPaint.measureText(pageNumber)
                    , mReaderConfig.getPadding()[1], mMarginPaint);
        }

        Paint.FontMetrics fm = mMarginPaint.getFontMetrics();
        float baseLine2centerLine = (fm.descent - fm.ascent) / 2 - fm.descent;// 中轴线到基准线距离

        //step4.在边缘区域画总百分比
        float percent = (float) mChapterIndex * 100 / mChapterSum + (float) (mPageIndex + 1) * 100f / mPageSum / mChapterSum;
        String percentStr = decimalFormat.format(percent) + "%";
        drawPercentage(canvas, percent, mAreaWidth - mReaderConfig.getPadding()[2] - mMarginPaint.measureText(percentStr),
                mAreaHeight - mReaderConfig.getPadding()[3] / 2 + baseLine2centerLine, mMarginPaint);

        //step5.在边缘区域画电池
        int left = mReaderConfig.getPadding()[0];
        int top = mAreaHeight - mReaderConfig.getPadding()[3] / 2 - mReaderConfig.getBatteryWidthAndHeight()[1] / 2;
        int right = left + mReaderConfig.getBatteryWidthAndHeight()[0];
        int bottom = top + mReaderConfig.getBatteryWidthAndHeight()[1];
        drawBattery(canvas, mBatteryPaint, mBattery, new Rect(left, top, right, bottom), new Rect(left + 2, top + 2, right - 2, bottom - 2));

        //step6.在边缘区域画时间
        String time = dateFormat.format(new Date());
        drawTime(canvas, time, right + 20, mAreaHeight - mReaderConfig.getPadding()[3] / 2 + baseLine2centerLine, mMarginPaint);
    }

    /**
     * 画边缘地区的标题
     *
     * @param canvas Canvas
     * @param title  标题名称
     * @param x      x
     * @param y      baseline
     * @param paint  Paint
     */
    protected void drawMarginTitle(Canvas canvas, @Nullable String title, int x, int y, Paint paint) {
        if (title != null)
            canvas.drawText(title, x, y, paint);
    }

    /**
     * 画页码
     *
     * @param canvas    Canvas
     * @param pageIndex page索引
     * @param pageSum   总页书
     * @param x         x
     * @param y         baseline
     * @param paint     Paint
     */
    protected void drawPagination(Canvas canvas, int pageIndex, int pageSum, float x, int y, Paint paint) {
        String pageNumber = String.valueOf((pageIndex + 1) + "/" + pageSum);
        canvas.drawText(pageNumber, x, y, paint);
    }

    /**
     * 画电池
     *
     * @param canvas       画布
     * @param batteryPaint 电池画笔
     * @param battery      电池量
     * @param outRect      外部Rect
     * @param innerRect    内部Rect
     */
    protected void drawBattery(Canvas canvas, Paint batteryPaint, int battery, Rect outRect, Rect innerRect) {
        batteryPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(outRect, batteryPaint);

        batteryPaint.setStyle(Paint.Style.FILL);
        innerRect.right = (int) (innerRect.left + innerRect.width() * (battery * 1f / 100));
        canvas.drawRect(innerRect, batteryPaint);
        canvas.drawRect(outRect.right, outRect.top + outRect.height() / 3,
                outRect.right + outRect.height() / 4, outRect.bottom - outRect.height() / 3, batteryPaint);
    }

    /**
     * 画时间
     *
     * @param canvas      canvas
     * @param x           x
     * @param y           baseline
     * @param marginPaint Paint
     */
    protected void drawTime(Canvas canvas, String time, int x, float y, Paint marginPaint) {
        DLog.d(TAG, "drawTime, x:" + x + " ,y:" + y);
        canvas.drawText(time, x, y, marginPaint);
    }

    /**
     * 画总百分比
     *
     * @param canvas      canvas
     * @param percent     百分数
     * @param x           x
     * @param y           baseline
     * @param marginPaint Paint
     */
    protected void drawPercentage(Canvas canvas, float percent, float x, float y, Paint marginPaint) {
        if (percent <= 100)
            canvas.drawText(decimalFormat.format(percent) + "%", x, y, marginPaint);
    }

    /**
     * 如果是第0页需要画章节大标题
     *
     * @param canvas 画布
     */
    protected void maybeDrawChapterTitle(Canvas canvas) {
        //如果时第0页需要画章节大标题
        if (mPageIndex == 0) {
            Paint.FontMetrics pfm = mChapterPaint.getFontMetrics();
            float y = (int) (mReaderConfig.getPadding()[1] + (pfm.bottom - pfm.top)) + mReaderConfig.getLineSpace() / 2;
            for (int i = 0; i < mChapterNameLines.size(); i++) {
                canvas.drawText(mChapterNameLines.get(i).getLineData(), mReaderConfig.getPadding()[0], y, mChapterPaint);
                y += (pfm.bottom - pfm.top) + mReaderConfig.getLineSpace();
            }
        }
    }

    /**
     * 画正文
     *
     * @param canvas 画布
     */
    protected void drawMainBodyArea(Canvas canvas) {
        //画正文
        if (mShowLines == null || mShowLines.size() <= 0)
            return;

        Paint.FontMetrics fm = mMainBodyPaint.getFontMetrics();
        float textHeight = fm.bottom - fm.top;

        float x = mReaderConfig.getPadding()[0];
        float y = mReaderConfig.getPadding()[1] + textHeight + mReaderConfig.getLineSpace() / 2;
        if (mPageIndex == 0) {
            y += mChapterTitleHeight;
        }
        for (int i = 0; i < (mPageIndex == 0 ? mLineNumPerPageInFirstPage : mLineNumPerPageWithoutFirstPage); i++) {
            //要画的这行在整章中的索引（那么在整章中前面就有 索引数 行数）
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
            ShowLine showLine = mShowLines.get(lineInChapter);
            drawLineText(canvas, showLine, x, y, textHeight);
            y += (textHeight + mReaderConfig.getLineSpace());
        }
    }

    /**
     * 第一页  第二页  第三页
     * 0      5       10
     * 1      6       11
     * 2      7       12
     * 3      8
     * 4      9
     *
     * @param canvas
     */
    protected void drawLineText(Canvas canvas, ShowLine showLine, float x, float y, float textHeight) {
        int paddingLeft = mReaderConfig.getPadding()[0];
        int paddingRight = mReaderConfig.getPadding()[2];

        float rightPosition;
        float bottomPosition = y + mMainBodyPaint.getFontMetrics().descent;
        float topPosition = bottomPosition - textHeight - mMainBodyPaint.getFontMetrics().descent;

        // 满一行并且不是以换行符号结束，为了排版整齐，挨个画字符
        if (showLine.isFullLine && !showLine.endWithWrapMark) {
            List<ShowChar> showChars = showLine.charsData;
            int charSum = showChars.size();
            float start = x;

            int retractCharNum = 0;// 缩进符数量
            String lineData = showLine.getLineData();
            // 遍历计算缩进符数量
            String tempData = showLine.getLineData();
            for (String retract : TextBreakUtils.sRetract) {
                while (tempData.startsWith(retract)) {
                    retractCharNum++;
                    tempData = tempData.substring(retractCharNum);
                }
            }

            Rect bounds = new Rect();
            mMainBodyPaint.getTextBounds(lineData, 0, retractCharNum, bounds);

            float retractsWidth = retractCharNum == 0 ? 0 : mMainBodyPaint.measureText(lineData, 0, retractCharNum - 1);
            float landscapeSpace = (mAreaWidth - paddingLeft - paddingRight - retractsWidth - showChars.get(charSum - 1).charWidth)
                    * 1f / (charSum - retractCharNum - 1);

            if (retractCharNum > 0) {
//                canvas.drawText(lineData.substring(0, retractCharNum - 1), x, y, mMainBodyPaint);
                start += retractsWidth;
            }
            for (int i = retractCharNum; i < showChars.size(); i++) {
                ShowChar showChar = showChars.get(i);
                canvas.drawText(String.valueOf(showChar.charData), start, y, mMainBodyPaint);
                showChar.rectF = new RectF(start, topPosition, start + showChar.charWidth, bottomPosition);
                start += landscapeSpace;
            }
        } else {
            canvas.drawText(showLine.getLineData(), x, y, mMainBodyPaint);

            // 计算每个字符的位置
            for (ShowChar showChar : showLine.charsData) {
                rightPosition = x + showChar.charWidth;
                showChar.rectF = new RectF(x, topPosition, rightPosition, bottomPosition);
            }
        }
    }

    /**
     * @return 当前页第一个字符索引
     */
    public int getCurrPageFirstCharIndex() {
        if (mPageIndex == 0) {
            return 0;
        } else {
            int pageFirstLineIndex = mLineNumPerPageInFirstPage + (mPageIndex - 1) * mLineNumPerPageWithoutFirstPage;
            return mShowLines.get(pageFirstLineIndex).getLineFirstIndexInChapter();
        }
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int pageIndex) {
        mPageIndex = pageIndex;
    }

    public int getChapterIndex() {
        return mChapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        mChapterIndex = chapterIndex;
    }

    public int getPageSum() {
        return mPageSum;
    }

    public void setPageSum(int pageSum) {
        mPageSum = pageSum;
    }

    public int getChapterSum() {
        return mChapterSum;
    }

    public void setChapterSum(int chapterSum) {
        mChapterSum = chapterSum;
    }

    public int getCharIndex() {
        return mCharIndex;
    }

    public void setCharIndex(int charIndex) {
        DLog.d(TAG, "charIndex change! oldCharIndex:" + mCharIndex + " ,charIndex:" + charIndex);
        mCharIndex = charIndex;
    }

    public void setArea(int areaWidth, int areaHeight) {
        DLog.d(TAG, "areaWidth:" + areaWidth + ",areaHeight:" + areaHeight);
        this.mAreaWidth = areaWidth;
        this.mAreaHeight = areaHeight;
        calculateChapterParameter();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@NonNull String title) {
        if (!title.equals(mTitle)) {
            mTitle = title;
//            calculateChapterParameter();
        }
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(@Nullable String content) {
        if (content == null) {
            mShowLines = null;
        }
        mContent = content;
        calculateChapterParameter();
    }

    ReaderConfig getReaderConfig() {
        return mReaderConfig;
    }

    void setReaderConfig(@NonNull ReaderConfig readerConfig) {
        ReaderConfig oldReaderConfig = mReaderConfig;
        mReaderConfig = readerConfig;

        int oldTextSize = oldReaderConfig.getTextSize();
//        int[] oldBatteryWidthAndHeight = oldReaderConfig.getBatteryWidthAndHeight();
        int oldLineSpace = oldReaderConfig.getLineSpace();
        int[] oldPadding = oldReaderConfig.getPadding();
        ColorsConfig oldColorsConfig = oldReaderConfig.getColorsConfig();

        int newTextSize = readerConfig.getTextSize();
//        int[] newBatteryWidthAndHeight = readerConfig.getBatteryWidthAndHeight();
        int newLineSpace = readerConfig.getLineSpace();
        int[] newPadding = readerConfig.getPadding();
        ColorsConfig newColorsConfig = readerConfig.getColorsConfig();


        if (oldTextSize != newTextSize || oldColorsConfig.getTextColor() != newColorsConfig.getTextColor()
                || oldColorsConfig.getBatteryColor() != newColorsConfig.getBatteryColor()) {
            initPaints();
        }

        if (oldTextSize != newTextSize || oldLineSpace != newLineSpace || oldPadding != newPadding) {
            calculateChapterParameter();
        }
    }

    public int getBattery() {
        return mBattery;
    }

    /**
     * 设置电量
     *
     * @param battery 电量 0 -- 100之间
     */
    public void setBattery(int battery) {
        mBattery = battery;
    }

    Paint getBodyTextPaint() {
        return mMainBodyPaint;
    }
}
