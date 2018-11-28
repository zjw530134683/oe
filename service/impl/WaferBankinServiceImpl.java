/*
 * Project Name:iVision
 * File Name:WaferInfoReceiveServiceImpl.java
 * Package Name:com.tce.ivision.modules.oe.service.impl
 * Date:2012/12/18下午8:14:31
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
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zkplus.spring.SpringUtil;

import com.tce.ivision.model.ScrapInfo;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.model.WaferInvLovSub;
import com.tce.ivision.model.WaferInvTxnDefinition;
import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.model.LotInfo;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.ProductInfo;
import com.tce.ivision.model.Shipping;
import com.tce.ivision.model.ShippingDetail;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.model.WaferBankinWafer;
import com.tce.ivision.model.WaferInspArea;
import com.tce.ivision.model.WaferInspAreaBase;
import com.tce.ivision.model.WaferInspAreaDefine;
import com.tce.ivision.model.WaferInvFgnameMapping;
import com.tce.ivision.model.WaferInvFgnameSetup;
import com.tce.ivision.model.WaferInvLovMain;
import com.tce.ivision.model.WaferInvRmnameMapping;
import com.tce.ivision.model.WaferInvRmnameSetup;
import com.tce.ivision.model.WaferInventoryStage;
import com.tce.ivision.model.WaferManagementHistory;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.base.service.impl.BaseServiceImpl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.oe.model.WaferFilter;
import com.tce.ivision.modules.oe.service.WaferBankinService;
import com.tce.ivision.modules.wafer.model.WaferBankinWaferWaferDataChangeModel;
import com.tce.ivision.modules.wafer.model.WaferInvReceiveLot;
import com.tce.ivision.modules.wafer.model.WaferInvReceiveWafer;
import com.tce.ivision.modules.wafer.model.WaferInventoryManagement;
import com.tce.ivision.modules.wafer.model.WaferInventoryManagementParameter;
import com.tce.ivision.modules.wafer.model.WaferReceiveOperation;
import com.tce.ivision.modules.wafer.model.WaferStatusMaintenanceParameter;
import com.tce.ivision.units.common.BeanUtil;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.DateUtil;
import com.tce.ivision.units.common.LogType;

/**
 * ClassName: WaferInfoReceiveServiceImpl <br/>
 * date: 2012/12/18 下午8:14:31 <br/>
 *
 * @author 060489-Jeff
 * @version 
 * @since JDK 1.6
 */
public class WaferBankinServiceImpl extends BaseServiceImpl implements WaferBankinService{
	/**
	 * 抓WaferInfoReceive DATA.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInfoReceiveByHql(java.lang.String)
	 */
	@Override
	public List<WaferBankin> listAll() {
		String hql = "select a from WaferBankin a";  
        Query query = this.getWaferBankinDao().createQuery(hql);         
		return query.list();
	}

	/**
	 * 新增受領Wafer.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#saveItems(java.util.List)
	 */
	@Override
	public boolean saveItems(List<WaferBankinWafer> inItems, String inExistMsg, boolean inDiffWaferDataFlag) {	
		boolean saveFlag=false;
		if(inItems.size() > 0 && inItems != null){
			if(!"".equals(inExistMsg.trim())){
				//若inExistMsg不是空白，則代表有檢查到資料已經有一樣的CUSTOMER_ID+CUSTOMER_LOTNO+WAFER_DATA+WAFER_RECEIVE_FLAG=0+CLOSE_FLAG=0的資料，則先前WAFER_BANKIN資料的CLOSE_FLAG=1
				List<WaferBankin> waferBankinLists = (List<WaferBankin>)this.getWaferBankinDao()
						.createQuery("SELECT l FROM WaferBankin l WHERE customerLotno=:customerLotno and customerId=:customerId and waferData=:waferData and waferReceiveFlag=0 and closeFlag=0 ")
						.setParameter("customerLotno", inItems.get(0).getWaferBankin().getCustomerLotno())
						.setParameter("customerId", inItems.get(0).getWaferBankin().getCustomerId())
						.setParameter("waferData", inItems.get(0).getWaferBankin().getWaferData())
						.list();
				if(waferBankinLists.size() > 0){			
					for(int j=0; j<waferBankinLists.size(); j++){
						waferBankinLists.get(j).setCloseFlag(true);
						waferBankinLists.get(j).setWaferPrepUpdateDate(new Date());
						waferBankinLists.get(j).setWaferPrepUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
						this.getWaferBankinDao().update(waferBankinLists.get(j));
					}
				}
			}
			if(inItems.get(0).getWaferBankin().getWaferBankinIdx() != 0){
				inItems.get(0).getWaferBankin().setWaferPrepUpdateDate(new Date());
				inItems.get(0).getWaferBankin().setWaferPrepUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
				this.getWaferBankinDao().update(inItems.get(0).getWaferBankin());
				
				//inDiffWaferDataFlag = TRUE，則代表有異動到WAFER_DATA，畫面上選擇的WAFER_DATA已經跟原先存進資料庫裡的不同,故要先將舊[WAFER_BANKIN_WAFER].CLOSE_FLAG=1再INSERT新的
				if(inDiffWaferDataFlag){
					//OCF-PR-160303_mark
					//將異動後的資料先放到saveWaferBankinWafer，之後再UPDATE
					//List<WaferBankinWafer> saveWaferBankinWafer = new ArrayList<WaferBankinWafer>();
					//for(int i=0; i<inItems.size(); i++){
					//	WaferBankinWafer newWaferBankinWafer = new WaferBankinWafer();
					//	newWaferBankinWafer.setWaferBankin(inItems.get(0).getWaferBankin());
					//	newWaferBankinWafer.setReceiveWaferNo(inItems.get(i).getReceiveWaferNo());
					//	newWaferBankinWafer.setInventoryCode(inItems.get(i).getInventoryCode());
					//	newWaferBankinWafer.setUpdateDate(new Date());
					//	newWaferBankinWafer.setCreateDate(new Date());
					//	newWaferBankinWafer.setUpdateUser(inItems.get(i).getUpdateUser());
					//	saveWaferBankinWafer.add(newWaferBankinWafer);
					//}

					//將舊的[WAFER_BANKIN_WAFER].CLOSE_FLAG=1
					List<WaferBankinWafer> waferBankinWaferLists = new ArrayList<WaferBankinWafer>();
					waferBankinWaferLists = (List<WaferBankinWafer>)this.getWaferBankinWaferDao()
							.createQuery("SELECT l FROM WaferBankinWafer l WHERE waferBankin=:waferBankin and closeFlag=0 ")
							.setParameter("waferBankin", inItems.get(0).getWaferBankin())
							.list();
					
					//OCF-PR-160303_先撈出DB原有的WAFER_DATA，並用WaferBankinWaferWaferDataChangeModel來記錄那些WAFER_DATA已被更動(changeFlag)，以及是否已有更新成新的WAFER_DATA(fillInFlag)
					List<WaferBankinWaferWaferDataChangeModel> waferBankinWaferWaferDataChangeModelLists = new ArrayList<WaferBankinWaferWaferDataChangeModel>();
					if(waferBankinWaferLists.size() > 0){			
						for(int j=0; j<waferBankinWaferLists.size(); j++){
							WaferBankinWaferWaferDataChangeModel waferBankinWaferWaferDataChangeModel = new WaferBankinWaferWaferDataChangeModel();
							waferBankinWaferWaferDataChangeModel.setWaferBankinWafer(waferBankinWaferLists.get(j));
							waferBankinWaferWaferDataChangeModelLists.add(waferBankinWaferWaferDataChangeModel);
						}
					}
					
					//先檢查DB那些WAFER_DATA跟新異動後的WAFER_DATA比對，那些是有被異動的
					boolean checkflag = false;
					if(waferBankinWaferWaferDataChangeModelLists.size() > 0){
						for(int j=0; j<waferBankinWaferWaferDataChangeModelLists.size(); j++){
							for(int k=0; k<inItems.size(); k++){
								if(waferBankinWaferWaferDataChangeModelLists.get(j).getWaferBankinWafer().getReceiveWaferNo().equals(inItems.get(k).getReceiveWaferNo())){
									checkflag = true;
								}
							}
							if(!checkflag){
								waferBankinWaferWaferDataChangeModelLists.get(j).setChangeFlag(true);
							}
						}
					}
					
					//再用新異動的資料來跟DB的比對，若比對不到代表新異動是新增的WAFER_DATA，則要將DB內有被異動的UPDATE成新異動過後的WAFER_DATA
					checkflag = false;
					for(int j=0; j<inItems.size(); j++){
						for(int k=0; k<waferBankinWaferWaferDataChangeModelLists.size(); k++){
							if(waferBankinWaferWaferDataChangeModelLists.get(k).getWaferBankinWafer().getReceiveWaferNo().equals(inItems.get(j).getReceiveWaferNo())){
								checkflag = true;
							}
						}
						if(!checkflag){
							for(int t=0; t<waferBankinWaferWaferDataChangeModelLists.size(); t++){
								if(waferBankinWaferWaferDataChangeModelLists.get(t).getChangeFlag() && !waferBankinWaferWaferDataChangeModelLists.get(t).getFillInFlag()){
									waferBankinWaferWaferDataChangeModelLists.get(t).getWaferBankinWafer().setReceiveWaferNo(inItems.get(j).getReceiveWaferNo());
									waferBankinWaferWaferDataChangeModelLists.get(t).setFillInFlag(true);
								}
							}
						}
					}
					
					//UPDATE DB
					for(int k=0; k<waferBankinWaferWaferDataChangeModelLists.size(); k++){
						if(waferBankinWaferWaferDataChangeModelLists.get(k).getChangeFlag() && waferBankinWaferWaferDataChangeModelLists.get(k).getFillInFlag()){
							this.getWaferBankinWaferDao().update(waferBankinWaferWaferDataChangeModelLists.get(k).getWaferBankinWafer());
						}
					}
					
					//INSERT進新的資料
					//this.createItems(saveWaferBankinWafer); //OCF-PR-160303 mark
				}else{
					for(int k=0; k<inItems.get(0).getWaferBankin().getWaferBankinWafers().size(); k++){
						inItems.get(0).getWaferBankin().getWaferBankinWafers().get(k).setUpdateDate(new Date());
						inItems.get(0).getWaferBankin().getWaferBankinWafers().get(k).setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
					}
					this.updateItems(inItems.get(0).getWaferBankin().getWaferBankinWafers());
				}
				saveFlag = true;
			}else{
				//新增
				this.getWaferBankinDao().create(inItems.get(0).getWaferBankin());
				
				List<WaferInvTxnDefinition> waferInvTxnDefinitionLists = this.getWaferInvTxnDefinition("PMP_BN");
				List<UiFieldSet> uiFieldSetList = this.getUiFieldSetLists("com.tce.ivision.modules.wafer.ctrl", "BOND_TXN_MATERIAL_TYPE");
				String waferData = "";
				int txnQty=0;
				String bondTxnMaterialName = "";
		for(int i=0;i<inItems.size();i++){
					String[] splitWaferNo = inItems.get(i).getReceiveWaferNo().split("-");
					waferData = waferData + String.valueOf(Integer.valueOf(splitWaferNo[splitWaferNo.length-1])) + ";";
					txnQty = txnQty + 1;
					
					inItems.get(i).setWaferInvCodeFrom("PMP");
					inItems.get(i).setWaferInvCodeTo("S0");
					String managementFunction = this.getUiFieldParamValueByMeaning("WAFER_MANAGEMENT_HISTORY", "WAFER_MANAGEMENT_STATUS_PREFIX", "com.tce.ivision.modules.wafer.ctrl.WaferReceiptManualViewCtrl");
					inItems.get(i).setWaferManagementStatus("("+managementFunction+")" + " PMP to S0");
					inItems.get(i).setWaferManagementComment("");
					inItems.get(i).setWaferManagementUpdateDate(new Date());
					inItems.get(i).setWaferManagementUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
					
					//保稅相關所需欄位
					if(waferInvTxnDefinitionLists.size() > 0){
						inItems.get(i).setTceTxnType(waferInvTxnDefinitionLists.get(0).getTceTxnType());
						inItems.get(i).setBondTxnType(waferInvTxnDefinitionLists.get(0).getBondTxnType());
						//塞入BOND_TXN_MATERIAL_NAME此欄位的規則
						//1. 需先依保稅的交易型態(IN,MI...)到[UI_FIELD_PARAM]找出設定的RULE
						//2. RULE設定為To，則代表用WAFER_INV_CODE_TO的STAGE，再去[WAFER_INVENTORY_STAGE]找該STAGE是屬於什麼料號型態(RM/FG)，再決定該欄位是要塞入什麼料號型態
						//3. RULE設定為From，則代表用WAFER_INV_CODE_FROM的STAGE,塞入料號型態規則如上
						if(uiFieldSetList.size() > 0){
							if(uiFieldSetList.get(0).getUiFieldParams().size() > 0){
								for(int t=0; t<uiFieldSetList.get(0).getUiFieldParams().size(); t++){
									if(uiFieldSetList.get(0).getUiFieldParams().get(t).getMeaning().equals(waferInvTxnDefinitionLists.get(0).getBondTxnType())){
										String stage = "";
										if(uiFieldSetList.get(0).getUiFieldParams().get(t).getParaValue().equals("To")){
											stage = inItems.get(i).getWaferInvCodeTo();
										}else if(uiFieldSetList.get(0).getUiFieldParams().get(t).getParaValue().equals("From")){
											stage = inItems.get(i).getWaferInvCodeFrom();
										}
										List<WaferInventoryStage> waferInventoryStage = this.getWaferInventoryStage(stage);
										if(waferInventoryStage.size() > 0){
											if("RM".equals(waferInventoryStage.get(0).getBondMaterialType())){
												inItems.get(i).setBondTxnMaterialName(inItems.get(0).getWaferBankin().getRawMaterialName());
												bondTxnMaterialName = inItems.get(0).getWaferBankin().getRawMaterialName();
											}else if("FG".equals(waferInventoryStage.get(0).getBondMaterialType())){
												inItems.get(i).setBondTxnMaterialName(inItems.get(i).getFinishGoodsName());
												bondTxnMaterialName = inItems.get(i).getFinishGoodsName();
											}
										}
										break;
									}
								}
							}
						}
						inItems.get(i).setBondTxnMaterialName(inItems.get(0).getWaferBankin().getRawMaterialName());
						//waferManagementHistory.setTceTxnId(tceTxnId); //此欄位由TR_WAFER_MANAGEMENT_HISTORY_AFTERINSERT(TRIGGER)來編碼
					}
					
					this.getWaferBankinWaferDao().create(inItems.get(i));
				}
				
				//OCF-PR-160303_新增Preparation作業塞入[WAFER_MANAGEMENT_HISTORY]，供保稅交易記錄			
				WaferManagementHistory waferManagementHistory = new WaferManagementHistory();
				waferManagementHistory.setWaferManagementHistoryIdx(0);
				waferManagementHistory.setWaferBankin(inItems.get(0).getWaferBankin());
				if(!"".equals(waferData)){
					waferManagementHistory.setManagementWaferData(waferData.substring(0, waferData.length()-1));
				}else{
					waferManagementHistory.setManagementWaferData(inItems.get(0).getWaferBankin().getWaferData());
				}
				waferManagementHistory.setWaferInvCodeFrom("PMP");
				waferManagementHistory.setWaferInvCodeTo("S0");
				String managementFunction = this.getUiFieldParamValueByMeaning("WAFER_MANAGEMENT_HISTORY", "WAFER_MANAGEMENT_STATUS_PREFIX", "com.tce.ivision.modules.wafer.ctrl.WaferReceiptManualViewCtrl");
				waferManagementHistory.setWaferManagementStatus("("+managementFunction+")" + " PMP to S0");
				waferManagementHistory.setWaferManagementComment("");
				waferManagementHistory.setWaferManagementUpdateDate(new Date());
				waferManagementHistory.setWaferManagementUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
				waferManagementHistory.setCreateDate(new Date());
				
				//保稅相關所需欄位
				//搜尋[WAFER_INV_TXN_DEFINITION]塞入相關保稅交易用的定義
				if(waferInvTxnDefinitionLists.size() > 0){
					waferManagementHistory.setBondTxnType(waferInvTxnDefinitionLists.get(0).getBondTxnType());
					if(waferInvTxnDefinitionLists.get(0).getTxnRequestFlag()){
						waferManagementHistory.setTxnRequestFlag("1");
					}else{
						waferManagementHistory.setTxnRequestFlag("0");
					}					
					waferManagementHistory.setTceTxnType(waferInvTxnDefinitionLists.get(0).getTceTxnType());
					waferManagementHistory.setRawMaterialName(inItems.get(0).getWaferBankin().getRawMaterialName());
					waferManagementHistory.setBondTxnMaterialName(bondTxnMaterialName);
					waferManagementHistory.setWaferFrom(inItems.get(0).getWaferBankin().getWaferFrom());
					List<WaferInventoryStage> waferInventoryStage = this.getWaferInventoryStage("S0");
					if(waferInventoryStage.size() > 0){
						waferManagementHistory.setBondStage(waferInventoryStage.get(0).getBondCategory());
					}
					waferManagementHistory.setTxnQty(txnQty);
					//waferManagementHistory.setTceTxnId(tceTxnId); //此欄位由TR_WAFER_MANAGEMENT_HISTORY_BEFOREINSERT(TRIGGER)來編碼
				}
				this.getWaferManagementHistoryDao().create(waferManagementHistory);
				
				saveFlag = true;
			}
		}
		return saveFlag;
	}

