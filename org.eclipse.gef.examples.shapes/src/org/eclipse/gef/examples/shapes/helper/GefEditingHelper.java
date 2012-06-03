package org.eclipse.gef.examples.shapes.helper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.examples.shapes.model.CursorShape;
import org.eclipse.gef.examples.shapes.model.Shape;
import org.eclipse.gef.examples.shapes.model.ShapesDiagram;

public class GefEditingHelper {

	public static GefEditingHelper INSTANCE = new GefEditingHelper();

	private CursorShape cursor;
	private ShapesDiagram diagram;
	private Shape movingShape;
	private PropertyChangeListener propertyChangeListener;

	private GefEditingHelper() {
		this.cursor = null;
		this.diagram = null;
		this.movingShape = null;
		this.propertyChangeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String prop = evt.getPropertyName();
				if (Shape.LOCATION_PROP.equals(prop)) {
					if (movingShape != null) {
						movingShape.setLocation(cursor.getLocation());
					}
				}
			}
		};
	};

	public void setCursor(CursorShape shape) {
		this.cursor = shape;
	}

	public void setDiagram(ShapesDiagram diagram) {
		this.diagram = diagram;
	}

	public void switchGefEditingMode() {
		if (cursor == null) {
			return;
		}
		boolean isEditing = cursor.switchGefEditingMode();
		if (isEditing) {
			movingShape = findShapeToEdit();
			if (movingShape == null) {
				// no shape found, exit editing mode
				cursor.switchGefEditingMode();
			} else {
				// hook shape to cursor
				cursor.addPropertyChangeListener(propertyChangeListener);
			}
		} else {
			if (!(movingShape == null)) {
				// remove hook
				cursor.removePropertyChangeListener(propertyChangeListener);
				movingShape = null;
			}
		}
	}

	private Shape findShapeToEdit() {
		Shape foundShape = null;
		Point cursorLocation = cursor.getLocation();
		List<Shape> shapes = diagram.getChildren();
		for (Shape s : shapes) {
			if (!s.equals(cursor)) {
				Point location = s.getLocation();
				int xMin = location.x;
				int xMax = xMin + s.getSize().width;
				int yMin = location.y;
				int yMax = yMin + s.getSize().height;
				// check if cursor position is lying inside the bounds of the current shape
				if ((xMin <= cursorLocation.x && cursorLocation.x <= xMax)
					&& (yMin <= cursorLocation.y && cursorLocation.y <= yMax)) {
					foundShape = s;
					break;
				}
			}
		}
		return foundShape;
	}
}
