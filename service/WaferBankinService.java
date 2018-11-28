/*
 * Project Name:iVision
 * File Name:WaferInfoReceiveService.java
 * Package Name:com.tce.ivision.modules.oe.service
 * Date:2012/12/18下午8:13:16
 * 
 * 說明:
 * WaferInfoReceive的Service
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.model.LotInfo;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.ProductInfo;
import com.tce.ivision.model.Shipping;
import com.tce.ivision.model.ShippingDetail;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferBankinWafer;
import com.tce.ivision.model.WaferInvFgnameMapping;
import com.tce.ivision.model.WaferInvFgnameSetup;
import com.tce.ivision.model.WaferInvLovMain;
import com.tce.ivision.model.WaferInvLovSub;
import com.tce.ivision.model.WaferInvRmnameMapping;
import com.tce.ivision.model.WaferInvRmnameSetup;
import com.tce.ivision.model.WaferInvTxnDefinition;
import com.tce.ivision.model.WaferInventoryStage;
import com.tce.ivision.model.WaferManagementHistory;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.base.service.BaseService;
import com.tce.ivision.modules.oe.model.WaferFilter;
import com.tce.ivision.modules.oe.model.WaferBankinModel;
import com.tce.ivision.modules.wafer.model.WaferInvReceiveLot;
import com.tce.ivision.modules.wafer.model.WaferInvReceiveWafer;
import com.tce.ivision.modules.wafer.model.WaferInventoryManagement;
import com.tce.ivision.modules.wafer.model.WaferInventoryManagementParameter;
import com.tce.ivision.modules.wafer.model.WaferReceiveOperation;
import com.tce.ivision.modules.wafer.model.WaferStatusMaintenanceParameter;

/**
 * ClassName: OrderEntryService <br/>
 * date: 2012/12/18下午8:13:16 <br/>
 *
 * @author 060489
 * @version 
 * @since JDK 1.6
 */
public interface WaferBankinService extends BaseService {
	public List<WaferBankin> listAll();
	public List<WaferBankin> listBySearch(WaferFilter inWaferfilter);
	
	public boolean checkDataExist(Object[] inObjs);	
	public String checkDataExistNew(Object[] inObjs);	//OCF-PR-150307
	public void saveTransactionItems(Object[] inObjs);
	public void updateTransactionItems(Object[] inObjs);
	public boolean updateWaferBankinAndInsert(Object[] inObjs);
	
	public boolean saveItems(List<WaferBankinWafer> inItems, String inExistMsg, boolean diffWaferDataFlag);//OCF-PR-150202_Allison add
	
	//public String checkDataExistForOMNI(Object[] inObjs);	//IT-PR-141008_Allison add //OCF-PR-150202_原程式已mark不需要_Allison
	public List<CustomerTable> getCustomerShortNameByCustomerId(String inCustomerId);//IT-PR-141008_Allison add
	public boolean updateWaferBankinCloseFlag(int inWaferBankinIdx);//IT-PR-141008_Allison add
	public String checkDataExistNew(WaferBankin inWaferBankinLists);	//OCF-PR-150202_Allison add
	public void updateTransactionItems(List<WaferBankin> inWaferBankinLists); //OCF-PR-150202_Allison add
	public WaferReceiveOperation getWaferBankinByPrepare(String inCustomerLotNo, String inWaferQty); //OCF-PR-150202_Allison add
	public List<WaferBankin> checkExistsWaferInTce(String inCustomerId, String inCustomerLotNo); //OCF-PR-150202_Allison add
	public List<WaferInventoryStage> waferInventoryStageLists(String inCheckExistTce); //OCF-PR-150202_Allison add
	public boolean updateWaferBankinByWaferReceiveOperation(WaferBankin inWaferBankin);//OCF-PR-150202_Allison add
	//public List<WaferBankin> getWaferBankinByManagement(WaferInventoryManagementParameter inWaferInventoryManagementParameter); //OCF-PR-150202_Allison add
	public List<WaferInventoryManagement> getWaferBankinByManagement(WaferInventoryManagementParameter inWaferInventoryManagementParameter); //OCF-PR-150202_Allison add
	public void updateWaferBankinWaferByManagementConfirm(List<WaferBankinWafer> inWaferBankinWafer);//OCF-PR-150202_Allison add
	public List<LinkedHashMap<String, String>> getProductAndOcfLotNoByWaferNo(WaferBankin inWaferBankin);
	public List<WaferBankin> getWaferBankinReceiveCountByWaferNo(String inCustomerId, String inCustomerLotNo, String inWaferNo);
	public void insertWaferManagementHistoryByWaferReceiveOperation(WaferManagementHistory inWaferManagementHistory);
	public List<WaferManagementHistory> getWaferBankinHistoryByWaferHistory(String inFrom, String inTo, WaferBankin inWaferBankin);
	public List<LinkedHashMap<String, String>> getProductAndOcfLotNoByWaferDataAndWaferNo(WaferInvReceiveLot inWaferInvReceiveLot, String inWaferNo);
	public List<LinkedHashMap<String, String>> getLotResultByVwOrderLotWafer(WaferInvReceiveLot inWaferInvReceiveLot, String inWaferNo);
	public List<WaferBankinWafer> getWaferBankinWaferByWaferNo(String inWaferNo);
	public List<WaferBankin> getWaferBankinByMaintenance(WaferStatusMaintenanceParameter inWaferStatusMaintenanceParameter);
	public void updateWaferBankinWaferByMaintenanceConfirm(List<WaferBankinWafer> inWaferBankinWafer);
	public void insertWaferManagementHistoryByWaferStatusMaintenance(WaferManagementHistory inWaferManagementHistory);
	public void updateWaferBankinByMaintenanceConfirm(WaferBankin inWaferBankin);
	public List<WaferBankin> listBySearchByOe(WaferFilter inWaferfilter);
	public List<LinkedHashMap<String, String>> getOeLotInfoByCustomerLotNo(String inCustomerLotNo);
	public List<LinkedHashMap<String, String>> getShippingWaferInfoByCustomerLotNo(String inCustomerLotNo);
	public List<LinkedHashMap<String, String>> getOeDataByCustomerLotNo(String inCustomerLotNo);
	public List<LinkedHashMap<String, String>> getOeOrderNumberByCustomerLotNo(String inCustomerLotNo, String inWaferData);
	public List<LinkedHashMap<String, String>> getOrderHeaderAndLotInfoByOrderNumber(String inOrderNumber, String inCustomerLotNo);
	public List<LinkedHashMap<String, String>> getOeByCustomerLotNo(String inOrderNumber);
	public List<WaferInventoryManagement> getOrderHeaderAndLotInfoByOrderNumber(List<WaferInventoryManagement> inWaferInventoryManagement);
	public WaferBankin getWaferBankinByIdx(int inWaferBankinIdx);
	public List<LinkedHashMap<String, String>> getOeWithOrderStatusByCustomerLotNo(String inCustomerLotNo);
	public List<LinkedHashMap<String, String>> getOeLotInfoByOrderNumber(String inOrderNumber);
	public List<WaferInvReceiveWafer> getWaferInvReceiveWafer(String inWaferBankinIdxs);
	public List<WaferStatus> getWaferStatusByCustomerLotNoAndOrderNumber(String inCustomerLotNo, String inOrderNumber);
	public void updateWaferStatusByWaferReceiveOperation(List<WaferStatus> inWaferStatusLists);
	public void updateWaferBankinByDeclarationNoConfirm(WaferBankin inWaferBankin);
	public void updateWaferBankinWaferByWaferBankinWaferIdx(Integer inWaferBankinWaferIdx, String inOrderNumber);
	public List<OrderHeader> getOrderHeader(String inOrderNumber);
	
