package kz.zvezdochet.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import kz.zvezdochet.bean.Planet;
import kz.zvezdochet.bean.PlanetSignPosition;
import kz.zvezdochet.bean.PositionType;
import kz.zvezdochet.bean.Sign;
import kz.zvezdochet.bean.SkyPoint;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.tool.Connector;

/**
 * Сервис позиций планет в знаках Зодиака
 * @author Natalie Didenko
 */
public class PlanetSignPositionService extends DictionaryService {

	public PlanetSignPositionService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "planetsignposition" : "us_planetsignposition";
	}

	@Override
	public Model create() {
		return new PlanetSignPosition();
	}

	/**
	 * Поиск позиции планеты в знаках Зодиака
	 * @param skypoint небесная точка
	 * @return тип позиции
	 * @throws DataAccessException
	 */
	public PlanetSignPosition find(SkyPoint skypoint) throws DataAccessException {
        PreparedStatement ps = null;
        ResultSet rs = null;
		String sql;
		try {
			sql = "select * from " + getTableName() + 
				" where planetid = ? " +
					"and signid = ?";
			ps = Connector.getInstance().getConnection().prepareStatement(sql);
			ps.setLong(1, skypoint.getId());
			ps.setLong(2, skypoint.getSign().getId());
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
select typeid from planetsignposition
where planetid = 20
and signid = 10
 */
	}

	@Override
	public PlanetSignPosition init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		PlanetSignPosition position = (model != null) ? (PlanetSignPosition)model : (PlanetSignPosition)create();
		super.init(rs, position);
		position.setPlanet((Planet)new PlanetService().find(rs.getLong("planetid")));
		position.setSign((Sign)new SignService().find(rs.getLong("signid")));
		position.setType((PositionType)new PositionTypeService().find(rs.getLong("typeid")));
		return position;
	}
}
