package com.tce.ivision.modules.oe.model;

import java.math.BigDecimal;
import java.util.Date;

import org.zkoss.util.resource.Labels;


public class OffloadWaferConfirm{
	
	private String waferNo;
	private String waferConfirm;
	private String confirmDate;
	private String shippingConfirmIdx;


    public OffloadWaferConfirm() {
    }

    

	/**
	 * waferNo.
	 *
	 * @return  the waferNo
	 * @since   JDK 1.6
	 */
	public String getWaferNo() {
		return waferNo;
	}



	/**
	 * waferNo.
	 *
	 * @param   waferNo    the waferNo to set
	 * @since   JDK 1.6
	 */
	public void setWaferNo(String waferNo) {
		this.waferNo = waferNo;
	}



	/**
	 * waferConfirm.
	 *
	 * @return  the waferConfirm
	 * @since   JDK 1.6
	 */
	public String getWaferConfirm() {
		return waferConfirm;
	}



	/**
	 * waferConfirm.
	 *
	 * @param   waferConfirm    the waferConfirm to set
	 * @since   JDK 1.6
	 */
	public void setWaferConfirm(String waferConfirm) {
		this.waferConfirm = waferConfirm;
	}



	/**
	 * confirmDate.
	 *
	 * @return  the confirmDate
	 * @since   JDK 1.6
	 */
	public String getConfirmDate() {
		return confirmDate;
	}



	/**
	 * confirmDate.
	 *
	 * @param   confirmDate    the confirmDate to set
	 * @since   JDK 1.6
	 */
	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}
	
	public String getShippingConfirmIdx() {
		return shippingConfirmIdx;
	}
	
	public void setShippingConfirmIdx(String shippingConfirmIdx) {
		this.shippingConfirmIdx = shippingConfirmIdx;
	}



	@Override
	public String toString() {
		return "OffloadWaferConfirm [waferNo=" + waferNo
				+ ", waferConfirm=" + waferConfirm + ", confirmDate="
				+ confirmDate
				+ ",shippingConfirmIdx=" + shippingConfirmIdx
				+ "]";
	}
    

}