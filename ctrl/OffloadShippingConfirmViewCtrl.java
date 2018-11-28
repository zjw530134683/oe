/*
 * Project Name:iVision
 * File Name:OffloadShippingConfirmViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2014/12/05 上午10:16:16
 * 
 * 說明:
 * 
 * 修改歷史:
 * 2014-12-02 Allison  Initialize
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
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

import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.model.EmplInfo;
import com.tce.ivision.model.ExchangeRate;
import com.tce.ivision.model.ExchangeRateOffload;
import com.tce.ivision.model.LotResult;
import com.tce.ivision.model.OffloadLotno;
import com.tce.ivision.model.OffloadShipping;
import com.tce.ivision.model.UiFieldParam;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.fin.service.FinService;
import com.tce.ivision.modules.oe.model.OffloadOperation;
import com.tce.ivision.modules.oe.model.OffloadOperationParameter;
import com.tce.ivision.modules.oe.model.OffloadShippingConfirm;
import com.tce.ivision.modules.oe.model.OffloadWaferConfirm;
import com.tce.ivision.modules.oe.render.OffloadOperationRender;
import com.tce.ivision.modules.oe.service.OffloadOperationService;
import com.tce.ivision.modules.shp.service.ShippingService;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.DateUtil;
import com.tce.ivision.units.common.ZkComboboxControl;

/**
 * ClassName: OffloadShippingConfirmViewCtrl <br/>
 * date: Date:2014/12/05 上午10:16:16 <br/>
 *
 * @author Allison
 * @version 
 * @since JDK 1.6
 */
public class OffloadShippingConfirmViewCtrl extends BaseViewCtrl implements ListitemRenderer {

