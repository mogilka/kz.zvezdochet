package kz.zvezdochet.provider;

import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.bean.Event;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.service.EventService;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

/**
 * Обработчик автопоиска событий
 * @author Nataly Didenko
 *
 */
public class EventProposalProvider implements IContentProposalProvider {
	private Object[] humanFilter = new Object[] {0,1,2};

	public EventProposalProvider(Object[] human) {
		humanFilter = human;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		IContentProposal[] contentProposals = null;
		List<Model> proposals = new ArrayList<Model>();
		try {
			proposals = new EventService().findByName(contents, humanFilter);
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
	private IContentProposal makeContentProposal(final Model proposal) {
		return new EventContentProposal((Event)proposal);
	}

	/**
	 * Обработчик передачи выбранного элемента в визуальный компонент
	 * @author Nataly Didenko
	 *
	 */
	public class EventContentProposal implements IContentProposal {
		private Event dict;

		public EventContentProposal(Event dict) {
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
			return dict.toString();
		}

		@Override
		public String getDescription() {
			return dict.getDescription();
		}		

		/**
		 * Возвращает найденный объект
		 * @return объект справочника
		 */
		public Event getObject() {
			return dict;
		}
	}
}
