package kz.zvezdochet.test.draw;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class LineStyles { public static void main(String[] args) { 
    final Display display = new Display(); 
    final Shell shell = new Shell(display); 
    shell.addListener(SWT.Paint, new Listener() { 
        public void handleEvent(Event event) { 
            GC gc = event.gc; 
            gc.setLineWidth(10); 
            gc.setLineStyle(SWT.LINE_SOLID); 
            gc.drawLine(10, 10, 200, 10); 
            gc.setLineStyle(SWT.LINE_DASH); 
            gc.drawLine(10, 30, 200, 30); 
            gc.setLineStyle(SWT.LINE_DOT); 
            gc.drawLine(10, 50, 200, 50); 
            gc.setLineStyle(SWT.LINE_DASHDOT); 
            gc.drawLine(10, 70, 200, 70); 
            gc.setLineStyle(SWT.LINE_DASHDOTDOT); 
            gc.drawLine(10, 90, 200, 90); 
        }}); 
    shell.setText("Line Styles"); 
    shell.setSize(250, 150); 
    shell.open(); 
    while (!shell.isDisposed()) { 
        if (!display.readAndDispatch()) 
            display.sleep(); 
    } 
    display.dispose(); 
}} 
