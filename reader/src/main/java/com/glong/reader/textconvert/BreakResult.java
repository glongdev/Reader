package com.glong.reader.textconvert;

import java.util.List;

/**
 * 对图书内容测量之后的封装
 */
public class BreakResult {

    /**
     * 测量了的字符数
     */
    public int chartNums;

    /**
     * 是否满一行了
     */
    public boolean isFullLine;

    /**
     * 是否以分段符标志结束的
     */
    public boolean endWithWrapMark;

    /**
     * 测量了的字符数据
     */
    public List<ShowChar> showChars = null;

    public boolean hasData() {
        return showChars != null && showChars.size() > 0;
    }
}
