package com.tce.ivision.modules.oe.model;

import java.math.BigDecimal;
import java.util.Date;

import org.zkoss.util.resource.Labels;


public class OffloadOperation{
	
	private boolean select;
	private String offloadStatus;
	private String customer;
	private String orderNumber;
	private String poNo;
	private String product;
	private String customerLotNo;
	private String waferQty;
	private String orderPrice;
	private String currency;
	private String offloadType;
	private String offloadTo;
	private String offloadPo;
	private Date offloadDueDate;
	private String waferId;
	private Date confirmDate;
	private Date updateDate;
	private String updateUser;
	private Date cancelDate;
	private String cancelUser;
	private String cancelReason;
	private boolean cancelFlag;
	private boolean newFlag;
	private String billTo;
	private String shipTo;
	private String poItem;
	private String poNumber;

    public OffloadOperation() {
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
	 * offloadStatus.
	 *
	 * @return  the offloadStatus
	 * @since   JDK 1.6
	 */
	public String getOffloadStatus() {
		return offloadStatus;
	}



	/**
	 * offloadStatus.
	 *
	 * @param   offloadStatus    the offloadStatus to set
	 * @since   JDK 1.6
	 */
	public void setOffloadStatus(String offloadStatus) {
		this.offloadStatus = offloadStatus;
	}



	/**
	 * customer.
	 *
	 * @return  the customer
	 * @since   JDK 1.6
	 */
	public String getCustomer() {
		return customer;
	}



	/**
	 * customer.
	 *
	 * @param   customer    the customer to set
	 * @since   JDK 1.6
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
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
	 * poNo.
	 *
	 * @return  the poNo
	 * @since   JDK 1.6
	 */
	public String getPoNo() {
		return poNo;
	}



	/**
	 * poNo.
	 *
	 * @param   poNo    the poNo to set
	 * @since   JDK 1.6
	 */
	public void setPoNo(String poNo) {
		this.poNo = poNo;
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



	/**
	 * orderPrice.
	 *
	 * @return  the orderPrice
	 * @since   JDK 1.6
	 */
	public String getOrderPrice() {
		return orderPrice;
	}



	/**
	 * orderPrice.
	 *
	 * @param   orderPrice    the orderPrice to set
	 * @since   JDK 1.6
	 */
	public void setOrderPrice(String orderPrice) {
		this.orderPrice = orderPrice;
	}



	/**
	 * currency.
	 *
	 * @return  the currency
	 * @since   JDK 1.6
	 */
	public String getCurrency() {
		return currency;
	}



	/**
	 * currency.
	 *
	 * @param   currency    the currency to set
	 * @since   JDK 1.6
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}



	/**
	 * offloadType.
	 *
	 * @return  the offloadType
	 * @since   JDK 1.6
	 */
	public String getOffloadType() {
		return offloadType;
	}



	/**
	 * offloadType.
	 *
	 * @param   offloadType    the offloadType to set
	 * @since   JDK 1.6
	 */
	public void setOffloadType(String offloadType) {
		this.offloadType = offloadType;
	}



	/**
	 * offloadTo.
	 *
	 * @return  the offloadTo
	 * @since   JDK 1.6
	 */
	public String getOffloadTo() {
		return offloadTo;
	}



	/**
	 * offloadTo.
	 *
	 * @param   offloadTo    the offloadTo to set
	 * @since   JDK 1.6
	 */
	public void setOffloadTo(String offloadTo) {
		this.offloadTo = offloadTo;
	}



	/**
	 * offloadPo.
	 *
	 * @return  the offloadPo
	 * @since   JDK 1.6
	 */
	public String getOffloadPo() {
		return offloadPo;
	}



	/**
	 * offloadPo.
	 *
	 * @param   offloadPo    the offloadPo to set
	 * @since   JDK 1.6
	 */
	public void setOffloadPo(String offloadPo) {
		this.offloadPo = offloadPo;
	}



	/**
	 * offloadDueDate.
	 *
	 * @return  the offloadDueDate
	 * @since   JDK 1.6
	 */
	public Date getOffloadDueDate() {
		return offloadDueDate;
	}



	/**
	 * offloadDueDate.
	 *
	 * @param   offloadDueDate    the offloadDueDate to set
	 * @since   JDK 1.6
	 */
	public void setOffloadDueDate(Date offloadDueDate) {
		this.offloadDueDate = offloadDueDate;
	}



	/**
	 * waferId.
	 *
	 * @return  the waferId
	 * @since   JDK 1.6
	 */
	public String getWaferId() {
		return waferId;
	}



	/**
	 * waferId.
	 *
	 * @param   waferId    the waferId to set
	 * @since   JDK 1.6
	 */
	public void setWaferId(String waferId) {
		this.waferId = waferId;
	}



	/**
	 * confirmDate.
	 *
	 * @return  the confirmDate
	 * @since   JDK 1.6
	 */
	public Date getConfirmDate() {
		return confirmDate;
	}



	/**
	 * confirmDate.
	 *
	 * @param   confirmDate    the confirmDate to set
	 * @since   JDK 1.6
	 */
	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}



	/**
	 * updateDate.
	 *
	 * @return  the updateDate
	 * @since   JDK 1.6
	 */
	public Date getUpdateDate() {
		return updateDate;
	}



	/**
	 * updateDate.
	 *
	 * @param   updateDate    the updateDate to set
	 * @since   JDK 1.6
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}



	/**
	 * updateUser.
	 *
	 * @return  the updateUser
	 * @since   JDK 1.6
	 */
	public String getUpdateUser() {
		return updateUser;
	}



	/**
	 * updateUser.
	 *
	 * @param   updateUser    the updateUser to set
	 * @since   JDK 1.6
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}



	/**
	 * cancelDate.
	 *
	 * @return  the cancelDate
	 * @since   JDK 1.6
	 */
	public Date getCancelDate() {
		return cancelDate;
	}



	/**
	 * cancelDate.
	 *
	 * @param   cancelDate    the cancelDate to set
	 * @since   JDK 1.6
	 */
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}



