package com.glong.sample.adpater;

import com.glong.reader.widget.ReaderView;
import com.glong.sample.entry.ChapterContentBean;
import com.glong.sample.entry.ChapterItemBean;
import com.glong.sample.localtest.LocalServer;

/**
 * Created by Garrett on 2018/11/28.
 * contact me krouky@outlook.com
 */
public class MyReaderAdapter extends ReaderView.Adapter<ChapterItemBean, ChapterContentBean> {
    @Override
    public String obtainCacheKey(ChapterItemBean chapterItemBean) {
        return chapterItemBean.getChapterId() + "userId";
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
}
