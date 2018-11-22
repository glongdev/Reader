package com.glong.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.glong.reader.widget.EffectOfCover;
import com.glong.reader.widget.EffectOfNon;
import com.glong.reader.widget.EffectOfRealBothWay;
import com.glong.reader.widget.EffectOfRealOneWay;
import com.glong.reader.widget.EffectOfSlide;
import com.glong.reader.widget.ReaderView;
import com.glong.sample.entry.ChapterContentBean;
import com.glong.sample.entry.ChapterItemBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ReaderView.Adapter<ChapterItemBean, ChapterContentBean> mAdapter;
    private ReaderView.ReaderManager mReaderManager;
    private ReaderView mReaderView;

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
            case R.id.switch_effect_of_real:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initReader();
        initData();
        initViews();
    }

    private void initViews() {
        findViewById(R.id.real_one_way).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mReaderManager.toPrevChapter();
//                mReaderView.invalidateBothPage();
                mReaderView.setEffect(new EffectOfRealOneWay(MainActivity.this));
            }
        });

        findViewById(R.id.real_both_way).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mReaderManager.toNextChapter();
//                mReaderView.invalidateBothPage();
                mReaderView.setEffect(new EffectOfRealBothWay(MainActivity.this));
            }
        });

        findViewById(R.id.cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReaderView.setEffect(new EffectOfCover(MainActivity.this));
            }
        });

        findViewById(R.id.slide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReaderView.setEffect(new EffectOfSlide(MainActivity.this));
            }
        });

        findViewById(R.id.non).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReaderView.setEffect(new EffectOfNon(MainActivity.this));
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
        mReaderView = findViewById(R.id.reader_view);

        mReaderManager = new ReaderView.ReaderManager() {
            @Override
            public void startFromCache(String key, Class clazz, int charIndex) {
                super.startFromCache(key, clazz, charIndex);
            }
        };

        mReaderView.setReaderManager(mReaderManager);
        mReaderView.setEffect(new EffectOfRealOneWay(this));

        mAdapter = new ReaderView.Adapter<ChapterItemBean, ChapterContentBean>() {
            @Override
            public String obtainCacheKey(ChapterItemBean chapterItemBean) {
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
            public ChapterContentBean downLoad(ChapterItemBean chapterItemBean) {
                try {
                    Thread.sleep(1000);
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

//            @Override
//            public Request requestParams(ChapterItemBean chapterItemBean) {
//                return new Request.Builder().baseUrl("").addUrlParams("page", "0").addBody("chapterId", 123)
//                        .addHeader("head", "header").post().build();
//            }
        };

        mReaderView.setAdapter(mAdapter);

        mReaderView.setBackgroundColor(Color.GREEN);
    }
}
