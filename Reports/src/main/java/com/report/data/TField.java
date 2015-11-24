package com.report.data;

import java.util.Date;

import com.report.enums.TFieldType;

public class TField {

	private TDataSet DataSet;
	private String fieldName;
	private TFieldType DataType;
	private Object value;

	private void installType() {
		if (value == null) {
			DataType = null;
		} else {
			if (value instanceof Double || value instanceof Float)
				DataType = TFieldType.ftFloat;
			else if (value instanceof Integer)
				DataType = TFieldType.ftInteger;
			else if (value instanceof Date)
				DataType = TFieldType.ftDate;
			else
				DataType = TFieldType.ftString;
		}
	}

	public TField(String fieldName, Object value, TDataSet DataSet) {
		this.DataSet = DataSet;
		this.value = value;
		installType();
	}

	public int AsInteger() {
		return Integer.valueOf(value.toString());
	}

	public String AsString() {
		return value.toString();
	}

	public double AsFloat() {
		return Double.valueOf(value.toString());
	}

	@SuppressWarnings("deprecation")
	public Date AsDateTime() {
		return new Date(value.toString());
	}

	public TDataSet getDataSet() {
		return DataSet;
	}

	public void setDataSet(TDataSet dataSet) {
		DataSet = dataSet;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void Clear() {
		value = null;
		installType();
		fieldName = "";
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		installType();
	}

	public TFieldType getDataType() {
		return DataType;
	}

	public void setDataType(TFieldType dataType) {
		DataType = dataType;
	}
}
