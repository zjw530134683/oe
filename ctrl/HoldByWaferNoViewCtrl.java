
package com.tce.ivision.modules.oe.ctrl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
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
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.oe.model.HoldModel;
import com.tce.ivision.modules.oe.service.OrderEntryService;
import com.tce.ivision.modules.oe.service.OrderSchedulingService;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.ZkComboboxControl;
import com.tce.ivision.units.common.service.CommonService;

public class HoldByWaferNoViewCtrl extends BaseViewCtrl implements ListitemRenderer{
	
	/**
	 * Logger
	 */
	public static Logger log = Logger.getLogger(HoldByWaferNoViewCtrl.class);
	
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
	 * zk component:Window winHoldWafer;
	 */
	private Window winHoldWafer;
	
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
	 * zk component:Listheader colHold
	 */
	private Listheader colHold;
	
	/**
	 * zk component:Listhead listheadLine
	 */
	private Listhead listheadLineHoldWafer;
	
	/**
	 * zk component:Listbox grdLotno
	 */
	private Listbox grdHoldWaferLotno;
	
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
	 * zk component:Textbox edtLineCustomerJob
	 */
	private Textbox edtLineCustomerJob;	
	
	/**
	 * zk component:Label lblLineCustomerJob
	 */
	private Label lblLineCustomerJob;
	
	/**
	 * zk component:Textbox edtLineCustomerLotNo
	 */
	private Textbox edtLineCustomerLotNo;
	
	/**
	 * zk component:Label lblLineCustomerLotNo
	 */
	private Label lblLineCustomerLotNo;
	
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
	
	private Label lblGLockComment;
	
	private Textbox edtGLockComment;
	
	private Listheader colHoldWaferNo;
	
	private Listheader colHoldLockComment;
	
	/**
	 * PARA_TYPE=CUSTOMER_HOLD & TOPPAN_HOLD 的UiFieldSet
	 */
	private List<UiFieldSet> holdComments;
	
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
	private OrderHeader orderHeaderLists;
	
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
	
	private boolean allWaferFlag=false;
	
	private String waferData;
	
	private Hold HoldLists;
	
	private String poItem="";
	
