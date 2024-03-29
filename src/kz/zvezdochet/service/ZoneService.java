package kz.zvezdochet.service;

import java.util.List;
import java.util.Locale;

import kz.zvezdochet.bean.Zone;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDiagramService;

/**
 * Сервис зон
 * @author Natalie Didenko
 */
public class ZoneService extends TextGenderDiagramService {

	public ZoneService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "zones" : "us_zones";
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
