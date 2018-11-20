package com.glong.reader.resolve;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.glong.reader.config.ReaderConfig;
import com.glong.reader.textconvert.ShowChar;
import com.glong.reader.textconvert.ShowLine;
import com.glong.reader.textconvert.TextUtils;
import com.glong.reader.util.Log;

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
    public static final int UNKOWN = -2;//未知，在实际翻页时动态计算

    //本章在总章节中的索引
    protected int mChapterIndex;

    //当前页第一个字符在本章中
    protected int mCharIndex;


    //当前章节的文字内容(正文)
    protected String mContent = "";

    //当前章节标题
    protected String mTitle;

    // 总章节数
    protected int mChapterSum;

    private int mAreaWidth;
    private int mAreaHeight;

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

    private Paint mMainBodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//正文的画笔
    private Paint mMarginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//画边缘的画笔，如：页码、章节名称、电池等
    private Paint mChapterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画章节名称的Paint

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);

    protected DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    /**
     * 界面布局配置，此处依赖{@link ReaderConfig}是因为需要使用TextSize计算总页数
     */
    private ReaderConfig mReaderConfig;

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
        mMainBodyPaint.setColor(mReaderConfig.getReaderBackground().getTextColor());
        mMainBodyPaint.setTextSize(mReaderConfig.getTextSize());

        mChapterPaint.setTextSize(mReaderConfig.getTextSize() * 1.4f);
        mChapterPaint.setColor(mReaderConfig.getReaderBackground().getTextColor());

        mMarginPaint.setColor(mReaderConfig.getReaderBackground().getTextColor());
        mMarginPaint.setTextSize(40);
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

        if (!android.text.TextUtils.isEmpty(mTitle)) {
            //计算大标题所占行数
            mChapterNameLines = TextUtils.breakToLineList(mTitle, usableWidth, 0, mChapterPaint);

            //计算第0页大章节标题所占高度
            Paint.FontMetrics chapterFM = mChapterPaint.getFontMetrics();
            mChapterTitleHeight = (int) (mChapterNameLines.size() * (chapterFM.bottom - chapterFM.top + mReaderConfig.getLineSpace()) * 1.5f);
        }

        //第0页 能展示多少行正文
        mLineNumPerPageInFirstPage = TextUtils.measureLines(usableHeight - mChapterTitleHeight, mReaderConfig.getLineSpace(), mMainBodyPaint);

        //正常情况下（除第0页）一页能展示多少行
        mLineNumPerPageWithoutFirstPage = TextUtils.measureLines(usableHeight, mReaderConfig.getLineSpace(), mMainBodyPaint);

        //正文所占行数
        if (!android.text.TextUtils.isEmpty(mContent)) {
            mShowLines = TextUtils.breakToLineList(mContent, usableWidth, 0, mMainBodyPaint);
        }

        //计算总页数
        if (mShowLines != null) {
            mPageSum = mShowLines.size() - mLineNumPerPageInFirstPage == 0 ?
                    1 : ((mShowLines.size() - mLineNumPerPageInFirstPage) - 1) / Math.max(1, mLineNumPerPageWithoutFirstPage) + 1 + 1;
            calculatePageIndex();
        } else {
            mPageSum = -1;
        }
    }

    /**
     * 根据字符索引计算页码
     */
    private void calculatePageIndex() {
        if (mCharIndex == FIRST_INDEX) {
            mPageIndex = 0;
        } else if (mCharIndex == LAST_INDEX) {
            mPageIndex = mPageSum - 1;
        } else {
            //step 1.计算当前字符索引在哪一行

            // 为了提升效率，如果字符索引在mContent.length()后半部分，则从后半部分遍历
            int lineIndex = 0;
            if (mCharIndex >= mContent.length() / 2) {
                for (int i = mPageSum - 1; i >= 0; --i) {
                    ShowLine showLine = mShowLines.get(i);
                    if (mCharIndex >= showLine.getLineFirstIndexInChapter() && mCharIndex <= showLine.getLineLastIndexInChapter()) {
                        lineIndex = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i <= mPageSum - 1; i++) {
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
    }

    public void drawPage(Canvas canvas) {
        Log.d(TAG, "start drawPage,title:" + mTitle + " ,content:" + mContent);
//        if (!mInitialized) {
//            Log.d(TAG, "No initialization calculation yet, bounce method!");
//        }
        if (mTitle != null) {
            drawMarginArea(canvas);
            maybeDrawChapterTitle(canvas);
        }
        if (mShowLines != null) {
            drawMainBodyArea(canvas);
        }
    }

    /**
     * 画边缘区域内容，分为6步
     *
     * @param canvas 画布
     */
    private void drawMarginArea(Canvas canvas) {
        //step1.清空画布
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Drawable drawable = mReaderConfig.getReaderBackground().getBackground();
        if (drawable instanceof ColorDrawable) {
            int color = ((ColorDrawable) drawable).getColor();
            canvas.drawColor(color);
        } else if (drawable instanceof BitmapDrawable) {
            //画背景
        }

        //step2.在边缘区域画章节名称
//        Paint.FontMetrics fm = mChapterPaint.getFontMetrics();
        canvas.drawText(mTitle, mReaderConfig.getPadding()[0], mReaderConfig.getPadding()[1], mMarginPaint);

        //step3.在边缘区域画页码
        if (mPageSum != -1) {//mPageSum == -1 代表当前页的内容为空
            String pageNumber = String.valueOf((mPageIndex + 1) + "/" + mPageSum);
            canvas.drawText(pageNumber, mAreaWidth - mReaderConfig.getPadding()[2] - mMarginPaint.measureText(pageNumber)
                    , mReaderConfig.getPadding()[1], mMarginPaint);
        }

        //step4.在边缘区域画时间
        Paint.FontMetrics fm = mMarginPaint.getFontMetrics();
        String time = dateFormat.format(new Date());
        canvas.drawText(time, mReaderConfig.getPadding()[0], mAreaHeight - mReaderConfig.getPadding()[3] / 2 - fm.bottom / 2 + fm.top / 2, mMarginPaint);

        //step5.在边缘区域画总百分比
        float percent = (float) mChapterIndex * 100 / mChapterSum + (float) (mPageIndex + 1) * 100f / mPageSum / mChapterSum;
        String percentStr = decimalFormat.format(percent) + "%";
        canvas.drawText(percentStr, mAreaWidth - mReaderConfig.getPadding()[2] - mMarginPaint.measureText(percentStr),
                mAreaHeight - mReaderConfig.getPadding()[3] / 2 - fm.bottom / 2 + fm.top / 2, mMarginPaint);

        //step6.在边缘区域画电池
    }

    /**
     * 如果是第0页需要画章节大标题
     *
     * @param canvas 画布
     */
    private void maybeDrawChapterTitle(Canvas canvas) {
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
    private void drawMainBodyArea(Canvas canvas) {
        //画正文
        if (mShowLines == null || mShowLines.size() <= 0)
            return;
        //char 文字索引
        mCharIndex = mShowLines.get(0).getLineFirstIndexInChapter();

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
    private void drawLineText(Canvas canvas, ShowLine showLine, float x, float y, float textHeight) {
        canvas.drawText(showLine.getLineData(), x, y, mMainBodyPaint);
        float rightPosition;
        float bottomPosition = y + mMainBodyPaint.getFontMetrics().descent;
        float topPosition = bottomPosition - textHeight - mMainBodyPaint.getFontMetrics().descent;
        for (ShowChar showChar : showLine.charsData) {
            rightPosition = x + showChar.charWidth;
            showChar.rectF = new RectF(x, topPosition, rightPosition, bottomPosition);
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
        mCharIndex = charIndex;
    }

    public void setArea(int areaWidth, int areaHeight) {
        Log.d(TAG, "areaWidth:" + areaWidth + ",areaHeight:" + areaHeight);
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

    public ReaderConfig getReaderConfig() {
        return mReaderConfig;
    }

    public void setReaderConfig(@NonNull ReaderConfig readerConfig) {
        mReaderConfig = readerConfig;
        initPaints();
    }
}
