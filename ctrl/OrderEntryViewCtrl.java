/*
 * Project Name:iVision
 * File Name:OrderEntryViewCtrl.java
 * Package Name:com.tce.ivision.modules.oe.ctrl
 * Date:2012/12/26下午5:19:22
 * 
 *  說明:
 * 1.同時New Add/Modify ORDER_HEAER,ORDER_LINE,ORDER_LINE_LOTNO
 * 2.ORDER_LINE 可 COPY FROM B2B
 * 3.查詢已關帳的ORDER_HEAER,ORDER_LINE,ORDER_LINE_LOTNO
 * 
 * 修改歷史:
 * 2012.12.17 OCF#OE002 Fanny Initial
 * 2013.02.18           Fanny Level-3有Update Wafer_Qty時，Wafer_status用update
 *                                   有Update PO_Number,Customer_lotno時，Wafer_status用insert(原本那一筆state_flag=2)
 * 2013.02.20           Fanny ORDER_LINE_INT.PO_NUM --> CUSTOMER_PO
 * 2013.03.07           Fanny 1.WAFER_STATUS新增欄位LOT_NO
 *                            2.Order Modify時，insert to WAFER_STATUS時以下欄位保持原本的值
 *                            WAFER_STATUS.LOT_NO
 *                            WAFER_STATUS.LOTISSUE_FLAG
 *                            WAFER_STATUS.INAVI_FLAG
 *                            WAFER_STATUS.INFO_FLAG
 *                            WAFER_STATUS.WAFER_FLAG
 *                            WAFER_STATUS.PO_FLAG
 * 2013.03.20           Fanny requirement change from Johnson:OCF-iVision_OE#002_V2.7.doc
 *                            [WAFER_STATUS] add field ORDER_NUMBER
 * 2013.06.21           Fanny (1)Line.SHIP_TO唯讀，SHIP_TO的值改帶入HEADER.SHIP_TO
 *                               若用B2B import時，則以B2B的SHIP_TO為主
 *                               ps.如果Line.ship_to已經有值的時候，就以Line.ship_to為主
 *                            (2)Save前，確認BILL_TO+PO_NUMBER必須是唯一(排除CANCEL_FLAG=1)
 * 2013.06.26           Fanny (1)Line ship_to 暫時先hide(2)如果客戶非APTINA時，如果改HEADER時，LINE SHIP_TO要一起修改
 * 2013.07.09           Fanny (1)Save前至[PRODUCT_INFO]檢查是否有資料，沒有的話ALARM(CUSTOMER_CODE+PRODUCT_CLASS_CODE+PRODUCT+DELETE_FLAG=0)
 *                            (2)OrderStatus=20時，不允許刪除，且限制部分欄位的修改(Header-->	Customer,PO Number,Order Type, Product,
 *	                             Line-->Customer LotNo)
 *                            (3)mode=newadd時waferSize=12                                                                                                                   
 * 2014.07.11           Fanny 如果用PO_NUMBER+CUSTOMER_LOTNO至SHIPPING_DETAIL找，只要其中一筆有做ship confirm時，
 *                            若有修改Price/Currency/Rate/BillTo，
 * 						                秀出Alarm提示User該筆資料已Ship Confirm無法同步至EBS
 * 2014.09.01           Fanny Save前確認BillTo與ShipTo是否與Customer有relationship 
 * 2014.10.06           Fanny IT-PR-141004 修改Relashionship判斷方法                                                                                                                                           
 *                                                                                       
 * 
 */
package com.tce.ivision.modules.oe.ctrl;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.zkoss.io.NullWriter;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.tce.ivision.model.BaseCtrlSet;
import com.tce.ivision.model.CustomerTable;
import com.tce.ivision.model.Hold;
import com.tce.ivision.model.MailList;
import com.tce.ivision.model.OeReworkCountSetup;
import com.tce.ivision.model.OrderHeader;
import com.tce.ivision.model.OrderInternalCheckInfo;
import com.tce.ivision.model.OrderLine;
import com.tce.ivision.model.OrderLineInt;
import com.tce.ivision.model.OrderLineLotno;
import com.tce.ivision.model.ProductInfo;
import com.tce.ivision.model.ProductNameSetup;
import com.tce.ivision.model.ShippingDetail;
import com.tce.ivision.model.TsesOvtRmaLot;
import com.tce.ivision.model.UiFieldParam;
import com.tce.ivision.model.UiFieldSet;
import com.tce.ivision.model.WaferBankin;
import com.tce.ivision.model.WaferBankinWafer;
import com.tce.ivision.model.WaferStatus;
import com.tce.ivision.model.business.FieldDefine;
import com.tce.ivision.modules.as.service.UserService;
import com.tce.ivision.modules.base.ctrl.BaseViewCtrl;
import com.tce.ivision.modules.cus.service.CustomerInformationService;
import com.tce.ivision.modules.fin.service.FinService;
import com.tce.ivision.modules.oe.model.CreateOeList;
import com.tce.ivision.modules.oe.model.OeOrderNoConfirmModel;
import com.tce.ivision.modules.oe.model.OrderEntryLineModel;
import com.tce.ivision.modules.oe.model.OrderEntryLotnoModel;
import com.tce.ivision.modules.oe.model.OrderQueryModel;
import com.tce.ivision.modules.oe.model.WaferFilter;
import com.tce.ivision.modules.oe.render.OrderLotnoRender;
import com.tce.ivision.modules.oe.service.OrderEntryService;
import com.tce.ivision.modules.oe.service.WaferBankinService;
import com.tce.ivision.modules.oe.service.WaferStatusService;
import com.tce.ivision.modules.oracle.service.OracleService;
import com.tce.ivision.units.common.BeanUtil;
import com.tce.ivision.units.common.DateFormatUtil;
import com.tce.ivision.units.common.MailUtil;
import com.tce.ivision.units.common.ZkComboboxControl;
import com.tce.ivision.units.common.service.CommonService;

/**
 * ClassName: OrderEntryViewCtrl <br/>
 * date: 2012/12/26 下午5:19:22 <br/>
 *
 * @author 030260
 * @version 
 * @since JDK 1.6
 */
public class OrderEntryViewCtrl extends BaseViewCtrl implements ListitemRenderer{
	/**
	 * Logger
	 */
	public static Logger log = Logger.getLogger(OrderEntryViewCtrl.class);
	
	/**
	 * poNumber限定欄位長度
	 */
	public static int poNumberLen=8;//2013.02.20
	
	/**
	 * customerPo限定欄位長度
	 */
	public static int customerPoLen=10;//2013.02.20
	
	/**
	 * poItem限定欄位長度
	 */
	public static int poItemLen=5;
	
	/**
	 * CustomerLotno限定欄位長度
	 */
	public static int customerLotnoLen=20;
	
	//Global var
	/**
	 * Login in User
	 */	
	String userId="030260";
	
	/**
	 * Login User的Operation Unit
	 */
	String operationUnit="01";
	
	//data from parent
	/**
	 * 進入此介面的狀態:readonly,modify,newadd
	 */
	String mode="readonly";
	
	/**
	 * 從前一個畫面傳來的orderQueryModel:前一個畫面查詢出來的結果,User選了哪一筆data
	 */
	OrderQueryModel orderQueryModel;
	
	/**
	 * zk component:Window winOrderEntry
	 */
	private Window winOrderEntry;
	
	/**
	 * zk component:Button btnSave
	 */
	private Button btnSave;
	
	/**
	 * zk component:Listheader colBatchWaferQty
	 */
	private Listheader colBatchWaferQty;
	
	/**
	 * zk component:Listheader colBatchCustomerLotno
	 */
	private Listheader colBatchCustomerLotno;
	
	/**
	 * zk component:Listheader colBatchPoItem
	 */
	private Listheader colBatchPoItem;
	
	/**
	 * zk component:Listheader colBatchPoItem
	 */
	private Listheader colBatchNO;
	
	/**
	 * zk component:Listbox grdLotno
	 */
	private Listbox grdLotno;
	
	/**
	 * zk component:Button btnLotnoDel
	 */
	private Button btnLotnoDel;
	
	/**
	 * zk component:Button btnLotnoAdd
	 */
	private Button btnLotnoAdd;
	
	/**
	 * zk component:Caption grbLotnoData
	 */
	private Caption grbLotnoData;
	
	/**
	 * zk component:Listheader colLineMtrlNum
	 */
	private Listheader colLineMtrlNum;
	
	/**
	 * zk component:Listheader colLineSourceMtrlNum
	 */
	private Listheader colLineSourceMtrlNum;
	
	/**
	 * zk component:Listheader colLineCompanyCod
	 */
	private Listheader colLineCompanyCode;
	
	/**
	 * zk component:Listheader colLineShipToVendorCode
	 */
	private Listheader colLineShipToVendorCode;
	
	/**
	 * zk component:Listheader colLineCfaProId
	 */
	private Listheader colLineCfaProId;
	
	/**
	 * zk component:Listheader colLineFab
	 */
	private Listheader colLineFab;
	
	/**
	 * zk component:Listheader colLineCountryOfFab
	 */
	private Listheader colLineCountryOfFab;
	
	/**
	 * zk component:Listheader colLineShipComment
	 */
	private Listheader colLineShipComment;
	
	/**
	 * zk component:Listheader colLlineShipToVendorName
	 */
	//private Listheader colLlineShipToVendorName;//2013.06.26
	
	/**
	 * zk component:Listheader colLineSellingPrice
	 */
	private Listheader colLineSellingPrice;
	
	/**
	 * zk component:Listheader colLineCfaSite 2013.01.15
	 */
	private Listheader colLineCfaSite;
	
	/**
	 * zk component:Listheader colLineUnitPrice
	 */
	private Listheader colLineUnitPrice;
	
	/**
	 * zk component:Listheader colLineCurrency
	 */
	private Listheader colLineCurrency;
	
	/**
	 * zk component:Listheader colLineDesignId
	 */
	private Listheader colLineDesignId;
	
	/**
	 * zk component:Listheader colLineWaferSize
	 */
	private Listheader colLineWaferSize;
	
	/**
	 * zk component:Listheader colLineRequestDate
	 */
	private Listheader colLineRequestDate;
	
	/**
	 * zk component:Listheader colLineWaferQty
	 */
	private Listheader colLineWaferQty;
	
	/**
	 * zk component:Listheader colLineWaferSupplyDate
	 */
	private Listheader colLineWaferSupplyDate;//2013.02.08
	
	/**
	 * zk component:Listheader colLineMtrlGroup
	 */
	private Listheader colLineMtrlGroup;
	
	/**
	 * zk component:Listheader colBatchWaferDie
	 */
	private Listheader colBatchWaferDie; //OCF-PR-150202_Allison add
	
	/**
	 * zk component:Listheader colBatchGradeRecord
	 */
	private Listheader colBatchGradeRecord; //OCF-PR-150202_Allison add
	
	/**
	 * zk component:Listheader colBatchEngNo
	 */
	private Listheader colBatchEngNo; //OCF-PR-150202_Allison add
	
	/**
	 * zk component:Listheader colBatchTestProgram
	 */
	private Listheader colBatchTestProgram; //OCF-PR-150202_Allison add
	
	/**
	 * zk component:Listheader colLineMtrlDesc
	 */
	private Listheader colLineMtrlDesc;
	
	/**
	 * zk component:Listheader colLinePoItem
	 */
	private Listheader colLinePoItem;
	
	/**
	 * zk component:Listbox grdLine
	 */
	private Listbox grdLine;
	
	/**
	 * zk component:Button btnLineCopyfrom
	 */
	private Button btnLineCopyfrom;
	
	/**
	 * zk component:Button btnLineDel
	 */
	private Button btnLineDel;
	
	/**
	 * zk component:Button btnLineAdd
	 */
	private Button btnLineAdd;
	
	/**
	 * zk component:Caption grbLineData
	 */
	private Caption grbLineData;
	
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
	 * zk component:Textbox edtOrderStatus
	 */
	private Textbox edtOrderStatus;
	
	/**
	 * zk component:Label lblOrderStatus
	 */
	private Label lblOrderStatus;
	
	/**
	 * zk component:Textbox edtTotalWaferQty
	 */
	private Textbox edtTotalWaferQty;
	
	/**
	 * zk component:Label lblTotalWaferQty
	 */
	private Label lblTotalWaferQty;
	
	/**
	 * zk component:Combobox cbxWaferSize
	 */
	private Combobox cbxWaferSize;
	
	/**
	 * zk component:Label lblWaferSize
	 */
	private Label lblWaferSize;
	
	/**
	 * zk component:Combobox cbxShipTo
	 */
	private Combobox cbxShipTo;
	
	/**
	 * zk component:Label lblShipTo
	 */
	private Label lblShipTo;
	
	/**
	 * zk component:Textbox edtAccountMgr
	 */
	private Textbox edtAccountMgr;
	
	/**
	 * zk component:Label lblAccountMgr
	 */
	private Label lblAccountMgr;
	
	/**
	 * zk component:Combobox cbxBillTo
	 */
	private Combobox cbxBillTo;
	
	/**
	 * zk component:Label lblBillTo
	 */
	private Label lblBillTo;
	
	/**
	 * zk component:Textbox edtCustomerPo
	 */
	private Textbox edtCustomerPo;
	
	/**
	 * zk component:Label lblCustomerPo
	 */
	private Label lblCustomerPo;
	
	/**
	 * zk component:Combobox cbxOrderFrom
	 */
	private Combobox cbxOrderFrom;
	
	/**
	 * zk component:Label lblOrderFrom
	 */
	private Label lblOrderFrom;
	
	/**
	 * zk component:Textbox edtPoNum
	 */
	private Textbox edtPoNum;
	
	/**
	 * zk component:Label lblPoNumber
	 */
	private Label lblPoNumber;
	
	/**
	 * zk component:Textbox edtProduct
	 */
	private Textbox edtProduct;
	/**
	 * zk component:Label lblProduct
	 */
	private Label lblProduct;
	
	/**
	 * zk component:Textbox edtOrderNumber
	 */
	private Textbox edtOrderNumber;
	
	/**
	 * zk component:Label lblOrderNumber
	 */
	private Label lblOrderNumber;
	
	/**
	 * zk component:Combobox cbxOrderType
	 */
	private Combobox cbxOrderType;
	
	/**
	 * zk component:Label lblOrderType
	 */
	private Label lblOrderType;
	
	/**
	 * zk component:Textbox edtOrderDate
	 */
	private Textbox edtOrderDate;
	
	/**
	 * zk component:Label lblOrderDate
	 */
	private Label lblOrderDate;
	
	/**
	 * zk component:Combobox cbxCustomer
	 */
	private Combobox cbxCustomer;
	
	/**
	 * zk component:Label lblCustomer
	 */
	private Label lblCustomer;
	
	/**
	 * zk component:Tab tabLine
	 */
	private Tab tabLine;
	
	/**
	 * zk component:Tab tabHeader
	 */
	private Tab tabHeader;
	
	/**
	 * zk component:Caption grbActionType
	 */
	private Caption grbActionType;
	
	/**
	 * zk component:Radio radioEdit
	 */
	private Radio radioEdit;
	
	/**
	 * zk component:Radio radioReadonly
	 */
	private Radio radioReadonly;
	
	/**
	 * zk component:Label lblGRequestData
	 */
	private Label lblGRequestData;
	
	/**
	 * zk component:Label lblGCurrency
	 */
	private Label lblGCurrency;
	
	/**
	 * zk component:Label lblGCfaSite
	 */
	private Label lblGCfaSite;//2013.01.15
	
	/**
	 * zk component:Combobox cbxGCfaSite
	 */
	private Combobox cbxGCfaSite;
	
	/**
	 * zk component:lblGWaferSupplyDate
	 */
	private Label lblGWaferSupplyDate;//2013.02.08
	
	/**
	 * zk component:dtbGWaferSupplyDate
	 */
	private Datebox dtbGWaferSupplyDate;//2013.02.08
	
	/**
	 * zk component:lblGReworkFlag
	 */
	private Label lblGReworkFlag;//version:XQ181004 2018.11.12 RMA add by will
	
	/**
	 * zk component:Combobox cbxGReworkFlag
	 */
	private Combobox cbxGReworkFlag;//version:XQ181004 2018.11.12 RMA add by will
	
	/**
	 * zk component:Datebox dtbGRequestDate
	 */
	private Datebox dtbGRequestDate;
	
	/**
	 * zk component:Combobox cbxGCurrency
	 */
	private Combobox cbxGCurrency;
	
	/**
	 * zk component:Caption grbGlobalSetting
	 */
	private Caption grbGlobalSetting;
	
	//要傳遞至下一個畫面(Copy from B2B)的物件
	/**
	 * zk component:Listbox grdLineIntPa 傳遞至Copy from B2B的物件
	 */
	private Listbox grdLineIntPa;
	
	private Listheader colLineShippingDestination;//OCF-PR-160602_新增SHIPPING DESTINATION供OVT B2B REPORT用
	
	/**
	 * zk component:Combobox cbxSelectOrderLineIntIdxPa 傳遞至Copy from B2B的物件
	 */
	private Combobox cbxSelectOrderLineIntIdxPa;
	/**
	 * zk component
	 */
	private Checkbox chkB2bDisableFlag;
	
	/**
	 * zk component 2017.12.20
	 */
	private Label lblInternalProduct;
	private Textbox edtInternalProduct;
	private Button btnSetInternalProduct;

	
	//Spring
	/**
	 * 
	 */
	private WaferStatusService waferStatusService = (WaferStatusService) SpringUtil.getBean("waferStatusService");
	
	/**
	 * UserService
	 */
	private UserService userService=(UserService) SpringUtil.getBean("userService");
	
	/**
	 * OracleService
	 */
	private OracleService oracleService=(OracleService) SpringUtil.getBean("oracleService");//2014.09.01
	
	/**
	 * OrderEntryService
	 */
	private OrderEntryService orderEntryService = (OrderEntryService) SpringUtil.getBean("orderEntryService");
	
	/**
	 * FinService
	 */
	private FinService finService=(FinService) SpringUtil.getBean("finService");
	
	/**
	 * CustomerInformationService
	 */
	private CustomerInformationService customerInformationService = (CustomerInformationService) SpringUtil.getBean("customerInformationService");
	
	/**
	 * CommonService
	 */
	private CommonService commonService = (CommonService) SpringUtil.getBean("commonService");
	
	/**
	 * WaferBankinService
	 */
	private WaferBankinService waferBankinService = (WaferBankinService) SpringUtil.getBean("waferBankinService");
	
	
	//java bean
	/**
	 * 準備要儲存的OrderHeader
	 */
	private OrderHeader orderHeader;
	
	//OCF-PR-151102_修改當新建OE時，OE的ORDER_NUMBER編碼變到ORDER_LINE_LOTNO，即有幾個CUSTOMER_LOTNO就是幾筆ORDER
	private List<OrderHeader> orderHeaders = new ArrayList<OrderHeader>();
	
	/**
	 * 準備要儲存的ORDER_LINE
	 */
	private List<OrderLine> orderLines;
	
	/**
	 * 準備要儲存的OrderEntryLineModel
	 */
	private List<OrderEntryLineModel> saveOrderEntryLineModels;
	
	/**
	 * 準備要儲存ORDER_LINE_LOTNO
	 */
	private List<OrderEntryLotnoModel> saveOrderEntryLotnoModels;
	
	/**
	 * By Po Item準備要儲存ORDER_LINE_LOTNO
	 */
	private List<OrderEntryLotnoModel> tmporderEntryLotnoModels;
	
	/**
	 * 準備要insert WAFER_STATUS
	 */
	private List<WaferStatus> insertWaferStatus;
	
	/**
	 * 準備要update WAFER_STATUS
	 */
	private List<WaferStatus> updateWaferStatus;
	
	/**
	 * CustomerType=C的CustomerTable
	 */
	private List<CustomerTable> customers;
	
	/**
	 * CustomerType=B的CustomerTable
	 */
	private List<CustomerTable> billtos;
	
	/**
	 * CustomerType=S的CustomerTable
	 */
	private List<CustomerTable> shiptos;
	
	/**
	 * CustomerType=O的CustomerTable
	 */
	private List<CustomerTable> orderfroms;
	
	/**
	 * PARA_TYPE=OE_ORDER_TYPE的UiFieldSet
	 */
	private List<UiFieldSet> orderTypes;
	
	/**
	 * PARA_TYPE=OE_ORDER_TYPE的UiFieldParam
	 */
	private List<UiFieldParam> orderTypeUiFieldParams;
	
	/**
	 * PARA_TYPE=OE_WAFER_SIZE的UiFieldSet
	 */
	private List<UiFieldSet> waferSizes;
	
	/**
	 * PARA_TYPE=OE_WAFER_SIZE的UiFieldParam
	 */
	private List<UiFieldParam> waferSizeUiFieldParams;
	
	/**
	 * PARA_TYPE=iMask-OM的UiFieldSet
	 */
	private List<UiFieldSet> iMaskOMs;//2014.09.01
	
	/**
	 * PARA_TYPE=iMask-OM的UiFieldParam
	 */
	private List<UiFieldParam> iMaskOMUiFieldParams;//2014.09.01
	
	/**
	 * PARA_TYPE=OE_CFA_SITE的UiFieldSet
	 */
	private List<UiFieldSet> cfaSites;//2013.01.15
	
	/**
	 * PARA_TYPE=OE_REWORK_FLAG的UiFieldSet
	 * RMA add by will
	 * 2018.11.12
	 * version:XQ181004
	 */
	private List<UiFieldSet> reworkFlag;
	
	/**
	 * PARA_TYPE=OE_CFA_SITE的UiFieldParam
	 */
	private List<UiFieldParam> cfaSiteUiFieldParams;//2013.01.15
	
	/**
	 * PARA_TYPE=OE_REWORK_FLAG的UiFieldParam
	 * RMA add by will
	 * 2018.11.12
	 * version:XQ181004
	 */
	private List<UiFieldParam> reworkFlagUiFieldParams;
	
	/**
	 * PARA_TYPE=OE_ORDER_TYPE的UiFieldSet
	 */
	private List<UiFieldSet> orderStatus;//2013.01.15
	
	/**
	 * PARA_TYPE=OE_CURRENCY的UiFieldSet
	 */
	private List<UiFieldSet> currencys;
	
	/**
	 * PARA_TYPE=OE_CURRENCY的UiFieldParam
	 */
	private List<UiFieldParam> currencyUiFieldParams;
	
	/**
	 * User選擇cbxCustomer時所對應的CustomerTable
	 */
	private CustomerTable customer;
	
	/**
	 * User選擇cbxBillTo時所對應的CustomerTable
	 */
	private CustomerTable billto;
	
	/**
	 * User選擇cbxShipTo時所對應的CustomerTable
	 */
	private CustomerTable shipto;
	
	
	/**
	 * User選擇cbxOrderFrom時所對應的CustomerTable
	 */
	private CustomerTable orderfrom;
	
	/**
	 * PARA_TYPE=OE_ORDER_TYPE,UiFieldParam
	 */
	private UiFieldParam orderType;
	
	/**
	 * PARA_TYPE=OE_ORDER_TYPE,UiFieldParam
	 */
	private UiFieldParam waferSizeUiFieldParam;
	
	/**
	 * 依據customerId,orderType,product找出internalProduct -- 2017.12.20
	 */
	private ProductNameSetup productNameSetup; 
	
	/**
	 * 日期格式:yyyy/MM/dd
	 */
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
	
	/**
	 * 日期格式:yyyy/MM/dd HH:MM:ss
	 */
	private SimpleDateFormat dateFormatYear2Min = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public List<OrderInternalCheckInfo> saveOrderInternalCheckInfoList = new ArrayList<OrderInternalCheckInfo>();//OCF-PR-150202_接收若檢查OE_INTERNAL_CHECK時檢查到時，放入資料的Arraylist_Allison add
	public List<OeReworkCountSetup> oeReworkCountSetupList = new ArrayList<OeReworkCountSetup>();//OCF-PR-150202_儲存該OE的客戶是否有設定Rework次數_Allison add
	public List<OeOrderNoConfirmModel> oeOrderNoConfirmModel = new ArrayList<OeOrderNoConfirmModel>();//OCF-PR-150307 add
	
	public Window winOrderQuery;
	
	private List<UiFieldParam> lShipTo; //2018.05.14
	
	/**
	 *
	 *
	 */
	@Override
	public void doAfterCompose(Component inComp) throws Exception {
		super.doAfterCompose(inComp);
		userId=loginId;
		operationUnit=OU;
		winOrderEntry.setAttribute("saveOrderInternalCheckInfoList", saveOrderInternalCheckInfoList);//用來跟OE Internal Confirm畫面傳遞參數的Attribute
		winOrderEntry.setAttribute("oeOrderNoConfirmModel", oeOrderNoConfirmModel);//OCF-PR-160307_用來跟OE Order No. Confirm畫面傳遞參數用的Attribute
		winOrderQuery = (Window) execution.getArg().get("winOrderQuery"); 
		List<UiFieldSet> lSetCheckShipTo=commonService.getUiFieldSetLists(this.getClass().getName(),"OE_CHECK_SHIPTO");//2018.05.14
		if (lSetCheckShipTo.size()>0){
			lShipTo=lSetCheckShipTo.get(0).getUiFieldParams();
		}
		this.formShow();
	}
	
