package org.eclipse.gef.examples.shapes.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.examples.shapes.ShapesEditor;
import org.eclipse.gef.examples.shapes.helper.GefEditingHelper;
import org.eclipse.gef.examples.shapes.listener.GefEditingGestureListener;
import org.eclipse.gef.examples.shapes.model.CursorShape;
import org.eclipse.gef.examples.shapes.model.ShapesDiagram;
import org.eclipse.gef.examples.shapes.model.commands.ShapeCreateCommand;
import org.eclipse.ui.PlatformUI;
import org.jnect.core.KinectManager;
import org.jnect.gesture.GestureProxy;
import org.jnect.gesture.detectors.RightHandAboveHeadGestureDetector;

public class StartGefEditingHandler extends AbstractHandler {

	private static boolean addedGestures = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!addedGestures) {
			GestureProxy.INSTANCE.addGestureListener(new GefEditingGestureListener());
			GestureProxy.INSTANCE.addGestureDetector(new RightHandAboveHeadGestureDetector());
			addedGestures = true;
		}
		KinectManager.INSTANCE.startSkeletonTracking();

		// create cursor and display it
		CursorShape cursor = new CursorShape(KinectManager.INSTANCE.getSkeletonModel().getLeftHand());
		ShapesEditor editor = (ShapesEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.getActiveEditor();
		ShapesDiagram diagram = editor.getModel();
		new ShapeCreateCommand(cursor, diagram, new Rectangle()).execute();

		// add handle to helper
		GefEditingHelper.INSTANCE.setCursor(cursor);

		return null;
	}
}
