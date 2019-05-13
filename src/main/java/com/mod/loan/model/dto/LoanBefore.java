package com.mod.loan.model.dto;

public class LoanBefore {

	private String event;
	
	private String eventTime;
	
	private String eventDescribe;
	
	private Integer remainDay;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public String getEventDescribe() {
		return eventDescribe;
	}

	public void setEventDescribe(String eventDescribe) {
		this.eventDescribe = eventDescribe;
	}

	public Integer getRemainDay() {
		return remainDay;
	}

	public void setRemainDay(Integer remainDay) {
		this.remainDay = remainDay;
	}

}
