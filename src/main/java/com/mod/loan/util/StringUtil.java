package com.mod.loan.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

public class StringUtil {

    /**
     * 根据前缀生成订单编号
     *
     * @return
     */
    public static String getOrderNumber(String prefix) {
        // TODO 目前生成规则为：日期精确到毫秒
        return prefix + TimeUtils.parseTime(new Date(), TimeUtils.dateformat6) + RandomUtils.generateRandomNum(6);
    }

    /**
     * 银行卡保留后四位
     *
     * @param bankCard 银行卡号
     * @return
     */
    public static String bankTailNo(String bankCard) {

        String pattern = "(?<=\\d{0})\\d(?=\\d{4})";

        if (bankCard == null || bankCard.isEmpty()) {
            return null;
        } else {
            return bankCard.replaceAll(pattern, "");
        }
    }

    /**
     * 整数金额处理
     *
     * @param money
     * @return
     */
    public static String moneyFormat(BigDecimal money) {
        return new DecimalFormat(".00").format(money);
    }

    public static void main(String[] args) {
        System.out.println(decodeUnicode("\\u7238\\u7238"));
    }

    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuilder buffer = new StringBuilder();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr;
            if (end == -1) {
                charStr = dataStr.substring(start + 2);
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            // 16进制parse整形字符串。
            char letter = (char) Integer.parseInt(charStr, 16);
            buffer.append(Character.toString(letter));
            start = end;
        }
        return buffer.toString();
    }
}
