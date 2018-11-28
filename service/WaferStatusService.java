/*
 * Project Name:iVision
 * File Name:WaferStatusService.java
 * Package Name:com.tce.ivision.modules.oe.service
 * Date:2012/12/25下午1:24:53
 * 
 * 說明:
 * Wafer Status interface Service
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.service;

import java.util.List;

import com.tce.ivision.model.WaferInventoryStage;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.base.service.BaseService;

/**
 * ClassName: WaferStatusService <br/>
 * date: 2012/12/25 下午1:24:53 <br/>
 *
 * @author 060489-Jeff
 * @version 
 * @since JDK 1.6
 */
public interface WaferStatusService extends BaseService{
	public WaferStatus searchByCustomer(String inCustomerLotno, String inCustomerId, String inWaferStatFlag, int inWaferQty);	
	public WaferStatus getWaferStatusByCustomerIdandCustomerLotno(String inOperationUnit,String inCustomerId,String inCustomerLotno);
	public WaferStatus getWaferStatusByEntityId(String inEntityId);
	public WaferStatus searchByCustomerByCustomerJob(String inCustomerLotno, String inCustomerId, String inWaferStatFlag, String inCustomerJob);//IT-PR-141008_Allison
	public List<WaferInventoryStage> getWaferInventoryStagesByStatus(String[] aryStatus);//OCFPR170202
}