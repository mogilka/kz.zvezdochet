package kz.zvezdochet.service;

import java.util.List;
import java.util.Locale;

import kz.zvezdochet.bean.YinYang;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.core.service.TextGenderDiagramService;

/**
 * Сервис Инь-Ян
 * @author Natalie Didenko
 */
public class YinYangService extends TextGenderDiagramService {

	public YinYangService() {
		String lang = Locale.getDefault().getLanguage();
		tableName = lang.equals("ru") ? "yinyang" : "us_yinyang";
	}

	@Override
	public Model create() {
		return new YinYang();
	}

	@Override
	public List<Model> getList() throws DataAccessException {
		if (null == list)
			list = super.getList();
		return list;
	}
}
