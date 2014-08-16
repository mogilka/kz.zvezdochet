package kz.zvezdochet.service;

import kz.zvezdochet.bean.Protraction;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;

/**
 * Сервиса начертаний аспектов
 * @author Nataly Didenko
 */
public class ProtractionService extends DictionaryService {

	public ProtractionService() {
		tableName = "protraction";
	}

	@Override
	public Model create() {
		return new Protraction();
	}
}
