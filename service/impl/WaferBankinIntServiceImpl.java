/*
 * Project Name:iVision
 * File Name:WaferInIntTableServiceImpl.java
 * Package Name:com.tce.ivision.modules.oe.service.impl
 * Date:2012/12/18下午8:14:31
 * 
 * 說明:
 * TODO 簡短描述這個類別/介面
 * 
 * 修改歷史:
 * TODO yyyy-mm-dd 編號 作者姓名  改了哪些東西
 * 
 */
package com.tce.ivision.modules.oe.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.tce.ivision.model.WaferBankinInt;
import com.tce.ivision.modules.base.service.impl.BaseServiceImpl;
import com.tce.ivision.modules.oe.model.WaferFilter;
import com.tce.ivision.modules.oe.service.WaferBankinIntService;
import com.tce.ivision.units.common.DateFormatUtil;

/**
 * ClassName: WaferInIntTableServiceImpl <br/>
 * date: 2012/12/18 下午8:14:31 <br/>
 *
 * @author 060489-Jeff
 * @version 
 * @since JDK 1.6
 */
public class WaferBankinIntServiceImpl extends BaseServiceImpl implements WaferBankinIntService{	
	/**
	 * 抓WaferInfoReceive DATA.
	 * @see com.tce.ivision.modules.oe.service.WaferInfoReceiveService#getWaferInfoReceiveByHql(java.lang.String)
	 */
/*	@Override
	public List<WaferInIntTable> ListAll() {
		String hql = "select a from WaferInIntTable a";  
        Query query = this.getWaferInIntTableDao().createQuery(hql);         
		return query.list();
	}*/


	/**
	 * 條件尋找 B2B Wafer Data.
	 * @see com.tce.ivision.modules.oe.service.WaferBankinIntService#listBySearch(com.tce.ivision.modules.oe.model.WaferFilter)
	 */
	@Override
	public List<WaferBankinInt> listBySearch(WaferFilter inWaferfilter) {
		/*
		String hql = "select a from WaferInIntTable a where 1=1 ";
		if(!"*".equals(waferfilter.getCustomerfilter())){
			 hql+=" and a.customer = :customer";  
		}
		if(!"*".equals(waferfilter.getCustomerlotnofilter())){
			 hql+=" and a.customerLotno = :customerLotno";  
		}
		if(!"*".equals(waferfilter.getStartdatefilter())&&!"*".equals(waferfilter.getEnddatefilter())){
			 hql+=" and (a.createdDate >= "+waferfilter.getStartdatefilter() + " and a.createdDate <= "+waferfilter.getEnddatefilter();
			 
		}		
		*/
		
		log.info("Filter Condition:");
		log.info("Customer:"+inWaferfilter.getCustomerfilter());
		log.info("Customerlotno:"+inWaferfilter.getCustomerlotnofilter());
		log.info("StartDate:"+inWaferfilter.getStartdatefilter());
		log.info("Enddate:"+inWaferfilter.getEnddatefilter());
		log.info("GetFlag:False");
		List<WaferBankinInt> datas = new ArrayList<WaferBankinInt>();
		try{
			Criteria criteria = this.getWaferInIntTableDao().createCriteria(WaferBankinInt.class);
			if(!"-".equals(inWaferfilter.getCustomerfilter())){
				criteria.add(Restrictions.eq("customerId",inWaferfilter.getCustomerfilter()));
			}
			if(!"-".equals(inWaferfilter.getCustomerlotnofilter())){
				criteria.add(Restrictions.like("customerLotno","%"+inWaferfilter.getCustomerlotnofilter()+"%"));
			}
			if(!"-".equals(inWaferfilter.getStartdatefilter())||!"-".equals(inWaferfilter.getEnddatefilter())){				
				criteria.add(Restrictions.between("importDate", 
								new Date(inWaferfilter.getStartdatefilter()),
								new Date(inWaferfilter.getEnddatefilter())
							));
			}
			
			criteria.add(Restrictions.eq("getFlag",false));			
			datas = criteria.list();
		} catch (Exception e) {
			 StringWriter stringWriter = new StringWriter();
             e.printStackTrace(new PrintWriter(stringWriter));
             log.error(stringWriter.toString());
		}
		
		if (datas.size()>0){
			return datas;
		}else{
			return null;
		}
	}


}
