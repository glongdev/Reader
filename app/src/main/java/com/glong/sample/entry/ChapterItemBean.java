package com.glong.sample.entry;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public class ChapterItemBean {

    @SerializedName("id")
    private String chapterId;

    @SerializedName("catalog")
    private String chapterName;

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
}
