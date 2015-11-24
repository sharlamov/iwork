package com.report.work;

import com.report.data.TDataSet;
import com.report.data.TField;

public class TDetail extends TAnyRow {

	private static final long serialVersionUID = 1L;

	public TDetail(TDetailList AOwner, int ARow, String AExpr, String AValue) {
		super(AOwner, ARow, AExpr, AValue);
		CreateCells();
	}

	// ---------------------------------------------------------------------------
	void GetColorFields(TField ABgField, TField AFgField, TDataSet ADataSet,
			TField AField) { // Поиск полей, определяющих цвета фона и текста
								// строки или ячейки
		String asFld = "";
		if (AField != null)
			asFld = "_" + AField.getFieldName();
		TField F = ADataSet.FieldByName("BGCOLOR" + asFld);
		if (F != null)
			ABgField = F;
		F = ADataSet.FieldByName("FGCOLOR" + asFld);
		if (F != null)
			AFgField = F;
	}

}