	public void formShow(){
		getCustomFieldLength();
		btnSave.setImage("/images/icons/disk.png");
		btnLineAdd.setImage("/images/add.png");
		btnLineDel.setImage("/images/delete.png");
		btnLotnoAdd.setImage("/images/add.png");
		btnLotnoDel.setImage("/images/delete.png");
		btnLineCopyfrom.setImage("images/icons/page_copy.png");
		
		
		//data from parent
		String tmpmode=(String) execution.getArg().get("mode");
		OrderQueryModel tmpOrderQueryModel=(OrderQueryModel) execution.getArg().get("orderQueryModel");
		if (tmpmode!=null){
			mode=tmpmode;
		}
		if (tmpOrderQueryModel!=null){
			orderQueryModel=tmpOrderQueryModel;
		}
		
		//initial Bean
		orderHeader = new OrderHeader();
		orderLines=new ArrayList<OrderLine>();
		saveOrderEntryLineModels=new ArrayList<OrderEntryLineModel>();
		saveOrderEntryLotnoModels=new ArrayList<OrderEntryLotnoModel>();
			
		//OrderStatus = PRODUCTION(20),OrderEntryLine頁面裡不可點選delete,upload from b2b按鈕_2013.10.08_Allison
		if(!"newadd".equals(tmpmode)){
			if ("20".equals(orderQueryModel.getOrderHeader().getOrderStatus())){
				btnLineAdd.setDisabled(true);
				btnLineDel.setDisabled(true);
				btnLotnoAdd.setDisabled(true);
				btnLotnoDel.setDisabled(true);
				btnLineCopyfrom.setDisabled(true);			
			}
		}
		
		//判斷Action Type是"edit" or "readonly" 
		if ("newadd".equals(mode)){
			radioEdit.setChecked(true);
			radioReadonly.setChecked(false);
		}
		else{
			if ("modify".equals(mode)){
				radioEdit.setChecked(true);
				radioReadonly.setChecked(false);
			}
			else{
				radioEdit.setChecked(false);
				radioReadonly.setChecked(true);
			}
			orderHeader=orderQueryModel.getOrderHeader();
			this.setModifyData(orderQueryModel);
		}
		
		if ("newadd".equals(mode)){//2013.07.09
			String waferSize=this.getParaValueByMeaning("12", waferSizeUiFieldParams);
			cbxWaferSize.setValue(waferSize);
			this.onSelect$cbxWaferSize();
		}
		
		
	}
	/**
	 * author:will
	 * version:XQ20181007
	 * dis:change to uppercase when lose focus
	 * 
	 */
	public void onBlur$edtProduct(){
		String lStr=edtProduct.getValue().trim();
		edtProduct.setValue(lStr.toUpperCase());
	}
	
	
	/**
	 * 
	 * getCustomFieldLength:設定特定欄位的長度限制. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	
	public void getCustomFieldLength(){
		//ORDER_HEADER
		List<FieldDefine> orderHeaderFieldDefines=commonService.getFieldDefines("ORDER_HEADER");
		int tmpLen=this.getCustomFieldLength(orderHeaderFieldDefines, "BILLTO_PO");//OCF-PR-150108_PO Num的欄位會改存到BILLTO_PO欄位，故欄位的長度應改抓成[ORDER_HEADER].BILLTO_PO_Allison
		if (tmpLen!=0){
			poNumberLen=tmpLen;
		}
		edtPoNum.setMaxlength(poNumberLen);
		
		tmpLen=this.getCustomFieldLength(orderHeaderFieldDefines, "CUSTOMER_PO");//2013.02.20
		if (tmpLen!=0){
			customerPoLen=tmpLen;
		}
		edtCustomerPo.setMaxlength(customerPoLen);
		
		//ORDER_LINE_LOTNO
		List<FieldDefine> oderLineLotnoFieldDefines=commonService.getFieldDefines("ORDER_LINE_LOTNO");
		tmpLen=this.getCustomFieldLength(oderLineLotnoFieldDefines, "PO_ITEM");
		if (tmpLen!=0){
			poItemLen=tmpLen;
		}
		
		tmpLen=this.getCustomFieldLength(oderLineLotnoFieldDefines, "CUSTOMER_LOTNO");
		if (tmpLen!=0){
			customerLotnoLen=tmpLen;
		}
	}
	
	/**
	 * 
	 * setModifyData:查詢已存在資料庫的ORDER data，並將資料帶入畫面上的元件. <br/>
	 *
	 * @author 030260
	 * @param inOrderQueryModel
	 * @since JDK 1.6
	 */
	public void setModifyData(OrderQueryModel inOrderQueryModel){
		//Header
		String orderFrom="";
		if ("".equals(inOrderQueryModel.getOrderHeader().getOrderFrom())){
			
		}
		else {
			orderFrom=customerInformationService.getCustomerTableByCustomerId(inOrderQueryModel.getOrderHeader().getOrderFrom()).getCustomerShortName();
		}
		
		String waferSize="";
		if ("".equals(inOrderQueryModel.getOrderHeader().getWaferSize())){
			
		}
		else{
			waferSize=this.getParaValueByMeaning(orderQueryModel.getOrderHeader().getWaferSize(), waferSizeUiFieldParams);
		}
		
		cbxCustomer.setValue(inOrderQueryModel.getCustomerName());
		cbxOrderType.setValue(inOrderQueryModel.getOrderTypeName());
		//edtProduct.setValue(inOrderQueryModel.getOrderHeader().getProduct()); //2017.12.20 remark
		edtProduct.setValue(inOrderQueryModel.getOrderHeader().getRealProduct()); //2017.12.20
		edtInternalProduct.setValue(inOrderQueryModel.getOrderHeader().getProduct()); //2017.12.20
		cbxOrderFrom.setValue(orderFrom);
		cbxBillTo.setValue(inOrderQueryModel.getBillToName());
		cbxShipTo.setValue(inOrderQueryModel.getShipToName());
		edtTotalWaferQty.setValue(String.valueOf(inOrderQueryModel.getOrderHeader().getTotalWaferQty()));
		edtOrderDate.setValue(DateFormatUtil.getDateTimeFormat().format(inOrderQueryModel.getOrderHeader().getOrderDate()));
	    edtOrderNumber.setValue(inOrderQueryModel.getOrderHeader().getOrderNumber());
	    //edtPoNum.setValue(inOrderQueryModel.getOrderHeader().getPoNumber());//IT-PR-141201
	    edtPoNum.setValue(inOrderQueryModel.getOrderHeader().getBilltoPo());//IT-PR-141201
	    if(inOrderQueryModel.getOrderHeader().getCustomerPo() != null){
	    edtCustomerPo.setValue(inOrderQueryModel.getOrderHeader().getCustomerPo());
	    }else{
	    	edtCustomerPo.setValue("");
	    }
	    edtAccountMgr.setValue(inOrderQueryModel.getOrderHeader().getAccountMgr());
	    cbxWaferSize.setValue(waferSize);
	    edtOrderStatus.setValue(inOrderQueryModel.getOrderStatusName());
	    
	    if (!"".equals(inOrderQueryModel.getOrderHeader().getCustomerId())){
	    	customer=customerInformationService.getCustomerTableByCustomerId(inOrderQueryModel.getOrderHeader().getCustomerId());
	    }
	    
	    if (!"".equals(inOrderQueryModel.getOrderHeader().getBillTo())){
	    	billto=customerInformationService.getCustomerTableByCustomerId(inOrderQueryModel.getOrderHeader().getBillTo());
	    }
	    
	    if (!"".equals(inOrderQueryModel.getOrderHeader().getShipTo())){
	    	shipto=customerInformationService.getCustomerTableByCustomerId(inOrderQueryModel.getOrderHeader().getShipTo());
	    }
	    
	    if (!"".equals(inOrderQueryModel.getOrderHeader().getOrderFrom())){
	    	orderfrom=customerInformationService.getCustomerTableByCustomerId(inOrderQueryModel.getOrderHeader().getOrderFrom());
	    }
	    
	    if (!"".equals(inOrderQueryModel.getOrderHeader().getOrderType())){
	    	orderType=orderTypeUiFieldParams.get(cbxOrderType.getSelectedIndex());
	    }
	    
	    if (!"".equals(inOrderQueryModel.getOrderHeader().getWaferSize())){
	    	waferSizeUiFieldParam=waferSizeUiFieldParams.get(cbxWaferSize.getSelectedIndex());
	    }
	    
	    chkB2bDisableFlag.setChecked(inOrderQueryModel.getOrderHeader().getB2bDisableFlag());
	    
	    //Line
	    edtLineOrderNumber.setText(edtOrderNumber.getText().trim());
	    edtLinePoNumber.setText(edtPoNum.getText().trim());
	    edtLineCustomerPo.setText(edtCustomerPo.getText().trim());
	    //XQ20181007_will
	    edtLineProduct.setText(edtProduct.getText().trim().toUpperCase());
	    List<OrderLine> tmpOrderLines=new ArrayList<OrderLine>();
	    tmpOrderLines=orderEntryService.getOrderLinesByOrderNumber(edtOrderNumber.getText().trim());
	    if (tmpOrderLines.size()>0){
	    	for (int i=0;i<tmpOrderLines.size();i++){
	    		OrderEntryLineModel tmpOrderEntryLineModel=new OrderEntryLineModel();
	    		tmpOrderEntryLineModel.setOrderLineIntIdx(0);
	    		tmpOrderEntryLineModel.setOrderLine(tmpOrderLines.get(i));
	    		
	    		saveOrderEntryLineModels.add(tmpOrderEntryLineModel);
	    	}
	    	
	    	
	    	for (int i=0;i<saveOrderEntryLineModels.size();i++){
	    		//先將WaferSize由meaning轉成ParaValue,之後真正儲存時會再轉為meaning
	    		String tmpWaferSizeMeaning=saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize();
	    		saveOrderEntryLineModels.get(i).getOrderLine().setWaferSize(this.getParaValueByMeaning(tmpWaferSizeMeaning, waferSizeUiFieldParams));
	    		
	    		//先將Currency由meaning轉成ParaValue,之後真正儲存時會再轉為meaning
	    		String tmpCurrencyMeaning=saveOrderEntryLineModels.get(i).getOrderLine().getCurrency();
	    		saveOrderEntryLineModels.get(i).getOrderLine().setCurrency(this.getParaValueByMeaning(tmpCurrencyMeaning, currencyUiFieldParams));
	    		
	    		//2013.01.15先將CfaSite由meaning轉成ParaValue,之後真正儲存時會再轉為meaning
	    		String tmpCfaSiteMeaning=saveOrderEntryLineModels.get(i).getOrderLine().getCfaSite();
	    		saveOrderEntryLineModels.get(i).getOrderLine().setCfaSite(this.getParaValueByMeaning(tmpCfaSiteMeaning, cfaSiteUiFieldParams));
	    	}
	    	
	    	grdLine.setModel(new ListModelList(saveOrderEntryLineModels));
	    	grdLine.setItemRenderer(this);
	    	grdLine.setSelectedIndex(0);
	    }
	    
	    //Lotno
	    List<OrderLineLotno> tempOrderLotnos=new ArrayList<OrderLineLotno>();
	    tempOrderLotnos=orderEntryService.getOrderLineLotnosByOrderNumber(edtOrderNumber.getText());
	    if (tempOrderLotnos.size()>0){
	    	for (int i=0;i<tempOrderLotnos.size();i++){
	    		OrderEntryLotnoModel tmpOrderEntryLotnoModel=new OrderEntryLotnoModel();
	    		tmpOrderEntryLotnoModel.setMode(mode);
	    		tmpOrderEntryLotnoModel.setOrderLineLotno(tempOrderLotnos.get(i));
	    		tmpOrderEntryLotnoModel.setCustomerLotnoLen(customerLotnoLen);
	    		tmpOrderEntryLotnoModel.setOrderHeaderOrderStatus(orderHeader.getOrderStatus());//2013.07.09
	    		
	    		//如果tempOrderLotnos.get(i).isHoldflag==1時，代表有Hold的資料,一定是非newadd的狀態下才可能會有hold
	    		if (tempOrderLotnos.get(i).isHoldFlag()){
	    			Hold tmpHold=orderEntryService.getHold(orderHeader.getOrderNumber(), tempOrderLotnos.get(i).getPoItem(), tempOrderLotnos.get(i).getCustomerLotno().trim());
	    			tmpOrderEntryLotnoModel.setHold(tmpHold);
	    		}
	    		else {
	    			tmpOrderEntryLotnoModel.setHold(null);
				}
	    		
	    		//2013.02.07 WaferStatus
//	    		WaferStatus tmpWaferStatus=new WaferStatus();
//	    		tmpWaferStatus=waferStatusService.getWaferStatusByEntityId(tempOrderLotnos.get(i).getEntityId());
//	    		tmpOrderEntryLotnoModel.setWaferStatus(tmpWaferStatus);
	    		
	    		saveOrderEntryLotnoModels.add(tmpOrderEntryLotnoModel);
	    	}
	    }
	    this.onClick$grdLine();
	    
	    this.setReadonly();//如果是readonly的話，控制Button disable
	}

	/**
	 * 設定畫面上的Label naming
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#setLabelsValue()
	 */
	@Override
	protected void setLabelsValue() {
		//edtPoNum.setMaxlength(poNumberLen);
		//window
		winOrderEntry.setTitle(Labels.getLabel("oe.edit.winOrderEntry"));
		
		//Radio
		grbActionType.setLabel(Labels.getLabel("oe.edit.grbActionType"));
		radioEdit.setLabel(Labels.getLabel("oe.edit.radioEdit"));
		radioReadonly.setLabel(Labels.getLabel("oe.edit.radioReadonly"));
		
		//sheet name
		tabHeader.setLabel(Labels.getLabel("oe.edit.tabHeader"));
		tabLine.setLabel(Labels.getLabel("oe.edit.tabLine"));
		
		//actionType
		grbActionType.setLabel(Labels.getLabel("oe.edit.grbActionType"));

		//field define
		lblCustomer.setValue(Labels.getLabel("oe.edit.header.lblCustomer"));
		lblOrderType.setValue(Labels.getLabel("oe.edit.header.lblOrderType"));
		lblProduct.setValue(Labels.getLabel("oe.edit.header.lblProduct"));
		lblInternalProduct.setValue(Labels.getLabel("oe.edit.header.lblInternalProduct")); //2017.12.20
		lblOrderFrom.setValue(Labels.getLabel("oe.edit.header.lblOrderFrom"));
		lblBillTo.setValue(Labels.getLabel("oe.edit.header.lblBillTo"));
		lblShipTo.setValue(Labels.getLabel("oe.edit.header.lblShipTo"));
		lblTotalWaferQty.setValue(Labels.getLabel("oe.edit.header.lblTotalWaferQty"));
		lblOrderDate.setValue(Labels.getLabel("oe.edit.header.lblOrderDate"));
		lblOrderNumber.setValue(Labels.getLabel("oe.edit.header.lblOrderNumber"));
		lblPoNumber.setValue(Labels.getLabel("oe.edit.header.lblPoNumber"));
		lblCustomerPo.setValue(Labels.getLabel("oe.edit.header.lblCustomerPo"));
		lblAccountMgr.setValue(Labels.getLabel("oe.edit.header.lblAccountMgr"));
		lblWaferSize.setValue(Labels.getLabel("oe.edit.header.lblWaferSize"));
		lblOrderStatus.setValue(Labels.getLabel("oe.edit.header.lblOrderStatus"));
		
		//Group box
		grbHeaderData.setLabel(Labels.getLabel("oe.edit.line.grbHeaderData"));
		grbLineData.setLabel(Labels.getLabel("oe.edit.line.grbLineData"));
		grbLotnoData.setLabel(Labels.getLabel("oe.edit.line.grbLotnoData"));
		grbGlobalSetting.setLabel(Labels.getLabel("oe.edit.line.grbGlobalSetting"));
		
		lblLinePONumber.setValue(Labels.getLabel("oe.edit.line.lblLinePONumber"));
		lblLineProduct.setValue(Labels.getLabel("oe.edit.line.lblLineProduct"));
	    lblLineCustomerPo.setValue(Labels.getLabel("oe.edit.line.lblLineCustomerPo"));
		lblLineOrderNumber.setValue(Labels.getLabel("oe.edit.line.lblLineOrderNumber"));
		lblGRequestData.setValue(Labels.getLabel("oe.edit.line.grdLine.colLineRequestDate"));
		lblGCurrency.setValue(Labels.getLabel("oe.edit.line.grdLine.colLineCurrency"));
		lblGCfaSite.setValue(Labels.getLabel("oe.edit.line.grdLine.colLineCfaSite"));
		lblGWaferSupplyDate.setValue(Labels.getLabel("oe.edit.line.grdLine.colLineWaferSupplyDate"));//2013.02.08
		//version:XQ181004 RMA add by will 2018.11.08
		lblGReworkFlag.setValue(Labels.getLabel("oe.edit.line.grdLine.colLineReworkFlag"));
		//Button
		//log.debug(Labels.getLabel("oe.edit.btnSave"));
		btnSave.setLabel(Labels.getLabel("oe.edit.btnSave"));
		btnLineAdd.setLabel(Labels.getLabel("oe.edit.line.btnLineAdd"));
		btnLineDel.setLabel(Labels.getLabel("oe.edit.line.btnLineDel"));
		btnLineCopyfrom.setLabel(Labels.getLabel("oe.edit.line.btnLineCopyfrom"));
		btnLotnoAdd.setLabel(Labels.getLabel("oe.edit.line.btnLotnoAdd"));
		btnLotnoDel.setLabel(Labels.getLabel("oe.edit.line.btnLotnoDel"));
		
		//grdLine field name
		colLinePoItem.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLinePoItem"));
		colLineMtrlNum.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineMtrlNum"));
		colLineSourceMtrlNum.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineSourceMtrlNum"));
		colLineMtrlDesc.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineMtrlDesc"));
		colLineMtrlGroup.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineMtrlGroup"));
		colLineWaferQty.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineWaferQty"));
		colLineWaferSupplyDate.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineWaferSupplyDate"));//2013.02.08
		colLineWaferSize.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineWaferSize"));
		colLineCurrency.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineCurrency"));
		colLineUnitPrice.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineUnitPrice"));
		colLineSellingPrice.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineSellingPrice"));
		colLineRequestDate.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineRequestDate"));
		colLineDesignId.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineDesignId"));
		colLineCfaProId.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineCfaProId"));
		colLineShipToVendorCode.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineShipToVendorCode"));
		//colLlineShipToVendorName.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLlineShipToVendorName"));//2013.06.26
		colLineShipComment.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineShipComment"));
		colLineShippingDestination.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineShippingDestination"));//OCF-PR-160307 add
		colLineCompanyCode.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineCompanyCode"));
		colLineCountryOfFab.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineCountryOfFab"));
		colLineFab.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineFab"));
		colLineCfaSite.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineCfaSite"));//2013.01.15
		
		colBatchWaferDie.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineWaferDie"));//OCF-PR-150202_Allison add
		colBatchGradeRecord.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineGradeRecord"));//OCF-PR-150202_Allison add
		colBatchEngNo.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineEngNo"));//OCF-PR-150202_Allison add
		colBatchTestProgram.setLabel(Labels.getLabel("oe.edit.line.grdLine.colLineTestProgram"));//OCF-PR-150202_Allison add
		
		//grdLotno field name
		colBatchPoItem.setLabel(Labels.getLabel("oe.edit.line.grdLotno.colBatchPoItem"));
		colBatchCustomerLotno.setLabel(Labels.getLabel("oe.edit.line.grdLotno.colBatchCustomerLotno"));
		colBatchWaferQty.setLabel(Labels.getLabel("oe.edit.line.grdLotno.colBatchWaferQty"));
		
		chkB2bDisableFlag.setValue(Labels.getLabel("oe.edit.line.grdLotno.chkB2bDisableFlag"));
	}
	
	public void chkProductInfo(){//2013.07.09
		ProductInfo productInfo=orderEntryService.getProductInfo(customer.getInaviCode(), orderType.getMeaning(), edtProduct.getValue());//2013.07.15 moidfy
		if (productInfo==null){
			this.showmessage("Information", Labels.getLabel("oe.edit.chk.productInfo"));
		}
	}

