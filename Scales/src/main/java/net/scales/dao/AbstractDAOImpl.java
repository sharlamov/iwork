package net.scales.dao;

import net.scales.model.CustomItem;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class AbstractDAOImpl {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private final Pattern paramsPattern = Pattern.compile("([:][a-zA-Z0-9_$]+)");

	public Session currentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public <T> void convertToModel(Map<String, Object> map, T model) {
        Field[] allFields = model.getClass().getDeclaredFields();
        for (Field field : allFields) {
            try {
            	Object value;
            	String fName = field.getName().toUpperCase();
            	if(fName.startsWith("CLC") && (field.getType() == CustomItem.class)){
            		int size = fName.endsWith("T") ? fName.length() - 1 : fName.length();
            		String fId = fName.substring(3, size);
            		value = new CustomItem(map.get(fId),null,map.get(fName));
            	}else{
            		value = map.get(fName);
            	}
            	field.setAccessible(true);
                field.set(model,value);	
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

	public <T> List<T> convertToListModel(List<Map<String, Object>> list, Class<T> type) {
		List<T> newlist = new ArrayList<>(list.size());
		for (Map<String, Object> map : list) {
			try {
				T model = type.newInstance();
				convertToModel(map, model);
				newlist.add(model);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return newlist;
	}
	
	public <T> void parseToParams(Query query, T model) {	
		Matcher m = paramsPattern.matcher(query.getQueryString());

		Class<?> c = model.getClass();

		while (m.find()) {
			try {
				String fName = m.group().replace(":", "");
				
				Field field = c.getDeclaredField(fName);
				if(field != null){
					field.setAccessible(true);
					Object value = field.get(model);
					if (value instanceof CustomItem) {
						CustomItem cu = (CustomItem)value;
						if(cu.getId() != null)
							value = cu.getId();
						else
							value = null;
					}
					
					query.setParameter(fName, value != null ? value : "");
					//System.out.println(fName + ": " + value);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public List getLimitList(int size, String query, String sql) {
		sql = "select * from (" + sql + ") " + "where lower(denumirea) like '%"
				+ query.trim().toLowerCase()
				+ "%' and rownum < :size order by 2";
		return currentSession().createSQLQuery(sql).setInteger("size", size)
				.list();
	}
}
