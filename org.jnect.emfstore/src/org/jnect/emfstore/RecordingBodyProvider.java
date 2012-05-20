package org.jnect.emfstore;

import org.jnect.bodymodel.Body;
import org.jnect.core.IBodyProvider;

public class RecordingBodyProvider implements IBodyProvider {
	BodyBuffer buffer;

	public RecordingBodyProvider() {
		buffer = new BodyBuffer();
	}

	@Override
	public Body getBody() {
		return buffer.getBufferBody();
	}

	@Override
	public void save() {
		EMFStorage store = EMFStorage.getInstance();
		buffer.flushToBody(store.getRecordingBody(), store);
	}

}
