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

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;

import com.tce.ivision.model.LotResult;
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
public class OrderSchedulingLotResultRender implements ListitemRenderer<LotResult> {
	/**
	 * Log4j Component
	 */
	public static Logger log = Logger.getLogger(OrderSchedulingLotResultRender.class);
	
	
	@Override
	public void render(Listitem inItem, LotResult inData, int inIndex)
			throws Exception {
		final LotResult lotResult = inData;
		if(lotResult.isShippingFlag()){
			inItem.setStyle("background:#FFD");
		}
		//宣告Listcell
		Listcell cellLotResultLotNo = new Listcell();
		Listcell cellLotResultInaviLotNo = new Listcell();
		Listcell cellLotResultWaferQty = new Listcell();
		Listcell cellLotResultCommitDeliveryDate = new Listcell();
		Listcell cellLotResultRescheduleDate = new Listcell();
		Listcell cellLotResultSplitReq = new Listcell();
		Listcell cellLotResultSplitLotNo = new Listcell();		
		
		
		
		cellLotResultLotNo.setLabel(lotResult.getLotNo());
		cellLotResultInaviLotNo.setLabel(lotResult.getInaviLotno());
		cellLotResultWaferQty.setLabel(String.valueOf(lotResult.getWaferNum()));

		
		if(!lotResult.isShippingFlag()){
			final Datebox dateboxCommitDeliveryDate = new Datebox();
			dateboxCommitDeliveryDate.setValue(lotResult.getCommitDeliveryDate());
			dateboxCommitDeliveryDate.setId("dateboxCommitDeliveryDateLotResult"+String.valueOf(inIndex));
			dateboxCommitDeliveryDate.setFormat(Labels.getLabel("format.date"));
			dateboxCommitDeliveryDate.setParent(cellLotResultCommitDeliveryDate);
			
			dateboxCommitDeliveryDate.addEventListener("onChange", new EventListener<Event>() {
				public void onEvent(Event inEvent) throws Exception{
					if(dateboxCommitDeliveryDate.getValue() != null){
						lotResult.setCommitDeliveryDate(DateUtil.setTime(dateboxCommitDeliveryDate.getValue(),18,0,0));//自動設定時間為18：00
    				}else{
    					lotResult.setCommitDeliveryDate(dateboxCommitDeliveryDate.getValue());
    				}
				}
			});
			
			//IT-PR-140616_Allison add
			final Datebox dateboxRescheduleDate = new Datebox();
			dateboxRescheduleDate.setValue(lotResult.getRescheduleDate());
			dateboxRescheduleDate.setId("dateboxRescheduleDate"+String.valueOf(inIndex));
			dateboxRescheduleDate.setFormat(Labels.getLabel("format.date"));
			dateboxRescheduleDate.setParent(cellLotResultRescheduleDate);
			
			dateboxRescheduleDate.addEventListener("onChange", new EventListener<Event>() {
				public void onEvent(Event inEvent) throws Exception{
					if(dateboxRescheduleDate.getValue() != null){
						lotResult.setRescheduleDate(DateUtil.setTime(dateboxRescheduleDate.getValue(),18,0,0));//自動設定時間為18：00
    				}else{
    					lotResult.setRescheduleDate(dateboxRescheduleDate.getValue());
    				}
				}
			});
		}else{
			if(lotResult.getCommitDeliveryDate() != null){
				cellLotResultCommitDeliveryDate.setLabel(DateFormatUtil.getSimpleDateFormat().format(lotResult.getCommitDeliveryDate()));
			}else{
				cellLotResultCommitDeliveryDate.setLabel("");
			}
			
			//IT-PR-140616_Allison add
			if(lotResult.getRescheduleDate() != null){
				cellLotResultRescheduleDate.setLabel(DateFormatUtil.getSimpleDateFormat().format(lotResult.getRescheduleDate()));
			}else{
				cellLotResultRescheduleDate.setLabel("");
			}
		}
		
		if(lotResult.isDivideFlag() == true){
			//if(!"".equals(lotResult.getSplitRequest())){
				cellLotResultSplitReq.setLabel("Yes");
				cellLotResultSplitReq.setStyle("color:#FF00FF;");
			//}else{
			//	cellLotResultSplitReq.setLabel("");
			//}
		}else{
			cellLotResultSplitReq.setLabel("");
		}
		if(lotResult.getSplitLotno() != null){
			if(!"".equals(lotResult.getSplitLotno())){
				cellLotResultSplitLotNo.setLabel(lotResult.getSplitLotno());
				cellLotResultSplitLotNo.setStyle("color:#FF00FF;");
			}else{
				cellLotResultSplitLotNo.setLabel("");
			}
		}else{
			cellLotResultSplitLotNo.setLabel("");
		}
		
		//將each Litcell 放上ListItme上
		cellLotResultLotNo.setParent(inItem);
		cellLotResultInaviLotNo.setParent(inItem);
		cellLotResultWaferQty.setParent(inItem);
		cellLotResultCommitDeliveryDate.setParent(inItem);
		cellLotResultRescheduleDate.setParent(inItem);
		cellLotResultSplitReq.setParent(inItem);
		cellLotResultSplitLotNo.setParent(inItem);
		
		
	}

}
