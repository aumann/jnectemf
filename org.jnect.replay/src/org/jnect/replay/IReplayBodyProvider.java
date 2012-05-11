package org.jnect.replay;

import java.util.NoSuchElementException;

import org.jnect.bodymodel.Body;


public interface IReplayBodyProvider {
	public Body getReplayBody();
	
	public void replay(int from);
	
	public void setReplayToState(int state) throws IndexOutOfBoundsException;
	
	public void hasNext();
	
	public void stepForward() throws NoSuchElementException;
	
	public void stepBackward()  throws NoSuchElementException;
	
	/**
	 * @return The number of distinct body states this replay provider offers. 
	 */
	public int getReplayStatesCount();

}
