package com.glong.sample.adpater;

import com.glong.reader.util.Request;
import com.glong.reader.widget.ReaderView;
import com.glong.sample.api.Api;
import com.glong.sample.entry.ChapterContent2Bean;
import com.glong.sample.entry.ChapterItemBean;

/**
 * Created by Garrett on 2018/11/28.
 * contact me krouky@outlook.com
 */
public class MyReaderAdapter extends ReaderView.Adapter<ChapterItemBean, ChapterContent2Bean> {
    @Override
    public String obtainCacheKey(ChapterItemBean chapterItemBean) {
        return chapterItemBean.getChapterId() + "userId";
    }

    @Override
    public String obtainChapterName(ChapterItemBean chapterItemBean) {
        return chapterItemBean.getChapterName();
    }

    @Override
    public String obtainChapterContent(ChapterContent2Bean ChapterContent2Bean) {
        return ChapterContent2Bean.getResult().getData().get(0).getSub2();
    }

    @Override
    public ChapterContent2Bean downLoad(ChapterItemBean chapterItemBean) {
        return null;//LocalServer.syncDownloadContent(chapterItemBean);
    }

    @Override
    public Request requestParams(ChapterItemBean chapterItemBean) {
        return new Request.Builder().baseUrl("http://apis.juhe.cn/goodbook/query")
                .addUrlParams("key", Api.KEY).addUrlParams("catalog_id", chapterItemBean.getChapterId())
                .addUrlParams("pn", 0).addUrlParams("rn", 1)
                .get().build();
    }
}
