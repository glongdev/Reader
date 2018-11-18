package com.glong.reader.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.glong.reader.TurnStatus;
import com.glong.reader.config.ReaderConfig;

import java.io.File;
import java.util.List;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public class ReaderView extends View {

    private ReaderManager mReaderManager;

    private ReaderConfig mReaderConfig = new ReaderConfig.Builder().build();

    private PageChangedCallback mPageChangedCallback = new DefaultPageChangedCallback();

    public ReaderView(Context context) {
        this(context, null);
    }

    public ReaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setReaderConfig(ReaderConfig readerConfig) {
        this.mReaderConfig = readerConfig;
    }

    public ReaderConfig getReaderConfig() {
        return this.mReaderConfig;
    }

    public static abstract class Adapter<K, T> implements IDownload<K, T> {

        private List<K> mChapterList;

        public void setChapterList(List<K> chapters) {
            this.mChapterList = chapters;
        }

        public List<K> getChapterList() {
            return mChapterList;
        }

        public void notifyDataSetChanged() {

        }

        public abstract String obtainCacheKey(K k);

        public abstract String obtainChapterKey(K k);

        public abstract String obtainChapterName(K k);

        public abstract String obtainChapterContent(T t);
    }

    class DefaultPageChangedCallback implements PageChangedCallback {

        @Override
        public void invalidate() {
            ReaderView.this.postInvalidate();
        }

        @Override
        public void drawPage(Canvas canvas) {
            mReaderManager.drawPage(canvas);
        }

        @Override
        public TurnStatus toPrevPage() {
            return mReaderManager.toPrevPage();
        }

        @Override
        public TurnStatus toNextPage() {
            return mReaderManager.toNextPage();
        }
    }

    static abstract class ReaderManager implements IReaderManager {

        ReaderView mReaderView;

        @Override
        public TurnStatus toPrevPage() {
            return null;
        }

        @Override
        public TurnStatus toNextPage() {
            return null;
        }

        @Override
        public TurnStatus toPrevChapter() {
            return null;
        }

        @Override
        public TurnStatus toNextChapter() {
            return null;
        }

        @Override
        public TurnStatus toSpecifiedChapter(int chapterIndex, int charIndex) {
            return null;
        }

        @Override
        public void startFromCache(String key, Class clazz, int charIndex) {

        }

        @Override
        public void startFromCache(File cacheDir, String key, Class clazz, int charIndex) {

        }

        void setReaderView(ReaderView readerView) {
            this.mReaderView = readerView;
        }
    }
}
