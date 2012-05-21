package org.jnect.emfstore;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.jnect.bodymodel.Body;
import org.jnect.bodymodel.PositionedElement;

public class BodyBuffer {
	Body body;
	final int NEEDED_CHANGES;
	List<float[]> buffer = Collections.synchronizedList(new LinkedList<float[]>());

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

	public void flushToBody(Body flushBody, ICommitter committer, int commitResolution, IProgressMonitor monitor) {
		final int BODY_PART_COUNT = NEEDED_CHANGES / 3;
		assert flushBody.eContents().size() == BODY_PART_COUNT;
		monitor.beginTask("Saving to EMFStore", buffer.size());
		EList<EObject> bodyContents = flushBody.eContents();

		for (EObject elem : bodyContents) {
			elem.eSetDeliver(false);
		}

		synchronized (buffer) {
			Iterator<float[]> bufferIt = buffer.iterator();
			int collectedBodyChanges = 0;
			while (bufferIt.hasNext() && !monitor.isCanceled()) {
				float[] values = bufferIt.next();
				// flushBody.eSetDeliver(false);
				for (int i = 0; i < BODY_PART_COUNT/* - 1 */; i++) {
					EObject elem = bodyContents.get(i);
					if (!(elem instanceof PositionedElement))
						continue;
					PositionedElement pos = (PositionedElement) elem;
					pos.setX(values[i * 3]);
					pos.setY(values[i * 3 + 1]);
					pos.setZ(values[i * 3 + 2]);
				}
				collectedBodyChanges++;
				if (collectedBodyChanges == commitResolution) {
					committer.commit();
					collectedBodyChanges = 0;
				}
				monitor.worked(1);
			}
			if (collectedBodyChanges != 0) {
				committer.commit();
			}
			buffer.clear();
		}
	}
}
