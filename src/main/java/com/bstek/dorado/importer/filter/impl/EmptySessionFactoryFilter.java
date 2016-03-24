package com.bstek.dorado.importer.filter.impl;

import java.util.Map;

import org.hibernate.SessionFactory;

import com.bstek.dorado.importer.filter.SessionFactoryFilter;

/**
 *@author Kevin.yang
 *@since 2015年8月23日
 */
public class EmptySessionFactoryFilter implements SessionFactoryFilter {

	@Override
	public void filter(Map<String, SessionFactory> SessionFactoryMap) {
	
	}

}
