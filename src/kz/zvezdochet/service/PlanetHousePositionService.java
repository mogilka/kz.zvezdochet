package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import kz.zvezdochet.bean.House;
import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.PlanetHousePosition;
import kz.zvezdochet.bean.PositionType;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Сервис позиций планет в астрологических домах
 * @author Natalie Didenko
 */
public class PlanetHousePositionService extends DictionaryService {

	public PlanetHousePositionService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "planethouseposition" : "us_planethouseposition";
	}

	@Override
	public Model create() {
		return new PlanetHousePosition();
	}

	/**
	 * Поиск позиции планеты в домах
	 * @param skypoint небесная точка
	 * @return тип позиции
	 * @throws DataAccessException
	 */
	public PlanetHousePosition find(SkyPoint skypoint) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + getTableName() +
				" where planetid = ? " +
					"and houseid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, skypoint.getId());
			ps.setLong(2, skypoint.getHouse().getId());
			rs = ps.executeQuery();
			if (rs.next())
				return init(rs, null);
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
		return null;
/*
select typeid from planethouseposition
where planetid = 20
and houseid = 173
*/
	}


	@Override
	public PlanetHousePosition init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetHousePosition position = (model != null) ? (PlanetHousePosition)model : (PlanetHousePosition)create();
		super.init(rs, position);
		position.setPlanet((Planet)new PlanetService().find(rs.getLong("planetid")));
		position.setHouse((House)new HouseService().find(rs.getLong("houseid")));
		position.setType((PositionType)new PositionTypeService().find(rs.getLong("typeid")));
		return position;
	}
}
