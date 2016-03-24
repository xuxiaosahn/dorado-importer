package com.bstek.dorado.importer.policy;

import com.bstek.dorado.importer.model.ImporterSolution;

/**
 *@author Kevin.yang
 *@since 2015年9月3日
 */
public interface AutoCreateMappingRulePolicy {
	void apply(ImporterSolution importerSolution);
}
