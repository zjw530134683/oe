/*
 * Project Name:iVision_PR150202
 * File Name:OeInternalInfoViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2015/5/21下午1:20:15
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
import org.zkoss.zul.Caption;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.EmplInfo;
import com.tce.ivision.model.OrderInternalCheckInfo;
import com.tce.ivision.model.WaferManagementHistory;
import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.oe.model.OrderEntryLotnoModel;
import com.tce.ivision.modules.oe.service.OrderEntryService;
import com.tce.ivision.units.common.DateFormatUtil;

/**
 * ClassName: OeInternalInfoViewCtrl <br/>
 * date: 2015/5/21 下午1:20:15 <br/>
 *
 * @author 130707
 * @version 
 * @since JDK 1.6
 */
public class OeInternalInfoViewCtrl extends BaseViewCtrl implements ListitemRenderer {

	private Window winOeInternalInfo;
	private Button btnExit;
	private Button btnSave;
	private Button btnModify;
	private Listheader headerConfirmCreateDate;
	private Listheader headerConfirmUpdateDate;
	private Listheader headerConfirmUser;
	private Listheader headerConfirmReason;
	private Listheader headerCheckRule;
	private Listbox listboxOeCheckRuleConfirmInfo;
	private Label lblOeCheckRuleConfirmInfoTitle;
	private Label edtNoticeConfirmCreateDate;
	private Label edtNoticeConfirmUpdateDate;
	private Label edtNoticeConfirmUser;
	private Textbox edtNotice;
	private Button btnWaferSelection;
	private Textbox edtTargetWaferData;
	private Label lblNoticeConfirmCreateDate;
	private Label lblNoticeConfirmUpdateDate;
	private Label lblNoticeConfirmUser;
	private Label lblNotice;
	private Label lblTargetWaferData;
	private Caption OeInternalNoticeTitle;
	private Image imgCheckRuleInfo;
	private Window winCheckRulePopup;

	public Textbox selectWaferData = new Textbox();
	public OrderEntryLotnoModel orderEntryLotnoModel = new OrderEntryLotnoModel();
	public List<OrderInternalCheckInfo> orderInternalCheckInfoNoticeList = new ArrayList<OrderInternalCheckInfo>();//處理Notice的ArrayList
	public List<OrderInternalCheckInfo> orderInternalCheckInfoCheckRuleList = new ArrayList<OrderInternalCheckInfo>();//處理Check Rule的ArrayList
	public String mode="";
	
	/**
	 * OrderEntryService
	 */
	private OrderEntryService orderEntryService = (OrderEntryService) SpringUtil.getBean("orderEntryService");
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

		edtTargetWaferData.setReadonly(true);
		edtTargetWaferData.setDisabled(true);
		edtTargetWaferData.setStyle("color:BLUE !important;");
		edtNotice.setDisabled(true);

		selectWaferData = (Textbox) execution.getArg().get("waferData");
		orderEntryLotnoModel = (OrderEntryLotnoModel) execution.getArg().get("OeData");

