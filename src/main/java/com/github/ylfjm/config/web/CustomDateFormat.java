package com.github.ylfjm.config.web;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.springframework.util.StringUtils;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateFormat extends StdDateFormat {

    private static final String format1 = "yyyy-MM";
    private static final String format2 = "yyyy-MM-dd";
    private static final String format3 = "yyyy-MM-dd HH:mm";
    private static final String format4 = "yyyy-MM-dd HH:mm:ss";
    private static final String format5 = "yyyy/MM";
    private static final String format6 = "yyyy/MM/dd";
    private static final String format7 = "yyyy/MM/dd HH:mm";
    private static final String format8 = "yyyy/MM/dd HH:mm:ss";

    public static final CustomDateFormat instance = new CustomDateFormat();

    @Override
    public Date parse(String dateStr, ParsePosition pos) {
        return getDate(dateStr, pos);
    }

    @Override
    public Date parse(String dateStr) {
        ParsePosition pos = new ParsePosition(0);
        return getDate(dateStr, pos);
    }

    private Date getDate(String dateStr, ParsePosition pos) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        if (dateStr.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(dateStr, format1, pos);
        } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(dateStr, format2, pos);
        } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, format3, pos);
        } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, format4, pos);
        } else if (dateStr.matches("^\\d{4}/\\d{1,2}$")) {
            return parseDate(dateStr, format5, pos);
        } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
            return parseDate(dateStr, format6, pos);
        } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, format7, pos);
        } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, format8, pos);
        } else {
            // throw new IllegalArgumentException("Invalid boolean value '" + dateStr + "'");
            return super.parse(dateStr, pos);
        }
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        SimpleDateFormat sdf = new SimpleDateFormat(format4);
        return sdf.format(date, toAppendTo, fieldPosition);
    }

    @Override
    public CustomDateFormat clone() {
        return new CustomDateFormat();
    }

    /**
     * @param dateStr 字符类型的日期
     * @param pattern 转换格式
     */
    private static Date parseDate(String dateStr, String pattern, ParsePosition pos) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(dateStr, pos);
        } catch (Exception e) {

        }
        return null;
    }
}