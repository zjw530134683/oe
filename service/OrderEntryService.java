/*
 * Project Name:iVision
 * File Name:OrderEntryService1.java
 * Package Name:com.tce.ivision.modules.oe.service
 * Date:2012/12/19上午9:00:34
 * 
 * 說明:
 *有關Order Entry相關table的Service
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.tce.ivision.model.TsesOvtRmaLot;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.model.WaferBankinWafer;
import com.tce.ivision.model.WaferInfo;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.base.service.BaseService;
import com.tce.ivision.modules.oe.model.OeOrderNoConfirmModel;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.model.ProductNameSetup;


/**
 * ClassName: OrderEntryService1 <br/>
 * date: 2012/12/19 上午9:00:34 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public interface OrderEntryService extends BaseService {
	//ORDER_HEADER
	//public void createOrderHeader(OrderHeader inOrderHeader);
	public OrderHeader createOrderHeader(OrderHeader inOrderHeader);//OCF-PR-151002 modify
	public void updateOrderHeader(OrderHeader inOrerHeader);
	public List<OrderHeader> getOrderHeadersByHql(String inOperationUnit, String inCustomerId,String inProduct,String inPoNumber,String inOrderDateS,String inOrderDateE,String inOrderStatus);
	public OrderHeader getOrderHeaderByOrderNumber(String inOrderNumber);
	public OrderHeader getOrderHeaderByPoNumberProduct(String inPONumber,String inProduct);//2013.05.07
	public List<OrderHeader> getOrderHeaderByBillToPoNumber(String inBillTo,String inOrderNumber);//2013.06.21 //2013.06.25 先不要下到PoNumber，因為MySql是不分大小寫的，所以PONumber另外用java程式比較
	public OrderHeader getOrderHeaderByPoNumber(String inPONumber);//2014.10.28
	
	//ORDER_LINE
	public void dropOrderLines(List<OrderLine> inOrderLines, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public void deleteOrderLines(List<OrderLine> inOrderLines, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public void updateOrderLines(List<OrderLine> inOrderlines, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public void createOrderLines(List<OrderLine> inOrderLines, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public List<OrderLine> getOrderLinesByOrderNumber(String inOrderNumber);
	public void saveOrderLine(OrderLine inOrderLines, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public void deleteOrderLines(OrderLine inOrderLine);//OCF-PR-151204_若Save中有錯誤，要先將已經Save到DB的ORDER_LINE刪除
	
	//ORDER_LINE_LOTNO
	//public void createOrderLineLotnos(List<OrderLineLotno> inOrderLineLotnos, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public void createOrderLineLotno(OrderLineLotno inOrderLineLotno, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public void updateOrderLineLotnos(List<OrderLineLotno> inOrderLineLotnos, String inUserId, Date inNowTime, String inClassName, String inActionName);
	//public void updateOrderEntryLotnoModel(List<OrderEntryLotnoModel> inOrderEntryLotnoModels);
	public void deleteOrderLineLotnos(List<OrderLineLotno> inOrderLineLotnos, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public void dropOrderLineLotnos(List<OrderLineLotno> inOrderLineLotnos, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public List<OrderLineLotno> getOrderLineLotnosByOrderNumber(String inOrderNumber);
	public OrderLineLotno getOrderLineLotno(String inOrderNumber, String inPoItem, String inCustomerLotno);
	public List<OrderLineLotno> getOrderLineLotnosByOrderNumberPoItem(String inOrderNumber, String inPoItem);
	public List<OrderLineLotno> getOrderLineLotnosByOrderNumberAndCustomerLotNo(String inOrderNumber, String inCustomerLotNo);
	
	//ORDER_LINE_INT
	public List<OrderLineInt> getOrderLineInts(String inCustomerId,String inCustomerPo,String inImportDateS, String inImportDateE);
	public OrderLineInt getOrderLineIntByIdx(int inOrderLineIntIdx);
	public List<OrderLineInt> getOrderLineIntByWiReport(String inCustomerLotNo,String inCustomerJob, String inWaferData);
	public void insertOrderLineInts(List<OrderLineInt> inOrderLineInts);//IT-PR-141008_Allison add
	public void updateOrderLineInts(List<OrderLineInt> inOrderLineInts, String inUserId);//IT-PR-141008_Allison add
	
	//OTHER
	public String createOrderNumber(int inIdx, Date inDate);
	public List<String> getDistinctProduct(String inCustomerId);
	public void deleteOrderData(OrderHeader inOrderHeader,List<OrderLine> inOrderLines, List<OrderLineLotno> inOrderLineLotnos,
			List<WaferStatus> inWaferStatus, String inUserId,Date inNowTime, String inClassName, List<LotInfo> inLotInfos, List<LotResult> inLotResults);//IT-PR-141008_Allison modify
	public void saveOETransactionItems(Object[] inObjs);
	public String createEntityId(Date inDate);
	public OrderHeader saveOrderHeaders(OrderHeader inOrderHeader);//OCF-PR-151002_先儲存ORDER_HEADER才不會因Hinbernate在同一個Session Service而無法重新編新的OrderHeaderIdx
	public String saveEntityIds(OrderLineLotno inOrderLineLotNo, Date inNowTime);//OCF-PR-151002_先儲存ENTITY_ID才不會因Hinbernate在同一個Session Service而無法重新編新的ENTITY_ID
	public void deleteOrderHeaders(OrderHeader inOrderHeader);//OCF-PR-151002_若Save中有錯誤，要先將已經Save到DB的ORDER_HEADER刪除
	public OeOrderNoConfirmModel getOeDatasByOrderNumber(String inOrderNumber, String inCustomerLotNo);
	
	//WAFER_STATUS
	public void createWaferStatus(WaferStatus inWaferStatus,String inUserId, Date inNowTime, String inClassName, String inActionName);//2013.04.01
	public void updateWaferStatus(WaferStatus inWaferStatus,String inUserId, Date inNowTime, String inClassName, String inActionName);//2013.04.01
	public List<WaferStatus> getWaferStatusByOrderLineLotno(List<OrderLineLotno> indelOrderLineLotnos);//2013.02.18
	public WaferStatus getWaferStatusByEntityId(String inEntityId);//2013.02.18
	
	//HOLD
	public Hold getHold(String inOrderNumber, String inPoItem, String inCustomerLotno);//2013.01.15
	public void saveHoldTransactionItems(Object[] inObjs);
	public void saveHoldAndHoldWaferTransactionItems(Object[] inObjs);
	public void insertHolds(List<Hold> inHolds, String inUserId, Date inNowTime, String inClassName, String inActionName);
	public void updateHolds(List<Hold> inHolds, String inUserId, Date inNowTime, String inClassName, String inActionName);
	
	//Logging
	public void createLogging(String inClassName,String inTableName, String inActionName, Object inContent, String inUser, Date inDate);
	
	//Encode
	public Encode getEncode(String inCodeType,String inFixCode);//2013.02.21
	public void updateEncode(Encode inEncode);//2013.02.21
	public void createEncode(Encode inEncode);//2013.02.21
	
	//PRODUCT_INFO
	public ProductInfo getProductInfo(String inCustomerCode,String inProductClassCode,String inProduct);//2013.07.09
	
	//WAFER_BANKIN_INT
	public void insertWaferBankinInts(List<WaferBankinInt> inWaferBankinInts);//IT-PR-141008_Allison add
	public void updateWaferBankinInts(List<WaferBankinInt> inWaferBankinInts, String inUserId);//IT-PR-141008_Allison add
	
	//WAFER_INFO
	public List<WaferInfo> getWaferInfoByCustomerLotNo(String inCustomerLotNo);
	public List<WaferInfo> getWaferInfoByCustomerLotNoAndLotNo(String inCustomerLotNo, String inLotNo);
	
	//HOLD_WAFER
	public List<HoldWafer> getHoldWafer(int holdIdx);//IT-PR-141008_Allison add
	public void insertHoldWafers(List<HoldWafer> inHoldWafers, String inUserId, Date inNowTime, String inClassName, String inActionName, String inOrderNumber, List<Hold> inHolds, List<OrderLineLotno> inOrderLineLotNos);
	public void updateHoldWafers(List<HoldWafer> inHoldWafers, String inUserId, Date inNowTime, String inClassName, String inActionName, String inOrderNumber, List<Hold> inHolds, List<OrderLineLotno> inOrderLineLotNos);
	public HoldWafer getHoldWaferByHoldIdxAndWaferNo(int holdIdx, String inWaferNo);//IT-PR-141008_Allison add
	
	//LOT_INFO
	public List<LotInfo> getLotInfoByOrderNumber(String inOrderNumber);//IT-PR-141008_Allison add
	public List<LotInfo> getLotInfoByOrderNumberAndCustomerLotNo(String inOrderNumber, String inCustomerLotNo);//IT-PR-141008_Allison add
	public void updateLotInfos(LotInfo inLotInfo,String inUserId, Date inNowTime, String inClassName, String inActionName);//IT-PR-141008_Allison add
	//LOT_RESULT
	public List<LotResult> getLotResultByLotNo(List<LotInfo> inLotInfos);//IT-PR-141008_Allison add
	public void updateLotResults(LotResult inLotResult,String inUserId, Date inNowTime, String inClassName, String inActionName);//IT-PR-141008_Allison add
	
	//WAFER_BANKIN
	public List<WaferBankin> getWaferBankinByCustomerLotNo(String inCustomerLotNo);
	public List<WaferBankin> getWaferBankinAndWaferBankinWaferByCustomerLotNoAndCustomerId(List<String> inCustomerLotNoList, String inCustomerId);//OCF-PR-150202_Allison add
	public void updateWaferBankinAndWafers(List<WaferBankin> inWaferBankinList);
	public List<WaferBankinWafer> getWaferBankinWaferByOrderNumber(String inOrderNumber);
	public void updateWaferBankinWafers(List<WaferBankinWafer> inWaferBankinWaferList);
	public List<WaferBankin> getWaferBankinByCustomerLotNoAndCustomerId(String inCustomerLotNo, String inCustomerId);
	
	//OE_REWORK_COUNT_SET
	public List<OeReworkCountSetup> getOeReworkCountSetupByCustomerId(String inCustomerId);//OCF-PR-150202_Allison add
	
	//ORDER_INTERNAL_CHECK_INFO
	public List<OrderInternalCheckInfo> getOrderInternalCheckInfoByOrderLineLotNoIdx(int inOrderLineLotNoIdx);//OCF-PR-150202_Allison add
	public List<OrderInternalCheckInfo> getOrderInternalCheckInfoCheckRuleByOrderNumberAndCustomerLotNo(String inOrderNumber, String inCustomerLotNo);//OCF-PR-150202_Allison add
	public List<OrderInternalCheckInfo> getOrderInternalCheckInfoNoticeByOrderNumberAndCustomerLotNo(String inOrderNumber, String inCustomerLotNo);//OCF-PR-150202_Allison add
	public void updateOrderInternalCheckInfo(List<OrderInternalCheckInfo> inOrderInternalCheckInfoList);//OCF-PR-150202_Allison add
	public void insertOrderInternalCheckInfo(List<OrderInternalCheckInfo> inOrderInternalCheckInfoList);//OCF-PR-150202_Allison add
	public void updateOrderInternalCheckInfoModifyReason(List<OrderInternalCheckInfo> inOrderInternalCheckInfoList);//OCF-PR-150202_Allison add
	public List<OrderInternalCheckInfo> getOrderInternalCheckInfoByOrderLineLotNos(List<Integer> inOrderLineLotnoIdxs);//OCF-PR-150202_Allison add
	public void dropOrderInternalCheckInfos(List<OrderInternalCheckInfo> inOrderInternalCheckInfos, String inUserId, Date inNowTime, String inClassName, String inActionName);
	
	public List<UiFieldSet> getUiFieldSet(String inClassName, String inParaType);
	
	//2017.12.20 取出PRODUCT_NAME_SETUP.INTERN_PRODUCT 
	public ProductNameSetup getInternalProdcut(String inCustomerId, String inProduct);
	//version:XQ181004 20181110 add by will 增加RMA的table存储
	public void saveReworkFlag(TsesOvtRmaLot tsesOvtRmaLot);
	//2017.12.20 利用PO_NUMBER條件(目前程式寫法ORDER_NUMBER=PO_NUMBER) 取出ORDER_HEADER.REAL_PRODUCT 
	//在ORDER_HEADER Table中ORDER_NUMBER為唯一值
	//public String getRealProduct(String inPoNumber);
}
