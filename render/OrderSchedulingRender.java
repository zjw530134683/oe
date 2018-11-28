/*
 * Project Name:iVision
 * File Name:OrderSchedulingRender.java
 * Package Name:com.tce.ivision.modules.oe.render
 * Date:2012/12/29下午6:23:14
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.render;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.tce.ivision.modules.oe.model.OrderScheduling;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.DateUtil;
/**
 * 
 * ClassName: OrderSchedulingRender <br/>
 * date: 2012/12/29 下午6:32:17 <br/>
 *
 * @author honda
 * @version 
 * @since JDK 1.6
 */
public class OrderSchedulingRender implements ListitemRenderer<OrderScheduling> {
	/**
	 * Log4j Component
	 */
	public static Logger log = Logger.getLogger(OrderSchedulingRender.class);
	
	
	@Override
	public void render(Listitem inItem, OrderScheduling inData, int inIndex)
			throws Exception {
		final OrderScheduling orderScheduling = inData;
		if(orderScheduling.isShippingFlag()){
			inItem.setStyle("background:#FFD");
		}
		//宣告Listcell
		Listcell cellLotInfoOperation = new Listcell();
		Listcell cellLotInfoCustomer = new Listcell();
		Listcell cellLotInfoBilltoPo = new Listcell();//IT-PR-141201
		Listcell cellLotInfoPoNumber = new Listcell();
		Listcell cellLotInfoProduct = new Listcell();
		Listcell cellLotInfoRealProduct = new Listcell(); //2017.12.20
		Listcell cellLotInfoMtrlDesc = new Listcell();
		Listcell cellLotInfoLotNo = new Listcell();
		Listcell cellLotInfoWaferQty = new Listcell();
		Listcell cellLotInfoRequestDeliveryDate = new Listcell();
		Listcell cellLotInfoCommitDeliveryDate = new Listcell();
		Listcell cellLotInfoRescheduleDate = new Listcell();
		Listcell cellLotInfoOrderConfirmNumber = new Listcell();
		Listcell cellLotInfoCustomerLotNo1 = new Listcell();
		Listcell cellLotInfoCustomerLotNo2 = new Listcell();
		Listcell cellLotInfoCustomerLotNo3 = new Listcell();
		Listcell cellLotInfoCustomerLotNo4 = new Listcell();
		Listcell cellLotInfoCustomerLotNo5 = new Listcell();
		Listcell cellLotInfoCustomerLotNo6 = new Listcell();
		Listcell cellLotInfoCustomerLotNo7 = new Listcell();
		Listcell cellLotInfoCustomerLotNo8 = new Listcell();
		Listcell cellLotInfoCustomerLotNo9 = new Listcell();
		Listcell cellLotInfoCustomerLotNo10 = new Listcell();
		Listcell cellLotInfoCustomerLotNo11 = new Listcell();
		Listcell cellLotInfoCustomerLotNo12 = new Listcell();
		Listcell cellLotInfoCustomerLotNo13 = new Listcell();
		Listcell cellLotInfoCustomerLotNo14 = new Listcell();
		Listcell cellLotInfoCustomerLotNo15 = new Listcell();
		Listcell cellLotInfoCustomerLotNo16 = new Listcell();
		Listcell cellLotInfoCustomerLotNo17 = new Listcell();
		Listcell cellLotInfoCustomerLotNo18 = new Listcell();
		Listcell cellLotInfoCustomerLotNo19 = new Listcell();
		Listcell cellLotInfoCustomerLotNo20 = new Listcell();
		Listcell cellLotInfoCustomerLotNo21 = new Listcell();
		Listcell cellLotInfoCustomerLotNo22 = new Listcell();
		Listcell cellLotInfoCustomerLotNo23 = new Listcell();
		Listcell cellLotInfoCustomerLotNo24 = new Listcell();
		Listcell cellLotInfoCustomerLotNo25 = new Listcell();
		
		if(!orderScheduling.isShippingFlag()){
			Button btncellModify = new Button();
			btncellModify.setId("btncellModify"+inIndex);
			btncellModify.setImage("/images/edit.png");
			btncellModify.setParent(cellLotInfoOperation);
			btncellModify.addEventListener("onClick", new EventListener<Event>() {
				@Override
				public void onEvent(Event inEvent) throws Exception {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("lotNo",orderScheduling.getLotNo());
					Window wLotResultEdit = (Window)Executions.createComponents("/WEB-INF/modules/oe/OrderSchedulingLotResult.zul",null, map );
					wLotResultEdit.doModal();
				}
			});
		}
		
		cellLotInfoCustomer.setLabel(orderScheduling.getCustomerShortName());
		cellLotInfoBilltoPo.setLabel(orderScheduling.getBilltoPo());//IT-PR-141201
		cellLotInfoPoNumber.setLabel(orderScheduling.getPoNumber());
		cellLotInfoProduct.setLabel(orderScheduling.getProduct());
		cellLotInfoRealProduct.setLabel(orderScheduling.getRealProduct()); //2017.12.20
		cellLotInfoMtrlDesc.setLabel(orderScheduling.getMtrlDesc());
		cellLotInfoLotNo.setLabel(orderScheduling.getLotNo());
		cellLotInfoWaferQty.setLabel(String.valueOf(orderScheduling.getInputQty()));
		cellLotInfoRequestDeliveryDate.setLabel(orderScheduling.getRequestDate());

		
		if(!orderScheduling.isShippingFlag()){
			final Datebox dateboxCommitDeliveryDate = new Datebox();
			log.debug(orderScheduling.getCommitDeliveryDate());
			dateboxCommitDeliveryDate.setValue(orderScheduling.getCommitDeliveryDate());
			dateboxCommitDeliveryDate.setId("dateboxCommitDeliveryDate"+String.valueOf(inIndex));
			dateboxCommitDeliveryDate.setFormat(Labels.getLabel("format.date"));
			dateboxCommitDeliveryDate.setParent(cellLotInfoCommitDeliveryDate);
			
			dateboxCommitDeliveryDate.addEventListener("onChange", new EventListener<Event>() {
				public void onEvent(Event inEvent) throws Exception{
					if(dateboxCommitDeliveryDate.getValue() != null){
						SimpleDateFormat Format = new SimpleDateFormat("yyyy/MM/dd");					
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						Date RequestDate = dateFormat.parse(orderScheduling.getRequestDate());
						if(dateboxCommitDeliveryDate.getValue().before(RequestDate)){
							Messagebox.show(Labels.getLabel("modules.oe.OrderScheduling.ctrl.message.beforeRequireDate")+Format.format(RequestDate), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
						}
						orderScheduling.setCommitDeliveryDate(DateUtil.setTime(dateboxCommitDeliveryDate.getValue(),18,0,0));//自動設定時間為18：00
    				}else{
    					SimpleDateFormat Format = new SimpleDateFormat("yyyy/MM/dd");					
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						Date RequestDate = dateFormat.parse(orderScheduling.getRequestDate());
						if(dateboxCommitDeliveryDate.getValue().before(RequestDate)){
							Messagebox.show(Labels.getLabel("modules.oe.OrderScheduling.ctrl.message.beforeRequireDate")+Format.format(RequestDate), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
						}
    					orderScheduling.setCommitDeliveryDate(dateboxCommitDeliveryDate.getValue());
    				}
				}
			});
			
			//IT-PR-140616_Allison add
			final Datebox dateboxRescheduleDate = new Datebox();
			log.debug(orderScheduling.getRescheduleDate());
			dateboxRescheduleDate.setValue(orderScheduling.getRescheduleDate());
			dateboxRescheduleDate.setId("dateboxRescheduleDate"+String.valueOf(inIndex));
			dateboxRescheduleDate.setFormat(Labels.getLabel("format.date"));
			dateboxRescheduleDate.setParent(cellLotInfoRescheduleDate);
			
			dateboxRescheduleDate.addEventListener("onChange", new EventListener<Event>() {
				public void onEvent(Event inEvent) throws Exception{
					if(dateboxRescheduleDate.getValue() != null){
						SimpleDateFormat Format = new SimpleDateFormat("yyyy/MM/dd");					
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						Date RequestDate = dateFormat.parse(orderScheduling.getRequestDate());
						if(dateboxRescheduleDate.getValue().before(RequestDate)){
							Messagebox.show(Labels.getLabel("modules.oe.OrderScheduling.ctrl.message.beforeRequireDate")+Format.format(RequestDate), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
						}
						orderScheduling.setRescheduleDate(DateUtil.setTime(dateboxRescheduleDate.getValue(),18,0,0));//自動設定時間為18：00
    				}else{
    					SimpleDateFormat Format = new SimpleDateFormat("yyyy/MM/dd");					
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						Date RequestDate = dateFormat.parse(orderScheduling.getRequestDate());
						if(dateboxRescheduleDate.getValue().before(RequestDate)){
							Messagebox.show(Labels.getLabel("modules.oe.OrderScheduling.ctrl.message.beforeRequireDate")+Format.format(RequestDate), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
						}
    					orderScheduling.setRescheduleDate(dateboxRescheduleDate.getValue());
    				}
				}
			});			
			
		}else{
			if(orderScheduling.getCommitDeliveryDate() != null){
				cellLotInfoCommitDeliveryDate.setLabel(DateFormatUtil.getSimpleDateFormat().format(orderScheduling.getCommitDeliveryDate()));//2013.10.01
			}else{
				cellLotInfoCommitDeliveryDate.setLabel("-");
			}
			
			//IT-PR-140616_Allison add
			if(orderScheduling.getRescheduleDate() != null){
				cellLotInfoRescheduleDate.setLabel(DateFormatUtil.getSimpleDateFormat().format(orderScheduling.getRescheduleDate()));//2013.10.01
			}else{
				cellLotInfoRescheduleDate.setLabel("-");
			}
		}
		
		
		cellLotInfoOrderConfirmNumber.setLabel(orderScheduling.getOrderCfmNo());
		cellLotInfoCustomerLotNo1.setLabel(orderScheduling.getCustomerLotno1());
		cellLotInfoCustomerLotNo2.setLabel(orderScheduling.getCustomerLotno2());
		cellLotInfoCustomerLotNo3.setLabel(orderScheduling.getCustomerLotno3());
		cellLotInfoCustomerLotNo4.setLabel(orderScheduling.getCustomerLotno4());
		cellLotInfoCustomerLotNo5.setLabel(orderScheduling.getCustomerLotno5());
		cellLotInfoCustomerLotNo6.setLabel(orderScheduling.getCustomerLotno6());
		cellLotInfoCustomerLotNo7.setLabel(orderScheduling.getCustomerLotno7());
		cellLotInfoCustomerLotNo8.setLabel(orderScheduling.getCustomerLotno8());
		cellLotInfoCustomerLotNo9.setLabel(orderScheduling.getCustomerLotno9());
		cellLotInfoCustomerLotNo10.setLabel(orderScheduling.getCustomerLotno10());
		cellLotInfoCustomerLotNo11.setLabel(orderScheduling.getCustomerLotno11());
		cellLotInfoCustomerLotNo12.setLabel(orderScheduling.getCustomerLotno12());
		cellLotInfoCustomerLotNo13.setLabel(orderScheduling.getCustomerLotno13());
		cellLotInfoCustomerLotNo14.setLabel(orderScheduling.getCustomerLotno14());
		cellLotInfoCustomerLotNo15.setLabel(orderScheduling.getCustomerLotno15());
		cellLotInfoCustomerLotNo16.setLabel(orderScheduling.getCustomerLotno16());
		cellLotInfoCustomerLotNo17.setLabel(orderScheduling.getCustomerLotno17());
		cellLotInfoCustomerLotNo18.setLabel(orderScheduling.getCustomerLotno18());
		cellLotInfoCustomerLotNo19.setLabel(orderScheduling.getCustomerLotno19());
		cellLotInfoCustomerLotNo20.setLabel(orderScheduling.getCustomerLotno20());
		cellLotInfoCustomerLotNo21.setLabel(orderScheduling.getCustomerLotno21());
		cellLotInfoCustomerLotNo22.setLabel(orderScheduling.getCustomerLotno22());
		cellLotInfoCustomerLotNo23.setLabel(orderScheduling.getCustomerLotno23());
		cellLotInfoCustomerLotNo24.setLabel(orderScheduling.getCustomerLotno24());
		cellLotInfoCustomerLotNo25.setLabel(orderScheduling.getCustomerLotno25());
		
		//將each Litcell 放上ListItme上
		cellLotInfoOperation.setParent(inItem);
		cellLotInfoCustomer.setParent(inItem);
		cellLotInfoBilltoPo.setParent(inItem);//IT-PR-141201
		cellLotInfoPoNumber.setParent(inItem);
		cellLotInfoProduct.setParent(inItem);
		cellLotInfoRealProduct.setParent(inItem); //2017.12.20
		cellLotInfoMtrlDesc.setParent(inItem);
		cellLotInfoLotNo.setParent(inItem);
		cellLotInfoWaferQty.setParent(inItem);
		cellLotInfoRequestDeliveryDate.setParent(inItem);
		cellLotInfoCommitDeliveryDate.setParent(inItem);
		cellLotInfoRescheduleDate.setParent(inItem);
		cellLotInfoOrderConfirmNumber.setParent(inItem);
		cellLotInfoCustomerLotNo1.setParent(inItem);
		cellLotInfoCustomerLotNo2.setParent(inItem);
		cellLotInfoCustomerLotNo3.setParent(inItem);
		cellLotInfoCustomerLotNo4.setParent(inItem);
		cellLotInfoCustomerLotNo5.setParent(inItem);
		cellLotInfoCustomerLotNo6.setParent(inItem);
		cellLotInfoCustomerLotNo7.setParent(inItem);
		cellLotInfoCustomerLotNo8.setParent(inItem);
		cellLotInfoCustomerLotNo9.setParent(inItem);
		cellLotInfoCustomerLotNo10.setParent(inItem);
		cellLotInfoCustomerLotNo11.setParent(inItem);
		cellLotInfoCustomerLotNo12.setParent(inItem);
		cellLotInfoCustomerLotNo13.setParent(inItem);
		cellLotInfoCustomerLotNo14.setParent(inItem);
		cellLotInfoCustomerLotNo15.setParent(inItem);
		cellLotInfoCustomerLotNo16.setParent(inItem);
		cellLotInfoCustomerLotNo17.setParent(inItem);
		cellLotInfoCustomerLotNo18.setParent(inItem);
		cellLotInfoCustomerLotNo19.setParent(inItem);
		cellLotInfoCustomerLotNo20.setParent(inItem);
		cellLotInfoCustomerLotNo21.setParent(inItem);
		cellLotInfoCustomerLotNo22.setParent(inItem);
		cellLotInfoCustomerLotNo23.setParent(inItem);
		cellLotInfoCustomerLotNo24.setParent(inItem);
		cellLotInfoCustomerLotNo25.setParent(inItem);
		
		
	}

}
