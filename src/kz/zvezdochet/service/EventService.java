package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kz.zvezdochet.bean.Aspect;
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
	 * @param human -1|0|1 все|события|люди
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
				sql = "insert into " + tableName + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"name = ?, " +
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
					"date = ?, " +
					"accuracy = ?, " +
					"human = ?," +
					"userid = ? " +
					"where id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, event.getName());
			ps.setBoolean(2, event.isFemale());
			if (event.getPlace() != null && event.getPlace().getId() != null)
				ps.setLong(3, event.getPlace().getId());
			else
				ps.setLong(3, java.sql.Types.NULL);
			ps.setDouble(4, event.getZone());
			ps.setString(5, event.getSign());
			ps.setString(6, event.getElement());
			ps.setBoolean(7, event.isCelebrity());
			ps.setString(8, event.getDescription());
			ps.setInt(9, event.getRectification());
			ps.setBoolean(10, event.isRightHanded());
			String birth = DateUtil.formatCustomDateTime(event.getBirth(), "yyyy-MM-dd HH:mm:ss");
			ps.setString(11, birth);
			ps.setString(12, event.getDeath() != null ? DateUtil.formatCustomDateTime(event.getDeath(), "yyyy-MM-dd HH:mm:ss") : null);
			ps.setString(13, DateUtil.formatCustomDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
			ps.setString(14, event.getAccuracy());
			ps.setBoolean(15, event.isHuman());
			ps.setLong(16, 0);
			if (model.getId() != null) 
				ps.setLong(17, model.getId());
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
			savePlanets(event);
			savePlanetSigns(event);
			saveBlob(event);
			if (!birth.contains("00:00:00")) {
				saveHouses(event);
				savePlanetHouses(event);
			}
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
		if (rs.getString("finaldate") != null) 
			event.setDeath(DateUtil.getDatabaseDateTime(rs.getString("finaldate")));
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
		if (rs.getString("Sign") != null)
			event.setSign(rs.getString("Sign"));
		if (rs.getString("Element") != null)
			event.setElement(rs.getString("Element"));
		if (rs.getString("Placeid") != null)
			event.setPlaceid(rs.getLong("Placeid"));
		if (rs.getString("Zone") != null)
			event.setZone(rs.getDouble("Zone"));
		s = rs.getString("human");
		event.setHuman(s != null && s.equals("1") ? true : false);
		if (rs.getString("accuracy") != null)
			event.setAccuracy(rs.getString("accuracy"));
		event.setUserid(rs.getLong("userid"));
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
		conf.initPlanetSigns();
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
			ps.setInt(1, sign.getNumber());
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
				" inner join " + getPlanetTable() + " ep on e.id = ep.eventid" +
				" where " + aspect.getValue() + 
					" between abs(" +
					"abs(" + planet.getCode() + ") + " + aspect.getOrbis() +
						"- abs(" + planet2.getCode() + ") + " + aspect.getOrbis() + ")" +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
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
}
