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
				sql = "select * from " + tableName + " order by ordinalnumber";
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
				sql = "insert into " + tableName + " values(0,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else
				sql = "update " + tableName + " set " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"ordinalnumber = ?, " +
					"i0 = ?, " +
					"f0 = ?, " +
					"i1000 = ?, " +
					"f1000 = ?, " +
					"i2000 = ?, " +
					"f2000 = ?, " +
					"i3000 = ?, " +
					"f3000 = ?, " +
					"color = ?, " +
					"diagram = ?, " +
					"elementid = ?, " +
					"yingyangid = ?, " +
					"crossid = ?, " +
					"squareid = ?, " +
					"zoneid = ?, " +
					"halfspherevid = ?, " +
					"halfspherehid = ? " +
					"where id = " + dict.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, dict.getCode());
			ps.setString(2, dict.getName());
			ps.setString(3, dict.getDescription());
			ps.setInt(4, dict.getNumber());
			ps.setDouble(5, dict.getI0());
			ps.setDouble(6, dict.getF0());
			ps.setDouble(7, dict.getI1000());
			ps.setDouble(8, dict.getF1000());
			ps.setDouble(9, dict.getI2000());
			ps.setDouble(10, dict.getF2000());
			ps.setDouble(11, dict.getI3000());
			ps.setDouble(12, dict.getF3000());
			ps.setString(13, CoreUtil.colorToRGB(dict.getColor()));
			ps.setString(14, dict.getDiaName());
			ps.setLong(15, dict.getElementId());
			ps.setLong(16, dict.getYinyangId());
			ps.setLong(17, dict.getCrossId());
			ps.setLong(18, dict.getSquareId());
			ps.setLong(19, dict.getZoneId());
			ps.setLong(20, dict.getVerticalHalfSphereId());
			ps.setLong(21, dict.getHorizontalalHalfSphereId());
			if (model.getId() != null)
				ps.setLong(22, model.getId());

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
		sign.setI0(rs.getDouble("i0"));
		sign.setF0(rs.getDouble("f0"));
		sign.setI1000(rs.getDouble("i1000"));
		sign.setF1000(rs.getDouble("f1000"));
		sign.setI2000(rs.getDouble("i2000"));
		sign.setF2000(rs.getDouble("f2000"));
		sign.setI3000(rs.getDouble("i3000"));
		sign.setF3000(rs.getDouble("f3000"));
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

	/**
	 * Поиск знаков Зодиака категории
	 * @param id идентификатор категории
	 * @return список знаков Зодиака
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
