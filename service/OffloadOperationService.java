/*
 * Project Name:iVision
 * File Name:OffloadOperationService.java
 * Package Name:com.tce.ivision.modules.oe.service
 * Date:2014/12/02 上午11:56:17
 * 
 * 說明:
 * OffloadOperationService 
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */

package com.tce.ivision.modules.oe.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.tce.ivision.model.ExchangeRateOffload;
import com.tce.ivision.model.LotResult;
import com.tce.ivision.model.OffloadLotno;
import com.tce.ivision.model.OffloadShipping;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferBankinWafer;
import com.tce.ivision.modules.base.service.BaseService;
import com.tce.ivision.modules.oe.model.OffloadOperation;
import com.tce.ivision.modules.oe.model.OffloadOperationParameter;
import com.tce.ivision.modules.oe.model.OffloadShippingConfirm;
import com.tce.ivision.modules.oe.model.OffloadWaferConfirm;
import com.tce.ivision.modules.oe.model.OrderScheduling;
import com.tce.ivision.modules.oe.model.OrderSchedulingParameter;

/**
 * ClassName: OffloadOperationService <br/>
 * date: 2014/12/02 上午11:56:17 <br/>
 *
 * @author Allison
 * @version 
 * @since JDK 1.6
 */
public interface OffloadOperationService extends BaseService {
	public List<OffloadOperation> getOffloadOperations(OffloadOperationParameter inOffloadOperationParameter);
	public void updateOrderHeader(String[] inOrderNumbers, String inOffloadType);
	public void updateOrderLineLotNo(OffloadOperation inOffloadOperationLists, String inOffloadType);
	public List<String> getProductWithStatusAndDateAndCustomer(String inCustomerId);
	public boolean saveOffloadLotNo(OffloadOperation inOffloadOperationLists, String inOffloadType);
	public List<ExchangeRateOffload> getExchangeRateOffloadLists(String inYear);
	public boolean saveExchangeRateOffload(List<ExchangeRateOffload> inExchangeRateOffloadLists);
	public OrderHeader getOrderHeader(String inOrderNumber);
	public ExchangeRateOffload getExchangeRateOffload();
	public List<OffloadShippingConfirm> getOffloadShippingLists(int inOffloadLotnoIdx);
	public OffloadLotno getOffloadLotno(String inOrderNumber, String inCustomerLotno);
	public boolean saveOffloadShipping(OffloadShippingConfirm inOffloadShippingConfirmLists);
	public void updateOffloadLotno(OffloadLotno inOffloadLotnos);
	public LinkedHashMap<String,String> updateWaferBankinsAndInsertManagementHistory(String inCustomerLotNo, String inUser, List<OffloadWaferConfirm> inOffloadWaferConfirmLists);
	public List<WaferBankin> getWaferBankinLists(String inCustomerLotNo);
}

