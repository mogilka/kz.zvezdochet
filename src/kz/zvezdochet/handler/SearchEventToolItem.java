package kz.zvezdochet.handler;

import javax.annotation.PostConstruct;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import kz.zvezdochet.Messages;

/**
 * Поле поиска события
 * @author Natalie Didenko
 *
 */
public class SearchEventToolItem {
	@PostConstruct
	public void createControls(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		Text text = new Text(comp, SWT.SEARCH | SWT.ICON_SEARCH | SWT.CANCEL | SWT.BORDER);
		text.setMessage(Messages.SearchEventToolItem_Search);
		GridDataFactory.fillDefaults().hint(130, SWT.DEFAULT).applyTo(text);
	}
}
