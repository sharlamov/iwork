package com.report.work;

import java.util.ArrayList;
import java.util.List;

import com.report.book.TF1Book6;

public class TMergedCellsInfoList {

	List<TMergedCellsInfo> FList;
	TF1Book6 FBook;

	public TMergedCellsInfoList(TF1Book6 ABook, int ATopRow, int ALeftCol,
			int ABottomRow, int ARightCol) {
		FList = new ArrayList<TMergedCellsInfo>();
		FBook = ABook;
		for (int r = ATopRow; r <= ABottomRow; r++) {
			for (int c = ALeftCol; c <= ARightCol; c++) {
				boolean bInRange = false;
				for (int i = FList.size() - 1; i >= 0; i--)
					if (FList.get(i).IsCellInRange(r, c)) {
						bInRange = true;
						break;
					}
				if (bInRange)
					continue;
				FBook.setSelection(r, c, r, c);
				if ((FBook.SelStartRow() == FBook.SelEndRow())
						&& (FBook.SelStartCol() == FBook.SelEndCol()))
					continue;
				FList.add(new TMergedCellsInfo(FBook));
			}
		}
	}

	void DeleteColumn(int ACol) {
		for (int i = FList.size() - 1; i >= 0; i--)
			FList.get(i).DeleteColumn(ACol);
	}
}
