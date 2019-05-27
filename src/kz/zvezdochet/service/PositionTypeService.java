package kz.zvezdochet.service;

import kz.zvezdochet.bean.PositionType;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;

/**
 * Сервис позиций небесных тел
 * @author Natalie Didenko
 */
public class PositionTypeService extends DictionaryService {

	public PositionTypeService() {
		tableName = "positiontype";
	}

	@Override
	public Model create() {
		return new PositionType();
	}
}
