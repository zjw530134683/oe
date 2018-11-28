/*
 * Project Name:iVision
 * File Name:HoldViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2013/1/16上午11:50:42
 * 
 * 說明:
 * Order Query -- 新增 Hold function
 * 包含Hold and Release function
 * 
 * 修改歷史:
 * 2013.01.15 OCF#OE002 Fanny Initial
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.BaseCtrlSet;
import com.tce.ivision.model.EmplInfo;
import com.tce.ivision.model.Hold;
import com.tce.ivision.model.HoldWafer;
import com.tce.ivision.model.LotInfo;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferInfo;
import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.oe.model.HoldModel;
import com.tce.ivision.modules.oe.service.OrderEntryService;
import com.tce.ivision.modules.oe.service.OrderSchedulingService;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.ZkComboboxControl;
import com.tce.ivision.units.common.service.CommonService;

/**
 * ClassName: HoldViewCtrl <br/>
 * date: 2013/1/16 上午11:50:42 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class HoldViewCtrl extends BaseViewCtrl implements ListitemRenderer{
	
	/**
	 * Logger
	 */
	public static Logger log = Logger.getLogger(OrderEntryViewCtrl.class);
	
	//Global var
	/**
	 * Login in User
	 */	
	String userId="030260";
	
	/**
	 * Login User的Operation Unit
	 */
	String operationUnit="01";
	
	/**
	 * Login User Dept
	 */
	String userDept="IT";
	
	//data from parent
	/**
	 * 從前一個畫面傳來的orderNumber
	 */
	String orderNumber="";

	/**
	 * zk component:Window winHold;
	 */
	private Window winHold;
	
	/**
	 * zk component:Button btnSave
	 */
	private Button btnSave;
	
	/**
	 * zk component:Listheader colHoldHoldIssueDate
	 */
	private Listheader colHoldHoldIssueDate;
	
	/**
	 * zk component:Listheader colHoldHoldUser
	 */
	private Listheader colHoldHoldUser;
	
	/**
	 * zk component:Listheader colHoldHoldComment
	 */
	private Listheader colHoldHoldComment;
	
	/**
	 * zk component:Listheader colHoldHoldReason
	 */
	private Listheader colHoldHoldReason;
	
	/**
	 * zk component:Listheader colHoldHoldProcessName
	 */
	private Listheader colHoldHoldProcessName;
	
	/**
	 * zk component:Listheader colHoldHoldType
	 */
	private Listheader colHoldHoldType;
	
	/**
	 * zk component:Listheader colHoldLotInfo
	 */
	private Listheader colHoldLotInfo;
	
	/**
	 * zk component:Listheader colHoldWaferQty
	 */
	private Listheader colHoldWaferQty;
	
	/**
	 * zk component:Listheader colHoldLotno
	 */
	private Listheader colHoldLotno;
	
	/**
	 * zk component:Listheader colHoldPoItem
	 */
	private Listheader colHoldPoItem;
	
	/**
	 * zk component:Listheader colHold
	 */
	private Listheader colHold;
	
	/**
	 * zk component:Listhead listheadLine
	 */
	private Listhead listheadLine;
	
	/**
	 * zk component:Listbox grdLotno
	 */
	private Listbox grdLotno;
	
	/**
	 * zk component:Combobox cbxGHoldReason
	 */
	private Combobox cbxGHoldReason;
	
	/**
	 * zk component:Label lblGHoldReason
	 */
	private Label lblGHoldReason;
	
	/**
	 * zk component:Combobox cbxGHoldProcessName
	 */
	private Combobox cbxGHoldProcessName;
	
	/**
	 * zk component:Label lblGHoldProcessName
	 */
	private Label lblGHoldProcessName;
	
	/**
	 * zk component:Combobox cbxGHoldType
	 */
	private Combobox cbxGHoldType;
	
	/**
	 * zk component:Label lblGHoldType
	 */
	private Label lblGHoldType;
	
	/**
	 * zk component:Caption grbGlobalSetting
	 */
	private Caption grbGlobalSetting;
	
	/**
	 * zk component:Textbox edtLineOrderNumber
	 */
	private Textbox edtLineOrderNumber;
	
	/**
	 * zk component:Label lblLineOrderNumber
	 */
	private Label lblLineOrderNumber;
	
	/**
	 * zk component:Textbox edtLineCustomerPo
	 */
	private Textbox edtLineCustomerPo;
	
	/**
	 * zk component:Label lblLineCustomerPo
	 */
	private Label lblLineCustomerPo;
	
	/**
	 * zk component:Textbox edtLineProduct
	 */
	private Textbox edtLineProduct;
	
	/**
	 * zk component:Label lblLineProduct
	 */
	private Label lblLineProduct;
	
	/**
	 * zk component:Textbox edtLinePoNumber
	 */
	private Textbox edtLinePoNumber;
	
	/**
	 * zk component:Label lblLinePONumber
	 */
	private Label lblLinePONumber;
	
	/**
	 * zk component:Caption grbHeaderData
	 */
	private Caption grbHeaderData;
	
	/**
	 * zk component:Checkbox chbSelectAll
	 */
	private Checkbox chbSelectAll;
	
	/**
	 * zk component:Label lblGHoldComment
	 */
	private Label lblGHoldComment;
	
	/**
	 * zk component:Textbox edtHoldComment
	 */
	private Combobox edtGHoldComment;
	
	/**
	 * zk component:btnGHoldCommentOK
	 */
	private Image imgGHoldCommentOK;
	
	private Listheader colHoldAllWafer;//IT-PR-141008_Allison add
	
	private Listheader colHoldCustomerJob;//IT-PR-141008_Allison add
	
	private Listheader colHoldLockComment;//IT-PR-141008_Allison add
	
	private Listheader colHoldWaferData;//IT-PR-141008_Allison add
	
	private Textbox edtGLockComment;//IT-PR-141008_Allison add
	
	private Button btnSearch;//IT-PR-141008_為了讓Hold By WaferNo的畫面要回來時可以Refresh畫面，故弄一個隱藏的Search按鈕_Allison add
	
	//Spring
	/**
	 * OrderEntryService
	 */
	private OrderEntryService orderEntryService = (OrderEntryService) SpringUtil.getBean("orderEntryService");
	
	/**
	 * CommonService
	 */
	private CommonService commonService = (CommonService) SpringUtil.getBean("commonService");
	
	/**
	 * CustomerInformationService
	 */
	private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil.getBean("customerInformationService");
	
	/**
	 * OrderSchedulingService
	 */
	private OrderSchedulingService orderSchedulingService = (OrderSchedulingService) SpringUtil.getBean("orderSchedulingService");
	
	/**
	 * UserService
	 */
	private UserService userService = (UserService) SpringUtil.getBean("userService");//IT-PR-141008_Allison add
	
	//java bean
	/**
	 * 依據orderNumber找出來的OrderHeader orderHeader
	 */
	private OrderHeader orderHeader;
	
	/**
	 * 依據orderNumber找出來的List<OrderLineLotno> orderLineLotnos
	 */
	private List<OrderLineLotno> orderLineLotnos;
	
	/**
	 * PARA_TYPE=HOLD_PROCESS_NAME 的UiFieldSet
	 */
	private List<UiFieldSet> holdProcessNames;
	
	/**
	 * PARA_TYPE=HOLD_REASON 的UiFieldSet
	 */
	private List<UiFieldSet> holdReasons;
	
	/**
	 * PARA_TYPE=HOLD_TYPE 的UiFieldSet
	 */
	private List<UiFieldSet> holdTypes;
	
	/**
	 * 準備要跟DB做存取的List
	 */
	private List<HoldModel> holdModels;
	
	/**
	 * PARA_TYPE=CUSTOMER_HOLD & TOPPAN_HOLD 的UiFieldSet
	 */
	private List<UiFieldSet> holdComments;
	
	private List<UiFieldSet> holdCommentAlls;
	
	
	/**
	 *
	 *
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		userId=loginId;
		operationUnit=OU;
		btnSave.setImage("/images/icons/disk.png");
		imgGHoldCommentOK.setSrc("/images/icons/accept.png");
		imgGHoldCommentOK.setTooltiptext(Labels.getLabel("hold.imgGHoldCommentOK.tooltiptext"));
		
		//data from parent
		orderNumber=(String) execution.getArg().get("orderNumber");
		btnSearch.setVisible(false);
		this.getThisOrderNumberData();
		EmplInfo emplInfos = userService.getEmplInfoByEmplId(userId);
		userDept = emplInfos.getDeptCode();
		userDept = commonService.getUiFieldParamValueByMeaning("com.tce.ivision.modules.as.ctrl.UserDefineViewCtrl", "DEPARTMENT", emplInfos.getDeptCode());
		//log.debug(userDept);
		edtGLockComment.setDisabled(true);
	}
	
	/**
	 * 
	 * getThisOrderNumberData:依據orderNumber找出ORDER_HEADER,ORDER_LINE_LOTNO. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void getThisOrderNumberData(){
		//Header
		orderHeader=orderEntryService.getOrderHeaderByOrderNumber(orderNumber);
		if (orderHeader!=null){
			edtLinePoNumber.setText(orderHeader.getPoNumber());
			edtLineProduct.setText(orderHeader.getProduct());
			edtLineCustomerPo.setText(orderHeader.getCustomerPo());
			edtLineOrderNumber.setText(orderHeader.getOrderNumber());
			
			//Lotno
			holdModels=new ArrayList<HoldModel>();
			List<OrderLineLotno> orderLineLotnos=orderEntryService.getOrderLineLotnosByOrderNumber(orderNumber);
			if (orderLineLotnos.size()>0){
				for (int i=0;i<orderLineLotnos.size();i++){
					HoldModel tmpHoldModel=new HoldModel();
					tmpHoldModel.setOrderLineLotno(orderLineLotnos.get(i));
					tmpHoldModel.setLotno(orderSchedulingService.getLotnoforOEHoldUsed(orderHeader.getPoNumber(), orderLineLotnos.get(i).getCustomerLotno()));
					tmpHoldModel.setShippingFlag(orderSchedulingService.getShippingFlagforOEHoldUsed(orderHeader.getPoNumber(), orderLineLotnos.get(i).getCustomerLotno()));
					//tmpHoldModel.setAllWaferFlag();
					if("".equals(orderLineLotnos.get(i).getCustomerJob()) || orderLineLotnos.get(i).getCustomerJob() == null){
						tmpHoldModel.setCustomerJob("");
					}else{
						tmpHoldModel.setCustomerJob(orderLineLotnos.get(i).getCustomerJob());
					}
					tmpHoldModel.setWaferDataFlag(true);
					Hold tmpHold=orderEntryService.getHold(orderNumber, orderLineLotnos.get(i).getPoItem(), orderLineLotnos.get(i).getCustomerLotno());
					
					if(tmpHold!=null){
						if (tmpHold.getReleaseFlag()){
							if("10".equals(tmpHold.getHoldReason())){
								holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "CUSTOMER_HOLD");
							}else{
								holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "TOPPAN_HOLD");
							}
							
							List<HoldWafer> tmpHoldWafer=orderEntryService.getHoldWafer(tmpHold.getHoldIdx());
							tmpHoldModel.setHoldTypeName("");
							tmpHoldModel.setHoldProcessNameName("");
							tmpHoldModel.setHoldReasonName("");
							tmpHoldModel.setHoldComment("");
							tmpHoldModel.setHold(tmpHold);
							tmpHoldModel.setLockComment("");//IT-PR-141008_Allison add
							tmpHoldModel.setHoldWafers(tmpHoldWafer);//IT-PR-141008_Allison add
							tmpHoldModel.setAllWaferFlag(false);
							tmpHoldModel.setSelected(false);
						}else{
							if("10".equals(tmpHold.getHoldReason())){
								holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "CUSTOMER_HOLD");
							}else{
								holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "TOPPAN_HOLD");
							}
							List<HoldWafer> tmpHoldWafer=orderEntryService.getHoldWafer(tmpHold.getHoldIdx());
							tmpHoldModel.setHoldTypeName(this.getParaValueByMeaning(tmpHold.getHoldType(), holdTypes.get(0).getUiFieldParams()));
							tmpHoldModel.setHoldProcessNameName(this.getParaValueByMeaning(tmpHold.getHoldProcessName(), holdProcessNames.get(0).getUiFieldParams()));
							tmpHoldModel.setHoldReasonName(this.getParaValueByMeaning(tmpHold.getHoldReason(), holdReasons.get(0).getUiFieldParams()));
							tmpHoldModel.setHoldComment(this.getParaValueByMeaning(tmpHold.getHoldComment(), holdComments.get(0).getUiFieldParams()));
							tmpHoldModel.setHold(tmpHold);
							tmpHoldModel.setLockComment(tmpHold.getLockComment());//IT-PR-141008_Allison add
							tmpHoldModel.setHoldWafers(tmpHoldWafer);//IT-PR-141008_Allison add
							tmpHoldModel.setAllWaferFlag(tmpHold.getAllWafersFlag());
							tmpHoldModel.setSelected(true);
						}
					}else{
						tmpHoldModel.setHoldTypeName("");
						tmpHoldModel.setHoldProcessNameName("");
						tmpHoldModel.setHoldReasonName("");
						tmpHoldModel.setHoldComment("");
						tmpHoldModel.setHold(null);
						tmpHoldModel.setHoldWafers(null);//IT-PR-141008_Allison add
						tmpHoldModel.setLockComment("");//IT-PR-141008_Allison add
						tmpHoldModel.setAllWaferFlag(false);//IT-PR-141008_Allison add
						tmpHoldModel.setSelected(false);
					}
										
					holdModels.add(tmpHoldModel);
				}//end for i
				
				grdLotno.setMultiple(false);
				grdLotno.setModel(new ListModelList(holdModels));
				grdLotno.setItemRenderer(this);
				grdLotno.setMultiple(true);
			}//end if orderLineLotnos.size()
		}
	}

	/**
	 * 設定畫面上的Label naming.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		//window
		winHold.setTitle(Labels.getLabel("hold.winHold"));
		
		//groupbox
		grbHeaderData.setLabel(Labels.getLabel("oe.edit.line.grbHeaderData"));
		grbGlobalSetting.setLabel(Labels.getLabel("hold.grbGlobalSetting"));
		
		//checkbox
		chbSelectAll.setLabel(Labels.getLabel("hold.chbSelectAll"));
		
		//Label
		lblLinePONumber.setValue(Labels.getLabel("oe.edit.line.lblLinePONumber"));
		lblLineProduct.setValue(Labels.getLabel("oe.edit.line.lblLineProduct"));
	    lblLineCustomerPo.setValue(Labels.getLabel("oe.edit.line.lblLineCustomerPo"));
		lblLineOrderNumber.setValue(Labels.getLabel("oe.edit.line.lblLineOrderNumber"));
		lblGHoldType.setValue(Labels.getLabel("hold.lblGHoldType"));
		lblGHoldProcessName.setValue(Labels.getLabel("hold.lblGHoldProcessName"));
		lblGHoldReason.setValue(Labels.getLabel("hold.lblGHoldReason"));
		lblGHoldComment.setValue(Labels.getLabel("hold.lblGHoldComment"));
		
		//grdLotno field name
		colHold.setLabel(Labels.getLabel("hold.colHold"));
		colHoldPoItem.setLabel(Labels.getLabel("hold.colHoldPoItem"));
		colHoldLotno.setLabel(Labels.getLabel("hold.colHoldLotno"));
		colHoldWaferQty.setLabel(Labels.getLabel("hold.colHoldWaferQty"));
		colHoldHoldType.setLabel(Labels.getLabel("hold.colHoldHoldType"));
		colHoldHoldProcessName.setLabel(Labels.getLabel("hold.colHoldHoldProcessName"));
		colHoldHoldReason.setLabel(Labels.getLabel("hold.colHoldHoldReason"));
		colHoldHoldComment.setLabel(Labels.getLabel("hold.colHoldHoldComment"));
		colHoldLockComment.setLabel(Labels.getLabel("hold.colHoldLockComment"));
		colHoldHoldUser.setLabel(Labels.getLabel("hold.colHoldHoldUser"));
		colHoldHoldIssueDate.setLabel(Labels.getLabel("hold.colHoldHoldIssueDate"));
		colHoldLotInfo.setLabel(Labels.getLabel("hold.colHoldLotInfo"));
		colHoldAllWafer.setLabel(Labels.getLabel("hold.colHoldAllWafer"));
		colHoldCustomerJob.setLabel(Labels.getLabel("hold.colHoldCustomerJob"));
		colHoldWaferData.setLabel(Labels.getLabel("hold.colHoldWaferData"));
		
		//Button
		btnSave.setLabel(Labels.getLabel("hold.btnSave"));
	}

	/**
	 * 設置Combobox的選單內容.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
		//Hold Process Name
		holdProcessNames=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "HOLD_PROCESS_NAME");
		if (holdProcessNames.size()>0){
			ZkComboboxControl.setComboboxItems(cbxGHoldProcessName, holdProcessNames.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
		}
		
		//Hold Reason
		holdReasons=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "HOLD_REASON");
		if (holdReasons.size()>0){
			ZkComboboxControl.setComboboxItems(cbxGHoldReason, holdReasons.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
		}
		
		//Hold Type
		holdTypes=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "HOLD_TYPE");
		if (holdTypes.size()>0){
			ZkComboboxControl.setComboboxItems(cbxGHoldType, holdTypes.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
		}
		
		//Hold Comments(For所有的Hold Comment)
		holdCommentAlls=commonService.getUiFieldSetListByParaDescs("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "HOLD_COMMENTS");
	}
	
	public void onChange$cbxGHoldType(){
		if (holdModels.size()>0){
			for (int i=0;i<holdModels.size();i++){
				if (holdModels.get(i).isSelected()){
					holdModels.get(i).setHoldTypeName(cbxGHoldType.getText());
					if("HOLD".equals(holdModels.get(i).getHoldTypeName())){
						holdModels.get(i).setHoldProcessNameName("");
					}
				}
			}//end for i
			this.refreshgrdLotno();
		}
	}
	
	public void onChange$cbxGHoldProcessName(){
		if (holdModels.size()>0){
			for (int i=0;i<holdModels.size();i++){
				if (holdModels.get(i).isSelected()){
					if ("10".equals(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()))){
						holdModels.get(i).setHoldProcessNameName("");
					}
					else{
						holdModels.get(i).setHoldProcessNameName(cbxGHoldProcessName.getText());
					}
					
				}
			}//end for i
			this.refreshgrdLotno();
		}
	}
	
	public void onChange$cbxGHoldReason(){
		if (holdModels.size()>0){
			for (int i=0;i<holdModels.size();i++){
				if (holdModels.get(i).isSelected()){
					holdModels.get(i).setHoldReasonName(cbxGHoldReason.getText());
				}
			}//end for i
			this.refreshgrdLotno();
		}
		//IT-PR-141008_Hold Code/Comment add
		edtGHoldComment.getItems().clear();
		if(cbxGHoldReason.getText().equals("CUSTOMER HOLD")){
			holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "CUSTOMER_HOLD");
			if (holdComments.size()>0){
				for(int i=0; i<holdComments.get(0).getUiFieldParams().size(); i++){
					edtGHoldComment.appendItem(holdComments.get(0).getUiFieldParams().get(i).getParaValue());
				}
			}
		}
		if(cbxGHoldReason.getText().equals("TOPPAN HOLD")){
			holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "TOPPAN_HOLD");
			if (holdComments.size()>0){
				for(int i=0; i<holdComments.get(0).getUiFieldParams().size(); i++){
					edtGHoldComment.appendItem(holdComments.get(0).getUiFieldParams().get(i).getParaValue());
				}
			}
		}
	}
	
	public void onClick$imgGHoldCommentOK(){
		if (holdModels.size()>0){
			for (int i=0;i<holdModels.size();i++){
				if (holdModels.get(i).isSelected()){
					holdModels.get(i).setHoldComment(edtGHoldComment.getText());
				}
			}//end for i
			this.refreshgrdLotno();
		}
	}
	
	public void onCheck$chbSelectAll(){
		if (holdModels.size()>0){
			for (int i=0;i<holdModels.size();i++){
					holdModels.get(i).setSelected(chbSelectAll.isChecked());
			}
			this.refreshgrdLotno();
		}
	}
	
	public void onChange$edtGHoldComment(){
		if (holdModels.size()>0){
			for (int i=0;i<holdModels.size();i++){
				if (holdModels.get(i).isSelected()){
					holdModels.get(i).setHoldComment(edtGHoldComment.getText());					
				}
			}//end for i
			this.refreshgrdLotno();
		}
		if(edtGHoldComment.getText().contains("LOCK") && cbxGHoldReason.getText().equals("CUSTOMER HOLD")){
			edtGLockComment.setDisabled(false);
			this.showmessage("Warning", Labels.getLabel("hold.edtGHoldComment.lockcomment.input"));
		}
	}
	
	public void refreshgrdLotno(){
		//grdLotno.setMultiple(false);
		grdLotno.setModel(new ListModelList(holdModels));
		grdLotno.setItemRenderer(this);
		//grdLotno.setMultiple(true);
	}
	
	public void onClick$btnSave(){
		if (grdLotno.getItemCount()<=0){
			this.showmessage("Warning", Labels.getLabel("hold.save.noitem"));
			return;
		}
		
		if (this.chkData()==false){//check mustbe
			return;
		}
		
		Messagebox.show(Labels.getLabel("oe.save.confirm"), "Question", 
				Messagebox.OK | Messagebox.CANCEL, 
				Messagebox.QUESTION, 
				new org.zkoss.zk.ui.event.EventListener(){
				    public void onEvent(Event inEvt) throws InterruptedException {
				    	if ("onOK".equals(inEvt.getName())){
				    		 	saveHold();
				    	 }
				    }
				});
	}
	
	public void onChange$edtGLockComment(){
		if (holdModels.size()>0){
			for (int i=0;i<holdModels.size();i++){
				if (holdModels.get(i).isSelected()){
					holdModels.get(i).setLockComment(edtGLockComment.getText());					
				}
			}//end for i
			this.refreshgrdLotno();
		}
	}
	
	public void onClick$btnSearch(){
		this.getThisOrderNumberData();
	}
	
	public void saveHold(){
		Date nowtime=new Date();
		List<Hold> insertHolds=new ArrayList<Hold>();
		List<Hold> updateHolds=new ArrayList<Hold>();
		List<OrderLineLotno> orderLineLotnos=new ArrayList<OrderLineLotno>();
		List<HoldWafer> insertHoldWafers=new ArrayList<HoldWafer>();
		List<HoldWafer> updateHoldWafers=new ArrayList<HoldWafer>();
		
		for (int i=0;i<holdModels.size();i++){
			if (holdModels.get(i).isSelected()){
				holdModels.get(i).getOrderLineLotno().setHoldFlag(true);
				if (holdModels.get(i).getHold()==null){
					//原本  HOLD_FLAG=0  ---> 1
					Hold newHold=new Hold();
					newHold.setHoldIdx(0);
					newHold.setOrderNumber(orderNumber);
					newHold.setPoItem(holdModels.get(i).getOrderLineLotno().getPoItem());
					newHold.setCustomerLotno(holdModels.get(i).getOrderLineLotno().getCustomerLotno());
					newHold.setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
					newHold.setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
					newHold.setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
					newHold.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdComments.get(0).getUiFieldParams()));
					newHold.setHoldUser(userId);
					newHold.setHoldDept(userDept);
					newHold.setHoldIssueDate(nowtime);
					newHold.setReleaseFlag(false);
					newHold.setReleaseUser("");
					newHold.setReleaseDate(null);
					newHold.setLockComment(holdModels.get(i).getLockComment());
					newHold.setAllWafersFlag(true);//IT-PR-141008_Hold整個CustomerLotNo_Allison
					insertHolds.add(newHold);
					
					//IT-PR-141008_By CustomerLotNo Hold的話，則代表是全部的WaferNo都要Hold，所以要將所有的WaferNo資料存到HOLD_WAFER裡_Allison
					if(holdModels.get(i).getOrderLineLotno().getWaferData() != null){
						String tmpWaferData[]=holdModels.get(i).getOrderLineLotno().getWaferData().split(";");
						for(int j=0; j<tmpWaferData.length; j++){
							HoldWafer newHoldWafer = new HoldWafer();
							newHoldWafer.setHoldIdx(0);
							newHoldWafer.setHoldWaferIdx(0);
							newHoldWafer.setCustomerJob(holdModels.get(i).getCustomerJob());
							newHoldWafer.setCustomerLotno(holdModels.get(i).getOrderLineLotno().getCustomerLotno());
							if(Integer.valueOf(tmpWaferData[j])<10){
								newHoldWafer.setWaferNo(holdModels.get(i).getOrderLineLotno().getCustomerLotno()+"-0"+Integer.valueOf(tmpWaferData[j].toString()));
							}else{
								newHoldWafer.setWaferNo(holdModels.get(i).getOrderLineLotno().getCustomerLotno()+"-"+Integer.valueOf(tmpWaferData[j].toString()));
							}
							newHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdComments.get(0).getUiFieldParams()));
							newHoldWafer.setHoldDept(userDept);
							newHoldWafer.setHoldIssueDate(nowtime);
							newHoldWafer.setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
							newHoldWafer.setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
							newHoldWafer.setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
							newHoldWafer.setHoldUser(userId);
							newHoldWafer.setReleaseDate(null);
							newHoldWafer.setReleaseFlag(false);
							newHoldWafer.setReleaseUser("");
							newHoldWafer.setLockComment(holdModels.get(i).getLockComment());
							newHoldWafer.setB2bHoldreleaseFlag("00");
							insertHoldWafers.add(newHoldWafer);
						}
					}else{
						List<LotInfo> lotInfos= orderEntryService.getLotInfoByOrderNumberAndCustomerLotNo(orderNumber, holdModels.get(i).getOrderLineLotno().getCustomerLotno());
						List<WaferInfo> waferInfos;
						if(lotInfos.size()>0){
							waferInfos=orderEntryService.getWaferInfoByCustomerLotNoAndLotNo(holdModels.get(i).getOrderLineLotno().getCustomerLotno(), lotInfos.get(0).getLotNo());
						}else{
							waferInfos=orderEntryService.getWaferInfoByCustomerLotNo(holdModels.get(i).getOrderLineLotno().getCustomerLotno());
						}

						if (waferInfos.size()>0){
							for (int j=0;j<waferInfos.size();j++){
								HoldWafer newHoldWafer = new HoldWafer();
								newHoldWafer.setHoldIdx(0);
								newHoldWafer.setHoldWaferIdx(0);
								newHoldWafer.setCustomerJob(holdModels.get(i).getCustomerJob());
								newHoldWafer.setCustomerLotno(holdModels.get(i).getOrderLineLotno().getCustomerLotno());
								newHoldWafer.setWaferNo(waferInfos.get(j).getWaferNo());
								newHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdComments.get(0).getUiFieldParams()));
								newHoldWafer.setHoldDept(userDept);
								newHoldWafer.setHoldIssueDate(nowtime);
								newHoldWafer.setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
								newHoldWafer.setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
								newHoldWafer.setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
								newHoldWafer.setHoldUser(userId);
								newHoldWafer.setReleaseDate(null);
								newHoldWafer.setReleaseFlag(false);
								newHoldWafer.setReleaseUser("");
								newHoldWafer.setLockComment(holdModels.get(i).getLockComment());
								newHoldWafer.setB2bHoldreleaseFlag("00");
								insertHoldWafers.add(newHoldWafer);
							}
						}else{
							List<WaferBankin> waferBankins=orderEntryService.getWaferBankinByCustomerLotNo(holdModels.get(i).getOrderLineLotno().getCustomerLotno());
							if(waferBankins.size()>0){
								String tmpWaferData[]=waferBankins.get(0).getWaferData().split(";");
								for(int j=0; j<tmpWaferData.length; j++){
									HoldWafer newHoldWafer = new HoldWafer();
									newHoldWafer.setHoldIdx(0);
									newHoldWafer.setHoldWaferIdx(0);
									newHoldWafer.setCustomerJob(holdModels.get(i).getCustomerJob());
									newHoldWafer.setCustomerLotno(holdModels.get(i).getOrderLineLotno().getCustomerLotno());
									if(Integer.valueOf(tmpWaferData[j])<10){
										newHoldWafer.setWaferNo(holdModels.get(i).getOrderLineLotno().getCustomerLotno()+"-0"+Integer.valueOf(tmpWaferData[j].toString()));
									}else{
										newHoldWafer.setWaferNo(holdModels.get(i).getOrderLineLotno().getCustomerLotno()+"-"+Integer.valueOf(tmpWaferData[j].toString()));
									}
									newHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdComments.get(0).getUiFieldParams()));
									newHoldWafer.setHoldDept(userDept);
									newHoldWafer.setHoldIssueDate(nowtime);
									newHoldWafer.setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
									newHoldWafer.setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
									newHoldWafer.setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
									newHoldWafer.setHoldUser(userId);
									newHoldWafer.setReleaseDate(null);
									newHoldWafer.setReleaseFlag(false);
									newHoldWafer.setReleaseUser("");
									newHoldWafer.setLockComment(holdModels.get(i).getLockComment());
									newHoldWafer.setB2bHoldreleaseFlag("00");
									insertHoldWafers.add(newHoldWafer);
								}
							}
						}
					}
				}else{
					//原本  HOLD_FLAG=1  ---> 1
					holdModels.get(i).getHold().setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
					holdModels.get(i).getHold().setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
					holdModels.get(i).getHold().setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
					if(!"".equals(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()))){
						holdModels.get(i).getHold().setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()));
					}else{
						holdModels.get(i).getHold().setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(1).getUiFieldParams()));
					}
					holdModels.get(i).getHold().setHoldUser(userId);
					holdModels.get(i).getHold().setHoldDept(userDept);
					holdModels.get(i).getHold().setHoldIssueDate(nowtime);
					holdModels.get(i).getHold().setLockComment(holdModels.get(i).getLockComment());
					holdModels.get(i).getHold().setAllWafersFlag(true);
					holdModels.get(i).getHold().setReleaseDate(null);
					holdModels.get(i).getHold().setReleaseFlag(false);
					holdModels.get(i).getHold().setReleaseUser("");
					updateHolds.add(holdModels.get(i).getHold());//IT-PR-141008_Hold整個CustomerLotNo_Allison

					for(int j=0; j<holdModels.get(i).getHoldWafers().size(); j++){
						holdModels.get(i).getHoldWafers().get(j).setHoldIdx(holdModels.get(i).getHold().getHoldIdx());
						holdModels.get(i).getHoldWafers().get(j).setHoldWaferIdx(holdModels.get(i).getHoldWafers().get(j).getHoldWaferIdx());
						holdModels.get(i).getHoldWafers().get(j).setCustomerJob(holdModels.get(i).getCustomerJob());
						holdModels.get(i).getHoldWafers().get(j).setCustomerLotno(holdModels.get(i).getOrderLineLotno().getCustomerLotno());
						if(!"".equals(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()))){
							holdModels.get(i).getHoldWafers().get(j).setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()));
						}else{
							holdModels.get(i).getHoldWafers().get(j).setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(1).getUiFieldParams()));
						}
						holdModels.get(i).getHoldWafers().get(j).setHoldDept(userDept);
						holdModels.get(i).getHoldWafers().get(j).setHoldIssueDate(nowtime);
						holdModels.get(i).getHoldWafers().get(j).setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
						holdModels.get(i).getHoldWafers().get(j).setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
						holdModels.get(i).getHoldWafers().get(j).setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
						holdModels.get(i).getHoldWafers().get(j).setHoldUser(userId);
						holdModels.get(i).getHoldWafers().get(j).setReleaseDate(null);
						holdModels.get(i).getHoldWafers().get(j).setReleaseFlag(false);
						holdModels.get(i).getHoldWafers().get(j).setReleaseUser("");
						holdModels.get(i).getHoldWafers().get(j).setLockComment(holdModels.get(i).getLockComment());
						holdModels.get(i).getHoldWafers().get(j).setB2bHoldreleaseFlag("00");
						holdModels.get(i).getHoldWafers().get(j).setReleaseDate(null);
						holdModels.get(i).getHoldWafers().get(j).setReleaseFlag(false);
						holdModels.get(i).getHoldWafers().get(j).setReleaseUser("");
						updateHoldWafers.add(holdModels.get(i).getHoldWafers().get(j));
					}
				}
			}else{
				if (holdModels.get(i).getHold()==null){
					//原本  HOLD_FLAG=0  ---> 0
				}else{
					//log.debug(holdModels.get(i).getHold().getAllWafersFlag());
					//log.debug(holdModels.get(i).getHold().getReleaseFlag());
					if(!holdModels.get(i).getHold().getAllWafersFlag()){
						if(!holdModels.get(i).getHold().getReleaseFlag()){
							this.showmessage("Warning", Labels.getLabel("hold.save.allWaferFlagFalse"));
							onClick$btnSearch();
							return;
						}
					}
					
					holdModels.get(i).getOrderLineLotno().setHoldFlag(false);
					holdModels.get(i).getHold().setAllWafersFlag(false);
					//原本  HOLD_FLAG=1  ---> 0  代表是Hold release
					holdModels.get(i).getHold().setReleaseFlag(true);
					holdModels.get(i).getHold().setReleaseUser(userId);
					holdModels.get(i).getHold().setReleaseDate(nowtime);
					updateHolds.add(holdModels.get(i).getHold());
					for(int j=0; j<holdModels.get(i).getHoldWafers().size(); j++){
						holdModels.get(i).getHoldWafers().get(j).setReleaseFlag(true);
						holdModels.get(i).getHoldWafers().get(j).setReleaseUser(userId);
						holdModels.get(i).getHoldWafers().get(j).setReleaseDate(nowtime);
						updateHoldWafers.add(holdModels.get(i).getHoldWafers().get(j));
					}
					
				}
			}
			
			orderLineLotnos.add(holdModels.get(i).getOrderLineLotno());
		}//end for i
		
		//將要處理的table放在Object[]內
		Object[] objs=  new Object[10];
		
		//1.insertHolds 
		objs[0]=insertHolds;
		
		//2.updateHolds
		objs[1]=updateHolds;
		
		//3.orderLineLotnos
		objs[2]=orderLineLotnos;
		
		//4.userId
		objs[3]=userId;
		
		//5.nowtime
		objs[4]=nowtime;
		
		//6.class_name
		objs[5]=this.getClass().getName();
		
		//7.action_name
		objs[6]="Hold Save";
		
		//8.insertHoldWafers
		objs[7]=insertHoldWafers;
		
		//9.updateHoldWafers
		objs[8]=updateHoldWafers;
		
		//10.orderNumber
		objs[9]=orderNumber;
		
		orderEntryService.saveHoldTransactionItems(objs);
		this.showmessage("Information", Labels.getLabel("oe.save.success"));
		//self.detach();
		this.getThisOrderNumberData();
	}
	
	/**
	 * 
	 * chkData:如果有勾選Hold時，檢查Hold Process Name及Hold Reasonz必填. <br/>
	 *
	 * @author 030260
	 * @return
	 * @since JDK 1.6
	 */
	public boolean chkData(){
		for (int i=0;i<holdModels.size();i++){
			if (holdModels.get(i).isSelected()){
				//Hold Type
				if ("".equals(holdModels.get(i).getHoldTypeName())){
					this.showmessage("Warning", Labels.getLabel("hold.save.mustbe", new Object[] {i+1,Labels.getLabel("hold.colHoldHoldType")}));
					return false;
				}
				
				//IT-PR-141008_若Hold_Reason="CUSTOMER HOLD" & Hold_COmment="(LOCK) Customer Lock"，則LOCK COMMENT不可空白
				if ("CUSTOMER HOLD".equals(holdModels.get(i).getHoldReasonName()) && "(LOCK) Customer Lock".equals(holdModels.get(i).getHoldComment()) && "".equals(holdModels.get(i).getLockComment())){
					this.showmessage("Warning", Labels.getLabel("hold.save.mustbe", new Object[] {i+1,Labels.getLabel("hold.colHoldLockComment")}));
					return false;
				}
				
				String tmpHoldTypeMeaning=getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams());
				if ("10".equals(tmpHoldTypeMeaning)){
					
				}
				else{
					//Hold Process Name
					if ("".equals(holdModels.get(i).getHoldProcessNameName())){
						this.showmessage("Warning", Labels.getLabel("hold.save.mustbe", new Object[] {i+1,Labels.getLabel("hold.colHoldHoldProcessName")}));
						return false;
					}
					
					//Hold Reason
					if ("".equals(holdModels.get(i).getHoldReasonName())){
						this.showmessage("Warning", Labels.getLabel("hold.save.mustbe", new Object[] {i+1,Labels.getLabel("hold.colHoldHoldReason")}));
						return false;
					}
				}
				
				//如果ShippingFlag=true時，不可以hold
				if (holdModels.get(i).isShippingFlag()){
					this.showmessage("Warning", Labels.getLabel("hold.save.shippingflag", new Object[] {i+1}));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * grdLotno的Render.
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(final Listitem inItem, Object inData, final int inIndex) throws Exception {
		final HoldModel smf=(HoldModel) inData;
		
		//宣告Listcell
		Listcell cellFutureHold = new Listcell();
		Listcell cellPoItem = new Listcell();
		Listcell cellCustomerLotno = new Listcell();
		Listcell cellAllWafer = new Listcell();//IT-PR-141008_Allison add
		Listcell cellCustomerJob = new Listcell();//IT-PR-141008_Allison add
		Listcell cellWaferQty = new Listcell();
		Listcell cellWaferData = new Listcell();//IT-PR-141008_Allison add
		Listcell cellHoldType = new Listcell();
		Listcell cellHoldProcessName = new Listcell();
		Listcell cellHoldReason = new Listcell();
		Listcell cellHoldComment = new Listcell();
		Listcell cellLockComment = new Listcell();//IT-PR-141008_Allison add
		Listcell cellHoldUser = new Listcell();
		Listcell cellHoldIssueDate = new Listcell();
		Listcell cellLotInfo = new Listcell();
		
		//先宣告每一個Listcell所對應的物件(TextBox,Combobox...)
		final Checkbox chbcellHold = new Checkbox();
		final Combobox cbxcellHoldType = new Combobox();
		final Combobox cbxcellHoldProcessName = new Combobox();
		final Combobox cbxcellHoldReason = new Combobox();
		final Combobox cbxcellHoldComment = new Combobox();
		final Checkbox chbAllWafer = new Checkbox();//IT-PR-141008_Allison add
		final Button btnWaferData = new Button();//IT-PR-141008_Allison add
		final Textbox edtcellLockComment = new Textbox();//IT-PR-141008_Allison add
		
		chbcellHold.setId("chbcellHold"+inIndex);
		cbxcellHoldType.setId("cbxcellHoldType"+inIndex);
		cbxcellHoldProcessName.setId("cbxcellHoldProcessName"+inIndex);
		cbxcellHoldReason.setId("cbxcellHoldReason"+inIndex);
		cbxcellHoldComment.setId("cbxcellHoldComment"+inIndex);
		chbAllWafer.setId("chbAllWafer"+inIndex);
		btnWaferData.setId("btnWaferData"+inIndex);
		edtcellLockComment.setId("edtcellLockComment"+inIndex);
		
		
		String tmpHoldTypeMeaning=getMeaningByParaValue(smf.getHoldTypeName(), holdTypes.get(0).getUiFieldParams());
		if ("10".equals(tmpHoldTypeMeaning)){//HOLD
			cbxcellHoldProcessName.setDisabled(true);
		}
		else{
			cbxcellHoldProcessName.setDisabled(false);
		}
		
		//設定所對應物件的屬性
			//Hold
		    chbcellHold.setParent(cellFutureHold);
		    chbcellHold.setChecked(smf.isSelected());
		    chbcellHold.addEventListener("onCheck", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setSelected(chbcellHold.isChecked());
					chbAllWafer.setChecked(chbcellHold.isChecked());
					
					if (smf.isSelected()==false){
						cbxcellHoldType.setText("");
						cbxcellHoldProcessName.setText("");
						cbxcellHoldReason.setText("");
						cbxcellHoldComment.setText("");
						edtcellLockComment.setText("");
						
						smf.setHoldTypeName("");
						smf.setHoldProcessNameName("");
						smf.setHoldReasonName("");
						smf.setHoldComment("");
						smf.setLockComment("");
					}
				}
			});
		
			//PoItem
		 	cellPoItem.setLabel(smf.getOrderLineLotno().getPoItem());
		 	
		 	//Customer Lotno
		 	if("".equals(smf.getOrderLineLotno().getCustomerLotno()) || smf.getOrderLineLotno().getCustomerLotno() == null){
		 		cellCustomerLotno.setLabel("");
		 	}else{
		 		cellCustomerLotno.setLabel(smf.getOrderLineLotno().getCustomerLotno());
		 	}
		 	
			//All Wafer(Checkbox)
		 	chbAllWafer.setParent(cellAllWafer);
		 	chbAllWafer.setChecked(smf.isAllWaferFlag());
		 	chbAllWafer.addEventListener("onCheck", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setAllWaferFlag(chbAllWafer.isChecked());

				}
			});
		 	
		 	//Customer Job
		 	
		 	cellCustomerJob.setLabel(smf.getOrderLineLotno().getCustomerJob());
		 	
		 	//Wafer Qty
		 	cellWaferQty.setLabel(String.valueOf(smf.getOrderLineLotno().getWaferQty()));
		 	
		 	//Wafer Data(Button)
		 	btnWaferData.setLabel(Labels.getLabel("hold.btnWaferData"));
		 	btnWaferData.setParent(cellWaferData);
		 	btnWaferData.addEventListener("onClick", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					Map args = new HashMap();
					args.put("customerLotNo", smf.getOrderLineLotno().getCustomerLotno());
					args.put("customerJob", smf.getCustomerJob());
					args.put("orderNumber", orderNumber);
					args.put("allWaferFlag", smf.isAllWaferFlag());
					args.put("waferData", smf.getOrderLineLotno().getWaferData());
					args.put("poItem", smf.getOrderLineLotno().getPoItem());
					Window winimport = (Window)Executions.createComponents("/WEB-INF/modules/oe/HoldByWaferNo.zul", null, args);
					winimport.setParent(winHold);
					winimport.doModal();
				}
			});
		 	
		 	//Hold Type
		 	ZkComboboxControl.setComboboxItems(cbxcellHoldType, holdTypes.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
		 	cbxcellHoldType.setParent(cellHoldType);
		 	if (smf.isSelected()){
		 		log.debug(smf.getHoldTypeName());
		 		cbxcellHoldType.setText(smf.getHoldTypeName());
		 	}
		 	else{
		 		cbxcellHoldType.setText("");
		 	}
		 	cbxcellHoldType.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setHoldTypeName(cbxcellHoldType.getText());
					
					String tmpHoldTypeMeaning=getMeaningByParaValue(cbxcellHoldType.getText(), holdTypes.get(0).getUiFieldParams());
					if ("10".equals(tmpHoldTypeMeaning)){//HOLD
						cbxcellHoldProcessName.setDisabled(true);
						cbxcellHoldProcessName.setText("");
						smf.setHoldProcessNameName("");
					}
					else{
						cbxcellHoldProcessName.setDisabled(false);
					}
				}
			});
		 	
		 	//Hold Process Name
		 	ZkComboboxControl.setComboboxItems(cbxcellHoldProcessName, holdProcessNames.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
		 	cbxcellHoldProcessName.setParent(cellHoldProcessName);
		 	if (smf.isSelected()){
		 		if("10".equals(tmpHoldTypeMeaning)){
		 			cbxcellHoldProcessName.setText("");
		 		}else{
		 			cbxcellHoldProcessName.setText(smf.getHoldProcessNameName());
		 		}
		 	}
		 	else{
		 		cbxcellHoldProcessName.setText("");
		 	}
		 	cbxcellHoldProcessName.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setHoldProcessNameName(cbxcellHoldProcessName.getText());
				}
			});
		 	
		 	//Hold Reason
		 	ZkComboboxControl.setComboboxItems(cbxcellHoldReason, holdReasons.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
		 	cbxcellHoldReason.setParent(cellHoldReason);
		 	if (smf.isSelected()){
		 		cbxcellHoldReason.setText(smf.getHoldReasonName());
		 	}
		 	else{
		 		cbxcellHoldReason.setText("");
		 	}
		 	cbxcellHoldReason.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setHoldReasonName(cbxcellHoldReason.getText());
					//IT-PR-141008_改成Combobox_Allison
					cbxcellHoldComment.getItems().clear();
				 	if(smf.getHoldReasonName().equals("CUSTOMER HOLD")){
				 		holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "CUSTOMER_HOLD");
				 		if (holdComments.size()>0){
				 			ZkComboboxControl.setComboboxItems(cbxcellHoldComment, holdComments.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
				 		}
				 	}
				 	if(smf.getHoldReasonName().equals("TOPPAN HOLD")){
				 		holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "TOPPAN_HOLD");
				 		if (holdComments.size()>0){
				 			ZkComboboxControl.setComboboxItems(cbxcellHoldComment, holdComments.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
				 		}
				 	}
				}
			});
		 	
		 	//Hold Comment
			cbxcellHoldComment.setParent(cellHoldComment);
		 	if (smf.isSelected()){
		 		if("".equals(smf.getHoldComment()) || smf.getHoldComment() == null){
		 			cbxcellHoldComment.setText("");
		 		}else{
		 			cbxcellHoldComment.setText(smf.getHoldComment());
		 		}
		 	}else{
		 		cbxcellHoldComment.setText("");
		 	}
		 	cbxcellHoldComment.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setHoldComment(cbxcellHoldComment.getText());
					//smf.setHoldComment(smf.getHoldComment());
					if("(LOCK) Customer Lock".equals(cbxcellHoldComment.getText())){
						edtcellLockComment.setDisabled(false);
						edtcellLockComment.setText(edtcellLockComment.getText());
						smf.setLockComment(edtcellLockComment.getText());
					}else{
						edtcellLockComment.setDisabled(true);
						edtcellLockComment.setText("");
						smf.setLockComment("");
					}
				}
			});
		 	
		 	//Lock Comment
		 	edtcellLockComment.setParent(cellLockComment);
		 	edtcellLockComment.setText(smf.getLockComment());
		 	if(!"(LOCK) Customer Lock".equals(cbxcellHoldComment.getText())){
		 		edtcellLockComment.setDisabled(true);
		 		edtcellLockComment.setText("");
		 	}else{
		 		edtcellLockComment.setDisabled(false);
		 		edtcellLockComment.setText(smf.getLockComment());
		 	}
		 	edtcellLockComment.addEventListener("onChange", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setLockComment(edtcellLockComment.getText());
				}
			});
		 	
		 	//Hold User
		 	if (smf.isSelected()){
		 		if (smf.getHold()==null){
		 			cellHoldUser.setLabel("");
		 		}
		 		else{
		 			cellHoldUser.setLabel(smf.getHold().getHoldUser());
		 		}
		 		
		 	}
		 	else{
		 		cellHoldUser.setLabel("");
		 	}
		 	
		 	//Hold Issue Date
		 	if (smf.isSelected()){
		 		if(smf.getHold() != null){
		 			cellHoldIssueDate.setLabel(DateFormatUtil.getDateTimeFormat().format(smf.getHold().getHoldIssueDate()));
		 		}else{
		 			cellHoldIssueDate.setLabel("");
		 		}		 		
		 	}else{
		 		cellHoldIssueDate.setLabel("");
		 	}
		 	
		 	//Lot Info
		 	cellLotInfo.setLabel(smf.getLotno());
		 	
		 	
		//將each Litcell 放上ListItme上
		cellFutureHold.setParent(inItem);
		cellPoItem.setParent(inItem);
		cellCustomerLotno.setParent(inItem);
		cellAllWafer.setParent(inItem);
		cellCustomerJob.setParent(inItem);
		cellWaferQty.setParent(inItem);
		cellWaferData.setParent(inItem);
		cellHoldType.setParent(inItem);
		cellHoldProcessName.setParent(inItem);
		cellHoldReason.setParent(inItem);
		cellHoldComment.setParent(inItem);
		cellLockComment.setParent(inItem);
		cellHoldUser.setParent(inItem);
		cellHoldIssueDate.setParent(inItem);
		cellLotInfo.setParent(inItem);		
		
	}

}
