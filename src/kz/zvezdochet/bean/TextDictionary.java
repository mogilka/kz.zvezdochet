package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Dictionary;

/**
 * Прототип справочника многотекстовых значений
 * @author Nataly Didenko
 */
public class TextDictionary extends Dictionary {
	private static final long serialVersionUID = 6051185469564751147L;

	/**
	 * Текст
	 */
    private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
