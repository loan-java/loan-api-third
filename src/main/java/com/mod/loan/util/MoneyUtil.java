package com.mod.loan.util;
import java.math.BigDecimal;


public class MoneyUtil {

    /**
     * 综合费用=借款金额*综合费率
     *
     * @param borrowMoney 借款金额
     * @param totalRate  综合费率
     * @return totalFee 综合费用
     */
    public static BigDecimal totalFee(BigDecimal borrowMoney,  BigDecimal totalRate) {
        BigDecimal totalFee = borrowMoney.multiply(totalRate.divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP));
        return totalFee.setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 利息=借款金额*利率*借款期限
     * @param borrowMoney 借款金额
     * @param borrowDay 借款期限
     * @param productRate 年化利率
     * @return interestFee
     */
    public static BigDecimal interestFee(BigDecimal borrowMoney, Integer borrowDay, BigDecimal productRate) {
        BigDecimal borrowDayDec = new BigDecimal(borrowDay);
        BigDecimal productRateDec = productRate.divide(new BigDecimal(1000),4,BigDecimal.ROUND_HALF_UP);
        BigDecimal interestFee = borrowMoney.multiply(borrowDayDec).multiply(productRateDec);
        return interestFee;
    }

    /**
     * 实际到账=借款金额-综合费用
     * @param borrowMoney 借款金额
     * @param totalFee 综合费用
     * @return actualMoney实际到账
     */
    public static BigDecimal actualMoney(BigDecimal borrowMoney, BigDecimal totalFee){
        return borrowMoney.subtract(totalFee);
    }

    /**
     *应还金额=借款金额+利息+逾期费用-还款减免金额
     * @param borrowMoney 借款金额
     * @param interestFee 利息
     * @param overdueFee 逾期金额
     * @param hadRepay 已还金额
     * @return shouldrepay 应还金额
     */
    public static BigDecimal shouldrepay(BigDecimal borrowMoney, BigDecimal interestFee,BigDecimal overdueFee,BigDecimal hadRepay){
        return borrowMoney.add(interestFee).add(overdueFee).subtract(hadRepay);
    }
}
