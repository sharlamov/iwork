package com.report.work;

import com.report.data.TField;
import com.report.enums.TGrpType;

public class TGrpRow extends TSumRow {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TGrpType Type;
	boolean Pivot;

	public TGrpRow(TGrp AOwner, int ARow, String AExpr, String AValue,
			TGrpType AType) {
		super(AOwner, ARow, AExpr, AValue);
		Type = AType;
		if ((AType == TGrpType.gtHeader) && (AValue == "PIVOT"))
			Pivot = true;
	}

	// ---------------------------------------------------------------------------
	boolean Used() throws Exception {
		if (Type != ((TGrp) Owner).Owner.WorkType)
			return false;
		if (RowCond != null)
			if (Type == TGrpType.gtHeader)
				return RowCond.RowUsed(GetDataSet());
			else
				return RowCond.RowUsed(((TGrp) Owner).Owner.Echo.DataSet);
		return true;
	}

	// ---------------------------------------------------------------------------
	TAnyCellValue ParseToken(TAnyCell ACell, String AToken) {
		if (Type == TGrpType.gtHeader)
			return ((TAnyRow) this).ParseToken(ACell, AToken);
		return super.ParseToken(ACell, AToken);
	}

	// ---------------------------------------------------------------------------
	public TField FindField(String AFieldName) {
		TField Field = ((TGrp) Owner).Owner.EchoDataSet().FieldByName(
				AFieldName);
		if (Field != null)
			return Field;
		return ((TAnyRow) this).FindField(AFieldName);
	}

	// ---------------------------------------------------------------------------
	void MakeRow() throws Exception {
		super.MakeRow();

		if (LastOutputRow != 0 && (Type == TGrpType.gtHeader)
				&& ((TGrp) Owner).Pivot && ((TGrp) Owner).PivotInfo.TopRow == 0)
			((TGrp) Owner).PivotInfo.TopRow = LastOutputRow;
	}

}
