/*
 * Project Name:iVision
 * File Name:OrderLotnoRender.java
 * Package Name:com.tce.ivision.modules.oe.render
 * Date:2012/12/24下午3:36:51
 * 
 * 說明:
 * OrderEntryViewCtrl.grdLotno的render
 * 
 * 修改歷史:
 * 2012.12.17 OCF#OE002 Fanny Initial
 * 
 */
package com.tce.ivision.modules.oe.render;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.BaseCtrlSet;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.modules.oe.model.OrderEntryLotnoModel;
import com.tce.ivision.units.common.service.CommonService;


/**
 * ClassName: OrderLotnoRender <br/>
 * date: 2012/12/24 下午3:36:51 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderLotnoRender implements ListitemRenderer {
	/**
	 * Logger
	 */
	public static Logger log = Logger.getLogger(OrderLotnoRender.class);
	
	/**
	 * CustomerLotno限定欄位長度
	 */
	//public static int customerLotnoLen=20;
	
	/**
	 * CommonService
	 */
	private CommonService commonService = (CommonService) SpringUtil.getBean("commonService");
	
	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(Listitem inItem, Object inData, int inIndex) throws Exception {
		final OrderEntryLotnoModel smf=(OrderEntryLotnoModel) inData;
		
		log.debug("render....OrderLotnoRender"+inIndex);
		
		//宣告Listcell
		Listcell cellLotnoNo = new Listcell();
		Listcell cellLotnoPoItem = new Listcell();
		Listcell cellLotnoLotno = new Listcell();
		Listcell cellLotnoCustomerJob = new Listcell();
		Listcell cellLotnoWaferQty = new Listcell();
		Listcell cellLotnoPriority = new Listcell();
		Listcell cellLotnoLotType = new Listcell();
		Listcell cellLotnoSo = new Listcell();
		Listcell cellLotnoSoLine = new Listcell();
		final Listcell cellLotnoWaferData = new Listcell();
		Listcell cellLotnoWiRmaNo = new Listcell();
		Listcell cellLineWaferDie = new Listcell();//OCF-PR-150202_Allison add
		Listcell cellLineGradeRecord = new Listcell();//OCF-PR-150202_Allison add
		Listcell cellLineEngNo = new Listcell();//OCF-PR-150202_Allison add
		Listcell cellLineTestProgram = new Listcell();//OCF-PR-150202_Allison add
		Listcell cellLotnoOeInternalNo = new Listcell();//OCF-PR-150202_Allison add
		Listcell cellLotnoPackingListPrintDisable = new Listcell();//OCF-PR-150302
		Listcell cellLotnoShippingRemark = new Listcell();//OCF-PR-150302
		
		//先宣告每一個Listcell所對應的物件(TextBox,Combobox...)
		final Textbox edtcellLotno = new Textbox();
		final Spinner edtcellWaferQty = new Spinner();
		final Textbox edtcellSo = new Textbox();
		final Textbox edtcellSoLine = new Textbox();
		final Textbox edtcellLotType = new Textbox();
		final Textbox edtcellPriority = new Textbox();
		final Textbox edtcellWaferData = new Textbox();
		final Textbox edtcellWiRmaNo = new Textbox();
		final Textbox edtcellCustomerJob = new Textbox();//IT-PR-141201
		final Textbox edtcellWaferDie = new Textbox(); //OCF-PR-150202_Allison add
		final Textbox edtcellGradeRecord = new Textbox(); //OCF-PR-150202_Allison add
		final Textbox edtcellEngNo = new Textbox(); //OCF-PR-150202_Allison add
		final Textbox edtcellTestProgram = new Textbox(); //OCF-PR-150202_Allison add
		final Button btncellOeInternalNo = new Button();//OCF-PR-150202_Allison add
		
		final Checkbox chkcellPackingListPrintDisable = new Checkbox();//OCF-PR-150302
		final Textbox edtcellShippingRemark = new Textbox();//OCF-PR-150302
		
		//設定所對應物件的屬性
		    //POItem
		    cellLotnoPoItem.setLabel(smf.getOrderLineLotno().getPoItem().trim());
		    
			//Lotno
		    //if ("readonly".equals(smf.getMode())){//2013.07.09
		    if (("readonly".equals(smf.getMode()))||
		    	(("modify".equals(smf.getMode()))&&("20".equals(smf.getOrderHeaderOrderStatus())))){
				cellLotnoLotno.setId("cellLotnoLotno"+inIndex);
		    	cellLotnoLotno.setLabel(smf.getOrderLineLotno().getCustomerLotno().trim());
			}
		    else{
		    	edtcellLotno.setId("edtcellLotno"+inIndex);
		    	edtcellLotno.setMaxlength(smf.getCustomerLotnoLen());
				edtcellLotno.setInplace(true);
				edtcellLotno.setWidth("90%");
				edtcellLotno.setText(smf.getOrderLineLotno().getCustomerLotno().trim());
				edtcellLotno.setParent(cellLotnoLotno);
				edtcellLotno.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLineLotno().setCustomerLotno(edtcellLotno.getText().trim());
					}
				});
		    }
		    
			//CustomerJob
		    if(!"".equals(smf.getOrderLineLotno().getCustomerJob()) && smf.getOrderLineLotno().getCustomerJob() != null){
		    	//OCF-PR-150107
		    	//cellLotnoCustomerJob.setLabel(smf.getOrderLineLotno().getCustomerJob().trim());
		    	edtcellCustomerJob.setId("edtcellCustomerJob"+inIndex);
	    		edtcellCustomerJob.setMaxlength(30);
	    		edtcellCustomerJob.setInplace(true);
	    		edtcellCustomerJob.setWidth("90%");
	    		if(smf.getOrderLineLotno().getCustomerJob() != null){
	    			edtcellCustomerJob.setText(smf.getOrderLineLotno().getCustomerJob().trim());
	    		}else{
	    			edtcellCustomerJob.setText("");
	    		}
	    		edtcellCustomerJob.setParent(cellLotnoCustomerJob);
	    		edtcellCustomerJob.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLineLotno().setCustomerJob(edtcellCustomerJob.getText().trim());
					}
				});
		    }else{
		    	//IT-PR-141201
		    	//cellLotnoCustomerJob.setLabel("");
		    	if(("newadd".equals(smf.getMode()))){		    		
		    		edtcellCustomerJob.setId("edtcellCustomerJob"+inIndex);
		    		edtcellCustomerJob.setMaxlength(30);
		    		edtcellCustomerJob.setInplace(true);
		    		edtcellCustomerJob.setWidth("90%");
		    		if(smf.getOrderLineLotno().getCustomerJob() != null){
		    			edtcellCustomerJob.setText(smf.getOrderLineLotno().getCustomerJob().trim());
		    		}else{
		    			edtcellCustomerJob.setText("");
		    		}
		    		edtcellCustomerJob.setParent(cellLotnoCustomerJob);
		    		edtcellCustomerJob.addEventListener("onChange", new EventListener() {
						public void onEvent(Event inEvent) throws Exception{
							smf.getOrderLineLotno().setCustomerJob(edtcellCustomerJob.getText().trim());
						}
					});
		    	}
		    }
			
			//WaferQty
		    if ("readonly".equals(smf.getMode())){
		    	cellLotnoWaferQty.setId("cellLotnoWaferQty"+inIndex);
				cellLotnoWaferQty.setLabel(String.valueOf(smf.getOrderLineLotno().getWaferQty()).toString().trim());
			}
			else {
				edtcellWaferQty.setId("edtcelllotnoWaferQty"+inIndex);
				edtcellWaferQty.setInplace(true);
				edtcellWaferQty.setWidth("90%");
				edtcellWaferQty.setText(String.valueOf(smf.getOrderLineLotno().getWaferQty()));
				edtcellWaferQty.setParent(cellLotnoWaferQty);
				edtcellWaferQty.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						if ("".equals(edtcellWaferQty.getText())){
							smf.getOrderLineLotno().setWaferQty(0);
						}
						else{
							smf.getOrderLineLotno().setWaferQty(Integer.valueOf(edtcellWaferQty.getText()));
						}
					}
				});
			}
			
		    //Priority
		    if ("readonly".equals(smf.getMode())){
		    		if(!"".equals(smf.getOrderLineLotno().getPriority()) && smf.getOrderLineLotno().getPriority() != null){
		    			cellLotnoPriority.setLabel(smf.getOrderLineLotno().getPriority().trim());
		    		}else{
		    			cellLotnoPriority.setLabel("");
		    		}
				}
			    else{
			    	edtcellPriority.setId("edtcellPriority"+inIndex);
			    	edtcellPriority.setInplace(true);
			    	edtcellPriority.setWidth("90%");
			    	if(!"".equals(smf.getOrderLineLotno().getPriority()) && smf.getOrderLineLotno().getPriority() != null){
			    		edtcellPriority.setText(smf.getOrderLineLotno().getPriority().trim());
			    	}else{
			    		edtcellPriority.setText("");
			    	}
			    	edtcellPriority.setParent(cellLotnoPriority);
			    	edtcellPriority.addEventListener("onChange", new EventListener() {
						public void onEvent(Event inEvent) throws Exception{
							smf.getOrderLineLotno().setPriority(edtcellPriority.getText().trim());
						}
					});
			    }

		    //LotType
		    if ("readonly".equals(smf.getMode())){
		    		if(!"".equals(smf.getOrderLineLotno().getLotType()) && smf.getOrderLineLotno().getLotType() != null){
		    			cellLotnoLotType.setLabel(smf.getOrderLineLotno().getLotType().trim());
		    		}else{
		    			cellLotnoLotType.setLabel("");
		    		}
				}
			    else{
			    	edtcellLotType.setId("edtcellLotType"+inIndex);
			    	edtcellLotType.setInplace(true);
			    	edtcellLotType.setWidth("90%");
			    	if(!"".equals(smf.getOrderLineLotno().getLotType()) && smf.getOrderLineLotno().getLotType() != null){
			    		edtcellLotType.setText(smf.getOrderLineLotno().getLotType().trim());
			    	}else{
			    		edtcellLotType.setText("");
			    	}
			    	edtcellLotType.setParent(cellLotnoLotType);
			    	edtcellLotType.addEventListener("onChange", new EventListener() {
						public void onEvent(Event inEvent) throws Exception{
							smf.getOrderLineLotno().setLotType(edtcellLotType.getText().trim());
						}
					});
			    }
		    
		    //So
		    if (("readonly".equals(smf.getMode()))){
			    	if(!"".equals(smf.getOrderLineLotno().getSo()) && smf.getOrderLineLotno().getSo() != null){
			    		cellLotnoSo.setLabel(smf.getOrderLineLotno().getSo().trim());
			    	}else{
			    		cellLotnoSo.setLabel("");
			    	}
				}
			    else{
			    	edtcellSo.setId("edtcellSo"+inIndex);
			    	edtcellSo.setInplace(true);
			    	edtcellSo.setWidth("90%");
			    	if(!"".equals(smf.getOrderLineLotno().getSo()) && smf.getOrderLineLotno().getSo() != null){
			    		edtcellSo.setText(smf.getOrderLineLotno().getSo().trim());
			    	}else{
			    		edtcellSo.setText("");
			    	}
			    	edtcellSo.setParent(cellLotnoSo);
			    	edtcellSo.addEventListener("onChange", new EventListener() {
						public void onEvent(Event inEvent) throws Exception{
							smf.getOrderLineLotno().setSo(edtcellSo.getText().trim());
						}
					});
			    }
		    
		    //SoLine
		    if (("readonly".equals(smf.getMode()))){
			    	if(!"".equals(smf.getOrderLineLotno().getSoLine()) && smf.getOrderLineLotno().getSoLine() != null){
			    		cellLotnoSoLine.setLabel(smf.getOrderLineLotno().getSoLine().trim());
			    	}else{
			    		cellLotnoSoLine.setLabel("");
			    	}
				}
			    else{
			    	edtcellSoLine.setId("edtcellSoLine"+inIndex);
			    	edtcellSoLine.setInplace(true);
			    	edtcellSoLine.setWidth("90%");
			    	if(!"".equals(smf.getOrderLineLotno().getSoLine()) && smf.getOrderLineLotno().getSoLine() != null){
			    		edtcellSoLine.setText(smf.getOrderLineLotno().getSoLine().trim());
			    	}else{
			    		edtcellSoLine.setText("");
			    	}
			    	edtcellSoLine.setParent(cellLotnoSoLine);
			    	edtcellSoLine.addEventListener("onChange", new EventListener() {
						public void onEvent(Event inEvent) throws Exception{
							smf.getOrderLineLotno().setSoLine(edtcellSoLine.getText().trim());
						}
					});
			    }
		    
		    //Wafer Data
		    cellLotnoWaferData.setId("cellLotnoWaferData"+inIndex);
		    if ("readonly".equals(smf.getMode())){
		    	if(!"".equals(smf.getOrderLineLotno().getWaferData()) && smf.getOrderLineLotno().getWaferData() != null){
		    		cellLotnoWaferData.setLabel(smf.getOrderLineLotno().getWaferData().trim());
		    	}else{
		    		cellLotnoWaferData.setLabel("");
		    	}
			}
			else {
				edtcellWaferData.setDisabled(true);
				edtcellWaferData.setId("edtcellWaferData"+inIndex);
				edtcellWaferData.setInplace(true);
				edtcellWaferData.setReadonly(true);
				//edtcellWaferData.setConstraint("/[0-9]+/");//IT-PR-141201
				if(!"".equals(smf.getOrderLineLotno().getWaferData()) && smf.getOrderLineLotno().getWaferData() != null){
					edtcellWaferData.setText(smf.getOrderLineLotno().getWaferData().trim());
				}else{
					edtcellWaferData.setText("");
				}
				edtcellWaferData.setParent(cellLotnoWaferData);
//				edtcellWaferData.addEventListener("onChange", new EventListener() {
//					public void onEvent(Event inEvent) throws Exception{
//						if ("".equals(edtcellWaferData.getText())){
//							smf.getOrderLineLotno().setWaferData("");
//						}
//						else{
//							smf.getOrderLineLotno().setWaferData(edtcellWaferData.getText());
//						}
//					}
//				});
				
				//OCF-PR-150202_新增Wafer Selection可選擇Wafer再帶到waferData欄位_Allison
				Button btnWaferData = new Button();
				btnWaferData.setLabel(Labels.getLabel("modules.oe.waferreceipt.render.waferData"));
				btnWaferData.setId("btnWaferData" + inIndex);
				edtcellWaferData.setStyle("background:#FFD");
				btnWaferData.setWidth("40%");
				edtcellWaferData.setDisabled(false);
				btnWaferData.setParent(cellLotnoWaferData);
				btnWaferData.addEventListener("onClick", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						Map args = new HashMap();
						edtcellWaferData.setFocus(false);
						edtcellWaferData.setConstraint("");
						args.put("waferData", edtcellWaferData);
						Window winWaferSelect = (Window)Executions.createComponents("/WEB-INF/modules/wafer/WaferSelection.zul", null, args);
						winWaferSelect.doModal();
						Label selectWaferData = (Label) winWaferSelect.getFellow("lblWaferData");
						edtcellWaferData.setText("");
						edtcellWaferData.setText(selectWaferData.getValue());
						if ("".equals(edtcellWaferData.getText())){
							smf.getOrderLineLotno().setWaferData("");
						}
						else{
							smf.getOrderLineLotno().setWaferData(edtcellWaferData.getText());
						}
					}
				});
			}


		    //WI RMA NO
		    if ("readonly".equals(smf.getMode())){
		    	if(!"".equals(smf.getOrderLineLotno().getWiRmaNo()) && smf.getOrderLineLotno().getWiRmaNo() != null){	
		    		cellLotnoWiRmaNo.setLabel(smf.getOrderLineLotno().getWiRmaNo().trim());
		    	}else{
		    		cellLotnoWiRmaNo.setLabel("");
		    	}
			}else{
			    	edtcellWiRmaNo.setId("edtcellWiRmaNo"+inIndex);
			    	edtcellWiRmaNo.setInplace(true);
			    	edtcellWiRmaNo.setWidth("90%");
			    	if(!"".equals(smf.getOrderLineLotno().getWiRmaNo()) && smf.getOrderLineLotno().getWiRmaNo() != null){
			    		edtcellWiRmaNo.setText(smf.getOrderLineLotno().getWiRmaNo().trim());
			    	}else{
			    		edtcellWiRmaNo.setText("");
			    	}
			    	edtcellWiRmaNo.setParent(cellLotnoWiRmaNo);
			    	edtcellWiRmaNo.addEventListener("onChange", new EventListener() {
						public void onEvent(Event inEvent) throws Exception{
							smf.getOrderLineLotno().setWiRmaNo(edtcellWiRmaNo.getText().trim());
						}
					});
			    }

			//WAFER DIE
			if ("readonly".equals(smf.getMode())){
				if("".equals(smf.getOrderLineLotno().getWaferDie()) || smf.getOrderLineLotno().getWaferDie() == null){
					cellLineWaferDie.setLabel("");
				}else{
					cellLineWaferDie.setLabel(smf.getOrderLineLotno().getWaferDie().trim());
				}
			}
			else{
				edtcellWaferDie.setId("edtcellWaferDie"+inIndex);
				edtcellWaferDie.setInplace(true);
				edtcellWaferDie.setWidth("90%");
				if("".equals(smf.getOrderLineLotno().getWaferDie()) || smf.getOrderLineLotno().getWaferDie() == null){
					edtcellWaferDie.setText("");
				}else{
					edtcellWaferDie.setText(smf.getOrderLineLotno().getWaferDie().trim());
				}
				edtcellWaferDie.setParent(cellLineWaferDie);
				edtcellWaferDie.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLineLotno().setWaferDie(edtcellWaferDie.getText());
					}
				});
			}
			
			//GRADE RECORD
			if ("readonly".equals(smf.getMode())){
				if("".equals(smf.getOrderLineLotno().getGradeRecord()) || smf.getOrderLineLotno().getGradeRecord() == null){
					cellLineGradeRecord.setLabel("");
				}else{
					cellLineGradeRecord.setLabel(smf.getOrderLineLotno().getGradeRecord().trim());
				}
			}
			else{
				edtcellGradeRecord.setId("edtcellGradeRecord"+inIndex);
				edtcellGradeRecord.setInplace(true);
				edtcellGradeRecord.setWidth("90%");
				if("".equals(smf.getOrderLineLotno().getGradeRecord()) || smf.getOrderLineLotno().getGradeRecord() == null){
					edtcellGradeRecord.setText("");
				}else{
					edtcellGradeRecord.setText(smf.getOrderLineLotno().getGradeRecord().trim());
				}
				edtcellGradeRecord.setParent(cellLineGradeRecord);
				edtcellGradeRecord.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLineLotno().setGradeRecord(edtcellGradeRecord.getText());
					}
				});
			}
			
			//ENG NO
			if ("readonly".equals(smf.getMode())){
				if("".equals(smf.getOrderLineLotno().getEngNo()) || smf.getOrderLineLotno().getEngNo() == null){
					cellLineEngNo.setLabel("");
				}else{
					cellLineEngNo.setLabel(smf.getOrderLineLotno().getEngNo().trim());
				}
			}
			else{
				edtcellEngNo.setId("edtcellEngNo"+inIndex);
				edtcellEngNo.setInplace(true);
				edtcellEngNo.setWidth("90%");
				if("".equals(smf.getOrderLineLotno().getEngNo()) || smf.getOrderLineLotno().getEngNo() == null){
					edtcellEngNo.setText("");
				}else{
					edtcellEngNo.setText(smf.getOrderLineLotno().getEngNo().trim());
				}
				edtcellEngNo.setParent(cellLineEngNo);
				edtcellEngNo.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLineLotno().setEngNo(edtcellEngNo.getText());
					}
				});
			}
			
			//TEST PROGRAM
			if ("readonly".equals(smf.getMode())){
				if("".equals(smf.getOrderLineLotno().getTestProgram()) || smf.getOrderLineLotno().getTestProgram() == null){
					cellLineTestProgram.setLabel("");
				}else{
					cellLineTestProgram.setLabel(smf.getOrderLineLotno().getTestProgram().trim());
				}
			}
			else{
				edtcellTestProgram.setId("edtcellTestProgram"+inIndex);
				edtcellTestProgram.setInplace(true);
				edtcellTestProgram.setWidth("90%");
				if("".equals(smf.getOrderLineLotno().getTestProgram()) || smf.getOrderLineLotno().getTestProgram() == null){
					edtcellTestProgram.setText("");
				}else{
					edtcellTestProgram.setText(smf.getOrderLineLotno().getTestProgram().trim());
				}
				edtcellTestProgram.setParent(cellLineTestProgram);
				edtcellTestProgram.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLineLotno().setTestProgram(edtcellTestProgram.getText());
					}
				});
			}
		    
		    //OE Internal Info 
		    btncellOeInternalNo.setId("btncellOeInternalNo"+inIndex);
		    btncellOeInternalNo.setWidth("90%");
		    btncellOeInternalNo.setLabel("OE Internal Info.");
		    btncellOeInternalNo.setParent(cellLotnoOeInternalNo);
		    btncellOeInternalNo.addEventListener("onClick", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					Map args = new HashMap();
					args.put("waferData", edtcellWaferData);
					args.put("OeData", smf);
					
					Window winOeInternalInfo = (Window)Executions.createComponents("/WEB-INF/modules/oe/OeInternalInfo.zul", null, args);
					winOeInternalInfo.doModal();
				}
			});
		    
		  //Packing List Print Disable OCF-PR-150302
			if ("readonly".equals(smf.getMode())){
				if(smf.getOrderLineLotno().isPackingListPrintDisable()){
					cellLotnoPackingListPrintDisable.setLabel("Y");
				}else{
					cellLotnoPackingListPrintDisable.setLabel("N");
				}
			}
			else{
				chkcellPackingListPrintDisable.setId("chkcellPackingListPrintDisable"+inIndex);
				chkcellPackingListPrintDisable.setWidth("90%");
				if(smf.getOrderLineLotno().isPackingListPrintDisable()){
					chkcellPackingListPrintDisable.setChecked(true);
				}else{
					chkcellPackingListPrintDisable.setChecked(false);
				}
				chkcellPackingListPrintDisable.setParent(cellLotnoPackingListPrintDisable);
				chkcellPackingListPrintDisable.addEventListener("onCheck", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLineLotno().setPackingListPrintDisable(chkcellPackingListPrintDisable.isChecked());
					}
				});
			}
			
			//Shipping Remark OCF-PR-150302
			if ("readonly".equals(smf.getMode())){
				if("".equals(smf.getOrderLineLotno().getShippingRemark()) || smf.getOrderLineLotno().getShippingRemark() == null){
					cellLotnoShippingRemark.setLabel("");
				}else{
					cellLotnoShippingRemark.setLabel(smf.getOrderLineLotno().getShippingRemark().trim());
				}
			}
			else{
				edtcellShippingRemark.setId("edtcellShippingRemark"+inIndex);
				edtcellShippingRemark.setInplace(true);
				edtcellShippingRemark.setWidth("90%");
				if("".equals(smf.getOrderLineLotno().getShippingRemark()) || smf.getOrderLineLotno().getShippingRemark() == null){
					edtcellShippingRemark.setText("");
				}else{
					edtcellShippingRemark.setText(smf.getOrderLineLotno().getShippingRemark().trim());
				}
				edtcellShippingRemark.setParent(cellLotnoShippingRemark);
				edtcellShippingRemark.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLineLotno().setShippingRemark(edtcellShippingRemark.getText());
					}
				});
			}
			
		//將each Litcell 放上ListItme上
		cellLotnoNo.setParent(inItem);
		cellLotnoPoItem.setParent(inItem);
		cellLotnoLotno.setParent(inItem);
		cellLotnoCustomerJob.setParent(inItem);
		cellLotnoWaferQty.setParent(inItem);
		cellLotnoPriority.setParent(inItem);
		cellLotnoLotType.setParent(inItem);
		cellLotnoSo.setParent(inItem);
		cellLotnoSoLine.setParent(inItem);
		cellLotnoWaferData.setParent(inItem);
		cellLotnoWiRmaNo.setParent(inItem);
		cellLineWaferDie.setParent(inItem);
		cellLineGradeRecord.setParent(inItem);
		cellLineEngNo.setParent(inItem);
		cellLineTestProgram.setParent(inItem);
		cellLotnoOeInternalNo.setParent(inItem);
		cellLotnoPackingListPrintDisable.setParent(inItem);//OCF-PR-150302
		cellLotnoShippingRemark.setParent(inItem);//OCF-PR-150302
		setMustbeInput(inItem);

	}
	
	/**
	 * 
	 * setMustbeInput:設定grdLine哪些欄位是Mustbe,將其變為淺黃色的底色及設定Constraint. <br/>
	 * TODO(這裡描述這個方法適用條件 – 可選).<br/>
	 * TODO(這裡描述這個方法的執行流程 – 可選).<br/>
	 * TODO(這裡描述這個方法的使用方法 – 可選).<br/>
	 * TODO(這裡描述這個方法的注意事項 – 可選).<br/>
	 *
	 * @author 030260
	 * @param inItem
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @since JDK 1.6
	 */
	public void setMustbeInput(Listitem inItem) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException{
		List<BaseCtrlSet> baseCtrlSetList = commonService.getBaseCtrlSetLists(this.getClass().getName());
		List<Component> headerlists = inItem.getFellowIfAny("listheadLotno").getChildren();
		
		List<String> headers = new ArrayList<String>();
		for (int i = 0; i < headerlists.size(); i++) {
			headers.add(headerlists.get(i).getId());
		}
		
		List<String> bases = new ArrayList<String>();
		for (int i = 0; i < baseCtrlSetList.size(); i++) {
			bases.add(baseCtrlSetList.get(i).getBeanColumnName());
		}

		//bases.retainAll(headers);
		
		for (int i = 0; i < baseCtrlSetList.size(); i++) {
			//log.debug("i="+i+","+baseCtrlSetList.get(i).getComponentName());
			
			for (int j = 0; j < bases.size(); j++) {
				//log.debug("j="+j+","+bases.get(j).toString());
				
				//log.debug("baseCtrlSetList.get(i).getBeanColumnName()="+baseCtrlSetList.get(i).getBeanColumnName());
				//log.debug("bases.get(j).toString()="+bases.get(j).toString());
				if (baseCtrlSetList.get(i).getBeanColumnName().equals(bases.get(j).toString())) {
					List<Component> cells = inItem.getChildren();
					for (int l = 0; l < cells.size(); l++) {
						//log.debug("l="+l+","+cells.get(l).getChildren());
						
						List<Component> cellchildrens = cells.get(l).getChildren();
						for (int k = 0; k < cellchildrens.size(); k++) {							
							//log.debug("k="+k+","+cellchildrens.get(k).getId());
							//log.debug("baseCtrlSetList.get(i).getComponentName()="+baseCtrlSetList.get(i).getComponentName());
							
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
						}//end for k
					}//end for l
				}//end if
			}//end for j
		}//end for i
	}

}
