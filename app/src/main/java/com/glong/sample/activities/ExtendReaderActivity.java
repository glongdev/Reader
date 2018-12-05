package com.glong.sample.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.SeekBar;
import android.widget.Toast;

import com.glong.reader.TurnStatus;
import com.glong.reader.widget.EffectOfCover;
import com.glong.reader.widget.EffectOfNon;
import com.glong.reader.widget.EffectOfRealBothWay;
import com.glong.reader.widget.EffectOfRealOneWay;
import com.glong.reader.widget.EffectOfSlide;
import com.glong.reader.widget.PageChangedCallback;
import com.glong.reader.widget.ReaderView;
import com.glong.sample.R;
import com.glong.sample.ScreenUtils;
import com.glong.sample.adpater.MyAdapter;
import com.glong.sample.entry.ChapterContentBean;
import com.glong.sample.entry.ChapterItemBean;
import com.glong.sample.localtest.LocalServer;
import com.glong.sample.view.MenuView;
import com.glong.sample.view.SettingView;
import com.glong.sample.view.SimpleOnSeekBarChangeListener;

import java.util.List;

public class ExtendReaderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ExtendReaderActivity.class.getSimpleName();

    private ReaderView mReaderView;
    private ReaderView.ReaderManager mReaderManager;
    private ReaderView.Adapter<ChapterItemBean, ChapterContentBean> mAdapter;

    private MenuView mMenuView;// 菜单View
    private SettingView mSettingView;// 设置View
    private SeekBar mChapterSeekBar;//调节章节的SeekBar
    private SeekBar mLightSeekBar;// 调节亮度的SeekBar
    private SeekBar mTextSizeSeekBar;//调节字号的SeekBar
    private SeekBar mTextSpaceSeekBar;// 调节文字间距的SeekBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend_reader);

        initReader();
        initView();
        initToolbar();
        initData();
    }

    private void initView() {
        mMenuView = findViewById(R.id.menu_view);
        mSettingView = findViewById(R.id.setting_view);
        mChapterSeekBar = findViewById(R.id.chapter_seek_bar);
        mLightSeekBar = findViewById(R.id.light_seek_bar);
        mTextSizeSeekBar = findViewById(R.id.text_size_seek_bar);
        mTextSpaceSeekBar = findViewById(R.id.text_space_seek_bar);


        findViewById(R.id.setting).setOnClickListener(this);//设置
        findViewById(R.id.text_prev_chapter).setOnClickListener(this);//上一章
        findViewById(R.id.text_next_chapter).setOnClickListener(this);//下一章
        findViewById(R.id.reader_catalogue).setOnClickListener(this);//目录
        // 切换背景
        findViewById(R.id.reader_bg_0).setOnClickListener(this);
        findViewById(R.id.reader_bg_1).setOnClickListener(this);
        findViewById(R.id.reader_bg_2).setOnClickListener(this);
        findViewById(R.id.reader_bg_3).setOnClickListener(this);
        // 切换翻页效果
        findViewById(R.id.effect_real_one_way).setOnClickListener(this);
        findViewById(R.id.effect_real_both_way).setOnClickListener(this);
        findViewById(R.id.effect_cover).setOnClickListener(this);
        findViewById(R.id.effect_slide).setOnClickListener(this);
        findViewById(R.id.effect_non).setOnClickListener(this);

        mChapterSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                TurnStatus turnStatus = mReaderManager.toSpecifiedChapter(seekBar.getProgress(), 0);
                if (turnStatus == TurnStatus.LOAD_SUCCESS)
                    mReaderView.invalidateBothPage();
            }
        });

        mLightSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ScreenUtils.changeAppBrightness(ExtendReaderActivity.this, progress);
            }
        });

        mTextSizeSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReaderView.setTextSize(progress + 20);//文字大小限制最小20
            }
        });

        mTextSpaceSeekBar.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mReaderView.setLineSpace(progress);
            }
        });

        mLightSeekBar.setMax(255);
        mTextSizeSeekBar.setMax(100);
        mTextSpaceSeekBar.setMax(200);

        // 初始化SeekBar位置
        mChapterSeekBar.setProgress(0);// 如果需要历史纪录的话，可以在这里实现
        mLightSeekBar.setProgress(ScreenUtils.getSystemBrightness(this));
        mTextSizeSeekBar.setProgress(mReaderView.getTextSize() - 20);
        mTextSpaceSeekBar.setProgress(mReaderView.getLineSpace());
    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.extend_reader_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_cache:
                Toast.makeText(this, "删除缓存" + (mReaderManager.getCache().removeAll() ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initReader() {
        mReaderView = findViewById(R.id.extend_reader);
        mReaderManager = new ReaderView.ReaderManager();
        mAdapter = new MyAdapter();

        mReaderView.setReaderManager(mReaderManager);
        mReaderView.setAdapter(mAdapter);

        mReaderView.setPageChangedCallback(new PageChangedCallback() {
            @Override
            public TurnStatus toPrevPage() {
                TurnStatus turnStatus = mReaderManager.toPrevPage();
                if (turnStatus == TurnStatus.NO_PREV_CHAPTER) {
                    Toast.makeText(ExtendReaderActivity.this, "没有上一页啦", Toast.LENGTH_SHORT).show();
                }
                return turnStatus;
            }

            @Override
            public TurnStatus toNextPage() {
                TurnStatus turnStatus = mReaderManager.toNextPage();
                if (turnStatus == TurnStatus.NO_NEXT_CHAPTER) {
                    Toast.makeText(ExtendReaderActivity.this, "没有下一页啦", Toast.LENGTH_SHORT).show();
                }
                return turnStatus;
            }
        });
    }

    private void initData() {
        LocalServer.getChapterList("1", new LocalServer.OnResponseCallback() {
            @Override
            public void onSuccess(List<ChapterItemBean> chapters) {
                mAdapter.setChapterList(chapters);
                mAdapter.notifyDataSetChanged();
                mChapterSeekBar.setMax(chapters.size() - 1);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private long mDownTime;
    private float mDownX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int longClickTime = ViewConfiguration.get(this).getLongPressTimeout();
        int touchSlop = ViewConfiguration.get(this).getScaledPagingTouchSlop();
        int screenWidth = ScreenUtils.getScreenWidth(this);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (ev.getX() > screenWidth / 3 && ev.getX() < screenWidth * 2 / 3) {
                    mDownTime = System.currentTimeMillis();
                } else {
                    mDownTime = 0;
                }
                mDownX = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - mDownTime < longClickTime && (Math.abs(ev.getX() - mDownX) < touchSlop)) {

                    if (!mMenuView.isShowing() && !mSettingView.isShowing()) {
                        Log.d(TAG, "show menuView!");
                        mMenuView.show();
                        return true;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                mMenuView.dismiss();
                mSettingView.show();
                break;
            case R.id.text_prev_chapter://上一章
                TurnStatus turnStatus = mReaderManager.toPrevChapter();
                if (turnStatus == TurnStatus.LOAD_SUCCESS) {
                    mReaderView.invalidateBothPage();
                    mChapterSeekBar.setProgress(mChapterSeekBar.getProgress() - 1);
                } else if (turnStatus == TurnStatus.DOWNLOADING) {
                    mChapterSeekBar.setProgress(mChapterSeekBar.getProgress() - 1);
                } else if (turnStatus == TurnStatus.NO_PREV_CHAPTER) {
                    Toast.makeText(this, "没有上一章啦", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.text_next_chapter://下一章
                TurnStatus turnStatus2 = mReaderManager.toNextChapter();
                if (turnStatus2 == TurnStatus.LOAD_SUCCESS) {
                    mReaderView.invalidateBothPage();
                    mChapterSeekBar.setProgress(mChapterSeekBar.getProgress() + 1);
                } else if (turnStatus2 == TurnStatus.DOWNLOADING) {
                    mChapterSeekBar.setProgress(mChapterSeekBar.getProgress() + 1);
                } else if (turnStatus2 == TurnStatus.NO_NEXT_CHAPTER) {
                    Toast.makeText(this, "没有下一章啦", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reader_catalogue://目录
                break;
            // 切换背景
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
            //切换翻页效果
            case R.id.effect_real_one_way:
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

            // 日间/夜间模式的切换可参考：https://www.jianshu.com/p/ef3d05809dce
        }
    }
}
