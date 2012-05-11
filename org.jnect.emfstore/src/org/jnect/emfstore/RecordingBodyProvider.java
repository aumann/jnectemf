package org.jnect.emfstore;

import org.jnect.bodymodel.Body;
import org.jnect.core.IBodyProvider;

public class RecordingBodyProvider implements IBodyProvider {

	@Override
	public Body getBody() {
		EMFStorage store = EMFStorage.getInstance();
		return store.getRecordingBody();
	}

}