	public void onClick$btnSave(){	
		log.debug(mode);
		this.chkBaseCtrlSet(); //確認畫面上有設定no empty的元件是否都有值.
		
		String checkflag = this.checkOtherInput();//OCF-PR-150202_確認沒有設定在[BASE_CTRL_SET]裡，必須另外判斷是否必填的欄位_Allison add
		if(!"".equals(checkflag)){
			if("1".equals(checkflag)){
				Messagebox.show(Labels.getLabel("oe.save.message.noWaferData"), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}else{
				Messagebox.show(Labels.getLabel("oe.save.message.waferQtyNotEqualWaferDataCount")+checkflag, "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}
		}
		
		this.chkProductInfo();//2013.07.09 //Check Product Name是否有建在[PRODUCT_INFO]
		
		if (this.chkData()==false){
			return;
		}

		//OCF-PR-150202_新增OE按Save三個檢查機制_Allison
		//Check 1. OE檢查不可重覆OE(條件: 檢查是否有Order未關帳 & WaferNo <> Shipping)
		//      →目的 : 防止WaferNo 已做OE &下線 , 但尚未出貨, PC 要再做一次OE(若是改工單, 需先做Order Cancel再OE)
		//Check 2. OE 一次以上的檢查, 跳出PC Comment給User, 若要繼續OE需輸入Comment才能做OE(條件: OE create次數, 但不包含CANCEL_FLAG=1)
		//      →目的 : 提醒PC注意若前一次OE的Wafer有特殊狀況, 需要將前一次OE的Internal Notice待到這一次OE
		//Check 3. OE檢查Rework次數是否超過PC設定的次數(條件: 檢查Wafer Shipping次數)
		//      →目的 : 提醒PC注意Wafer在TCE Rework的次數(因客戶有要求Wafer Rework的次數)
		//OCF-PR-150604_當建新Order & modify時有修改Customer or WaferData，才需要檢查
		boolean flag = false;
		if("modify".equals(mode)){
			flag = this.checkModifyCustomerAndWaferData();
		}
		if("newadd".equals(mode) || !flag){
			if(this.oeInternalCheck() ==  false){
				return;
			}
		}else{
			if(saveOrderInternalCheckInfoList.size() > 0){
				saveOrderInternalCheckInfoList.clear();
			}
		}
		
		//OCF-PR-160307_若符合OE Internal Check Rule1的話，則代表有Wafer同時在兩個OE內，因此需跳出視窗Order No Confirm視窗給PC選擇要儲存成那個Order No(http://it-sd-dev/redmine/issues/7469)
		if("newadd".equals(mode) || !flag){
			LinkedHashMap<String,String> errMsg = this.internalCheckRule1();
			if(!"".equals(errMsg.get("msg"))){
				oeOrderNoConfirmModel.clear();

				Map args = new HashMap();
				args.put("errMsg", errMsg);
				args.put("thisOrderNumber", edtOrderNumber.getText());
				args.put("thisProduct", edtProduct.getText());
				args.put("saveOrderEntryLotnoModels", saveOrderEntryLotnoModels);
				args.put("Function", "OE");//OCF-PR-160702 add
				Window winOeOrderNoConfirm = (Window)Executions.createComponents("/WEB-INF/modules/oe/OeOrderNoConfirm.zul", null, args);
				winOeOrderNoConfirm.setParent(winOrderEntry);
				winOeOrderNoConfirm.doModal();

				oeOrderNoConfirmModel = (List<OeOrderNoConfirmModel>) winOrderEntry.getAttribute("oeOrderNoConfirmModel");
			}
		}
		
		String msg=Labels.getLabel("oe.save.confirm");//2013.06.25
		//2013.06.25 Line.WaferSize如果與Header不同時，alarm
		/*boolean waferSizeDiffFlag=false;
		boolean shipToDiffFlag=false;
		for (int i=0;i<saveOrderEntryLineModels.size();i++){
			if (!cbxWaferSize.getValue().equals(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize())){
				waferSizeDiffFlag=true;
			}
			if (!cbxShipTo.getValue().equals(saveOrderEntryLineModels.get(i).getOrderLine().getShipToVendorName())){
				shipToDiffFlag=true;
			}
		}//end for i
		
		if (waferSizeDiffFlag){
			msg+="\r\n\r\n"+Labels.getLabel("oe.save.chk.lineheader.wafersize",new Object[] {Labels.getLabel("oe.edit.header.lblWaferSize")});
		}
		if (shipToDiffFlag){
			msg+="\r\n\r\n"+Labels.getLabel("oe.save.chk.lineheader.wafersize",new Object[] {Labels.getLabel("oe.edit.header.lblShipTo")});
		}*/
		
		Messagebox.show(msg, "Question", 
				Messagebox.OK | Messagebox.CANCEL, 
				Messagebox.QUESTION, 
				new org.zkoss.zk.ui.event.EventListener(){
				    public void onEvent(Event inEvt) throws InterruptedException,Exception {
				    	if ("onOK".equals(inEvt.getName())){
				    			if(!updateWaferBankinWafer()){
				    				return;
				    			}
				    		 	saveOE();
				    	 }
				    }
				});
		
	}
	
	/**
	 * @author will
	 * des:save rework flag 2 TSES_OVTRMA_LOT 
	 * date：2018.11.10
	 * version:XQ181004
	 */
	 public void saveReWork(){
		 String cbxGReworkFlagVal =cbxGReworkFlag.getValue();
		 //借用saveOrderEntryLotnoModels中的信息
		 if (saveOrderEntryLotnoModels.size()>0 && cbxGReworkFlagVal.trim().length()>0){
			 
			 for(int i=0 ;i<saveOrderEntryLotnoModels.size(); i++){
				 TsesOvtRmaLot tsesOvtRmaLot=new TsesOvtRmaLot();
				 tsesOvtRmaLot.setOrderNumber(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getOrderNumber());
				 tsesOvtRmaLot.setCustomerLotno(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno());
				 tsesOvtRmaLot.setRmaConfirm(cbxGReworkFlagVal);
				 tsesOvtRmaLot.setCreatedDate(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getUpdateDate());
				 tsesOvtRmaLot.setCreatedUser(userId);
				 orderEntryService.saveReworkFlag(tsesOvtRmaLot);
			 }
		 }
	 }
	
	/**
	 * 
	 * shipConfirmChk:如果Ship Confirm後有修改po,price,billto,currency時，秀出alarm及發mail
	 *
	 * @author 030260
	 * @throws Exception 
	 * @since JDK 1.6
	 */
	public void shipConfirmChk() throws Exception{
		if ("modify".equals(mode)){
			boolean haveShipConfirmLine=false;
			boolean havePackingList=false;
			String diffHeader="";
			//Before orderHeader
			OrderHeader beforeOrderHeader=orderEntryService.getOrderHeaderByOrderNumber(edtOrderNumber.getText());
			
			//Before orderLine
			List<OrderLine> beforeOrderLines=orderEntryService.getOrderLinesByOrderNumber(edtOrderNumber.getText());
			
			//PO是否有異動
			String before=beforeOrderHeader.getPoNumber();
			String after=edtPoNum.getText();
			if (!before.equals(after)){
				diffHeader+="["+Labels.getLabel("oe.query.grdOrderData.colDataPONumber")+"]"+before+"-->"+after;
			}
			
			//BillTo是否有異動
			CustomerTable beforeBillTo=customerInformationService.getCustomerTableByCustomerId(beforeOrderHeader.getBillTo());
			before=beforeOrderHeader.getBillTo();
			CustomerTable afterBillTo=billtos.get(cbxBillTo.getSelectedIndex());
			after=afterBillTo.getCustomerId();
			if (!before.equals(after)){
				diffHeader+="["+Labels.getLabel("oe.query.grdOrderData.colDataBillTo")+"]"+beforeBillTo.getCustomerShortName()+"-->"+cbxBillTo.getText();
			}
			
			//Price,Currency,BillTo,PO(Vicky不需要比對PO)
			String diffLine="";
			String vivianStr="";
			for (int i=0;i<beforeOrderLines.size();i++){
				//是否已做ship confirm
				boolean shipconfirm=false;
				//是否已開送貨單
				boolean packinglist=false;
				
				//此筆PO Item底下的OrderLineLotno
				List<OrderLineLotno> tmpOrderLineLotnos=orderEntryService.getOrderLineLotnosByOrderNumberPoItem(beforeOrderLines.get(i).getOrderNumber(), 
																												beforeOrderLines.get(i).getPoItem());
				for (int j=0;j<tmpOrderLineLotnos.size();j++){
					List<ShippingDetail> shippingDetails=finService.getShippingDetailsByPoNumberCustomerLotno(beforeOrderHeader.getPoNumber(), 
																									   	      tmpOrderLineLotnos.get(j).getCustomerLotno());
					if (shippingDetails.size()>0){
						packinglist=true;
						
						for (int k=0;k<shippingDetails.size();k++){
							if (shippingDetails.get(k).isShipConfirm()){
								shipconfirm=true;
								break;//break k
							}
						}//end for k
					}
				}//end for j
				
				if (packinglist==true){
					havePackingList=true;
					
					//是否可以在saveOrderEntryLineModels找到資料
					int c=-1;
					boolean flag=false;
					for (int j=0;j<saveOrderEntryLineModels.size();j++){
						if ((beforeOrderLines.get(i).getOrderNumber().equals(saveOrderEntryLineModels.get(j).getOrderLine().getOrderNumber())) &&
							(beforeOrderLines.get(i).getPoItem().equals(saveOrderEntryLineModels.get(j).getOrderLine().getPoItem()))){
							flag=true;
							c=j;
							break;//j
						}
					}//end for j
					
					if (flag==true){//flag=true才有可能是有異動的，如果flag=false代表是後來新增的PO Item
						String str1="";
						//Price是否有異動
						before=String.valueOf(beforeOrderLines.get(i).getUnitPrice());
						after=String.valueOf(saveOrderEntryLineModels.get(c).getOrderLine().getUnitPrice());
						if (!before.equals(after)){
							str1+="["+Labels.getLabel("oe.edit.line.grdLine.colLineUnitPrice")+"]"+before+"-->"+after;
						}
						
						//Currency是否有異動
					    before=this.getParaValueByMeaning(beforeOrderLines.get(i).getCurrency(),currencyUiFieldParams);
					    after=saveOrderEntryLineModels.get(c).getOrderLine().getCurrency();
					    if (!before.equals(after)){
							str1+="["+Labels.getLabel("oe.edit.line.grdLine.colLineCurrency")+"]"+before+"-->"+after;
						}
					    
					    if (!"".equals(str1)){
					    	vivianStr+="["+Labels.getLabel("oe.edit.line.lblLineOrderNumber")+"]"+beforeOrderLines.get(i).getOrderNumber()+
					    			  "["+Labels.getLabel("oe.edit.line.lblLineProduct")+"]"+beforeOrderHeader.getProduct()+
					    			  "["+Labels.getLabel("oe.edit.line.grdLine.colLinePoItem")+"]"+beforeOrderLines.get(i).getPoItem();
					    	if (!"".equals(diffHeader)){
					    		vivianStr+=diffHeader;
					    	}
					    	vivianStr+=str1+"\n";
					    }
					    
					}
				}
				
				if (shipconfirm==true){
					haveShipConfirmLine=true;
					
					//是否可以在saveOrderEntryLineModels找到資料
					int c=-1;
					boolean flag=false;
					for (int j=0;j<saveOrderEntryLineModels.size();j++){
						if ((beforeOrderLines.get(i).getOrderNumber().equals(saveOrderEntryLineModels.get(j).getOrderLine().getOrderNumber())) &&
							(beforeOrderLines.get(i).getPoItem().equals(saveOrderEntryLineModels.get(j).getOrderLine().getPoItem()))){
							flag=true;
							c=j;
							break;//j
						}
					}//end for j
					
					if (flag==true){//flag=true才有可能是有異動的，如果flag=false代表是後來新增的PO Item
						String str1="";
						//Price是否有異動
						before=String.valueOf(beforeOrderLines.get(i).getUnitPrice());
						after=String.valueOf(saveOrderEntryLineModels.get(c).getOrderLine().getUnitPrice());
						if (!before.equals(after)){
							str1+="["+Labels.getLabel("oe.edit.line.grdLine.colLineUnitPrice")+"]"+before+"-->"+after;
						}
						
						//Currency是否有異動
					    before=this.getParaValueByMeaning(beforeOrderLines.get(i).getCurrency(),currencyUiFieldParams);
					    after=saveOrderEntryLineModels.get(c).getOrderLine().getCurrency();
					    if (!before.equals(after)){
							str1+="["+Labels.getLabel("oe.edit.line.grdLine.colLineCurrency")+"]"+before+"-->"+after;
						}
					    
					    if (!"".equals(str1)){
					    	diffLine+="["+Labels.getLabel("oe.edit.line.lblLineOrderNumber")+"]"+beforeOrderLines.get(i).getOrderNumber()+
					    			  "["+Labels.getLabel("oe.edit.line.lblLineProduct")+"]"+beforeOrderHeader.getProduct()+
					    			  "["+Labels.getLabel("oe.edit.line.grdLine.colLinePoItem")+"]"+beforeOrderLines.get(i).getPoItem();
					    	if (!"".equals(diffHeader)){
					    		diffLine+=diffHeader;
					    	}
					    	diffLine+=str1+"\n";
					    }
					}
				}//end if (shipconfirm==true)
			}//end i
			
			if ((haveShipConfirmLine==true)&&(!"".equals(diffLine))){
				this.showmessage("Warning", Labels.getLabel("oe.edit.shipconfirm.alarm",
						new Object[] {"\r\n",diffLine}));
				Date today=new Date();
				String mailContent="Operator: "+userId+","+userService.getEmplNameByEmplId(userId)+"\r\n";
				mailContent+="Operation Time: "+dateFormatYear2Min.format(today)+"\r\n";
				mailContent+="Program: iVision-Order Entry"+"\r\n\r\n";
				mailContent+=Labels.getLabel("oe.edit.shipconfirm.alarm",new Object[] {"\r\n",diffLine});
				MailUtil mailUtil=new MailUtil();
				List<MailList> maiLists=commonService.getMailLists("AFTER_SHIPCONFIRM_ALARM");
				mailUtil.mailing(maiLists, "Your operation can not be synchronized to iMask!", mailContent, null);
			}
			
			if ((havePackingList==true)&&(!"".equals(vivianStr))){
				//this.showmessage("Warning", Labels.getLabel("oe.edit.packinglist.alarm",
				//		new Object[] {"\r\n",diffLine}));
				Date today=new Date();
				String mailContent1="Operator: "+userId+","+userService.getEmplNameByEmplId(userId)+"\r\n";
				mailContent1+="Operation Time: "+dateFormatYear2Min.format(today)+"\r\n";
				mailContent1+="Program: iVision-Order Entry"+"\r\n\r\n";
				mailContent1+=Labels.getLabel("oe.edit.packinglist.alarm",new Object[] {"\r\n",diffLine});
				MailUtil mailUtil1=new MailUtil();
				List<MailList> maiList1s=commonService.getMailLists("AFTER_PACKINGLIST_ALARM");
				mailUtil1.mailing(maiList1s, "After Packing List,PC change OE data!", mailContent1, null);
			}
		}//end if ("modify".equals(mode))
	}
	
	/**
	 * 
	 * chkBaseCtrlSet:確認畫面上有設定no empty的元件是否都有值. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void chkBaseCtrlSet(){
		//Header
		//欄位輸入加上去頭尾空白_2013.10.08_Allison
		cbxCustomer.getValue().trim();
		cbxOrderType.getValue().trim();
		edtProduct.getValue().trim();
		cbxOrderFrom.getValue().trim();
		cbxBillTo.getValue().trim();
		cbxShipTo.getValue().trim();
		//edtTotalWaferQty.getValue().trim().trim();//OCF-PR-151002_OE建立by到OrderLineLotNo，因此一開始不必先輸入總WaferQty
		edtOrderDate.getValue().trim();
		edtOrderNumber.getValue().trim();
		edtPoNum.getValue().trim();
		edtCustomerPo.getValue().trim();
		edtAccountMgr.getValue().trim().trim();
		cbxWaferSize.getValue().trim();
		edtOrderStatus.getValue().trim();
		edtInternalProduct.getValue().trim(); //2017.12.20
		
		//Line
		//寫以下程式碼是為了讓有設定Constraint=on empty的元件產生作用
		List<Listitem> selectItems = new ArrayList<Listitem>(grdLine.getItems());
		for (int i =0;i< selectItems.size(); i++) {
			Listitem o = selectItems.get(i);

			//處理 由使用者可填寫的欄位
			Textbox edtcellPoItem = (Textbox) o.getFellow("edtcellPoItem"+o.getIndex());
			Textbox edtcellMtrlDesc = (Textbox) o.getFellow("edtcellMtrlDesc"+o.getIndex());
			Textbox edtcellMtrlGroup = (Textbox) o.getFellow("edtcellMtrlGroup"+o.getIndex());
			Spinner edtcellWaferQty = (Spinner) o.getFellow("edtcellWaferQty"+o.getIndex());
			Datebox edtcellWaferSupplyDate = (Datebox) o.getFellow("edtcellWaferSupplyDate"+o.getIndex());//2013.02.08
			Datebox edtcellRequestDate = (Datebox) o.getFellow("edtcellRequestDate"+o.getIndex());
			Combobox cbxcellWaferSize = (Combobox) o.getFellow("cbxcellWaferSize"+o.getIndex());
			Textbox edtcellDesignId = (Textbox) o.getFellow("edtcellDesignId"+o.getIndex());
			Combobox cbxcellCurrency = (Combobox) o.getFellow("cbxcellCurrency"+o.getIndex());
			Textbox edtcellUnitPrice = (Textbox) o.getFellow("edtcellUnitPrice"+o.getIndex());
			Textbox edtcellSellingPrice = (Textbox) o.getFellow("edtcellSellingPrice"+o.getIndex());
			Combobox cbxcellCfaSite = (Combobox) o.getFellow("cbxcellCfaSite"+o.getIndex());
			//Textbox edtcellShipToVendorName = (Textbox) o.getFellow("edtcellShipToVendorName"+o.getIndex());//2013.06.26
			Textbox edtcellShipComment = (Textbox) o.getFellow("edtcellShipComment"+o.getIndex());
			Textbox edtcellCountryOfFab = (Textbox) o.getFellow("edtcellCountryOfFab"+o.getIndex());
			Textbox edtcellFab = (Textbox) o.getFellow("edtcellFab"+o.getIndex());
			Textbox edtcellCfaProId = (Textbox) o.getFellow("edtcellCfaProId"+o.getIndex());
			//Textbox edtcellShipToVendorCode = (Textbox) o.getFellow("edtcellShipToVendorCode"+o.getIndex());
			Textbox edtcellCompanyCode = (Textbox) o.getFellow("edtcellCompanyCode"+o.getIndex());
			Textbox edtcellMtrlNum = (Textbox) o.getFellow("edtcellMtrlNum"+o.getIndex());
			
			//欄位輸入加上去頭尾空白_2013.10.08_Allison
			edtcellPoItem.getValue().trim();
			edtcellMtrlDesc.getValue().trim();
			edtcellMtrlGroup.getValue().trim();
			edtcellWaferQty.getValue().toString().trim();
			edtcellWaferSupplyDate.getValue().toString().trim();//2013.02.08
			edtcellRequestDate.getValue().toString().trim();
			cbxcellWaferSize.getValue().trim();
			edtcellDesignId.getValue().trim();
			cbxcellCurrency.getValue().trim();
			edtcellUnitPrice.getValue().trim();
			edtcellSellingPrice.getValue().trim();
			cbxcellCfaSite.getValue().trim();
			//edtcellShipToVendorName.getValue();//2013.06.26
			edtcellShipComment.getValue().trim();
			edtcellCountryOfFab.getValue().trim();
			edtcellFab.getValue().trim();
			edtcellCfaProId.getValue().trim();
			//edtcellShipToVendorCode.getValue().trim();
			edtcellCompanyCode.getValue().trim();
			edtcellMtrlNum.getValue().trim();
		}
	}
	
	/**
	 * 自訂的MustBe check for "newadd" 及 "modify" 狀態
	 */
	public boolean chkData(){
		//1.Header
		if (this.chkComboboxSelectItemindex()==false){
			return false;
		}
		
		//2017.12.20
		if ("".equals(edtInternalProduct.getText().trim())){
    		this.showmessage("Warning", Labels.getLabel("oe.save.line.musthavedata",new Object[] {Labels.getLabel("oe.edit.header.lblInternalProduct")}));
    		return false;			
		}
		
			//2013.02.07 如果是APTINA時,CUSTOMER_PO必填
		    //if ("01".equals(customer.getCustomerId())){//IT-PR-141201 CustomerPO改必填
		    	if ("".equals(edtCustomerPo.getText())){
		    		this.showmessage("Warning", Labels.getLabel("oe.edit.header.chkCustomerPo",new Object[] {Labels.getLabel("oe.edit.header.lblCustomerPo")}));
		    		return false;
		    	}
		    //}
		    
		    //2013.06.21 BILL_TO+PO_NUMBER不可重複(排除CANCEL_FLAG=1)
		    /*IT-PR-141201改為不卡PO_NUMBER重複
		    boolean poNumDuplicateFlag=false;
		    int poNumDuplicateRow=-1;
		    List<OrderHeader> chkDuplicateBilltoPoNumbers=orderEntryService.getOrderHeaderByBillToPoNumber(billto.getCustomerId(), edtLineOrderNumber.getValue());
		    for (int i=0;i<chkDuplicateBilltoPoNumbers.size();i++){
		    	if (edtPoNum.getValue().compareTo(chkDuplicateBilltoPoNumbers.get(i).getPoNumber())==0){
		    		poNumDuplicateFlag=true;
		    		poNumDuplicateRow=i;
		    		break;
		    	}
		    }
		    if (poNumDuplicateFlag){
		    	this.showmessage("Error", Labels.getLabel("oe.edit.header.chkBillToPoNumber",
		    			new Object[] {billto.getCustomerShortName(),edtPoNum.getValue(),sdf.format(chkDuplicateBilltoPoNumbers.get(poNumDuplicateRow).getOrderDate())}));
		    	return false;
		    }*/
		    
		
		//2.Line
			//2-1.Line必須有資料
			if (grdLine.getItemCount()==0){
				this.showmessage("Warning", Labels.getLabel("oe.save.line.musthavedata",
						new Object[] {Labels.getLabel("oe.edit.line.grbLineData")}));
				return false;
			}
			
			//2-2.Line.POItem必須有值，且不可重複
			if (this.chkgrdLinePOItemcannotDuplicate()==false){
				return false;
			}
			
			//2-3.Line.PoItem必須在Lotno都要有找到資料
			for (int i=0;i<saveOrderEntryLineModels.size();i++){
				boolean lotnoFlag=false;
				for (int j=0;j<saveOrderEntryLotnoModels.size();j++){
					if (saveOrderEntryLineModels.get(i).getOrderLine().getPoItem().trim().equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getPoItem().trim())){
						lotnoFlag=true;
						break;
					}
				}
				
				if (lotnoFlag==false){
					this.showmessage("Warning",Labels.getLabel("oe.edit.line.chkLotno",
							new Object[] {Labels.getLabel("oe.edit.line.grdLine.colLinePoItem"),
										  saveOrderEntryLineModels.get(i).getOrderLine().getPoItem(),
										  Labels.getLabel("oe.edit.line.grbLotnoData")}));
					return false;
				}
			}
			
			int sumWaferQty=0;
			for (int i=0;i<saveOrderEntryLineModels.size();i++){
				//2-4.Line.waferSize ?= Header.waferSize
				//log.debug("Header.wafersize="+cbxWaferSize.getText());
				//log.debug("Line.WaferSize="+saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize());
				if (saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize().equals(cbxWaferSize.getText())){
					
				}
				else{
					this.showmessage("Warning", Labels.getLabel("oe.edit.line.chkWaferSize.diffwithHeader",new Object[] {Labels.getLabel("oe.edit.line.grdLine.colLineWaferSize")}));
					return false;
				}
				
				//2-5.Line.WaferQty ?= sum(Lotno.WaferQty)
				int sumLotnoWafetQty=0;
				for (int j=0;j<saveOrderEntryLotnoModels.size();j++){
					if (saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getPoItem().trim().equals(saveOrderEntryLineModels.get(i).getOrderLine().getPoItem().trim())){
						sumLotnoWafetQty=sumLotnoWafetQty+saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getWaferQty();
					}
				}
				if (sumLotnoWafetQty!=saveOrderEntryLineModels.get(i).getOrderLine().getWaferQty()){
					this.showmessage("Warning", Labels.getLabel("oe.edit.line.chkWaferQty.diffwithLotnosum",
							new Object[] {Labels.getLabel("oe.edit.line.grdLine.colLineWaferQty"),
										  Labels.getLabel("oe.edit.line.grdLine.colLinePoItem"),
										  saveOrderEntryLineModels.get(i).getOrderLine().getPoItem()}));
					return false;
				}
				
				//2-6.WaferSize 必須是選單內的選項
				if (!"".equals(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize())){
					if (this.chkSelectItemforCombobox(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize(), waferSizeUiFieldParams)==false){
						this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",
								new java.lang.Object[] {Labels.getLabel("oe.edit.line.grdLine.colLineWaferSize")}));
						return false;
					}
				}
				
				//2-7.Currency 必須是選單內的選項
				if (!"".equals(saveOrderEntryLineModels.get(i).getOrderLine().getCurrency())){
					if (this.chkSelectItemforCombobox(saveOrderEntryLineModels.get(i).getOrderLine().getCurrency(), currencyUiFieldParams)==false){
						this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",new java.lang.Object[] {Labels.getLabel("oe.edit.line.grdLine.colLineCurrency")}));
						return false;
					}
				}
				
				//2-8.CfaSite 必須是選單內的選項 2013.01.15
				if (!"".equals(saveOrderEntryLineModels.get(i).getOrderLine().getCfaSite())){
					if (this.chkSelectItemforCombobox(saveOrderEntryLineModels.get(i).getOrderLine().getCfaSite(), cfaSiteUiFieldParams)==false){
						this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",new java.lang.Object[] {Labels.getLabel("oe.edit.line.grdLine.colLineCfaSite")}));
						return false;
					}
				}
				
				sumWaferQty=sumWaferQty+saveOrderEntryLineModels.get(i).getOrderLine().getWaferQty();
			}
			
			//2-8.Line.WaferQty(sum) ?= Header Total Wafer Qty
			//OCF-PR-151002_不卡Line的總數量需跟OrderHeader一致，因OE已經by到CustomerLotNo，數量是到OrderLineLotNo
			//if (sumWaferQty!=Integer.valueOf(edtTotalWaferQty.getText().trim())){
			//	this.showmessage("Warning", Labels.getLabel("oe.edit.line.chkWaferQtySum.diffwithHeader",
			//			new Object[] {Labels.getLabel("oe.edit.line.grdLine.colLineWaferQty")}));
			//	return false;
			//}
			
		//3.Lotno
			//3-1.Lotno必須有資料
			if (saveOrderEntryLotnoModels.size()==0){
				this.showmessage("Warning", Labels.getLabel("oe.save.lotno.musthavedata",
						new Object[] {Labels.getLabel("oe.edit.line.grbLotnoData")}));
				return false;
			}
			
			//3-2.Lotno.POitme+CustomerLotno必須有值，且不可重複
			if (this.chkgrdLotnoCustomerLotnoDuplicate()==false){
				return false;
			}
	
		//4.BillTo,ShipTo與customer 是否有relationship 2014.09.01
			if ("Y".equals(iMaskOMUiFieldParams.get(0).getMeaning())){
				String ebsMsg="";
				
				//Customer是否有EBS_CUST_ACCOUNT_ID
				if ((customer.getEbsCustAccountId()==null) || ("".equals(customer.getEbsCustAccountId()))){
					ebsMsg+= Labels.getLabel("oe.edit.line.chkEbsCustomerId",
							new Object[] {Labels.getLabel("oe.edit.header.lblCustomer"),Labels.getLabel("modules.cus.ctrl.label.EbsCustAccountId")})+"\r\n";
				}
				
				//BillTo是否有EBS_CUST_ACCOUNT_ID
				if ((billto.getEbsCustAccountId()==null) || ("".equals(billto.getEbsCustAccountId()))){
					ebsMsg+= Labels.getLabel("oe.edit.line.chkEbsCustomerId",
							new Object[] {Labels.getLabel("oe.edit.header.lblBillTo"),Labels.getLabel("modules.cus.ctrl.label.EbsCustAccountId")})+"\r\n";
				}
				
				//ShipTo是否有EBS_CUST_ACCOUNT_ID
				if ((shipto.getEbsCustAccountId()==null) || ("".equals(shipto.getEbsCustAccountId()))){
					ebsMsg+= Labels.getLabel("oe.edit.line.chkEbsCustomerId",
							new Object[] {Labels.getLabel("oe.edit.header.lblShipTo"),Labels.getLabel("modules.cus.ctrl.label.EbsCustAccountId")})+"\r\n";
				}
				
				//BillTo
				if ((customer.getEbsCustAccountId()!=null) &&
					(!"".equals(customer.getEbsCustAccountId())) &&
					(billto.getEbsCustAccountId()!=null) &&
					(!"".equals(billto.getEbsCustAccountId())) ){
					//先判斷是否Customer與BillTo是同一家客戶
					boolean billToFlag=false;
					if (customer.getEbsCustAccountId().equals(billto.getEbsCustAccountId())){
						billToFlag=true;
					}
					else {
						billToFlag=commonService.checkEbsRelationship(Integer.valueOf(customer.getEbsCustomerId()), 
								                                      Integer.valueOf(billto.getEbsCustAccountId()));//2014.10.06
						if (billToFlag==false){
							ebsMsg+=Labels.getLabel("oe.edit.line.chkEbsRelatinship",
									new Object[] {Labels.getLabel("oe.edit.header.lblCustomer"),Labels.getLabel("oe.edit.header.lblBillTo")})+"\r\n";
						}
					}
				}
				
				//ShipTo
				if ((customer.getEbsCustAccountId()!=null) &&
					(!"".equals(customer.getEbsCustAccountId())) &&
					(shipto.getEbsCustAccountId()!=null) &&
					(!"".equals(shipto.getEbsCustAccountId()))){
					//先判斷是否Customer與ShipTo是同一家客戶
					boolean shipToFlag=false;
					if (customer.getEbsCustAccountId().equals(shipto.getEbsCustAccountId())){
						shipToFlag=true;
					}
					else{
						shipToFlag=commonService.checkEbsRelationship(Integer.valueOf(customer.getEbsCustomerId()),
								                                      Integer.valueOf(shipto.getEbsCustAccountId()));//2014.10.06
						if (shipToFlag==false){
							ebsMsg+=Labels.getLabel("oe.edit.line.chkEbsRelatinship",
									new Object[] {Labels.getLabel("oe.edit.header.lblCustomer"),Labels.getLabel("oe.edit.header.lblShipTo")})+"\r\n";
						}
					}
				}
				
				if (!"".equals(ebsMsg)){
					this.showmessage("Warning", ebsMsg);
					return false;
				}
			}
	
		return true;
	}
	
