package md.sh.model;

import java.util.List;

public interface IDataSet extends List<List<Object>> {

    List<Object> getFirst();

    Object getObject(int row, int col);

    Object getObject(String name);

    int size();

    int getColCount();

    int findField(String name);

    List<Object> get(int i);

    Double sum(String substring);

    List<String> getNames();

    List<DataSet> groupBy(String fName);
}
