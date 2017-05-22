package com.orders.dao;

import com.dao.model.DataSet;

import java.io.Serializable;

public interface QueryFactory extends Serializable {

    DataSet exec(String sql, Object... values) throws Exception;

    <T> T value(String sql, Class<T> clazz, Object... values);
}
