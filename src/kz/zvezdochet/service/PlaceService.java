package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Place;
import kz.zvezdochet.core.bean.Base;
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
	public Base find(Long id) throws DataAccessException {
		if (id == null) return null;
		Place place = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String query = "select * from " + tableName + " where id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			if (rs.next()) 
				place = init(rs);
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
		return place;
	}

	@Override
	public List<Base> getList() throws DataAccessException {
        List<Base> list = new ArrayList<Base>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String query = "select * from " + tableName + " order by name";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				Place place = init(rs);
				list.add(place);
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
	public Place init(ResultSet rs) throws DataAccessException, SQLException {
		Place place = (Place)super.init(rs);
		place.setLatitude(Double.parseDouble(rs.getString("Latitude")));
		place.setLongitude(Double.parseDouble(rs.getString("Longitude")));
		place.setGreenwich(rs.getDouble("Greenwich"));
		return place;
	}

	@Override
	public Base create() {
		return new Place();
	}
}
