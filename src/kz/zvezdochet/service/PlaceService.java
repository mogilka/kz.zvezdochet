package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Place;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Реализация сервиса местностей
 * @author Nataly Didenko
 * 
 * @see ReferenceService Реализация сервиса справочников  
 */
public class PlaceService extends ReferenceService {

	public PlaceService() {
		tableName = "places";
	}

	@Override
	public Model save(Model element) throws DataAccessException {
		Place reference = (Place)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(latitude, longitude, code, name, description, greenwich) values(?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"latitude = ?, " +
					"longitude = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"greenwich = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setDouble(1, reference.getLatitude());
			ps.setDouble(2, reference.getLongitude());
			ps.setString(3, reference.getCode());
			ps.setString(4, reference.getName());
			ps.setString(5, reference.getDescription());
			ps.setDouble(6, reference.getGreenwich());
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
	public Place init(ResultSet rs, Model base) throws DataAccessException, SQLException {
		Place place = new Place();
		super.init(rs, place);
		place.setLatitude(Double.parseDouble(rs.getString("Latitude")));
		place.setLongitude(Double.parseDouble(rs.getString("Longitude")));
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
			String query = "select * from " + tableName + " where name like ?";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
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
