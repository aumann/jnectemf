package org.jnect.emfstore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
		final EMFStorage store = EMFStorage.getInstance();
		Job commitJob = new Job("Saving recorded data.") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				buffer.flushToBody(store.getRecordingBody(), store, monitor);
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		commitJob.setUser(true); // show dialog
		commitJob.schedule();
	}

}
