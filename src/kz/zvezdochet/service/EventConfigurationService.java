package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.AspectConfiguration;
import kz.zvezdochet.service.AspectConfigurationService;
import kz.zvezdochet.bean.EventConfiguration;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Сервис конфигурации аспектов события
 * @author Natalie Didenko
 */
public class EventConfigurationService extends ModelService {

	public EventConfigurationService() {
		tableName = "eventconf";
	}

	@Override
	public Model create() {
		return new EventConfiguration();
	}

	@Override
	public EventConfiguration init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		EventConfiguration type = (model != null) ? (EventConfiguration)model : (EventConfiguration)create();
		type.setEventid(rs.getLong("eventid"));
		type.setVertex(rs.getString("vertex"));
		type.setLeftFoot(rs.getString("leftfoot"));
		type.setRightFoot(rs.getString("rightfoot"));
		type.setBase(rs.getString("base"));
		type.setLeftHand(rs.getString("lefthand"));
		type.setRightHand(rs.getString("righthand"));
		type.setLeftHorn(rs.getString("lefthorn"));
		type.setRightHorn(rs.getString("righthorn"));
		type.setConf((AspectConfiguration)new AspectConfigurationService().find(rs.getLong("confid")));
		return type;
	}

	/**
	 * Поиск конфигураций аспектов персоны
	 * @param personid идентификатор персоны
	 * @return список конфигураций
	 */
	public List<EventConfiguration> findByEvent(Long personid) throws DataAccessException {
		if (null == personid) return null;
		List<EventConfiguration> list = new ArrayList<EventConfiguration>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " where eventid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, personid);
			rs = ps.executeQuery();
			while (rs.next())
				list.add(init(rs, null));
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
		// TODO Auto-generated method stub
		return null;
	}
}
