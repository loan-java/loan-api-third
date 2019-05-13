package com.mod.loan.model.dto;

public class LoanIng {

	private long id;	//借款记录的id

	private String shouldRepay ;//待还款金额
	
	private String lastRepayTime; //最迟还款时间
	
	private Integer  DaysOfRepayment;//距离还款日还剩
	
	private Integer overdueDays; //逾期天数

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getShouldRepay() {
		return shouldRepay;
	}

	public void setShouldRepay(String shouldRepay) {
		this.shouldRepay = shouldRepay;
	}

	public String getLastRepayTime() {
		return lastRepayTime;
	}

	public void setLastRepayTime(String lastRepayTime) {
		this.lastRepayTime = lastRepayTime;
	}

	public Integer getDaysOfRepayment() {
		return DaysOfRepayment;
	}

	public void setDaysOfRepayment(Integer daysOfRepayment) {
		DaysOfRepayment = daysOfRepayment;
	}

	public Integer getOverdueDays() {
		return overdueDays;
	}

	public void setOverdueDays(Integer overdueDays) {
		this.overdueDays = overdueDays;
	}
	
	
	
}