	//OCF-PR-160303_Bond System
	public boolean saveWaferInvRmnameSetup(WaferInvRmnameSetup inWaferInvRmnameSetup);
	public List<WaferInvRmnameSetup> getWaferInvRmnameSetupLists();
	public List<WaferInvRmnameMapping> getWaferInvRmnameMappingLits();
	public boolean saveWaferInvRmnameMapping(WaferInvRmnameMapping inWaferInvRmnameMapping);
	public List<WaferInvFgnameSetup> getWaferInvFgnameSetupLists();
	public List<WaferInvFgnameMapping> getWaferInvFgnameMappingLits();
	public boolean saveWaferInvFgnameSetup(WaferInvFgnameSetup inWaferInvFgnameSetup);
	public List<ProductInfo> getTceInternalProductInfo();
	public boolean saveWaferInvFgnameMapping(WaferInvFgnameMapping inWaferInvFgnameMapping);
	public List<WaferInvLovMain> getWaferInvLovMainList(String inClassName, String inComponentId);
	public List<ShippingDetail> getShippingByCustomerLotNoAndLotNo(String inCustomerLotNo, String inLotNo);
	public List<OrderHeader> getOrderHeaderByOrderNumbers(List<String> inOrderNumbers);
	public List<LotInfo> getLotInfoByOrderNumbers(List<String> inOrderNumbers, String inCustomerLotNo);
	public List<WaferInvFgnameMapping> getWaferInvFgnameMappingByProduct(String inProductName);
	public List<WaferInvTxnDefinition> getWaferInvTxnDefinition(String inTceTxnType);
	public List<WaferInvTxnDefinition> getWaferInvTxnDefinitionByLovAndInvTo(String inLov, String inInvTo);
	public List<WaferInvTxnDefinition> getWaferInvTxnDefinitionByLov(String inLov);
	public List<WaferInventoryStage> getWaferInventoryStage(String inStage);
	public List<UiFieldSet> getUiFieldSetLists(String inClassName,String inParaType);
	public List<WaferInvLovSub> getWaferInvLovSubList(String inClassName, String inComponentId);
	public String getUiFieldParamValueByMeaning(String inClassName,String inParaType,String inMeaning);
	public List<OrderHeader> getOrderHeaderByOrderNumber(String inOrderNumbers);
	public String createPackingListNumber(String inPackingListType, Date inShippingDate);
	
	/**
	 * [Internal Product Setup]按Save時,同時直接塞資料進Table: WAFER_INV_FGNAME_MAPPING. (為了讓保稅的程式正常運作)
	 */	
	public void insertWaferInvFgnameMappingByInternalProductSetupSave(String inProduct);
	
	/**
	 * FOR TSES
	 */	
	public void saveWaferInvRmnameMappingBySupplierLotId(String inSupplierLotId);
	public List<WaferInvRmnameMapping> getWaferInvRmnameMappingBySupplierLotId(String inDesignId);
	
	
}
