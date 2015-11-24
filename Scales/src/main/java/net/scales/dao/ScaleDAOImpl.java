package net.scales.dao;

import net.scales.model.CustomUser;
import net.scales.model.Scale;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class ScaleDAOImpl extends AbstractDAOImpl implements ScaleDAO {

	BigDecimal returnResult;

	@SuppressWarnings("unchecked")
	public List<Scale> getScalesOut(Date date1, Date date2, CustomUser user) {
		String sql = "SELECT * from ytrans_vtf_prohodn_out "
				+ "where elevator=:elevator and TRUNC(TIME_IN,'DD') between TRUNC(to_date(:FDATA1),'DD') and TRUNC(to_date(:FDATA2),'DD') "
				+ "and div = :div ORDER BY id ";
		Query query = currentSession().createSQLQuery(sql);
		query.setDate("FDATA1", date1);
		query.setDate("FDATA2", date2);
		query.setParameter("elevator", user.getElevator().getId().longValue());
		query.setParameter("div", user.getDiv().getId().longValue());
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		return convertToListModel(query.list(), Scale.class);
	}

	@SuppressWarnings("unchecked")
	public List<Scale> getScalesIn(Date date1, Date date2, CustomUser user) {
		String sql = "SELECT * FROM VTF_PROHODN_MPFS "
				+ "where elevator=:elevator and TRUNC(TIME_IN,'DD') between TRUNC(to_date(:FDATA1),'DD') and TRUNC(to_date(:FDATA2),'DD') "
				+ "and div = :div ORDER BY id ";
		Query query = currentSession().createSQLQuery(sql);
		query.setDate("FDATA1", date1);
		query.setDate("FDATA2", date2);
		query.setParameter("elevator", user.getElevator().getId().longValue());
		query.setParameter("div", user.getDiv().getId().longValue());
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		return convertToListModel(query.list(), Scale.class);
	}

	public void updateScale(Scale data) throws HibernateException, SQLException {
		String sql = "UPDATE ytrans_vtf_prohodn_out SET  sofer = :sofer,"
				+ "nr_vagon = :nr_vagon, nr_remorca = :nr_remorca,"
				+ "dep_perevoz = :clcdep_perevoz, dep_destinat = :clcdep_destinatt,"
				+ "prazgruz_s_12 = :clcprazgruz_s_12t, punctto_s_12 = :clcpunctto_s_12t, sc = :clcsct,"
				+ "sezon_yyyy = :sezon_yyyy, ttn_n = :ttn_n, ttn_data = :ttn_data,"
				+ "nr_analiz = :nr_analiz, masa_brutto = :masa_brutto,"
				+ "masa_tara = :masa_tara,masa_netto = :masa_netto,"
				+ "time_in = :time_in,time_out = NVL (:time_out, SYSDATE),"
				+ "elevator = :clcelevatort,userid = :userid WHERE ID = :id";
		Query updateQuery = currentSession().createSQLQuery(sql);
		parseToParams(updateQuery, data);
		updateQuery.executeUpdate();
	}

	public void insertScale(Scale data) throws HibernateException, SQLException {
		String sql = "insert into ytrans_vtf_prohodn_out (id,sofer,nr_vagon,nr_remorca,dep_perevoz,dep_destinat,prazgruz_s_12,punctto_s_12,sc,sezon_yyyy,ttn_n,ttn_data,nr_analiz,masa_brutto,masa_tara,masa_netto,time_in,time_out,elevator,userid, priznak_arm,div) "
				+ " values (id_mp_vesy.nextval, :sofer, :nr_vagon, :nr_remorca, :clcdep_perevoz, :clcdep_destinatt, :clcprazgruz_s_12t, :clcpunctto_s_12t, :clcsct, :sezon_yyyy, :ttn_n, :ttn_data, :nr_analiz, :masa_brutto, :masa_tara, :masa_netto, :time_in, NVL(:time_out, SYSDATE), :clcelevatort, :userid, 2, :div) ";
		Query updateQuery = currentSession().createSQLQuery(sql);
		parseToParams(updateQuery, data);
		updateQuery.executeUpdate();
	}

	public void updateScaleIn(Scale data) throws HibernateException,
			SQLException {
		String sql = "update vtf_prohodn_mpfs set  sofer = :sofer,"
				+ "auto = :auto, nr_remorca = :nr_remorca,"
				+ "dep_postav = :clcdep_postavt, ppogruz_s_12 = :clcppogruz_s_12t, "
				+ "sc_mp = :clcsc_mpt, sezon_yyyy = :sezon_yyyy, ttn_n = :ttn_n, ttn_data = :ttn_data, "
				+ "masa_ttn = :masa_ttn, nr_analiz = :nr_analiz, dep_transp = :clcdep_transpt, "
				+ "masa_brutto = :masa_brutto, masa_tara = :masa_tara, masa_netto = :masa_netto, "
				+ "time_in = :time_in, time_out = NVL(:time_out, SYSDATE), "
				+ "elevator = :clcelevatort, userid = :userid where id = :id";
		Query updateQuery = currentSession().createSQLQuery(sql);
		parseToParams(updateQuery, data);
		updateQuery.executeUpdate();
	}

	public void insertScaleIn(Scale data) throws HibernateException,
			SQLException {
		String sql = "insert into vtf_prohodn_mpfs "
				+ "(id,sofer,auto,nr_remorca,dep_postav, ppogruz_s_12, sc_mp, sezon_yyyy, ttn_n, ttn_data, masa_ttn, nr_analiz, dep_transp, masa_brutto,masa_tara, masa_netto, time_in, time_out, div, elevator, userid, priznak_arm) "
				+ " values (id_mp_vesy.nextval, :sofer, :auto, :nr_remorca, :clcdep_postavt, :clcppogruz_s_12t, :clcsc_mpt, :sezon_yyyy, :ttn_n, :ttn_data, :masa_ttn, :nr_analiz, :clcdep_transpt, :masa_brutto, :masa_tara, :masa_netto, :time_in,  NVL(:time_out, SYSDATE), :div, :clcelevatort, :userid, 1) ";
		Query updateQuery = currentSession().createSQLQuery(sql);
		parseToParams(updateQuery, data);
		updateQuery.executeUpdate();
	}
}
