package kz.zvezdochet.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import kz.zvezdochet.Activator;
import kz.zvezdochet.core.handler.Handler;
import kz.zvezdochet.core.ui.util.DialogUtil;
import kz.zvezdochet.core.util.PlatformUtil;
import kz.zvezdochet.part.AspectPart;

/**
 * Сохранение таблицы аспектов в файл
 * @author Natalie Didenko
 * @link https://stackoverflow.com/questions/14795707/export-full-scrolled-composite-to-image
 */
public class AspectsSaveHandler extends Handler {

	@Execute
	public void execute(@Active MPart activePart) {
		try {
			AspectPart aspectPart = (AspectPart)activePart.getObject();
			Image image = new Image(Display.getDefault(), aspectPart.getTableViewer().getTable().getBounds());
	        GC gc = new GC(image);
	        aspectPart.getTableViewer().getTable().print(gc);
	        ImageLoader loader = new ImageLoader();
	        loader.data = new ImageData[] {image.getImageData()};
	        String card = PlatformUtil.getPath(Activator.PLUGIN_ID, "/out/aspects.jpg").getPath();
	        loader.save(card, SWT.IMAGE_JPEG);
	        image.dispose();
	        gc.dispose();
		} catch (Exception e) {
			DialogUtil.alertError(e);
			updateStatus("Ошибка", true);
			e.printStackTrace();
		}
	}
}
