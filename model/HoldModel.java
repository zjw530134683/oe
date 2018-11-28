/*
 * Project Name:iVision
 * File Name:FutureHoldModel.java
 * Package Name:com.tce.ivision.modules.oe.model
 * Date:2013/1/15下午5:02:09
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.model;

import java.util.List;

import com.tce.ivision.model.Hold;
import com.tce.ivision.model.HoldWafer;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.model.WaferInfo;

/**
 * ClassName: FutureHoldModel <br/>
 * date: 2013/1/15 下午5:02:09 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class HoldModel {
	boolean selected;
	OrderLineLotno orderLineLotno;
	Hold hold;
	String holdTypeName;
	String holdProcessNameName;
	String holdReasonName;
	String holdComment;
	String lotno;
	String iNaviLotNo;
	boolean shippingFlag;
	boolean allWaferFlag;//IT-PR-141008_Allison add
	String customerJob;//IT-PR-141008_Allison add
	boolean waferDataFlag;//IT-PR-141008_Allison add
	String lockComment;//IT-PR-141008_Allison add
	String waferInfo;//IT-PR-141008_Allison add
	List<HoldWafer> holdWafers;//IT-PR-141008_Allison add
	HoldWafer holdWafer;//IT-PR-141008_Allison add
	String holdUser;//IT-PR-141008_Allison add
	String holdIssueDate;//IT-PR-141008_Allison add
	boolean holdExistFlag;//IT-PR-141008_Allison add
	int holdIdx;//IT-PR-141008_Allison add
	int holdWaferIdx;//IT-PR-141008_Allison add
	boolean releaseFlag;//IT-PR-141008_Allison add
	
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
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
	public String getHoldTypeName() {
		return holdTypeName;
	}
	public void setHoldTypeName(String holdTypeName) {
		this.holdTypeName = holdTypeName;
	}
	public String getHoldProcessNameName() {
		return holdProcessNameName;
	}
	public void setHoldProcessNameName(String holdProcessNameName) {
		this.holdProcessNameName = holdProcessNameName;
	}
	public String getHoldReasonName() {
		return holdReasonName;
	}
	public void setHoldReasonName(String holdReasonName) {
		this.holdReasonName = holdReasonName;
	}
	public String getHoldComment() {
		return holdComment;
	}
	public void setHoldComment(String holdComment) {
		this.holdComment = holdComment;
	}
	public String getLotno() {
		return lotno;
	}
	public void setLotno(String lotno) {
		this.lotno = lotno;
	}
	public boolean isShippingFlag() {
		return shippingFlag;
	}
	public void setShippingFlag(boolean shippingFlag) {
		this.shippingFlag = shippingFlag;
	}
	
	public boolean isAllWaferFlag() {
		return allWaferFlag;
	}
	public void setAllWaferFlag(boolean allWaferFlag) {
		this.allWaferFlag = allWaferFlag;
	}
	
	public boolean isWaferDataFlag() {
		return waferDataFlag;
	}
	public void setWaferDataFlag(boolean waferDataFlag) {
		this.waferDataFlag = waferDataFlag;
	}
	
	public String getCustomerJob() {
		return customerJob;
	}
	public void setCustomerJob(String customerJob) {
		this.customerJob = customerJob;
	}
	
	public String getLockComment() {
		return lockComment;
	}
	public void setLockComment(String lockComment) {
		this.lockComment = lockComment;
	}
	public String getWaferInfo() {
		return waferInfo;
	}
	public void setWaferInfo(String waferInfo) {
		this.waferInfo = waferInfo;
	}
	
	public List<HoldWafer> getHoldWafers() {
		return holdWafers;
	}
	public void setHoldWafers(List<HoldWafer> holdWafers) {
		this.holdWafers = holdWafers;
	}
	
	public String getiNaviLotNo() {
		return iNaviLotNo;
	}
	public void setiNaviLotNo(String iNaviLotNo) {
		this.iNaviLotNo = iNaviLotNo;
	}
	
	public HoldWafer getHoldWafer() {
		return holdWafer;
	}
	public void setHoldWafer(HoldWafer holdWafer) {
		this.holdWafer = holdWafer;
	}
	
	public String getHoldUser() {
		return holdUser;
	}
	public void setHoldUser(String holdUser) {
		this.holdUser = holdUser;
	}
	
	public String getHoldIssueDate() {
		return holdIssueDate;
	}
	public void setHoldIssueDate(String holdIssueDate) {
		this.holdIssueDate = holdIssueDate;
	}
	
	public boolean isHoldExistFlag() {
		return holdExistFlag;
	}
	public void setHoldExistFlag(boolean holdExistFlag) {
		this.holdExistFlag = holdExistFlag;
	}
	
	public int getHoldIdx() {
		return holdIdx;
	}
	public void setHoldIdx(int holdIdx) {
		this.holdIdx = holdIdx;
	}
	
	public int getHoldWaferIdx() {
		return holdWaferIdx;
	}
	public void setHoldWaferIdx(int holdWaferIdx) {
		this.holdWaferIdx = holdWaferIdx;
	}
	
	public boolean isReleaseFlag() {
		return releaseFlag;
	}
	public void setReleaseFlag(boolean releaseFlag) {
		this.releaseFlag = releaseFlag;
	}
}
