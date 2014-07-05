package kz.zvezdochet.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

public class EventsHandler {
	@Execute
	public void execute() {
		System.out.println("Called");
	}
}
