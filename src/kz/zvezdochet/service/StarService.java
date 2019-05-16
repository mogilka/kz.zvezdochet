package kz.zvezdochet.service;

import kz.zvezdochet.bean.Star;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DictionaryService;

/**
 * Сервис звёзд
 * @author Natalie Didenko
 */
public class StarService extends DictionaryService {

	public StarService() {
		tableName = "stars";
	}

	@Override
	public Model create() {
		return new Star();
	}
}
