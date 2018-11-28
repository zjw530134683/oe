/*
 * Project Name:iVision
 * File Name:OffloadOperationRender.java
 * Package Name:com.tce.ivision.modules.oe.render
 * Date:2014/12/02下午3:00:00
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.render;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.modules.oe.model.OffloadOperation;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.DateUtil;
import com.tce.ivision.units.common.ZkComboboxControl;
import com.tce.ivision.units.common.service.CommonService;
/**
 * 
 * ClassName: OffloadOperationRender <br/>
 * date: 2014/12/02 下午03:00:00 <br/>
 *
 * @author Allison
 * @version 
 * @since JDK 1.6
 */
public class OffloadOperationRender implements ListitemRenderer<OffloadOperation> {
	/**
	 * Log4j Component
	 */
	public static Logger log = Logger.getLogger(OffloadOperationRender.class);
	
	/**
	 * CommonService
	 */
	private CommonService commonService = (CommonService) SpringUtil.getBean("commonService");
	
	
	@Override
	public void render(Listitem inItem, OffloadOperation inData, int inIndex) throws Exception {
		final OffloadOperation offloadOpertion = inData;
		
		NumberFormat formatter = new DecimalFormat("#,###");
		if("20".equals(offloadOpertion.getOffloadStatus())){
			inItem.setStyle("background:#98FB98");
		}else if("30".equals(offloadOpertion.getOffloadStatus())){
			inItem.setStyle("background:#DCDCDC");
		}
		//宣告Listcell
		Listcell cellSelect = new Listcell();
		Listcell cellOffloadStatus = new Listcell();
		Listcell cellCustomer = new Listcell();
		Listcell cellOrderNumber = new Listcell();
		Listcell cellPoNo = new Listcell();
		Listcell cellProduct = new Listcell();
		Listcell cellCustomerLotNo = new Listcell();
		Listcell cellWaferQty = new Listcell();
		Listcell cellOrderPrice = new Listcell();
		Listcell cellCurrency = new Listcell();
		Listcell cellOffloadType = new Listcell();
		Listcell cellOffloadTo = new Listcell();
		Listcell cellOffloadPo = new Listcell();
		Listcell cellOffloadDueDate = new Listcell();
		Listcell cellWaferId = new Listcell();
		Listcell cellConfirmDate = new Listcell();
		Listcell cellUpdateDate = new Listcell();
		Listcell cellUpdateUser = new Listcell();
		Listcell cellCancelDate = new Listcell();
		Listcell cellCancelUser = new Listcell();
		Listcell cellCancelReason = new Listcell();
		
		//SELECT
		Checkbox chkSelect = new Checkbox();
		chkSelect.setId("chkSelect"+inIndex);
		chkSelect.addEventListener("onCheck", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				if("".equals(offloadOpertion.getOffloadStatus())){
					
				}
			}
		});
		chkSelect.setParent(cellSelect);
		
		//OFFLOAD_STATUS
		String tmpOffloadStatus="";
		List<UiFieldSet> offloadStatus=commonService.getUiFieldSetLists(this.getClass().getName(), "OFFLOAD_STATUS");
		if (offloadStatus.size()>0){
			for(int i=0; i<offloadStatus.size(); i++){
				for(int j=0; j<offloadStatus.get(i).getUiFieldParams().size(); j++){
					if(offloadStatus.get(i).getUiFieldParams().get(j).getMeaning().equals(offloadOpertion.getOffloadStatus())){
						tmpOffloadStatus=offloadStatus.get(i).getUiFieldParams().get(j).getParaValue();
						break;
					}
				}
			}
		}
		cellOffloadStatus.setLabel(tmpOffloadStatus);
		
		//CUSTOMER
		cellCustomer.setLabel(offloadOpertion.getCustomer());
		
		//ORDER_NUMBER
		cellOrderNumber.setLabel(offloadOpertion.getOrderNumber());
		
		//PO_NO
		cellPoNo.setLabel(offloadOpertion.getPoNo());
		
		//PRODUCT
		cellProduct.setLabel(offloadOpertion.getProduct());
		
		//CUSTOMER_LOTNO
		cellCustomerLotNo.setLabel(offloadOpertion.getCustomerLotNo());
		
		//WAFER_QTY
		cellWaferQty.setLabel(offloadOpertion.getWaferQty());
		
		//ORDER_PRICE
		cellOrderPrice.setLabel(formatter.format(offloadOpertion.getOrderPrice()));
		
		//CURRENCY
		String tmpCurrency="";
		List<UiFieldSet> currencys=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.OrderEntryViewCtrl", "OE_CURRENCY");
		if (currencys.size()>0){
			for(int i=0; i<currencys.size(); i++){
				for(int j=0; j<currencys.get(i).getUiFieldParams().size(); j++){
					if(currencys.get(i).getUiFieldParams().get(j).getMeaning().equals(offloadOpertion.getCurrency())){
						tmpCurrency=currencys.get(i).getUiFieldParams().get(j).getParaValue();
						break;
					}
				}
			}
		}
		cellCurrency.setLabel(tmpCurrency);
		
		//OFFLOAD_TYPE
		Combobox comboboxOffloadType = new Combobox();
		List<UiFieldSet> offloadType=commonService.getUiFieldSetLists(this.getClass().getName(), "OFFLOAD_TYPE");
		if (offloadType.size()>0){
			for(int i=0; i<offloadType.size(); i++){
				for(int j=0; j<offloadType.get(i).getUiFieldParams().size(); j++){
					comboboxOffloadType.appendItem(offloadType.get(i).getUiFieldParams().get(j).getParaValue());
				}
			}
		}
		
		if(offloadOpertion.getOffloadType() != null){
			for(int i=0; i<comboboxOffloadType.getItemCount(); i++){
				if(offloadOpertion.getOffloadType().equals(comboboxOffloadType.getItemAtIndex(i).getValue())){
					comboboxOffloadType.setSelectedIndex(i);
				}
			}
		}		
		comboboxOffloadType.setId("comboboxOffloadType"+String.valueOf(inIndex));
		comboboxOffloadType.setParent(cellOffloadType);
		
		//OFFLOAD_TO
		Combobox comboboxOffloadTo = new Combobox();
		List<UiFieldSet> offloadTo=commonService.getUiFieldSetLists(this.getClass().getName(), "OFFLOAD_TO");
		if (offloadTo.size()>0){
			for(int i=0; i<offloadTo.size(); i++){
				for(int j=0; j<offloadTo.get(i).getUiFieldParams().size(); j++){
					comboboxOffloadTo.appendItem(offloadTo.get(i).getUiFieldParams().get(j).getParaValue());
				}
			}
		}
		
		if(offloadOpertion.getOffloadTo() != null){
			for(int i=0; i<comboboxOffloadTo.getItemCount(); i++){
				if(offloadOpertion.getOffloadTo().equals(comboboxOffloadTo.getItemAtIndex(i).getValue())){
					comboboxOffloadTo.setSelectedIndex(i);
				}
			}
		}		
		comboboxOffloadTo.setId("comboboxOffloadTo"+String.valueOf(inIndex));
		comboboxOffloadTo.setParent(cellOffloadTo);
		
		//OFFLOAD_PO
		cellOffloadPo.setLabel(offloadOpertion.getOffloadPo());
		
		//OFFLOAD_DUEDATE
		Datebox dateboxOffloadDueDate = new Datebox();
		dateboxOffloadDueDate.setValue(offloadOpertion.getOffloadDueDate());
		dateboxOffloadDueDate.setId("dateboxOffloadDueDate"+String.valueOf(inIndex));
		dateboxOffloadDueDate.setFormat(Labels.getLabel("format.date"));
		dateboxOffloadDueDate.setParent(cellOffloadDueDate);
		
		//WAFER_ID
		cellWaferId.setLabel(offloadOpertion.getWaferId());
		
		//CONFIRM_DATE
		if(offloadOpertion.getConfirmDate() != null){
			cellConfirmDate.setLabel(DateFormatUtil.getSimpleDateFormat().format(offloadOpertion.getConfirmDate()));
		}else{
			cellConfirmDate.setLabel("");
		}
		
		//UPDATE_DATE
		if(offloadOpertion.getUpdateDate() != null){
			cellUpdateDate.setLabel(DateFormatUtil.getSimpleDateFormat().format(offloadOpertion.getUpdateDate()));
		}else{
			cellUpdateDate.setLabel("");
		}
		
		//UPDATE_USER
		cellUpdateUser.setLabel(offloadOpertion.getUpdateUser());
		
		//CANCEL_DATE
		if(offloadOpertion.getCancelDate() != null){
			cellCancelDate.setLabel(DateFormatUtil.getSimpleDateFormat().format(offloadOpertion.getCancelDate()));
		}else{
			cellCancelDate.setLabel("");
		}
		
		//CANCEL_USER
		cellCancelUser.setLabel(offloadOpertion.getCancelUser());
		
		//CANCEL_REASON
		cellCancelReason.setLabel(offloadOpertion.getCancelReason());
		
		
		//將each Litcell 放上ListItme上
		cellSelect.setParent(inItem);
		cellOffloadStatus.setParent(inItem);
		cellCustomer.setParent(inItem);
		cellOrderNumber.setParent(inItem);
		cellPoNo.setParent(inItem);
		cellProduct.setParent(inItem);
		cellCustomerLotNo.setParent(inItem);
		cellWaferQty.setParent(inItem);
		cellOrderPrice.setParent(inItem);
		cellCurrency.setParent(inItem);
		cellOffloadType.setParent(inItem);
		cellOffloadTo.setParent(inItem);
		cellOffloadPo.setParent(inItem);
		cellOffloadDueDate.setParent(inItem);
		cellWaferId.setParent(inItem);
		cellConfirmDate.setParent(inItem);
		cellUpdateDate.setParent(inItem);
		cellUpdateUser.setParent(inItem);
		cellCancelDate.setParent(inItem);
		cellCancelUser.setParent(inItem);
		cellCancelReason.setParent(inItem);	
		
	}

}
