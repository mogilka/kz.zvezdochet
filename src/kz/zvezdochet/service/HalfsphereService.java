package kz.zvezdochet.service;

import java.util.List;

import kz.zvezdochet.bean.Halfsphere;
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
}
