/*
 * Project Name:iVision
 * File Name:OrderSchedulingServiceImpl.java
 * Package Name:com.tce.ivision.modules.cus.service.impl
 * Date:2012/12/28下午4:53:24
 * 
 * 說明:
 * OrderScheduling部份的service實作
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */

package com.tce.ivision.modules.oe.service.impl;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.zkoss.zk.ui.Sessions;

import com.tce.ivision.model.LotInfo;
import com.tce.ivision.model.LotResult;
import com.tce.ivision.modules.base.service.impl.BaseServiceImpl;
import com.tce.ivision.modules.oe.model.OrderScheduling;
import com.tce.ivision.modules.oe.model.OrderSchedulingParameter;
import com.tce.ivision.modules.oe.service.OrderSchedulingService;
import com.tce.ivision.units.common.DateUtil;
import com.tce.ivision.units.common.LogType;

/**
 * ClassName: OrderSchedulingServiceImpl <br/>
 * date: 2012/12/28 下午4:53:24 <br/>
 *
 * @author 110647
 * @version 
 * @since JDK 1.6
 */
public class OrderSchedulingServiceImpl extends BaseServiceImpl implements
		OrderSchedulingService {
	/**
	 * log4j component
	 */
	public static Logger log = Logger.getLogger(OrderSchedulingServiceImpl.class);
	/**
	 * 
	 * 取得符合條件的OrderScheduling List
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#getOrderSchedulings(com.tce.ivision.modules.oe.model.OrderSchedulingParameter)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OrderScheduling> getOrderSchedulings(OrderSchedulingParameter inOrderSchedulingParameter) {
		List<OrderScheduling> orderSchedulingList=new ArrayList<OrderScheduling>();
		try {
			//改為使用VIEW的方式搜尋
			String sql=" SELECT * FROM VW_ORDER_SCHEDULING ";
			String whereParam = "";
			if(!"".equals(inOrderSchedulingParameter.getCustomerId())){
				//若whereParam不是空字串則需加上AND
				if(!"".equals(whereParam)){
					whereParam = whereParam + " AND ";
				}
				whereParam = whereParam + " CUSTOMER_ID = :customerId ";
			}
			if(!"".equals(inOrderSchedulingParameter.getProduct()) ){
				//若whereParam不是空字串則需加上AND
				if(!"".equals(whereParam)){
					whereParam = whereParam + " AND ";
				}
				whereParam = whereParam +" PRODUCT = :product ";
			}
			if(inOrderSchedulingParameter.getBeginDate() != null && 
				inOrderSchedulingParameter.getEndDate() != null ){
				//若whereParam不是空字串則需加上AND
				if(!"".equals(whereParam)){
					whereParam = whereParam + " AND ";
				}
				whereParam = whereParam +" ORDER_DATE BETWEEN :beginDate AND :endDate ";
			}
			if(!"".equals(inOrderSchedulingParameter.getPoNumber())){
				//若whereParam不是空字串則需加上AND
				if(!"".equals(whereParam)){
					whereParam = whereParam + " AND ";
				}
				whereParam = whereParam +" PO_NUMBER like :poNumber ";
			}
			if(!"".equals(inOrderSchedulingParameter.getLotNo())){
				//若whereParam不是空字串則需加上AND
				if(!"".equals(whereParam)){
					whereParam = whereParam + " AND ";
				}
				whereParam = whereParam +" LOT_NO like :lotNo ";
			}
			//若有參數的話則加上WHERE 
			if(!"".equals(whereParam)){
				whereParam = " WHERE " + whereParam;
			}			
			sql = sql + whereParam;

			log.debug(sql);
			Query query = this.getDao().createSQLQuery(sql);
			
			if(!"".equals(inOrderSchedulingParameter.getCustomerId())){
				query.setParameter("customerId", inOrderSchedulingParameter.getCustomerId());
			}
			if(!"".equals(inOrderSchedulingParameter.getProduct()) ){
				query.setParameter("product", inOrderSchedulingParameter.getProduct());
			}
			if(inOrderSchedulingParameter.getBeginDate() != null && 
				inOrderSchedulingParameter.getEndDate() != null ){
				query.setParameter("beginDate", DateUtil.setTime(inOrderSchedulingParameter.getBeginDate(), 0, 0, 0));
				query.setParameter("endDate", DateUtil.setTime(inOrderSchedulingParameter.getEndDate(), 23, 59, 59));
			}

			if(!"".equals(inOrderSchedulingParameter.getPoNumber())){
				query.setParameter("poNumber", inOrderSchedulingParameter.getPoNumber()+"%");
			}

			if(!"".equals(inOrderSchedulingParameter.getLotNo())){
				query.setParameter("lotNo", inOrderSchedulingParameter.getLotNo()+"%");
			}
			
			List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			for(int i = 0; i < tmpList.size(); i++ ){
				Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
				OrderScheduling wip = new OrderScheduling();
				wip.setInputQty((Integer) row.get("INPUT_QTY"));
				wip.setLotNo((String) row.get("LOT_NO"));
				wip.setCustomerLotno1((String) row.get("CUSTOMER_LOTNO1"));
				wip.setCustomerLotno2((String) row.get("CUSTOMER_LOTNO2"));
				wip.setCustomerLotno3((String) row.get("CUSTOMER_LOTNO3"));
				wip.setCustomerLotno4((String) row.get("CUSTOMER_LOTNO4"));
				wip.setCustomerLotno5((String) row.get("CUSTOMER_LOTNO5"));
				wip.setCustomerLotno6((String) row.get("CUSTOMER_LOTNO6"));
				wip.setCustomerLotno7((String) row.get("CUSTOMER_LOTNO7"));
				wip.setCustomerLotno8((String) row.get("CUSTOMER_LOTNO8"));
				wip.setCustomerLotno9((String) row.get("CUSTOMER_LOTNO9"));
				wip.setCustomerLotno10((String) row.get("CUSTOMER_LOTNO10"));
				wip.setCustomerLotno11((String) row.get("CUSTOMER_LOTNO11"));
				wip.setCustomerLotno12((String) row.get("CUSTOMER_LOTNO12"));
				wip.setCustomerLotno13((String) row.get("CUSTOMER_LOTNO13"));
				wip.setCustomerLotno14((String) row.get("CUSTOMER_LOTNO14"));
				wip.setCustomerLotno15((String) row.get("CUSTOMER_LOTNO15"));
				wip.setCustomerLotno16((String) row.get("CUSTOMER_LOTNO16"));
				wip.setCustomerLotno17((String) row.get("CUSTOMER_LOTNO17"));
				wip.setCustomerLotno18((String) row.get("CUSTOMER_LOTNO18"));
				wip.setCustomerLotno19((String) row.get("CUSTOMER_LOTNO19"));
				wip.setCustomerLotno20((String) row.get("CUSTOMER_LOTNO20"));
				wip.setCustomerLotno21((String) row.get("CUSTOMER_LOTNO21"));
				wip.setCustomerLotno22((String) row.get("CUSTOMER_LOTNO22"));
				wip.setCustomerLotno23((String) row.get("CUSTOMER_LOTNO23"));
				wip.setCustomerLotno24((String) row.get("CUSTOMER_LOTNO24"));
				wip.setCustomerLotno25((String) row.get("CUSTOMER_LOTNO25"));
				wip.setShippingFlag((Boolean) row.get("SHIPPING_FLAG"));
				wip.setCommitDeliveryDate((Date) row.get("COMMIT_DELIVERY_DATE"));
				wip.setRescheduleDate((Date) row.get("RESCHEDULE_DATE"));
				wip.setOrderCfmNo((String) row.get("ORDER_CFM_NO"));
				wip.setCustomerShortName((String) row.get("CUSTOMER_SHORT_NAME"));
				wip.setProduct((String) row.get("PRODUCT"));
				wip.setRealProduct((String) row.get("REAL_PRODUCT")); //2017.12.20
				wip.setBilltoPo((String) row.get("BILLTO_PO"));//IT-PR-141201
				wip.setPoNumber((String) row.get("PO_NUMBER"));
				wip.setOrderNumber((String) row.get("ORDER_NUMBER"));
				wip.setMtrlDesc((String) row.get("MTRL_DESC"));
				wip.setRequestDate((String) row.get("REQUEST_DATE"));
				
				orderSchedulingList.add(wip);
				log.debug(wip.toString());
	
			}
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());	
			e.printStackTrace();
		}
		return orderSchedulingList;
	}
	/**
	 * 
	 * 用customerId,orderStatus,orderDate為條件取出符合的PRODUCT清單
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#getProductWithStatusAndDate(boolean, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getProductWithStatusAndDateAndCustomer(String inCustomerId, boolean inCloseFlag,
			Date inBeginDate, Date inEndDate) {
		List<Object> tmps=new ArrayList<Object>();
		List<String> datas=new ArrayList<String>();
		try {
			String sql="SELECT DISTINCT PRODUCT FROM ORDER_HEADER " +
					"WHERE CANCEL_FLAG=0 AND CUSTOMER_ID=:CUSTOMER_ID ";
			if(!inCloseFlag){
				sql = sql + " AND ORDER_STATUS <> '30' AND ORDER_STATUS <> '40' ";
			}else{
				sql = sql + " AND ORDER_STATUS = '30' ";
			}
			if(inBeginDate != null && inEndDate != null){
				sql = sql + " AND ORDER_DATE BETWEEN :BEGIN_DATE AND :END_DATE ";
			}
			Query query = this.getDao().createSQLQuery(sql);
			query.setParameter("CUSTOMER_ID", inCustomerId);
			if(inBeginDate != null && inEndDate != null){
				query.setParameter("BEGIN_DATE", DateUtil.setTime(inBeginDate, 0, 0, 0));
				query.setParameter("END_DATE", DateUtil.setTime(inEndDate, 23, 59, 59));
			}
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
	/**
	 * 
	 * 產生ORDER_CFM_NO<br/>
	 * Order Confirm Number自動編碼，規則如下：<br/>
	 * ‘TCE’+YYYYMMDD+’-‘+99999999(流水號) (example: TCE20121228-00000001)
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#createOrderCfmNo()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String createOrderCfmNo() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		int todayMaxIndex = 0;
		String orderCfmNo = "";
		List<Object> tmps=new ArrayList<Object>();
		try {
			//搜尋ORDER_CFM_NO開頭符合TCEYYYYMMDD-的ORDER_CFM_NO並取出當日流水號的最大值
			String sql=" SELECT MAX(CAST(SUBSTRING(ORDER_CFM_NO,13) AS SIGNED)) AS TODAY_MAX_INDEX " +
					" FROM LOT_INFO WHERE ORDER_CFM_NO LIKE :ORDER_CFM_NO ";
			Query query = this.getDao().createSQLQuery(sql);
			query.setParameter("ORDER_CFM_NO", "TCE" + dateFormat.format(new Date()) + "-%" );
			tmps=query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			if(tmps.size() > 0){
				Map<String,Object> row = (Map<String,Object>) tmps.get(0);
				log.debug(tmps.get(0));
				if(row.get("TODAY_MAX_INDEX") != null){
					BigInteger bigInteger = (BigInteger) row.get("TODAY_MAX_INDEX");
					todayMaxIndex = bigInteger.intValue();
				}
			}
			todayMaxIndex++;
			orderCfmNo = "TCE" + dateFormat.format(new Date()) + "-" + String.format("%08d",todayMaxIndex);
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());	
			e.printStackTrace();
		}
		return orderCfmNo;
	}
	/**
	 * 
	 * 用來UPDATE LOT_INFO中的COMMIT_DELIVERY_DATE及ORDER_CFM_NO
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#updateOrderScheduling(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateOrderScheduling(List<OrderScheduling> inOrderSchedulings,String inClassName) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date nowDate = new Date();//2013.10.01
		String updateUser = (String)Sessions.getCurrent().getAttribute("loginid");//2013.10.01
		boolean commitDateUpdateFlag=false; //IT-PR-140616_Allison add
		boolean commitDateCreateFlag=false; //IT-PR-140616_Allison add
		boolean rescheduleDateUpdateFlag=false; //IT-PR-140616_Allison add
		boolean rescheduleDateCreateFlag=false; //IT-PR-140616_Allison add
		String tmpCommitDate="";//IT-PR-140616_儲存更改前的commitDate_Allison add
		String tmpRescheduleDate="";//IT-PR-140616_儲存更改前的RescheduleDate_Allison add
		for(int i = 0; i < inOrderSchedulings.size(); i++){
			if(inOrderSchedulings.get(i).isShippingFlag() == false){
				List<LotInfo> lotInfos = (List<LotInfo>)this.getLotInfoDao()
						.createQuery("SELECT l FROM LotInfo l WHERE lotNo=:lotNo AND deleteFlag = false ")
						.setParameter("lotNo", inOrderSchedulings.get(i).getLotNo())
						.list();
				if(lotInfos != null){
					if(lotInfos.size() > 0){
						LotInfo lotInfo = lotInfos.get(0);
						//IT-PR-140616_Allison modify
						commitDateUpdateFlag=false;
						commitDateCreateFlag=false;
						rescheduleDateUpdateFlag=false;
						rescheduleDateCreateFlag=false;
						
						if(inOrderSchedulings.get(i).getCommitDeliveryDate() != null){
							//若LOT_INFO中沒有CommitDeliveryDate則直接UPDATE
							if(lotInfo.getCommitDeliveryDate() == null){
								lotInfo.setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
								commitDateCreateFlag=true;
							}else{//若LOT_INFO中有CommitDeliveryDate則比對是否有變更，有變更再UPDATE
								if(!dateFormat.format(lotInfo.getCommitDeliveryDate()).equals(dateFormat.format(inOrderSchedulings.get(i).getCommitDeliveryDate()))){
									tmpCommitDate=String.valueOf(lotInfo.getCommitDeliveryDate());
									lotInfo.setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
									commitDateUpdateFlag=true;
								}
							}
						}else{
							//若lotInfo中有CommitDeliveryDate,而inOrderSchedulings沒有則表示使用者要刪掉CommitDeliveryDate
							if(lotInfo.getCommitDeliveryDate() != null){
								tmpCommitDate=String.valueOf(lotInfo.getCommitDeliveryDate());
								lotInfo.setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
								commitDateUpdateFlag=true;
							}
						}
						
						if(inOrderSchedulings.get(i).getRescheduleDate() != null){
							//若LOT_INFO中沒有RescheduleDate則直接UPDATE
							if(lotInfo.getRescheduleDate() == null){
								lotInfo.setRescheduleDate(inOrderSchedulings.get(i).getRescheduleDate());
								rescheduleDateCreateFlag=true;
							}else{//若LOT_INFO中有RescheduleDate則比對是否有變更，有變更再UPDATE
								if(!dateFormat.format(lotInfo.getRescheduleDate()).equals(dateFormat.format(inOrderSchedulings.get(i).getRescheduleDate()))){
									tmpRescheduleDate=String.valueOf(lotInfo.getRescheduleDate());
									lotInfo.setRescheduleDate(inOrderSchedulings.get(i).getRescheduleDate());
									rescheduleDateUpdateFlag=true;
								}
							}
						}else{
							//若lotInfo中有RescheduleDate,而inOrderSchedulings沒有則表示使用者要刪掉RescheduleDate
							if(lotInfo.getRescheduleDate() != null){
								tmpRescheduleDate=String.valueOf(lotInfo.getRescheduleDate());
								lotInfo.setRescheduleDate(inOrderSchedulings.get(i).getRescheduleDate());
								rescheduleDateUpdateFlag=true;
							}
						}
						
						if(rescheduleDateUpdateFlag || rescheduleDateCreateFlag || commitDateUpdateFlag || commitDateCreateFlag){
							lotInfo.setOrderCfmNo(createOrderCfmNo());
							lotInfo.setUpdateUser(updateUser);//2013.10.01
							lotInfo.setUpdateDate( nowDate);//2013.10.01
							this.getLotInfoDao().update(lotInfo);
							if(rescheduleDateCreateFlag || commitDateCreateFlag){
								this.createLogging(inClassName, "LotInfo", LogType.ADD, lotInfo.toString(),updateUser,nowDate);//2013.10.01
							}else{
								this.createLogging(inClassName, "LotInfo", LogType.MODIFY, lotInfo.toString()+",originalCommitDate:"+tmpCommitDate+",originalRescheduleDate:"+tmpRescheduleDate,updateUser,nowDate);//2013.10.01 //IT-PR-140616_Allison modify
							}

							//2013.10.01
							List<LotResult> lotResults = this.getLotResultByLotNo(lotInfo.getLotNo());
							for(int j = 0; j < lotResults.size(); j++){
								lotResults.get(j).setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
								lotResults.get(j).setRescheduleDate(inOrderSchedulings.get(i).getRescheduleDate());
								lotResults.get(j).setUpdateUser(updateUser);
								lotResults.get(j).setUpdateDate(nowDate);
								this.getLotResultDao().update(lotResults.get(j));
							}
							log.debug("update:"+lotInfo.toString());
						}
						
						/* IT-PR-140616_以下改寫到上面(多了判斷RescheduleDate_Allison)
						if(inOrderSchedulings.get(i).getCommitDeliveryDate() != null){
							//若LOT_INFO中沒有CommitDeliveryDate則直接UPDATE
							if(lotInfo.getCommitDeliveryDate() == null){
								lotInfo.setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
								lotInfo.setOrderCfmNo(createOrderCfmNo());
								lotInfo.setUpdateUser(updateUser);//2013.10.01
								lotInfo.setUpdateDate( nowDate);//2013.10.01
								this.getLotInfoDao().update(lotInfo);
								this.createLogging(inClassName, "LotInfo", LogType.ADD, lotInfo.toString(),updateUser,nowDate);//2013.10.01

								//2013.10.01
								List<LotResult> lotResults = this.getLotResultByLotNo(lotInfo.getLotNo());
								for(int j = 0; j < lotResults.size(); j++){
									lotResults.get(j).setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
									lotResults.get(j).setUpdateUser(updateUser);
									lotResults.get(j).setUpdateDate(nowDate);
									this.getLotResultDao().update(lotResults.get(j));
								}
								log.debug("update:"+lotInfo.toString());
							}else{//若LOT_INFO中有CommitDeliveryDate則比對是否有變更，有變更再UPDATE
								if(!dateFormat.format(lotInfo.getCommitDeliveryDate()).equals(dateFormat.format(inOrderSchedulings.get(i).getCommitDeliveryDate()))){
									lotInfo.setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
									lotInfo.setOrderCfmNo(createOrderCfmNo());
									lotInfo.setUpdateUser(updateUser);//2013.10.01
									lotInfo.setUpdateDate( nowDate);//2013.10.01
									this.getLotInfoDao().update(lotInfo);
									this.createLogging(inClassName, "LotInfo", LogType.MODIFY, lotInfo.toString(), updateUser, nowDate);//2013.10.01
									
									//2013.10.01
									List<LotResult> lotResults = this.getLotResultByLotNo(lotInfo.getLotNo());
									for(int j = 0; j < lotResults.size(); j++){
										lotResults.get(j).setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
										lotResults.get(j).setUpdateUser(updateUser);
										lotResults.get(j).setUpdateDate(nowDate);
										this.getLotResultDao().update(lotResults.get(j));
									}
									log.debug("update:"+lotInfo.toString());
								}
							}
						}else{
							//若lotInfo中有CommitDeliveryDate,而inOrderSchedulings沒有則表示使用者要刪掉CommitDeliveryDate
							if(lotInfo.getCommitDeliveryDate() != null){
								lotInfo.setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
								lotInfo.setOrderCfmNo("");
								lotInfo.setUpdateUser(updateUser);//2013.10.01
								lotInfo.setUpdateDate( nowDate);//2013.10.01
								this.getLotInfoDao().update(lotInfo);
								this.createLogging(inClassName, "LotInfo", LogType.MODIFY, lotInfo.toString(), updateUser, nowDate);//2013.10.01
								
								//2013.10.01
								List<LotResult> lotResults = this.getLotResultByLotNo(lotInfo.getLotNo());
								for(int j = 0; j < lotResults.size(); j++){
									lotResults.get(j).setCommitDeliveryDate(inOrderSchedulings.get(i).getCommitDeliveryDate());
									lotResults.get(j).setUpdateUser(updateUser);
									lotResults.get(j).setUpdateDate(nowDate);
									this.getLotResultDao().update(lotResults.get(j));
								}
							}
						}
						*/
					}
				}
			}
		}
	}
	/**
	 * 依據PO_NUMBER+CUSTOMER_LOTNO1~25找出有哪些LOT_NO
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#getLotnoforOEHoldUsed(java.lang.String, java.lang.String)
	 */
	@Override
	public String getLotnoforOEHoldUsed(String inPoNumber, String inCustomerLono) {
		List<LotInfo> lotInfos=new ArrayList<LotInfo>();
		String hql="select c from LotInfo c where poNumber=:po_number and deleteFlag=0 and shippingFlag=0 and "+
		           "(customerLotno1=:customer_lotno or customerLotno2=:customer_lotno or customerLotno3=:customer_lotno or "+
		           " customerLotno4=:customer_lotno or customerLotno5=:customer_lotno or customerLotno6=:customer_lotno or "+
		           " customerLotno7=:customer_lotno or customerLotno8=:customer_lotno or customerLotno9=:customer_lotno or "+
		           " customerLotno10=:customer_lotno or customerLotno11=:customer_lotno or customerLotno12=:customer_lotno or "+
		           " customerLotno13=:customer_lotno or customerLotno14=:customer_lotno or customerLotno15=:customer_lotno or "+
		           " customerLotno16=:customer_lotno or customerLotno17=:customer_lotno or customerLotno18=:customer_lotno or "+
		           " customerLotno19=:customer_lotno or customerLotno20=:customer_lotno or customerLotno21=:customer_lotno or "+
		           " customerLotno22=:customer_lotno or customerLotno23=:customer_lotno or customerLotno24=:customer_lotno or "+
		           " customerLotno25=:customer_lotno) ";
		try {
			lotInfos=this.getLotInfoDao().createQuery(hql)
					.setParameter("po_number", inPoNumber)
					.setParameter("customer_lotno", inCustomerLono)
					.list();
		} catch (Exception e) {
			
		}
		
		if (lotInfos.size()>0){
			String r="";
			for (int i=0;i<lotInfos.size();i++){
				r=r+lotInfos.get(i).getLotNo()+";";
			}
			return r;
		}
		else{
			return "";
		}
		
	}
	/**
	 * 依據PO_NUMBER+CUSTOMER_LOTNO1~25找出有哪些SHIPPING_FLAG
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#getShippingFlagforOEHoldUsed(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean getShippingFlagforOEHoldUsed(String inPoNumber,
			String inCustomerLono) {
		List<LotInfo> lotInfos=new ArrayList<LotInfo>();
		String hql="select c from LotInfo c where poNumber=:po_number and "+
		           "(customerLotno1=:customer_lotno or customerLotno2=:customer_lotno or customerLotno3=:customer_lotno or "+
		           " customerLotno4=:customer_lotno or customerLotno5=:customer_lotno or customerLotno6=:customer_lotno or "+
		           " customerLotno7=:customer_lotno or customerLotno8=:customer_lotno or customerLotno9=:customer_lotno or "+
		           " customerLotno10=:customer_lotno or customerLotno11=:customer_lotno or customerLotno12=:customer_lotno or "+
		           " customerLotno13=:customer_lotno or customerLotno14=:customer_lotno or customerLotno15=:customer_lotno or "+
		           " customerLotno16=:customer_lotno or customerLotno17=:customer_lotno or customerLotno18=:customer_lotno or "+
		           " customerLotno19=:customer_lotno or customerLotno20=:customer_lotno or customerLotno21=:customer_lotno or "+
		           " customerLotno22=:customer_lotno or customerLotno23=:customer_lotno or customerLotno24=:customer_lotno or "+
		           " customerLotno25=:customer_lotno) ";
		try {
			lotInfos=this.getLotInfoDao().createQuery(hql)
					.setParameter("po_number", inPoNumber)
					.setParameter("customer_lotno", inCustomerLono)
					.list();
		} catch (Exception e) {
			
		}
		
		if (lotInfos.size()>0){
			return lotInfos.get(0).isShippingFlag();
		}
		else{
			return false;
		}
	}
	/**
	 * 用LOT_NO取出LOT_RESULT且只取出iNaviLotNo第15碼為A或B的資料
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#getLotResultByLotNo(java.lang.String)
	 */
	@SuppressWarnings("unchecked")//2013.10.01
	@Override
	public List<LotResult> getLotResultByLotNoOnlyABType(String inLotNo) {
		List<LotResult> lotResults=new ArrayList<LotResult>();
		String hql= "SELECT a FROM LotResult a WHERE lotNo = :LOT_NO AND (substring(a.inaviLotno, 15, 1) = 'A' OR substring(a.inaviLotno, 15, 1) = 'B') order by substring(a.inaviLotno, 15, 4)";
		try {
			lotResults=this.getLotResultDao().createQuery(hql)
					.setParameter("LOT_NO", inLotNo)
					.list();
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());	
			e.printStackTrace();
		}
		return lotResults;
	}
	/**
	 * 用LOT_NO取出LOT_RESULT
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#getLotResultByLotNo(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LotResult> getLotResultByLotNo(String inLotNo) {
		List<LotResult> lotResults=new ArrayList<LotResult>();
		String hql= "SELECT a FROM LotResult a WHERE lotNo = :LOT_NO ";
		try {
			lotResults=this.getLotResultDao().createQuery(hql)
					.setParameter("LOT_NO", inLotNo)
					.list();
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());	
			e.printStackTrace();
		}
		return lotResults;
	}
	/**
	 * update LotResult
	 * @see com.tce.ivision.modules.oe.service.OrderSchedulingService#updateOrderSchedulingLotResult(java.util.List)
	 */
	@Override
	public void updateOrderSchedulingLotResult(List<LotResult> inLotResults) {//2013.10.01
		Date nowDate = new Date();
		String updateUser = (String)Sessions.getCurrent().getAttribute("loginid");
		for(int i = 0;i < inLotResults.size(); i++){
			inLotResults.get(i).setUpdateUser(updateUser);
			inLotResults.get(i).setUpdateDate(nowDate);
			this.getLotResultDao().update(inLotResults.get(i));
		}
		
	}
	

}

