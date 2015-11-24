package com.report.work;

public class TTitleRow extends TAnyRow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean PageTitle;

	public TTitleRow(TTitle AOwner, int ARow, String AExpr, String AValue) {
		super(AOwner, ARow, AExpr, AValue);
		PageTitle = (AExpr == Util.asPAGE);
	}

	// ---------------------------------------------------------------------------
	void MakeRow() throws Exception {
		int nDstRow = F1C.CurrRow;
		super.MakeRow();
		if (!PageTitle || (F1C.CurrRow == nDstRow))
			return;
		if (F1C.StartPageHead == 0)
			F1C.StartPageHead = nDstRow;
		F1C.EndPageHead = nDstRow;
	}
}
