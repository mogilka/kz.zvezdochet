package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Реализация сервиса знаков Зодиака
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class SignService extends ReferenceService {
	
	public SignService() {
		tableName = "signs";
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list) {
			list = new ArrayList<Model>();
	        PreparedStatement ps = null;
	        ResultSet rs = null;
			String query;
			try {
				query = "select * from " + tableName + " order by finalpoint";
				ps = Connector.getInstance().getConnection().prepareStatement(query);
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
	public Model save(Model element) throws DataAccessException {
		Sign reference = (Sign)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(ordinalnumber, color, code, name, description, initialpoint, finalpoint, diagram) " +
					"values(?,?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"ordinalnumber = ?, " +
					"color = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"initialpoint = ?, " +
					"finalpoint = ?, " +
					"diagram = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setInt(1, reference.getNumber());
			ps.setString(2, CoreUtil.colorToRGB(reference.getColor()));
			ps.setString(3, reference.getCode());
			ps.setString(4, reference.getName());
			ps.setString(5, reference.getDescription());
			ps.setDouble(6, reference.getInitialPoint());
			ps.setDouble(7, reference.getCoord());
			ps.setString(8, reference.getDiaName());
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
	public Sign init(ResultSet rs, Model base) throws DataAccessException, SQLException {
		Sign sign = new Sign();
		super.init(rs, sign);
		sign.setCoord(Double.parseDouble(rs.getString("FinalPoint")));
		sign.setInitialPoint(rs.getDouble("InitialPoint"));
		sign.setNumber(rs.getInt("OrdinalNumber"));
		sign.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		sign.setDiaName(rs.getString("Diagram"));
		return sign;
	}

	@Override
	public Model create() {
		return new Sign();
	}
}
