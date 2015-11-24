package com.report.work;

import com.report.data.TDataSet;
import com.report.data.TField;

import java.util.ArrayList;

public class TAnyRow extends ArrayList<TAnyCell> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final int F1BorderNone = 0;

    int Row, GraphCount, LastOutputRow;
    boolean bSupressBlankLines, bSetRowHeightAuto, bPageBreak,
            bPageBreakNoHeader, bPageBreakBelow;
    TRowCond RowCond;
    TRowRepJumpSrc RepJumps;

    public static TfrmF1Container F1C;
    public static TLastValues Last = new TLastValues();

    TAnyRowList Owner;

    TAnyCell GetCells(int AIndex) {
        return get(AIndex);
    }

    void GetColorFields(TField ABgField, TField AFgField, TDataSet ADataSet,
                        TField AField) {
    }

    public TAnyRow(TAnyRowList AOwner, int ARow, String AExpr, String AValue) {
        Owner = AOwner;
        Row = ARow;
        RowCond = TRowCond.CreateFor(AValue);
        GraphCount = F1C.GraphObjList.GetCountForRow(Row);
        bSupressBlankLines = F1C.bSupressBlankLines;
        bSetRowHeightAuto = AValue.contains("SETROWHEIGHTAUTO");
        if (AExpr.contains(Util.asPAGEBREAK)) {
            bPageBreak = true;
            bPageBreakBelow = true;
        } else if (AValue.contains(Util.asPAGEBREAK)) {
            bPageBreak = true;
            bPageBreakNoHeader = AValue.contains(Util.asNOHEADER);
            bPageBreakBelow = AValue.contains(Util.asBELOW);
        }
    }

    // ---------------------------------------------------------------------------
    boolean Used() throws Exception {
        return (RowCond != null && GetDataSet() != null) ? RowCond
                .RowUsed(GetDataSet()) : true;
    }

    // ---------------------------------------------------------------------------
    void CreateCells() {
        for (int i = 1; i <= F1C.nLastCol; i++) { // i - номер столбца в
            // шаблоне, nCol - номер
            // столбца в отчете
            int nCol = i - F1C.FirstCol;
            String asEntry = F1C.Src.textRC(Row, i).trim();
            if (asEntry.isEmpty())
                continue;

            // Сложные формулы можно вводить как "'=[A010]-[A020]" - с символа
            // '\''
            if ((asEntry.charAt(1) == '\'') && (asEntry.length() > 2)
                    && (asEntry.charAt(2) == '=') && (asEntry.charAt(3) != '='))
                asEntry = asEntry.substring(1); // Убираем ведущий апостроф

            if (nCol >= -1) { // Нас интересуют все столбцы, начиная с третьего
                // системного
                F1C.RepJumps.ScanCell(this, nCol, asEntry, RepJumps);
                if (asEntry.isEmpty()) // Здесь asEntry может быть уменьшен
                    continue;
            }

            TAnyCell Cell = Parse(nCol, asEntry, (asEntry.charAt(1) == '='));
            if (Cell != null)
                add(Cell);
        }
    }

    // ---------------------------------------------------------------------------
    TDataSet GetDataSet() {
        return Owner != null ? Owner.DataSet : null;
    }

    // ---------------------------------------------------------------------------
    public static boolean IsDetail(TDataSet ADataSet) {
        return ADataSet.equals(F1C.dataDetail);
    }

    // ---------------------------------------------------------------------------
    static int GetWordLength(String AText, int APos) {
        int nLen = AText.length();
        for (int i = APos; i <= nLen; i++)
            if (!Util.cAlpha.contains(AText.charAt(i) + ""))
                return i - APos;
        return nLen - APos + 1;
    }

    // ---------------------------------------------------------------------------
    static String as_ = "_";
    // ---------------------------------------------------------------------------
    static TRefInfo RefInfo;

    // ---------------------------------------------------------------------------
    // Функция находит в оставшейся части строки выражения типа ссылки на ячейку
    // Поддерживается относительная и абсолютная адресация (A1, B$2, $C4, $D$6)
    // Поддерживаются интервалы типа C2:E8 (как две разные ссылки)
    // "Бесконечные" интервалы (типа C:E, 3:5) ПОКА не поддерживаются
    // nPos1 и nPos2 - позиции найденных слов (после них нет смысла искать)
    // ---------------------------------------------------------------------------
    static int FindRef(String AText, int APos, int APos1, int APos2) {
        int nLen = APos1 != 0 ? APos1 : AText.length();
        if (APos2 != 0 && (APos2 < APos1))
            nLen = APos2;
        boolean bInString = false;
        RefInfo.clear();
        while (++APos <= nLen) {
            char cSym = AText.charAt(APos);
            if (cSym == '\"') {
                bInString = !bInString;
                if (!bInString)
                    RefInfo.clear();
                continue;
            }
            if (bInString)
                continue;
            String asSym = ("" + cSym).toUpperCase();
            if (cSym == '$') {
                if (RefInfo.nPhase == 0) {
                    RefInfo.bAbsCol = true;
                    RefInfo.nPhase = 1;
                    RefInfo.nPos = APos;
                } else if ((RefInfo.nPhase == 2) || (RefInfo.nPhase == 3)) {
                    RefInfo.bAbsRow = true;
                    RefInfo.nPhase = 4;
                } else
                    RefInfo.clear();
            } else if (Character.isLetterOrDigit(cSym)) // Это буква?
            {
                if (RefInfo.nPhase < 2) {
                    RefInfo.asCol = asSym;
                    RefInfo.nPhase = (RefInfo.asCol.charAt(0) <= 'I') ? 2 : 3;
                    if (!RefInfo.bAbsCol)
                        RefInfo.nPos = APos;
                } else if ((RefInfo.nPhase == 2)
                        && ((RefInfo.asCol.charAt(0) < 'I') || (asSym.charAt(0) <= 'V'))) {
                    RefInfo.asCol += asSym;
                    RefInfo.nPhase++;
                } else
                    RefInfo.clear();
            } else if (!Character.isLetterOrDigit(cSym)
                    && (RefInfo.nPhase >= 2) && (RefInfo.nPhase <= 5)) {
                if (RefInfo.nPhase < 5) {
                    RefInfo.asRow = asSym;
                    RefInfo.nPhase = 5;
                } else {
                    RefInfo.asRow += asSym;
                    if ((RefInfo.asRow.length() == 5)
                            && (Integer.valueOf(RefInfo.asRow) > 65536))
                        RefInfo.nPhase = 6; // Перебор
                }
            } else if (RefInfo.nPhase == 5) // Формула готова
                break;
            else if (RefInfo.nPhase != 0)
                RefInfo.clear();
        }
        if (RefInfo.nPhase == 5) {
            RefInfo.nLen = APos - RefInfo.nPos;
            return RefInfo.nPos;
        }
        return 0;
    }

    // ---------------------------------------------------------------------------
    // Функция находит в оставшейся части строки слово, начинающееся с символа
    // '_'
    // Текст в кавычках пропускается без анализа
    // ---------------------------------------------------------------------------
    static int FindToken(String AText, int APos) {
        int nLen = AText.length();
        boolean bInString = false;
        boolean bFirstSym = true;
        while (++APos <= nLen) {
            char cSym = AText.charAt(APos);
            if (cSym == '\"') {
                bInString = !bInString;
                if (!bInString)
                    bFirstSym = true;
                continue;
            }
            if (bInString)
                continue;
            if (bFirstSym && (cSym == '_'))
                return APos;
            bFirstSym = !Character.isLetterOrDigit(cSym);
        }
        return 0;
    }

    // ---------------------------------------------------------------------------
    // Функция находит в оставшейся части строки слово в квадратных скобках
    // Текст в кавычках (до открывающей скобки!) пропускается без анализа
    // ALast - номер последнего символа, до которого имеет смысл искать
    // ---------------------------------------------------------------------------
    static int nAddOpenPos, nAddClosePos;
    static String asAddFieldName;

    static int FindOpenBracket(String AText, int APos, int ALast) {
        nAddOpenPos = 0;
        nAddClosePos = 0;
        asAddFieldName = "";
        int nLen = ALast != 0 ? ALast : AText.length();
        boolean bInString = false;
        while (++APos <= nLen) {
            char cSym = AText.charAt(APos);
            if (cSym == '\"') {
                if (nAddOpenPos != 0) {
                    nAddOpenPos = 0; // Никаких кавычек внутри квадратных
                    // скобок!
                    asAddFieldName = "";
                }
                bInString = !bInString;
                continue;
            }
            if (bInString)
                continue;
            if (nAddOpenPos == 0) {
                if (cSym == '[')
                    nAddOpenPos = APos;
                continue;
            }
            if (cSym == ']') {
                asAddFieldName = asAddFieldName.trim();
                nAddClosePos = APos;
                return nAddOpenPos;
            } else if ((cSym == ' ') || Character.isLetterOrDigit(cSym)) {
                asAddFieldName += cSym;
                continue;
            }
            nAddOpenPos = 0;
            asAddFieldName = "";
        }
        return 0;
    }

    // ---------------------------------------------------------------------------
    // Функция просто создает объект AnyCell, если он еще не создан
    // ---------------------------------------------------------------------------
    static TAnyCell ensureCellExists(TAnyCell AnyCell, TAnyRow AnyRow, int ACol, boolean AUseFormula) {
        return AnyCell == null ? new TAnyCell(AnyRow, ACol, AUseFormula) : AnyCell;
    }

    // ---------------------------------------------------------------------------
    // Функция TAnyRow::Parse() разбирает текст одной ячейки шаблона
    // и возвращает указатель на объект TAnyCell, который сама же
    // создает и заполняет при необходимости. Объект TAnyCell заполняется
    // объектами-потомками класса TAnyCellValue.
    // Различаются следующие типы значений (потомков класса TAnyCellValue):
    // - TStrCellValue - неинтерпретируемое строковое значение
    // - TFldCellValue - значение объекта TField
    // - TSumCellValue - значение суммы, накопленной в объекте TFieldSum
    // - TRefCellValue - ссылка на другую ячейку шаблона
    // Виртуальный метод ParseToken() разбирает слово, нач. с символа '_',
    // и возвращает соответствующий ему объект TAnyCellValue;
    // ---------------------------------------------------------------------------
    TAnyCell Parse(int ACol, String AText, boolean AUseFormula) {
        TAnyCell acResult = null;
        int nPos = 0;
        while ((ACol >= 0) && (nPos < AText.length())) // Не для системных
        // столбцов
        {
            TAnyCellValue cv = null;
            // Поиск символа подчеркивания в начале слова в оставшейся части
            // строки
            int nPos1 = FindToken(AText, nPos);
            // Поиск открывающей квадратной скобки в оставшейся части строки
            int nPos2 = TAddCellValue.DataSet != null ? FindOpenBracket(AText,
                    nPos, nPos1) : 0;
            // Поиск ссылки на другую ячейку в оставшейся части строки
            int nPos3 = AUseFormula ? FindRef(AText, nPos, nPos1, nPos2) : 0;

            // Выбор наименьшего из ненулевых значений nPos1, nPos2 и nPos3
            if (nPos1 != 0 && (nPos2 == 0 || (nPos2 > nPos1))
                    && (nPos3 == 0 || (nPos3 > nPos1))) {
                int nLen = GetWordLength(AText, nPos1);
                nPos2 = nPos1 + nLen;
                acResult = ensureCellExists(acResult, this, ACol, AUseFormula);
                cv = ParseToken(acResult, AText.substring(nPos1, nPos1 + nLen));
            } else if (nPos2 != 0 && (nPos3 == 0 || (nPos3 > nPos2))) {
                acResult = ensureCellExists(acResult, this, ACol, AUseFormula);
                cv = new TAddCellValue(acResult, asAddFieldName);
                acResult.HasCellJumps = true;
                nPos1 = nPos2;
                nPos2 = nAddClosePos + 1;
            } else if (nPos3 != 0) {
                acResult = ensureCellExists(acResult, this, ACol, AUseFormula);
                cv = new TRefCellValue(acResult, RefInfo);
                nPos1 = nPos3;
                nPos2 = nPos1 + RefInfo.nLen;
            } else
                // Не найдено ни '_', ни '[', ни ссылки на ячейку
                break;

            if (cv == null) // Не создано значимого объекта CellValue
            { // nPos1 - начало найденного фрагмента, nPos2 - символ после него
                nPos = nPos2 - 1;
                continue; // Продолжим поиск дальше
            }

            acResult = ensureCellExists(acResult, this, ACol, AUseFormula);
            if (nPos1 > 1) // Текст перед найденным фрагментом
                acResult.add(new TStrCellValue(acResult, AText.substring(1, nPos1)));
            acResult.add(cv); // Значимый объект CellValue

            AText = AText.substring(nPos2); // Оставшийся текст
            nPos = 0;
        }

        if (!AText.isEmpty()) { // Оставшийся и не интересующий нас текст
            acResult = ensureCellExists(acResult, this, ACol, AUseFormula);
            acResult.add(new TStrCellValue(acResult, AText));
        }

        if (acResult != null && acResult.isEmpty()) { // Если каким-то образом
            // объект acResult
            // создан, но пуст -
            // удалим его
            return null;
        }

        acResult.CheckProps(); // Итоговая проверка свойств (например, FIsField)

        return acResult;
    }

    // ---------------------------------------------------------------------------
    TAnyCellValue ParseToken(TAnyCell ACell, String AToken) {
        int nLen = AToken.length();
        if ((nLen < 2) || (AToken.charAt(0) != '_'))
            return null;
        String asFieldName = AToken.substring(1);
        TField Field = FindField(asFieldName);
        if (Field != null)
            return new TFldCellValue(ACell, Field);
        return null;
    }

    // ---------------------------------------------------------------------------
    public TField FindField(String AFieldName) {
        return F1C.FindField(GetDataSet(), AFieldName);
    }

    // ---------------------------------------------------------------------------
    static int FieldColor(TField AField, int ADefault) {
        switch (AField.getDataType()) {
            case ftString:
                return Integer.parseInt("0x" + AField.AsString(), 16);
            case ftInteger:
                return AField.AsInteger();
            case ftFloat:
                return AField.AsInteger();
            default:
                return ADefault;
        }
    }

    // ---------------------------------------------------------------------------
    void MakeRow() throws Exception {
        Integer pHorizontal = null, pVertical = null, pOrientation = null;
        Integer pWordWrap = null;

        LastOutputRow = 0;
        boolean bIsRowEmpty = GraphCount == 0;
        int nDstRow = F1C.CurrRow;
        F1C.CopyRow(Row);
        TDataSet DS = GetDataSet();
        // Поиск полей, определяющих цвета фона и текста строки
        TField fldRowBgColor = null, fldRowFgColor = null;
        if (F1C.bUseRowColor && DS != null)
            GetColorFields(fldRowBgColor, fldRowFgColor, DS, null);

        for (int nCell = size() - 1; nCell >= 0; nCell--) // malloc optimization
        {
            TAnyCell Cell = get(nCell);
            F1C.CurrCol = Cell.Col + F1C.FirstCol;
            if (F1C.CurrCol < 1) // Пропуск системных столбцов
                break;

            // Компиляция содержимого ячейки выполняется здесь:
            String asEntry = Cell.GetValue();

            if (Cell.HasCellJumps)
                Cell.SetupCellJumps();

            if (bIsRowEmpty && bSupressBlankLines && !asEntry.trim().isEmpty())
                bIsRowEmpty = false;
            if (asEntry.isEmpty() && fldRowBgColor == null
                    && !F1C.bUseCellColor)
                continue;

            if (!asEntry.isEmpty() && !F1C.bNoCopyCellFormat && Cell.IsDateTime) { // Даты надо отображать в формате dd.mm.yyyy
                //F1C.Dst.SetSelection(nDstRow, F1C.CurrCol, nDstRow, F1C.CurrCol);
                //bSelected = true;
                String asFormat = F1C.Dst.getCellDataFormat(nDstRow, F1C.CurrCol);
                if (asFormat.equals("General"))
                    F1C.Dst.setCellDataFormat("dd.mm.yyyy", nDstRow, F1C.CurrCol);
            }
            // Поиск полей, определяющих цвета фона и текста ячейки
            if (DS != null && F1C.bUseCellColor && Cell.IsField) {
                TField CellField = ((TFldCellValue) Cell.get(0)).getField();
                GetColorFields(fldRowBgColor, fldRowFgColor, DS, CellField);
            }

            // Установка формата ячейки для SpreadSheet
            if (!asEntry.trim().isEmpty() && !Cell.IsDateTime
                    && F1C.bUseSpredSheetFormat) {
                //F1C.Dst.SetSelection(nDstRow, F1C.CurrCol, nDstRow, F1C.CurrCol);
                String cellMask = F1C.Dst.getCellDataFormat(nDstRow, F1C.CurrCol);

                if (cellMask.equals("General") || asEntry.charAt(0) == '\'') {
                    F1C.Dst.setCellDataFormat("###", nDstRow, F1C.CurrCol);
                    if (Cell.IsFloat) {
                        double fValue = Cell.GetAsFloat();
                        if (fValue - (int) fValue != 0)
                            F1C.Dst.setCellDataFormat("#.####", nDstRow, F1C.CurrCol);
                    }

                    F1C.Dst.GetAlignment(nDstRow, F1C.CurrCol, pHorizontal, pWordWrap, pVertical,
                            pOrientation);
                    try {
                        F1C.Dst.SetAlignment(nDstRow, F1C.CurrCol,
                                pHorizontal == 1 ? 2 : pHorizontal, pWordWrap,
                                pVertical, pOrientation);
                    } catch (Exception e) {
                        F1C.Dst.SetAlignment(nDstRow, F1C.CurrCol, 3, 0, 3, 0);
                    }
                }
            }

            if (asEntry.isEmpty())
                continue;

            // Собственно заполнение содержимого ячейки
            if (Cell.IsString)
                F1C.Dst.setTextRC(asEntry, nDstRow, F1C.CurrCol);
            else if (F1C.bUseSpredSheetFormat && Cell.GetValue().length() > 15
                    && Cell.GetValue().trim().indexOf("=") != 1)
                F1C.Dst.setTextRC(asEntry, nDstRow, F1C.CurrCol);
            else if (Cell.IsInteger || Cell.IsFloat)
                F1C.Dst.setNumberRC(Cell.GetAsFloat(), nDstRow, F1C.CurrCol);
            else
                F1C.Dst.setTextRC(asEntry, nDstRow, F1C.CurrCol);
        }

        if (GraphCount != 0)
            F1C.GraphObjList.CopyForRow(F1C.Dst, Row, F1C.FirstCol - 4, nDstRow
                    - Row);
        else if (bIsRowEmpty && bSupressBlankLines) // else добавлено 02.03.2009
            return;
        // Визуальное объединение повторяющихся ячеек (путем очистки текста и
        // рамок)
        if (F1C.bJoinValues) {
            if (Owner.UseJoin || F1C.bJoinEnforced) {
                boolean bSame = Last.Same;
                for (int nCell = 0; nCell < size(); nCell++) {
                    TAnyCell Cell = GetCells(nCell);
                    if (!Cell.IsField)
                        continue;
                    int nCurrCol = Cell.Col + F1C.FirstCol;
                    if (nCurrCol < 1) // Пропуск системных столбцов
                        continue;
                    String asFieldName = ((TFldCellValue) Cell.get(0))
                            .getFieldName();
                    if (F1C.JoinFields.indexOf(asFieldName) < 0)
                        continue;
                    String asEntry = F1C.Dst.textRC(nDstRow, nCurrCol);
                    if (bSame) {
                        if (Last.Values.get(nCurrCol - 1).equals(asEntry)) {
                            F1C.Dst.setTextRC("", nDstRow, nCurrCol);
                            Integer nLeft = null, nRight = null, nTop = null, nBottom = null, nShade = null;
                            Integer crLeft = null, crRight = null, crTop = null, crBottom = null;
                            //F1C.Dst.SetSelection(nDstRow, nCurrCol, nDstRow, nCurrCol);
                            F1C.Dst.getBorder(nDstRow, nCurrCol, nLeft, nRight, nTop, nBottom, crLeft, crRight, crTop, crBottom);
                            nTop = 0;
                            F1C.Dst.setBorder(nDstRow, nCurrCol, nLeft, nRight, nTop, nBottom, crLeft, crRight, -1, crBottom);
                        } else
                            bSame = false;
                    }
                    Last.Values.set(nCurrCol - 1, asEntry);
                }
                Last.Same = true;
            } else
                Last.Same = false;
        }

        if (!F1C.bNoCopyCellFormat)
            F1C.Dst.setRowHeight(F1C.Src.getRowHeight(Row), nDstRow);
        F1C.CurrRow++;
        if (F1C.CurrRow > Util.nMaxRow)
            throw new Exception("To mutch rows");

        LastOutputRow = nDstRow;

        if (bSetRowHeightAuto && !F1C.bSetRowHeightAuto)
            F1C.Dst.setRowHeightAuto(nDstRow, nDstRow);

        if (bPageBreak)
            if (bPageBreakNoHeader)
                bPageBreakNoHeader = false;
            else
                F1C.Dst.AddRowPageBreak(nDstRow + (bPageBreakBelow ? 1 : 0));
    }

}