	/**
	 * 
	 * chkSelectItemforCombobox:確認Combobox的內容，User是否都有選到選單內的選項. <br/>
	 *
	 * @author 030260
	 * @param inSelectItemValue
	 * @param inUiFieldParams
	 * @return
	 * @since JDK 1.6
	 */
	public boolean chkSelectItemforCombobox(String inSelectItemValue,List<UiFieldParam> inUiFieldParams){
		if (inUiFieldParams.size()>0){
			boolean flag=false;
			for (int i=0;i<inUiFieldParams.size();i++){
				if (inSelectItemValue.equals(inUiFieldParams.get(i).getParaValue())){
					flag=true;
					break;
				}
			}
			
			if (flag==false){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Check 畫面上Combobox User是否有選擇選單裡的選項
	 */	
	public boolean chkComboboxSelectItemindex(){
		if ((!("".equals(cbxCustomer.getText()))) && (cbxCustomer.getSelectedIndex()<0)){
			this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",new java.lang.Object[] {Labels.getLabel("oe.edit.header.lblCustomer")}));
			return false;
		}
		
		if ((!("".equals(cbxOrderType.getText()))) && (cbxOrderType.getSelectedIndex()<0)){
			this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",new java.lang.Object[] {Labels.getLabel("oe.edit.header.lblOrderType")}));
			return false;
		}
		
		if ((!("".equals(cbxOrderFrom.getText()))) && (cbxOrderFrom.getSelectedIndex()<0)){
			this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",new java.lang.Object[] {Labels.getLabel("oe.edit.header.lblOrderFrom")}));
			return false;
		}
		
		if ((!("".equals(cbxBillTo.getText()))) && (cbxBillTo.getSelectedIndex()<0)){
			this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",new java.lang.Object[] {Labels.getLabel("oe.edit.header.lblBillTo")}));
			return false;
		}
		
		if ((!("".equals(cbxShipTo.getText()))) && (cbxShipTo.getSelectedIndex()<0)){
			this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",new java.lang.Object[] {Labels.getLabel("oe.edit.header.lblShipTo")}));
			return false;
		}
		
		if ((!("".equals(cbxWaferSize.getText()))) && (cbxWaferSize.getSelectedIndex()<0)){
			this.showmessage("Warning", Labels.getLabel("oe.save.combobox.selectitem",new java.lang.Object[] {Labels.getLabel("oe.edit.header.lblWaferSize")}));
			return false;
		}
			
		return true;
	}
	
	public void onSelect$cbxCustomer(){
		if (cbxCustomer.getSelectedIndex()<0){
			return;
		}
		
		if(oeReworkCountSetupList.size() > 0){
			oeReworkCountSetupList.clear();
		}
		
		customer=customers.get(cbxCustomer.getSelectedIndex());
		if (customer!=null){
			log.debug(customer.getAccountManager());
			if (customer.getAccountManager()!=null){//2014.09.01 bugfix
			edtAccountMgr.setText(customer.getAccountManager().trim());
			
			//OCF-PR-150202_選擇客戶後，搜尋該客戶是否有設定REWORK_COUNT
			oeReworkCountSetupList= orderEntryService.getOeReworkCountSetupByCustomerId(customer.getCustomerId());
		}
		else {
			edtAccountMgr.setText("");
		}
			
		}
		else {
			edtAccountMgr.setText("");
		}
		changeLineShipTo(); //2018.05.14
	}
	
	public void onChange$cbxCustomer(){
		this.onSelect$cbxCustomer();
	}
	
	public void onSelect$cbxOrderType(){
		if (cbxOrderType.getSelectedIndex()<0){
			return;
		}
		orderType=orderTypeUiFieldParams.get(cbxOrderType.getSelectedIndex());
	}
	
	public void onChange$cbxOrderType(){
		this.onSelect$cbxOrderType();
	}
	
	public void onSelect$cbxWaferSize(){
		if (cbxWaferSize.getSelectedIndex()<0){
			return;
		}
		waferSizeUiFieldParam=waferSizeUiFieldParams.get(cbxWaferSize.getSelectedIndex());
	}

	public void onChange$cbxWaferSize(){
		this.onSelect$cbxWaferSize();
	}
	
	public void onSelect$cbxBillTo(){
		if (cbxBillTo.getSelectedIndex()<0){
			return;
		}
		billto=billtos.get(cbxBillTo.getSelectedIndex());
	}
	
	public void onChange$cbxBillTo(){
		this.onSelect$cbxBillTo();
	}
	
	public void onSelect$cbxShipTo(){
		if (cbxShipTo.getSelectedIndex()<0){
			return;
		}
		shipto=shiptos.get(cbxShipTo.getSelectedIndex());
		this.changeLineShipTo();//2013.06.26
	}
	
	public void onChange$cbxShipTo(){
		this.onSelect$cbxShipTo();
	}
	
	//2013.06.26 如果非APTINA的SHIP_TO有改時，LINE要跟著一起修改
	public void changeLineShipTo(){
		if(!"".equals(customer) && customer != null){
			if (!"01".equals(customer.getCustomerId())){
				for (int i=0;i<saveOrderEntryLineModels.size();i++){
					saveOrderEntryLineModels.get(i).getOrderLine().setShipToVendorName(cbxShipTo.getValue().trim());
					Listcell cellLineShipToVendorName = (Listcell)component.getFellow("cellLineShipToVendorName"+i);
					cellLineShipToVendorName.setLabel(cbxShipTo.getValue().trim());
					List<CustomerTable> customerTableList=customerInformationService.getCustomerTableByCustomerShortNoBusPurpose(cbxShipTo.getValue().trim());
					Listcell cellLineShipToVendorCode = (Listcell)component.getFellow("cellLineShipToVendorCode"+i);
					cellLineShipToVendorCode.setLabel(customerTableList.get(0).getCustomerId());

				}//end for i
				//Listcell cellLineShipToVendorName = (Listcell) o.getFellow("cellLineShipToVendorName"+o.getIndex());

			}
		}
	}
	
	public void onSelect$cbxOrderFrom(){
		if (cbxOrderFrom.getSelectedIndex()<0){
			return;
		}
		orderfrom=orderfroms.get(cbxOrderFrom.getSelectedIndex());
	}
	
	public void onChange$cbxOrderFrom(){
		this.onSelect$cbxOrderFrom();
	}
	
	public void onChange$edtProduct(){
		//XQ20181007_will
		edtLineProduct.setText(edtProduct.getText().trim().toUpperCase());
	}
	
	public void onChange$edtPoNum(){
		edtLinePoNumber.setText(edtPoNum.getText().trim());
	}
	
	public void onChange$edtCustomerPo(){
		edtLineCustomerPo.setText(edtCustomerPo.getText().trim());
	}
	
	public void onChange$dtbGRequestDate(){
		if (saveOrderEntryLineModels.size()>0){
			for (int i=0;i<saveOrderEntryLineModels.size();i++){
				saveOrderEntryLineModels.get(i).getOrderLine().setRequestDate(dtbGRequestDate.getText().trim());
			}
			grdLine.setModel(new ListModelList(saveOrderEntryLineModels));
			grdLine.setItemRenderer(this);
		}
	}
	
	public void onChange$dtbGWaferSupplyDate(){//2013.02.08
		if (saveOrderEntryLineModels.size()>0){
			for (int i=0;i<saveOrderEntryLineModels.size();i++){
				saveOrderEntryLineModels.get(i).getOrderLine().setWaferSupplyDate(dtbGWaferSupplyDate.getValue());
			}
			grdLine.setModel(new ListModelList(saveOrderEntryLineModels));
			grdLine.setItemRenderer(this);
		}
	}
	
	public void onChange$cbxGCurrency(){
		if (saveOrderEntryLineModels.size()>0){
			for (int i=0;i<saveOrderEntryLineModels.size();i++){
				saveOrderEntryLineModels.get(i).getOrderLine().setCurrency(cbxGCurrency.getText().trim());
			}
			grdLine.setModel(new ListModelList(saveOrderEntryLineModels));
			grdLine.setItemRenderer(this);
		}
	}
	
	public void onChange$cbxGCfaSite(){
		if (saveOrderEntryLineModels.size()>0){
			for (int i=0;i<saveOrderEntryLineModels.size();i++){
				saveOrderEntryLineModels.get(i).getOrderLine().setCfaSite(cbxGCfaSite.getText().trim());
			}
			grdLine.setModel(new ListModelList(saveOrderEntryLineModels));
			grdLine.setItemRenderer(this);
		}
	}
	
	/**
	 * 
	 * getDeleteOrderLineLotnos:如果是modify狀態，找出原OrderLineLotno的資料，如果不是modify狀態，則回傳null. <br/>
	 *
	 * @author 030260
	 * @param inOrderNumber
	 * @return
	 * @since JDK 1.6
	 */
	public List<OrderLineLotno> getDeleteOrderLineLotnos(String inOrderNumber,Date inNowTime){
		if ("modify".equals(mode)){
			List<OrderLineLotno> delOrderLineLotnos=new ArrayList<OrderLineLotno>();
			delOrderLineLotnos=orderEntryService.getOrderLineLotnosByOrderNumber(edtOrderNumber.getText());
			if (delOrderLineLotnos.size()>0){
				for (int i=0;i<delOrderLineLotnos.size();i++){
					delOrderLineLotnos.get(i).setCancelFlag(true);
				}	
			}
			return delOrderLineLotnos;
		}
		else {
			return null;
		}
	}
	
	public List<OrderInternalCheckInfo> getDeleteOrderInternalCheckInfos(List<OrderLineLotno> inOrderLotnos,Date inNowTime){
		List<OrderInternalCheckInfo> delOrderInternalCheckInfos = new ArrayList<OrderInternalCheckInfo>();
		if ("modify".equals(mode)){
			if(inOrderLotnos.size() > 0){
				List<Integer> orderLineNoIdxs = new ArrayList<Integer>();
				for(int i=0; i<inOrderLotnos.size(); i++){
					orderLineNoIdxs.add(inOrderLotnos.get(i).getOrderLineLotnoIdx());
				}
				delOrderInternalCheckInfos = orderEntryService.getOrderInternalCheckInfoByOrderLineLotNos(orderLineNoIdxs);
				return delOrderInternalCheckInfos;
			}
		}
		return delOrderInternalCheckInfos;
	}
	
	/**
	 * 
	 * getDeleteOrderLines:如果是modify狀態，找出原OrderLine的資料，如果不是modify狀態，則回傳null. <br/>
	 *
	 * @author 030260
	 * @param inOrderNumber
	 * @param inNowTime
	 * @return
	 * @since JDK 1.6
	 */
	public List<OrderLine> getDeleteOrderLines(String inOrderNumber,Date inNowTime){
		if ("modify".equals(mode)){
			List<OrderLine> delOrderLines=new ArrayList<OrderLine>();
			delOrderLines=orderEntryService.getOrderLinesByOrderNumber(edtOrderNumber.getText());
			if (delOrderLines.size()>0){
				for (int i=0;i<delOrderLines.size();i++){
					delOrderLines.get(i).setCancelFlag(true);
				}
			}			
			log.debug(delOrderLines);
			return delOrderLines;
		}
		else{
			return null;
		}
	}
	
	/**
	 * 
	 * composeOrderLines:組合要儲存的orderLines. <br/>
	 *
	 * @author 030260
	 * @param inNowTime
	 * @return
	 * @since JDK 1.6
	 */
	public void composeCreateOrderLines(Date inNowTime){
		if(orderLines.size() > 0){
			orderLines.clear();
		}
		saveOrderEntryLineModels=(List<OrderEntryLineModel>) grdLine.getModel();
		if(saveOrderEntryLotnoModels.size() > 0){
			for(int j=0; j<saveOrderEntryLotnoModels.size(); j++){
				for (int i=0;i<saveOrderEntryLineModels.size();i++){
					if(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getPoItem().equals(saveOrderEntryLineModels.get(i).getOrderLine().getPoItem())){
						OrderLine saveOrderLine=new OrderLine();
						
						saveOrderLine.setOrderHeader(orderHeaders.get(j));
						saveOrderLine.setOrderNumber(orderHeaders.get(j).getOrderNumber().trim());
						saveOrderLine.setWaferQty(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getWaferQty());//OCF-PR-151002_新建OE改為ORDER_LINE_LOTNO的waferQty						
						saveOrderLine.setPoItem(saveOrderEntryLineModels.get(i).getOrderLine().getPoItem().toString().trim());
						saveOrderLine.setSourceMtrlNum(saveOrderEntryLineModels.get(i).getOrderLine().getSourceMtrlNum().trim());
						saveOrderLine.setMtrlNum(saveOrderEntryLineModels.get(i).getOrderLine().getMtrlNum().trim());
						saveOrderLine.setSourceMtrlNum(saveOrderEntryLineModels.get(i).getOrderLine().getSourceMtrlNum().trim());
						saveOrderLine.setMtrlDesc(saveOrderEntryLineModels.get(i).getOrderLine().getMtrlDesc().trim());
						saveOrderLine.setMtrlNumMtrlgrp(saveOrderEntryLineModels.get(i).getOrderLine().getMtrlNumMtrlgrp().trim());
						//saveOrderLine.setWaferQty(saveOrderEntryLineModels.get(i).getOrderLine().getWaferQty());
						
						saveOrderLine.setWaferSupplyDate(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSupplyDate());//2013.02.08
						saveOrderLine.setRequestDate(saveOrderEntryLineModels.get(i).getOrderLine().getRequestDate().trim());
						saveOrderLine.setDesignId(saveOrderEntryLineModels.get(i).getOrderLine().getDesignId().trim());
						saveOrderLine.setCfaPorId(saveOrderEntryLineModels.get(i).getOrderLine().getCfaPorId().trim());
						saveOrderLine.setCountryOfFab(saveOrderEntryLineModels.get(i).getOrderLine().getCountryOfFab().trim());
						saveOrderLine.setFab(saveOrderEntryLineModels.get(i).getOrderLine().getFab().trim());
						//saveOrderLine.setWaferSize(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize());
						saveOrderLine.setWaferSize(this.getMeaningByParaValue(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize().trim(), waferSizeUiFieldParams));
						saveOrderLine.setCfaSite(this.getMeaningByParaValue(saveOrderEntryLineModels.get(i).getOrderLine().getCfaSite().trim(), cfaSiteUiFieldParams));//2013.01.15
						saveOrderLine.setShipToVendorCode(saveOrderEntryLineModels.get(i).getOrderLine().getShipToVendorCode().trim());
						saveOrderLine.setShipToVendorName(saveOrderEntryLineModels.get(i).getOrderLine().getShipToVendorName().trim());
						if(!"".equals(saveOrderEntryLineModels.get(i).getOrderLine().getShippingDestination()) && saveOrderEntryLineModels.get(i).getOrderLine().getShippingDestination() != null){
						saveOrderLine.setShippingDestination(saveOrderEntryLineModels.get(i).getOrderLine().getShippingDestination().trim());//OCF-PR-160602 add
						}else{
							saveOrderLine.setShippingDestination("");
						}
						//if ("newadd".equals(mode)){ //修改原本當modify時因CreateDate不允許null，故不分mode都儲存NowTime_2013.12.04_Allison
						saveOrderLine.setCreatedDate(inNowTime);
						saveOrderLine.setCreatedUser(userId);
						//}
						//else {
						//saveOrderLine.setCreatedDate(saveOrderEntryLineModels.get(i).getOrderLine().getCreatedDate());
						//saveOrderLine.setCreatedUser(saveOrderEntryLineModels.get(i).getOrderLine().getCreatedUser());
						//}
						//saveOrderLine.setCurrency(saveOrderEntryLineModels.get(i).getOrderLine().getCurrency());
						saveOrderLine.setCurrency(this.getMeaningByParaValue(saveOrderEntryLineModels.get(i).getOrderLine().getCurrency(), currencyUiFieldParams));
						saveOrderLine.setUnitPrice(saveOrderEntryLineModels.get(i).getOrderLine().getUnitPrice());
						saveOrderLine.setSellingPrice(saveOrderEntryLineModels.get(i).getOrderLine().getSellingPrice());
						saveOrderLine.setCompanyCode(saveOrderEntryLineModels.get(i).getOrderLine().getCompanyCode());
						saveOrderLine.setUpdateUser(userId);
						saveOrderLine.setUpdateDate(inNowTime);
						saveOrderLine.setCancelFlag(false);
						if(!"".equals(saveOrderEntryLineModels.get(i).getOrderLine().getShipComment()) && saveOrderEntryLineModels.get(i).getOrderLine().getShipComment() != null){
							saveOrderLine.setShipComment(saveOrderEntryLineModels.get(i).getOrderLine().getShipComment().trim());
						}else{
							saveOrderLine.setShipComment("");
						}
						saveOrderLine.setSubName(saveOrderEntryLineModels.get(i).getOrderLine().getSubName());
						saveOrderLine.setStage(saveOrderEntryLineModels.get(i).getOrderLine().getStage());
						saveOrderLine.setOperationDescription(saveOrderEntryLineModels.get(i).getOrderLine().getOperationDescription());
						if(BeanUtil.isNotEmpty(saveOrderEntryLineModels.get(i).getOrderLine().getSalesPartId())) //2018.05.14
						saveOrderLine.setSalesPartId(saveOrderEntryLineModels.get(i).getOrderLine().getSalesPartId().trim()); //2018.05.14
						List<OrderLineLotno> orderLineLotnos = new ArrayList<OrderLineLotno>();
						orderLineLotnos.add(saveOrderEntryLotnoModels.get(j).getOrderLineLotno());
						saveOrderLine.setOrderLineLotnos(orderLineLotnos);
						//version:XQ181004 如果用户选择了RMA或其他，那么ship comment这栏强制变成该文本
						//该代码是作用于update OE创建时
						//add by will 2018/11/12
						if(cbxGReworkFlag.getValue().trim().length()>0){
							saveOrderLine.setShipComment(cbxGReworkFlag.getValue().trim());
						}
						orderLines.add(saveOrderLine);
					}
					//orderEntryService.createOrderLines(orderLines);
				}
			}
		}
	}
	
	public void composeUpdateOrderLines(Date inNowTime){
		saveOrderEntryLineModels=(List<OrderEntryLineModel>) grdLine.getModel();
		for (int i=0;i<saveOrderEntryLineModels.size();i++){
			OrderLine saveOrderLine=new OrderLine();
			saveOrderLine.setOrderHeader(orderHeader);
			saveOrderLine.setOrderNumber(orderHeader.getOrderNumber().trim());
			saveOrderLine.setPoItem(saveOrderEntryLineModels.get(i).getOrderLine().getPoItem().toString().trim());
			saveOrderLine.setSourceMtrlNum(saveOrderEntryLineModels.get(i).getOrderLine().getSourceMtrlNum().trim());
			saveOrderLine.setMtrlNum(saveOrderEntryLineModels.get(i).getOrderLine().getMtrlNum().trim());
			saveOrderLine.setSourceMtrlNum(saveOrderEntryLineModels.get(i).getOrderLine().getSourceMtrlNum().trim());
			saveOrderLine.setMtrlDesc(saveOrderEntryLineModels.get(i).getOrderLine().getMtrlDesc().trim());
			saveOrderLine.setMtrlNumMtrlgrp(saveOrderEntryLineModels.get(i).getOrderLine().getMtrlNumMtrlgrp().trim());
			saveOrderLine.setWaferQty(saveOrderEntryLineModels.get(i).getOrderLine().getWaferQty());
			saveOrderLine.setWaferSupplyDate(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSupplyDate());//2013.02.08
			saveOrderLine.setRequestDate(saveOrderEntryLineModels.get(i).getOrderLine().getRequestDate().trim());
			saveOrderLine.setDesignId(saveOrderEntryLineModels.get(i).getOrderLine().getDesignId().trim());
			saveOrderLine.setCfaPorId(saveOrderEntryLineModels.get(i).getOrderLine().getCfaPorId().trim());
			saveOrderLine.setCountryOfFab(saveOrderEntryLineModels.get(i).getOrderLine().getCountryOfFab().trim());
			saveOrderLine.setFab(saveOrderEntryLineModels.get(i).getOrderLine().getFab().trim());
			//saveOrderLine.setWaferSize(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize());
			saveOrderLine.setWaferSize(this.getMeaningByParaValue(saveOrderEntryLineModels.get(i).getOrderLine().getWaferSize().trim(), waferSizeUiFieldParams));
			saveOrderLine.setCfaSite(this.getMeaningByParaValue(saveOrderEntryLineModels.get(i).getOrderLine().getCfaSite().trim(), cfaSiteUiFieldParams));//2013.01.15
			saveOrderLine.setShipToVendorCode(saveOrderEntryLineModels.get(i).getOrderLine().getShipToVendorCode().trim());
			if(!"".equals(saveOrderEntryLineModels.get(i).getOrderLine().getShippingDestination()) && saveOrderEntryLineModels.get(i).getOrderLine().getShippingDestination() != null){
			saveOrderLine.setShippingDestination(saveOrderEntryLineModels.get(i).getOrderLine().getShippingDestination().trim());//OCF-PR-160602 add
			}else{
				saveOrderLine.setShippingDestination("");
			}
			saveOrderLine.setShipToVendorName(saveOrderEntryLineModels.get(i).getOrderLine().getShipToVendorName().trim());
			//if ("newadd".equals(mode)){ //修改原本當modify時因CreateDate不允許null，故不分mode都儲存NowTime_2013.12.04_Allison
				saveOrderLine.setCreatedDate(inNowTime);
				saveOrderLine.setCreatedUser(userId);
			//}
			//else {
				//saveOrderLine.setCreatedDate(saveOrderEntryLineModels.get(i).getOrderLine().getCreatedDate());
				//saveOrderLine.setCreatedUser(saveOrderEntryLineModels.get(i).getOrderLine().getCreatedUser());
			//}
			//saveOrderLine.setCurrency(saveOrderEntryLineModels.get(i).getOrderLine().getCurrency());
			saveOrderLine.setCurrency(this.getMeaningByParaValue(saveOrderEntryLineModels.get(i).getOrderLine().getCurrency(), currencyUiFieldParams));
			saveOrderLine.setUnitPrice(saveOrderEntryLineModels.get(i).getOrderLine().getUnitPrice());
			saveOrderLine.setSellingPrice(saveOrderEntryLineModels.get(i).getOrderLine().getSellingPrice());
			saveOrderLine.setCompanyCode(saveOrderEntryLineModels.get(i).getOrderLine().getCompanyCode());
			saveOrderLine.setUpdateUser(userId);
			saveOrderLine.setUpdateDate(inNowTime);
			saveOrderLine.setCancelFlag(false);
			if(!"".equals(saveOrderEntryLineModels.get(i).getOrderLine().getShipComment()) && saveOrderEntryLineModels.get(i).getOrderLine().getShipComment() != null){
				saveOrderLine.setShipComment(saveOrderEntryLineModels.get(i).getOrderLine().getShipComment().trim());
			}else{
				saveOrderLine.setShipComment("");
			}
			saveOrderLine.setSubName(saveOrderEntryLineModels.get(i).getOrderLine().getSubName());
			saveOrderLine.setStage(saveOrderEntryLineModels.get(i).getOrderLine().getStage());
			saveOrderLine.setOperationDescription(saveOrderEntryLineModels.get(i).getOrderLine().getOperationDescription());
			if(BeanUtil.isNotEmpty(saveOrderEntryLineModels.get(i).getOrderLine().getSalesPartId())) //2018.05.14
			saveOrderLine.setSalesPartId(saveOrderEntryLineModels.get(i).getOrderLine().getSalesPartId().trim()); //2018.05.14

			if(cbxCustomer.getText().equals("OMNI")){ //2018.05.14
				
				if(!compareWithProductForOVT(saveOrderEntryLineModels.get(i).getOrderLine().getMtrlNum(), saveOrderEntryLineModels.get(i).getOrderLine().getSalesPartId())){
					Messagebox.show(Labels.getLabel("oe.save.message.salesPartId.inconsistent")+"\r\n"+"[Mtrl Num]："+saveOrderEntryLineModels.get(i).getOrderLine().getMtrlNum().trim()+"\r\n"+"[Sales Part ID]："+saveOrderEntryLineModels.get(i).getOrderLine().getSalesPartId().trim(), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				}				
				if(!checkSalesPartIdForOVT(cbxShipTo.getText(), saveOrderEntryLineModels.get(i).getOrderLine().getSalesPartId())){
					Messagebox.show(Labels.getLabel("oe.save.message.salesPartId.incorrect"), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
				}

			}
			//version:XQ181004 如果用户选择了RMA或其他，那么ship comment这栏强制变成该文本
			//该代码是作用于update OE查询时
			//add by will 2018/11/12
			if(cbxGReworkFlag.getValue().trim().length()>0){
				saveOrderLine.setShipComment(cbxGReworkFlag.getValue().trim());
			}
			orderLines.add(saveOrderLine);
		}
		//orderEntryService.createOrderLines(orderLines);
	}
	
	public boolean updateWaferBankinWafer(){
		//先用ORDER_NUMBER去[WAFER_BANKIN_WAFER]找出是否有資料，若有修改畫面上的WAFER_DATA，則必須要先將先前已經UPDATE的ORDER_NUMBER清空，才不會修改了WAFER_DATA，但資料庫舊有ORDER_NUMBER的對應仍在的錯誤
		//OCF-PR-160202_修改如下：
		//檢查是否有修改WaferData，若有修改WaferData的話，則比對[WAFER_BANKIN_WAFER]找出符合WaferData的，若有[WAFER_BANKIN_WAFER].WAFER_OUT_FLAG=1的，則跳出Alarm不允許儲存；若無[WAFER_BANKIN_WAFER].WAFER_OUT_FLAG=1的，則該原OrderNumber對應到的[WAFER_BANKIN_WAFER].ORDER_NUMBER先清空，底下的saveoe會再比對一次塞入OrderNumber
		//OCF-PR-160303_[WAFER_BANKIN_WAFER]新增INAVI_LOTNO欄位，故FOLLOW原ORDER_NUMBER規則，若符合上述條件，則INAVI_LOTNO也需清空
		boolean flag = true;
		if("modify".equals(mode)){
			for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
				String[] oeWaferData = saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData().split(";");
				
				List<OrderLineLotno> orderLineLotNos = orderEntryService.getOrderLineLotnosByOrderNumberAndCustomerLotNo(orderHeader.getOrderNumber(), saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno());
				for(int j=0; j<orderLineLotNos.size(); j++){
					String[] dbWaferData = orderLineLotNos.get(j).getWaferData().split(";");
					
					boolean checkflag = false;
					for(int y=0; y<oeWaferData.length; y++){
						for(int l=0; l<dbWaferData.length; l++){
							if(oeWaferData[y].equals(dbWaferData[l])){
								checkflag = true;
								break;
							}
						}
						
						if(!checkflag || oeWaferData.length != dbWaferData.length){
							List<WaferBankin> updateWaferBankinWaferList = orderEntryService.getWaferBankinByCustomerLotNoAndCustomerId(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno(), orderHeader.getCustomerId());
							if(updateWaferBankinWaferList.size() > 0){
								for(int k=0; k<updateWaferBankinWaferList.size(); k++){
									if(updateWaferBankinWaferList.get(k).getWaferBankinWafers().size() > 0){
										for(int l=updateWaferBankinWaferList.get(k).getWaferBankinWafers().size()-1; l>=0; l--){
											boolean checkWaferNoFlag=false;
											String[] splitWaferData = saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData().split(";");
											for(int t=0; t<splitWaferData.length; t++){
												String tmpWaferNo="";
												if(Integer.valueOf(splitWaferData[t]) < 10){
													tmpWaferNo = saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno() + "-0" + String.valueOf(Integer.valueOf(splitWaferData[t]));
												}else{
													tmpWaferNo = saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno() + "-" + String.valueOf(Integer.valueOf(splitWaferData[t]));
												}

												if(updateWaferBankinWaferList.get(k).getWaferBankinWafers().get(l).getReceiveWaferNo().equals(tmpWaferNo)){
													checkWaferNoFlag = true;
													break;
												}
											}
											if(!checkWaferNoFlag){
												updateWaferBankinWaferList.get(k).getWaferBankinWafers().remove(l);
											}
										}
									}
								}
							}
							
							String waferOutWaferData = "";
							if(updateWaferBankinWaferList.size() > 0){
								for(int t=0; t<updateWaferBankinWaferList.size(); t++){
									for(int l=0; l<updateWaferBankinWaferList.get(t).getWaferBankinWafers().size(); l++){
										if(updateWaferBankinWaferList.get(t).getWaferBankinWafers().get(l).getWaferOutFlag()){
											waferOutWaferData = waferOutWaferData + updateWaferBankinWaferList.get(t).getWaferBankinWafers().get(l).getReceiveWaferNo().substring(updateWaferBankinWaferList.get(t).getWaferBankinWafers().get(l).getReceiveWaferNo().indexOf("-")+1, updateWaferBankinWaferList.get(t).getWaferBankinWafers().get(l).getReceiveWaferNo().length()) + ";";
										}
									}

									if(!"".equals(waferOutWaferData)){
										Messagebox.show(Labels.getLabel("oe.save.message.waferOutExists")+"\r\n"+waferOutWaferData, "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
										flag = false;
									}else{
										List<WaferBankinWafer> updateWaferBankinWaferLists = orderEntryService.getWaferBankinWaferByOrderNumber(orderHeader.getOrderNumber());
										for(int w=0; w<updateWaferBankinWaferLists.size(); w++){
											updateWaferBankinWaferLists.get(w).setOrderNumber("");
											updateWaferBankinWaferLists.get(w).setiNaviLotNo("");//OCF-PR-160303 add
											updateWaferBankinWaferLists.get(w).setUpdateDate(new Date());
											updateWaferBankinWaferLists.get(w).setUpdateUser(loginId);
										}
										
										orderEntryService.updateWaferBankinWafers(updateWaferBankinWaferLists);
									}
								}
							}
		
							break;
						}
					}
				}
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * saveOE:儲存ORDER_HEADER,ORDER_LINE,ORDER_LINE_LOTNO. <br/>
	 *        包含modify狀態時將ORDER_LINE,ORDER_LINE_LOTNO的CANCEL_FLAG設為TRUE
	 *
	 * @author 030260
	 * @throws Exception 
	 * @since JDK 1.6
	 */
	public void saveOE() throws Exception{
	    this.shipConfirmChk();
		Date nowtime=new Date();
		log.debug(DateFormatUtil.getDateTimeFormat().format(nowtime));
		
		//將要處理的table放在Object[]內
		Object[] objs=  new Object[17];//2013.02.18
			//1.mode
		    objs[0]=mode;
		    
			//2.要刪除的OrderLineLotno
		    List<OrderLineLotno> delLineLotnos=this.getDeleteOrderLineLotnos(edtOrderNumber.getText(), nowtime);
			objs[1]=delLineLotnos;
			
			//2.1要刪除的OrderInternalCheckInfo
			List<OrderInternalCheckInfo> delOrderInternalCheckInfo = getDeleteOrderInternalCheckInfos(delLineLotnos, nowtime);
			objs[15]=delOrderInternalCheckInfo;
			
			//3.要刪除的OrderLine
			List<OrderLine> delOrderLines = this.getDeleteOrderLines(edtOrderNumber.getText(), nowtime);
			objs[2]=delOrderLines;
			
			//4.要更新或者新增的OrderHeader
			//OCF-PR-151002_建立OE by到CustomerLotNo，有幾筆OrderLineLotNo就有幾筆OE
			if ("newadd".equals(mode)){
				this.createHeader(nowtime);
				objs[3]=new ArrayList<HashMap>();
			}
			else{
				this.updateHeaer(nowtime);
			objs[3]=orderHeader;
			}
			//objs[3]=orderHeader;
			
			//5.要新增的OrderLine
			//OCF-PR-151002_建立OE by到CustomerLotNo，有幾筆OrderLineLotNo就有幾筆OE
			if ("newadd".equals(mode)){
				this.composeCreateOrderLines(nowtime);
			}else{
				this.composeUpdateOrderLines(nowtime);
			}
			objs[4]=orderLines;
			
			//6.要儲存的saveOrderEntryLotnoModels
			this.composeOrderEntryLotnoModels(nowtime);//如果有Hold時，重新讀取PoItem及CustomerLotno
			objs[5]=saveOrderEntryLotnoModels;
			
			//7.UserID
			objs[6]=userId;
			
			//8.nowtime
			objs[7]=nowtime;
			
			//9.ClassName
			objs[8]=this.getClass().getName();
			
			//10.ActionName
			objs[9]="Save-"+mode;
			
			//11.waferStatus(Insert)
			if ("newadd".equals(mode)){
				this.composeInsertWaferStatus(nowtime);
				objs[10]=insertWaferStatus;
			}
			else{
				objs[10]=null;
			}
			
			//12.waferStatus(update)
			if ("modify".equals(mode)){
				this.composeUpdateWaferStatus(nowtime);
				objs[11]=updateWaferStatus;
				if ((insertWaferStatus!=null)&&(insertWaferStatus.size()>0)){//2013.02.18
					objs[10]=insertWaferStatus;
				}
			}
			else{
				objs[11]=null;
			}
			
			//13.2013.02.18 依據delLineLotnos的EntityId找出修改前的WaferStatus
			if ("modify".equals(mode)){
				List<WaferStatus> beforeWaferStatus=new ArrayList<WaferStatus>();
				for (int i=0;i<delLineLotnos.size();i++){
					WaferStatus tmpWaferStatus=orderEntryService.getWaferStatusByEntityId(delLineLotnos.get(i).getEntityId());
					if (tmpWaferStatus!=null){
						beforeWaferStatus.add(tmpWaferStatus);
					}
				}
				objs[12]=beforeWaferStatus;
			}
			else{
				objs[12]=null;
			}
			
			//14. OCF-PR-150202_Order Internal Check(目前有三個Check Rule)，若有檢查到的，則要存入[ORDER_INTERNAL_CHECK_INFO]裡_Allison add
			objs[13] = saveOrderInternalCheckInfoList;
			
			//15. 若QA有先做了Wafer Receive Operation，則update 依CUSTOMER_LOTNO + WAFER_DATA來[WAFER_BANKIN_WAFER].ORDER_NUMBER作連結_Allison add
			List<String> customerLotNoList = new ArrayList<String>();
			if(saveOrderEntryLotnoModels.size() > 0){
				for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
					customerLotNoList.add(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno());
				}
			}
		
			List<WaferBankin> updateWaferBankinWaferList = orderEntryService.getWaferBankinAndWaferBankinWaferByCustomerLotNoAndCustomerId(customerLotNoList, orderHeader.getCustomerId());
			//搜尋出來需要再對[WAFER_BANKIN_WAFER]做條件過濾：1. WAFER_RECEIVE_FLAG=1, WAFER_OUT_FLAG=0, CLOSE_FLAG=0，且RECEIVE_WAFER_NO與跟ORDER_LINE_LONOT的WAFER_DATA一樣
			if(updateWaferBankinWaferList.size() > 0){
				for(int i=0; i<updateWaferBankinWaferList.size(); i++){
					if(updateWaferBankinWaferList.get(i).getWaferBankinWafers().size() > 0){
						for(int j=updateWaferBankinWaferList.get(i).getWaferBankinWafers().size()-1; j>=0; j--){
							if(updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).getCloseFlag() || updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).getWaferOutFlag() || !updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).getWaferReceiveFlag()){								
								updateWaferBankinWaferList.get(i).getWaferBankinWafers().remove(j);
							}else{
								boolean checkWaferNoFlag=false;
								if(saveOrderEntryLotnoModels.size() > 0){
									for(int k=0; k<saveOrderEntryLotnoModels.size(); k++){
										String[] splitWaferData = saveOrderEntryLotnoModels.get(k).getOrderLineLotno().getWaferData().split(";");
										for(int l=0; l<splitWaferData.length; l++){
											String tmpWaferNo="";
											if(Integer.valueOf(splitWaferData[l]) < 10){
												tmpWaferNo = saveOrderEntryLotnoModels.get(k).getOrderLineLotno().getCustomerLotno() + "-0" + String.valueOf(Integer.valueOf(splitWaferData[l]));
											}else{
												tmpWaferNo = saveOrderEntryLotnoModels.get(k).getOrderLineLotno().getCustomerLotno() + "-" + String.valueOf(Integer.valueOf(splitWaferData[l]));
											}

											if(updateWaferBankinWaferList.get(i).getWaferBankinWafers().get(j).getReceiveWaferNo().equals(tmpWaferNo)){
												checkWaferNoFlag = true;
												break;
											}
										}
										if(checkWaferNoFlag){
											break;
										}
									}
									if(!checkWaferNoFlag){
										updateWaferBankinWaferList.get(i).getWaferBankinWafers().remove(j);
									}
								}
							}
						}
					}
				}
			}
			objs[14] = updateWaferBankinWaferList;
			
		//OCF-PR-151002_因OE需BY到CUSTOMER_LOTNO來建立，因此[ORDER_HEADER].ORDER_NUMBER & [ORDER_LINE_LOTNO].ENTITY_ID需要先編出來，因此用try catch 在Save的中間的error
		try{
			if ("newadd".equals(mode)){
				List<HashMap> saveOrderNumbers = new ArrayList<HashMap>();
				if(orderHeaders.size() > 0){
					for(int i=0; i<orderHeaders.size(); i++){
						OrderHeader inOrderHeader = orderHeaders.get(i);
						inOrderHeader.setTotalWaferQty(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty());
						inOrderHeader = orderEntryService.saveOrderHeaders(inOrderHeader);
						HashMap tmpOrderNumber = new HashMap();
						tmpOrderNumber.put(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno()+"_"+saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerJob(), inOrderHeader.getOrderNumber());
						saveOrderNumbers.add(tmpOrderNumber);
						
						orderLines.get(i).setOrderHeader(inOrderHeader);
						orderLines.get(i).setOrderNumber(inOrderHeader.getOrderNumber());
						
						orderEntryService.saveOrderLine(orderLines.get(i), loginId, nowtime, this.getClass().getName(), "Save-"+mode);
					}
				}
				
				if(saveOrderEntryLotnoModels.size() > 0){
					for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
						String entityId = orderEntryService.saveEntityIds(saveOrderEntryLotnoModels.get(i).getOrderLineLotno(), nowtime);
						saveOrderEntryLotnoModels.get(i).getOrderLineLotno().setEntityId(entityId);
						insertWaferStatus.get(i).setEntityId(entityId);
					}
				}
				objs[3]=saveOrderNumbers;
			}
		
		objs[16]=oeOrderNoConfirmModel;//OCF-PR-160307 add
		
		orderEntryService.saveOETransactionItems(objs);
		}catch(Exception e){
			if(orderHeaders.size() > 0){
				for(int i=0; i<orderHeaders.size(); i++){
					orderEntryService.deleteOrderHeaders(orderHeaders.get(i));
				}
			}
			
			this.showmessage("Error",Labels.getLabel("common.message.saveng", new java.lang.Object[] {}) + "\r\n" + e.toString());
			log.error(e);
		}
		//添加RMA的功能
	 	//version:XQ181004 RMA add by will 20181109
		//当reworkFlag不为空时才进行存储
	 	saveReWork();
		this.showmessage("Information",Labels.getLabel("oe.save.success",
				new java.lang.Object[] {}));
		
		//self.detach();//2013.01.15 Johnson要求不要關掉form
		//OCF-PR-151002_因新建OE by到OrderLineLotNo，Save成功後要跳出這次Save的所有Order讓User可點選一個個確認，但Modify的時候已經是單一筆Order的狀態，所以無需再跳出另外的視窗
		if ("newadd".equals(mode)){
			mode="modify";
			winOrderEntry.getParent().setAttribute("orderMode", "newadd");//回傳attribute到OrderQueryViewCtrl，若是直接按Create New Order，則回到OrderQueryViewCtrl時則不必觸發onClick$btnSearch
			self.detach();
			
			List<CreateOeList> createOeList = new ArrayList<CreateOeList>();
			if(saveOrderEntryLotnoModels.size() > 0){
				for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
					CreateOeList data = new CreateOeList();
					data.setOrderNumber(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getOrderNumber());
					data.setCustomerLotNo(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno());
					data.setPoItem(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem());
					data.setWaferQty(String.valueOf(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty()));
					
					createOeList.add(data);
		}
			}
			Map args = new HashMap();	
			args.put("createOeList", createOeList);
			Window winimport = (Window)Executions.createComponents("/WEB-INF/modules/oe/CreateOeList.zul", null, args);
			winimport.setParent(winOrderQuery);
			winimport.doModal();
			
		}else{
			orderQueryModel=new OrderQueryModel();
			orderQueryModel.setOrderHeader(orderHeader);
			if (customer!=null){
				orderQueryModel.setCustomerName(customer.getCustomerShortName());
			}
			else{
				orderQueryModel.setCustomerName("");
			}
				
			if (billto!=null){
				orderQueryModel.setBillToName(billto.getCustomerShortName());
			}
			else{
				orderQueryModel.setBillToName("");
			}
			
			if (shipto!=null){
				orderQueryModel.setShipToName(shipto.getCustomerShortName());
			}
			else{
				orderQueryModel.setShipToName("");
			}
			
			orderQueryModel.setOrderStatusName(this.getParaValueByMeaning(orderHeader.getOrderStatus(), orderStatus.get(0).getUiFieldParams()));
			orderQueryModel.setOrderTypeName(this.getParaValueByMeaning(orderHeader.getOrderType(), orderTypeUiFieldParams));
			
		//}
		this.formShow();
	}
	}
	
	/**
	 * 
	 * composeInsertWaferStatusModels:"newadd"狀態時，要同時insert to WAFER_STATUS. <br/>
	 * 組合insertWaferStatus有哪些資料
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void composeInsertWaferStatus(Date inDate){//2013.02.07
		insertWaferStatus=new ArrayList<WaferStatus>();
		
		if (saveOrderEntryLotnoModels.size()>0){
			for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
				//Search WaferBankin filer
				WaferFilter waferFilter=new WaferFilter();
				waferFilter.setCustomerfilter(customer.getCustomerId());
				waferFilter.setCustomerlotnofilter(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim());
				waferFilter.setEnddatefilter("-");
				waferFilter.setStartdatefilter("-");
				waferFilter.setWaferDataFilter(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData());
				List<WaferBankin> waferBankins=waferBankinService.listBySearchByOe(waferFilter);
				
				WaferStatus tmpWaferStatus=new WaferStatus();
				tmpWaferStatus.setOperationUnit(operationUnit);
				tmpWaferStatus.setPoNumber(orderHeaders.get(i).getPoNumber());
				tmpWaferStatus.setCustomerId(orderHeaders.get(i).getCustomerId());
				tmpWaferStatus.setOrderNumber(orderHeaders.get(i).getOrderNumber());//2013.03.20
				tmpWaferStatus.setCustomerLotno(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim());
				tmpWaferStatus.setWaferQty(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty());
				if ((waferBankins!=null) && (waferBankins.size()>0)){
					tmpWaferStatus.setWaferRecieveQty(waferBankins.get(0).getWaferQty());
					tmpWaferStatus.setWaferFlag(true);
				}
				else{
					tmpWaferStatus.setWaferRecieveQty(0);
					tmpWaferStatus.setWaferFlag(false);
				}
				tmpWaferStatus.setInfoFlag(true);
				tmpWaferStatus.setPoFlag(true);
				tmpWaferStatus.setLotissueFlag(false);
				tmpWaferStatus.setInaviFlag(false);
				tmpWaferStatus.setStateFlag("0");
				tmpWaferStatus.setCreateDate(inDate);
				tmpWaferStatus.setUpdateDate(inDate);
				tmpWaferStatus.setUpdateUser(userId);
				tmpWaferStatus.setEntityId("");
				tmpWaferStatus.setLotNo("");//2013.03.07
				tmpWaferStatus.setCustomerJob(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerJob());//IT-PR-141008_Allison add
				insertWaferStatus.add(tmpWaferStatus);
			}
		}
	}
	
	/**
	 * 
	 * composeUpdateWaferStatus:"modify"狀態時，要同時update WAFER_STATUS. <br/>
	 *
	 * @author 030260
	 * @param inDate
	 * @since JDK 1.6
	 */
	public void composeUpdateWaferStatus(Date inDate){
		updateWaferStatus=new ArrayList<WaferStatus>();
		insertWaferStatus=new ArrayList<WaferStatus>();//2013.02.18
		if (saveOrderEntryLotnoModels.size()>0){
			for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
				if ("".equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getEntityId())){//2013.02.18
					//如果entityId是空白，代表是以modify狀態新增一筆ORDER_LINE_LOTNO
					int tmpWaferStatus=insertWaferStatus.size();
					saveOrderEntryLotnoModels.get(i).setTmpWaferStatus(tmpWaferStatus);//記錄的是insertWaferStatus的第幾筆資料
					
					//Search WaferBankin filer
					WaferFilter waferFilter=new WaferFilter();
					waferFilter.setCustomerfilter(customer.getCustomerId());
					waferFilter.setCustomerlotnofilter(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim());
					waferFilter.setEnddatefilter("-");
					waferFilter.setStartdatefilter("-");
					waferFilter.setWaferDataFilter(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData());//IT-PR-141201
					List<WaferBankin> waferBankins=waferBankinService.listBySearchByOe(waferFilter);
					
					//並新增一筆WaferStatus
					WaferStatus newWaferStatus=new WaferStatus();
					newWaferStatus.setOperationUnit(operationUnit);
					newWaferStatus.setPoNumber(orderHeader.getPoNumber());
					newWaferStatus.setCustomerId(orderHeader.getCustomerId());
					newWaferStatus.setOrderNumber(orderHeader.getOrderNumber());//2013.03.20
					newWaferStatus.setCustomerLotno(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim());
					newWaferStatus.setWaferQty(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty());
					if ((waferBankins!=null) && (waferBankins.size()>0)){
						newWaferStatus.setWaferRecieveQty(waferBankins.get(0).getWaferQty());
						newWaferStatus.setWaferFlag(true);
					}
					else{
						newWaferStatus.setWaferRecieveQty(0);
						newWaferStatus.setWaferFlag(false);
					}
					newWaferStatus.setInfoFlag(true);
					newWaferStatus.setPoFlag(true);
					newWaferStatus.setLotissueFlag(false);
					newWaferStatus.setInaviFlag(false);
					newWaferStatus.setStateFlag("0");
					newWaferStatus.setCreateDate(inDate);
					newWaferStatus.setUpdateDate(inDate);
					newWaferStatus.setUpdateUser(userId);
					newWaferStatus.setEntityId("");
					newWaferStatus.setLotNo("");//2013.03.07
					newWaferStatus.setCustomerJob(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerJob());//IT-PR-141008_Allison add
					
					insertWaferStatus.add(newWaferStatus);
				}
				else {
					WaferStatus tmpWaferStatus=waferStatusService.getWaferStatusByEntityId(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getEntityId());
					if (tmpWaferStatus!=null){
						//2013.02.18如果WaferQty有異動WaferStatus用Update
						//          如果PoNumber,Customer_lono有異動，原WaferStatus.state_flag=2，並新增一筆WaferStatus
						
						//先判斷WaferQty是否有異動
						String oStr=String.valueOf(tmpWaferStatus.getWaferQty());
						String mStr=String.valueOf(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty());						
						String oCustomerJob = tmpWaferStatus.getCustomerJob();//判斷CustomerJob是否有異動OCF-PR-150505
						if(oCustomerJob == null){//OCF-PR-150505
							oCustomerJob = "";
						}
						String mCustomerJob = saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerJob();//OCF-PR-150505
						if(mCustomerJob == null){//OCF-PR-150505
							mCustomerJob = "";
						}
						if (!oStr.equals(mStr) || !oCustomerJob.equals(mCustomerJob)){//OCF-PR-150505
							if(!oStr.equals(mStr)){//OCF-PR-150505
								tmpWaferStatus.setWaferQty(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty());
								tmpWaferStatus.setStateFlag("1");
							}
							if(!oCustomerJob.equals(mCustomerJob)){//OCF-PR-150505
								tmpWaferStatus.setCustomerJob(mCustomerJob);
							}
							tmpWaferStatus.setUpdateDate(inDate);
							tmpWaferStatus.setUpdateUser(userId);
							
							updateWaferStatus.add(tmpWaferStatus);
						}
						
						//再判斷PoNumber,Customer_lono
						oStr=tmpWaferStatus.getPoNumber()+","+
								    tmpWaferStatus.getCustomerLotno().trim();
						mStr=orderHeader.getPoNumber()+","+
								    saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim();
						if (!oStr.equals(mStr)){
							//原WaferStatus.state_flag=2
							tmpWaferStatus.setStateFlag("2");
							tmpWaferStatus.setUpdateDate(inDate);
							tmpWaferStatus.setUpdateUser(userId);
							tmpWaferStatus.setEntityId("");//2013.02.18 將EntityID清空，醬子下次用EntityID找Wafer_status時，才不會找到兩筆
							
							updateWaferStatus.add(tmpWaferStatus);
							
							//Search WaferBankin filer
							WaferFilter waferFilter=new WaferFilter();
							waferFilter.setCustomerfilter(customer.getCustomerId());
							waferFilter.setCustomerlotnofilter(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim());
							waferFilter.setEnddatefilter("-");
							waferFilter.setStartdatefilter("-");
							List<WaferBankin> waferBankins=waferBankinService.listBySearchByOe(waferFilter);
							
							//並新增一筆WaferStatus
							WaferStatus newWaferStatus=new WaferStatus();
							newWaferStatus.setOperationUnit(operationUnit);
							newWaferStatus.setPoNumber(orderHeader.getPoNumber());
							newWaferStatus.setCustomerId(orderHeader.getCustomerId());
							newWaferStatus.setOrderNumber(orderHeader.getOrderNumber());//2013.03.20
							newWaferStatus.setCustomerLotno(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim());
							newWaferStatus.setWaferQty(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty());
							if ((waferBankins!=null) && (waferBankins.size()>0)){
								newWaferStatus.setWaferRecieveQty(waferBankins.get(0).getWaferQty());
								//newWaferStatus.setWaferFlag(true);//2013.03.07 mark
							}
							else{
								newWaferStatus.setWaferRecieveQty(0);
								//newWaferStatus.setWaferFlag(false);//2013.03.07 mark
							}
							//newWaferStatus.setInfoFlag(true);//2013.03.07 mark
							//newWaferStatus.setPoFlag(true);//2013.03.07 mark
							//newWaferStatus.setLotissueFlag(false);//2013.03.07 mark
							//newWaferStatus.setInaviFlag(false);//2013.03.07 mark
							newWaferStatus.setStateFlag("0");
							newWaferStatus.setCreateDate(inDate);
							newWaferStatus.setUpdateDate(inDate);
							newWaferStatus.setUpdateUser(userId);
							newWaferStatus.setEntityId(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getEntityId());
							//2013.03.07 以下欄位要Follow原有的值
							newWaferStatus.setLotNo(tmpWaferStatus.getLotNo());//2013.03.07 add this field
							newWaferStatus.setLotissueFlag(tmpWaferStatus.isLotissueFlag());
							newWaferStatus.setInaviFlag(tmpWaferStatus.isInaviFlag());
							newWaferStatus.setInfoFlag(tmpWaferStatus.isInfoFlag());
							newWaferStatus.setWaferFlag(tmpWaferStatus.isWaferFlag());
							newWaferStatus.setPoFlag(tmpWaferStatus.isPoFlag());
							newWaferStatus.setCustomerJob(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerJob());
							
							insertWaferStatus.add(newWaferStatus);
						}
					}
				}
				
			}//end for i
		}
	}
	
