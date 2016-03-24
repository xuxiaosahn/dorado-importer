package com.bstek.dorado.importer.processor.impl;

import com.bstek.dorado.importer.policy.Context;
import com.bstek.dorado.importer.processor.CellPreprocessor;

/**
 *@author Kevin.yang
 *@since 2015年9月2日
 */
public class EmptyCellPreprocessor implements CellPreprocessor {

	@Override
	public void process(Context context) {

	}

	@Override
	public boolean support(Context context) {
		return false;
	}

}
