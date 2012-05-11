package org.jnect.emfstore;

import java.util.NoSuchElementException;

import org.jnect.bodymodel.Body;
import org.jnect.replay.IReplayBodyProvider;

public class ReplayBodyProvider implements IReplayBodyProvider {

	@Override
	public Body getReplayBody() {
		EMFStorage store = EMFStorage.getInstance();
		return store.getReplayingBody();
	}

	@Override
	public void replay(int from) {
		EMFStorage.getInstance().replay(from);

	}

	@Override
	public void setReplayToState(int state) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub

	}

	@Override
	public void hasNext() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stepForward() throws NoSuchElementException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stepBackward() throws NoSuchElementException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getReplayStatesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
