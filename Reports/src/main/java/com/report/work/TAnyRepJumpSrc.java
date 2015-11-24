package com.report.work;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.report.data.TField;

public class TAnyRepJumpSrc {

	TAnyRow OwnerRow;
	Map<String, TField> Params;

	public TAnyRepJumpSrc(TAnyRow AOwnerRow, List<String> AParams) {
		OwnerRow = AOwnerRow;
		Params = new HashMap<String, TField>();
		if (AParams != null)
			Setup(AParams);
	}

	public void Setup(List<String> AParams) {
		Params.clear();
		for (String asName : AParams) {
			if (asName.isEmpty())
				continue;

			if (asName.startsWith("_"))
				Params.put(asName, OwnerRow.FindField(asName.substring(1)));
		}
	}
}
