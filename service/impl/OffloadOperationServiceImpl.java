/*
 * Project Name:iVision
 * File Name:OffloadOperationServiceImpl.java
 * Package Name:com.tce.ivision.modules.cus.service.impl
 * Date:2014/12/02 上午11:57:24
 * 
 * 說明:
 * OrderScheduling部份的service實作
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */

package com.tce.ivision.modules.oe.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.zkoss.zk.ui.Sessions;

import com.tce.ivision.model.ExchangeRateOffload;
import com.tce.ivision.model.LotInfo;
import com.tce.ivision.model.LotResult;
import com.tce.ivision.model.OffloadLotno;
import com.tce.ivision.model.OffloadShipping;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.model.Shipping;
import com.tce.ivision.model.ShippingDetail;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferBankinWafer;
import com.tce.ivision.model.WaferInfo;
import com.tce.ivision.model.WaferManagementHistory;
import com.tce.ivision.modules.base.service.impl.BaseServiceImpl;
import com.tce.ivision.modules.oe.model.OffloadOperation;
import com.tce.ivision.modules.oe.model.OffloadOperationParameter;
import com.tce.ivision.modules.oe.model.OffloadShippingConfirm;
import com.tce.ivision.modules.oe.model.OffloadWaferConfirm;
import com.tce.ivision.modules.oe.model.OrderScheduling;
import com.tce.ivision.modules.oe.model.OrderSchedulingParameter;
import com.tce.ivision.modules.oe.service.OffloadOperationService;
import com.tce.ivision.modules.oe.service.OrderSchedulingService;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.DateUtil;
import com.tce.ivision.units.common.LogType;

/**
 * ClassName: OffloadOperationServiceImpl <br/>
 * date: 2014/12/02 上午11:57:24 <br/>
 *
 * @author Allison
 * @version 
 * @since JDK 1.6
 */
