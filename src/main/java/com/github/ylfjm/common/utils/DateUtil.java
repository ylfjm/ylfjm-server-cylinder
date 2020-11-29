package com.github.ylfjm.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    // 默认时间格式化参数

    public static final String defaultDateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 按指定格式，格式化指定的日期，并以字符串形式返回。
     */
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(defaultDateTimeFormat);
        return formatter.format(date);
    }

    /**
     * JWT过期时间设置在凌晨，使用概率比较低的时间点，避免用户正在使用token过期，数据丢失
     *
     * @param expire 单位：秒
     */
    public static Date JWTTokenExpirationTime(Date date, int expire) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //expire秒之后的时间
        calendar.add(Calendar.SECOND, expire);
        return calendar.getTime();
    }

    public static Date minuteAfter(Date date, int expire) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, expire);
        return calendar.getTime();
    }

}
