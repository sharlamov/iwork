package com.report.work;

import com.report.data.TField;
import com.report.enums.TGrpType;

public class TGrp extends TSum {

	private static final long serialVersionUID = 1L;

	private static final String F1ShiftHorizontal = null;

	TGrpList Owner;
	TField Field;
	String FieldName, KeyValue;
	boolean Detail, Pivot;
	TPivotColInfoList PivotInfo;

	boolean Difference() {
		return KeyValue.equals(Field.AsString());
	}

	public TGrp(TGrpList AOwner, String AFieldName) {
		super();
		Owner = AOwner;
		FieldName = AFieldName;
		DataSet = Owner.DataSet;
		Detail = TAnyRow.IsDetail(DataSet);
		if (!Detail)
			Field = DataSet.FieldByName(FieldName);
	}

	// ---------------------------------------------------------------------------
	void Add(int ARow, String AExpr, String AValue, TGrpType AType) {
		TGrpRow Row = new TGrpRow(this, ARow, AExpr, AValue, AType);
		if (!Pivot && Row.Pivot)
			Pivot = true;

		Row.CreateCells();
		add(Row);
	}

	// ---------------------------------------------------------------------------
	void Reset() {
		if (Detail)
			Field = DataSet.FieldByName(FieldName);
		KeyValue = Field.AsString();
		int nCnt = FieldSumCount();
		for (int i = 0; i < nCnt; i++)
			FieldSum(i).Reset();
	}

	// ---------------------------------------------------------------------------
	void MakeRows() throws Exception {
		if (Pivot && (Owner.WorkType == TGrpType.gtHeader))
			PivotResetInfo();

		super.MakeRows();

		if (Pivot && (Owner.WorkType == TGrpType.gtFooter))
			PivotClearEmpty();
	}

	// ---------------------------------------------------------------------------
	void PivotInitInfo() { // Сбор информации о столбцах Pivot и соответствующих
							// им полях
		if (!Pivot || PivotInfo != null)
			return;

		PivotInfo = new TPivotColInfoList();

		TfrmF1Container F1C = TAnyRow.F1C;
		int nOffset = F1C.bPreserveSysCols ? 0 : Util.TEMPLATE_FORMULAS_COLS;

		if (F1C.Detail1.size() == 0)
			return;

		if (F1C.TitleRow != 0) {
			int nRow = F1C.TitleRow;
			for (int i = 4; i <= F1C.nLastCol; i++) {
				String asField = F1C.Src.textRC(nRow, i).toUpperCase();
				if (asField.isEmpty())
					continue;
				TField F = DataSet.FieldByName(asField);
				if (F != null)
					PivotInfo.add(new TPivotColInfo(i - nOffset, F));
			}
		} else {
			TDetail Detail = F1C.Detail1.GetItems(0);
			int nRow = Detail.Row;
			for (int i = Util.TEMPLATE_FORMULAS_COLS + 1; i <= F1C.nLastCol; i++) {
				String asField = F1C.Src.textRC(nRow, i).toUpperCase();
				if ((asField.length() < 4) || (asField.charAt(0) != '_'))
					continue;
				int nPos = asField.indexOf("$");
				if (nPos < 3)
					continue;
				TField F = DataSet.FieldByName(asField.substring(1));
				if (F != null)
					PivotInfo.add(new TPivotColInfo(i - nOffset, F));
			}
		}
	}

	// ---------------------------------------------------------------------------
	void PivotResetInfo() { // Сброс TopRow и признака Used в информации о
							// столбцах Pivot
		if (!Pivot || PivotInfo == null)
			return;

		PivotInfo.TopRow = 0;

		int nCnt = PivotInfo.size();
		for (int i = 0; i < nCnt; i++)
			PivotInfo.get(i).Reset();
	}

	// ---------------------------------------------------------------------------
	void PivotCheckInfo() { // Установка признака Used в зависимости от
							// содержимого поля
		if (!Pivot || PivotInfo == null)
			return;

		int nCnt = PivotInfo.size();
		for (int i = 0; i < nCnt; i++)
			PivotInfo.get(i).Check();
	}

	void PivotClearEmpty()
	{ //  Удаление столбцов Pivot на основании признака Used
	  if (!Pivot || PivotInfo == null || PivotInfo.TopRow == 0 || PivotInfo.size() == 0)
	    return;

	  TfrmF1Container F1C = TAnyRow.F1C;

	  int nTopRow = PivotInfo.TopRow;
	  int nLastRow = F1C.CurrRow - 1;

	  TMergedCellsInfoList MC = new TMergedCellsInfoList(F1C.Dst, nTopRow, PivotInfo.get(0).Col,
	      nLastRow, PivotInfo.get(PivotInfo.size() - 1).Col);

	  for (int i = PivotInfo.size() - 1; i >= 0; i--)
	  {
	    TPivotColInfo Info = PivotInfo.get(i);
	    if (Info.Used)
	      continue;
	    int nCol = Info.Col;
	    MC.DeleteColumn(nCol);
	    //F1C.Dst.DeleteRange(nTopRow, nCol, nLastRow, nCol, F1ShiftHorizontal);
	  }

	  //  TAnyRow::F1C->CurrRow
	}

}
