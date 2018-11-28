/*
 * Project Name:iVision
 * File Name:WaferReceipt.java
 * Package Name:com.tce.ivision.modules.oe
 * Date:2012/12/11下午5:00:20
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 2013.07.15      Fanny,update [WAFER_STATUS].WAFER_QTY --> [WAFER_STATUS].WAFER_RECIEVE_QTY
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.*;


import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.oe.model.WaferFilter;
import com.tce.ivision.modules.oe.render.WaferReceiptEditableRender;
import com.tce.ivision.modules.oe.service.WaferBankinIntService;
import com.tce.ivision.modules.oe.service.WaferBankinService;
import com.tce.ivision.modules.oe.service.WaferStatusService;
import com.tce.ivision.units.common.ZkComboboxControl;




/**
 * ClassName: WaferReceipt <br/>
 * date: 2012/12/11 下午5:00:20 <br/>
 *
 * @author jeff
 * @version 
 * @since JDK 1.6
 */
public class WaferReceiptViewCtrl extends BaseViewCtrl{
	/**
	 * serialVersionUID用來作為Java對象序列化中的版本標示之用
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Wafer受領作業 Window
	 */
	private Window winWaferReceip;
	/**
	 * Customer Label
	 */
	private Label lblCustomer;// Customer
	/**
	 * Customer lot no Label
	 */
	private Label lblCustomerlotno; //Customer lot no
	/**
	 * 日期 Label
	 */
	private Label lblDate; // 日期
	/**
	 * 搜尋B2B Data Button
	 */
	private Button btnB2bSearch; // 搜尋B2B Data
	/**
	 * 存檔 Button
	 */
	private Button btnSave; // 存檔
	/**
	 * Customer Combobox
	 */
	private Combobox cbxCustomer;
	/**
	 * Customer Lot No Textbox
	 */
	private Textbox edtCustomerlotno;
	/**
	 * Date Start
	 */
	private Datebox dbxDateStart;
	/**
	 * Date End
	 */
	private Datebox dbxDateEnd;
	
	
	//--Grid 欄位 
	/**
	 * Wafer 顯示 Listbox
	 */
	private Listbox waferlistbox;
	/**
	 * Customer Listheader
	 */
	private Listheader customer;
	/**
	 * BatchID Listheader
	 */
	private Listheader customerlotno;
	/**
	 * WaferInQTY Listheader
	 */
	private Listheader waferInQty;
	/**
	 * WaferInDate Listheader
	 */
	private Listheader waferInDate;
	/**
	 * DieQTY Listheader
	 */
	private Listheader dieQty;
	/**
	 * MaterialNumber Listheader
	 */
	private Listheader mtrlNum;
	/**
	 * MaterialDesc Listheader
	 */
	private Listheader mtrlDesc;
	/**
	 * DesignID Listheader
	 */
	private Listheader designId;
	/**
	 * FabricationFacility Listheader
	 */
	private Listheader fab;
	/**
	 * AWB Listheader
	 */
	private Listheader awb;
	/**
	 * DocNumber Listheader
	 */
	private Listheader docNumber;
	/**
	 * WaferData Listheader
	 */
	private Listheader waferData;

	//IT-PR-141008_CUSTOMER_JOB,CUSTOMER_PO,PO_ITEM,GRADE_RECORD,SOURCE_MTRL_NUM,ENG_NO,TEST_PROGRAM,ESOD,WAFER_DIE_Allison add
	private Listheader customerJob;
	private Listheader customerPo;
	private Listheader poItem;
	private Listheader gradeRecord;
	private Listheader sourceMtrlNum;
	private Listheader engNo;
	private Listheader testProgram;
	private Listheader esod;
	private Listheader waferDie;
	
	
	
	
	
