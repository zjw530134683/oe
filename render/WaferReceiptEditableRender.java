/*
 * Project Name:iVision
 * File Name:WaferlistboxRenderer.java
 * Package Name:com.tce.ivision.modules.oe.render
 * Date:2012/12/24上午9:55:17
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.render;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

import com.ibm.icu.math.BigDecimal;
import com.tce.ivision.model.BaseCtrlSet;
import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.units.common.ZkComboboxControl;
import com.tce.ivision.units.common.service.CommonService;

/**
 * ClassName: WaferlistboxRenderer <br/>
 * date: 2012/12/24 上午9:55:17 <br/>
 * 
 * @author 060489-Jeff
 * @version
 * @since JDK 1.6
 */
public class WaferReceiptEditableRender implements ListitemRenderer {
	private static Logger log = Logger.getLogger(WaferReceiptEditableRender.class);
	private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil
			.getBean("customerInformationService");
	private CommonService commonService = (CommonService) SpringUtil
			.getBean("commonService");

	/**
	 * For Wafer Receive Listbox(waferlistbox) render
	 * 
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem,
	 *      java.lang.Object, int)
	 */
	@Override
	public void render(Listitem item, Object data, int index) throws Exception {
		if (data instanceof WaferBankinInt) {
			WaferBankinInt record = (WaferBankinInt) data;
			new Listcell("").setParent(item);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			Listcell customerCell = new Listcell();
			Label lblcbxCustomer = new Label();
			lblcbxCustomer.setId("lblcbxCustomer" + index);
			lblcbxCustomer.setValue(getCustomerName(record.getCustomerId()));
			lblcbxCustomer.setAttribute("customerId", record.getCustomerId());
			lblcbxCustomer.setParent(customerCell);
			customerCell.setParent(item);
			
			// customer lot no			
			//new Listcell(record.getCustomerLotno()).setParent(item);
			Listcell customerlotnoCell = new Listcell();
			Label lbledtCustomerlotno = new Label();
			lbledtCustomerlotno.setId("lbledtCustomerlotno" + index);
			lbledtCustomerlotno.setValue(record.getCustomerLotno());			
			lbledtCustomerlotno.setParent(customerlotnoCell);
			customerlotnoCell.setParent(item);			
			
			//IT-PR-141008_Customer Job_Allison add		
			Listcell customerJobCell = new Listcell();
			Textbox lbledtCustomerJob = new Textbox();
			lbledtCustomerJob.setId("edtCustomerJob" + index);
			lbledtCustomerJob.setValue(record.getCustomerJob());			
			lbledtCustomerJob.setParent(customerJobCell);
			customerJobCell.setParent(item);	
			
/*
			Listcell customerCell = new Listcell();
			Combobox cbxCustomer = new Combobox();
			cbxCustomer.setId("cbxCustomer" + index);
			cbxCustomer.setInplace(true);
			cbxCustomer.setWidth("99%");
			cbxCustomer.setValue(getCustomerName(record.getCustomer()));
			cbxCustomer.setReadonly(true);
			List<CustomerTable> customers = customerInformationService.getCustomerTableByBusPurpose("C");
			ZkComboboxControl.setComboboxItemValues(cbxCustomer, customers, "getCustomerShortName", "getCustomerId", "isCancelFlag", false);
			cbxCustomer.setParent(customerCell);
			customerCell.setParent(item);
			
			// customer lot no			
			//new Listcell(record.getCustomerLotno()).setParent(item);
			Listcell customerlotnoCell = new Listcell();
			Textbox edtCustomerlotno = new Textbox();
			edtCustomerlotno.setId("edtCustomerlotno" + index);
			edtCustomerlotno.setInplace(true);
			edtCustomerlotno.setValue(record.getCustomerLotno());			
			edtCustomerlotno.setParent(customerlotnoCell);
			customerlotnoCell.setParent(item);			
*/			
			// wafer in qty
			// header :colWaferInQTY
			Listcell waferinqtyCell = new Listcell();
			Spinner spinWaferInQTY = new Spinner();
			spinWaferInQTY.setId("spinWaferInQTY" + index);
			spinWaferInQTY.setInplace(true);
			spinWaferInQTY.setValue(record.getCurrentWaferQty());
			spinWaferInQTY.setParent(waferinqtyCell);
			waferinqtyCell.setParent(item);

			// wafer in date
			Listcell waferindateCell = new Listcell();
			Datebox dbxWaferInDate = new Datebox();
			dbxWaferInDate.setInplace(true);
			dbxWaferInDate.setWidth("99%");
			dbxWaferInDate.setId("dbxWaferInDate" + index);
			dbxWaferInDate.setFormat(Labels.getLabel("format.datetimeformat"));
			dbxWaferInDate.setParent(waferindateCell);
			waferindateCell.setParent(item);

			// die qty
			//new Listcell(record.getDieQty()).setParent(item);
			Listcell dieQTYCell = new Listcell();
			Spinner spinDieQTY = new Spinner();			
			spinDieQTY.setId("spinDieQTY" + index);
			spinDieQTY.setInplace(true);
			if(!"".equals(record.getDieQty()) && record.getDieQty() != null){
				spinDieQTY.setValue(Integer.parseInt(record.getDieQty()));
			}else{
				spinDieQTY.setValue(0);
			}
			spinDieQTY.setParent(dieQTYCell);
			dieQTYCell.setParent(item);							
			
			// material number
			//new Listcell(record.getMtrlNum()).setParent(item);
			Listcell mtrlNumCell = new Listcell();
			Textbox edtMtrlNum = new Textbox();
			edtMtrlNum.setId("edtMtrlNum" + index);			
			edtMtrlNum.setWidth("99%");
			edtMtrlNum.setInplace(true);
			edtMtrlNum.setValue(record.getMtrlNum());
			edtMtrlNum.setParent(mtrlNumCell);
			mtrlNumCell.setParent(item);							
			
			// material desc
			//new Listcell(record.getMtrlDesc()).setParent(item);
			Listcell mtrlDescCell = new Listcell();
			Textbox edtMtrlDesc = new Textbox();
			edtMtrlDesc.setId("edtMtrlDesc" + index);			
			edtMtrlDesc.setInplace(true);
			edtMtrlDesc.setValue(record.getMtrlDesc());
			edtMtrlDesc.setParent(mtrlDescCell);
			mtrlDescCell.setParent(item);			
			// design id
//			new Listcell(record.getDesignId()).setParent(item);
			Listcell designIdCell = new Listcell();
			Textbox edtDesignId = new Textbox();
			edtDesignId.setId("edtDesignId" + index);			
			edtDesignId.setWidth("99%");
			edtDesignId.setInplace(true);
			edtDesignId.setValue(record.getDesignId());
			edtDesignId.setParent(designIdCell);
			designIdCell.setParent(item);			
			// fab
			//new Listcell(record.getFab()).setParent(item);
			Listcell fabCell = new Listcell();
			Textbox edtFab = new Textbox();
			edtFab.setId("edtFab" + index);			
			edtFab.setInplace(true);
			edtFab.setValue(record.getFab());
			edtFab.setParent(fabCell);
			fabCell.setParent(item);			
			// awb
			//new Listcell(record.getAwb()).setParent(item);
			Listcell awbCell = new Listcell();
			Textbox edtAwb = new Textbox();
			edtAwb.setId("edtAwb" + index);			
			edtAwb.setInplace(true);
			edtAwb.setValue(record.getAwb());
			edtAwb.setParent(awbCell);
			awbCell.setParent(item);			
			// doc number
			//new Listcell(record.getAptinaDocNumber()).setParent(item);
			Listcell docNumberCell = new Listcell();
			Textbox edtDocNumber = new Textbox();
			edtDocNumber.setId("edtDocNumber" + index);			
			edtDocNumber.setInplace(true);
			edtDocNumber.setValue(record.getAptinaDocNumber());
			edtDocNumber.setParent(docNumberCell);
			docNumberCell.setParent(item);			
			// wafer data
			//new Listcell(record.getWaferData()).setParent(item);
			Listcell waferDataCell = new Listcell();
			Textbox edtWaferData = new Textbox();
			edtWaferData.setId("edtWaferData" + index);			
			edtWaferData.setInplace(true);
			edtWaferData.setValue(record.getWaferData());
			edtWaferData.setParent(waferDataCell);
			waferDataCell.setParent(item);
			
			//IT-PR-141008_Customer Po_Allison add		
			Listcell customerPo = new Listcell();
			Textbox lbledtCustomerPo = new Textbox();
			lbledtCustomerPo.setId("edtCustomerPo" + index);
			lbledtCustomerPo.setValue(record.getCustomerPo());			
			lbledtCustomerPo.setParent(customerPo);
			customerPo.setParent(item);
			
			//IT-PR-141008_Po Item_Allison add		
			Listcell poItem = new Listcell();
			Textbox lbledtPoItem = new Textbox();
			lbledtPoItem.setId("edtPoItem" + index);
			lbledtPoItem.setValue(record.getPoItem());			
			lbledtPoItem.setParent(poItem);
			poItem.setParent(item);
			
			//IT-PR-141008_Grade Record_Allison add		
			Listcell gradeRecord = new Listcell();
			Textbox lbledtGradeRecord = new Textbox();
			lbledtGradeRecord.setId("edtGradeRecord" + index);
			lbledtGradeRecord.setValue(record.getGradeRecord());			
			lbledtGradeRecord.setParent(gradeRecord);
			gradeRecord.setParent(item);

			//IT-PR-141008_Source Mtrl Num_Allison add		
			Listcell sourceMtrlNum = new Listcell();
			Textbox lbledtSourceMtrlNum = new Textbox();
			lbledtSourceMtrlNum.setId("edtSourceMtrlNum" + index);
			lbledtSourceMtrlNum.setValue(record.getSourceMtrlNum());			
			lbledtSourceMtrlNum.setParent(sourceMtrlNum);
			sourceMtrlNum.setParent(item);
			
			//IT-PR-141008_Eng No_Allison add		
			Listcell engNo = new Listcell();
			Textbox lbledtEngNo = new Textbox();
			lbledtEngNo.setId("edtEngNo" + index);
			lbledtEngNo.setValue(record.getEngNo());			
			lbledtEngNo.setParent(engNo);
			engNo.setParent(item);
			
			//IT-PR-141008_Test Program_Allison add		
			Listcell testProgram = new Listcell();
			Textbox lbledtTestProgram = new Textbox();
			lbledtTestProgram.setId("edtTestProgram" + index);
			lbledtTestProgram.setValue(record.getTestProgram());			
			lbledtTestProgram.setParent(testProgram);
			testProgram.setParent(item);
			
			//IT-PR-141008_E-SOD_Allison add		
			Listcell esod = new Listcell();
			Textbox lbledtEsod = new Textbox();
			lbledtEsod.setId("edtEsod" + index);
			if(record.getEsod() != null && !"".equals(record.getEsod())){
				lbledtEsod.setValue(sdf.format(record.getEsod()));
			}else{
				lbledtEsod.setValue("");
			}
			lbledtEsod.setParent(esod);
			esod.setParent(item);
			
			//IT-PR-141008_Wafer Die_Allison add		
			Listcell waferDie = new Listcell();
			Textbox lbledtWaferDie = new Textbox();
			lbledtWaferDie.setId("edtWaferDie" + index);
			lbledtWaferDie.setValue(record.getWaferDie());			
			lbledtWaferDie.setParent(waferDie);
			waferDie.setParent(item);
			
			setMustbeInput(item);

		} else if (data instanceof String) {
			new Listcell((String) data).setParent(item);

		}

	}

