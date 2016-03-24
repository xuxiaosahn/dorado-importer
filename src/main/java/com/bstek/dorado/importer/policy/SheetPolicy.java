package com.bstek.dorado.importer.policy;


/**
 *@author Kevin.yang
 *@since 2015年8月22日
 */
public interface SheetPolicy<T extends Context> {
	
	void apply(T context) throws Exception;
	
}
