package org.jnect.emfstore.replay.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.jnect.demo.gef.HumanDiagramGraphicalEditor;
import org.jnect.emfstore.replay.Replay;

public class StartReplayHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Replay replay = Replay.getInstance();
		((HumanDiagramGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.getActiveEditor()).setContent(replay.getReplayBody());
		replay.setupBody();
		replay.displaySlider();
		return null;
	}

}
