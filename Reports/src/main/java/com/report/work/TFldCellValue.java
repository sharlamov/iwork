package com.report.work;

import java.util.Date;

import com.report.data.TDataSet;
import com.report.data.TField;
import com.report.enums.TFieldType;

public class TFldCellValue extends TAnyCellValue {

	private boolean FDetail;
	private TField Field;
	private TDataSet DataSet;
	private String FieldName;

	public TFldCellValue(TAnyCell AOwner, TField AField) {
		super(AOwner);
		Field = AField;
		DataSet = AField.getDataSet();
		FieldName = AField.getFieldName();
		// FDetail = TAnyRow::IsDetail(DataSet);
	}

	public String GetValue(){
		  
	    if (FDetail) //  ptt2 меняет значения указателей на поля при переходах ptt1
	      Field = DataSet.FieldByName(FieldName);
	    IsFloat = false;
	    IsString = false;
	    IsInteger = false;
	    IsDateTime = false;

	    String asResult = Field.AsString();
	    if (asResult.isEmpty()){
	      if (Owner.UseFormula)
	        return "\"\"";
	    }

	    String asClass = Field.getClass().getName();
	    if ((asClass.substring(0, 4).equals("TCont")) && ((Field.getDataType() == TFieldType.ftInteger) || (Field.getDataType() == TFieldType.ftFloat))){
	      IsString = true;
	    }
	    else if (Field.getDataType() == TFieldType.ftInteger){
	      if (Owner.UseFormula && asResult.isEmpty())
	        asResult = "0";
	      IsInteger = true;
	    }
	    else if ((Field.getDataType() == TFieldType.ftFloat) || (Field.getDataType() == TFieldType.ftCurrency))
	    {
	      if (!asResult.isEmpty())
	      {
	        double flValue = Field.AsFloat();
	        if (TAnyRow.F1C.minFloat != 0f && (Math.abs(flValue) < TAnyRow.F1C.minFloat))
	          asResult = "0";
	      }
	      else if (Owner.UseFormula)
	        asResult = "0";
	      IsFloat = true;
	    }
	    else if ((Field.getDataType() == TFieldType.ftString) || (Field.getDataType() == TFieldType.ftMemo) || (Field.getDataType() == TFieldType.ftFmtMemo))
	    {
	      if (Owner.UseFormula)
	        asResult = "\"" + asResult.replace("\"", "\"\"") + "\"";
	      else
	        IsString = true;
	    }
	    else if ((Field.getDataType() == TFieldType.ftDate))
	    {
	      if (Owner.UseFormula)
	        asResult = "\"" + asResult + "\"";
	      else if (TAnyRow.F1C.SmartDate)
	        IsDateTime = !asResult.isEmpty();
	      else
	        IsString = true;
	    }
	    return asResult;
	  }

	// ---------------------------------------------------------------------------
	public int GetAsInteger() {
		return Field.AsInteger();
	}

	// ---------------------------------------------------------------------------
	public double GetAsFloat() {
		return Field.AsFloat();
	}

	// ---------------------------------------------------------------------------
	public Date GetAsDateTime() {
		return Field.AsDateTime();
	}

	// ---------------------------------------------------------------------------
	public String GetDebugInfo() {
		return "FldCellValue: " + DataSet.getName() + "." + FieldName + ": \""
				+ Field.AsString() + "\"\n";
	}

	public TField getField() {
		return Field;
	}

	public TDataSet getDataSet() {
		return DataSet;
	}

	public String getFieldName() {
		return FieldName;
	}

}
