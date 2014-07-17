package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.House;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Реализация сервиса домов
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class HouseService extends ReferenceService {
	
	public HouseService() {
		tableName = "houses";
	}
	
	@Override
	public List<Base> getList() throws DataAccessException {
        List<Base> list = new ArrayList<Base>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " order by id";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				House house = init(rs);
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
	public Base getEntityByCode(String code) throws DataAccessException {
        House house = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " where code = " + code;
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) 
				house = init(rs);
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

	@Override
	public Base find(Long id) throws DataAccessException {
        House house = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		String query;
		try {
			query = "select * from " + tableName + " where id = " + id;
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) 
				house = init(rs);
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

	@Override
	public Base save(Base element) throws DataAccessException {
		House reference = (House)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(ordinalnumber, color, code, name, description, combination, short, designation, diagram, header, linkname) " +
					"values(?,?,?,?,?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
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
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setInt(1, reference.getNumber());
			ps.setString(2, CoreUtil.colorToRGB(reference.getColor()));
			ps.setString(3, reference.getCode());
			ps.setString(4, reference.getName());
			ps.setString(5, reference.getDescription());
			ps.setString(6, reference.getCombination());
			ps.setString(7, reference.getShortName());
			ps.setString(8, reference.getDesignation());
			ps.setString(9, reference.getDiaName());
			ps.setString(10, reference.getHeaderName());
			ps.setString(11, reference.getLinkName());
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
	public House init(ResultSet rs) throws DataAccessException, SQLException {
		House house = (House)super.init(rs);
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
	public Base create() {
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
		String query;
		try {
			query = "select * from " + tableName + " where OrdinalNumber = " + index;
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			rs = ps.executeQuery();
			if (rs.next()) 
				house = init(rs);
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
