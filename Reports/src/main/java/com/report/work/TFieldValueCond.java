package com.report.work;

import com.report.data.TDataSet;
import com.report.data.TField;



public class TFieldValueCond extends TAnyRowCond {
	  
	enum TCondOper { coEqual, coNotEqual };
	  
	  String FieldName;
	  TCondOper Oper;
	  int Value;
	  
	  boolean Setup(String AParams){
	    Value = 0;
	    Oper = TCondOper.coEqual;
	    int nPos;
	    if ((nPos = AParams.indexOf(",")) != -1) //  AParams типа "FIELD,VALUE"
	      FieldName = AParams.substring(0, nPos - 1);
	    else if ((nPos = AParams.indexOf("=")) != 0)  //  AParams типа "FIELD=VALUE"
	    {
	      FieldName = AParams.substring(0, nPos - 1);
	      if (AParams.substring(nPos + 1, 1) == "=")  //  AParams типа "FIELD==VALUE"
	        nPos++;
	    }
	    else if ((nPos = AParams.indexOf("!")) != 0)  //  AParams типа "FIELD!VALUE"
	    {
	      FieldName = AParams.substring(0, nPos - 1);
	      if (AParams.substring(nPos + 1, 1) == "=")  //  AParams типа "FIELD!=VALUE"
	        nPos++;
	      Oper = TCondOper.coNotEqual;
	    }
	    else if ((nPos = AParams.indexOf("<>")) != 0) //  AParams типа "FIELD<>VALUE"
	    {
	      FieldName = AParams.substring(1, nPos - 1);
	      nPos++;
	      Oper = TCondOper.coNotEqual;
	    }
	    else  //  AParams типа "FIELD" обрабатывается как "FIELD=1"
	    {
	      FieldName = AParams;
	      Value = 1;
	    }
	    FieldName = FieldName.trim();
	    if (Value == 0)
	      Value = Integer.valueOf(AParams.substring(nPos));
	    return true;
	  }

	  //---------------------------------------------------------------------------
	  boolean RowUsed(TDataSet ADataSet) throws Exception{
	    TField field = TAnyRow.F1C.FindField(ADataSet, FieldName);
	    if (field == null)
	      throw new Exception("Error in template function 'UseWhen': field '" + FieldName + "' not found!");
	    int nFldValue = field.AsInteger();
	    switch (Oper)
	    {
	      case coEqual :
	        return nFldValue == Value;
	      case coNotEqual :
	        return nFldValue != Value;
	    }
	    return true;
	  }

}
