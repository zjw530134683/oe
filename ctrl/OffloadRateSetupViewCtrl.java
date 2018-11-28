/*
 * Project Name:iVision
 * File Name:WaferNoComposingTestViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2014/12/04上午10:24:06
 * 
 * 說明:
 * Wafer No Composing Test畫面
 * 
 * 修改歷史:
 * 2014.12.04          Allison
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
import com.tce.ivision.modules.oe.service.OffloadOperationService;
import com.tce.ivision.modules.setup.model.ShippingWaferNoSetup;
import com.tce.ivision.modules.setup.model.ShippingWaferNoSetupDetail;
import com.tce.ivision.modules.setup.service.ShippingWaferNoSetupService;
import com.tce.ivision.modules.shp.service.LotInfoService;
import com.tce.ivision.modules.shp.service.ShippingService;
import com.tce.ivision.modules.shp.service.WaferInfoService;

import com.tce.ivision.units.common.ZkComboboxControl;


/**
 * ClassName: OffloadRateSetupViewCtrl <br/>
 * date: 2014/12/04 上午10:24:06 <br/>
 *
 * @author 130707
 * @version 
 * @since JDK 1.6
 */
public class OffloadRateSetupViewCtrl extends BaseViewCtrl implements ListitemRenderer{
	
	private static final String ServletContext = null;
	/**
	 * zk component initial
	 */
	private Window winOffloadRateSetup;
	private Label lblYear;
	private Combobox cbxYear;
	private Button btnSave;
	private Button btnExit;
	private Listbox listboxExchangeRateOffload;
	private Listheader headerMonth;
	private Listheader headerNtd;
	private Listheader headerUsd;
	private Listheader headerUpdateDate;
	private Listheader headerUpdateUser;


	private OffloadOperationService offloadOperationService = (OffloadOperationService) SpringUtil.getBean("offloadOperationService");
	/**
	 * UserService
	 */
	private UserService userService = (UserService) SpringUtil.getBean("userService");
	
	private List<ExchangeRateOffload> exchangeRateOffloadLists;