	public void createItems(List<WaferBankinWafer> saveWaferBankinWafer){
		for(int i=0; i<saveWaferBankinWafer.size(); i++){
			this.getWaferBankinWaferDao().create(saveWaferBankinWafer.get(i));
		}
		}
	
	public void updateItems(List<WaferBankinWafer> saveWaferBankinWafer){
		for(int i=0; i<saveWaferBankinWafer.size(); i++){
			if(!saveWaferBankinWafer.get(i).getCloseFlag()){
				this.getWaferBankinWaferDao().update(saveWaferBankinWafer.get(i));
			}
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#saveTransactionItems()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void saveTransactionItems(Object[] inObjs) {
		//IT-PR-141008_若WAFER_BANKIN檢查有重履的，則要先將先前的資料CLOSE_FLAG=1_Allison add
		boolean existFlag=(Boolean) inObjs[3];
		if(existFlag){
			updateWaferBankinAndInsert(inObjs);
		}else{
			//將saveWaferlistboxs Data List 丟到WaferInfoReceiveService去做新增存檔動作		
			//saveItems((List<WaferBankin>) inObjs[0]);
			log.info("Save...WAFER_INFO_RECEIVE...done");
				
		//更新Wafer Status WAFERFLG=1, update QTY 
			//OCF-PR-150202_不需再update [WAFER_STATUS]_Allison mark
			//updateWaferStatus((List<WaferStatus>) inObjs[1]);
		}	
		
		 if(inObjs[2] != null){		
			//將updateWaferlistboxs Data List 丟到WaferInIntTableService去做更新GetFlag動作
			//將Wafer B2B Data 設為已受領==>GetFlag=1
				List<WaferBankinInt> updateWaferlistboxs = (List<WaferBankinInt>) inObjs[2];
				for (int i=0;i<updateWaferlistboxs.size();i++){
					this.getWaferInIntTableDao().update(updateWaferlistboxs.get(i));
					
				}
				log.info("Update...WAFER_BANKIN_INT...done");
		 }
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#listBySearch(com.tce.ivision.modules.oe.model.WaferFilter)
	 */
	@Override
	public List<WaferBankin> listBySearch(WaferFilter inWaferfilter) {
		log.info("Filter Condition:");
		log.info("Customer:"+inWaferfilter.getCustomerfilter());
		log.info("Customerlotno:"+inWaferfilter.getCustomerlotnofilter());
		log.info("StartDate:"+inWaferfilter.getStartdatefilter());
		log.info("Enddate:"+inWaferfilter.getEnddatefilter());
		//log.info("GetFlag:False");
		log.info("closeFlag:"+inWaferfilter.getCloseFlag());
		log.info("Material Type:"+inWaferfilter.getMaterialTypeFilter());
		log.info("ReceivedBeforeShipFlag:"+inWaferfilter.getReceiveBeforeShipFlag());
		
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
		if(!"-".equals(inWaferfilter.getCustomerfilter())){
			criteria.add(Restrictions.eq("customerId",inWaferfilter.getCustomerfilter()));
		}
		//IT-PR-141201
		if(!"-".equals(inWaferfilter.getWaferDataFilter()) && inWaferfilter.getWaferDataFilter() != null){
			criteria.add(Restrictions.eq("waferData",inWaferfilter.getWaferDataFilter()));
		}
		if(!"-".equals(inWaferfilter.getCustomerlotnofilter())){
			criteria.add(Restrictions.like("customerLotno","%"+inWaferfilter.getCustomerlotnofilter()+"%"));
		}
		if(!"-".equals(inWaferfilter.getStartdatefilter())||!"-".equals(inWaferfilter.getEnddatefilter())){
			
			SimpleDateFormat sdf = new SimpleDateFormat(Labels.getLabel("format.date"));
			//進行轉換
			Date sdate = null;
			Date edate = null;
			try {
				sdate = sdf.parse(inWaferfilter.getStartdatefilter());
				edate = sdf.parse(inWaferfilter.getEnddatefilter());
				edate.setHours(23);
				edate.setMinutes(59);
				edate.setSeconds(59);
			} catch (ParseException e) {
				 StringWriter stringWriter = new StringWriter();
	             e.printStackTrace(new PrintWriter(stringWriter));
	             log.error(stringWriter.toString());
			}
			
			criteria.add(Restrictions.between("waferPrepCreateDate", sdate, edate));
		}
		//criteria.add(Restrictions.eq("closeFlag", false));//IT-PR-140712_WAFER_BANKIN增加CLOSE_FLAG欄位，故Search時要排除
		criteria.add(Restrictions.eq("closeFlag", inWaferfilter.getCloseFlag()));//IT-PR-141008_畫面多Delete的Checkbox，可Query出Close_Flag=1的_Allison add
		
		//OCF-PR-160303_若畫面的Received~Before SHIP打勾，則代表可以秀出WAFER_RECEIVE_FLAG=1 & WAFER_OUT_FLAG=0的資料(因應若被QA做了Wafer Receive，但是因Preparation作業錯誤，而需重新做Preparation作業_confirm with PC-Vicky)
		if(!inWaferfilter.getReceiveBeforeShipFlag()){
		criteria.add(Restrictions.eq("waferOutFlag", false));//OCF-PR-150202_新增搜尋條件加上[WAFER_BANKIN].WAFEROUT_FLAG=0_Allison
		criteria.add(Restrictions.eq("waferReceiveFlag", false));//OCF-PR-150202_新增搜尋條件加上[WAFER_BANKIN].WAFER_RECEIVE_FLAG=0_Allison
		}else{
			criteria.add(Restrictions.eq("waferOutFlag", false));
			criteria.add(Restrictions.eq("waferReceiveFlag", true));
		}
		if(!"-".equals(inWaferfilter.getMaterialTypeFilter())){//OCF-PR-160303 add
			criteria.add(Restrictions.eq("materialType",inWaferfilter.getMaterialTypeFilter()));
		}
		//log.debug(inWaferfilter.getStartdatefilter());
		//log.debug(criteria.list().size());
		return 	criteria.list();
	}


	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateTransactionItems(java.lang.Object[])
	 */
	@Override
	public void updateTransactionItems(Object[] inObjs) {
		//將saveWaferlistboxs Data List 丟到WaferInfoReceiveService去做新增存檔動作		
		List<WaferBankin> waferinforeceives = (List<WaferBankin>) inObjs[0];
		for(int i=0;i<waferinforeceives.size();i++){
			this.getWaferBankinDao().update(waferinforeceives.get(i));
		}
		log.info("Update...WAFER_INFO_RECEIVE...done");
	
		//更新Wafer Status WAFERFLG=1, update QTY 
		updateWaferStatus((List<WaferStatus>) inObjs[1]);
		
	}

	
	private void updateWaferStatus(List<WaferStatus> inUpdateWaferStatus){
		
		for (int i=0;i<inUpdateWaferStatus.size();i++){
			this.getWaferStatusDao().update(inUpdateWaferStatus.get(i));		
		}
		log.info("Update...WAFER_STATUS...done");
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#beforeSaveCheck(java.lang.Object[])
	 */
	@Override
	public boolean checkDataExist(Object[] inObjs) {
		
		List<WaferBankin> waferbankins = (List<WaferBankin>) inObjs[0];
		int checkCount=0;
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
		for(int i=0;i<waferbankins.size();i++){			
			log.debug("Check-getCustomerId--->"+waferbankins.get(i).getCustomerId());
			log.debug("Check-getCustomerLotno--->"+waferbankins.get(i).getCustomerLotno().trim());
			//log.debug("Check-getWaferInDate--->"+waferbankins.get(i).getWaferInDate());
			//log.debug("Check-getWaferInQty--->"+waferbankins.get(i).getWaferInQty());
			criteria.add(Restrictions.eq("customerId",waferbankins.get(i).getCustomerId()));
			criteria.add(Restrictions.eq("customerLotno",waferbankins.get(i).getCustomerLotno().trim()));
			//criteria.add(Restrictions.eq("waferInDate",waferbankins.get(i).getWaferInDate()));//IT-PR-140712_Allison無需下到waferInDate和waferInQty來判斷，只要有Customer LotNo來判斷是否有重覆_Allison
			//criteria.add(Restrictions.eq("waferInQty",waferbankins.get(i).getWaferInQty()));
			if(criteria.list().size()>0){
			  checkCount++;
			}
		}
		log.info("Check...WAFER_BANKIN...done");
		if(checkCount==0){
		 return false;
		}else{
		 return true;
		}
	}

	
	
	//IT-PR-140712_若找到相同的CustomerLotNo，則需要先將WaferBankIn裡先前的資料的Close_Flag=1，再insert新的資料
	public boolean updateWaferBankinAndInsert(Object[] inObjs){
		boolean updateFlag=false;
		List<WaferBankin> waferinforeceives = (List<WaferBankin>) inObjs[0];
		
		//1. 先依CustomerLotNo將將先前WAFER_BANKIN資料的CLOSE_FLAG=1
		for(int i=0; i<waferinforeceives.size(); i++){
			//OCF-PR-150307
			List<WaferBankin> waferBankinLists = (List<WaferBankin>)this.getWaferBankinDao()
					.createQuery("SELECT l FROM WaferBankin l WHERE customerLotno=:customerLotno and customerId=:customerId  and waferData=:waferData ")
					.setParameter("customerLotno", waferinforeceives.get(i).getCustomerLotno())
					.setParameter("customerId", waferinforeceives.get(i).getCustomerId())
					.setParameter("waferData", waferinforeceives.get(i).getWaferData())
					.list();
			if(waferBankinLists.size() > 0){			
				for(int j=0; j<waferBankinLists.size(); j++){
					waferBankinLists.get(j).setCloseFlag(true);
					this.getWaferBankinDao().update(waferBankinLists.get(j));
					this.createLogging(this.getClass().getName(), "WAFER_BANKIN", LogType.MODIFY, "", (String)Sessions.getCurrent().getAttribute("loginid"), new Date());	
					updateFlag=true;
				}
			}
		}		
		
		//OCF-PR-150202_不需再update[WAFER_STATUS]_Allison mark
		//if(updateFlag){
			//2. 更新Wafer Status WAFERFLG=1, update QTY 
		//	updateWaferStatus((List<WaferStatus>) inObjs[1]);
		//}
		//3. INSERT新的WAFER_BANKIN的資料
		for(int k=0; k<waferinforeceives.size(); k++){
			WaferBankin waferbankin = new WaferBankin();

			waferbankin.setCustomerId(waferinforeceives.get(k).getCustomerId().trim());
			waferbankin.setCustomerLotno(waferinforeceives.get(k).getCustomerLotno().trim());			
			waferbankin.setWaferPrepQty(waferinforeceives.get(k).getWaferPrepQty());
			waferbankin.setWaferInDate(waferinforeceives.get(k).getWaferInDate());			
			waferbankin.setDieQty(waferinforeceives.get(k).getDieQty());
			waferbankin.setMtrlNum(waferinforeceives.get(k).getMtrlNum().trim());
			waferbankin.setMtrlDesc(waferinforeceives.get(k).getMtrlDesc().trim());
			waferbankin.setDesignId(waferinforeceives.get(k).getDesignId().trim());
			waferbankin.setFab(waferinforeceives.get(k).getFab().trim());
			waferbankin.setDocNumber(waferinforeceives.get(k).getDocNumber().trim());
			waferbankin.setAwb(waferinforeceives.get(k).getAwb().trim());			
			waferbankin.setWaferData(waferinforeceives.get(k).getWaferData().trim());
			waferbankin.setCreatedate(new Date());
			waferbankin.setOperationUnit(waferinforeceives.get(k).getOperationUnit());
			waferbankin.setCloseFlag(false);
			//OCF-PR-150202_以下欄位已刪除故mark_Allison
//			waferbankin.setCustomerJob(waferinforeceives.get(k).getCustomerJob());
//			waferbankin.setCustomerPo(waferinforeceives.get(k).getCustomerPo());
//			waferbankin.setPoItem(waferinforeceives.get(k).getPoItem());
//			waferbankin.setGradeRecord(waferinforeceives.get(k).getGradeRecord());
//			waferbankin.setSourceMtrlNum(waferinforeceives.get(k).getSourceMtrlNum());
//			waferbankin.setEngNo(waferinforeceives.get(k).getEngNo());
//			waferbankin.setTestProgram(waferinforeceives.get(k).getTestProgram());
//			waferbankin.setEsod(waferinforeceives.get(k).getEsod());
//			waferbankin.setWaferDie(waferinforeceives.get(k).getWaferDie());
			waferbankin.setShipComment(waferinforeceives.get(k).getShipComment());

			waferbankin.setUpdateDate(new Date());
			waferbankin.setUpdateUser(waferinforeceives.get(k).getUpdateUser());
			
			this.getWaferBankinDao().create(waferbankin);
			this.createLogging(this.getClass().getName(), "WAFER_BANKIN", LogType.ADD, "", (String)Sessions.getCurrent().getAttribute("loginid"), new Date());
		}
		return updateFlag;
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#beforeSaveCheck(java.lang.Object[])
	 */
//	@Override
//	public String checkDataExistForOMNI(Object[] inObjs) {
//		List<WaferBankin> waferbankins = (List<WaferBankin>) inObjs[0];
//		int checkCount=0;
//		String existMsg="";
//		
//		for(int i=0;i<waferbankins.size();i++){
//			Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
//			log.debug("Check-getCustomerId--->"+waferbankins.get(i).getCustomerId());
//			log.debug("Check-getCustomerLotno--->"+waferbankins.get(i).getCustomerLotno().trim());
//			log.debug("Check-getCustomerJob--->"+waferbankins.get(i).getCustomerJob().trim());
//			log.debug("Check-getWaferData--->"+waferbankins.get(i).getWaferData().trim());
//			criteria.add(Restrictions.eq("customerId",waferbankins.get(i).getCustomerId()));
//			criteria.add(Restrictions.eq("customerLotno",waferbankins.get(i).getCustomerLotno().trim()));
//			criteria.add(Restrictions.eq("customerJob",waferbankins.get(i).getCustomerJob().trim()));
//			criteria.add(Restrictions.eq("waferData",waferbankins.get(i).getWaferData().trim()));
//			//criteria.add(Restrictions.eq("waferInDate",waferbankins.get(i).getWaferInDate()));//IT-PR-140712_Allison無需下到waferInDate和waferInQty來判斷，只要有Customer LotNo來判斷是否有重覆_Allison
//			//criteria.add(Restrictions.eq("waferInQty",waferbankins.get(i).getWaferInQty()));
//			log.debug(criteria.list().size());
//			log.debug(criteria.list());
//			if(criteria.list().size()>0){
//				List<CustomerTable> customerTables = getCustomerShortNameByCustomerId(waferbankins.get(i).getCustomerId());
//				if("".equals(existMsg)){
//					existMsg = customerTables.get(0).getCustomerShortName() + "/"+ waferbankins.get(i).getCustomerLotno().trim()+"/"+ waferbankins.get(i).getCustomerJob().trim() +"/"+ waferbankins.get(i).getWaferData().trim() +"/"+ waferbankins.get(i).getWaferData().trim();					
//				}else{
//					existMsg = existMsg + "\r\n" + customerTables.get(0).getCustomerShortName() +"/"+ waferbankins.get(i).getCustomerLotno().trim()+"/"+ waferbankins.get(i).getCustomerJob().trim() +"/"+ waferbankins.get(i).getWaferData().trim() +"/"+ waferbankins.get(i).getWaferData().trim();
//				}
//				checkCount++;
//			}
//		}
//		log.info("Check...WAFER_BANKIN...done");
//
//		 return existMsg;
//	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#beforeSaveCheck(java.lang.Object[])
	 */
	@Override
	public String checkDataExistNew(Object[] inObjs) {
		List<WaferBankin> waferbankins = (List<WaferBankin>) inObjs[0];
		int checkCount=0;
		String existMsg="";
		
		for(int i=0;i<waferbankins.size();i++){
			Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
			log.debug("Check-getCustomerId--->"+waferbankins.get(i).getCustomerId());
			log.debug("Check-getCustomerLotno--->"+waferbankins.get(i).getCustomerLotno().trim());
			//log.debug("Check-getCustomerJob--->"+waferbankins.get(i).getCustomerJob().trim());
			log.debug("Check-getWaferData--->"+waferbankins.get(i).getWaferData().trim());
			criteria.add(Restrictions.eq("customerId",waferbankins.get(i).getCustomerId()));
			criteria.add(Restrictions.eq("customerLotno",waferbankins.get(i).getCustomerLotno().trim()));
			//criteria.add(Restrictions.eq("customerJob",waferbankins.get(i).getCustomerJob().trim()));
			criteria.add(Restrictions.eq("waferData",waferbankins.get(i).getWaferData().trim()));
			criteria.add(Restrictions.eq("waferReceiveFlag",false));
			//criteria.add(Restrictions.eq("waferInDate",waferbankins.get(i).getWaferInDate()));//IT-PR-140712_Allison無需下到waferInDate和waferInQty來判斷，只要有Customer LotNo來判斷是否有重覆_Allison
			//criteria.add(Restrictions.eq("waferInQty",waferbankins.get(i).getWaferInQty()));
			log.debug(criteria.list().size());
			log.debug(criteria.list());
			if(criteria.list().size()>0){
				List<CustomerTable> customerTables = getCustomerShortNameByCustomerId(waferbankins.get(i).getCustomerId());
				if("".equals(existMsg)){
					//existMsg = customerTables.get(0).getCustomerShortName() + "/"+ waferbankins.get(i).getCustomerLotno().trim()+"/"+ waferbankins.get(i).getCustomerJob().trim() +"/"+ waferbankins.get(i).getWaferData().trim() +"/"+ waferbankins.get(i).getWaferData().trim();
					existMsg = customerTables.get(0).getCustomerShortName() + "/"+ waferbankins.get(i).getCustomerLotno().trim() +"/"+ waferbankins.get(i).getWaferData().trim() +"/"+ waferbankins.get(i).getWaferData().trim();
				}else{
					//existMsg = existMsg + "\r\n" + customerTables.get(0).getCustomerShortName() +"/"+ waferbankins.get(i).getCustomerLotno().trim()+"/"+ waferbankins.get(i).getCustomerJob().trim() +"/"+ waferbankins.get(i).getWaferData().trim() +"/"+ waferbankins.get(i).getWaferData().trim();
					existMsg = existMsg + "\r\n" + customerTables.get(0).getCustomerShortName() +"/"+ waferbankins.get(i).getCustomerLotno().trim()+"/"+ waferbankins.get(i).getWaferData().trim() +"/"+ waferbankins.get(i).getWaferData().trim();
				}
				checkCount++;
			}
		}
		log.info("Check...WAFER_BANKIN...done");

		 return existMsg;
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.cus.service.CustomerInformationService#getCustomerTableByCustomerId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CustomerTable> getCustomerShortNameByCustomerId(String inCustomerId) {
		List<CustomerTable> datas=new ArrayList<CustomerTable>();
		try {
			String hql="select c from CustomerTable c where customerId=:customerId ";
			datas=this.getCustomerTableDao().createQuery(hql)
					.setParameter("customerId", inCustomerId)
					.list();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return datas;
	}
	
	
	//IT-PR-141008_畫面上選Delete，將[WAFER_BANKIN].CLOSE_FLAG=1
	public boolean updateWaferBankinCloseFlag(int inWaferBankinIdx){
		boolean deleteFlag=false;		
		//將先前WAFER_BANKIN資料的CLOSE_FLAG=1
		List<WaferBankin> waferBankinLists = (List<WaferBankin>)this.getWaferBankinDao()
				.createQuery("SELECT l FROM WaferBankin l WHERE waferBankinIdx=:waferBankinIdx ")
				.setParameter("waferBankinIdx", inWaferBankinIdx)
				.list();
		if(waferBankinLists.size() > 0){			
			for(int j=0; j<waferBankinLists.size(); j++){
				waferBankinLists.get(j).setCloseFlag(true);
				waferBankinLists.get(j).setUpdateDate(new Date());
				waferBankinLists.get(j).setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
				this.getWaferBankinDao().update(waferBankinLists.get(j));
				this.createLogging(this.getClass().getName(), "WAFER_BANKIN", LogType.MODIFY, "", (String)Sessions.getCurrent().getAttribute("loginid"), new Date());	
				deleteFlag=true;
			}
		}

		//OCF-PR-160307_同時也將[WAFER_BANKIN_WAFER]資料的CLOSE_FLAG=1
		List<WaferBankinWafer> waferBankinWaferLists = (List<WaferBankinWafer>)this.getWaferBankinWaferDao()
				.createQuery("SELECT l FROM WaferBankinWafer l WHERE waferBankin.waferBankinIdx=:waferBankinIdx ")
				.setParameter("waferBankinIdx", inWaferBankinIdx)
				.list();
		if(waferBankinWaferLists.size() > 0){			
			for(int j=0; j<waferBankinWaferLists.size(); j++){
				waferBankinWaferLists.get(j).setCloseFlag(true);
				waferBankinWaferLists.get(j).setUpdateDate(new Date());
				waferBankinWaferLists.get(j).setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
				this.getWaferBankinWaferDao().update(waferBankinWaferLists.get(j));
				this.createLogging(this.getClass().getName(), "WAFER_BANKIN_WAFER", LogType.MODIFY, "", (String)Sessions.getCurrent().getAttribute("loginid"), new Date());	
				deleteFlag=true;
			}
		}
		
		return deleteFlag;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#checkDataExistNew(java.util.List)
	 */
	@Override
	public String checkDataExistNew(WaferBankin inWaferBankinLists) {
		int checkCount=0;
		String existMsg="";

		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
		log.debug("Check-getCustomerId--->"+inWaferBankinLists.getCustomerId());
		log.debug("Check-getCustomerLotno--->"+inWaferBankinLists.getCustomerLotno().trim());
		log.debug("Check-getWaferData--->"+inWaferBankinLists.getWaferData().trim());
		criteria.add(Restrictions.eq("customerId",inWaferBankinLists.getCustomerId()));
		criteria.add(Restrictions.eq("customerLotno",inWaferBankinLists.getCustomerLotno().trim()));
		criteria.add(Restrictions.eq("waferData",inWaferBankinLists.getWaferData().trim()));
		criteria.add(Restrictions.eq("waferReceiveFlag",false));
		criteria.add(Restrictions.eq("closeFlag",false));

		if(criteria.list().size()>0){
			List<CustomerTable> customerTables = getCustomerShortNameByCustomerId(inWaferBankinLists.getCustomerId());
			if("".equals(existMsg)){
				//existMsg = customerTables.get(0).getCustomerShortName() + "/"+ waferbankins.get(i).getCustomerLotno().trim()+"/"+ waferbankins.get(i).getCustomerJob().trim() +"/"+ waferbankins.get(i).getWaferData().trim() +"/"+ waferbankins.get(i).getWaferData().trim();
				existMsg = customerTables.get(0).getCustomerShortName() + " / "+ inWaferBankinLists.getCustomerLotno().trim() +" / "+ inWaferBankinLists.getWaferData().trim();
			}else{
				//existMsg = existMsg + "\r\n" + customerTables.get(0).getCustomerShortName() +"/"+ waferbankins.get(i).getCustomerLotno().trim()+"/"+ waferbankins.get(i).getCustomerJob().trim() +"/"+ waferbankins.get(i).getWaferData().trim() +"/"+ waferbankins.get(i).getWaferData().trim();
				existMsg = existMsg + "\r\n" + customerTables.get(0).getCustomerShortName() +" / "+ inWaferBankinLists.getCustomerLotno().trim()+" / "+ inWaferBankinLists.getWaferData().trim();
			}
			checkCount++;
		}
		log.info("Check...WAFER_BANKIN...done");

		return existMsg;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateTransactionItems(java.util.List)
	 */
	@Override
	public void updateTransactionItems(List<WaferBankin> inWaferBankinLists) {	
		for(int i=0;i<inWaferBankinLists.size();i++){
			this.getWaferBankinDao().update(inWaferBankinLists.get(i));
		}
		log.info("Update...WAFER_INFO_RECEIVE...done");
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferBankinByPrepare(java.lang.String, java.lang.String)
	 */
	@Override
	public WaferReceiveOperation getWaferBankinByPrepare(String inCustomerLotNo, String inWaferQty) {
		WaferReceiveOperation data = new WaferReceiveOperation();
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
		if(!"".equals(inCustomerLotNo)){
			criteria.add(Restrictions.eq("customerLotno", inCustomerLotNo));
		}
		if(!"".equals(inWaferQty)){
			criteria.add(Restrictions.eq("waferPrepQty", Integer.valueOf(inWaferQty)));
		}

		criteria.add(Restrictions.eq("closeFlag", false));//IT-PR-141008_畫面多Delete的Checkbox，可Query出Close_Flag=1的_Allison add
		criteria.add(Restrictions.eq("waferReceiveFlag", false));//OCF-PR-150202_新增搜尋條件加上[WAFER_BANKIN].WAFER_RECEIVE_FLAG=0_Allison
		criteria.add(Restrictions.eq("waferOutFlag", false));
		if(criteria.list().size() > 0){
			int dataCount=0;
			List<WaferBankin> waferBankInLists = new ArrayList<WaferBankin>();
			for(int i=0; i<criteria.list().size(); i++){
				dataCount = dataCount + 1;
				WaferBankin waferBankIn = (WaferBankin) criteria.list().get(i);
				waferBankInLists.add(waferBankIn);
			}
			data.setDataSelect(dataCount);
			data.setWaferBankin(waferBankInLists);
		}
		return 	data;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#checkExistsWaferInTce(com.tce.ivision.model.WaferBankin)
	 */
	@Override
	public List<WaferBankin> checkExistsWaferInTce(String inCustomerId, String inCustomerLotNo) {	
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);

		criteria.add(Restrictions.eq("customerId", inCustomerId));
		criteria.add(Restrictions.eq("customerLotno", inCustomerLotNo.trim()));
		//OCF-PR-160303_修改為by每片waferData來檢查，檢查已有WAFER_RECEIVE_FLAG=1 & WAFER_OUT_FLAG=0的
		//criteria.add(Restrictions.eq("waferQty", Integer.valueOf(inWaferQty.trim())));//OCF-PR-160303 mark
		//criteria.add(Restrictions.eq("waferData", inWaferData.trim()));//OCF-PR-160303 mark
		criteria.add(Restrictions.eq("waferOutFlag", false));
		criteria.add(Restrictions.eq("waferReceiveFlag", true));
		criteria.add(Restrictions.eq("closeFlag", false));

		return criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#waferInventoryStageLists()
	 */
	@Override
	public List<WaferInventoryStage> waferInventoryStageLists(String inCheckExistTce) {
		Criteria criteria = this.getWaferInventoryStageDao().createCriteria(WaferInventoryStage.class);

		if(!"".equals(inCheckExistTce)){
			criteria.add(Restrictions.eq("inTceFlag", true));
		}
		criteria.add(Restrictions.eq("cancelFlag", false));

		return criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateWaferBankinByWaferReceiveOperation()
	 */
	@Override
	public boolean updateWaferBankinByWaferReceiveOperation(WaferBankin inWaferBankin) {
		boolean saveFlag = false;
		if(inWaferBankin != null){
			this.getWaferBankinDao().update(inWaferBankin);
			
			for(int i=0; i<inWaferBankin.getWaferBankinWafers().size(); i++){
				if(!inWaferBankin.getWaferBankinWafers().get(i).getCloseFlag()){
					this.getWaferBankinWaferDao().update(inWaferBankin.getWaferBankinWafers().get(i));
				}
			}
			
			saveFlag=true;
		}
		return saveFlag;
	}

//	/**
//	 * TODO 簡單描述該方法的實現功能（可選）.
//	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferBankinByManagement(com.tce.ivision.modules.wafer.model.WaferInventoryManagementParameter)
//	 */
//	@Override
//	public List<WaferBankin> getWaferBankinByManagement(WaferInventoryManagementParameter inWaferInventoryManagementParameter) {
//		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
//		
//		if((!"".equals(inWaferInventoryManagementParameter.getReceiveStartDate()) && inWaferInventoryManagementParameter.getReceiveStartDate() != null) && (!"".equals(inWaferInventoryManagementParameter.getReceiveEndDate()) && inWaferInventoryManagementParameter.getReceiveEndDate() != null)){
//			criteria.add(Restrictions.between("waferInDate", inWaferInventoryManagementParameter.getReceiveStartDate(), inWaferInventoryManagementParameter.getReceiveEndDate()));
//		}
//		if(!"".equals(inWaferInventoryManagementParameter.getCustomerId()) && inWaferInventoryManagementParameter.getCustomerId() != null){
//			criteria.add(Restrictions.eq("customerId", inWaferInventoryManagementParameter.getCustomerId()));
//		}
//		if(!"".equals(inWaferInventoryManagementParameter.getCustomerLotNo()) && inWaferInventoryManagementParameter.getCustomerLotNo() != null){
//			criteria.add(Restrictions.like("customerLotno", "%"+inWaferInventoryManagementParameter.getCustomerLotNo()+"%"));
//		}
//		if(!"".equals(inWaferInventoryManagementParameter.getWaferFrom()) && inWaferInventoryManagementParameter.getWaferFrom() != null){
//			criteria.add(Restrictions.eq("waferFrom", inWaferInventoryManagementParameter.getWaferFrom()));
//		}
////		if(!"".equals(inWaferInventoryManagementParameter.getWaferOut()) && inWaferInventoryManagementParameter.getWaferOut() != null){
////			if("Y".equals(inWaferInventoryManagementParameter.getWaferOut())){
////				criteria.add(Restrictions.eq("waferOutFlag", true));
////			}else{
////				criteria.add(Restrictions.eq("waferOutFlag", false));
////			}
////		}
//		criteria.add(Restrictions.eq("closeFlag", false));
//		criteria.add(Restrictions.eq("waferReceiveFlag", true));
//		
//		criteria.addOrder(Order.asc("waferInDate"));
//		
//		return 	criteria.list();
//	}
	
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferBankinByManagement(com.tce.ivision.modules.wafer.model.WaferInventoryManagementParameter)
	 */
	@Override
	public List<WaferInventoryManagement> getWaferBankinByManagement(WaferInventoryManagementParameter inWaferInventoryManagementParameter) {
		List<WaferInventoryManagement> datas=new ArrayList<WaferInventoryManagement>();
		
		String sql="SELECT * FROM VW_WAFER_BANKIN_ORDERHEADER_LOTINFO WHERE WAFER_BANKIN_IDX IS NOT NULL ";
		if((!"".equals(inWaferInventoryManagementParameter.getReceiveStartDate()) && inWaferInventoryManagementParameter.getReceiveStartDate() != null) && (!"".equals(inWaferInventoryManagementParameter.getReceiveEndDate()) && inWaferInventoryManagementParameter.getReceiveEndDate() != null)){
			sql = sql + " AND WAFER_IN_DATE BETWEEN :beginDate AND :endDate ";
		}
		if(!"".equals(inWaferInventoryManagementParameter.getCustomerId()) && inWaferInventoryManagementParameter.getCustomerId() != null){
			sql = sql + " AND CUSTOMER_ID=:customerId ";
		}
		if(!"".equals(inWaferInventoryManagementParameter.getCustomerLotNo()) && inWaferInventoryManagementParameter.getCustomerLotNo() != null){
			sql = sql + "AND CUSTOMER_LOTNO LIKE :customerLotNo";
		}
		if(!"".equals(inWaferInventoryManagementParameter.getWaferFrom()) && inWaferInventoryManagementParameter.getWaferFrom() != null){
			sql = sql + "AND WAFER_FROM LIKE :waferFrom";
		}	
		
		Query query = this.getDao().createSQLQuery(sql);
		
		if((!"".equals(inWaferInventoryManagementParameter.getReceiveStartDate()) && inWaferInventoryManagementParameter.getReceiveStartDate() != null) && (!"".equals(inWaferInventoryManagementParameter.getReceiveEndDate()) && inWaferInventoryManagementParameter.getReceiveEndDate() != null)){
			query.setParameter("beginDate", inWaferInventoryManagementParameter.getReceiveStartDate());
			query.setParameter("endDate", inWaferInventoryManagementParameter.getReceiveEndDate());
		}
		if(!"".equals(inWaferInventoryManagementParameter.getCustomerId()) && inWaferInventoryManagementParameter.getCustomerId() != null){
			query.setParameter("customerId", inWaferInventoryManagementParameter.getCustomerId());
		}
		if(!"".equals(inWaferInventoryManagementParameter.getCustomerLotNo()) && inWaferInventoryManagementParameter.getCustomerLotNo() != null){
			query.setParameter("customerLotNo", "%"+inWaferInventoryManagementParameter.getCustomerLotNo()+"%");
		}
		if(!"".equals(inWaferInventoryManagementParameter.getWaferFrom()) && inWaferInventoryManagementParameter.getWaferFrom() != null){
			query.setParameter("waferFrom", inWaferInventoryManagementParameter.getWaferFrom());
		}	

		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
				int waferBankinIdx=Integer.valueOf((Integer) row.get("WAFER_BANKIN_IDX"));
				
				WaferBankin waferBankin=this.getWaferBankinByIdx(waferBankinIdx);
				
				if (waferBankin != null){
					WaferInventoryManagement data=new WaferInventoryManagement();
					
					data.setProduct((String) row.get("PRODUCT"));
					data.setOcfLotNo((String) row.get("OCF_LOT_NO"));
					data.setWaferBankins(waferBankin);
					
					datas.add(data);
				}
		}
		
		return datas;
	}
	

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateWaferBankinWaferByManagementConfirm(java.util.List)
	 */
	@Override
	public void updateWaferBankinWaferByManagementConfirm(List<WaferBankinWafer> inWaferBankinWafer) {
		for(int i=0; i<inWaferBankinWafer.size(); i++){
			if(!inWaferBankinWafer.get(i).getCloseFlag()){
				this.getWaferBankinWaferDao().update(inWaferBankinWafer.get(i));
			}
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getProductAndOcfLotNoByWaferNo(com.tce.ivision.model.WaferBankin)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getProductAndOcfLotNoByWaferNo(WaferBankin inWaferBankin) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT DISTINCT PRODUCT, OCF_LOT_NO FROM VW_ORDER_LOT_WAFER WHERE CUSTOMER_LOTNO=:CUSTOMER_LOTNO AND WAFER_DATA=:WAFER_DATA ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inWaferBankin.getCustomerLotno());
		query.setParameter("WAFER_DATA", inWaferBankin.getWaferData());
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("PRODUCT", (String) row.get("PRODUCT"));
			data.put("OCF_LOT_NO", (String) row.get("OCF_LOT_NO"));
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferBankinReceiveCountByWaferNo(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<WaferBankin> getWaferBankinReceiveCountByWaferNo(String inCustomerId, String inCustomerLotNo, String inWaferNo) {
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);

		criteria.add(Restrictions.eq("closeFlag", false));
		criteria.add(Restrictions.eq("waferReceiveFlag", true));
		criteria.add(Restrictions.eq("customerId", inCustomerId));
		criteria.add(Restrictions.eq("customerLotno", inCustomerLotNo));
		criteria.createCriteria("waferBankinWafers").add(Restrictions.eq("receiveWaferNo", inWaferNo));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#insertWaferManagementHistoryByWaferReceiveOperation(com.tce.ivision.model.WaferManagementHistory)
	 */
	@Override
	public void insertWaferManagementHistoryByWaferReceiveOperation(WaferManagementHistory inWaferManagementHistory) {
		if(inWaferManagementHistory != null){
			this.getWaferManagementHistoryDao().create(inWaferManagementHistory);
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferBankinHistoryByWaferHistory(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WaferManagementHistory> getWaferBankinHistoryByWaferHistory(String inFrom, String inTo, WaferBankin inWaferBankin) {
		Criteria criteria = this.getWaferManagementHistoryDao().createCriteria(WaferManagementHistory.class);
		
		if(!"All".equals(inFrom)){
			criteria.add(Restrictions.eq("waferInvCodeFrom", inFrom));
		}
		if(!"All".equals(inTo)){
			criteria.add(Restrictions.eq("waferInvCodeTo", inTo));
		}
		criteria.createCriteria("waferBankin").add(Restrictions.eq("waferBankinIdx", inWaferBankin.getWaferBankinIdx()));
		//criteria.add(Restrictions.sizeLe("waferManagementHistories", 1));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getProductAndOcfLotNoByWaferDataAndWaferNo(com.tce.ivision.model.WaferBankin)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getProductAndOcfLotNoByWaferDataAndWaferNo(WaferInvReceiveLot inWaferInvReceiveLot, String inWaferNo) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT DISTINCT PRODUCT, OCF_LOT_NO FROM VW_ORDER_LOT_WAFER WHERE CUSTOMER_LOTNO=:CUSTOMER_LOTNO AND WAFER_DATA=:WAFER_DATA AND WAFER_NO=:WAFER_NO ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inWaferInvReceiveLot.getCustomerLotNo());
		query.setParameter("WAFER_DATA", inWaferInvReceiveLot.getWaferData());
		query.setParameter("WAFER_NO", inWaferNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("PRODUCT", (String) row.get("PRODUCT"));
			data.put("OCF_LOT_NO", (String) row.get("OCF_LOT_NO"));
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getLotResultByVwOrderLotWafer(com.tce.ivision.modules.wafer.model.WaferInvReceiveLot, java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getLotResultByVwOrderLotWafer(WaferInvReceiveLot inWaferInvReceiveLot, String inWaferNo) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT CREATE_DATE, SHIP_DATE FROM VW_ORDER_LOT_WAFER WHERE CUSTOMER_LOTNO=:CUSTOMER_LOTNO AND WAFER_DATA=:WAFER_DATA AND WAFER_NO=:WAFER_NO ORDER BY CREATE_DATE DESC LIMIT 1 ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inWaferInvReceiveLot.getCustomerLotNo());
		query.setParameter("WAFER_DATA", inWaferInvReceiveLot.getWaferData());
		query.setParameter("WAFER_NO", inWaferNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			if((Date) row.get("CREATE_DATE") != null){
				data.put("CREATE_DATE", DateFormatUtil.getDateTimeFormat().format((Date) row.get("CREATE_DATE")));
			}else{
				data.put("CREATE_DATE", "-");
			}
			if((Date) row.get("SHIP_DATE") != null){
				data.put("SHIP_DATE", DateFormatUtil.getDateTimeFormat().format((Date) row.get("SHIP_DATE")));
			}else{
				data.put("SHIP_DATE", "");
			}
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferBankinWaferByWaferNo(java.lang.String)
	 */
	@Override
	public List<WaferBankinWafer> getWaferBankinWaferByWaferNo(String inWaferNo) {
		Criteria criteria = this.getWaferBankinWaferDao().createCriteria(WaferBankinWafer.class);
		criteria.add(Restrictions.eq("receiveWaferNo", inWaferNo));
		criteria.add(Restrictions.eq("closeFlag", false));
		
		List<String> inventoryCodeList = new ArrayList<String>();
		inventoryCodeList.add("S6");
		inventoryCodeList.add("S7");
		inventoryCodeList.add("S8");
		inventoryCodeList.add("S9");
		inventoryCodeList.add("SA");
		criteria.add(Restrictions.in("inventoryCode", inventoryCodeList));
		
		criteria.addOrder(Order.desc("updateDate"));
		criteria.setMaxResults(1);
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferBankinByMaintenance(com.tce.ivision.modules.wafer.model.WaferInventoryManagementParameter)
	 */
	@Override
	public List<WaferBankin> getWaferBankinByMaintenance(WaferStatusMaintenanceParameter inWaferStatusMaintenanceParameter) {
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
		
		if((!"".equals(inWaferStatusMaintenanceParameter.getReceiveStartDate()) && inWaferStatusMaintenanceParameter.getReceiveStartDate() != null) && (!"".equals(inWaferStatusMaintenanceParameter.getReceiveEndDate()) && inWaferStatusMaintenanceParameter.getReceiveEndDate() != null)){
			criteria.add(Restrictions.between("waferInDate", inWaferStatusMaintenanceParameter.getReceiveStartDate(), inWaferStatusMaintenanceParameter.getReceiveEndDate()));
		}
		if(!"".equals(inWaferStatusMaintenanceParameter.getCustomerId()) && inWaferStatusMaintenanceParameter.getCustomerId() != null){
			criteria.add(Restrictions.eq("customerId", inWaferStatusMaintenanceParameter.getCustomerId()));
		}
		if(!"".equals(inWaferStatusMaintenanceParameter.getCustomerLotNo()) && inWaferStatusMaintenanceParameter.getCustomerLotNo() != null){
			criteria.add(Restrictions.like("customerLotno", "%"+inWaferStatusMaintenanceParameter.getCustomerLotNo()+"%"));
		}
		if(!"".equals(inWaferStatusMaintenanceParameter.getWaferFrom()) && inWaferStatusMaintenanceParameter.getWaferFrom() != null){
			criteria.add(Restrictions.eq("waferFrom", inWaferStatusMaintenanceParameter.getWaferFrom()));
		}
//		if(!"".equals(inWaferStatusMaintenanceParameter.getWaferOut()) && inWaferStatusMaintenanceParameter.getWaferOut() != null){
//			if("Y".equals(inWaferStatusMaintenanceParameter.getWaferOut())){
//				criteria.add(Restrictions.eq("waferOutFlag", true));
//			}else{
//				criteria.add(Restrictions.eq("waferOutFlag", false));
//			}
//		}
		criteria.add(Restrictions.eq("closeFlag", false));
		criteria.add(Restrictions.eq("waferReceiveFlag", true));
		
		criteria.addOrder(Order.desc("waferInDate"));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateWaferBankinWaferByMaintenanceConfirm(java.util.List)
	 */
	@Override
	public void updateWaferBankinWaferByMaintenanceConfirm(List<WaferBankinWafer> inWaferBankinWafer) {
		for(int i=0; i<inWaferBankinWafer.size(); i++){
			if(!inWaferBankinWafer.get(i).getCloseFlag()){
				this.getWaferBankinWaferDao().update(inWaferBankinWafer.get(i));
			}
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#insertWaferManagementHistoryByWaferStatusMaintenance(com.tce.ivision.model.WaferManagementHistory)
	 */
	@Override
	public void insertWaferManagementHistoryByWaferStatusMaintenance(WaferManagementHistory inWaferManagementHistory) {
		if(inWaferManagementHistory != null){
			this.getWaferManagementHistoryDao().create(inWaferManagementHistory);
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateWaferBankinByMaintenanceConfirm(com.tce.ivision.model.WaferBankin)
	 */
	@Override
	public void updateWaferBankinByMaintenanceConfirm(WaferBankin inWaferBankin) {
		if(inWaferBankin != null){
			this.getWaferBankinDao().update(inWaferBankin);
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#listBySearchByOe(com.tce.ivision.modules.oe.model.WaferFilter)
	 */
	@Override
	public List<WaferBankin> listBySearchByOe(WaferFilter inWaferfilter) {	
		Criteria criteria = this.getWaferBankinDao().createCriteria(WaferBankin.class);
		if(!"-".equals(inWaferfilter.getCustomerfilter())){
			criteria.add(Restrictions.eq("customerId",inWaferfilter.getCustomerfilter()));
		}
		
		if(!"-".equals(inWaferfilter.getWaferDataFilter()) && inWaferfilter.getWaferDataFilter() != null){
			criteria.add(Restrictions.eq("waferData",inWaferfilter.getWaferDataFilter()));
		}
		if(!"-".equals(inWaferfilter.getCustomerlotnofilter())){
			criteria.add(Restrictions.like("customerLotno","%"+inWaferfilter.getCustomerlotnofilter()+"%"));
		}
		
		//OCF-PR-150202_OE在搜尋[WAFER_BANKIN]時，需加上搜尋條件如下：
		criteria.add(Restrictions.eq("closeFlag", false));
		criteria.add(Restrictions.eq("waferOutFlag", false));
		criteria.add(Restrictions.eq("waferReceiveFlag", true));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOeLotInfoByCustomerLotNo(java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getOeLotInfoByCustomerLotNo(String inCustomerLotNo) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT WAFER_NO, PO_NUMBER, LOTRESULT_SHIPPING_FLAG, LOT_SHIPPING_FLAG FROM VW_ORDER_LOT_WAFER WHERE CUSTOMER_LOTNO=:CUSTOMER_LOTNO ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("WAFER_NO", (String) row.get("WAFER_NO"));
			data.put("PO_NUMBER", (String) row.get("PO_NUMBER"));
			data.put("LOTRESULT_SHIPPING_FLAG", String.valueOf((Boolean) row.get("LOTRESULT_SHIPPING_FLAG")));
			data.put("LOT_SHIPPING_FLAG", String.valueOf((Boolean) row.get("LOT_SHIPPING_FLAG")));
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getShippingWaferInfoByCustomerLotNo(java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getShippingWaferInfoByCustomerLotNo(String inCustomerLotNo) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT CUSTOMER_LOTNO, WAFER_NO FROM VW_SHIPPING_WAFERINFO WHERE CUSTOMER_LOTNO=:CUSTOMER_LOTNO ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("WAFER_NO", (String) row.get("WAFER_NO"));
			data.put("CUSTOMER_LOTNO", (String) row.get("CUSTOMER_LOTNO"));
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOeDataByCustomerLotNo(java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getOeDataByCustomerLotNo(String inCustomerLotNo) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT CUSTOMER_LOTNO, PO_NUMBER, ORDER_LINE_LOTNO_IDX, WAFER_DATA FROM VW_ORDER_HEADER_LINE_LINELOTNO WHERE CUSTOMER_LOTNO=:CUSTOMER_LOTNO ORDER BY ORDER_DATE DESC ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("WAFER_DATA", (String) row.get("WAFER_DATA"));
			data.put("CUSTOMER_LOTNO", (String) row.get("CUSTOMER_LOTNO"));
			data.put("PO_NUMBER", (String) row.get("PO_NUMBER"));
			data.put("ORDER_LINE_LOTNO_IDX", String.valueOf((Integer) row.get("ORDER_LINE_LOTNO_IDX")));
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOeOrderNumberByCustomerLotNo(java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getOeOrderNumberByCustomerLotNo(String inCustomerLotNo, String inWaferNo) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		//String sql=" SELECT ORDER_NUMBER, WAFER_DATA FROM VW_ORDER_HEADER_LINE_LINELOTNO WHERE CUSTOMER_LOTNO=:CUSTOMER_LOTNO AND (ORDER_STATUS='10' OR ORDER_STATUS='20') ORDER BY ORDER_DATE DESC ";
		String sql="";
		sql+="SELECT "; 
		sql+="    O.* ";
		sql+="FROM ";
		sql+="    (SELECT  ";
		sql+="        O.ORDER_NUMBER, ";
		sql+="            CASE ";
		sql+="                WHEN O.SLOT > 9 THEN CONCAT(O.CUSTOMER_LOTNO, '-', O.SLOT) ";
		sql+="                ELSE CONCAT(O.CUSTOMER_LOTNO, '-0', O.SLOT) ";
		sql+="            END WAFER_NO ";
		sql+="    FROM ";
		sql+="        (SELECT  ";
		sql+="        OE.ORDER_NUMBER, ";
		sql+="            OE.CUSTOMER_LOTNO, ";
		sql+="            SUBSTRING_INDEX(SUBSTRING_INDEX(OE.WAFER_DATA, ';', n.r), ';', - 1) SLOT ";
		sql+="    FROM ";
		sql+="        (SELECT  ";
		sql+="        ORDER_NUMBER, WAFER_DATA, CUSTOMER_LOTNO ";
		sql+="    FROM ";
		sql+="        VW_ORDER_HEADER_LINE_LINELOTNO ";
		sql+="    WHERE ";
		sql+="        CUSTOMER_LOTNO=:CUSTOMER_LOTNO ";
		sql+="            AND (ORDER_STATUS = '10' ";
		sql+="            OR ORDER_STATUS = '20') ";
		sql+="    ORDER BY ORDER_DATE DESC) OE ";
		sql+="    JOIN (SELECT  ";
		sql+="        @rownum/*'*/:=/*'*/@rownum + 1 AS 'r' ";
		sql+="    FROM ";
		sql+="        (SELECT 1 no UNION ALL SELECT 2 no UNION ALL SELECT 3 no UNION ALL SELECT 4 no UNION ALL SELECT 5 no UNION ALL SELECT 6 no UNION ALL SELECT 7 no UNION ALL SELECT 8 no UNION ALL SELECT 9 no UNION ALL SELECT 10 no UNION ALL SELECT 11 no UNION ALL SELECT 12 no UNION ALL SELECT 13 no UNION ALL SELECT 14 no UNION ALL SELECT 15 no UNION ALL SELECT 16 no UNION ALL SELECT 17 no UNION ALL SELECT 18 no UNION ALL SELECT 19 no UNION ALL SELECT 20 no UNION ALL SELECT 21 no UNION ALL SELECT 22 no UNION ALL SELECT 23 no UNION ALL SELECT 24 no UNION ALL SELECT 25 no) a, (SELECT @rownum/*'*/:=/*'*/0) b) n ON CHAR_LENGTH(OE.WAFER_DATA) - CHAR_LENGTH(REPLACE(OE.WAFER_DATA, ';', '')) >= n.r - 1 ";
		sql+="    ORDER BY OE.ORDER_NUMBER , OE.CUSTOMER_LOTNO , n.r) O) O ";
		sql+="WHERE ";
		sql+="    O.WAFER_NO =:WAFER_NO ";
		
		
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
		//query.setParameter("CUSTOMER_LOTNO2", inCustomerLotNo);
		query.setParameter("WAFER_NO", inWaferNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		//OCF-PR-150606_修改先用CustomerLotNo找orderStatus=10, 20的資料，再比對是否與這次收到的waferData有符合，有符合的再update [WAFER_BANKIN_WAFER].ORDER_NUMBER
		boolean flag=false;
		if(tmpList.size() > 0){
			{
				Map<String,String> row = (Map<String,String>)tmpList.get(0);
				LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
				
				//String[] splitWaferData = inWaferData.split("-");
				//String[] splitOeWaferData = String.valueOf((String) row.get("WAFER_DATA")).split(";");
				//if(inWaferData.equals((String) row.get("WAFER_NO"))){
						data.put("ORDER_NUMBER", (String) row.get("ORDER_NUMBER"));
					    data.put("WAFER_DATA", (String) row.get("WAFER_NO"));
						list.add(data);						
						flag=true;
						//break;
				//}
				
				
			}
		}
		if(flag){
			sql="";	
			sql+=" SELECT  ";
			sql+="     S.* ";
			sql+=" FROM ";
			sql+="     (SELECT  ";
			sql+="         S.*, ";
			sql+="             CASE ";
			sql+="                 WHEN S.SLOT > 9 THEN CONCAT(S.CUSTOMER_LOTNO, '-', S.SLOT) ";
			sql+="                 ELSE CONCAT(S.CUSTOMER_LOTNO, '-0', S.SLOT) ";
			sql+="             END WAFER_NO ";
			sql+="     FROM ";
			sql+="         (SELECT  ";
		sql+="        SA.PO_NUMBER, ";
		sql+="            SA.CUSTOMER_LOTNO, ";
		sql+="            SA.OCF_LOTNO, ";
		sql+="            SUBSTRING_INDEX(SUBSTRING_INDEX(SA.WAFER_DATA, ';', n.r), ';', - 1) SLOT ";
		sql+="    FROM ";
		sql+="        SHIPPING S ";
		sql+="    JOIN SHIPPING_DETAIL SD ON S.SHIPPING_IDX = SD.SHIPPING_IDX ";
		sql+="    JOIN SHIPPING_ARRANGEMENT SA ON LEFT(SD.INAVI_LOTNO, 13) = SA.OCF_LOTNO ";
		sql+="        AND SD.SHIP_QTY = SA.WAFER_QTY ";
		sql+="    JOIN (SELECT  ";
		sql+="        @rownum/*'*/:=/*'*/@rownum + 1 AS 'r' ";
		sql+="    FROM ";
		sql+="        (SELECT 1 no UNION ALL SELECT 2 no UNION ALL SELECT 3 no UNION ALL SELECT 4 no UNION ALL SELECT 5 no UNION ALL SELECT 6 no UNION ALL SELECT 7 no UNION ALL SELECT 8 no UNION ALL SELECT 9 no UNION ALL SELECT 10 no UNION ALL SELECT 11 no UNION ALL SELECT 12 no UNION ALL SELECT 13 no UNION ALL SELECT 14 no UNION ALL SELECT 15 no UNION ALL SELECT 16 no UNION ALL SELECT 17 no UNION ALL SELECT 18 no UNION ALL SELECT 19 no UNION ALL SELECT 20 no UNION ALL SELECT 21 no UNION ALL SELECT 22 no UNION ALL SELECT 23 no UNION ALL SELECT 24 no UNION ALL SELECT 25 no) a, (SELECT @rownum/*'*/:=/*'*/0) b) n ON CHAR_LENGTH(SA.WAFER_DATA) - CHAR_LENGTH(REPLACE(SA.WAFER_DATA, ';', '')) >= n.r - 1 ";
		sql+="    WHERE ";
		sql+="        S.PL_CANCEL_FLAG = 0 ";
		sql+="            AND S.PACKING_CFM_FLAG = 1 ";
		sql+="            AND SA.CANCEL_FLAG = 0 ";
		sql+="            AND SA.CUSTOMER_LOTNO=:CUSTOMER_LOTNO ";
			sql+="             AND SA.PO_NUMBER=:ORDER_NUMBER ";
			sql+="     ORDER BY SA.PO_NUMBER , SA.CUSTOMER_LOTNO , SA.OCF_LOTNO , n.r) S ";
		sql+="    WHERE ";
			sql+="         S.SLOT IS NOT NULL) S ";
		sql+="WHERE ";
			sql+="     S.WAFER_NO =:WAFER_NO ";
			
			query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
			query.setParameter("ORDER_NUMBER", (String)list.get(0).get("ORDER_NUMBER"));
		query.setParameter("WAFER_NO", inWaferNo);
		
			tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();			
		if(tmpList.size() > 0){
				flag=false;
				list = new ArrayList<LinkedHashMap<String, String>>();
			}
		}
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOrderHeaderAndLotInfoByOrderNumber(java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getOrderHeaderAndLotInfoByOrderNumber(String inOrderNumber, String inCustomerLotNo) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" select a.PRODUCT, (SELECT b.OCF_LOT_NO FROM LOT_INFO b, ORDER_LINE_LOTNO c WHERE a.ORDER_NUMBER = b.ORDER_NUMBER AND c.ORDER_NUMBER = a.ORDER_NUMBER AND (b.CUSTOMER_LOTNO1 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO2 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO3 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO4 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO5 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO6 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO7 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO8 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO9 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO10 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO11 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO12 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO13 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO14 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO15 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO16 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO17 = c.CUSTOMER_LOTNO) AND b.ORDER_NUMBER = c.ORDER_NUMBER AND c.CUSTOMER_LOTNO=:CUSTOMER_LOTNO or b.CUSTOMER_LOTNO18 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO19 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO20 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO21 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO22 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO23 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO24 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO25 = c.CUSTOMER_LOTNO) AS OCF_LOT_NO from ORDER_HEADER a WHERE a.order_number in (" + inOrderNumber + ") ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("PRODUCT", (String) row.get("PRODUCT"));
			if((String) row.get("OCF_LOT_NO") != null){
				data.put("OCF_LOT_NO", (String) row.get("OCF_LOT_NO"));
			}else{
				data.put("OCF_LOT_NO", "");
			}
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOeByCustomerLotNo(java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getOeByCustomerLotNo(String inOrderNumber) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT A.WAFER_DATA, PO_NUMBER, ";
		   sql = sql + " (SELECT GROUP_CONCAT(C.SHIPPING_WAFER_DATA SEPARATOR ';') AS SHIPPING_WAFER_DATA FROM OFFLOAD_LOTNO B, OFFLOAD_SHIPPING C WHERE A.ORDER_NUMBER = B.ORDER_NUMBER AND A.CUSTOMER_LOTNO = B.CUSTOMER_LOTNO AND B.OFFLOAD_LOTNO_IDX = C.OFFLOAD_LOTNO_IDX AND B.CANCEL_FLAG =0) AS OFFLOAD_SHIPPING_WAFER_DATA ";
		   sql = sql + " FROM VW_ORDER_HEADER_LINE_LINELOTNO A WHERE ORDER_NUMBER=:ORDER_NUMBER";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("ORDER_NUMBER", inOrderNumber);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("WAFER_DATA", (String) row.get("WAFER_DATA"));
			data.put("PO_NUMBER", (String) row.get("PO_NUMBER"));
			data.put("OFFLOAD_SHIPPING_WAFER_DATA", (String) row.get("OFFLOAD_SHIPPING_WAFER_DATA"));
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOrderHeaderAndLotInfoByOrderNumber(java.util.List)
	 */
	@Override
	public List<WaferInventoryManagement> getOrderHeaderAndLotInfoByOrderNumber(List<WaferInventoryManagement> inWaferInventoryManagement) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		String composeOrderNumber="";
		
		for(int j=0; j<inWaferInventoryManagement.size(); j++){
			composeOrderNumber="";
			if(inWaferInventoryManagement.get(j).getWaferBankins().getWaferBankinWafers().size() > 0){
				for (int k=0;k<inWaferInventoryManagement.get(j).getWaferBankins().getWaferBankinWafers().size();k++){
					if(inWaferInventoryManagement.get(j).getWaferBankins().getWaferBankinWafers().get(k).getOrderNumber() != null){
						composeOrderNumber+="'"+inWaferInventoryManagement.get(j).getWaferBankins().getWaferBankinWafers().get(k).getOrderNumber()+"',";
					}
				}
			}
			if(!"".equals(composeOrderNumber)){
				composeOrderNumber=composeOrderNumber.substring(0,composeOrderNumber.length()-1);
			}else{
				composeOrderNumber="''";
			}
			
			String sql=" select a.PRODUCT, (SELECT b.OCF_LOT_NO FROM LOT_INFO b, ORDER_LINE_LOTNO c WHERE a.ORDER_NUMBER = b.ORDER_NUMBER AND c.ORDER_NUMBER = a.ORDER_NUMBER AND (b.CUSTOMER_LOTNO1 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO2 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO3 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO4 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO5 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO6 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO7 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO8 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO9 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO10 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO11 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO12 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO13 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO14 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO15 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO16 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO17 = c.CUSTOMER_LOTNO) AND b.ORDER_NUMBER = c.ORDER_NUMBER AND c.CUSTOMER_LOTNO=:CUSTOMER_LOTNO or b.CUSTOMER_LOTNO18 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO19 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO20 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO21 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO22 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO23 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO24 = c.CUSTOMER_LOTNO or b.CUSTOMER_LOTNO25 = c.CUSTOMER_LOTNO) AS OCF_LOT_NO from ORDER_HEADER a WHERE a.order_number in (" + composeOrderNumber + ") ";
			Query query = this.getDao().createSQLQuery(sql);
			query.setParameter("CUSTOMER_LOTNO", inWaferInventoryManagement.get(j).getWaferBankins().getCustomerLotno());

			List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			for(int i = 0; i < tmpList.size(); i++ ){
				Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
				
				inWaferInventoryManagement.get(j).setProduct((String) row.get("PRODUCT"));
				inWaferInventoryManagement.get(j).setOcfLotNo((String) row.get("OCF_LOT_NO"));
				
			}
		}
		
		return inWaferInventoryManagement;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferBankinByIdx(int)
	 */
	@Override
	public WaferBankin getWaferBankinByIdx(int inWaferBankinIdx) {
		List<WaferBankin> datas=new ArrayList<WaferBankin>();
		String hql="select c from WaferBankin c where waferBankinIdx=:idx ";
		datas=this.getOrderHeaderDao().createQuery(hql)
					.setParameter("idx", inWaferBankinIdx)
					.list();
		
		if (datas.size()>0){
			return datas.get(0);
		}
		else{
			return null;
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOeWithOrderStatusByCustomerLotNo(java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getOeWithOrderStatusByCustomerLotNo(String inCustomerLotNo) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT CUSTOMER_LOTNO, PO_NUMBER, ORDER_LINE_LOTNO_IDX, WAFER_DATA FROM VW_ORDER_HEADER_LINE_LINELOTNO WHERE CUSTOMER_LOTNO=:CUSTOMER_LOTNO AND (ORDER_STATUS = '10' OR ORDER_STATUS = '20') ORDER BY ORDER_DATE DESC ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("CUSTOMER_LOTNO", inCustomerLotNo);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("WAFER_DATA", (String) row.get("WAFER_DATA"));
			data.put("CUSTOMER_LOTNO", (String) row.get("CUSTOMER_LOTNO"));
			data.put("PO_NUMBER", (String) row.get("PO_NUMBER"));
			data.put("ORDER_LINE_LOTNO_IDX", String.valueOf((Integer) row.get("ORDER_LINE_LOTNO_IDX")));
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOeLotInfoByOrderNumber(java.lang.String)
	 */
	@Override
	public List<LinkedHashMap<String, String>> getOeLotInfoByOrderNumber(String inOrderNumber) {
		List<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		
		String sql=" SELECT WAFER_NO, PO_NUMBER, LOTRESULT_SHIPPING_FLAG, LOT_SHIPPING_FLAG FROM VW_ORDER_LOT_WAFER WHERE ORDER_NUMBER=:ORDER_NUMBER ";
		Query query = this.getDao().createSQLQuery(sql);
		query.setParameter("ORDER_NUMBER", inOrderNumber);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
			data.put("WAFER_NO", (String) row.get("WAFER_NO"));
			data.put("PO_NUMBER", (String) row.get("PO_NUMBER"));
			data.put("LOTRESULT_SHIPPING_FLAG", String.valueOf((Boolean) row.get("LOTRESULT_SHIPPING_FLAG")));
			data.put("LOT_SHIPPING_FLAG", String.valueOf((Boolean) row.get("LOT_SHIPPING_FLAG")));
			
			list.add(data);
		}
		
		return list;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvReceiveWafer(java.lang.String)
	 */
	@Override
	public List<WaferInvReceiveWafer> getWaferInvReceiveWafer(String inWaferBankinIdxs) {
		List<WaferInvReceiveWafer> list = new ArrayList<WaferInvReceiveWafer>();
		
		String sql=" SELECT * FROM VW_WAFER_BANKIN_WAFER_ORDERHEADER_LOTINFO WHERE WAFER_BANKIN_IDX IN (" + inWaferBankinIdxs + ") ";
		Query query = this.getDao().createSQLQuery(sql);
		log.debug(sql);
		
		List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		
		String[] aryRawStatus = {"BANK"};
		String[] aryBank = getStagebystatus(aryRawStatus);
		
		String[] arySemiStatus = {"PROD","ENG","SCRAP"};
		String[] aryWip = getStagebystatus(arySemiStatus);
		
		String[] aryFinishStatus = {"BDNORMAL","BDRMA","NBDRMA","CUSSCRAP"};
		String[] aryFg = getStagebystatus(aryFinishStatus);
		
		for(int i = 0; i < tmpList.size(); i++ ){
			Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
			WaferInvReceiveWafer data = new WaferInvReceiveWafer();
			data.setReceiveDate((Date) row.get("RECEIVE_DATE"));
			data.setCustomer((String) row.get("CUSTOMER_ID"));
			data.setWaferFrom((String) row.get("WAFER_FROM"));
			data.setCustomerLotNo((String) row.get("CUSTOMER_LOTNO"));
			data.setWaferNo((String) row.get("RECEIVE_WAFER_NO"));
			data.setProduct((String) row.get("PRODUCT"));
			data.setOcfLotNo((String) row.get("OCF_LOT_NO"));
			data.setStage((String) row.get("INVENTORY_CODE"));
			if((Date) row.get("WAFER_IN_DATE") != null){
				data.setWaferInDate(DateFormatUtil.getDateTimeFormat().format((Date) row.get("WAFER_IN_DATE")));
			}else{
				data.setWaferInDate("-");
			}
			if((Date) row.get("WAFER_OUT_SHIPPING_DATE") != null){
				data.setWaferOutShippingDate(DateFormatUtil.getDateTimeFormat().format((Date) row.get("WAFER_OUT_SHIPPING_DATE")));
			}else{
				data.setWaferOutShippingDate("-");
			}
			data.setManagementUpdateComment((String) row.get("WAFER_MANAGEMENT_COMMENT"));
			if((Date) row.get("WAFER_MANAGEMENT_UPDATE_DATE") != null){
				data.setManagementUpdateDate(DateFormatUtil.getDateTimeFormat().format((Date) row.get("WAFER_MANAGEMENT_UPDATE_DATE")));
			}else{
				data.setManagementUpdateDate("-");
			}
			data.setManagementUpdateUser((String) row.get("WAFER_MANAGEMENT_UPDATE_USER"));
			/*
			 * OCFPR170202 
			 */
			if(BeanUtil.isNotNull(row.get("RAW_MATERIAL_NAME"))){
				data.setRawMaterialName((String) row.get("RAW_MATERIAL_NAME"));
			}
			if(BeanUtil.isNotNull(row.get("SEMI_FINISH_GOODS_NAME"))){
				data.setSemiFinishGoodsName((String) row.get("SEMI_FINISH_GOODS_NAME"));
			}
			if(BeanUtil.isNotNull(row.get("FINISH_GOODS_NAME"))){
				data.setFinishGoodsName((String) row.get("FINISH_GOODS_NAME"));
			}
			//log.debug(data.toString());
			if(BeanUtil.exist(aryBank, data.getStage())){
				data.setBondedInvName(data.getRawMaterialName());
			}else if(BeanUtil.exist(aryWip, data.getStage())){
				data.setBondedInvName(data.getSemiFinishGoodsName());
			}else if(BeanUtil.exist(aryFg, data.getStage())){
				data.setBondedInvName(data.getFinishGoodsName());
			}
			
			list.add(data);
		}
		
		return list;
	}
	
	/**
	 * getStagebystatus:(這裡用一句話描述這個方法的作用). <br/>
	 * TODO(這裡描述這個方法適用條件 – 可選).<br/>
	 * TODO(這裡描述這個方法的執行流程 – 可選).<br/>
	 * TODO(這裡描述這個方法的使用方法 – 可選).<br/>
	 * TODO(這裡描述這個方法的注意事項 – 可選).<br/>
	 *
	 * @author 160950
	 * @return
	 * @since JDK 1.6
	 */
	private String[] getStagebystatus(String[] aryStatus) {
		//String[] aryRawStatus = {"BANK"};
		Criteria criteria = this.getWaferInventoryStageDao().createCriteria(WaferInventoryStage.class).add(Restrictions.eq("inTceFlag", true)).add(Restrictions.and
		(
		                Restrictions.in("status", aryStatus),
		                Restrictions.eq("cancelFlag", false)		                
		));		
		List<WaferInventoryStage> datas = criteria.list();	
		String[] aryStages = new String[datas.size()];	
		for (int i = 0;i<datas.size();i++) {
			WaferInventoryStage bean = datas.get(i);
			aryStages[i] = bean.getStage();
		}
		return aryStages;
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferStatusByCustomerLotNoAndOrderNumber(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WaferStatus> getWaferStatusByCustomerLotNoAndOrderNumber(String inCustomerLotNo, String inOrderNumber) {
		Criteria criteria = this.getWaferStatusDao().createCriteria(WaferStatus.class);	
		criteria.add(Restrictions.eq("customerLotno", inCustomerLotNo));
		criteria.add(Restrictions.eq("orderNumber", inOrderNumber));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateWaferStatusByWaferReceiveOperation()
	 */
	@Override
	public void updateWaferStatusByWaferReceiveOperation(List<WaferStatus> inWaferStatusLists) {
		if(inWaferStatusLists.size() > 0){
			for(int i=0; i<inWaferStatusLists.size(); i++){
				this.getWaferStatusDao().update(inWaferStatusLists.get(i));
			}
		}
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#saveWaferInvRmnameSetup(java.util.List)
	 */
	@Override
	public boolean saveWaferInvRmnameSetup(WaferInvRmnameSetup inWaferInvRmnameSetup) {
		boolean saveflag = true;
		
		if(inWaferInvRmnameSetup.getWaferInvRmnameSetupIdx() != null){
			Date txnSendDate = new Date();
			boolean txnSendFlag = false;
			
			String sql=" SELECT * FROM WAFER_INV_RMNAME_SETUP WHERE WAFER_INV_RMNAME_SETUP_IDX=:WAFER_INV_RMNAME_SETUP_IDX ";
			Query query = this.getDao().createSQLQuery(sql);
			query.setParameter("WAFER_INV_RMNAME_SETUP_IDX", inWaferInvRmnameSetup.getWaferInvRmnameSetupIdx());
			
			List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			for(int i = 0; i < tmpList.size(); i++ ){
				Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
				txnSendFlag = (Boolean) row.get("TXN_SEND_FLAG");
				txnSendDate = (Date) row.get("TXN_SEND_DATE");
			}
			
			inWaferInvRmnameSetup.setTxnSendDate(txnSendDate);
			inWaferInvRmnameSetup.setTxnSendFlag(txnSendFlag);
			inWaferInvRmnameSetup.setUpdateDate(new Date());
			inWaferInvRmnameSetup.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			this.getWaferInvRmnameSetupDao().update(inWaferInvRmnameSetup);
		}else{
			inWaferInvRmnameSetup.setUpdateDate(new Date());
			inWaferInvRmnameSetup.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			inWaferInvRmnameSetup.setCreateDate(new Date());
			inWaferInvRmnameSetup.setCreateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			this.getWaferInvRmnameSetupDao().create(inWaferInvRmnameSetup);
		}
		
		return saveflag;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvRmnameSetupLists()
	 */
	@Override
	public List<WaferInvRmnameSetup> getWaferInvRmnameSetupLists() {
		Criteria criteria = this.getWaferInvRmnameSetupDao().createCriteria(WaferInvRmnameSetup.class);	
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvRmnameMappingLits()
	 */
	@Override
	public List<WaferInvRmnameMapping> getWaferInvRmnameMappingLits() {
		Criteria criteria = this.getWaferInvRmnameMappingDao().createCriteria(WaferInvRmnameMapping.class);	
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#saveWaferInvRmnameMapping(com.tce.ivision.model.WaferInvRmnameMapping)
	 */
	@Override
	public boolean saveWaferInvRmnameMapping(WaferInvRmnameMapping inWaferInvRmnameMapping) {
		boolean saveflag = true;
		
		if(inWaferInvRmnameMapping.getWaferInvRmnameMappingIdx() != null){			
			inWaferInvRmnameMapping.setUpdateDate(new Date());
			inWaferInvRmnameMapping.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			this.getWaferInvRmnameMappingDao().update(inWaferInvRmnameMapping);
		}else{
			inWaferInvRmnameMapping.setUpdateDate(new Date());
			inWaferInvRmnameMapping.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			inWaferInvRmnameMapping.setCreateDate(new Date());
			inWaferInvRmnameMapping.setCreateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			this.getWaferInvRmnameMappingDao().create(inWaferInvRmnameMapping);
		}
		
		return saveflag;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvFgnameSetupLists()
	 */
	@Override
	public List<WaferInvFgnameSetup> getWaferInvFgnameSetupLists() {
		Criteria criteria = this.getWaferInvFgnameSetupDao().createCriteria(WaferInvFgnameSetup.class);	
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvFgnameMappingLits()
	 */
	@Override
	public List<WaferInvFgnameMapping> getWaferInvFgnameMappingLits() {
		Criteria criteria = this.getWaferInvFgnameMappingDao().createCriteria(WaferInvFgnameMapping.class);	
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#saveWaferInvFgnameSetup(com.tce.ivision.model.WaferInvFgnameSetup)
	 */
	@Override
	public boolean saveWaferInvFgnameSetup(WaferInvFgnameSetup inWaferInvFgnameSetup) {
		boolean saveflag = true;
		
		if(inWaferInvFgnameSetup.getWaferInvFgnameSetupIdx() != null){
			Date txnSendDate = new Date();
			boolean txnSendFlag = false;
			
			String sql=" SELECT * FROM WAFER_INV_FGNAME_SETUP WHERE WAFER_INV_FGNAME_SETUP_IDX=:WAFER_INV_FGNAME_SETUP_IDX ";
			Query query = this.getDao().createSQLQuery(sql);
			query.setParameter("WAFER_INV_FGNAME_SETUP_IDX", inWaferInvFgnameSetup.getWaferInvFgnameSetupIdx());
			
			List<Object> tmpList = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			for(int i = 0; i < tmpList.size(); i++ ){
				Map<String,Object> row = (Map<String,Object>)tmpList.get(i);
				txnSendFlag = (Boolean) row.get("TXN_SEND_FLAG");
				txnSendDate = (Date) row.get("TXN_SEND_DATE");
			}
			
			inWaferInvFgnameSetup.setTxnSendDate(txnSendDate);
			inWaferInvFgnameSetup.setTxnSendFlag(txnSendFlag);	
			inWaferInvFgnameSetup.setUpdateDate(new Date());
			inWaferInvFgnameSetup.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			this.getWaferInvFgnameSetupDao().update(inWaferInvFgnameSetup);
		}else{
			inWaferInvFgnameSetup.setUpdateDate(new Date());
			inWaferInvFgnameSetup.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			inWaferInvFgnameSetup.setCreateDate(new Date());
			inWaferInvFgnameSetup.setCreateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			this.getWaferInvFgnameSetupDao().create(inWaferInvFgnameSetup);
		}
		
		return saveflag;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getTceInternalProductInfo()
	 */
	@Override
	public List<ProductInfo> getTceInternalProductInfo() {
		Criteria criteria = this.getProductInfoDao().createCriteria(ProductInfo.class);	
		//criteria.add(Restrictions.eq("productClassCode", "P"));
		criteria.add(Restrictions.eq("deleteFlag", false));
		criteria.addOrder(Order.asc("customerCode"));
		criteria.addOrder(Order.asc("product"));
		criteria.setProjection(Projections.distinct(Projections.property("product")));//OCF-PR-160307_修改distinct product，不分量產/試作
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#saveWaferInvFgnameMapping(com.tce.ivision.model.WaferInvFgnameMapping)
	 */
	@Override
	public boolean saveWaferInvFgnameMapping(WaferInvFgnameMapping inWaferInvFgnameMapping) {
		boolean saveflag = true;
		
		if(inWaferInvFgnameMapping.getWaferInvFgnameMappingIdx() != null){
			inWaferInvFgnameMapping.setUpdateDate(new Date());
			inWaferInvFgnameMapping.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			this.getWaferInvFgnameMappingDao().update(inWaferInvFgnameMapping);
		}else{
			inWaferInvFgnameMapping.setUpdateDate(new Date());
			inWaferInvFgnameMapping.setUpdateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			inWaferInvFgnameMapping.setCreateDate(new Date());
			inWaferInvFgnameMapping.setCreateUser((String)Sessions.getCurrent().getAttribute("loginid"));
			this.getWaferInvFgnameMappingDao().create(inWaferInvFgnameMapping);
		}
		
		return saveflag;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvLovMainList()
	 */
	@Override
	public List<WaferInvLovMain> getWaferInvLovMainList(String inClassName, String inComponentId) {
		Criteria criteria = this.getWaferInvLovMainDao().createCriteria(WaferInvLovMain.class);	
		criteria.add(Restrictions.eq("className", inClassName));
		criteria.add(Restrictions.eq("componentId", inComponentId));
		criteria.add(Restrictions.eq("enableFlag", true));
		criteria.addOrder(Order.asc("seq"));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getShippingByCustomerLotNoAndLotNo(java.lang.String, java.lang.String)
	 */
	@Override
	public List<ShippingDetail> getShippingByCustomerLotNoAndLotNo(String inCustomerLotNo, String inLotNo) {
		Criteria criteria = this.getShippingDetailDao().createCriteria(ShippingDetail.class);	
		criteria.add(Restrictions.eq("customerLotno", inCustomerLotNo));
		criteria.add(Restrictions.eq("lotNo", inLotNo));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOrderHeaderByOrderNumbers(java.util.List)
	 */
	@Override
	public List<OrderHeader> getOrderHeaderByOrderNumbers(List<String> inOrderNumbers) {
		Criteria criteria = this.getOrderHeaderDao().createCriteria(OrderHeader.class);	
		criteria.add(Restrictions.in("orderNumber", inOrderNumbers));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getLotInfoByOrderNumbers(java.util.List)
	 */
	@Override
	public List<LotInfo> getLotInfoByOrderNumbers(List<String> inOrderNumbers, String inCustomerLotNo) {
		Criteria criteria = this.getLotInfoDao().createCriteria(LotInfo.class);	
		criteria.add(Restrictions.in("orderNumber", inOrderNumbers));
		criteria.add(Restrictions.disjunction()
		        .add(Restrictions.eq("customerLotno1", inCustomerLotNo))
		        .add(Restrictions.eq("customerLotno2", inCustomerLotNo))
		        .add(Restrictions.eq("customerLotno3", inCustomerLotNo))
		        .add(Restrictions.eq("customerLotno4", inCustomerLotNo))
		        .add(Restrictions.eq("customerLotno5", inCustomerLotNo))
		        .add(Restrictions.eq("customerLotno6", inCustomerLotNo))
		        .add(Restrictions.eq("customerLotno7", inCustomerLotNo))
		        .add(Restrictions.eq("customerLotno8", inCustomerLotNo))
		    );
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvFgnameSetupByProduct(java.lang.String)
	 */
	@Override
	public List<WaferInvFgnameMapping> getWaferInvFgnameMappingByProduct(String inProductName) {
		Criteria criteria = this.getWaferInvFgnameMappingDao().createCriteria(WaferInvFgnameMapping.class);	
		criteria.add(Restrictions.eq("tceInternalProduct", inProductName).ignoreCase());
		criteria.add(Restrictions.eq("enableFlag", true));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateWaferBankinByDeclarationNoConfirm(java.util.List)
	 */
	@Override
	public void updateWaferBankinByDeclarationNoConfirm(WaferBankin inWaferBankin) {
		if(inWaferBankin != null){
			this.getWaferBankinDao().update(inWaferBankin);
		}
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ischedule.schedule.job.ivision.service.IvisionService#getWaferInvTxnDefinition(java.lang.String)
	 */
	@Override
	public List<WaferInvTxnDefinition> getWaferInvTxnDefinition(String inTceTxnType) {
		Criteria criteria = this.getWaferInvTxnDefinitionDao().createCriteria(WaferInvTxnDefinition.class);
		criteria.add(Restrictions.eq("tceTxnType", inTceTxnType));
		criteria.addOrder(Order.asc("txnSeq"));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInventoryStage(java.lang.String)
	 */
	@Override
	public List<WaferInventoryStage> getWaferInventoryStage(String inStage) {
		Criteria criteria = this.getWaferInventoryStageDao().createCriteria(WaferInventoryStage.class);
		criteria.add(Restrictions.eq("stage", inStage));
		criteria.add(Restrictions.eq("cancelFlag", false));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvTxnDefinitionByLovAndInvTo(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WaferInvTxnDefinition> getWaferInvTxnDefinitionByLovAndInvTo(String inLov, String inInvTo) {
		Criteria criteria = this.getWaferInvTxnDefinitionDao().createCriteria(WaferInvTxnDefinition.class);
		criteria.add(Restrictions.eq("waferInvLov", inLov));
		criteria.add(Restrictions.eq("invTransferTo", inInvTo));
		criteria.addOrder(Order.asc("txnSeq"));
		
		return 	criteria.list();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvTxnDefinitionByLov(java.lang.String)
	 */
	@Override
	public List<WaferInvTxnDefinition> getWaferInvTxnDefinitionByLov(String inLov) {
		Criteria criteria = this.getWaferInvTxnDefinitionDao().createCriteria(WaferInvTxnDefinition.class);
		criteria.add(Restrictions.eq("waferInvLov", inLov));
		criteria.addOrder(Order.asc("txnSeq"));
		
		return 	criteria.list();
	}
	
	public List<UiFieldSet> getUiFieldSetLists(String inClassName,String inParaType) {
		List<UiFieldSet> dataList=new ArrayList<UiFieldSet>();
		log.debug(inClassName);
		log.debug(inParaType);
		String hql="select u from UiFieldSet u WHERE className=:className AND paraType=:paraType ";
		dataList=this.getUiFieldSetDao().createQuery(hql)
				.setParameter("className", inClassName)
				.setParameter("paraType", inParaType)
				.list();
		return dataList;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getWaferInvLovSubList(java.lang.String, java.lang.String)
	 */
	@Override
	public List<WaferInvLovSub> getWaferInvLovSubList(String inClassName, String inComponentId) {
		Criteria criteria = this.getWaferInvLovSubDao().createCriteria(WaferInvLovSub.class);	
		criteria.add(Restrictions.eq("className", inClassName));
		criteria.add(Restrictions.eq("componentId", inComponentId));
		criteria.add(Restrictions.eq("enableFlag", true));
		criteria.addOrder(Order.asc("seq"));
		
		return 	criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getUiFieldParamValueByMeaning(String inClassName,	String inParaType, String inMeaning) {
		String value = "";
		String sql = "SELECT p.PARA_VALUE " +
				" FROM UI_FIELD_SET s,UI_FIELD_PARAM p " +
				" WHERE s.UI_FIELD_SET_IDX = p.UI_FIELD_SET_IDX " +
				" AND s.CLASS_NAME = :CLASS_NAME " +
				" AND s.PARA_TYPE = :PARA_TYPE " +
				" AND p.MEANING = :MEANING ";
		List<Object> tmpList = this.getDao().createSQLQuery(sql)
				.setParameter("CLASS_NAME", inClassName)
				.setParameter("PARA_TYPE", inParaType)
				.setParameter("MEANING", inMeaning)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
		if(tmpList != null){
			if(tmpList.size() > 0){
				Map<String,Object> row = (Map<String,Object>)tmpList.get(0);
				value = (String) row.get("PARA_VALUE");
			}
		}
		return value;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOrderHeaderByOrderNumber(java.lang.String)
	 */
	@Override
	public List<OrderHeader> getOrderHeaderByOrderNumber(String inOrderNumbers) {
		Criteria criteria = this.getOrderHeaderDao().createCriteria(OrderHeader.class);	
		criteria.add(Restrictions.eq("orderNumber", inOrderNumbers));
		
		return 	criteria.list();
	}
	
	/**
	 * 產生Packing list number 
	 * ex. T12122001
	 *	Code 1 : 台帳類別(T: TCE製作, J: 委外廠製作要回廠, N: 委外廠製作不回廠, C: Free charge, R: Remount)
	 *	Code 2-3 : Year
	 *	Code 4-5 : Month
	 *	Code 6-7 : Day
	 *	Code 8-9 : 流水單號
	 * @see com.tce.ivision.modules.shp.service.ShippingService#createPackingListNumber()
	 * @param inPackingListType
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String createPackingListNumber(String inPackingListType, Date inShippingDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Labels.getLabel("format.shortDate"));
		int todayMaxIndex = 0;
		String packingListNumber = "";
		List<Object> tmps=new ArrayList<Object>();
		try {
			//搜尋PACKING_LIST_NUMBER開頭符合#YYYYMMDD00的PACKING_LIST_NUMBER並取出當日流水號的最大值
			String sql=" SELECT MAX(CAST(SUBSTRING(PACKING_LIST_NUMBER,8) AS SIGNED)) AS TODAY_MAX_INDEX " +
					" FROM WAFER_MANAGEMENT_HISTORY WHERE PACKING_LIST_NUMBER LIKE :PACKING_LIST_NUMBER ";
			Query query = this.getDao().createSQLQuery(sql);
			query.setParameter("PACKING_LIST_NUMBER", inPackingListType + dateFormat.format(inShippingDate) + "%" );
			
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
			packingListNumber = inPackingListType + dateFormat.format(inShippingDate) + String.format("%02d",todayMaxIndex);
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());	
			e.printStackTrace();
			throw new RuntimeException(Labels.getLabel("modules.shp.packinglist.ctrl.createPackingListNumbererror"));
		}		
		log.debug("------------->"+packingListNumber);
		return packingListNumber;
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#updateWaferBankinWaferByWaferBankinWaferIdx(java.lang.Integer)
	 */
	@Override
	public void updateWaferBankinWaferByWaferBankinWaferIdx(Integer inWaferBankinWaferIdx, String inOrderNumber) {
		List<WaferBankinWafer> waferBankinWafers = (List<WaferBankinWafer>)this.getWaferBankinWaferDao()
				.createQuery("SELECT l FROM WaferBankinWafer l WHERE waferBankinWaferIdx=:waferBankinWaferIdx ")
				.setParameter("waferBankinWaferIdx", inWaferBankinWaferIdx)
				.list();
		if(waferBankinWafers.size() > 0){
			waferBankinWafers.get(0).setOrderNumber(inOrderNumber);
			this.getWaferBankinWaferDao().update(waferBankinWafers.get(0));
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinService#getOrderHeader(java.lang.String)
	 */
	@Override
	public List<OrderHeader> getOrderHeader(String inOrderNumber) {
		Criteria criteria = this.getOrderHeaderDao().createCriteria(OrderHeader.class);	
		criteria.add(Restrictions.eq("orderNumber", inOrderNumber));
		
		return 	criteria.list();
	}
	
	/**
	 * 2017.12.20
	 * [Internal Product Setup]按Save時,同時直接塞WAFER_INV_FGNAME_SETUP 第1筆資料
	 * 進Table: WAFER_INV_FGNAME_MAPPING. (為了讓保稅的程式正常運作)
	 */		
	public void insertWaferInvFgnameMappingByInternalProductSetupSave(String inProduct){
			List<WaferInvFgnameSetup> waferInvFgnameSetupLists = new ArrayList<WaferInvFgnameSetup>();
			WaferInvFgnameMapping waferInvFgnameMappings = new WaferInvFgnameMapping();
						
			//找WAFER_INV_FGNAME_SETUP Table資料
			Criteria criteria = this.getWaferInvFgnameSetupDao().createCriteria(WaferInvFgnameSetup.class);
			criteria.addOrder(Order.asc("waferInvFgnameSetupIdx")); //依WAFER_INV_FGNAME_SETUP_IDX排序			
			waferInvFgnameSetupLists = criteria.list();
			
			if (waferInvFgnameSetupLists.size()>0){
				log.debug(waferInvFgnameSetupLists.get(0).getWaferInvFgnameSetupIdx()); //get(0)為第1筆資料
				waferInvFgnameMappings.setWaferInvFgnameSetup(waferInvFgnameSetupLists.get(0));
				waferInvFgnameMappings.setTceInternalProduct(inProduct);
				waferInvFgnameMappings.setFinishGoodsName(waferInvFgnameSetupLists.get(0).getFinishGoodsName());
				waferInvFgnameMappings.setSemiFinishGoodsName(waferInvFgnameSetupLists.get(0).getSemiFinishGoodsName());
				waferInvFgnameMappings.setBondType(waferInvFgnameSetupLists.get(0).getBondType());
				waferInvFgnameMappings.setEnableFlag(true);
				waferInvFgnameMappings.setCreateUser("System");				
				waferInvFgnameMappings.setCreateDate(new Date());
				waferInvFgnameMappings.setUpdateUser("System");
				waferInvFgnameMappings.setUpdateDate(new Date());
				this.getWaferInvFgnameMappingDao().create(waferInvFgnameMappings);
			}
				
	}
	

	/**
	 * [getWaferInvRmnameMappingBySupplierLotId]
	 */	
	@Override
	public List<WaferInvRmnameMapping> getWaferInvRmnameMappingBySupplierLotId(String insupplierLotId){
		Criteria criteria = this.getWaferInvRmnameMappingDao().createCriteria(WaferInvRmnameMapping.class);	
		criteria.add(Restrictions.eq("supplierLotId", insupplierLotId));
		
		return 	criteria.list();
	}
	
	
	/**
	 * [ Wafer Receive Preparation]按Save時,同時直接塞WAFER_INV_RMNAME_SETUP 第1筆資料
	 * 進Table: WAFER_INV_RMNAME_MAPPING. (為了讓保稅的程式正常運作)
	 */	
	public void saveWaferInvRmnameMappingBySupplierLotId(String inDesignId){
		
		List<WaferInvRmnameMapping> chkWaferInvRmnameMappingLits=this.getWaferInvRmnameMappingBySupplierLotId(inDesignId);
						
		if (chkWaferInvRmnameMappingLits.size()<1)
		{		
			List<WaferInvRmnameSetup> waferInvRmnameSetupLists = new ArrayList<WaferInvRmnameSetup>();
			WaferInvRmnameMapping waferInvRmnameMappings = new WaferInvRmnameMapping();
						
			//找WAFER_INV_RMNAME_SETUP Table資料
			Criteria criteria = this.getWaferInvRmnameSetupDao().createCriteria(WaferInvRmnameSetup.class);
			criteria.addOrder(Order.asc("waferInvRmnameSetupIdx")); //依WAFER_INV_RMNAME_SETUP_IDX排序			
			waferInvRmnameSetupLists = criteria.list();
			
			if (waferInvRmnameSetupLists.size()>0){
				log.debug(waferInvRmnameSetupLists.get(0).getWaferInvRmnameSetupIdx()); //get(0)為第1筆資料
				waferInvRmnameMappings.setWaferInvRmnameSetup(waferInvRmnameSetupLists.get(0));
				waferInvRmnameMappings.setSupplierLotId(inDesignId);
				waferInvRmnameMappings.setRawMaterialName(waferInvRmnameSetupLists.get(0).getRawMaterialName());
				waferInvRmnameMappings.setBondType(waferInvRmnameSetupLists.get(0).getBondType());
				waferInvRmnameMappings.setSpecification(waferInvRmnameSetupLists.get(0).getSpecification());
				waferInvRmnameMappings.setEnableFlag(true);
				waferInvRmnameMappings.setCreateUser("System");				
				waferInvRmnameMappings.setCreateDate(new Date());
				waferInvRmnameMappings.setUpdateUser("System");
				waferInvRmnameMappings.setUpdateDate(new Date());
				this.getWaferInvRmnameMappingDao().create(waferInvRmnameMappings);
			}
		
		}
		
			
   }
	
	
}
