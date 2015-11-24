package com.report.work;

import com.report.data.TField;

public class TPivotColInfo {
	int Col;
	TField Field;
	boolean Used;

	public TPivotColInfo(int ACol, TField AField) {
		Col = ACol;
		Field = AField;
	}

	void Reset() {
		Used = false;
	}

	void Check() {
		Used |= (Field != null);
	}

}
