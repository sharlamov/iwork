package com.report.work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TRepJumpSrcList {

	List<TRowRepJumpSrc> FJumps;

	// ---------------------------------------------------------------------------
	void Clear() {
		if (FJumps == null)
			return;

		FJumps.clear();
	}

	// ---------------------------------------------------------------------------
	int GetJumpCount() {
		return FJumps != null ? FJumps.size() : 0;
	}

	// ---------------------------------------------------------------------------
	TRowRepJumpSrc GetJumps(int AIndex) {
		if (FJumps != null && (AIndex >= 0) && (AIndex < FJumps.size()))
			return FJumps.get(AIndex);
		return null;
	}

	// ---------------------------------------------------------------------------
	void ScanCell(TAnyRow AOwnerRow, int ACol, String AText,
			TRowRepJumpSrc ARepJumps) {
		if (AText.indexOf("[RepJump]") != -1) {
			List<String> Src = Arrays.asList(AText.split(" "));
			int nIndex = Src.indexOf("[RepJump]");
			if (nIndex >= 0) {
				List<String> Tgt = new ArrayList<String>();
				nIndex++;

				while (nIndex < Src.size()) {
					String asLine = Src.get(nIndex);
					if (asLine.isEmpty() && Tgt.size() > 0) {// Пустая строка
																// удаляется, но
																// не
																// переносится в
																// Tgt
						Src.remove(nIndex);
						break;
					}
					if (asLine.indexOf("=") < 2)
						break;
					Tgt.add(asLine);
					Src.remove(nIndex);
				}

				if (!Tgt.isEmpty()) {
					Src.remove(--nIndex);// Удаление строки [RepJump]

					if (nIndex > 0) { // Проверка предыдущей строки
						String asLine = Src.get(--nIndex);
						if (asLine.isEmpty())
							Src.remove(nIndex); // Удаление пустой строки перед
												// строкой [RepJump]
					}

					if (ARepJumps == null) {
						if (FJumps == null)
							FJumps = new ArrayList<TRowRepJumpSrc>();
						ARepJumps = new TRowRepJumpSrc(AOwnerRow);
						FJumps.add(ARepJumps);
					}

					if (ACol < 0)
						ARepJumps.Setup(Tgt);
					else
						ARepJumps.AddCell(ACol, Tgt);

					AText = "";
					for (String s : Src) {
						AText += s + " ";
					}
					AText = AText.trim();
				}

			}
		}

	}

}