	/**
	 * 
	 * composeOrderEntryLotnoModels:如果有Hold時，重新讀取PoItem及CustomerLotno. <br/>
	 * OE-Modify狀態時，可能會有Hold，如果User有改POItem,Customerlotno時，Hold也要順便修改
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void composeOrderEntryLotnoModels(Date inDate){
		for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
			
			if (saveOrderEntryLotnoModels.get(i).getHold()!=null){
				saveOrderEntryLotnoModels.get(i).getHold().setPoItem(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem().trim());
				saveOrderEntryLotnoModels.get(i).getHold().setCustomerLotno(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim());
			}
		}
	}
	
	/**
	 * 
	 * composeWaferStatus:組合要Insert or Update的WAFER_STATUS. <br/>
	 *
	 * @author 030260
	 * @param inNowTime
	 * @return
	 * @since JDK 1.6
	 */
//	public void composeWaferStatus(Date inNowTime){
//		waferStatusModels=new ArrayList<WaferStatusModel>();
//		
//		if (saveOrderEntryLotnoModels.size()>0){
//			for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
//				WaferStatusModel saveWaferStatusModel=new WaferStatusModel();
//				String tmpMode="";
//				WaferStatus tmpWaferStatus=waferStatusService.getWaferStatusByCustomerIdandCustomerLotno(operationUnit,orderHeader.getCustomerId(), saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno());
//				if (tmpWaferStatus==null){
//					tmpMode="insert";
//					tmpWaferStatus=new WaferStatus();
//					tmpWaferStatus.setWaferStatusIdx(0);
//					tmpWaferStatus.setCreateDate(inNowTime);
//					tmpWaferStatus.setCustomerLotno(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno());
//					tmpWaferStatus.setInfoFlag(false);
//					tmpWaferStatus.setWaferFlag(false);
//					tmpWaferStatus.setUpdateDate(inNowTime);
//					tmpWaferStatus.setUpdateUser(userId);					
//					tmpWaferStatus.setOperationUnit(operationUnit);
//					tmpWaferStatus.setPoFlag(true);
//					tmpWaferStatus.setStateFlag("0");
//					tmpWaferStatus.setLotissueFlag(false);
//					tmpWaferStatus.setInaviFlag(false);
//					tmpWaferStatus.setWaferQty(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty());
//					tmpWaferStatus.setCustomerId(orderHeader.getCustomerId());
//				}
//				else {
//					tmpMode="update";
//					tmpWaferStatus.setPoFlag(true);
//					tmpWaferStatus.setUpdateDate(inNowTime);
//					tmpWaferStatus.setUpdateUser(userId);
//				}
//				
//				
//				saveWaferStatusModel.setMode(tmpMode);
//				saveWaferStatusModel.setWaferStatus(tmpWaferStatus);
//				waferStatusModels.add(saveWaferStatusModel);
//			}//end for i
//		}
//
//	}
	
