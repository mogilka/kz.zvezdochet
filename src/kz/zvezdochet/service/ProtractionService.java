package kz.zvezdochet.service;

import kz.zvezdochet.bean.Protraction;
import kz.zvezdochet.core.bean.BaseEntity;
import kz.zvezdochet.core.service.ReferenceService;

/**
 * Реализация сервиса начертаний аспектов
 * @author nataly
 *
 * @see ReferenceService Реализация сервиса справочников  
 */
public class ProtractionService extends ReferenceService {

	public ProtractionService() {
		tableName = "protraction";
	}

	@Override
	public BaseEntity createEntity() {
		return new Protraction();
	}
}
