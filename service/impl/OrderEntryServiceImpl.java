/*
 * Project Name:iVision
 * File Name:OrderEntryServiceImpl.java
 * Package Name:com.tce.ivision.modules.oe.service.impl
 * Date:2012/12/19上午9:02:19
 * 
 * 說明:
 *有關Order Entry相關table的Service Implement
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Persistence;

import org.apache.bcel.generic.IXOR;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.zkoss.zk.ui.Sessions;

import com.tce.ivision.model.B2bInfo;
import com.tce.ivision.model.Encode;
import com.tce.ivision.model.Hold;
import com.tce.ivision.model.HoldWafer;
import com.tce.ivision.model.LotInfo;
import com.tce.ivision.model.LotResult;
import com.tce.ivision.model.OeReworkCountSetup;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.OrderInternalCheckInfo;
import com.tce.ivision.model.OrderLine;
import com.tce.ivision.model.OrderLineInt;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.model.ProductInfo;
import com.tce.ivision.model.ProductNameSetup;
import com.tce.ivision.model.TsesOvtRmaLot;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.model.WaferBankinWafer;
import com.tce.ivision.model.WaferInfo;
import com.tce.ivision.model.WaferInspArea;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.base.service.impl.BaseServiceImpl;
import com.tce.ivision.modules.map.model.WaferInspResultLogsImport;
import com.tce.ivision.modules.oe.model.OeOrderNoConfirmModel;
import com.tce.ivision.modules.oe.model.OrderEntryLotnoModel;
import com.tce.ivision.modules.oe.service.OrderEntryService;
import com.tce.ivision.units.common.LogType;
import com.tce.ivision.model.ProductNameSetup;

/**
 * ClassName: OrderEntryServiceImpl <br/>
 * date: 2012/12/19 上午9:02:19 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderEntryServiceImpl extends BaseServiceImpl implements OrderEntryService{
	/**
	 * Logger
	 */
	public static Logger log = Logger.getLogger(OrderEntryServiceImpl.class);
	
	/**
	 * insert into OrderHeader
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#createOrderHeader(com.tce.ivision.model.OrderHeader)
	 */
	@Override
	public OrderHeader createOrderHeader(OrderHeader inOrderHeader) {
		Criteria criteria = this.getOrderHeaderDao().createCriteria(OrderHeader.class).setProjection(Projections.max("OrderHeaderIdx"));
		Integer maxIdx = (Integer)criteria.uniqueResult();
		
		inOrderHeader.setOrderHeaderIdx(0);
		inOrderHeader.setOrderNumber(createOrderNumber(maxIdx + 1,inOrderHeader.getOrderDate()));
		inOrderHeader.setPoNumber(inOrderHeader.getOrderNumber());//IT-PR-141201
		this.getOrderHeaderDao().create(inOrderHeader);
		//this.updateOrderHeader(inOrderHeader);
		
		return inOrderHeader;
	}

	/**
	 * update OrderHeader
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderHeader(com.tce.ivision.model.OrderHeader)
	 */
	@Override
	public void updateOrderHeader(OrderHeader inOrerHeader) {
		this.getOrderHeaderDao().update(inOrerHeader);
	}

	/**
	 * 依據idx,date，編出OrderNumber，並回傳之
	 * @param inIdx
	 * @param inDate
	 * @return orderNumber
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#createOrderNumber(int, java.util.Date)
	 */
	@Override
	public String createOrderNumber(int inIdx, Date inDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(inDate);
		String YY = String.format("%04d", cal.get(Calendar.YEAR));
		YY = YY.substring(YY.length()-2);
		String MM = String.format("%02d", cal.get(Calendar.MONTH)+1);
		String DD = String.format("%02d", cal.get(Calendar.DATE));
		String ID = String.format("%03d", inIdx % 1000);//IT-PR-141201
		log.debug(YY+MM+DD+ID);
		return YY+MM+DD+ID;
	}

	/**
	 * 依據inCustomerId找出ORDER_HEADER
	 * @param inCustomerId=ORDER_HEADER.CUSTOMER_ID
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getDistinctProduct(int)
	 */
	@Override
	public List<String> getDistinctProduct(String inCustomerId) {
		List<Object> tmps=new ArrayList<Object>();
		List<String> datas=new ArrayList<String>();
		try {
			String sql="SELECT DISTINCT PRODUCT FROM ORDER_HEADER WHERE CANCEL_FLAG=0 AND CUSTOMER_ID=:CUSTOMER_ID ";
			tmps=this.getDao().createSQLQuery(sql).setParameter("CUSTOMER_ID", inCustomerId).list();
		} catch (Exception e) {
			
		}
		
		if (tmps.size()>0){
			for(Object obj:tmps){
				String data="";
				if (obj!=null){
					data=obj.toString();
				}
				datas.add(data);
			}
		}
		
		return datas;
	}

	/**
	 * 依據CustomerId,Product,PoNumber,OrderDate,inOrderStatus 找出符合條件的ORDER_HEADER
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderHeadersByHql(java.lang.String)
	 */
	@Override
	public List<OrderHeader> getOrderHeadersByHql(String inOperationUnit, String inCustomerId,String inProduct,String inPoNumber,String inOrderDateS,String inOrderDateE,String inOrderStatus) {	
		List<OrderHeader> datas=new ArrayList<OrderHeader>();
		String hql="select c from OrderHeader c where cancelFlag=0 and operationUnit='"+inOperationUnit+"' ";
		if ("".equals(inCustomerId)){
			
		}
		else{
			hql=hql+" and customerId='"+inCustomerId+"' ";
		}
		
	    if ("".equals(inProduct)){
			
		}
		else {
			hql=hql+" and product='"+inProduct+"' ";
		}
		
		if ("".equals(inPoNumber)){
			
		}
		else{
			hql=hql+" and poNumber='"+inPoNumber+"' ";
		}
		
		if (("".equals(inOrderDateS))||("".equals(inOrderDateE))){
			
		}
		else{
			hql=hql+" and orderDate>='"+inOrderDateS+"' and orderDate<='"+inOrderDateE+"' ";
		}
		
		if ("30".equals(inOrderStatus)){
			hql=hql+" and orderStatus<>'30' ";
		}
		
		try {
			datas=this.getOrderHeaderDao().createQuery(hql).list();
		} catch (Exception e) {
			
		}
		return datas;
	}

	/**
	 * 用inOrderNumber找出ORDER_LINE_LOTNO
	 * @param inOrderNumber = ORDER_LINE_LOTNO.ORDER_NUMBER
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderLineLotnosByOrderNumber(java.lang.String)
	 */
	@Override
	public List<OrderLineLotno> getOrderLineLotnosByOrderNumber(
			String inOrderNumber) {
		List<OrderLineLotno> datas=new ArrayList<OrderLineLotno>();
		try {
			String hql="select c from OrderLineLotno c where cancelFlag=0 and orderNumber=:order_number order by poItem,customerLotno ";
			datas=this.getOrderLineLotnoDao().createQuery(hql)
					.setParameter("order_number", inOrderNumber)
					.list();
		} catch (Exception e) {
			
		}
		return datas;
	}

	/**
	 * 將相同的OrderNumber的OrderHeader,OrderLine,OrderLineLotno，將cancelFlag設為true
	 * @param inOrderHeader = 要刪除的OrderHeader
	 * @param inOrderLines = 要刪除的OrderLine
	 * @param inOrderLineLotnos = 要刪除的OrderLineLotno
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#deleteOrderData(com.tce.ivision.model.OrderHeader, java.util.List, java.util.List)
	 */
	@Override
	public void deleteOrderData(OrderHeader inOrderHeader,
			List<OrderLine> inOrderLines, List<OrderLineLotno> inOrderLineLotnos,
			List<WaferStatus> inWaferStatus,//2013.02.18
			String inUserId,Date inNowTime, String inClassName, List<LotInfo> inLotInfos, List<LotResult> inLotResults) {
		String actionName="Delete";
		
		//Header
		inOrderHeader.setCancelFlag(true);
		inOrderHeader.setUpdateDate(inNowTime);//2013.02.19 for INF004
		inOrderHeader.setUpdateUser(inUserId);//2013.02.19 for INF004
		this.getOrderHeaderDao().update(inOrderHeader);
		this.createLogging(inClassName, "OrderHeader", actionName, inOrderHeader.toString(), inUserId, inNowTime);
		
		//Lines
		this.deleteOrderLines(inOrderLines,inUserId,inNowTime,inClassName,actionName);
		
		//Lotno
		this.deleteOrderLineLotnos(inOrderLineLotnos,inUserId,inNowTime,inClassName,actionName);
		
		//2013.02.18 WaferStatus
		if (inWaferStatus.size()>0){
			for (int i=0;i<inWaferStatus.size();i++){
				inWaferStatus.get(i).setStateFlag("2");
				inWaferStatus.get(i).setUpdateUser(inUserId);
				inWaferStatus.get(i).setUpdateDate(inNowTime);
				//inWaferStatus.get(i).setEntityId("");
				this.updateWaferStatus(inWaferStatus.get(i),inUserId,inNowTime,inClassName,actionName);//2013.04.01
				
			}
		}
		
		//IT-PR-141008_LotInfo_Allison add
		if(inLotInfos.size()>0){
			for(int i=0; i<inLotInfos.size(); i++){
				inLotInfos.get(i).setDeleteFlag(true);
				inLotInfos.get(i).setUpdateUser(inUserId);
				inLotInfos.get(i).setUpdateDate(inNowTime);
				this.updateLotInfos(inLotInfos.get(i),inUserId,inNowTime,inClassName,actionName);
			}
		}
		
		//IT-PR-141008_LotResult_Allison add
		if(inLotResults.size()>0){
			for(int i=0; i<inLotResults.size(); i++){
				inLotResults.get(i).setDeleteFlag(true);
				inLotResults.get(i).setUpdateUser(inUserId);
				inLotResults.get(i).setUpdateDate(inNowTime);
				this.updateLotResults(inLotResults.get(i),inUserId,inNowTime,inClassName,actionName);
			}
		}
	}

	/**
	 * UPDATE ORDER_LINE CANCEL_FLAG=1
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderLines(java.util.List)
	 */
	@Override
	public void deleteOrderLines(List<OrderLine> inOrderLines, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		for (int i=0;i<inOrderLines.size();i++){
			OrderLine delOrderLine = inOrderLines.get(i);
			delOrderLine.setCancelFlag(true);
			delOrderLine.setUpdateDate(inNowTime);//2013.02.19 for INF004
			delOrderLine.setUpdateUser(inUserId);//2013.02.19 for INF004
			this.getOrderLineDao().update(delOrderLine);
			this.createLogging(inClassName, "OrderLine", inActionName, delOrderLine.toString(), inUserId, inNowTime);
		}
	}
	/**
	 * UPDATE ORDER_LINE_INT
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderLineInts(java.util.List)
	 */
	@Override
	public void updateOrderLineInts(List<OrderLineInt> inOrderLineInts, String inUserId) {
		boolean updateFlag=false;
		Date nowtime=new Date();
		for (int i=0;i<inOrderLineInts.size();i++){
			List<OrderLineInt> orderLineIntLists = (List<OrderLineInt>)this.getOrderLineIntDao()
					.createQuery("SELECT l FROM OrderLineInt l WHERE customerLotNo=:customerLotNo AND customerJob=:customerJob AND waferData=:waferData ")
					.setParameter("customerLotNo", inOrderLineInts.get(i).getCustomerLotNo())
					.setParameter("customerJob", inOrderLineInts.get(i).getCustomerJob())
					.setParameter("waferData", inOrderLineInts.get(i).getWaferData())
					.list();
			if(orderLineIntLists.size() > 0){
				for(int j=0; j<orderLineIntLists.size(); j++){
					//1. 先將原本[ORDER_LINE_INT].GET_FLAG=1
					OrderLineInt updateOrderLineInt = orderLineIntLists.get(j);
					updateOrderLineInt.setGetFlag(true);
//					updateOrderLineInt.setCustomerId(inOrderLineInts.get(i).getCustomerId());
//					updateOrderLineInt.setCustomerPo(inOrderLineInts.get(i).getCustomerPo());
//					updateOrderLineInt.setPoItem(inOrderLineInts.get(i).getPoItem());
//					updateOrderLineInt.setSourceMtrlNum(inOrderLineInts.get(i).getSourceMtrlNum());
//					updateOrderLineInt.setMtrlNum(inOrderLineInts.get(i).getMtrlNum());
//					updateOrderLineInt.setMtrlDesc(inOrderLineInts.get(i).getMtrlDesc());
//					updateOrderLineInt.setWaferQty(inOrderLineInts.get(i).getWaferQty());
//					updateOrderLineInt.setDelivDate(inOrderLineInts.get(i).getDelivDate());
//					updateOrderLineInt.setDesignId(inOrderLineInts.get(i).getDesignId());
//					updateOrderLineInt.setCountryOfFab(inOrderLineInts.get(i).getCountryOfFab());
//					updateOrderLineInt.setShipToVendorCode(inOrderLineInts.get(i).getShipToVendorCode());
//					updateOrderLineInt.setShipToVendorName(inOrderLineInts.get(i).getShipToVendorName());
//					updateOrderLineInt.setShipComment(inOrderLineInts.get(i).getShipComment());
//					updateOrderLineInt.setCompCode(inOrderLineInts.get(i).getCompCode());
//					updateOrderLineInt.setImportDate(inOrderLineInts.get(i).getImportDate());
//					updateOrderLineInt.setFileName(inOrderLineInts.get(i).getFileName());
//					updateOrderLineInt.setSubName(inOrderLineInts.get(i).getSubName());
//					updateOrderLineInt.setStage(inOrderLineInts.get(i).getStage());
//					updateOrderLineInt.setCustomerLotNo(inOrderLineInts.get(i).getCustomerLotNo());
//					updateOrderLineInt.setCustomerJob(inOrderLineInts.get(i).getCustomerJob());
//					updateOrderLineInt.setPriority(inOrderLineInts.get(i).getPriority());
//					updateOrderLineInt.setLotType(inOrderLineInts.get(i).getLotType());
//					updateOrderLineInt.setWaferData(inOrderLineInts.get(i).getWaferData());
//					updateOrderLineInt.setOperationDescription(inOrderLineInts.get(i).getOperationDescription());
//					updateOrderLineInt.setWiRmaNo(inOrderLineInts.get(i).getWiRmaNo());

					this.getOrderLineIntDao().update(updateOrderLineInt);
					this.createLogging(this.getClass().getName(), "OrderLineInt", LogType.MODIFY, updateOrderLineInt.toString(), inUserId, nowtime);
				}
				
				//2. 再新增資料
				OrderLineInt insertOrderLineInt = new OrderLineInt();
				insertOrderLineInt.setCustomerId(inOrderLineInts.get(i).getCustomerId());
				insertOrderLineInt.setCustomerPo(inOrderLineInts.get(i).getCustomerPo());
				insertOrderLineInt.setPoItem(inOrderLineInts.get(i).getPoItem());
				insertOrderLineInt.setSourceMtrlNum(inOrderLineInts.get(i).getSourceMtrlNum());
				insertOrderLineInt.setMtrlNum(inOrderLineInts.get(i).getMtrlNum());
				insertOrderLineInt.setMtrlDesc(inOrderLineInts.get(i).getMtrlDesc());
				insertOrderLineInt.setWaferQty(inOrderLineInts.get(i).getWaferQty());
				insertOrderLineInt.setDelivDate(inOrderLineInts.get(i).getDelivDate());
				insertOrderLineInt.setDesignId(inOrderLineInts.get(i).getDesignId());
				insertOrderLineInt.setCountryOfFab(inOrderLineInts.get(i).getCountryOfFab());
				insertOrderLineInt.setShipToVendorCode(inOrderLineInts.get(i).getShipToVendorCode());
				insertOrderLineInt.setShipToVendorName(inOrderLineInts.get(i).getShipToVendorName());
				insertOrderLineInt.setShipComment(inOrderLineInts.get(i).getShipComment());
				insertOrderLineInt.setCompCode(inOrderLineInts.get(i).getCompCode());
				insertOrderLineInt.setImportDate(inOrderLineInts.get(i).getImportDate());
				insertOrderLineInt.setFileName(inOrderLineInts.get(i).getFileName());
				insertOrderLineInt.setSubName(inOrderLineInts.get(i).getSubName());
				insertOrderLineInt.setStage(inOrderLineInts.get(i).getStage());
				insertOrderLineInt.setCustomerLotNo(inOrderLineInts.get(i).getCustomerLotNo());
				insertOrderLineInt.setCustomerJob(inOrderLineInts.get(i).getCustomerJob());
				insertOrderLineInt.setPriority(inOrderLineInts.get(i).getPriority());
				insertOrderLineInt.setLotType(inOrderLineInts.get(i).getLotType());
				insertOrderLineInt.setWaferData(inOrderLineInts.get(i).getWaferData());
				insertOrderLineInt.setOperationDescription(inOrderLineInts.get(i).getOperationDescription());
				insertOrderLineInt.setWiRmaNo(inOrderLineInts.get(i).getWiRmaNo());
				insertOrderLineInt.setWaferDie(inOrderLineInts.get(i).getWaferDie());
				insertOrderLineInt.setGradeRecord(inOrderLineInts.get(i).getGradeRecord());
				insertOrderLineInt.setTestProgram(inOrderLineInts.get(i).getTestProgram());
				insertOrderLineInt.setEngNo(inOrderLineInts.get(i).getEngNo());

				this.getOrderLineIntDao().create(insertOrderLineInt);
				this.createLogging(this.getClass().getName(), "OrderLineInt", LogType.ADD, inOrderLineInts.toString(), inUserId, nowtime);
			}else{
				OrderLineInt insertOrderLineInt = new OrderLineInt();
				insertOrderLineInt.setCustomerId(inOrderLineInts.get(i).getCustomerId());
				insertOrderLineInt.setCustomerPo(inOrderLineInts.get(i).getCustomerPo());
				insertOrderLineInt.setPoItem(inOrderLineInts.get(i).getPoItem());
				insertOrderLineInt.setSourceMtrlNum(inOrderLineInts.get(i).getSourceMtrlNum());
				insertOrderLineInt.setMtrlNum(inOrderLineInts.get(i).getMtrlNum());
				insertOrderLineInt.setMtrlDesc(inOrderLineInts.get(i).getMtrlDesc());
				insertOrderLineInt.setWaferQty(inOrderLineInts.get(i).getWaferQty());
				insertOrderLineInt.setDelivDate(inOrderLineInts.get(i).getDelivDate());
				insertOrderLineInt.setDesignId(inOrderLineInts.get(i).getDesignId());
				insertOrderLineInt.setCountryOfFab(inOrderLineInts.get(i).getCountryOfFab());
				insertOrderLineInt.setShipToVendorCode(inOrderLineInts.get(i).getShipToVendorCode());
				insertOrderLineInt.setShipToVendorName(inOrderLineInts.get(i).getShipToVendorName());
				insertOrderLineInt.setShipComment(inOrderLineInts.get(i).getShipComment());
				insertOrderLineInt.setCompCode(inOrderLineInts.get(i).getCompCode());
				insertOrderLineInt.setImportDate(inOrderLineInts.get(i).getImportDate());
				insertOrderLineInt.setFileName(inOrderLineInts.get(i).getFileName());
				insertOrderLineInt.setSubName(inOrderLineInts.get(i).getSubName());
				insertOrderLineInt.setStage(inOrderLineInts.get(i).getStage());
				insertOrderLineInt.setCustomerLotNo(inOrderLineInts.get(i).getCustomerLotNo());
				insertOrderLineInt.setCustomerJob(inOrderLineInts.get(i).getCustomerJob());
				insertOrderLineInt.setPriority(inOrderLineInts.get(i).getPriority());
				insertOrderLineInt.setLotType(inOrderLineInts.get(i).getLotType());
				insertOrderLineInt.setWaferData(inOrderLineInts.get(i).getWaferData());
				insertOrderLineInt.setOperationDescription(inOrderLineInts.get(i).getOperationDescription());
				insertOrderLineInt.setWiRmaNo(inOrderLineInts.get(i).getWiRmaNo());
				insertOrderLineInt.setWaferDie(inOrderLineInts.get(i).getWaferDie());
				insertOrderLineInt.setGradeRecord(inOrderLineInts.get(i).getGradeRecord());
				insertOrderLineInt.setTestProgram(inOrderLineInts.get(i).getTestProgram());
				insertOrderLineInt.setEngNo(inOrderLineInts.get(i).getEngNo());

				this.getOrderLineIntDao().create(insertOrderLineInt);
				this.createLogging(this.getClass().getName(), "OrderLineInt", LogType.ADD, insertOrderLineInt.toString(), inUserId, nowtime);
			}
		}
	}
	
	/**
	 * UPDATE ORDER_LINE_INT
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderLineInts(java.util.List)
	 */
	@Override
	public void updateWaferBankinInts(List<WaferBankinInt> inWaferBankinInts, String inUserId) {
		Date nowtime=new Date();
		for (int i=0;i<inWaferBankinInts.size();i++){
			List<WaferBankinInt> waferBankinIntLists = (List<WaferBankinInt>)this.getWaferBankinIntDao()
					.createQuery("SELECT l FROM WaferBankinInt l WHERE customerLotno=:customerLotno AND customerJob=:customerJob AND waferData=:waferData ")
					.setParameter("customerLotno", inWaferBankinInts.get(i).getCustomerLotno())
					.setParameter("customerJob", inWaferBankinInts.get(i).getCustomerJob())
					.setParameter("waferData", inWaferBankinInts.get(i).getWaferData())
					.list();
			if(waferBankinIntLists.size() > 0){
				for(int j=0; j<waferBankinIntLists.size(); j++){
					//1. 先update [WAFER_BANKIN_INT].GET_FLAG=1
					WaferBankinInt updateWaferBankinInt = waferBankinIntLists.get(j);
					updateWaferBankinInt.setGetFlag(true);
//					updateWaferBankinInt.setCurrentWaferQty(inWaferBankinInts.get(i).getCurrentWaferQty());
//					updateWaferBankinInt.setCustomerId(inWaferBankinInts.get(i).getCustomerId());
//					updateWaferBankinInt.setCustomerLotno(inWaferBankinInts.get(i).getCustomerLotno());
//					updateWaferBankinInt.setDesignId(inWaferBankinInts.get(i).getDesignId());
//					updateWaferBankinInt.setWaferDie(inWaferBankinInts.get(i).getDieQty());
//					updateWaferBankinInt.setFileName(inWaferBankinInts.get(i).getFileName());
//					updateWaferBankinInt.setMtrlNum(inWaferBankinInts.get(i).getMtrlNum());
//					updateWaferBankinInt.setMtrlDesc(inWaferBankinInts.get(i).getMtrlDesc());
//					updateWaferBankinInt.setShipComment(inWaferBankinInts.get(i).getShipComment());
//					updateWaferBankinInt.setWaferData(inWaferBankinInts.get(i).getWaferData());
//					updateWaferBankinInt.setCustomerJob(inWaferBankinInts.get(i).getCustomerJob());
//					updateWaferBankinInt.setCustomerPo(inWaferBankinInts.get(i).getCustomerPo());
//					updateWaferBankinInt.setPoItem(inWaferBankinInts.get(i).getPoItem());
//					updateWaferBankinInt.setGradeRecord(inWaferBankinInts.get(i).getGradeRecord());
//					updateWaferBankinInt.setSourceMtrlNum(inWaferBankinInts.get(i).getSourceMtrlNum());
//					updateWaferBankinInt.setEngNo(inWaferBankinInts.get(i).getEngNo());
//					updateWaferBankinInt.setTestProgram(inWaferBankinInts.get(i).getTestProgram());

					this.getWaferBankinIntDao().update(updateWaferBankinInt);
					this.createLogging(this.getClass().getName(), "WaferBankinInt", LogType.MODIFY, updateWaferBankinInt.toString(), inUserId, nowtime);
				}
			}
			
			//2. 再新增新的WAFER_BANKIN_INT資料
			WaferBankinInt insertWaferBankinInt = new WaferBankinInt();
			insertWaferBankinInt.setCurrentWaferQty(inWaferBankinInts.get(i).getCurrentWaferQty());
			insertWaferBankinInt.setCustomerId(inWaferBankinInts.get(i).getCustomerId());
			insertWaferBankinInt.setCustomerLotno(inWaferBankinInts.get(i).getCustomerLotno());
			insertWaferBankinInt.setDesignId(inWaferBankinInts.get(i).getDesignId());
			insertWaferBankinInt.setWaferDie(inWaferBankinInts.get(i).getDieQty());
			insertWaferBankinInt.setFileName(inWaferBankinInts.get(i).getFileName());
			insertWaferBankinInt.setMtrlNum(inWaferBankinInts.get(i).getMtrlNum());
			insertWaferBankinInt.setMtrlDesc(inWaferBankinInts.get(i).getMtrlDesc());
			insertWaferBankinInt.setShipComment(inWaferBankinInts.get(i).getShipComment());
			insertWaferBankinInt.setWaferData(inWaferBankinInts.get(i).getWaferData());
			insertWaferBankinInt.setCustomerJob(inWaferBankinInts.get(i).getCustomerJob());
			insertWaferBankinInt.setCustomerPo(inWaferBankinInts.get(i).getCustomerPo());
			insertWaferBankinInt.setPoItem(inWaferBankinInts.get(i).getPoItem());
			insertWaferBankinInt.setGradeRecord(inWaferBankinInts.get(i).getGradeRecord());
			insertWaferBankinInt.setSourceMtrlNum(inWaferBankinInts.get(i).getSourceMtrlNum());
			insertWaferBankinInt.setEngNo(inWaferBankinInts.get(i).getEngNo());
			insertWaferBankinInt.setTestProgram(inWaferBankinInts.get(i).getTestProgram());
			insertWaferBankinInt.setEsod(inWaferBankinInts.get(i).getEsod());
			insertWaferBankinInt.setWaferDie(inWaferBankinInts.get(i).getWaferDie());

			this.getWaferBankinIntDao().create(insertWaferBankinInt);
			this.createLogging(this.getClass().getName(), "WaferBankinInt", LogType.ADD, insertWaferBankinInt.toString(), inUserId, nowtime);
		}
	}
	
	/**
	 * DELETE ORDER_LINE
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderLines(java.util.List)
	 */
	@Override
	public void dropOrderLines(List<OrderLine> inOrderLines, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		for (int i=0;i<inOrderLines.size();i++){
			this.createLogging(inClassName, "OrderLine", inActionName, inOrderLines.get(i).toString(), inUserId, inNowTime);
			this.getOrderLineDao().delete(inOrderLines.get(i));
		}
	}
	
	/**
	 * UPDATE ORDER_LINE
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderLines(java.util.List)
	 */
	@Override
	public void updateOrderLines(List<OrderLine> inOrderLines, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		for (int i=0;i<inOrderLines.size();i++){
			this.getOrderLineDao().update(inOrderLines.get(i));
			this.createLogging(inClassName, "OrderLine", inActionName, inOrderLines.get(i).toString(), inUserId, inNowTime);
		}
	}
	
	/**
	 * INSERT INTO ORDER_LIN
	 */
	@Override
	public void createOrderLines(List<OrderLine> inOrderLines, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		if (inOrderLines.size()>0){
			for (int i=0;i<inOrderLines.size();i++){
				this.getOrderLineDao().create(inOrderLines.get(i));
				this.createLogging(inClassName, "OrderLine", inActionName, inOrderLines.get(i).toString(), inUserId, inNowTime);
			}
		}
	}

	/**
	 * UPDATE ORDER_LINE_LOTNO.CANCEL_FLAG=1 
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderLineLotnos(java.util.List)
	 */
	@Override
	public void deleteOrderLineLotnos(List<OrderLineLotno> inOrderLineLotnos, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		for (int i=0;i<inOrderLineLotnos.size();i++){
			OrderLineLotno delOrderLineLotno = inOrderLineLotnos.get(i);
			delOrderLineLotno.setCancelFlag(true);
			delOrderLineLotno.setUpdateDate(inNowTime);//2013.02.19 for INF004
			delOrderLineLotno.setUpdateUser(inUserId);//2013.02.19 for INF004
			this.getOrderLineLotnoDao().update(delOrderLineLotno);
			this.createLogging(inClassName, "OrderLineLotno", inActionName, delOrderLineLotno.toString(), inUserId, inNowTime);
		}
	}
	
	/**
	 * DLETE ORDER_LINE_LOTNO 
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderLineLotnos(java.util.List)
	 */
	@Override
	public void dropOrderLineLotnos(List<OrderLineLotno> inOrderLineLotnos, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		for (int i=0;i<inOrderLineLotnos.size();i++){
			this.createLogging(inClassName, "OrderLineLotno", inActionName, inOrderLineLotnos.get(i).toString(), inUserId, inNowTime);
			this.getOrderLineLotnoDao().delete(inOrderLineLotnos.get(i));
		}
	}

	/**
	 * DLETE ORDER_INTERNAL_CHECK_INFO
	*/
	@Override
	public void dropOrderInternalCheckInfos(List<OrderInternalCheckInfo> inOrderInternalCheckInfos, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		for (int i=0;i<inOrderInternalCheckInfos.size();i++){
			this.createLogging(inClassName, "OrderInternalCheckInfo", inActionName, inOrderInternalCheckInfos.get(i).toString(), inUserId, inNowTime);
			this.getOrderInternalCheckInfoDao().delete(inOrderInternalCheckInfos.get(i));
		}
	}

	/**
	 * 依據Customer,PONumber,Import找出的ORDER_LINE_INT
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderLineInts(java.lang.String, java.lang.String)
	 */
	@Override
	public List<OrderLineInt> getOrderLineInts(String inCustomerId,
			String inCustomerPo, String inImportDateS, String inImportDateE) {
		List<OrderLineInt> datas=new ArrayList<OrderLineInt>();
		String hql="select c from OrderLineInt c where getFlag=0 and customerId='"+inCustomerId+"' ";
		if ("".equals(inCustomerPo)){
			
		}
		else{
			hql=hql+" and customerPo='"+inCustomerPo+"' ";//2013.02.20
		}
		
		if ((!"".equals(inImportDateS))&&(!"".equals(inImportDateE))){
			hql=hql+" and importDate>='"+inImportDateS+"' and importDate<='"+inImportDateE+"' ";
		}
		
		log.debug(hql);
		try {
			datas=this.getOrderLineIntDao().createQuery(hql)
					.list();
		} catch (Exception e) {
			
		}
		
		return datas;
	}
	
	
	@Override
	public List<OrderLineInt> getOrderLineIntByWiReport(String inCustomerLotNo,String inCustomerJob, String inWaferData) {
		List<OrderLineInt> orderLineIntLists=new ArrayList<OrderLineInt>();
		
		String sql="SELECT * FROM ORDER_LINE_INT WHERE CUSTOMER_LOTNO =:CUSTOMER_LOTNO AND CUSTOMER_JOB =:CUSTOMER_JOB AND WAFER_DATA =:WAFER_DATA AND GET_FLAG=0 ";
		
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
		query.setParameter("CUSTOMER_JOB", inCustomerJob);
		query.setParameter("WAFER_DATA", inWaferData);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		if (tmpList != null){
			if (tmpList.size()>0){
				for(int i = 0; i < tmpList.size(); i++ ){
					Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
					OrderLineInt tmpOrderLineInt=new OrderLineInt();
					tmpOrderLineInt.setCustomerLotNo((String) row.get("CUSTOMER_LOTNO"));
					tmpOrderLineInt.setCustomerJob((String) row.get("CUSTOMER_JOB"));
					tmpOrderLineInt.setWaferData((String) row.get("WAFER_DATA"));
					
					orderLineIntLists.add(tmpOrderLineInt);
				}
			}
		}
		return orderLineIntLists;
	}

	/**
	 * 依據ORDER_LINE_INT_IDX找出ORDER_LINE_INT
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderLineIntByIdx(int)
	 */
	@Override
	public OrderLineInt getOrderLineIntByIdx(int inOrderLineIntIdx) {
		List<OrderLineInt> datas=new ArrayList<OrderLineInt>();
		try {
			String hql="select c from OrderLineInt c where orderLineIntIdx=:idx ";
			datas=this.getOrderLineIntDao().createQuery(hql)
					.setParameter("idx", inOrderLineIntIdx)
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
	}

	/**
	 * 依據orderNumber找出OrderLine,並排除cancelFlag=1的資料
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderLinesByOrderNumber(java.lang.String)
	 */
	@Override
	public List<OrderLine> getOrderLinesByOrderNumber(String inOrderNumber) {
		List<OrderLine> datas=new ArrayList<OrderLine>();
		try {
			String hql="select c from OrderLine c where cancelFlag=0 and orderNumber=:orderNumber order by poItem ";
			datas=this.getOrderLineDao().createQuery(hql)
					.setParameter("orderNumber", inOrderNumber)
					.list();
		} catch (Exception e) {
			
		}
		return datas;
	}

	/**
	 * INSERT INTO ORDER_LINE_LOTNO
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#createOrderLineLotnos(java.util.List)
	 */
	@Override
	public void createOrderLineLotno(OrderLineLotno inOrderLineLotno, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		//if (inOrderLineLotnoS.size()>0){
			//for (int i=0;i<inOrderLineLotnos.size();i++){
				this.getOrderLineLotnoDao().create(inOrderLineLotno);
				this.createLogging(inClassName, "OrderLineLotno", inActionName, inOrderLineLotno.toString(), inUserId, inNowTime);
			//}
		//}
	}

	/**
	 * UPDATE ORDER_LINE_LOTNO
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderLineLotnos(java.util.List)
	 */
	@Override
	public void updateOrderLineLotnos(List<OrderLineLotno> inOrderLineLotnos, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		if (inOrderLineLotnos.size()>0){
			for (int i=0;i<inOrderLineLotnos.size();i++){
				this.getOrderLineLotnoDao().update(inOrderLineLotnos.get(i));
				this.createLogging(inClassName, "OrderLineLotno", inActionName, inOrderLineLotnos.get(i).toString(), inUserId, inNowTime);
			}
		}
	}
	
	/**
	 * 
	 * 透過 OrderEntryLotnoModel ,UPDATE ORDER_LINE_LOTNO
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderEntryLotnoModel(java.util.List)
	 */
	/*@Override
	public void updateOrderEntryLotnoModel(List<OrderEntryLotnoModel> inOrderEntryLotnoModels){
		if (inOrderEntryLotnoModels.size()>0){
			for (int i=0;i<inOrderEntryLotnoModels.size();i++){
				if (inOrderEntryLotnoModels.get(i).getOrderLineLotno().getOrderLineLotnoIdx()==0){
					this.getOrderLineLotnoDao().create(inOrderEntryLotnoModels.get(i).getOrderLineLotno());
				}
				else{
					this.getOrderLineLotnoDao().update(inOrderEntryLotnoModels.get(i).getOrderLineLotno());
				}
			}
		}
	}*/

	/**
	 * ORDER_HEADER,ORDER_LINE,ORDER_LINT_LOTNO一起儲存，如果發生Exception時會Rollback
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#saveOETransactionItems(java.lang.Object[])
	 */
	@Override
	public void saveOETransactionItems(Object[] inObjs) {
		//A.將inObjs轉換為正確的型態
		//1.mode
		String mode=(String) inObjs[0];

		//2.要刪除的OrderLineLotno
		List<OrderLineLotno> delOrderLineLotnos=(List<OrderLineLotno>) inObjs[1];

		//2.1要刪除的OrderInternalCheckInfo
		List<OrderInternalCheckInfo> delOrderInternalCheckInfos=(List<OrderInternalCheckInfo>) inObjs[15];

		//3.要刪除的OrderLine
		List<OrderLine> delOrderLines=(List<OrderLine>) inObjs[2];

		//4.要更新或者新增的OrderHeader
		List<HashMap> orderNumbers = new ArrayList<HashMap>();
		OrderHeader orderHeader = new OrderHeader();
		if("newadd".equals(mode)){
			orderNumbers = (List<HashMap>) inObjs[3];
		}else{
			orderHeader=(OrderHeader) inObjs[3];	
		}
		//OrderHeader orderHeader=(OrderHeader) inObjs[3];

		//5.要新增的OrderLine
		List<OrderLine> orderLines=(List<OrderLine>) inObjs[4];

		//6.要儲存的saveOrderEntryLotnoModels
		List<OrderEntryLotnoModel> saveOrderEntryLotnoModels=(List<OrderEntryLotnoModel>) inObjs[5];

		//7.UserID
		String userId=(String) inObjs[6];

		//8.nowtime
		Date nowtime=(Date) inObjs[7];

		//9.ClassName
		String className=(String) inObjs[8];

		//10.ActionName
		String actionName=(String) inObjs[9];

		//11.WAFER_STATUS(insert)
		List<WaferStatus> insertWaferStatus=(List<WaferStatus>) inObjs[10];

		//12.WAFER_STATUS(update)
		List<WaferStatus> updateWaferStatus=(List<WaferStatus>) inObjs[11];

		//13.WAFER_STATU(修改前的WAFER_STATUS)
		List<WaferStatus> beforeWaferStatus=(List<WaferStatus>) inObjs[12];//2013.02.18

		//14.[ORDER_INTERNAL_CHECK_INFO]
		List<OrderInternalCheckInfo> saveOrderInternalCheckInfo=(List<OrderInternalCheckInfo>) inObjs[13];//OCF-PR-150202_Allison add

		//15.依CUSTOMER_LOTNO + WAFER_DATA來update [WAFER_BANKIN_WAFER].ORDER_NUMBER
		List<WaferBankin> updateWaferBankinWaferList = (List<WaferBankin>) inObjs[14];//OCF-PR-150202_Allison add

		//16.若有重複OE的，則PC會選擇要以那個ORDER_NUMBER來儲存[WAFER_BANKIN_WAFER].ORDER_NUMBER
		List<OeOrderNoConfirmModel> oeOrderNoConfirmModel = (List<OeOrderNoConfirmModel>) inObjs[16];//OCF-PR-160307_Allison add


		//B.開始Insert or Update的動作
		//Item 3:orderHeader
		if ("newadd".equals(mode)){
			//OCF-PR-151002_新建OE，改到OrderEntryViewCtrl的saveOETransactionItems前先Save，因需要先Save編出多筆OrderNumber
			//			for(int i=0; i<orderHeaders.size(); i++){
			//				this.createOrderHeader(orderHeaders.get(i));
			//				this.createLogging(className, "OrderHeader", actionName, orderHeaders.get(i).toString(), userId, nowtime);
			//			}
		}
		else{
			this.updateOrderHeader(orderHeader);
			this.createLogging(className, "OrderHeader", actionName, orderHeader.toString(), userId, nowtime);
		}
		//this.createLogging(className, "OrderHeader", actionName, orderHeader.toString(), userId, nowtime);

		//Item 4:orderLines
		if ("newadd".equals(mode)){
			//OCF-PR-151204_改到OrderEntryViewCtrl先儲存了，因有跟OrderHeader join
			//			log.debug(orderLines.size());
			//			for(int j=0; j<orderLines.size(); j++){
			//				log.debug(orderHeaders.get(j).getOrderHeaderIdx());
			//				log.debug(orderHeaders.get(j).getOrderNumber());
			//				orderLines.get(j).setOrderHeader(orderHeaders.get(j));
			//				orderLines.get(j).setOrderNumber(orderHeaders.get(j).getOrderNumber());
			////				this.createOrderLines(orderLines, userId, nowtime, className, actionName);
			////				for(int k=0; k<orderNumbers.size(); k++){
			////					for(int l=0; l<orderLines.get(j).getOrderLineLotnos().size(); l++){
			////						if(orderNumbers.get(k).containsKey(orderLines.get(j).getOrderLineLotnos().get(l).getCustomerLotno()+"_"+orderLines.get(j).getOrderLineLotnos().get(l).getCustomerJob())){
			////							orderLines.get(j).setOrderNumber(orderNumbers.get(k).get(orderLines.get(j).getOrderLineLotnos().get(l).getCustomerLotno()+"_"+orderLines.get(j).getOrderLineLotnos().get(l).getCustomerJob()).toString());
			//							this.createOrderLines(orderLines, userId, nowtime, className, actionName);
			////						}
			////					}
			////				}
			//			}
		}else{
			for (int i=0;i<orderLines.size();i++){
				orderLines.get(i).setOrderNumber(orderHeader.getOrderNumber());
			}
			this.createOrderLines(orderLines, userId, nowtime, className, actionName);
		}

		//Item 5:saveOrderEntryLotnoModels
		if ("newadd".equals(mode)){
			List<Hold> updateHolds=new ArrayList<Hold>();//OE-Modify狀態時，可能會有Hold，如果User有改POItem,Customerlotno時，Hold也要順便修改
			for (int j=0;j<saveOrderEntryLotnoModels.size();j++){
				for(int k=0; k<orderNumbers.size(); k++){
					if(orderNumbers.get(k).containsKey(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getCustomerLotno()+"_"+saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getCustomerJob())){
						//saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setOrderLineLotnoIdx(0);
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setOrderLine(orderLines.get(j));
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setOrderNumber(orderNumbers.get(k).get(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getCustomerLotno()+"_"+saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getCustomerJob()).toString());
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setUpdateUser(userId);
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setUpdateDate(nowtime);

						if ("newadd".equals(mode)){
							//OCF-PR-151002_改到OrderEntryViewCtrl的saveOETransactionItems前先處理了
							//if ("".equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getEntityId())){
							//	String empEntityId=this.createEntityId(nowtime);
							//	saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setEntityId(empEntityId);
							//	insertWaferStatus.get(j).setEntityId(empEntityId);
							//}
						}
						else{
							if ("".equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getEntityId())){
								String empEntityId=this.createEntityId(nowtime);
								saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setEntityId(empEntityId);
								int tmpc=saveOrderEntryLotnoModels.get(j).getTmpWaferStatus();
								insertWaferStatus.get(tmpc).setEntityId(empEntityId);
							}
						}

						this.createOrderLineLotno(saveOrderEntryLotnoModels.get(j).getOrderLineLotno(), userId, nowtime, className, actionName);//2013.02.18

						if (saveOrderEntryLotnoModels.get(j).getHold()!=null){
							updateHolds.add(saveOrderEntryLotnoModels.get(j).getHold());
						}
					}
				}
			}//end for j

			if (updateHolds.size()>0){
				this.updateHolds(updateHolds, userId, nowtime, className, actionName);
			}
		}else{
			//Item 5:saveOrderEntryLotnoModels
			List<Hold> updateHolds=new ArrayList<Hold>();//OE-Modify狀態時，可能會有Hold，如果User有改POItem,Customerlotno時，Hold也要順便修改
			for (int i=0;i<orderLines.size();i++){
				//List<OrderLineLotno> tmpSaveOrderLineLotnos=new ArrayList<OrderLineLotno>();
				for (int j=0;j<saveOrderEntryLotnoModels.size();j++){
					log.debug("Line PoItem="+orderLines.get(i).getPoItem()+", Lotno POItem="+saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getPoItem());
					if (orderLines.get(i).getPoItem().trim().equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getPoItem().trim())){
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setOrderLineLotnoIdx(0);
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setOrderLine(orderLines.get(i));
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setOrderNumber(orderHeader.getOrderNumber());
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setUpdateUser(orderHeader.getUpdateUser());
						saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setUpdateDate(orderHeader.getUpdateDate());

						//OCF-PR-151002_上面if已經分成 newadd和else，故這邊不會進入newadd，因此mark
						//if ("newadd".equals(mode)){
						//	if ("".equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getEntityId())){
						//		String empEntityId=this.createEntityId(nowtime);
						//		saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setEntityId(empEntityId);
						//		insertWaferStatus.get(j).setEntityId(empEntityId);
						//	}
						//}
						//else{
						if ("".equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getEntityId())){
							String empEntityId=this.createEntityId(nowtime);
							saveOrderEntryLotnoModels.get(j).getOrderLineLotno().setEntityId(empEntityId);
							int tmpc=saveOrderEntryLotnoModels.get(j).getTmpWaferStatus();
							insertWaferStatus.get(tmpc).setEntityId(empEntityId);
						}
						//}

						//tmpSaveOrderLineLotnos.add(saveOrderEntryLotnoModels.get(j).getOrderLineLotno());
						this.createOrderLineLotno(saveOrderEntryLotnoModels.get(j).getOrderLineLotno(), userId, nowtime, className, actionName);//2013.02.18

						if (saveOrderEntryLotnoModels.get(j).getHold()!=null){
							updateHolds.add(saveOrderEntryLotnoModels.get(j).getHold());
						}
					}
				}//end for j
				//if (tmpSaveOrderLineLotnos.size()>0){
				//	this.createOrderLineLotnos(tmpSaveOrderLineLotnos, userId, nowtime, className, actionName);
				//}
				if (updateHolds.size()>0){
					this.updateHolds(updateHolds, userId, nowtime, className, actionName);
				}
			}//end for i
		}

		//Item 2,3:delOrderLineLotnos,delOrderLines
		if ("modify".equals(mode)){
			//this.updateOrderLineLotnos(delOrderLineLotnos, userId, nowtime, className, actionName);
			//this.updateOrderLines(delOrderLines, userId, nowtime, className, actionName);
			this.dropOrderInternalCheckInfos(delOrderInternalCheckInfos, userId, nowtime, className, actionName);
			this.dropOrderLineLotnos(delOrderLineLotnos, userId, nowtime, className, actionName);
			this.dropOrderLines(delOrderLines, userId, nowtime, className, actionName);
		}

		//Item 11,12
		if ("newadd".equals(mode)){
			if (insertWaferStatus.size()>0){
				for (int i=0;i<insertWaferStatus.size();i++){
					for(int k=0; k<orderNumbers.size(); k++){
						if(orderNumbers.get(k).containsKey(insertWaferStatus.get(i).getCustomerLotno()+"_"+insertWaferStatus.get(i).getCustomerJob())){
							insertWaferStatus.get(i).setOrderNumber(orderNumbers.get(k).get(insertWaferStatus.get(i).getCustomerLotno()+"_"+insertWaferStatus.get(i).getCustomerJob()).toString());//2013.03.20
							insertWaferStatus.get(i).setPoNumber(orderNumbers.get(k).get(insertWaferStatus.get(i).getCustomerLotno()+"_"+insertWaferStatus.get(i).getCustomerJob()).toString());//IT-PR-141201
							//insertWaferStatus.get(i).setOrderNumber(orderHeaders.get(i).getOrderNumber());
							//insertWaferStatus.get(i).setPoNumber(orderHeaders.get(i).getOrderNumber());
							this.createWaferStatus(insertWaferStatus.get(i),userId,nowtime,className,actionName);//2013.04.01
						}
					}
				}//end for i
			}
		}
		else{
			if (updateWaferStatus.size()>0){
				for (int i=0;i<updateWaferStatus.size();i++){
					this.updateWaferStatus(updateWaferStatus.get(i),userId,nowtime,className,actionName);//2013.04.01
				}//end for i
			}

			//Update的狀態也有可能會insert WAFER_STATUS
			if ((insertWaferStatus!=null)&&(insertWaferStatus.size()>0)){
				for (int i=0;i<insertWaferStatus.size();i++){
					insertWaferStatus.get(i).setOrderNumber(orderHeader.getOrderNumber());//2013.03.20
					insertWaferStatus.get(i).setPoNumber(orderHeader.getPoNumber());//IT-PR-141201
					this.createWaferStatus(insertWaferStatus.get(i),userId,nowtime,className,actionName);//2013.04.01
				}//end for i
			}
		}

		//Item13 確認是否有要清空WAFER_STATUS.ENTITY_ID的情況
		//情況一:
		//Before WAFER_STATUS  --> After ORDER_LINE_LOTNO
		//       1             --> 1
		//       2             -->
		//修改前WAFER_STATUS有兩筆，修改後將第二筆資料刪除，所以WAFER_STATUS的第二筆也應該要將ENTITY_ID清空，及STATE_FLAG=2
		if ("modify".equals(mode)){
			if ((beforeWaferStatus!=null)&&(beforeWaferStatus.size()>0)){
				for (int i=0;i<beforeWaferStatus.size();i++){
					boolean flag=false;
					for (int j=0;j<saveOrderEntryLotnoModels.size();j++){
						if (beforeWaferStatus.get(i).getEntityId().equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getEntityId())){
							flag=true;
							break;
						}
					}//end for j

					if (flag==false){
						beforeWaferStatus.get(i).setStateFlag("2");
						beforeWaferStatus.get(i).setUpdateDate(nowtime);
						beforeWaferStatus.get(i).setUpdateUser(userId);
						beforeWaferStatus.get(i).setEntityId("");
						this.updateWaferStatus(beforeWaferStatus.get(i),userId,nowtime,className,actionName);//2013.04.01
					}
				}//end for i
			}
		}

		//Item14 若saveOrderInternalCheckInfo有資料，則要存入[ORDER_INTERNAL_CHECK_INFO]裡
		if ("newadd".equals(mode)){
			//			if(saveOrderInternalCheckInfo.size() > 0){
			//				for(int i=0; i<saveOrderInternalCheckInfo.size(); i++){
			//					saveOrderInternalCheckInfo.get(i).setOrderNumber(orderHeader.getOrderNumber());
			//				}
			//				this.insertOrderInternalCheckInfo(saveOrderInternalCheckInfo);
			//			}
		}else{
			if(saveOrderInternalCheckInfo.size() > 0){
				for(int i=0; i<saveOrderInternalCheckInfo.size(); i++){
					saveOrderInternalCheckInfo.get(i).setOrderNumber(orderHeader.getOrderNumber());
				}
				this.insertOrderInternalCheckInfo(saveOrderInternalCheckInfo);
			}
		}


		//Item15若updateWaferBankinWaferList有資料，則要update [WAFER_BANKIN_WAFER].ORDER_NUMBER來做連結
		if ("newadd".equals(mode)){
			if(updateWaferBankinWaferList.size() > 0){
				for(int i=0; i<updateWaferBankinWaferList.size(); i++){
					String composeWaferData = "";
					if(updateWaferBankinWaferList.get(i).getWaferBankinWafers().size() > 0){
						for(int j=0; j<updateWaferBankinWaferList.get(i).getWaferBankinWafers().size(); j++){
							boolean checkflag = false;
							String orderNumber = "";
							for(int k=0; k<saveOrderEntryLotnoModels.size(); k++){
								String[] splitOeWaferData = saveOrderEntryLotnoModels.get(k).getOrderLineLotno().getWaferData().split(";");

								for(int r=0; r<splitOeWaferData.length; r++){
									String tmpWaferNo = "";
									if(Integer.valueOf(splitOeWaferData[r].toString()) < 10){
										tmpWaferNo = saveOrderEntryLotnoModels.get(k).getOrderLineLotno().getCustomerLotno()+"-0"+splitOeWaferData[r].toString();
									}else{
										tmpWaferNo = saveOrderEntryLotnoModels.get(k).getOrderLineLotno().getCustomerLotno()+"-"+splitOeWaferData[r].toString();
									}

									if(updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).getReceiveWaferNo().equals(tmpWaferNo)){
										checkflag = true;
										orderNumber = saveOrderEntryLotnoModels.get(k).getOrderLineLotno().getOrderNumber();
										break;
									}
								}
								if(checkflag){
									break;
								}
							}

							if(checkflag){
								//OCF-PR-160307_若oeOrderNoConfirmModel有資料，代表有Wafer是重複OE，則orderNumber需依跳出視窗PC所選的orderNumber來update
								if(oeOrderNoConfirmModel.size() > 0){
									for(int o=0; o<oeOrderNoConfirmModel.size(); o++){
										if(oeOrderNoConfirmModel.get(o).isSelect()){
											String[] splitWaferData = oeOrderNoConfirmModel.get(o).getWaferData().split(";");
											boolean chkFlag = false;
											for(int s=0; s<splitWaferData.length; s++){
												String formatStr = "%02d";
												String tmpWaferNo = oeOrderNoConfirmModel.get(o).getCustomerLotNo()+"-"+String.format(formatStr, Integer.valueOf(splitWaferData[s]));
												if(updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).getReceiveWaferNo().equals(tmpWaferNo)){
													chkFlag = true;
												}	
											}
											if(chkFlag){
												if("New Order".equals(oeOrderNoConfirmModel.get(o).getOrderNumber())){
													updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).setOrderNumber(orderNumber);
												}else{
													updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).setOrderNumber(oeOrderNoConfirmModel.get(o).getOrderNumber());
												}
											}else{
												updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).setOrderNumber(orderNumber);
											}
										}
									}
								}else{
								updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).setOrderNumber(orderNumber);
								}								
								//OCF-PR-160303_若原[WAFER_BANKIN_WAFER].INAVI_LOTNO已有資料，代表是重新OE(double OE)，則需將INAVI_LOTNO清空，於iNavigator重新RFIDCard時會再重塞一次INAVI_LOTNO
								if(!"".equals(updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).getiNaviLotNo())){
									updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).setiNaviLotNo("");
								}
							}
						}
						this.updateWaferBankinAndWafers(updateWaferBankinWaferList);
					}
				}
			}
		}else{
			if(updateWaferBankinWaferList.size() > 0){
				for(int i=0; i<updateWaferBankinWaferList.size(); i++){
					if(updateWaferBankinWaferList.get(i).getWaferBankinWafers().size() > 0){
						for(int j=0; j<updateWaferBankinWaferList.get(i).getWaferBankinWafers().size(); j++){
							//OCF-PR-160307_若oeOrderNoConfirmModel有資料，代表有Wafer是重複OE，則orderNumber需依跳出視窗PC所選的orderNumber來update
							if(oeOrderNoConfirmModel.size() > 0){
								for(int o=0; o<oeOrderNoConfirmModel.size(); o++){
									if(oeOrderNoConfirmModel.get(o).isSelect()){
										String[] splitWaferData = oeOrderNoConfirmModel.get(o).getWaferData().split(";");
										boolean chkFlag = false;
										for(int s=0; s<splitWaferData.length; s++){
											String tmpWaferNo = oeOrderNoConfirmModel.get(o).getCustomerLotNo()+"-"+String.format("%02d", Integer.valueOf(splitWaferData[s]));
											if(updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).getReceiveWaferNo().equals(tmpWaferNo)){
												chkFlag = true;
											}	
										}
										if(chkFlag){
											updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).setOrderNumber(oeOrderNoConfirmModel.get(o).getOrderNumber());
										}else{
											updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).setOrderNumber(orderHeader.getOrderNumber());
										}
									}
								}
							}else{
							updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).setOrderNumber(orderHeader.getOrderNumber());
						}
						}
						this.updateWaferBankinAndWafers(updateWaferBankinWaferList);
					}
				}
			}
		}
	}


	/**
	 * INSERT INTO WAFER_STATUS
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#createWaferStatus(com.tce.ivision.model.WaferStatus)
	 */
	@Override
	public void createWaferStatus(WaferStatus inWaferStatus,String inUserId, Date inNowTime, String inClassName, String inActionName) {
		this.getWaferStatusDao().create(inWaferStatus);
		this.createLogging(inClassName, "WaferStatus", inActionName, inWaferStatus.toString(), inUserId, inNowTime);//2013.04.01 
	}

	/**
	 * UPDATE WAFER_STATUS
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateWaferStatus(com.tce.ivision.model.WaferStatus)
	 */
	@Override
	public void updateWaferStatus(WaferStatus inWaferStatus,String inUserId, Date inNowTime, String inClassName, String inActionName) {
		this.getWaferStatusDao().update(inWaferStatus);
		this.createLogging(inClassName, "WaferStatus", inActionName, inWaferStatus.toString(), inUserId, inNowTime);//2013.04.01
	}

	/**
	 * 依據OrderNumber找出ORDER_HEADER.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderHeaderByOrderNumber(java.lang.String)
	 */
	@Override
	public OrderHeader getOrderHeaderByOrderNumber(String inOrderNumber) {
		List<OrderHeader> datas=new ArrayList<OrderHeader>();
		String hql="select c from OrderHeader c where orderNumber=:order_number ";
		try {
			datas=this.getOrderHeaderDao().createQuery(hql)
					.setParameter("order_number", inOrderNumber)
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
		
	}
	
	/**
	 * 依據OrderNumber找出ORDER_HEADER.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderHeaderByOrderNumber(java.lang.String)
	 */
	@Override
	public OrderHeader getOrderHeaderByPoNumberProduct(String inPONumber,String inProduct) {
		List<OrderHeader> datas=new ArrayList<OrderHeader>();
		String hql="select c from OrderHeader c where poNumber=:poNumber and product=:product ";
		try {
			datas=this.getOrderHeaderDao().createQuery(hql)
					.setParameter("poNumber", inPONumber)
					.setParameter("product", inProduct)
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
		
	}
	
	/**
	 * 依據BILL_TO+PO_NUMBER找出ORDER_HEADER.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderHeaderByOrderNumber(java.lang.String)
	 */
	@Override
	public List<OrderHeader> getOrderHeaderByBillToPoNumber(String inBillTo,String inOrderNumber) {
		List<OrderHeader> datas=new ArrayList<OrderHeader>();
		String hql="select c from OrderHeader c where billTo=:billTo and cancelFlag=0 and orderStatus <> '40' ";
		if (!"".equals(inOrderNumber)){
			hql+="and orderNumber<>'"+inOrderNumber+"' ";
		}
		try {
			datas=this.getOrderHeaderDao().createQuery(hql)
					.setParameter("billTo", inBillTo)
					.list();
		} catch (Exception e) {
			
		}
		
		return datas;
		
	}

	/**
	 * 讀取HOLD BY ORDER_NUMBER+PO_ITEM+CUSTOMER_LOTNO 且 ORDER BY HOLD_ISSUE_DATE DESC 取第一筆.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getHold(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Hold getHold(String inOrderNumber, String inPoItem,
			String inCustomerLotno) {
		List<Hold> datas=new ArrayList<Hold>();
		//String hql="select c from Hold c where orderNumber=:order_number and poItem=:po_item and customerLotno=:lotno "+
		String hql="select c from Hold c where orderNumber=:order_number and customerLotno=:lotno "+//OCF-PR-160307_mark PO_ITEM條件，解決OE若更改PO_ITEM後找不到先前HOLD資料的問題
		           "order by holdIssueDate desc";
		try {
			datas=this.getHoldDao().createQuery(hql)
					.setParameter("order_number", inOrderNumber)
					//.setParameter("po_item", inPoItem) //OCF-PR-160307_mark PO_ITEM條件，解決OE若更改PO_ITEM後找不到先前HOLD資料的問題
					.setParameter("lotno", inCustomerLotno)
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#saveHoldTransactionItems(java.lang.Object[])
	 */
	@Override
	public void saveHoldTransactionItems(Object[] inObjs) {
		//A.將inObjs轉換為正確的型態
			//1.insertHolds
		    List<Hold> insertHolds= (List<Hold>) inObjs[0];
		    
		    //2.updateHolds
		    List<Hold> updateHolds= (List<Hold>) inObjs[1];
		    
		    //3.orderLineLotnos
		    List<OrderLineLotno> orderLineLotnos = (List<OrderLineLotno>) inObjs[2];
			
			//4.userId
			String userId=(String) inObjs[3];
			
			//5.nowtime
			Date nowtime=(Date) inObjs[4];
			
			//6.class_name
			String className=(String) inObjs[5];
			
			//7.action_name
			String actionName=(String) inObjs[6];
			
			//8.insertHoldWafers
			List<HoldWafer> insertHoldWafers = (List<HoldWafer>) inObjs[7];
			
			//9.insertHoldWafers
			List<HoldWafer> updateHoldWafers = (List<HoldWafer>) inObjs[8];
			
			//9.insertHoldWafers
			String orderNumber = (String) inObjs[9];
		    
		//B.開始Insert or Update的動作
		    this.insertHolds(insertHolds, userId, nowtime, className, actionName);
		    this.updateHolds(updateHolds, userId, nowtime, className, actionName);
		    this.insertHoldWafers(insertHoldWafers, userId, nowtime, className, actionName, orderNumber, insertHolds, orderLineLotnos);
		    this.updateHoldWafers(updateHoldWafers, userId, nowtime, className, actionName, orderNumber, updateHolds, orderLineLotnos);
		    this.updateOrderLineLotnos(orderLineLotnos, userId, nowtime, className, actionName);
		
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#saveHoldTransactionItems(java.lang.Object[])
	 */
	@Override
	public void saveHoldAndHoldWaferTransactionItems(Object[] inObjs) {
		//A.將inObjs轉換為正確的型態
			//1.insertHolds
		    List<Hold> insertHolds= (List<Hold>) inObjs[0];
		    
		    //2.updateHolds
		    List<Hold> updateHolds= (List<Hold>) inObjs[1];
		    
		    //3.orderLineLotnos
		    List<OrderLineLotno> orderLineLotnos = (List<OrderLineLotno>) inObjs[2];
			
			//4.userId
			String userId=(String) inObjs[3];
			
			//5.nowtime
			Date nowtime=(Date) inObjs[4];
			
			//6.class_name
			String className=(String) inObjs[5];
			
			//7.action_name
			String actionName=(String) inObjs[6];
			
			//8.insertHoldWafers
			List<HoldWafer> insertHoldWafers = (List<HoldWafer>) inObjs[7];
			
			//9.insertHoldWafers
			List<HoldWafer> updateHoldWafers = (List<HoldWafer>) inObjs[8];
			
			//9.insertHoldWafers
			String orderNumber = (String) inObjs[9];
		    
		//B.開始Insert or Update的動作
		    this.insertHolds(insertHolds, userId, nowtime, className, actionName);
		    this.updateHolds(updateHolds, userId, nowtime, className, actionName);
		    this.insertHoldWafers(insertHoldWafers, userId, nowtime, className, actionName, orderNumber, insertHolds, orderLineLotnos);
		    this.updateHoldWafers(updateHoldWafers, userId, nowtime, className, actionName, orderNumber, updateHolds, orderLineLotnos);
		    this.updateOrderLineLotnos(orderLineLotnos, userId, nowtime, className, actionName);
		
	}


	/**
	 * insert HOLD
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#insertHolds(java.util.List)
	 */
	@Override
	public void insertHolds(List<Hold> inHolds, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		if (inHolds.size()>0){
			for (int i=0;i<inHolds.size();i++){
				this.getHoldDao().create(inHolds.get(i));
				this.createLogging(inClassName, "Hold", inActionName, inHolds.get(i).toString(), inUserId, inNowTime);
			}
		}
	}

	/**
	 * update HOLD
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateHolds(java.util.List)
	 */
	@Override
	public void updateHolds(List<Hold> inHolds, String inUserId, Date inNowTime, String inClassName, String inActionName) {
		if (inHolds.size()>0){
			for (int i=0;i<inHolds.size();i++){
				this.getHoldDao().update(inHolds.get(i));
				this.createLogging(inClassName, "Hold", inActionName, inHolds.get(i).toString(), inUserId, inNowTime);
			}
		}
	}

	/**
	 * 讀取ORDER_LINE_LOTNO BY ORDER_NUMBER+PO_ITEM+CUSTOMER_LOTNO
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderLineLotno(java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OrderLineLotno getOrderLineLotno(String inOrderNumber,
			String inPoItem, String inCustomerLotno) {
		List<OrderLineLotno> datas=new ArrayList<OrderLineLotno>();
		try {
			String hql="select c from OrderLineLotno c where cancelFlag=0 and orderNumber=:order_number and poItem=:po_item and customerLotno=:lotno ";
			log.debug(inOrderNumber);
			log.debug(inPoItem);
			log.debug(inCustomerLotno);
			log.debug(hql);
			
			datas=this.getOrderLineLotnoDao().createQuery(hql)
					.setParameter("order_number", inOrderNumber)
					.setParameter("po_item", inPoItem)
					.setParameter("lotno", inCustomerLotno)
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
	 * 讀取ORDER_LINE_LOTNO BY ORDER_NUMBER+PO_ITEM+CUSTOMER_LOTNO
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderLineLotno(java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OrderLineLotno> getOrderLineLotnosByOrderNumberPoItem(String inOrderNumber, String inPoItem) {
		List<OrderLineLotno> datas=new ArrayList<OrderLineLotno>();
		try {
			String hql="select c from OrderLineLotno c where cancelFlag=0 and orderNumber=:order_number and poItem=:po_item ";
			log.debug(inOrderNumber);
			log.debug(inPoItem);
			log.debug(hql);
			
			datas=this.getOrderLineLotnoDao().createQuery(hql)
					.setParameter("order_number", inOrderNumber)
					.setParameter("po_item", inPoItem)
					.list();
		} catch (Exception e) {
			 StringWriter stringWriter = new StringWriter();
             e.printStackTrace(new PrintWriter(stringWriter));
             log.error(stringWriter.toString());
		}
		return datas;
		
		
	}

	/**
	 * 依據inDate編出EntityID.
	 * format:YYYYMMDD###
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#createEntityId(java.util.Date)
	 */
	@Override
	public String createEntityId(Date inDate) {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//		int todayMaxIndex = 0;
//		String entityId = "";
//		List<Object> tmps=new ArrayList<Object>();
//		try {
//			//搜尋ENTITY_ID開頭符合YYYYMMDD的ENTITY_ID並取出當日流水號的最大值
//			String sql=" SELECT MAX(CAST(SUBSTRING(ENTITY_ID,9) AS SIGNED)) AS TODAY_MAX_INDEX " +
//					" FROM ORDER_LINE_LOTNO WHERE ENTITY_ID LIKE :ENTITY_ID ";
//			Query query = this.getDao().createSQLQuery(sql);
//			query.setParameter("ENTITY_ID", dateFormat.format(new Date()) + "%" );
//			tmps=query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
//			if(tmps.size() > 0){
//				Map<String,Object> row = (Map<String,Object>) tmps.get(0);
//				log.debug(tmps.get(0));
//				if(row.get("TODAY_MAX_INDEX") != null){
//					BigInteger bigInteger = (BigInteger) row.get("TODAY_MAX_INDEX");
//					todayMaxIndex = bigInteger.intValue();
//				}
//			}
//			todayMaxIndex++;
//			entityId =  dateFormat.format(new Date()) + String.format("%03d",todayMaxIndex);
//		} catch (Exception e) {
//			log.debug(e.getLocalizedMessage());	
//			e.printStackTrace();
//		}
//		return entityId;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String codeType="ENTITY_ID";
		String fixCode=dateFormat.format(new Date());
		String entityId="";
		int todayMaxIndex=0;
		Encode encode=this.getEncode(codeType, fixCode);
		if (encode==null){
			todayMaxIndex=1;
			
			encode=new Encode();
			encode.setCodeType(codeType);
			encode.setFixCode(fixCode);
			encode.setNo(todayMaxIndex);
			this.createEncode(encode);
		}
		else{
			todayMaxIndex=encode.getNo()+1;
			
			encode.setNo(todayMaxIndex);
			this.updateEncode(encode);
		}
		
		entityId=fixCode+String.format("%03d",todayMaxIndex);
		return entityId;
	}

	/**
	 * 2013.02.18 依據indelOrderLineLotnos的entityId找出有哪些wafer_status要將state_flag改為2
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getWaferStatusByOrderLineLotno(java.util.List)
	 */
	@Override
	public List<WaferStatus> getWaferStatusByOrderLineLotno(
			List<OrderLineLotno> indelOrderLineLotnos) {
		List<WaferStatus> datas=new ArrayList<WaferStatus>();
		for (int i=0;i<indelOrderLineLotnos.size();i++){
			if ((indelOrderLineLotnos.get(i).getEntityId()!=null)&&
			    (!"".equals(indelOrderLineLotnos.get(i).getEntityId()))){
				WaferStatus delWaferStatus=this.getWaferStatusByEntityId(indelOrderLineLotnos.get(i).getEntityId());
				if (delWaferStatus!=null){
					datas.add(delWaferStatus);
				}
			}
			
		}
		
		return datas;
	}

	/**
	 * 2013.02.18 依據Entity找出Wafer_Status
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getWaferStatusByEntityId(java.lang.String)
	 */
	@Override
	public WaferStatus getWaferStatusByEntityId(String inEntityId) {
		List<WaferStatus> datas=new ArrayList<WaferStatus>();
		String hql="select c from WaferStatus c where entityId=:entity_id ";
		try {
			datas=this.getWaferStatusDao().createQuery(hql)
					.setParameter("entity_id", inEntityId)
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
	}

	/**
	 * 依據CODE_TYPE,FIX_CODE找出Encode
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getEncode(java.lang.String, java.lang.String)
	 */
	@Override
	public Encode getEncode(String inCodeType, String inFixCode) {
		List<Encode> datas=new ArrayList<Encode>();
		String hql="select c from Encode c where codeType=:code_type and fixCode=:fix_code ";
		try {
			datas=this.getEncodeDao().createQuery(hql)
					.setParameter("code_type", inCodeType)
					.setParameter("fix_code", inFixCode)
					.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
		
	}

	/**
	 * update Encode
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateEncode(com.tce.ivision.model.Encode)
	 */
	@Override
	public void updateEncode(Encode inEncode) {
		this.getEncodeDao().update(inEncode);
	}

	/**
	 * insert Encode
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#createEncode(com.tce.ivision.model.Encode)
	 */
	@Override
	public void createEncode(Encode inEncode) {
		this.getEncodeDao().create(inEncode);
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getProductInfo(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ProductInfo getProductInfo(String inCustomerCode,
			String inProductClassCode, String inProduct) {
		List<ProductInfo> datas=new ArrayList<ProductInfo>();
		String hql="select c from ProductInfo c where customerCode=:customerCode and productClassCode=:productClassCode "+
		           "and product=:product and deleteFlag=0 ";
		try {
			datas=this.getProductInfoDao().createQuery(hql)
					.setParameter("customerCode", inCustomerCode)
					.setParameter("productClassCode", inProductClassCode)
					.setParameter("product", inProduct)
					.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else {
			return null;
		}
	}
	
	/**
	 * insert ORDER_LINE_INT
	 * @see com.tce.ischedule.schedule.job.b2b.aptina.service.AptinaB2bService#insertOrderLineInts(java.util.List)
	 */
	@Override
	public void insertOrderLineInts(List<OrderLineInt> inOrderLineInts) { 
		if (inOrderLineInts.size()>0){
			for (int i=0;i<inOrderLineInts.size();i++){
				this.getOrderLineIntDao().create(inOrderLineInts.get(i));
			}
		}
	}
	
	/**
	 * insert ORDER_LINE_INT
	 * @see com.tce.ischedule.schedule.job.b2b.aptina.service.AptinaB2bService#insertOrderLineInts(java.util.List)
	 */
	@Override
	public void insertWaferBankinInts(List<WaferBankinInt> inWaferBankinInts) { 
		if (inWaferBankinInts.size()>0){
			for (int i=0;i<inWaferBankinInts.size();i++){
				this.getWaferBankinIntDao().create(inWaferBankinInts.get(i));
			}
		}
	}
	
	/**
	 * 用inCustomerLotNo找出WAFER_INFO

	 */
	@Override
	public List<WaferInfo> getWaferInfoByCustomerLotNo(String inCustomerLotNo) {
		List<WaferInfo> datas=new ArrayList<WaferInfo>();
		try {
			String hql="select c from WaferInfo c where customerLotno=:customerLotno order by waferNo ";
			datas=this.getWaferInfoDao().createQuery(hql)
					.setParameter("customerLotno", inCustomerLotNo)
					.list();
		} catch (Exception e) {
			
		}
		return datas;
	}
	
	/**
	 * 用inOrderNumber & inCustomerLotNo找出ORDER_LINE_LOTNO
	 */
	@Override
	public List<OrderLineLotno> getOrderLineLotnosByOrderNumberAndCustomerLotNo(String inOrderNumber, String inCustomerLotNo) {
		List<OrderLineLotno> datas=new ArrayList<OrderLineLotno>();
		try {
			String hql="select c from OrderLineLotno c where cancelFlag=0 and orderNumber=:order_number and customerLotno=:customerLotno order by poItem,customerLotno ";
			datas=this.getOrderLineLotnoDao().createQuery(hql)
					.setParameter("order_number", inOrderNumber)
					.setParameter("customerLotno", inCustomerLotNo)
					.list();
		} catch (Exception e) {
			
		}
		return datas;
	}
	
	/**
	 * 讀取HOLD_WAFER BY HOLD_IDX
	 */
	@Override
	public List<HoldWafer> getHoldWafer(int holdIdx) {
		List<HoldWafer> datas=new ArrayList<HoldWafer>();
		String hql="select c from HoldWafer c where holdIdx=:holdIdx "+
		           "order by waferNo";
		try {
			datas=this.getHoldDao().createQuery(hql)
					.setParameter("holdIdx", holdIdx)
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas;
		}
		else{
			return null;
		}
	}
	
	/**
	 * insert HOLD_WAFER
	 */
	@Override
	public void insertHoldWafers(List<HoldWafer> inHoldWafers, String inUserId, Date inNowTime, String inClassName, String inActionName, String inOrderNumber, List<Hold> inHolds, List<OrderLineLotno> inOrderLineLotNos) {
		if (inHoldWafers.size()>0){
			for (int i=0;i<inHoldWafers.size();i++){
				List<WaferInspResultLogsImport> waferInspResultsList=new ArrayList<WaferInspResultLogsImport>();
				try {
					String sql="SELECT HOLD_IDX FROM ORDER_LINE_LOTNO a, HOLD b " +
					           "WHERE a.ORDER_NUMBER=b.ORDER_NUMBER and a.PO_ITEM=b.PO_ITEM and a.CUSTOMER_LOTNO=b.CUSTOMER_LOTNO " +
							   "AND b.CUSTOMER_LOTNO = :CUSTOMER_LOTNO  "; 

					log.debug(sql);
					Query query = this.getDao().createSQLQuery(sql);
					query.setParameter("CUSTOMER_LOTNO", inHoldWafers.get(i).getCustomerLotno());					
					
					List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
					for(int j = 0; j < tmpList.size(); j++ ){
						Map<String,Object> row = (Map<String,Object>)tmpList.get(j);
						inHoldWafers.get(i).setHoldIdx((Integer) row.get("HOLD_IDX"));
			
					}
				} catch (Exception e) {
					log.debug(e.getLocalizedMessage());	
					e.printStackTrace();
				}
				this.getHoldWaferDao().create(inHoldWafers.get(i));
				this.createLogging(inClassName, "HOLD_WAFER", inActionName, inHoldWafers.get(i).toString(), inUserId, inNowTime);
			}
		}
	}

	/**
	 * update HOLD_WAFER
	 */
	@Override
	public void updateHoldWafers(List<HoldWafer> inHoldWafers, String inUserId, Date inNowTime, String inClassName, String inActionName, String inOrderNumber, List<Hold> inHolds, List<OrderLineLotno> inOrderLineLotNos) {
		if (inHoldWafers.size()>0){
			for (int i=0;i<inHoldWafers.size();i++){
				List<HoldWafer> holdWafers = (List<HoldWafer>)this.getHoldWaferDao()
						.createQuery("SELECT l FROM HoldWafer l WHERE holdWaferIdx=:holdWaferIdx AND holdIdx=:holdIdx ")
						.setParameter("holdIdx", inHoldWafers.get(i).getHoldIdx())
						.setParameter("holdWaferIdx", inHoldWafers.get(i).getHoldWaferIdx())
						.list();
				if(holdWafers.size() > 0){
					if(inHoldWafers.get(i).getLockComment() == null){
						inHoldWafers.get(i).setLockComment("");
					}
					if(inHoldWafers.get(i).getHoldProcessName() == null){
						inHoldWafers.get(i).setHoldProcessName("");
					}
					if(inHoldWafers.get(i).isReleaseFlag() == holdWafers.get(0).isReleaseFlag()){
						if(!holdWafers.get(0).getHoldComment().equals(inHoldWafers.get(i).getHoldComment()) || !holdWafers.get(0).getHoldProcessName().equals(inHoldWafers.get(i).getHoldProcessName()) || !holdWafers.get(0).getHoldReason().equals(inHoldWafers.get(i).getHoldReason()) || !holdWafers.get(0).getHoldType().equals(inHoldWafers.get(i).getHoldType()) || !holdWafers.get(0).getLockComment().equals(inHoldWafers.get(i).getLockComment())){
							if(!holdWafers.get(0).isReleaseFlag()){
								HoldWafer tmpHoldWafer = holdWafers.get(0);
								tmpHoldWafer.setCustomerLotno(inHoldWafers.get(i).getCustomerLotno());
								tmpHoldWafer.setCustomerJob(inHoldWafers.get(i).getCustomerJob());
								tmpHoldWafer.setWaferNo(inHoldWafers.get(i).getWaferNo());
								if(inHoldWafers.get(i).getHoldComment() != null && !"".equals(inHoldWafers.get(i).getHoldComment())){
									tmpHoldWafer.setHoldComment(inHoldWafers.get(i).getHoldComment());
								}
								if(inHoldWafers.get(i).getHoldDept() != null && !"".equals(inHoldWafers.get(i).getHoldDept())){
									tmpHoldWafer.setHoldDept(inHoldWafers.get(i).getHoldDept());
								}
								if(inHoldWafers.get(i).getHoldIssueDate() != null && !"".equals(inHoldWafers.get(i).getHoldIssueDate())){
									tmpHoldWafer.setHoldIssueDate(inHoldWafers.get(i).getHoldIssueDate());
								}
								//if(inHoldWafers.get(i).getHoldProcessName() != null && !"".equals(inHoldWafers.get(i).getHoldProcessName())){
								tmpHoldWafer.setHoldProcessName(inHoldWafers.get(i).getHoldProcessName());
								//}
								if(inHoldWafers.get(i).getHoldReason() != null && !"".equals(inHoldWafers.get(i).getHoldReason())){
									tmpHoldWafer.setHoldReason(inHoldWafers.get(i).getHoldReason());
								}
								if(inHoldWafers.get(i).getHoldType() != null && !"".equals(inHoldWafers.get(i).getHoldType())){
									tmpHoldWafer.setHoldType(inHoldWafers.get(i).getHoldType());
								}
								if(inHoldWafers.get(i).getHoldUser() != null && !"".equals(inHoldWafers.get(i).getHoldUser())){
									tmpHoldWafer.setHoldUser(inHoldWafers.get(i).getHoldUser());
								}
								tmpHoldWafer.setReleaseDate(inHoldWafers.get(i).getReleaseDate());
								tmpHoldWafer.setReleaseFlag(inHoldWafers.get(i).isReleaseFlag());
								tmpHoldWafer.setReleaseUser(inHoldWafers.get(i).getReleaseUser());
								//if(inHoldWafers.get(i).getLockComment() != null && !"".equals(inHoldWafers.get(i).getLockComment())){
								tmpHoldWafer.setLockComment(inHoldWafers.get(i).getLockComment());
								//}
								if(inHoldWafers.get(i).getB2bHoldreleaseFlag() != null && !"".equals(inHoldWafers.get(i).getB2bHoldreleaseFlag())){
									tmpHoldWafer.setB2bHoldreleaseFlag(inHoldWafers.get(i).getB2bHoldreleaseFlag());
								}


								this.getHoldWaferDao().update(tmpHoldWafer);
								this.createLogging(inClassName, "HOLD_WAFER", LogType.MODIFY, "", (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
							}
						}
					}else{
						//if(!holdWafers.get(0).getHoldComment().equals(inHoldWafers.get(i).getHoldComment()) || !holdWafers.get(0).getHoldProcessName().equals(inHoldWafers.get(i).getHoldProcessName()) || !holdWafers.get(0).getHoldReason().equals(inHoldWafers.get(i).getHoldReason()) || !holdWafers.get(0).getHoldType().equals(inHoldWafers.get(i).getHoldType()) || !holdWafers.get(0).getLockComment().equals(inHoldWafers.get(i).getLockComment()) || !holdWafers.get(0).isReleaseFlag() == inHoldWafers.get(i).isReleaseFlag()){
						HoldWafer tmpHoldWafer = holdWafers.get(0);
						tmpHoldWafer.setCustomerLotno(inHoldWafers.get(i).getCustomerLotno());
						tmpHoldWafer.setCustomerJob(inHoldWafers.get(i).getCustomerJob());
						tmpHoldWafer.setWaferNo(inHoldWafers.get(i).getWaferNo());
						if(inHoldWafers.get(i).getHoldComment() != null && !"".equals(inHoldWafers.get(i).getHoldComment())){
							tmpHoldWafer.setHoldComment(inHoldWafers.get(i).getHoldComment());
						}
						if(inHoldWafers.get(i).getHoldDept() != null && !"".equals(inHoldWafers.get(i).getHoldDept())){
							tmpHoldWafer.setHoldDept(inHoldWafers.get(i).getHoldDept());
						}
						if(inHoldWafers.get(i).getHoldIssueDate() != null && !"".equals(inHoldWafers.get(i).getHoldIssueDate())){
							tmpHoldWafer.setHoldIssueDate(inHoldWafers.get(i).getHoldIssueDate());
						}
						//if(inHoldWafers.get(i).getHoldProcessName() != null && !"".equals(inHoldWafers.get(i).getHoldProcessName())){
						tmpHoldWafer.setHoldProcessName(inHoldWafers.get(i).getHoldProcessName());
						//}
						if(inHoldWafers.get(i).getHoldReason() != null && !"".equals(inHoldWafers.get(i).getHoldReason())){
							tmpHoldWafer.setHoldReason(inHoldWafers.get(i).getHoldReason());
						}
						if(inHoldWafers.get(i).getHoldType() != null && !"".equals(inHoldWafers.get(i).getHoldType())){
							tmpHoldWafer.setHoldType(inHoldWafers.get(i).getHoldType());
						}
						if(inHoldWafers.get(i).getHoldUser() != null && !"".equals(inHoldWafers.get(i).getHoldUser())){
							tmpHoldWafer.setHoldUser(inHoldWafers.get(i).getHoldUser());
						}
						log.debug(inHoldWafers.get(i).isReleaseFlag());
						tmpHoldWafer.setReleaseDate(inHoldWafers.get(i).getReleaseDate());
						tmpHoldWafer.setReleaseFlag(inHoldWafers.get(i).isReleaseFlag());
						tmpHoldWafer.setReleaseUser(inHoldWafers.get(i).getReleaseUser());
						//if(inHoldWafers.get(i).getLockComment() != null && !"".equals(inHoldWafers.get(i).getLockComment())){
						tmpHoldWafer.setLockComment(inHoldWafers.get(i).getLockComment());
						//}
						if(inHoldWafers.get(i).getB2bHoldreleaseFlag() != null && !"".equals(inHoldWafers.get(i).getB2bHoldreleaseFlag())){
							tmpHoldWafer.setB2bHoldreleaseFlag(inHoldWafers.get(i).getB2bHoldreleaseFlag());
						}


						this.getHoldWaferDao().update(tmpHoldWafer);
						this.createLogging(inClassName, "HOLD_WAFER", LogType.MODIFY, "", (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
					//}
					}
				}
			}
		}
	}

	/**
	 * 2014.10.28 依據PO_NUMBER找出ORDER_HEADER.CANCEL_FLAG=0
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderHeaderByPoNumber()
	 */
	@Override
	public OrderHeader getOrderHeaderByPoNumber(String inPONumber) {
		List<OrderHeader> datas=new ArrayList<OrderHeader>();
		String hql="select c from OrderHeader c where poNumber=:poNumber and cancelFlag=0 ";
		try {
			datas=this.getOrderHeaderDao().createQuery(hql)
					.setParameter("poNumber", inPONumber)
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
	}
	
	/**
	 * 依據orderNumber找出LotInfo,並排除deleteFlag=1的資料
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getLotInfoByOrderNumber(java.lang.String)
	 */
	@Override
	public List<LotInfo> getLotInfoByOrderNumber(String inOrderNumber) {
		List<LotInfo> datas=new ArrayList<LotInfo>();
		try {
			String hql="select c from LotInfo c where deleteFlag=0 and orderNumber=:orderNumber order by updateDate ";
			datas=this.getLotInfoDao().createQuery(hql)
					.setParameter("orderNumber", inOrderNumber)
					.list();
		} catch (Exception e) {
			
		}
		return datas;
	}
	
	/**
	 * 依據lotNo找出LotResult,並排除deleteFlag=1的資料
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getLotResultByLotNo(java.lang.String)
	 */
	@Override
	public List<LotResult> getLotResultByLotNo(List<LotInfo> inLotInfos) {
		List<LotResult> datas=new ArrayList<LotResult>();
		if (inLotInfos.size()>0){
			for (int i=0;i<inLotInfos.size();i++){
				List<LotResult> lotResults = (List<LotResult>)this.getLotResultDao()
						.createQuery("SELECT l FROM LotResult l WHERE deleteFlag=0 and lotNo=:lotNo ")
						.setParameter("lotNo", inLotInfos.get(i).getLotNo())
						.list();
				if(lotResults.size() > 0){
					for(int j=0; j<lotResults.size(); j++){
						LotResult tmpLotResult = lotResults.get(j);
						datas.add(tmpLotResult);
					}
				}
			}
		}
		return datas;
	}
	
	/**
	 * UPDATE LOT_INFO
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateLotInfos(com.tce.ivision.model.WaferStatus)
	 */
	@Override
	public void updateLotInfos(LotInfo inLotInfos,String inUserId, Date inNowTime, String inClassName, String inActionName) {
		this.getLotInfoDao().update(inLotInfos);
		this.createLogging(inClassName, "LotInfo", inActionName, inLotInfos.toString(), inUserId, inNowTime);
	}
	
	
	/**
	 * UPDATE LOT_RESULT
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateLotResults(com.tce.ivision.model.WaferStatus)
	 */
	@Override
	public void updateLotResults(LotResult inResults,String inUserId, Date inNowTime, String inClassName, String inActionName) {
		this.getLotResultDao().update(inResults);
		this.createLogging(inClassName, "LotResult", inActionName, inResults.toString(), inUserId, inNowTime);
	}
	
	/**
	 * 讀取HOLD_WAFER BY HOLD_IDX+WAFER_NO
	 * */
	@Override
	public HoldWafer getHoldWaferByHoldIdxAndWaferNo(int holdIdx, String inWaferNo) {
		List<HoldWafer> datas=new ArrayList<HoldWafer>();
		String hql="select c from HoldWafer c where holdIdx=:holdIdx and waferNo=:waferNo "+
		           "order by holdIssueDate desc";
		try {
			datas=this.getHoldDao().createQuery(hql)
					.setParameter("holdIdx", holdIdx)
					.setParameter("waferNo", inWaferNo)
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
	}
	
	/**
	 * 用inCustomerLotNo找出WAFER_BANKIN

	 */
	@Override
	public List<WaferBankin> getWaferBankinByCustomerLotNo(String inCustomerLotNo) {
		List<WaferBankin> datas=new ArrayList<WaferBankin>();
		try {
			String hql="select c from WaferBankin c where customerLotno=:customerLotno and close_flag=0 ";
			datas=this.getWaferBankinDao().createQuery(hql)
					.setParameter("customerLotno", inCustomerLotNo)
					.list();
		} catch (Exception e) {
			
		}
		return datas;
	}
	
	/**
	 * 用inCustomerLotNo找出WAFER_INFO

	 */
	@Override
	public List<WaferInfo> getWaferInfoByCustomerLotNoAndLotNo(String inCustomerLotNo, String inLotNo) {
		List<WaferInfo> datas=new ArrayList<WaferInfo>();
		try {
			String hql="select c from WaferInfo c where customerLotno=:customerLotno and lotNo=:lotNo order by waferNo ";
			datas=this.getWaferInfoDao().createQuery(hql)
					.setParameter("customerLotno", inCustomerLotNo)
					.setParameter("lotNo", inLotNo)
					.list();
		} catch (Exception e) {
			
		}
		return datas;
	}
	
	
	/**
	 * 依據orderNumber找出LotInfo,並排除deleteFlag=1的資料
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getLotInfoByOrderNumber(java.lang.String)
	 */
	@Override
	public List<LotInfo> getLotInfoByOrderNumberAndCustomerLotNo(String inOrderNumber, String inCustomerLotNo) {
		List<LotInfo> datas=new ArrayList<LotInfo>();
		try {
			String hql="select c from LotInfo c where deleteFlag=0 and orderNumber=:orderNumber and (customerLotno1=:customerLotno1 or customerLotno2=:customerLotno2 or customerLotno3=:customerLotno3 or customerLotno4=:customerLotno4 or customerLotno5=:customerLotno5 or customerLotno6=:customerLotno6 or customerLotno7=:customerLotno7 or customerLotno8=:customerLotno8 or customerLotno9=:customerLotno9 or customerLotno10=:customerLotno10 or customerLotno11=:customerLotno11 or customerLotno12=:customerLotno12 or customerLotno13=:customerLotno13 or customerLotno14=:customerLotno14 or customerLotno15=:customerLotno15 or customerLotno16=:customerLotno16 or customerLotno17=:customerLotno17 or customerLotno18=:customerLotno18 or customerLotno19=:customerLotno19 or customerLotno20=:customerLotno20 or customerLotno21=:customerLotno21 or customerLotno22=:customerLotno22 or customerLotno23=:customerLotno23 or customerLotno24=:customerLotno24 or customerLotno25=:customerLotno25) order by updateDate ";
			log.debug(hql);
			datas=this.getLotInfoDao().createQuery(hql)
					.setParameter("orderNumber", inOrderNumber)
					.setParameter("customerLotno1", inCustomerLotNo)
					.setParameter("customerLotno2", inCustomerLotNo)
					.setParameter("customerLotno3", inCustomerLotNo)
					.setParameter("customerLotno4", inCustomerLotNo)
					.setParameter("customerLotno5", inCustomerLotNo)
					.setParameter("customerLotno6", inCustomerLotNo)
					.setParameter("customerLotno7", inCustomerLotNo)
					.setParameter("customerLotno8", inCustomerLotNo)
					.setParameter("customerLotno9", inCustomerLotNo)
					.setParameter("customerLotno10", inCustomerLotNo)
					.setParameter("customerLotno11", inCustomerLotNo)
					.setParameter("customerLotno12", inCustomerLotNo)
					.setParameter("customerLotno13", inCustomerLotNo)
					.setParameter("customerLotno14", inCustomerLotNo)
					.setParameter("customerLotno15", inCustomerLotNo)
					.setParameter("customerLotno16", inCustomerLotNo)
					.setParameter("customerLotno17", inCustomerLotNo)
					.setParameter("customerLotno18", inCustomerLotNo)
					.setParameter("customerLotno19", inCustomerLotNo)
					.setParameter("customerLotno20", inCustomerLotNo)
					.setParameter("customerLotno21", inCustomerLotNo)
					.setParameter("customerLotno22", inCustomerLotNo)
					.setParameter("customerLotno23", inCustomerLotNo)
					.setParameter("customerLotno24", inCustomerLotNo)
					.setParameter("customerLotno25", inCustomerLotNo)
					.list();
		} catch (Exception e) {
			
		}
		return datas;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOeReworkCountSetupByCustomerId(java.lang.String)
	 */
	@Override
	public List<OeReworkCountSetup> getOeReworkCountSetupByCustomerId(String inCustomerId) {
		Criteria criteria = this.getOeReworkCountSetupDao().createCriteria(OeReworkCountSetup.class);
		criteria.add(Restrictions.eq("customerId", inCustomerId));
		criteria.add(Restrictions.eq("enableSetup", true));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderInternalCheckInfoByOrderLineLotNoIdx(int)
	 */
	@Override
	public List<OrderInternalCheckInfo> getOrderInternalCheckInfoByOrderLineLotNoIdx(int inOrderLineLotNoIdx) {
		Criteria criteria = this.getOrderInternalCheckInfoDao().createCriteria(OrderInternalCheckInfo.class);
		criteria.createCriteria("orderLineLotno").add(Restrictions.eq("OrderLineLotnoIdx", inOrderLineLotNoIdx));
		criteria.addOrder(Order.desc("createDate"));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderInternalCheckInfoByOrderNumberAndCustomerLotNo(java.lang.String, java.lang.String)
	 */
	@Override
	public List<OrderInternalCheckInfo> getOrderInternalCheckInfoCheckRuleByOrderNumberAndCustomerLotNo(String inOrderNumber, String inCustomerLotNo) {
		Criteria criteria = this.getOrderInternalCheckInfoDao().createCriteria(OrderInternalCheckInfo.class);
		criteria.add(Restrictions.isNull("oeNoticeCreateDate"));
		criteria.add(Restrictions.eq("orderNumber", inOrderNumber)).add(Restrictions.eq("customerLotno", inCustomerLotNo));
		criteria.addOrder(Order.asc("createDate"));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderInternalCheckInfo(java.util.List)
	 */
	@Override
	public void updateOrderInternalCheckInfo(List<OrderInternalCheckInfo> inOrderInternalCheckInfoList) {
		if(inOrderInternalCheckInfoList != null && inOrderInternalCheckInfoList.size() > 0){
			for(int i=0; i<inOrderInternalCheckInfoList.size(); i++){
				this.getOrderInternalCheckInfoDao().update(inOrderInternalCheckInfoList.get(i));
			}
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#insertOrderInternalCheckInfo(java.util.List)
	 */
	@Override
	public void insertOrderInternalCheckInfo(List<OrderInternalCheckInfo> inOrderInternalCheckInfoList) {
		if(inOrderInternalCheckInfoList != null && inOrderInternalCheckInfoList.size() > 0){
			for(int i=0; i<inOrderInternalCheckInfoList.size(); i++){
				this.getOrderInternalCheckInfoDao().create(inOrderInternalCheckInfoList.get(i));
			}
		}		
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderInternalCheckInfoNoticeByOrderNumberAndCustomerLotNo(java.lang.String, java.lang.String)
	 */
	@Override
	public List<OrderInternalCheckInfo> getOrderInternalCheckInfoNoticeByOrderNumberAndCustomerLotNo(String inOrderNumber, String inCustomerLotNo) {
		Criteria criteria = this.getOrderInternalCheckInfoDao().createCriteria(OrderInternalCheckInfo.class);
		criteria.add(Restrictions.isNotNull("oeNoticeCreateDate"));
		criteria.add(Restrictions.eq("orderNumber", inOrderNumber)).add(Restrictions.eq("customerLotno", inCustomerLotNo));
		criteria.addOrder(Order.desc("createDate"));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateOrderInternalCheckInfoModifyReason(java.util.List)
	 */
	@Override
	public void updateOrderInternalCheckInfoModifyReason(List<OrderInternalCheckInfo> inOrderInternalCheckInfoList) {
		if(inOrderInternalCheckInfoList != null && inOrderInternalCheckInfoList.size() > 0){
			for(int i=0; i<inOrderInternalCheckInfoList.size(); i++){
				this.getOrderInternalCheckInfoDao().update(inOrderInternalCheckInfoList.get(i));
			}
		}	
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getWaferBankinAndWaferBankinWaferByCustomerLotNo(java.lang.String)
	 */
	@Override
	public List<WaferBankin> getWaferBankinAndWaferBankinWaferByCustomerLotNoAndCustomerId(List<String> inCustomerLotNoList,  String inCustomerId) {
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);

		criteria.add(Restrictions.eq("closeFlag", false));
		criteria.add(Restrictions.eq("waferReceiveFlag", true));
		criteria.add(Restrictions.eq("waferOutFlag", false));
		criteria.add(Restrictions.eq("customerId", inCustomerId));
		criteria.add(Restrictions.in("customerLotno", inCustomerLotNoList));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateWaferBankinAndWafers(java.util.List)
	 */
	@Override
	public void updateWaferBankinAndWafers(List<WaferBankin> inWaferBankinList) {
		if(inWaferBankinList != null && inWaferBankinList.size() > 0){
			for(int i=0; i<inWaferBankinList.size(); i++){
				if(inWaferBankinList.get(i).getWaferBankinWafers().size() > 0){
					for(int j=0; j<inWaferBankinList.get(i).getWaferBankinWafers().size(); j++){
						this.getWaferBankinWaferDao().update(inWaferBankinList.get(i).getWaferBankinWafers().get(j));
					}
				}
			}
		}		
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getWaferBankinWaferByOrderNumber(java.lang.String)
	 */
	@Override
	public List<WaferBankinWafer> getWaferBankinWaferByOrderNumber(String inOrderNumber) {
		Criteria criteria = this.getWaferBankinWaferDao().createCriteria(WaferBankinWafer.class);

		criteria.add(Restrictions.eq("closeFlag", false));
		criteria.add(Restrictions.eq("orderNumber", inOrderNumber));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#updateWaferBankinWafers(java.util.List)
	 */
	@Override
	public void updateWaferBankinWafers(List<WaferBankinWafer> inWaferBankinWaferList) {
		if(inWaferBankinWaferList != null && inWaferBankinWaferList.size() > 0){
			for(int i=0; i<inWaferBankinWaferList.size(); i++){
				this.getWaferBankinWaferDao().update(inWaferBankinWaferList.get(i));
			}
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOrderInternalCheckInfoByOrderLineLotNos(java.util.List)
	 */
	@Override
	public List<OrderInternalCheckInfo> getOrderInternalCheckInfoByOrderLineLotNos(List<Integer> inOrderLineLotnoIdxs) {
		Criteria criteria = this.getOrderInternalCheckInfoDao().createCriteria(OrderInternalCheckInfo.class);
		criteria.createCriteria("orderLineLotno").add(Restrictions.in("OrderLineLotnoIdx", inOrderLineLotnoIdxs));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#saveOrderHeaders(java.lang.Object[])
	 */
	@Override
	public OrderHeader saveOrderHeaders(OrderHeader inOrderHeader) {
		OrderHeader saveOrderHeader = this.createOrderHeader(inOrderHeader);
		this.createLogging("com.tce.ivision.modules.oe.ctrl", "OrderHeader", "ADD", inOrderHeader.toString(), (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
		
		return saveOrderHeader;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#saveEntityIds(com.tce.ivision.model.OrderHeader)
	 */
	@Override
	public String saveEntityIds(OrderLineLotno inOrderLineLotNo, Date inNowTime) {
		inOrderLineLotNo.setEntityId("");
		String empEntityId = "";
		if ("".equals(inOrderLineLotNo.getEntityId())){
			empEntityId=this.createEntityId(inNowTime);			
		}
		
		return empEntityId;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#deleteOrderHeaders(com.tce.ivision.model.OrderHeader)
	 */
	@Override
	public void deleteOrderHeaders(OrderHeader inOrderHeader) {
		this.getOrderHeaderDao().delete(inOrderHeader);
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#saveOrderLine(com.tce.ivision.model.OrderLine, java.lang.String, java.util.Date, java.lang.String, java.lang.String)
	 */
	@Override
	public void saveOrderLine(OrderLine inOrderLines, String inUserId,Date inNowTime, String inClassName, String inActionName) {
		this.getOrderLineDao().create(inOrderLines);
		this.createLogging(inClassName, "OrderLine", inActionName, inOrderLines.toString(), inUserId, inNowTime);
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.version:XQ181004
	 * version:XQ181004 add by will 20181110 往DB中插入RMA
	 */
	@Override
	public void saveReworkFlag(TsesOvtRmaLot tsesOvtRmaLot) {
		this.getReworkFlagDao().create(tsesOvtRmaLot);
	}
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#deleteOrderLines(com.tce.ivision.model.OrderLine)
	 */
	@Override
	public void deleteOrderLines(OrderLine inOrderLine) {
		this.getOrderLineDao().delete(inOrderLine);		
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getWaferBankinByCustomerLotNoAndCustomerId(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WaferBankin> getWaferBankinByCustomerLotNoAndCustomerId(String inCustomerLotNo, String inCustomerId) {
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);

		criteria.add(Restrictions.eq("closeFlag", false));
		criteria.add(Restrictions.eq("waferReceiveFlag", true));
		criteria.add(Restrictions.eq("customerId", inCustomerId));
		criteria.add(Restrictions.eq("customerLotno", inCustomerLotNo));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OrderEntryService#getOeDatasByOrderNumber(java.lang.String)
	 */
	@Override
	public OeOrderNoConfirmModel getOeDatasByOrderNumber(String inOrderNumber, String inCustomerLotNo) {
		OeOrderNoConfirmModel data = new OeOrderNoConfirmModel();
		
		String sql=" SELECT CUSTOMER_LOTNO, PRODUCT, CUSTOMER_JOB FROM VW_ORDER_HEADER_LINE_LINELOTNO WHERE ORDER_NUMBER=:ORDER_NUMBER AND CUSTOMER_LOTNO=:CUSTOMER_LOTNO AND (ORDER_STATUS = '10' OR ORDER_STATUS = '20') ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("ORDER_NUMBER", inOrderNumber);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			
			data.setCustomerJob((String) row.get("CUSTOMER_JOB"));
			data.setCustomerLotNo(inCustomerLotNo);
			data.setProduct((String) row.get("PRODUCT"));
		}
		
		return data;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<UiFieldSet> getUiFieldSet(String inClassName, String inParaType) {
		List<UiFieldSet> datas=new ArrayList<UiFieldSet>();
		try {
			String hql="select c from UiFieldSet c where className=:className and paraType=:paraType ";
			datas=this.getUiFieldParamDao().createQuery(hql)
					.setParameter("className", inClassName)
					.setParameter("paraType", inParaType)
					.list();
		} catch (Exception e) {
			// TODO: handle exception
		}
		//log.debug(datas.get(0).getParaValue());
		return datas;
	}	
	
	/**
	 * 取出PRODUCT_NAME_SETUP.INTERN_PRODUCT名稱	 * 
	 */	
	@Override
	public ProductNameSetup getInternalProdcut(String inCustomerId, String inProduct){		
		List<ProductNameSetup> datas=new ArrayList<ProductNameSetup>();
		String hql="select c from ProductNameSetup c where customerId=:customerId and product=:product and cancelFlag=0 ";
		log.debug(hql);
		try {
			datas=this.getOrderHeaderDao().createQuery(hql)
					.setParameter("customerId", inCustomerId)
					.setParameter("product", inProduct)					
					.list();
		} catch (Exception e) {
			
		}
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
	}

	///**
	// * 2017.12.20 利用PO_NUMBER條件(目前程式寫法ORDER_NUMBER=PO_NUMBER) 取出ORDER_HEADER.REAL_PRODUCT 
	// */	
	//@Override
	//public String getRealProduct(String inPoNumber) {
	//	List<OrderHeader> datas=new ArrayList<OrderHeader>();
	//	String hql="select c from OrderHeader c where cancelFlag=0 and poNumber=:poNumber ";
	//	log.debug(hql);
	//	try {
	//		datas=this.getOrderHeaderDao().createQuery(hql)
	//				.setParameter("poNumber", inPoNumber)
	//				.list();
	//	} catch (Exception e) {
	//		
	//	}
	//	
	//	if (datas.size()>0){
	//		return datas.get(0).getRealProduct();
	//	}
	//	else{
	//		return "";
	//	}		
	//}

}
