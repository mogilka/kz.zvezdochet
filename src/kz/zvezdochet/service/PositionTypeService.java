package kz.zvezdochet.service;

import kz.zvezdochet.bean.PositionType;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.ReferenceService;

/**
 * Сервис позиций небесных тел
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class PositionTypeService extends ReferenceService {

	public PositionTypeService() {
		tableName = "positiontype";
	}

	@Override
	public Model create() {
		return new PositionType();
	}
}
