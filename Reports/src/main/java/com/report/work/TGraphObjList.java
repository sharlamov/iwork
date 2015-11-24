package com.report.work;

import java.util.ArrayList;

import com.report.book.TF1Book6;

public class TGraphObjList extends ArrayList<TGraphObj> {

	private static final long serialVersionUID = 1L;

	TGraphObj GetObj(int AIndex) {
		return get(AIndex);
	}

	void Scan(TF1Book6 ABook) {
		clear();
		try {
			int nId = ABook.ObjFirstID();
			while (true) {
				add(new TGraphObj(ABook, nId));
				nId = ABook.ObjNextID(nId);
			}
		} catch (Exception e) {
		}
	}

	// ---------------------------------------------------------------------------
	int GetCountForRow(int ARow) {
		int nResult = 0;
		for (TGraphObj obj : this) {
			if (obj.Row == ARow)
				nResult++;
		}
		return nResult;
	}

	// ---------------------------------------------------------------------------
	void CopyForRow(TF1Book6 ABook, int ARow, int ADX, int ADY) {
		for (TGraphObj obj : this) {
			if (obj.Row == ARow)
				obj.Copy(ABook, ADX, ADY);
		}
	}
}
