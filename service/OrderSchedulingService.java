/*
 * Project Name:iVision
 * File Name:OrderSchedulingService.java
 * Package Name:com.tce.ivision.modules.oe.service
 * Date:2012/12/12下午5:13:17
 * 
 * 說明:
 * OrderSchedulingService Interface
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */

package com.tce.ivision.modules.oe.service;

import java.util.Date;
import java.util.List;

import com.tce.ivision.model.LotResult;
import com.tce.ivision.modules.base.service.BaseService;
import com.tce.ivision.modules.oe.model.OrderScheduling;
import com.tce.ivision.modules.oe.model.OrderSchedulingParameter;

/**
 * ClassName: OrderSchedulingService <br/>
 * date: 2012/12/12 下午5:13:17 <br/>
 *
 * @author 110647
 * @version 
 * @since JDK 1.6
 */
public interface OrderSchedulingService extends BaseService {
	public List<OrderScheduling> getOrderSchedulings(OrderSchedulingParameter inOrderSchedulingParameter);
	public List<String> getProductWithStatusAndDateAndCustomer(String inCustomerId, boolean inCloseFlag,Date inBeginDate,Date inEndDate);
	public String createOrderCfmNo();
	public void updateOrderScheduling(List<OrderScheduling> inOrderSchedulings,String inClassName);
	public String getLotnoforOEHoldUsed(String inPoNumber, String inCustomerLono);//2013.01.15
	public boolean getShippingFlagforOEHoldUsed(String inPoNumber, String inCustomerLono);//2013.01.15
	public List<LotResult> getLotResultByLotNoOnlyABType(String inLotNo);//2013.10.01
	public List<LotResult> getLotResultByLotNo(String inLotNo);//2013.10.01
	public void updateOrderSchedulingLotResult(List<LotResult> inLotResults);//2013.10.01
}

