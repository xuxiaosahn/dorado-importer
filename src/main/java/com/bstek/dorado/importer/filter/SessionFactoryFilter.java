package com.bstek.dorado.importer.filter;

import java.util.Map;

import org.hibernate.SessionFactory;

/**
 *@author Kevin.yang
 *@since 2015年8月23日
 */
public interface SessionFactoryFilter {

	void filter(Map<String, SessionFactory> SessionFactoryMap);
}
