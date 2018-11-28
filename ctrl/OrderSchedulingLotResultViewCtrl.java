/*
 * Project Name:iVision
 * File Name:OrderSchedulingLotResultViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2013/10/1下午1:16:16
 * 
 * 說明:
 * 用來設定LOT_NO底下的各iNaviLotNo(型態為A或B)的交期
 * 
 * 修改歷史:
 * 2013-10-01 OE-003_v1.3 Honda  Initialize
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.LotResult;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.oe.render.OrderSchedulingLotResultRender;
import com.tce.ivision.modules.oe.service.OrderSchedulingService;

/**
 * ClassName: OrderSchedulingLotResultViewCtrl <br/>
 * date: 2013/10/1 下午1:16:16 <br/>
 *
 * @author honda
 * @version 
 * @since JDK 1.6
 */
public class OrderSchedulingLotResultViewCtrl extends BaseViewCtrl {

	/**
	 * serialVersionUID:
	 * @since JDK 1.6
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Zk window component
	 */
	private Window winOrderSchedulingLotResult;
	/**
	 * Zk button component
	 */
	private Button btnExit;
	/**
	 * Zk button component
	 */
	private Button btnSave;
	/**
	 * Zk listheader component
	 */
	private Listheader headerLotResultSplitLotNo;
	/**
	 * Zk listheader component
	 */
	private Listheader headerLotResultSplitReq;
	/**
	 * Zk listheader component
	 */
	private Listheader headerLotResultCommitDeliveryDate;
	/**
	 * Zk listheader component
	 */
	private Listheader headerLotResultRescheduleDate;
	/**
	 * Zk listheader component
	 */
	private Listheader headerLotResultWaferQty;
	/**
	 * Zk listheader component
	 */
	private Listheader headerLotResultInaviLotNo;
	/**
	 * Zk listheader component
	 */
	private Listheader headerLotResultLotNo;
	/**
	 * Zk listheader component
	 */
	private Listbox listboxLotResult;
	/**
	 * Zk caption component
	 */
	private Caption captionLotResult;
	/**
	 * lot no
	 */
	private String lotNo;
	/**
	 * lot result list
	 */
	private List<LotResult> lotResults;

	/**
	 * OrderSchedulingService
	 */
	private OrderSchedulingService orderSchedulingService = (OrderSchedulingService) SpringUtil.getBean("orderSchedulingService");
	/**
	 * 頁面載入後開始執行初始化的function
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		lotNo = (String)arg.get("lotNo");
		lotResults = orderSchedulingService.getLotResultByLotNoOnlyABType(lotNo);
		listboxLotResult.setModel(new ListModelList<LotResult>(lotResults));
		listboxLotResult.setItemRenderer(new OrderSchedulingLotResultRender());

	}
	/**
	 * 
	 * onClick$btnSave:btnSave onClick event <br/>
	 *
	 * @author honda
	 * @since JDK 1.6
	 */
	public void onClick$btnSave(){
		Messagebox.show(Labels.getLabel("common.message.saveconfirm"),
				"Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION, 
				new org.zkoss.zk.ui.event.EventListener<Event>(){
				    public void onEvent(Event inEvt){
				    	if (Messagebox.ON_OK.equals(inEvt.getName())){
				    		orderSchedulingService.updateOrderSchedulingLotResult(lotResults);
				    		Messagebox.show(Labels.getLabel("common.message.opeartion.success"), "Information", Messagebox.OK, Messagebox.INFORMATION);				    		
				    	}
				    }
				}
			);
	}
	/**
	 * 
	 * onClick$btnExit:btnExit onClick event <br/>
	 *
	 * @author honda
	 * @since JDK 1.6
	 */
	public void onClick$btnExit(){
		winOrderSchedulingLotResult.onClose();
	}

	/**
	 * 初始化多語系LABEL
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		winOrderSchedulingLotResult.setTitle(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.winOrderSchedulingLotResult"));
		btnExit.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.btnExit"));
		btnSave.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.btnSave"));
		headerLotResultSplitLotNo.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.headerLotResultSplitLotNo"));
		headerLotResultSplitReq.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.headerLotResultSplitReq"));
		headerLotResultCommitDeliveryDate.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.headerLotResultCommitDeliveryDate"));
		headerLotResultRescheduleDate.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.headerLotResultRescheduleDate"));
		headerLotResultWaferQty.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.headerLotResultWaferQty"));
		headerLotResultInaviLotNo.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.headerLotResultInaviLotNo"));
		headerLotResultLotNo.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.headerLotResultLotNo"));
		captionLotResult.setLabel(Labels.getLabel("modules.oe.OrderSchedulingLotResult.ctrl.label.captionLotResult"));		
	}

	/**
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
		
	}

}
