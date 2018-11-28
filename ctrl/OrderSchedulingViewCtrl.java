/*
 * Project Name:iVision
 * File Name:OrderSchedulingViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2012/12/28下午7:23:58
 * 
 * 說明:
 * 納期回答的設定介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.oe.model.OrderScheduling;
import com.tce.ivision.modules.oe.model.OrderSchedulingParameter;
import com.tce.ivision.modules.oe.render.OrderSchedulingRender;
import com.tce.ivision.modules.oe.service.OrderSchedulingService;
import com.tce.ivision.units.common.DateUtil;
import com.tce.ivision.units.common.ZkComboboxControl;

/**
 * ClassName: OrderSchedulingViewCtrl <br/>
 * date: 2012/12/28 下午7:23:58 <br/>
 *
 * @author honda
 * @version 
 * @since JDK 1.6
 */
public class OrderSchedulingViewCtrl extends BaseViewCtrl {

	/**
	 * serialVersionUID
	 * @since JDK 1.6
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 與ZK前端 對應的Window元件
	 */
	private Window winOrderScheduling;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo25;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo24;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo23;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo22;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo21;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo20;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo19;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo18;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo17;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo16;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo15;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo14;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo13;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo12;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo11;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo10;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo9;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo8;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo7;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo6;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo5;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo4;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo3;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo2;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomerLotNo1;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoOrderConfirmNumber;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCommitDeliveryDate;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoReScheduleDate;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoRequestDeliveryDate;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoWaferQty;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoLotNo;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoMtrlDesc;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoProduct;
	/**
	 * 與ZK前端 對應的List Header元件 2017.12.20
	 */
	private Listheader headerLotInfoRealProduct;	
	
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoPoNumber;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoBilltoPo;//IT-PR-141201
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoCustomer;
	/**
	 * 與ZK前端 對應的List Header元件
	 */
	private Listheader headerLotInfoOperation;
	/**
	 * 與ZK前端 對應的List Box元件
	 */
	private Listbox listboxLotInformation;
	/**
	 * ZK前端 LotInfo Caption
	 */
	private Caption captionLotInfo;
	/**
	 * ZK前端 LotNo 輸入格
	 */
	private Textbox tbLotNo;
	/**
	 * ZK前端 LotNo 標籤
	 */
	private Label lbLotNo;
	/**
	 * ZK前端 Product 選項
	 */
	private Combobox cbProduct;
	/**
	 * ZK前端 Product 標籤
	 */
	private Label lbProduct;
	/**
	 * ZK前端 Search 按鈕
	 */
	private Button btnSearch;
	/**
	 * ZK前端 Po Number 輸入格
	 */
	private Textbox tbPoNumber;
	/**
	 * ZK前端 Po Number 標籤 
	 */
	private Label lbPoNumber;
	/**
	 * ZK前端 Customer 選項
	 */
	private Combobox cbCustomer;
	/**
	 * ZK前端 Customer 標籤
	 */
	private Label lbCustomer;
	/**
	 * ZK前端 結束時間 
	 */
	private Datebox dateEndDate;
	/**
	 * ZK前端 開始時間
	 */
	private Datebox dateBeginDate;
	/**
	 * ZK前端 OrderDate 標籤
	 */
	private Label lbOrderDate;
	/**
	 * ZK前端 Close 勾選欄位
	 */
	private Checkbox chkClosed;
	/**
	 * ZK前端 Save 按鈕
	 */
	private Button btnSave;
	/**
	 * ZK Image component
	 */
	private Image imgCopyCommitDate;

	/**
	 * CustomerInformationService
	 */
	private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil.getBean("customerInformationService");
	/**
	 * OrderSchedulingService
	 */
	private OrderSchedulingService orderSchedulingService = (OrderSchedulingService) SpringUtil.getBean("orderSchedulingService");
	
	/**
	 * customerTable List 用於接取query出來的customerTable Bean
	 */
	private List<CustomerTable> customerTableList;
	
	/**
	 * 搜尋參數的bean 
	 */
	private OrderSchedulingParameter queryParameter;
	/**
	 * 搜尋出來的OrderScheduling List
	 */
	private List<OrderScheduling> orderSchedulingList;
	
