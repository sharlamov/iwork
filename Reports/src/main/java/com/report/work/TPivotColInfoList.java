package com.report.work;

import java.util.ArrayList;

public class TPivotColInfoList extends ArrayList<TPivotColInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int TopRow;

	TPivotColInfo GetInfo(int AIndex) {
		return get(AIndex);
	}
}
