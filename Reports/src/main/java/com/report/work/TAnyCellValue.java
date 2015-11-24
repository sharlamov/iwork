package com.report.work;

import java.util.Date;

public class TAnyCellValue {

	protected TAnyCell Owner;
	protected boolean IsString, IsInteger, IsFloat, IsDateTime;

	public String GetValue() { return "0";}
	public int GetAsInteger() { return 0; }
	public double GetAsFloat() { return 0.; }
	public Date GetAsDateTime() { return new Date(); }
	public String GetDebugInfo() { return ""; };
	
	public TAnyCellValue(TAnyCell AOwner){
		Owner = AOwner;
	}
	
	public TAnyCell getOwner() {
		return Owner;
	}
	public void setOwner(TAnyCell owner) {
		Owner = owner;
	}
	public boolean isIsString() {
		return IsString;
	}
	public void setIsString(boolean isString) {
		IsString = isString;
	}
	public boolean isIsInteger() {
		return IsInteger;
	}
	public void setIsInteger(boolean isInteger) {
		IsInteger = isInteger;
	}
	public boolean isIsFloat() {
		return IsFloat;
	}
	public void setIsFloat(boolean isFloat) {
		IsFloat = isFloat;
	}
	public boolean isIsDateTime() {
		return IsDateTime;
	}
	public void setIsDateTime(boolean isDateTime) {
		IsDateTime = isDateTime;
	}
}
