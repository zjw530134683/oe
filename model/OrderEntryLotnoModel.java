/*
 * Project Name:iVision
 * File Name:OrderEntryBatchModel.java
 * Package Name:com.tce.ivision.modules.oe.model
 * Date:2012/12/11下午1:23:48
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.model;

import com.tce.ivision.model.Hold;
import com.tce.ivision.model.OrderLine;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.model.WaferStatus;

/**
 * ClassName: OrderEntryBatchModel <br/>
 * date: 2012/12/11 下午1:23:48 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderEntryLotnoModel {
	String mode;
	OrderLineLotno orderLineLotno;
	Hold hold;//是否有對應到hold
	int customerLotnoLen;
	int tmpWaferStatus;//2013.02.18
	String orderHeaderOrderStatus;//2013.07.09
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public OrderLineLotno getOrderLineLotno() {
		return orderLineLotno;
	}
	public void setOrderLineLotno(OrderLineLotno orderLineLotno) {
		this.orderLineLotno = orderLineLotno;
	}
	public Hold getHold() {
		return hold;
	}
	public void setHold(Hold hold) {
		this.hold = hold;
	}
	public int getCustomerLotnoLen() {
		return customerLotnoLen;
	}
	public void setCustomerLotnoLen(int customerLotnoLen) {
		this.customerLotnoLen = customerLotnoLen;
	}
	public int getTmpWaferStatus() {
		return tmpWaferStatus;
	}
	public void setTmpWaferStatus(int tmpWaferStatus) {
		this.tmpWaferStatus = tmpWaferStatus;
	}
	public String getOrderHeaderOrderStatus() {
		return orderHeaderOrderStatus;
	}
	public void setOrderHeaderOrderStatus(String orderHeaderOrderStatus) {
		this.orderHeaderOrderStatus = orderHeaderOrderStatus;
	}
}