	/**
	 * cancelUser.
	 *
	 * @return  the cancelUser
	 * @since   JDK 1.6
	 */
	public String getCancelUser() {
		return cancelUser;
	}



	/**
	 * cancelUser.
	 *
	 * @param   cancelUser    the cancelUser to set
	 * @since   JDK 1.6
	 */
	public void setCancelUser(String cancelUser) {
		this.cancelUser = cancelUser;
	}



	/**
	 * cancelReason.
	 *
	 * @return  the cancelReason
	 * @since   JDK 1.6
	 */
	public String getCancelReason() {
		return cancelReason;
	}



	/**
	 * cancelReason.
	 *
	 * @param   cancelReason    the cancelReason to set
	 * @since   JDK 1.6
	 */
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	
	public boolean isNewFlag() {
		return newFlag;
	}
	
	public void setNewFlag(boolean newFlag) {
		this.newFlag = newFlag;
	}
	
	public boolean isCancelFlag() {
		return cancelFlag;
	}
	
	public void setCancelFlag(boolean cancelFlag) {
		this.cancelFlag = cancelFlag;
	}
	
	public String getBillTo() {
		return billTo;
	}
	
	public void setBillTo(String billTo) {
		this.billTo = billTo;
	}
	
	public String getShipTo() {
		return shipTo;
	}
	
	public void setShipTo(String shipTo) {
		this.shipTo = shipTo;
	}

	public String getPoItem() {
		return poItem;
	}
	
	public void setPoItem(String poItem) {
		this.poItem = poItem;
	}
	
	public String getPoNumber() {
		return poNumber;
	}
	
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}


	@Override
	public String toString() {
		return "OffloadOperation [select=" + select
				+ ", offloadStatus=" + offloadStatus + ", customer="
				+ customer + ", orderNumber=" + orderNumber
				+ ", poNo=" + poNo + ", product="
				+ product + ", customerLotNo=" + customerLotNo
				+ ", waferQty=" + waferQty + ", orderPrice="
				+ orderPrice + ", currency=" + currency
				+ ", offloadType=" + offloadType + ", offloadTo="
				+ offloadTo + ", offloadPo;=" + offloadPo
				+ ", offloadDueDate=" + offloadDueDate + ", waferId="
				+ waferId + ", confirmDate=" + confirmDate
				+ ", updateDate=" + updateDate + ", updateUser="
				+ updateUser + ", cancelDate=" + cancelDate
				+ ", cancelUser=" + cancelUser + ", cancelReason="
				+ cancelReason 
				+ ", newFlag=" + newFlag
				+ ", cancelFlag=" + cancelFlag
				+ ", billTo=" + billTo
				+ ", shipTo=" + shipTo
				+ ", poItem=" + poItem
				+ ", poNumber=" + poNumber
				+ "]";
	}
    

}