public class OffloadOperationServiceImpl extends BaseServiceImpl implements OffloadOperationService {
	/**
	 * log4j component
	 */
	public static Logger log = Logger.getLogger(OffloadOperationServiceImpl.class);
	/**
	 * 
	 * 取得符合條件的OffloadOperation List
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#getOffloadOperations(com.tce.ivision.modules.oe.model.OrderSchedulingParameter)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OffloadOperation> getOffloadOperations(OffloadOperationParameter inOffloadOperationParameter) {
		List<OffloadOperation> offloadOperationLists=new ArrayList<OffloadOperation>();
		try {
			//改為使用VIEW的方式搜尋
			//String sql=" SELECT * FROM VW_ORDER_DATA WHERE SUB_NAME IS NOT NULL AND SUB_NAME <> '' AND SUB_NAME NOT LIKE 'TCE%'  AND (ORDER_STATUS ='10' OR ORDER_STATUS='20') ";
			//OCF-PR-150202_將[ORDER_HEADER].ORDER_STATUS條件修改成ORDER_STATUS <> 40的都可秀出，若只有原本的10,20，則該ORDER若出貨超過24h被關帳，則此筆OFFLOAD的資料就會看不到了
			String sql=" SELECT * FROM VW_ORDER_DATA WHERE SUB_NAME IS NOT NULL AND SUB_NAME <> '' AND SUB_NAME NOT LIKE 'TCE%'  AND ORDER_STATUS <> '40' ";
			if(!"".equals(inOffloadOperationParameter.getCustomerId())){
				sql = sql + " AND CUSTOMER_ID = :customerId ";
			}
			if(!"".equals(inOffloadOperationParameter.getProduct()) ){
				sql = sql +" AND PRODUCT = :product ";
			}
			if(inOffloadOperationParameter.getBeginDate() != null &&
					inOffloadOperationParameter.getEndDate() != null ){
				sql = sql +" AND ORDER_DATE BETWEEN :beginDate AND :endDate ";
			}

			log.debug(sql);
			Query query = this.getDao().createSQLQuery(sql);
			
			if(!"".equals(inOffloadOperationParameter.getCustomerId())){
				query.setParameter("customerId", inOffloadOperationParameter.getCustomerId());
			}
			if(!"".equals(inOffloadOperationParameter.getProduct()) ){
				query.setParameter("product", inOffloadOperationParameter.getProduct());
			}
			if(inOffloadOperationParameter.getBeginDate() != null && 
					inOffloadOperationParameter.getEndDate() != null ){
				query.setParameter("beginDate", DateUtil.setTime(inOffloadOperationParameter.getBeginDate(), 0, 0, 0));
				query.setParameter("endDate", DateUtil.setTime(inOffloadOperationParameter.getEndDate(), 23, 59, 59));
			}
			
			List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			for(int i = 0; i < tmpList.size(); i++ ){
				Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
				OffloadOperation wip = new OffloadOperation();
				
				String sql2=" SELECT * FROM OFFLOAD_LOTNO WHERE ORDER_NUMBER=:ORDER_NUMBER AND CUSTOMER_LOTNO=:CUSTOMER_LOTNO ";
				
				Query query2 = this.getDao().createSQLQuery(sql2);
				query2.setParameter("ORDER_NUMBER", (String) row.get("ORDER_NUMBER"));
				query2.setParameter("CUSTOMER_LOTNO", (String) row.get("CUSTOMER_LOTNO"));
				
				List<Object> tmpList2 = query2.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
				if(tmpList2.size()>0){
					for(int j = 0; j < tmpList2.size(); j++ ){
						Map<String,Object> row2 = (Map<String,Object>)tmpList2.get(j);
						wip.setOffloadStatus((String) row2.get("OFFLOAD_STATUS"));
						wip.setOffloadType((String) row2.get("OFFLOAD_TYPE"));
						wip.setOffloadTo((String) row2.get("OFFLOAD_TO"));
						wip.setOffloadPo((String) row2.get("OFFLOAD_PO"));
						wip.setOffloadDueDate((Date) row2.get("OFFLOAD_DUEDATE"));
						wip.setConfirmDate((Date) row2.get("CONFIRM_DATE"));
						wip.setUpdateDate((Date) row2.get("UPDATE_DATE"));
						wip.setUpdateUser((String) row2.get("UPDATE_USER"));
						wip.setCancelDate((Date) row2.get("CANCEL_DATE"));
						wip.setCancelUser((String) row2.get("CANCEL_USER"));
						wip.setCancelReason((String) row2.get("CANCEL_REASON"));
						wip.setCancelFlag((Boolean) row2.get("CANCEL_FLAG"));
						wip.setNewFlag(false);
					}
				}else{
					wip.setNewFlag(true);
				}	
				
				wip.setCustomer(inOffloadOperationParameter.getCustomerName());
				wip.setOrderNumber((String) row.get("ORDER_NUMBER"));
				wip.setPoNo((String) row.get("BILLTO_PO"));
				wip.setProduct((String) row.get("PRODUCT"));
				wip.setCustomerLotNo((String) row.get("CUSTOMER_LOTNO"));
				wip.setWaferQty(String.valueOf((Integer) row.get("LOTNO_WAFER_QTY")));
				BigDecimal orderPrice = BigDecimal.valueOf((Double) row.get("UNIT_PRICE") * (Integer) row.get("LOTNO_WAFER_QTY"));
				wip.setOrderPrice(String.valueOf(orderPrice.setScale(2,RoundingMode.HALF_UP)));
				wip.setCurrency((String) row.get("CURRENCY"));
				wip.setWaferId((String) row.get("WAFER_DATA"));
				wip.setBillTo((String) row.get("BILL_TO"));
				wip.setShipTo((String) row.get("SHIP_TO"));
				wip.setPoItem((String) row.get("PO_ITEM"));
				wip.setPoNumber((String) row.get("PO_NUMBER"));
				
				if(inOffloadOperationParameter.isOffloadCloseFlag()){//若畫面上勾選了Offload Close，則Offload Status=30的也要秀出來
					offloadOperationLists.add(wip);
				}else{
					if(!"30".equals(wip.getOffloadStatus())){
						offloadOperationLists.add(wip);
					}
				}
				log.debug(wip.toString());
			}
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());	
			e.printStackTrace();
		}
		return offloadOperationLists;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateOrderHeader(String[] inOrderNumbers, String inOffloadType){
		List<OrderHeader> datas=new ArrayList<OrderHeader>();
		for(int i=0; i<inOrderNumbers.length; i++){
			String hql="select c from OrderHeader c where cancelFlag=0 and orderNumber=:orderNumber ";
			datas=this.getOrderHeaderDao().createQuery(hql)
					.setParameter("orderNumber", inOrderNumbers[i])
					.list();
		
			for(int j=0; j<datas.size(); j++){
				if("CONFIRM".equals(inOffloadType)){
					datas.get(j).setB2bDisableFlag(true);
					datas.get(j).setOrderStatus("20");
				}else{
					datas.get(j).setB2bDisableFlag(false);
					datas.get(j).setOrderStatus("10");
				}
				this.getOrderHeaderDao().update(datas.get(j));
				this.createLogging(this.getClass().getName(), "ORDER_HEADER", "UPDATE", datas.get(j), (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateOrderLineLotNo(OffloadOperation inOffloadOperationLists, String inOffloadType){
		List<OrderLineLotno> datas=new ArrayList<OrderLineLotno>();
		String hql="select c from OrderLineLotno c where cancelFlag=0 and orderNumber=:orderNumber and customerLotno=:customerLotno ";
		datas=this.getOrderLineLotnoDao().createQuery(hql)
				.setParameter("orderNumber", inOffloadOperationLists.getOrderNumber())
				.setParameter("customerLotno", inOffloadOperationLists.getCustomerLotNo())
				.list();
		for(int i=0; i<datas.size(); i++){
			if("CONFIRM".equals(inOffloadType)){
				datas.get(i).setOffloadFlag(true);
			}else{
				datas.get(i).setOffloadFlag(false);
			}
			this.getOrderLineLotnoDao().update(datas.get(i));
			this.createLogging(this.getClass().getName(), "ORDER_LINE_LOTNO", "UPDATE", datas.get(i), (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean saveOffloadLotNo(OffloadOperation inOffloadOperationLists, String inOffloadType){
		boolean saveFlag=false;
		List<OffloadLotno> datas=new ArrayList<OffloadLotno>();
		Date nowTime = new Date();
		String hql="select c from OffloadLotno c where cancelFlag=0 and orderNumber=:orderNumber and customerLotno=:customerLotno ";
		datas=this.getOffloadLotnoDao().createQuery(hql)
				.setParameter("orderNumber", inOffloadOperationLists.getOrderNumber())
				.setParameter("customerLotno", inOffloadOperationLists.getCustomerLotNo())
				.list();
		
		if(datas.size()>0){
			for(int i=0; i<datas.size(); i++){
				if("MODIFY".equals(inOffloadType)){
					//				datas.get(i).setOffloadStatus("20");
					//				datas.get(i).setOrderNumber(inOffloadOperationLists.getOrderNumber());
					//				datas.get(i).setCustomerLotno(inOffloadOperationLists.getCustomerLotNo());
					//				datas.get(i).setOffloadType(inOffloadOperationLists.getOffloadType());
					//				datas.get(i).setOffloadTo(inOffloadOperationLists.getOffloadTo());
					//				datas.get(i).setOffloadPo(inOffloadOperationLists.getOffloadPo());
					datas.get(i).setOffloadDuedate(inOffloadOperationLists.getOffloadDueDate());
					//datas.get(i).setConfirmDate(nowTime);
					datas.get(i).setUpdateDate(nowTime);
					datas.get(i).setUpdateUser(inOffloadOperationLists.getUpdateUser());
				}else if("CANCEL".equals(inOffloadType)){
					datas.get(i).setOffloadStatus("");
					datas.get(i).setUpdateDate(nowTime);
					datas.get(i).setUpdateUser(inOffloadOperationLists.getUpdateUser());
					datas.get(i).setCancelDate(nowTime);
					datas.get(i).setCancelFlag(true);
					datas.get(i).setCancelUser(inOffloadOperationLists.getUpdateUser());
					datas.get(i).setCancelReason(inOffloadOperationLists.getCancelReason());
				}
				this.getOffloadLotnoDao().update(datas.get(i));
				this.createLogging(this.getClass().getName(), "OFFLOAD_LOTNO", "UPDATE", datas.get(i), (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
			}
		}else{
			OffloadLotno offloadLotNo = new OffloadLotno();
			offloadLotNo.setOffloadStatus("20");
			offloadLotNo.setOrderNumber(inOffloadOperationLists.getOrderNumber());
			offloadLotNo.setCustomerLotno(inOffloadOperationLists.getCustomerLotNo());
			offloadLotNo.setOffloadType(inOffloadOperationLists.getOffloadType());
			offloadLotNo.setOffloadTo(inOffloadOperationLists.getOffloadTo());
			offloadLotNo.setOffloadPo(inOffloadOperationLists.getOffloadPo());
			offloadLotNo.setOffloadDuedate(inOffloadOperationLists.getOffloadDueDate());
			offloadLotNo.setConfirmDate(nowTime);
			offloadLotNo.setUpdateDate(nowTime);
			offloadLotNo.setUpdateUser(inOffloadOperationLists.getUpdateUser());
			
			this.getOffloadLotnoDao().create(offloadLotNo);
			this.createLogging(this.getClass().getName(), "OFFLOAD_LOTNO", "INSERT", offloadLotNo, (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
		}
		saveFlag=true;
		
		return saveFlag;
	}
	
	
	
	/**
	 * 
	 * 用customerId,orderStatus,orderDate為條件取出符合的PRODUCT清單
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#getProductWithStatusAndDate(boolean, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getProductWithStatusAndDateAndCustomer(String inCustomerId) {
		List<Object> tmps=new ArrayList<Object>();
		List<String> datas=new ArrayList<String>();
		try {
			String sql="SELECT DISTINCT PRODUCT FROM ORDER_HEADER " +
					"WHERE CANCEL_FLAG=0 AND CUSTOMER_ID=:CUSTOMER_ID AND (ORDER_STATUS = '10' OR ORDER_STATUS = '20') ";
			Query query = this.getDao().createSQLQuery(sql);
			query.setParameter("CUSTOMER_ID", inCustomerId);
			tmps=query.list();
			if (tmps.size()>0){
				for(Object obj:tmps){
					String data="";
					if (obj!=null){
						data=obj.toString();
					}
					datas.add(data);
				}
			}
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());	
			e.printStackTrace();
		}
		return datas;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<ExchangeRateOffload> getExchangeRateOffloadLists(String inYear) {
		List<ExchangeRateOffload> exchangeRateLists=new ArrayList<ExchangeRateOffload>();
		for(int i=1; i<13; i++){
			String month="";
			if(i<10){
				month="0"+String.valueOf(i);
			}else{
				month=String.valueOf(i);
			}
			String hql="select c from ExchangeRateOffload c where year=:year and month=:month ";
			List<ExchangeRateOffload> exchangeRateList=this.getExchangeRateOffloadDao().createQuery(hql)
					.setParameter("year", inYear)
					.setParameter("month", month)
					.list();
			
			ExchangeRateOffload exchangeRateOffload = new ExchangeRateOffload();
			exchangeRateOffload.setMonth(month);
			exchangeRateOffload.setYear(inYear);
			if(exchangeRateList.size()>0){
				if(exchangeRateList.get(0).getNtdRate().compareTo(new BigDecimal(0.000)) == 1){
					exchangeRateOffload.setNtdRate(exchangeRateList.get(0).getNtdRate());
				}else{
					exchangeRateOffload.setNtdRate(null);
				}
				if(exchangeRateList.get(0).getUsdRate().compareTo(new BigDecimal(0.000)) == 1){
					exchangeRateOffload.setUsdRate(exchangeRateList.get(0).getUsdRate());
				}else{
					exchangeRateOffload.setUsdRate(null);
				}
				exchangeRateOffload.setUpdateDate(exchangeRateList.get(0).getUpdateDate());
				exchangeRateOffload.setUpdateUser(exchangeRateList.get(0).getUpdateUser());
			}else{
				exchangeRateOffload.setNtdRate(null);
				exchangeRateOffload.setUsdRate(null);
				exchangeRateOffload.setUpdateDate(null);
				exchangeRateOffload.setUpdateUser("");
			}
			
			exchangeRateLists.add(exchangeRateOffload);
		}
		
		return exchangeRateLists;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public boolean saveExchangeRateOffload(List<ExchangeRateOffload> inExchangeRateOffloadLists) {
		Date nowTime=new Date();
		boolean saveFlag=false;
		for(int i=0; i<inExchangeRateOffloadLists.size(); i++){
			String hql="select c from ExchangeRateOffload c where year=:year and month=:month ";
			List<ExchangeRateOffload> datas=this.getExchangeRateOffloadDao().createQuery(hql)
					.setParameter("year", inExchangeRateOffloadLists.get(i).getYear())
					.setParameter("month", inExchangeRateOffloadLists.get(i).getMonth())
					.list();
			
			if(datas.size()>0){
				if(inExchangeRateOffloadLists.get(i).getNtdRate() == null){
					inExchangeRateOffloadLists.get(i).setNtdRate(BigDecimal.valueOf(Double.valueOf(0.000)));//若原本[EXCHANGE_RATE_OFFLOAD]有值，但後來都刪除，則帶0.000(Decimal不允許空白，塞null會造成錯誤)
				}
				if(inExchangeRateOffloadLists.get(i).getUsdRate() == null){
					inExchangeRateOffloadLists.get(i).setUsdRate(BigDecimal.valueOf(Double.valueOf(0.000)));//若原本[EXCHANGE_RATE_OFFLOAD]有值，但後來都刪除，則帶0.000(Decimal不允許空白，塞null會造成錯誤)
				}
				if(!datas.get(0).getNtdRate().equals(inExchangeRateOffloadLists.get(i).getNtdRate()) || !datas.get(0).getUsdRate().equals(inExchangeRateOffloadLists.get(i).getUsdRate())){
					datas.get(0).setNtdRate(inExchangeRateOffloadLists.get(i).getNtdRate());
					datas.get(0).setUsdRate(inExchangeRateOffloadLists.get(i).getUsdRate());
					datas.get(0).setUpdateDate(nowTime);
					datas.get(0).setUpdateUser(inExchangeRateOffloadLists.get(i).getUpdateUser());
					
					this.getExchangeRateOffloadDao().update(datas.get(0));
					saveFlag=true;
				}
			}else{
				if(inExchangeRateOffloadLists.get(i).getNtdRate()!= null && inExchangeRateOffloadLists.get(i).getUsdRate() != null){
					ExchangeRateOffload exchangeRateOffload = new ExchangeRateOffload();
					exchangeRateOffload.setMonth(inExchangeRateOffloadLists.get(i).getMonth());
					exchangeRateOffload.setYear(inExchangeRateOffloadLists.get(i).getYear());
					exchangeRateOffload.setNtdRate(inExchangeRateOffloadLists.get(i).getNtdRate());
					exchangeRateOffload.setUsdRate(inExchangeRateOffloadLists.get(i).getUsdRate());
					exchangeRateOffload.setUpdateDate(nowTime);
					exchangeRateOffload.setUpdateUser(inExchangeRateOffloadLists.get(i).getUpdateUser());

					this.getExchangeRateOffloadDao().create(exchangeRateOffload);
					saveFlag=true;
				}
			}
		}
		return saveFlag;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public OrderHeader getOrderHeader(String inOrderNumber) {
		String hql="select c from OrderHeader c where orderNumber=:orderNumber ";
		List<OrderHeader> datas=this.getOrderHeaderDao().createQuery(hql)
				.setParameter("orderNumber", inOrderNumber)
				.list();
		
		return datas.get(0);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public ExchangeRateOffload getExchangeRateOffload(){
		Date nowTime = new Date();
		String hql="select c from ExchangeRateOffload c where year=:year and month=:month ";
		List<ExchangeRateOffload> data=this.getExchangeRateOffloadDao().createQuery(hql)
				.setParameter("year", DateFormatUtil.getYearFormater().format(nowTime))
				.setParameter("month", DateFormatUtil.getMonthFormater().format(nowTime))
				.list();
		
		if(data.size() > 0){
			return data.get(0);
		}else{
			return null;
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<OffloadShippingConfirm> getOffloadShippingLists(int inOffloadLotnoIdx) {
		List<OffloadShippingConfirm> offloadShippingConfirmLists=new ArrayList<OffloadShippingConfirm>();
		try {
			//改為使用VIEW的方式搜尋
			String sql=" SELECT * FROM OFFLOAD_SHIPPING WHERE OFFLOAD_LOTNO_IDX=:OFFLOAD_LOTNO_IDX ";

			log.debug(sql);
			Query query = this.getDao().createSQLQuery(sql);
			query.setParameter("OFFLOAD_LOTNO_IDX", inOffloadLotnoIdx);
			
			List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			for(int i = 0; i < tmpList.size(); i++ ){
				Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
				OffloadShippingConfirm wip = new OffloadShippingConfirm();
				wip.setOffloadLotNoIdx((Integer) row.get("OFFLOAD_LOTNO_IDX"));
				wip.setOffloadShippingIdx((Integer) row.get("OFFLOAD_SHIPPING_IDX"));
				wip.setShippingIdx((Integer) row.get("SHIPPING_IDX"));
				wip.setPackingListType((String) row.get("PACKING_LIST_TYPE"));
				wip.setPackingListNumber((String) row.get("PACKING_LIST_NUMBER"));
				wip.setiNaviLotNo((String) row.get("INAVI_LOTNO"));
				wip.setShipQty((Integer) row.get("SHIP_QTY"));
				wip.setRmaQty((Integer) row.get("RMA_QTY"));
				wip.setScrapQty((Integer) row.get("SCRAP_QTY"));
				wip.setShippingWaferData((String) row.get("SHIPPING_WAFER_DATA"));
				wip.setRmaWaferData((String) row.get("RMA_WAFER_DATA"));
				wip.setScrapWaferData((String) row.get("SCRAP_WAFER_DATA"));
				wip.setShipDate((Date) row.get("SHIP_DATE"));
				wip.setConfirmDate((Date) row.get("CONFIRM_DATE"));
				wip.setConfirmUser((String) row.get("CONFIRM_USER"));
				wip.setRemark((String) row.get("REMARK"));
				wip.setIsConfirm(true);

				offloadShippingConfirmLists.add(wip);
				log.debug(wip.toString());
			}
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());	
			e.printStackTrace();
		}
		
		return offloadShippingConfirmLists;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public OffloadLotno getOffloadLotno(String inOrderNumber, String inCustomerLotno){
		Date nowTime = new Date();
		String hql="select c from OffloadLotno c where orderNumber=:orderNumber and customerLotno=:customerLotno and cancelFlag=0 ";
		List<OffloadLotno> data=this.getOffloadLotnoDao().createQuery(hql)
				.setParameter("orderNumber", inOrderNumber)
				.setParameter("customerLotno", inCustomerLotno)
				.list();
		
		if(data.size() > 0){
			return data.get(0);
		}else{
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean saveOffloadShipping(OffloadShippingConfirm inOffloadShippingConfirmLists){
		boolean saveFlag=false;
		Date nowTime = new Date();
		List<Shipping> shippingLists=new ArrayList<Shipping>();
		List<OffloadShipping> offloadShippingLists=new ArrayList<OffloadShipping>();
		
		//1. 先將資料存入[SHIPPING] & [SHIPPING_DETAIL]
		Shipping saveShipping = new Shipping();
		saveShipping.setCreateDate(nowTime);
		saveShipping.setPackingCfmDate(nowTime);
		saveShipping.setPackingCfmFlag(true);
		saveShipping.setPackingListNumber(inOffloadShippingConfirmLists.getPackingListNumber());
		saveShipping.setPackingListType(inOffloadShippingConfirmLists.getPackingListType());
		saveShipping.setPackingRemarkInternal(inOffloadShippingConfirmLists.getRemark());
		saveShipping.setBillTo(inOffloadShippingConfirmLists.getBillTo());
		saveShipping.setShipCfmOperator((String)Sessions.getCurrent().getAttribute("loginid"));
		saveShipping.setShipDate(inOffloadShippingConfirmLists.getShipDate());
		saveShipping.setShipToVendorName(inOffloadShippingConfirmLists.getShipTo());
		saveShipping.setShipToVendorAddress(inOffloadShippingConfirmLists.getShipToAddress());
		saveShipping.setUpdateDate(nowTime);
		saveShipping.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
		saveShipping.setPackingRemarkCustomer("");
		saveShipping.setPackingRemarkInternalOld("");
			
		this.getShippingDao().create(saveShipping);
		this.createLogging(this.getClass().getName(), "SHIPPING", "INSERT", saveShipping, (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
			
		ShippingDetail saveShippingDetail = new ShippingDetail();
		saveShippingDetail.setShipping(saveShipping);
		saveShippingDetail.setCustomerLotno(inOffloadShippingConfirmLists.getCustomerLotNo());
		saveShippingDetail.setLotNo(inOffloadShippingConfirmLists.getiNaviLotNo().substring(0,8));
		saveShippingDetail.setOcfLotNo(inOffloadShippingConfirmLists.getiNaviLotNo().substring(0,13));
		saveShippingDetail.setPoItem(inOffloadShippingConfirmLists.getPoItem());
		saveShippingDetail.setPoNumber(inOffloadShippingConfirmLists.getPoNumber());
		saveShippingDetail.setProduct(inOffloadShippingConfirmLists.getProduct());
		saveShippingDetail.setShipQty(inOffloadShippingConfirmLists.getShipQty());
		saveShippingDetail.setExchangeRate(inOffloadShippingConfirmLists.getExchangeRate());
		saveShippingDetail.setiNaviLotNo(inOffloadShippingConfirmLists.getiNaviLotNo());
		saveShippingDetail.setShippingPoNumber(inOffloadShippingConfirmLists.getBillToPo());
		saveShippingDetail.setRmaQty(inOffloadShippingConfirmLists.getRmaQty());
		saveShippingDetail.setScrapQty(inOffloadShippingConfirmLists.getScrapQty());
		saveShippingDetail.setBilltoPo(inOffloadShippingConfirmLists.getBillToPo());
			
		this.getShippingDetailDao().create(saveShippingDetail);		
		this.createLogging(this.getClass().getName(), "SHIPPING_DETAIL", "INSERT", saveShipping, (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
		
		String hql2="select c from OffloadShipping c where packingListNumber=:packingListNumber ";
		offloadShippingLists=this.getOffloadShippingDao().createQuery(hql2)
				.setParameter("packingListNumber", inOffloadShippingConfirmLists.getPackingListNumber())
				.list();
		
		if(offloadShippingLists.size()<=0){
			OffloadShipping offloadShipping = new OffloadShipping();
			offloadShipping.setOffloadLotNoIdx(inOffloadShippingConfirmLists.getOffloadLotNoIdx());
			offloadShipping.setShippingIdx(saveShipping.getShippingIdx());
			offloadShipping.setPackingListType(inOffloadShippingConfirmLists.getPackingListType());
			offloadShipping.setPackingListNumber(inOffloadShippingConfirmLists.getPackingListNumber());
			offloadShipping.setiNaviLotNo(inOffloadShippingConfirmLists.getiNaviLotNo());
			offloadShipping.setShipQty(inOffloadShippingConfirmLists.getShipQty());
			offloadShipping.setRmaQty(inOffloadShippingConfirmLists.getRmaQty());
			offloadShipping.setScrapQty(inOffloadShippingConfirmLists.getScrapQty());
			offloadShipping.setShippingWaferData(inOffloadShippingConfirmLists.getShippingWaferData());
			offloadShipping.setRmaWaferData(inOffloadShippingConfirmLists.getRmaWaferData());
			offloadShipping.setScrapWaferData(inOffloadShippingConfirmLists.getScrapWaferData());
			offloadShipping.setShipDate(inOffloadShippingConfirmLists.getShipDate());
			offloadShipping.setConfirmDate(nowTime);
			offloadShipping.setConfirmUser((String)Sessions.getCurrent().getAttribute("loginid"));
			offloadShipping.setRemark(inOffloadShippingConfirmLists.getRemark());
			
			this.getOffloadShippingDao().create(offloadShipping);
			this.createLogging(this.getClass().getName(), "OFFLOAD_SHIPPING", "INSERT", saveShipping, (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
		}
		saveFlag=true;
		
		return saveFlag;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void updateOffloadLotno(OffloadLotno inOffloadLotnos){
		Date nowtime = new Date();
		inOffloadLotnos.setOffloadStatus("30");
		inOffloadLotnos.setUpdateDate(nowtime);
		inOffloadLotnos.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
		this.getOffloadLotnoDao().update(inOffloadLotnos);
		this.createLogging(this.getClass().getName(), "OFFLOAD_LOTNO", "UPDATE", inOffloadLotnos, (String)Sessions.getCurrent().getAttribute("loginid"), nowtime);
	}


	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OffloadOperationService#updateWaferBankinsAndInsertManagementHistory(java.lang.String, java.lang.String)
	 */
	@Override
	public LinkedHashMap<String, String> updateWaferBankinsAndInsertManagementHistory(String inCustomerLotNo, String inUser, List<OffloadWaferConfirm> inOffloadWaferConfirmLists) {
		LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
		
		//OCF-PR-150202_新增Shipping(Packing List) Confirm後，需update [WAFER_BANKIN_WAFER] & insert記錄到 [WAFER_MANAGEMENT_HISTORY]，或update [WAFER_BANKIN]_Allison add
		Date nowtime = new Date();
		int countWaferOutFlag=0;//OCF-PR-150202_計算該CustomerLotNo底下所有WaferNo的[WAFER_BANKIN_WAFER].WAFER_OUT_FLAG=TRUE
		List<WaferBankin> waferBankinList = this.getWaferBankinLists(inCustomerLotNo);
		String composeCustomerLotNo="";
		String composeWaferData="";
		String composeFrom="";
		String composeTo="";
		
		if(waferBankinList.size() > 0){
			for(int j=0; j<waferBankinList.size(); j++){
				if(waferBankinList.get(j).getWaferBankinWafers().size() > 0){
					for(int k=0; k<waferBankinList.get(j).getWaferBankinWafers().size(); k++){
						for (int i=0;i<inOffloadWaferConfirmLists.size();i++){
							if(inOffloadWaferConfirmLists.get(i).getWaferNo().equals(waferBankinList.get(j).getWaferBankinWafers().get(k).getReceiveWaferNo()) && inOffloadWaferConfirmLists.get(i).getWaferConfirm() != null){
								String originalInventoryCode = waferBankinList.get(j).getWaferBankinWafers().get(k).getInventoryCode();
								if(!waferBankinList.get(j).getWaferBankinWafers().get(k).getCloseFlag() && !waferBankinList.get(j).getWaferBankinWafers().get(k).getWaferOutFlag() && waferBankinList.get(j).getWaferBankinWafers().get(k).getWaferReceiveFlag()){
									waferBankinList.get(j).getWaferBankinWafers().get(k).setUpdateDate(nowtime);
									waferBankinList.get(j).getWaferBankinWafers().get(k).setUpdateUser(inUser);
									waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferOutFlag(true);
									waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferInvCodeFrom(originalInventoryCode);
									waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferManagementComment("");
									waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferManagementUpdateDate(nowtime);
									waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferManagementUpdateUser(inUser);

									//INSERT一筆記錄到[WAFER_MANAGEMENT_HISTORY]
									WaferManagementHistory waferManagementHistory = new WaferManagementHistory();
									waferManagementHistory.setWaferBankin(waferBankinList.get(j));
									String[] splitWaferNo = waferBankinList.get(j).getWaferBankinWafers().get(k).getReceiveWaferNo().split("-");
									waferManagementHistory.setManagementWaferData(String.valueOf(Integer.valueOf(splitWaferNo[1])));
									waferManagementHistory.setWaferInvCodeFrom(originalInventoryCode);
									waferManagementHistory.setWaferManagementComment("");
									waferManagementHistory.setWaferManagementUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
									waferManagementHistory.setWaferManagementUpdateDate(new Date());
									waferManagementHistory.setCreateDate(new Date());

									if("RMA".equals(inOffloadWaferConfirmLists.get(i).getWaferConfirm())){
										waferBankinList.get(j).getWaferBankinWafers().get(k).setInventoryCode("S8");
										waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferInvCodeTo("S8");
										waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferManagementStatus(originalInventoryCode + " to " + "S8");

										waferManagementHistory.setWaferInvCodeTo("S8");
										waferManagementHistory.setWaferManagementStatus(originalInventoryCode + " to " + "S8");
										
										if(!composeFrom.contains(originalInventoryCode)){
											composeFrom = composeFrom + originalInventoryCode + ",";
										}
										if(!composeTo.contains("S8 (WAFEROUT / RMA)")){
											composeTo = composeTo  + "S8 (WAFEROUT / RMA)" + ",";
										}
									}else if("Scrap".equals(inOffloadWaferConfirmLists.get(i).getWaferConfirm())){
										waferBankinList.get(j).getWaferBankinWafers().get(k).setInventoryCode("S9");
										waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferInvCodeTo("S9");
										waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferManagementStatus(originalInventoryCode + " to " + "S9");

										waferManagementHistory.setWaferInvCodeTo("S9");
										waferManagementHistory.setWaferManagementStatus(originalInventoryCode + " to " + "S9");
										
										if(!composeFrom.contains(originalInventoryCode)){
											composeFrom = composeFrom + originalInventoryCode + ",";
										}
										if(!composeTo.contains("S9 (WAFEROUT / SCRAP)")){
											composeTo = composeTo + "S9 (WAFEROUT / SCRAP)" + ",";
										}
									}else{
										waferBankinList.get(j).getWaferBankinWafers().get(k).setInventoryCode("S6");
										waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferInvCodeTo("S6");
										waferBankinList.get(j).getWaferBankinWafers().get(k).setWaferManagementStatus(originalInventoryCode + " to " + "S6");

										waferManagementHistory.setWaferInvCodeTo("S6");
										waferManagementHistory.setWaferManagementStatus(originalInventoryCode + " to " + "S6");
										
										if(!composeFrom.contains(originalInventoryCode)){
											composeFrom = composeFrom + originalInventoryCode + ",";
										}
										if(!composeTo.contains("S6 (WAFEROUT / PROD)")){
											composeTo = composeTo + "S6 (WAFEROUT / PROD)" + ",";
										}
									}
									
									//組合update的CustomerLotNo & WaferData，save完後要回傳會要秀在Confirm後的message上
									if(!composeCustomerLotNo.contains(inCustomerLotNo)){
										composeCustomerLotNo = composeCustomerLotNo + inCustomerLotNo + "/";
										composeWaferData = composeWaferData + String.valueOf(Integer.valueOf(splitWaferNo[1])) + "/";
									}else{
										composeWaferData = composeWaferData.replace("/", "") + ";" + String.valueOf(Integer.valueOf(splitWaferNo[1]));
									}

									this.getWaferManagementHistoryDao().create(waferManagementHistory);
									this.getWaferBankinWaferDao().update(waferBankinList.get(j).getWaferBankinWafers().get(k));
								}
							}
						}
					}
					
					for(int l=0; l<waferBankinList.get(j).getWaferBankinWafers().size(); l++){
						if(!waferBankinList.get(j).getWaferBankinWafers().get(l).getCloseFlag() && waferBankinList.get(j).getWaferBankinWafers().get(l).getWaferReceiveFlag() && waferBankinList.get(j).getWaferBankinWafers().get(l).getWaferOutFlag()){
							countWaferOutFlag = countWaferOutFlag + 1; 
						}
					}
					
					if(waferBankinList.get(j).getWaferBankinWafers().size() == countWaferOutFlag){
						waferBankinList.get(j).setUpdateDate(nowtime);
						waferBankinList.get(j).setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
						waferBankinList.get(j).setWaferOutFlag(true);
						
						this.getWaferBankinDao().update(waferBankinList.get(j));
					}
				}
			}
		}
		
		if(!"".equals(composeCustomerLotNo)){
			data.put("composeCustomerLotNo", composeCustomerLotNo.substring(0, composeCustomerLotNo.length()-1));
		}else{
			data.put("composeCustomerLotNo", "");
		}
		data.put("composeWaferData", composeWaferData);
		if(!"".equals(composeFrom)){
			data.put("composeFrom", composeFrom.substring(0, composeFrom.length()-1));
		}else{
			data.put("composeFrom", "");
		}
		if(!"".equals(composeTo)){
			data.put("composeTo", composeTo.substring(0, composeTo.length()-1));
		}else{
			data.put("composeTo", "");
		}
		
		return data;
	}


	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.OffloadOperationService#getWaferBankinWaferByOrderNumber(java.lang.String)
	 */
	@Override
	public List<WaferBankin> getWaferBankinLists(String inCustomerLotNo) {
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
		
		criteria.add(Restrictions.eq("customerLotno", inCustomerLotNo));
		criteria.add(Restrictions.eq("closeFlag", false));
		criteria.add(Restrictions.eq("waferReceiveFlag", true));
		criteria.add(Restrictions.eq("waferOutFlag", false));
		
		return 	criteria.list();
	}


}

