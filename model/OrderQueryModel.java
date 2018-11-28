/*
 * Project Name:iVision
 * File Name:OrderQueryModel.java
 * Package Name:com.tce.ivision.modules.oe.model
 * Date:2012/12/13下午1:05:32
 * 
 * 說明:
 * OrderQueryViewCtrl.grdOrderData的modal
 * 
 * 修改歷史:
 * 2012.12.17 OCF#OE002 Fanny Initial
 * 
 */
package com.tce.ivision.modules.oe.model;

import java.util.Date;

import org.zkoss.zul.Button;

import com.tce.ivision.model.OrderHeader;


/**
 * ClassName: OrderQueryModel <br/>
 * date: 2012/12/13 下午1:05:32 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderQueryModel {
	String customerName;
	String orderTypeName;
	String billToName;
	String shipToName;
	String orderStatusName;
	OrderHeader orderHeader;
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getOrderTypeName() {
		return orderTypeName;
	}
	public void setOrderTypeName(String orderTypeName) {
		this.orderTypeName = orderTypeName;
	}
	public String getBillToName() {
		return billToName;
	}
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}
	public String getShipToName() {
		return shipToName;
	}
	public void setShipToName(String shipToName) {
		this.shipToName = shipToName;
	}
	public OrderHeader getOrderHeader() {
		return orderHeader;
	}
	public void setOrderHeader(OrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}
	public String getOrderStatusName() {
		return orderStatusName;
	}
	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}
	
}
