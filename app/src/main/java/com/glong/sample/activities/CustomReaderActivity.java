package com.glong.sample.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.glong.reader.widget.ReaderView;
import com.glong.sample.R;
import com.glong.sample.custom.MyReaderResolve;
import com.glong.sample.entry.ChapterContentBean;
import com.glong.sample.entry.ChapterItemBean;
import com.glong.sample.localtest.LocalServer;

import java.util.List;

public class CustomReaderActivity extends AppCompatActivity {

    private ReaderView mReaderView;
    private ReaderView.Adapter<ChapterItemBean, ChapterContentBean> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_reader);

        initReaderView();
        initData();
    }

    private void initData() {
        LocalServer.getChapterList("1", new LocalServer.OnResponseCallback() {
            @Override
            public void onSuccess(List<ChapterItemBean> chapters) {
                mAdapter.setChapterList(chapters);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void initReaderView() {
        mReaderView = findViewById(R.id.custom_reader_view);
        ReaderView.ReaderManager readerManager = new ReaderView.ReaderManager();
        mReaderView.setReaderManager(readerManager);

        mAdapter = new ReaderView.Adapter<ChapterItemBean, ChapterContentBean>() {
            @Override
            public String obtainCacheKey(ChapterItemBean chapterItemBean) {
                return chapterItemBean.getChapterId() + "custom";
            }

            @Override
            public String obtainChapterName(ChapterItemBean chapterItemBean) {
                return chapterItemBean.getChapterName();
            }

            @Override
            public String obtainChapterContent(ChapterContentBean chapterContentBean) {
                return chapterContentBean.getChapterContent();
            }

            @Override
            public ChapterContentBean downLoad(ChapterItemBean chapterItemBean) {
                return LocalServer.syncDownloadContent(chapterItemBean);
            }
        };

        mReaderView.setAdapter(mAdapter);
        readerManager.setCustomReaderResolve(new MyReaderResolve());
    }
}
