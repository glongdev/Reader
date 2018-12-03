package com.glong.reader.widget;

import android.content.Context;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.glong.reader.ReaderSparseBooleanArray;
import com.glong.reader.TurnStatus;
import com.glong.reader.cache.Cache;
import com.glong.reader.cache.DiskCache;
import com.glong.reader.config.ColorsConfig;
import com.glong.reader.config.ReaderConfig;
import com.glong.reader.textconvert.TextBreakUtils;
import com.glong.reader.util.Log;
import com.glong.reader.util.NetUtil;
import com.glong.reader.util.Request;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.BATTERY_SERVICE;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public class ReaderView extends View {
    private static final String TAG = "ReaderView";

    protected Canvas mCurrPageCanvas;
    protected Canvas mNextPageCanvas;

    /**
     * 当前页
     */
    protected Bitmap mCurrPageBitmap;
    /**
     * 下一页
     */
    protected Bitmap mNextPageBitmap;

    private ReaderManager mReaderManager;

    private Adapter mAdapter;

    private Effect mEffect;

    private ReaderConfig mReaderConfig;

    private PageChangedCallback mPageChangedCallback;
    private PageDrawingCallback mPageDrawingCallback;

    private AdapterDataObserver mObserver;
    private boolean isCanTouch = true;

    public ReaderView(@NonNull Context context) {
        this(context, null);
    }

    public ReaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mEffect = new EffectOfRealOneWay(context);
        mReaderConfig = new ReaderConfig.Builder().build();
        SimplePageChangedCallback simplePageChangedCallback = new SimplePageChangedCallback();
        mPageChangedCallback = simplePageChangedCallback;
        mPageDrawingCallback = simplePageChangedCallback;
        mObserver = new AdapterDataObserver();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (mCurrPageBitmap == null && mNextPageBitmap == null) {
            mCurrPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
            mNextPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

            mCurrPageCanvas = new Canvas(mCurrPageBitmap);
            mNextPageCanvas = new Canvas(mNextPageBitmap);
        }

        if (mReaderManager != null) {
            mReaderManager.onAreaChanged(width, height);
            if (mCurrPageCanvas != null) {
                mReaderManager.drawPage(mCurrPageCanvas);
            }
        }
        initEffectConfiguration();
    }

    /**
     * 初始化 {@link Effect}配置
     */
    private void initEffectConfiguration() {
        mEffect.config(getMeasuredWidth(), getMeasuredHeight(), mCurrPageBitmap, mNextPageBitmap);
        mEffect.setPageChangedCallback(mPageChangedCallback);
        mEffect.setPageDrawingCallback(mPageDrawingCallback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCanTouch) {
            if (mEffect.onTouchEvent(event)) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mEffect.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        mEffect.computeScroll();
    }

    public void invalidateCurrPage() {
        if (mCurrPageCanvas != null) {
            mReaderManager.drawPage(mCurrPageCanvas);
            postInvalidate();
        }
    }

    public void invalidateNextPage() {
        if (mNextPageCanvas != null) {
            mReaderManager.drawPage(mNextPageCanvas);
            postInvalidate();
        }
    }

    public void invalidateBothPage() {
        if (mCurrPageCanvas != null && mNextPageCanvas != null) {
            mReaderManager.drawPage(mCurrPageCanvas);
            mReaderManager.drawPage(mNextPageCanvas);
            postInvalidate();
        }
    }

    public void setAdapter(@NonNull Adapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
            this.mAdapter.onDetachedFromReaderView(this);
        }
        Adapter oldAdapter = mAdapter;
        mAdapter = adapter;
        adapter.registerAdapterDataObserver(this.mObserver);
        adapter.onAttachedToReaderView(this);
        if (this.mReaderManager != null) {
            mReaderManager.onAdapterChanged(oldAdapter, this.mAdapter);
        }
        mAdapter.notifyDataSetChanged();
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public ReaderManager getReaderManager() {
        return mReaderManager;
    }

    public void setReaderManager(@NonNull ReaderManager readerManager) {
        mReaderManager = readerManager;
        mReaderManager.setReaderView(this, mReaderConfig);
    }

    /**
     * 设置布局配置（包括文字大小、颜色等等所有配置）
     *
     * @param readerConfig 配置对象
     */
    public void setReaderConfig(@NonNull ReaderConfig readerConfig) {
        this.mReaderConfig = readerConfig;
        if (mReaderManager == null) {
            throw new NullPointerException("You must set A ReaderManager to ReaderView!");
        }
        mReaderManager.setReaderConfig(mReaderConfig);
    }

    /**
     * @return 如果没设置会返回一个默认的ReaderConfig
     */
    public ReaderConfig getReaderConfig() {
        return ReaderConfig.newInstance(mReaderConfig);
    }

    /**
     * 设置正文文字大小
     */
    public void setTextSize(int textSize) {
        if (textSize > 0) {
            ReaderConfig readerConfig = getReaderConfig();
            readerConfig.setTextSize(textSize);
            setReaderConfig(readerConfig);
            invalidateBothPage();
        }
    }

    public int getTextSize() {
        return mReaderConfig.getTextSize();
    }

    /**
     * 设置正文行间距
     */
    public void setLineSpace(int lineSpace) {
        if (lineSpace >= 0) {
            ReaderConfig readerConfig = getReaderConfig();
            readerConfig.setLineSpace(lineSpace);
            setReaderConfig(readerConfig);
            invalidateBothPage();
        }
    }

    public int getLineSpace() {
        return mReaderConfig.getLineSpace();
    }

    /**
     * 设置正文距离四个边界的距离
     *
     * @param padding 边界
     */
    public void setBodyTextPadding(@NonNull int[] padding) {
        if (padding.length != 4) {
            throw new IllegalArgumentException("padding length must == 4");
        }
        ReaderConfig readerConfig = getReaderConfig();
        readerConfig.setPadding(padding);
        setReaderConfig(readerConfig);
        invalidateBothPage();
    }

    public int[] getBodyTextPadding() {
        return mReaderConfig.getPadding();
    }

    /**
     * 设置电池的长和宽
     *
     * @param widthAndHeight 长：widthAndHeight[0] ,宽：widthAndHeight[1]
     */
    public void setBatteryWidthAndHeight(@NonNull int[] widthAndHeight) {
        if (widthAndHeight.length != 2) {
            throw new IllegalArgumentException("battery widthAndHeight length must == 2");
        }
        ReaderConfig readerConfig = getReaderConfig();
        readerConfig.setBatteryWidthAndHeight(widthAndHeight);
        setReaderConfig(readerConfig);
        invalidateBothPage();
    }

    public Paint getBodyTextPaint() {
        if (mReaderManager == null) {
            throw new NullPointerException("You must set A ReaderManager to ReaderView!");
        }
        return mReaderManager.getBodyTextPaint();
    }

    public int[] getBatteryWidthAndHeight() {
        return mReaderConfig.getBatteryWidthAndHeight();
    }

    /**
     * 设置所有颜色相关
     * 因为颜色都是对应的，比如文字和背景，白色的背景往往对应黑色的文字、黑色的电池颜色等等
     *
     * @param colorsConfig 颜色相关对象
     */
    public void setColorsConfig(@NonNull ColorsConfig colorsConfig) {
        ReaderConfig readerConfig = getReaderConfig();
        readerConfig.setColorsConfig(colorsConfig);
        setReaderConfig(readerConfig);
        invalidateBothPage();
    }

    public ColorsConfig getColorsConfig() {
        return mReaderConfig.getColorsConfig();
    }

    /**
     * 设置翻页动效
     *
     * @param effect 翻页动效
     */
    public void setEffect(@NonNull Effect effect) {
        if (effect.getClass() != mEffect.getClass()) {
            this.mEffect = effect;
            initEffectConfiguration();
            if (mReaderManager != null) {
                invalidateBothPage();
            }
        }
    }

    public Effect getEffect() {
        return mEffect;
    }

    public PageChangedCallback getPageChangedCallback() {
        return mPageChangedCallback;
    }

    /**
     * 设置翻页回调
     *
     * @param pageChangedCallback 翻页回调
     */
    public void setPageChangedCallback(@NonNull PageChangedCallback pageChangedCallback) {
        mPageChangedCallback = pageChangedCallback;
        mEffect.setPageChangedCallback(pageChangedCallback);
    }

    public PageDrawingCallback getPageDrawingCallback() {
        return mPageDrawingCallback;
    }

    /**
     * 设置刷新和画的回调
     */
    public void setPageDrawingCallback(PageDrawingCallback pageDrawingCallback) {
        mPageDrawingCallback = pageDrawingCallback;
        mEffect.setPageDrawingCallback(pageDrawingCallback);
    }

    public boolean isCanTouch() {
        return isCanTouch;
    }

    public void setCanTouch(boolean canTouch) {
        isCanTouch = canTouch;
    }

    /**
     * 增加分段符号
     *
     * @param paragraph 分段符
     */
    public void addParagraph(String paragraph) {
        TextBreakUtils.sParagraph.add(paragraph);
    }

    /**
     * 检验{@link ReaderView#mReaderManager}不为null
     * 如果是null抛出NullPointerException
     */
    private void checkReaderManagerNonNull() {
        if (getReaderManager() == null) {
            throw new NullPointerException("You must invoke com.glong.reader.ReaderView#setReaderManager()" +
                    "to set a com.glong.reader.ReaderManager instance");
        }
    }

    public static abstract class Adapter<K, T> implements IDownload<K, T> {
        private static final String TAG = "ReaderView#Adapter";

        private AdapterDataObservable mObservable = new AdapterDataObservable();
        private List<K> mChapterList;

        public void setChapterList(List<K> chapters) {
            Log.d(this.TAG, "setChapterList ,listSize : " + chapters.size());
            this.mChapterList = chapters;
        }

        public List<K> getChapterList() {
            return mChapterList;
        }

        public void registerAdapterDataObserver(@NonNull DataObserver observer) {
            this.mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(@NonNull DataObserver observer) {
            this.mObservable.unregisterObserver(observer);
        }

        public void onAttachedToReaderView(@NonNull ReaderView readerView) {
        }

        public void onDetachedFromReaderView(@NonNull ReaderView readerView) {
        }

        public void notifyDataSetChanged() {
            Log.d(Adapter.TAG, "notifyDataSetChanged");
            mObservable.notifyChanged();
        }

        int getChapterCount() {
            return mChapterList == null ? 0 : mChapterList.size();
        }

        public abstract String obtainCacheKey(K k);

        public abstract String obtainChapterName(K k);

        public abstract String obtainChapterContent(T t);

        @Override
        public Request requestParams(K k) {
            return null;
        }
    }

    public static class ReaderManager implements IReaderManager {
        private static final String TAG = "ReaderView#ReaderManage";

        private boolean mIsUsingCache;

        private TurnStatus mLastTurnStatus = TurnStatus.IDLE;
        ReaderView mReaderView;
        Cache mCache;
        ReaderResolve mReaderResolve;

        private OnReaderWatcherListener mOnReaderWatcherListener;
        private ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        /**
         * <Integer,Boolean>键值对，Boolean 为true表示下载并且下载完成后需要展示
         */
        private ReaderSparseBooleanArray mDownloadingQueue = new ReaderSparseBooleanArray();

        public ReaderManager() {
        }

        private void initReaderResolve() {
            Adapter adapter = mReaderView.getAdapter();
            mReaderResolve.setArea(mReaderView.getMeasuredWidth(), mReaderView.getMeasuredHeight());
            if (adapter != null)
                mReaderResolve.setChapterSum(adapter.getChapterCount());
        }

        void onAreaChanged(int width, int height) {
            mReaderResolve.setArea(width, height);
        }

        @Override
        public final TurnStatus toPrevPage() {
            int pageIndex = mReaderResolve.getPageIndex();
            if (pageIndex > 0) {
                mReaderResolve.setPageIndex(pageIndex - 1);

                mReaderResolve.setCharIndex(mReaderResolve.getCurrPageFirstCharIndex());
                if (mOnReaderWatcherListener != null) {
                    mOnReaderWatcherListener.onPageChanged(mReaderResolve.getPageIndex());
                }
                return result(TurnStatus.LOAD_SUCCESS);
            }
            return toPrevChapter();
        }

        @Override
        public final TurnStatus toNextPage() {
            int pageIndex = mReaderResolve.getPageIndex();
            if (pageIndex < mReaderResolve.getPageSum() - 1) {
                mReaderResolve.setPageIndex(pageIndex + 1);

                mReaderResolve.setCharIndex(mReaderResolve.getCurrPageFirstCharIndex());
                if (mOnReaderWatcherListener != null) {
                    mOnReaderWatcherListener.onPageChanged(mReaderResolve.getPageIndex());
                }
                return result(TurnStatus.LOAD_SUCCESS);
            }
            return toNextChapter();
        }

        /**
         * 跳转到上一章的最后字符位置
         */
        @Override
        public final TurnStatus toPrevChapter() {
            return toPrevChapter(ReaderResolve.LAST_INDEX);
        }

        /**
         * 跳转到上一章的指定字符位置
         *
         * @param charIndex 指定字符索引
         */
        public final TurnStatus toPrevChapter(int charIndex) {
            int chapterIndex = mReaderResolve.getChapterIndex();
            if (chapterIndex == 0) {
                return result(TurnStatus.NO_PREV_CHAPTER);
            }
            return toSpecifiedChapter(chapterIndex - 1, charIndex);
        }

        /**
         * 跳转到下一章的最后一个
         */
        @Override
        public final TurnStatus toNextChapter() {
            return toNextChapter(ReaderResolve.FIRST_INDEX);
        }

        /**
         * 跳转到下一章的指定字符位置
         *
         * @param charIndex 指定字符
         */
        public final TurnStatus toNextChapter(int charIndex) {
            int chapterIndex = mReaderResolve.getChapterIndex();
            if (chapterIndex >= mReaderResolve.getChapterSum() - 1) {
                return result(TurnStatus.NO_NEXT_CHAPTER);
            }
            return toSpecifiedChapter(chapterIndex + 1, ReaderResolve.FIRST_INDEX);
        }

        @Override
        public final TurnStatus toSpecifiedChapter(final int chapterIndex, final int charIndex) {
            checkAdapterNonNull();
            final Adapter adapter = mReaderView.getAdapter();
            if (adapter.getChapterCount() == 0) {
                return result(TurnStatus.LOAD_FAILURE);
            }
            if (this.mDownloadingQueue.contains(chapterIndex)) {
                if (Log.DEBUG)
                    Toast.makeText(mReaderView.getContext(), "正在下载", Toast.LENGTH_SHORT).show();
                mDownloadingQueue.put(chapterIndex, true);// 正在下载的可能是缓存在下载，所以这里必须设置为true
                return result(TurnStatus.DOWNLOADING);
            }

            final Object chapterItem = adapter.getChapterList().get(chapterIndex);

            ParameterizedType parameterizedType = (ParameterizedType) adapter.getClass().getGenericSuperclass();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//            for (Type actualTypeArgument : actualTypeArguments) {
//                Log.d(TAG, "type :" + actualTypeArgument);
//            }

            Object cache = mCache.get(adapter.obtainCacheKey(chapterItem), actualTypeArguments[1]);

            if (cache == null) {
                this.download(adapter, chapterItem, chapterIndex, charIndex, true);
                return result(TurnStatus.DOWNLOADING);
            } else {
                setUpReaderResolve(chapterIndex, charIndex, adapter.obtainChapterName(chapterItem), adapter.obtainChapterContent(cache));
                return result(TurnStatus.LOAD_SUCCESS);
            }
        }

        private TurnStatus result(TurnStatus turnStatus) {
            mLastTurnStatus = turnStatus;
            return turnStatus;
        }

        @Override
        public void startFromCache(String key, int chapterIndex, int charIndex, @NonNull String chapterName) {
            if (mReaderView == null)
                throw new NullPointerException("mReaderView == null,Have you already called method ReaderView#setReaderMananger()");
            startFromCache(mCache.getCacheDir(), key, chapterIndex, charIndex, chapterName);
        }

        @Override
        public void startFromCache(File cacheDir, String key, int chapterIndex, int charIndex, @NonNull String chapterName) {
            if (mReaderView == null)
                throw new NullPointerException("mReaderView == null,Have you already called method ReaderView#setReaderManger()?");
            Adapter adapter = mReaderView.getAdapter();
            if (adapter == null)
                throw new NullPointerException("Have you already called method ReaderView#setAdapter()?");
            mCache.setCacheDir(cacheDir);
            ParameterizedType parameterizedType = (ParameterizedType) adapter.getClass().getGenericSuperclass();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Object cache = mCache.get(key, actualTypeArguments[1]);
            if (cache != null) {
                mReaderResolve.setArea(mReaderView.getMeasuredWidth(), mReaderView.getMeasuredHeight());
                mReaderResolve.setChapterSum(1);
                setUpReaderResolve(chapterIndex, charIndex, chapterName, adapter.obtainChapterContent(cache));
                mIsUsingCache = true;
                ReaderManager.this.mReaderView.invalidateBothPage();
            } else {
                mReaderResolve.setChapterIndex(chapterIndex);
                mReaderResolve.setCharIndex(charIndex);
            }
        }

        void setReaderView(ReaderView readerView, @NonNull ReaderConfig readerConfig) {
            mReaderResolve = new ReaderResolve(readerConfig);
            this.mReaderView = readerView;
            if (mCache == null) {
                mCache = new DiskCache(readerView.getContext().getCacheDir());
            }
            initReaderResolve();
        }

        @Override
        public void drawPage(@NonNull Canvas canvas) {
            BatteryManager batteryManager = (BatteryManager) mReaderView.getContext().getSystemService(BATTERY_SERVICE);
            if (batteryManager != null) {
                int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                Log.d(TAG, "battery:" + battery);
                mReaderResolve.setBattery(battery);
            }
            mReaderResolve.drawPage(canvas);
        }

        /**
         * 返回缓存，如果没有通过{@link this#setCache(Cache)}设置缓存，则返回默认的缓存
         * <p>
         * 注意：如果没有通过{@link this#setCache(Cache)}设置缓存并且在调用
         * {@link ReaderView#setReaderManager(ReaderManager)}之前调用该方法返回null
         *
         * @return 缓存
         */
        public Cache getCache() {
            return mCache;
        }

        public void setCache(Cache cache) {
            mCache = cache;
        }

        public ReaderResolve getReaderResolve() {
            return mReaderResolve;
        }

        public void setCustomReaderResolve(ReaderResolve readerResolve) {
            mReaderResolve = readerResolve;
            mReaderResolve.calculateChapterParameter();
            this.mReaderView.invalidateCurrPage();
        }

        /**
         * 更新ReaderResolve
         */
        private void setUpReaderResolve(int chapterIndex, int charIndex, @NonNull String title, String content) {
            mReaderResolve.setChapterIndex(chapterIndex);
            mReaderResolve.setCharIndex(charIndex);
            mReaderResolve.setTitle(title);
            mReaderResolve.setContent(content);

            if (mOnReaderWatcherListener != null) {
                mOnReaderWatcherListener.onChapterChanged(chapterIndex, mReaderResolve.getPageIndex());
                mOnReaderWatcherListener.onPageChanged(mReaderResolve.getPageIndex());
            }
            //章节发生变化后，缓存前后章节（如果没有缓存的话）
            cacheNearChapter(chapterIndex);
//            mReaderView.invalidateCurrPage();
        }

        /**
         * 缓存指定章节 前后章节（前后章节的数量通过{@link Cache#setCacheAmount(int)}设置，默认值为3）
         *
         * @param chapterIndex 指定章节
         */
        private void cacheNearChapter(int chapterIndex) {
            int cacheAmount = mCache.getCacheAmount();
            final Adapter adapter = this.mReaderView.getAdapter();

            for (int i = chapterIndex - cacheAmount; i <= chapterIndex + cacheAmount; i++) {
                //如果i合法
                if (i >= 0 && i < adapter.getChapterCount()) {
                    final Object chapterItem = adapter.getChapterList().get(i);
                    // 还没有缓存，则开始下载
                    if (!mCache.isCached(adapter.obtainCacheKey(chapterItem))) {
                        final int downloadChapterIndex = i;
                        ReaderManager.this.download(adapter, chapterItem, downloadChapterIndex, ReaderResolve.UNKNOWN, false);
                    }
                }
            }
        }

        /**
         * 下载 or 下载并且展示
         *
         * @param adapter           {@link Adapter}
         * @param chapterItem       one item of {@link Adapter#getChapterList()}
         * @param chapterIndex      章节索引
         * @param charIndex         字符索引
         * @param showAfterDownload 下载完成后是否展示
         */
        private void download(final Adapter adapter, final Object chapterItem
                , final int chapterIndex, final int charIndex, final boolean showAfterDownload) {
            mFixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(ReaderManager.TAG, " start download chapterIndex:" + chapterIndex);
                    if (showAfterDownload)
                        ReaderManager.this.toastInAsync("开始下载");

                    if (showAfterDownload && mOnReaderWatcherListener != null)
                        mOnReaderWatcherListener.onChapterDownloadStart(chapterIndex);

                    ReaderManager.this.mDownloadingQueue.put(chapterIndex, showAfterDownload);

                    Object downLoad;
                    Request request = adapter.requestParams(chapterItem);
                    if (request == null) {
                        downLoad = adapter.downLoad(chapterItem);
                    } else {
                        downLoad = NetUtil.download(request);
                    }

                    if (downLoad != null) {
                        Log.d(ReaderManager.TAG, "download " + chapterIndex
                                + " success,content:" + adapter.obtainChapterContent(downLoad));

                        if (mDownloadingQueue.get(chapterIndex)) {
                            ReaderManager.this.toastInAsync("下载成功");

                            if (mOnReaderWatcherListener != null)
                                mOnReaderWatcherListener.onChapterDownloadSuccess(chapterIndex);

                            if (mLastTurnStatus != TurnStatus.LOAD_SUCCESS) {
                                // 当字符索引未知时，需要计算一下
                                int tempCharIndex = charIndex;
                                if (tempCharIndex == ReaderResolve.UNKNOWN) {
                                    if (ReaderManager.this.mReaderResolve.getChapterIndex() < chapterIndex) {
                                        tempCharIndex = ReaderResolve.FIRST_INDEX;
                                    } else {
                                        tempCharIndex = ReaderResolve.LAST_INDEX;
                                    }
                                }
                                setUpReaderResolve(chapterIndex, tempCharIndex, adapter.obtainChapterName(chapterItem), adapter.obtainChapterContent(downLoad));
                                if (showAfterDownload) {
//                                    if (mReaderResolve.getChapterIndex() == chapterIndex)
                                    ReaderManager.this.mReaderView.invalidateBothPage();
//                                    else
                                }
                            }
                        }
                        // 保存至缓存
                        mCache.put(adapter.obtainCacheKey(chapterItem), downLoad);
                    } else {
                        // 章节下载失败
                        if (showAfterDownload && mOnReaderWatcherListener != null)
                            mOnReaderWatcherListener.onChapterDownloadError(chapterIndex);
                    }
                    ReaderManager.this.mDownloadingQueue.delete(chapterIndex);
                }
            });
        }

        public void onAdapterChanged(Adapter oldAdapter, Adapter adapter) {
        }

        TurnStatus onChanged() {
            Adapter adapter = ReaderManager.this.mReaderView.getAdapter();
//            ReaderManager.this.mReaderResolve.setArea(ReaderManager.this.mReaderView.getMeasuredWidth(),
//                    ReaderManager.this.mReaderView.getMeasuredHeight());
            ReaderManager.this.mReaderResolve.setChapterSum(adapter.getChapterCount());

            if (mIsUsingCache) {
                mIsUsingCache = false;
                return TurnStatus.IDLE;
            }

            int chapterIndex = 0;
            if (ReaderManager.this.mReaderResolve.getChapterIndex() <= adapter.getChapterCount()) {
                chapterIndex = ReaderManager.this.mReaderResolve.getChapterIndex();
            }
            return ReaderManager.this.toSpecifiedChapter(chapterIndex, mReaderResolve.getCharIndex());
        }

        public OnReaderWatcherListener getOnReaderWatcherListener() {
            return mOnReaderWatcherListener;
        }

        public void setOnReaderWatcherListener(OnReaderWatcherListener onReaderWatcherListener) {
            mOnReaderWatcherListener = onReaderWatcherListener;
        }

        private void checkAdapterNonNull() {
            if (mReaderView.getAdapter() == null) {
                throw new NullPointerException("You must invoke com.glong.reader.ReaderView#setAdapter()" +
                        "to set a com.glong.reader.Adapter instance");
            }
        }

        private void toastInAsync(final String msg) {
            if (Log.DEBUG)
                ReaderManager.this.mReaderView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mReaderView.getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
        }

        /**
         * 设置阅读器配置
         *
         * @param readerConfig 阅读器配置
         */
        void setReaderConfig(ReaderConfig readerConfig) {
            mReaderResolve.setReaderConfig(readerConfig);
        }

        Paint getBodyTextPaint() {
            return mReaderResolve.getBodyTextPaint();
        }
    }

    static class AdapterDataObservable extends Observable<DataObserver> {
        void notifyChanged() {
            for (int i = this.mObservers.size() - 1; i >= 0; --i) {
                (this.mObservers.get(i)).onChanged();
            }
        }
    }

    private class AdapterDataObserver extends DataObserver {

        @Override
        public void onChanged() {
            TurnStatus turnStatus = ReaderView.this.getReaderManager().onChanged();
            if (turnStatus == TurnStatus.LOAD_SUCCESS) {
                ReaderView.this.invalidateCurrPage();
            }
        }
    }

    private abstract class DataObserver {
        public abstract void onChanged();
    }

    //提供一个简单的PageChangedCallback的实现
    public class SimplePageChangedCallback implements PageChangedCallback, PageDrawingCallback {

        @Override
        public void invalidate() {
            ReaderView.this.postInvalidate();
        }

        @Override
        public void drawCurrPage() {
            checkReaderManagerNonNull();
            ReaderView.this.mReaderManager.drawPage(ReaderView.this.mCurrPageCanvas);
        }

        @Override
        public void drawNextPage() {
            checkReaderManagerNonNull();
            ReaderView.this.mReaderManager.drawPage(ReaderView.this.mNextPageCanvas);
        }

        @Override
        public TurnStatus toPrevPage() {
            checkReaderManagerNonNull();
            return ReaderView.this.mReaderManager.toPrevPage();
        }

        @Override
        public TurnStatus toNextPage() {
            checkReaderManagerNonNull();
            return ReaderView.this.mReaderManager.toNextPage();
        }
    }
}
