package org.jnect.demo.gef.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.jnect.demo.gef.HumanDiagramGraphicalEditor;
import org.jnect.emfstore.EMFStorage;

public class StartReplayHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EMFStorage store = EMFStorage.getInstance();
		((HumanDiagramGraphicalEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).setContent(store.getReplayingBody());
		store.replay(); 
		return null;
	}

}
