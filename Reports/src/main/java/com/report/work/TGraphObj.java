package com.report.work;

import java.awt.Color;

import com.report.book.TF1Book6;
import com.report.enums.TGraphType;

public class TGraphObj {
	TF1Book6 Src;
	int nId, Row;
	TGraphType nType;
	Float nX1, nY1, nX2, nY2;
	short nLineStyle, nLineWeight, nPattern;
	int nLineColor, nPatFG, nPatBG;
	boolean bUseLineStyle, bUsePattern;

	public TGraphObj(TF1Book6 ABook, Integer AId) {
		Src = ABook;
		nId = AId;
		ABook.ObjGetPos(AId, nX1, nY1, nX2, nY2);

		Row = (int) Math.floor(nY1);
		nType = ABook.ObjGetType(AId);
		bUseLineStyle = (nType == TGraphType.F1ObjPicture)
				|| (nType == TGraphType.F1ObjRectangle)
				|| (nType == TGraphType.F1ObjPolygon)
				|| (nType == TGraphType.F1ObjLine)
				|| (nType == TGraphType.F1ObjOval)
				|| (nType == TGraphType.F1ObjArc);
		bUsePattern = (nType == TGraphType.F1ObjRectangle)
				|| (nType == TGraphType.F1ObjOval);

		if (bUseLineStyle || bUsePattern) {
			ABook.ObjSetSelection(AId);
			if (bUseLineStyle)
				ABook.GetLineStyle(nLineStyle, nLineColor, nLineWeight);
			if (bUsePattern)
				ABook.GetPattern(nPattern, nPatFG, nPatBG);
		}
	}

	// ---------------------------------------------------------------------------
	void Copy(TF1Book6 ABook, int ADX, int ADY) {
		int nNewId = 0;
		try {
			if (nType == TGraphType.F1ObjPicture) {
				Src.ObjSetSelection(nId);
				Src.EditCopy();
				ABook.EditPaste();
				nNewId = ABook.ObjSelection(0);
				ABook.ObjSetPos(nNewId, nX1 + ADX, nY1 + ADY, nX2 + ADX, nY2
						+ ADY);
			} else {
				ABook.ObjNew(nType, nX1 + ADX, nY1 + ADY, nX2 + ADX, nY2 + ADY,
						nNewId);
				ABook.ObjSetSelection(nNewId);
			}
			if (bUseLineStyle || bUsePattern) {
				ABook.ObjSetSelection(nNewId);
				if (bUseLineStyle)
					ABook.SetLineStyle(nLineStyle, new Color(nLineColor),
							nLineWeight);
				if (bUsePattern)
					ABook.SetPattern(nPattern, new Color(nPatFG), new Color(
							nPatBG));
			}
		} catch (Exception E) {
		}
	}

}
