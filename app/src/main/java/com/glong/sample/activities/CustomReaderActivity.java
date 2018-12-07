package com.glong.sample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

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
                return "龟虽寿";//chapterItemBean.getChapterName();
            }

            @Override
            public String obtainChapterContent(ChapterContentBean chapterContentBean) {
                return "曹操</p>" + "神龟虽寿，犹有竟时；</p>" + "腾蛇乘雾，终为土灰。</p>"
                        + "老骥伏枥，志在千里；</p>" + "烈士暮年，壮心不已。</p>"
                        + "盈缩之期，不但在天；</p>" + "养怡之福，可得永年。</p>"
                        + "幸甚至哉，歌以咏志。</p>"
                        +
                        "曹操</p>" + "神龟虽寿，犹有竟时；</p>" + "腾蛇乘雾，终为土灰。</p>"
                        + "老骥伏枥，志在千里；</p>" + "烈士暮年，壮心不已。</p>"
                        + "盈缩之期，不但在天；</p>" + "养怡之福，可得永年。</p>"
                        + "幸甚至哉，歌以咏志。</p>"
                        +
                        "曹操</p>" + "神龟虽寿，犹有竟时；</p>" + "腾蛇乘雾，终为土灰。</p>"
                        + "老骥伏枥，志在千里；</p>" + "烈士暮年，壮心不已。</p>"
                        + "盈缩之期，不但在天；</p>" + "养怡之福，可得永年。</p>"
                        + "幸甚至哉，歌以咏志。</p>"
                        +
                        "曹操</p>" + "神龟虽寿，犹有竟时；</p>" + "腾蛇乘雾，终为土灰。</p>"
                        + "老骥伏枥，志在千里；</p>" + "烈士暮年，壮心不已。</p>"
                        + "盈缩之期，不但在天；</p>" + "养怡之福，可得永年。</p>"
                        + "幸甚至哉，歌以咏志。</p>";
            }

            @Override
            public ChapterContentBean downLoad(ChapterItemBean chapterItemBean) {
                return LocalServer.syncDownloadContent(chapterItemBean);
            }
        };

        mReaderView.setAdapter(mAdapter);
        readerManager.setCustomReaderResolve(new MyReaderResolve());
        mReaderView.setLineSpace(50);

        View firstPageView = LayoutInflater.from(this).inflate(R.layout.first_page_view_layout, null);
        firstPageView.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomReaderActivity.this, "You clicked ImageView!", Toast.LENGTH_SHORT).show();
            }
        });
        mReaderView.addView(firstPageView, ReaderView.ChildInPage.FIRST_PAGE);

        View lastPageView = LayoutInflater.from(this).inflate(R.layout.last_page_view_layout, null);
        lastPageView.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomReaderActivity.this, WebViewActivity.class));
            }
        });
        mReaderView.addView(lastPageView, ReaderView.ChildInPage.LAST_PAGE);
    }
}
