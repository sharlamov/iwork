package com.report.work;

import com.report.data.TDataList;
import com.report.data.TDataSet;
import com.report.data.TField;
import com.report.enums.TFieldType;

public class TEchoDataSet {

	TDataList DataSet = null;
	TDataSet Src;
	boolean bClear;

	// ---------------------------------------------------------------------------
	void SetDataSet(TDataSet ADataSet) {
		if (DataSet != null)
			DataSet = null;
		Src = ADataSet;
		if (ADataSet == null) {
			DataSet = null;
			return;
		}
		DataSet = new TDataList("Sagi virt");
		int nCnt = Src.Fields().size();
		for (int i = 0; i < nCnt; i++) {
			TField F = Src.Fields().get(i);
			TFieldType fType = F.getDataType();
			DataSet.AddField(F.getFieldName(), fType,
					(fType == TFieldType.ftString) ? F.AsString().length() : 0, false);
		}
		bClear = true;
	}

	// ---------------------------------------------------------------------------
	void clear() {
		if (DataSet == null || bClear)
			return;
		int nCnt = DataSet.Fields().size();
		for (int i = 0; i < nCnt; i++)
			DataSet.Fields().get(i).Clear();
		bClear = true;
	}

	// ---------------------------------------------------------------------------
	void Populate() {
		if (DataSet == null || Src == null)
			return;
		int nCnt = Src.Fields().size();
		for (int i = 0; i < nCnt; i++)
			DataSet.Fields().get(i).setValue(Src.Fields().get(i).getValue());
		bClear = false;
	}

}
