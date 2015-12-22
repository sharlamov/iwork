package com.dao;

import com.model.CustomItem;
import com.model.CustomUser;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.SQLException;

@Repository
public class UserDAOImpl implements UserDAO {

	@Autowired
	private SessionFactory sessionFactory;

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}

	public Object loadUserByUsername(String username) {
		String sql = "select id, username,nvl(admin,0) as admin, "
				+ "nvl(password, (select old_value  from a$act t where action='p' and  t.obj_id=a.obj_id and  stamp=(select max(t1.stamp) from a$act t1 where t1.action='p' and  t.obj_id=t1.obj_id))) as pass "
				+ "from a$users$v a where enabled=1 and username = :user_name";
		return currentSession().createSQLQuery(sql)
				.setString("user_name", username).uniqueResult();
	}

	public void initUserParams(String userName, String password)
			throws HibernateException, SQLException {
		String sql = " declare n number; begin n\\:=un$userparams.registeruser(:login, :pass); end; ";
		Query updateQuery = currentSession().createSQLQuery(sql);
		updateQuery.setString("login", userName).setString("pass", password)
				.executeUpdate();
	}
	
	public CustomItem getUserDiv(BigDecimal userId) {
		String sql = "select to_number(div), (select denumirea from vms_univers where cod = div and tip='O' and gr1='DIV') dname from "
				+ "(SELECT TRIM(SYS_CONNECT_BY_PATH ( (select value from a$adp$v p WHERE key = 'DIVDEFAULT' and obj_id = a.obj_id), ' ' )) div "
				+ "FROM a$adm a "
				+ "CONNECT BY obj_id = PRIOR parent_id START WITH obj_id = (select obj_id from a$adp$v p where key='ID' and value=:userId) order by level "
				+ ") where div is not null and rownum = 1";		
		Query sqlQuery = currentSession().createSQLQuery(sql);
		Object[] details = (Object[]) sqlQuery.setString("userId", userId.toString()).setMaxResults(1).uniqueResult();
		return details == null ? null : new CustomItem(details[0], null, details[1]);
	}
	
	public CustomItem getUserElevator(BigDecimal userId) {
		String sql = "select elevator, clcelevatort  from vms_user_elevator where userid = :userId";
		Query sqlQuery = currentSession().createSQLQuery(sql);
		Object[] details = (Object[]) sqlQuery.setParameter("userId", userId).setMaxResults(1).uniqueResult();
		return details == null ? null : new CustomItem(details[0], null, details[1]);
	}
	
	public int getUserScaleType(BigDecimal userId) {
		String sql = " select nvl(max(value),0) from a$users$v a, A$ADP$v v where a.ID=:userId and v.obj_id=a.obj_id and KEY='CANTARE' ";
		Query query = currentSession().createSQLQuery(sql).setParameter("userId",userId);
		return Integer.parseInt(query.uniqueResult().toString());
	}
	
	public String getUserRole(BigDecimal userId) {
		String sql = " select max(value) from a$users$v a, A$ADP$v v where a.ID=:userId and v.obj_id=a.obj_id and KEY='WEB_USER_ROLE' ";
		Query query = currentSession().createSQLQuery(sql).setParameter("userId",userId);
		Object obj = query.uniqueResult();
		return  obj == null ? "" : obj.toString().trim().toUpperCase();
	}
	
	public void initContext(CustomUser user){
		String sql = " begin "
				+ "envun4.envsetvalue('PARAM_USERID',:userId); "
				+ "envun4.envsetvalue('INIPARAM_ADMINLEVEL',:adminLevel); "
				+ "un$div.set_def(:div); "
				+ "end; ";
		Query updateQuery = currentSession().createSQLQuery(sql);
		updateQuery
		.setParameter("userId", user.getId())
		.setParameter("adminLevel", user.getAdminLevel())
		.setParameter("div", user.getDiv().getId())
				.executeUpdate();
	}
}
