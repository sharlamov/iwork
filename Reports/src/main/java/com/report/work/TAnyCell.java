package com.report.work;

import java.util.ArrayList;
import java.util.Date;

public class TAnyCell extends ArrayList<TAnyCellValue>{

	private static final long serialVersionUID = 1L;
	int Col;
	boolean IsField, IsString, IsInteger, IsFloat, IsDateTime, UseFormula, HasCellJumps;
	TAnyRow Owner;
	
	
	public TAnyCell(TAnyRow AOwner, int ACol, boolean AUseFormula){
		Owner = AOwner;
		Col = ACol;
		UseFormula = AUseFormula;		
	}
	
	
	public String GetValue(){
	  String asResult = "";
	  IsFloat = true;
	  IsString = true;
	  IsInteger = true;
	  IsDateTime = true;
	  
	  for(TAnyCellValue CellValue: this){
		    asResult += CellValue.GetValue();
		    IsFloat &= CellValue.IsFloat;
		    IsString &= CellValue.IsString;
		    IsInteger &= CellValue.IsInteger;
		    IsDateTime &= CellValue.IsDateTime;  
	  }
	  
	  return asResult;
	}
  
	int GetAsInteger(){
	  return isEmpty() ? 0 : this.get(0).GetAsInteger();
	}
	//---------------------------------------------------------------------------
	double GetAsFloat() throws Exception{
	  TAnyCellValue cv = get(0);
	    return isEmpty() ? 0. : Util.Round6(cv.GetAsFloat());
	}
	//---------------------------------------------------------------------------
	Date GetAsDateTime(){
	  return isEmpty() ? new Date() : get(0).GetAsDateTime();
	}
	//---------------------------------------------------------------------------
	void CheckProps(){
	  IsField = (size() == 1) &&  (get(0) instanceof TFldCellValue);
	}
	//---------------------------------------------------------------------------
	void SetupCellJumps(){
	  if (!HasCellJumps)
	    return;

	  for(TAnyCellValue acv: this){
		  TAddCellValue cv = (TAddCellValue)acv;
		  if (cv != null){
			  //????TAnyRow.F1C.PreviewForm.AddCellJump(TAnyRow.F1C.CurrRow, TAnyRow.F1C.CurrCol, cv.KeyValue);			  
		  }
	  }
	}
  
	public int getCol() {
		return Col;
	}
	public void setCol(int col) {
		Col = col;
	}
	public boolean isIsField() {
		return IsField;
	}
	public void setIsField(boolean isField) {
		IsField = isField;
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
	public boolean isUseFormula() {
		return UseFormula;
	}
	public void setUseFormula(boolean useFormula) {
		UseFormula = useFormula;
	}
	public boolean isHasCellJumps() {
		return HasCellJumps;
	}
	public void setHasCellJumps(boolean hasCellJumps) {
		HasCellJumps = hasCellJumps;
	}
	public TAnyRow getOwner() {
		return Owner;
	}
	public void setOwner(TAnyRow owner) {
		Owner = owner;
	}
	  
	  
}