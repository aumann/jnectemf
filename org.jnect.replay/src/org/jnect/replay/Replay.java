package org.jnect.replay;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.jnect.bodymodel.Body;

public class Replay {
	
	private static Replay INSTANCE;
	
	
	IReplayBodyProvider replayProvider;
	
	private Replay() {
		setupReplayProvider();
	}
	
	public static Replay getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Replay();
		return INSTANCE;
	}
	
	public void replay() {
		replayProvider.replay(0);
	}
	
	public Body getReplayBody() {
		return replayProvider.getReplayBody();
	}

	private void setupReplayProvider() {
		IConfigurationElement[] replayBodyProviders = Platform.getExtensionRegistry().getConfigurationElementsFor("org.jnect.replay.replaybodyprovider");
		if (replayBodyProviders.length != 1)
			throw new IllegalStateException("In order to replay, there must be at least (at the moment exactly) one storage plugin providing a replay body!");
	
		try {
			replayProvider = (IReplayBodyProvider) replayBodyProviders[0].createExecutableExtension("class");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
