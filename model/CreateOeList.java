package com.tce.ivision.modules.oe.model;

import java.math.BigDecimal;
import java.util.Date;

import org.zkoss.util.resource.Labels;


public class CreateOeList{
	
	private String orderNumber = "";
	private String poItem = "";
	private String customerLotNo = "";
	private String waferQty = "";

    public CreateOeList() {
	}

	/**
	 * orderNumber.
	 *
	 * @return  the orderNumber
	 * @since   JDK 1.6
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * orderNumber.
	 *
	 * @param   orderNumber    the orderNumber to set
	 * @since   JDK 1.6
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * poItem.
	 *
	 * @return  the poItem
	 * @since   JDK 1.6
	 */
	public String getPoItem() {
		return poItem;
	}

	/**
	 * poItem.
	 *
	 * @param   poItem    the poItem to set
	 * @since   JDK 1.6
	 */
	public void setPoItem(String poItem) {
		this.poItem = poItem;
	}

	/**
	 * customerLotNo.
	 *
	 * @return  the customerLotNo
	 * @since   JDK 1.6
	 */
	public String getCustomerLotNo() {
		return customerLotNo;
	}

	/**
	 * customerLotNo.
	 *
	 * @param   customerLotNo    the customerLotNo to set
	 * @since   JDK 1.6
	 */
	public void setCustomerLotNo(String customerLotNo) {
		this.customerLotNo = customerLotNo;
	}

	/**
	 * waferQty.
	 *
	 * @return  the waferQty
	 * @since   JDK 1.6
	 */
	public String getWaferQty() {
		return waferQty;
	}

	/**
	 * waferQty.
	 *
	 * @param   waferQty    the waferQty to set
	 * @since   JDK 1.6
	 */
	public void setWaferQty(String waferQty) {
		this.waferQty = waferQty;
	}

    
}