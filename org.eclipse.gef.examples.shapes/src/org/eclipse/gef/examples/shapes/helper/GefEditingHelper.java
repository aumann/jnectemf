package org.eclipse.gef.examples.shapes.helper;

import org.eclipse.gef.examples.shapes.model.CursorShape;

public class GefEditingHelper {

	public static GefEditingHelper INSTANCE = new GefEditingHelper();

	private CursorShape cursor;

	private GefEditingHelper() {
		this.cursor = null;
	};

	public void setCursor(CursorShape shape) {
		cursor = shape;
	}

	public void switchGefEditingMode() {
		if (cursor == null) {
			return;
		}
		cursor.switchGefEditingMode();
	}
}
