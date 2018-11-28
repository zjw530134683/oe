/*
 * Project Name:iVision
 * File Name:OffloadWaferConfirmViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2014/12/05上午10:24:06
 * 
 * 說明:
 * 
 * 修改歷史:
 * 2014.12.05          Allison
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.hibernate.mapping.Constraint;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;

import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.ZkComboboxControl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
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
import com.tce.ivision.model.ExchangeRateOffload;
import com.tce.ivision.model.LotResult;
import com.tce.ivision.model.ShippingDetail;
import com.tce.ivision.model.UiFieldParam;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.model.WaferInfo;
import com.tce.ivision.model.business.VWWipLotInfo;

import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.oe.model.OffloadOperation;
import com.tce.ivision.modules.oe.model.OffloadShippingConfirm;
import com.tce.ivision.modules.oe.model.OffloadWaferConfirm;
import com.tce.ivision.modules.oe.service.OffloadOperationService;
import com.tce.ivision.modules.setup.model.ShippingWaferNoSetup;
import com.tce.ivision.modules.setup.model.ShippingWaferNoSetupDetail;
import com.tce.ivision.modules.setup.service.ShippingWaferNoSetupService;
import com.tce.ivision.modules.shp.service.LotInfoService;
import com.tce.ivision.modules.shp.service.ShippingService;
import com.tce.ivision.modules.shp.service.WaferInfoService;

import com.tce.ivision.units.common.ZkComboboxControl;


/**
 * ClassName: OffloadWaferConfirmViewCtrl <br/>
 * date: 2014/12/05 上午10:24:06 <br/>
 *
 * @author 130707
 * @version 
 * @since JDK 1.6
 */
public class OffloadWaferConfirmViewCtrl extends BaseViewCtrl implements ListitemRenderer{
	
	private static final String ServletContext = null;
	/**
	 * zk component initial
	 */
	private Window winOffloadWaferConfirm;
	private Button btnShippingAll;
	private Button btnConfirm;
	private Button btnExit;
	private Button btnClear;
	private Listbox listboxOffloadWaferConfirm;
	private Listheader headerWaferNo;
	private Listheader headerWaferConfirm;
	private Listheader headerConfirmDate;
	
	

	private OffloadOperationService offloadOperationService = (OffloadOperationService) SpringUtil.getBean("offloadOperationService");
	/**
	 * UserService
	 */
	private UserService userService = (UserService) SpringUtil.getBean("userService");
	
	private List<OffloadWaferConfirm> offloadWaferConfirmLists = new ArrayList<OffloadWaferConfirm>();
	
	private List<OffloadShippingConfirm> offloadShippingLists;
	
	private String waferData="";
	
	private String customerLotNo="";
	
	private String shippingConfirmIdx="";
	
	private String offloadStatus="";