	private HoldWafer HoldWaferLists;
	
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
		edtLineCustomerLotNo.setText((String) execution.getArg().get("customerLotNo"));
		edtLineCustomerJob.setText((String) execution.getArg().get("customerJob"));
		orderNumber=(String) execution.getArg().get("orderNumber");
		allWaferFlag=(Boolean) execution.getArg().get("allWaferFlag");
		waferData=(String) execution.getArg().get("waferData");
		poItem=(String) execution.getArg().get("poItem");
		this.getThisOrderNumberData();
		EmplInfo emplInfos = userService.getEmplInfoByEmplId(userId);
		userDept = emplInfos.getDeptCode();
		userDept = commonService.getUiFieldParamValueByMeaning("com.tce.ivision.modules.as.ctrl.UserDefineViewCtrl", "DEPARTMENT", emplInfos.getDeptCode());
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
		orderHeaderLists=orderEntryService.getOrderHeaderByOrderNumber(orderNumber);
		if (orderHeaderLists!=null){
			//Lotno
			holdModels=new ArrayList<HoldModel>();
			orderLineLotnos=orderEntryService.getOrderLineLotnosByOrderNumberAndCustomerLotNo(orderNumber, edtLineCustomerLotNo.getText());
			List<LotInfo> lotInfos= orderEntryService.getLotInfoByOrderNumberAndCustomerLotNo(orderNumber, edtLineCustomerLotNo.getText());
			HoldLists=orderEntryService.getHold(orderNumber, orderLineLotnos.get(0).getPoItem(), edtLineCustomerLotNo.getText());
			
			//先到[WAFER_INFO]找是否有WAFER_NO的資料，若無的話，則要用[ORDER_LINE_INT].WAFER_DATA來帶出WAFER_NO
			List<WaferInfo> waferInfos;
			if(lotInfos.size() > 0){
				waferInfos=orderEntryService.getWaferInfoByCustomerLotNoAndLotNo(edtLineCustomerLotNo.getText(), lotInfos.get(0).getLotNo());
			}else{
				waferInfos=orderEntryService.getWaferInfoByCustomerLotNo(edtLineCustomerLotNo.getText());
			}
			if (waferInfos.size()>0){
				for (int i=0;i<waferInfos.size();i++){
					HoldModel tmpHoldModel=new HoldModel();

					//找是否已經有HOLD的資料，若[HOLD]無資料，代表還沒被HOLD，有的話則要帶出HOLD的資料
					if(HoldLists != null){
						HoldWaferLists = orderEntryService.getHoldWaferByHoldIdxAndWaferNo(HoldLists.getHoldIdx(), waferInfos.get(i).getWaferNo());
						if(HoldWaferLists != null){
							if(!HoldWaferLists.isReleaseFlag()){
								tmpHoldModel.setHoldIdx(HoldWaferLists.getHoldIdx());
								tmpHoldModel.setHoldWaferIdx(HoldWaferLists.getHoldWaferIdx());
								tmpHoldModel.setSelected(!HoldWaferLists.isReleaseFlag());
								tmpHoldModel.setWaferInfo(waferInfos.get(i).getWaferNo());
								tmpHoldModel.setHoldTypeName(this.getParaValueByMeaning(HoldWaferLists.getHoldType(), holdTypes.get(0).getUiFieldParams()));
								tmpHoldModel.setHoldProcessNameName(this.getParaValueByMeaning(HoldWaferLists.getHoldProcessName(), holdProcessNames.get(0).getUiFieldParams()));
								tmpHoldModel.setHoldReasonName(this.getParaValueByMeaning(HoldWaferLists.getHoldReason(), holdReasons.get(0).getUiFieldParams()));
								if(!"".equals(this.getParaValueByMeaning(HoldWaferLists.getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()))){
									tmpHoldModel.setHoldComment(this.getParaValueByMeaning(HoldWaferLists.getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()));
								}else{
									tmpHoldModel.setHoldComment(this.getParaValueByMeaning(HoldWaferLists.getHoldComment(), holdCommentAlls.get(1).getUiFieldParams()));
								}
								tmpHoldModel.setLockComment(HoldWaferLists.getLockComment());
								tmpHoldModel.setHoldUser(HoldWaferLists.getHoldUser());
								tmpHoldModel.setHoldIssueDate((DateFormatUtil.getDateTimeFormat().format(HoldWaferLists.getHoldIssueDate())));
								tmpHoldModel.setHoldExistFlag(true);
							}else{
								tmpHoldModel.setHoldIdx(HoldWaferLists.getHoldIdx());
								tmpHoldModel.setHoldWaferIdx(HoldWaferLists.getHoldWaferIdx());
								tmpHoldModel.setSelected(false);
								tmpHoldModel.setWaferInfo(waferInfos.get(i).getWaferNo());
								tmpHoldModel.setHoldTypeName("");
								tmpHoldModel.setHoldProcessNameName("");
								tmpHoldModel.setHoldReasonName("");
								tmpHoldModel.setHoldComment("");
								tmpHoldModel.setLockComment("");
								tmpHoldModel.setHoldUser("");
								tmpHoldModel.setHoldIssueDate("");
								//tmpHoldModel.setiNaviLotNo(waferInfos.get(i).getiNaviLotNo());
								tmpHoldModel.setHoldExistFlag(true);
								tmpHoldModel.setReleaseFlag(true);
							}
						}else{
							tmpHoldModel.setWaferInfo(waferInfos.get(i).getWaferNo());
						}
					}else{
						tmpHoldModel.setSelected(false);
						tmpHoldModel.setWaferInfo(waferInfos.get(i).getWaferNo());
						tmpHoldModel.setHoldTypeName("");
						tmpHoldModel.setHoldProcessNameName("");
						tmpHoldModel.setHoldReasonName("");
						tmpHoldModel.setHoldComment("");
						tmpHoldModel.setLockComment("");
						tmpHoldModel.setHoldUser("");
						tmpHoldModel.setHoldIssueDate("");
						//tmpHoldModel.setiNaviLotNo(waferInfos.get(i).getiNaviLotNo());
						tmpHoldModel.setHoldExistFlag(false);
					}
					tmpHoldModel.setiNaviLotNo(waferInfos.get(i).getiNaviLotNo());
					
					holdModels.add(tmpHoldModel);
				}//end for i
			}else{
				String tmpWaferData[] = null;
				if(waferData != null){
					tmpWaferData=waferData.split(";");
				}else{
					List<WaferBankin> waferBankins=orderEntryService.getWaferBankinByCustomerLotNo(edtLineCustomerLotNo.getText());
					if(waferBankins.size()>0){
						tmpWaferData=waferBankins.get(0).getWaferData().split(";");
					}
				}
				for(int i=0; i<tmpWaferData.length; i++){
					String tmpWaferNo="";
					if(Integer.valueOf(tmpWaferData[i])<10){
						tmpWaferNo = edtLineCustomerLotNo.getText()+"-0"+tmpWaferData[i].toString();
					}else{
						tmpWaferNo = edtLineCustomerLotNo.getText()+"-"+tmpWaferData[i].toString();
					}
					
					HoldModel tmpHoldModel=new HoldModel();
					
					if(HoldLists != null){
						HoldWaferLists = orderEntryService.getHoldWaferByHoldIdxAndWaferNo(HoldLists.getHoldIdx(), tmpWaferNo);
						tmpHoldModel.setWaferInfo(tmpWaferNo);
						if(HoldWaferLists != null){
							if(!HoldWaferLists.isReleaseFlag()){
								tmpHoldModel.setHoldIdx(HoldWaferLists.getHoldIdx());
								tmpHoldModel.setHoldWaferIdx(HoldWaferLists.getHoldWaferIdx());
								tmpHoldModel.setSelected(!HoldWaferLists.isReleaseFlag());
								tmpHoldModel.setHoldTypeName(this.getParaValueByMeaning(HoldWaferLists.getHoldType(), holdTypes.get(0).getUiFieldParams()));
								tmpHoldModel.setHoldProcessNameName(this.getParaValueByMeaning(HoldWaferLists.getHoldProcessName(), holdProcessNames.get(0).getUiFieldParams()));
								tmpHoldModel.setHoldReasonName(this.getParaValueByMeaning(HoldWaferLists.getHoldReason(), holdReasons.get(0).getUiFieldParams()));
								//tmpHoldModel.setHoldComment(this.getParaValueByMeaning(HoldWaferLists.getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()));
								if(!"".equals(this.getParaValueByMeaning(HoldWaferLists.getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()))){
									tmpHoldModel.setHoldComment(this.getParaValueByMeaning(HoldWaferLists.getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()));
								}else{
									tmpHoldModel.setHoldComment(this.getParaValueByMeaning(HoldWaferLists.getHoldComment(), holdCommentAlls.get(1).getUiFieldParams()));
								}
								tmpHoldModel.setLockComment(HoldWaferLists.getLockComment());
								tmpHoldModel.setHoldUser(HoldWaferLists.getHoldUser());
								tmpHoldModel.setHoldIssueDate((DateFormatUtil.getDateTimeFormat().format(HoldWaferLists.getHoldIssueDate())));
								tmpHoldModel.setiNaviLotNo("");
								tmpHoldModel.setHoldExistFlag(true);
							}else{
								tmpHoldModel.setHoldIdx(HoldWaferLists.getHoldIdx());
								tmpHoldModel.setHoldWaferIdx(HoldWaferLists.getHoldWaferIdx());
								tmpHoldModel.setSelected(!HoldWaferLists.isReleaseFlag());
								tmpHoldModel.setHoldTypeName("");
								tmpHoldModel.setHoldProcessNameName("");
								tmpHoldModel.setHoldReasonName("");
								//tmpHoldModel.setHoldComment(this.getParaValueByMeaning(HoldWaferLists.getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()));
								tmpHoldModel.setHoldComment("");
								tmpHoldModel.setLockComment("");
								tmpHoldModel.setHoldUser("");
								tmpHoldModel.setHoldIssueDate(null);
								tmpHoldModel.setiNaviLotNo("");
								tmpHoldModel.setHoldExistFlag(true);
							}
						}
					}else{
						tmpHoldModel.setSelected(false);
						tmpHoldModel.setWaferInfo(tmpWaferNo);
						tmpHoldModel.setHoldTypeName("");
						tmpHoldModel.setHoldProcessNameName("");
						tmpHoldModel.setHoldReasonName("");
						tmpHoldModel.setHoldComment("");
						tmpHoldModel.setLockComment("");
						tmpHoldModel.setHoldUser("");
						tmpHoldModel.setHoldIssueDate("");
						tmpHoldModel.setiNaviLotNo("");
						tmpHoldModel.setHoldExistFlag(false);
					}
											
					holdModels.add(tmpHoldModel);
				}
			}	
		
					
			grdHoldWaferLotno.setMultiple(false);
			grdHoldWaferLotno.setModel(new ListModelList(holdModels));
			grdHoldWaferLotno.setItemRenderer(this);
			grdHoldWaferLotno.setMultiple(true);
		}
	}

	/**
	 * 設定畫面上的Label naming.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		//window
		winHoldWafer.setTitle(Labels.getLabel("hold.winHoldWafer"));
		
		//groupbox
		grbHeaderData.setLabel(Labels.getLabel("oe.edit.line.grbHeaderDataByWafer"));
		grbGlobalSetting.setLabel(Labels.getLabel("hold.grbGlobalSetting"));
		
		//checkbox
		chbSelectAll.setLabel(Labels.getLabel("hold.chbSelectAll"));
		
		//Label
		lblLineCustomerLotNo.setValue(Labels.getLabel("oe.edit.line.lblLineCustomerLotNo"));
		lblLineCustomerJob.setValue(Labels.getLabel("oe.edit.line.lblLineCustomerJob"));
		lblGHoldType.setValue(Labels.getLabel("hold.lblGHoldType"));
		lblGHoldProcessName.setValue(Labels.getLabel("hold.lblGHoldProcessName"));
		lblGHoldReason.setValue(Labels.getLabel("hold.lblGHoldReason"));
		lblGHoldComment.setValue(Labels.getLabel("hold.lblGHoldComment"));
		lblGLockComment.setValue(Labels.getLabel("hold.lblGLockComment"));
		
		//grdHoldWaferLotno field name
		colHold.setLabel(Labels.getLabel("hold.colHold"));
		colHoldWaferNo.setLabel(Labels.getLabel("hold.colHoldWaferNo"));
		colHoldHoldType.setLabel(Labels.getLabel("hold.colHoldHoldType"));
		colHoldHoldProcessName.setLabel(Labels.getLabel("hold.colHoldHoldProcessName"));
		colHoldHoldReason.setLabel(Labels.getLabel("hold.colHoldHoldReason"));
		colHoldHoldComment.setLabel(Labels.getLabel("hold.colHoldHoldComment"));
		colHoldLockComment.setLabel(Labels.getLabel("hold.colHoldLockComment"));
		colHoldHoldUser.setLabel(Labels.getLabel("hold.colHoldHoldUser"));
		colHoldHoldIssueDate.setLabel(Labels.getLabel("hold.colHoldHoldIssueDate"));
		colHoldLotInfo.setLabel(Labels.getLabel("hold.colHoldiNaviLotNo"));
		
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
					if(!holdModels.get(i).isSelected()){
						holdModels.get(i).setLockComment("");
					}
			}
			this.refreshgrdLotno();
		}
	}
	
	public void onChange$edtGHoldComment(){
		if (holdModels.size()>0){
			for (int i=0;i<holdModels.size();i++){
				if (holdModels.get(i).isSelected()){
					holdModels.get(i).setHoldComment(edtGHoldComment.getText());
					if(holdModels.get(i).getHoldWafer() != null){
						holdModels.get(i).setHoldComment(edtGHoldComment.getText());
					}
				}
			}//end for i
			this.refreshgrdLotno();
		}
		if(edtGHoldComment.getText().contains("LOCK") && cbxGHoldReason.getText().equals("CUSTOMER HOLD")){
			edtGLockComment.setDisabled(false);
			this.showmessage("Warning", Labels.getLabel("hold.edtGHoldComment.lockcomment.input"));
		}
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
	
	public void refreshgrdLotno(){
		//grdLotno.setMultiple(false);
//		for(int i=0; i<holdModels.size(); i++){
//			if(holdModels.get(i).isReleaseFlag()){
//				holdModels.get(i).setHoldTypeName("");
//				holdModels.get(i).setHoldProcessNameName("");
//				holdModels.get(i).setHoldReasonName("");
//				holdModels.get(i).setHoldComment("");
//				holdModels.get(i).setLockComment("");
//			}
//		}
		grdHoldWaferLotno.setModel(new ListModelList(holdModels));
		grdHoldWaferLotno.setItemRenderer(this);
		//grdLotno.setMultiple(true);
	}
	

	
	public void onClick$btnSave(){
		if (grdHoldWaferLotno.getItemCount()<=0){
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
	
	public void onClose$winHoldWafer(){
		Events.sendEvent(new Event("onClick", (Button)(winHoldWafer.getParent().getFellow("btnSearch"))));
		winHoldWafer.detach();
	}
	
	public void saveHold(){
		Date nowtime=new Date();
		List<Hold> insertHolds=new ArrayList<Hold>();
		List<Hold> updateHolds=new ArrayList<Hold>();
		List<OrderLineLotno> orderLineLotNoLists=new ArrayList<OrderLineLotno>();
		List<HoldWafer> insertHoldWafers=new ArrayList<HoldWafer>();
		List<HoldWafer> updateHoldWafers=new ArrayList<HoldWafer>();
		int holdCount=0;
		
		String tmpHoldComment="";
		String tmpHoldProcessName="";
		String tmpHoldReason="";
		String tmpHoldType="";
		String tmpLockComment="";

		for (int i=0;i<holdModels.size();i++){
			if (holdModels.get(i).isSelected()){
				if(holdModels.get(i).isHoldExistFlag()){
					HoldWafer updateHoldWafer = new HoldWafer();
					updateHoldWafer.setHoldIdx(holdModels.get(i).getHoldIdx());
					updateHoldWafer.setHoldWaferIdx(holdModels.get(i).getHoldWaferIdx());
					updateHoldWafer.setCustomerLotno(edtLineCustomerLotNo.getText());
					updateHoldWafer.setCustomerJob(edtLineCustomerJob.getText());
					updateHoldWafer.setWaferNo(holdModels.get(i).getWaferInfo());
					//updateHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdComments.get(0).getUiFieldParams()));
					if(!"".equals(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()))){
						updateHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()));
					}else{
						updateHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(1).getUiFieldParams()));
					}
					updateHoldWafer.setHoldDept(userDept);
					updateHoldWafer.setHoldIssueDate(nowtime);
					updateHoldWafer.setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
					updateHoldWafer.setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
					updateHoldWafer.setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
					updateHoldWafer.setHoldUser(userId);
					updateHoldWafer.setLockComment(holdModels.get(i).getLockComment());
					updateHoldWafer.setB2bHoldreleaseFlag("00");
					updateHoldWafer.setReleaseDate(null);
					updateHoldWafer.setReleaseFlag(false);
					updateHoldWafer.setReleaseUser("");
					
					updateHoldWafers.add(updateHoldWafer);
				}else{
					HoldWafer insertHoldWafer = new HoldWafer();
					insertHoldWafer.setCustomerLotno(edtLineCustomerLotNo.getText());
					insertHoldWafer.setCustomerJob(edtLineCustomerJob.getText());
					insertHoldWafer.setWaferNo(holdModels.get(i).getWaferInfo());
					insertHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdComments.get(0).getUiFieldParams()));
					insertHoldWafer.setHoldDept(userDept);
					insertHoldWafer.setHoldIssueDate(nowtime);
					insertHoldWafer.setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
					insertHoldWafer.setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
					insertHoldWafer.setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
					insertHoldWafer.setHoldUser(userId);
					insertHoldWafer.setLockComment(holdModels.get(i).getLockComment());
					insertHoldWafer.setB2bHoldreleaseFlag("00");
					insertHoldWafer.setReleaseDate(null);
					insertHoldWafer.setReleaseFlag(false);
					insertHoldWafer.setReleaseUser("");
					
					insertHoldWafers.add(insertHoldWafer);
				}	
				holdCount=holdCount+1;
				
				if("".equals(tmpHoldType)){	
					if(!"".equals(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()))){
						tmpHoldComment=this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams());
					}else{
						tmpHoldComment=this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(1).getUiFieldParams());
					}
					tmpHoldProcessName=this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams());
					tmpHoldReason=this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams());
					tmpHoldType=this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams());
					tmpLockComment=holdModels.get(i).getLockComment();
				}
			}else{
				if(holdModels.get(i).isHoldExistFlag()){
					if(!holdModels.get(i).isReleaseFlag()){
						HoldWafer updateHoldWafer = new HoldWafer();
						updateHoldWafer.setHoldIdx(holdModels.get(i).getHoldIdx());
						updateHoldWafer.setHoldWaferIdx(holdModels.get(i).getHoldWaferIdx());
						updateHoldWafer.setCustomerLotno(edtLineCustomerLotNo.getText());
						updateHoldWafer.setCustomerJob(edtLineCustomerJob.getText());
						updateHoldWafer.setWaferNo(holdModels.get(i).getWaferInfo());
						//updateHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
						//					if(!"".equals(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()))){
						//						updateHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()));
						//					}else{
						//						updateHoldWafer.setHoldComment(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(1).getUiFieldParams()));
						//					}
						//					updateHoldWafer.setHoldDept(userDept);
						//					updateHoldWafer.setHoldIssueDate(nowtime);
						//					updateHoldWafer.setHoldProcessName(this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams()));
						//					updateHoldWafer.setHoldReason(this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams()));
						//					updateHoldWafer.setHoldType(this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams()));
						//					updateHoldWafer.setHoldUser(userId);
						//					updateHoldWafer.setLockComment(holdModels.get(i).getLockComment());
						//					updateHoldWafer.setB2bHoldreleaseFlag("00");
						updateHoldWafer.setReleaseDate(nowtime);
						updateHoldWafer.setReleaseFlag(true);
						updateHoldWafer.setReleaseUser(userId);

						updateHoldWafers.add(updateHoldWafer);
					}
				}
				if("".equals(tmpHoldType)){
					//tmpHoldComment=this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdComments.get(0).getUiFieldParams());
					if(!"".equals(this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams()))){
						tmpHoldComment=this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(0).getUiFieldParams());
					}else{
						tmpHoldComment=this.getMeaningByParaValue(holdModels.get(i).getHoldComment(), holdCommentAlls.get(1).getUiFieldParams());
					}
					tmpHoldProcessName=this.getMeaningByParaValue(holdModels.get(i).getHoldProcessNameName(), holdProcessNames.get(0).getUiFieldParams());
					tmpHoldReason=this.getMeaningByParaValue(holdModels.get(i).getHoldReasonName(), holdReasons.get(0).getUiFieldParams());
					tmpHoldType=this.getMeaningByParaValue(holdModels.get(i).getHoldTypeName(), holdTypes.get(0).getUiFieldParams());
					tmpLockComment=holdModels.get(i).getLockComment();
				}
			}
		}
		
		if(holdCount != holdModels.size()){
			if(holdCount != 0){
				if(HoldLists != null){
					HoldLists.setAllWafersFlag(false);
					HoldLists.setHoldComment(tmpHoldComment);
					HoldLists.setHoldProcessName(tmpHoldProcessName);
					HoldLists.setHoldReason(tmpHoldReason);
					HoldLists.setHoldDept(userDept);
					HoldLists.setHoldIssueDate(nowtime);
					HoldLists.setHoldType(tmpHoldType);
					HoldLists.setHoldUser(userId);
					HoldLists.setCustomerLotno(edtLineCustomerLotNo.getText());
					HoldLists.setOrderNumber(orderNumber);
					HoldLists.setPoItem(poItem);
					HoldLists.setLockComment(tmpLockComment);
					HoldLists.setReleaseDate(null);
					HoldLists.setReleaseFlag(false);
					HoldLists.setReleaseUser("");
					updateHolds.add(HoldLists);
				}else{
					Hold newHold = new Hold();
					newHold.setAllWafersFlag(false);
					newHold.setHoldComment(tmpHoldComment);
					newHold.setHoldProcessName(tmpHoldProcessName);
					newHold.setHoldReason(tmpHoldReason);
					newHold.setHoldDept(userDept);
					newHold.setHoldIssueDate(nowtime);
					newHold.setHoldType(tmpHoldType);
					newHold.setHoldUser(userId);
					newHold.setCustomerLotno(edtLineCustomerLotNo.getText());
					newHold.setOrderNumber(orderNumber);
					newHold.setPoItem(poItem);
					newHold.setLockComment(tmpLockComment);
					newHold.setReleaseDate(null);
					newHold.setReleaseFlag(false);
					newHold.setReleaseUser("");
					insertHolds.add(newHold);
				}

				if(orderLineLotnos != null){
					orderLineLotnos.get(0).setHoldFlag(false);
					orderLineLotNoLists = orderLineLotnos;
				}	
			}else{
				if(HoldLists != null){
					HoldLists.setAllWafersFlag(false);
					HoldLists.setReleaseDate(nowtime);
					HoldLists.setReleaseFlag(true);
					HoldLists.setReleaseUser(userId);
					updateHolds.add(HoldLists);
				}
				if(orderLineLotnos != null){
					orderLineLotnos.get(0).setHoldFlag(false);
					orderLineLotNoLists = orderLineLotnos;
				}	
			}
		}else{
			if(HoldLists != null){
				HoldLists.setAllWafersFlag(true);
				HoldLists.setHoldComment(tmpHoldComment);
				HoldLists.setHoldProcessName(tmpHoldProcessName);
				HoldLists.setHoldReason(tmpHoldReason);
				HoldLists.setHoldDept(userDept);
				HoldLists.setHoldIssueDate(nowtime);
				HoldLists.setHoldType(tmpHoldType);
				HoldLists.setHoldUser(userId);
				HoldLists.setCustomerLotno(edtLineCustomerLotNo.getText());
				HoldLists.setOrderNumber(orderNumber);
				HoldLists.setPoItem(poItem);
				HoldLists.setLockComment(tmpLockComment);
				HoldLists.setReleaseDate(null);
				HoldLists.setReleaseFlag(false);
				HoldLists.setReleaseUser("");
				updateHolds.add(HoldLists);
			}else{
				Hold newHold = new Hold();
				newHold.setAllWafersFlag(true);
				newHold.setHoldComment(tmpHoldComment);
				newHold.setHoldProcessName(tmpHoldProcessName);
				newHold.setHoldReason(tmpHoldReason);
				newHold.setHoldDept(userDept);
				newHold.setHoldIssueDate(nowtime);
				newHold.setHoldType(tmpHoldType);
				newHold.setHoldUser(userId);
				newHold.setCustomerLotno(edtLineCustomerLotNo.getText());
				newHold.setOrderNumber(orderNumber);
				newHold.setPoItem(poItem);
				newHold.setLockComment(tmpLockComment);
				newHold.setReleaseDate(null);
				newHold.setReleaseFlag(false);
				newHold.setReleaseUser("");
				insertHolds.add(newHold);
			}
			
			if(orderLineLotnos != null){
				orderLineLotnos.get(0).setHoldFlag(true);
				orderLineLotNoLists = orderLineLotnos;
			}			
		}
		
		Object[] objs=  new Object[10];
		
		//1.insertHolds
		objs[0]=insertHolds;
		
		//2.updateHolds
		objs[1]=updateHolds;

		//3.orderLineLotnos
		objs[2]=orderLineLotNoLists;
		
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
					
				}else{
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
	public void render(final Listitem inItem, Object inData, int inIndex) throws Exception {
		final HoldModel smf=(HoldModel) inData;
		
		
		//宣告Listcell
		Listcell cellFutureHold = new Listcell();
		Listcell cellWaferNo = new Listcell();
		Listcell cellHoldType = new Listcell();
		Listcell cellHoldProcessName = new Listcell();
		Listcell cellHoldReason = new Listcell();
		Listcell cellHoldComment = new Listcell();
		Listcell cellLockComment = new Listcell();
		Listcell cellHoldUser = new Listcell();
		Listcell cellHoldIssueDate = new Listcell();
		Listcell celliNaviLotNo = new Listcell();
		
		//先宣告每一個Listcell所對應的物件(TextBox,Combobox...)
		final Checkbox chbcellHold = new Checkbox();
		final Combobox cbxcellHoldType = new Combobox();
		final Combobox cbxcellHoldProcessName = new Combobox();
		final Combobox cbxcellHoldReason = new Combobox();
		final Combobox cbxcellHoldComment = new Combobox();
		final Textbox edtcellLockComment = new Textbox();
		
		chbcellHold.setId("chbcellHold"+inIndex);
		cbxcellHoldType.setId("cbxcellHoldType"+inIndex);
		cbxcellHoldProcessName.setId("cbxcellHoldProcessName"+inIndex);
		cbxcellHoldReason.setId("cbxcellHoldReason"+inIndex);
		cbxcellHoldComment.setId("cbxcellHoldComment"+inIndex);
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
						
						if(smf.getHoldWafer() != null){
							smf.getHoldWafer().setHoldType("");
							smf.getHoldWafer().setHoldProcessName("");
							smf.getHoldWafer().setHoldReason("");
							smf.getHoldWafer().setHoldComment("");
							smf.getHoldWafer().setLockComment("");
						}
					}
				}
			});
		
			//WaferNo
		    cellWaferNo.setLabel(smf.getWaferInfo());
		 	
		 	//Hold Type
		 	ZkComboboxControl.setComboboxItems(cbxcellHoldType, holdTypes.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
		 	cbxcellHoldType.setParent(cellHoldType);
		 	if (smf.isSelected()){
		 		cbxcellHoldType.setText(smf.getHoldTypeName());
		 	}
		 	else{
		 		cbxcellHoldType.setText("");
		 	}
		 	cbxcellHoldType.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setHoldTypeName(cbxcellHoldType.getText());
					if(smf.getHoldWafer() != null){
						smf.getHoldWafer().setHoldType(getMeaningByParaValue(cbxcellHoldType.getText(), holdTypes.get(0).getUiFieldParams()));
					}
					
					String tmpHoldTypeMeaning=getMeaningByParaValue(cbxcellHoldType.getText(), holdTypes.get(0).getUiFieldParams());
					if ("10".equals(tmpHoldTypeMeaning)){//HOLD
						cbxcellHoldProcessName.setDisabled(true);
						cbxcellHoldProcessName.setText("");	
						smf.setHoldProcessNameName("");
						if(smf.getHoldWafer() != null){
							smf.getHoldWafer().setHoldProcessName("");
						}
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
		 		cbxcellHoldProcessName.setText(smf.getHoldProcessNameName());
		 	}
		 	else{
		 		cbxcellHoldProcessName.setText("");
		 	}
		 	cbxcellHoldProcessName.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					smf.setHoldProcessNameName(cbxcellHoldProcessName.getText());
					if(smf.getHoldWafer() != null){
						smf.getHoldWafer().setHoldProcessName(getMeaningByParaValue(cbxcellHoldProcessName.getText(), holdProcessNames.get(0).getUiFieldParams()));
					}
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
					if(smf.getHoldWafer() != null){
						smf.getHoldWafer().setHoldReason(getMeaningByParaValue(cbxcellHoldReason.getText(), holdReasons.get(0).getUiFieldParams()));
					}
					
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
		 	//IT-PR-141008_改成Combobox_Allison
//		 	if(smf.getHoldReasonName().equals("CUSTOMER HOLD")){
//		 		holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "CUSTOMER_HOLD");
//		 		if (holdComments.size()>0){
//		 			ZkComboboxControl.setComboboxItems(cbxcellHoldComment, holdComments.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
//		 		}
//		 	}
//		 	if(smf.getHoldReasonName().equals("TOPPAN HOLD")){
//		 		holdComments=commonService.getUiFieldSetLists("com.tce.ivision.modules.oe.ctrl.HoldViewCtrl", "TOPPAN_HOLD");
//		 		if (holdComments.size()>0){
//		 			ZkComboboxControl.setComboboxItems(cbxcellHoldComment, holdComments.get(0).getUiFieldParams(), "getParaValue","isEnabled",true);
//		 		}
//		 	}
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
					if(smf.getHoldWafer() != null){
						smf.getHoldWafer().setHoldComment(cbxcellHoldComment.getText());
					}
					if("(LOCK) Customer Lock".equals(cbxcellHoldComment.getText())){
						edtcellLockComment.setDisabled(false);
						edtcellLockComment.setText(edtcellLockComment.getText());
						smf.setLockComment(edtcellLockComment.getText());
					}else{
						edtcellLockComment.setDisabled(true);
						edtcellLockComment.setText("");
						smf.setLockComment("");
					}
					//cbxcellHoldComment.setText(cbxcellHoldComment.getText());
				}
			});
		 	
		 	//Lock Comment
		 	edtcellLockComment.setParent(cellLockComment);
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
					if(smf.getHoldWafer() != null){
						smf.getHoldWafer().setLockComment(edtcellLockComment.getText());
					}
				}
			});
		 	
		 	//Hold User
		 	if (smf.isSelected()){
		 		if ("".equals(smf.getHoldUser())){
		 			cellHoldUser.setLabel("");
		 		}
		 		else{
		 			cellHoldUser.setLabel(smf.getHoldUser());
		 		}
		 		
		 	}else{
		 		cellHoldUser.setLabel("");
		 	}

		 	//Hold Issue Date
//		 	if(smf.getHoldWafer()==null || "".equals(smf.getHoldWafer())){
//		 		cellHoldIssueDate.setLabel("");
//		 	}else{
//			 	if (smf.isSelected()){
//			 		if (smf.getHoldWafer().getHoldIssueDate()==null){
//			 			cellHoldIssueDate.setLabel("");
//			 		}else{
//			 			cellHoldIssueDate.setLabel(DateFormatUtil.getDateTimeFormat().format(smf.getHoldWafer().getHoldIssueDate()));
//			 		}
//			 		
//			 	}else{
//			 		cellHoldIssueDate.setLabel("");
//			 	}
//		 	}
		 	//2014.11.26_bugfix_Allison
		 	if(smf.getHoldIssueDate() == null || "".equals(smf.getHoldIssueDate())){
		 		cellHoldIssueDate.setLabel("");
		 	}else{
		 		if (smf.isSelected()){
		 			cellHoldIssueDate.setLabel(smf.getHoldIssueDate());
		 		}else{
		 			cellHoldIssueDate.setLabel("");
		 		}
		 	}

		 	
		 	//iNavi LotNo
		 	celliNaviLotNo.setLabel(smf.getiNaviLotNo());
		 	
		 	
		//將each Litcell 放上ListItme上
		cellFutureHold.setParent(inItem);
		cellWaferNo.setParent(inItem);
		cellHoldType.setParent(inItem);
		cellHoldProcessName.setParent(inItem);
		cellHoldReason.setParent(inItem);
		cellHoldComment.setParent(inItem);
		cellLockComment.setParent(inItem);
		cellHoldUser.setParent(inItem);
		cellHoldIssueDate.setParent(inItem);
		celliNaviLotNo.setParent(inItem);		
		
	}

}
