package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис планет
 * @author Natalie Didenko
 */
public class PlanetService extends DictionaryService {

	public PlanetService() {
		tableName = "planets";
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		//TODO оптимизировать и в других сервисах тоже. кэшировать
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by id";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Planet planet = init(rs, null);
				list.add(planet);
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
	public Model save(Model model) throws DataAccessException {
		Planet dict = (Planet)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(ordinalnumber, color, code, name, description, score, fictitious) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"ordinalnumber = ?, " +
					"color = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"score = ?, " +
					"fictitious = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, dict.getNumber());
			ps.setString(2, CoreUtil.colorToRGB(dict.getColor()));
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			ps.setDouble(6, dict.getScore());
			ps.setBoolean(7, dict.isFictitious());
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

	@Override
	public Planet init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Planet planet = (model != null) ? (Planet)model : (Planet)create();
		super.init(rs, planet);
		planet.setScore(rs.getDouble("Score"));
		planet.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		planet.setNumber(rs.getInt("OrdinalNumber"));
		planet.setShortName(rs.getString("shortname"));
		planet.setSymbol(rs.getString("symbol"));
		String s = rs.getString("Fictitious");
		boolean f = s.equals("1") ? true : false;
		planet.setFictitious(f);
		planet.setSynastry(rs.getString("synastry"));
		planet.setNegative(rs.getString("negative"));
		planet.setPositive(rs.getString("positive"));
		s = rs.getString("good");
		f = s.equals("1") ? true : false;
		planet.setGood(f);
		return planet;
	}

	@Override
	public Model create() {
		return new Planet();
	}

	/**
	 * Поиск позиции планеты в знаках Зодиака
	 * @param planet планета
	 * @param type тип позиции планеты в знаке
	 * @param daily true|false - дневное|ночное рождение
	 * @return знак Зодиака
	 * @throws DataAccessException
	 */
	public Sign getSignPosition(Planet planet, String type, boolean daily) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select p.signid from " + getSignPositionTable() + " p " +
				"inner join " + new PositionTypeService().getTableName() + " t on p.typeid = t.id " +
				"where p.planetid = ? " +
					"and t.code like ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, planet.getId());
			ps.setString(2, type);
			rs = ps.executeQuery();
			if (rs.next()) {
				Long id = rs.getLong(1);
				return (Sign)new SignService().find(id);
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
		return null;
	}

	/**
	 * Возвращает наименование таблицы позиций планет в знаках
	 * @return наименование ТБД
	 */
	private String getSignPositionTable() {
		return "planetsignposition";
	}

	/**
	 * Возвращает наименование таблицы позиций планет в домах
	 * @return наименование ТБД
	 */
	private String getHousePositionTable() {
		return "planethouseposition";
	}

	/**
	 * Поиск позиции планеты в домах
	 * @param planet планета
	 * @param type тип позиции планеты в доме
	 * @param daily true|false - дневное|ночное рождение
	 * @return астрологический дом
	 * @throws DataAccessException
	 */
	public House getHousePosition(Planet planet, String type, boolean daily) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select p.houseid from " + getHousePositionTable() + " p " +
				"inner join " + new PositionTypeService().getTableName() + " t on p.typeid = t.id " +
				"where p.planetid = ? " +
					"and t.code like ? " +
					"and p.day = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, planet.getId());
			ps.setString(2, type);
			ps.setBoolean(3, daily);
			rs = ps.executeQuery();
			if (rs.next()) {
				Long id = rs.getLong(1);
				return (House)new HouseService().find(id);
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
		return null;
	}

	/**
	 * Поиск управителя знака
	 * @param sign знак Зодиака
	 * @param daily true|false - дневное|ночное рождение
	 * @return планета
	 * @throws DataAccessException
	 */
	public Planet getRuler(Sign sign, boolean daily) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select p.planetid from " + getSignPositionTable() + " p " +
				"inner join " + new PositionTypeService().getTableName() + " t on p.typeid = t.id " +
				"where p.signid = ? " +
					"and t.code like ? " +
					"and day = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, sign.getId());
			ps.setString(2, "HOME");
			ps.setBoolean(3, daily);
			rs = ps.executeQuery();
			if (rs.next()) {
				Long id = rs.getLong(1);
				return (Planet)find(id);
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
		return null;
	}
}
