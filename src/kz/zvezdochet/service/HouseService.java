package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.House;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис астрологических домов
 * @author Nataly Didenko
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
			sql = "select * from " + tableName + " order by id";
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
					"(ordinalnumber, color, code, name, description, combination, short, designation, diagram, header, linkname) " +
					"values(?,?,?,?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"ordinalnumber = ?, " +
					"color = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"combination = ?, " +
					"short = ?, " +
					"designation = ?, " +
					"diagram = ?, " +
					"header = ?, " +
					"linkname = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, dict.getNumber());
			ps.setString(2, CoreUtil.colorToRGB(dict.getColor()));
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			ps.setString(6, dict.getCombination());
			ps.setString(7, dict.getShortName());
			ps.setString(8, dict.getDesignation());
			ps.setString(9, dict.getDiaName());
			ps.setString(10, dict.getHeaderName());
			ps.setString(11, dict.getLinkName());
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
			update();
		}
		return dict;
	}

	@Override
	public House init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		House house = (model != null) ? (House)model : (House)create();
		super.init(rs, house);
		house.setNumber(rs.getInt("OrdinalNumber"));
		house.setCombination(rs.getString("Combination"));
		house.setShortName(rs.getString("Short"));
		house.setDesignation(rs.getString("Designation"));
		house.setDiaName(rs.getString("Diagram"));
		house.setHeaderName(rs.getString("Header"));
		house.setLinkName(rs.getString("LinkName"));
		house.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
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
}
