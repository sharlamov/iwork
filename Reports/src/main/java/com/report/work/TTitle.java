package com.report.work;

public class TTitle extends TAnyRowList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	void Add(int ARow, String AExpr, String AValue) {
		TTitleRow Row = new TTitleRow(this, ARow, AExpr, AValue);
		Row.CreateCells();
		add(Row);
	}

}
