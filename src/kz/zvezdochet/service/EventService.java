package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.DateUtil;

/**
 * Реализация сервиса событий
 * @author Nataly Didenko
 *
 * @see ModelService Реализация интерфейса сервиса управления объектами на уровне БД  
 */
public class EventService extends ModelService {

	public EventService() {
		tableName = "events";
	}

	/**
	 * Поиск события по наименованию
	 * @param text поисковое выражение
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Model> findByName(String text) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + 
				" where callname like ? or surname like ?" +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, "%" + text + "%");
			ps.setString(2, "%" + text + "%");
			rs = ps.executeQuery();
			while (rs.next())
				list.add(init(rs, null));
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
	public Model save(Model model) throws DataAccessException {
		Event event = (Event)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"surname = ?, " +
					"callname = ?, " +
					"gender = ?, " +
					"placeid = ?, " +
					"zone = ?, " +
					"sign = ?, " +
					"element = ?, " +
					"celebrity = ?, " +
					"comment = ?, " +
					"rectification = ?, " +
					"righthanded = ?, " +
					"initialdate = ?, " +
					"finaldate = ?, " +
					"date = ? " +
					"where id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, event.getSurname());
			ps.setString(2, event.getName());
			ps.setBoolean(3, event.isFemale());
			if (event.getPlace() != null && event.getPlace().getId() != null)
				ps.setLong(4, event.getPlace().getId());
			else
				ps.setLong(4, java.sql.Types.NULL);
			ps.setDouble(5, event.getZone());
			ps.setString(6, event.getSign());
			ps.setString(7, event.getElement());
			ps.setBoolean(8, event.isCelebrity());
			ps.setString(9, event.getDescription());
			ps.setInt(10, event.getRectification());
			ps.setBoolean(11, event.isRightHanded());
			ps.setString(12, DateUtil.formatCustomDateTime(event.getBirth(), "yyyy-MM-dd HH:mm:ss"));
			ps.setString(13, event.getDeath() != null ? DateUtil.formatCustomDateTime(event.getDeath(), "yyyy-MM-dd HH:mm:ss") : null);
			ps.setString(14, DateUtil.formatCustomDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
			if (model.getId() != null) 
				ps.setLong(15, model.getId());

			result = ps.executeUpdate();
			if (1 == result) {
				if (null == model.getId()) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
					    System.out.println("inserted " + tableName + "\t" + autoIncKeyFromApi);
					}
					if (rsid != null)
						rsid.close();
				}
			}
			savePlanets(event);
			saveHouses(event);
			savePlanetHouses(event);
			savePlanetSigns(event);
			saveBlob(event);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return model;
	}

	/**
	 * Сохранение текстовой и мультимедийной информации о событии
	 * @param event событие
	 */
	private void saveBlob(Event event) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String table = getBlobTable();
		try {
			String sql = "select id from " + table + " where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			rs = ps.executeQuery();
			long id = (rs.next()) ? rs.getLong("id") : 0;
			ps.close();
			
			if (0 == id)
				sql = "insert into " + table + " values(0,?,?,?,?)";
			else {
				sql = "update " + table + " set "
					+ "eventid = ?,"
					+ "photo = ?,"
					+ "biography = ?,"
					+ "test = ?"
					+ "where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			ps.setString(2, ""); //TODO сохранять изображение в папку
			ps.setString(3, event.getText());
			ps.setString(4, "");
			if (id != 0)
				ps.setLong(5, id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Поиск дополнительной информации о событии
	 * @param eventId идентификатор события
	 * @return массив, содержащий:<br>
	 * - текстовое описание события<br>
	 * - изображение
	 * @throws DataAccessException
	 */
	public Object[] findBlob(Long eventId) throws DataAccessException {
		if (eventId == null) return null;
		Object[] blob = new Object[2];
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String query = "select * from blobs where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setLong(1, eventId);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString("Biography") != null)
					blob[0] = rs.getString("Biography");
				if (rs.getString("Photo") != null)
					blob[1] = rs.getBytes("Photo");
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
		return blob;
	}

	@Override
	public Model init(ResultSet rs, Model model) throws SQLException {
		Event event = (model != null) ? (Event)model : (Event)create();
		event.setId(Long.parseLong(rs.getString("ID")));
		if (rs.getString("Callname") != null)
			event.setName(rs.getString("Callname"));
		if (rs.getString("Surname") != null)
			event.setSurname(rs.getString("Surname"));
		event.setBirth(DateUtil.getDatabaseDateTime(rs.getString("initialdate")));
		if (rs.getString("finaldate") != null) 
			event.setBirth(DateUtil.getDatabaseDateTime(rs.getString("finaldate")));
		String s = rs.getString("RightHanded");
		event.setRightHanded(s.equals("1") ? true : false);
		if (rs.getString("Rectification") != null) 
			event.setRectification(Integer.parseInt(rs.getString("Rectification")));
		s = rs.getString("Celebrity");
		event.setCelebrity(s.equals("1") ? true : false);
		if (rs.getString("Comment") != null)
			event.setDescription(rs.getString("Comment"));
		s = rs.getString("Gender");
		event.setFemale(s.equals("1") ? true : false);
		if (rs.getString("Sign") != null)
			event.setSign(rs.getString("Sign"));
		if (rs.getString("Element") != null)
			event.setElement(rs.getString("Element"));
		if (rs.getString("Placeid") != null)
			event.setPlaceid(Long.parseLong(rs.getString("Placeid")));
		if (rs.getString("Zone") != null)
			event.setZone(Double.parseDouble(rs.getString("Zone")));
		return event;
	}

	@Override
	public Model create() {
		return new Event();
	}

	/**
	 * Инициализация планет события
	 * @param event событие
	 * @throws DataAccessException
	 */
	public void initPlanets(Event event) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + getPlanetTable() + " where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			rs = ps.executeQuery();
			if (rs.next()) {
				for (Model model : event.getConfiguration().getPlanets()) {
					Planet planet = (Planet)model;
					if (rs.getString(planet.getCode()) != null) {
						double coord = rs.getDouble(planet.getCode());
						planet.setCoord(Math.abs(coord));
						planet.setRetrograde(coord < 0);
					}
				}
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
	}

	/**
	 * Инициализация астрологических домов события
	 * @param event событие
	 * @throws DataAccessException
	 */
	public void initHouses(Event event) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from eventhouses where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			rs = ps.executeQuery();
			if (rs.next()) {
				for (Model model : event.getConfiguration().getHouses()) {
					House house = (House)model;
					if (rs.getString(house.getCode()) != null)
						house.setCoord(rs.getDouble(house.getCode()));
				}
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
	}

	/**
	 * Сохранение планет конфигурации события
	 * @param event событие
	 * @throws DataAccessException
	 */
	private void savePlanets(Event event) throws DataAccessException {
		if (null == event.getConfiguration()) return;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String table = getPlanetTable();
		try {
			String sql = "select id from " + table + " where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			rs = ps.executeQuery();
			long id = (rs.next()) ? rs.getLong("id") : 0;
			ps.close();
			
			List<Model> planets = event.getConfiguration().getPlanets();
			if (0 == id)
				sql = "insert into " + table + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else {
				sql = "update " + table + " set eventid = ?,";
				for (int i = 0; i < planets.size(); i++) {
					sql += " " + ((Planet)planets.get(i)).getCode() + " = ?";
					if (i < planets.size() - 1)
						sql += ",";
				}
				sql += " where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			for (int i = 0; i < planets.size(); i++) {
				Planet planet = ((Planet)planets.get(i));
				double coord = planet.getCoord();
				if (planet.isRetrograde())
					coord *= -1;
				ps.setDouble(i + 2, coord);
			}
			if (id != 0)
				ps.setLong(18, id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Возвращает имя таблицы, хранящей планеты конфигурации события
	 * @return имя ТБД
	 */
	public String getPlanetTable() {
		return "eventplanets";
	}

	/**
	 * Возвращает имя таблицы, хранящей дома конфигурации события
	 * @return имя ТБД
	 */
	private String getHouseTable() {
		return "eventhouses";
	}

	/**
	 * Возвращает имя таблицы, хранящей позиции планет в домах конфигурации события
	 * @return имя ТБД
	 */
	private String getPlanetHouseTable() {
		return "eventpositions";
	}

	/**
	 * Возвращает имя таблицы, хранящей позиции планет в знаках конфигурации события
	 * @return имя ТБД
	 */
	public String getPlanetSignTable() {
		return "eventsigns";
	}

	/**
	 * Возвращает имя таблицы, хранящей текстовую и мультимедийную информацию о событии
	 * @return имя ТБД
	 */
	private String getBlobTable() {
		return "blobs";
	}

	/**
	 * Сохранение домов конфигурации события
	 * @param event событие
	 * @throws DataAccessException
	 */
	private void saveHouses(Event event) throws DataAccessException {
		if (null == event.getConfiguration()) return;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String table = getHouseTable();
		try {
			String sql = "select id from " + table + " where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			rs = ps.executeQuery();
			long id = (rs.next()) ? rs.getLong("id") : 0;
			ps.close();
			
			List<Model> houses = event.getConfiguration().getHouses();
			if (0 == id)
				sql = "insert into " + table + " values(0,?,"
						+ "?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?,?,?,?,?,"
						+ "?,?,?,?,?,?)";
			else {
				sql = "update " + table + " set eventid = ?,";
				for (int i = 0; i < houses.size(); i++) {
					sql += " " + ((House)houses.get(i)).getCode() + " = ?";
					if (i < houses.size() - 1)
						sql += ",";
				}
				sql += " where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			for (int i = 0; i < houses.size(); i++)
				ps.setDouble(i + 2, ((House)houses.get(i)).getCoord());
			if (id != 0)
				ps.setLong(38, id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Сохранение позиций планет в домах конфигурации события
	 * @param event событие
	 * @throws DataAccessException
	 */
	private void savePlanetHouses(Event event) throws DataAccessException {
		if (null == event.getConfiguration()) return;
		event.getConfiguration().initPlanetHouses();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String table = getPlanetHouseTable();
		try {
			String sql = "select id from " + table + " where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			rs = ps.executeQuery();
			long id = (rs.next()) ? rs.getLong("id") : 0;
			ps.close();
			
			List<Model> planets = event.getConfiguration().getPlanets();
			if (0 == id)
				sql = "insert into " + table + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else {
				sql = "update " + table + " set eventid = ?,";
				for (int i = 0; i < planets.size(); i++) {
					sql += " " + ((Planet)planets.get(i)).getCode() + " = ?";
					if (i < planets.size() - 1)
						sql += ",";
				}
				sql += " where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			for (int i = 0; i < planets.size(); i++) {
				Planet planet = ((Planet)planets.get(i));
				ps.setInt(i + 2, planet.getHouse().getNumber());
			}
			if (id != 0)
				ps.setLong(18, id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Сохранение позиций планет в знаках конфигурации события
	 * @param event событие
	 * @throws DataAccessException
	 */
	private void savePlanetSigns(Event event) throws DataAccessException {
		if (null == event.getConfiguration()) return;
		event.getConfiguration().initPlanetSigns();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String table = getPlanetSignTable();
		try {
			String sql = "select id from " + table + " where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			rs = ps.executeQuery();
			long id = (rs.next()) ? rs.getLong("id") : 0;
			ps.close();
			
			List<Model> planets = event.getConfiguration().getPlanets();
			if (0 == id)
				sql = "insert into " + table + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else {
				sql = "update " + table + " set eventid = ?,";
				for (int i = 0; i < planets.size(); i++)
					sql += " " + ((Planet)planets.get(i)).getCode() + " = ?,";
				sql += "celebrity = ?";
				sql += " where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			for (int i = 0; i < planets.size(); i++) {
				Planet planet = ((Planet)planets.get(i));
				ps.setLong(i + 2, planet.getSign().getId());
			}
			ps.setInt(18, event.isCelebrity() ? 1 : 0);
			if (id != 0)
				ps.setLong(19, id);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
