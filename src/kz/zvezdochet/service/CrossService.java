package kz.zvezdochet.service;

import java.util.List;

import kz.zvezdochet.bean.Cross;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.GenderTextDiagramService;

/**
 * Сервис крестов
 * @author Nataly Didenko
 */
public class CrossService extends GenderTextDiagramService {

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
}