		if(orderEntryLotnoModel != null && orderEntryLotnoModel.getOrderLineLotno() != null){
			if((orderEntryLotnoModel.getOrderLineLotno().getOrderNumber() != null && !"".equals(orderEntryLotnoModel.getOrderLineLotno().getOrderNumber())) && (orderEntryLotnoModel.getOrderLineLotno().getCustomerLotno() != null && !"".equals(orderEntryLotnoModel.getOrderLineLotno().getCustomerLotno()))){
				orderInternalCheckInfoNoticeList = orderEntryService.getOrderInternalCheckInfoNoticeByOrderNumberAndCustomerLotNo(orderEntryLotnoModel.getOrderLineLotno().getOrderNumber(), orderEntryLotnoModel.getOrderLineLotno().getCustomerLotno());
				orderInternalCheckInfoCheckRuleList = orderEntryService.getOrderInternalCheckInfoCheckRuleByOrderNumberAndCustomerLotNo(orderEntryLotnoModel.getOrderLineLotno().getOrderNumber(), orderEntryLotnoModel.getOrderLineLotno().getCustomerLotno());

				//若資料庫已經有Notice的資料，將orderInternalCheckInfoNoticeList的資料放入相對應欄位中
				if(orderInternalCheckInfoNoticeList.size() > 0){
					for(int i=0; i<orderInternalCheckInfoNoticeList.size(); i++){
						if(orderInternalCheckInfoNoticeList.get(i).getOeNoticeUpdateDate() != null){
							edtNotice.setText(orderInternalCheckInfoNoticeList.get(i).getOeNotice());
							edtTargetWaferData.setText(orderInternalCheckInfoNoticeList.get(i).getOeNoticeWaferData());
							if(orderInternalCheckInfoNoticeList.get(i).getOeNoticeUpdateUser() != null && !"".equals(orderInternalCheckInfoNoticeList.get(i).getOeNoticeUpdateUser())){
								EmplInfo noticeConfirmUser = userService.getEmplInfoByEmplId(orderInternalCheckInfoNoticeList.get(i).getOeNoticeUpdateUser());
								if(noticeConfirmUser != null){
									edtNoticeConfirmUser.setValue(noticeConfirmUser.getEmplFamilyname()+noticeConfirmUser.getEmplFirstname());
								}
							}
							if(orderInternalCheckInfoNoticeList.get(i).getOeNoticeUpdateDate() != null){
								edtNoticeConfirmUpdateDate.setValue(DateFormatUtil.getDateTimeFormatHHmm().format(orderInternalCheckInfoNoticeList.get(i).getOeNoticeUpdateDate()));
							}
							if(orderInternalCheckInfoNoticeList.get(i).getOeNoticeCreateDate() != null){
								edtNoticeConfirmCreateDate.setValue(DateFormatUtil.getDateTimeFormatHHmm().format(orderInternalCheckInfoNoticeList.get(i).getOeNoticeCreateDate()));
							}
							break;
						}
					}

				}
				
				//若資料庫內有Check Rule的資料，將orderInternalCheckInfoCheckRuleList放入listboxOeCheckRuleConfirmInfo
				listboxOeCheckRuleConfirmInfo.setModel	(new ListModelList<OrderInternalCheckInfo>(orderInternalCheckInfoCheckRuleList));
				listboxOeCheckRuleConfirmInfo.setItemRenderer(this);
				listboxOeCheckRuleConfirmInfo.renderAll();
			}
		}
	}


	public void onClick$btnExit(){
		winOeInternalInfo.detach();
	}


	public void onClick$btnSave(){
		Messagebox.show(Labels.getLabel("oe.save.confirm"), "Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener(){
			public void onEvent(Event inEvt) throws InterruptedException,Exception {
				if ("onOK".equals(inEvt.getName())){
					boolean saveFlag=false;

					//處理Notice的Save作業
					if(orderInternalCheckInfoNoticeList.size() > 0){
						for(int i=0; i<orderInternalCheckInfoNoticeList.size(); i++){
							if("10".equals(orderEntryLotnoModel.getOrderHeaderOrderStatus()) || "20".equals(orderEntryLotnoModel.getOrderHeaderOrderStatus())){//若20的話代表已經是PRODUCTION；若10的話代表是BOOKED狀態，因此修改Noice直接存到[ORDER_INTERNAL_CHECK_INFO]裡
								if("NewAdd".equals(mode)){
									orderEntryService.insertOrderInternalCheckInfo(orderInternalCheckInfoNoticeList);
									saveFlag=true;
								}else if("Modify".equals(mode)){
									orderEntryService.updateOrderInternalCheckInfo(orderInternalCheckInfoNoticeList);
									saveFlag=true;
								}
							}else{//若非10 or 20的狀態，則代表是Create New Order，則需傳回Order Entry畫面再一起儲存才能取得ORDER_LINE_LOTNO的資訊(ORDER_LINE_LOTNO_IDX, ORDER_NUMBER)
								orderEntryLotnoModel.getOrderLineLotno().setOrderInternalCheckInfo(orderInternalCheckInfoNoticeList);
								saveFlag=true;
							}
						}
					}

					//處理Check Rule修改Confirm Reason的Save作業(Check Rule修改Confirm Reason是在先前OE檢查到且輸入過Comment存入的，因此在此修改一定是update)
					if(orderInternalCheckInfoCheckRuleList.size() > 0){
						orderEntryService.updateOrderInternalCheckInfoModifyReason(orderInternalCheckInfoCheckRuleList);
						saveFlag=true;
					}

					if(saveFlag){
						Messagebox.show(Labels.getLabel("oe.save.message.OeInternalCheckInfoSaveOk"), "Information", Messagebox.OK, Messagebox.INFORMATION);
					}

					edtNotice.setDisabled(true);
					if(listboxOeCheckRuleConfirmInfo.getItemCount() > 0){
						for(int i=0; i<listboxOeCheckRuleConfirmInfo.getItemCount(); i++){
							Textbox edtConfirmReason = (Textbox)component.getFellow("edtConfirmReason"+String.valueOf(i));
							edtConfirmReason.setDisabled(true);
						}
					}
				}
			}
		});
	}


	public void onClick$btnModify(){
		edtNotice.setDisabled(false);
		
		if(listboxOeCheckRuleConfirmInfo.getItemCount() > 0){
			for(int i=0; i<listboxOeCheckRuleConfirmInfo.getItemCount(); i++){
				Textbox edtConfirmReason = (Textbox)component.getFellow("edtConfirmReason"+String.valueOf(i));
				edtConfirmReason.setDisabled(false);
			}
		}
	}


	public void onClick$btnWaferSelection(){
		Map args = new HashMap();
		edtTargetWaferData.setFocus(false);
		edtTargetWaferData.setConstraint("");
		args.put("waferData", edtTargetWaferData);
		args.put("selectWaferData", selectWaferData);
		Window winWaferSelect = (Window)Executions.createComponents("/WEB-INF/modules/wafer/WaferSelection.zul", null, args);
		winWaferSelect.doModal();
		Label selectWaferData = (Label) winWaferSelect.getFellow("lblWaferData");
		edtTargetWaferData.setText("");
		edtTargetWaferData.setText(selectWaferData.getValue());
	}

	
	public void onChange$edtNotice(){
		if(!"".equals(edtNotice.getText())){
			if(orderInternalCheckInfoNoticeList.size() > 0){
				for(int i=0; i<orderInternalCheckInfoNoticeList.size(); i++){
					if(orderInternalCheckInfoNoticeList.get(i).getOeNoticeUpdateDate() != null){
						orderInternalCheckInfoNoticeList.get(i).setOeNotice(edtNotice.getText());
						orderInternalCheckInfoNoticeList.get(i).setOeNoticeCreateDate(new Date());
						orderInternalCheckInfoNoticeList.get(i).setOeNoticeUpdateDate(new Date());
						orderInternalCheckInfoNoticeList.get(i).setOeNoticeUpdateUser(loginId);
						orderInternalCheckInfoNoticeList.get(i).setOeNoticeWaferData(edtTargetWaferData.getText());
						
						mode="Modify";
						break;
					}
				}
			}else{
				OrderInternalCheckInfo orderInternalCheckInfo = new OrderInternalCheckInfo();
				orderInternalCheckInfo.setOeNotice(edtNotice.getText());
				orderInternalCheckInfo.setOeNoticeCreateDate(new Date());
				orderInternalCheckInfo.setOeNoticeUpdateDate(new Date());
				orderInternalCheckInfo.setOeNoticeUpdateUser(loginId);
				orderInternalCheckInfo.setOeNoticeWaferData(edtTargetWaferData.getText());
				orderInternalCheckInfo.setOrderLineLotno(orderEntryLotnoModel.getOrderLineLotno());
				orderInternalCheckInfo.setOrderNumber(orderEntryLotnoModel.getOrderLineLotno().getOrderNumber());
				orderInternalCheckInfo.setCustomerLotno(orderEntryLotnoModel.getOrderLineLotno().getCustomerLotno());
				orderInternalCheckInfo.setWaferData(orderEntryLotnoModel.getOrderLineLotno().getWaferData());
				orderInternalCheckInfo.setCreateDate(new Date());
				orderInternalCheckInfo.setCreateUser(loginId);
				
				orderInternalCheckInfoNoticeList.add(orderInternalCheckInfo);
				mode="NewAdd";
			}
		}
	}
	
	
	public void onClick$imgCheckRuleInfo(){
		winCheckRulePopup = (Window)Executions.createComponents("/WEB-INF/modules/oe/OeInternalCheckRulePopup.zul", null, null);	
		winCheckRulePopup.doHighlighted();
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
		final OrderInternalCheckInfo orderInternalCheckInfo = (OrderInternalCheckInfo) inData;

		Listcell cellCheckRule = new Listcell();
		Listcell cellConfirmReason = new Listcell();
		Listcell cellConfirmUser = new Listcell();
		Listcell cellConfirmUpdateDate = new Listcell();
		Listcell cellConfirmCreateDate = new Listcell();

		final Textbox edtConfirmReason = new Textbox();

		cellCheckRule.setId("cellCheckRule"+inIndex);
		cellConfirmReason.setId("cellConfirmReason"+inIndex);
		cellConfirmUser.setId("cellConfirmUser"+inIndex);
		cellConfirmUpdateDate.setId("cellConfirmUpdateDate"+inIndex);
		cellConfirmCreateDate.setId("cellConfirmCreateDate"+inIndex);
		edtConfirmReason.setId("edtConfirmReason"+inIndex);


		//CHECK RULE
		if(orderInternalCheckInfo.getOeCheck1ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck1ConfirmReason())){
			cellCheckRule.setLabel("Check[1]");
		}else if(orderInternalCheckInfo.getOeCheck2ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck2ConfirmReason())){
			cellCheckRule.setLabel("Check[2]");
		}else if(orderInternalCheckInfo.getOeCheck3ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck3ConfirmReason())){
			cellCheckRule.setLabel("Check[3]");
		}

		//CONFIRM REASON
		edtConfirmReason.setDisabled(true);
		edtConfirmReason.setWidth("98%");
		if(orderInternalCheckInfo.getOeCheck1ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck1ConfirmReason())){
			edtConfirmReason.setText(orderInternalCheckInfo.getOeCheck1ConfirmReason());
		}else if(orderInternalCheckInfo.getOeCheck2ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck2ConfirmReason())){
			edtConfirmReason.setText(orderInternalCheckInfo.getOeCheck2ConfirmReason());
		}else if(orderInternalCheckInfo.getOeCheck3ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck3ConfirmReason())){
			edtConfirmReason.setText(orderInternalCheckInfo.getOeCheck3ConfirmReason());
		}
		edtConfirmReason.setParent(cellConfirmReason);
		edtConfirmReason.addEventListener("onChange", new EventListener() {
			public void onEvent(Event inEvent) throws Exception{
				if("".equals(edtConfirmReason.getText().trim())){
					Messagebox.show(Labels.getLabel("oe.save.message.confirmReasonEmpty"), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
					return;
				}
				
				if(orderInternalCheckInfo.getOeCheck1ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck1ConfirmReason())){
					orderInternalCheckInfo.setOeCheck1ConfirmReason(edtConfirmReason.getText());
					orderInternalCheckInfo.setOeCheck1ConfirmCreateDate(new Date());
					orderInternalCheckInfo.setOeCheck1ConfirmUpdateDate(new Date());
					orderInternalCheckInfo.setOeCheck1ConfirmUser(loginId);
				}else if(orderInternalCheckInfo.getOeCheck2ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck2ConfirmReason())){
					orderInternalCheckInfo.setOeCheck2ConfirmReason(edtConfirmReason.getText());
					orderInternalCheckInfo.setOeCheck2ConfirmCreateDate(new Date());
					orderInternalCheckInfo.setOeCheck2ConfirmUpdateDate(new Date());
					orderInternalCheckInfo.setOeCheck2ConfirmUser(loginId);
				}else if(orderInternalCheckInfo.getOeCheck3ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck3ConfirmReason())){
					orderInternalCheckInfo.setOeCheck3ConfirmReason(edtConfirmReason.getText());
					orderInternalCheckInfo.setOeCheck3ConfirmCreateDate(new Date());
					orderInternalCheckInfo.setOeCheck3ConfirmUpdateDate(new Date());
					orderInternalCheckInfo.setOeCheck3ConfirmUser(loginId);
				}
				mode="Modify";
			}
		});

		//CONFIRM USER
		EmplInfo confirmUser;
		if(orderInternalCheckInfo.getOeCheck1ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck1ConfirmReason())){
			confirmUser = userService.getEmplInfoByEmplId(orderInternalCheckInfo.getOeCheck1ConfirmUser());
			if(confirmUser != null){
				cellConfirmUser.setLabel(confirmUser.getEmplFamilyname()+confirmUser.getEmplFirstname());
			}
		}else if(orderInternalCheckInfo.getOeCheck2ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck2ConfirmReason())){
			confirmUser = userService.getEmplInfoByEmplId(orderInternalCheckInfo.getOeCheck2ConfirmUser());
			if(confirmUser != null){
				cellConfirmUser.setLabel(confirmUser.getEmplFamilyname()+confirmUser.getEmplFirstname());
			}
		}else if(orderInternalCheckInfo.getOeCheck3ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck3ConfirmReason())){
			confirmUser = userService.getEmplInfoByEmplId(orderInternalCheckInfo.getOeCheck3ConfirmUser());
			if(confirmUser != null){
				cellConfirmUser.setLabel(confirmUser.getEmplFamilyname()+confirmUser.getEmplFirstname());
			}
		}

		//CONFIRM UPDATE DATE
		if(orderInternalCheckInfo.getOeCheck1ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck1ConfirmReason())){
			cellConfirmUpdateDate.setLabel(DateFormatUtil.getDateTimeFormatHHmm().format(orderInternalCheckInfo.getOeCheck1ConfirmUpdateDate()));
		}else if(orderInternalCheckInfo.getOeCheck2ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck2ConfirmReason())){
			cellConfirmUpdateDate.setLabel(DateFormatUtil.getDateTimeFormatHHmm().format(orderInternalCheckInfo.getOeCheck2ConfirmUpdateDate()));
		}else if(orderInternalCheckInfo.getOeCheck3ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck3ConfirmReason())){
			cellConfirmUpdateDate.setLabel(DateFormatUtil.getDateTimeFormatHHmm().format(orderInternalCheckInfo.getOeCheck3ConfirmUpdateDate()));
		}

		//CONFIRM CREATE DATE
		if(orderInternalCheckInfo.getOeCheck1ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck1ConfirmReason())){
			cellConfirmCreateDate.setLabel(DateFormatUtil.getDateTimeFormatHHmm().format(orderInternalCheckInfo.getOeCheck1ConfirmCreateDate()));
		}else if(orderInternalCheckInfo.getOeCheck2ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck2ConfirmReason())){
			cellConfirmCreateDate.setLabel(DateFormatUtil.getDateTimeFormatHHmm().format(orderInternalCheckInfo.getOeCheck2ConfirmCreateDate()));
		}else if(orderInternalCheckInfo.getOeCheck3ConfirmReason() != null && !"".equals(orderInternalCheckInfo.getOeCheck3ConfirmReason())){
			cellConfirmCreateDate.setLabel(DateFormatUtil.getDateTimeFormatHHmm().format(orderInternalCheckInfo.getOeCheck3ConfirmCreateDate()));
		}

		cellCheckRule.setParent(inItem);
		cellConfirmReason.setParent(inItem);
		cellConfirmUser.setParent(inItem);
		cellConfirmUpdateDate.setParent(inItem);
		cellConfirmCreateDate.setParent(inItem);
	}
}
