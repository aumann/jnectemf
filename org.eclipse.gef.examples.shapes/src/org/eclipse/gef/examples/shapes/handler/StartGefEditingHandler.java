package org.eclipse.gef.examples.shapes.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.examples.shapes.ShapesEditor;
import org.eclipse.gef.examples.shapes.model.CursorShape;
import org.eclipse.gef.examples.shapes.model.ShapesDiagram;
import org.eclipse.gef.examples.shapes.model.commands.ShapeCreateCommand;
import org.eclipse.ui.PlatformUI;
import org.jnect.core.KinectManager;

public class StartGefEditingHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		KinectManager.INSTANCE.startSkeletonTracking();

		// create cursor and display it
		Point point = new Point(5, 5);
		Dimension dimension = new Dimension(10, 10);
		CursorShape cursor = new CursorShape(KinectManager.INSTANCE.getSkeletonModel().getLeftHand());
		ShapesEditor editor = (ShapesEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.getActiveEditor();
		ShapesDiagram diagram = editor.getModel();
		new ShapeCreateCommand(cursor, diagram, new Rectangle(point, dimension)).execute();

		return null;
	}
}
