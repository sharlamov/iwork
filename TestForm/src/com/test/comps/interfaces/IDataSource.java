package com.test.comps.interfaces;

import com.dao.model.DataSet;

import java.util.Map;

public interface IDataSource {
    DataSet exec(String query, Object... params) throws Exception;

    DataSet filterDataSet(String sql, DataSet params, Map<String, String> stringStringMap);
}
