package com.tce.ivision.modules.oe.model;

import java.util.Date;



public class OffloadOperationParameter{

	private String customerId;
	
	private String customerName;

	private String product;

	private boolean customerFlag;
	
	private boolean orderDateFlag;
	
	private Date beginDate;
	
	private Date endDate;
	
	private boolean offloadCloseFlag;

	public OffloadOperationParameter(){
		customerId="";
		customerName="";
		product="";
		customerFlag=false;
		orderDateFlag=false;
		offloadCloseFlag=false;
		beginDate=null;
		endDate=null;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public boolean isCustomerFlag() {
		return customerFlag;
	}

	public void setCustomerFlag(boolean customerFlag) {
		this.customerFlag = customerFlag;
	}

	public boolean isOrderDateFlag() {
		return orderDateFlag;
	}

	public void setOrderDateFlag(boolean orderDateFlag) {
		this.orderDateFlag = orderDateFlag;
	}

	public boolean isOffloadCloseFlag() {
		return offloadCloseFlag;
	}

	public void setOffloadCloseFlag(boolean offloadCloseFlag) {
		this.offloadCloseFlag = offloadCloseFlag;
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
		return "OrderSchedulingParameter [customerId=" + customerId + ", customerName=" + customerName
				+ ", product=" + product + ", customerFlag=" + customerFlag
				+ ", orderDateFlag=" + orderDateFlag + ", offloadCloseFlag=" + offloadCloseFlag
				+ ", beginDate=" + beginDate + ", endDate=" + endDate + "]";
	}
	

}