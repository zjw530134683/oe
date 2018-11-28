/*
 * Project Name:iVision_PR160307
 * File Name:OeOrderNoConfirmViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2016/6/22下午3:44:36
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
import java.util.LinkedHashMap;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.tce.ivision.model.EmplInfo;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.OrderInternalCheckInfo;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.oe.model.OeOrderNoConfirmModel;
import com.tce.ivision.modules.oe.model.OrderEntryLotnoModel;
import com.tce.ivision.modules.oe.service.OrderEntryService;
import com.tce.ivision.units.common.DateFormatUtil;

/**
 * ClassName: OeOrderNoConfirmViewCtrl <br/>
 * date: 2016/6/22 下午3:44:36 <br/>
 *
 * @author 130707
 * @version 
 * @since JDK 1.6
 */
public class OeOrderNoConfirmViewCtrl extends BaseViewCtrl implements ListitemRenderer {

	private Window winOeOrderNoConfirm;
	private Button btnExit;
	private Button btnSave;
	private Label edtConfirmDate;
	private Label lblConfirmDate;
	private Label edtConfirmUser;
	private Label lblConfirmUser;
	private Listheader headerWaferData;
	private Listheader headerCustomerLotNo;
	private Listheader headerCustomerJob;
	private Listheader headerProduct;
	private Listheader headerOrderNo;
	private Listheader headerSelect;
	private Listbox listboxOeOrderNoConfirm;

	public LinkedHashMap<String, String> list = new LinkedHashMap<String, String>();
	public List<OeOrderNoConfirmModel> oeOrderNoConfirmModel = new ArrayList<OeOrderNoConfirmModel>();
	
	public String function="";//OCF-PR-160702 add 
	
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
		function = (String) execution.getArg().get("Function");
		
		//先列出檢查到的Order
		list = (LinkedHashMap<String, String>) execution.getArg().get("errMsg");
		String thisOrderNumber = (String) execution.getArg().get("thisOrderNumber");
		String thisProduct = (String) execution.getArg().get("thisProduct");
		String thisCustomerLotNo = (String) execution.getArg().get("thisCustomerLotNo");
		
		if(list.size() > 0){
			String msg = list.get("msg");
			String[] splitMsg = msg.split("\r\n");
			if(splitMsg.length > 0){
				for(int i=0; i<splitMsg.length; i++){
					String[] splitOeInfo = splitMsg[i].split(" / ");
					if(splitOeInfo.length > 0){
						if(oeOrderNoConfirmModel.size() > 0){
							boolean checkflag = false;
							int idx = 0;
							
							for(int k=0; k<oeOrderNoConfirmModel.size(); k++){
								if(oeOrderNoConfirmModel.get(k).getOrderNumber().equals(splitOeInfo[2]) && oeOrderNoConfirmModel.get(k).getCustomerLotNo().equals(splitOeInfo[0])){
									checkflag = true;
									idx = k;
								}
							}
							if(checkflag){
								oeOrderNoConfirmModel.get(idx).setWaferData(oeOrderNoConfirmModel.get(idx).getWaferData()+";"+splitOeInfo[1]);
							}else{
								OeOrderNoConfirmModel data = orderEntryService.getOeDatasByOrderNumber(splitOeInfo[2], splitOeInfo[0]);
								data.setOrderNumber(splitOeInfo[2]);
								data.setWaferData(splitOeInfo[1]);
								
								oeOrderNoConfirmModel.add(data);	
							}
						}else{
							OeOrderNoConfirmModel data = orderEntryService.getOeDatasByOrderNumber(splitOeInfo[2], splitOeInfo[0]);
							data.setOrderNumber(splitOeInfo[2]);
							data.setWaferData(splitOeInfo[1]);
							
							oeOrderNoConfirmModel.add(data);	
						}
					}
				}
			}
		}
		
		if(!"".equals(thisOrderNumber) && thisOrderNumber != null){
			if(oeOrderNoConfirmModel.size() > 0){
				for(int i=oeOrderNoConfirmModel.size()-1; i>=0; i--){
					if(oeOrderNoConfirmModel.get(i).getOrderNumber().equals(thisOrderNumber)){
						oeOrderNoConfirmModel.remove(i);
					}
				}
			}
		}
		
