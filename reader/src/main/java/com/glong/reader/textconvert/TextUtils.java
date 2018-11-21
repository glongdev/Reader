package com.glong.reader.textconvert;

import android.graphics.Paint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 测量工具
 */
public class TextUtils {

    public static Set<String> sParagraph = new HashSet<>();

    static {
        sParagraph.add("<br><br>");
        sParagraph.add("<br>");
        sParagraph.add("</p>");
    }

    /**
     * 截取一行的Char
     *
     * @param cs           字符串源
     * @param measureWidth 行测量的最大宽度
     * @param textPadding  字符间距
     * @param paint        测量的画笔
     * @return 如果cs为空或者长度为0，返回null
     * --------------------
     * TODO
     * --------------------
     */
    public static BreakResult breakText(char[] cs, float measureWidth, float textPadding, Paint paint) {
        if (cs == null || cs.length == 0) {
            return null;
        }

        BreakResult breakResult = new BreakResult();
        breakResult.showChars = new ArrayList<>();
        float width = 0;

        for (int i = 0, size = cs.length; i < size; i++) {
            String measureStr = String.valueOf(cs[i]);
            float charWidth = paint.measureText(measureStr);

            for (String paragraph : sParagraph) {
                if (paragraph != null && paragraph.length() > 0 && size - i >= paragraph.length()) {
                    char[] paragraphArray = paragraph.toCharArray();
                    int length = paragraphArray.length;
                    boolean isLineFeed = true;
                    for (int j = 0; j < length; j++) {
                        if (paragraphArray[j] != cs[i + j]) {
                            isLineFeed = false;
                            break;
                        }
                    }
                    if (isLineFeed) {
                        breakResult.chartNums = i + length;
                        breakResult.isFullLine = true;
                        breakResult.endWithWrapMark = true;
                        return breakResult;
                    }
                }
            }

            if (width <= measureWidth && (width + textPadding + charWidth) > measureWidth) {
                breakResult.chartNums = i;
                breakResult.isFullLine = true;
                return breakResult;
            }

            ShowChar showChar = new ShowChar();
            showChar.charData = cs[i];
            showChar.charWidth = charWidth;
            breakResult.showChars.add(showChar);
            width += charWidth + textPadding;
        }

        breakResult.chartNums = cs.length;
        return breakResult;
    }

    public static BreakResult breakText(String text, float measureWidth, float textPadding, Paint paint) {
        if (android.text.TextUtils.isEmpty(text)) {
            return null;
        }
        return breakText(text.toCharArray(), measureWidth, textPadding, paint);
    }

    /**
     * 测量能显示多少行
     *
     * @param measureHeight 最大高度
     * @param lineSpace     行间距
     * @param paint         画笔
     * @return 行数
     */
    public static int measureLines(float measureHeight, float lineSpace, Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = fm.bottom - fm.top;
        float heightEveryLine = textHeight + lineSpace;
        return (int) ((measureHeight - lineSpace) / heightEveryLine);
    }

    /**
     * 截取字符串
     *
     * @param src          源字符串
     * @param measureWidth 文字展示长度
     * @param textPadding  文字padding
     * @param paint        Paint
     * @return
     */
    public static List<ShowLine> breakToLineList(String src, float measureWidth, float textPadding, Paint paint) {
        String textData = src;
        List<ShowLine> showLines = new ArrayList<>();

        while (textData.length() > 0) {
            BreakResult breakResult = breakText(textData, measureWidth, textPadding, paint);
            ShowLine showLine = new ShowLine();
            showLine.charsData = breakResult.showChars;
            if (showLine.getLineFirstIndexInChapter() != -1) {
                showLines.add(showLine);
            }
            textData = textData.substring(breakResult.chartNums);
        }

        //给每个字符添加当前章节中的索引（即所有字符串中的索引）
        int indexCharInChapter = 0;
        int indexLineInChapter = 0;
        for (ShowLine everyLine : showLines) {
            everyLine.indexInChapter = indexLineInChapter;
            indexLineInChapter++;
            for (ShowChar everyChar : everyLine.charsData) {
                everyChar.indexInChapter = indexCharInChapter;
                indexCharInChapter++;
            }
        }
        return showLines;
    }
}