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
    public Boolean isFullLine;

    /**
     * 是否以<br><br>标志结束的
     */
    public Boolean endWithWrapMark;

    /**
     * 测量了的字符数据
     */
    public List<ShowChar> showChars = null;

    public Boolean hasData() {
        return showChars != null && showChars.size() > 0;
    }
}
