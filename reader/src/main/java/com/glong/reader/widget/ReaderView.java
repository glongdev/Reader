package com.glong.reader.widget;

import android.content.Context;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import com.glong.reader.config.ReaderConfig;
import com.glong.reader.resolve.ReaderResolve;
import com.glong.reader.util.Log;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private AdapterDataObserver mObserver;

    public ReaderView(@NonNull Context context) {
        this(context, null);
    }

    public ReaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mEffect = new EffectOfNon(context);
        mReaderConfig = new ReaderConfig.Builder().build();
        mPageChangedCallback = new SimplePageChangedCallback();
        mObserver = new AdapterDataObserver();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int height = ReaderUtils.measureSize(1000, heightMeasureSpec);
//        int width = ReaderUtils.measureSize(600, widthMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        setMeasuredDimension(width, height);

        initEffectConfiguration();
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
    }

    /**
     * 初始化 {@link Effect}配置
     */
    private void initEffectConfiguration() {
        mEffect.config(getMeasuredWidth(), getMeasuredHeight(), mCurrPageBitmap, mNextPageBitmap);
        mEffect.setPageChangedCallback(mPageChangedCallback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEffect.onTouchEvent(event)) {
            return true;
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
        mReaderManager.setReaderView(this);
    }

    public void setReaderConfig(@NonNull ReaderConfig readerConfig) {
        this.mReaderConfig = readerConfig;
    }

    public ReaderConfig getReaderConfig() {
        return this.mReaderConfig;
    }

    public void setEffect(@NonNull Effect effect) {
        this.mEffect = effect;
        initEffectConfiguration();
    }

    public Effect getEffect() {
        return mEffect;
    }

    public PageChangedCallback getPageChangedCallback() {
        return mPageChangedCallback;
    }

    public void setPageChangedCallback(@NonNull PageChangedCallback pageChangedCallback) {
        mPageChangedCallback = pageChangedCallback;
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

        public abstract String obtainChapterKey(K k);

        public abstract String obtainChapterName(K k);

        public abstract String obtainChapterContent(T t);

        public abstract Class<K> castFirstGeneric();

        public abstract Class<T> castSecondGeneric();
    }

    public static abstract class ReaderManager implements IReaderManager {
        private static final String TAG = "ReaderView#ReaderManage";

        private TurnStatus mLastTurnStatus = TurnStatus.IDLE;
        ReaderView mReaderView;
        Cache mCache;
        ReaderResolve mReaderResolve = new ReaderResolve();

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

                if (mOnReaderWatcherListener != null) {
                    mOnReaderWatcherListener.onPageChanged(mReaderResolve.getPageIndex());
                }
                return result(TurnStatus.LOAD_SUCCESS);
            }
            return toNextChapter();
        }

        @Override
        public final TurnStatus toPrevChapter() {
            int chapterIndex = mReaderResolve.getChapterIndex();
            if (chapterIndex == 0) {
                return result(TurnStatus.NO_PREV_CHAPTER);
            }
            return toSpecifiedChapter(chapterIndex - 1, ReaderResolve.LAST_INDEX);
        }

        @Override
        public final TurnStatus toNextChapter() {
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
            Object cache = mCache.get(adapter.obtainCacheKey(chapterItem), adapter.castSecondGeneric());

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
        public void startFromCache(String key, Class clazz, int charIndex) {

        }

        @Override
        public void startFromCache(File cacheDir, String key, Class clazz, int charIndex) {

        }

        void setReaderView(ReaderView readerView) {
            this.mReaderView = readerView;
            if (mCache == null) {
                mCache = new DiskCache(readerView.getContext().getCacheDir());
            }
            initReaderResolve();
        }

        @Override
        public void drawPage(@NonNull Canvas canvas) {
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

        public void setReaderResolve(ReaderResolve readerResolve) {
            mReaderResolve = readerResolve;
            mReaderResolve.calculateChapterParameter();
            this.mReaderView.invalidateCurrPage();
        }

        private void setUpReaderResolve(int chapterIndex, int charIndex, String title, String content) {
            mReaderResolve.setChapterIndex(chapterIndex);
            mReaderResolve.setCharIndex(charIndex);
            mReaderResolve.setTitle(title);
            mReaderResolve.setContent(content);

            if (mOnReaderWatcherListener != null) {
                mOnReaderWatcherListener.onChapterChanged(chapterIndex, mReaderResolve.getPageIndex());
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
                        ReaderManager.this.download(adapter, chapterItem, downloadChapterIndex, ReaderResolve.UNKOWN, false);
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
                    Object downLoad = adapter.downLoad(chapterItem);

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
                                if (tempCharIndex == ReaderResolve.UNKOWN) {
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

        public TurnStatus onChanged() {
            Adapter adapter = ReaderManager.this.mReaderView.getAdapter();
//            ReaderManager.this.mReaderResolve.setArea(ReaderManager.this.mReaderView.getMeasuredWidth(),
//                    ReaderManager.this.mReaderView.getMeasuredHeight());
            ReaderManager.this.mReaderResolve.setChapterSum(adapter.getChapterCount());

            int chapterIndex = 0;
            if (ReaderManager.this.mReaderResolve.getChapterIndex() <= adapter.getChapterCount()) {
                chapterIndex = ReaderManager.this.mReaderResolve.getChapterIndex();
            }
            return ReaderManager.this.toSpecifiedChapter(chapterIndex, 0);
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
    public class SimplePageChangedCallback implements PageChangedCallback {

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
