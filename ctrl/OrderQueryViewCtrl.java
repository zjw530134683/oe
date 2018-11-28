/*
 * Project Name:iVision
 * File Name:OrderQueryViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2012/12/14上午10:40:14
 * 
 * 說明:
 * 1.透過此介面查詢OrderStatus為(10)BOOKED,(20)PRODUCTION,(30)CLOSED的資料
 * 2.Create New Order
 * 3.Delete
 * 
 * 修改歷史:
 * 2012.12.17 OCF#OE002 Fanny Initial
 * 2013.02.18           Fanny 刪除時，將WaferStatus.STATE_FLAG設為2
 * 2013.03.07           Fanny OrderStatus=20時，可修改
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Image;
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
import com.tce.ivision.model.LotInfo;
import com.tce.ivision.model.LotResult;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.OrderLine;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.model.UiFieldParam;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.apa.service.IapaService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.inavi.model.TDiffuseLot;
import com.tce.ivision.modules.inavi.model.TLot;
import com.tce.ivision.modules.inavi.model.TLotResult;
import com.tce.ivision.modules.inavi.model.TOrder;
import com.tce.ivision.modules.inavi.model.TWaferStatus;
import com.tce.ivision.modules.inavi.service.InaviService;
import com.tce.ivision.modules.oe.model.OrderQueryModel;
import com.tce.ivision.modules.oe.service.OrderEntryService;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.ZkComboboxControl;
import com.tce.ivision.units.common.service.CommonService;

/**
 * ClassName: OrderQueryViewCtrl <br/>
 * date: 2012/12/14 上午10:40:14 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderQueryViewCtrl extends BaseViewCtrl implements ListitemRenderer{
	/**
	 * Logger
	 */
	public static Logger log = Logger.getLogger(OrderQueryViewCtrl.class);
	
	//Global var
	/**
	 * Login UserId
	 */
	String userId="030260";
	
	/**
	 * Login User的OperationUnit
	 */
	String operationUnit="01";

	/**
	 * zul component:Order Query window
	 */
	private Window winOrderQuery;
	
	/**
	 * zul component:Listheader colDataOrderStatus
	 */
	private Listheader colDataOrderStatus;
	
	/**
	 * zul component:Listheader colDataCustomerPo
	 */
	//private Listheader colDataCustomerPo;
	
	/**
	 * zul component:Listheader colDataPONumber
	 */
	private Listheader colDataPONumber;
	
	/**
	 * zul component:Listheader coldataOrderNumber
	 */
	private Listheader coldataOrderNumber;
	
	/**
	 * zul component:Listheader colDataWaferSize
	 */
	private Listheader colDataWaferSize;
	
	/**
	 * zul component:Listheader colDataTotalWaferQty
	 */
	private Listheader colDataTotalWaferQty;
	
	/**
	 * zul component:Listheader colDataShipTo
	 */
	private Listheader colDataShipTo;
	
	/**
	 * zul component:Listheader colDataBillTo
	 */
	private Listheader colDataBillTo;
	
	/**
	 * zul component:Listheader colDataOrderDate
	 */
	private Listheader colDataOrderDate;
	
	/**
	 * zul component:Listheader colDataOrderType
	 */
	private Listheader colDataOrderType;
	
	/**
	 * zul component:Listheader colDataProduct
	 */
	private Listheader colDataProduct;
	
	/**
	 * zul component:Listheader colDataRealProduct
	 */
	private Listheader colDataRealProduct;
	
	/**
	 * zul component:Listheader colDataCustomer
	 */
	private Listheader colDataCustomer;
	
	/**
	 * zul component:Listheader colDataNo
	 */
	private Listheader colDataNo;
	
	/**
	 * zul component:查詢結果 Listbox grdOrderData
	 */
	private Listbox grdOrderData;
	
	/**
	 * zul component:Button btnSearch
	 */
	private Button btnSearch;
	
	/**
	 * zul component:查詢時是否包含已關帳的資料 Checkbox chbClosed
	 */
	private Checkbox chbClosed;
	
	/**
	 * zul component:查詢條件 Datebox dtbOrderDateE
	 */
	private Datebox dtbOrderDateE;
	
	/**
	 * zul component:查詢條件 Datebox dtbOrderDateS
	 */
	private Datebox dtbOrderDateS;
	
	/**
	 * zul component:  Order Date Label
	 */
	private Label lblOrderDate;
	
	/**
	 * zul component:查詢條件 PoNumber
	 */
	private Textbox edtPoNum;
	
	/**
	 * zul component: PoNumber
	 */
	private Label lblPoNumber;
	
	/**
	 * zul component:查詢條件 Product
	 */
	private Combobox cbxProduct;
	
	/**
	 * zul component: Product Label
	 */
	private Label lblProduct;
	
	/**
	 * zul component:查詢條件 Customer
	 */
	private Combobox cbxCustomer;
	
	/**
	 * zul component: Customer Label
	 */
	private Label lblCustomer;
	
	/**
	 * zul component: Button btnCreateNewOrder
	 */
	private Button btnCreateNewOrder;
	
	/**
	 * zul component:查詢條件 Order Date
	 */
	private Checkbox chbOrderDate;
	
	//String 
	/**
	 * OrderEntryService
	 */
	private OrderEntryService orderEntryService = (OrderEntryService) SpringUtil.getBean("orderEntryService");
	
	/**
	 * CustomerInformationService
	 */
	private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil.getBean("customerInformationService");
	
	/**
	 * CommonService
	 */
	private CommonService commonService = (CommonService) SpringUtil.getBean("commonService");
	/**
	 * inaviService
	 */
	private InaviService inaviService = (InaviService) SpringUtil.getBean("inaviService");
	
	//Java bean
	/**
	 *CustomerTable,Csutoer Type=C 
	 */
	private List<CustomerTable> customers;
	
	/**
	 * User選擇Customer時所對應的CustomerTable bean
	 */
	private CustomerTable customer;
	
	/**
	 * User選擇Customer時，所對應的Product
	 */
	private List<String> products;
	
	/**
	 * 查詢後的結果
	 */
	private List<OrderHeader> orderHeaders;
	
	/**
	 * 查詢後的結果
	 */
	private List<OrderQueryModel> datas;
	
	/**
	 * PARA_TYPE=OQ_ORDER_TYPE,List<UiFieldSet> orderTypes
	 */
	private List<UiFieldSet> orderTypes;
	
	/**
	 * PARA_TYPE=OQ_ORDER_TYPE,List<UiFieldParam> orderTypeUiFieldParams
	 */
	private List<UiFieldParam> orderTypeUiFieldParams;
	
	/**
	 * PARA_TYPE=OE_ORDER_STATUS,List<UiFieldSet> orderStatuses
	 */
	private List<UiFieldSet> orderStatuses;
	
	/**
	 * PARA_TYPE=OE_ORDER_STATUS,List<UiFieldParam> orderStatusUiFieldParams
	 */
	private List<UiFieldParam> orderStatusUiFieldParams;
	

	/**
	 *
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		userId=loginId;
		operationUnit=OU;
		btnSearch.setImage("/images/icons/magnifier.png");
		btnCreateNewOrder.setImage("images/icons/page_add.png");
		winOrderQuery.setAttribute("orderMode", "");
	}	
	
	public void onSelect$cbxCustomer(){
		if (cbxCustomer.getSelectedIndex()<0){
			return;
		}
		customer=customers.get(cbxCustomer.getSelectedIndex());
		ZkComboboxControl.setComboboxClear(cbxProduct);
		products=orderEntryService.getDistinctProduct(customer.getCustomerId());
		if (products.size()>0){
			for (int i=0;i<products.size();i++){
				cbxProduct.appendItem(products.get(i));
			}
		}
	}
	
	public void onChange$cbxCustomer(){
		this.onSelect$cbxCustomer();
	}
	
	/**
	 * 
	 * clearListbox:清空Listbox inListbox. <br/>
	 *
	 * @author 030260
	 * @param inListbox
	 * @since JDK 1.6
	 */
	public void clearListbox(Listbox inListbox){
		datas=new ArrayList<OrderQueryModel>();
		grdOrderData.setModel(new ListModelList(datas));
		grdOrderData.setItemRenderer(this);
	}
	
	/**
	 * 
	 * onClick$btnSearch:依據畫面上的條件，搜尋出想要找的OrderHeader(不含被刪除的資料). <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void onClick$btnSearch(){
		if (this.chkCondition()==false){
			return;
		}
	
		this.clearListbox(grdOrderData);
		
		String conCustomerId="";
		String conProduct="";
		String conPoNumber="";
		String conOrderDateS="";
		String conOrderDateE="";
		String conOrderStatus="";
		
		if (!("".equals(cbxCustomer.getText()))){
			conCustomerId=customer.getCustomerId();
		}
		if (!("".equals(cbxProduct.getText().trim()))){
			conProduct=cbxProduct.getText().trim();
		}
	    if (!("".equals(edtPoNum.getText()))){
	    	conPoNumber=edtPoNum.getText().trim();
	    }
	    if (chbOrderDate.isChecked()){
	    	conOrderDateS=dtbOrderDateS.getText()+" 00:00:00";
	    	conOrderDateE=dtbOrderDateE.getText()+" 23:59:59";
	    }
	    if (chbClosed.isChecked()==false){
	    	conOrderStatus="30";
	    }
	    orderHeaders=orderEntryService.getOrderHeadersByHql(operationUnit,conCustomerId,conProduct,conPoNumber,conOrderDateS,conOrderDateE,conOrderStatus);
	    if (orderHeaders.size()==0){
	    	this.showmessage("Information", Labels.getLabel("common.message.query.nodata"));
	    	return;
	    }
	    
	    //List<OrderQueryModel> datas=new ArrayList<OrderQueryModel>();
	    datas=new ArrayList<OrderQueryModel>();
	    for (int i=0;i<orderHeaders.size();i++){
	    	OrderQueryModel data=new OrderQueryModel();
	    	
	    	if ("".equals(orderHeaders.get(i).getCustomerId())){
	    		data.setCustomerName("");
	    	}
	    	else {
	    		data.setCustomerName(customerInformationService.getCustomerTableByCustomerId(orderHeaders.get(i).getCustomerId()).getCustomerShortName());
			}
	    	
	    	if ("".equals(orderHeaders.get(i).getOrderType())){
	    		data.setOrderTypeName("");
	    	}
	    	else{
	    		data.setOrderTypeName(this.getParaValueByMeaning(orderHeaders.get(i).getOrderType(),orderTypeUiFieldParams));
	    	}
	    	
	    	if ("".equals(orderHeaders.get(i).getOrderStatus())){
	    		data.setOrderStatusName("");
	    	}
	    	else{
	    		data.setOrderStatusName(this.getParaValueByMeaning(orderHeaders.get(i).getOrderStatus(), orderStatusUiFieldParams));
	    	}
	    	
	    	if ("".equals(orderHeaders.get(i).getBillTo())){
	    		data.setBillToName("");
	    	}
	    	else{
	    		data.setBillToName(customerInformationService.getCustomerTableByCustomerId(orderHeaders.get(i).getBillTo()).getCustomerShortName());
	    	}
	    	
	    	if ("".equals(orderHeaders.get(i).getShipTo())){
	    		data.setShipToName("");
	    	}
	    	else {
				data.setShipToName(customerInformationService.getCustomerTableByCustomerId(orderHeaders.get(i).getShipTo()).getCustomerShortName());
			}
	    	
	    	data.setOrderHeader(orderHeaders.get(i));
	    	datas.add(data);
	    }
	    grdOrderData.setModel(new ListModelList(datas));
	    grdOrderData.setItemRenderer(this);
	    grdOrderData.setSelectedIndex(0);
	}
	
	public void onCheck$chbOrderDate(){
		boolean flag=!(chbOrderDate.isChecked());
		dtbOrderDateS.setDisabled(flag);
		dtbOrderDateE.setDisabled(flag);
		if (chbOrderDate.isChecked()){
			dtbOrderDateE.setValue(new Date());
			Calendar now=Calendar.getInstance();
			now.add(Calendar.DAY_OF_YEAR, -1);
			dtbOrderDateS.setValue(now.getTime());
		}
	}
	
	/**
	 * 
	 * chkCondition:User至少要選一個查詢條件，並檢查所指示的查詢條件是否正確. <br/>
	 *
	 * @author 030260
	 * @return
	 * @since JDK 1.6
	 */
	public boolean chkCondition(){
		//至少要選擇一個條件
		if (("".equals(cbxCustomer.getText())) &&
			("".equals(cbxProduct.getText().trim())) &&
			("".equals(edtPoNum.getText().trim())) &&
			(chbOrderDate.isChecked()==false)){
			this.showmessage("Warning", Labels.getLabel("oe.query.check.condition"));
			return false;
		}
		
		//Customer如果有選擇，必須是選單內的選項
		if (!("".equals(cbxCustomer.getText()))){
			if (cbxCustomer.getSelectedIndex()<0){
				this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",
						new java.lang.Object[] {Labels.getLabel("oe.query.lblCustomer")}));
				return false;
			}
		}
		
		//OrderDate S <= E
		if (chbOrderDate.isChecked()){
			if ("".equals(dtbOrderDateS.getText())){
				this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",
						new java.lang.Object[] {Labels.getLabel("oe.query.lblOrderDate")}));
				return false;
			}
			
			if ("".equals(dtbOrderDateE.getText())){
				this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",
						new java.lang.Object[] {Labels.getLabel("oe.query.lblOrderDate")}));
				return false;
			}
			
			if (dtbOrderDateE.getText().compareTo(dtbOrderDateS.getText())<0){
				this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",
						new java.lang.Object[] {Labels.getLabel("oe.query.lblOrderDate")}));
				return false;
			}
		}
		return true;
	}

	/**
	 * 設定畫面上Label Caption...
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		//window
		winOrderQuery.setTitle(Labels.getLabel("oe.query.winOrderQuery"));
		
		//Button
		btnCreateNewOrder.setLabel(Labels.getLabel("oe.query.btnCreateNewOrder"));
		btnSearch.setLabel(Labels.getLabel("oe.query.btnSearch"));
		btnCreateNewOrder.setTooltiptext(Labels.getLabel("oe.query.btnCreateNewOrder.tooltiptext"));
		
		//Label
		lblCustomer.setValue(Labels.getLabel("oe.query.lblCustomer"));
		lblProduct.setValue(Labels.getLabel("oe.query.lblProduct"));
		lblPoNumber.setValue(Labels.getLabel("oe.query.lblPoNumber"));
		lblOrderDate.setValue(Labels.getLabel("oe.query.lblOrderDate"));
		
		//CheckBox
		chbClosed.setLabel(Labels.getLabel("oe.query.chbClosed"));
				
		//grdOrderData field define
		colDataCustomer.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataCustomer"));
		colDataProduct.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataProduct"));
		colDataOrderType.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataOrderType"));
		colDataOrderDate.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataOrderDate"));
		colDataBillTo.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataBillTo"));
		colDataShipTo.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataShipTo"));
		colDataTotalWaferQty.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataTotalWaferQty"));
		colDataWaferSize.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataWaferSize"));
		colDataOrderStatus.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataOrderStatus"));
		colDataPONumber.setLabel(Labels.getLabel("oe.query.grdOrderData.colDataPONumber"));
		coldataOrderNumber.setLabel(Labels.getLabel("oe.query.grdOrderData.coldataOrderNumber"));
	}
	
	/**
	 * 設定Combobox的選單內容.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
		//Customer
		customers=customerInformationService.getCustomerTableByBusPurpose("C");
		ZkComboboxControl.setComboboxClear(cbxCustomer);
		ZkComboboxControl.setComboboxItems(cbxCustomer, customers, "getCustomerShortName", "isCancelFlag",false);
		ZkComboboxControl.setComboboxClear(cbxProduct);

		//OrderType
		orderTypes=commonService.getUiFieldSetLists(this.getClass().getName(), "OQ_ORDER_TYPE");
		if (orderTypes.size()>0){
			orderTypeUiFieldParams=orderTypes.get(0).getUiFieldParams();
		}
		
		//OrderStatus
		orderStatuses=commonService.getUiFieldSetLists(this.getClass().getName(), "OE_ORDER_STATUS");
		log.debug(orderStatuses.size());
		log.debug(orderStatuses.get(0).getUiFieldParams().size());
		if (orderStatuses.size()>0){
			orderStatusUiFieldParams=orderStatuses.get(0).getUiFieldParams();
			log.debug(orderStatusUiFieldParams.size());
			for (int i=0;i<orderStatusUiFieldParams.size();i++){
				log.debug(orderStatusUiFieldParams.get(i).getMeaning());
			}
		}
	}

	
	/**
	 * 
	 * onClick$btnCreateNewOrder:進入下一個畫面，進行Create New Order. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void onClick$btnCreateNewOrder(){
		Map args = new HashMap();
		args.put("winid", "winOrderEntry");	
		args.put("mode", "newadd");
		args.put("orderQueryModel", null);
		args.put("winOrderQuery", winOrderQuery);
		Window winimport = (Window)Executions.createComponents("/WEB-INF/modules/oe/OrderEntry.zul", null, args);
		winimport.setParent(winOrderQuery);
		winimport.doModal();
		//OCF-PR-151002_如果是直接按Create New Order，則建完OE回到OrderQueryViewCtrl畫面時，不檢查是否有選擇搜尋條件，因為直接按Create New Order回來一定不會有選條件，就會跳出Alarm訊息
		log.debug(winOrderQuery.getAttribute("orderMode"));
		if(!winOrderQuery.getAttribute("orderMode").equals("newadd")){
		onClick$btnSearch();
	}
	}
	
	/**
	 * 
	 * deleteOE:將欲刪除的ORDER_HEADER,ORDER_LINE,ORDER_LINE_LOTNO的CANCEL_FLAG設為TRUE. <br/>
	 *
	 * @author 030260
	 * @param inOrderQueryModel
	 * @since JDK 1.6
	 */
	public void deleteOE(OrderQueryModel inOrderQueryModel){
		//取得現在的時間
		Date nowtime=new Date();
		//Update ivdb相關TABLE
		OrderHeader delOrderHeader=inOrderQueryModel.getOrderHeader();
		delOrderHeader.setOrderStatus("40");//代表OrderStatus=CANCEL
		List<OrderLine> delOrderLines=orderEntryService.getOrderLinesByOrderNumber(inOrderQueryModel.getOrderHeader().getOrderNumber());
		List<OrderLineLotno> delOrderLineLotnos=orderEntryService.getOrderLineLotnosByOrderNumber(inOrderQueryModel.getOrderHeader().getOrderNumber());
		List<WaferStatus> delWaferStatuss=orderEntryService.getWaferStatusByOrderLineLotno(delOrderLineLotnos);//2013.02.18
		List<LotInfo> delLotInfos=orderEntryService.getLotInfoByOrderNumber(inOrderQueryModel.getOrderHeader().getOrderNumber());//IT-PR-141008_Allison add
		List<LotResult> delLotResults=orderEntryService.getLotResultByLotNo(delLotInfos);//IT-PR-141008_Allison add
		orderEntryService.deleteOrderData(delOrderHeader, delOrderLines, delOrderLineLotnos, delWaferStatuss, userId, nowtime, this.getClass().getName(), delLotInfos, delLotResults);//2013.02.18 //IT-PR-141008_Allison modify
		
		//OCF-PR-141204_新增直接Update wfdb相關Table_Allison
		List<TOrder> tOrderLists = inaviService.getTOrder(inOrderQueryModel.getOrderHeader().getPoNumber());
		List<TWaferStatus> tWaferStatusLists = inaviService.getTWaferStatus(inOrderQueryModel.getOrderHeader().getPoNumber());
		List<TLot> tLotLists = inaviService.getTLot(inOrderQueryModel.getOrderHeader().getPoNumber());
		List<TLotResult> tLotResultLists = inaviService.getTLotResult(tLotLists);
		List<TDiffuseLot> tDiffuseLotLists = inaviService.getTDiffuseLot(inOrderQueryModel.getOrderHeader().getPoNumber());
		inaviService.deleteOrderData(tOrderLists, tWaferStatusLists, tLotLists, userId, nowtime, this.getClass().getName(), tLotResultLists, tDiffuseLotLists);
		
		this.showmessage("Information", Labels.getLabel("common.message.opeartion.success"));
		this.onClick$btnSearch();
	}
	
	/**
	 * 
	 * chkDeleteOrder:如果該OrderNumber底下的ORDER_LINE_LOTNO尚有Hold_flag=1的資料，不允許刪除. <br/>
	 *
	 * @author 030260
	 * @return
	 * @since JDK 1.6
	 */
	public boolean chkDeleteOrderHoldFlag(OrderQueryModel inOrderQueryModel){
		List<OrderLineLotno> chkOrderLineLotnos=new ArrayList<OrderLineLotno>();
		chkOrderLineLotnos=orderEntryService.getOrderLineLotnosByOrderNumber(inOrderQueryModel.getOrderHeader().getOrderNumber());
		for (int i=0;i<chkOrderLineLotnos.size();i++){
			if (chkOrderLineLotnos.get(i).isHoldFlag()){
				this.showmessage("Warning", Labels.getLabel("oe.query.click.delete.chkhold",
						new Object[] {Labels.getLabel("oe.edit.line.grbLotnoData")}));
				return false;
			}
		}
		return true;
	}

	/**
	 * Listbox grdData 的render.
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(Listitem inItem, Object inData, int inIndex) throws Exception {
		final OrderQueryModel smf=(OrderQueryModel) inData;
		
		log.debug("render....OrderQueryRender"+inIndex);
		
		Listcell no = new Listcell();
		Listcell customer = new Listcell();
		Listcell product = new Listcell();
		Listcell realProduct = new Listcell(); //2017.12.20
		Listcell orderType = new Listcell();
		Listcell poNumber = new Listcell();
		Listcell orderDate = new Listcell();
		Listcell billTo = new Listcell();
		Listcell shipTo = new Listcell();
		Listcell totalWaferQty = new Listcell();
		Listcell waferSize = new Listcell();
		Listcell orderNumber = new Listcell();
		Listcell orderStatus = new Listcell();
		
		customer.setLabel(smf.getCustomerName().trim());
		product.setLabel(smf.getOrderHeader().getProduct().trim());
		
		if(smf.getOrderHeader().getRealProduct()!=null){ //2017.12.20
			realProduct.setLabel(smf.getOrderHeader().getRealProduct().toString().trim());
		}else{
			realProduct.setLabel("");			
		}		 
		orderType.setLabel(smf.getOrderTypeName().trim());
		poNumber.setLabel(smf.getOrderHeader().getPoNumber().trim());
		orderDate.setLabel(DateFormatUtil.getDateTimeFormat().format(smf.getOrderHeader().getOrderDate()).toString().trim());
		billTo.setLabel(smf.getBillToName().trim());
		shipTo.setLabel(smf.getShipToName().trim());
		totalWaferQty.setLabel(String.valueOf(smf.getOrderHeader().getTotalWaferQty()).toString().trim());
		waferSize.setLabel(smf.getOrderHeader().getWaferSize().trim());
		orderNumber.setLabel(smf.getOrderHeader().getOrderNumber().trim());
		orderStatus.setLabel(smf.getOrderStatusName().trim());
		
		//Funtion Image
		Image imgcellDetail = new Image();
		Image imgcellHold = new Image();
		Image imgcellDelete = new Image();
		Label celllabel1=new Label();
		Label celllabel2=new Label();
		celllabel1.setValue(" ");
		celllabel2.setValue(" ");
		
		//Detail
		imgcellDetail.setId("cellDetail"+inIndex);
		imgcellDetail.setSrc("/images/icons/application_view_detail.png");
		imgcellDetail.setParent(no);
		imgcellDetail.setTooltiptext(Labels.getLabel("oe.query.btnDetail.tooltiptext"));
		imgcellDetail.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				String mode="newadd";
				if ("10".equals(smf.getOrderHeader().getOrderStatus())){
					mode="modify";
				}
				else if ("20".equals(smf.getOrderHeader().getOrderStatus())){
					mode="modify";//2013.03.07
				}
				else if ("30".equals(smf.getOrderHeader().getOrderStatus())){
					mode="readonly";
				}else if("40".equals(smf.getOrderHeader().getOrderStatus())){//OCF-PR-151002_補上orderStatus=40時，要readonly才讀的資料
					mode="readonly";
				}
				
				Map args = new HashMap();
				args.put("winid", "winOrderEntry");	
				args.put("mode", mode);
				args.put("orderQueryModel", smf);
				Window winimport = (Window)Executions.createComponents("/WEB-INF/modules/oe/OrderEntry.zul", null, args);
				winimport.doModal();
				onClick$btnSearch();
			}
			
		});
		
		//Future Hold
		imgcellHold.setId("cellFutureHold"+inIndex);
		imgcellHold.setSrc("/images/icons/lock_add.png");
		celllabel1.setParent(no);
		imgcellHold.setParent(no);
		celllabel2.setParent(no);
		imgcellHold.setTooltiptext(Labels.getLabel("oe.query.btnHold.tooltiptext"));
		if ("30".equals(smf.getOrderHeader().getOrderStatus())){//CLOSE,DELETE時，不可以Hold
			imgcellHold.setVisible(false);
		}
		else {
			imgcellHold.setVisible(true);
		}
		imgcellHold.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event inEvent) throws Exception {
				Map args = new HashMap();
				args.put("winid", "winFutureHold");
				args.put("orderNumber", smf.getOrderHeader().getOrderNumber());
				Window winimport = (Window)Executions.createComponents("/WEB-INF/modules/oe/Hold.zul", null, args);
				winimport.doModal();
			}
			
		});
		
		//Delete
		imgcellDelete.setId("imgcellDelete"+inIndex);
		imgcellDelete.setSrc("/images/icons/cancel.png");
		imgcellDelete.setParent(no);
		imgcellDelete.setTooltiptext(Labels.getLabel("oe.query.btnDelete.tooltiptext"));
		if ("10".equals(smf.getOrderHeader().getOrderStatus()) || "20".equals(smf.getOrderHeader().getOrderStatus()) ){
			//imgcellDelete.setDisabled(false);
			imgcellDelete.setVisible(true);
		}else{
			//imgcellDelete.setDisabled(true);
			imgcellDelete.setVisible(false);
		}
		imgcellDelete.addEventListener("onClick", new EventListener() {
			@Override
			public void onEvent(Event inEvent) throws Exception {				
				if ("10".equals(smf.getOrderHeader().getOrderStatus()) || "20".equals(smf.getOrderHeader().getOrderStatus())){
					
				}else{
					Messagebox.show(Labels.getLabel("oe.query.click.delete.error",
							new java.lang.Object[] {Labels.getLabel("oe.query.grdOrderData.colDataOrderStatus"),smf.getOrderStatusName()}),
							"Error", Messagebox.OK, Messagebox.ERROR);
					return;
				}
				
				if (chkDeleteOrderHoldFlag(smf)==false){
					return;
				}
				
				Messagebox.show(Labels.getLabel("common.message.deleteconfirm"), "Question", 
						Messagebox.OK | Messagebox.CANCEL, 
						Messagebox.QUESTION, 
						new org.zkoss.zk.ui.event.EventListener(){
						    public void onEvent(Event inEvt) throws InterruptedException {
						    	if ("onOK".equals(inEvt.getName())){
						    		 deleteOE(smf);
						    	}
						    }
						});
			}
			
		});
		
		no.setParent(inItem);
		customer.setParent(inItem);
		product.setParent(inItem);
		realProduct.setParent(inItem); //2017.12.20
		orderType.setParent(inItem);
		poNumber.setParent(inItem);
		orderDate.setParent(inItem);
		billTo.setParent(inItem);
		shipTo.setParent(inItem);
		totalWaferQty.setParent(inItem);
		waferSize.setParent(inItem);
		orderNumber.setParent(inItem);
		orderStatus.setParent(inItem);
		
	}

}
