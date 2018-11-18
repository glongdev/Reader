package com.glong.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.glong.reader.widget.ReaderView;
import com.glong.sample.support.ChapterContentBean;
import com.glong.sample.support.ChapterItemBean;

import java.io.Reader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }

    private void test() {
        ReaderView.Adapter<ChapterItemBean, ChapterContentBean> adapter
                = new ReaderView.Adapter<ChapterItemBean, ChapterContentBean>() {
            @Override
            public String obtainCacheKey(ChapterItemBean chapterItemBean) {
                return null;
            }

            @Override
            public String obtainChapterKey(ChapterItemBean chapterItemBean) {
                return null;
            }

            @Override
            public String obtainChapterName(ChapterItemBean chapterItemBean) {
                return null;
            }

            @Override
            public String obtainChapterContent(ChapterContentBean chapterContentBean) {
                return null;
            }

            @Override
            public ChapterContentBean downLoad(ChapterItemBean chapterItemBean) {
                return null;
            }
        };


    }
}
