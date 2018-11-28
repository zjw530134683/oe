/*
 * Project Name:iVision
 * File Name:OffloadOperationViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2014/12/02 上午10:16:16
 * 
 * 說明:
 * 
 * 修改歷史:
 * 2014-12-02 Allison  Initialize
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
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

import com.tce.ivision.config.Config;
import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.model.EmplInfo;
import com.tce.ivision.model.ExchangeRateOffload;
import com.tce.ivision.model.LotResult;
import com.tce.ivision.model.OffloadLotno;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.UiFieldParam;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.oe.model.OffloadOperation;
import com.tce.ivision.modules.oe.model.OffloadOperationParameter;
import com.tce.ivision.modules.oe.model.OffloadWaferConfirm;
import com.tce.ivision.modules.oe.render.OffloadOperationRender;
import com.tce.ivision.modules.oe.service.OffloadOperationService;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.DateUtil;
import com.tce.ivision.units.common.ZkComboboxControl;

/**
 * ClassName: OffloadOperationViewCtrl <br/>
 * date: Date:2014/12/02 上午10:16:16 <br/>
 *
 * @author Allison
 * @version 
 * @since JDK 1.6
 */
public class OffloadOperationViewCtrl extends BaseViewCtrl implements ListitemRenderer{

	/**
	 * serialVersionUID:
	 * @since JDK 1.6
	 */
	private static final long serialVersionUID = 1L;
	private Window winOffloadOperationFunction;
	private Checkbox chkCustomer;
	private Label lbCustomer;
	private Combobox cbCustomer;
	private Label lbProduct;
	private Combobox cbxProduct;
	private Checkbox chkOrderDate;
	private Label lbOrderStartDate;
	private Datebox dateBeginDate;
	private Label lbOrderEndDate;
	private Datebox dateEndDate;
	private Checkbox chkOffloadClose;
	private Button btnSearch;
	private Button btnOffloadRate;
	private Listbox listboxOffloadInformation;
	private Listheader headerSelect;
	private Listheader headerOffloadStatus;
	private Listheader headerCustomer;
	private Listheader headerOrderNumber;
	private Listheader headerPoNo;
	private Listheader headerProduct;
	private Listheader headerCustomerLotNo;
	private Listheader headerWaferQty;
	private Listheader headerOrderPrice;
	private Listheader headerCurrency;
	private Listheader headerOffloadType;
	private Listheader headerOffloadTo;
	private Listheader headerOffloadPo;
	private Listheader headerOffloadDueDate;
	private Listheader headerWaferId;
	private Listheader headerConfirmDate;
	private Listheader headerUpdateDate;
	private Listheader headerUpdateUser;
	private Listheader headerCancelDate;
	private Listheader headerCancelUser;
	private Listheader headerCancelReason;
	private Button btnOffloadConfirm;
	private Button btnModify;
	private Button btnPrint;
	private Button btnShipping;
	private Button btnCancel;
	private Button btnExit;
	
	private OffloadOperationService offloadOperationService = (OffloadOperationService) SpringUtil.getBean("offloadOperationService");
	/**
	 * CustomerInformationService
	 */
	private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil.getBean("customerInformationService");
	/**
	 * UserService
	 */
	private UserService userService = (UserService) SpringUtil.getBean("userService");
	/**
	 * config bean, in this case, i use the LocalTmpFileDir parameter
	 */
	private Config config = (Config) SpringUtil.getBean("config");
	/**
	 * customerTable List 用於接取query出來的customerTable Bean
	 */
	private List<CustomerTable> customerTableList;
	/**
	 * 搜尋出來的Offload Operation List
	 */
	private List<OffloadOperation> offloadOperationList;
	/**
	 * 要Save的Offload Operation List
	 */
	private List<OffloadOperation> saveOffloadOperationList;
	/**
	 * 搜尋參數的bean 
	 */
	private OffloadOperationParameter queryParameter;
	/**
	 * tmpFileFolder LocalTmpFileDir+'/'
	 */
	private final String tmpFileFolder = config.getLocalTmpFileDir()+"/"+"OffloadForm/";
	private String excelPath="";
	private DecimalFormat jpdFormat = new DecimalFormat("#,###");
	private DecimalFormat usdFormat = new DecimalFormat("#,###.00");
	
