package com.reporting.dao;

import com.reporting.model.CustomItem;
import org.hibernate.Query;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

@Repository
public class CommonDAOImpl extends AbstractDAOImpl implements CommonDAO {

    BigDecimal returnResult;

    @Override
    public BigDecimal insertUnivItem(final CustomItem ci, final String tip,
                                     final String gr1) {
        final String sql = "{call insert into vms_univers (cod, codvechi, denumirea, tip, gr1) values (id_tms_univers.nextval, ?, ?, ?, ?) RETURNING cod INTO ? }";

        currentSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                CallableStatement stmt = connection.prepareCall(sql);
                stmt.setString(1, ci.getName());
                stmt.setString(2, ci.getLabel());
                stmt.setString(3, tip);
                stmt.setString(4, gr1);
                stmt.registerOutParameter(5, Types.NUMERIC);
                stmt.executeUpdate();
                returnResult = new BigDecimal(stmt.getLong(5));
            }
        });

        return returnResult;
    }

    @Override
    public BigDecimal insertSyssItem(final CustomItem ci, final String tip,
                                     final String gr1) {
        final String sql = "{call insert into tms_syss (tip, cod, um, denumirea, cod1) "
                + "values (?, ?, ?, ?, (select nvl(max(cod1),0) + 1 from vms_syss where tip = ?  and cod = ? )) RETURNING cod1 INTO ? }";

        currentSession().doWork(new Work() {
            public void execute(Connection connection) throws SQLException {
                CallableStatement stmt = connection.prepareCall(sql);
                stmt.setString(1, tip);
                stmt.setString(2, gr1);
                stmt.setString(3, ci.getName());
                stmt.setString(4, ci.getLabel());
                stmt.setString(5, tip);
                stmt.setString(6, gr1);
                stmt.registerOutParameter(7, Types.NUMERIC);
                stmt.executeUpdate();
                returnResult = new BigDecimal(stmt.getLong(7));
            }
        });

        return returnResult;
    }

    @Override
    public Object getEnvParam(String param) {
        String sql = "select max(value) from a$env where name = :param";
        return currentSession().createSQLQuery(sql).setString("param", param)
                .uniqueResult();
    }

    @Override
    public List<Object> completeListItem(int i, String query, String table,
                                         String tip, String gr1) {
        String sql = table.equalsIgnoreCase("UNIV") ?
                "select cod, denumirea ||', '|| codvechi as denumirea from vms_univers where tip = '" + tip + "' and gr1='" + gr1 + "'" :
                "select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as denumirea from vms_syss s where tip='" + tip + "' and cod='" + gr1 + "' and cod1>0";
        return getLimitList(i, query, sql);
    }

    @Override
    public List<Object> getTransportList(int i, String query) {
        String sql = "select cod, denumirea ||', '|| codvechi as denumirea from vms_univers where tip='O' and gr1='E'";
        return getLimitList(i, query, sql);
    }

    @Override
    public List<Object> getDestinatarList(int i, String query) {
        String sql = "select cod, denumirea ||', '|| codvechi as denumirea from vms_univers where tip='O' and gr1 in ('E', 'COTA')";
        return getLimitList(i, query, sql);
    }

    @Override
    public List<Object> getPunctulList(int i, String query) {
        String sql = "select cod1 as cod, denumirea ||', '|| nmb1t ||', '|| nmb2t as denumirea from vms_syss s where tip='S' and cod='12' and cod1>0";
        return getLimitList(i, query, sql);
    }

    @Override
    public List<Object> getTipulList(int i, String query) {
        String sql = "select cod, denumirea from vms_univers where tip='M' and gr1='P'";
        return getLimitList(i, query, sql);
    }

    @Override
    public List<Object> getDepList(int i, String query) {
        String sql = "select cod, denumirea from vms_univers where tip='O' and gr1='I'";
        return getLimitList(i, query, sql);
    }

    @Override
    public BigDecimal getDefSezon(Date fDate, long div) {
        String sql = "select nvl(max(sezon_yyyy),to_char(to_date(:fdate), 'yyyy')) from yvtrans_sezon a where :fdate between datastart and dataend and div = :div";
        Query query = currentSession().createSQLQuery(sql)
                .setDate("fdate", fDate).setLong("div", div);
        return new BigDecimal(query.uniqueResult().toString());
    }
}
