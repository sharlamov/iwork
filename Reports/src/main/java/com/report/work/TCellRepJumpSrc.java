package com.report.work;

import java.util.List;

public class TCellRepJumpSrc extends TAnyRepJumpSrc {

	int Col;

	public TCellRepJumpSrc(TAnyRow AOwnerRow, int ACol, List<String> AParams) {
		super(AOwnerRow, AParams);
		Col = ACol;
	}
}
