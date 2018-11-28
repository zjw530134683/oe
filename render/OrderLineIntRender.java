/*
 * Project Name:iVision
 * File Name:OrderLineIntRender.java
 * Package Name:com.tce.ivision.modules.oe.render
 * Date:2012/12/21上午9:09:42
 * 
 * 說明:
 * OrderLineB2BViewCtrl.grdLineInt的render
 * 
 * 修改歷史:
 * 2012.12.17 OE002 Fanny Initial
 * 
 */
package com.tce.ivision.modules.oe.render;

import org.apache.log4j.Logger;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.tce.ivision.model.OrderLineInt;
import com.tce.ivision.units.common.DateFormatUtil;

/**
 * ClassName: OrderLineIntRender <br/>
 * date: 2012/12/21 上午9:09:42 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderLineIntRender implements ListitemRenderer {
	/**
	 * Logger
	 */
	public static Logger log = Logger.getLogger(OrderLineIntRender.class);

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(Listitem inItem, Object inData, int inIndex) throws Exception {
		OrderLineInt smf=(OrderLineInt) inData;
		
		log.debug("render....OrderLineIntRender");
		
		Listcell colb2bPoNum = new Listcell();
		Listcell colb2bPoItem = new Listcell();
		Listcell colb2bMtrlDesc = new Listcell();
		Listcell colb2bMtrlNumMtrlGroup = new Listcell();
		Listcell colb2bCustomerJob = new Listcell();
		Listcell colb2bWaferQty = new Listcell();
		Listcell colb2bDelivDate = new Listcell();
		Listcell colb2bDesignId = new Listcell();
		Listcell colb2bShipToVendorName = new Listcell();
		Listcell colb2bShipComment = new Listcell();
		Listcell colb2bFilename = new Listcell();
		Listcell colb2bImportDate = new Listcell();
		Listcell colb2bCountryOfFab = new Listcell();
		Listcell colb2bFab = new Listcell();
		Listcell colb2bUnitPrice = new Listcell();
		Listcell colb2bTPrice = new Listcell();
		Listcell colb2bSubName = new Listcell();
		Listcell colb2bStage = new Listcell();
		Listcell colb2bOperationDescription = new Listcell();
		Listcell colb2bPriority = new Listcell();
		Listcell colb2bLotType = new Listcell();
		Listcell colb2bWaferData = new Listcell();
		
		
		//Listcell colb2bMtrlNum = new Listcell();
		//Listcell colb2bSourceMtrlNum = new Listcell();
		//Listcell colb2bCfaPorId = new Listcell();
		//Listcell colb2bWaferSize = new Listcell();
		//Listcell colb2bShipToVendorCode = new Listcell();
		//Listcell colb2bCreateDate = new Listcell();
		//Listcell colb2bCreateTime = new Listcell();
		//Listcell colb2bCompCode = new Listcell();
		
		if(smf.getCustomerPo()!=null && !"".equals(smf.getCustomerPo())){
			colb2bPoNum.setLabel(smf.getCustomerPo().trim());//2013.02.20
		}else{
			colb2bPoNum.setLabel("");
		}
		if(smf.getPoItem()!=null && !"".equals(smf.getPoItem())){
			colb2bPoItem.setLabel(smf.getPoItem().trim());
		}else{
			colb2bPoItem.setLabel("");
		}
		
		//colb2bSourceMtrlNum.setLabel(smf.getSourceMtrlNum());
		//colb2bMtrlNum.setLabel(smf.getMtrlNum());
		if(smf.getMtrlDesc()!=null && !"".equals(smf.getMtrlDesc())){
			colb2bMtrlDesc.setLabel(smf.getMtrlDesc().trim());
		}else{
			colb2bMtrlDesc.setLabel("");
		}
		
		if(smf.getMtrlNumMtrlgrp()!=null && !"".equals(smf.getMtrlNumMtrlgrp())){
			colb2bMtrlNumMtrlGroup.setLabel(smf.getMtrlNumMtrlgrp().trim());
		}else{
			colb2bMtrlNumMtrlGroup.setLabel("");
		}
		
		colb2bCustomerJob.setLabel(smf.getCustomerJob());
		
		if(String.valueOf(smf.getWaferQty())!=null && !"".equals(String.valueOf(smf.getWaferQty()))){
			colb2bWaferQty.setLabel(String.valueOf(smf.getWaferQty()).toString().trim());
		}else{
			colb2bWaferQty.setLabel("");
		}
		
		if(smf.getDelivDate()!=null && !"".equals(smf.getDelivDate())){
			colb2bDelivDate.setLabel(smf.getDelivDate().toString().trim());
		}else{
			colb2bDelivDate.setLabel("");
		}
		
		if(smf.getDesignId()!=null && !"".equals(smf.getDesignId())){
			colb2bDesignId.setLabel(smf.getDesignId().trim());
		}else{
			colb2bDesignId.setLabel("");
		}
		
		if(smf.getCountryOfFab()!=null && !"".equals(smf.getCountryOfFab())){
			colb2bCountryOfFab.setLabel(smf.getCountryOfFab().trim());
		}else{
			colb2bCountryOfFab.setLabel("");
		}
		//colb2bCfaPorId.setLabel(smf.getCfaPorId());
		
		if(smf.getFab()!=null && !"".equals(smf.getFab())){
			colb2bFab.setLabel(smf.getFab().trim());
		}else{
			colb2bFab.setLabel("");
		}
		
		//colb2bWaferSize.setLabel(smf.getWaferSize());
		//colb2bShipToVendorCode.setLabel(smf.getShipToVendorCode());
		if(smf.getShipToVendorName()!=null && !"".equals(smf.getShipToVendorName().trim())){
			colb2bShipToVendorName.setLabel(smf.getShipToVendorName().trim());
		}else{
			colb2bShipToVendorName.setLabel("");
		}
		if(smf.getShipComment()!=null && !"".equals(smf.getShipComment())){
			colb2bShipComment.setLabel(smf.getShipComment().trim());
		}else{
			colb2bShipComment.setLabel("");
		}
		
		//colb2bCreateDate.setLabel(smf.getCreatedDate());
		//colb2bCreateTime.setLabel(smf.getCreatedTime());
		if(String.valueOf(smf.getUnitPrice())!=null && !"null".equals(String.valueOf(smf.getUnitPrice()))){
			colb2bUnitPrice.setLabel(String.valueOf(smf.getUnitPrice()).toString().trim());
		}else{
			colb2bUnitPrice.setLabel("");
		}
		
		if(String.valueOf(smf.gettPrice())!=null && !"null".equals(String.valueOf(smf.gettPrice()))){
			colb2bTPrice.setLabel(String.valueOf(smf.gettPrice()).toString().trim());
		}else{
			colb2bTPrice.setLabel("");
		}
		//colb2bCompCode.setLabel(smf.getPoNum());
		colb2bImportDate.setLabel(DateFormatUtil.getDateTimeFormat().format(smf.getImportDate()).toString().trim());
		if(smf.getFileName()!=null && !"".equals(smf.getFileName())){
			colb2bFilename.setLabel(smf.getFileName().trim());
		}else{
			colb2bFilename.setLabel("");
		}
		
		//IT-PR-141008_Allison add
		colb2bSubName.setLabel(smf.getSubName());
		colb2bStage.setLabel(smf.getStage());
		colb2bOperationDescription.setLabel(smf.getOperationDescription());
		colb2bPriority.setLabel(smf.getPriority());
		colb2bLotType.setLabel(smf.getLotType());
		colb2bWaferData.setLabel(smf.getWaferData());
		
		
		
		colb2bPoNum.setParent(inItem);
		colb2bPoItem.setParent(inItem);
		colb2bMtrlDesc.setParent(inItem);
		colb2bMtrlNumMtrlGroup.setParent(inItem);
		colb2bCustomerJob.setParent(inItem);
		colb2bWaferQty.setParent(inItem);
		colb2bDelivDate.setParent(inItem);
		colb2bDesignId.setParent(inItem);
		colb2bShipToVendorName.setParent(inItem);
		colb2bShipComment.setParent(inItem);
		colb2bFilename.setParent(inItem);
		colb2bImportDate.setParent(inItem);
		colb2bCountryOfFab.setParent(inItem);
		colb2bFab.setParent(inItem);
		colb2bUnitPrice.setParent(inItem);
		colb2bTPrice.setParent(inItem);
		
		//IT-PR-141008_Allison add
		colb2bSubName.setParent(inItem);
		colb2bStage.setParent(inItem);
		colb2bOperationDescription.setParent(inItem);
		colb2bPriority.setParent(inItem);
		colb2bLotType.setParent(inItem);
		colb2bWaferData.setParent(inItem);
		
		//colb2bSourceMtrlNum.setParent(inItem);
		//colb2bMtrlNum.setParent(inItem);
		//colb2bCfaPorId.setParent(inItem);
		//colb2bWaferSize.setParent(inItem);
		//colb2bShipToVendorCode.setParent(inItem);
		//colb2bCreateDate.setParent(inItem);
		//colb2bCreateTime.setParent(inItem);
		//colb2bCompCode.setParent(inItem);
	}
}
