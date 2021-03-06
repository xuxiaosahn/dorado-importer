package com.bstek.dorado.importer.processor;

import com.bstek.dorado.importer.policy.Context;

/**
 *@author Kevin.yang
 *@since 2015年8月30日
 */
public interface CellPreprocessor {

	void process(Context context);
	
	boolean support(Context context);
}
