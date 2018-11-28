/*
 * Project Name:iVision
 * File Name:WaferStatusServiceImpl.java
 * Package Name:com.tce.ivision.modules.oe.service.impl
 * Date:2012/12/25下午1:30:20
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.model.WaferInventoryStage;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.base.service.impl.BaseServiceImpl;
import com.tce.ivision.modules.oe.service.WaferStatusService;

/**
 * ClassName: WaferStatusServiceImpl <br/>
 * date: 2012/12/25 下午1:30:20 <br/>
 *
 * @author 060489-Jeff
 * @version 
 * @since JDK 1.6
 */
public class WaferStatusServiceImpl extends BaseServiceImpl implements WaferStatusService{

	/**
	 * 使用CustomerLotno尋找DATA.
	 * @see com.tce.ivision.modules.oe.service.WaferStatusService#searchByCustomerLotno(java.lang.String)
	 */
	@Override
	public WaferStatus searchByCustomer(String inCustomerLotno, String inCustomerId, String inWaferStatFlag, int inWaferQty) {
		List<WaferStatus> datas = new ArrayList<WaferStatus>();		
		try {
			Criteria criteria = this.getWaferStatusDao().createCriteria(WaferStatus.class);
			criteria.add(Restrictions.eq("customerLotno",inCustomerLotno));
			criteria.add(Restrictions.eq("customerId",inCustomerId));
			criteria.add(Restrictions.not(Restrictions.eq("stateFlag",inWaferStatFlag)));
			criteria.add(Restrictions.eq("waferFlag",false));
			criteria.add(Restrictions.eq("waferQty",inWaferQty));//OCF-PR-150307
			datas = criteria.list();		 	
		} catch (Exception e) {
			 StringWriter stringWriter = new StringWriter();
             e.printStackTrace(new PrintWriter(stringWriter));
             log.error(stringWriter.toString());
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}else{
			return null;
		}
			
	}

	/**
	 * 依據CustomerID+CustomerLotno尋找WAFER_STATUS.
	 * @see com.tce.ivision.modules.oe.service.WaferStatusService#getWaferStatusByCustomerIdandCustomerLotno(java.lang.String, java.lang.String)
	 */
	@Override
	public WaferStatus getWaferStatusByCustomerIdandCustomerLotno(String inOperationunit,
			String inCustomerId, String inCustomerLotno) {
		List<WaferStatus> datas=new ArrayList<WaferStatus>();
		try {
			datas=this.getWaferStatusDao().createQuery("select c from WaferStatus c where customerId=:customer_id and customerLotno=:lot_no and operationUnit=:operation_unit ")
					.setParameter("customer_id", inCustomerId)
					.setParameter("lot_no", inCustomerLotno)
					.setParameter("operation_unit", inOperationunit)
					.list();
		} catch (Exception e) {
			 StringWriter stringWriter = new StringWriter();
             e.printStackTrace(new PrintWriter(stringWriter));
             log.error(stringWriter.toString());			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}else{
			return null;
		}
		
	}

	/**
	 * 依據EntityID尋找WAFER_STATUS.
	 * @see com.tce.ivision.modules.oe.service.WaferStatusService#getWaferStatusByEntityId(java.lang.String)
	 */
	@Override
	public WaferStatus getWaferStatusByEntityId(String inEntityId) {
		List<WaferStatus> datas=new ArrayList<WaferStatus>();
		try {
			datas=this.getWaferStatusDao().createQuery("select c from WaferStatus c where entityId=:entity_id ")
					.setParameter("entity_id", inEntityId)
					.list();
		} catch (Exception e) {
			 StringWriter stringWriter = new StringWriter();
             e.printStackTrace(new PrintWriter(stringWriter));
             log.error(stringWriter.toString());			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}else{
			return null;
		}
	}
	public WaferStatus searchByCustomerByCustomerJob(String inCustomerLotno, String inCustomerId, String inWaferStatFlag, String inCustomerJob) {
		List<WaferStatus> datas = new ArrayList<WaferStatus>();		
		try {
			Criteria criteria = this.getWaferStatusDao().createCriteria(WaferStatus.class);
			criteria.add(Restrictions.eq("customerLotno",inCustomerLotno));
			criteria.add(Restrictions.eq("customerId",inCustomerId));
			criteria.add(Restrictions.eq("customerJob",inCustomerJob));
			criteria.add(Restrictions.not(Restrictions.eq("stateFlag",inWaferStatFlag)));
			
			datas = criteria.list();		 	
		} catch (Exception e) {
			 StringWriter stringWriter = new StringWriter();
             e.printStackTrace(new PrintWriter(stringWriter));
             log.error(stringWriter.toString());
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}else{
			return null;
		}
			
	}
	public List<WaferInventoryStage> getWaferInventoryStagesByStatus(String[] aryStatus){
		List<WaferInventoryStage> datas = new ArrayList<WaferInventoryStage>();	
		Criteria criteria = this.getWaferInventoryStageDao().createCriteria(WaferInventoryStage.class);
		criteria.add(Restrictions.in("status", aryStatus));
		criteria.add(Restrictions.eq("cancelFlag", false));
		datas = criteria.list();	
		return datas;
	}

}
