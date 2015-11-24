package com.report.work;

public class TStrCellValue extends TAnyCellValue {
	String FValue;

	public String GetValue() {
		return FValue;
	}

	public TStrCellValue(TAnyCell AOwner, String AValue) { // Нераспознанные
															// формулы полей
															// заменяем пустыми
															// строками
		super(AOwner);
		FValue = AValue;
		if ((FValue.length() >= 2) && (FValue.charAt(0) == '_')
				&& (FValue.charAt(1) != '_'))
			FValue = new String();
	}

	public String GetDebugInfo() {
		return "StrCellValue: \"" + FValue + "\"\n";
	}
}
