package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kz.zvezdochet.bean.Element;
import kz.zvezdochet.bean.YinYang;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDiagramService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.CoreUtil;

/**
 * Сервис стихий
 * @author Natalie Didenko
 */
public class ElementService extends TextGenderDiagramService {

	public ElementService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "elements" : "us_elements";
	}

	@Override
	public Element init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Element element = (model != null) ? (Element)model : (Element)create();
		super.init(rs, model);
		element.setTemperament(rs.getString("temperament"));
		element.setColor(CoreUtil.rgbToColor(rs.getString("Color")));
		element.setDimColor(CoreUtil.rgbToColor(rs.getString("dimcolor")));
		element.setLightColor(CoreUtil.rgbToColor(rs.getString("lightcolor")));
		element.setDiaName(rs.getString("Diagram"));
		element.setShortName(rs.getString("shortname"));
		YinYangService service = new YinYangService();
		element.setYinYang((YinYang)service.find(rs.getLong("yinyangid")));
		element.setSynastry(rs.getString("synastry"));
		element.setTriangle(rs.getString("triangle"));
		int val = Integer.parseInt(rs.getString("loyalty"));
		element.setLoyalty(1 == val ? true : false);
		return element;
	}

	@Override
	public Model create() {
		return new Element();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}

	public List<Model> getList(boolean duplicate) throws DataAccessException {
        List<Model> list = new ArrayList<Model>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName +
				" where duplicate = ?" +
				" order by name";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setInt(1, duplicate ? 1 : 0);
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

	@Override
	public Map<Long, Model> getMap() throws DataAccessException {
		if (null == map) {
			map = new HashMap<>();
	        PreparedStatement ps = null;
	        ResultSet rs = null;
			String sql;
			try {
				sql = "select * from " + tableName + " where id < 5 order by name";
				ps = Connector.getInstance().getConnection().prepareStatement(sql);
				rs = ps.executeQuery();
				while (rs.next()) {
					Model type = init(rs, create());
					map.put(type.getId(), type);
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
		return map;
	}
}
