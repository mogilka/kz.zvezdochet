package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kz.zvezdochet.bean.Place;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.util.DateUtil;

/**
 * Сервис местностей
 * @author Natalie Didenko
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
			if (null == model.getId()) 
				sql = "insert into " + tableName + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"latitude = ?, " +
					"longitude = ?, " +
					"name = ?, " +
					"greenwich = ?, " +
					"description = ?, " +
					"code = ?, " +
					"date = ?, " +
					"parentid = ?, " +
					"type = ?, " +
					"zone = ?, " +
					"dst = ?, " +
					"name_en = ?, " +
					"descr_en = ? " +
				"where id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setDouble(1, dict.getLatitude());
			ps.setDouble(2, dict.getLongitude());
			ps.setString(3, dict.getName());
			ps.setDouble(4, dict.getGreenwich());
			ps.setString(5, dict.getDescription());
			ps.setString(6, dict.getCode());
			ps.setString(7, DateUtil.formatCustomDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
			if (dict.getParentid() > 0)
				ps.setLong(8, dict.getParentid());
			else
				ps.setNull(8, java.sql.Types.NULL);
			ps.setString(9, dict.getType());
			ps.setDouble(10, dict.getZone());
			ps.setBoolean(11, dict.isDst());
			ps.setString(12, dict.getNameEn());
			ps.setString(13, dict.getDescrEn());

			if (model.getId() != null)
				ps.setLong(14, model.getId());
			System.out.println(ps);

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
			DialogUtil.alertError(e);
		} finally {
			try {
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			afterSave();
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
		place.setZone(rs.getDouble("zone"));
		place.setParentid(rs.getLong("parentid"));
		place.setType(rs.getString("type"));
		place.setDate(DateUtil.getDatabaseDateTime(rs.getString("date")));
		place.setNameEn(rs.getString("name_en"));
		place.setDescrEn(rs.getString("descr_en"));
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

	public Model create(Model model) throws DataAccessException {
		Place dict = (Place)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql = "insert into " + tableName + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, dict.getId());
			ps.setDouble(2, dict.getLatitude());
			ps.setDouble(3, dict.getLongitude());
			ps.setString(4, dict.getName());
			ps.setDouble(5, dict.getGreenwich());
			ps.setString(6, dict.getDescription());
			ps.setString(7, dict.getCode());
			ps.setString(8, DateUtil.formatCustomDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
			if (dict.getParentid() > 0)
				ps.setLong(9, dict.getParentid());
			else
				ps.setNull(9, java.sql.Types.NULL);
			ps.setString(10, dict.getType());
			ps.setDouble(11, dict.getZone());
			ps.setBoolean(12, dict.isDst());
			ps.setString(13, dict.getNameEn());
			ps.setString(14, dict.getDescrEn());
			System.out.println(ps);
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
			afterSave();
		}
		return dict;
	}

	/**
	 * Поиск даты последнего обновления
	 * @return дата
	 * @throws DataAccessException
	 */
	public Date findLastDate() throws DataAccessException {
		Date date = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select max(date) as maxdate from " + tableName;
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			date = rs.next() ? DateUtil.getDatabaseDateTime(rs.getString("maxdate")) : new Date();
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
		return date;
	}
}
