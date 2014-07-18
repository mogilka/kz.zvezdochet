package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	public Aspect init(ResultSet rs, Base base) throws DataAccessException, SQLException {
		Aspect aspect = new Aspect();
		super.init(rs, aspect);
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
