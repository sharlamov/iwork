package com.report.work;

import java.util.ArrayList;
import java.util.List;

import com.report.data.TDataSet;

public class TRowCond {

	List<TAnyRowCond> Conds;

	public TRowCond() {
		Conds = new ArrayList<TAnyRowCond>();
	}

	static TAnyRowCond CreateOne(String APos) {

		int cPos1 = APos.indexOf("USEWHEN");
		int cPos2 = APos.indexOf("USEWITH");
		if (cPos1 == -1 && cPos2 == -1)
			return null;
		TAnyRowCond ResCond;
		if (cPos2 == -1 || (cPos1 > -1 && (cPos1 < cPos2)))
			ResCond = new TFieldValueCond();
		else {
			ResCond = new TFieldExistCond();
			cPos1 = cPos2;
		}

		cPos1 = APos.substring(cPos1).indexOf("(");
		cPos2 = cPos1 > -1 ? APos.substring(++cPos1).indexOf(")") : -1;

		if (cPos2 == -1)
			return null;

		String asParams = APos.substring(cPos1, cPos2);
		asParams = asParams.trim();
		if (!ResCond.Setup(asParams))
			return null;

		APos = APos.substring(cPos2);
		return ResCond;
	}

	static TRowCond CreateFor(String AValue) {
		TRowCond RC = null;
		String cPos = AValue;
		while (true) {
			TAnyRowCond C = CreateOne(cPos);
			if (C == null)
				break;
			if (RC == null)
				RC = new TRowCond();
			RC.Conds.add(C);
		}
		return RC;
	}

	boolean RowUsed(TDataSet ADataSet) throws Exception {
		int nCnt = Conds.size();
		for (int i = 0; i < nCnt; i++) {
			TAnyRowCond C = Conds.get(i);
			if (!C.RowUsed(ADataSet))
				return false;
		}
		return true;
	}

}
