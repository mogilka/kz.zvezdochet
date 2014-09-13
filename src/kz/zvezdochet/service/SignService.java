package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис знаков Зодиака
 * @author Nataly Didenko
 */
public class SignService extends DictionaryService {
	
	public SignService() {
		tableName = "signs";
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list) {
			list = new ArrayList<Model>();
	        PreparedStatement ps = null;
	        ResultSet rs = null;
			String sql;
			try {
				sql = "select * from " + tableName + " order by finalpoint";
				ps = Connector.getInstance().getConnection().prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					Sign sign = init(rs, null);
					list.add(sign);
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
		return list;
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		Sign dict = (Sign)model;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String sql;
			if (null == model.getId()) 
				sql = "insert into " + tableName + 
					"(ordinalnumber, color, code, name, description, initialpoint, finalpoint, diagram) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"ordinalnumber = ?, " +
					"color = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"initialpoint = ?, " +
					"finalpoint = ?, " +
					"diagram = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, dict.getNumber());
			ps.setString(2, CoreUtil.colorToRGB(dict.getColor()));
			ps.setString(3, dict.getCode());
			ps.setString(4, dict.getName());
			ps.setString(5, dict.getDescription());
			ps.setDouble(6, dict.getInitialPoint());
			ps.setDouble(7, dict.getCoord());
			ps.setString(8, dict.getDiaName());
			result = ps.executeUpdate();
			if (result == 1) {
				if (null == model.getId()) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        model.setId(autoIncKeyFromApi);
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
		return dict;
	}

	@Override
	public Sign init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Sign sign = (model != null) ? (Sign)model : (Sign)create();
		super.init(rs, sign);
		sign.setCoord(rs.getDouble("FinalPoint"));
		sign.setInitialPoint(rs.getDouble("InitialPoint"));
		sign.setNumber(rs.getInt("OrdinalNumber"));
		sign.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		sign.setDiaName(rs.getString("Diagram"));
		sign.setElementId(rs.getInt("elementid"));
		sign.setYinyangId(rs.getInt("yinyangid"));
		sign.setCrossId(rs.getInt("crossid"));
		sign.setSquareId(rs.getInt("squareid"));
		sign.setZoneId(rs.getInt("zoneid"));
		sign.setVerticalHalfSphereId(rs.getInt("halfspherevid"));
		sign.setHorizontalalHalfSphereId(rs.getInt("halfspherehid"));
		return sign;
	}

	@Override
	public Model create() {
		return new Sign();
	}
}
