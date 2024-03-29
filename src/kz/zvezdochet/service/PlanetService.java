package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kz.zvezdochet.bean.Planet;
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
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "planets" : "us_planets";
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
			if (!model.isExisting()) 
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
			ps.setBoolean(7, dict.isFictious());
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
		planet.setLoyalty(rs.getString("loyalty"));
		planet.setBadName(rs.getString("badname"));
		planet.setGoodName(rs.getString("goodname"));
		planet.setOrbis(rs.getDouble("orbis"));
		planet.setMinOrbis(rs.getDouble("orbis_min"));
		planet.setAspectingName(rs.getString("aspecting"));
		planet.setAspectingBadName(rs.getString("aspecting_bad"));
		planet.setAspectedName(rs.getString("aspected"));
		planet.setAspectedBadName(rs.getString("aspected_bad"));
		return planet;
	}

	@Override
	public Model create() {
		return new Planet();
	}

	/**
	 * Поиск планеты-управителя знака
	 * @param signid идентификатор знака Зодиака
	 * @param daily true|false - дневное|ночное рождение
	 * @param fictious true|false - фиктивные|реальные планеты
	 * @return планета
	 * @throws DataAccessException
	 */
	public Planet getRuler(long signid, boolean daily, boolean fictious) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select p.planetid from " + new PlanetSignPositionService().getTableName() + " p " +
				"inner join " + new PositionTypeService().getTableName() + " t on p.typeid = t.id " +
				"where p.signid = ? " +
					"and t.code like ? " +
					"and day = ? " +
					"and fictious = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, signid);
			ps.setString(2, "HOME");
			ps.setBoolean(3, daily);
			ps.setBoolean(4, fictious);
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