	 //---get Spring Service
	/**
	 * WaferInfoReceiveService
	 */
	private WaferBankinService waferBankinService = (WaferBankinService) SpringUtil.getBean("waferBankinService");	
	/**
	 * WaferInIntTableService
	 */
    private WaferBankinIntService waferBankinIntService = (WaferBankinIntService) SpringUtil.getBean("waferBankinIntService");
    /**
     * WaferStatusService
     */
    private WaferStatusService waferStatusService = (WaferStatusService) SpringUtil.getBean("waferStatusService");
    /**
     * CustomerInformationService
     */
    private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil.getBean("customerInformationService");
	
    //private CommonService commonService = (CommonService) SpringUtil.getBean("commonService");

    List<WaferBankin> waferinforeceiveLists = new ArrayList<WaferBankin>();
    
    
    
	/**
	 * 
	 * 頁面載入後開始執行初始化的function.
	 * @see org.zkoss.zk.ui.util.GenericForwardComposer#doAfterCompose(org.zkoss.zk.ui.Component)
	 */
    @Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
				
		//設定存檔Button為Disabled
		btnSave.setDisabled(true);

	}
    
	public void onSelect$waferlistbox(SelectEvent inEvt) throws Exception {
			Set seld = inEvt.getSelectedItems();	
			
			if(seld.size()>0){
				btnSave.setDisabled(false);
			}else{
				btnSave.setDisabled(true);
			}
	}

	
	
	
	/**
	 * 
	 * onClick$btnB2bSearch:Wafer受領作業-搜尋B2B DATA <br/>
	 *
	 * @author 060489-Jeff
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public void  onClick$btnB2bSearch() throws Exception{
		btnSave.setDisabled(true);
		
		WaferFilter waferfilter = new WaferFilter();
		//log.debug(selectedCustomer.getCustomerId());
		//log.debug(selectedCustomer.getCustomerShortName());
		if(cbxCustomer.getSelectedIndex()==-1){
			waferfilter.setCustomerfilter("-");
		}else{		
			waferfilter.setCustomerfilter(String.valueOf(cbxCustomer.getSelectedItem().getValue()));
		}
		
		if(edtCustomerlotno.getValue().isEmpty()){
			waferfilter.setCustomerlotnofilter("-");
		}else{		
			waferfilter.setCustomerlotnofilter(edtCustomerlotno.getValue());
		}
			
		if(dbxDateStart.getValue()==null){
			waferfilter.setStartdatefilter("-");
		}else{
			waferfilter.setStartdatefilter(dbxDateStart.getText());
		}
		
		if(dbxDateEnd.getValue()==null){
			waferfilter.setEnddatefilter("-");
		}else{
			waferfilter.setEnddatefilter(dbxDateEnd.getText());
		}
		
		log.info("Filter Condition:");
		log.info("Customer:"+waferfilter.getCustomerfilter());
		log.info("Customerlotno:"+waferfilter.getCustomerlotnofilter());
		log.info("StartDate:"+waferfilter.getStartdatefilter());
		log.info("Enddate:"+waferfilter.getEnddatefilter());
		log.info("GetFlag:False");
		
		if( "-".equals(waferfilter.getCustomerfilter()) &&
			"-".equals(waferfilter.getCustomerlotnofilter()) &&			
			("-".equals(waferfilter.getStartdatefilter()) || 
			 "-".equals(waferfilter.getEnddatefilter()))
		  ){
			Messagebox.show(Labels.getLabel("modules.oe.waferreceipt.grid.dosearchcheck"),"Warning", Messagebox.OK, Messagebox.EXCLAMATION);
		 			
		}else{
		
			List<WaferBankinInt> waferininttableLists = waferBankinIntService.listBySearch(waferfilter);
			if(waferininttableLists!=null){
				//設定multiple (checkbox)
				waferlistbox.setMultiple(false);
				waferlistbox.setModel(new ListModelList<WaferBankinInt>(waferininttableLists));				
				waferlistbox.setItemRenderer(new WaferReceiptEditableRender());				
				waferlistbox.setMultiple(true);
				waferlistbox.setCheckmark(true);

				waferlistbox.setSizedByContent(true);
				waferlistbox.setSpan(true);

			}else{
				waferlistbox.getItems().clear();
				Messagebox.show(Labels.getLabel("common.message.query.nodata"),"Warning", Messagebox.OK, Messagebox.EXCLAMATION);	
			}
		}
	}	

	public void onClick$btnSave(){
		List<WaferBankinInt> allWaferListboxs = (List<WaferBankinInt>) waferlistbox.getModel();		
		List<WaferBankinInt> updateWaferlistboxs  = new ArrayList<WaferBankinInt>();
		List<WaferBankin> saveWaferlistboxs  = new ArrayList<WaferBankin>();
		List<WaferStatus> updateWaferSataus  = new ArrayList<WaferStatus>();
		int messageStatus = -1;
		
		
		Set items = waferlistbox.getSelectedItems();
		//log.debug(items.size());			
		
		//處理勾選到的ITEM
		List<Listitem> selectItems = new ArrayList<Listitem>(items);
		for (int i =0;i< selectItems.size(); i++) {
			Listitem o = selectItems.get(i);
			//處理 由使用者可填寫的欄位
			Label cbxCustomer = (Label) o.getFellow("lblcbxCustomer"+o.getIndex());
			Label edtCustomerlotno =(Label) o.getFellow("lbledtCustomerlotno"+o.getIndex());			
			Spinner spinWaferInQTY = (Spinner) o.getFellow("spinWaferInQTY"+o.getIndex());
			Datebox dbxWaferInDate = (Datebox) o.getFellow("dbxWaferInDate"+o.getIndex());
			Spinner spinDieQTY = (Spinner) o.getFellow("spinDieQTY"+o.getIndex());
			Textbox edtMtrlNum =(Textbox) o.getFellow("edtMtrlNum"+o.getIndex());
			Textbox edtMtrlDesc =(Textbox) o.getFellow("edtMtrlDesc"+o.getIndex());
			Textbox edtDesignId =(Textbox) o.getFellow("edtDesignId"+o.getIndex());
			Textbox edtFab =(Textbox) o.getFellow("edtFab"+o.getIndex());
			Textbox edtAwb =(Textbox) o.getFellow("edtAwb"+o.getIndex());
			Textbox edtDocNumber =(Textbox) o.getFellow("edtDocNumber"+o.getIndex());
			Textbox edtWaferData =(Textbox) o.getFellow("edtWaferData"+o.getIndex());
			Textbox edtCustomerJob =(Textbox) o.getFellow("edtCustomerJob"+o.getIndex());//IT-PR-141008_Allison add
			Textbox edtCustomerPo =(Textbox) o.getFellow("edtCustomerPo"+o.getIndex());//IT-PR-141008_Allison add
			Textbox edtPoItem =(Textbox) o.getFellow("edtPoItem"+o.getIndex());//IT-PR-141008_Allison add
			Textbox edtGradeRecord =(Textbox) o.getFellow("edtGradeRecord"+o.getIndex());//IT-PR-141008_Allison add
			Textbox edtSourceMtrlNum =(Textbox) o.getFellow("edtSourceMtrlNum"+o.getIndex());//IT-PR-141008_Allison add
			Textbox edtEngNo =(Textbox) o.getFellow("edtEngNo"+o.getIndex());//IT-PR-141008_Allison add
			Textbox edtTestProgram =(Textbox) o.getFellow("edtTestProgram"+o.getIndex());//IT-PR-141008_Allison add
			Textbox edtEsod =(Textbox) o.getFellow("edtEsod"+o.getIndex());//IT-PR-141008_Allison add
			Textbox edtWaferDie =(Textbox) o.getFellow("edtWaferDie"+o.getIndex());//IT-PR-141008_Allison add
						
			log.debug(cbxCustomer.getValue()+", "+
			edtCustomerlotno.getValue()+", "+
			spinWaferInQTY.getValue()+", "+
			dbxWaferInDate.getText()+", "+
			spinDieQTY.getValue()+", "+
			edtMtrlNum.getValue()+", "+
			edtMtrlDesc.getValue()+", "+
			edtDesignId.getValue()+", "+
			edtFab.getValue()+", "+
			edtAwb.getValue()+", "+
			edtDocNumber.getValue()+", "+
			edtWaferData.getValue()+", "+ 
			edtCustomerJob.getValue()+", "+
			edtCustomerPo.getValue()+", "+
			edtPoItem.getValue()+", "+
			edtGradeRecord.getValue()+", "+
			edtSourceMtrlNum.getValue()+", "+
			edtEngNo.getValue()+", "+
			edtTestProgram.getValue()+", "+
			edtEsod.getValue()+", "+
			edtWaferDie.getValue()
			);
						
		
			
			//將B2B資料複制到受領TABLE
			Date nowtime=new Date();
			log.debug("Created_date:"+nowtime);
			WaferBankin waferbankin = new WaferBankin();
			
			//抓取畫面上的DATA
			waferbankin.setCustomerId(String.valueOf(cbxCustomer.getAttribute("customerId")));
			waferbankin.setCustomerLotno(edtCustomerlotno.getValue());			
			waferbankin.setWaferQty(spinWaferInQTY.getValue());
			waferbankin.setWaferInDate(dbxWaferInDate.getValue());			
			waferbankin.setDieQty(spinDieQTY.getValue());
			waferbankin.setMtrlNum(edtMtrlNum.getValue());
			waferbankin.setMtrlDesc(edtMtrlDesc.getValue());
			waferbankin.setDesignId(edtDesignId.getValue());
			waferbankin.setFab(edtFab.getValue());
			waferbankin.setDocNumber(edtDocNumber.getValue());
			waferbankin.setAwb(edtAwb.getValue());			
			waferbankin.setWaferData(edtWaferData.getValue());
			waferbankin.setCreatedate(nowtime);
			waferbankin.setOperationUnit(OU);
			
			//OCF-PR-150202_以下Allison mark
//			if(!"".equals(edtCustomerJob.getValue())){//IT-PR-141008_Allison add
//				waferbankin.setCustomerJob(edtCustomerJob.getValue().trim());
//			}else{
//				waferbankin.setCustomerJob("");
//			}
//			if(!"".equals(edtCustomerPo.getValue())){
//				waferbankin.setCustomerPo(edtCustomerPo.getValue().trim());
//			}else{
//				waferbankin.setCustomerPo("");
//			}
//			if(!"".equals(edtPoItem.getValue())){
//				waferbankin.setPoItem(edtPoItem.getValue().trim());
//			}else{
//				waferbankin.setPoItem("");
//			}
//			if(!"".equals(edtGradeRecord.getValue())){
//				waferbankin.setGradeRecord(edtGradeRecord.getValue().trim());
//			}else{
//				waferbankin.setGradeRecord("");
//			}
//			if(!"".equals(edtSourceMtrlNum.getValue())){
//				waferbankin.setSourceMtrlNum(edtSourceMtrlNum.getValue().trim());
//			}else{
//				waferbankin.setSourceMtrlNum("");
//			}
//			if(!"".equals(edtEngNo.getValue())){
//				waferbankin.setEngNo(edtEngNo.getValue().trim());
//			}else{
//				waferbankin.setEngNo("");
//			}
//			if(!"".equals(edtTestProgram.getValue())){
//				waferbankin.setTestProgram(edtTestProgram.getValue().trim());
//			}else{
//				waferbankin.setTestProgram("");
//			}
//			if(!"".equals(edtEsod.getValue())){
//				waferbankin.setEsod(edtEsod.getValue());
//			}else{
//				waferbankin.setEsod("");
//			}
//			if(!"".equals(edtWaferDie.getValue())){
//				waferbankin.setWaferDie(edtWaferDie.getValue());
//			}else{
//				waferbankin.setWaferDie("");
//			}
			
			//B2B來的DATA			
			WaferBankinInt waferininttable = allWaferListboxs.get(o.getIndex());			
			log.debug(waferininttable.getCurrentWaferQty());
				
			//將GETFLAG設定為已讀(1)
			waferininttable.setGetFlag(true);	
			//UPDATE勾選到的ITEM-WAFER_IN_INT_TABLE		
			updateWaferlistboxs.add(waferininttable);
				
			//waferinforeceive.setCreatedDate(waferininttable.getCreatedDate());
			//waferinforeceive.setCreatedTime(waferininttable.getCreatedTime());
			waferbankin.setDarkBondPads(waferininttable.getDarkBondPads());
			waferbankin.setImagerCustomerRev(waferininttable.getImagerCustomerRev());
			waferbankin.setOffshoreProbeFacility(waferininttable.getOffshoreProbeFacility());			
			waferbankin.setProbeShipPartType(waferininttable.getProbeShipPartType());
			waferbankin.setShipComment(waferininttable.getShipComment());
				
			waferbankin.setUpdateDate(new Date());
			waferbankin.setUpdateUser(loginId);

				
				//讀取Wafer_Status 資料
				WaferStatus waferstatus;
				log.debug(edtCustomerJob.getValue());
				if(!"".equals(edtCustomerJob.getValue())){
					waferstatus = waferStatusService.searchByCustomerByCustomerJob(waferbankin.getCustomerLotno(),waferbankin.getCustomerId(),"2",edtCustomerJob.getValue().trim());
				}else{
					waferstatus = waferStatusService.searchByCustomer(waferbankin.getCustomerLotno(),waferbankin.getCustomerId(),"2",waferbankin.getWaferQty());
				}
				//List<WaferStatus> waferstatus = waferStatusService.searchByCustomer(waferininttable.getCustomerLotno(),waferininttable.getCustomerId(),"2");			
				if(waferstatus!=null){				
					//UPDATE勾選到的ITEM-WAFER_STATUS
					//waferstatus.get(j).setWaferQty(waferbankin.getWaferInQty());//2013.07.15 mark
					waferstatus.setWaferRecieveQty(waferbankin.getWaferQty());//2013.07.15
					waferstatus.setWaferFlag(true);
					waferstatus.setStateFlag("0");
					waferstatus.setUpdateDate(new Date());
					waferstatus.setUpdateUser(loginId);
					waferstatus.setCustomerJob(edtCustomerJob.getValue());
					updateWaferSataus.add(waferstatus);								
				}else{//IT-PR-141201
					messageStatus = Messagebox.show(Labels.getLabel("modules.oe.waferreceipt.savecheck.WaferStatusError",
							new java.lang.Object[] {waferbankin.getCustomerLotno()+"\n\r"}),
							"Information", Messagebox.OK | Messagebox.CANCEL, Messagebox.INFORMATION);
					if(messageStatus == Messagebox.CANCEL){
						return;
					}
				}
			
									
			//SAVE勾選到的ITEM-WAFER_INFO_RECEIVE
			saveWaferlistboxs.add(waferbankin);

			waferlistbox.renderAll();
		}	
		
		log.debug("updateWaferSataus.size():"+updateWaferSataus.size());
		log.debug("messageStatus:"+messageStatus);

		//if (Messagebox.OK != messageStatus){//OCF-PR-141203
		if(messageStatus == -1 || messageStatus == Messagebox.OK){
				
			//將要處理的資料放到Array內，傳到Service處理
			Object[] objs=  new Object[4];
			//存入WAFER_INFO_RECEIVE
			  objs[0] = saveWaferlistboxs;			  
			//存入WAFER_STATUS
			  objs[1] = updateWaferSataus;
			  
				//存入WAFER_IN_INT_TABLE
			  objs[2] = updateWaferlistboxs;
			  messageStatus = -1;//OCF-PR-141203
			  //OCF-PR-150307
//			 if(!"OMNI".equals(cbxCustomer.getValue())){
//				  if(waferBankinService.checkDataExist(objs)){
//					  messageStatus = Messagebox.show(Labels.getLabel("modules.oe.waferreceipt.savecheck.DataExistError"),"Warning", Messagebox.YES | Messagebox.NO, Messagebox.EXCLAMATION);
//				  }
//			 }else{
//				  String checkMsg = waferBankinService.checkDataExistForOMNI(objs);
//				  if(!"".equals(checkMsg)){
//					  messageStatus = Messagebox.show(Labels.getLabel("modules.oe.waferreceipt.savecheck.DataExistErrorOmni", new java.lang.Object[] {checkMsg+"\n\r"}),"Warning", Messagebox.YES | Messagebox.NO, Messagebox.EXCLAMATION);
//				  }
//			 }
			  String checkMsg = waferBankinService.checkDataExistNew(objs);
			 if(!"".equals(checkMsg)){
					messageStatus = Messagebox.show(Labels.getLabel("modules.oe.waferreceipt.savecheck.DataExistErrorNew", new java.lang.Object[] {checkMsg+"\n\r"}),"Warning", Messagebox.YES | Messagebox.NO, Messagebox.EXCLAMATION);
				}
			 log.debug(messageStatus);
			//}else{
			 if(messageStatus == 16 || messageStatus == -1){//若檢查到重履按了YES(Code=16)，或是沒檢查到重履則Code=-1
				//將Array傳到Service處理  
				if(messageStatus == 16){//若檢查到重履按YES後，將原本的資料
					objs[3]=true;
				}else{
					objs[3]=false;
				}
				waferBankinService.saveTransactionItems(objs);	

				//刪除LISTBOX勾選的ITEM
				for (int i =0;i< selectItems.size();i++) {
					Listitem o = selectItems.get(i);							
					allWaferListboxs.remove(o.getIndex());
										
				}									
				btnSave.setDisabled(true);
				Messagebox.show(Labels.getLabel("modules.oe.waferreceipt.saveok"),"Information", Messagebox.OK, Messagebox.INFORMATION);
			 }
			//}
			
		}else{
			Messagebox.show(Labels.getLabel("common.message.saveng"),"Error", Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	
	
	/**
	 * 讀取設定語系
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	public void setLabelsValue() {		
		//label
		winWaferReceip.setTitle(Labels.getLabel("modules.oe.waferreceipt.ctrl.winWaferReceip")); //Wafer受領作業
		lblCustomer.setValue(Labels.getLabel("modules.oe.waferreceipt.ctrl.lblCustomer"));  //Customer
		lblCustomerlotno.setValue(Labels.getLabel("modules.oe.waferreceipt.ctrl.lblCustomerlotno")); //Customer lot no
		lblDate.setValue(Labels.getLabel("modules.oe.waferreceipt.ctrl.lblDate")); //日期
		btnB2bSearch.setLabel(Labels.getLabel("modules.oe.waferreceipt.ctrl.btnB2bSearch")); //搜尋B2B Data				
		//grid
										   
		customer.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.customer")); //Customer
		customerlotno.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.customerlotno")); //Customer Lot No/Batch ID
		waferInQty.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.waferInQty")); //Wafer In QTY
		waferInDate.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.waferInDate")); //Wafer In Date
		dieQty.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.dieQty")); //Die QTY
		mtrlNum.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.mtrlNum")); //Material Number
		mtrlDesc.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.mtrlDesc")); //Material Desc
		designId.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.designId")); //Design ID
		fab.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.fab")); //FABRICATION FACILITY
		awb.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.awb")); //AWB
		docNumber.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.docNumber")); //Doc Number
		waferData.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.waferData")); //Wafer Data
		
		customerJob.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.customerJob"));
		customerPo.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.customerPo"));
		poItem.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.poItem"));
		gradeRecord.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.gradeRecord"));
		sourceMtrlNum.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.sourceMtrlNum"));
		engNo.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.engNo"));
		testProgram.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.testProgram"));
		esod.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.esod"));
		waferDie.setLabel(Labels.getLabel("modules.oe.waferreceipt.grid.waferDie"));
	}
	/**
	 * Combobox初始化添加資料
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	public void initialComboboxItem() throws Exception {
		List<CustomerTable> customers = customerInformationService.getCustomerTableByBusPurpose("C");
		log.debug(customers.size());
		ZkComboboxControl.setComboboxItemValues(cbxCustomer, customers, "getCustomerShortName", "getCustomerId", "isCancelFlag", false);
	}

}