	private String getCustomerName(String inCustomerId) {
		CustomerTable customterTable = customerInformationService
				.getCustomerTableByCustomerId(inCustomerId);
		
		if(customterTable!=null){
			return customterTable.getCustomerShortName();
		}else{
			return "";
		}
	}

	private void setMustbeInput(Listitem inItem) throws IllegalArgumentException,
			SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {

		List<BaseCtrlSet> baseCtrlSetList = commonService.getBaseCtrlSetLists("com.tce.ivision.modules.oe.render.WaferlistboxRenderer");
		
		List<Component> headerlists = inItem.getFellowIfAny("waferlistboxhead").getChildren();

		//log.debug(headerlists.toString());

		List<String> headers = new ArrayList<String>();
		for (int i = 0; i < headerlists.size(); i++) {
			headers.add(headerlists.get(i).getId());
			//log.debug("??????"+headerlists.get(i).getId());
		}

		List<String> bases = new ArrayList<String>();
		for (int i = 0; i < baseCtrlSetList.size(); i++) {
			bases.add(baseCtrlSetList.get(i).getBeanColumnName());
			//log.debug("@@@@@@@@@@@"+ baseCtrlSetList.get(i).getBeanColumnName());
		}

		// headerlists.retainAll(baseCtrlSetList);
		bases.retainAll(headers);
		
		//log.debug("baseCtrlSetList.size():"+baseCtrlSetList.size());
		for (int i = 0; i < baseCtrlSetList.size(); i++) {
			//log.debug("bases.size():"+bases.size());
			for (int j = 0; j < bases.size(); j++) {
				if (baseCtrlSetList.get(i).getBeanColumnName().equals(bases.get(j).toString())) {
					//log.debug("!!!!!!!!!!!!!!!!!!!!!!!!"+ bases.get(j).toString());
					List<Component> cells = inItem.getChildren();
					for (int l = 0; l < cells.size(); l++) {
						List<Component> cellchildrens = cells.get(l).getChildren();
						for (int k = 0; k < cellchildrens.size(); k++) {							
/*							log.debug(
									baseCtrlSetList.get(i).getComponentName()
									+"--"+
									cellchildrens.get(k).getId()
									+"--"+
									"".equals(baseCtrlSetList.get(i).getConstraintValue())
									);
*/							//log.debug(baseCtrlSetList.get(i).getConstraintValue());
							if (cellchildrens.get(k).getId().startsWith(baseCtrlSetList.get(i).getComponentName())
									&& !"".equals(baseCtrlSetList.get(i).getConstraintValue())) {
	
								// 欄位顏色設為淺黃色
								Class.forName(cellchildrens.get(k).getClass().getName())
										.getMethod("setStyle",new Class[] { String.class })
										.invoke(cellchildrens.get(k),new Object[] { "background:#FFD" });
								//log.debug(baseCtrlSetList.get(i).getConstraintValue());

								// 若table中有設定constraint的話將該元件設定欄位格式及格式不符時產生的error msg
								Class.forName(cellchildrens.get(k).getClass().getName())
										.getMethod("setConstraint",	new Class[] { String.class })
										.invoke(cellchildrens.get(k),new Object[] { baseCtrlSetList.get(i).getConstraintValue()
														+ " : "
														+ Labels.getLabel(baseCtrlSetList.get(i).getErrorMsgProreries()) });								
							}
						}
					}
				}
			}

		}

		/*
		 * List<BaseCtrlSet> baseCtrlSetList =
		 * commonService.getBaseCtrlSetLists(this.getClass().getName());
		 * 
		 * for(int i = 0; i < baseCtrlSetList.size(); i++){ // 欄位顏色設為淺黃色
		 * //Class.
		 * forName(inComp.getFellow(baseCtrlSetList.get(i).getComponentName
		 * ()).getClass().getName())
		 * if(inColumnName.equals(baseCtrlSetList.get(i).getBeanColumnName())){
		 * 
		 * Class.forName(inComp.getClass().getName()) .getMethod("setStyle", new
		 * Class[] {String.class}) .invoke(inComp, new Object[]
		 * {"background:#FFD"});
		 * 
		 * 若table中有設定constraint的話將該元件設定欄位格式及格式不符時產生的error msg
		 * 
		 * //Class.forName(inComp.getFellow(baseCtrlSetList.get(i).getComponentName
		 * ()).getClass().getName())
		 * 
		 * Class.forName(inComp.getClass().getName())
		 * .getMethod("setConstraint", new Class[] {String.class})
		 * .invoke(inComp, new Object[] {
		 * baseCtrlSetList.get(i).getConstraintValue() + " : " +
		 * Labels.getLabel(baseCtrlSetList.get(i).getErrorMsgProreries()) }); }
		 * }
		 */
	}

}
