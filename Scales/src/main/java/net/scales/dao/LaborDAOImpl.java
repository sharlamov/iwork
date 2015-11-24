package net.scales.dao;

import net.scales.model.Labor;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class LaborDAOImpl extends AbstractDAOImpl implements LaborDAO {

    public List<Labor> getLaborsIn(Date date, Long elevatorId) {
        String sql = "select * from VTF_LABOR_MP "
                + "where to_date(:cdate) between to_date(to_char('14.06')||'.'||to_char(sezon_yyyy)) "
                + "and to_date(to_char('14.06')||'.'||to_char(sezon_yyyy+1)) and div=un$div.get_def() "
                + "and NR_ANALIZ>0 and elevator = :elevator ";
        Query query = currentSession().createSQLQuery(sql);
        query.setDate("cdate", date);
        query.setLong("elevator", elevatorId);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return convertToListModel(query.list(), Labor.class);
    }

    public List<Labor> getLaborsOut(Date date, Long elevatorId) {
        String sql = "select * from VTF_LABOR_MP "
                + "where to_date(:cdate) between to_date(to_char('19.06')||'.'||to_char(sezon_yyyy)) "
                + "and to_date(to_char('18.06')||'.'||to_char(sezon_yyyy+1)) and div=un$div.get_def() "
                + "and nvl(PRIZNAK_ARM,0)<>20 and elevator = :elevator and NR_ANALIZ between -200000 and 0 ";
        Query query = currentSession().createSQLQuery(sql);
        query.setDate("cdate", date);
        query.setLong("elevator", elevatorId);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return convertToListModel(query.list(), Labor.class);
    }

    public void updateLabor(Labor data, Labor oldData)
            throws HibernateException, SQLException {
        String sql = "update vtf_labor_mp set "
                + "sezon_yyyy = :sezon_yyyy, nr_analiz = :nr_analiz, data_analiz = :data_analiz, "
                + "anlz_vlajn = :anlz_vlajn, anlz_sorn = :anlz_sorn, anlz_kislot = :anlz_kislot, "
                + "anlz_zernprim = :anlz_zernprim, anlz_zaraj = :anlz_zaraj, anlz_maslprim = :anlz_maslprim, "
                + "sanlz_vlajn = :sanlz_vlajn, sanlz_sorn = :sanlz_sorn, sanlz_kislot = :sanlz_kislot, "
                + "sanlz_zernprim = :sanlz_zernprim, sanlz_zaraj = :sanlz_zaraj, sanlz_maslprim = :sanlz_maslprim, "
                + "txt_comment = :txt_comment, sertificat_nr = :sertificat_nr, sklad=:clcskladt, "
                + "partida = :partida, ttn_seria = :ttn_seria, ttn_nr = :ttn_nr, anlz_natura = :anlz_natura, anlz_kleik = :anlz_kleik, "
                + "sc_mp = :clcsc_mpt, anlz_pr_vih_zerna = :anlz_pr_vih_zerna, nr_sert = :nr_sert, anlz_bitoe_zerno = :anlz_bitoe_zerno "
                + "where sezon_yyyy = " + oldData.getSezon_yyyy().longValue()
                + " and nr_analiz = " + oldData.getNr_analiz().longValue()
                + "and div = " + oldData.getDiv().longValue()
                + " and elevator = " + oldData.getElevator().longValue();

        Query updateQuery = currentSession().createSQLQuery(sql);
        parseToParams(updateQuery, data);
        updateQuery.executeUpdate();
    }

    public void insertLaborIn(Labor data) throws HibernateException,
            SQLException {
        String sql = "insert into vtf_labor_mp "
                + "(sezon_yyyy, nr_analiz, data_analiz, anlz_vlajn, anlz_sorn, "
                + "anlz_kislot, anlz_zernprim, anlz_zaraj, anlz_maslprim, "
                + "sanlz_vlajn, sanlz_sorn, sanlz_kislot, sanlz_zernprim, "
                + "sanlz_zaraj, sanlz_maslprim, txt_comment, userid, sertificat_nr, "
                + "priznak_arm, partida, ttn_seria, ttn_nr, anlz_natura, anlz_kleik, "
                + "div,sc_mp,anlz_pr_vih_zerna,nr_sert,anlz_bitoe_zerno,elevator) "
                + "values (:sezon_yyyy,"
                + "(select nvl(max(NR_ANALIZ),0)+1 from tf_labor_mp where nr_analiz > 0 and div=:div and sezon_yyyy = :sezon_yyyy and elevator = :elevator), "
                + ":data_analiz, :anlz_vlajn, "
                + ":anlz_sorn, :anlz_kislot, :anlz_zernprim, :anlz_zaraj, "
                + ":anlz_maslprim, :sanlz_vlajn, :sanlz_sorn, :sanlz_kislot, "
                + ":sanlz_zernprim, :sanlz_zaraj, :sanlz_maslprim, :txt_comment, "
                + ":userid, :sertificat_nr, :priznak_arm, :partida, :ttn_seria, "
                + ":ttn_nr, :anlz_natura, :anlz_kleik, :div,:clcsc_mpt,:anlz_pr_vih_zerna, "
                + ":nr_sert,:anlz_bitoe_zerno,:elevator) ";

        Query updateQuery = currentSession().createSQLQuery(sql);
        parseToParams(updateQuery, data);
        updateQuery.executeUpdate();
    }

    public void insertLaborOut(Labor data) throws HibernateException,
            SQLException {
        String sql = "insert into vtf_labor_mp "
                + "(sezon_yyyy, nr_analiz, data_analiz, anlz_vlajn, anlz_sorn, "
                + "anlz_kislot, anlz_zernprim, anlz_zaraj, anlz_maslprim, "
                + "sanlz_vlajn, sanlz_sorn, sanlz_kislot, sanlz_zernprim, "
                + "sanlz_zaraj, sanlz_maslprim, txt_comment, userid, sertificat_nr, "
                + "priznak_arm, partida, ttn_seria, ttn_nr, anlz_natura, anlz_kleik, "
                + "div,sc_mp,anlz_pr_vih_zerna,nr_sert,anlz_bitoe_zerno,elevator, sklad) "
                + "values (:sezon_yyyy,"
                + "(select nvl(min(nr_analiz),0)-1 from tf_labor_mp where sezon_yyyy=:sezon_yyyy and div=:div and (nr_analiz<0 and nr_analiz>-200000 ) and nvl(priznak_arm,0)<>20 and elevator = :elevator), "
                + ":data_analiz, :anlz_vlajn, "
                + ":anlz_sorn, :anlz_kislot, :anlz_zernprim, :anlz_zaraj, "
                + ":anlz_maslprim, :sanlz_vlajn, :sanlz_sorn, :sanlz_kislot, "
                + ":sanlz_zernprim, :sanlz_zaraj, :sanlz_maslprim, :txt_comment, "
                + ":userid, :sertificat_nr, :priznak_arm, :partida, :ttn_seria, "
                + ":ttn_nr, :anlz_natura, :anlz_kleik, :div,:clcsc_mpt,:anlz_pr_vih_zerna "
                + ",:nr_sert,:anlz_bitoe_zerno,:elevator, :clcskladt) ";

        Query updateQuery = currentSession().createSQLQuery(sql);
        parseToParams(updateQuery, data);
        updateQuery.executeUpdate();
    }
}
