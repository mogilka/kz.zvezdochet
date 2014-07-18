package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Реализация сервиса планет
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class PlanetService extends ReferenceService {

	public PlanetService() {
		tableName = "planets";
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
	public Base save(Base element) throws DataAccessException {
		Planet reference = (Planet)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(ordinalnumber, color, code, name, description, score, sword, shield, belt, kernel, " +
						"mine, strong, weak, retro, damaged, perfect, fictitious) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"ordinalnumber = ?, " +
					"color = ?, " +
					"code = ?, " +
					"name = ?, " +
					"description = ?, " +
					"score = ?, " +
					"sword = ?, " +
					"shield = ?, " +
					"belt = ?, " +
					"kernel = ?, " +
					"mine = ?, " +
					"strong = ?, " +
					"weak = ?, " +
					"retro = ?, " +
					"damaged = ?, " +
					"perfect = ?, " +
					"fictitious = ? " +
					"where id = " + reference.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setInt(1, reference.getNumber());
			ps.setString(2, CoreUtil.colorToRGB(reference.getColor()));
			ps.setString(3, reference.getCode());
			ps.setString(4, reference.getName());
			ps.setString(5, reference.getDescription());
			ps.setDouble(6, reference.getScore());
			ps.setString(7, reference.getSwordText());
			ps.setString(8, reference.getShieldText());
			ps.setString(9, reference.getBeltText());
			ps.setString(10, reference.getKernelText());
			ps.setString(11, reference.getMineText());
			ps.setString(12, reference.getStrongText());
			ps.setString(13, reference.getWeakText());
			ps.setString(14, reference.getRetroText());
			ps.setString(15, reference.getDamagedText());
			ps.setString(16, reference.getPerfectText());
			ps.setBoolean(17, reference.isFictitious());
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
	public Planet init(ResultSet rs, Base base) throws DataAccessException, SQLException {
		Planet planet = new Planet();
		super.init(rs, planet);
		planet.setScore(Double.parseDouble(rs.getString("Score")));
		planet.setSwordText(rs.getString("Sword"));
		planet.setShieldText(rs.getString("Shield"));
		planet.setBeltText(rs.getString("Belt"));
		planet.setKernelText(rs.getString("Kernel"));
		planet.setMineText(rs.getString("Mine"));
		planet.setStrongText(rs.getString("Strong"));
		planet.setWeakText(rs.getString("Weak"));
		planet.setDamagedText(rs.getString("Damaged"));
		planet.setPerfectText(rs.getString("Perfect"));
		planet.setRetroText(rs.getString("Retro"));
		planet.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		planet.setNumber(Integer.parseInt(rs.getString("OrdinalNumber")));
//		Image img = Toolkit.getDefaultToolkit().createImage(rs.getBytes("Image")); TODO
//      planet.setImage(img);
		String s = rs.getString("Fictitious");
		boolean f = s.equals("1") ? true : false;
		planet.setFictitious(f);
		return planet;
	}

	@Override
	public Base create() {
		return new Planet();
	}
}
