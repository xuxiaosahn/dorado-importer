package com.bstek.dorado.importer.parser;

import com.bstek.dorado.importer.policy.Context;

/**
 *@author Kevin.yang
 *@since 2015年8月30日
 */
public interface CellPreParser {
	
	final String DEFAULT = "importer.defaultCellPreParser";

	
	String getName();
	
	void parse(Context context);
}
