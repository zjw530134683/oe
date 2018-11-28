/*
 * Project Name:iVision_PR150602
 * File Name:CreateOeListViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2015/11/16下午3:21:58
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.OrderInternalCheckInfo;
import com.tce.ivision.model.UiFieldParam;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.oe.model.CreateOeList;
import com.tce.ivision.modules.oe.model.OrderQueryModel;
import com.tce.ivision.modules.oe.service.OrderEntryService;

/**
 * ClassName: CreateOeListViewCtrl <br/>
 * date: 2015/11/16 下午3:21:58 <br/>
 *
 * @author 130707
 * @version 
 * @since JDK 1.6
 */
public class CreateOeListViewCtrl extends BaseViewCtrl implements ListitemRenderer{

	private Window winCreateOeList;
	private Listheader headerView;
	private Listheader headerWaferQty;
	private Listheader headerCustomerLotNo;
	private Listheader headerPoItem;
	private Listheader headerOrderNumber;
	private Listbox listboxCreateOeList;
	private Button btnExit;

	private List<CreateOeList> createOeLists = new ArrayList<CreateOeList>();
	private List<UiFieldParam> orderStatusUiFieldParams =  new ArrayList<UiFieldParam>();
	private List<UiFieldParam> orderTypeUiFieldParams = new ArrayList<UiFieldParam>();
	
	/**
	 * OrderEntryService
	 */
	private OrderEntryService orderEntryService = (OrderEntryService) SpringUtil.getBean("orderEntryService");
	/**
	 * CustomerInformationService
	 */
	private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil.getBean("customerInformationService");
	
	
	/**
	 *
	 *
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		createOeLists = (List<CreateOeList>) execution.getArg().get("createOeList");
		listboxCreateOeList.setModel(new ListModelList<CreateOeList>(createOeLists));
		listboxCreateOeList.setItemRenderer(this);
		listboxCreateOeList.renderAll();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		// TODO Auto-generated method stub
		
	}
	
	public void onClick$btnExit(){
		winCreateOeList.getParent().setAttribute("orderMode", "newadd");//回傳attribute到OrderQueryViewCtrl，若是直接按Create New Order，則回到OrderQueryViewCtrl時則不必觸發onClick$btnSearch
		winCreateOeList.detach();
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
		//OrderType
		List<UiFieldSet> orderTypes = commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.OrderQueryViewCtrl", "OQ_ORDER_TYPE");
		if (orderTypes.size()>0){
			orderTypeUiFieldParams=orderTypes.get(0).getUiFieldParams();
		}

		//OrderStatus
		List<UiFieldSet> orderStatuses = commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.OrderQueryViewCtrl", "OE_ORDER_STATUS");
		if (orderStatuses.size()>0){
			orderStatusUiFieldParams=orderStatuses.get(0).getUiFieldParams();
			log.debug(orderStatusUiFieldParams.size());
			for (int i=0;i<orderStatusUiFieldParams.size();i++){
				log.debug(orderStatusUiFieldParams.get(i).getMeaning());
			}
		}
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(Listitem inItem, Object inData, int inIndex) throws Exception {
		final CreateOeList createOeList = (CreateOeList) inData;
		
		Listcell cellOrderNumber = new Listcell();
		Listcell cellPoItem = new Listcell();
		Listcell cellCustomerLotNo = new Listcell();
		Listcell cellWaferQty = new Listcell();
		Listcell cellView = new Listcell();
		
		final Button btnView = new Button();
		
		cellOrderNumber.setId("cellOrderNumber" + inIndex);
		cellPoItem.setId("cellPoItem" + inIndex);
		cellCustomerLotNo.setId("cellCustomerLotNo" + inIndex);
		cellWaferQty.setId("cellWaferQty" + inIndex);
		btnView.setId("btnView" + inIndex);
		btnView.setLabel("View");
		
		
		//ORDER_NUMBER
		cellOrderNumber.setLabel(createOeList.getOrderNumber());
		
		//PO_ITEM
		cellPoItem.setLabel(createOeList.getPoItem());
		
		//CUSTOMER_LOTNO
		cellCustomerLotNo.setLabel(createOeList.getCustomerLotNo());
		
		//WAFER_QTY
		cellWaferQty.setLabel(createOeList.getWaferQty());
		
		//VIEW
		btnView.setParent(cellView);
		btnView.addEventListener("onClick", new EventListener() {
			public void onEvent(Event inEvent) throws Exception{
				OrderHeader orderHeader = orderEntryService.getOrderHeaderByOrderNumber(createOeList.getOrderNumber());
				
				OrderQueryModel orderQueryModel=new OrderQueryModel();
				if ("".equals(orderHeader.getCustomerId())){
					orderQueryModel.setCustomerName("");
				}
				else {
					orderQueryModel.setCustomerName(customerInformationService.getCustomerTableByCustomerId(orderHeader.getCustomerId()).getCustomerShortName());
				}

				if ("".equals(orderHeader.getOrderType())){
					orderQueryModel.setOrderTypeName("");
				}
				else{
					orderQueryModel.setOrderTypeName(getParaValueByMeaning(orderHeader.getOrderType(),orderTypeUiFieldParams));
				}

				if ("".equals(orderHeader.getOrderStatus())){
					orderQueryModel.setOrderStatusName("");
				}
				else{
					orderQueryModel.setOrderStatusName(getParaValueByMeaning(orderHeader.getOrderStatus(), orderStatusUiFieldParams));
				}

				if ("".equals(orderHeader.getBillTo())){
					orderQueryModel.setBillToName("");
				}
				else{
					orderQueryModel.setBillToName(customerInformationService.getCustomerTableByCustomerId(orderHeader.getBillTo()).getCustomerShortName());
				}

				if ("".equals(orderHeader.getShipTo())){
					orderQueryModel.setShipToName("");
				}
				else {
					orderQueryModel.setShipToName(customerInformationService.getCustomerTableByCustomerId(orderHeader.getShipTo()).getCustomerShortName());
				}

				orderQueryModel.setOrderHeader(orderHeader);

				Map args = new HashMap();
				args.put("winid", "winOrderEntry");	
				args.put("mode", "modify");
				args.put("orderQueryModel", orderQueryModel);
				Window winimport = (Window)Executions.createComponents("/WEB-INF/modules/oe/OrderEntry.zul", null, args);
				winimport.doModal();
			}
		});
		
		btnView.setParent(cellView);
		
		
		cellOrderNumber.setParent(inItem);
		cellPoItem.setParent(inItem);
		cellCustomerLotNo.setParent(inItem);
		cellWaferQty.setParent(inItem);
		cellView.setParent(inItem);
	}
}