	/**
	 * serialVersionUID:
	 * @since JDK 1.6
	 */
	private static final long serialVersionUID = 1L;
	private Window winOffloadShippingConfirmFunction;
	private Label lblOrderNumber;
	private Label lblOrderNumberField;
	private Label lblOffloadQty;
	private Label lblOffloadQtyField;
	private Button btnSave;
	private Label lblProduct;
	private Label lblProductField;
	private Label lblConfirmQty;
	private Label lblConfirmQtyField;
	private Button btnConfirm;
	private Label lblCustomerLotNo;
	private Label lblCustomerLotNoField;
	private Label lblRemainingQty;
	private Label lblRemainingQtyField;
	private Button btnExit;
	private Listbox listboxOffloadShippingInfo;
	private Listheader headerSelect;
	private Listheader headerPackingListType;
	private Listheader headerPackingListNumber;
	private Listheader headeriNaviLotNo;
	private Listheader headerShippingQty;
	private Listheader headerRmaQty;
	private Listheader headerScrapQty;
	private Listheader headerRemark;
	private Listheader headerShippingDate;
	private Listheader headerConfirmDate;
	private Listheader headerConfirmUser;
	private Button btnNewAdd;
	private Button btnOffloadWaferConfirm;
	private Button btnSearch;//此Search Button是隱藏的，為了讓Child Form回此refresh畫面時可呼叫用
	
	
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
	 * ShippingService
	 */
	private ShippingService shippingService = (ShippingService) SpringUtil.getBean("shippingService");
	/**
	 * FinService
	 */
	private FinService finService = (FinService) SpringUtil.getBean("finService");
	/**
	 * customerTable List 用於接取query出來的customerTable Bean
	 */
	private List<CustomerTable> customerTableList;
	/**
	 * 搜尋參數的bean 
	 */
	private OffloadOperationParameter queryParameter;
	private List<OffloadShippingConfirm> offloadShippingLists;
	private List<OffloadWaferConfirm> offloadWaferConfirmLists = new ArrayList<OffloadWaferConfirm>();
	private OffloadLotno offloadLotno;
	private String waferData="";
	private int offloadLotnoIdx;
	private int waferQty=0;
	private String billTo="";
	private String shipTo="";
	private String poItem="";
	private String poNumber="";
	private String product="";
	private String billToPo="";
	private String currency=""; 
	private String mode="";
	private boolean iNaviCheck=false;
	private String offloadStatus="";
	
	
	/**
	 * 頁面載入後開始執行初始化的function
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		lblOrderNumberField.setValue((String) execution.getArg().get("orderNumber"));
		lblProductField.setValue((String) execution.getArg().get("product"));
		lblCustomerLotNoField.setValue((String) execution.getArg().get("customerLotNo"));
		waferData = (String) execution.getArg().get("waferData");
		lblOffloadQtyField.setValue((String) execution.getArg().get("waferQty"));
		billTo = (String) execution.getArg().get("billTo");
		shipTo = (String) execution.getArg().get("shipTo");
		poItem = (String) execution.getArg().get("poItem");
		poNumber = (String) execution.getArg().get("poNumber");
		product = (String) execution.getArg().get("product");
		billToPo = (String) execution.getArg().get("billToPo");
		currency = (String) execution.getArg().get("currency");
		offloadStatus = (String) execution.getArg().get("offloadStatus");
		if("Offload Close".equals(offloadStatus)){
			btnOffloadWaferConfirm.setDisabled(false);
		}else{
			btnOffloadWaferConfirm.setDisabled(true);
		}
		btnConfirm.setDisabled(true);
		
		winOffloadShippingConfirmFunction.setAttribute("offloadWaferConfirm", offloadWaferConfirmLists);//用來跟Offload Wafer Confirm畫面傳遞參數的Attribute
		winOffloadShippingConfirmFunction.setAttribute("shippingConfirmIdx", "");//記錄listboxOffloadShippingInfo所選的idx，會用來與Offload Wafer Confirm畫面傳遞用
		
		offloadLotno = offloadOperationService.getOffloadLotno((String) execution.getArg().get("orderNumber"), (String) execution.getArg().get("customerLotNo"));
		offloadLotnoIdx = offloadLotno.getOffloadLotNoIdx();
		onClick$btnSearch();
	}
	

	public void onClick$btnSearch(){		
		int shipQty=0;
		int rmaQty=0;
		int scrapQty=0;
		String shipWaferData="";
		String rmaWaferData="";
		String scrapWaferData="";
		if(offloadShippingLists != null){
			if(offloadShippingLists.size() > 0){
				for(int i=0; i<offloadShippingLists.size(); i++){
					if(offloadShippingLists.get(i).isSelect()){
						List<OffloadWaferConfirm> offloadWaferConfirmLists = (List<OffloadWaferConfirm>) winOffloadShippingConfirmFunction.getAttribute("offloadWaferConfirm");
						if(offloadWaferConfirmLists.size()>0){
							for(int j=0; j<offloadWaferConfirmLists.size(); j++){//從Offload Wafer Confirm畫面做完Confirm後，回傳回來的資料
								if(offloadWaferConfirmLists.get(j).getWaferConfirm() != null && offloadWaferConfirmLists.get(j).getConfirmDate() != null){//如果Wafer Confirm & Confirm Date都是null的，則代表這筆沒有做過Offload Wafer Confirm
									if(offloadWaferConfirmLists.get(j).getShippingConfirmIdx() != null){
										if(offloadWaferConfirmLists.get(j).getShippingConfirmIdx().equals(String.valueOf(i))){
											String[] tmpWaferNo = offloadWaferConfirmLists.get(j).getWaferNo().split("-");
											if("Shipping".equals(offloadWaferConfirmLists.get(j).getWaferConfirm())){
												shipQty = shipQty + 1;
												shipWaferData = shipWaferData + String.valueOf(Integer.valueOf(tmpWaferNo[1])) + ";";
											}else if("RMA".equals(offloadWaferConfirmLists.get(j).getWaferConfirm())){
												rmaQty = rmaQty + 1;
												rmaWaferData = rmaWaferData + String.valueOf(Integer.valueOf(tmpWaferNo[1])) + ";";
											}else if("Scrap".equals(offloadWaferConfirmLists.get(j).getWaferConfirm())){
												scrapQty = scrapQty + 1;
												scrapWaferData = scrapWaferData + String.valueOf(Integer.valueOf(tmpWaferNo[1])) + ";";
											}
										}
									}
								}
							}
							if(!"".equals(shipWaferData)){
								shipWaferData = shipWaferData.substring(0 , shipWaferData.length()-1);
							}
							if(!"".equals(rmaWaferData)){
								rmaWaferData = rmaWaferData.substring(0, rmaWaferData.length()-1);
							}
							if(!"".equals(scrapWaferData)){
								scrapWaferData = scrapWaferData.substring(0, scrapWaferData.length()-1);
							}

							offloadShippingLists.get(i).setShipQty(shipQty);
							offloadShippingLists.get(i).setRmaQty(rmaQty);
							offloadShippingLists.get(i).setScrapQty(scrapQty);
							offloadShippingLists.get(i).setShippingWaferData(shipWaferData);
							offloadShippingLists.get(i).setRmaWaferData(rmaWaferData);
							offloadShippingLists.get(i).setScrapWaferData(scrapWaferData);

						}else{
							offloadShippingLists = offloadOperationService.getOffloadShippingLists(offloadLotnoIdx);	
						}
					}
				}
			}else{
				offloadShippingLists = offloadOperationService.getOffloadShippingLists(offloadLotnoIdx);
			}
		}else{
			offloadShippingLists = offloadOperationService.getOffloadShippingLists(offloadLotnoIdx);
		}
		
		listboxOffloadShippingInfo.setModel(new ListModelList<OffloadShippingConfirm>(offloadShippingLists));
		listboxOffloadShippingInfo.setItemRenderer(this);
		
		if(offloadShippingLists.size()>0 && offloadShippingLists != null){
			int confirmQty=0;
			int RemainQty=0;
			for(int i=0; i<offloadShippingLists.size(); i++){
				if(offloadShippingLists.get(i).isConfirm()){
					confirmQty = confirmQty + offloadShippingLists.get(i).getShipQty() + offloadShippingLists.get(i).getRmaQty() + offloadShippingLists.get(i).getScrapQty();
					lblConfirmQtyField.setValue(String.valueOf(confirmQty));
					lblRemainingQtyField.setValue(String.valueOf(Integer.valueOf(lblOffloadQtyField.getValue())-confirmQty));
				}
			}
		}
		
		if("0".equals(lblRemainingQtyField.getValue())){//當Remaining Qty=0時，代表所有的Wafer Qty都已經做了Offload Shipping，故不能再New Add & 要Update [OFFLOAD_LOTNO. OFFLOAD_STATUS] = '30'(Offload Close)
			btnNewAdd.setDisabled(true);
			btnSave.setDisabled(true);
			offloadOperationService.updateOffloadLotno(offloadLotno);
		}
	}


	public void onClick$btnExit(){
		winOffloadShippingConfirmFunction.getParent().setAttribute("offloadLotno", offloadLotno);
		Events.sendEvent(new Event("onClick", (Button)(winOffloadShippingConfirmFunction.getParent().getFellow("btnSearch"))));
		winOffloadShippingConfirmFunction.detach();
	}
	
	
	public void onClick$btnOffloadWaferConfirm(){
		int countSelect=0;
		int selectIdx=0;
		for(int i=0; i<offloadShippingLists.size(); i++){
			if(offloadShippingLists.get(i).isSelect()){
				countSelect=countSelect+1;
				selectIdx=i;
			}
		}
		
		
		Map args = new HashMap();
		args.put("waferData", waferData);
		args.put("customerLotNo", lblCustomerLotNoField.getValue());
		args.put("offloadWaferConfirmList", winOffloadShippingConfirmFunction.getAttribute("offloadWaferConfirm"));
		args.put("shippingConfirmSelectIdx", String.valueOf(selectIdx));
		args.put("offloadShippingLists", offloadShippingLists);
		args.put("offloadStatus", offloadStatus);
		
		mode="WaferConfirm";
		Window winWaferConfirm = (Window)Executions.createComponents("/WEB-INF/modules/oe/OffloadWaferConfirm.zul", null, args);
		winWaferConfirm.setParent(winOffloadShippingConfirmFunction);
		winWaferConfirm.doModal();	
	}
	
	
	public void onClick$btnNewAdd(){
		OffloadShippingConfirm offloadShipping =  new OffloadShippingConfirm();
		Date shipDate = new Date();
		offloadShipping.setPackingListType("N");
		offloadShipping.setPackingListNumber(shippingService.createPackingListNumber("N", shipDate));
		offloadShippingLists.add(offloadShipping);
		listboxOffloadShippingInfo.setModel(new ListModelList<OffloadShippingConfirm>(offloadShippingLists));
		listboxOffloadShippingInfo.setItemRenderer(this);
		listboxOffloadShippingInfo.clearSelection();
		btnOffloadWaferConfirm.setDisabled(true);
		btnNewAdd.setDisabled(true);
		mode="New";
	}
	
	
	
	public void onClick$btnSave(){
		List<OffloadWaferConfirm> offloadWaferConfirmLists = (List<OffloadWaferConfirm>) winOffloadShippingConfirmFunction.getAttribute("offloadWaferConfirm");
		if(offloadWaferConfirmLists.size()<=0){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.message.noWaferConfirm"), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		
		if(!iNaviCheck){
			Messagebox.show(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.message.inavilotnoerror"), "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		
		for(int i=0; i<offloadShippingLists.size(); i++){
			if(offloadShippingLists.get(i).isSelect()){
				//檢查PACKING_LIST_TYPE & INAVI_LOTNO & SHIPPING_DATE欄位不可為空白
				Combobox cbxPackingListType = (Combobox)component.getFellow("comboboxPackingListType"+String.valueOf(i));
				Textbox edtiNaviLotNo = (Textbox)component.getFellow("textboxiNaviLotno"+String.valueOf(i));
				Datebox dtxShipDate = (Datebox)component.getFellow("dateboxShipDate"+String.valueOf(i));
				if("".equals(cbxPackingListType.getText())){
					cbxPackingListType.setConstraint("no empty");
					cbxPackingListType.setFocus(true);
					log.debug(cbxPackingListType.getText());//勿刪，要有這行setConstraint才會生效不讓程式繼續往下跑
				}
				if("".equals(edtiNaviLotNo.getText())){
					edtiNaviLotNo.setConstraint("no empty");
					edtiNaviLotNo.setFocus(true);
					log.debug(edtiNaviLotNo.getText());//勿刪，要有這行setConstraint才會生效不讓程式繼續往下跑
				}
				if("".equals(dtxShipDate.getText())){
					dtxShipDate.setConstraint("no empty");
					dtxShipDate.setFocus(true);
					log.debug(dtxShipDate.getText());//勿刪，要有這行setConstraint才會生效不讓程式繼續往下跑
				}
			}
		}
		
		Messagebox.show(Labels.getLabel("common.message.saveok"), "Information", Messagebox.OK, Messagebox.INFORMATION);
		btnConfirm.setDisabled(false);//一定要按了Save成功後，才可以按Confirm
		mode="Save";
	}
	
	
	
	public void onClick$btnConfirm(){
		boolean saveFlag=false;
		for(int i=0; i<offloadShippingLists.size(); i++){
			if(offloadShippingLists.get(i).isSelect()){
				offloadShippingLists.get(i).setOffloadLotNoIdx(offloadLotnoIdx);
				CustomerTable orderBillTo = customerInformationService.getCustomerTableByCustomerIdCancelFlag(billTo);
				CustomerTable orderShipTo = customerInformationService.getCustomerTableByCustomerIdCancelFlag(shipTo);
				offloadShippingLists.get(i).setBillTo(orderBillTo.getCustomerName());//OCF-PR-150602_修改正確應該是帶[CUSTOMER_TABLE].CUSTOMER_NAME
				offloadShippingLists.get(i).setShipTo(orderShipTo.getCustomerEnglishName());
				offloadShippingLists.get(i).setShipToAddress(orderShipTo.getCustomerAddress());
				offloadShippingLists.get(i).setCustomerLotNo(lblCustomerLotNoField.getValue());
				offloadShippingLists.get(i).setPoItem(poItem);
				offloadShippingLists.get(i).setPoNumber(poNumber);
				offloadShippingLists.get(i).setProduct(product);
				offloadShippingLists.get(i).setBillToPo(billToPo);
				
				//參照PackingListOperationViewCtrl裡的程式寫法
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				ExchangeRate exchangeRate= finService.getExchangeRateByShipDate(df.format(offloadShippingLists.get(i).getShipDate()), currency);
				if("NTD".equals(currency)){
					offloadShippingLists.get(i).setExchangeRate(new BigDecimal(1));
				}else{
					if (exchangeRate!=null){
						offloadShippingLists.get(i).setExchangeRate(exchangeRate.getTtmRate());
					}
					else {
						offloadShippingLists.get(i).setExchangeRate(new BigDecimal(0));
					}
				}
				
				saveFlag = offloadOperationService.saveOffloadShipping(offloadShippingLists.get(i));
			}
		}
		
		if(saveFlag){
			LinkedHashMap<String,String> saveData = offloadOperationService.updateWaferBankinsAndInsertManagementHistory(lblCustomerLotNoField.getValue(), loginId, offloadWaferConfirmLists);
			Messagebox.show(Labels.getLabel("modules.shp.inspreport.ctrl.confirmok", new Object[]{saveData.get("composeFrom"), saveData.get("composeTo"), saveData.get("composeCustomerLotNo"), saveData.get("composeWaferData")}), "Information", Messagebox.OK, Messagebox.INFORMATION);
			mode="Confirm";
			offloadShippingLists.clear();
			btnOffloadWaferConfirm.setDisabled(true);
			btnNewAdd.setDisabled(false);
			btnConfirm.setDisabled(true);
			List<OffloadWaferConfirm> offloadWaferConfirmLists = (List<OffloadWaferConfirm>) winOffloadShippingConfirmFunction.getAttribute("offloadWaferConfirm");
			if(offloadWaferConfirmLists != null){
				if(offloadWaferConfirmLists.size()>0){
					offloadWaferConfirmLists.clear();
				}
			}
			onClick$btnSearch();
		}
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
	

	/**
	 * 初始化多語系LABEL
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		winOffloadShippingConfirmFunction.setTitle(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.winOffloadShippingConfirmFunction"));;
		lblOrderNumber.setValue(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.lblOrderNumber"));
		lblOffloadQty.setValue(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.lblOffloadQty"));
		btnSave.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.btnSave"));
		lblProduct.setValue(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.lblProduct"));
		lblConfirmQty.setValue(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.lblConfirmQty"));
		btnConfirm.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.btnConfirm"));
		lblCustomerLotNo.setValue(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.lblCustomerLotNo"));
		lblRemainingQty.setValue(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.lblRemainingQty"));
		btnExit.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.btnExit"));
		headerSelect.setValue(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerSelect"));
		headerPackingListType.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerPackingListType"));
		headerPackingListNumber.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerPackingListNumber"));
		headeriNaviLotNo.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headeriNaviLotNo"));
		headerShippingQty.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerShippingQty"));
		headerRmaQty.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerRmaQty"));
		headerScrapQty.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerScrapQty"));
		headerRemark.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerRemark"));
		headerShippingDate.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerShippingDate"));
		headerConfirmDate.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerConfirmDate"));
		headerConfirmUser.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.headerConfirmUser"));
		btnNewAdd.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.btnNewAdd"));
		btnOffloadWaferConfirm.setLabel(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.label.btnOffloadWaferConfirm"));
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
		final OffloadShippingConfirm offloadShipping = (OffloadShippingConfirm) inData;
		Date nowtime = new Date();
		
		//宣告Listcell
		Listcell cellSelect = new Listcell();
		Listcell cellPackingListType = new Listcell();
		final Listcell cellPackingListNumber = new Listcell();
		Listcell celliNaviLotNo = new Listcell();
		Listcell cellShippingQty = new Listcell();
		Listcell cellRmaQty = new Listcell();
		Listcell cellScrapQty = new Listcell();
		Listcell cellRemark = new Listcell();
		Listcell cellShippingDate = new Listcell();
		Listcell cellConfirmDate = new Listcell();
		Listcell cellConfirmUser = new Listcell();
		
		final Checkbox checkboxSelect = new Checkbox();
		final Combobox comboboxPackingListType = new Combobox();
		final Textbox textboxiNaviLotno = new Textbox();
		final Textbox textboxRemark = new Textbox();
		final Datebox dateboxShipDate = new Datebox();
		
		cellSelect.setId("cellSelect"+String.valueOf(inIndex));
		cellPackingListType.setId("cellPackingListType"+String.valueOf(inIndex));
		cellPackingListNumber.setId("cellPackingListNumber"+String.valueOf(inIndex));
		celliNaviLotNo.setId("celliNaviLotNo"+String.valueOf(inIndex));
		cellShippingQty.setId("cellShippingQty"+String.valueOf(inIndex));
		cellRmaQty.setId("cellRmaQty"+String.valueOf(inIndex));
		cellScrapQty.setId("cellScrapQty"+String.valueOf(inIndex));
		cellRemark.setId("cellRemark"+String.valueOf(inIndex));
		cellShippingDate.setId("cellShippingDate"+String.valueOf(inIndex));
		cellConfirmDate.setId("cellConfirmDate"+String.valueOf(inIndex));
		cellConfirmUser.setId("cellConfirmUser"+String.valueOf(inIndex));
		
		checkboxSelect.setId("checkboxSelect"+String.valueOf(inIndex));
		comboboxPackingListType.setId("comboboxPackingListType"+String.valueOf(inIndex));
		textboxiNaviLotno.setId("textboxiNaviLotno"+String.valueOf(inIndex));
		textboxRemark.setId("textboxRemark"+String.valueOf(inIndex));
		dateboxShipDate.setId("dateboxShipDate"+String.valueOf(inIndex));
		
		//SELECT
		checkboxSelect.setChecked(offloadShipping.isSelect());
		if(offloadShipping.getConfirmDate() != null){
			if(offloadShipping.getConfirmDate().before(nowtime) || offloadShipping.isConfirm()){
				checkboxSelect.setDisabled(true);
			}
		}
		checkboxSelect.setParent(cellSelect);
		checkboxSelect.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				offloadShippingLists.get(inIndex).setSelect(checkboxSelect.isChecked());
				boolean selectFlag=false;
				int selectIdx=0;
				for(int i=0; i<offloadShippingLists.size(); i++){
					if(offloadShippingLists.get(i).isSelect()){
						selectFlag=true;
						selectIdx=i;
					}
				}
				if(!selectFlag){
					btnOffloadWaferConfirm.setDisabled(true);
				}else{
					btnOffloadWaferConfirm.setDisabled(false);
					winOffloadShippingConfirmFunction.setAttribute("shippingConfirmIdx", selectIdx);
				}
				
				comboboxPackingListType.setDisabled(!checkboxSelect.isChecked());
				textboxiNaviLotno.setDisabled(!checkboxSelect.isChecked());
				textboxRemark.setDisabled(!checkboxSelect.isChecked());
				dateboxShipDate.setDisabled(!checkboxSelect.isChecked());
			}
		});
		
		//PACKING_LIST_TYPE
		final List<UiFieldSet> packingListType=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.OffloadShippingConfirmViewCtrl", "PACKING_LIST_TYPE");
		if (packingListType.size()>0){
			for(int i=0; i<packingListType.size(); i++){
				for(int j=0; j<packingListType.get(i).getUiFieldParams().size(); j++){
					comboboxPackingListType.appendItem(packingListType.get(i).getUiFieldParams().get(j).getParaValue());
					comboboxPackingListType.setValue(packingListType.get(i).getUiFieldParams().get(j).getParaValue());
				}
			}
		}

		Date shipDate= new Date();
		if(offloadShipping.getPackingListType() != null && !"".equals(offloadShipping.getPackingListType())){
			comboboxPackingListType.setText(getParaValueByMeaning(offloadShipping.getPackingListType(), packingListType.get(0).getUiFieldParams()));
		}else{
			comboboxPackingListType.setText("");
		}
		if(offloadShipping.getConfirmDate() != null){
			if(offloadShipping.getConfirmDate().before(nowtime) || offloadShipping.isConfirm()){
				comboboxPackingListType.setDisabled(true);
			}else{
				comboboxPackingListType.setDisabled(!checkboxSelect.isChecked());
			}
		}else{
			comboboxPackingListType.setDisabled(!checkboxSelect.isChecked());
		}
		comboboxPackingListType.setParent(cellPackingListType);
		comboboxPackingListType.addEventListener("onSelect", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				Date shipDate= new Date();
				offloadShippingLists.get(inIndex).setPackingListType(getMeaningByParaValue(comboboxPackingListType.getText(), packingListType.get(0).getUiFieldParams()));
				cellPackingListNumber.setLabel(shippingService.createPackingListNumber(getMeaningByParaValue(comboboxPackingListType.getText(), packingListType.get(0).getUiFieldParams()), shipDate));
				offloadShippingLists.get(inIndex).setPackingListNumber(cellPackingListNumber.getLabel());
				
//				
//				String tmpPackingListNumber="";
//				for(int i=0; i<offloadShippingLists.size(); i++){	
//					if(inIndex != i){
//						if(!"".equals(offloadShippingLists.get(i).getPackingListType()) && offloadShippingLists.get(i).getPackingListType() != null){
//							if(offloadShippingLists.get(i).getPackingListType().substring(0, 1).equals(comboboxPackingListType.getText().substring(0, 1))){
//								if("".equals(tmpPackingListNumber)){
//									tmpPackingListNumber = offloadShippingLists.get(i).getPackingListNumber();
//								}else{
//									if(offloadShippingLists.get(i).getPackingListNumber() != null){
//										if(Integer.valueOf(tmpPackingListNumber.substring(1,9)) < Integer.valueOf(offloadShippingLists.get(i).getPackingListNumber().substring(1, 9))){
//											tmpPackingListNumber = offloadShippingLists.get(i).getPackingListNumber();
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//				
//				log.debug(tmpPackingListNumber);
//				if(!"".equals(tmpPackingListNumber) && tmpPackingListNumber != null){
//					cellPackingListNumber.setLabel(getMeaningByParaValue(comboboxPackingListType.getText(), packingListType.get(0).getUiFieldParams())+String.valueOf(Integer.valueOf(tmpPackingListNumber.substring(1, 9))+1));
//					offloadShippingLists.get(inIndex).setPackingListNumber(cellPackingListNumber.getLabel());
//				}else{
//					
//				}
			}
		});

		//PACKING_LIST_NUMBER
		if(offloadShipping.getPackingListNumber() != null && !"".equals(offloadShipping.getPackingListNumber())){
			cellPackingListNumber.setLabel(offloadShipping.getPackingListNumber());
		}else{
			cellPackingListNumber.setLabel("");
		}
		
		//INAVI_LOTNO
		textboxiNaviLotno.setMaxlength(18);
		if(offloadShipping.getiNaviLotNo() != null && !"".equals(offloadShipping.getiNaviLotNo())){
			textboxiNaviLotno.setText(offloadShipping.getiNaviLotNo());
		}else{
			textboxiNaviLotno.setText("");
		}
		if(offloadShipping.getConfirmDate() != null){
			if(offloadShipping.getConfirmDate().before(nowtime) || offloadShipping.isConfirm()){
				textboxiNaviLotno.setDisabled(true);
			}else{
				textboxiNaviLotno.setDisabled(!checkboxSelect.isChecked());
			}
		}else{
			textboxiNaviLotno.setDisabled(!checkboxSelect.isChecked());
		}
		textboxiNaviLotno.setParent(celliNaviLotNo);
		textboxiNaviLotno.addEventListener("onChange", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				//Check iNavi LotNo的format必須符合：
				//Code 1- 8 : 文字(LotNo)
				//Code 9     : 要為'-'
				//Code 10- 13 : 文字
				//Code 14     : 要為'-'
				//Code 15- 18 : 文字
				//Ex:T1411001-0001-A000
				if(!textboxiNaviLotno.getText().matches("[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}")){
					Messagebox.show(Labels.getLabel("modules.oe.OffloadShippingConfirm.ctrl.message.inavilotnoerror"), "Error", Messagebox.OK, Messagebox.ERROR);
					iNaviCheck=false;
				}else{
					offloadShipping.setiNaviLotNo(textboxiNaviLotno.getText().trim());
					offloadShippingLists.get(inIndex).setiNaviLotNo(textboxiNaviLotno.getText().trim());
					iNaviCheck=true;
				}
			}
		});
		
		//SHIPPING_QTY
		if(!"".equals(offloadShipping.getShipQty())){
			cellShippingQty.setLabel(String.valueOf(offloadShipping.getShipQty()));
		}else{
			cellShippingQty.setLabel("");
		}
		
		//RMA_QTY
		if(!"".equals(offloadShipping.getRmaQty())){
			cellRmaQty.setLabel(String.valueOf(offloadShipping.getRmaQty()));
		}else{
			cellRmaQty.setLabel("");
		}
		
		//SCRAP_QTY
		if(!"".equals(offloadShipping.getScrapQty())){
			cellScrapQty.setLabel(String.valueOf(offloadShipping.getScrapQty()));
		}else{
			cellScrapQty.setLabel("");
		}
		
		//REMARK
		if(offloadShipping.getRemark() != null && !"".equals(offloadShipping.getRemark())){
			textboxRemark.setText(offloadShipping.getRemark());
		}else{
			textboxRemark.setText("");
		}
		if(offloadShipping.getConfirmDate() != null){
			if(offloadShipping.getConfirmDate().before(nowtime) || offloadShipping.isConfirm()){
				textboxRemark.setDisabled(true);
			}else{
				textboxRemark.setDisabled(!checkboxSelect.isChecked());
			}		
		}else{
			textboxRemark.setDisabled(!checkboxSelect.isChecked());
		}
		textboxRemark.setParent(cellRemark);
		textboxRemark.addEventListener("onChange", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				offloadShippingLists.get(inIndex).setRemark(textboxRemark.getText());
			}
		});
		
		//SHIPPING_DATE
		dateboxShipDate.setFormat(Labels.getLabel("format.datetime"));
		if(offloadShipping.getShipDate() != null && !"".equals(offloadShipping.getShipDate())){
			dateboxShipDate.setValue(offloadShipping.getShipDate());
			offloadShippingLists.get(inIndex).setShipDate(offloadShipping.getShipDate());
		}else{
			dateboxShipDate.setValue(nowtime);
			dateboxShipDate.setText(DateFormatUtil.getDateTimeFormatHHmm().format(nowtime));
			offloadShippingLists.get(inIndex).setShipDate(nowtime);
		}
		if(offloadShipping.getConfirmDate() != null){
			if(offloadShipping.getConfirmDate().before(nowtime) || offloadShipping.isConfirm()){
				dateboxShipDate.setDisabled(true);
			}else{
				dateboxShipDate.setDisabled(!checkboxSelect.isChecked());
			}
		}else{
			dateboxShipDate.setDisabled(!checkboxSelect.isChecked());
		}
		dateboxShipDate.setWidth("99%");
		dateboxShipDate.setParent(cellShippingDate);
		dateboxShipDate.addEventListener("onChange", new EventListener<Event>() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				offloadShippingLists.get(inIndex).setShipDate(dateboxShipDate.getValue());
			}
		});
		
		//CONFIRM_DATE
		if(offloadShipping.getConfirmDate() != null){
			cellConfirmDate.setLabel(DateFormatUtil.getDateTimeFormat().format(offloadShipping.getConfirmDate()));
		}else{
			cellConfirmDate.setLabel("");
		}
		
		//CONFIRM_USER
		EmplInfo updateUser = userService.getEmplInfoByEmplId(offloadShipping.getConfirmUser());
		if(updateUser != null){
			cellConfirmUser.setLabel(updateUser.getEmplFamilyname()+updateUser.getEmplFirstname());
		}else{
			cellConfirmUser.setLabel("");
		}
		
		
		
		//將each Litcell 放上ListItme上
		cellSelect.setParent(inItem);
		cellPackingListType.setParent(inItem);
		cellPackingListNumber.setParent(inItem);
		celliNaviLotNo.setParent(inItem);
		cellShippingQty.setParent(inItem);
		cellRmaQty.setParent(inItem);
		cellScrapQty.setParent(inItem);
		cellRemark.setParent(inItem);
		cellShippingDate.setParent(inItem);
		cellConfirmDate.setParent(inItem);
		cellConfirmUser.setParent(inItem);

	}
}
