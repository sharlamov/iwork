package com.report.work;

public class TSumRow extends TAnyRow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TSumRow(TSum AOwner, int ARow, String AExpr, String AValue) {
		super(AOwner, ARow, AExpr, AValue);
	}

	// ---------------------------------------------------------------------------
	TAnyCellValue ParseToken(TAnyCell ACell, String AToken) {
		TFieldSum FieldSum = ((TSum) Owner).TryToAdd(AToken);
		return FieldSum != null ? new TSumCellValue(ACell, FieldSum) : 
			super.ParseToken(ACell, AToken);
	}

}
