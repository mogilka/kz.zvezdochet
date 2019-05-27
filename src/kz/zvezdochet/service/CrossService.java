package kz.zvezdochet.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import kz.zvezdochet.bean.Cross;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDiagramService;

/**
 * Сервис крестов
 * @author Natalie Didenko
 */
public class CrossService extends TextGenderDiagramService {

	public CrossService() {
		tableName = "crosses";
	}

	@Override
	public Model create() {
		return new Cross();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}

	@Override
	public Cross init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		if (null == model)
			model = create();
		super.init(rs, model);
		Cross cross = (Cross)model;
		cross.setConfiguration(rs.getString("configuration"));
		cross.setTau(rs.getString("tau"));
		return cross;
	}
}
