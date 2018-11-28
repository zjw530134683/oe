/*
 * Project Name:iVision_PR150202
 * File Name:OeInternalConfirmViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2015/5/15下午3:46:50
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.EmplInfo;
import com.tce.ivision.model.OrderInternalCheckInfo;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.oe.model.OrderEntryLotnoModel;
import com.tce.ivision.modules.wafer.model.WaferDetailOperation;
import com.tce.ivision.units.common.DateFormatUtil;

/**
 * ClassName: OeInternalConfirmViewCtrl <br/>
 * date: 2015/5/15 下午3:46:50 <br/>
 *
 * @author 130707
 * @version 
 * @since JDK 1.6
 */
public class OeInternalConfirmViewCtrl extends BaseViewCtrl implements ListitemRenderer {

	private Window winOeInternalConfirm;
	private Button btnExit;
	private Button btnSave;
	private Label edtOeConfirmDate;
	private Label lblOeConfirmDate;
	private Label edtOeConfirmUser;
	private Label lblOeConfirmUser;
	private Listheader headerOeInternalConfirmReason;
	private Listheader headerWaferData;
	private Listheader headerCustomerLotNo;
	private Listbox listboxOeInternalConfirm;
	
	LinkedHashMap<String, String> list = new LinkedHashMap<String, String>();
	List<OrderEntryLotnoModel> saveOrderEntryLotnoModels = new ArrayList<OrderEntryLotnoModel>();
	List<OrderInternalCheckInfo> orderInternalCheckInfoList = new ArrayList<OrderInternalCheckInfo>();//本次Check的資料
	List<OrderInternalCheckInfo> saveOrderInternalCheckInfoList = new ArrayList<OrderInternalCheckInfo>();//要跟Order Entry互傳最後要Save的(因可能Check1, Check2, Check3同時發生，因此再建一個saveOrderInternalCheckInfoList)
	Date nowtime = new Date();
	String checkType="";
	
	/**
	 * UserService
	 */
	private UserService userService = (UserService) SpringUtil.getBean("userService");
	
