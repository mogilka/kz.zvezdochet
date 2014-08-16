package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Place;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Сервис местностей
 * @author Nataly Didenko
 */
public class PlaceService extends DictionaryService {

	public PlaceService() {
		tableName = "places";
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		Place dict = (Place)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (model.getId() == null) 
				sql = "insert into " + tableName + 
					"(latitude, longitude, code, name, description, greenwich) values(?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"latitude = ?, " +
					"longitude = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"greenwich = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setDouble(1, dict.getLatitude());
			ps.setDouble(2, dict.getLongitude());
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			ps.setDouble(6, dict.getGreenwich());
			result = ps.executeUpdate();
			if (result == 1) {
				if (model.getId() == null) { 
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
	public Place init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Place place = (model != null) ? (Place)model : (Place)create();
		super.init(rs, place);
		place.setLatitude(rs.getDouble("Latitude"));
		place.setLongitude(rs.getDouble("Longitude"));
		place.setGreenwich(rs.getDouble("Greenwich"));
		return place;
	}

	@Override
	public Model create() {
		return new Place();
	}

	/**
	 * Поиск местности по имени
	 * @param name наименование
	 * @return список мест
	 * @throws DataAccessException
	 */
	public List<Place> findByName(String name) throws DataAccessException {
		List<Place> list = new ArrayList<Place>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " where name like ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, "%" + name + "%");
			rs = ps.executeQuery();
			while (rs.next()) {
				Place type = init(rs, null);
				list.add(type);
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
}
