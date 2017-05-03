package com.bridge.enums;

import com.dao.service.JDBCFactory;

public interface IFireListener {
    void fire(JDBCFactory factory, String sql);
}