	/**
	 * 頁面載入後開始執行初始化的function
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		queryParameter = new OffloadOperationParameter();
		inComp.setAttribute("queryParameter", queryParameter);
		binder.bindBean("value", queryParameter);
		binder.loadAll();
		
		//建立Temp dir
		 File tmpB2bFolder=new File(tmpFileFolder);
		 if(!tmpB2bFolder.exists()){
			tmpB2bFolder.mkdir();
		 }
	}
	
	private boolean checkQueryParameterByCheckCustomer(OffloadOperationParameter inParam){
		boolean checkFlag = true;
		if("".equals(inParam.getCustomerId()) || "".equals(inParam.getProduct())){
			checkFlag=false;
		}
		return checkFlag;
	}
	
	private boolean checkQueryParameterByCheckOrderDate(OffloadOperationParameter inParam){
		boolean checkFlag = true;
		log.debug(inParam.getBeginDate());
		log.debug(inParam.getEndDate());
		if(inParam.getBeginDate() == null || inParam.getEndDate() == null){
			checkFlag=false;
		}
		return checkFlag;
	}
	
	/**
	 * onSelect$cbCustomer:cbCustomer onSelect event
	 * @author honda
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public void onSelect$cbCustomer() throws Exception{
		log.debug("Selected CustomerId:"+queryParameter.getCustomerId());
		List<String> productList = offloadOperationService.getProductWithStatusAndDateAndCustomer(queryParameter.getCustomerId());
		ZkComboboxControl.setComboboxClear(cbxProduct);
		queryParameter.setProduct("");
		ZkComboboxControl.setComboboxItems(cbxProduct, productList, "toString", "", false);
	}
	
	public void onClick$chkCustomer() throws Exception{
		queryParameter.setCustomerFlag(chkCustomer.isChecked());
		if(chkCustomer.isChecked()){
			customerTableList=customerInformationService.getCustomerTableByBusPurpose("C");
			ZkComboboxControl.setComboboxClear(cbCustomer);
			ZkComboboxControl.setComboboxItemValues(cbCustomer, customerTableList, "getCustomerShortName","getCustomerId", "isCancelFlag",false);
			ZkComboboxControl.setComboboxClear(cbxProduct);
			queryParameter.setCustomerId(cbCustomer.getValue());
		}else{
			customerTableList=null;
			ZkComboboxControl.setComboboxClear(cbCustomer);
			ZkComboboxControl.setComboboxClear(cbxProduct);
			queryParameter.setCustomerId("");
			queryParameter.setProduct("");
		}
	}
	
	public void onClick$chkOrderDate(){
		queryParameter.setOrderDateFlag(chkOrderDate.isChecked());
		if(!chkOrderDate.isChecked()){
			dateBeginDate.setText("");
			dateEndDate.setText("");
			queryParameter.setBeginDate(null);
			queryParameter.setEndDate(null);	
		}
	}
	
	public void onClick$chkOffloadClose(){
		queryParameter.setOffloadCloseFlag(chkOffloadClose.isChecked());
	}
	
	public void onClick$btnSearch(){
		queryParameter.setProduct(cbxProduct.getText());
		queryParameter.setCustomerName(cbCustomer.getText());
		log.debug(queryParameter.getCustomerId());

		if(!chkCustomer.isChecked() && !chkOrderDate.isChecked()){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.QueryParameterNotEnough"), "Error", Messagebox.OK, Messagebox.ERROR);
			listboxOffloadInformation.setModel(new ListModelList<OffloadOperation>());
			listboxOffloadInformation.setItemRenderer(this);
			listboxOffloadInformation.renderAll();
			return;
		}
		
		if(chkCustomer.isChecked()){//若勾選要以CUSTOMER+PRODUCT來查詢，則CUSTOMER+PRODUCT不能空白
			if(checkQueryParameterByCheckCustomer(queryParameter) == false){
				Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.QueryParameterNotEnoughByCustomer"), "Error", Messagebox.OK, Messagebox.ERROR);
				listboxOffloadInformation.setModel(new ListModelList<OffloadOperation>());
				listboxOffloadInformation.setItemRenderer(this);
				listboxOffloadInformation.renderAll();
				return;
			}
		}
		
		if(chkOrderDate.isChecked()){//若勾選要以CUSTOMER+PRODUCT來查詢，則CUSTOMER+PRODUCT不能空白
			if(checkQueryParameterByCheckOrderDate(queryParameter) == false){
				Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.QueryParameterNotEnoughByOrderDate"), "Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}			
		}
		
		listboxOffloadInformation.setMultiple(false);//要先將Multiple設定False
		listboxOffloadInformation.setCheckmark(false);//再將Checkmark設定False
		offloadOperationList = offloadOperationService.getOffloadOperations(queryParameter);
	
		if(offloadOperationList.size()>0){
			OffloadLotno offloadLotnos = (OffloadLotno) winOffloadOperationFunction.getAttribute("offloadLotno");
			if(offloadLotnos != null){
				if("30".equals(offloadLotnos.getOffloadStatus())){
					if(!queryParameter.isOffloadCloseFlag()){
						Messagebox.show(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.message.offloadClose",new Object[] {offloadLotnos.getOrderNumber(), offloadLotnos.getCustomerLotno()}), "Information", Messagebox.OK, Messagebox.INFORMATION);
						winOffloadOperationFunction.setAttribute("offloadLotno", null);
					}
				}
			}
			
			saveOffloadOperationList = offloadOperationList;
			listboxOffloadInformation.setModel(new ListModelList<OffloadOperation>(offloadOperationList));
			listboxOffloadInformation.setItemRenderer(this);
			listboxOffloadInformation.renderAll();
			listboxOffloadInformation.setCheckmark(true);//Search完後先將Checkmark設定true
			listboxOffloadInformation.setMultiple(true);//再將Multiple設定true，這樣子才不會出現若重履按Search，一下變成checkbox，一下變成radiobox的情況
		}else{
			OffloadLotno offloadLotnos = (OffloadLotno) winOffloadOperationFunction.getAttribute("offloadLotno");
			if(offloadLotnos != null){
				if("30".equals(offloadLotnos.getOffloadStatus())){
					if(!queryParameter.isOffloadCloseFlag()){
						Messagebox.show(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.message.offloadClose",new Object[] {offloadLotnos.getOrderNumber(), offloadLotnos.getCustomerLotno()}), "Information", Messagebox.OK, Messagebox.INFORMATION);
						winOffloadOperationFunction.setAttribute("offloadLotno", null);
					}
				}
			}else{
				Messagebox.show(Labels.getLabel("common.message.query.nodata"), "Information", Messagebox.OK, Messagebox.INFORMATION);
			}
			offloadOperationList.clear();
			listboxOffloadInformation.setModel(new ListModelList<OffloadOperation>(offloadOperationList));
			listboxOffloadInformation.setItemRenderer(this);
			listboxOffloadInformation.renderAll();
		}
		
	}
	
	

	/**
	 * 
	 * onClick$btnExit:btnExit onClick event <br/>
	 *
	 * @author Allison
	 * @since JDK 1.6
	 */
	public void onClick$btnExit(){
		winOffloadOperationFunction.detach();
		Executions.sendRedirect("index.zul");
	}
	
