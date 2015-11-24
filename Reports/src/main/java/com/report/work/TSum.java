package com.report.work;

import java.util.ArrayList;
import java.util.List;

import com.report.data.TDataSet;

public class TSum extends TAnyRowList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<TFieldSum> FieldSumList;
	int nFieldIndex;
	boolean FHasMasterSum, FHasDetailSum;

	public TSum() {
		super();
		FieldSumList = new ArrayList<TFieldSum>();
	}

	// ---------------------------------------------------------------------------
	int FieldSumCount() {
		return FieldSumList.size();
	}

	// ---------------------------------------------------------------------------
	TFieldSum FieldSum(int AIndex) {
		return FieldSumList.get(AIndex);
	}

	// ---------------------------------------------------------------------------
	TFieldSum FieldSumByName(String AFormula) {
		return FieldExists(AFormula, false) ? FieldSum(nFieldIndex) : null;
	}

	// ---------------------------------------------------------------------------
	static boolean IsFormulaCorrect(String AFormula) {
		if (AFormula.length() < 3)
			return false;
		char cSym = '_';
		return ((AFormula.charAt(0) == cSym) && (AFormula.charAt(1) == cSym) && (AFormula
				.charAt(2) != cSym));
	}

	// ---------------------------------------------------------------------------
	boolean FieldExists(String AFormula, boolean ADontTest) {
		if (!ADontTest && !IsFormulaCorrect(AFormula))
			return false;
		String asFieldName = AFormula.substring(2).toUpperCase();
		for (nFieldIndex = 0; nFieldIndex < FieldSumCount(); nFieldIndex++)
			if (FieldSum(nFieldIndex).FieldName == asFieldName)
				return true;
		return false;
	}

	// ---------------------------------------------------------------------------
	double FieldValue() {
		return FieldSum(nFieldIndex).Value;
	}

	// ---------------------------------------------------------------------------
	void Add(int ARow, String AExpr, String AValue) {
		TSumRow Row = new TSumRow(this, ARow, AExpr, AValue);
		Row.CreateCells();
		add(Row);
	}

	// ---------------------------------------------------------------------------
	TFieldSum TryToAdd(TDataSet ADataSet, String AFieldName) {
		if (ADataSet == null || ADataSet.FieldByName(AFieldName) == null)
			return null;
		TFieldSum FieldSum = new TFieldSum(ADataSet, AFieldName, 0.0);
		nFieldIndex = FieldSumList.size();
		FieldSumList.add(FieldSum);
		if (ADataSet == TAnyRow.F1C.dataMaster)
			FHasMasterSum = true;
		else if (ADataSet == TAnyRow.F1C.dataDetail)
			FHasDetailSum = true;
		return FieldSum;
	}

	// ---------------------------------------------------------------------------
	TFieldSum TryToAdd(String AFormula) {
		if (!IsFormulaCorrect(AFormula))
			return null;
		if (!FieldExists(AFormula, true)) {
			String asFieldName = AFormula.substring(2).toUpperCase();
			while (true) {
				if (DataSet.equals(TAnyRow.F1C.dataDetail)
						&& TryToAdd(DataSet, asFieldName) != null)
					break;
				if (TryToAdd(TAnyRow.F1C.dataMaster, asFieldName) != null)
					break;
				if (!DataSet.equals(TAnyRow.F1C.dataDetail)
						&& TryToAdd(TAnyRow.F1C.dataDetail, asFieldName) != null)
					break;
				return null;
			}
		}
		return FieldSum(nFieldIndex);
	}

	// ---------------------------------------------------------------------------
	void Increment(TDataSet ADataSet) {
		if ((ADataSet == TAnyRow.F1C.dataMaster) && !FHasMasterSum)
			return;
		if ((ADataSet == TAnyRow.F1C.dataDetail) && !FHasDetailSum)
			return;
		for (int i = 0; i < FieldSumCount(); i++) {
			TFieldSum FS = FieldSum(i);
			if (FS.DataSet == ADataSet)
				FS.Inc();
		}
	}

	// ---------------------------------------------------------------------------
	void Clear() {
		if (FieldSumList != null)
			FieldSumList.clear();
		clear();
		FHasMasterSum = false;
		FHasDetailSum = false;
	}

}
