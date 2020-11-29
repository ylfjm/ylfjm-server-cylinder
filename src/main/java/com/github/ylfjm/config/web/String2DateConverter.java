package com.github.ylfjm.config.web;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Spring MVC String转Date类型全局转换器
 *
 * @author YLFJM
 * @date 2018/11/27
 */
// @Configuration
// public class String2DateConverter implements Converter<String, Date> {
public class String2DateConverter {

    private static final String format1 = "yyyy-MM";
    private static final String format2 = "yyyy-MM-dd";
    private static final String format3 = "yyyy-MM-dd HH:mm";
    private static final String format4 = "yyyy-MM-dd HH:mm:ss";
    private static final String format5 = "yyyy/MM";
    private static final String format6 = "yyyy/MM/dd";
    private static final String format7 = "yyyy/MM/dd HH:mm";
    private static final String format8 = "yyyy/MM/dd HH:mm:ss";

    // /**
    //  * Spring MVC String转Date类型全局转换器
    //  *
    //  * @param dateStr 接受到的字符串类型日期
    //  */
    // @Override
    // public Date convert(String dateStr) {
    //     if (!StringUtils.hasText(dateStr)) {
    //         return null;
    //     }
    //     dateStr = dateStr.trim();
    //     if (dateStr.matches("^\\d{4}-\\d{1,2}$")) {
    //         return parseDate(dateStr, format1);
    //     } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
    //         return parseDate(dateStr, format2);
    //     } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
    //         return parseDate(dateStr, format3);
    //     } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
    //         return parseDate(dateStr, format4);
    //     } else if (dateStr.matches("^\\d{4}/\\d{1,2}$")) {
    //         return parseDate(dateStr, format5);
    //     } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
    //         return parseDate(dateStr, format6);
    //     } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
    //         return parseDate(dateStr, format7);
    //     } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
    //         return parseDate(dateStr, format8);
    //     } else {
    //         throw new IllegalArgumentException("Invalid boolean value '" + dateStr + "'");
    //     }
    // }

    /**
     * Spring MVC String转Date类型全局转换器
     *
     * @param dateStr 接受到的字符串类型日期
     */
    public static Date parse(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        dateStr = dateStr.trim();
        if (dateStr.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(dateStr, format1);
        } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(dateStr, format2);
        } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, format3);
        } else if (dateStr.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, format4);
        } else if (dateStr.matches("^\\d{4}/\\d{1,2}$")) {
            return parseDate(dateStr, format5);
        } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2}$")) {
            return parseDate(dateStr, format6);
        } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, format7);
        } else if (dateStr.matches("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(dateStr, format8);
        } else {
            throw new IllegalArgumentException("Invalid boolean value '" + dateStr + "'");
        }
    }

    /**
     * @param dateStr 字符类型的日期
     * @param pattern 转换格式
     */
    private static Date parseDate(String dateStr, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(dateStr);
        } catch (Exception e) {

        }
        return null;
    }

}
