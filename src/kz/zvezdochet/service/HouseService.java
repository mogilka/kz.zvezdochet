package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Element;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис астрологических домов
 * @author Natalie Didenko
 */
public class HouseService extends DictionaryService {
	
	public HouseService() {
		tableName = "houses";
	}

	@Override
	public List<Model> getList() throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " order by ordinalnumber";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				House house = init(rs, null);
				list.add(house);
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
		House dict = (House)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(ordinalnumber, color, code, name, description, designation, diagram, category) " +
					"values(?,?,?,?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"ordinalnumber = ?, " +
					"color = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"designation = ?, " +
					"diagram = ?, " +
					"category = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, dict.getNumber());
			ps.setString(2, CoreUtil.colorToRGB(dict.getColor()));
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			ps.setString(8, dict.getDesignation());
			ps.setString(9, dict.getDiaName());
			ps.setString(11, dict.getCategory());
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
	public House init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		House house = (model != null) ? (House)model : (House)create();
		super.init(rs, house);
		house.setNumber(rs.getInt("OrdinalNumber"));
		house.setDesignation(rs.getString("Designation"));
		house.setDiaName(rs.getString("Diagram"));
		house.setCategory(rs.getString("category"));
		house.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		house.setElementId(rs.getInt("elementid"));
		house.setYinyangId(rs.getInt("yinyangid"));
		house.setCrossId(rs.getInt("crossid"));
		house.setSquareId(rs.getInt("squareid"));
		house.setZoneId(rs.getInt("zoneid"));
		house.setVerticalHalfSphereId(rs.getInt("halfspherevid"));
		house.setHorizontalalHalfSphereId(rs.getInt("halfspherehid"));
		house.setMain(rs.getBoolean("main"));
		house.setExportOnSign(rs.getBoolean("exportonsign"));
		house.setElement((Element)new ElementService().find(rs.getLong("elementid")));
		house.setStage(rs.getString("stage"));
		house.setPositive(rs.getString("positive"));
		house.setNegative(rs.getString("negative"));
		house.setSynastry(rs.getString("synastry"));
		house.setGeneral(rs.getString("general"));
		return house;
	}

	@Override
	public Model create() {
		return new House();
	}

	/**
	 * Поиск астрологического дома по номеру
	 * @param index порядковый номер дома
	 * @return астрологический дом
	 * @throws DataAccessException 
	 */
	public House getHouse(int index) throws DataAccessException {
        House house = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + tableName + " where OrdinalNumber = " + index;
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) 
				house = init(rs, null);
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
		return house;
	}

	/**
	 * Поиск астрологических домов категории
	 * @param id идентификатор категории
	 * @return список домов
	 * @throws DataAccessException
	 */
	public List<Model> findByCross(long id) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " where crossignid = " + id;
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Model type = init(rs, create());
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
