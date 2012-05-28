package org.jnect.emfstore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.jnect.bodymodel.Body;
import org.jnect.bodymodel.PositionedElement;

public class BodyBuffer {
	Body body;
	final int NEEDED_CHANGES;
	List<float[]> buffer = Collections.synchronizedList(new ArrayList<float[]>());

	public BodyBuffer() {
		this.body = EMFStorage.createAndFillBody();
		body.eAdapters().add(new CommitBodyChangesAdapter());
		// 3 changes (x, y, z) in every body element
		NEEDED_CHANGES = body.eContents().size() * 3;
	}

	public Body getBufferBody() {
		return body;
	}

	private class CommitBodyChangesAdapter extends EContentAdapter {
		private int currChanges = 0;

		@Override
		public void notifyChanged(Notification notification) {
			if (++currChanges % NEEDED_CHANGES == 0) {
				nextBody();
			}

		}
	}

	void nextBody() {
		float[] state = new float[NEEDED_CHANGES];
		assert NEEDED_CHANGES == body.eContents().size();
		for (int i = 0; i < NEEDED_CHANGES / 3; i++) {
			EObject elem = body.eContents().get(i);
			if (!(elem instanceof PositionedElement))
				continue;
			PositionedElement pos = (PositionedElement) elem;
			state[i * 3] = pos.getX();
			state[i * 3 + 1] = pos.getY();
			state[i * 3 + 2] = pos.getZ();
		}
		buffer.add(state);
	}

	public void flushToBody(Body flushBody, ICommitter committer, IProgressMonitor monitor) {
		assert flushBody.eContents().size() == NEEDED_CHANGES / 3;
		monitor.beginTask("Saving to EMFStore", buffer.size());
		synchronized (buffer) {
			Iterator<float[]> bufferIt = buffer.iterator();
			while (bufferIt.hasNext() && !monitor.isCanceled()) {
				float[] values = bufferIt.next();
				for (int i = 0; i < NEEDED_CHANGES / 3; i++) {
					EObject elem = flushBody.eContents().get(i);
					if (!(elem instanceof PositionedElement))
						continue;
					PositionedElement pos = (PositionedElement) elem;
					pos.setX(values[i * 3]);
					pos.setY(values[i * 3 + 1]);
					pos.setZ(values[i * 3 + 2]);
				}
				committer.commit();
				monitor.worked(1);
			}
			buffer.clear();
		}
	}
}
