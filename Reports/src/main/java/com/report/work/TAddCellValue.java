package com.report.work;

import com.report.data.TDataSet;
import com.report.data.TField;

public class TAddCellValue extends TAnyCellValue {

	public static TDataSet DataSet;
	public static TField Field;
	double FValue;
	double Value;
	String KeyValue;

	public TAddCellValue(TAnyCell AOwner, String AKeyValue) {
		super(AOwner);
		KeyValue = AKeyValue;

		if (DataSet.Locate("SID", KeyValue, "TLocateOptions()"))
			Value = Field.AsFloat();
	}

	static void Init(TDataSet ADataSet) {
		DataSet = ADataSet;
		Field = ADataSet != null ? ADataSet.FieldByName("SVALUE") : null;
	}

	// ---------------------------------------------------------------------------
	public String GetValue() {
		if (Value == (double) FValue)
			IsInteger = true;
		else
			IsFloat = true;
		return Value + "";
	}

	// ---------------------------------------------------------------------------
	public int GetAsInteger() {
		return (int) FValue;
	}

	// ---------------------------------------------------------------------------
	public double GetAsFloat() {
		return FValue;
	}

	// ---------------------------------------------------------------------------
	public String GetDebugInfo() {
		return "AddCellValue: " + DataSet.getName() + "."
				+ Field.getFieldName() + ": \"" + Field.AsString() + "\"\n";
	}
}
