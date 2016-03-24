package com.bstek.dorado.importer.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;

import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.dao.hibernate.HibernateUtils;
import com.bstek.dorado.dao.hibernate.policy.SaveContext;
import com.bstek.dorado.dao.hibernate.policy.impl.SmartSavePolicy;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.bstek.dorado.importer.filter.EntityClassNameFilter;
import com.bstek.dorado.importer.filter.SessionFactoryFilter;
import com.bstek.dorado.importer.model.ImporterSolution;
import com.bstek.dorado.importer.model.MappingRule;
import com.bstek.dorado.importer.parser.CellPostParser;
import com.bstek.dorado.importer.parser.CellPreParser;
import com.bstek.dorado.importer.policy.AutoCreateMappingRulePolicy;

/**
 *@author Kevin.yang
 *@since 2015年8月23日
 */
public class ImporterSolutionMaintain implements ApplicationContextAware {
	
	private Collection<String> sessionFactoryNames;
	private Map<String, List<String>> entityClassNameMap = new HashMap<>();
	private Collection<Map<String,String>> cellPreParsers = new ArrayList<>();
	private Collection<Map<String,String>> cellPostParsers = new ArrayList<>();
	private AutoCreateMappingRulePolicy autoCreateMappingRulePolicy;


	@DataProvider
	public void loadImporterSolutions(Page<ImporterSolution> page, Criteria criteria) {
		HibernateUtils.createLinq(ImporterSolution.class)
			.setPage(page)
			.where(criteria)
			.paging();
	}
	
	@DataProvider
	public void loadMappingRules(Page<MappingRule> page, Criteria criteria, String importerSolutionId) {
		HibernateUtils.createLinq(MappingRule.class)
			.setPage(page)
			.where(criteria)
			.add(Restrictions.eq("importerSolutionId", importerSolutionId))
			.orders(Order.asc("excelColumn"))
			.paging();
	}
	
	@DataProvider
	public void loadEntries(Page<com.bstek.dorado.importer.model.Entry> page, Criteria criteria, String importerSolutionId) {
		HibernateUtils.createLinq(MappingRule.class)
			.setPage(page)
			.where(criteria)
			.add(Restrictions.eq("importerSolutionId", importerSolutionId))
			.orders(Order.asc("excelColumn"))
			.paging();
	}
	
	@DataProvider
	public Collection<String> loadSessionFactoryNames() {
		return sessionFactoryNames;
	}
	
	@DataProvider
	public Collection<String> loadEntityClassNames(String sessionFactoryName) {
		return entityClassNameMap.get(sessionFactoryName);
	}
	
	@DataProvider
	public Collection<Map<String, String>> loadCellPreParsers() {
		return cellPreParsers;
	}
	
	@DataProvider
	public Collection<Map<String, String>> loadCellPostParsers() {
		return cellPostParsers;
	}
	
	@DataResolver
	@Transactional
	public void saveImporterSolutions(Collection<ImporterSolution> importerSolutions) {
		HibernateUtils.save(importerSolutions, new SmartSavePolicy() {

			@Override
			public void apply(SaveContext context) {
				if (context.getEntity() instanceof com.bstek.dorado.importer.model.Entry) {
					MappingRule mappingRule = context.getParent();
					com.bstek.dorado.importer.model.Entry entry = context.getEntity();
					entry.setMappingRuleId(mappingRule.getId());
				}
				super.apply(context);
			}
			
		});
	}
	
	@Expose
	@Transactional
	public void autoCreateMappingRules(ImporterSolution importerSolution) {
		autoCreateMappingRulePolicy.apply(importerSolution);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		Map<String, SessionFactory> sessionFactoryMap = 
				new HashMap<>(applicationContext.getBeansOfType(SessionFactory.class));
				
		Collection<SessionFactoryFilter> sessionFactoryFilters = applicationContext
				.getBeansOfType(SessionFactoryFilter.class).values();
		
		for (SessionFactoryFilter sessionFactoryFilter : sessionFactoryFilters) {
			sessionFactoryFilter.filter(sessionFactoryMap);
		}
		
		Collection<EntityClassNameFilter> entityClassFilters = applicationContext
				.getBeansOfType(EntityClassNameFilter.class).values();
		
		for (Entry<String, SessionFactory> entry : sessionFactoryMap.entrySet()) {
			List<String> entityClassNames = new ArrayList<>(entry.getValue().getAllClassMetadata().keySet());
			for (EntityClassNameFilter entityClassFilter : entityClassFilters) {
				entityClassFilter.filter(entityClassNames);
			}
			entityClassNameMap.put(entry.getKey(), entityClassNames);
		}
		sessionFactoryNames = new ArrayList<String>(sessionFactoryMap.keySet());
		
		Map<String, CellPreParser> cellPreParserMap = applicationContext.getBeansOfType(CellPreParser.class);
		for (Entry<String, CellPreParser> entry : cellPreParserMap.entrySet()) {
			Map<String, String> map = new HashMap<>();
			map.put("beanId", entry.getKey());
			map.put("parserName", entry.getValue().getName());
			cellPreParsers.add(map);
		}
		
		Map<String, CellPostParser> cellPostParserMap = applicationContext.getBeansOfType(CellPostParser.class);
		for (Entry<String, CellPostParser> entry : cellPostParserMap.entrySet()) {
			Map<String, String> map = new HashMap<>();
			map.put("beanId", entry.getKey());
			map.put("parserName", entry.getValue().getName());
			cellPostParsers.add(map);
		}
		
	}

	public void setAutoCreateMappingRulePolicy(
			AutoCreateMappingRulePolicy autoCreateMappingRulePolicy) {
		this.autoCreateMappingRulePolicy = autoCreateMappingRulePolicy;
	}
	
}
