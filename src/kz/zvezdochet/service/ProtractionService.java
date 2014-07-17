package kz.zvezdochet.service;

import kz.zvezdochet.bean.Protraction;
import kz.zvezdochet.core.bean.Base;
import kz.zvezdochet.core.service.ReferenceService;

/**
 * Реализация сервиса начертаний аспектов
 * @author Nataly Didenko
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class ProtractionService extends ReferenceService {

	public ProtractionService() {
		tableName = "protraction";
	}

	@Override
	public Base create() {
		return new Protraction();
	}
}