		//再列出這個畫面的Order
		if("OE".equals(function)){
		List<OrderEntryLotnoModel> saveOrderEntryLotnoModels = (List<OrderEntryLotnoModel>) execution.getArg().get("saveOrderEntryLotnoModels");
		OeOrderNoConfirmModel data = new OeOrderNoConfirmModel();
		if(!"".equals(thisOrderNumber) && thisOrderNumber != null){
			data.setOrderNumber(thisOrderNumber);
		}else{
			data.setOrderNumber("New Order");
		}
		data.setProduct(thisProduct);
		data.setCustomerJob(saveOrderEntryLotnoModels.get(0).getOrderLineLotno().getCustomerJob());
		data.setCustomerLotNo(saveOrderEntryLotnoModels.get(0).getOrderLineLotno().getCustomerLotno());
		
		String composeDuplicateWaferData = "";
		if(oeOrderNoConfirmModel.size() > 0){
			for(int i=0; i<oeOrderNoConfirmModel.size(); i++){
				composeDuplicateWaferData = composeDuplicateWaferData + oeOrderNoConfirmModel.get(i).getWaferData() + ";";
			}
		}
		data.setWaferData(composeDuplicateWaferData.substring(0, composeDuplicateWaferData.length()-1));
		
		oeOrderNoConfirmModel.add(data);
		}else if("Maintenance".equals(function)){
			OeOrderNoConfirmModel data = new OeOrderNoConfirmModel();
			if(!"".equals(thisOrderNumber) && thisOrderNumber != null){
				data.setOrderNumber(thisOrderNumber);
			}else{
				data.setOrderNumber("New Order");
			}
			
			OrderHeader orderHeader = orderEntryService.getOrderHeaderByOrderNumber(thisOrderNumber);
			data.setProduct(orderHeader.getProduct());
			
			List<OrderLineLotno> orderLineLotnos = orderEntryService.getOrderLineLotnosByOrderNumberAndCustomerLotNo(thisOrderNumber, thisCustomerLotNo);
			if(orderLineLotnos.size() > 0){
				data.setCustomerJob(orderLineLotnos.get(0).getCustomerJob());
			}
			data.setCustomerLotNo(thisCustomerLotNo);
			
			String composeDuplicateWaferData = "";
			if(oeOrderNoConfirmModel.size() > 0){
				for(int i=0; i<oeOrderNoConfirmModel.size(); i++){
					composeDuplicateWaferData = composeDuplicateWaferData + oeOrderNoConfirmModel.get(i).getWaferData() + ";";
				}
			}
			data.setWaferData(composeDuplicateWaferData.substring(0, composeDuplicateWaferData.length()-1));
			
			oeOrderNoConfirmModel.add(data);
		}
		
		ListModel<OeOrderNoConfirmModel> model = new ListModelList<OeOrderNoConfirmModel>(oeOrderNoConfirmModel);
		listboxOeOrderNoConfirm.setCheckmark(false);
		//listboxOeOrderNoConfirm.setMultiple(false);
		listboxOeOrderNoConfirm.setModel(model);
		listboxOeOrderNoConfirm.setItemRenderer(this);
		listboxOeOrderNoConfirm.renderAll();
		listboxOeOrderNoConfirm.setCheckmark(true);
		//listboxOeOrderNoConfirm.setMultiple(true);
		
		EmplInfo confirmUser = userService.getEmplInfoByEmplId(loginId);
		if(confirmUser != null){
			edtConfirmUser.setValue(confirmUser.getEmplFamilyname()+confirmUser.getEmplFirstname());
		}else{
			edtConfirmUser.setValue("");
		}
		edtConfirmDate.setValue(DateFormatUtil.getDateTimeFormatHHmm().format(new Date()));
	}

	public void onClick$btnExit(){
		winOeOrderNoConfirm.detach();
	}
	
	public void onClick$btnSave(){
		winOeOrderNoConfirm.getParent().setAttribute("oeOrderNoConfirmModel", oeOrderNoConfirmModel);//將oeOrderNoConfirmModel傳遞參數給Order Entry畫面
		winOeOrderNoConfirm.detach();
	}
	
	public void onSelect$listboxOeOrderNoConfirm(){
		if(oeOrderNoConfirmModel.size() > 0){
			for(int i=0; i<oeOrderNoConfirmModel.size(); i++){
				oeOrderNoConfirmModel.get(i).setSelect(listboxOeOrderNoConfirm.getItemAtIndex(i).isSelected());
			}
		}
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
		final OeOrderNoConfirmModel OeOrderNoConfirmModel = (OeOrderNoConfirmModel) inData;
		
		final Listcell cellSelect = new Listcell();
		final Listcell cellOrderNo = new Listcell();
		final Listcell cellProduct = new Listcell();
		final Listcell cellCustomerJob = new Listcell();
		final Listcell cellCustomerLotNo = new Listcell();
		final Listcell cellWaferData = new Listcell();
		
		//ORDER NO
		cellOrderNo.setLabel(OeOrderNoConfirmModel.getOrderNumber());
		
		//PRODUCT
		cellProduct.setLabel(OeOrderNoConfirmModel.getProduct());
		
		//CUSTOMER JOB
		cellCustomerJob.setLabel(OeOrderNoConfirmModel.getCustomerJob());
		
		//CUSTOMER LOTNO
		cellCustomerLotNo.setLabel(OeOrderNoConfirmModel.getCustomerLotNo());
		
		//WAFER DATA
		cellWaferData.setLabel(OeOrderNoConfirmModel.getWaferData());
		
		cellSelect.setParent(inItem);
		cellOrderNo.setParent(inItem);
		cellProduct.setParent(inItem);
		cellCustomerJob.setParent(inItem);
		cellCustomerLotNo.setParent(inItem);
		cellWaferData.setParent(inItem);
	}
}
