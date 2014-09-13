package kz.zvezdochet.service;

import java.util.List;

import kz.zvezdochet.bean.Zone;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.GenderTextDiagramService;

/**
 * Сервис зон
 * @author Nataly Didenko
 */
public class ZoneService extends GenderTextDiagramService {

	public ZoneService() {
		tableName = "zones";
	}

	@Override
	public Model create() {
		return new Zone();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
