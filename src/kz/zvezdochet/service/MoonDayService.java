package kz.zvezdochet.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.bean.MoonDay;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.ModelService;

/**
 * Сервис лунных дней
 * @author Natalie Didenko
 */
public class MoonDayService extends ModelService {

	public MoonDayService() {
		tableName = "moonday";
	}

	@Override
	public MoonDay init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		MoonDay day = (model != null) ? (MoonDay)model : (MoonDay)create();
		day.setId(rs.getLong("id"));
		day.setSymbol(rs.getString("symbol"));
		day.setBirth(rs.getString("birth"));
		day.setMineral(rs.getString("mineral"));
		return day;
	}

	@Override
	public Model create() {
		return new MoonDay();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}

	@Override
	public Model save(Model model) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
