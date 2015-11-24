package com.report.work;

public class TRefCellValue extends TAnyCellValue {
	int nRow, nCol;
	boolean bAbsRow, bAbsCol;

	public TRefCellValue(TAnyCell AOwner, TRefInfo ARefInfo) {
		super(AOwner);
		nRow = Integer.valueOf(ARefInfo.asRow);
		nCol = ColToInt(ARefInfo.asCol);
		bAbsRow = ARefInfo.bAbsRow;
		bAbsCol = ARefInfo.bAbsCol;
		if (!bAbsRow)
			nRow -= Owner.Owner.Row;
		if (!bAbsCol)
			nCol -= Owner.Col + Owner.Owner.F1C.FirstCol;
	}

	// ---------------------------------------------------------------------------
	static int ColToInt(String ACol) {
		if (ACol.length() == 1)
			return ACol.charAt(0) - '@';
		return (ACol.charAt(0) - '@') * 26 + ACol.charAt(1) - '@';
	}

	// ---------------------------------------------------------------------------
	static String IntToCol(int ACol) {
		return --ACol < 26 ? ('A' + ACol) + "" : ('@' + ACol / 26) + ""
				+ ('A' + ACol % 26) + "";
	}

	// ---------------------------------------------------------------------------
	public String GetValue() {
		String asResult = "";
		int nCurrRow = nRow;
		int nCurrCol = nCol;
		if (bAbsCol)
			asResult += "$";
		else
			nCurrCol += Owner.Owner.F1C.CurrCol;
		asResult += IntToCol(nCurrCol);
		if (bAbsRow)
			asResult += "$";
		else
			nCurrRow += Owner.Owner.F1C.CurrRow;
		asResult += nCurrRow;
		return asResult;
	}

	// ---------------------------------------------------------------------------
	static String GetRefText(int ANum, boolean AAbs) {
		return AAbs ? ANum + "" : "[" + ANum + "]";
	}

	// ---------------------------------------------------------------------------
	public String GetDebugInfo() {
		return "RefCellValue: Row=" + GetRefText(nRow, bAbsRow) + ", Col="
				+ GetRefText(nCol, bAbsCol) + "\"\n";
	}

}
