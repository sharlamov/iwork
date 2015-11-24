package com.report.work;

public class TSumCellValue extends TAnyCellValue {
	public TFieldSum FieldSum;

	public TSumCellValue(TAnyCell AOwner, TFieldSum AFieldSum) {
		super(AOwner);
		FieldSum = AFieldSum;
	}

	public String GetValue() {
		double flValue = FieldSum.Value;
		if (TAnyRow.F1C.minFloat != 0d
				&& (Math.abs(flValue) < TAnyRow.F1C.minFloat))
			flValue = 0d;
		if (flValue == (double) flValue)
			IsInteger = true;
		else
			IsFloat = true;
		return flValue + "";
	}

	public int GetAsInteger() {
		return (int) FieldSum.Value;
	}

	public double GetAsFloat() {
		return FieldSum.Value;
	}

	public String GetDebugInfo() {
		return "SumCellValue: " + FieldSum.DataSet.getName() + "."
				+ FieldSum.FieldName + ": \"" + FieldSum.Value + "\"\n";
	}

}