	/**
	 * 
	 * createHeader:組合要insert的orderHeader. <br/>
	 *
	 * @author 030260
	 * @param inNowtime
	 * @since JDK 1.6
	 */
	public void createHeader(Date inNowtime){
		if(orderHeaders.size() > 0){
			orderHeaders.clear();
		}
		if(saveOrderEntryLotnoModels.size() > 0){
			for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
		//Header
		orderHeader.setOrderHeaderIdx(0);
		orderHeader.setOrderNumber("999999999");
		orderHeader.setOperationUnit(operationUnit);
		if (customer!=null){
			orderHeader.setCustomerId(customer.getCustomerId().trim());
		}
		else {
			orderHeader.setCustomerId("");
		}
		if (orderType!=null){
			orderHeader.setOrderType(orderType.getMeaning().trim());
		}
		else{
			orderHeader.setOrderType("");
		}
		if (waferSizeUiFieldParam!=null){
			orderHeader.setWaferSize(waferSizeUiFieldParam.getMeaning().trim());
		}
		else{
			orderHeader.setWaferSize("");
		}
		
		//orderHeader.setProduct(edtProduct.getText().trim()); //2017.12.20 remark
		orderHeader.setProduct(edtInternalProduct.getText().trim()); //2017.12.20
		orderHeader.setRealProduct(edtProduct.getText().trim()); //2017.12.20
		
		if (orderfrom!=null){
			orderHeader.setOrderFrom(orderfrom.getCustomerId().trim());
		}
		else{
			orderHeader.setOrderFrom("");
		}
		if (billto!=null){
			orderHeader.setBillTo(billto.getCustomerId().trim());
		}
		else{
			orderHeader.setBillTo("");
		}
		if (shipto!=null){
			orderHeader.setShipTo(shipto.getCustomerId().trim());
		}
		else{
			orderHeader.setShipTo("");
		}
		orderHeader.setOrderDate(inNowtime);
		orderHeader.setPoNumber(orderHeader.getOrderNumber());//IT-PR-141201
		orderHeader.setBilltoPo(edtPoNum.getText().trim());//IT-PR-141201
		orderHeader.setCustomerPo(edtCustomerPo.getText().trim());
		orderHeader.setAccountMgr(edtAccountMgr.getText().trim());
		//orderHeader.setWaferSize(cbxWaferSize.getText());
		orderHeader.setWaferSize(waferSizeUiFieldParam.getMeaning().trim());
		orderHeader.setOrderStatus("10");
				//if ("".equals(edtTotalWaferQty.getText())){
				//	orderHeader.setTotalWaferQty(0);
				//}
				//else{
				//	orderHeader.setTotalWaferQty(Integer.valueOf(edtTotalWaferQty.getText().trim()));
				//}
				
				orderHeader.setTotalWaferQty(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty());//OCF-PR-151002_因新建OE by到CustomerLotNo為一筆Order，因此totalWaferQty就是每一個CustomerLotNo的waferQty
				
		orderHeader.setCancelFlag(false);
		orderHeader.setUpdateUser(userId);
		orderHeader.setUpdateDate(inNowtime);
		orderHeader.setB2bDisableFlag(chkB2bDisableFlag.isChecked());	
		
		//orderEntryService.createOrderHeader(orderHeader);
		edtOrderStatus.setText(orderHeader.getOrderStatus().trim());
		edtOrderDate.setText(DateFormatUtil.getDateFormater().format(orderHeader.getOrderDate()).toString().trim());
				
				orderHeaders.add(orderHeader);
			}
		}
	}
	
	/**
	 * 
	 * updateHeaer:組合要update的orderHeader. <br/>
	 *
	 * @author 030260
	 * @param inNowtime
	 * @since JDK 1.6
	 */
	public void updateHeaer(Date inNowtime){
		//Header
		if (customer!=null){
			orderHeader.setCustomerId(customer.getCustomerId().trim());
		}
		else {
			orderHeader.setCustomerId("");
		}
		if (orderType!=null){
			orderHeader.setOrderType(orderType.getMeaning().trim());
		}
		else{
			orderHeader.setOrderType("");
		}
		if (waferSizeUiFieldParam!=null){
			orderHeader.setWaferSize(waferSizeUiFieldParam.getMeaning().trim());
		}
		else{
			orderHeader.setWaferSize("");
		}
		
		//orderHeader.setProduct(edtProduct.getText().trim()); //2017.12.20 remark
		orderHeader.setProduct(edtInternalProduct.getText().trim()); //2017.12.20
		orderHeader.setRealProduct(edtProduct.getText().trim()); //2017.12.20
		
		if (orderfrom!=null){
			orderHeader.setOrderFrom(orderfrom.getCustomerId().trim());
		}
		else{
			orderHeader.setOrderFrom("");
		}
		if (billto!=null){
			orderHeader.setBillTo(billto.getCustomerId().trim());
		}
		else{
			orderHeader.setBillTo("");
		}
		if (shipto!=null){
			orderHeader.setShipTo(shipto.getCustomerId().trim());
		}
		else{
			orderHeader.setShipTo("");
		}
		//orderHeader.setPoNumber(edtPoNum.getText().trim());//IT-PR-141201
		orderHeader.setBilltoPo(edtPoNum.getText().trim());//IT-PR-141201
		orderHeader.setCustomerPo(edtCustomerPo.getText().trim());
		orderHeader.setAccountMgr(edtAccountMgr.getText().trim());
		//orderHeader.setWaferSize(cbxWaferSize.getText());
		orderHeader.setWaferSize(waferSizeUiFieldParam.getMeaning().trim());
		if ("".equals(edtTotalWaferQty.getText())){
			orderHeader.setTotalWaferQty(0);
		}
		else{
			orderHeader.setTotalWaferQty(Integer.valueOf(edtTotalWaferQty.getText().toString().trim()));
		}
		orderHeader.setUpdateUser(userId);
		orderHeader.setUpdateDate(inNowtime);
		orderHeader.setB2bDisableFlag(chkB2bDisableFlag.isChecked());
		//orderEntryService.updateOrderHeader(orderHeader);
	}

	/**
	 * 
	 * 設置Combobox的選單內容
	 * @see com.tce.ivision.modules.base.ctrl.BaseViewCtrl#initialComboboxItem()
	 */
	@Override
	protected void initialComboboxItem() throws Exception {
		//Customer
		customers=customerInformationService.getCustomerTableByBusPurpose("C");
		ZkComboboxControl.setComboboxItems(cbxCustomer, customers, "getCustomerShortName","isCancelFlag",false);
		//ZkComboboxControl.setComboboxItemValues(cbxCustomer, customers, "getCustomerShortName", "getCustomerId", "", true);
		
		//BillTo
		billtos=customerInformationService.getCustomerTableByBusPurpose("B");
		ZkComboboxControl.setComboboxItems(cbxBillTo, billtos, "getCustomerShortName","isCancelFlag",false);
		
		//ShipTo
		shiptos=customerInformationService.getCustomerTableByBusPurpose("S");
		ZkComboboxControl.setComboboxItems(cbxShipTo, shiptos, "getCustomerShortName","isCancelFlag",false);
		
		//OrderFrom
		orderfroms=customerInformationService.getCustomerTableByBusPurpose("O");
		ZkComboboxControl.setComboboxItems(cbxOrderFrom, orderfroms, "getCustomerShortName","isCancelFlag",false);
		
		//OrderType
		orderTypes=commonService.getUiFieldSetLists(this.getClass().getName(), "OE_ORDER_TYPE");
		if (orderTypes.size()>0){
			orderTypeUiFieldParams=orderTypes.get(0).getUiFieldParams();
			ZkComboboxControl.setComboboxItems(cbxOrderType, orderTypeUiFieldParams, "getParaValue","isEnabled",true);
			//ZkComboboxControl.setComboboxItemValues(cbxOrderType, orderTypeUiFieldParams, "getParaValue", "getMeaning", "isEnabled",true);
		}
		
		//WaferSize
		waferSizes=commonService.getUiFieldSetLists(this.getClass().getName(), "OE_WAFER_SIZE");
		if (waferSizes.size()>0){
			waferSizeUiFieldParams=waferSizes.get(0).getUiFieldParams();
			ZkComboboxControl.setComboboxItems(cbxWaferSize, waferSizeUiFieldParams, "getParaValue","isEnabled",true);
		}
		
		//CfaSite 2013.01.15
		cfaSites=commonService.getUiFieldSetLists(this.getClass().getName(), "OE_CFA_SITE");
		if (cfaSites.size()>0){
			cfaSiteUiFieldParams=cfaSites.get(0).getUiFieldParams();
			ZkComboboxControl.setComboboxItems(cbxGCfaSite, cfaSiteUiFieldParams, "getParaValue", "isEnabled", true);
		}
		//version:XQ181004 ReworkFlag 2018.11.09
		//RMA add by will des：添加RMA到下拉框中
		reworkFlag=commonService.getUiFieldSetLists(this.getClass().getName(), "OE_REWORK_FLAG");
		if (reworkFlag.size()>0){
			reworkFlagUiFieldParams=reworkFlag.get(0).getUiFieldParams();
			ZkComboboxControl.setComboboxItems(cbxGReworkFlag, reworkFlagUiFieldParams, "getParaValue", "isEnabled", true);
		}
		//Currency
		currencys=commonService.getUiFieldSetLists(this.getClass().getName(), "OE_CURRENCY");
		if (currencys.size()>0){
			currencyUiFieldParams=currencys.get(0).getUiFieldParams();
			ZkComboboxControl.setComboboxItems(cbxGCurrency, currencyUiFieldParams, "getParaValue", "isEnabled", true);
		}
		
		//2013.01.15 OrderStatus
		orderStatus=commonService.getUiFieldSetLists(this.getClass().getName(), "OE_ORDER_TYPE");
		
		//iMask-OM
		iMaskOMs=commonService.getUiFieldSetLists(this.getClass().getName(),"iMask-OM");//2014.08.28
		if (iMaskOMs.size()>0){
			iMaskOMUiFieldParams=iMaskOMs.get(0).getUiFieldParams();
		}
	}
	
	/**
	 * 
	 * setReadonly:如果是readonly時，將畫面上的元件設定為readonly或者disable. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void setReadonly(){
		//設定畫面上的元件是否要read only
		if ("readonly".equals(mode)){
			cbxCustomer.setReadonly(true);
			cbxOrderType.setReadonly(true);
			edtProduct.setReadonly(true);
			cbxOrderFrom.setReadonly(true);
			cbxBillTo.setReadonly(true);
			cbxShipTo.setReadonly(true);
			edtTotalWaferQty.setReadonly(true);
			edtOrderDate.setReadonly(true);
		    edtOrderNumber.setReadonly(true);
		    edtPoNum.setReadonly(true);
		    edtCustomerPo.setReadonly(true);
		    edtAccountMgr.setReadonly(true);
		    cbxWaferSize.setReadonly(true);
		    edtOrderStatus.setReadonly(true);
		    btnSave.setDisabled(true);
		    
		    /*cbxCustomer.setDisabled(true);
		    cbxOrderType.setDisabled(true);
		    cbxOrderFrom.setDisabled(true);
		    cbxBillTo.setDisabled(true);
		    cbxShipTo.setDisabled(true);
		    cbxWaferSize.setDisabled(true);*/
		    cbxCustomer.setButtonVisible(false);
		    cbxOrderType.setButtonVisible(false);
		    cbxOrderFrom.setButtonVisible(false);
		    cbxBillTo.setButtonVisible(false);
		    cbxShipTo.setButtonVisible(false);
		    cbxWaferSize.setButtonVisible(false);
		    
		    cbxCustomer.setDisabled(true);//2013.07.09
		    cbxOrderType.setDisabled(true);//2013.07.09
		    cbxOrderFrom.setDisabled(true);//2013.07.09
		    cbxBillTo.setDisabled(true);//2013.07.09
		    cbxShipTo.setDisabled(true);//2013.07.09
		    cbxWaferSize.setDisabled(true);//2013.07.09
		    
		    btnLineAdd.setDisabled(true);
		    btnLineDel.setDisabled(true);
		    btnLineCopyfrom.setDisabled(true);
		    btnLotnoAdd.setDisabled(true);
		    btnLotnoDel.setDisabled(true);
		    
		    //cbxGCurrency.setDisabled(true);
		    cbxGCurrency.setReadonly(true);
		    cbxGCurrency.setButtonVisible(false);
		    cbxGCurrency.setDisabled(true);//2013.07.09
		    dtbGRequestDate.setDisabled(true);
		    cbxGCfaSite.setReadonly(true);
		    cbxGCfaSite.setButtonVisible(false);
		    cbxGCfaSite.setDisabled(true);//2013.07.09
		}
		
		//2013.07.09 OrderStatus=20時，限制部分欄位readonly
		if ("20".equals(orderHeader.getOrderStatus())){
			cbxCustomer.setReadonly(true);
			//edtPoNum.setReadonly(true);//OCF-PR-150107
			cbxOrderType.setReadonly(true);
			edtProduct.setReadonly(true);
			
			cbxCustomer.setButtonVisible(false);
			cbxOrderType.setButtonVisible(false);
			
			cbxCustomer.setDisabled(true);
			cbxOrderType.setDisabled(true);
		}
		
	}
	
	
	/**
	 * Add grdLine
	 */
	public void onClick$btnLineAdd(){
		OrderEntryLineModel addOrderEntryLineModel=new OrderEntryLineModel();
		addOrderEntryLineModel.setOrderLineIntIdx(0);
		
		OrderLine addOrderLine=new OrderLine();
		addOrderLine.setOrderLineIdx(0);
		addOrderLine.setOrderNumber(edtOrderNumber.getText().trim());
		addOrderLine.setPoItem("");
		addOrderLine.setSourceMtrlNum("");
		addOrderLine.setMtrlNum("");
		addOrderLine.setSourceMtrlNum("");
		addOrderLine.setMtrlDesc("");
		addOrderLine.setMtrlNumMtrlgrp("");
		addOrderLine.setWaferQty(0);
		addOrderLine.setRequestDate("");
		addOrderLine.setDesignId("");
		addOrderLine.setCfaPorId("");
		addOrderLine.setCountryOfFab("");
		addOrderLine.setFab("");
		log.debug(cbxWaferSize.getText());
		addOrderLine.setWaferSize(cbxWaferSize.getText().trim());//先給ParaValue等真正儲存時再轉成meaning
		//addOrderLine.setWaferSize(this.getMeaningByParaValue(cbxWaferSize.getText(), waferSizeUiFieldParams));
		//addOrderLine.setCfaSite("TOPPAN-TCE");//2013.01.15
		addOrderLine.setCfaSite("TOPPAN-TSES"); //2017.12.20
		addOrderLine.setShipToVendorCode("");
		//addOrderLine.setShipToVendorName("");//2013.06.21 mark
		addOrderLine.setShipToVendorName(cbxShipTo.getValue().trim());//2013.06.21
		//addOrderLine.setShippingDestination(cbxShipTo.getValue().trim());//OCF-PR-160602
		addOrderLine.setShipComment("");
		addOrderLine.setCreatedDate(new Date());
		addOrderLine.setCreatedUser(userId);
		addOrderLine.setCurrency("");
		addOrderLine.setUnitPrice(0.0);
		addOrderLine.setSellingPrice(0.0);
		addOrderLine.setCompanyCode("");
		addOrderLine.setUpdateUser(userId);
		addOrderLine.setUpdateDate(new Date());
		addOrderLine.setCancelFlag(false);
		addOrderLine.setOrderHeader(orderHeader);
		List<CustomerTable> customerTableList=customerInformationService.getCustomerTableByCustomerShortNoBusPurpose(cbxShipTo.getValue().trim());
		addOrderLine.setShipToVendorCode(customerTableList.get(0).getCustomerId());
		addOrderEntryLineModel.setOrderLine(addOrderLine);
		
		saveOrderEntryLineModels.add(addOrderEntryLineModel);
		grdLine.setModel(new ListModelList(saveOrderEntryLineModels));
		grdLine.setItemRenderer(this);
	}
	
	
	/**
	 * Del grdLine
	 */
	public void onClick$btnLineDel(){
		if (grdLine.getItemCount()==0){
			return;
		}
		
		if (grdLine.getSelectedIndex()<0){
			return;
		}
		
		//如果已經有ORDER_LINE_LOTNO的資料，就不可刪除
		if (saveOrderEntryLotnoModels.size()>0){
			List<OrderEntryLineModel> tmpOrderEntryLineModels=(List<OrderEntryLineModel>) grdLine.getModel();
			OrderEntryLineModel selOrderEntryLineModel=tmpOrderEntryLineModels.get(grdLine.getSelectedIndex());
			boolean lotnoFlag=false;
			for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
				if ((selOrderEntryLineModel.getOrderLine().getPoItem().trim().equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem().trim()))&&
				    (saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCancelFlag()==false)){
					lotnoFlag=true;
					break;
				}
			}
			
			if (lotnoFlag==true){
				this.showmessage("Warning", Labels.getLabel("oe.edit.line.delete.check.lotno",
						new Object[] {Labels.getLabel("oe.edit.line.grbLotnoData")}));
				return;
			}
		}
		
		//先將非User所選的data copy to tmpSaveOrderEntryLineModels
		List<OrderEntryLineModel> tmpSaveOrderEntryLineModels=new ArrayList<OrderEntryLineModel>();
		if (saveOrderEntryLineModels.size()>0){
			for (int i=0;i<saveOrderEntryLineModels.size();i++){
				if (i==grdLine.getSelectedIndex()){
				}
				else{
					tmpSaveOrderEntryLineModels.add(saveOrderEntryLineModels.get(i));
				}
			}
		}
		saveOrderEntryLineModels=new ArrayList<OrderEntryLineModel>();
		
		//tmpSaveOrderEntryLineModels copy to saveOrderEntryLineModels
		if (tmpSaveOrderEntryLineModels.size()>0){
			for (int i=0;i<tmpSaveOrderEntryLineModels.size();i++){
				saveOrderEntryLineModels.add(tmpSaveOrderEntryLineModels.get(i));
			}
			tmpSaveOrderEntryLineModels=new ArrayList<OrderEntryLineModel>();
		}
		log.debug("saveOrderEntryLineModels.size="+saveOrderEntryLineModels.size());
		
		grdLine.setModel(new ListModelList(saveOrderEntryLineModels));
		grdLine.setItemRenderer(this);
		
	}
	
	/**
	 * ORDER_LINE Copy From B2B data
	 */
	public void onClick$btnLineCopyfrom(){
		if ((cbxCustomer.getSelectedIndex()<0) || ("".equals(edtPoNum.getText().trim()))){
			this.showmessage("Warning", Labels.getLabel("oe.edit.copyfromb2b.check",
					new Object[] {Labels.getLabel("oe.edit.header.lblCustomer"),
					Labels.getLabel("oe.edit.header.lblPoNumber")}));
			return;
		}
		
		ZkComboboxControl.setComboboxClear(cbxSelectOrderLineIntIdxPa);
		//要先把grdLine有的資料先儲存到datas
		List<OrderEntryLineModel> datas=new ArrayList<OrderEntryLineModel>();
		saveOrderEntryLineModels=new ArrayList<OrderEntryLineModel>();
		if (grdLine.getItemCount()>0){
			datas=(List<OrderEntryLineModel>) grdLine.getModel();
		}
		
		Map args = new HashMap();
		args.put("winid", "winOrderLineCopyFromB2B");	
		args.put("customer", cbxCustomer.getText());
		args.put("customerId", customer.getCustomerId());
		args.put("customerPo", edtCustomerPo.getText());
		args.put("grdLineIntPa", grdLineIntPa);
		args.put("cbxSelectOrderLineIntIdxPa",cbxSelectOrderLineIntIdxPa);
		Window winimport = (Window)Executions.createComponents("/WEB-INF/modules/oe/OrderLineB2B.zul", null, args);
		winimport.doModal();
		
		log.debug(cbxSelectOrderLineIntIdxPa.getItemCount());
		
		//先將copy From 之前的datas copy 至 saveOrderEntryLineModels
		if (datas.size()>0){
			for (int i=0;i<datas.size();i++){
				saveOrderEntryLineModels.add(datas.get(i));
			}
			log.debug(saveOrderEntryLotnoModels.size());
		}
		
		
		//cbxSelectOrderLineIntIdxPa 是從Copy From B2B 畫面傳回來的
		boolean existFlag=false;
		if (cbxSelectOrderLineIntIdxPa.getItemCount()>0){
			String duplicatePoItemstr="";
			int tmpWaferQty=0;
			String tmpPoItem="";
			for (int i=0;i<cbxSelectOrderLineIntIdxPa.getItemCount();i++){
				OrderLineInt selOrderLineInt = orderEntryService.getOrderLineIntByIdx(Integer.valueOf(cbxSelectOrderLineIntIdxPa.getItemAtIndex(i).getLabel()));
				
				//確認PoItem是否在datas已有重複的資料，如果有重複，就不可以再放入
				boolean duplicateFlag=false;
				if ((datas.size()>0)&&(selOrderLineInt!=null)){
					log.debug(selOrderLineInt.getPoItem());
					for (int k=0;k<datas.size();k++){
						log.debug(datas.get(k).getOrderLine().getPoItem());
						if (selOrderLineInt.getPoItem().trim().equals(datas.get(k).getOrderLine().getPoItem().trim())){
							duplicatePoItemstr=duplicatePoItemstr+selOrderLineInt.getPoItem().trim()+";";
							duplicateFlag=true;
							break;
						}
					}//end for k
				}
				
				if (duplicateFlag==false){
					if (selOrderLineInt!=null){	
						boolean checkPoItemFlag=false;
						//IT-PR-141008_因OMNI的WI Report會有相同PoItem的狀況，故要先檢查若有相同的，則WaferQty要相加_Allison
						for(int j=0; j<saveOrderEntryLineModels.size(); j++){
							if(selOrderLineInt.getPoItem().equals(saveOrderEntryLineModels.get(j).getOrderLine().getPoItem())){
								saveOrderEntryLineModels.get(j).getOrderLine().setWaferQty(saveOrderEntryLineModels.get(j).getOrderLine().getWaferQty()+selOrderLineInt.getWaferQty());
								checkPoItemFlag=true;
								break;
							}
						}
						if(!checkPoItemFlag){
							OrderEntryLineModel data=new OrderEntryLineModel();
							data.setOrderLineIntIdx(Integer.valueOf(cbxSelectOrderLineIntIdxPa.getItemAtIndex(i).getLabel()));
							
							OrderLine dataOrderLine=new OrderLine();
							if(selOrderLineInt.getPoItem()!=null && !"".equals(selOrderLineInt.getPoItem())){
								dataOrderLine.setPoItem(selOrderLineInt.getPoItem().trim());
							}else{
								dataOrderLine.setPoItem("");
							}
							if(selOrderLineInt.getMtrlNum()!=null && !"".equals(selOrderLineInt.getMtrlNum())){
								dataOrderLine.setMtrlNum(selOrderLineInt.getMtrlNum().trim());
							}else{
								dataOrderLine.setMtrlNum("");
							}
							if(selOrderLineInt.getSourceMtrlNum()!=null && !"".equals(selOrderLineInt.getSourceMtrlNum())){
								dataOrderLine.setSourceMtrlNum(selOrderLineInt.getSourceMtrlNum().trim());
							}else{
								dataOrderLine.setSourceMtrlNum("");
							}
							if(selOrderLineInt.getMtrlDesc()!=null && !"".equals(selOrderLineInt.getMtrlDesc())){
								dataOrderLine.setMtrlDesc(selOrderLineInt.getMtrlDesc().trim());
							}else{
								dataOrderLine.setMtrlDesc("");
							}
							if(selOrderLineInt.getMtrlNumMtrlgrp()!=null && !"".equals(selOrderLineInt.getMtrlNumMtrlgrp())){
								dataOrderLine.setMtrlNumMtrlgrp(selOrderLineInt.getMtrlNumMtrlgrp().trim());
							}else{
								dataOrderLine.setMtrlNumMtrlgrp("");
							}
							dataOrderLine.setWaferQty(Integer.valueOf(selOrderLineInt.getWaferQty()));
							if(selOrderLineInt.getWaferSize()!=null && !"".equals(selOrderLineInt.getWaferSize())){
								dataOrderLine.setWaferSize(selOrderLineInt.getWaferSize().trim());//先暫時存ParaValue,等真正儲存的時候再轉換為meaning
							}else{
								dataOrderLine.setWaferSize("");
							}
							//dataOrderLine.setWaferSize(this.getMeaningByParaValue(selOrderLineInt.getWaferSize(), waferSizeUiFieldParams));
							//dataOrderLine.setCfaSite("TOPPAN-TCE");//2013.01.15
							dataOrderLine.setCfaSite("TOPPAN-TSES"); //2017.12.20
							dataOrderLine.setCurrency("");
							dataOrderLine.setUnitPrice(0.0);
							dataOrderLine.setSellingPrice(0.0);
							if(selOrderLineInt.getDelivDate()!=null && !"".equals(selOrderLineInt.getDelivDate())){
								dataOrderLine.setRequestDate(selOrderLineInt.getDelivDate().toString().trim());
							}else{
								dataOrderLine.setRequestDate("");
							}
							if(selOrderLineInt.getDesignId()!=null && !"".equals(selOrderLineInt.getDesignId())){
								dataOrderLine.setDesignId(selOrderLineInt.getDesignId().trim());
							}else{
								dataOrderLine.setDesignId("");
							}
							if(selOrderLineInt.getCfaPorId()!=null && !"".equals(selOrderLineInt.getCfaPorId())){
								dataOrderLine.setCfaPorId(selOrderLineInt.getCfaPorId().trim());
							}else{
								dataOrderLine.setCfaPorId("");
							}
							if(selOrderLineInt.getShipToVendorCode()!=null && !"".equals(selOrderLineInt.getShipToVendorCode())){
								dataOrderLine.setShipToVendorCode(selOrderLineInt.getShipToVendorCode().trim());
							}else{
								dataOrderLine.setShipToVendorCode("");
							}
							if(selOrderLineInt.getShipToVendorName()!=null && !"".equals(selOrderLineInt.getShipToVendorName())){
								dataOrderLine.setShipToVendorName(selOrderLineInt.getShipToVendorName().trim());
							}else{
								dataOrderLine.setShipToVendorName("");
							}
							//OCF-PR-160702_不從工單帶資料
							//if(selOrderLineInt.getShipToVendorName()!=null && !"".equals(selOrderLineInt.getShipToVendorName())){
							//	dataOrderLine.setShippingDestination(selOrderLineInt.getShipToVendorName().trim());
							//}else{
							//	dataOrderLine.setShippingDestination("");
							//}
							if(selOrderLineInt.getShipComment()!=null && !"".equals(selOrderLineInt.getShipComment())){
								dataOrderLine.setShipComment(selOrderLineInt.getShipComment().trim());
							}else{
								dataOrderLine.setShipComment("");
							}
							if(selOrderLineInt.getCompCode()!=null && !"".equals(selOrderLineInt.getCompCode())){
								dataOrderLine.setCompanyCode(selOrderLineInt.getCompCode().trim());
							}else{
								dataOrderLine.setCompanyCode("");
							}
							if(selOrderLineInt.getCountryOfFab()!=null && !"".equals(selOrderLineInt.getCountryOfFab())){
								dataOrderLine.setCountryOfFab(selOrderLineInt.getCountryOfFab().trim());
							}else{
								dataOrderLine.setCountryOfFab("");
							}
							if(selOrderLineInt.getFab()!=null && !"".equals(selOrderLineInt.getFab())){
								dataOrderLine.setFab(selOrderLineInt.getFab().trim());
							}else{
								dataOrderLine.setFab("");
							}
							if(selOrderLineInt.getSourceMtrlNum()!=null && !"".equals(selOrderLineInt.getSourceMtrlNum())){
								dataOrderLine.setSourceMtrlNum(selOrderLineInt.getSourceMtrlNum().trim());
							}else{
								dataOrderLine.setSourceMtrlNum("");
							}
							if(selOrderLineInt.getSubName()!=null && !"".equals(selOrderLineInt.getSubName())){
								dataOrderLine.setSubName(selOrderLineInt.getSubName());
							}else{
								dataOrderLine.setSubName("");
							}
							if(selOrderLineInt.getStage()!=null && !"".equals(selOrderLineInt.getStage())){
								dataOrderLine.setStage(selOrderLineInt.getStage());
							}else{
								dataOrderLine.setStage("");
							}
							if(selOrderLineInt.getOperationDescription()!=null && !"".equals(selOrderLineInt.getOperationDescription())){
								dataOrderLine.setOperationDescription(selOrderLineInt.getOperationDescription());
							}else{
								dataOrderLine.setOperationDescription("");
							}
							dataOrderLine.setWaferSize(cbxWaferSize.getText());
							
							data.setOrderLine(dataOrderLine);
							
							saveOrderEntryLineModels.add(data);
						}
					}
				}//end if duplicateFlag==false
			}//end for i
			
			if (!"".equals(duplicatePoItemstr)){
				this.showmessage("Warning", Labels.getLabel("oe.edit.copyfromb2b.poItem.duplicate",
						new Object[] {Labels.getLabel("oe.edit.line.grdLine.colLinePoItem"),"\r\n"+duplicatePoItemstr}));
				existFlag=true;
			}
			
			grdLine.setModel(new ListModelList(saveOrderEntryLineModels));
			grdLine.setItemRenderer(this);
		}
		
		//IT-PR-141008_客戶若為OMNI，且是用Upload From B2B，則直接將WI Report的CustomerLotNo的資訊帶入Customer LotNo Info中_Allison
		if (!existFlag){
			/**
			 * @author zhouyang
			 * 日期：2018/11/2
			 * 除OV外客户的工单导入需要去掉OMNI的判断，否则工单导入不成功。
			 */
			//cbxCustomer.getText().equals("OMNI") &&
			if( cbxSelectOrderLineIntIdxPa.getItemCount()>0){
				this.onClick$btnLotnoAdd();
			}
		}
	}
	
	/**
	 * 
	 * clearListbox:清空Listbox. <br/>
	 *
	 * @author 030260
	 * @param inListbox
	 * @since JDK 1.6
	 */
	public void clearListbox(Listbox inListbox){
		for (int i=inListbox.getItemCount()-1;i==0;i--){
			inListbox.removeItemAt(i);
		}
	}
	
	/**
	 * 
	 * onClick$btnLotnoAdd:Add LotNO data. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void onClick$btnLotnoAdd(){	
		if (this.chkgrdLinePOItemcannotDuplicate()==false){
			return;
		}
		
		//IT-PR-141008_客戶若為OMNI，且是用Upload From B2B，則直接將WI Report的CustomerLotNo的資訊帶入Customer LotNo Info中_Allison
		//2018/11/2 zhouyang 除OV外客户的工单导入需要去掉OMNI的判断，否则工单导入不成功。
		//cbxCustomer.getText().equals("OMNI") &&
		if( cbxSelectOrderLineIntIdxPa.getItemCount()>0){
			for(int i=0; i<grdLine.getItemCount(); i++){
				grdLine.setSelectedIndex(i);
				for(int j=0; j<cbxSelectOrderLineIntIdxPa.getItemCount(); j++){
					OrderLineInt selOrderLineInt = orderEntryService.getOrderLineIntByIdx(Integer.valueOf(cbxSelectOrderLineIntIdxPa.getItemAtIndex(j).getLabel()));

					//User選擇的grdLine
					List<OrderEntryLineModel> tmpOrderEntryLineModels=new ArrayList<OrderEntryLineModel>();
					tmpOrderEntryLineModels=(List<OrderEntryLineModel>) grdLine.getModel();

					//清空tmporderEntryLotnoModels
					tmporderEntryLotnoModels=new ArrayList<OrderEntryLotnoModel>();
					if(!tmporderEntryLotnoModels.isEmpty()){
						tmporderEntryLotnoModels=(List<OrderEntryLotnoModel>) grdLotno.getModel();
					}

					if(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine().getPoItem().trim().equals(selOrderLineInt.getPoItem())){
						//新增一筆OrderEntryLotnoModel
						OrderLineLotno tmpOrderLineLotno=new OrderLineLotno();
						tmpOrderLineLotno.setOrderLine(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine());
						tmpOrderLineLotno.setOrderLineLotnoIdx(0);
						tmpOrderLineLotno.setCancelFlag(false);
						if(selOrderLineInt.getCustomerLotNo()!= null && !"".equals(selOrderLineInt.getCustomerLotNo())){
							tmpOrderLineLotno.setCustomerLotno(selOrderLineInt.getCustomerLotNo().trim());
						}else{
							tmpOrderLineLotno.setCustomerLotno("");
						}
						//tmpOrderLineLotno.setOrderNumber(tmpOrderEntryLineModels.get(grdLine.getSelectedCount()).getOrderLine().getOrderNumber());
						if(!"".equals(edtOrderNumber.getText())){
						tmpOrderLineLotno.setOrderNumber(edtOrderNumber.getText().trim());
						}else{
							tmpOrderLineLotno.setOrderNumber("");
						}
						if(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine().getPoItem()!=null && !"".equals(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine().getPoItem())){
						tmpOrderLineLotno.setPoItem(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine().getPoItem().trim());
						}else{
							tmpOrderLineLotno.setPoItem("");
						}
						tmpOrderLineLotno.setUpdateDate(new Date());
						tmpOrderLineLotno.setUpdateUser(userId);
						tmpOrderLineLotno.setWaferQty(selOrderLineInt.getWaferQty());
						tmpOrderLineLotno.setOrderLine(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine());
						tmpOrderLineLotno.setHoldFlag(false);//2013.01.15
						tmpOrderLineLotno.setEntityId("");//2013.02.07
						if(selOrderLineInt.getCustomerJob()!=null && !"".equals(selOrderLineInt.getCustomerJob())){
							tmpOrderLineLotno.setCustomerJob(selOrderLineInt.getCustomerJob().trim());//IT-PR-141008_Allison add
						}else{
							tmpOrderLineLotno.setCustomerJob("");//IT-PR-141008_Allison add
						}
						if(selOrderLineInt.getPriority()!=null && !"".equals(selOrderLineInt.getPriority())){
							tmpOrderLineLotno.setPriority(selOrderLineInt.getPriority().trim());
						}else{
							tmpOrderLineLotno.setPriority("");
						}
						if(selOrderLineInt.getLotType()!=null && !"".equals(selOrderLineInt.getLotType())){
							tmpOrderLineLotno.setLotType(selOrderLineInt.getLotType().trim());
						}else{
							tmpOrderLineLotno.setLotType("");
						}
						tmpOrderLineLotno.setSo("");
						tmpOrderLineLotno.setSoLine("");
						if(selOrderLineInt.getWaferData()!=null && !"".equals(selOrderLineInt.getWaferData())){
							tmpOrderLineLotno.setWaferData(selOrderLineInt.getWaferData().trim());
						}else{
							tmpOrderLineLotno.setWaferData("");	
						}
						if(selOrderLineInt.getWiRmaNo()!=null && !"".equals(selOrderLineInt.getWiRmaNo())){
							tmpOrderLineLotno.setWiRmaNo(selOrderLineInt.getWiRmaNo().trim());
						}else{
							tmpOrderLineLotno.setWiRmaNo("");
						}
						if(selOrderLineInt.getWaferDie()!=null && !"".equals(selOrderLineInt.getWaferDie())){
							tmpOrderLineLotno.setWaferDie(selOrderLineInt.getWaferDie().trim());
						}else{
							tmpOrderLineLotno.setWaferDie("");
						}
						if(selOrderLineInt.getGradeRecord()!=null && !"".equals(selOrderLineInt.getGradeRecord())){
							tmpOrderLineLotno.setGradeRecord(selOrderLineInt.getGradeRecord().trim());
						}else{
							tmpOrderLineLotno.setGradeRecord("");
						}
						if(selOrderLineInt.getEngNo()!=null && !"".equals(selOrderLineInt.getEngNo())){
							tmpOrderLineLotno.setEngNo(selOrderLineInt.getEngNo().trim());
						}else{
							tmpOrderLineLotno.setEngNo("");
						}
						if(selOrderLineInt.getTestProgram()!=null && !"".equals(selOrderLineInt.getTestProgram())){
							tmpOrderLineLotno.setTestProgram(selOrderLineInt.getTestProgram().trim());
						}else{
							tmpOrderLineLotno.setTestProgram("");
						}
						//OCF-PR-150302
						tmpOrderLineLotno.setPackingListPrintDisable(false);
						tmpOrderLineLotno.setShippingRemark("");
						OrderEntryLotnoModel tmpOrderEntryLotnoModel=new OrderEntryLotnoModel();
						tmpOrderEntryLotnoModel.setMode(mode);
						tmpOrderEntryLotnoModel.setOrderLineLotno(tmpOrderLineLotno);
						tmpOrderEntryLotnoModel.setHold(null);//新增一筆時是沒有Hold的狀態的
						tmpOrderEntryLotnoModel.setCustomerLotnoLen(customerLotnoLen);
						tmpOrderEntryLotnoModel.setOrderHeaderOrderStatus(orderHeader.getOrderStatus());//2013.07.09

						//tmpOrderEntryLotnoModel,tmpOrderEntryLotnoModel各新增一筆
						saveOrderEntryLotnoModels.add(tmpOrderEntryLotnoModel);
						tmporderEntryLotnoModels.add(tmpOrderEntryLotnoModel);
					}
				}
			}	
			List<OrderEntryLotnoModel> selectOrderEntryLineModels=new ArrayList<OrderEntryLotnoModel>();
			for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
				if(tmporderEntryLotnoModels.size() > 0){
				if(tmporderEntryLotnoModels.get(0).getOrderLineLotno().getPoItem().equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem())){
					selectOrderEntryLineModels.add(saveOrderEntryLotnoModels.get(i));
				}
			}
			}
			
			//產生Render
			grdLotno.setModel(new ListModelList(selectOrderEntryLineModels));
			grdLotno.setItemRenderer(new OrderLotnoRender());
		}else{
			//檢查grdLine是否有選擇一筆data，且PO Item不可空白,且grdLine的PO Item不可重複
			if (grdLine.getSelectedIndex()<0){
				this.showmessage("Warning", Labels.getLabel("oe.edit.line.grdLine.choiceone"));
				return;
			} 
			
			//User選擇的grdLine
			List<OrderEntryLineModel> tmpOrderEntryLineModels=new ArrayList<OrderEntryLineModel>();
			tmpOrderEntryLineModels=(List<OrderEntryLineModel>) grdLine.getModel();
			
			//清空tmporderEntryLotnoModels
			tmporderEntryLotnoModels=new ArrayList<OrderEntryLotnoModel>();
			tmporderEntryLotnoModels=(List<OrderEntryLotnoModel>) grdLotno.getModel();
			
			//新增一筆OrderEntryLotnoModel
			OrderLineLotno tmpOrderLineLotno=new OrderLineLotno();
			tmpOrderLineLotno.setOrderLine(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine());
			tmpOrderLineLotno.setOrderLineLotnoIdx(0);
			tmpOrderLineLotno.setCancelFlag(false);
			tmpOrderLineLotno.setCustomerLotno("");
			//tmpOrderLineLotno.setOrderNumber(tmpOrderEntryLineModels.get(grdLine.getSelectedCount()).getOrderLine().getOrderNumber());
			if(!"".equals(edtOrderNumber.getText())){
			tmpOrderLineLotno.setOrderNumber(edtOrderNumber.getText().trim());
			}else{
				tmpOrderLineLotno.setOrderNumber("");
			}
			if(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine().getPoItem()!=null && !"".equals(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine().getPoItem())){
			tmpOrderLineLotno.setPoItem(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine().getPoItem().trim());
			}else{
				tmpOrderLineLotno.setPoItem("");
			}
			tmpOrderLineLotno.setUpdateDate(new Date());
			tmpOrderLineLotno.setUpdateUser(userId);
			tmpOrderLineLotno.setWaferQty(0);
			tmpOrderLineLotno.setOrderLine(tmpOrderEntryLineModels.get(grdLine.getSelectedIndex()).getOrderLine());
			tmpOrderLineLotno.setHoldFlag(false);//2013.01.15
			tmpOrderLineLotno.setEntityId("");//2013.02.07
			//OCF-PR-150302
			tmpOrderLineLotno.setPackingListPrintDisable(false);
			tmpOrderLineLotno.setShippingRemark("");
			
			OrderEntryLotnoModel tmpOrderEntryLotnoModel=new OrderEntryLotnoModel();
			tmpOrderEntryLotnoModel.setMode(mode);
			tmpOrderEntryLotnoModel.setOrderLineLotno(tmpOrderLineLotno);
			tmpOrderEntryLotnoModel.setHold(null);//新增一筆時是沒有Hold的狀態的
			tmpOrderEntryLotnoModel.setCustomerLotnoLen(customerLotnoLen);
			tmpOrderEntryLotnoModel.setOrderHeaderOrderStatus(orderHeader.getOrderStatus());//2013.07.09
			
			//tmpOrderEntryLotnoModel,tmpOrderEntryLotnoModel各新增一筆
			saveOrderEntryLotnoModels.add(tmpOrderEntryLotnoModel);
			tmporderEntryLotnoModels.add(tmpOrderEntryLotnoModel);
			//產生Render
			grdLotno.setModel(new ListModelList(tmporderEntryLotnoModels));
			grdLotno.setItemRenderer(new OrderLotnoRender());	
		}
	}
	
	/**
	 * 
	 * onClick$btnLotnoDel:Delete LotNO data. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void onClick$btnLotnoDel(){
		if (grdLotno.getItemCount()==0){
			return;
		}
		
		if (grdLotno.getSelectedIndex()<0){
			return;
		}
		
		//tmporderEntryLotnoModels是User選擇要刪除的data
		tmporderEntryLotnoModels=new ArrayList<OrderEntryLotnoModel>();
		tmporderEntryLotnoModels=(List<OrderEntryLotnoModel>) grdLotno.getModel();
		String selPoItem=tmporderEntryLotnoModels.get(grdLotno.getSelectedIndex()).getOrderLineLotno().getPoItem().trim();
		String selCustomerLotno=tmporderEntryLotnoModels.get(grdLotno.getSelectedIndex()).getOrderLineLotno().getCustomerLotno().trim();
		
		//要刪除的CustomerLotno是否已經有Hold，如果是，不允許刪除
		if (tmporderEntryLotnoModels.get(grdLotno.getSelectedIndex()).getOrderLineLotno().isHoldFlag()){
			this.showmessage("Warning", Labels.getLabel("oe.edit.lotno.delete.chkhold"));
			return;
		}
		
		//把不是要刪除的資料放在tmpSaveOrderLineLotnos
		List<OrderEntryLotnoModel> tmpSaveOrderEntryLotnos=new ArrayList<OrderEntryLotnoModel>();
		if (saveOrderEntryLotnoModels.size()>0){
			for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
				if ((saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem().trim().equals(selPoItem))&&
				    (saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim().equals(selCustomerLotno))){
				}
				else {
					tmpSaveOrderEntryLotnos.add(saveOrderEntryLotnoModels.get(i));
				}
			}
		}
		saveOrderEntryLotnoModels=new ArrayList<OrderEntryLotnoModel>();//清空saveOrderEntryLotnoModels
		
		//再把tmpSaveOrderLineLotnos copy to saveOrderEntryLotnoModels
		if (tmpSaveOrderEntryLotnos.size()>0){
			for (int i=0;i<tmpSaveOrderEntryLotnos.size();i++){
				saveOrderEntryLotnoModels.add(tmpSaveOrderEntryLotnos.get(i));
			}
		}
		
		grdLotno.setModel(new ListModelList(saveOrderEntryLotnoModels));
		grdLotno.setItemRenderer(new OrderLotnoRender());
		
		//tmpSaveOrderEntryLotnos=new ArrayList<OrderEntryLotnoModel>();//清空tmpSaveOrderEntryLotnos
		
		//grdLotno.removeItemAt(grdLotno.getSelectedIndex());
		//tmporderEntryLotnoModels=new ArrayList<OrderEntryLotnoModel>();
		//tmporderEntryLotnoModels=(List<OrderEntryLotnoModel>) grdLotno.getModel();
	}
	
	/**
	 * 
	 * onClick$grdLine:依據User所點的grdLine，帶出相對應的grdLotno. <br/>
	 *
	 * @author 030260
	 * @since JDK 1.6
	 */
	public void onClick$grdLine(){
		//如果User沒有選取grdLine就離開
		if (grdLine.getSelectedIndex()<0){
			return;
		}
		
		//清空grdLotno
		this.clearListbox(grdLotno);
		
		//User選擇的Line為哪一筆
		List<OrderEntryLineModel> selOrderEntryLineModels=(List<OrderEntryLineModel>) grdLine.getModel();
		OrderEntryLineModel selOrderEntryLineModel=selOrderEntryLineModels.get(grdLine.getSelectedIndex());
		
		//清空tmpOrderLineLotnos
		tmporderEntryLotnoModels=new ArrayList<OrderEntryLotnoModel>();
		
		//將saveOrderEntryLotnoModels.POItem = selOrderEntryLineModel.POItem 放在tmpOrderLineLotnos
		if (saveOrderEntryLotnoModels.size()>0){
			for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
				if (saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem().trim().equals(selOrderEntryLineModel.getOrderLine().getPoItem().trim())){
					tmporderEntryLotnoModels.add(saveOrderEntryLotnoModels.get(i));
				}
			}
		}
		grdLotno.setModel(new ListModelList(tmporderEntryLotnoModels));
		grdLotno.setItemRenderer(new OrderLotnoRender());
		
	}
	
	/**
	 * 
	 * chkgrdLinePOItemcannotDuplicate:檢查grLine POItem不可以空白及重複. <br/>
	 *
	 * @author 030260
	 * @return
	 * @since JDK 1.6
	 */
	public boolean chkgrdLinePOItemcannotDuplicate(){
		log.debug("grdLine.itemcount="+grdLine.getItemCount());
		if (grdLine.getItemCount()>0){
			saveOrderEntryLineModels=(List<OrderEntryLineModel>) grdLine.getModel();
			for (int i=0;i<saveOrderEntryLineModels.size();i++){
				if ("".equals(saveOrderEntryLineModels.get(i).getOrderLine().getPoItem().trim())){
					this.showmessage("Warning", Labels.getLabel("oe.edit.line.chkPOItem.empty",
							new Object[] {Labels.getLabel("oe.edit.line.grbLineData"),
							              String.valueOf(i+1),
							              Labels.getLabel("oe.edit.line.grdLine.colLinePoItem")}));
					return false;
				}
				
				for (int j=i+1;j<saveOrderEntryLineModels.size();j++){
					if (saveOrderEntryLineModels.get(i).getOrderLine().getPoItem().trim().equals(saveOrderEntryLineModels.get(j).getOrderLine().getPoItem().trim())){
						this.showmessage("Warning", Labels.getLabel("oe.edit.line.chkPOItem.duplicate",new Object[] {Labels.getLabel("oe.edit.line.grbLineData"),
								String.valueOf(i+1),
								String.valueOf(j+1),
								Labels.getLabel("oe.edit.line.grdLine.colLinePoItem")}));
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 
	 * chkgrdLotnoCustomerLotnoDuplicate:檢查grdLotno POItem+CustomerLotno不可空白及重複. <br/>
	 *
	 * @author 030260
	 * @return
	 * @since JDK 1.6
	 */	
	public boolean chkgrdLotnoCustomerLotnoDuplicate(){
		if (saveOrderEntryLotnoModels.size()>0){
			for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
				if (("".equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno()))||
					("".equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem().trim()))){
					this.showmessage("Warning", Labels.getLabel("oe.edit.lotno.chkPOItemCustomerLotno.emtpy",
							new Object[] {Labels.getLabel("oe.edit.line.grbLotnoData"),
								          String.valueOf(i+1),
								          Labels.getLabel("oe.edit.line.grdLotno.colBatchPoItem"),
								          Labels.getLabel("oe.edit.line.grdLotno.colBatchCustomerLotno")}));
					return false;
				}
				
				for (int j=i+1;j<saveOrderEntryLotnoModels.size();j++){
					if ((saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem().trim().equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getPoItem().trim()))&&
						(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno().trim().equals(saveOrderEntryLotnoModels.get(j).getOrderLineLotno().getCustomerLotno().trim()))){
						this.showmessage("Warning", Labels.getLabel("oe.edit.lotno.chkPOItemCustomerLotno.duplicate",
								new Object[] {Labels.getLabel("oe.edit.line.grbLotnoData"),
											  Labels.getLabel("oe.edit.line.grdLotno.colBatchPoItem"),
								              saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem().trim(),
											  Labels.getLabel("oe.edit.line.grdLotno.colBatchCustomerLotno")}));
						return false;
					}
				}//end for j
			}//end for i
		}
		return true;
	}
	
	
	public boolean oeInternalCheck(){
		LinkedHashMap<String,String> errMsg;
		saveOrderInternalCheckInfoList.clear();
		//String errMsg="";
		
		boolean checkflag = false;
		//檢查Check 1. 
		errMsg = this.internalCheckRule1();
		if(!"".equals(errMsg.get("msg"))){
			//先跳出找到有重複資料的視窗
			Messagebox.show(Labels.getLabel("oe.save.message.checkRule1")+"\r\n"+errMsg.get("msg"), "Information", Messagebox.OK, Messagebox.INFORMATION);
			
			//再跳出要輸入Comment的視窗，若有輸入Comment才允許Save這筆OE
			Map args = new HashMap();
			args.put("errMsg", errMsg);
			args.put("saveOrderEntryLotnoModels", saveOrderEntryLotnoModels);
			args.put("orderInternalCheckInfoList", winOrderEntry.getAttribute("orderInternalCheckInfoList"));
			args.put("CheckType", "1");
			Window winOeInternalConfirm = (Window)Executions.createComponents("/WEB-INF/modules/oe/OeInternalConfirm.zul", null, args);
			winOeInternalConfirm.setParent(winOrderEntry);
			winOeInternalConfirm.doModal();
			
			saveOrderInternalCheckInfoList = (List<OrderInternalCheckInfo>) winOrderEntry.getAttribute("saveOrderInternalCheckInfoList");
			if(saveOrderInternalCheckInfoList.size() <= 0){//saveOrderInternalCheckInfoList.size()<=0，是按在出現OeInternal Confirm畫面按Exit，未輸入Comment繼續Save
				return false;
			}
		}
		
		//檢查Check 2.
		errMsg.clear();
		errMsg = this.internalCheckRule2();
		if(!"".equals(errMsg.get("msg"))){
			//先跳出找到有重複資料的視窗
			Messagebox.show(Labels.getLabel("oe.save.message.checkRule2")+"\r\n"+errMsg.get("msg"), "Information", Messagebox.OK, Messagebox.INFORMATION);
			
			//再跳出要輸入Comment的視窗，若有輸入Comment才允許Save這筆OE
			Map args = new HashMap();
			args.put("errMsg", errMsg);
			args.put("saveOrderEntryLotnoModels", saveOrderEntryLotnoModels);
			args.put("orderInternalCheckInfoList", winOrderEntry.getAttribute("orderInternalCheckInfoList"));
			args.put("CheckType", "2");
			Window winOeInternalConfirm = (Window)Executions.createComponents("/WEB-INF/modules/oe/OeInternalConfirm.zul", null, args);
			winOeInternalConfirm.setParent(winOrderEntry);
			winOeInternalConfirm.doModal();
			
			saveOrderInternalCheckInfoList = (List<OrderInternalCheckInfo>) winOrderEntry.getAttribute("saveOrderInternalCheckInfoList");
			if(saveOrderInternalCheckInfoList.size() <= 0){//saveOrderInternalCheckInfoList.size()<=0，是按在出現OeInternal Confirm畫面按Exit，未輸入Comment繼續Save，且沒有發生Check1 & 2的Alarm
				return false;
			}else{
				boolean checkFlag=false;
				for(int i=0; i<saveOrderInternalCheckInfoList.size(); i++){
					if(saveOrderInternalCheckInfoList.get(i).getOeCheck2ConfirmReason() != null && !"".equals(saveOrderInternalCheckInfoList.get(i).getOeCheck2ConfirmReason())){
						checkFlag=true;
						break;
					}
				}
				if(!checkFlag){
					return false;
				}
			}
		}
		
		//檢查Check 3.
		if(oeReworkCountSetupList.size() > 0){//若oeReworkCountSetupList.size() > 0，代表此客戶有設定Rework次數，才需要做Check3的檢查
			errMsg.clear();
			errMsg = this.internalCheckRule3();
			if(!"".equals(errMsg.get("msg"))){
				//先跳出找到有重複資料的視窗
				Messagebox.show(Labels.getLabel("oe.save.message.checkRule3")+"\r\n"+errMsg.get("msg"), "Information", Messagebox.OK, Messagebox.INFORMATION);
				
				//再跳出要輸入Comment的視窗，若有輸入Comment才允許Save這筆OE
				Map args = new HashMap();
				args.put("errMsg", errMsg);
				args.put("saveOrderEntryLotnoModels", saveOrderEntryLotnoModels);
				args.put("orderInternalCheckInfoList", winOrderEntry.getAttribute("orderInternalCheckInfoList"));
				args.put("CheckType", "3");
				Window winOeInternalConfirm = (Window)Executions.createComponents("/WEB-INF/modules/oe/OeInternalConfirm.zul", null, args);
				winOeInternalConfirm.setParent(winOrderEntry);
				winOeInternalConfirm.doModal();
				
				saveOrderInternalCheckInfoList = (List<OrderInternalCheckInfo>) winOrderEntry.getAttribute("saveOrderInternalCheckInfoList");
				if(saveOrderInternalCheckInfoList.size() <= 0){//saveOrderInternalCheckInfoList.size()<=0，是按在出現OeInternal Confirm畫面按Exit，未輸入Comment繼續Save，且沒有發生Check1 & 2的Alarm
					return false;
				}else{
					boolean checkFlag=false;
					for(int i=0; i<saveOrderInternalCheckInfoList.size(); i++){
						if(saveOrderInternalCheckInfoList.get(i).getOeCheck3ConfirmReason() != null && !"".equals(saveOrderInternalCheckInfoList.get(i).getOeCheck3ConfirmReason())){
							checkFlag=true;
							break;
						}
					}
					if(checkFlag){
						return true;
					}else{
						return false;
					}
				}
			}
		}
		//檢查Check 4.
				errMsg.clear();
				errMsg = this.internalCheckRule4();
				if(!"".equals(errMsg.get("msg"))){
					Messagebox.show(Labels.getLabel("oe.save.message.checkRule4")+"\r\n"+errMsg.get("msg"), "Information", Messagebox.OK, Messagebox.INFORMATION);
					return false;
				}		
		
		return true;	
	}
	
	//OCF-PR-160702_以下程式WaferStatusMaintenanceOperationViewCtrl也有，若有修改Rule需一起修改
	public LinkedHashMap<String,String> internalCheckRule1(){
		String msg="";
		String errLotNoIdx="";

		if(saveOrderEntryLotnoModels.size() > 0){
			for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
				String customerLotNo=saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno();
				String waferData=saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData();

				//1. 先到[LOT_INFO]、[LOT_RESULT]、[ORDER_HEADER]、[ORDER_LINE_LOTNO]、[ORDER_LINE]、[WAFER_INFO]找是否有該CUSTOMER_LOTNO，再比對搜尋出來的WAFER_NO是否有重覆到
				List<LinkedHashMap<String, String>> list = waferBankinService.getOeWithOrderStatusByCustomerLotNo(customerLotNo);

				if(list.size() > 0){
					String[] splitWaferData = waferData.split(";");
					for(int j=list.size()-1; j>=0; j--){
						boolean checkflag=false;
						String[] splitOeWaferData = list.get(j).get("WAFER_DATA").split(";");
						for(int l=0; l<splitOeWaferData.length; l++){
							for(int k=0; k<splitWaferData.length; k++){
								if(String.valueOf(splitWaferData[k]).equals(String.valueOf(splitOeWaferData[l]))){
									checkflag=true;
									break;
								}
							}
							if(checkflag){
								break;
							}
						}
						if(!checkflag){
							list.remove(j);
						}
					}
				}
				
				if(list.size() > 0){
					for(int j=0; j<list.size(); j++){
						List<LinkedHashMap<String, String>> list2 = waferBankinService.getOeLotInfoByOrderNumber(list.get(j).get("PO_NUMBER"));
						
						if(list2.size() > 0){
							String[] splitWaferData = waferData.split(";");
							for(int k=0; k<splitWaferData.length; k++){
								String waferNo="";
								if(Integer.valueOf(splitWaferData[k]) < 10){
									waferNo = "0" + splitWaferData[k];
								}else{
									waferNo = splitWaferData[k];
								}
								for(int l=0; l<list2.size(); l++){
									if(!"true".equals(list2.get(l).get("LOTRESULT_SHIPPING_FLAG")) && !"true".equals(list2.get(l).get("LOT_SHIPPING_FLAG"))){
										if((customerLotNo+"-"+waferNo).equals(list2.get(l).get("WAFER_NO"))){
											msg = msg + customerLotNo + " / " + splitWaferData[k] + " / " + list2.get(l).get("PO_NUMBER") + "\r\n";
											if("".equals(errLotNoIdx)){
												errLotNoIdx = errLotNoIdx + i + ";";
											}else{
												String[] spilitErrLotNoIdx = errLotNoIdx.split(";");
												boolean flag=false;
												for(int n=0; n<spilitErrLotNoIdx.length; n++){
													if(String.valueOf(spilitErrLotNoIdx[n]).equals(String.valueOf(i))){
														flag=true;
													}
												}
												if(!flag){
													errLotNoIdx = errLotNoIdx + i + ";";
												}
											}
											break;
										}
									}
								}
							}
						}else{
							String[] splitWaferData = waferData.split(";");
							//如果在OFFLOAD SHIPPING有找到，則代表有OFFLOAD SHIPPING，則要先比對與[OFFLOAD_SHIPPING].WAFER_DATA是否有相符的WAFER_DATA, 若無相符的WAFER_DATA，再比對OE的WAFER_DATA(因OFFLOAD SHIPPING會分次出貨)
							List<LinkedHashMap<String, String>> list3 = waferBankinService.getOeByCustomerLotNo(list.get(j).get("PO_NUMBER"));
							if(list3.size() > 0){
								for(int k=0; k<list3.size(); k++){
									if(list3.get(k).get("OFFLOAD_SHIPPING_WAFER_DATA") == null){//如果[OFFLOAD_SHIPPING].WAFER_DATA是NULL，代表沒有在OFFLOAD SHIPPING，因此只要比對OE的WAFER_DATA
										for(int l=0; l<splitWaferData.length; l++){
											String[] oeWaferDataSpilit = list3.get(k).get("WAFER_DATA").split(";");
											if(oeWaferDataSpilit.length > 0){
												for(int t=0; t<oeWaferDataSpilit.length; t++){
													if(String.valueOf(splitWaferData[l]).equals(String.valueOf(oeWaferDataSpilit[t]))){
														msg = msg + customerLotNo + " / " + splitWaferData[l] + " / " + list3.get(k).get("PO_NUMBER") + "\r\n";
														if("".equals(errLotNoIdx)){
															errLotNoIdx = errLotNoIdx + i + ";";
														}else{
															String[] spilitErrLotNoIdx = errLotNoIdx.split(";");
															boolean flag=false;
															for(int n=0; n<spilitErrLotNoIdx.length; n++){
																if(String.valueOf(spilitErrLotNoIdx[n]).equals(String.valueOf(i))){
																	flag=true;
																}
															}
															if(!flag){
																errLotNoIdx = errLotNoIdx + i + ";";
															}
														}
														break;
													}
												}
											}
										}
									}else{
										//如果在OFFLOAD SHIPPING有找到，則代表有OFFLOAD SHIPPING，則要先比對與[OFFLOAD_SHIPPING].WAFER_DATA是否有相符的WAFER_DATA, 若無相符的WAFER_DATA，再比對OE的WAFER_DATA(因OFFLOAD SHIPPING會分次出貨)
										String[] offloadWaferDataSpilit = list3.get(k).get("OFFLOAD_SHIPPING_WAFER_DATA").split(";");
										if(offloadWaferDataSpilit.length > 0){
											boolean checkflag=false;
											for(int l=0; l<offloadWaferDataSpilit.length; l++){
												for(int t=0; t<splitWaferData.length; t++){
													if(String.valueOf(splitWaferData[t]).equals(String.valueOf(offloadWaferDataSpilit[l]))){
														checkflag=true;
														break;
													}
												}
											}
											if(!checkflag){
												String[] oeWaferDataSpilit = list3.get(k).get("WAFER_DATA").split(";");
												if(oeWaferDataSpilit.length > 0){
													for(int l=0; l<oeWaferDataSpilit.length; l++){
														for(int t=0; t<splitWaferData.length; t++){
															if(String.valueOf(splitWaferData[t]).equals(String.valueOf(oeWaferDataSpilit[l]))){
																msg = msg + customerLotNo + " / " + splitWaferData[t] + " / " + list3.get(k).get("PO_NUMBER") + "\r\n";
																if("".equals(errLotNoIdx)){
																	errLotNoIdx = errLotNoIdx + i + ";";
																}else{
																	String[] spilitErrLotNoIdx = errLotNoIdx.split(";");
																	boolean flag=false;
																	for(int n=0; n<spilitErrLotNoIdx.length; n++){
																		if(String.valueOf(spilitErrLotNoIdx[n]).equals(String.valueOf(i))){
																			flag=true;
																		}
																	}
																	if(!flag){
																		errLotNoIdx = errLotNoIdx + i + ";";
																	}
																}
																break;
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
					}
				}
			}
		}
		
		LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
		data.put("msg", msg);
		data.put("errLotNoIdx", errLotNoIdx);
		
		return data;
	}
	
	
	public LinkedHashMap<String,String> internalCheckRule2(){
		String msg="";
		String errLotNoIdx="";

		if(saveOrderEntryLotnoModels.size() > 0){
			for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
				String customerLotNo=saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno();
				String waferData=saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData();

				List<LinkedHashMap<String, String>> list = waferBankinService.getOeDataByCustomerLotNo(customerLotNo);

				if(list.size() > 0){
					String[] splitWaferData = waferData.split(";");
					for(int k=0; k<splitWaferData.length; k++){
						for(int j=0; j<list.size(); j++){
							List<OrderInternalCheckInfo> orderInternalCheckInfoList = orderEntryService.getOrderInternalCheckInfoByOrderLineLotNoIdx(Integer.valueOf(list.get(j).get("ORDER_LINE_LOTNO_IDX")));
							if(orderInternalCheckInfoList.size() > 0){
								if(list.get(j).get("WAFER_DATA") != null){
									String[] oeWaferData = list.get(j).get("WAFER_DATA").split(";");
									for(int n=0; n<oeWaferData.length; n++){
										if(String.valueOf(splitWaferData[k]).equals(String.valueOf(oeWaferData[n]))){
											for(int l=0; l<orderInternalCheckInfoList.size(); l++){
												if(orderInternalCheckInfoList.get(l).getOeNoticeWaferData() != null && !"".equals(orderInternalCheckInfoList.get(l).getOeNoticeWaferData())){
													boolean flag=false;
													String[] noticeWaferData = 	orderInternalCheckInfoList.get(l).getOeNoticeWaferData().split(";");
													for(int m=0; m<noticeWaferData.length; m++){
														if(String.valueOf(noticeWaferData[m]).equals(String.valueOf(splitWaferData[k]))){
															msg = msg + customerLotNo + " / " + splitWaferData[k] + " / (" + list.get(j).get("PO_NUMBER") + ")" + orderInternalCheckInfoList.get(l).getOeNotice() + "\r\n";
															flag=true;
															break;
														}
													}
													if(flag){
														break;
													}
												}
											}
										}
									}
								}
							}
						}
					}
					if(!"".equals(msg)){
						errLotNoIdx = errLotNoIdx + i + ";";
					}
				}
			}
		}
		
		LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
		data.put("msg", msg);
		data.put("errLotNoIdx", errLotNoIdx);
		
		return data;
	}
	
	
	public LinkedHashMap<String,String> internalCheckRule3(){
		String msg="";
		String errLotNoIdx="";

		if(saveOrderEntryLotnoModels.size() > 0){
			for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
				String customerLotNo=saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno();
				String waferData=saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData();

				List<LinkedHashMap<String, String>> list = waferBankinService.getShippingWaferInfoByCustomerLotNo(customerLotNo);

				if(list.size() > 0){
					String[] splitWaferData = waferData.split(";");
					for(int k=0; k<splitWaferData.length; k++){
						int waferCount=0;
						String waferNo="";
						if(Integer.valueOf(splitWaferData[k]) < 10){
							waferNo = "0" + splitWaferData[k];
						}else{
							waferNo = splitWaferData[k];
						}
						for(int j=0; j<list.size(); j++){
							if((customerLotNo+"-"+waferNo).equals(list.get(j).get("WAFER_NO"))){
								waferCount = waferCount + 1;
							}
						}
						
						//若該CUSTOMER+WAFER_NO的出貨次數大於PC設定的REWORK次數，則要Alarm
						if(waferCount >= Integer.valueOf(oeReworkCountSetupList.get(0).getReworkCount())){
							msg = msg + customerLotNo + " / " + splitWaferData[k] + " / " + waferCount + " / " +  oeReworkCountSetupList.get(0).getReworkCount() +  "\r\n";
						}	
					}
					if(!"".equals(msg)){
						errLotNoIdx = errLotNoIdx + i + ";";
					}
				}
			}
		}
		
		LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
		data.put("msg", msg);
		data.put("errLotNoIdx", errLotNoIdx);
		
		return data;
	}
	public LinkedHashMap<String,String> internalCheckRule4(){
		String msg="";
		String errLotNoIdx="";
		final String paraTypeString = "OE_DESIGNID_CHECK_CUSTOMER";
		List<UiFieldSet> lUiFieldSets = orderEntryService.getUiFieldSet("com.tce.ivision.modules.oe.ctrl.OrderEntryViewCtrl",paraTypeString);
	
		if(saveOrderEntryLineModels.size() > 0){
			for(int i=0; i<saveOrderEntryLineModels.size(); i++){						
				OrderEntryLineModel tmpEntryLineModel = saveOrderEntryLineModels.get(i);
				//2.如OE中的Design ID欄位為10或空白,則不需判斷此規則
				if(BeanUtil.isEmpty(tmpEntryLineModel.getOrderLine().getDesignId())||"10".equals(tmpEntryLineModel.getOrderLine().getDesignId())){
					continue;
				}
				//Customer為OMNI
				//4.Product name的判斷規則如下 :
				// a.XXXX-Design ID-XXX  ex :OV13855-TP31-Bonder1(比對兩個dash中間 color code是否與Design ID 相符.)
				//b.XXXX-Design ID      ex :OV5675-TC05 (比對dash後的color code是否與Design ID 相符)
				for (UiFieldSet uiFieldSet : lUiFieldSets) {
					for (UiFieldParam uiFieldParam : uiFieldSet.getUiFieldParams()) {
						if(uiFieldParam.isEnabled() && uiFieldParam.getParaValue().equals(cbxCustomer.getValue().trim())){
							String[] aryProduct = edtProduct.getValue().split("-");
							if(!tmpEntryLineModel.getOrderLine().getDesignId().equals(aryProduct[1])){
								msg += "Order Header [ "+edtProduct.getValue()+" ] is not matching \r\n";
								msg += "Order Line  [ "+tmpEntryLineModel.getOrderLine().getDesignId()+" ]  \r\n";
								errLotNoIdx = errLotNoIdx + i + ";";
							}
						   }
						}
				 }
				}
			   /*if(msg.length()>0){
				   msg = "Save Error [ OMNI’S Color Code Check ]\r\n"+msg;
			   }*/
		}
		LinkedHashMap<String,String> data = new LinkedHashMap<String,String>();
		data.put("msg", msg);
		data.put("errLotNoIdx", errLotNoIdx);
		return data;
	}
	public String checkOtherInput(){
		List<OrderEntryLotnoModel> tmporderEntryLotnoModels=new ArrayList<OrderEntryLotnoModel>();
		tmporderEntryLotnoModels = (List<OrderEntryLotnoModel>) grdLotno.getModel();
		
		String checkflag="";
		if(saveOrderEntryLotnoModels.size() > 0){
			for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
				if("".equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData()) || saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData() == null){
					checkflag="1";
					break;
				}else{
					String[] splitWaferNo = saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData().split(";");
					if(Integer.valueOf(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty()) != null){
						if(Integer.valueOf(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty()) != splitWaferNo.length){
							checkflag = checkflag + saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno() + " / " + String.valueOf(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferQty()) + " / " + saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData() + "\r\n";
						}
					}
				}
			}
		}
		
		return checkflag;
	}
	
	
	
	public boolean checkModifyCustomerAndWaferData(){
		boolean checkflag=false;
		List<OrderLineLotno> orderLineLotnos = orderEntryService.getOrderLineLotnosByOrderNumber(saveOrderEntryLotnoModels.get(0).getOrderLineLotno().getOrderNumber());
		
		if(saveOrderEntryLotnoModels.size() > 0){
			for(int i=0; i<saveOrderEntryLotnoModels.size(); i++){
				checkflag=false;
				if(orderLineLotnos.size() > 0){
					for(int j=0; j<orderLineLotnos.size(); j++){
						if(orderLineLotnos.get(j).getCustomerLotno().equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno())){
							checkflag=true;
							break;
						}
					}
				}
				
				if(checkflag){
					boolean checkWaferDataFlag=false;
					if(orderLineLotnos.size() > 0){
						for(int j=0; j<orderLineLotnos.size(); j++){
							if(orderLineLotnos.get(j).getCustomerLotno().equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getCustomerLotno())){
								if(orderLineLotnos.get(j).getWaferData().equals(saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getWaferData())){
									checkWaferDataFlag=true;
									break;
								}
							}
						}
						if(!checkWaferDataFlag){
							checkflag=false;
						}
					}
				}
				
				if(!checkflag){
					break;
				}
			}
		}
		
		LinkedHashMap<String,String> mapErr= this.internalCheckRule4();
		if(!"".equals(mapErr.get("msg"))){
			Messagebox.show(Labels.getLabel("oe.save.message.checkRule4")+"\r\n"+mapErr.get("msg"), "Information", Messagebox.OK, Messagebox.INFORMATION);
			return false;
		}
		
		return checkflag;
	}
	

	
	/**
	 * 
	 * setMustbeInput:設定grdLine哪些欄位是Mustbe,將其變為淺黃色的底色及設定Constraint. <br/>
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
		//log.debug(this.getClass().getName());
		List<BaseCtrlSet> baseCtrlSetList = commonService.getBaseCtrlSetLists(this.getClass().getName());
		List<Component> headerlists = inItem.getFellowIfAny("listheadLine").getChildren();
		
		List<String> headers = new ArrayList<String>();
		for (int i = 0; i < headerlists.size(); i++) {
			headers.add(headerlists.get(i).getId());
		}
		
		List<String> bases = new ArrayList<String>();
		for (int i = 0; i < baseCtrlSetList.size(); i++) {
			bases.add(baseCtrlSetList.get(i).getBeanColumnName());
		}

		bases.retainAll(headers);
		
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
//							System.out.println(cellchildrens.get(k).getId()+"++++++");
//							System.out.println(baseCtrlSetList.get(i).getComponentName()+"......");
//							System.out.println(cellchildrens.get(k).getClass().getName()+"-------");
//							if (cellchildrens.get(k).getId().contains("SalesPartID")){
//								Class.forName(cellchildrens.get(k).getClass().getName())
//								.getMethod("",new Class[] { String.class })
//								.invoke(cellchildrens.get(k),new Object[] { "readonly='readonly'" });
//							}
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

	/**
	 * grdLine的Render.
	 * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
	 */
	@Override
	public void render(Listitem inItem, Object inData, int inIndex) throws Exception {
		final OrderEntryLineModel smf = (OrderEntryLineModel) inData;
		
		//宣告Listcell
		Listcell celllineNo = new Listcell();
		Listcell cellLinePoItem = new Listcell();
		Listcell cellLineMtrlDesc = new Listcell();
		Listcell cellLineMtrlGroup = new Listcell();
		Listcell cellLineWaferQty = new Listcell();
		Listcell cellLineWaferSupplyDate = new Listcell();//2013.02.08
		Listcell cellLineRequestDate = new Listcell();
		Listcell cellLineWaferSize = new Listcell();
		Listcell cellLineDesignId = new Listcell();
		Listcell cellLineCurrency = new Listcell();
		Listcell cellLineUnitPrice = new Listcell();
		Listcell cellLineSellingPrice = new Listcell();
		Listcell cellLineCfaSite = new Listcell();//2013.01.15
		Listcell cellLineShipToVendorName = new Listcell();
		Listcell cellLineShippingDestination = new Listcell();//OCF-PR-160602 add
		Listcell cellLineShipComment = new Listcell();
		Listcell cellLineCountryOfFab = new Listcell();
		Listcell cellLineFab = new Listcell();
		Listcell cellLineCfaProId = new Listcell();
		Listcell cellLineShipToVendorCode = new Listcell();
		Listcell cellLineCompanyCode = new Listcell();
		Listcell cellLineMtrlNum = new Listcell();
		Listcell cellLineSourceMtrlNum = new Listcell();
		Listcell cellLineSubName = new Listcell();
		Listcell cellLineStage = new Listcell();
		Listcell cellLineOperationDescription = new Listcell();
		Listcell cellLineSalesPartID = new Listcell();//add OCF-PR-180402 by Peter 
		
		
		//設定物件的id
		colLinePoItem.setId("colLinePoItem"+inIndex);
		colLineWaferSize.setId("colLineWaferSize"+inIndex);
		cellLineShipToVendorName.setId("cellLineShipToVendorName"+inIndex);
		cellLineShipToVendorCode.setId("cellLineShipToVendorCode"+inIndex);
		
		//先宣告每一個Listcell所對應的物件(TextBox,Combobox...)
		final Textbox edtcellPoItem = new Textbox();
		final Textbox edtcellMtrlDesc = new Textbox();
		final Textbox edtcellMtrlGroup = new Textbox();
		final Spinner edtcellWaferQty = new Spinner();
		final Datebox edtcellWaferSupplyDate = new Datebox();//2013.02.08
		final Datebox edtcellRequestDate = new Datebox();
		final Combobox cbxcellWaferSize = new Combobox();
		final Textbox edtcellDesignId = new Textbox();
		final Combobox cbxcellCurrency = new Combobox();
		final Textbox edtcellUnitPrice = new Textbox();
		final Textbox edtcellSellingPrice = new Textbox();
		final Combobox cbxcellCfaSite = new Combobox();//2013.01.15
		final Textbox edtcellShipToVendorName = new Textbox();//2013.06.26
		final Textbox edtcellShipComment = new Textbox();
		final Textbox edtcellCountryOfFab = new Textbox();
		final Textbox edtcellFab = new Textbox();
		final Textbox edtcellCfaProId = new Textbox();
		final Textbox edtcellShipToVendorCode = new Textbox();
		//final Textbox edtcellShippingDestination = new Textbox();//OCF-PR-160602 add
		final Combobox cbxShippingDestination = new Combobox();//OCF-PR-160702_修改為Combobox
		final Textbox edtcellCompanyCode = new Textbox();
		final Textbox edtcellMtrlNum = new Textbox();
		final Textbox edtcellSourceMtrlNum = new Textbox();
		final Textbox edtcellSubName = new Textbox();
		final Textbox edtcellStage = new Textbox();
		final Textbox edtcellOperationDescription = new Textbox();
		final Textbox edtcellLineSalesPartID = new Textbox();//add OCF-PR-180402 by Peter
		
		//設定所對應物件的屬性
			//PoItem
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getPoItem()) || smf.getOrderLine().getPoItem() == null){
					cellLinePoItem.setLabel("");
				}else{
					cellLinePoItem.setLabel(smf.getOrderLine().getPoItem().trim());
				}
			}
			else{
				edtcellPoItem.setId("edtcellPoItem"+inIndex);
				edtcellPoItem.setInplace(true);
				edtcellPoItem.setWidth("90%");
				edtcellPoItem.setMaxlength(poItemLen);
				if("".equals(smf.getOrderLine().getPoItem()) || smf.getOrderLine().getPoItem() == null){
					edtcellPoItem.setText("");
				}else{
					edtcellPoItem.setText(smf.getOrderLine().getPoItem().trim());
				}
				edtcellPoItem.setParent(cellLinePoItem);
				edtcellPoItem.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						String beforePoItem=smf.getOrderLine().getPoItem().trim();
						smf.getOrderLine().setPoItem(edtcellPoItem.getText().trim());
						if (saveOrderEntryLotnoModels.size()>0){
							for (int i=0;i<saveOrderEntryLotnoModels.size();i++){
								if (saveOrderEntryLotnoModels.get(i).getOrderLineLotno().getPoItem().trim().equals(beforePoItem)){
									saveOrderEntryLotnoModels.get(i).getOrderLineLotno().setPoItem(edtcellPoItem.getText().trim());
								}
							}
						}
						
					}
				});
			}
			
			//MtrlDesc
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getMtrlDesc()) || smf.getOrderLine().getMtrlDesc() == null){
					cellLineMtrlDesc.setLabel("");
				}else{
					cellLineMtrlDesc.setLabel(smf.getOrderLine().getMtrlDesc().trim());
				}
			}
			else{
				edtcellMtrlDesc.setId("edtcellMtrlDesc"+inIndex);
				edtcellMtrlDesc.setInplace(true);
				edtcellMtrlDesc.setWidth("100px");
				if("".equals(smf.getOrderLine().getMtrlDesc()) || smf.getOrderLine().getMtrlDesc() == null){
					edtcellMtrlDesc.setText("");
				}else{
					edtcellMtrlDesc.setText(smf.getOrderLine().getMtrlDesc().trim());
				}
				edtcellMtrlDesc.setParent(cellLineMtrlDesc);
				edtcellMtrlDesc.addEventListener("onChange", new EventListener(){
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setMtrlDesc(edtcellMtrlDesc.getText().trim());
					}
				});
			}
			
			//MtrlGroup
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getMtrlNumMtrlgrp()) || smf.getOrderLine().getMtrlNumMtrlgrp() == null){
					cellLineMtrlGroup.setLabel("");
				}else{
					cellLineMtrlGroup.setLabel(smf.getOrderLine().getMtrlNumMtrlgrp().trim());
				}
			}
			else{
				edtcellMtrlGroup.setId("edtcellMtrlGroup"+inIndex);
				edtcellMtrlGroup.setInplace(true);
				edtcellMtrlGroup.setWidth("90%");
				if("".equals(smf.getOrderLine().getMtrlNumMtrlgrp()) || smf.getOrderLine().getMtrlNumMtrlgrp() == null){
					edtcellMtrlGroup.setText("");
				}else{
					edtcellMtrlGroup.setText(smf.getOrderLine().getMtrlNumMtrlgrp().trim());
				}
				edtcellMtrlGroup.setParent(cellLineMtrlGroup);
				edtcellMtrlGroup.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setMtrlNumMtrlgrp(edtcellMtrlGroup.getText());
					}
				});
			}
			
			//WaferQty
			if ("readonly".equals(mode)){
				cellLineWaferQty.setLabel(String.valueOf(smf.getOrderLine().getWaferQty()).toString().trim());
			}
			else{
				edtcellWaferQty.setId("edtcellWaferQty"+inIndex);
				edtcellWaferQty.setInplace(true);
				edtcellWaferQty.setWidth("60px");
				edtcellWaferQty.setText(String.valueOf(smf.getOrderLine().getWaferQty()).toString().trim());
				edtcellWaferQty.setParent(cellLineWaferQty);
				edtcellWaferQty.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						if ("".equals(edtcellWaferQty.getText())){
							smf.getOrderLine().setWaferQty(0);
							smf.getOrderLine().setSellingPrice(0.0);
							edtcellSellingPrice.setText("0.0");
						}
						else{
							smf.getOrderLine().setWaferQty(Integer.valueOf(edtcellWaferQty.getText()));
							
							if (smf.getOrderLine().getUnitPrice().equals(0.0)){
								smf.getOrderLine().setSellingPrice(0.0);
								edtcellSellingPrice.setText("0.0");
							}
							else{
								smf.getOrderLine().setSellingPrice(Double.valueOf(smf.getOrderLine().getUnitPrice() * smf.getOrderLine().getWaferQty()));
								edtcellSellingPrice.setText(String.valueOf(smf.getOrderLine().getSellingPrice()).toString().trim());
							}
						}
					}
				});
			}
			
			//2013.02.08 Wafer Supply Date
			if ("readonly".equals(mode)){
				if (smf.getOrderLine().getWaferSupplyDate()!=null){
					cellLineWaferSupplyDate.setLabel(sdf.format(smf.getOrderLine().getWaferSupplyDate()).trim());
				}
				else{
					cellLineWaferSupplyDate.setLabel("");
				}
			}
			else{
				edtcellWaferSupplyDate.setId("edtcellWaferSupplyDate"+inIndex);
				edtcellWaferSupplyDate.setInplace(true);
				edtcellWaferSupplyDate.setWidth("90%");
				edtcellWaferSupplyDate.setFormat("yyyy/MM/dd HH:mm:ss");
				edtcellWaferSupplyDate.setValue(smf.getOrderLine().getWaferSupplyDate());
				edtcellWaferSupplyDate.setParent(cellLineWaferSupplyDate);
				edtcellWaferSupplyDate.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						log.debug(dateFormatYear2Min.format(edtcellWaferSupplyDate.getValue()));
						smf.getOrderLine().setWaferSupplyDate(edtcellWaferSupplyDate.getValue());
					}
				});
			}
			
			//RequestDate
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getRequestDate()) || smf.getOrderLine().getRequestDate() == null){
					cellLineRequestDate.setLabel("");
				}else{
					cellLineRequestDate.setLabel(smf.getOrderLine().getRequestDate().trim());
				}
			}
			else {
				edtcellRequestDate.setId("edtcellRequestDate"+inIndex);
				edtcellRequestDate.setInplace(true);
				edtcellRequestDate.setWidth("90%");
				edtcellRequestDate.setFormat("yyyy/MM/dd HH:mm:ss");
				if("".equals(smf.getOrderLine().getRequestDate()) || smf.getOrderLine().getRequestDate() == null){
					edtcellRequestDate.setText("");
				}else{
					if(smf.getOrderLine().getRequestDate().length()<=10){
						edtcellRequestDate.setText(smf.getOrderLine().getRequestDate().trim()+" 00:00:00");
					}else{
						edtcellRequestDate.setText(smf.getOrderLine().getRequestDate().trim());
					}
				}
				edtcellRequestDate.setParent(cellLineRequestDate);
				edtcellRequestDate.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setRequestDate(edtcellRequestDate.getText());
					}
				});
			}
			
			//WaferSize
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getWaferSize()) || smf.getOrderLine().getWaferSize() == null){
					cellLineWaferSize.setLabel("");
				}else{
					cellLineWaferSize.setLabel(smf.getOrderLine().getWaferSize().trim());
				}
				//cellLineWaferSize.setLabel(this.getParaValueByMeaning(smf.getOrderLine().getWaferSize(), waferSizeUiFieldParams));
			}
			else{
				ZkComboboxControl.setComboboxItems(cbxcellWaferSize, waferSizeUiFieldParams, "getParaValue","isEnabled",true);
				cbxcellWaferSize.setId("cbxcellWaferSize"+inIndex);
				cbxcellWaferSize.setInplace(true);
				cbxcellWaferSize.setWidth("60px");
				if("".equals(smf.getOrderLine().getWaferSize()) || smf.getOrderLine().getWaferSize() == null){
					cbxcellWaferSize.setText("");
				}else{
					cbxcellWaferSize.setText(smf.getOrderLine().getWaferSize().trim());
				}
				//cbxcellWaferSize.setText(this.getParaValueByMeaning(smf.getOrderLine().getWaferSize(), waferSizeUiFieldParams));
				cbxcellWaferSize.setParent(cellLineWaferSize);
				cbxcellWaferSize.addEventListener("onSelect", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setWaferSize(cbxcellWaferSize.getText());//先暫時存ParaValue等真正儲存的時間再轉成meaning
					}
				});
			}
			
			
			//Design ID
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getDesignId()) || smf.getOrderLine().getDesignId() == null){
					cellLineDesignId.setLabel("");
				}else{
					cellLineDesignId.setLabel(smf.getOrderLine().getDesignId().trim());
				}
			}
			else{
				edtcellDesignId.setId("edtcellDesignId"+inIndex);
				edtcellDesignId.setInplace(true);
				edtcellDesignId.setWidth("90%");
				if("".equals(smf.getOrderLine().getDesignId()) || smf.getOrderLine().getDesignId() == null){
					edtcellDesignId.setText("");
				}else{
					edtcellDesignId.setText(smf.getOrderLine().getDesignId().trim());
				}
				edtcellDesignId.setParent(cellLineDesignId);
				edtcellDesignId.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setDesignId(edtcellDesignId.getText());
					}
				});
			}
			
			//Currency
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getCurrency()) || smf.getOrderLine().getCurrency() == null){
					cellLineCurrency.setLabel("");
				}else{
					cellLineCurrency.setLabel(smf.getOrderLine().getCurrency().trim());	
				}
			}
			else{
				ZkComboboxControl.setComboboxItems(cbxcellCurrency, currencyUiFieldParams, "getParaValue","isEnabled",true);
				cbxcellCurrency.setId("cbxcellCurrency"+inIndex);
				cbxcellCurrency.setInplace(true);
				cbxcellCurrency.setWidth("50px");
				if("".equals(smf.getOrderLine().getCurrency()) || smf.getOrderLine().getCurrency() == null){
					cbxcellCurrency.setText("");
				}else{
					cbxcellCurrency.setText(smf.getOrderLine().getCurrency().trim());
				}
				cbxcellCurrency.setParent(cellLineCurrency);
				cbxcellCurrency.addEventListener("onSelect", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setCurrency(cbxcellCurrency.getText());//先暫時存ParaValue等真正儲存的時間再轉成meaning
					}
				});
			}
			
			
			//Unit Price
			if ("readonly".equals(mode)){
				cellLineUnitPrice.setLabel(String.valueOf(smf.getOrderLine().getUnitPrice()).trim());
			}
			else{
				edtcellUnitPrice.setId("edtcellUnitPrice"+inIndex);
				edtcellUnitPrice.setInplace(true);
				edtcellUnitPrice.setWidth("90%");
				edtcellUnitPrice.setText(String.valueOf(smf.getOrderLine().getUnitPrice()).toString().trim());
				edtcellUnitPrice.setParent(cellLineUnitPrice);
				edtcellUnitPrice.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						if ("".equals(edtcellUnitPrice.getText())){
							smf.getOrderLine().setUnitPrice(0.0);
							smf.getOrderLine().setSellingPrice(0.0);
							edtcellSellingPrice.setText("0.0");
						}
						else{
							smf.getOrderLine().setUnitPrice(Double.valueOf(edtcellUnitPrice.getText()));
							if (smf.getOrderLine().getWaferQty()!=0){
								smf.getOrderLine().setSellingPrice(Double.valueOf(smf.getOrderLine().getUnitPrice() * smf.getOrderLine().getWaferQty()));
								edtcellSellingPrice.setText(String.valueOf(smf.getOrderLine().getSellingPrice().toString().trim()));
							}
							else{
								smf.getOrderLine().setSellingPrice(0.0);
								edtcellSellingPrice.setText("0.0");
							}
						}
					}
				});
			}
			
			//SellingPrice
			if ("readonly".equals(mode)){
				cellLineSellingPrice.setLabel(String.valueOf(smf.getOrderLine().getSellingPrice()).trim());
			}
			else{
				edtcellSellingPrice.setId("edtcellSellingPrice"+inIndex);
				edtcellSellingPrice.setInplace(true);
				edtcellSellingPrice.setWidth("90%");
				edtcellSellingPrice.setText(String.valueOf(smf.getOrderLine().getSellingPrice().toString().trim()));
				edtcellSellingPrice.setParent(cellLineSellingPrice);
				edtcellSellingPrice.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setSellingPrice(Double.valueOf(edtcellSellingPrice.getText()));
					}
				});
			}
			
			//CfaSite 2013.01.15
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getCfaSite()) || smf.getOrderLine().getCfaSite() == null){
					cellLineCfaSite.setLabel("");
				}else{
					cellLineCfaSite.setLabel(smf.getOrderLine().getCfaSite().trim());
				}
			}
			else{
				ZkComboboxControl.setComboboxItems(cbxcellCfaSite, cfaSiteUiFieldParams, "getParaValue","isEnabled",true);
				cbxcellCfaSite.setId("cbxcellCfaSite"+inIndex);
				cbxcellCfaSite.setInplace(true);
				cbxcellCfaSite.setWidth("90px");
				if("".equals(smf.getOrderLine().getCfaSite()) || smf.getOrderLine().getCfaSite() == null){
					cbxcellCfaSite.setText("");
				}else{
					cbxcellCfaSite.setText(smf.getOrderLine().getCfaSite().trim());
				}
				cbxcellCfaSite.setParent(cellLineCfaSite);
				cbxcellCfaSite.addEventListener("onSelect", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setCfaSite(cbxcellCfaSite.getText());//先暫時存ParaValue等真正儲存的時間再轉成meaning
					}
				});
			}
			
			//ShipToVendorName
			//edtcellShipToVendorName.setReadonly(true);//2013.06.21
			cellLineShipToVendorName.setLabel(smf.getOrderLine().getShipToVendorName());
				
