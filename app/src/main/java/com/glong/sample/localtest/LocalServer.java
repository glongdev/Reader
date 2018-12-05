package com.glong.sample.localtest;


import com.glong.sample.entry.ChapterContentBean;
import com.glong.sample.entry.ChapterItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Garrett on 2018/11/23.
 * contact me krouky@outlook.com
 */
public class LocalServer {

    /**
     * 模拟网络请求
     *
     * @param bookId 书ID
     */
    public static void getChapterList(String bookId, final OnResponseCallback onResponseCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ChapterItemBean> chapters = new ArrayList<>();
                while (chapters.size() < 100) {
                    ChapterItemBean chapterItemBean = new ChapterItemBean();
                    chapterItemBean.setChapterId("id" + (chapters.size() + 1));
                    chapterItemBean.setChapterName("第" + (chapters.size() + 1) + "章 名言名句");
                    chapters.add(chapterItemBean);
                }
                onResponseCallback.onSuccess(chapters);
            }
        }).start();
    }

    /**
     * 模拟网络同步下载
     *
     * @return 章节内容
     */
    public static ChapterContentBean syncDownloadContent(ChapterItemBean chapterItemBean) {
        ChapterContentBean chapterContentBean = new ChapterContentBean();
        chapterContentBean.setChapterId(chapterItemBean.getChapterId());
        chapterContentBean.setChapterName(chapterItemBean.getChapterName());
        StringBuilder contentBuilder = new StringBuilder();
        while (contentBuilder.length() < 1000) {
//            contentBuilder.append(String.valueOf(chapterItemBean.getChapterName() + "</p>" +
//                    "　　</p>" +
//                    "　　世界上一成不变的东西，只有“任何事物都是在不断变化的”这条真理。</p> —— 斯里兰卡<br><br>" +
//                    "　　</p>" +
//                    "　　我需要三件东西：爱情友谊和图书。然而这三者之间何其相通！炽热的爱情可以充实图书的内容，图书又是人们最忠实的朋友。</p> —— 蒙田<br><br>" +
//                    "　　</p>" +
//                    "　　生活有度，人生添寿。</p> —— 书摘<br><br>"));
            contentBuilder.append(LocalConstant.CONTENT);

        }
        chapterContentBean.setChapterContent(contentBuilder.toString());
        return chapterContentBean;
    }

    public interface OnResponseCallback {
        void onSuccess(List<ChapterItemBean> chapters);

        void onError(Exception e);
    }
}
