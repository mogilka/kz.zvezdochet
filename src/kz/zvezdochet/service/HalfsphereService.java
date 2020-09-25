package kz.zvezdochet.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.bean.Halfsphere;
import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDiagramService;

/**
 * Сервис полусфер
 * @author Natalie Didenko
 */
public class HalfsphereService extends TextGenderDiagramService {

	public HalfsphereService() {
		tableName = "halfspheres";
	}

	@Override
	public Model create() {
		return new Halfsphere();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}

	@Override
	public Halfsphere init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		Dictionary type = super.init(rs, model);
		Halfsphere halfsphere = (Halfsphere)type;
		halfsphere.setCup(rs.getString("cup"));
		halfsphere.setBow(rs.getString("bow"));
		return halfsphere;
	}
}
