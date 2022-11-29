package kz.zvezdochet.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import kz.zvezdochet.bean.CardKind;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.bean.TextGenderDictionary;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDictionaryService;

/**
 * Сервис вида космограммы
 * @author Natalie Didenko
 */
public class CardKindService extends TextGenderDictionaryService {

	public CardKindService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "cardkinds" : "us_cardkinds";
	}

	@Override
	public Model create() {
		return new CardKind();
	}

	@Override
	public TextGenderDictionary init(ResultSet rs, Model model) throws DataAccessException, SQLException {
		CardKind type = (model != null) ? (CardKind)model : (CardKind)create();
		super.init(rs, model);
		type.setHigh(rs.getString("high"));
		type.setMedium(rs.getString("medium"));
		type.setLow(rs.getString("low"));
		type.setDegree(rs.getString("degree"));
		return type;
	}
}
