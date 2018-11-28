package com.glong.reader.textconvert;

import android.graphics.RectF;

/**
 * 可见字符数据封装
 */
public class ShowChar {
    /**
     * 字符数据
     */
    public char charData;

    /**
     * 当前字符是否被选中
     */
    public boolean selected = false;

    /**
     * 字符宽度
     */
    public float charWidth = 0;

    /**
     * 当前字符在当前章节中的索引
     */
    public int indexInChapter;

    /**
     * 当前字符 上下左右四个位置
     */
    public RectF rectF;
//    public Point topLeftPosition = null;
//    public Point topRightPosition = null;
//    public Point bottomLeftPosition = null;
//    public Point bottomRightPosition = null;

}