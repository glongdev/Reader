package com.glong.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.glong.reader.widget.ReaderView;
import com.glong.sample.entry.ChapterContentBean;
import com.glong.sample.entry.ChapterItemBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ReaderView.Adapter<ChapterItemBean, ChapterContentBean> mAdapter;
    private ReaderView.ReaderManager mReaderManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                boolean removed = mReaderManager.getCache().removeAll();
                Toast.makeText(this, "删除缓存" + (removed ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initReader();
        initData();
    }

    private void initViews() {
        findViewById(R.id.prev_chapter_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReaderManager.toPrevChapter();
            }
        });

        findViewById(R.id.next_chapter_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReaderManager.toNextChapter();
            }
        });
    }

    private void initData() {
        List<ChapterItemBean> chapters = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ChapterItemBean bean = new ChapterItemBean();
            bean.setChapterId(String.valueOf("id" + i));
            bean.setChapterName("第" + i + "章");
            chapters.add(bean);
        }
        mAdapter.setChapterList(chapters);
        mAdapter.notifyDataSetChanged();
    }

    private void initReader() {
        final ReaderView readerView = findViewById(R.id.reader_view);

        mReaderManager = new ReaderView.ReaderManager() {
            @Override
            public void startFromCache(String key, Class clazz, int charIndex) {
                super.startFromCache(key, clazz, charIndex);
            }
        };

        readerView.setReaderManager(mReaderManager);

        mAdapter = new ReaderView.Adapter<ChapterItemBean, ChapterContentBean>() {
            @Override
            public String obtainCacheKey(ChapterItemBean chapterItemBean) {
                return chapterItemBean.getChapterId();
            }

            @Override
            public String obtainChapterKey(ChapterItemBean chapterItemBean) {
                return chapterItemBean.getChapterId();
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
            public Class<ChapterItemBean> castFirstGeneric() {
                return ChapterItemBean.class;
            }

            @Override
            public Class<ChapterContentBean> castSecondGeneric() {
                return ChapterContentBean.class;
            }

            @Override
            public ChapterContentBean downLoad(ChapterItemBean chapterItemBean) {
                try {
                    Thread.sleep(10000);
                    ChapterContentBean chapterContentBean = new ChapterContentBean();
                    chapterContentBean.setChapterId(chapterItemBean.getChapterId());
                    chapterContentBean.setChapterName(chapterItemBean.getChapterName());
                    chapterContentBean.setChapterContent(chapterContentBean.getChapterName() + Constant.STR_CONTENT + chapterContentBean.getChapterName());
                    return chapterContentBean;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        readerView.setAdapter(mAdapter);
    }
}
