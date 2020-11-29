package com.github.ylfjm.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class UUIDUtil {

    private static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] num_digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    //兑换码显示规则：小写英文字母+数字
    private static final char[] JD_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * 生成16位随机UUID
     */
    public static String uuid() {
        Random localRandom = new Random();
        char[] arrayOfChar = new char[16];
        for (int i = 0; i < arrayOfChar.length; i++) {
            arrayOfChar[i] = digits[localRandom.nextInt(digits.length)];
        }
        return new String(arrayOfChar);
    }

    public static String numuuid(int length) {
        Random random = new Random();
        char[] arrayOfChar = new char[length];
        for (int i = 0; i < arrayOfChar.length; i++) {
            arrayOfChar[i] = num_digits[random.nextInt(num_digits.length)];
        }
        return new String(arrayOfChar);
    }

    public static String longuuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String longuuid32() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    /**
     * 订单号生成规则<br/>
     * 订单号编码共16 位，采用纯数字，第1 位代表产品类型编码，第2~9
     * 位采用8位日期，第10~16位采用随机数。
     *
     * @param orderType ==================================
     *                  ===     1    ===    智能净水    ===
     *                  ===     2    ===    健康食品    ===
     *                  ===     3    ===    生物理疗    ===
     *                  ===     4    ===    健康睡眠    ===
     *                  ===     5    ===    健康评估    ===
     *                  ===     20   ===    商户订单号  ===
     *                  ===     21   ===    推送内容    ===
     *                  ===     22   ===    短信内容    ===
     *                  ==================================
     * @return 订单号
     */
    public static long buildOrderId(int orderType) {
        //当前日期年月日
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        return Long.parseLong(orderType + dateStr + numuuid(7));
    }


    /**
     * 6位用户编号，第一位不为0
     *
     * @return 用户编号
     */
    public static String buildUserId() {
        return uuid();
    }

}
