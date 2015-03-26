package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.bean.SkyPointAspect;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.core.util.Translit;
import kz.zvezdochet.util.Configuration;

/**
 * Сервис событий
 * @author Nataly Didenko
 */
public class EventService extends ModelService {

	public EventService() {
		tableName = "events";
	}

	/**
	 * Поиск события по наименованию
	 * @param text поисковое выражение
	 * @param human -1|0|1|2 все|события|живые существа|персонажи
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Model> findByName(String text, int human) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String wherehuman = (human > -1) ? "and human = " + human : "";
			String sql = "select * from " + tableName + 
				" where name like ? " + wherehuman +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, "%" + text + "%");
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
				sql = "insert into " + tableName + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"name = ?, " +
					"gender = ?, " +
					"placeid = ?, " +
					"zone = ?, " +
					"celebrity = ?, " +
					"comment = ?, " +
					"rectification = ?, " +
					"righthanded = ?, " +
					"initialdate = ?, " +
					"finaldate = ?, " +
					"date = ?, " +
					"human = ?," +
					"accuracy = ?, " +
					"userid = ?," +
					"calculated = ?, " +
					"fancy = ?, " +
					"dst = ?, " +
					"finalplaceid = ? " +
					"where id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, event.getName());
			ps.setBoolean(2, event.isFemale());
			if (event.getPlace() != null && event.getPlace().getId() > 0)
				ps.setLong(3, event.getPlace().getId());
			else
				ps.setNull(3, java.sql.Types.NULL);
			ps.setDouble(4, event.getZone());
			ps.setBoolean(5, event.isCelebrity());
			ps.setString(6, event.getDescription());
			ps.setInt(7, event.getRectification());
			ps.setBoolean(8, event.isRightHanded());
			String birth = DateUtil.formatCustomDateTime(event.getBirth(), "yyyy-MM-dd HH:mm:ss");
			ps.setString(9, birth);
			ps.setDate(10, event.getDeath() != null ? new java.sql.Date(event.getDeath().getTime()) : null);
			ps.setString(11, DateUtil.formatCustomDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
			ps.setInt(12, event.getHuman());
			ps.setString(13, event.getAccuracy());
			ps.setNull(14, java.sql.Types.NULL);
			ps.setInt(15, 1);
			ps.setString(16, Translit.convert(event.getName(), true));
			ps.setDouble(17, event.getDst());
			if (event.getFinalPlace() != null && event.getFinalPlace().getId() > 0)
				ps.setLong(18, event.getFinalPlace().getId());
			else
				ps.setNull(18, java.sql.Types.NULL);
			if (model.getId() != null)
				ps.setLong(19, model.getId());
			System.out.println(ps);

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
			if (event.isNeedSaveCalc()) {
				savePlanets(event);
				saveAspects(event);
				savePlanetSigns(event);
				if (!birth.contains("00:00:00")) {
					saveHouses(event);
					savePlanetHouses(event);
				}
			}
			if (event.isNeedSaveBlob())
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
				sql = "insert into " + table + "(eventid, biography, conversation) values(?,?,?)";
			else {
				sql = "update " + table + " set "
					+ "eventid = ?,"
					+ "biography = ?,"
					+ "conversation = ?"
					+ "where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			ps.setString(2, event.getText());
			ps.setString(3, event.getConversation()); //TODO сохранять изображение в папку
			if (id != 0)
				ps.setLong(4, id);
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
		if (null == eventId) return null;
		Object[] blob = new Object[3];
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from blobs where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, eventId);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString("Biography") != null)
					blob[0] = rs.getString("Biography");
				if (rs.getString("Photo") != null)
					blob[1] = rs.getBytes("Photo");
				if (rs.getString("conversation") != null)
					blob[2] = rs.getString("conversation");
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
		if (rs.getString("name") != null)
			event.setName(rs.getString("name"));
		event.setBirth(DateUtil.getDatabaseDateTime(rs.getString("initialdate")));
		java.sql.Date finaldate = rs.getDate("finaldate");
		if (finaldate != null) 
			event.setDeath(rs.getTimestamp("finaldate"));
		String s = rs.getString("RightHanded");
		event.setRightHanded(s.equals("1") ? true : false);
		if (rs.getString("Rectification") != null) 
			event.setRectification(rs.getInt("Rectification"));
		s = rs.getString("Celebrity");
		event.setCelebrity(s.equals("1") ? true : false);
		if (rs.getString("Comment") != null)
			event.setDescription(rs.getString("Comment"));
		s = rs.getString("Gender");
		event.setFemale(s.equals("1") ? true : false);
		if (rs.getString("Placeid") != null)
			event.setPlaceid(rs.getLong("Placeid"));
		if (rs.getString("finalplaceid") != null)
			event.setFinalPlaceid(rs.getLong("finalplaceid"));
		if (rs.getString("Zone") != null)
			event.setZone(rs.getDouble("Zone"));
		event.setHuman(rs.getInt("human"));
		if (rs.getString("accuracy") != null)
			event.setAccuracy(rs.getString("accuracy"));
		event.setUserid(rs.getLong("userid"));
		event.setDate(DateUtil.getDatabaseDateTime(rs.getString("date")));
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
			String sql = "select * from " + getHouseTable() + " where eventid = ?";
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
	public void savePlanets(Event event) throws DataAccessException {
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
	public void saveHouses(Event event) throws DataAccessException {
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
	public void savePlanetHouses(Event event) throws DataAccessException {
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
		event.getConfiguration().initPlanetSigns(event);
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

	/**
	 * Поиск похожих по характеру людей
	 * @param event человек
	 * @param celebrity true - поиск только знаменитостей
	 * @return список людей
	 * @throws DataAccessException
	 */
	public List<Model> findSimilar(Event event, int celebrity) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
		if (null == event.getConfiguration()) return list;
		Configuration conf = event.getConfiguration();
		conf.initPlanetSigns(event);
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select distinct e.* from " + getPlanetSignTable() + " es" + 
					" inner join " + tableName + " e on es.eventid = e.id" +
				" where sun = ? and mercury = ? and venus = ? and mars = ?" +
					" and e.id <> ?";
			if (celebrity >= 0)
				sql += " and e.celebrity = " + celebrity;
			sql += " order by year(initialdate)";

			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, ((Planet)conf.getPlanets().get(0)).getSign().getId());
			ps.setLong(2, ((Planet)conf.getPlanets().get(4)).getSign().getId());
			ps.setLong(3, ((Planet)conf.getPlanets().get(5)).getSign().getId());
			ps.setLong(4, ((Planet)conf.getPlanets().get(6)).getSign().getId());
			ps.setLong(5, event.getId());
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
/*
select distinct e.* from eventsigns es 
inner join events e on es.eventid = e.id 
where sun = 5 and mercury = 6 and venus = 6 and mars = 3
and e.id <> 31
and e.celebrity = 1 
order by year(initialdate)
 */
	}

	/**
	 * Поиск известных людей, родившихся в указанную дату
	 * @param date дата
	 * @return список людей
	 * @throws DataAccessException
	 */
	public List<Event> findEphemeron(Date date) throws DataAccessException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;

        List<Event> list = new ArrayList<Event>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = 
				"select * from " + tableName +
				" where celebrity = 1 " +
					"and cast(initialDate as char) like ?" + 
				" order by initialDate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, "%-" + DateUtil.formatDateNumber(month) +
				"-" + DateUtil.formatDateNumber(day) + "%");
			rs = ps.executeQuery();
			while (rs.next())
				list.add((Event)init(rs, null));
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

	/**
	 * Поиск события по знаку планеты
	 * @param planet планета
	 * @param sign знак Зодиака
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Model> findByPlanetSign(Planet planet, Sign sign) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select e.* from " + tableName + " e" + 
				" inner join " + getPlanetSignTable() + " ep on e.id = ep.eventid" +
				" where " + planet.getCode() + " = ?" +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, sign.getId());
			System.out.println(ps);
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

	/**
	 * Поиск события по дому планеты
	 * @param planet планета
	 * @param house астрологический дом
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Model> findByPlanetHouse(Planet planet, House house) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select e.* from " + tableName + " e" + 
				" inner join " + getPlanetHouseTable() + " ep on e.id = ep.eventid" +
				" where " + planet.getCode() + " = ?" +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, house.getNumber());
			System.out.println(ps);
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

	/**
	 * Поиск события по знаку планеты
	 * @param planet планета
	 * @param planet2 планета
	 * @param aspect астрологический аспект
	 * @return список событий
	 * @throws DataAccessException
	 * @todo для аспектов тоже сделать отдельную таблицу
	 */
	public List<Model> findByPlanetAspect(Planet planet, Planet planet2, Aspect aspect) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select e.* from " + tableName + " e" + 
				" inner join " + getAspectTable() + " ep on e.id = ep.eventid" +
				" where planetid = ?" + 
					" and planet2id = ?" +
					" and aspectid = ?" +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, planet.getId());
			ps.setLong(2, planet2.getId());
			ps.setLong(3, aspect.getId());
			System.out.println(ps);
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

	/**
	 * Возвращает имя таблицы, хранящей аспекты планет конфигурации события
	 * @return имя ТБД
	 */
	public String getAspectTable() {
		return "eventaspects";
	}

	/**
	 * Сохранение аспектов планет конфигурации события
	 * @param event событие
	 * @throws DataAccessException
	 */
	public void saveAspects(Event event) throws DataAccessException {
		if (null == event.getConfiguration()) return;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String table = getAspectTable();
		try {
			String sql = "update " + table + " set aspectid = null where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			ps.executeUpdate();
			ps.close();

			List<SkyPointAspect> aspects = event.getConfiguration().getAspects();
			for (SkyPointAspect aspect : aspects) {
				SkyPoint point = aspect.getSkyPoint1();
				SkyPoint point2 = aspect.getSkyPoint2();
				if (point.getNumber() > point2.getNumber()) continue;
				sql = "select id from " + table + 
					" where eventid = ?" +
					" and planetid = ?" +
					" and planet2id = ?";
				ps = Connector.getInstance().getConnection().prepareStatement(sql);
				ps.setLong(1, event.getId());
				ps.setLong(2, point.getId());
				ps.setLong(3, point2.getId());
				rs = ps.executeQuery();
				long id = (rs.next()) ? rs.getLong("id") : 0;
				ps.close();
				
				if (0 == id)
					sql = "insert into " + table + " values(0,?,?,?,?)";
				else
					sql = "update " + table + 
						" set eventid = ?,"
						+ " planetid = ?,"
						+ " aspectid = ?,"
						+ " planet2id = ?" +
						" where id = ?";
				ps = Connector.getInstance().getConnection().prepareStatement(sql);
				ps.setLong(1, event.getId());
				ps.setLong(2, point.getId());
				ps.setLong(3, aspect.getAspect().getId());
				ps.setLong(4, point2.getId());
				if (id != 0)
					ps.setLong(5, id);
				ps.executeUpdate();
			}
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

	@Override
	public List<Model> getList() throws DataAccessException {
		return super.getList();
	}

	/**
	 * Инициализация аспектов события
	 * @param event событие
	 * @throws DataAccessException
	 */
	public void initAspects(Event event) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			List<Model> aspectTypes = new AspectTypeService().getList();
			for (Model model : event.getConfiguration().getPlanets()) {
				Planet planet = (Planet)model;

				//создаем карту статистики по аспектам планеты
				Map<String, Integer> aspcountmap = new HashMap<String, Integer>();
				Map<String, String> aspmap = new HashMap<String, String>();
				for (Model asptype : aspectTypes)
					aspcountmap.put(((AspectType)asptype).getCode(), 0);

				//для каждой планеты ищем аспекты
				String sql = "select * from " + getAspectTable() +
					" where eventid = ?"
						+ " and planetid = ?"
						+ " and aspectid > 0";
				ps = Connector.getInstance().getConnection().prepareStatement(sql);
				ps.setLong(1, event.getId());
				ps.setLong(2, planet.getId());
				rs = ps.executeQuery();
				while (rs.next()) {
					Planet planet2 = (Planet)new PlanetService().find(rs.getLong("planet2id"));
					Aspect aspect = (Aspect)new AspectService().find(rs.getLong("aspectid"));
					SkyPointAspect spa = new SkyPointAspect();
					spa.setSkyPoint1(planet);
					spa.setSkyPoint2(planet2);
					spa.setAspect(aspect);
					event.getConfiguration().getAspects().add(spa);

					//фиксируем аспекты планеты
					aspmap.put(planet2.getCode(), aspect.getCode());
					//суммируем аспекты каждого типа для планеты
					String aspectTypeCode = aspect.getType().getCode();
					int score = aspcountmap.get(aspectTypeCode);
					//для людей считаем только аспекты главных планет
					aspcountmap.put(aspectTypeCode, ++score);

					//суммируем сильные аспекты
					aspectTypeCode = "COMMON";
					if (aspect.getType().getParentType() != null &&
							aspect.getType().getParentType().getCode() != null &&
							aspect.getType().getParentType().getCode().equals(aspectTypeCode)) {
						score = aspcountmap.get(aspectTypeCode);
						aspcountmap.put(aspectTypeCode, ++score);
					}
				}
				planet.setAspectCountMap(aspcountmap);
				planet.setAspectMap(aspmap);
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
	 * Поиск событий по периоду
	 * @param date начальная дата
	 * @param date2 конечная дата
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Model> findByDateRange(Date date, Date date2) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName +  
				" where initialdate between ? and ?" +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, DateUtil.dbdtf.format(date));
			ps.setString(2, DateUtil.dbdtf.format(date2));
			System.out.println(ps);
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

	/**
	 * Поиск недавно изменённых событий
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Model> findRecent() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " order by date desc limit 300";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
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

	/**
	 * Поиск события по фрагменту биографии
	 * @param text поисковое выражение
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Model> findByText(String text) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select e.* from " + getBlobTable() + " b" +
				" inner join " + tableName + " e on e.id = b.eventid" +
				" where biography like ? " +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, "%" + text + "%");
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

	/**
	 * Поиск противоположных по характеру людей
	 * @param event человек
	 * @param celebrity true - поиск только знаменитостей
	 * @return список людей
	 * @throws DataAccessException
	 */
	public List<Model> findNonSimilar(Event event, int celebrity) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
		if (null == event.getConfiguration()) return list;
		Configuration conf = event.getConfiguration();
		conf.initPlanetSigns(event);
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select distinct e.* from " + getPlanetSignTable() + " es" + 
					" inner join " + tableName + " e on es.eventid = e.id" +
				" where";

			Map<String, int[]> map = new HashMap<String, int[]>();
			map.put("sun", Sign.getOpposite(((Planet)conf.getPlanets().get(0)).getSign().getId().intValue()));
			map.put("mercury", Sign.getOpposite(((Planet)conf.getPlanets().get(4)).getSign().getId().intValue()));
			map.put("venus", Sign.getOpposite(((Planet)conf.getPlanets().get(5)).getSign().getId().intValue()));
			map.put("mars", Sign.getOpposite(((Planet)conf.getPlanets().get(6)).getSign().getId().intValue()));

			int j = -1;
			for (Entry<String, int[]> entry : map.entrySet()) {
				if (++j > 0)
					sql += " and";
				sql += " " + entry.getKey() + " ";

				int ids[] = entry.getValue();
				if (1 == ids.length)
					sql += "=" + ids[0];
				else {
					sql += "in(";
					int k = -1;
					for (int i : ids) {
						if (++k > 0)
							sql += ",";
						sql += i;
					}
					sql += ")";
				}
			}
			sql += " and e.id <> " + event.getId();
			if (celebrity >= 0)
				sql += " and e.celebrity = " + celebrity;
			sql += " order by year(initialdate)";
			System.out.println(sql);

			ps = Connector.getInstance().getConnection().prepareStatement(sql);
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
/*
select distinct e.* from eventsigns es 
inner join events e on es.eventid = e.id 
where sun = 5 and mercury = 6 and venus = 6 and mars = 3
and e.id <> 31
and e.celebrity = 1 
order by year(initialdate)
 */
	}
}
