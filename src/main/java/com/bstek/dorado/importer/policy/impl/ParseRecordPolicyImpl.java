package com.bstek.dorado.importer.policy.impl;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.sf.cglib.beans.BeanMap;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

import com.bstek.dorado.dao.BeanUtils;
import com.bstek.dorado.dao.hibernate.HibernateUtils;
import com.bstek.dorado.dao.hibernate.policy.SaveContext;
import com.bstek.dorado.dao.hibernate.policy.SavePolicy;
import com.bstek.dorado.importer.model.Cell;
import com.bstek.dorado.importer.model.MappingRule;
import com.bstek.dorado.importer.model.Record;
import com.bstek.dorado.importer.policy.Context;
import com.bstek.dorado.importer.policy.ParseRecordPolicy;
import com.bstek.dorado.importer.processor.CellPostprocessor;
import com.bstek.dorado.importer.processor.CellPreprocessor;
import com.bstek.dorado.importer.processor.CellProcessor;

/**
 *@author Kevin.yang
 *@since 2015年8月23日
 */
public class ParseRecordPolicyImpl implements ParseRecordPolicy, ApplicationContextAware {

	private Collection<CellPreprocessor> cellPreprocessors;
	private Collection<CellProcessor> cellProcessors;
	private Collection<CellPostprocessor> cellPostprocessors;
	private SavePolicy savePolicy = new SavePolicy() {

		@Override
		public void apply(SaveContext context) {
			context.getSession().save(context.getEntity());
		}
		
	} ;
	
	@Override
	@Transactional
	public void apply(Context context) throws ClassNotFoundException {
		List<Record> records = context.getRecords();
		
		for (int i = context.getStartRow(); i < records.size(); i++) {
			Record record = records.get(i);
			Object entity = BeanUtils.newInstance(context.getEntityClass());
			context.setCurrentEntity(entity);
			context.setCurrentRecord(record);
			String idProperty = HibernateUtils.getIdPropertyName(context.getEntityClass());
			BeanMap beanMap = BeanMap.create(context.getCurrentEntity());
			if (beanMap.getPropertyType(idProperty) == String.class) {
				beanMap.put(idProperty, UUID.randomUUID().toString());
			}
			for (MappingRule mappingRule : context.getMappingRules()) {
				Cell cell = record.getCell(mappingRule.getExcelColumn());

				context.setCurrentMappingRule(mappingRule);
				context.setCurrentCell(cell);
				cellPreprocess(context);
				cellProcess(context);
				cellPostprocess(context);
				
			}
			HibernateUtils.save(entity, savePolicy);
			if (i % 100 == 0) {
				HibernateUtils.getCurrentSession().flush();
				HibernateUtils.getCurrentSession().clear();
			}
		}

	}
	
	
	protected void cellPreprocess(Context context) {
		for (CellPreprocessor cellPreProcessor : cellPreprocessors) {
			if (cellPreProcessor.support(context)) {
				cellPreProcessor.process(context);
			}
		}
	}
	
	protected void cellProcess(Context context) {
		for (CellProcessor cellProcessor : cellProcessors) {
			if (cellProcessor.support(context)) {
				cellProcessor.process(context);
			}
		}
	}
	
	protected void cellPostprocess(Context context) {
		for (CellPostprocessor cellPostprocessor : cellPostprocessors) {
			if (cellPostprocessor.support(context)) {
				cellPostprocessor.process(context);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		cellPreprocessors = applicationContext.getBeansOfType(CellPreprocessor.class).values();
		cellProcessors = applicationContext.getBeansOfType(CellProcessor.class).values();
		cellPostprocessors = applicationContext.getBeansOfType(CellPostprocessor.class).values();

	}

}
