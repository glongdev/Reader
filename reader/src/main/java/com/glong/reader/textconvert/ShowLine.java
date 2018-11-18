package com.glong.reader.textconvert;

import java.util.List;

/**
 * 显示的行数据
 */
public class ShowLine {

    public List<ShowChar> charsData;

    /**
     * 当前行在当前章节的索引
     */
    public int indexInChapter;

    public String getLineData() {
        String lineData = "";
        if (charsData == null || charsData.size() == 0) return lineData;
        for (ShowChar c : charsData) {
            lineData = lineData + c.charData;
        }
        return lineData;
    }

    public int getLineFirstIndexInChapter() {
        if (charsData == null || charsData.size() <= 0) {
            return -1;
        }
        return charsData.get(0).indexInChapter;
    }

    public int getLineLastIndexInChapter() {
        if (charsData == null || charsData.size() <= 0) {
            return -1;
        }
        return charsData.get(charsData.size() - 1).indexInChapter;
    }
}
