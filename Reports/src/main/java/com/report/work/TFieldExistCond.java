package com.report.work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.report.data.TDataSet;

public class TFieldExistCond extends TAnyRowCond {

	List<String> FieldNames;

	public TFieldExistCond() {
		FieldNames = new ArrayList<String>();
	}

	boolean Setup(String AParams) {
		FieldNames = Arrays.asList(AParams.split(","));
		return true;
	}

	boolean RowUsed(TDataSet ADataSet){
	   int nCnt = FieldNames.size();
	   for(String s: FieldNames){
		   Object obj = TAnyRow.F1C.FindField(ADataSet, s);
		   if (obj != null)
		       return true;  
	   }
	   return false;
	 }
}
