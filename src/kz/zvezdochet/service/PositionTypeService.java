package kz.zvezdochet.service;

import java.util.Locale;

import kz.zvezdochet.bean.PositionType;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;

/**
 * Сервис позиций небесных тел
 * @author Natalie Didenko
 */
public class PositionTypeService extends DictionaryService {

	public PositionTypeService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "positiontype" : "us_positiontype";
	}

	@Override
	public Model create() {
		return new PositionType();
	}
}
