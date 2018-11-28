/*
 * Project Name:iVision
 * File Name:OrderEntryLineModel.java
 * Package Name:com.tce.ivision.modules.oe.model
 * Date:2012/12/12上午9:08:48
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.model;

import java.util.Date;

import com.tce.ivision.model.OrderLine;

/**
 * ClassName: OrderEntryLineModel <br/>
 * date: 2012/12/12 上午9:08:48 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderEntryLineModel {
	OrderLine orderLine;
	int orderLineIntIdx;
	
	public OrderLine getOrderLine() {
		return orderLine;
	}
	public void setOrderLine(OrderLine orderLine) {
		this.orderLine = orderLine;
	}
	public int getOrderLineIntIdx() {
		return orderLineIntIdx;
	}
	public void setOrderLineIntIdx(int orderLineIntIdx) {
		this.orderLineIntIdx = orderLineIntIdx;
	}
	
	
}
