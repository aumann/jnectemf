package org.jnect.emfstore;

import java.security.AccessControlException;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.client.model.Usersession;
import org.eclipse.emf.emfstore.client.model.Workspace;
import org.eclipse.emf.emfstore.client.model.WorkspaceManager;
import org.eclipse.emf.emfstore.client.model.util.EMFStoreClientUtil;
import org.eclipse.emf.emfstore.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.common.model.Project;
import org.eclipse.emf.emfstore.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.server.exceptions.EmfStoreException;
import org.eclipse.emf.emfstore.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.server.model.versioning.operations.AbstractOperation;
import org.jnect.bodymodel.Body;

public class EMFStorage{
	private Body body;
	ProjectSpace projectSpace;
	Usersession usersession;
	public EMFStorage(Body body) {
		this.body = body;
		connectToEMFStoreAndInit(body);
		
	}
	
	private void connectToEMFStoreAndInit(final Body body) {
		new EMFStoreCommand() {
            @Override
            protected void doRun() {
                try {
                    // create a default Usersession for the purpose of this tutorial and login
                    // see the corrsponding Javadoc for EMFStoreClientUtil.createUsersession(...) to setup the authentication for your custom client
                    usersession = EMFStoreClientUtil.createUsersession();
                    usersession.logIn();

                    // fetch the list of projects
                    Workspace currentWorkspace = WorkspaceManager.getInstance()
                            .getCurrentWorkspace();
                    List<ProjectInfo> projectList = currentWorkspace.getRemoteProjectList(usersession);

                    // retrieve the first Project from the List
                    ProjectInfo projectInfo = projectList.iterator().next();

                    // checkout the ProjectSpace, containing all Models of the Project, into the local Workspace
                    projectSpace = currentWorkspace.checkout(usersession, projectInfo);

                    // create and add a new "Book" from the example model
                    // change this part to create instances of your own model
                    Project project = projectSpace.getProject();
                    project.addModelElement(body);
                    projectSpace.commit(createLogMessage(usersession.getUsername(), "commit initial body"), null, new NullProgressMonitor());

                    
                    System.out.println("Client run completed.");
                } catch (AccessControlException e) {
                    ModelUtil.logException(e);
                } catch (EmfStoreException e) {
                    ModelUtil.logException(e);
                }
            }
        }.run(false);
 
	}
	
	private LogMessage createLogMessage(String name, String message) {
		LogMessage logMessage = VersioningFactory.eINSTANCE.createLogMessage();
		logMessage.setAuthor(name);
		logMessage.setDate(Calendar.getInstance().getTime());
		logMessage.setClientDate(Calendar.getInstance().getTime());
		logMessage.setMessage(message);
		return logMessage;
	}
		

	public void updateBody() {
        // commit the pending changes of the project to the EMF Store
        try {
			projectSpace.commit(createLogMessage(usersession.getUsername(), "commit new state"), null, new NullProgressMonitor());
        } catch (EmfStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void replay(PrimaryVersionSpec initCommit) throws EmfStoreException {
        for (ChangePackage p : projectSpace.getChanges(initCommit, projectSpace.getBaseVersion())) {
        	p.cannonize();
        	EList<AbstractOperation> operations = p.getOperations();
        	
        }
	}
	
	

}