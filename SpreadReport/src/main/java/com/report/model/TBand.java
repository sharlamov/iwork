package com.report.model;

import com.dao.model.IDataSet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TBand {

    protected final Sheet sheet;
    public TRange range;
    private List<TGroup> hgrps;
    private List<TGroup> fgrps;

    public TBand(Sheet sheet) {
        this.sheet = sheet;
        range = new TRange(sheet);
        hgrps = new LinkedList<>();
        fgrps = new LinkedList<>();
    }

    public void paint(IDataSet dataSet, boolean isFull) {
        if (procGrp()) {
            range.paint(dataSet, isFull);
        } else {
            paintGroup(dataSet, isFull, 0);
        }
    }

    public void paintGroup(IDataSet dataSet, boolean isFull, int i) {
        TGroup grp = fgrps.get(i);
        List<IDataSet> collect = dataSet.groupBy(grp.getFieldName());

        for (IDataSet set : collect) {
            grp.getHeader().paint(set, false);

            if (fgrps.size() - 1 == i) {
                range.paint(set, isFull);
            } else
                paintGroup(set, isFull, i + 1);

            grp.getFooter().paint(set, false);
        }
    }

    public void add(Row row) {
        range.add(row);
    }

    public void addMerged(Sheet oldSheet) {
        range.addMergedRange(oldSheet);
        fgrps.stream().forEach(g -> {
            g.getHeader().addMergedRange(oldSheet);
            g.getFooter().addMergedRange(oldSheet);
        });
    }

    public boolean procGrp() {
        for (int i = 0, count = 0; i < hgrps.size(); i++) {
            TGroup hGrp = hgrps.get(i);
            Optional<TGroup> fGrp = fgrps.stream().filter(grp -> grp.getFieldName().equals(hGrp.getFieldName())).findFirst();
            if (fGrp.isPresent()) {
                fGrp.get().getHeader().addAll(hgrps.get(i).getHeader());
            } else {
                fgrps.add(count++, hGrp);
            }
        }

        hgrps.clear();
        System.out.println(fgrps);
        return fgrps.isEmpty();

    }

    public void addGroup(boolean isHeader, Row row, String fieldName) {
        String fName = fieldName.toUpperCase();

        if (isHeader) {
            Optional<TGroup> group = hgrps.stream().filter(grp -> grp.getFieldName().equals(fName)).findFirst();
            group.ifPresent(t -> t.addRow(true, row));
            if (!group.isPresent())
                hgrps.add(new TGroup(sheet, fName, row, true));
        } else {
            Optional<TGroup> group = fgrps.stream().filter(grp -> grp.getFieldName().equals(fName)).findFirst();
            group.ifPresent(t -> t.addRow(false, row));
            if (!group.isPresent())
                fgrps.add(0, new TGroup(sheet, fName, row, false));
        }
    }
}
