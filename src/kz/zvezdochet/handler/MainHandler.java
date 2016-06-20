package kz.zvezdochet.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * Обработчик открытия главной страницы
 * @author Nataly Didenko
 *
 */
public class MainHandler {
	@Execute
	public void execute(MApplication app, EModelService service, EPartService partService) {
		MPerspective perspective = (MPerspective)service.find("kz.zvezdochet.runner.perspective.main", app);
		if (perspective != null)
			partService.switchPerspective(perspective);
	}
}