//			if ("readonly".equals(mode)){
//				cellLineShipToVendorName.setLabel(smf.getOrderLine().getShipToVendorName());
//			}
//			else{
//				//cellLineShipToVendorName.setLabel(smf.getOrderLine().getShipToVendorName()); 
//				edtcellShipToVendorName.setId("edtcellShipToVendorName"+inIndex);
//				edtcellShipToVendorName.setInplace(true);
//				edtcellShipToVendorName.setWidth("120px");
//				edtcellShipToVendorName.setText(smf.getOrderLine().getShipToVendorName());
//				edtcellShipToVendorName.setParent(cellLineShipToVendorName);
//				/*edtcellShipToVendorName.addEventListener("onChange", new EventListener() {
//					public void onEvent(Event inEvent) throws Exception{
//						smf.getOrderLine().setShipToVendorName(edtcellShipToVendorName.getText());
//					}
//				});*///2013.06.21 改為唯讀
//			}//2013.06.25
			
			//OCF-PR-160602 add
			//Shipping Destination
			cbxShippingDestination.setWidth("90%");
			ZkComboboxControl.setComboboxItems(cbxShippingDestination, shiptos, "getCustomerShortName","isCancelFlag",false);
			if(smf.getOrderLine().getShippingDestination() != null && !"".equals(smf.getOrderLine().getShippingDestination())){
				cbxShippingDestination.setText(smf.getOrderLine().getShippingDestination());
			}
			cbxShippingDestination.setInplace(true);
			cbxShippingDestination.setParent(cellLineShippingDestination);
			cbxShippingDestination.addEventListener("onChange", new EventListener() {
				public void onEvent(Event inEvent) throws Exception{
					log.debug(cbxShippingDestination.getText());
					smf.getOrderLine().setShippingDestination(cbxShippingDestination.getText());
				}
			});
			
			//ShipComment
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getShipComment()) || smf.getOrderLine().getShipComment() == null){
					cellLineShipComment.setLabel("");
				}else{
					cellLineShipComment.setLabel(smf.getOrderLine().getShipComment().trim());
				}
			}
			else{
				edtcellShipComment.setId("edtcellShipComment"+inIndex);
				edtcellShipComment.setInplace(true);
				edtcellShipComment.setWidth("90%");
				if("".equals(smf.getOrderLine().getShipComment()) || smf.getOrderLine().getShipComment() == null){
					edtcellShipComment.setText("");
				}else{
					edtcellShipComment.setText(smf.getOrderLine().getShipComment());
				}
				edtcellShipComment.setParent(cellLineShipComment);
				edtcellShipComment.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setShipComment(edtcellShipComment.getText());
					}
				});
			}
			
			//CountryOfFab
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getCountryOfFab()) || smf.getOrderLine().getCountryOfFab() == null){
					cellLineCountryOfFab.setLabel("");
				}else{
					cellLineCountryOfFab.setLabel(smf.getOrderLine().getCountryOfFab().trim());
				}
			}
			else{
				edtcellCountryOfFab.setId("edtcellCountryOfFab"+inIndex);
				edtcellCountryOfFab.setInplace(true);
				edtcellCountryOfFab.setWidth("90%");
				if("".equals(smf.getOrderLine().getCountryOfFab()) || smf.getOrderLine().getCountryOfFab() == null){
					edtcellCountryOfFab.setText("");
				}else{
					edtcellCountryOfFab.setText(smf.getOrderLine().getCountryOfFab().trim());
				}
				edtcellCountryOfFab.setParent(cellLineCountryOfFab);
				edtcellCountryOfFab.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setCountryOfFab(edtcellCountryOfFab.getText());
					}
				});
			}
			
			//Fab
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getFab().trim()) || smf.getOrderLine().getFab() == null){
					cellLineFab.setLabel("");
				}else{
					cellLineFab.setLabel(smf.getOrderLine().getFab().trim());
				}
			}
			else{
				edtcellFab.setId("edtcellFab"+inIndex);
				edtcellFab.setInplace(true);
				edtcellFab.setWidth("60px");
				if("".equals(smf.getOrderLine().getFab()) || smf.getOrderLine().getFab() == null){
					edtcellFab.setText("");
				}else{
					edtcellFab.setText(smf.getOrderLine().getFab().trim());
				}
				edtcellFab.setParent(cellLineFab);
				edtcellFab.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setFab(edtcellFab.getText());
					}
				});
			}
			
			//CfaProId
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getCfaPorId()) || smf.getOrderLine().getCfaPorId() == null){
					cellLineCfaProId.setLabel("");
				}else{
					cellLineCfaProId.setLabel(smf.getOrderLine().getCfaPorId().trim());
				}
			}
			else{
				edtcellCfaProId.setId("edtcellCfaProId"+inIndex);
				edtcellCfaProId.setInplace(true);
				edtcellCfaProId.setWidth("90%");
				if("".equals(smf.getOrderLine().getCfaPorId()) || smf.getOrderLine().getCfaPorId() == null){
					edtcellCfaProId.setText("");
				}else{
					edtcellCfaProId.setText(smf.getOrderLine().getCfaPorId().trim());
				}
				edtcellCfaProId.setParent(cellLineCfaProId);
				edtcellCfaProId.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setCfaPorId(edtcellCfaProId.getText());
					}
				});
			}
			
			//ShipToVendorCode
			//IT-PR-141008_改為不可編輯，且會跟著cellLineShipToVendorName連動，需求by Wen_Allison
			cellLineShipToVendorCode.setLabel(smf.getOrderLine().getShipToVendorCode().trim());
