package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kz.zvezdochet.bean.AspectConfiguration;
import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.EventConfiguration;
import kz.zvezdochet.bean.Planet;
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
		tableName = "eventconfs";
	}

	@Override
	public Model create() {
		return new EventConfiguration();
	}

	@Override
	public EventConfiguration init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		EventConfiguration type = (model != null) ? (EventConfiguration)model : (EventConfiguration)create();
		EventService eventService = new EventService();
		Event event = (Event)eventService.find(rs.getLong("eventid"));
		eventService.initPlanets(event);
		type.setEvent(event);

		type.setVertex(rs.getString("vertex"));
		type.setLeftFoot(rs.getString("leftfoot"));
		type.setRightFoot(rs.getString("rightfoot"));
		type.setBase(rs.getString("base"));
		type.setLeftHand(rs.getString("lefthand"));
		type.setRightHand(rs.getString("righthand"));
		type.setLeftHorn(rs.getString("lefthorn"));
		type.setRightHorn(rs.getString("righthorn"));
		type.setHouseid(rs.getLong("houseid"));
		type.setText(rs.getString("text"));
		type.setLevel(rs.getInt("level"));

		long confid = rs.getLong("confid");
		AspectConfiguration conf = (AspectConfiguration)new AspectConfigurationService().find(confid);
		conf.setVertex(getPlanets(type.getVertex(), event));
		conf.setBase(getPlanets(type.getBase(), event));
		conf.setLeftFoot(getPlanets(type.getLeftFoot(), event));
		conf.setRightFoot(getPlanets(type.getRightFoot(), event));
		conf.setLeftHand(getPlanets(type.getLeftHand(), event));
		conf.setRightHand(getPlanets(type.getRightHand(), event));
		conf.setLeftHorn(getPlanets(type.getLeftHorn(), event));
		conf.setRightHorn(getPlanets(type.getRightHorn(), event));
		conf.setData(type.getHouseid());
		conf.setDescription(type.getText());
		if (type.getLevel() > 0)
			conf.setPoints(type.getLevel());
		type.setConf(conf);
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

	/**
	 * Преобразование идентификаторов планет в массив планет
	 * @param strarr строка идентификаторов планет
	 * @param event событие
	 * @return массив планет
	 */
	private Planet[] getPlanets(String strarr, Event event) {
		Planet[] arr = null;
		if (strarr != null) {
			Map<Long, Planet> planets = event.getPlanets();
			String[] list = strarr.split(",");
			arr = new Planet[list.length];
			int i = -1;
			for (String strid : list) {
				long id = Long.parseLong(strid);
				Planet planet = planets.get(id);
				arr[++i] = planet;
			}
		}
		return arr;
	}
}
