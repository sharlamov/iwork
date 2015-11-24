package com.report.work;

public class TRefInfo {
	String asRow, asCol; // Описание фаз:
	boolean bAbsRow, bAbsCol; // 0123455556
	int nPos, nLen, nPhase; // $AB$12345

	void clear() {
		asRow = "";
		asCol = "";
		bAbsRow = false;
		bAbsCol = false;
		nPhase = 0;
	}

}
