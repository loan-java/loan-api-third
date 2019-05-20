package com.mod.loan.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 规则处理结果
 *
 * @author lijing
 * @date 2017/11/7 0007
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QjldHeTianResultDetailDTO implements Serializable {
    /**
     * 近1个月贷款机构失败扣款笔数
     */
    @JsonProperty("latest_one_month_fail")
    private String latest_one_month_fail;

    /**
     * 历史贷款机构成功扣款笔数
     */
    @JsonProperty("history_suc_fee")
    private String history_suc_fee;

    /**
     * 近1个月贷款机构成功扣款笔数
     */
    @JsonProperty("latest_one_month_suc")
    private String latest_one_month_suc;

    /**
     * 贷款行为分
     */
    @JsonProperty("loans_score")
    private String loans_score;

    /**
     * 信用贷款时长
     */
    @JsonProperty("loans_long_time")
    private String loans_long_time;

    /**
     * 近6个月贷款笔数
     */
    @JsonProperty("B_latest_six_month")
    private String B_latest_six_month;

    /**
     * 查询网络贷款类机构数
     */
    @JsonProperty("query_cash_count")
    private String query_cash_count;

    /**
     * 近3个月总查询笔数
     */
    @JsonProperty("latest_three_month")
    private String latest_three_month;

    /**
     * 申请准入分
     */
    @JsonProperty("apply_score")
    private String apply_score;

    /**
     * 近6个月总查询笔数
     */
    @JsonProperty("A_latest_six_month")
    private String A_latest_six_month;

    /**
     * 近1个月总查询笔数
     */
    @JsonProperty("latest_one_month")
    private String latest_one_month;

    /**
     * 最近查询时间
     */
    @JsonProperty("latest_query_time")
    private String latest_query_time;

    /**
     * 查询消费金融类机构数
     */
    @JsonProperty("query_finance_count")
    private String query_finance_count;

    /**
     * 逾期时间1
     */
    @JsonProperty("overdue_date")
    private String overdue_date;

    /**
     * 网络贷款机构平均授信额度
     */
    @JsonProperty("loans_avg_limit")
    private String loans_avg_limit;

    /**
     * 最大履约金额
     */
    @JsonProperty("max_performance_amt")
    private String max_performance_amt;
    /**
     * 履约笔数
     */
    @JsonProperty("count_performance")
    private String count_performance;

    /**
     * 小额现金分期分数
     */
    @JsonProperty("short")
    private String shorts;
    /**
     * 最大逾期金额
     */
    @JsonProperty("max_overdue_amt")
    private String max_overdue_amt;
    /**
     * 探针C探查结论
     */
    @JsonProperty("result_code")
    private String result_code;


}
