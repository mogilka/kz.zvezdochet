package kz.zvezdochet.provider;

import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Place;
import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.service.PlaceService;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

/**
 * Обработчик автозаполнения местностей
 * @author Nataly Didenko
 *
 */
public class PlaceProposalProvider implements IContentProposalProvider {
	private IContentProposal[] contentProposals;
	private boolean filterProposals = false;

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		List<Place> proposals = new ArrayList<Place>();
		try {
			proposals = new PlaceService().findByName(contents);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		if (filterProposals) {
			ArrayList<Object> list = new ArrayList<Object>();
			for (Dictionary p : proposals) {
				if (p.getName().length() >= contents.length()
						&& p.getName().substring(0, contents.length()).equalsIgnoreCase(contents)) {
					list.add(makeContentProposal(p));
				}
			}
			return (IContentProposal[])list.toArray(new IContentProposal[list.size()]);
		}
		if (null == contentProposals) {
			contentProposals = new IContentProposal[proposals.size()];
			for (int i = 0; i < proposals.size(); i++) {
				contentProposals[i] = makeContentProposal(proposals.get(i));
			}
		}
		return contentProposals;
	}

	/**
	 * Инициализация фильтра
	 * @param filterProposals true|false использвать|не использовать фильтр
	 */
	public void setFiltering(boolean filterProposals) {
		this.filterProposals = filterProposals;
		contentProposals = null;
	}

	/**
	 * Метод, формирующий человекопонятное описание объекта
	 * @param proposal объект
	 * @return описание найденного объекта
	 */
	private IContentProposal makeContentProposal(final Dictionary proposal) {
		return new PlaceContentProposal(proposal);
	}

	/**
	 * Обработчик передачи выбранного элемента в визуальный компонент
	 * @author Nataly Didenko
	 *
	 */
	public class PlaceContentProposal implements IContentProposal {
		private Dictionary dict;

		public PlaceContentProposal(Dictionary dict) {
			this.dict = dict;
		}

		@Override
		public String getContent() {
			return dict.getId().toString();
		}

		@Override
		public int getCursorPosition() {
			return 0;
		}

		@Override
		public String getLabel() {
			return dict.getName();
		}

		@Override
		public String getDescription() {
			return dict.getDescription();
		}		

		/**
		 * Возвращает найденный объект
		 * @return объект справочника
		 */
		public Dictionary getObject() {
			return dict;
		}
	}
}