//			if ("readonly".equals(mode)){
//				if("".equals(smf.getOrderLine().getShipToVendorCode()) || smf.getOrderLine().getShipToVendorCode() == null){
//					cellLineShipToVendorCode.setLabel("");
//				}else{
//					cellLineShipToVendorCode.setLabel(smf.getOrderLine().getShipToVendorCode().trim());
//				}
//			}
//			else{
//				edtcellShipToVendorCode.setId("edtcellShipToVendorCode"+inIndex);
//				edtcellShipToVendorCode.setInplace(true);
//				edtcellShipToVendorCode.setWidth("90%");
//				if("".equals(smf.getOrderLine().getShipToVendorCode()) || smf.getOrderLine().getShipToVendorCode() == null){
//					edtcellShipToVendorCode.setText("");
//				}else{
//					edtcellShipToVendorCode.setText(smf.getOrderLine().getShipToVendorCode().trim());
//				}
//				edtcellShipToVendorCode.setParent(cellLineShipToVendorCode);
//				edtcellShipToVendorCode.addEventListener("onChange", new EventListener() {
//					public void onEvent(Event inEvent) throws Exception{
//						smf.getOrderLine().setShipToVendorCode(edtcellShipToVendorCode.getText());
//					}
//				});
//			}
			
			//CompanyCode
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getCompanyCode()) || smf.getOrderLine().getCompanyCode() == null){
					cellLineCompanyCode.setLabel("");
				}else{
					cellLineCompanyCode.setLabel(smf.getOrderLine().getCompanyCode().trim());
				}
			}
			else{
				edtcellCompanyCode.setId("edtcellCompanyCode"+inIndex);
				edtcellCompanyCode.setInplace(true);
				edtcellCompanyCode.setWidth("90%");
				if("".equals(smf.getOrderLine().getCompanyCode()) || smf.getOrderLine().getCompanyCode() == null){
					edtcellCompanyCode.setText("");
				}else{
					edtcellCompanyCode.setText(smf.getOrderLine().getCompanyCode().trim());
				}
				edtcellCompanyCode.setParent(cellLineCompanyCode);
				edtcellCompanyCode.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setCompanyCode(edtcellCompanyCode.getText());
					}
				});
			}
			
			//MtrlNum
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getMtrlNum()) || smf.getOrderLine().getMtrlNum() == null){
					cellLineMtrlNum.setLabel("");
				}else{
					cellLineMtrlNum.setLabel(smf.getOrderLine().getMtrlNum().trim());
				}
			}
			else{
				edtcellMtrlNum.setId("edtcellMtrlNum"+inIndex);
				edtcellMtrlNum.setInplace(true);
				edtcellMtrlNum.setWidth("90%");
				if("".equals(smf.getOrderLine().getMtrlNum()) || smf.getOrderLine().getMtrlNum() == null){
					edtcellMtrlNum.setText("");
				}else{
					edtcellMtrlNum.setText(smf.getOrderLine().getMtrlNum().trim());
				}
				edtcellMtrlNum.setParent(cellLineMtrlNum);
				edtcellMtrlNum.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setMtrlNum(edtcellMtrlNum.getText());
					}
				});
			}
			
			//SourceMtrlNum
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getSourceMtrlNum()) || smf.getOrderLine().getSourceMtrlNum() == null){
					cellLineSourceMtrlNum.setLabel("");
				}else{
					cellLineSourceMtrlNum.setLabel(smf.getOrderLine().getSourceMtrlNum().trim());
				}
			}
			else{
				edtcellSourceMtrlNum.setId("edtcellSourceMtrlNum"+inIndex);
				edtcellSourceMtrlNum.setInplace(true);
				edtcellSourceMtrlNum.setWidth("90%");
				if("".equals(smf.getOrderLine().getSourceMtrlNum()) || smf.getOrderLine().getSourceMtrlNum() == null){
					edtcellSourceMtrlNum.setText("");
				}else{
					edtcellSourceMtrlNum.setText(smf.getOrderLine().getSourceMtrlNum().trim());
				}
				edtcellSourceMtrlNum.setParent(cellLineSourceMtrlNum);
				edtcellSourceMtrlNum.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setSourceMtrlNum(edtcellSourceMtrlNum.getText());
					}
				});
			}
				
			//Sub Name
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getSubName()) || smf.getOrderLine().getSubName() == null){
					cellLineSubName.setLabel("");
				}else{
					cellLineSubName.setLabel(smf.getOrderLine().getSubName().trim());
				}
			}
			else{
				edtcellSubName.setId("edtcellSubName"+inIndex);
				edtcellSubName.setInplace(true);
				edtcellSubName.setWidth("90%");
				if("".equals(smf.getOrderLine().getSubName()) || smf.getOrderLine().getSubName() == null){
					edtcellSubName.setText("");
				}else{
					edtcellSubName.setText(smf.getOrderLine().getSubName().trim());
				}
				edtcellSubName.setParent(cellLineSubName);
				edtcellSubName.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setSubName(edtcellSubName.getText());
					}
				});
			}
			
			//Stage
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getStage()) || smf.getOrderLine().getStage() == null){
					cellLineStage.setLabel("");
				}else{
					cellLineStage.setLabel(smf.getOrderLine().getStage().trim());
				}
			}
			else{
				edtcellStage.setId("edtcellStage"+inIndex);
				edtcellStage.setInplace(true);
				edtcellStage.setWidth("90%");
				if("".equals(smf.getOrderLine().getStage()) || smf.getOrderLine().getStage() == null){
					edtcellStage.setText("");
				}else{
					edtcellStage.setText(smf.getOrderLine().getStage().trim());
				}
				edtcellStage.setParent(cellLineStage);
				edtcellStage.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setStage(edtcellStage.getText());
					}
				});
			}
			
			//Operation Description
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getOperationDescription()) || smf.getOrderLine().getOperationDescription() == null){
					cellLineOperationDescription.setLabel("");
				}else{
					cellLineOperationDescription.setLabel(smf.getOrderLine().getOperationDescription().trim());
				}
			}
			else{
				edtcellOperationDescription.setId("edtcellOperationDescription"+inIndex);
				edtcellOperationDescription.setInplace(true);
				edtcellOperationDescription.setWidth("90%");
				if("".equals(smf.getOrderLine().getOperationDescription()) || smf.getOrderLine().getOperationDescription() == null){
					edtcellOperationDescription.setText("");
				}else{
					edtcellOperationDescription.setText(smf.getOrderLine().getOperationDescription().trim());
				}
				edtcellOperationDescription.setParent(cellLineOperationDescription);
				edtcellOperationDescription.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setOperationDescription(edtcellOperationDescription.getText());
					}
				});
			}
			
			//SalesPartId 2018.05.14
			if ("readonly".equals(mode)){
				if("".equals(smf.getOrderLine().getSalesPartId()) || smf.getOrderLine().getSalesPartId() == null){
					cellLineSalesPartID.setLabel("");
				}else{
					cellLineSalesPartID.setLabel(smf.getOrderLine().getSalesPartId().trim());
				}
			}
			else{
				edtcellLineSalesPartID.setId("edtcellLineSalesPartID"+inIndex);
				edtcellLineSalesPartID.setInplace(true);
				edtcellLineSalesPartID.setWidth("90%");
				//will XQ20181008增加readonly
				edtcellLineSalesPartID.setReadonly(true);
				if("".equals(smf.getOrderLine().getSalesPartId()) || smf.getOrderLine().getSalesPartId() == null){
					edtcellLineSalesPartID.setText("");
				}else{
					edtcellLineSalesPartID.setText(smf.getOrderLine().getSalesPartId().trim());
				}
				edtcellLineSalesPartID.setParent(cellLineSalesPartID);
				edtcellLineSalesPartID.addEventListener("onChange", new EventListener() {
					public void onEvent(Event inEvent) throws Exception{
						smf.getOrderLine().setSalesPartId(edtcellLineSalesPartID.getText());
					}
				});
			}
			
			
		//將each Litcell 放上ListItme上
		celllineNo.setParent(inItem);
		cellLinePoItem.setParent(inItem);
		cellLineMtrlDesc.setParent(inItem);
		cellLineMtrlGroup.setParent(inItem);
		cellLineWaferQty.setParent(inItem);
		cellLineWaferSupplyDate.setParent(inItem);//2013.02.08
		cellLineRequestDate.setParent(inItem);
		cellLineWaferSize.setParent(inItem);
		cellLineDesignId.setParent(inItem);
		cellLineCurrency.setParent(inItem);
		cellLineUnitPrice.setParent(inItem);
		cellLineSellingPrice.setParent(inItem);
		cellLineCfaSite.setParent(inItem);//2013.01.15
		cellLineShipToVendorName.setParent(inItem);
		cellLineShippingDestination.setParent(inItem);
		cellLineShipComment.setParent(inItem);
		cellLineCountryOfFab.setParent(inItem);
		cellLineFab.setParent(inItem);
		cellLineCfaProId.setParent(inItem);
		cellLineShipToVendorCode.setParent(inItem);
		cellLineCompanyCode.setParent(inItem);
		cellLineMtrlNum.setParent(inItem);
		cellLineSourceMtrlNum.setParent(inItem);
		cellLineSubName.setParent(inItem);
		cellLineStage.setParent(inItem);
		cellLineOperationDescription.setParent(inItem);
		cellLineSalesPartID.setParent(inItem); //2018.05.14
		
		setMustbeInput(inItem);
	}

	private boolean compareWithProductForOVT(String mtrlNum,String salesPartId){
		boolean match = true;
		if(BeanUtil.isEmpty(salesPartId))
			return match;
		else{
			if(BeanUtil.isEmpty(mtrlNum)){
				match = false;
				return match;
			}
			String mtrlNumProduct = mtrlNum.trim().split("-")[0];
			String salesProduct = salesPartId.trim().split("-")[0];
			if(!mtrlNumProduct.toUpperCase().equalsIgnoreCase(salesProduct)){
				match = false;
				return match;
			}			
			return match;
		}
	}
	private boolean checkSalesPartIdForOVT(String shipTo,String salesPartId){
	  boolean checkFlg = true;	
	  if(BeanUtil.isEmpty(salesPartId))return checkFlg;
	  boolean existFlg = false;	
	  for(UiFieldParam p:lShipTo){
		  if(p.getParaValue().toUpperCase().equalsIgnoreCase(shipTo)){
			  existFlg = true;
			  break;
		  }
	  }
	  if(!existFlg){
		  checkFlg = false;
	  }
	  return checkFlg;
	}
	
	//設定 畫面上Internal Product值
	public void onClick$btnSetInternalProduct(){
/*		
		//2017.12.20要先檢查
		//Customer, OrderType, Product要有資料
		if ((!("".equals(cbxCustomer.getText())))
			&& (!("".equals(edtProduct.getText().trim())))
			&& (!("".equals(cbxOrderType.getText().trim())))){
			
		}else {
			this.showmessage("Warning", Labels.getLabel("oe.edit.header.chkInternalProduct.empty",
					new Object[] {Labels.getLabel("oe.edit.header.lblCustomer"),
					              Labels.getLabel("oe.edit.header.lblOrderType"),
					              Labels.getLabel("oe.edit.header.lblProduct")}));
			return;
		}
*/
		
		String conCustomerId="";
		//String conOrderType="";
		String conProduct="";		
		
		if (!("".equals(cbxCustomer.getText()))){
			conCustomerId = customer.getCustomerId();
		}
		if (!("".equals(edtProduct.getText().trim()))){
			conProduct = edtProduct.getText().trim();
		}
		//if (!("".equals(cbxOrderType.getText().trim()))){
		//	conOrderType = commonService.getUiFieldParamMeaningByValue(this.getClass().getName(), "OE_ORDER_TYPE", cbxOrderType.getText().trim());
		//}
		
		log.debug("conCustomerId:"+conCustomerId+",conProduct:"+conProduct);
		
		productNameSetup = orderEntryService.getInternalProdcut(conCustomerId, conProduct);
		if (productNameSetup == null){
			//2017.12.20 要做卡關 
			//當CustomerId="0F"時,PRODUCT_NAME_SETUP TABLE一定要有設定值,否則不能Save
			//當CustomerId<>"0F"時,PRODUCT_NAME_SETUP TABLE不一定要有設定值,有設定則帶設定值,沒設定值則同Product值
			if ("0F".equals(conCustomerId)){
				this.showmessage("Warning", Labels.getLabel("oe.edit.header.chkInternalProduct.empty"));
				edtInternalProduct.setValue("");
			}else{
				edtInternalProduct.setValue(edtProduct.getValue().trim());
			}			
		}else{
			edtInternalProduct.setValue(productNameSetup.getInternProduct());
		}
		
	}
	
}