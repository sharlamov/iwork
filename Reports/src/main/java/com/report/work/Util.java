package com.report.work;

public class Util {
	
	public static final int TEMPLATE_FORMULAS_COLS = 3;
	public static final int TEMPLATE_MAX_ROWS = 65536;
	public static final int TEMPLATE_MAX_COLS = 255;
	public static final String asMergedCellPattern = "Cannot change part of a merged cell";
	public static final String asMergedCellMessage = "\nUse NOCOPYROWFORMAT=ON in PARAMS section of template";

	public static final String asPAGEBREAK = "PAGEBREAK";
	public static final String asNOHEADER = "NOHEADER";
	public static final String asBELOW = "BELOW";
	public static final String asTITLE = "TITLE";
	public static final String asPAGE = "PAGE";

	public static final String cAlpha = "_$1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
	public static final String cLetter = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";

	public static final int nMaxRow = 65536;
	
	public static double Round6(double AValue){ 
		return AValue < 0 ? (AValue * 1000000d - 0.5d) / 1000000d : (AValue * 1000000d + 0.5d) / 1000000d; //???????
	}
	
	public static boolean SetBoolValue(boolean AValue, String AText)
	{
	  return AValue ? !AText.equals("OFF") : AText.equals("ON");
	}

	
}
