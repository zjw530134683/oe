package com.tce.ivision.modules.oe.model;

import java.math.BigDecimal;
import java.util.Date;

import org.zkoss.util.resource.Labels;


public class OffloadShippingConfirm{
	
	private boolean select;
	private int offloadShippingIdx;
	private int offloadLotNoIdx;
	private int shippingIdx;
	private String packingListType;
	private String packingListNumber;
	private String iNaviLotNo;
	private int shipQty;
	private int rmaQty;
	private int scrapQty;
	private String shippingWaferData;
	private String rmaWaferData;
	private String scrapWaferData;
	private Date shipDate;
	private Date confirmDate;
	private String confirmUser;
	private String remark;
	private String billTo;
	private String shipTo;
	private String shipToAddress;
	private String customerLotNo;
	private String poItem;
	private String poNumber;
	private String product;
	private String billToPo;
	private BigDecimal exchangeRate;
	private boolean isConfirm;
	
	
    public OffloadShippingConfirm() {
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
	 * offloadShippingIdx.
	 *
	 * @return  the offloadShippingIdx
	 * @since   JDK 1.6
	 */
	public int getOffloadShippingIdx() {
		return offloadShippingIdx;
	}







	/**
	 * offloadShippingIdx.
	 *
	 * @param   offloadShippingIdx    the offloadShippingIdx to set
	 * @since   JDK 1.6
	 */
	public void setOffloadShippingIdx(int offloadShippingIdx) {
		this.offloadShippingIdx = offloadShippingIdx;
	}







	/**
	 * offloadLotNoIdx.
	 *
	 * @return  the offloadLotNoIdx
	 * @since   JDK 1.6
	 */
	public int getOffloadLotNoIdx() {
		return offloadLotNoIdx;
	}







	/**
	 * offloadLotNoIdx.
	 *
	 * @param   offloadLotNoIdx    the offloadLotNoIdx to set
	 * @since   JDK 1.6
	 */
	public void setOffloadLotNoIdx(int offloadLotNoIdx) {
		this.offloadLotNoIdx = offloadLotNoIdx;
	}







	/**
	 * shippingIdx.
	 *
	 * @return  the shippingIdx
	 * @since   JDK 1.6
	 */
	public int getShippingIdx() {
		return shippingIdx;
	}







	/**
	 * shippingIdx.
	 *
	 * @param   shippingIdx    the shippingIdx to set
	 * @since   JDK 1.6
	 */
	public void setShippingIdx(int shippingIdx) {
		this.shippingIdx = shippingIdx;
	}







	/**
	 * packingListType.
	 *
	 * @return  the packingListType
	 * @since   JDK 1.6
	 */
	public String getPackingListType() {
		return packingListType;
	}







	/**
	 * packingListType.
	 *
	 * @param   packingListType    the packingListType to set
	 * @since   JDK 1.6
	 */
	public void setPackingListType(String packingListType) {
		this.packingListType = packingListType;
	}







	/**
	 * packingListNumber.
	 *
	 * @return  the packingListNumber
	 * @since   JDK 1.6
	 */
	public String getPackingListNumber() {
		return packingListNumber;
	}







	/**
	 * packingListNumber.
	 *
	 * @param   packingListNumber    the packingListNumber to set
	 * @since   JDK 1.6
	 */
	public void setPackingListNumber(String packingListNumber) {
		this.packingListNumber = packingListNumber;
	}







	/**
	 * iNaviLotNo.
	 *
	 * @return  the iNaviLotNo
	 * @since   JDK 1.6
	 */
	public String getiNaviLotNo() {
		return iNaviLotNo;
	}







	/**
	 * iNaviLotNo.
	 *
	 * @param   iNaviLotNo    the iNaviLotNo to set
	 * @since   JDK 1.6
	 */
	public void setiNaviLotNo(String iNaviLotNo) {
		this.iNaviLotNo = iNaviLotNo;
	}







	/**
	 * shipQty.
	 *
	 * @return  the shipQty
	 * @since   JDK 1.6
	 */
	public int getShipQty() {
		return shipQty;
	}







	/**
	 * shipQty.
	 *
	 * @param   shipQty    the shipQty to set
	 * @since   JDK 1.6
	 */
	public void setShipQty(int shipQty) {
		this.shipQty = shipQty;
	}







	/**
	 * rmaQty.
	 *
	 * @return  the rmaQty
	 * @since   JDK 1.6
	 */
	public int getRmaQty() {
		return rmaQty;
	}







	/**
	 * rmaQty.
	 *
	 * @param   rmaQty    the rmaQty to set
	 * @since   JDK 1.6
	 */
	public void setRmaQty(int rmaQty) {
		this.rmaQty = rmaQty;
	}







	/**
	 * scrapQty.
	 *
	 * @return  the scrapQty
	 * @since   JDK 1.6
	 */
	public int getScrapQty() {
		return scrapQty;
	}







	/**
	 * scrapQty.
	 *
	 * @param   scrapQty    the scrapQty to set
	 * @since   JDK 1.6
	 */
	public void setScrapQty(int scrapQty) {
		this.scrapQty = scrapQty;
	}







	/**
	 * shippingWaferData.
	 *
	 * @return  the shippingWaferData
	 * @since   JDK 1.6
	 */
	public String getShippingWaferData() {
		return shippingWaferData;
	}







	/**
	 * shippingWaferData.
	 *
	 * @param   shippingWaferData    the shippingWaferData to set
	 * @since   JDK 1.6
	 */
	public void setShippingWaferData(String shippingWaferData) {
		this.shippingWaferData = shippingWaferData;
	}







	/**
	 * rmaWaferData.
	 *
	 * @return  the rmaWaferData
	 * @since   JDK 1.6
	 */
	public String getRmaWaferData() {
		return rmaWaferData;
	}







	/**
	 * rmaWaferData.
	 *
	 * @param   rmaWaferData    the rmaWaferData to set
	 * @since   JDK 1.6
	 */
	public void setRmaWaferData(String rmaWaferData) {
		this.rmaWaferData = rmaWaferData;
	}







	/**
	 * scrapWaferData.
	 *
	 * @return  the scrapWaferData
	 * @since   JDK 1.6
	 */
	public String getScrapWaferData() {
		return scrapWaferData;
	}







	/**
	 * scrapWaferData.
	 *
	 * @param   scrapWaferData    the scrapWaferData to set
	 * @since   JDK 1.6
	 */
	public void setScrapWaferData(String scrapWaferData) {
		this.scrapWaferData = scrapWaferData;
	}







	/**
	 * shipDate.
	 *
	 * @return  the shipDate
	 * @since   JDK 1.6
	 */
	public Date getShipDate() {
		return shipDate;
	}







	/**
	 * shipDate.
	 *
	 * @param   shipDate    the shipDate to set
	 * @since   JDK 1.6
	 */
	public void setShipDate(Date shipDate) {
		this.shipDate = shipDate;
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
	 * confirmUser.
	 *
	 * @return  the confirmUser
	 * @since   JDK 1.6
	 */
	public String getConfirmUser() {
		return confirmUser;
	}







	/**
	 * confirmUser.
	 *
	 * @param   confirmUser    the confirmUser to set
	 * @since   JDK 1.6
	 */
	public void setConfirmUser(String confirmUser) {
		this.confirmUser = confirmUser;
	}







	/**
	 * remark.
	 *
	 * @return  the remark
	 * @since   JDK 1.6
	 */
	public String getRemark() {
		return remark;
	}







	/**
	 * remark.
	 *
	 * @param   remark    the remark to set
	 * @since   JDK 1.6
	 */
	public void setRemark(String remark) {
		this.remark = remark;
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


	public String getShipToAddress() {
		return shipToAddress;
	}
	
	public void setShipToAddress(String shipToAddress) {
		this.shipToAddress = shipToAddress;
	}
	
	public String getCustomerLotNo() {
		return customerLotNo;
	}
	
	public void setCustomerLotNo(String customerLotNo) {
		this.customerLotNo = customerLotNo;
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
	
	public String getProduct() {
		return product;
	}
	
	public void setProduct(String product) {
		this.product = product;
	}

	public String getBillToPo() {
		return billToPo;
	}
	
	public void setBillToPo(String billToPo) {
		this.billToPo = billToPo;
	}
	
	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}
	
	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}
	
	public void setIsConfirm(boolean isConfirm) {
		this.isConfirm = isConfirm;
	}



	@Override
	public String toString() {
		return "OffloadShippingConfirm [select=" + select
				+ ", offloadShippingIdx=" + offloadShippingIdx + ", offloadLotNoIdx="
				+ offloadLotNoIdx + ", shippingIdx=" + shippingIdx
				+ ", packingListType=" + packingListType + ", packingListNumber="
				+ packingListNumber + ", iNaviLotNo=" + iNaviLotNo
				+ ", shipQty=" + shipQty + ", rmaQty="
				+ rmaQty + ", scrapQty=" + scrapQty
				+ ", shippingWaferData=" + shippingWaferData + ", rmaWaferData="
				+ rmaWaferData + ", scrapWaferData;=" + scrapWaferData
				+ ", shipDate=" + shipDate + ", confirmDate="
				+ confirmDate + ", confirmUser=" + confirmUser
				+ ", remark=" + remark
				+ ", billTo=" + billTo
				+ ", shipTo=" + shipTo
				+ ", shipToAddress=" + shipToAddress
				+ ", customerLotNo=" + customerLotNo
				+ ", poItem=" + poItem
				+ ", poNumber=" + poNumber
				+ ", product=" + product
				+ ", billToPo=" + billToPo
				+ ", exchangeRate=" + exchangeRate
				+ ", isConfirm=" + isConfirm
				+ "]";
	}
    

}