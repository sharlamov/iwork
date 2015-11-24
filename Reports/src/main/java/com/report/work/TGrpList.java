package com.report.work;

import com.report.data.TDataList;
import com.report.data.TDataSet;
import com.report.enums.TGrpType;

import java.util.ArrayList;

public class TGrpList extends ArrayList<TGrp> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    TDataSet DataSet;
    public TEchoDataSet Echo;
    int nLastChanged;
    TGrpType WorkType;
    TGrp PivotGrp;

    TGrp GetItems(int AIndex) {
        return get(AIndex);
    }

    void SetItems(int AIndex, TGrp AValue) {
        set(AIndex, AValue);
    }

    TDataSet EchoDataSet() {
        return Echo.DataSet;
    }

    // ---------------------------------------------------------------------------
    void SetDataSet(TDataSet ADataSet) {
        DataSet = ADataSet;
        Echo.DataSet = (TDataList) ADataSet;
    }

    // ---------------------------------------------------------------------------
    void Clear() {
        if (Echo != null)
            Echo.DataSet = null;
        PivotGrp = null;
        clear();
    }

    // ---------------------------------------------------------------------------
    int IndexOf(String AFieldName) {
        for (int i = 0; i < size(); i++)
            if (get(i).FieldName.equals(AFieldName))
                return i;
        return -1;
    }

    // ---------------------------------------------------------------------------
    TGrp ItemByName(String AFieldName) {
        int nIndex = IndexOf(AFieldName);
        return nIndex != -1 ? get(nIndex) : null;
    }

    // ---------------------------------------------------------------------------
    TGrp Add(TGrpType AType, int ARow, String AExpression, String AValue) {
        TGrp Grp;
        if (AExpression.startsWith(TAnyRow.as_))
            if ((AExpression.length() < 2) || (!AExpression.startsWith("_")))
                return null;

        String asFieldName = AExpression.substring(1);
        Grp = ItemByName(asFieldName);
        if (Grp == null) {
            if (DataSet.FieldByName(asFieldName) != null)
                Grp = new TGrp(this, asFieldName);
            else
                return null;

            if (AType == TGrpType.gtHeader)
                add(Grp);
            else
                set(0, Grp);
        }

        Grp.Add(ARow, AExpression, AValue, AType);

        if (PivotGrp == null && Grp.Pivot)
            PivotGrp = Grp;

        return Grp;
    }

    // ---------------------------------------------------------------------------
    void MakeHeaderRows() throws Exception {
        Echo.Populate();
        int nCnt = size();

        // Вывод header-строк в отчет
        WorkType = TGrpType.gtHeader;
        for (int i = nLastChanged; i < nCnt; i++)
            get(i).MakeRows();

        if (PivotGrp != null)
            PivotGrp.PivotCheckInfo();
    }

    // ---------------------------------------------------------------------------
    void MakeFooterRows() throws Exception {
        int nCnt = size();

        // Поиск старшей группировки с измененным значением
        for (nLastChanged = 0; nLastChanged < nCnt; nLastChanged++)
            if (get(nLastChanged).Difference())
                break;

        // Вывод footer-строк в отчет
        WorkType = TGrpType.gtFooter;
        for (int i = nCnt - 1; i >= nLastChanged; i--)
            get(i).MakeRows();

        // Сброс сумм и обновление ключей
        for (int i = nCnt - 1; i >= nLastChanged; i--)
            get(i).Reset();
    }

    // ---------------------------------------------------------------------------
    void Reset() {
        nLastChanged = 0;
        int nCnt = size();
        for (int i = 0; i < nCnt; i++)
            get(i).Reset();
        Echo.clear();
    }

    // ---------------------------------------------------------------------------
    void Increment(TDataSet ADataSet) {
        for (TGrp grp : this)
            grp.Increment(ADataSet);
    }

}
