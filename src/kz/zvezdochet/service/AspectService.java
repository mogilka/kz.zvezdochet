package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Реализация сервиса аспектов
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class AspectService extends ReferenceService {

	public AspectService() {
		tableName = "aspects";
	}

	@Override
	public Base getEntityByCode(String code) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Base> getList() throws DataAccessException {
        List<Base> list = new ArrayList<Base>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " order by name";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				Aspect aspect = init(rs);
				list.add(aspect);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { 
				if (rs != null) rs.close();
				if (ps != null) ps.close();
			} catch (SQLException e) { 
				e.printStackTrace(); 
			}
		}
		return list;
	}

	@Override
	public Base save(Base element) throws DataAccessException {
		Aspect reference = (Aspect)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(value, orbis, code, name, description, typeid) values(?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"value = ?, " +
					"orbis = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"typeid = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setDouble(1, reference.getValue());
			ps.setDouble(2, reference.getOrbis());
			ps.setString(3, reference.getCode());
			ps.setString(4, reference.getName());
			ps.setString(5, reference.getDescription());
			ps.setLong(6, reference.getType().getId());
			result = ps.executeUpdate();
			if (result == 1) {
				if (element.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        element.setId(autoIncKeyFromApi);
					    //System.out.println("inserted " + tableName + "\t" + autoIncKeyFromApi);
					}
					if (rsid != null) rsid.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			update();
		}
		return reference;
	}

	@Override
	public Aspect init(ResultSet rs) throws DataAccessException, SQLException {
		Aspect aspect = (Aspect)super.init(rs);
		aspect.setValue(Double.parseDouble(rs.getString("Value")));
		aspect.setOrbis(Double.parseDouble(rs.getString("Orbis")));
		Long typeId = Long.parseLong(rs.getString("TypeID"));
		aspect.setType((AspectType)new AspectTypeService().find(typeId));
		return aspect;
	}

	@Override
	public Base create() {
		return new Aspect();
	}
}
