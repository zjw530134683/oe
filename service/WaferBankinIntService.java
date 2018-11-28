/*
 * Project Name:iVision
 * File Name:WaferInIntTableService.java
 * Package Name:com.tce.ivision.modules.oe.service
 * Date:2012/12/18下午8:13:16
 * 
 * 說明:
 * WaferInIntTable的Service
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.service;

import java.util.List;

import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.modules.base.service.BaseService;
import com.tce.ivision.modules.oe.model.WaferFilter;

/**
 * ClassName: OrderEntryService <br/>
 * date: 2012/12/18下午8:13:16 <br/>
 *
 * @author 060489
 * @version 
 * @since JDK 1.6
 */
public interface WaferBankinIntService extends BaseService {
	//public List<WaferInIntTable> ListAll();
	public List<WaferBankinInt> listBySearch(WaferFilter inWaferfilter);
}
