package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Reference;

/**
 * Прототип Справочника многотекстовых значений
 * @author Nataly
 *
 * @see Reference Прототип справочника
 */
public class TextReference extends Reference {
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
