package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kz.zvezdochet.bean.Aspect;
import kz.zvezdochet.bean.AspectType;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Ingress;
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
	 * @param human null|0|1|2 все|события|живые существа|сообщества
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Model> findByName(String text, Object[] human) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String wherehuman = "";
			if (null == human)
				human = new Object[] {0,1,2};
			String arr = this.arrayToString(human);
			wherehuman = "and human in (" + arr + ")";

			String sql = "select * from " + tableName + 
				" where name like ? " + wherehuman +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, "%" + text + "%");
//			System.out.println(ps);
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
				sql = "insert into " + tableName + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
					"finalplaceid = ?, " +
					"backid = ?, " +
					"moondayid = ?, " +
					"cardkindid = ? " +
					"where id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, event.getName());
			ps.setBoolean(2, event.isFemale());
			if (event.getPlace() != null && event.getPlace().getId() != null && event.getPlace().getId() > 0)
				ps.setLong(3, event.getPlace().getId());
			else if (event.getPlaceid() > 0)
				ps.setLong(3, event.getPlaceid());
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
			Date now = new Date();
			ps.setString(11, event.isNeedSaveCalc() ? DateUtil.formatCustomDateTime(now, "yyyy-MM-dd HH:mm:ss") : DateUtil.formatCustomDateTime(event.getDate(), "yyyy-MM-dd HH:mm:ss"));
			ps.setInt(12, event.getHuman());
			ps.setString(13, event.getAccuracy());
			ps.setNull(14, 3);
			ps.setInt(15, event.isCalculated() ? 1 : 0);
			ps.setString(16, Translit.convert(event.getName(), true));
			ps.setDouble(17, event.getDst());

			if (event.getFinalPlace() != null && event.getFinalPlace().getId() > 0)
				ps.setLong(18, event.getFinalPlace().getId());
			else
				ps.setNull(18, java.sql.Types.NULL);

			ps.setLong(19, event.getBackid());

			if (event.getMoondayid() > 0)
				ps.setLong(20, event.getMoondayid());
			else
				ps.setNull(20, java.sql.Types.NULL);

			if (event.getCardkindid() > 0)
				ps.setLong(21, event.getCardkindid());
			else
				ps.setNull(21, java.sql.Types.NULL);

			if (model.getId() != null)
				ps.setLong(22, model.getId());
			System.out.println(ps);

			result = ps.executeUpdate();
			if (1 == result) {
				if (null == model.getId()) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
				        event.setDate(now);
					    System.out.println("inserted " + tableName + "\t" + autoIncKeyFromApi);
					}
					if (rsid != null)
						rsid.close();
				}
			}
			if (event.isNeedSaveCalc()) {
				savePlanets(event);
				saveAspects(event);
//				saveAspectsh(event);
				savePlanetSigns(event);
				if (!birth.contains("00:00:00")) {
					saveHouses(event);
					savePlanetHouses(event);
				}
//				saveIngress(event);
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
			System.out.println(ps);
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
		String s = rs.getString("initialdate");
		if (s.equals("0000-00-00 00:00:00"))
			event.setBirth(null);
		else
			event.setBirth(DateUtil.getDatabaseDateTime(s));
		java.sql.Date finaldate = rs.getDate("finaldate");
		if (finaldate != null) 
			event.setDeath(rs.getTimestamp("finaldate"));
		s = rs.getString("RightHanded");
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
		event.setFancy(rs.getString("fancy"));
		event.setBackid(rs.getLong("backid"));
		event.setDst(rs.getDouble("dst"));
		s = rs.getString("calculated");
		event.setCalculated(s.equals("1") ? true : false);
		event.setMoondayid(rs.getInt("moondayid"));
		event.setCardkindid(rs.getInt("cardkindid"));
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
				Collection<Planet> planets = event.getConfiguration().getPlanets().values();
				for (Planet planet : planets) {
					String code = planet.getCode();
					if (rs.getString(code) != null) {
						double coord = rs.getDouble(code);
						planet.setCoord(Math.abs(coord));
						if (coord < 0)
							planet.setRetrograde();
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
			
			Map<Long, Planet> planets = event.getConfiguration().getPlanets();
			if (0 == id)
				sql = "insert into " + table + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else {
				sql = "update " + table + " set eventid = ?,";
				for (long i = 19; i < 35; i++) {
					sql += " " + (planets.get(i)).getCode() + " = ?";
					if (i < 34)
						sql += ",";
				}
				sql += " where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			for (int i = 2; i < 18; i++) {
				Planet planet = planets.get((long)i + 17);
				double coord = planet.getCoord();
				if (planet.isRetrograde())
					coord *= -1;
				ps.setDouble(i, coord);
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
		event.getConfiguration().initHouses();
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
			
			Map<Long, Planet> planets = event.getConfiguration().getPlanets();
			if (0 == id)
				sql = "insert into " + table + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else {
				sql = "update " + table + " set eventid = ?,";
				for (long i = 19; i < 35; i++) {
					sql += " " + planets.get(i).getCode() + " = ?";
					if (i < 34)
						sql += ",";
				}
				sql += " where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			for (long i = 19; i < 35; i++) {
				Planet planet = ((Planet)planets.get(i));
				ps.setInt((int)i + 2, planet.getHouse().getNumber());
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
	public void savePlanetSigns(Event event) throws DataAccessException {
		if (null == event.getConfiguration()) return;
		event.getConfiguration().initPlanetSigns(false);
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
			
			Map<Long, Planet> planets = event.getConfiguration().getPlanets();
			if (0 == id)
				sql = "insert into " + table + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else {
				sql = "update " + table + " set eventid = ?,";
				for (long i = 19; i < 35; i++)
					sql += " " + (planets.get(i)).getCode() + " = ?,";
				sql += "celebrity = ?";
				sql += " where id = ?";
			}
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			for (long i = 19; i < 35; i++) {
				Planet planet = planets.get(i);
				ps.setLong((int)i + 2, planet.getSign().getId());
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
		if (null == event.getId()) return null;
        List<Model> list = new ArrayList<Model>();
		if (null == event.getConfiguration()) return list;
		Configuration conf = event.getConfiguration();
		conf.initPlanetSigns(false);
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select distinct e.* from " + getPlanetSignTable() + " es" + 
					" inner join " + tableName + " e on es.eventid = e.id" +
				" where sun = ? and mercury = ? and venus = ? and mars = ?" +
					" and e.id <> ? "
					+ "and e.human = 1";
			if (celebrity >= 0)
				sql += " and e.celebrity = " + celebrity;
			sql += " order by year(initialdate)";

			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			Map<Long, Planet> pmap = conf.getPlanets();
			ps.setLong(1, (pmap.get(19L)).getSign().getId());
			ps.setLong(2, (pmap.get(23L)).getSign().getId());
			ps.setLong(3, (pmap.get(24L)).getSign().getId());
			ps.setLong(4, (pmap.get(25L)).getSign().getId());
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
					"and human = 1 " +
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
	 * Возвращает имя таблицы, хранящей аспекты домов конфигурации события
	 * @return имя ТБД
	 */
	public String getAspectHouseTable() {
		return "eventaspectsh";
	}

	/**
	 * Сохранение аспектов планет события
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
				if (point.getNumber() > point2.getNumber())
					continue;
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
					sql = "insert into " + table + " values(0,?,?,?,?,?,?)";
				else
					sql = "update " + table + 
						" set eventid = ?,"
						+ " planetid = ?,"
						+ " aspectid = ?,"
						+ " planet2id = ?,"
						+ " exact = ?,"
						+ " application = ?" +
						" where id = ?";
				ps = Connector.getInstance().getConnection().prepareStatement(sql);
				ps.setLong(1, event.getId());
				ps.setLong(2, point.getId());
				ps.setLong(3, aspect.getAspect().getId());
				ps.setLong(4, point2.getId());
				ps.setInt(5, aspect.isExact() ? 1 : 0);
				ps.setInt(6, aspect.isApplication() ? 1 : 0);
				if (id > 0)
					ps.setLong(7, id);
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
			Map<Long, Planet> pmap = event.getConfiguration().getPlanets();
			for (Planet planet : pmap.values()) {
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
					Planet planet2 = pmap.get(rs.getLong("planet2id"));
					Aspect aspect = (Aspect)new AspectService().find(rs.getLong("aspectid"));
					SkyPointAspect spa = new SkyPointAspect();
					spa.setSkyPoint1(planet);
					spa.setSkyPoint2(planet2);
					spa.setAspect(aspect);
					spa.setExact(rs.getBoolean("exact"));
					spa.setApplication(rs.getBoolean("application"));
					event.getConfiguration().getAspects().add(spa);

					//фиксируем аспекты планеты
					aspmap.put(planet2.getCode(), aspect.getCode());
					//суммируем аспекты каждого типа для планеты
					String aspectTypeCode = aspect.getType().getCode();
					int score = aspcountmap.get(aspectTypeCode);
					//для людей считаем только аспекты главных планет
					aspcountmap.put(aspectTypeCode, ++score);

					//суммируем сильные аспекты
					String common = "COMMON";
					if (aspect.getType().getParentType() != null &&
							aspect.getType().getParentType().getCode().equals(common)) {
						score = aspcountmap.get(common);
						aspcountmap.put(common, ++score);
					}

					if (aspect.isMain()) {
						double points = aspect.getPoints();
						planet.addPoints(points);
						planet2.addPoints(points);
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
	 * Поиск событий за период
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
	public List<Model> findRecent(boolean celebrity) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + 
				" where celebrity = ? " +
					"and placeid <> 7095 " +
				"order by date desc limit 30";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, celebrity ? 1 : 0);
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
		conf.initPlanetSigns(false);
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select distinct e.* from " + getPlanetSignTable() + " es" + 
					" inner join " + tableName + " e on es.eventid = e.id" +
				" where";

			Map<String, int[]> map = new HashMap<String, int[]>();
			Map<Long, Planet> pmap = conf.getPlanets();
			map.put("sun", Sign.getOpposite((pmap.get(19L)).getSign().getId().intValue()));
			map.put("mercury", Sign.getOpposite((pmap.get(23L)).getSign().getId().intValue()));
			map.put("venus", Sign.getOpposite((pmap.get(24L)).getSign().getId().intValue()));
			map.put("mars", Sign.getOpposite((pmap.get(25L)).getSign().getId().intValue()));

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
//			System.out.println(sql);

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

	/**
	 * Поиск связанного события для импорта
	 * @param id идентификатор серверного события
	 * @return событие
	 * @throws DataAccessException
	 */
	public Model findBack(Long id) throws DataAccessException {
		if (null == id) return null;
		Model model = create();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " where backid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			if (rs.next()) 
				model = init(rs, model);
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
		return model;
	}

	/**
	 * Сохранение аспектов домов конфигурации события
	 * @param event событие
	 * @throws DataAccessException
	 */
	public void saveAspectsh(Event event) throws DataAccessException {
		if (null == event.getConfiguration()) return;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String table = getAspectHouseTable();
		try {
			String sql = "update " + table + " set aspectid = null where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			ps.executeUpdate();
			ps.close();

			List<SkyPointAspect> aspects = event.getConfiguration().getAspectsh();
			for (SkyPointAspect aspect : aspects) {
				SkyPoint point = aspect.getSkyPoint1();
				SkyPoint point2 = aspect.getSkyPoint2();
				sql = "select id from " + table + 
					" where eventid = ?" +
					" and planetid = ?" +
					" and houseid = ?";
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
						+ " houseid = ?" +
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

	/**
	 * Поиск подходящих по совместимости партнёров
	 * @param event человек
	 * @param celebrity true - поиск только знаменитостей
	 * @return список людей
	 * @throws DataAccessException
	 */
	public List<Model> findAkin(Event event, int celebrity) throws DataAccessException {
		if (null == event.getId())
			return null;
		if (event.getHuman() != 1)
			return null;

        List<Model> list = new ArrayList<Model>();
		if (null == event.getConfiguration())
			return list;
		Configuration conf = event.getConfiguration();
		conf.initPlanetSigns(false);
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select e.* from " + getPlanetSignTable() + " es" + 
				" inner join " + tableName + " e on es.eventid = e.id" +
				" where gender <> ? " +
					"and e.id <> ? " +
					"and e.human = 1";
			if (celebrity >= 0)
				sql += " and e.celebrity = " + celebrity;

			initPlanets(event);
			int year = event.getBirthYear();
			Sign sunSign, merSign, venSign, marSign;
			Map<String, int[]> map = new HashMap<>();
			Map<Long, Planet> planets = event.getConfiguration().getPlanets();
			sunSign = SkyPoint.getSign(planets.get(19L).getCoord(), year);
			map.put("Sun", Sign.getByElement(sunSign.getId().intValue()));
			merSign = SkyPoint.getSign(planets.get(23L).getCoord(), year);
			map.put("Mercury", Sign.getByElement(merSign.getId().intValue()));
			venSign = SkyPoint.getSign(planets.get(24L).getCoord(), year);
			map.put("Venus", Sign.getByElement(venSign.getId().intValue()));
			marSign = SkyPoint.getSign(planets.get(25L).getCoord(), year);
			map.put("Mars", Sign.getByElement(marSign.getId().intValue()));

			Iterator<Map.Entry<String, int[]>> iterator = map.entrySet().iterator();
		    while (iterator.hasNext()) {
		    	Entry<String, int[]> entry = iterator.next();
		    	String k = entry.getKey();
		    	int v[] = entry.getValue();
				sql += " and " + k + " ";
				if (1 == v.length)
					sql += "=" + v[0];
				else {
					sql += "in(";
					int l = -1;
					for (int i : v) {
						if (++l > 0)
							sql += ",";
						sql += i;
					}
					sql += ")";
				}
		    }
			sql += " order by year(initialdate)";
			
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, event.isFemale() ? 1 : 0);
			ps.setLong(2, event.getId());
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
SELECT * FROM `eventsigns` 
WHERE sun in (9,14) 
and mercury in (1,10,11) 
and venus in (1,10,11) 
and mars in (7,12)
and celebrity = 1
*/
	}

	/**
	 * Поиск событий по дате
	 * @param date дата
	 * @param limit количество событий
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Event> findByDate(String date, int limit) throws DataAccessException {
        List<Event> list = new ArrayList<Event>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName +  
				" where initialdate like ?" +
				" limit ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, date + "%");
			ps.setInt(2, limit);
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
	 * Сохранение ингрессий события
	 * @param event событие
	 * @throws DataAccessException
	 */
	public void saveIngress(Event event) throws DataAccessException {
		if (null == event.getConfiguration()) return;
        PreparedStatement ps = null;
        ResultSet rs = null;
        IngressService service = new IngressService();
        String table = service.getTableName();
		try {
			List<Model> list = event.getConfiguration().getIngresses();

			String sql = "update " + table + " set typeid = 9, objectid = null, skypointid = null where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, event.getId());
			ps.executeUpdate();
			ps.close();

			for (Model model : list) {
				Ingress ingress = (Ingress)model;

				sql = "select id from " + table + 
					" where eventid = ?" +
					" and planetid = ?" +
					" and typeid = ?";
				ps = Connector.getInstance().getConnection().prepareStatement(sql);
				ps.setLong(1, event.getId());
				ps.setLong(2, ingress.getPlanet().getId());
				ps.setLong(3, 9);
				rs = ps.executeQuery();
				long id = (rs.next()) ? rs.getLong("id") : 0;
				ps.close();
				
				if (0 == id)
					sql = "insert into " + table + " values(?,?,?,?,0,?)";
				else
					sql = "update " + table + 
						" set eventid = ?,"
						+ " planetid = ?,"
						+ " objectid = ?,"
						+ " typeid = ?,"
						+ " skypointid = ?" +
						" where id = ?";
				ps = Connector.getInstance().getConnection().prepareStatement(sql);
				ps.setLong(1, event.getId());
				ps.setLong(2, ingress.getPlanet().getId());

				if (ingress.getObject() != null)
					ps.setLong(3, ingress.getObject().getId());
				else
					ps.setLong(3, java.sql.Types.NULL);

				ps.setLong(4, ingress.getType().getId());

				if (ingress.getSkyPoint() != null)
					ps.setLong(5, ingress.getSkyPoint().getId());
				else
					ps.setLong(5, java.sql.Types.NULL);

				if (id > 0)
					ps.setLong(6, id);
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
}
