package com.tce.ivision.modules.oe.model;

import java.util.Date;



public class OrderSchedulingParameter{

	private String customerId;

	private String product;

	
	private String poNumber;
	
	private String lotNo;
	
	private boolean closedFlag;
	
	private Date beginDate;
	
	private Date endDate;

	public OrderSchedulingParameter(){
		customerId="";
		product="";
		poNumber="";
		lotNo="";
		closedFlag=false;
		beginDate=null;
		endDate=null;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getLotNo() {
		return lotNo;
	}

	public void setLotNo(String lotNo) {
		this.lotNo = lotNo;
	}

	public boolean isClosedFlag() {
		return closedFlag;
	}

	public void setClosedFlag(boolean closedFlag) {
		this.closedFlag = closedFlag;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "OrderSchedulingParameter [customerId=" + customerId
				+ ", product=" + product + ", poNumber=" + poNumber
				+ ", lotNo=" + lotNo + ", closedFlag=" + closedFlag
				+ ", beginDate=" + beginDate + ", endDate=" + endDate + "]";
	}
	

}