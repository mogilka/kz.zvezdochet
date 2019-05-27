package kz.zvezdochet.service;

import kz.zvezdochet.bean.CardKind;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.TextGenderDictionaryService;


/**
 * Сервис вида космограммы
 * @author Natalie Didenko
 */
public class CardKindService extends TextGenderDictionaryService {

	public CardKindService() {
		tableName = "cardkinds";
	}

	@Override
	public Model create() {
		return new CardKind();
	}
}
