package com.report.work;

import com.report.book.TF1Book6;
import com.report.data.TDataSet;
import com.report.data.TField;
import com.report.enums.TGrpType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TfrmF1Container {

    static String asMergedCellPattern = "Cannot change part of a merged cell";
    static String asMergedCellMessage = "\nUse NOCOPYROWFORMAT=ON in PARAMS section of template";
    // ---------------------------------------------------------------------------
    static String asPAGEBREAK = "PAGEBREAK";
    static String asNOHEADER = "NOHEADER";
    static String asBELOW = "BELOW";
    static String asTITLE = "TITLE";
    static String asPAGE = "PAGE";

    // //////// Переменные для функции ShowF1Report() ////////
    TF1Book6 Dst, Src;
    public TDataSet dataHeader, dataMaster, dataDetail;
    boolean bNoCopyCellFormat, bNoCopyRowFormat, bTestForCopyRowFormat,
            bIsRowEmpty, bPreserveSysCols, bSetRowHeightAuto,
            bSupressBlankLines, bAllowInCellEditing, bAlwaysShowHeading,
            bUseRowColor, bUseCellColor, SmartDate, bSetColWidthAuto,
            bJoinValues, bJoinEnforced, bShowZeroValues;
    double minFloat;
    int StartPageHead, EndPageHead;
    List<String> JoinFields, TitleSpec;
    String Formulas[] = new String[Util.TEMPLATE_MAX_ROWS];
    String Expressions[] = new String[Util.TEMPLATE_MAX_ROWS];
    String Values[] = new String[Util.TEMPLATE_MAX_ROWS];
    int nLastRow, nLastCol, FirstCol;
    int StartRow, CurrRow, CurrCol, SrcRow, TableRow, TitleRow;

    TRepJumpSrcList RepJumps;
    TGraphObjList GraphObjList;
    TDetailList Detail1, Detail2;
    TGrpList Grp1, Grp2;
    TTitle Title;
    TSum Sum;

    TDataSet FAdditionalDS;

    // bool SwModalShow;
    boolean bUseSpredSheetFormat;
    int TemplType;

    public TfrmF1Container() {
        TemplType = 0;
        minFloat = 0d;// 0.0000000001;
        RepJumps = new TRepJumpSrcList();
        GraphObjList = new TGraphObjList();
        Detail1 = new TDetailList();
        Detail2 = new TDetailList();
        Grp1 = new TGrpList();
        Grp2 = new TGrpList();
        Title = new TTitle();
        Sum = new TSum();
        JoinFields = new ArrayList<String>();
    }

    // ---------------------------------------------------------------------------
    void CopyCellFormats(int pRowS, int pColS, int pRowD, int pColD) {
        if (bNoCopyCellFormat)
            return;
        Dst.setCellStyle(Src.getCellStyle(pRowS, pColS), pRowS, pColS);
    }

    // ---------------------------------------------------------------------------
    void CopyCellValue(int pRowS, int pColS, int pRowD, int pColD) {
        if (Src.textRC(pRowS, pColS).isEmpty())
            return;

        switch (Src.typeRC(pRowS, pColS)) {
            case 1: // Number
                Dst.setNumberRC(Src.numberRC(pRowS, pColS), pRowD, pColD);
                break;
            case 2: // Text
                Dst.setTextRC(Src.textRC(pRowS, pColS), pRowD, pColD);
                break;
            case 3: // Logical
                Dst.setTextRC(Src.textRC(pRowS, pColS), pRowD, pColD);
                break;
            case 4: // Error
                break;
            case -1: // Formula
            case -2:
            case -3:
            case -4:
                Dst.setFormulaRC(Src.FormulaRC(pRowS, pColS), pRowD, pColD);
                break;
        } // end of switch
    } // CopyCellCopyCellValue()
    // ---------------------------------------------------------------------------

    void CopyCellEasy(int pRowS, int pColS, int pRowD, int pColD) {
        try {
            CopyCellValue(pRowS, pColS, pRowD, pColD);
        } catch (Exception e) {
        } finally {
            if (!bNoCopyCellFormat && bNoCopyRowFormat)
                CopyCellFormats(pRowS, pColS, pRowD, pColD);
        }
    }

    void SetupRowPagebreaks(int RowS) {
        if (Values[RowS].contains(asPAGEBREAK))
            if (Values[RowS].contains(asBELOW))
                Dst.AddRowPageBreak(CurrRow + 1);
            else
                Dst.AddRowPageBreak(CurrRow);
    }

    // ---------------------------------------------------------------------------
    void CopyRow(int RowS) {
        Dst.setRowHidden(Src.getRowHidden(RowS), CurrRow);
        if (bNoCopyCellFormat)
            return;

        if (bNoCopyRowFormat) {
            int nLastColForRow = Src.LastColForRow(RowS);
            for (int nCol = 1; nCol <= nLastColForRow; nCol++)
                CopyCellFormats(RowS, nCol, CurrRow, nCol);
        } else {
            Dst.copyRow(CurrRow, RowS, 1);
        }
    }

    void CheckForMergedCells() {
        bNoCopyRowFormat = Src.getNumMergedRegions() > 0;
    }

    void AddTitle(int ARow) {
        String asExpression = Expressions[ARow];
        String asValue = Values[ARow];
        Title.Add(ARow, asExpression, asValue);
    }

    // ---------------------------------------------------------------------------
    void AddGrp(TGrpType AType, int ARow) {
        TGrp Grp;
        String asExpression = Expressions[ARow];
        String asValue = Values[ARow];
        Grp = Grp1.Add(AType, ARow, asExpression, asValue);
        if (Grp == null && dataDetail != null)
            Grp = Grp2.Add(AType, ARow, asExpression, asValue);
    }

    // ---------------------------------------------------------------------------
    // SetupParams() вызывается из ShowF1Report() для инициализации данных
    // ---------------------------------------------------------------------------
    void SetupParams() {
        FirstCol = Util.TEMPLATE_FORMULAS_COLS + 1;
        nLastRow = Src.LastRow();
        nLastCol = Src.LastCol();

        GraphObjList.Scan(Src);

        RepJumps.Clear();
        Detail1.clear();
        Detail2.clear();
        Grp1.Clear();
        Grp2.Clear();
        Title.clear();
        Sum.Clear();
        JoinFields.clear();

        Detail1.DataSet = dataMaster;
        Detail2.DataSet = dataDetail;
        Grp1.DataSet = dataMaster;
        Grp2.DataSet = dataDetail;
        Title.DataSet = dataHeader;
        Sum.DataSet = dataHeader;

        StartPageHead = 0;
        EndPageHead = 0;

        CurrRow = 1;
        TitleRow = 0;

        for (int i = 1; i <= nLastRow; i++) {
            String asFormula = Src.textRC(i, 0).toUpperCase();
            String asExpression = Src.textRC(i, 1).toUpperCase();
            String asValue = Src.textRC(i, 2).toUpperCase();
            Formulas[i] = asFormula;
            Expressions[i] = asExpression;
            Values[i] = asValue;

            if (asFormula.equalsIgnoreCase("PARAMS")) {
                if (asExpression.equalsIgnoreCase("SETROWHEIGHTAUTO"))
                    Util.SetBoolValue(bSetRowHeightAuto, asValue);
                else if (asExpression.equalsIgnoreCase("SETCOLWIDTHAUTO"))
                    Util.SetBoolValue(bSetColWidthAuto, asValue);
                else if (asExpression.equalsIgnoreCase("SUPRESSBLANKLINES"))
                    Util.SetBoolValue(bSupressBlankLines, asValue);
                else if (asExpression.equalsIgnoreCase("NOCOPYROWFORMAT")) {
                    Util.SetBoolValue(bNoCopyRowFormat, asValue);
                    bTestForCopyRowFormat = false;
                } else if (asExpression.equalsIgnoreCase("PRESERVESYSCOLS"))
                    Util.SetBoolValue(bPreserveSysCols, asValue);
                else if (asExpression.equalsIgnoreCase("USEROWCOLOR"))
                    Util.SetBoolValue(bUseRowColor, asValue);
                else if (asExpression.equalsIgnoreCase("USECELLCOLOR"))
                    Util.SetBoolValue(bUseCellColor, asValue);
                else if (asExpression.equalsIgnoreCase("SMARTDATE"))
                    Util.SetBoolValue(SmartDate, asValue);
                else if (asExpression.equalsIgnoreCase("JOINVALUES"))
                    Util.SetBoolValue(bJoinValues, asValue); //  Объединение повторяющейся аналитики
                else if (asExpression.equalsIgnoreCase("JOINENFORCED"))
                    Util.SetBoolValue(bJoinEnforced, asValue); //  Принудительное объединение
                else if (asExpression.equalsIgnoreCase("JOINFIELDS"))
                    JoinFields = Arrays.asList(asValue.split(","));  //  Список полей аналитики
                else if (asExpression.equalsIgnoreCase("SHOWZEROVALUES"))
                    Util.SetBoolValue(bShowZeroValues, asValue); //  Отображение значений "0"
            } else if (asFormula.equalsIgnoreCase("TITLE")) { //  В этом типе строк параметр bSupressBlankLines всегда равен false
                boolean bSaved = bSupressBlankLines;
                bSupressBlankLines = false;
                AddTitle(i);
                bSupressBlankLines = bSaved;
            } else if (asFormula.equalsIgnoreCase("TITLESPEC1")) {
                if (TitleRow == 0)
                    TitleRow = i;
            } else if (dataMaster != null && asFormula.equalsIgnoreCase("DETAIL1")) { //  В этом типе строк параметр bSupressBlankLines всегда равен false
                boolean bSaved = bSupressBlankLines;
                bSupressBlankLines = false;
                Detail1.Add(i, asExpression, asValue);
                bSupressBlankLines = bSaved;
            } else if (dataDetail != null && asFormula.equalsIgnoreCase("DETAIL2"))
                Detail2.Add(i, asExpression, asValue);
            else if (dataMaster != null && (asFormula.equalsIgnoreCase("GROUPH")))
                AddGrp(TGrpType.gtHeader, i);
            else if (dataMaster != null && (asFormula.equalsIgnoreCase("GROUPF")))
                AddGrp(TGrpType.gtFooter, i);
            else if (asFormula.equalsIgnoreCase("SUMMARY"))
                Sum.Add(i, asExpression, asValue);
        }

        if (Grp1.PivotGrp != null)
            Grp1.PivotGrp.PivotInitInfo();

        if (!bNoCopyRowFormat && bTestForCopyRowFormat)
            CheckForMergedCells();

		  /*
            int nOn = 0, nOff = 0;
		  FOneReport.unwSource.CallBase(null, "GetF1Options", nOn, nOff, null);
		  if (nOn != 0)
		  {
		    bSetRowHeightAuto  |= (nOn & f1roSetRowHeightAuto);
		    bSetColWidthAuto   |= (nOn & f1roSetColWidthAuto);
		    bSupressBlankLines |= (nOn & f1roSupressBlankLines);
		    bPreserveSysCols   |= (nOn & f1roPreserveSysCols);
		    bNoCopyCellFormat  |= (nOn & f1roNoCopyCellFormat);
		    bNoCopyRowFormat   |= (nOn & f1roNoCopyRowFormat);
		    bUseRowColor       |= (nOn & f1roUseRowColor);
		    bUseCellColor      |= (nOn & f1roUseCellColor);
		    SmartDate         |= (nOn & f1roSmartDate);
		    bJoinValues        |= (nOn & f1roJoinValues);
		    bJoinEnforced      |= (nOn & f1roJoinEnforced);
		    bShowZeroValues    |= (nOn & f1roShowZeroValues);
		  }
		  if (nOff != 0)
		  {
		    bSetRowHeightAuto  &= !(nOff & f1roSetRowHeightAuto);
		    bSetColWidthAuto   &= !(nOff & f1roSetColWidthAuto);
		    bSupressBlankLines &= !(nOff & f1roSupressBlankLines);
		    bPreserveSysCols   &= !(nOff & f1roPreserveSysCols);
		    bNoCopyCellFormat  &= !(nOff & f1roNoCopyCellFormat);
		    bNoCopyRowFormat   &= !(nOff & f1roNoCopyRowFormat);
		    bUseRowColor       &= !(nOff & f1roUseRowColor);
		    bUseCellColor      &= !(nOff & f1roUseCellColor);
		    SmartDate         &= !(nOff & f1roSmartDate);
		    bJoinValues        &= !(nOff & f1roJoinValues);
		    bJoinEnforced      &= !(nOff & f1roJoinEnforced);
		    bShowZeroValues    &= !(nOff & f1roShowZeroValues);
		  }
		  */
    }

    // ---------------------------------------------------------------------------
    // Изменения делаются в Src, потом они скопируются в Dst,
    // а Src закроем без сохранения
    // ---------------------------------------------------------------------------
    void ModifyTemplate() {
        if (bPreserveSysCols) // Вместо удаления первых трех столбцов скрываем
            // их:
            Src.SetColWidth(1, Util.TEMPLATE_FORMULAS_COLS, 0, false);
        else {
            Src.deleteLine(0, Util.TEMPLATE_FORMULAS_COLS, true);
            nLastCol = Src.LastCol();
            FirstCol = 1;
        }
        Src.setShowHeading(false);
    }

    // ---------------------------------------------------------------------------
    void CopyColumnWidths() {
        for (int i = 1; i <= nLastCol; i++)
            Dst.setColWidth(Src.getColWidth(i), i);
    }

    // ---------------------------------------------------------------------------
    void InitPrintScale(TF1Book6 ASrc, TF1Book6 ADst) {
        Integer pScale = null;
        Integer pFitToPage = null;
        Integer pVPages = null, pHPages = null;
        //???ASrc.GetPrintScale(pScale, pFitToPage, pVPages, pHPages);
        //???ADst.SetPrintScale(pScale, pFitToPage, pVPages, pHPages);
        ADst.SetPageSetup(ASrc.GetPageSetup());
    }

    // ---------------------------------------------------------------------------
    // Функция построения отчета по шаблону - сердце компонента FOneReport
    // ---------------------------------------------------------------------------
    public void OpenF1Rep(TF1Book6 from, TF1Book6 in, TDataSet pttMaster, TDataSet pttDetail, String pReportTitle,
                          TDataSet pttHeader, TDataSet pAdditionalDS) throws Exception {
        if (pttMaster == null && pttHeader == null)
            throw new Exception("Both Master and Header datasets are null");

        dataMaster = pttMaster;
        dataDetail = pttDetail;
        dataHeader = pttHeader;
        FAdditionalDS = pAdditionalDS;
        TAddCellValue.Init(pAdditionalDS);
        TAnyRow.F1C = this;

        boolean retVal = false;
        //static const int nMaxRow = 65536;
        Src = from;
        Dst = in;
        in.insertSheets(1);//sh
        //-------------------------------------------------------- variable declarations
        double ProgrCPos = 1, ProgrStep;
        //-------------------------------------------- variables for printing the groups
        //-------------------------------------------------------------- Like parameters
        String pPrintTitles;
        //------------------------------------------------- end of variable declarations
        //test..!#endif
        try // finally
        {
            try // catch
            {
                //TEchoDataSet _ptt1(ptt1), _ptt2(ptt2);
                //--------------------------------------------------------- start initialization
                ////PSet.SwitchTo(0);
                SetupParams();
                ModifyTemplate();
                CopyColumnWidths();
                //------------------------------------------------------------------------
                ProgrStep = 100;
                int nMasterRecs = 0;
                if (dataMaster != null) {
                    nMasterRecs = dataMaster.RecordCount();
                    ProgrStep = 100. / nMasterRecs;
                }

                //------------------------------------------------------------------------
                Title.MakeRows();  //  Формирование шапки
                //------------------------------------------------------------------------
                TableRow = CurrRow;
                Grp1.Reset();
                TAnyRow.Last.Same = false;
                //------------------------------------------------------------------------
                int nMasterRec = 0;
                while (dataMaster != null && !dataMaster.Eof())  //  Цикл по master-записям
                {
                    nMasterRec++;
                    if (nMasterRecs != 0 && (nMasterRec > nMasterRecs))
                        break;
                    Grp1.MakeHeaderRows();
                    Detail1.MakeRows();
                    Grp1.Increment(dataMaster);
                    Sum.Increment(dataMaster);
                    if (dataDetail != null)//(Detail2->Count) //  Цикл по detail-записям
                    {
                        Grp2.Reset();
                        while (!dataDetail.Eof()) {
                            Grp2.MakeHeaderRows();
                            Detail2.MakeRows();
                            Grp2.Increment(dataDetail);
                            Sum.Increment(dataDetail);
                            dataDetail.Next();
                            Grp2.MakeFooterRows();
                        }
                    }
                    dataMaster.Next();
                    Grp1.MakeFooterRows();
                    ProgrCPos += ProgrStep;
                }  // while() : scan on table
                //------------------------------------------------------------------------
                Sum.MakeRows();
                //---------------------------------------- rest of the book's parameters
                Dst.setShowHeading(Src.getShowHeading());
                Dst.setAllowInCellEditing(false);
                Dst.setShowGridLines(false);
                Dst.setShowZeroValues(bShowZeroValues);

                if (Dst.getSheetName(Dst.getActiveSheet()).indexOf("Sheet") == 0)
                    Dst.setSheetName(Src.getSheetName(Src.getActiveSheet()), Dst.getActiveSheet());
                if (StartPageHead > 0) {
                    pPrintTitles = Dst.getSheetName(Dst.getActiveSheet()) + "!$A$" + StartPageHead + ":$IV$" + EndPageHead;
                    Dst.setPrintTitles(pPrintTitles);
                } else
                    Dst.setPrintTitles("");
                //----------------------------------------------- adjust the columns width
                if (bSetRowHeightAuto && CurrRow != 0)
                    Dst.setRowHeightAuto(1, CurrRow);
                if (bSetColWidthAuto && Dst.LastCol() != 0)
                    Dst.setColWidthAuto(FirstCol, Dst.LastCol());
                //---------------------------------------------------------------- page settings
                InitPrintScale(Src, Dst);
                retVal = true;
            } catch (Exception E) {
                throw E;
            }
            if (retVal) {
                if (CurrRow < Util.nMaxRow)
                    try {
                        Dst.deleteLine(CurrRow + 1, Util.nMaxRow - CurrRow, false);
                    } catch (Exception e) {
                    }
                Dst.setActiveSheet(0);
            }
        } finally {
            GraphObjList.clear();
            Detail1.clear();
            Detail2.clear();
            Grp1.clear();
            Grp2.Clear();
            Title.clear();
            Sum.Clear();
        } //  finally
    } // ShowF1Report()

    // ---------------------------------------------------------------------------

    // ---------------------------------------------------------------------------
    TField FindField(TDataSet ADataSet, String AFieldName) {
        TField Field = null;
        while (ADataSet != null) {
            Field = ADataSet.FieldByName(AFieldName);
            if (Field != null)
                break;
            if ((ADataSet == dataDetail) && (ADataSet != dataMaster))
                ADataSet = dataMaster;
            else if ((ADataSet == dataMaster) && (ADataSet != dataHeader))
                ADataSet = dataHeader;
            else
                ADataSet = null;
        }
        return Field;
    }

    // ---------------------------------------------------------------------------
    void FillCurrCol(List<String> pParams,
                     TLongIntItemsList pRowList, int pTopSRow, int pBottomSRow, int newRow) {
        Integer pLeft = null, pRight = null, pTop = null, pBottom = null, pShade = null;
        Integer opLeft = null, opRight = null, opTop = null, opBottom = null, opShade = null;
        Integer pcrLeft = null, pcrRight = null, pcrTop = null, pcrBottom = null;
        Integer opcrLeft = null, opcrRight = null, opcrTop = null, opcrBottom = null;

        for (int i = 0; i < pParams.size(); i++) {
            int j = 1 + TitleSpec.indexOf(pParams.get(i));
            if (j == 0)
                continue;
            pRowList.GoTop();
            int nRow = StartRow;
            while (!pRowList.Eol) {
                CopyCellEasy((int) pRowList.CurrIndex(), j, nRow, CurrCol);
                CopyCellFormats((int) pRowList.CurrIndex(), j, nRow, CurrCol);
                pRowList.Next();
                nRow++;
            }
            Dst.setColWidth(Src.getColWidth(j), CurrCol);
            if (newRow > 0) {
                if (pRowList != null && pRowList.Count > 0) {
                    CopyCellEasy(newRow, j, StartRow, CurrCol);
                    CopyCellFormats(newRow, j, StartRow, CurrCol);
                }
            }
            // draw group's top and bottom borders
            if (pRowList.Count > 0 && pTopSRow > 0 && pBottomSRow > 0) {
                Src.setActiveCell(pTopSRow, j);
                Src.getBorder(pTopSRow, j, pLeft, pRight, pTop, pBottom, pcrLeft, pcrRight, pcrTop, pcrBottom);

                Dst.setActiveCell(StartRow, CurrCol);
                Dst.getBorder(StartRow, CurrCol, opLeft, opRight, opTop, opBottom, opcrLeft, opcrRight, opcrTop, opcrBottom);
                Dst.setBorder(StartRow, CurrCol, opLeft, opRight, pTop, opBottom, opcrLeft, opcrRight, pcrTop, opcrBottom);

                Src.setActiveCell(pBottomSRow, j);
                Src.getBorder(pBottomSRow, j, pLeft, pRight, pTop, pBottom, pcrLeft, pcrRight, pcrTop, pcrBottom);

                Dst.setActiveCell(nRow - 1, CurrCol);
                Dst.getBorder(nRow - 1, CurrCol, opLeft, opRight, opTop, opBottom, opcrLeft, opcrRight, opcrTop, opcrBottom);
                Dst.setBorder(nRow - 1, CurrCol, opLeft, opRight, opTop, pBottom, opcrLeft, opcrRight, opcrTop, pcrBottom);
            }
            //
            CurrCol++;
        }
    } //  end of for (i = 0; i < pParams.Count; i++)

    // ---------------------------------------------------------------------------
    boolean AddNewSheet(TF1Book6 pF1Book1, String pSheetName) {
        pF1Book1.insertSheets(1);
        pF1Book1.setActiveSheet(pF1Book1.countSheets());
        pF1Book1.setSheetName(pSheetName, pF1Book1.countSheets());
        return true;
    }

    // ---------------------------------------------------------------------------
    int GetMaxObjId(TF1Book6 ABook) {
        int nId = 0;

        try {
            nId = ABook.ObjFirstID();
            while (true)
                nId = ABook.ObjNextID(nId);
        } catch (Exception e) {
        }

        return nId;
    }

    // ---------------------------------------------------------------------------
    boolean InitLogoInfo(TF1Book6 ABook, Rectangle Info) {
        boolean bResult = false;
        List<String> list;
        String sValue = "";

        int iMaxCol = Math.min(ABook.LastCol() + 1, 50);
        int iMaxRow = Math.min(ABook.LastRow() + 1, 50);

        for (int i = 1; i < iMaxRow; i++)
            for (int y = 1; y < iMaxCol; y++) {
                try {
                    sValue = ABook.textRC(i, y);
                } catch (Exception e) {
                }

                int len = sValue.length();

                if (len < 10)
                    continue;

                String startValue = sValue.trim().substring(0, 4).toLowerCase();
                if (startValue == "[logo" || startValue == "[ log") {
                    Info.x = i;
                    Info.y = y;
                    try {
                        sValue = sValue.trim().substring(6);
                        list = Arrays.asList(sValue.trim().split(","));
                        Info.width = Integer.valueOf(list.get(0));
                        Info.height = Integer.valueOf(list.get(1));
                        bResult = true;
                    } finally {
                        list = null;
                    }
                    break;
                }
            }
        return bResult;
    }

}