	/**
	 *
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		waferData = (String) execution.getArg().get("waferData");
		customerLotNo = (String) execution.getArg().get("customerLotNo"); 
		offloadWaferConfirmLists = (List<OffloadWaferConfirm>) execution.getArg().get("offloadWaferConfirmList");
		offloadShippingLists = (List<OffloadShippingConfirm>) execution.getArg().get("offloadShippingLists");
		shippingConfirmIdx = (String) execution.getArg().get("shippingConfirmSelectIdx");
		offloadStatus = (String) execution.getArg().get("offloadStatus");
		if("Offload Close".equals(offloadStatus)){
			btnShippingAll.setDisabled(true);
			btnConfirm.setDisabled(true);
			btnClear.setDisabled(true);
			shippingConfirmIdx="99999";//因Wafer Confirm欄位能否Enable會靠shippingConfirmIdx來比對，因此若為Offload Close要只能Review不能編輯，所以要設成99999
		}
		this.formShow();
	}
	
	
	
	public void onClick$btnExit(){
		int count=0;
		for(int i=0; i<offloadWaferConfirmLists.size(); i++){
			if("".equals(offloadWaferConfirmLists.get(i).getWaferConfirm()) || offloadWaferConfirmLists.get(i).getWaferConfirm() == null){
				count=count+1;
			}else{
				if(offloadWaferConfirmLists.get(i).getConfirmDate() == null || "".equals(offloadWaferConfirmLists.get(i).getConfirmDate())){
					Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.noConfirm"), "Information", Messagebox.OK, Messagebox.INFORMATION);
					return;
				}
				if(offloadWaferConfirmLists.get(i).getShippingConfirmIdx() == null){
					offloadWaferConfirmLists.get(i).setShippingConfirmIdx(shippingConfirmIdx);
				}
			}
		}
		if(count == offloadWaferConfirmLists.size()){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.noWaferConfirmExit"),
					"Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, 
					new org.zkoss.zk.ui.event.EventListener<Event>(){
					    public void onEvent(Event inEvt){
					    	if (Messagebox.ON_OK.equals(inEvt.getName())){
					    		winOffloadWaferConfirm.getParent().setAttribute("offloadWaferConfirm", offloadWaferConfirmLists);//將offloadWaferConfirmLists傳遞參數給Offload Shipping Confirm
					    		winOffloadWaferConfirm.getParent().setAttribute("shippingConfirmIdx", shippingConfirmIdx);
					    		Events.sendEvent(new Event("onClick", (Button)(winOffloadWaferConfirm.getParent().getFellow("btnSearch"))));
					    		winOffloadWaferConfirm.detach();
					    	}
					    }
			});
		}else{
			winOffloadWaferConfirm.getParent().setAttribute("offloadWaferConfirm", offloadWaferConfirmLists);//將offloadWaferConfirmLists傳遞參數給Offload Shipping Confirm
			winOffloadWaferConfirm.getParent().setAttribute("shippingConfirmIdx", shippingConfirmIdx);
			Events.sendEvent(new Event("onClick", (Button)(winOffloadWaferConfirm.getParent().getFellow("btnSearch"))));
    		winOffloadWaferConfirm.detach();
		}

	}

	
	
	public void formShow(){
		if(offloadWaferConfirmLists.size() <= 0){
			String[] waferNo = waferData.split(";");
			for(int i=0; i<waferNo.length; i++){
				OffloadWaferConfirm offloadWaferConfirm = new OffloadWaferConfirm();
				if(offloadShippingLists != null && offloadShippingLists.size() > 0){
					for(int j=0; j<offloadShippingLists.size(); j++){
						if(offloadShippingLists.get(j).getConfirmDate() != null){
							String[] tmpShippingWaferNo = offloadShippingLists.get(j).getShippingWaferData().split(";");
							for(int k=0; k<tmpShippingWaferNo.length; k++){
								if(String.valueOf(Integer.valueOf(waferNo[i])).equals(tmpShippingWaferNo[k])){
									offloadWaferConfirm.setWaferConfirm("Shipping");
									offloadWaferConfirm.setConfirmDate(DateFormatUtil.getDateTimeFormatHHmm().format(offloadShippingLists.get(j).getConfirmDate()));
									offloadWaferConfirm.setShippingConfirmIdx(String.valueOf(j));
								}
							}
							String[] tmpRmaWaferNo = offloadShippingLists.get(j).getRmaWaferData().split(";");
							for(int k=0; k<tmpRmaWaferNo.length; k++){
								if(String.valueOf(Integer.valueOf(waferNo[i])).equals(tmpRmaWaferNo[k])){
									offloadWaferConfirm.setWaferConfirm("RMA");
									offloadWaferConfirm.setConfirmDate(DateFormatUtil.getDateTimeFormatHHmm().format(offloadShippingLists.get(j).getConfirmDate()));
									offloadWaferConfirm.setShippingConfirmIdx(String.valueOf(j));
								}
							}
							String[] tmpScrapWaferNo = offloadShippingLists.get(j).getScrapWaferData().split(";");
							for(int k=0; k<tmpScrapWaferNo.length; k++){
								if(String.valueOf(Integer.valueOf(waferNo[i])).equals(tmpScrapWaferNo[k])){
									offloadWaferConfirm.setWaferConfirm("Scrap");
									offloadWaferConfirm.setConfirmDate(DateFormatUtil.getDateTimeFormatHHmm().format(offloadShippingLists.get(j).getConfirmDate()));
									offloadWaferConfirm.setShippingConfirmIdx(String.valueOf(j));
								}
							}
						}
					}
					if(Integer.valueOf(waferNo[i]) < 10){
						offloadWaferConfirm.setWaferNo(customerLotNo + "-0" + waferNo[i]);
					}else{
						offloadWaferConfirm.setWaferNo(customerLotNo + "-" + waferNo[i]);
					}
				}else{
					if(Integer.valueOf(waferNo[i]) < 10){
						offloadWaferConfirm.setWaferNo(customerLotNo + "-0" + waferNo[i]);
					}else{
						offloadWaferConfirm.setWaferNo(customerLotNo + "-" + waferNo[i]);
					}
				}
				offloadWaferConfirmLists.add(offloadWaferConfirm);
			}
		}
		
		listboxOffloadWaferConfirm.setModel(new ListModelList<OffloadWaferConfirm>(offloadWaferConfirmLists));
		listboxOffloadWaferConfirm.setItemRenderer(this);
	}

	
	public void onClick$btnShippingAll(){
		for(int i=0; i<offloadWaferConfirmLists.size(); i++){
			Combobox cbxWaferConfirm = (Combobox)component.getFellow("comboboxWaferConfirm"+i);
			if(offloadWaferConfirmLists.get(i).getShippingConfirmIdx() == null){
				cbxWaferConfirm.setSelectedIndex(0);
				offloadWaferConfirmLists.get(i).setWaferConfirm(cbxWaferConfirm.getText());
				offloadWaferConfirmLists.get(i).setShippingConfirmIdx(shippingConfirmIdx);
			}else if(offloadWaferConfirmLists.get(i).getShippingConfirmIdx().equals(shippingConfirmIdx)){
				cbxWaferConfirm.setSelectedIndex(0);
				offloadWaferConfirmLists.get(i).setWaferConfirm(cbxWaferConfirm.getText());
				offloadWaferConfirmLists.get(i).setShippingConfirmIdx(shippingConfirmIdx);
			}
		}
	}
	
	
	public void onClick$btnConfirm(){		
		Date nowTime = new Date();
		for(int i=0; i<offloadWaferConfirmLists.size(); i++){
			if(!"".equals(offloadWaferConfirmLists.get(i).getWaferConfirm()) && offloadWaferConfirmLists.get(i).getWaferConfirm() != null){
				if(shippingConfirmIdx.equals(offloadWaferConfirmLists.get(i).getShippingConfirmIdx())){
					offloadWaferConfirmLists.get(i).setConfirmDate(DateFormatUtil.getDateTimeFormatHHmm().format(nowTime));
				}
			}else{
				offloadWaferConfirmLists.get(i).setConfirmDate("");
				offloadWaferConfirmLists.get(i).setShippingConfirmIdx(null);
			}
		}
		
		listboxOffloadWaferConfirm.setModel(new ListModelList<OffloadWaferConfirm>(offloadWaferConfirmLists));
		listboxOffloadWaferConfirm.setItemRenderer(this);
	}
	
	
	public void onClick$btnClear(){
		for(int i=0; i<offloadWaferConfirmLists.size(); i++){
			if(shippingConfirmIdx.equals(offloadWaferConfirmLists.get(i).getShippingConfirmIdx())){
				offloadWaferConfirmLists.get(i).setWaferConfirm(null);
				offloadWaferConfirmLists.get(i).setConfirmDate(null);
			}
		}
		listboxOffloadWaferConfirm.setModel(new ListModelList<OffloadWaferConfirm>(offloadWaferConfirmLists));
		listboxOffloadWaferConfirm.setItemRenderer(this);
	}
	
	
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	protected void initialComboboxItem() throws Exception {
		
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
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(Listitem inItem, Object inData, final int inIndex) throws Exception {
		final OffloadWaferConfirm offloadWaferConfirm = (OffloadWaferConfirm) inData;
		
		if(offloadWaferConfirm.getShippingConfirmIdx() != null && !"".equals(offloadWaferConfirm.getShippingConfirmIdx())){
			if(!shippingConfirmIdx.equals(offloadWaferConfirm.getShippingConfirmIdx())){
				inItem.setStyle("background:#DCDCDC");
			}
		}
		
		//宣告Listcell
		Listcell cellWaferNo = new Listcell();
		Listcell cellWaferConfirm = new Listcell();
		final Listcell cellConfirmDate = new Listcell();
		
		final Combobox comboboxWaferConfirm = new Combobox();
		comboboxWaferConfirm.setId("comboboxWaferConfirm"+String.valueOf(inIndex));
		
		//WAFER_NO
		cellWaferNo.setId("cellWaferNo"+String.valueOf(inIndex));
		cellWaferNo.setLabel(offloadWaferConfirm.getWaferNo());
		
		
		//WAFER_CONFIRM
		final List<UiFieldSet> waferConfirm=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.OffloadWaferConfirmViewCtrl", "WAFER_CONFIRM_TYPE");
		if (waferConfirm.size()>0){
			for(int i=0; i<waferConfirm.size(); i++){
				for(int j=0; j<waferConfirm.get(i).getUiFieldParams().size(); j++){
					comboboxWaferConfirm.appendItem(waferConfirm.get(i).getUiFieldParams().get(j).getParaValue());
					comboboxWaferConfirm.getItemAtIndex(j).setValue(waferConfirm.get(i).getUiFieldParams().get(j).getParaValue());
				}
			}
		}

		if(offloadWaferConfirm.getWaferConfirm() != null && !"".equals(offloadWaferConfirm.getWaferConfirm())){
			for(int i=0; i<comboboxWaferConfirm.getItemCount(); i++){
				if(getParaValueByMeaning(offloadWaferConfirm.getWaferConfirm(), waferConfirm.get(0).getUiFieldParams()).equals(comboboxWaferConfirm.getItemAtIndex(i).getValue())){
					comboboxWaferConfirm.setSelectedIndex(i);
				}
			}
		}else{
			comboboxWaferConfirm.setText("");
		}
		
		if(offloadWaferConfirm.getShippingConfirmIdx() != null && !"".equals(offloadWaferConfirm.getShippingConfirmIdx())){
			if(!shippingConfirmIdx.equals(offloadWaferConfirm.getShippingConfirmIdx())){
				comboboxWaferConfirm.setDisabled(true);
			}
		}
		comboboxWaferConfirm.setParent(cellWaferConfirm);
		comboboxWaferConfirm.addEventListener("onSelect", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				if(!"".equals(comboboxWaferConfirm.getText())){
					offloadWaferConfirmLists.get(inIndex).setWaferConfirm(comboboxWaferConfirm.getText());
					offloadWaferConfirmLists.get(inIndex).setShippingConfirmIdx(shippingConfirmIdx);
				}else{
					offloadWaferConfirmLists.get(inIndex).setConfirmDate("");
					offloadWaferConfirmLists.get(inIndex).setShippingConfirmIdx(null);
					offloadWaferConfirmLists.get(inIndex).setWaferConfirm("");
					cellConfirmDate.setLabel("");
				}
			}
		});
				
		
		//CONFIRM_DATE
		cellConfirmDate.setId("cellConfirmDate"+String.valueOf(inIndex));
		if(offloadWaferConfirm.getConfirmDate() != null){
			if(shippingConfirmIdx.equals(offloadWaferConfirm.getShippingConfirmIdx())){
				cellConfirmDate.setLabel(offloadWaferConfirm.getConfirmDate());
			}else{
				cellConfirmDate.setLabel(offloadWaferConfirm.getConfirmDate());
			}
		}else{
			cellConfirmDate.setLabel("");
		}
		
		
		cellWaferNo.setParent(inItem);
		cellWaferConfirm.setParent(inItem);
		cellConfirmDate.setParent(inItem);

	}
}
