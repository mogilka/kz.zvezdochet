package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Сервис аспектов
 * @author Nataly Didenko
 */
public class AspectService extends DictionaryService {

	public AspectService() {
		tableName = "aspects";
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		Aspect dict = (Aspect)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(value, orbis, code, name, description, typeid) values(?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"value = ?, " +
					"orbis = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"typeid = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setDouble(1, dict.getValue());
			ps.setDouble(2, dict.getOrbis());
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			ps.setLong(6, dict.getType().getId());
			result = ps.executeUpdate();
			if (result == 1) {
				if (null == model.getId()) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
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
		return dict;
	}

	@Override
	public Aspect init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Aspect aspect = (model != null) ? (Aspect)model : (Aspect)create();
		super.init(rs, aspect);
		aspect.setValue(rs.getDouble("Value"));
		aspect.setOrbis(rs.getDouble("Orbis"));
		Long typeId = rs.getLong("TypeID");
		aspect.setType((AspectType)new AspectTypeService().find(typeId));
		return aspect;
	}

	@Override
	public Model create() {
		return new Aspect();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
