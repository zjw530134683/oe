/*
 * Project Name:iVision
 * File Name:OrderLineB2BViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2012/12/20下午5:29:40
 * 
 * 說明:
 * User選擇copy orderdata from B2B
 * 
 * 修改歷史:
 * 2012.12.17 OCF#OE002 Fanny Initial
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.util.Clients;  //2017.12.20


import com.tce.ivision.model.B2bInfo;
import com.tce.ivision.units.common.FtpUtilUseFtp4j;
import com.tce.ivision.config.Config;
import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.model.OrderLineInt;
import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.oe.render.OrderLineIntRender;
import com.tce.ivision.modules.oe.service.OrderEntryService;
import com.tce.ivision.units.common.CsvUtil;
import com.tce.ivision.units.common.TxtUtil;
import com.tce.ivision.units.common.ZkComboboxControl;

/**
 * ClassName: OrderLineB2BViewCtrl <br/>
 * date: 2012/12/20 下午5:29:40 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderLineB2BViewCtrl extends BaseViewCtrl {
	/**
	 * Logger
	 */
	public static Logger log = Logger.getLogger(OrderLineB2BViewCtrl.class);
	
	/**
	 * CustomerID
	 */
	private String customerId;
	
	/**
	 * zk component:Window winOrderLineCopyFromB2B
	 */
	private Window winOrderLineCopyFromB2B;
	
	/**
	 * zk component:Button btnCopy
	 */
	private Button btnCopy;
	
	/**
	 * zk component:Listheader colb2bPoNum
	 */
	private Listheader colb2bPoNum;
	
	/**
	 * zk component:Listheader colb2bPoItem
	 */
	private Listheader colb2bPoItem;
	
	/**
	 * zk component:Listheader colb2bMtrlDesc
	 */
	private Listheader colb2bMtrlDesc;
	
	/**
	 * zk component:Listheader colb2bMtrlNumMtrlGroup
	 */
	private Listheader colb2bMtrlNumMtrlGroup;
	
	/**
	 * zk component:Listheader colb2bWaferQty
	 */
	private Listheader colb2bWaferQty;
	
	/**
	 * zk component:Listheader colb2bDelivDate
	 */
	private Listheader colb2bDelivDate;
	
	/**
	 * zk component:Listheader colb2bDesignId
	 */
	private Listheader colb2bDesignId;
	
	/**
	 * zk component:Listheader colb2bShipToVendorName
	 */
	private Listheader colb2bShipToVendorName;
	
	/**
	 * zk component:Listheader colb2bShipComment
	 */
	private Listheader colb2bShipComment;
	
	/**
	 * zk component:Listheader colb2bFilename
	 */
	private Listheader colb2bFilename;
	
	/**
	 * zk component:Listheader colb2bImportDate
	 */
	private Listheader colb2bImportDate;
	
	/**
	 * zk component:Listheader colb2bCountryOfFab
	 */
	private Listheader colb2bCountryOfFab;
	
	/**
	 * zk component:Listheader colb2bFab
	 */
	private Listheader colb2bFab;
	
	/**
	 * zk component:Listheader colb2bUnitPrice
	 */
	private Listheader colb2bUnitPrice;
	
	/**
	 * zk component:Listheader colb2bTPrice
	 */
	private Listheader colb2bTPrice;
	
	/**
	 * zk component:Listbox grdLineInt
	 */
	private Listbox grdLineInt;
	
	/**
	 * zk component:Textbox edtCustomerPo
	 */
	private Textbox edtCustomerPo;
	
	/**
	 * zk component:Label lblCustomerPo
	 */
	private Label lblCustomerPo;
	
	/**
	 * zk component:Textbox edtCustomer
	 */
	private Textbox edtCustomer;
	
	/**
	 * zk component:Label lblCustomer
	 */
	private Label lblCustomer;
	
	/**
	 * zk component:Caption grbCondition
	 */
	private Caption grbCondition;
	
	//以下是來自OrderEntry.zul的物件
	/**
	 * zk component:Listbox grdLineIntPa 要回傳至前一個畫面的元件
	 * 前一個畫面也有一個一樣名稱的元件，且會同步
	 */
	private Listbox grdLineIntPa;
	
	/**
	 * zul component:查詢條件 Datebox dtbImportDateE
	 */
	private Datebox dtbImportDateE;
	
	/**
	 * zul component:查詢條件 Datebox dtbImportDateS
	 */
	private Datebox dtbImportDateS;
	
	/**
	 * zul component:Label lblImportDate
	 */
	private Label lblImportDate;
	
	/**
	 * zul component:Button btnSearch
	 */
	private Button btnSearch;
	
	private Button btnImport;
	
	private Media[] medias;
	
	/**
	 * zk component:Combobox cbxSelectOrderLineIntIdxPa 要回傳至前一個畫面的元件
	 * 前一個畫面也有一個一樣名稱的元件，且會同步
	 */
	private Combobox cbxSelectOrderLineIntIdxPa;
	
	//Spring
	/**
	 * OrderEntryService
	 */
	private OrderEntryService orderEntryService = (OrderEntryService) SpringUtil.getBean("orderEntryService");
	
	/**
	 * config bean, in this case, i use the LocalTmpFileDir parameter
	 */
	private Config config = (Config) SpringUtil.getBean("config");
	
	//Java Bean
	/**
	 * 依據Customer+PO Number,Customer 找出來的ORDER_LINE_INT data
	 */
	private List<OrderLineInt> orderLineInts;
	
	private String tmpFileFolder = "";
	
	private File localFullFile;
	
	/**
	 * CustomerInformationService
	 */
	private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil.getBean("customerInformationService");
	/**
	 * customerTable List 用於接取query出來的customerTable Bean
	 */
	private List<CustomerTable> customerTableList;
	
	private boolean excelType;
	
	List<OrderLineInt> orderLineIntLists=new ArrayList<OrderLineInt>();
	//List<WaferBankinInt> waferBankinIntLists=new ArrayList<WaferBankinInt>();//OCF-PR-150202_Allison mark
	
	private Date importDateS;//IT-PR-141008_Allison add
	private Date importDateE;//IT-PR-141008_Allison add
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//IT-PR-141008_Allison add
	
	private String userId="";

	/**
	 *
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		userId=loginId;
		cbxSelectOrderLineIntIdxPa = new Combobox();
		btnCopy.setImage("images/icons/page_copy.png");
		btnSearch.setImage("/images/icons/magnifier.png");
		btnImport.setUpload("multiple=false");
		btnImport.setImage("/images/icons/application_get.png");
		
		dtbImportDateS.setValue(new Date());//OCF-PR-151002_預設帶當天的日期_需求by PC-Vicky
		dtbImportDateE.setValue(new Date());//OCF-PR-151002_預設帶當天的日期_需求by PC-Vicky
		
		tmpFileFolder = config.getLocalTmpFileDir()+"/"+"OVTWIReportReceive/";
		//若暫存檔資料夾不存在則建立
		File tmpB2bFolder=new File(tmpFileFolder);
		if(!tmpB2bFolder.exists()){
			tmpB2bFolder.mkdir();
		}
	}
	
	public void onCreate$winOrderLineCopyFromB2B(Event inEvent){
		//data from parent
		CreateEvent ce = (CreateEvent) ((ForwardEvent) inEvent).getOrigin();
        Map params = ce.getArg();//透過該事件的 getArg() 取的傳過來的 Map 集合物件
        edtCustomer.setText((String) params.get("customer"));
        customerId=(String) params.get("customerId");
      	edtCustomerPo.setText((String) params.get("customerPo"));
      	grdLineIntPa = (Listbox) params.get("grdLineIntPa");
      	cbxSelectOrderLineIntIdxPa = (Combobox) params.get("cbxSelectOrderLineIntIdxPa");
      	
      	if (!"".equals(edtCustomerPo.getText())){//如果CustomerPO是空值就不用找了，讓User自行加上ImportDate去尋找B2b data
      		//orderLineInts=orderEntryService.getOrderLineInts(customerId, edtCustomerPo.getText(),"","");
      		
      		//OCF-PR-151002_預設加上Import Start & End Date來搜尋
      		String conImportDateS=dtbImportDateS.getText()+" 00:00:00";
    		String conImportDateE=dtbImportDateE.getText()+" 23:59:59";
    		orderLineInts=orderEntryService.getOrderLineInts(customerId, edtCustomerPo.getText(),conImportDateS,conImportDateE);
      		if (orderLineInts.size()>0){
    			btnCopy.setDisabled(false);
    			grdLineInt.setMultiple(false);
    			grdLineInt.setModel(new ListModelList(orderLineInts));
    			grdLineInt.setItemRenderer(new OrderLineIntRender());
    			grdLineInt.setMultiple(true);
    		}
    		else{
    			btnCopy.setDisabled(true);
    		}
      	}
      	else{
      		btnCopy.setDisabled(true);
      	}
	}

	/**
	 *  設定畫面上Label Caption...
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		//Window
		winOrderLineCopyFromB2B.setTitle(Labels.getLabel("oe.b2b.winOrderLineCopyFromB2B"));
		
		//Button
		btnCopy.setLabel(Labels.getLabel("oe.b2b.btnCopy"));
		
		//Header
		/*colb2bPoItem.setLabel(Labels.getLabel("oe.b2b.colb2bPoItem"));
		colb2bSourceMtrlNum.setLabel(Labels.getLabel("oe.b2b.colb2bSourceMtrlNum"));
		colb2bMtrlNum.setLabel(Labels.getLabel("oe.b2b.colb2bMtrlNum"));
		colb2bMtrlDesc.setLabel(Labels.getLabel("oe.b2b.colb2bMtrlDesc"));
		colb2bMtrlNumMtrlGroup.setLabel(Labels.getLabel("oe.b2b.colb2bMtrlNumMtrlGroup"));
		colb2bWaferQty.setLabel(Labels.getLabel("oe.b2b.colb2bWaferQty"));
		colb2bDelivDate.setLabel(Labels.getLabel("oe.b2b.colb2bDelivDate"));
		colb2bDesignId.setLabel(Labels.getLabel("oe.b2b.colb2bDesignId"));
		colb2bDesignId.setLabel(Labels.getLabel("oe.b2b.colb2bDesignId"));
		colb2bCountryOfFab.setLabel(Labels.getLabel("oe.b2b.colb2bCountryOfFab"));
		colb2bFab.setLabel(Labels.getLabel("oe.b2b.colb2bFab"));
		colb2bWaferSize.setLabel(Labels.getLabel("oe.b2b.colb2bWaferSize"));
		colb2bShipToVendorCode.setLabel(Labels.getLabel("oe.b2b.colb2bShipToVendorCode"));
		colb2bShipToVendorName.setLabel(Labels.getLabel("oe.b2b.colb2bShipToVendorName"));
		colb2bShipComment.setLabel(Labels.getLabel("oe.b2b.colb2bShipComment"));
		//colb2bCreateDate.setLabel(Labels.getLabel("oe.b2b.colb2bCreateDate"));
		//colb2bCreateTime.setLabel(Labels.getLabel("oe.b2b.colb2bCreateTime"));
		colb2bUnitPrice.setLabel(Labels.getLabel("oe.b2b.colb2bUnitPrice"));
		colb2bTPrice.setLabel(Labels.getLabel("oe.b2b.colb2bTPrice"));
		colb2bCompCode.setLabel(Labels.getLabel("oe.b2b.colb2bCompCode"));
		colb2bImportDate.setLabel(Labels.getLabel("oe.b2b.colb2bImportDate"));
		colb2bCfaPorId.setLabel(Labels.getLabel("oe.b2b.colb2bCfaPorId"));
		colb2bPoNum.setLabel(Labels.getLabel("oe.b2b.colb2bPoNum"));*/
		colb2bPoNum.setLabel(Labels.getLabel("oe.b2b.colb2bPoNum"));
		colb2bPoItem.setLabel(Labels.getLabel("oe.b2b.colb2bPoItem"));
		colb2bMtrlDesc.setLabel(Labels.getLabel("oe.b2b.colb2bMtrlDesc"));
		colb2bMtrlNumMtrlGroup.setLabel(Labels.getLabel("oe.b2b.colb2bMtrlNumMtrlGroup"));
		colb2bWaferQty.setLabel(Labels.getLabel("oe.b2b.colb2bWaferQty"));
		colb2bDelivDate.setLabel(Labels.getLabel("oe.b2b.colb2bDelivDate"));
		colb2bDesignId.setLabel(Labels.getLabel("oe.b2b.colb2bDesignId"));
		colb2bShipToVendorName.setLabel(Labels.getLabel("oe.b2b.colb2bShipToVendorName"));
		colb2bShipComment.setLabel(Labels.getLabel("oe.b2b.colb2bShipComment"));
		colb2bFilename.setLabel(Labels.getLabel("oe.b2b.colb2bFilename"));
		colb2bImportDate.setLabel(Labels.getLabel("oe.b2b.colb2bImportDate"));
		colb2bCountryOfFab.setLabel(Labels.getLabel("oe.b2b.colb2bCountryOfFab"));
		colb2bFab.setLabel(Labels.getLabel("oe.b2b.colb2bFab"));
		colb2bUnitPrice.setLabel(Labels.getLabel("oe.b2b.colb2bUnitPrice"));
		colb2bTPrice.setLabel(Labels.getLabel("oe.b2b.colb2bTPrice"));
		
		//GroupBox
		grbCondition.setLabel(Labels.getLabel("oe.b2b.grbCondition"));
		
		//Label
		lblCustomer.setValue(Labels.getLabel("oe.edit.header.lblCustomer"));
		lblCustomerPo.setValue(Labels.getLabel("oe.edit.header.lblCustomerPo"));
		lblImportDate.setValue(Labels.getLabel("oe.b2b.lblImportDate"));
		
	}

	/**
	 * TODO 簡單描述該方法的實現功能（可選）.
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
	}
	
	/**
	 * 
	 * onClick$btnSearch:依據Customer+PoNum+ImportDate查詢. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void onClick$btnSearch(){
		String conImportDateS="";
		String conImportDateE="";
		
		//確認ImportDate是否正確
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		if(importDateS != null){
			//進行轉換
			String dateString = sdf.format(importDateS);
			dtbImportDateS.setText(String.valueOf(dateString));
		}else{
			if ("".equals(dtbImportDateS.getText())){
				this.showmessage("Warning", Labels.getLabel("oe.b2b.search.check.importDate", new Object[]{Labels.getLabel("oe.b2b.lblImportDate")}));
				return;
			}
		}
		if(importDateE != null){
			String dateString = sdf.format(importDateE);
			dtbImportDateE.setText(String.valueOf(dateString));
		}else{
			if ("".equals(dtbImportDateE)){
				this.showmessage("Warning", Labels.getLabel("oe.b2b.search.check.importDate", new Object[]{Labels.getLabel("oe.b2b.lblImportDate")}));
				return;
			}
		}
		
		if (dtbImportDateE.getText().compareTo(dtbImportDateS.getText())<0){
			this.showmessage("Warning", Labels.getLabel("oe.b2b.search.check.importDate", new Object[]{Labels.getLabel("oe.b2b.lblImportDate")}));
			return;
		}
		
		conImportDateS=dtbImportDateS.getText()+" 00:00:00";
		conImportDateE=dtbImportDateE.getText()+" 23:59:59";
		
		orderLineInts=orderEntryService.getOrderLineInts(customerId, edtCustomerPo.getText(),conImportDateS,conImportDateE);
  		if (orderLineInts.size()>0){
			btnCopy.setDisabled(false);
			grdLineInt.setMultiple(false);
			grdLineInt.setModel(new ListModelList(orderLineInts));
			grdLineInt.setItemRenderer(new OrderLineIntRender());
			grdLineInt.setMultiple(true);
		}
		else{
			this.showmessage("Information", Labels.getLabel("common.message.query.nodata"));
			btnCopy.setDisabled(true);
		}
		
	}
	
	
	//IT-PR-141008_新增可以Import B2B WI Report_Allison
	public void onUpload$btnImport(final UploadEvent evt) throws Exception{
		medias = evt.getMedias();		

		
		for(int i=0; i<medias.length; i++){
			boolean existFlag=false;
			//if(!medias[i].getName().contains("TCE") && !medias[i].getName().contains("TOPPAN")){//檢查檔名是否包含TCE or TOPPAN字樣 //IT-PR-141201_增加判斷是否包含TOPPAN字樣_Allison
			if(!medias[i].getName().contains("TSES") && !medias[i].getName().contains("TOPPAN")){//2017.12.20 將"TCE"改為"TSES"
				this.showmessage("Warning", Labels.getLabel("oe.b2b.import"));
				return;
			}else{
				// 1. 讀取WI Report原始Excel檔，另存temp檔至本機
				//只能上傳xls or xlsx的副檔名，因csv和excel讀取檔案內容的方式不一樣
				if ((medias[0].getName().indexOf(".xls") != -1) || (medias[0].getName().indexOf(".xlsx") != -1)){
					//因xls及xlsx處理方式不一樣，因此設定xls及xlsx的flag
					if (medias[0].getName().indexOf(".xlsx") != -1){
						excelType = false;
					}else{
						excelType = true;
					}
				}else{
					Messagebox.show(Labels.getLabel("oe.b2b.import.errorXlsName"),"Error", Messagebox.OK, Messagebox.ERROR);
					return;
				}

				byte[] fin = medias[0].getByteData();
				final File localFullFile = new File(tmpFileFolder+medias[0].getName());
				OutputStream fout = new FileOutputStream(localFullFile);	
				fout.write(fin);
				fout.close();
				
				boolean existDataFlg = this.readExcelFile(localFullFile, medias[0].getName());				
				if(existDataFlg){
					//Messagebox.show(Labels.getLabel("oe.b2b.import.existWiReportData",new Object[]{tmpErrMsg}),"Warning", Messagebox.OK, Messagebox.EXCLAMATION);
					//若資料庫裡已經有CustomerLotNo+CustomerJob+WaferData的資料，則詢問是否仍要儲存
					Messagebox.show(Labels.getLabel("oe.b2b.import.existWiReportData"), "Question", 
						Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, 
						new org.zkoss.zk.ui.event.EventListener(){
							public void onEvent(Event inEvt) throws InterruptedException,Exception {
							   	if ("onYes".equals(inEvt.getName())){			
									orderEntryService.updateOrderLineInts(orderLineIntLists, userId);
									//orderEntryService.updateWaferBankinInts(waferBankinIntLists, userId); //OCF-PR-150202_匯入工單不儲存到[WAFER_BANKIN_INT], 只儲存到[ORDER_LINE_INT]_Allison
									
									//備份檔案
									FtpUtilUseFtp4j ftp4j=new FtpUtilUseFtp4j();
									log.debug(this.getClass().getName());
									B2bInfo b2bInfo = commonService.getB2bInfo(this.getClass().getName(), "BACKUP");
									String path=b2bInfo.getPath();
									
									//YYYYMM
									Date dateNow = new Date();
									path=path+"/"+dateFormat.format(dateNow).substring(0, 4)+dateFormat.format(dateNow).substring(5, 7);
									b2bInfo.setPath(path);
									if (ftp4j.ftpFolderExists(b2bInfo)==false){
										try {
											ftp4j.ftpCreateDir(b2bInfo, "");
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									
									boolean uploadflag=false;
									uploadflag = ftp4j.uploadFile(b2bInfo, localFullFile, "");
									localFullFile.delete();
									orderLineIntLists.clear();
							   		//waferBankinIntLists.clear(); //OCF-PR-150202_Allison mark							
									Messagebox.show(Labels.getLabel("common.message.saveok"),"Information", Messagebox.OK, Messagebox.INFORMATION);
									onClick$btnSearch();
							   	}else{
							   		localFullFile.delete();
							   		orderLineIntLists.clear();
							   		//waferBankinIntLists.clear(); //OCF-PR-150202_Allison mark
							   		return;
							   	}
							}
						});
				}else{
					//若ORDER_LINE_INT裡都沒有資料就開始儲存，並將檔案Upload到FTP備份				
					orderEntryService.insertOrderLineInts(orderLineIntLists);
					//orderEntryService.insertWaferBankinInts(waferBankinIntLists); //OCF-PR-150202_匯入工單不儲存到[WAFER_BANKIN_INT], 只儲存到[ORDER_LINE_INT]_Allison
					
					//備份檔案
					FtpUtilUseFtp4j ftp4j=new FtpUtilUseFtp4j();
					B2bInfo b2bInfo = commonService.getB2bInfo(this.getClass().getName(), "BACKUP");
					String path=b2bInfo.getPath();
					
					//YYYYMM
					Date dateNow = new Date();
					path=path+"/"+dateFormat.format(dateNow).substring(0, 4)+dateFormat.format(dateNow).substring(5, 7);
					b2bInfo.setPath(path);
					if (ftp4j.ftpFolderExists(b2bInfo)==false){
						try {
							ftp4j.ftpCreateDir(b2bInfo, "");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					boolean uploadflag=false;
					uploadflag = ftp4j.uploadFile(b2bInfo, localFullFile, "");
					localFullFile.delete();
					
					Messagebox.show(Labels.getLabel("common.message.saveok"),"Information", Messagebox.OK, Messagebox.INFORMATION);
					this.onClick$btnSearch();
				}
			}
		}
		
	}
	
	/**
	* 讀取Excel 
	*/
	private boolean readExcelFile(File file, String inFileName) throws Exception{
		File excelFile = file;
		FileInputStream fis = new FileInputStream(excelFile);
		boolean existFlag=false;
		String errorMsg="";
		String errCustomerLotNo="";
		String errCustomerJob="";
		
		if (excelType == true){
			HSSFWorkbook wb = new HSSFWorkbook(fis);
			HSSFSheet ws = wb.getSheetAt(0);
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			int rowNum = ws.getLastRowNum();
			log.debug(ws.getFirstRowNum());
			log.debug(rowNum);
			//因應Excel內容,讀取時去頭(欄位標題)
			for(int i = 1; i <= rowNum ; i++){			
				log.debug("Now process row:"+i);
				HSSFRow row = ws.getRow(i);
				if(!"".equals(row.getCell(6)) && row.getCell(6) != null){
					//依WI REPORT裡的WAFER_LOT+OVT_JOB+WAFER_ID當KEY,先去ORDER_LINE_INT是否已經有資料
					List<OrderLineInt> dbOrderLineInt = orderEntryService.getOrderLineIntByWiReport(row.getCell(6).toString(), row.getCell(7).toString(), row.getCell(14).toString().replace("_", ";").replace(".0", ""));
					if(dbOrderLineInt.size()>0){
						existFlag=true;
					}

					//if(!existFlag){//如果DB找不到已經存在的資料，則要Save，若有找到的話，則此筆不Save
					OrderLineInt tmpOrderLineInt=new OrderLineInt();
					//WaferBankinInt tmpWaferBankInt=new WaferBankinInt(); //OCF-PR-150202_匯入工單不儲存到[WAFER_BANKIN_INT], 只存到[ORDER_LINE_INT]_Allison

					HSSFCell customerPo=row.getCell(15);
					customerPo.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell poItem=row.getCell(16);
					poItem.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell sourceMtrlNum=row.getCell(10);
					sourceMtrlNum.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell mtrlNum=row.getCell(11);
					mtrlNum.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell mtrlDesc=row.getCell(9);
					mtrlDesc.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell waferQty=row.getCell(12);
					waferQty.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell delivDate=row.getCell(34);
					HSSFCell designId=row.getCell(17);
					designId.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell countryOfFab=row.getCell(3);
					countryOfFab.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell shipToVendorName=row.getCell(32);
					shipToVendorName.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell shipComment=row.getCell(41);
					shipComment.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell compCode=row.getCell(2);
					compCode.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell subName=row.getCell(4);
					subName.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell stage=row.getCell(5);
					stage.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell customerLotNo=row.getCell(6);
					customerLotNo.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell customerJob=row.getCell(7);
					customerJob.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell priority=row.getCell(29);
					priority.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell lotType=row.getCell(33);
					lotType.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell waferData=row.getCell(14);
					waferData.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell operationDescription=row.getCell(18);
					operationDescription.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell wiRmaNo=row.getCell(36);
					wiRmaNo.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell dieQty=row.getCell(13);
					dieQty.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell gradeRecord=row.getCell(23);
					gradeRecord.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell engNo=row.getCell(31);
					engNo.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell testProgram=row.getCell(35);
					testProgram.setCellType(Cell.CELL_TYPE_STRING);
					HSSFCell tmpDate=row.getCell(0);
					HSSFCell esod=row.getCell(34);
					
					String shipToVendorCode="";
					customerTableList=customerInformationService.getCustomerTableByCustomerShortNoBusPurpose(shipToVendorName.getStringCellValue());
					if(customerTableList.size()>0){
						shipToVendorCode=customerTableList.get(0).getCustomerId().trim();
					}

					Date importDate = new Date();
					String tmpDelivDate="";
					String tmpEsod="";
					Date eSod=null;
					if(tmpDate.getCellType() == Cell.CELL_TYPE_NUMERIC){
						tmpDate.setCellType(Cell.CELL_TYPE_STRING);
						//String tpDate = dateFormat.format(DateUtil.getJavaDate(Double.parseDouble(tmpDate.getStringCellValue())));
						//importDate = dateFormat.parse(tpDate);
					}

					log.debug(delivDate.getCellType());
					if(delivDate.getCellType() == Cell.CELL_TYPE_NUMERIC){
						delivDate.setCellType(Cell.CELL_TYPE_STRING);
						tmpDelivDate = dateFormat.format(DateUtil.getJavaDate(Double.parseDouble(tmpDate.getStringCellValue())));
						//importDate = dateFormat.parse(tpDate);
					}else if(delivDate.getCellType() == Cell.CELL_TYPE_FORMULA){
						try {  
							tmpDelivDate = String.valueOf(delivDate.getStringCellValue());  
						} catch (IllegalStateException e) {  
							tmpDelivDate = dateFormat.format(delivDate.getDateCellValue());  
						} 
					}else{
						tmpDelivDate=split1("/",delivDate.getStringCellValue(),2).substring(0, 4)+"/"+split1("/",delivDate.getStringCellValue(),0)+"/"+split1("/",delivDate.getStringCellValue(),1)+split1("/",delivDate.getStringCellValue(),2).substring(4, 10)+":00";
					}
					
					log.debug(tmpDelivDate);
					
					eSod = dateFormat.parse(tmpDelivDate);
					log.debug(eSod);

					if(importDateS != null){
						if (importDate.after(importDateS)){
							importDateE=importDate;
						}else if(importDate.before(importDateS)){
							importDateS=importDate;
						}

					}else{
						importDateS=importDate;
					}
					
					if(importDateE == null){
						importDateE=importDateS;
					}

					String fileName=inFileName;
					

					//塞資料到ORDER_LINE_INT
					//tmpOrderLineInt.setOrderLineIntIdx(0);
					tmpOrderLineInt.setCustomerId(customerId);
					if(customerPo.getStringCellValue()!= null && !"".equals(customerPo.getStringCellValue())){
						tmpOrderLineInt.setCustomerPo(customerPo.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setCustomerPo("");
					}
					if(poItem.getStringCellValue()!= null && !"".equals(poItem.getStringCellValue())){
						tmpOrderLineInt.setPoItem(poItem.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setPoItem("");
					}
					if(sourceMtrlNum.getStringCellValue()!= null && !"".equals(sourceMtrlNum.getStringCellValue())){
						tmpOrderLineInt.setSourceMtrlNum(sourceMtrlNum.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setSourceMtrlNum("");
					}
					if(mtrlNum.getStringCellValue()!= null && !"".equals(mtrlNum.getStringCellValue())){
						tmpOrderLineInt.setMtrlNum(mtrlNum.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setMtrlNum("");
					}
					if(mtrlDesc.getStringCellValue()!= null && !"".equals(mtrlDesc.getStringCellValue())){
						tmpOrderLineInt.setMtrlDesc(mtrlDesc.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setMtrlDesc("");
					}
					if(waferQty.getStringCellValue()!= null && !"".equals(waferQty.getStringCellValue())){
						tmpOrderLineInt.setWaferQty(Integer.valueOf(waferQty.getStringCellValue().trim()));
					}else{
						tmpOrderLineInt.setWaferQty(0);
					}
					tmpOrderLineInt.setDelivDate(tmpDelivDate);
					if(designId.getStringCellValue()!= null && !"".equals(designId.getStringCellValue())){
						tmpOrderLineInt.setDesignId(designId.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setDesignId("");
					}
					if(countryOfFab.getStringCellValue()!= null && !"".equals(countryOfFab.getStringCellValue())){
						tmpOrderLineInt.setCountryOfFab(countryOfFab.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setCountryOfFab("");
					}
					if(shipToVendorCode.toString()!= null && !"".equals(shipToVendorCode.toString())){
						tmpOrderLineInt.setShipToVendorCode(shipToVendorCode.toString().trim());
					}else{
						tmpOrderLineInt.setShipToVendorCode("");
					}
					if(shipToVendorName.getStringCellValue()!= null && !"".equals(shipToVendorName.getStringCellValue())){
						tmpOrderLineInt.setShipToVendorName(shipToVendorName.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setShipToVendorName("");
					}
					if(shipComment.getStringCellValue()!= null && !"".equals(shipComment.getStringCellValue())){
					tmpOrderLineInt.setShipComment(shipComment.getStringCellValue());
					}else{
						tmpOrderLineInt.setShipComment("");
					}
					if(compCode.getStringCellValue()!= null && !"".equals(compCode.getStringCellValue())){
						tmpOrderLineInt.setCompCode(compCode.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setCompCode("");
					}
					tmpOrderLineInt.setImportDate(importDate);
					if(fileName!= null && !"".equals(fileName)){
						tmpOrderLineInt.setFileName(fileName.trim());
					}else{
						tmpOrderLineInt.setFileName("");
					}
					if(subName.getStringCellValue()!= null && !"".equals(subName.getStringCellValue())){
						tmpOrderLineInt.setSubName(subName.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setSubName("");
					}
					if(stage.getStringCellValue()!= null && !"".equals(stage.getStringCellValue())){
						tmpOrderLineInt.setStage(stage.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setStage("");
					}
					if(customerLotNo.getStringCellValue()!= null && !"".equals(customerLotNo.getStringCellValue())){
						tmpOrderLineInt.setCustomerLotNo(customerLotNo.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setCustomerLotNo("");
					}
					if(customerJob.getStringCellValue()!= null && !"".equals(customerJob.getStringCellValue())){
						tmpOrderLineInt.setCustomerJob(customerJob.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setCustomerJob("");
					}
					if(priority.getStringCellValue()!= null && !"".equals(priority.getStringCellValue())){
						tmpOrderLineInt.setPriority(priority.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setPriority("");
					}
					if(lotType.getStringCellValue()!= null && !"".equals(lotType.getStringCellValue())){
						tmpOrderLineInt.setLotType(lotType.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setLotType("");
					}
					if(waferData.getStringCellValue()!= null && !"".equals(waferData.getStringCellValue())){
						tmpOrderLineInt.setWaferData(waferData.getStringCellValue().replace("_", ";").trim());//WI Report原始是用_區隔，要改用,存進DB
					}else{
						tmpOrderLineInt.setWaferData("");
					}
					if(operationDescription.getStringCellValue()!= null && !"".equals(operationDescription.getStringCellValue())){
						tmpOrderLineInt.setOperationDescription(operationDescription.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setOperationDescription("");
					}
					if(wiRmaNo.getStringCellValue()!= null && !"".equals(wiRmaNo.getStringCellValue())){
						tmpOrderLineInt.setWiRmaNo(wiRmaNo.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setWiRmaNo("");
					}
					
					//OCF-PR-150202_將原本存在[WAFER_BANKIN_INT]的欄位，存到[ORDER_LINE_INT]，不儲存[WAFER_BANKIN_INT]_Allison
					if(dieQty.getStringCellValue()!= null && !"".equals(dieQty.getStringCellValue())){
						tmpOrderLineInt.setWaferDie(dieQty.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setWaferDie("");
					}
					if(gradeRecord.getStringCellValue()!= null && !"".equals(gradeRecord.getStringCellValue())){
						tmpOrderLineInt.setGradeRecord(gradeRecord.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setGradeRecord("");
					}
					if(engNo.getStringCellValue()!= null && !"".equals(engNo.getStringCellValue())){
						tmpOrderLineInt.setEngNo(engNo.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setEngNo("");
					}
					if(testProgram.getStringCellValue()!= null && !"".equals(testProgram.getStringCellValue())){
						tmpOrderLineInt.setTestProgram(testProgram.getStringCellValue().trim());
					}else{
						tmpOrderLineInt.setTestProgram("");
					}

					orderLineIntLists.add(tmpOrderLineInt);

					//塞資料到WAFER_BANKIN_INT
					//tmpWaferBankInt.setWaferBankinIntIdx(0);
					/* OCF-PR-150202_匯入工單後，不儲存到[WAFER_BANKIN_INT], 只儲存到[ORDER_LINE_INT]_Allison
					tmpWaferBankInt.setCurrentWaferQty(Integer.valueOf(waferQty.getStringCellValue()));
					tmpWaferBankInt.setCustomerId(customerId);
					tmpWaferBankInt.setCustomerLotno(customerLotNo.getStringCellValue());
					tmpWaferBankInt.setDesignId(designId.getStringCellValue());
					tmpWaferBankInt.setWaferDie(dieQty.getStringCellValue());
					tmpWaferBankInt.setFileName(fileName);
					tmpWaferBankInt.setMtrlNum(mtrlNum.getStringCellValue());
					tmpWaferBankInt.setMtrlDesc(mtrlDesc.getStringCellValue());
					tmpWaferBankInt.setShipComment(shipComment.getStringCellValue());
					tmpWaferBankInt.setWaferData(waferData.getStringCellValue().replace("_", ";"));
					tmpWaferBankInt.setCustomerJob(customerJob.getStringCellValue());
					tmpWaferBankInt.setCustomerPo(customerPo.getStringCellValue());
					tmpWaferBankInt.setPoItem(poItem.getStringCellValue());
					tmpWaferBankInt.setGradeRecord(gradeRecord.getStringCellValue());
					tmpWaferBankInt.setSourceMtrlNum(sourceMtrlNum.getStringCellValue());
					tmpWaferBankInt.setEngNo(engNo.getStringCellValue());
					tmpWaferBankInt.setTestProgram(testProgram.getStringCellValue());
					tmpWaferBankInt.setEsod(eSod);

					waferBankinIntLists.add(tmpWaferBankInt);
					*/
					
					//}
					//				else{
					//					if("".equals(errCustomerLotNo)){
					//						errCustomerLotNo = row.getCell(6).getStringCellValue();
					//						errCustomerJob = row.getCell(7).getStringCellValue();
					//					}else{
					//						errCustomerLotNo = errCustomerLotNo + ","+row.getCell(6).getStringCellValue();
					//						errCustomerJob = errCustomerJob + ","+row.getCell(7).getStringCellValue();
					//					}
					//				}

				}
			}
//			errorMsg = "Customer LotNo: "+errCustomerLotNo;
//			errorMsg = "Customer Job: "+errCustomerJob;
		}else{
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet ws = wb.getSheetAt(0);
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				        
			int rowNum = ws.getLastRowNum();
			log.debug("data upload file:"+excelFile.getName()+",the file row num="+rowNum);
			for(int i = 1; i <= rowNum + 1 ; i++){
				log.debug("Now process row:"+i);
				XSSFRow row = ws.getRow(i);
				
				if(!"".equals(row.getCell(6)) && row.getCell(6) != null){
					//依WI REPORT裡的WAFER_LOT+OVT_JOB+WAFER_ID當KEY,先去ORDER_LINE_INT是否已經有資料
					List<OrderLineInt> dbOrderLineInt = orderEntryService.getOrderLineIntByWiReport(row.getCell(6).getStringCellValue(), row.getCell(7).getStringCellValue(), row.getCell(14).getStringCellValue());
					if(dbOrderLineInt.size()>0){
						existFlag=true;
					}

					//if(!existFlag){//如果DB找不到已經存在的資料，則要Save，若有找到的話，則此筆不Save
					OrderLineInt tmpOrderLineInt=new OrderLineInt();
					WaferBankinInt tmpWaferBankInt=new WaferBankinInt();

					XSSFCell customerPo=row.getCell(15);
					XSSFCell poItem=row.getCell(16);
					XSSFCell sourceMtrlNum=row.getCell(10);
					XSSFCell mtrlNum=row.getCell(11);
					XSSFCell mtrlDesc=row.getCell(9);
					XSSFCell waferQty=row.getCell(12);
					XSSFCell delivDate=row.getCell(34);
					XSSFCell designId=row.getCell(17);
					XSSFCell countryOfFab=row.getCell(3);
					XSSFCell shipToVendorName=row.getCell(32);
					XSSFCell shipComment=row.getCell(41);
					XSSFCell compCode=row.getCell(2);
					XSSFCell subName=row.getCell(4);
					XSSFCell stage=row.getCell(5);
					XSSFCell customerLotNo=row.getCell(6);
					XSSFCell customerJob=row.getCell(7);
					XSSFCell priority=row.getCell(29);
					XSSFCell lotType=row.getCell(33);
					XSSFCell waferData=row.getCell(14);
					XSSFCell operationDescription=row.getCell(18);
					XSSFCell wiRmaNo=row.getCell(36);
					XSSFCell dieQty=row.getCell(13);
					XSSFCell gradeRecord=row.getCell(23);
					XSSFCell engNo=row.getCell(31);
					XSSFCell testProgram=row.getCell(35);
					XSSFCell tmpDate=row.getCell(0);
					XSSFCell esod=row.getCell(34);
					String shipToVendorCode="";
					customerTableList=customerInformationService.getCustomerTableByCustomerShortNoBusPurpose(shipToVendorName.getStringCellValue());
					if(customerTableList.size()>0){
						shipToVendorCode=customerTableList.get(0).getCustomerId().trim();
					}

					DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
					String tmpImportDate=split1("/",tmpDate.getStringCellValue(),2).substring(0, 4)+"/"+split1("/",tmpDate.getStringCellValue(),0)+"/"+split1("/",tmpDate.getStringCellValue(),1)+split1("/",tmpDate.getStringCellValue(),2).substring(4, 10);
					Date importDate = dateFormat.parse(tmpImportDate);

					String fileName=inFileName;
					String tmpEsod=split1("/",esod.getStringCellValue(),2).substring(0, 4)+"/"+split1("/",esod.getStringCellValue(),0)+"/"+split1("/",esod.getStringCellValue(),1)+split1("/",esod.getStringCellValue(),2).substring(4, 10);
					Date eSod = dateFormat.parse(tmpEsod);

					//塞資料到ORDER_LINE_INT
					//tmpOrderLineInt.setOrderLineIntIdx(0);
					tmpOrderLineInt.setCustomerId(customerId);
					tmpOrderLineInt.setCustomerPo(String.valueOf((int) Math.round(customerPo.getNumericCellValue())));
					tmpOrderLineInt.setPoItem(String.valueOf((int) Math.round(poItem.getNumericCellValue())));
					tmpOrderLineInt.setSourceMtrlNum(sourceMtrlNum.getStringCellValue());
					tmpOrderLineInt.setMtrlNum(mtrlNum.getStringCellValue());
					tmpOrderLineInt.setMtrlDesc(mtrlDesc.getStringCellValue());
					tmpOrderLineInt.setWaferQty(Integer.valueOf((int) waferQty.getNumericCellValue()));
					tmpOrderLineInt.setDelivDate(delivDate.getStringCellValue());
					tmpOrderLineInt.setDesignId(designId.getStringCellValue());
					tmpOrderLineInt.setCountryOfFab(countryOfFab.getStringCellValue());
					tmpOrderLineInt.setShipToVendorCode(shipToVendorCode.toString());
					tmpOrderLineInt.setShipToVendorName(shipToVendorName.getStringCellValue());
					tmpOrderLineInt.setShipComment(shipComment.getStringCellValue());
					tmpOrderLineInt.setCompCode(compCode.getStringCellValue());
					tmpOrderLineInt.setImportDate(importDate);
					tmpOrderLineInt.setFileName(fileName);
					tmpOrderLineInt.setSubName(subName.getStringCellValue());
					tmpOrderLineInt.setStage(stage.getStringCellValue());
					tmpOrderLineInt.setCustomerLotNo(customerLotNo.getStringCellValue());
					tmpOrderLineInt.setCustomerJob(customerJob.getStringCellValue());
					tmpOrderLineInt.setPriority(String.valueOf((int) Math.round(priority.getNumericCellValue())));
					tmpOrderLineInt.setLotType(lotType.getStringCellValue());
					tmpOrderLineInt.setWaferData(waferData.getStringCellValue().replace("_", ";"));//WI Report原始是用_區隔，要改用,存進DB
					tmpOrderLineInt.setOperationDescription(operationDescription.getStringCellValue());
					tmpOrderLineInt.setWiRmaNo(wiRmaNo.getStringCellValue());

					orderLineIntLists.add(tmpOrderLineInt);

					//塞資料到WAFER_BANKIN_INT
					//tmpWaferBankInt.setWaferBankinIntIdx(0);
					/* OCF-PR-150202_匯入工單不儲存到[WAFER_BANKIN_INT], 只儲存到[ORDER_LINE_INT]_Allison
					tmpWaferBankInt.setCurrentWaferQty(Integer.valueOf((int) waferQty.getNumericCellValue()));
					tmpWaferBankInt.setCustomerId(customerId);
					tmpWaferBankInt.setCustomerLotno(customerLotNo.getStringCellValue());
					tmpWaferBankInt.setDesignId(designId.getStringCellValue());
					tmpWaferBankInt.setWaferDie(dieQty.getStringCellValue());
					tmpWaferBankInt.setFileName(fileName);
					tmpWaferBankInt.setMtrlNum(mtrlNum.getStringCellValue());
					tmpWaferBankInt.setMtrlDesc(mtrlDesc.getStringCellValue());
					tmpWaferBankInt.setShipComment(shipComment.getStringCellValue());
					tmpWaferBankInt.setWaferData(waferData.getStringCellValue().replace("_", ";"));
					tmpWaferBankInt.setCustomerJob(customerJob.getStringCellValue());
					tmpWaferBankInt.setCustomerPo(String.valueOf((int) Math.round(customerPo.getNumericCellValue())));
					tmpWaferBankInt.setPoItem(String.valueOf((int) Math.round(poItem.getNumericCellValue())));
					tmpWaferBankInt.setGradeRecord(gradeRecord.getStringCellValue());
					tmpWaferBankInt.setSourceMtrlNum(sourceMtrlNum.getStringCellValue());
					tmpWaferBankInt.setEngNo(engNo.getStringCellValue());
					tmpWaferBankInt.setTestProgram(testProgram.getStringCellValue());
					tmpWaferBankInt.setEsod(eSod);

					waferBankinIntLists.add(tmpWaferBankInt);
					*/
					
					//}
					//				else{
					//					if("".equals(errCustomerLotNo)){
					//						errCustomerLotNo = row.getCell(6).getStringCellValue();
					//						errCustomerJob = row.getCell(7).getStringCellValue();
					//					}else{
					//						errCustomerLotNo = errCustomerLotNo + ","+row.getCell(6).getStringCellValue();
					//						errCustomerJob = errCustomerJob + ","+row.getCell(7).getStringCellValue();
					//					}
					//				}
				}
			}
//			errorMsg = "Customer LotNo: "+errCustomerLotNo;
//			errorMsg = "Customer Job: "+errCustomerJob;
		}
		return existFlag;
	}
	
	/**
	 * 
	 * onClick$btnCopy:User確定要將所選的b2b data回傳至前一個畫面. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void onClick$btnCopy(){
		//OCF-PR-151102_檢查同一個PO_ITEM下不可有"不同"的"TARGET DEVICE"或"SOURCE_DEVICE"
		String errMsg = "";
		if(orderLineInts.size() > 0){
			for(int i=0; i<grdLineInt.getItemCount(); i++){
				if(grdLineInt.getItemAtIndex(i).isSelected()){
					for(int j=0; j<grdLineInt.getItemCount(); j++){//OCF-PR-151105_bugfix
						if(grdLineInt.getItemAtIndex(j).isSelected()){//OCF-PR-151105_bugfix
						if(orderLineInts.get(i).getPoItem().equals(orderLineInts.get(j).getPoItem())){
							if(!orderLineInts.get(i).getMtrlNum().equals(orderLineInts.get(j).getMtrlNum()) || !orderLineInts.get(i).getSourceMtrlNum().equals(orderLineInts.get(j).getSourceMtrlNum())){
								String msg1 = orderLineInts.get(i).getCustomerPo() + " / " + orderLineInts.get(i).getPoItem() + " / " + orderLineInts.get(i).getMtrlNum() + " / " + orderLineInts.get(i).getSourceMtrlNum() + "\r\n";
								String msg2 = orderLineInts.get(j).getCustomerPo() + " / " + orderLineInts.get(j).getPoItem() + " / " + orderLineInts.get(j).getMtrlNum() + " / " + orderLineInts.get(j).getSourceMtrlNum() + "\r\n";
								
								if("".equals(errMsg)){
									errMsg = msg1 + msg2;	
								}else{
									boolean checkflag = false;
									String splitErrMsg[] = errMsg.split("\r\n");
									if(splitErrMsg.length > 0){
										for(int k=0; k<splitErrMsg.length; k++){
											if(splitErrMsg[k].equals(msg1.replace("\r\n", "")) || splitErrMsg[k].equals(msg2.replace("\r\n", ""))){
												checkflag = true;
											}
										}
										
										if(!checkflag){
											errMsg = errMsg + msg1 + msg2;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		}
		
		if(!"".equals(errMsg)){
			Map params = new HashMap();
			params.put("width", 500);//指定對話視窗的寬度
			Messagebox.show(Labels.getLabel("oe.b2b.import.diffTargetDeviceSourceDevice") + errMsg, "Warning", null, null, Messagebox.INFORMATION, null, null, params);
			
			return;
		}
		
		int c=0;
		//ZkComboboxControl.setComboboxClear(cbxSelectOrderLineIntIdxPa);
		for (int i=0;i<grdLineInt.getItemCount();i++){
			if (grdLineInt.getItemAtIndex(i).isSelected()){
				c++;
				cbxSelectOrderLineIntIdxPa.appendItem(String.valueOf(orderLineInts.get(i).getOrderLineIntIdx()));
			}
		}
		
		if (c==0){
			this.showmessage("Warning", Labels.getLabel("oe.query.click.detail.error"));
			return;
		}
		
		//grdLineIntPa.setModel(new ListModelList(orderLineInts));
		//grdLineIntPa.setItemRenderer(new OrderLineIntRender());
		self.detach();
	}
	
	/**
	 * 
	 * split1:依據inC分隔inS，取出第inIndex的字串. <br/>
	 *
	 * @author 030260
	 * @param inC
	 * @param inS
	 * @param inIndex
	 * @return
	 * @since JDK 1.6
	 */
	public String split1(String inC, String inS, int inIndex)
	//split1(",","aaa,bbb,ccc",1)="bbb"
	{
		String tmpstr;
		int k,z;
		
		String ss[];
		ss=new String[100];
		
		k=inS.indexOf(inC);
		z=0;
		while (k!=-1) 
		{
			tmpstr=inS.substring(0,k);
			ss[z]=tmpstr;
			inS=inS.substring(k+1);
			k=inS.indexOf(inC);
			z++;
		}
		ss[z]=inS;
		
		/*for (int jj=0; jj<z+1;jj++)
		{
			System.out.println(ss[jj]);
		}*/
		
		return ss[inIndex];
	}

}