	/**
	 *
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		
		this.formShow();
	}
	
	
	
	public void onClick$btnExit(){
		winOffloadRateSetup.detach();
	}

	public void onSelect$cbxYear(){
		this.formShow();
	}
	
	public void onClick$btnSave(){
		boolean saveFlag=false;
		for(int i=0; i<exchangeRateOffloadLists.size(); i++){
			Doublebox edtNtd = (Doublebox)component.getFellow("doubleboxNtd"+String.valueOf(i));
			Doublebox edtUsd = (Doublebox)component.getFellow("doubleboxUsd"+String.valueOf(i));
			edtNtd.setFocus(true);
			edtUsd.setFocus(true);
			
			if(!"".equals(edtNtd.getText())){
				DecimalFormat numFormat  = new DecimalFormat("#.###");
				exchangeRateOffloadLists.get(i).setNtdRate(BigDecimal.valueOf(Double.valueOf(edtNtd.getValue())).setScale(3, RoundingMode.FLOOR));
			}else{
				exchangeRateOffloadLists.get(i).setNtdRate(null);
			}
			if(!"".equals(edtUsd.getText())){
				exchangeRateOffloadLists.get(i).setUsdRate(BigDecimal.valueOf(Double.valueOf(edtUsd.getValue())).setScale(3, RoundingMode.FLOOR));
			}else{
				exchangeRateOffloadLists.get(i).setUsdRate(null);
			}
			exchangeRateOffloadLists.get(i).setUpdateUser(loginId);
		}
		saveFlag = offloadOperationService.saveExchangeRateOffload(exchangeRateOffloadLists);
		if(saveFlag){
			Messagebox.show(Labels.getLabel("common.message.saveok"),"Information", Messagebox.OK, Messagebox.INFORMATION);
			this.formShow();
		}
	}
	
	public void formShow(){
		exchangeRateOffloadLists = offloadOperationService.getExchangeRateOffloadLists(cbxYear.getText());
		listboxExchangeRateOffload.setModel(new ListModelList<ExchangeRateOffload>(exchangeRateOffloadLists));
		listboxExchangeRateOffload.setItemRenderer(this);
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	protected void initialComboboxItem() throws Exception {
		//Year
		Date nowTime= new Date();
		for(int i=0; i<100; i++){//cbxYear預設選單內容為今年起+100年的都顯示出來
			cbxYear.appendItem(String.valueOf(Integer.valueOf(DateFormatUtil.getDateTimeFormat().format(nowTime).substring(0, 4))+i));
		}
		cbxYear.setSelectedIndex(0);
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
		final ExchangeRateOffload exchangeRateOffload = (ExchangeRateOffload) inData;
		
		//宣告Listcell
		Listcell cellMonth = new Listcell();
		Listcell cellNtd = new Listcell();
		Listcell cellUsd = new Listcell();
		Listcell cellUpdateDate = new Listcell();
		Listcell cellUpdateUser = new Listcell();
		
		final Doublebox doubleboxNtd = new Doublebox();
		final Doublebox doubleboxUsd = new Doublebox();
		doubleboxNtd.setFormat("#,##0.###");
		doubleboxUsd.setFormat("#,##0.###");
		doubleboxNtd.setRoundingMode("FLOOR");
		doubleboxUsd.setRoundingMode("FLOOR");
		
		//MONTH
		cellMonth.setLabel(exchangeRateOffload.getMonth());
		
		//NTD (vs.JPY)
		if(exchangeRateOffload.getNtdRate() != null){
			doubleboxNtd.setText(String.valueOf(exchangeRateOffload.getNtdRate()));
		}else{
			doubleboxNtd.setText("");
		}
		doubleboxNtd.setId("doubleboxNtd"+String.valueOf(inIndex));
		doubleboxNtd.setParent(cellNtd);
		doubleboxNtd.addEventListener("onChange", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				if(!"".equals(doubleboxNtd.getText())){
					doubleboxUsd.setConstraint("no empty");
				}else{
					doubleboxUsd.setConstraint("");
					doubleboxNtd.setConstraint("");
					if(!"".equals(doubleboxUsd.getText())){
						doubleboxNtd.setConstraint("no empty");
						doubleboxUsd.setConstraint("");
					}else{
						doubleboxNtd.setConstraint("");
						doubleboxUsd.setConstraint("");
					}
				}
			}
		});
		
		//USD (vs.JPY)
		if(exchangeRateOffload.getUsdRate() != null){
			doubleboxUsd.setText(String.valueOf(exchangeRateOffload.getUsdRate()));
		}else{
			doubleboxUsd.setText("");
		}
		doubleboxUsd.setId("doubleboxUsd"+String.valueOf(inIndex));
		doubleboxUsd.setParent(cellUsd);
		doubleboxUsd.addEventListener("onChange", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				if(!"".equals(doubleboxUsd.getText())){
					doubleboxNtd.setConstraint("no empty");
				}else{
					doubleboxUsd.setConstraint("");
					doubleboxNtd.setConstraint("");
					if(!"".equals(doubleboxNtd.getText())){
						doubleboxUsd.setConstraint("no empty");
						doubleboxNtd.setConstraint("");
					}else{
						doubleboxUsd.setConstraint("");
						doubleboxNtd.setConstraint("");
					}
				}
			}
		});
		
		//UPDATE_DATE
		if(exchangeRateOffload.getUpdateDate() != null){
			if(exchangeRateOffload.getNtdRate() != null && exchangeRateOffload.getUsdRate() != null){
				cellUpdateDate.setLabel(DateFormatUtil.getDateTimeFormatHHmm().format(exchangeRateOffload.getUpdateDate()));
			}else{
				cellUpdateDate.setLabel("");
			}
		}else{
			cellUpdateDate.setLabel("");
		}
		
		//UPDATE_USER
		EmplInfo updateUser = userService.getEmplInfoByEmplId(exchangeRateOffload.getUpdateUser());
		if(updateUser != null){
			if(exchangeRateOffload.getNtdRate() != null && exchangeRateOffload.getUsdRate() != null){
				cellUpdateUser.setLabel(updateUser.getEmplFamilyname()+updateUser.getEmplFirstname());
			}else{
				cellUpdateUser.setLabel("");
			}
		}else{
			cellUpdateUser.setLabel("");
		}
		
		
		cellMonth.setParent(inItem);
		cellNtd.setParent(inItem);
		cellUsd.setParent(inItem);
		cellUpdateDate.setParent(inItem);
		cellUpdateUser.setParent(inItem);
	}
}
