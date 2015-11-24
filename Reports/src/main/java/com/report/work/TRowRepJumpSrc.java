package com.report.work;

import java.util.ArrayList;
import java.util.List;

public class TRowRepJumpSrc extends TAnyRepJumpSrc {

	int FRow;
	List<TCellRepJumpSrc> FCells;

	TCellRepJumpSrc GetCells(int AIndex) {
		if (FCells != null && (AIndex >= 0) && (AIndex < FCells.size()))
			return FCells.get(AIndex);
		return null;
	}

	public TRowRepJumpSrc(TAnyRow AOwnerRow) {
		super(AOwnerRow, null);
		FRow = AOwnerRow.Row;
	}

	void Clear() {
		FCells.clear();
	}

	int Row() {
		return FRow;
	}

	int CellCount() {
		return GetCellCount();
	}

	TCellRepJumpSrc Cells(int AIndex) {
		return GetCells(AIndex);
	}

	int GetCellCount() {
		return FCells != null ? FCells.size() : 0;
	}

	void AddCell(int ACol, List<String> AParams) {
		if (FCells == null)
			FCells = new ArrayList<TCellRepJumpSrc>();

		FCells.add(new TCellRepJumpSrc(OwnerRow, ACol, AParams));
	}

}
