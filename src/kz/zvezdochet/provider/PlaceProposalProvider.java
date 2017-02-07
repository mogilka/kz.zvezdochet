package kz.zvezdochet.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import kz.zvezdochet.bean.Place;
import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.service.PlaceService;

/**
 * Обработчик автозаполнения местностей
 * @author Nataly Didenko
 *
 */
public class PlaceProposalProvider implements IContentProposalProvider {
	
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		IContentProposal[] contentProposals = null;
		List<Place> proposals = new ArrayList<Place>();
		try {
			proposals = new PlaceService().findByName(contents);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		contentProposals = new IContentProposal[proposals.size()];
		for (int i = 0; i < proposals.size(); i++)
			contentProposals[i] = makeContentProposal(proposals.get(i));
		return contentProposals;
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
