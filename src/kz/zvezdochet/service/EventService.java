package kz.zvezdochet.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Place;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.bean.Reference;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.BaseService;
import kz.zvezdochet.core.tool.Connector;
import kz.zvezdochet.core.util.DateUtil;
import kz.zvezdochet.util.Configuration;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Реализация сервиса событий
 * @author Nataly Didenko
 *
 * @see BaseService Реализация интерфейса сервиса управления объектами на уровне БД  
 */
public class EventService extends BaseService {

	public EventService() {
		tableName = "events";
	}

	/**
	 * Поиск события
	 * @param fullname sql-запрос
	 * @return список событий
	 * @throws DataAccessException
	 */
	public List<Base> getEvents(String fullname) throws DataAccessException {
        List<Base> list = new ArrayList<Base>();
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = 
				"select id, surname, callname, initialdate, comment, sign, element " +
				"from " + tableName + 
				" where callname like ? or surname like ?" +
				" order by initialdate";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setString(1, "%" + fullname + "%");
			ps.setString(2, "%" + fullname + "%");
			rs = ps.executeQuery();
			while (rs.next()) {
				Event event = new Event();
				event.setId(Long.parseLong(rs.getString("ID")));
				if (rs.getString("Callname") != null)
					event.setName(rs.getString("Callname"));
				if (rs.getString("Surname") != null)
					event.setSurname(rs.getString("Surname"));
				event.setBirth(DateUtil.getDatabaseDateTime(rs.getString("initialdate")));
				if (rs.getString("Comment") != null)
					event.setDescription(rs.getString("Comment"));
				if (rs.getString("Sign") != null)
					event.setSign(rs.getString("Sign"));
				if (rs.getString("Element") != null)
					event.setElement(rs.getString("Element"));
				list.add(event);
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
	public Base find(Long id) throws DataAccessException {
        Event event = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String sql = "select * from " + tableName + " where id = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				event = new Event();
				event.setId(Long.parseLong(rs.getString("ID")));
				if (rs.getString("Callname") != null)
					event.setName(rs.getString("Callname"));
				if (rs.getString("Surname") != null)
					event.setSurname(rs.getString("Surname"));
				event.setBirth(DateUtil.getDatabaseDateTime(rs.getString("initialdate")));
				if (rs.getString("Death") != null) 
					event.setBirth(DateUtil.getDatabaseDateTime(rs.getString("finaldate")));
				String s = rs.getString("RightHanded");
				event.setRightHanded(s.equals("1") ? true : false);
				if (rs.getString("Rectification") != null) 
					event.setRectification(Integer.parseInt(rs.getString("Rectification")));
				s = rs.getString("Celebrity");
				event.setCelebrity(s.equals("1") ? true : false);
				if (rs.getString("Comment") != null)
					event.setDescription(rs.getString("Comment"));
				s = rs.getString("Gender");
				event.setFemale(s.equals("1") ? true : false);
				if (rs.getString("Place") != null) {
					Long placeId = Long.parseLong(rs.getString("Place"));
					Base place = new PlaceService().find(placeId);
					event.setPlace((Place)place);
				}
				if (rs.getString("Zone") != null)
					event.setZone(Double.parseDouble(rs.getString("Zone")));
				
				//блобы
				Object[] blob = getEventBlobs(event.getId());
				if (blob != null && blob.length > 0) {
					if (blob[0] != null)
						event.setText(blob[0].toString());
					if (blob[1] != null) {
		                InputStream is = new ByteArrayInputStream((byte[])blob[1]);
						event.setImage(new Image(Display.getDefault(), is));
					}
				}
				
				//конфигурация
				event.setConfiguration(new Configuration());
				//планеты
				sql = "select * from eventplanets where eventid = ?";
				PreparedStatement pst = Connector.getInstance().getConnection().prepareStatement(sql);
				pst.setLong(1, id);
				ResultSet rst = pst.executeQuery();
				if (rst.next()) {
					for (Base entity : event.getConfiguration().getPlanets()) {
						Planet planet = (Planet)entity;
						if (rst.getString(planet.getCode()) != null)
							planet.setCoord(rst.getDouble(planet.getCode()));
					}
				}
				//дома
				sql = "select * from eventhouses where eventid = ?";
				pst = Connector.getInstance().getConnection().prepareStatement(sql);
				pst.setLong(1, id);
				rst = pst.executeQuery();
				if (rst.next()) {
					for (Base entity : event.getConfiguration().getHouses()) {
						House house = (House)entity;
						if (rst.getString(house.getCode()) != null)
							house.setCoord(rst.getDouble(house.getCode()));
					}
				}
				event.getConfiguration().getPlanetInHouses();
				event.getConfiguration().getPlanetInSigns();
				event.getConfiguration().getPlanetAspects();
				event.getConfiguration().getPlanetStatistics();
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
		return event;
	}

	@Override
	public Base save(Base element) throws DataAccessException {
		Event event = (Event)element;
		int result = -1;
        PreparedStatement ps = null;
		try {
			String query;
			if (element.getId() == null) 
				query = "insert into " + tableName + 
					"(surname, callname, gender, initialdate, place, zone, finaldate, " +
					"sign, element, celebrity, comment, rectification, righthanded) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			else
				query = "update " + tableName + " set " +
					"surname = ?, " +
					"callname = ?, " +
					"gender = ?, " +
					"initialdate = ?, " +
					"place = ?, " +
					"zone = ?, " +
					"finaldate = ?, " +
					"sign = ?, " +
					"element = ?, " +
					"celebrity = ?, " +
					"comment = ?, " +
					"rectification = ?, " +
					"righthanded = ? " +
					"where id = " + event.getId();
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setString(1, event.getSurname());
			ps.setString(2, event.getName());
			ps.setBoolean(3, event.isFemale());
			ps.setString(4, DateUtil.formatCustomDateTime(event.getBirth(), "yyyy-MM-dd HH:mm:ss"));
			if (event.getPlace() != null)
				ps.setLong(5, event.getPlace().getId());
			else
				ps.setLong(5, java.sql.Types.NULL);
			ps.setDouble(6, event.getZone());
			ps.setString(7, event.getDeath() != null ? DateUtil.formatCustomDateTime(event.getDeath(), "yyyy-MM-dd HH:mm:ss") : null);
			ps.setString(8, event.getSign());
			ps.setString(9, event.getElement());
			ps.setBoolean(10, event.isCelebrity());
			ps.setString(11, event.getDescription());
			ps.setInt(12, event.getRectification());
			ps.setBoolean(13, event.isRightHanded());
			result = ps.executeUpdate();
			if (result == 1) {
				if (element.getId() == null) { 
					Long autoIncKeyFromApi = -1L;
					ResultSet rsid = ps.getGeneratedKeys();
					if (rsid.next()) {
				        autoIncKeyFromApi = rsid.getLong(1);
				        element.setId(autoIncKeyFromApi);
					    System.out.println("inserted " + tableName + "\t" + autoIncKeyFromApi);
					}
					if (rsid != null) rsid.close();
				}
			}
			//TODO добавить сохранение блобов
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)	ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return event;
	}

	/**
	 * Поиск дополнительной информации о событии
	 * @param eventId идентификатор события
	 * @return массив, содержащий:<br>
	 * - текстовое описание события<br>
	 * - изображение
	 * @throws DataAccessException
	 */
	private Object[] getEventBlobs(Long eventId) throws DataAccessException {
		if (eventId == null) return null;
		Object[] blob = new Object[2];
        PreparedStatement ps = null;
        ResultSet rs = null;
		try {
			String query = "select * from blobs where code = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(query);
			ps.setLong(1, eventId);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString("Biography") != null)
					blob[0] = rs.getString("Biography");
				if (rs.getString("Photo") != null)
					blob[0] = rs.getBytes("Photo");
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
		return blob;
	}

	@Override
	public Reference init(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Base> getList() throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Base create() {
		// TODO Auto-generated method stub
		return null;
	}
}
