package com.report.work;

import java.util.ArrayList;

import com.report.data.TDataSet;

public class TAnyRowList extends ArrayList<TAnyRow> {

	private static final long serialVersionUID = 1L;

	TDataSet DataSet;

	TAnyRow GetRows(int AIndex) {
		return get(AIndex);
	}

	void SetRows(int AIndex, TAnyRow ARow) {
		set(AIndex, ARow);
	}

	public boolean UseJoin;

	int RowCount() {
		return size();
	}

	void MakeRows() throws Exception {
		for (TAnyRow row : this) {
			if (row.Used())
				row.MakeRow();
		}
	}

}