	/**
	 * 頁面載入後開始執行初始化的function
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		log.debug(this.getClass().getName());
		queryParameter = new OrderSchedulingParameter();
		inComp.setAttribute("queryParameter", queryParameter);
		binder.bindBean("value", queryParameter);
		binder.loadAll();
		btnSave.setDisabled(true);
	}
	/**
	 * checkQueryParameter:用來CHECK Query參數是否正確輸入. <br/>
	 * (1) Customer + Product <br/>
	 * (2) Lot_No <br/>
	 * @author honda
	 * @param inParam
	 * @return
	 * @since JDK 1.6
	 */
	private boolean checkQueryParameter(OrderSchedulingParameter inParam){
		boolean checkFlag = true;
		if("".equals(inParam.getLotNo()) 
			&& "".equals(inParam.getCustomerId())
			&& "".equals(inParam.getProduct())){
			checkFlag = false;
		}else if("".equals(inParam.getLotNo()) 
				&& "".equals(inParam.getCustomerId()) == false
				&& "".equals(inParam.getProduct())){
			checkFlag = false;
		}
		return checkFlag;
	}
	/** 
	 * onClick$btnSearch:btnSearch onClick event
	 *
	 * @author honda
	 * @since JDK 1.6
	 */
	public void onClick$btnSearch(){
		log.debug(queryParameter.toString());
		if(checkQueryParameter(queryParameter) == true){
			orderSchedulingList = orderSchedulingService.getOrderSchedulings(queryParameter);
			//若搜尋結果超過100筆則跳出詢問視窗問USER是否繼續顯示
			if(orderSchedulingList.size() > 100){
				Messagebox.show(Labels.getLabel("common.message.query.ResultMoreThanOneHundred"),
					"Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, 
					new org.zkoss.zk.ui.event.EventListener<Event>(){
					    public void onEvent(Event inEvt){
					    	if (Messagebox.ON_OK.equals(inEvt.getName())){
					    		listboxLotInformation.setModel(new ListModelList<OrderScheduling>(orderSchedulingList));
								listboxLotInformation.setItemRenderer(new OrderSchedulingRender());
					    	}
					    }
					}
				);
			}else if(orderSchedulingList.size() == 0){
				Messagebox.show(Labels.getLabel("common.message.query.nodata"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				listboxLotInformation.setModel(new ListModelList<OrderScheduling>(orderSchedulingList));
				listboxLotInformation.setItemRenderer(new OrderSchedulingRender());
			}else{
				listboxLotInformation.setModel(new ListModelList<OrderScheduling>(orderSchedulingList));
				listboxLotInformation.setItemRenderer(new OrderSchedulingRender());
			}
			if(orderSchedulingList.size() > 0){
				btnSave.setDisabled(false);
			}else{
				btnSave.setDisabled(true);
			}
			
		}else{
			Messagebox.show(Labels.getLabel("modules.oe.OrderScheduling.ctrl.message.QueryParameterNotEnough"), "Error", Messagebox.OK, Messagebox.ERROR);
		}
	}
	public void onClick$imgCopyCommitDate(){
		Messagebox.show(Labels.getLabel("modules.oe.OrderScheduling.ctrl.message.AutoSetEmptyCommitDate"),
				"Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, 
				new org.zkoss.zk.ui.event.EventListener<Event>(){
				    public void onEvent(Event inEvt){
				    	if (Messagebox.ON_OK.equals(inEvt.getName())){
				    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				    		for(int i = 0; i < orderSchedulingList.size(); i++){
				    			if(orderSchedulingList.get(i).getCommitDeliveryDate() == null){
				    				try {
										orderSchedulingList.get(i).setCommitDeliveryDate(DateUtil.setTime(sdf.parse(orderSchedulingList.get(i).getRequestDate()),18,0,0));
									} catch (ParseException e) {
										e.printStackTrace();
									}
				    			}
				    			
				    			//IT-PR-140616_Allison add
				    			if(orderSchedulingList.get(i).getRescheduleDate() == null){
				    				try {
										orderSchedulingList.get(i).setRescheduleDate(DateUtil.setTime(sdf.parse(orderSchedulingList.get(i).getRequestDate()),18,0,0));
									} catch (ParseException e) {
										e.printStackTrace();
									}
				    			}
				    		}
				    		listboxLotInformation.setModel(new ListModelList<OrderScheduling>(orderSchedulingList));
				    		listboxLotInformation.setItemRenderer(new OrderSchedulingRender());
				    		
				    		Messagebox.show(Labels.getLabel("common.message.opeartion.success"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				    		
				    	}
				    }
				}
			);
	}
	
	/**
	 * onSelect$cbCustomer:cbCustomer onSelect event
	 * @author honda
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public void onSelect$cbCustomer() throws Exception{
		log.debug("Selected CustomerId:"+queryParameter.getCustomerId());
		List<String> productList = orderSchedulingService.getProductWithStatusAndDateAndCustomer(
			queryParameter.getCustomerId(), queryParameter.isClosedFlag(),
			queryParameter.getBeginDate(), queryParameter.getEndDate()
		);
		ZkComboboxControl.setComboboxClear(cbProduct);
		queryParameter.setProduct("");
		ZkComboboxControl.setComboboxItems(cbProduct, productList, "toString", "", false);	
	}
	/**
	 * 
	 * onClick$btnSave:btnSave onClick event. <br/>
	 * @author honda
	 * @since JDK 1.6
	 */
	public void onClick$btnSave(){
		Messagebox.show(Labels.getLabel("common.message.saveconfirm"),
			"Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, 
			new org.zkoss.zk.ui.event.EventListener<Event>(){
			    public void onEvent(Event inEvt){
			    	if (Messagebox.ON_OK.equals(inEvt.getName())){
			    		orderSchedulingService.updateOrderScheduling(orderSchedulingList,this.getClass().getName());
			    		
			    		//reload search data to Update ORDER_CONFIRM_NO
			    		orderSchedulingList = orderSchedulingService.getOrderSchedulings(queryParameter);
			    		listboxLotInformation.setModel(new ListModelList<OrderScheduling>(orderSchedulingList));
			    		listboxLotInformation.setItemRenderer(new OrderSchedulingRender());
			    		
			    		Messagebox.show(Labels.getLabel("common.message.opeartion.success"), "Information", Messagebox.OK, Messagebox.INFORMATION);
			    		
			    	}
			    }
			}
		);
	}
	
	
	/**
	 * 
	 * 初始化多語系LABEL
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		headerLotInfoCustomerLotNo25.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo25"));
		headerLotInfoCustomerLotNo24.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo24"));
		headerLotInfoCustomerLotNo23.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo23"));
		headerLotInfoCustomerLotNo22.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo22"));
		headerLotInfoCustomerLotNo21.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo21"));
		headerLotInfoCustomerLotNo20.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo20"));
		headerLotInfoCustomerLotNo19.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo19"));
		headerLotInfoCustomerLotNo18.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo18"));
		headerLotInfoCustomerLotNo17.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo17"));
		headerLotInfoCustomerLotNo16.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo16"));
		headerLotInfoCustomerLotNo15.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo15"));
		headerLotInfoCustomerLotNo14.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo14"));
		headerLotInfoCustomerLotNo13.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo13"));
		headerLotInfoCustomerLotNo12.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo12"));
		headerLotInfoCustomerLotNo11.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo11"));
		headerLotInfoCustomerLotNo10.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo10"));
		headerLotInfoCustomerLotNo9.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo9"));
		headerLotInfoCustomerLotNo8.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo8"));
		headerLotInfoCustomerLotNo7.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo7"));
		headerLotInfoCustomerLotNo6.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo6"));
		headerLotInfoCustomerLotNo5.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo5"));
		headerLotInfoCustomerLotNo4.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo4"));
		headerLotInfoCustomerLotNo3.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo3"));
		headerLotInfoCustomerLotNo2.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo2"));
		headerLotInfoCustomerLotNo1.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomerLotNo1"));						
		headerLotInfoOrderConfirmNumber.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoOrderConfirmNumber"));
		headerLotInfoPoNumber.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoPoNumber"));
		headerLotInfoBilltoPo.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoBilltoPo"));//IT-PR-141201
		headerLotInfoCommitDeliveryDate.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCommitDeliveryDate"));
		headerLotInfoReScheduleDate.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoReScheduleDate"));
		headerLotInfoRequestDeliveryDate.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoRequestDeliveryDate"));
		headerLotInfoWaferQty.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoWaferQty"));
		headerLotInfoMtrlDesc.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoMtrlDesc"));
		headerLotInfoProduct.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoProduct"));
		headerLotInfoRealProduct.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoRealProduct")); //2017.12.20
		headerLotInfoCustomer.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoCustomer"));
		headerLotInfoLotNo.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoLotNo"));
		headerLotInfoOperation.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.headerLotInfoOperation"));		
		lbOrderDate.setValue(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.lbOrderDate"));
		chkClosed.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.chkClosed"));
		captionLotInfo.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.captionLotInfo"));
		lbLotNo.setValue(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.lbLotNo"));
		lbProduct.setValue(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.lbProduct"));
		btnSearch.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.btnSearch"));
		lbPoNumber.setValue(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.lbPoNumber"));
		lbCustomer.setValue(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.lbCustomer"));
		btnSave.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.btnSave"));
		//btnExit.setLabel(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.btnExit"));
		winOrderScheduling.setTitle(Labels.getLabel("modules.oe.OrderScheduling.ctrl.label.winOrderScheduling"));
	}
	/**
	 * 
	 * 初始化COMBOBOX選項
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
		//Customer
		customerTableList=customerInformationService.getCustomerTableByBusPurpose("C");
		ZkComboboxControl.setComboboxClear(cbCustomer);
		ZkComboboxControl.setComboboxItemValues(cbCustomer, customerTableList, "getCustomerShortName","getCustomerId", "isCancelFlag",false);
		ZkComboboxControl.setComboboxClear(cbProduct);
		
	}

}