	/**
	 *
	 *
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		list = (LinkedHashMap<String, String>) execution.getArg().get("errMsg");
		saveOrderEntryLotnoModels = (List<OrderEntryLotnoModel>) execution.getArg().get("saveOrderEntryLotnoModels");
		saveOrderInternalCheckInfoList = (List<OrderInternalCheckInfo>) execution.getArg().get("saveOrderInternalCheckInfoList");
		checkType = (String) execution.getArg().get("CheckType");
		
		if(saveOrderEntryLotnoModels.size() > 0 && list.size() > 0){
			String[] errLotNoIdx = list.get("errLotNoIdx").split(";");
			for(int i=0; i<errLotNoIdx.length; i++){
				if(!"".equals(errLotNoIdx[i])){
					OrderInternalCheckInfo orInternalCheckInfo = new OrderInternalCheckInfo();
					orInternalCheckInfo.setCustomerLotno(saveOrderEntryLotnoModels.get(Integer.valueOf(errLotNoIdx[i])).getOrderLineLotno().getCustomerLotno());
					orInternalCheckInfo.setWaferData(saveOrderEntryLotnoModels.get(Integer.valueOf(errLotNoIdx[i])).getOrderLineLotno().getWaferData());
					orInternalCheckInfo.setOrderLineLotno(saveOrderEntryLotnoModels.get(Integer.valueOf(errLotNoIdx[i])).getOrderLineLotno());
					orInternalCheckInfo.setOrderNumber(saveOrderEntryLotnoModels.get(Integer.valueOf(errLotNoIdx[i])).getOrderLineLotno().getOrderNumber());
					
					orderInternalCheckInfoList.add(orInternalCheckInfo);
				}
			}
		}
		
		ListModel<OrderInternalCheckInfo> model = new ListModelList<OrderInternalCheckInfo>(orderInternalCheckInfoList);
		listboxOeInternalConfirm.setModel(model);
		listboxOeInternalConfirm.setItemRenderer(this);
		listboxOeInternalConfirm.renderAll();
		
		EmplInfo confirmUser = userService.getEmplInfoByEmplId(loginId);
		if(confirmUser != null){
			edtOeConfirmUser.setValue(confirmUser.getEmplFamilyname()+confirmUser.getEmplFirstname());
		}else{
			edtOeConfirmUser.setValue("");
		}
		edtOeConfirmDate.setValue(DateFormatUtil.getDateTimeFormatHHmm().format(nowtime));
		
	}
	
	public void onClick$btnSave(){
		//讓Constraint可生效，用trim()，等同於讓focus停在edtOeInternalConfirmReason
		if(listboxOeInternalConfirm.getItemCount() > 0){
			for(int i=0; i<listboxOeInternalConfirm.getItemCount(); i++){
				Textbox edtOeInternalConfirmReason = (Textbox)component.getFellow("edtOeInternalConfirmReason"+String.valueOf(i));
				edtOeInternalConfirmReason.getText().trim();
			}
		}
		
		Messagebox.show(Labels.getLabel("oe.save.confirm"), "Question", 
				Messagebox.OK | Messagebox.CANCEL, 
				Messagebox.QUESTION, 
				new org.zkoss.zk.ui.event.EventListener(){
				    public void onEvent(Event inEvt) throws InterruptedException,Exception {
				    	if ("onOK".equals(inEvt.getName())){
				    		if(orderInternalCheckInfoList.size() > 0){
				    		 	for(int i=0; i<orderInternalCheckInfoList.size(); i++){
				    		 		if("1".equals(checkType)){
				    		 			orderInternalCheckInfoList.get(i).setOeCheck1ConfirmUser(loginId);
				    		 			orderInternalCheckInfoList.get(i).setOeCheck1ConfirmCreateDate(nowtime);
				    		 			orderInternalCheckInfoList.get(i).setOeCheck1ConfirmUpdateDate(nowtime);
				    		 		}else if("2".equals(checkType)){
				    		 			orderInternalCheckInfoList.get(i).setOeCheck2ConfirmUser(loginId);
				    		 			orderInternalCheckInfoList.get(i).setOeCheck2ConfirmCreateDate(nowtime);
				    		 			orderInternalCheckInfoList.get(i).setOeCheck2ConfirmUpdateDate(nowtime);
				    		 		}else if("3".equals(checkType)){
				    		 			orderInternalCheckInfoList.get(i).setOeCheck3ConfirmUser(loginId);
				    		 			orderInternalCheckInfoList.get(i).setOeCheck3ConfirmCreateDate(nowtime);
				    		 			orderInternalCheckInfoList.get(i).setOeCheck3ConfirmUpdateDate(nowtime);
				    		 		}
				    		 		orderInternalCheckInfoList.get(i).setCreateDate(nowtime);
				    		 		orderInternalCheckInfoList.get(i).setCreateUser(loginId);
				    		 	}
				    		 	
				    		 	if("1".equals(checkType)){
				    		 		Messagebox.show(Labels.getLabel("oe.save.message.check1SaveOK"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				    		 	}else if("2".equals(checkType)){
				    		 		Messagebox.show(Labels.getLabel("oe.save.message.check2SaveOK"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				    		 	}else if("3".equals(checkType)){
				    		 		Messagebox.show(Labels.getLabel("oe.save.message.check3SaveOK"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				    		 	}
				    		 	
				    		 	saveOrderInternalCheckInfoList.addAll(orderInternalCheckInfoList);
				    		 	winOeInternalConfirm.getParent().setAttribute("saveOrderInternalCheckInfoList", saveOrderInternalCheckInfoList);//將orderInternalCheckInfoList傳遞參數給Order Entry畫面
				    		 	winOeInternalConfirm.detach();
				    		}
				    	 }
				    }
				});
	}
	
	public void onClick$btnExit(){
		if(orderInternalCheckInfoList.size()>0){
			for(int i=0; i<orderInternalCheckInfoList.size(); i++){
				orderInternalCheckInfoList.get(i).setOeCheck1ConfirmReason("");
			}
		}
		winOeInternalConfirm.detach();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(Listitem inItem, Object inData, int inIndex) throws Exception {
		final OrderInternalCheckInfo oeInternalCheckInfo = (OrderInternalCheckInfo) inData;
		
		final Listcell cellCustomerLotNo = new Listcell();
		final Listcell cellWaferData = new Listcell();
		final Listcell cellOeInternalConfirmReason = new Listcell();
		
		final Textbox edtOeInternalConfirmReason = new Textbox();
		
		cellCustomerLotNo.setId("cellCustomerLotNo"+inIndex);
		cellWaferData.setId("cellWaferData"+inIndex);
		cellOeInternalConfirmReason.setId("cellOeInternalConfirmReason"+inIndex);
		edtOeInternalConfirmReason.setId("edtOeInternalConfirmReason"+inIndex);
		

		//CUSTOMER_LOTNO
		if(!"".equals(oeInternalCheckInfo.getCustomerLotno()) && oeInternalCheckInfo.getCustomerLotno() != null){
			cellCustomerLotNo.setLabel(oeInternalCheckInfo.getCustomerLotno());
		}else{
			cellCustomerLotNo.setLabel("");
		}
		
		//WAFER_DATA
		if(!"".equals(oeInternalCheckInfo.getWaferData()) && oeInternalCheckInfo.getWaferData() != null){
			cellWaferData.setLabel(oeInternalCheckInfo.getWaferData());
		}else{
			cellWaferData.setLabel("");
		}
		
		//OE_INTERNAL_CONFIRM_REASON
		edtOeInternalConfirmReason.setParent(cellOeInternalConfirmReason);
		edtOeInternalConfirmReason.setWidth("98%");
		edtOeInternalConfirmReason.setStyle("background:#FFD");
		edtOeInternalConfirmReason.setConstraint("no empty");
		edtOeInternalConfirmReason.addEventListener("onChange", new EventListener() {
			public void onEvent(Event inEvent) throws Exception{
				if("1".equals(checkType)){
					oeInternalCheckInfo.setOeCheck1ConfirmReason(edtOeInternalConfirmReason.getText());
				}else if("2".equals(checkType)){
					oeInternalCheckInfo.setOeCheck2ConfirmReason(edtOeInternalConfirmReason.getText());
				}else if("3".equals(checkType)){
					oeInternalCheckInfo.setOeCheck3ConfirmReason(edtOeInternalConfirmReason.getText());
				}
			}
		});
		
		cellCustomerLotNo.setParent(inItem);
		cellWaferData.setParent(inItem);
		cellOeInternalConfirmReason.setParent(inItem);
	}
}
