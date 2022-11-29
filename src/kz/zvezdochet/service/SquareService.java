package kz.zvezdochet.service;

import java.util.List;
import java.util.Locale;

import kz.zvezdochet.bean.Square;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDiagramService;

/**
 * Сервис крестов
 * @author Natalie Didenko
 */
public class SquareService extends TextGenderDiagramService {

	public SquareService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "squares" : "us_squares";
	}

	@Override
	public Model create() {
		return new Square();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
