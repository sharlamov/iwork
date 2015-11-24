package com.report.work;

public class TDetailList extends TAnyRowList {

	private static final long serialVersionUID = 1255130995120419896L;

	TDetail GetItems(int AIndex) {
		return (TDetail) GetItems(AIndex);
	}

	// ---------------------------------------------------------------------------
	void SetItems(int AIndex, TDetail AValue) {
		SetItems(AIndex, AValue);
	}

	// ---------------------------------------------------------------------------
	void Add(int ARow, String AExpr, String AValue) {
		add(new TDetail(this, ARow, AExpr, AValue));
		if (size() == 1) // Флаг UseJoin включается автоматически
			UseJoin = true; // если строка detailX только одна.
		else if (UseJoin)
			UseJoin = false;
	}
}
