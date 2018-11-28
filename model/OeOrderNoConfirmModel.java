package com.tce.ivision.modules.oe.model;

import java.math.BigDecimal;
import java.util.Date;

import org.zkoss.util.resource.Labels;


public class OeOrderNoConfirmModel{
	
	private boolean select;
	private String orderNumber;
	private String product;
	private String customerLotNo;
	private String customerJob;
	private String waferData;

    public OeOrderNoConfirmModel() {
    }

	/**
	 * select.
	 *
	 * @return  the select
	 * @since   JDK 1.6
	 */
	public boolean isSelect() {
		return select;
	}

	/**
	 * select.
	 *
	 * @param   select    the select to set
	 * @since   JDK 1.6
	 */
	public void setSelect(boolean select) {
		this.select = select;
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
	 * product.
	 *
	 * @return  the product
	 * @since   JDK 1.6
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * product.
	 *
	 * @param   product    the product to set
	 * @since   JDK 1.6
	 */
	public void setProduct(String product) {
		this.product = product;
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
	 * customerJob.
	 *
	 * @return  the customerJob
	 * @since   JDK 1.6
	 */
	public String getCustomerJob() {
		return customerJob;
	}

	/**
	 * customerJob.
	 *
	 * @param   customerJob    the customerJob to set
	 * @since   JDK 1.6
	 */
	public void setCustomerJob(String customerJob) {
		this.customerJob = customerJob;
	}

	/**
	 * waferData.
	 *
	 * @return  the waferData
	 * @since   JDK 1.6
	 */
	public String getWaferData() {
		return waferData;
	}

	/**
	 * waferData.
	 *
	 * @param   waferData    the waferData to set
	 * @since   JDK 1.6
	 */
	public void setWaferData(String waferData) {
		this.waferData = waferData;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OeOrderNoConfirmModel [select=" + select + ", orderNumber="
				+ orderNumber + ", product=" + product + ", customerLotNo="
				+ customerLotNo + ", customerJob=" + customerJob
				+ ", waferData=" + waferData + "]";
	}

}