package com.glong.sample.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import com.glong.reader.widget.EffectOfCover;
import com.glong.reader.widget.EffectOfNon;
import com.glong.reader.widget.EffectOfRealBothWay;
import com.glong.reader.widget.EffectOfRealOneWay;
import com.glong.reader.widget.EffectOfSlide;
import com.glong.reader.widget.ReaderView;
import com.glong.sample.R;
import com.glong.sample.entry.ChapterContentBean;
import com.glong.sample.entry.ChapterItemBean;
import com.glong.sample.localtest.LocalServer;

import java.util.List;

public class NormalReaderActivity extends AppCompatActivity implements View.OnClickListener {

    private ReaderView mReaderView;
    private ReaderView.Adapter<ChapterItemBean, ChapterContentBean> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_reader);

        initReader();
        initData();
        initViews();
    }

    private void initViews() {
        SeekBar textSizeSeek = findViewById(R.id.text_size_seek_bar);
        textSizeSeek.setMax(100);
        textSizeSeek.setProgress(mReaderView.getTextSize());
        textSizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReaderView.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar lineSpaceSeek = findViewById(R.id.line_space_seek_bar);
        lineSpaceSeek.setMax(100);
        lineSpaceSeek.setProgress(mReaderView.getLineSpace());
        lineSpaceSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReaderView.setLineSpace(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        findViewById(R.id.reader_bg_0).setOnClickListener(this);
        findViewById(R.id.reader_bg_1).setOnClickListener(this);
        findViewById(R.id.reader_bg_2).setOnClickListener(this);
        findViewById(R.id.reader_bg_3).setOnClickListener(this);

        findViewById(R.id.effect_real_one_way).setOnClickListener(this);
        findViewById(R.id.effect_real_both_way).setOnClickListener(this);
        findViewById(R.id.effect_cover).setOnClickListener(this);
        findViewById(R.id.effect_slide).setOnClickListener(this);
        findViewById(R.id.effect_non).setOnClickListener(this);
        findViewById(R.id.effect_default).setOnClickListener(this);
    }

    private void initReader() {
        final String userId = "123";
        mReaderView = findViewById(R.id.normal_reader_view);

        ReaderView.ReaderManager readerManager = new ReaderView.ReaderManager();
        mReaderView.setReaderManager(readerManager);

        mAdapter = new ReaderView.Adapter<ChapterItemBean, ChapterContentBean>() {

            @Override
            public String obtainCacheKey(ChapterItemBean chapterItemBean) {
                return chapterItemBean.getChapterId() + userId;
            }

            @Override
            public String obtainChapterName(ChapterItemBean chapterItemBean) {
                return chapterItemBean.getChapterName();
            }

            @Override
            public String obtainChapterContent(ChapterContentBean contentBean) {
                return contentBean.getChapterContent();
            }

            /**
             * 这个方法运行在子线程中，同步返回章节内容
             */
            @Override
            public ChapterContentBean downLoad(ChapterItemBean chapterItemBean) {
                return LocalServer.syncDownloadContent(chapterItemBean);
            }
        };

        mReaderView.setAdapter(mAdapter);
    }

    private void initData() {
        /*
         * 获取章节列表
         */
        LocalServer.getChapterList("123", new LocalServer.OnResponseCallback() {
            @Override
            public void onSuccess(final List<ChapterItemBean> chapters) {
                mAdapter.setChapterList(chapters);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reader_bg_0:
                mReaderView.setBackgroundColor(getResources().getColor(R.color.reader_bg_0));
                break;
            case R.id.reader_bg_1:
                mReaderView.setBackgroundColor(getResources().getColor(R.color.reader_bg_1));
                break;
            case R.id.reader_bg_2:
                mReaderView.setBackgroundColor(getResources().getColor(R.color.reader_bg_2));
                break;
            case R.id.reader_bg_3:
                mReaderView.setBackgroundColor(getResources().getColor(R.color.reader_bg_3));
                break;
            case R.id.effect_real_one_way:
            case R.id.effect_default:
                mReaderView.setEffect(new EffectOfRealOneWay(this));
                break;
            case R.id.effect_real_both_way:
                mReaderView.setEffect(new EffectOfRealBothWay(this));
                break;
            case R.id.effect_cover:
                mReaderView.setEffect(new EffectOfCover(this));
                break;
            case R.id.effect_slide:
                mReaderView.setEffect(new EffectOfSlide(this));
                break;
            case R.id.effect_non:
                mReaderView.setEffect(new EffectOfNon(this));
                break;
        }
    }
}