	public void onSelect$listboxOffloadInformation(){
		if(listboxOffloadInformation.getItemCount()>0){
			for(int i=0; i<listboxOffloadInformation.getItemCount(); i++){
				Listitem li = listboxOffloadInformation.getItemAtIndex(i);
				Listcell ltcOffloadStatus = (Listcell)li.getFellow("cellOffloadStatus"+li.getIndex());
				if(!listboxOffloadInformation.getItems().get(i).isSelected()){
					if("Offloading".equals(ltcOffloadStatus.getLabel())){
						Datebox dtxOffloadDueDate = (Datebox)li.getFellow("dateboxOffloadDueDate"+li.getIndex());
						Textbox edtCancelReason = (Textbox)li.getFellow("textboxCancelReason"+li.getIndex());
						dtxOffloadDueDate.setDisabled(true);
						edtCancelReason.setDisabled(true);
						dtxOffloadDueDate.setConstraint("");
						edtCancelReason.setConstraint("");
					}else if("Offload Close".equals(ltcOffloadStatus.getLabel())){
						
					}else{
						Combobox cbxOffloadType = (Combobox)li.getFellow("comboboxOffloadType"+li.getIndex());
						Combobox cbxOffloadTo = (Combobox)li.getFellow("comboboxOffloadTo"+li.getIndex());
						Textbox edtOffloadPo = (Textbox)li.getFellow("textboxOffloadPo"+li.getIndex());
						Datebox dtxOffloadDueDate = (Datebox)li.getFellow("dateboxOffloadDueDate"+li.getIndex());
						
						dtxOffloadDueDate.setConstraint("");
						cbxOffloadType.setConstraint("");
						cbxOffloadTo.setConstraint("");
						edtOffloadPo.setConstraint("");
					}
				}else{
					if("Offloading".equals(ltcOffloadStatus.getLabel())){
						Datebox dtxOffloadDueDate = (Datebox)li.getFellow("dateboxOffloadDueDate"+li.getIndex());
						Textbox edtCancelReason = (Textbox)li.getFellow("textboxCancelReason"+li.getIndex());
						dtxOffloadDueDate.setDisabled(false);
						edtCancelReason.setDisabled(false);
					}
				}
			}			
		}
	}
	
	
	public void onClick$btnOffloadConfirm(){
		Set<Listitem> items =  listboxOffloadInformation.getSelectedItems();			
		List<Listitem> list = new ArrayList<Listitem>(items);
		String tmpOrderNumber="";
		for (int i =0; i< list.size(); i++) {
			offloadOperationList.get(i).setSelect(true);
			Listitem li = list.get(i);
			//若OFFLOAD_STATUS=空白時，執行OFFLOAD_CONFIRM則OFFLOAD_TYPE,OFFLOAD_TO,OFFLOAD_PO,OFFLOAD_DUE_DATE,WAFER_ID不能空白
			Listcell ltcOffloadStatus = (Listcell)li.getFellow("cellOffloadStatus"+li.getIndex());
			if("".equals(ltcOffloadStatus.getLabel()) || ltcOffloadStatus.getLabel() == null){
				Combobox cbxOffloadType = (Combobox)li.getFellow("comboboxOffloadType"+li.getIndex());
				Combobox cbxOffloadTo = (Combobox)li.getFellow("comboboxOffloadTo"+li.getIndex());
				Textbox edtOffloadPo = (Textbox)li.getFellow("textboxOffloadPo"+li.getIndex());
				Datebox dtbOffloadDueDate = (Datebox)li.getFellow("dateboxOffloadDueDate"+li.getIndex());
				Listcell ltcWaferId = (Listcell)li.getFellow("cellWaferId"+li.getIndex());
				Listcell ltcOrderNumber = (Listcell)li.getFellow("cellOrderNumber"+li.getIndex());

				cbxOffloadType.setConstraint("no empty");
				cbxOffloadTo.setConstraint("no empty");
				edtOffloadPo.setConstraint("no empty");
				dtbOffloadDueDate.setConstraint("no empty");
				cbxOffloadType.setFocus(true);
				cbxOffloadTo.setFocus(true);
				edtOffloadPo.setFocus(true);
				dtbOffloadDueDate.setFocus(true);
				log.debug(cbxOffloadType.getText());
				log.debug(cbxOffloadTo.getText());
				log.debug(edtOffloadPo.getText());
				log.debug(dtbOffloadDueDate.getText());
					
				log.debug(ltcWaferId.getLabel());
				if("".equals(ltcWaferId.getLabel()) || ltcWaferId.getLabel() == null){
					Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.noWaferId"), "Error", Messagebox.OK, Messagebox.ERROR);
					return;
				}
				
				if("".equals(tmpOrderNumber)){
					tmpOrderNumber = ltcOrderNumber.getLabel();
				}else{
					if(!tmpOrderNumber.contains(ltcOrderNumber.getLabel())){
						tmpOrderNumber = tmpOrderNumber + "," + ltcOrderNumber.getLabel();
					}
				}
			}else{
				//若OFFLOAD_STATUS不等於空白時(Offloading or Offload Close)，不能執行OFFLOAD_CONFIRM
				Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.confirmNotAllow"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
		}
		
		String msgOrderNumber = this.checkOrderNumbers(tmpOrderNumber);		
		if(!"".equals(msgOrderNumber)){
			String msg = "The following Customer LotNo hasn't be checked for same Order Number. System will not allow offload 'CONFIRM' operation, please confirm it!"+"\r\n\r\n"+"[Order Number / Customer LotNo]"+"\r\n"+msgOrderNumber;
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.sameOrderNumberNotSelect",new Object[] {msg}), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		
		String[] splitOrderNumber=tmpOrderNumber.split(",");
		boolean saveFlag=false;
		for(int i=0; i<listboxOffloadInformation.getItemCount(); i++){
			if(listboxOffloadInformation.getItems().get(i).isSelected()){
				saveOffloadOperationList.get(i).setUpdateUser(loginId);//saveOffloadOperationList的OFFLOAD_TYPE,OFFLOAD_TO,OFFLOAD_PO,OFFLOAD_DUEDATE在Render裡的Event中給值
				offloadOperationService.updateOrderHeader(splitOrderNumber, "CONFIRM");//UPDATE [ORDER_HEADER].B2B_DISABLE_FLAG=1
				offloadOperationService.updateOrderLineLotNo(saveOffloadOperationList.get(i), "CONFIRM");//UPDATE [ORDER_LINE_LOTNO].OFFLOAD_FLAG=1
				saveFlag = offloadOperationService.saveOffloadLotNo(saveOffloadOperationList.get(i), "CONFIRM");//INSERT or UPDATE [OFFLOAD_LOTNO]
			}
		}
		if(saveFlag){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.confirmOk"),"Information", Messagebox.OK, Messagebox.INFORMATION);
			onClick$btnSearch();
		}
	}
	
	
	public void onClick$btnModify(){
		Set<Listitem> items =  listboxOffloadInformation.getSelectedItems();			
		List<Listitem> list = new ArrayList<Listitem>(items);
		boolean saveFlag=false;
		for (int i =0; i< list.size(); i++) {
			//offloadOperationList.get(i).setSelect(true);
			Listitem li = list.get(i);
			Listcell ltcOffloadStatus = (Listcell)li.getFellow("cellOffloadStatus"+li.getIndex());
			if(!"Offloading".equals(ltcOffloadStatus.getLabel())){
				//若OFFLOAD_STATUS不等於20(Offloading)時，不能執行OFFLOAD_CONFIRM
				Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.modifyNotAllow"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
		}
		for (int i =0; i< list.size(); i++) {
			Listitem li = list.get(i);
			Listcell ltcOffloadStatus = (Listcell)li.getFellow("cellOffloadStatus"+li.getIndex());
			if("Offloading".equals(ltcOffloadStatus.getLabel())){
				offloadOperationList.get(i).setSelect(true);
				saveFlag = offloadOperationService.saveOffloadLotNo(saveOffloadOperationList.get(li.getIndex()), "MODIFY");
			}
		}
		if(saveFlag){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.modifyOk"),"Information", Messagebox.OK, Messagebox.INFORMATION);
			onClick$btnSearch();
		}
	}
	
	
	public void onClick$btnOffloadRate(){
		Map args = new HashMap();
		Window winOffloadRate = (Window)Executions.createComponents("/WEB-INF/modules/oe/OffloadRateSetup.zul", null, args);
		winOffloadRate.setParent(winOffloadOperationFunction);
		winOffloadRate.doModal();
	}
	
	
	public void onClick$btnCancel(){
		Set<Listitem> items =  listboxOffloadInformation.getSelectedItems();			
		List<Listitem> list = new ArrayList<Listitem>(items);
		String tmpOrderNumber="";
		for (int i =0; i< list.size(); i++) {
			offloadOperationList.get(i).setSelect(true);
			Listitem li = list.get(i);
			//若OFFLOAD_STATUS=Offloading時，執行OFFLOAD_CANCEL則CANCEL_REASON不能空白
			Listcell ltcOffloadStatus = (Listcell)li.getFellow("cellOffloadStatus"+li.getIndex());
			if("Offloading".equals(ltcOffloadStatus.getLabel())){
				Textbox edtCancelReason = (Textbox)li.getFellow("textboxCancelReason"+li.getIndex());
				Listcell ltcOrderNumber = (Listcell)li.getFellow("cellOrderNumber"+li.getIndex());
				
				edtCancelReason.setConstraint("no empty");
				log.debug(edtCancelReason.getText());
				
				if("".equals(tmpOrderNumber)){
					tmpOrderNumber = ltcOrderNumber.getLabel();
				}else{
					tmpOrderNumber = tmpOrderNumber + "," + ltcOrderNumber.getLabel();
				}
			}else{
				//若OFFLOAD_STATUS不等於20(Offloading)時，不能執行OFFLOAD_CANCEL
				Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.cancelNotAllow"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				return;
			}
		}
		
		String msgOrderNumber = this.checkOrderNumbers(tmpOrderNumber);
		if(!"".equals(msgOrderNumber)){
			String msg = "The following Customer LotNo hasn't be checked for same Order Number. System will not allow offload 'CANCEL' operation, please confirm it!"+"\r\n\r\n"+"[Order Number / Customer LotNo]"+"\r\n"+msgOrderNumber;
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.sameOrderNumberNotSelect",new Object[] {msg}), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		
		String[] splitOrderNumber=tmpOrderNumber.split(",");
		boolean saveFlag=false;
		for(int i=0; i<listboxOffloadInformation.getItemCount(); i++){
			if(listboxOffloadInformation.getItems().get(i).isSelected()){
				saveOffloadOperationList.get(i).setUpdateUser(loginId);//saveOffloadOperationList的OFFLOAD_TYPE,OFFLOAD_TO,OFFLOAD_PO,OFFLOAD_DUEDATE在Render裡的Event中給值
				offloadOperationService.updateOrderHeader(splitOrderNumber, "CANCEL");//UPDATE [ORDER_HEADER].B2B_DISABLE_FLAG=1
				offloadOperationService.updateOrderLineLotNo(saveOffloadOperationList.get(i), "CANCEL");//UPDATE [ORDER_LINE_LOTNO].OFFLOAD_FLAG=1
				saveFlag = offloadOperationService.saveOffloadLotNo(saveOffloadOperationList.get(i), "CANCEL");//INSERT or UPDATE [OFFLOAD_LOTNO]
			}
		}
		if(saveFlag){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.cancelOk"),"Information", Messagebox.OK, Messagebox.INFORMATION);
			onClick$btnSearch();
		}
	}
	
	
	public void onClick$btnShipping(){
		Set<Listitem> items =  listboxOffloadInformation.getSelectedItems();			
		List<Listitem> list = new ArrayList<Listitem>(items);
		if(list.size()>1){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.shippingNotAllowMultiple"), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}else if(list.size() == 1){
			for(int i=0; i<list.size(); i++){
				Map args = new HashMap();
				Listitem li = list.get(i);
				Listcell ltcOffloadStatus = (Listcell)li.getFellow("cellOffloadStatus"+li.getIndex());
				if("Offloading".equals(ltcOffloadStatus.getLabel()) || "Offload Close".equals(ltcOffloadStatus.getLabel())){//Offload Status="Offloading"，且只有勾選一個時，才能執行Offload Shipping
					Listcell ltcOrderNumber = (Listcell)li.getFellow("cellOrderNumber"+li.getIndex());
					Listcell ltcProduct = (Listcell)li.getFellow("cellProduct"+li.getIndex());
					Listcell ltcCustomerLotNo = (Listcell)li.getFellow("cellCustomerLotNo"+li.getIndex());
					Listcell ltcWaferData = (Listcell)li.getFellow("cellWaferId"+li.getIndex());
					Listcell ltcWaferQty = (Listcell)li.getFellow("cellWaferQty"+li.getIndex());
					Listcell ltcCurrency = (Listcell)li.getFellow("cellCurrency"+li.getIndex());

					args.put("orderNumber", ltcOrderNumber.getLabel());
					args.put("product", ltcProduct.getLabel());
					args.put("customerLotNo", ltcCustomerLotNo.getLabel());
					args.put("waferData", ltcWaferData.getLabel());
					args.put("waferQty", ltcWaferQty.getLabel());
					args.put("billTo", offloadOperationList.get(li.getIndex()).getBillTo());
					args.put("shipTo", offloadOperationList.get(li.getIndex()).getShipTo());
					args.put("poItem", offloadOperationList.get(li.getIndex()).getPoItem());
					args.put("poNumber", offloadOperationList.get(li.getIndex()).getPoNumber());
					args.put("product", offloadOperationList.get(li.getIndex()).getProduct());
					args.put("billToPo", offloadOperationList.get(li.getIndex()).getPoNo());
					args.put("currency", ltcCurrency.getLabel());
					args.put("offloadStatus", ltcOffloadStatus.getLabel());
					
					Window winOffloadShippingConfirm = (Window)Executions.createComponents("/WEB-INF/modules/oe/OffloadShippingConfirmFunction.zul", null, args);
					winOffloadShippingConfirm.setParent(winOffloadOperationFunction);
					winOffloadShippingConfirm.doModal();
				}else{
					//若OFFLOAD_STATUS不等於20(Offloading)時，不能執行OFFLOAD_SHIPPING
					Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.shippingNotAllow"), "Information", Messagebox.OK, Messagebox.INFORMATION);
					return;
				}
			}
		}
	}
	
	
	public void onClick$btnPrint() throws Exception{
		//先檢查[EXCHANGE_RATE_OFFLOAD]是否已經有設定，若無設定則跳出視窗並不允許Print
		ExchangeRateOffload exchangeRateOffload = offloadOperationService.getExchangeRateOffload();
		if(exchangeRateOffload == null){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.exchangeRateOffloadNull"), "Information", Messagebox.OK, Messagebox.INFORMATION);
			return;
		}
		
		
		Set<Listitem> items =  listboxOffloadInformation.getSelectedItems();
		List<Listitem> list = new ArrayList<Listitem>(items);
		List<OffloadOperation> excelOffloadOpertionData=new ArrayList<OffloadOperation>();
		if(list.size()>0){
			//檢查若勾選項目的OFFLOAD_STATUS不等於20(Offloading)時，不能執行OFFLOAD_PRINT
			for(int i=0; i<list.size(); i++){
				Listitem li = list.get(i);
				Listcell ltcOffloadStatus = (Listcell)li.getFellow("cellOffloadStatus"+li.getIndex());
				if(!"Offloading".equals(ltcOffloadStatus.getLabel())){
					Messagebox.show(Labels.getLabel("modules.oe.OffloadOperation.ctrl.message.printNotAllow"), "Information", Messagebox.OK, Messagebox.INFORMATION);
					excelOffloadOpertionData.clear();
					return;
				}
				Listcell ltcOrderNumber = (Listcell)li.getFellow("cellOrderNumber"+li.getIndex());
				Listcell ltcProduct = (Listcell)li.getFellow("cellProduct"+li.getIndex());
				Listcell ltcCustomerLotNo = (Listcell)li.getFellow("cellCustomerLotNo"+li.getIndex());
				Listcell ltcWaferQty = (Listcell)li.getFellow("cellWaferQty"+li.getIndex());
				Listcell ltcWaferData = (Listcell)li.getFellow("cellWaferId"+li.getIndex());
				Listcell ltcCurrency = (Listcell)li.getFellow("cellCurrency"+li.getIndex());
				Listcell ltcOrderPrice = (Listcell)li.getFellow("cellOrderPrice"+li.getIndex());
				Listcell ltcSelect = (Listcell)li.getFellow("cellSelect"+li.getIndex());
				Listcell ltcCancelUser = (Listcell)li.getFellow("cellCancelUser"+li.getIndex());
				Combobox cbxOffloadTo=null;
				Listcell ltcOffloadTo=null;
				if(Boolean.valueOf((Boolean) ltcSelect.getValue())){
					cbxOffloadTo = (Combobox)li.getFellow("comboboxOffloadTo"+li.getIndex());
				}else{
					if(!"".equals(ltcCancelUser.getLabel())){
						cbxOffloadTo = (Combobox)li.getFellow("comboboxOffloadTo"+li.getIndex());
					}else{
						ltcOffloadTo = (Listcell)li.getFellow("cellOffloadTo"+li.getIndex());
					}
				}
				Datebox dateboxOffloadDueDate = (Datebox)li.getFellow("dateboxOffloadDueDate"+li.getIndex());
				
				SimpleDateFormat Format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");	
				OffloadOperation offloadData = new OffloadOperation();
				offloadData.setOrderNumber(ltcOrderNumber.getLabel());
				offloadData.setProduct(ltcProduct.getLabel());
				offloadData.setCustomerLotNo(ltcCustomerLotNo.getLabel());
				offloadData.setWaferQty(ltcWaferQty.getLabel());
				offloadData.setWaferId(ltcWaferData.getLabel());
				offloadData.setCurrency(ltcCurrency.getLabel());
				if(Boolean.valueOf((Boolean) ltcSelect.getValue())){
					offloadData.setOffloadTo(cbxOffloadTo.getText());
				}else{
					if(!"".equals(ltcCancelUser.getLabel())){
						offloadData.setOffloadTo(cbxOffloadTo.getText());
					}else{
						offloadData.setOffloadTo(ltcOffloadTo.getLabel());
					}
				}
				offloadData.setOffloadDueDate(Format.parse(dateboxOffloadDueDate.getText()));
				if("JPY".equals(ltcCurrency.getLabel())){//當為JPY時，取整數並四捨五入
					offloadData.setOrderPrice(jpdFormat.format(BigDecimal.valueOf(Double.valueOf(ltcOrderPrice.getLabel().replace(",", ""))).setScale(0)));
				}else if("USD".equals(ltcCurrency.getLabel())){//當為USD時，取小數點後2位並四捨五入
					offloadData.setOrderPrice(usdFormat.format(BigDecimal.valueOf(Double.valueOf(ltcOrderPrice.getLabel().replace(",", ""))).setScale(2)));
				}else{//當為其它幣別時，取整數並四捨五入
					offloadData.setOrderPrice(usdFormat.format(BigDecimal.valueOf(Double.valueOf(ltcOrderPrice.getLabel().replace(",", ""))).setScale(0)));
				}
				
				excelOffloadOpertionData.add(offloadData);
			}
		}
		if(list.size()>0){
			Listitem li = list.get(0);
			Listcell ltcCustomer = (Listcell)li.getFellow("cellCustomer"+li.getIndex());
			
			Date nowTime = new Date();
			File templateFile = new File(getPage().getDesktop().getWebApp().getRealPath("/reports/OffloadForm.xls"));//讀取Temp檔
			excelPath=tmpFileFolder+"OCFOffloadForm"+DateFormatUtil.getDateTimeFormateryyMMddHHmm().format(nowTime)+".xls";//命名檔案名稱
			Map beans = new HashMap();
			String printDate = DateFormatUtil.getSimpleDateFormat().format(nowTime);
			
			beans.put("printDate", printDate);//要塞入Excel裡的"請購日期"
			beans.put("customer", ltcCustomer.getLabel());//要塞入Excel裡的"客戶"
			
			XLSTransformer transformer = new XLSTransformer();            
			transformer.transformXLS(templateFile.getPath(), beans, excelPath);
			
			//讀取出已新增的套板檔案
			HSSFWorkbook editBook = new HSSFWorkbook(new FileInputStream(excelPath));		
			if(list.size()>10){//如果勾選的筆數超過10筆，則要再複製一個Sheet出來，編號從11開始編
				int cloneCount = list.size()/10;
				for(int i=0; i<cloneCount; i++){
					editBook.cloneSheet(0);
				}
			}
			
			int sheetCount=editBook.getNumberOfSheets();
			log.debug("共有"+sheetCount+"個Sheet");
			
			int count=0; 
			for(int i=0; i<sheetCount; i++){
				HSSFSheet sheet = editBook.getSheetAt(i);
				for(int j=10*i; j<10*i+10; j++){
					if(count<excelOffloadOpertionData.size()){
						sheet.getRow(3+j%10).getCell(0).setCellValue(j+1);//塞序號
						sheet.getRow(3+j%10).getCell(1).setCellValue(excelOffloadOpertionData.get(j).getProduct());//塞ProductName
						sheet.getRow(3+j%10).getCell(3).setCellValue(excelOffloadOpertionData.get(j).getCustomerLotNo());//塞Wafer No(表單是Wafer No，但實際是塞Customer LotNo)
						sheet.getRow(3+j%10).getCell(5).setCellValue(excelOffloadOpertionData.get(j).getWaferQty());//塞PCS
						sheet.getRow(3+j%10).getCell(6).setCellValue(excelOffloadOpertionData.get(j).getWaferId());//塞Wafer Data
						CustomerTable customerTable=customerInformationService.getCustomerTableByCustomerShortName(excelOffloadOpertionData.get(j).getOffloadTo(),"F");
						sheet.getRow(3+j%10).getCell(9).setCellValue(customerTable.getOffloadCurrency());//塞[CUSTOMER_TABLE].OFFLOAD_CURRENCY
						String offloadPrice = getOffloadPrice(customerTable.getOffloadCurrency(),excelOffloadOpertionData.get(j).getCurrency(), excelOffloadOpertionData.get(j).getOrderPrice(), exchangeRateOffload);
						sheet.getRow(3+j%10).getCell(10).setCellValue(String.valueOf(offloadPrice));//塞Offload Price
						sheet.getRow(3+j%10).getCell(11).setCellValue(excelOffloadOpertionData.get(j).getCurrency());//塞[ORDER_LINE].Currency
						sheet.getRow(3+j%10).getCell(12).setCellValue(String.valueOf(excelOffloadOpertionData.get(j).getOrderPrice()));//塞Original Price
						sheet.getRow(3+j%10).getCell(13).setCellValue(DateFormatUtil.getSimpleDateFormat().format(excelOffloadOpertionData.get(j).getOffloadDueDate()));//塞Delivery Date([OFFLOAD_LOTNO].OFFLOAD_DUEDATE)
						OrderHeader orderHeader = offloadOperationService.getOrderHeader(excelOffloadOpertionData.get(j).getOrderNumber());
						sheet.getRow(3+j%10).getCell(14).setCellValue(orderHeader.getCustomerPo());//塞Remark([ORDER_HEADER].CUSTOMER_PO)
					}else{
						break;
					}
					count = count + 1;
				}
			}
			
			FileOutputStream savefile = new FileOutputStream(excelPath);
			editBook.write(savefile);
			savefile.close();
			
			File f = new File(excelPath);
			Filedownload.save(f, "application/vnd.ms-excel");
			log.debug("Excel export OK!");
			f.deleteOnExit();
		}		
	}
	
	
	public String getOffloadPrice(String inOffloadCurrency, String inOeCurrency, String inOriginalPrice, ExchangeRateOffload inExchangeRateOffload){
		String offloadPrice = "";
		List<UiFieldSet> offloadPercentage=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.OffloadOperationViewCtrl", "OFFLOAD_PERCENTAGE");
		BigDecimal tmpOriginalPrice = BigDecimal.valueOf(Double.valueOf(inOriginalPrice.replace(",", "")));
		//當OFFLOAD_CURRENCY為USD時，格式取小數點後2位並四捨五入；當為JPY時，格式取整數並四捨五入
		if("USD".equals(inOeCurrency)){
			if("USD".equals(inOffloadCurrency)){
				//[OE]USD->[OFFLOAD_CURRENCY]USD公式：
				//[ORDER_LINE. UNIT_PRICE] * [ORDER_LINE_LOTNO. WAFER_QTY] * 0.95(設在[UI_FIELD_PARAM])
				offloadPrice = usdFormat.format(tmpOriginalPrice.multiply(BigDecimal.valueOf(Double.valueOf(offloadPercentage.get(0).getUiFieldParams().get(0).getParaValue()))).setScale(2, RoundingMode.HALF_UP));
			}else if("JPY".equals(inOffloadCurrency)){
				//[OE]USD->[OFFLOAD_CURRENCY]JPY公式：
				//[ORDER_LINE. UNIT_PRICE] * [ORDER_LINE_LOTNO. WAFER_QTY] * [EXCHANGE_RATE_OFFLOAD. USD_RATE] * 0.95(設在[UI_FIELD_PARAM])
				offloadPrice = jpdFormat.format(tmpOriginalPrice.multiply(inExchangeRateOffload.getUsdRate()).multiply(BigDecimal.valueOf(Double.valueOf(offloadPercentage.get(0).getUiFieldParams().get(0).getParaValue()))).setScale(0, RoundingMode.HALF_UP));
			}
		}else if("JPY".equals(inOeCurrency)){
			if("USD".equals(inOffloadCurrency)){
				//[OE]JPY->[OFFLOAD_CURRENCY]USD公式：
				//([ORDER_LINE. UNIT_PRICE] * [ORDER_LINE_LOTNO. WAFER_QTY] / [EXCHANGE_RATE_OFFLOAD. USD_RATE]) * 0.95(設在[UI_FIELD_PARAM])
				offloadPrice = usdFormat.format(tmpOriginalPrice.divide(inExchangeRateOffload.getUsdRate()).multiply(BigDecimal.valueOf(Double.valueOf(offloadPercentage.get(0).getUiFieldParams().get(0).getParaValue()))).setScale(2, RoundingMode.HALF_UP));
			}else if("JPY".equals(inOffloadCurrency)){
				//[OE]JPY->[OFFLOAD_CURRENCY]JPY公式：
				//[ORDER_LINE. UNIT_PRICE] X [ORDER_LINE_LOTNO. WAFER_QTY] * 0.95(設在[UI_FIELD_PARAM])
				offloadPrice = jpdFormat.format(tmpOriginalPrice.multiply(BigDecimal.valueOf(Double.valueOf(offloadPercentage.get(0).getUiFieldParams().get(0).getParaValue()))).setScale(0, RoundingMode.HALF_UP));
			}
		}else if("NTD".equals(inOeCurrency)){
			if("USD".equals(inOffloadCurrency)){
				//[OE]NTD->[OFFLOAD_CURRENCY]USD公式：
				//(([ORDER_LINE. UNIT_PRICE] X [ORDER_LINE_LOTNO. WAFER_QTY] * [EXCHANGE_RATE_OFFLOAD. NTD_RATE])/[EXCHANGE_RATE_OFFLOAD. USD_RATE]) * 0.95(設在[UI_FIELD_PARAM])
				offloadPrice = usdFormat.format(tmpOriginalPrice.multiply(inExchangeRateOffload.getNtdRate()).divide(inExchangeRateOffload.getUsdRate()).multiply(BigDecimal.valueOf(Double.valueOf(offloadPercentage.get(0).getUiFieldParams().get(0).getParaValue()))).setScale(2, RoundingMode.HALF_UP));
			}else if("JPY".equals(inOffloadCurrency)){
				offloadPrice = jpdFormat.format(tmpOriginalPrice.multiply(inExchangeRateOffload.getNtdRate()).multiply(BigDecimal.valueOf(Double.valueOf(offloadPercentage.get(0).getUiFieldParams().get(0).getParaValue()))).setScale(0, RoundingMode.HALF_UP));
			}
		}
		
		return offloadPrice;
	}
	
	public String getMeaningByParaValue(String inValue,List<UiFieldParam> inUiFieldParams){
		if (inUiFieldParams.size()>0){
			String r="";
			for (int i=0;i<inUiFieldParams.size();i++){
				if (inUiFieldParams.get(i).getParaValue().equals(inValue)){
					r=inUiFieldParams.get(i).getMeaning();
				}
			}
			return r;
		}
		else{
			return "";
		}
	}
	
	public static String getParaValueByMeaning(String inMeaning,List<UiFieldParam> inUiFieldParams){
		if (inUiFieldParams.size()>0){
			String r="";
			for (int i=0;i<inUiFieldParams.size();i++){
				if (inUiFieldParams.get(i).getMeaning().equals(inMeaning)){
					r=inUiFieldParams.get(i).getParaValue();
				}
			}
			return r;
		}
		else {
			return "";
		}
	}
	
	public String checkOrderNumbers(String inOrderNumbers){
		//檢查已經被勾選的ORDER_NUMBER是否還有其它相同的ORDER_NUMBER未被勾選起來
				String msgOrderNumber="";
				String[] splitOrderNumber=inOrderNumbers.split(",");
				for(int j=0; j<splitOrderNumber.length; j++){
					boolean checkFlag=false;
					for (int i =0; i< listboxOffloadInformation.getItemCount(); i++) {
						Listcell ltcOrderNumber = (Listcell)component.getFellow("cellOrderNumber"+String.valueOf(i));
						Listcell ltcCustomerLotNo = (Listcell)component.getFellow("cellCustomerLotNo"+String.valueOf(i));
						if(ltcOrderNumber.getLabel().equals(splitOrderNumber[j]) && !listboxOffloadInformation.getItems().get(i).isSelected()){
							checkFlag=true;
							if("".equals(msgOrderNumber)){
								msgOrderNumber = ltcOrderNumber.getLabel() +" / " + ltcCustomerLotNo.getLabel();
							}else{
								if(msgOrderNumber.contains(splitOrderNumber[j])){
									msgOrderNumber = msgOrderNumber + " , " + ltcCustomerLotNo.getLabel();
								}else{
									msgOrderNumber = msgOrderNumber + ltcOrderNumber.getLabel() +" / " + ltcCustomerLotNo.getLabel();
								}
							}
						}
					}
					if(!"".equals(msgOrderNumber)){
						if(checkFlag){
							msgOrderNumber = msgOrderNumber + "\r\n";
						}
					}
				}
				return msgOrderNumber;
	}
	
	

	/**
	 * 初始化多語系LABEL
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		winOffloadOperationFunction.setTitle(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.winOffloadOperationFunction"));
		lbCustomer.setValue(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.lbCustomer"));
		lbProduct.setValue(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.lbProduct"));
		lbOrderStartDate.setValue(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.lbOrderStartDate"));
		lbOrderEndDate.setValue(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.lbOrderEndDate"));
	
		headerOffloadStatus.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerOffloadStatus"));
		headerCustomer.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerCustomer"));
		headerOrderNumber.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerOrderNumber"));
		headerPoNo.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerPoNo"));
		headerProduct.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerProduct"));
		headerCustomerLotNo.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerCustomerLotNo"));
		headerWaferQty.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerWaferQty"));
		headerOrderPrice.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerOrderPrice"));
		headerCurrency.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerCurrency"));
		headerOffloadType.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerOffloadType"));
		headerOffloadTo.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerOffloadTo"));
		headerOffloadPo.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerOffloadPo"));
		headerOffloadDueDate.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerOffloadDueDate"));
		headerWaferId.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerWaferId"));
		headerConfirmDate.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerConfirmDate"));
		headerUpdateDate.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerUpdateDate"));
		headerUpdateUser.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerUpdateUser"));
		headerCancelDate.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerCancelDate"));
		headerCancelUser.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerCancelUser"));
		headerCancelReason.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.headerCancelReason"));
		
		btnOffloadConfirm.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.btnOffloadConfirm"));
		btnOffloadRate.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.btnOffloadRate"));
		btnSearch.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.btnSearch"));
		btnModify.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.btnModify"));
		btnPrint.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.btnPrint"));
		btnShipping.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.btnShipping"));
		btnCancel.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.btnCancel"));
		btnExit.setLabel(Labels.getLabel("modules.oe.OffloadOperation.ctrl.label.btnExit"));

	}

	/**
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
		
	}

	

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(Listitem inItem, Object inData, final int inIndex) throws Exception {
		final OffloadOperation offloadOpertion = (OffloadOperation) inData;
		
		if("20".equals(offloadOpertion.getOffloadStatus())){
			inItem.setStyle("background:#CDFFCE");
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
		
		final Combobox comboboxOffloadType = new Combobox();
		final Combobox comboboxOffloadTo = new Combobox();
		final Textbox textboxOffloadPo = new Textbox();
		final Datebox dateboxOffloadDueDate = new Datebox();
		final Textbox textboxCancelReason = new Textbox();
		comboboxOffloadType.setId("comboboxOffloadType"+String.valueOf(inIndex));
		comboboxOffloadTo.setId("comboboxOffloadTo"+String.valueOf(inIndex));
		textboxOffloadPo.setId("textboxOffloadPo"+String.valueOf(inIndex));
		dateboxOffloadDueDate.setId("dateboxOffloadDueDate"+String.valueOf(inIndex));
		textboxCancelReason.setId("textboxCancelReason"+String.valueOf(inIndex));
		
		cellSelect.setId("cellSelect"+String.valueOf(inIndex));
		cellSelect.setValue(offloadOpertion.isNewFlag());//cellSelect放isNewFlag，用來記錄此筆資料是否是新增or從資料庫裡有的資料
		
		//OFFLOAD_STATUS
		String tmpOffloadStatus="";
		List<UiFieldSet> offloadStatus=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.render.OffloadOperationRender", "OFFLOAD_STATUS");
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
		cellOffloadStatus.setId("cellOffloadStatus"+String.valueOf(inIndex));
		cellOffloadStatus.setLabel(tmpOffloadStatus);
		
		//CUSTOMER
		cellCustomer.setId("cellCustomer"+String.valueOf(inIndex));
		cellCustomer.setLabel(offloadOpertion.getCustomer());
		
		//ORDER_NUMBER
		cellOrderNumber.setId("cellOrderNumber"+String.valueOf(inIndex));
		cellOrderNumber.setLabel(offloadOpertion.getOrderNumber());
		
		//PO_NO
		cellPoNo.setLabel(offloadOpertion.getPoNo());
		
		//PRODUCT
		cellProduct.setId("cellProduct"+String.valueOf(inIndex));
		cellProduct.setLabel(offloadOpertion.getProduct());
		
		//CUSTOMER_LOTNO
		cellCustomerLotNo.setId("cellCustomerLotNo"+String.valueOf(inIndex));
		cellCustomerLotNo.setLabel(offloadOpertion.getCustomerLotNo());
		
		//WAFER_QTY
		cellWaferQty.setId("cellWaferQty"+String.valueOf(inIndex));
		cellWaferQty.setLabel(offloadOpertion.getWaferQty());
		
		//ORDER_PRICE
		cellOrderPrice.setId("cellOrderPrice"+String.valueOf(inIndex));
		if("J".equals(offloadOpertion.getCurrency())){//若Currency是日幣，則顯示格式為整數+四捨五入
			cellOrderPrice.setLabel(jpdFormat.format(Double.valueOf(offloadOpertion.getOrderPrice())));
		}else if("U".equals(offloadOpertion.getCurrency())){//若Currency是美金，則顯示格式為小數點後2位+四捨五入
			cellOrderPrice.setLabel(usdFormat.format(Double.valueOf(offloadOpertion.getOrderPrice())));
		}else{//其它Currency則用整數+四捨五入
			cellOrderPrice.setLabel(jpdFormat.format(Double.valueOf(offloadOpertion.getOrderPrice())));
		}
		
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
		cellCurrency.setId("cellCurrency"+String.valueOf(inIndex));
		cellCurrency.setLabel(tmpCurrency);
		
		//OFFLOAD_TYPE
		cellOffloadType.setId("cellOffloadType"+String.valueOf(inIndex));
		final List<UiFieldSet> offloadType=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.render.OffloadOperationRender", "OFFLOAD_TYPE");
		if(offloadOpertion.isNewFlag()){
			if (offloadType.size()>0){
				for(int i=0; i<offloadType.size(); i++){
					for(int j=0; j<offloadType.get(i).getUiFieldParams().size(); j++){
						comboboxOffloadType.appendItem(offloadType.get(i).getUiFieldParams().get(j).getParaValue());
						comboboxOffloadType.setValue(offloadType.get(i).getUiFieldParams().get(j).getParaValue());
					}
				}
			}

			if(offloadOpertion.getOffloadType() != null && !"".equals(offloadOpertion.getOffloadType())){
				for(int i=0; i<comboboxOffloadType.getItemCount(); i++){
					if(getParaValueByMeaning(offloadOpertion.getOffloadType(), offloadType.get(0).getUiFieldParams()).equals(comboboxOffloadType.getItemAtIndex(i).getValue())){
						comboboxOffloadType.setSelectedIndex(i);
					}
				}
			}else{
				comboboxOffloadType.setText("");
			}
			comboboxOffloadType.setParent(cellOffloadType);
		}else{
			if(!offloadOpertion.isCancelFlag()){
				cellOffloadType.setLabel(getParaValueByMeaning(offloadOpertion.getOffloadType(), offloadType.get(0).getUiFieldParams()));
			}else{
				if (offloadType.size()>0){
					for(int i=0; i<offloadType.size(); i++){
						for(int j=0; j<offloadType.get(i).getUiFieldParams().size(); j++){
							comboboxOffloadType.appendItem(offloadType.get(i).getUiFieldParams().get(j).getParaValue());
							comboboxOffloadType.setValue(offloadType.get(i).getUiFieldParams().get(j).getParaValue());
						}
					}
				}
				comboboxOffloadType.setText("");
				comboboxOffloadType.setParent(cellOffloadType);
			}
		}
		comboboxOffloadType.addEventListener("onSelect", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				saveOffloadOperationList.get(inIndex).setOffloadType(getMeaningByParaValue(comboboxOffloadType.getText(), offloadType.get(0).getUiFieldParams()));
			}
		});
		
		//OFFLOAD_TO
		cellOffloadTo.setId("cellOffloadTo"+String.valueOf(inIndex));
		//OCF-PR-150205_[OFFLOAD_LOTNO].OFFLOAD_TO改成帶[CUSTOMER_TABLE].BUS_PURPUSE='F'的CUSTOMER_SHORT_NAME，存入[OFFLOAD_LOTNO].OFFLOAD_TO存入CUSTOMER_ID_Allison
		final List<CustomerTable> offloadTo=customerInformationService.getCustomerTableByBusPurpose("F");
		if(offloadOpertion.isNewFlag()){
			if (offloadTo.size()>0){
				for(int i=0; i<offloadTo.size(); i++){
					comboboxOffloadTo.appendItem(offloadTo.get(i).getCustomerShortName());
					comboboxOffloadTo.getItemAtIndex(i).setValue(offloadTo.get(i).getCustomerId());
				}
			}

			if(offloadOpertion.getOffloadTo() != null && !"".equals(offloadOpertion.getOffloadTo())){
				for(int i=0; i<comboboxOffloadTo.getItemCount(); i++){
					if(offloadOpertion.getOffloadTo().equals(comboboxOffloadTo.getItemAtIndex(i).getValue())){
						comboboxOffloadTo.setSelectedIndex(i);
					}
				}
			}else{
				comboboxOffloadTo.setText("");
			}
			comboboxOffloadTo.setParent(cellOffloadTo);
		}else{
			if(!offloadOpertion.isCancelFlag()){
				log.debug(offloadOpertion.getOffloadTo());
				CustomerTable customerTable=customerInformationService.getCustomerTableByCustomerIdAndBusPurpose(offloadOpertion.getOffloadTo(),"F");
				cellOffloadTo.setLabel(customerTable.getCustomerShortName());
			}else{
				if (offloadTo.size()>0){
					for(int i=0; i<offloadTo.size(); i++){
						comboboxOffloadTo.appendItem(offloadTo.get(i).getCustomerShortName());
						comboboxOffloadTo.getItemAtIndex(i).setValue(offloadTo.get(i).getCustomerId());
					}
				}
				comboboxOffloadTo.setText("");
				comboboxOffloadTo.setParent(cellOffloadTo);
			}
		}
		comboboxOffloadTo.addEventListener("onSelect", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				saveOffloadOperationList.get(inIndex).setOffloadTo(comboboxOffloadTo.getItemAtIndex(comboboxOffloadTo.getSelectedIndex()).getValue().toString());
			}
		});
		
		//OFFLOAD_PO
		if(offloadOpertion.isNewFlag()){
			if(offloadOpertion.getOffloadPo() != null && !"".equals(offloadOpertion.getOffloadPo())){
				textboxOffloadPo.setText(offloadOpertion.getOffloadPo());
			}else{
				textboxOffloadPo.setText("");
			}
			textboxOffloadPo.setParent(cellOffloadPo);
		}else{
			if(!offloadOpertion.isCancelFlag()){
				cellOffloadPo.setLabel(offloadOpertion.getOffloadPo());
			}else{
				textboxOffloadPo.setText("");
				textboxOffloadPo.setParent(cellOffloadPo);
			}
		}
		textboxOffloadPo.addEventListener("onChange", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				saveOffloadOperationList.get(inIndex).setOffloadPo(textboxOffloadPo.getText());
			}
		});
		
		//OFFLOAD_DUEDATE
		if(!offloadOpertion.isCancelFlag()){
			dateboxOffloadDueDate.setValue(offloadOpertion.getOffloadDueDate());
		}else{
			dateboxOffloadDueDate.setValue(null);
			dateboxOffloadDueDate.setText("");
		}
		dateboxOffloadDueDate.setId("dateboxOffloadDueDate"+String.valueOf(inIndex));
		dateboxOffloadDueDate.setFormat(Labels.getLabel("format.datetimeformat"));
		if(!offloadOpertion.isNewFlag()){
			if(!offloadOpertion.isCancelFlag()){
				dateboxOffloadDueDate.setDisabled(true);
			}else{
				dateboxOffloadDueDate.setDisabled(false);
			}
		}else{
			dateboxOffloadDueDate.setDisabled(false);
		}
		dateboxOffloadDueDate.setParent(cellOffloadDueDate);
		dateboxOffloadDueDate.addEventListener("onChange", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				SimpleDateFormat Format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
				if(!"".equals(dateboxOffloadDueDate.getText())){
					saveOffloadOperationList.get(inIndex).setOffloadDueDate(Format.parse(dateboxOffloadDueDate.getText()));
				}else{
					saveOffloadOperationList.get(inIndex).setOffloadDueDate(null);
				}
			}
		});
		
		//WAFER_ID
		cellWaferId.setId("cellWaferId"+String.valueOf(inIndex));
		cellWaferId.setLabel(offloadOpertion.getWaferId());
		
		//CONFIRM_DATE
		if(offloadOpertion.getConfirmDate() != null){
			cellConfirmDate.setLabel(DateFormatUtil.getDateTimeFormat().format(offloadOpertion.getConfirmDate()));
		}else{
			cellConfirmDate.setLabel("");
		}
		
		//UPDATE_DATE
		if(offloadOpertion.getUpdateDate() != null){
			cellUpdateDate.setLabel(DateFormatUtil.getDateTimeFormat().format(offloadOpertion.getUpdateDate()));
		}else{
			cellUpdateDate.setLabel("");
		}
		
		//UPDATE_USER
		EmplInfo updateUser = userService.getEmplInfoByEmplId(offloadOpertion.getUpdateUser());
		if(updateUser != null){
			cellUpdateUser.setLabel(updateUser.getEmplFamilyname()+updateUser.getEmplFirstname());
		}else{
			cellUpdateUser.setLabel("");
		}
		
		//CANCEL_DATE
		if(offloadOpertion.getCancelDate() != null){
			cellCancelDate.setLabel(DateFormatUtil.getDateTimeFormat().format(offloadOpertion.getCancelDate()));
		}else{
			cellCancelDate.setLabel("");
		}
		
		//CANCEL_USER
		cellCancelUser.setId("cellCancelUser"+String.valueOf(inIndex));
		EmplInfo cancel_User = userService.getEmplInfoByEmplId(offloadOpertion.getCancelUser());
		if(cancel_User != null){
			cellCancelUser.setLabel(cancel_User.getEmplFamilyname()+cancel_User.getEmplFirstname());
		}else{
			cellCancelUser.setLabel("");
		}
		
		//CANCEL_REASON
		textboxCancelReason.setValue(offloadOpertion.getCancelReason());
		textboxCancelReason.setId("textboxCancelReason"+String.valueOf(inIndex));
		if(!offloadOpertion.isNewFlag()){
			textboxCancelReason.setDisabled(true);
		}else{
			textboxCancelReason.setDisabled(false);
		}
		textboxCancelReason.setParent(cellCancelReason);
		textboxCancelReason.addEventListener("onChange", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				saveOffloadOperationList.get(inIndex).setCancelReason(textboxCancelReason.getText());
			}
		});
		
		
		
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
