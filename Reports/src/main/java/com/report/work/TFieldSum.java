package com.report.work;

import com.report.book.*;
import com.report.data.TDataSet;

public class TFieldSum {
	public TDataSet DataSet;
	public String FieldName;
	public double Value;

	public TFieldSum(TDataSet ADataSet, String AFieldName, double AValue) {
		DataSet = ADataSet;
		FieldName = AFieldName;
		Value = AValue;
	}

	void Reset() {
		Value = 0;
	}

	void Inc() {
		Value += DataSet.FieldByName(FieldName).AsFloat();
	}

	double GetValue() {
		return Value;
	}

}
