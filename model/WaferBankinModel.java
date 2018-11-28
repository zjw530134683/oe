/*
 * Project Name:iVision
 * File Name:WaferInfoReceiveModel.java
 * Package Name:com.tce.ivision.modules.oe.model
 * Date:2013/1/9下午6:47:19
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.model;

import com.tce.ivision.model.WaferBankin;

/**
 * ClassName: WaferInfoReceiveModel <br/>
 * date: 2013/1/9 下午6:47:19 <br/>
 *
 * @author 060489-Jeff
 * @version 
 * @since JDK 1.6
 */
public class WaferBankinModel {
	String mode;
	WaferBankin waferinforeceive;
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public WaferBankin getWaferinforeceive() {
		return waferinforeceive;
	}
	public void setWaferinforeceive(WaferBankin waferinforeceive) {
		this.waferinforeceive = waferinforeceive;
	}
	
	
}
