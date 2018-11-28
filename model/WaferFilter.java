/*
 * Project Name:iVision
 * File Name:WaferFilter.java
 * Package Name:com.tce.ivision.modules.oe.model
 * Date:2012/12/21下午2:15:32
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.model;

/**
 * ClassName: WaferFilter <br/>
 * date: 2012/12/21 下午2:15:32 <br/>
 *
 * @author 060489-Jeff
 * @version 
 * @since JDK 1.6
 */
public class WaferFilter {
	//the search condition
	private String customerfilter;
	private String customerlotnofilter ;
	private String startdatefilter ;
	private String enddatefilter ;
	private boolean closeFlag;
	private String waferDataFilter;//IT-PR-141201
	private String materialTypeFilter;//OCF-PR-160303
	private boolean receiveBeforeShipFlag;//OCF-PR-160303 add

	public String getCustomerfilter() {
		return customerfilter;
	}
	public void setCustomerfilter(String customerfilter) {
		this.customerfilter = customerfilter;
	}
	public String getCustomerlotnofilter() {
		return customerlotnofilter;
	}
	public void setCustomerlotnofilter(String customerlotnofilter) {
		this.customerlotnofilter = customerlotnofilter;
	}
	public String getStartdatefilter() {
		return startdatefilter;
	}
	public void setStartdatefilter(String startdatefilter) {
		this.startdatefilter = startdatefilter;
	}
	public String getEnddatefilter() {
		return enddatefilter;
	}
	public void setEnddatefilter(String enddatefilter) {
		this.enddatefilter = enddatefilter;
	}
	
	public boolean getCloseFlag() {
		return closeFlag;
	}
	public void setCloseFlag(boolean closeFlag) {
		this.closeFlag = closeFlag;
	}
	/**
	 * waferDataFilter.
	 *
	 * @return  the waferDataFilter
	 * @since   JDK 1.6
	 */
	public String getWaferDataFilter() {
		return waferDataFilter;
	}
	/**
	 * waferDataFilter.
	 *
	 * @param   waferDataFilter    the waferDataFilter to set
	 * @since   JDK 1.6
	 */
	public void setWaferDataFilter(String waferDataFilter) {
		this.waferDataFilter = waferDataFilter;
	}
	
	public String getMaterialTypeFilter() {
		return materialTypeFilter;
	}
	
	public void setMaterialTypeFilter(String materialTypeFilter) {
		this.materialTypeFilter = materialTypeFilter;
	}
	
	public boolean getReceiveBeforeShipFlag() {
		return receiveBeforeShipFlag;
	}
	public void setReceiveBeforeShipFlag(boolean receiveBeforeShipFlag) {
		this.receiveBeforeShipFlag = receiveBeforeShipFlag;
	}
}
