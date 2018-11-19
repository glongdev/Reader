package com.glong.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.glong.reader.widget.ReaderView;
import com.glong.sample.support.ChapterContentBean;
import com.glong.sample.support.ChapterItemBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String str = "写作的时间长了之后，就有很多的小伙伴，都会问我，说，格格，你平时上班很忙、每天上下班通勤时间那么长，怎么还有时间做那么多事情呢？\n" +
            "\n" +
            "我的答案，特别简单，就是早起。我是从去春节时，开始每天5点起床的，到现在1年多了。\n" +
            "\n" +
            "好多人都对我能坚持早起，表示很佩服。其实，我自己并不觉得，这是一件多么厉害的事情。因为有很多人，都是早起一族，甚至比我起得更早。\n" +
            "\n" +
            "不过在早起之后，我发现，我对时间的管理确实更加高效了。\n" +
            "\n" +
            "那么，我当初为什么想要早起呢？\n" +
            "\n" +
            "其实，我当时是每天6:30起。但是我发现，自己想做的事情特别多，比如读书、写作、跑步、学习，可是老是觉得时间不够用。我仔细想了想，每一样我又都不想放弃。\n" +
            "\n" +
            "于是，我就读了一些时间管理的书，比如李笑来老师的书《把时间当作朋友》，还听了一些时间管理的课，比如古典老师的橙子学院的课，希望能解决时间管理的问题。\n" +
            "\n" +
            "在读书和听课后，我就果断决定，我要每天早上5点起床。睡觉时间，依然是23:00—24:00，并且，尽量在23:00前睡觉。\n" +
            "\n" +
            "当时，特别巧，有一个朋友也想每天5点起。于是，我们俩就商量好，每天早上起床后，就在我的读书会群里，发一个表情，算是早起打卡了。\n" +
            "\n" +
            "刚开始早起时，身体也不适应，会犯困，但慢慢身体就适应了。\n" +
            "\n" +
            "但是，我的意志也不是一直特别强大，也有过退缩、想放弃的时候。就是去年10月，我刚报无戒老师的写作训练营的时候。\n" +
            "\n" +
            "那时候北京5点起来，外面漆黑一片，天气也有点凉，我就有点不想早起了，不自觉就把生物钟，给调成了5:30起。\n" +
            "\n" +
            "但是，为了多一些学习的时间，我还是继续咬牙坚持每天5点起来听课。每天一边听课、一边记笔记。\n" +
            "\n" +
            "就这样，我算是经受了革命的考验，养成了早起的习惯。\n" +
            "\n" +
            "早起之后，你会发现，早起真的是非常棒的一件事。通过早起，你会发现你多出了很多的时间。而且，这个时间是整块的时间，能够不被别人打扰，非常高效地去做事情。\n" +
            "\n" +
            "如果你也想早起的话，那么，早起之后，你做什么，一定要提前规划好，才不至于浪费时间。\n" +
            "\n" +
            "有人喜欢早起后写作，写作其实是输出性的事情。\n" +
            "\n" +
            "我个人呢，更愿意在早起后做一些输入类的、重要而不紧急的事情。\n" +
            "\n" +
            "总结一下，我在早起后、上班之前的这段时间，大概有4个小时的时间。我除了听课学习外，还做了以下5件事情：\n" +
            "\n" +
            "（1）听音频：除了听无戒老师的写作课，我还购买了其他的音频产品，比如一些听书的APP，光听就可以了。我通常是在清晨起床后，一边洗漱、一边听音频，充分利用碎片时间。\n" +
            "\n" +
            "（2）读书：我除了利用通勤时间，在地铁上读书外，也会在早起后读书。如果早起后读书，就会有意识地读一些比较重要或比较烧脑的书。\n" +
            "\n" +
            "（3）写作：有时，我清晨起来，会有一些写作灵感。那我就会把这些灵感写下来，当作写作素材。等到中午休息或者晚上的时间，再把文章写出来。\n" +
            "\n" +
            "（4）跑步：在平时，我主要是早起去跑步。跑步的时候，我还会听书。每周跑步3次左右。冬天呢，则改为中午跑步。\n" +
            "\n" +
            "（5）学习：我每天都会背100个单词、看CHINA DAILY的新闻，以保持英语的语感。\n" +
            "\n" +
            "我用实践证明，只要你想，每个人都可以充分利用自己的时间，和时间作朋友，过上高效地生活。\n" +
            "\n" +
            "有些人可能觉得，我每天5起床，除了上班外，还要读书、跑步、写作、学英语，这样做太辛苦了。\n" +
            "\n" +
            "但是，我一点也不觉得辛苦。因为这些都是我真心喜欢、真心想做的事情，如果不去做，我反而会觉得很难受，好像生活中少了点什么。\n" +
            "\n" +
            "我知道，对于有些人而言，可能觉我做得这些努力，都微不足道。\n" +
            "\n" +
            "但是，对于我而言，正是这些点滴的努力，让我觉得特别地踏实。\n" +
            "\n" +
            "通过不断突破自己的舒适区，我感受到自己在一点点变化、一点点进步。虽然这变化和进步是那么地缓慢、不易察觉，但却可以被我真实的感知，让我走向一个越来越宽广的世界。\n" +
            "\n" +
            "我知道，自己智商不高，没有天赋，甚至有时会显得特别笨拙。所以，我就需要默默地努力，只有这样，才有可能实现我在疲惫生活中的英雄梦想。\n" +
            "\n" +
            "据说，陈道明老师在某个节目担任嘉宾时，节目里一群打鼓的孩子，当场受到了一些人的质疑。陈道明老师就以自己成名前7年跑龙套的经历，来鼓舞那些孩子们，对他们说：“你们一定要努力，但千万别着急。”\n" +
            "\n" +
            "我相信，凡是今天来听我分享的小伙伴，也一定都有自己的梦想。\n" +
            "\n" +
            "那么，现在，我也借花献佛，把同样的一句话，送给所有坚持努力的你：“你一定要努力，但千万别着急”。\n" +
            "\n" +
            "这就是我今天的分享。\n" +
            "\n" +
            "我希望在未来的日子里，能和大家一起努力、一起交流、一起进步，慢慢成长为更好的自己。\n" +
            "\n" +
            "谢谢大家！\n" +
            "\n" +
            "作者：修心的格格\n" +
            "链接：https://www.jianshu.com/p/49fd60b5393f\n" +
            "來源：简书\n" +
            "简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。";

    ReaderView.Adapter<ChapterItemBean, ChapterContentBean> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initData();
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

    private void init() {
        final ReaderView readerView = findViewById(R.id.reader_view);

        ReaderView.ReaderManager readerManager = new ReaderView.ReaderManager() {
            @Override
            public void startFromCache(String key, Class clazz, int charIndex) {
                super.startFromCache(key, clazz, charIndex);
            }
        };

        readerView.setReaderManager(readerManager);

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
                    Thread.sleep(1000);
                    ChapterContentBean chapterContentBean = new ChapterContentBean();
                    chapterContentBean.setChapterId(chapterItemBean.getChapterId());
                    chapterContentBean.setChapterName(chapterItemBean.getChapterName());
                    chapterContentBean.setChapterContent(chapterContentBean.getChapterName() + str + chapterContentBean.getChapterName());
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
