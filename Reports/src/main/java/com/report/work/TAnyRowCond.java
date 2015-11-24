package com.report.work;

import com.report.data.TDataSet;

public abstract class TAnyRowCond {
	boolean Setup(String AParams) { return  false; }
	boolean RowUsed(TDataSet ADataSet) throws Exception { return  false; }
}
