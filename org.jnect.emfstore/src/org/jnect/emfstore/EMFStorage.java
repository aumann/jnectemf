package org.jnect.emfstore;

import java.security.AccessControlException;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.client.model.Usersession;
import org.eclipse.emf.emfstore.client.model.Workspace;
import org.eclipse.emf.emfstore.client.model.WorkspaceManager;
import org.eclipse.emf.emfstore.client.model.util.EMFStoreClientUtil;
import org.eclipse.emf.emfstore.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.common.model.ModelFactory;
import org.eclipse.emf.emfstore.common.model.Project;
import org.eclipse.emf.emfstore.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.server.exceptions.EmfStoreException;
import org.eclipse.emf.emfstore.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.model.versioning.operations.AttributeOperation;
import org.jnect.bodymodel.Body;
import org.jnect.bodymodel.BodymodelFactory;
import org.jnect.bodymodel.CenterHip;
import org.jnect.bodymodel.CenterShoulder;
import org.jnect.bodymodel.Head;
import org.jnect.bodymodel.HumanLink;
import org.jnect.bodymodel.LeftAnkle;
import org.jnect.bodymodel.LeftElbow;
import org.jnect.bodymodel.LeftFoot;
import org.jnect.bodymodel.LeftHand;
import org.jnect.bodymodel.LeftHip;
import org.jnect.bodymodel.LeftKnee;
import org.jnect.bodymodel.LeftShoulder;
import org.jnect.bodymodel.LeftWrist;
import org.jnect.bodymodel.PositionedElement;
import org.jnect.bodymodel.RightAnkle;
import org.jnect.bodymodel.RightElbow;
import org.jnect.bodymodel.RightFoot;
import org.jnect.bodymodel.RightHand;
import org.jnect.bodymodel.RightHip;
import org.jnect.bodymodel.RightKnee;
import org.jnect.bodymodel.RightShoulder;
import org.jnect.bodymodel.RightWrist;
import org.jnect.bodymodel.Spine;

public class EMFStorage{
	private Body replayBody;
	ProjectSpace projectSpace;
	Usersession usersession;
	private boolean currentlyReplaying;
	
	private static EMFStorage INSTANCE;
	
	public EMFStorage(Body body) {
		if (INSTANCE == null) {
			INSTANCE = this;
			connectToEMFStoreAndInit(body);
		}
	}
	
	public static EMFStorage getInstance() {
		return INSTANCE;
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
		if (currentlyReplaying)
			return;
        // commit the pending changes of the project to the EMF Store
        try {
			projectSpace.commit(createLogMessage(usersession.getUsername(), "commit new state"), null, new NullProgressMonitor());
        } catch (EmfStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Body getReplayingBody() {
		if (replayBody == null)
			fillBody();
		return replayBody;
	}
	
	public void replay() {
		// dummyReplay();
		replay(0);
	}
	
	private void dummyReplay() {
		replayBody.getCenterHip().setX(replayBody.getCenterHip().getX() + 1.1f);
		replayBody.getLeftKnee().setY(replayBody.getLeftKnee().getY() + 0.1f);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		replayBody.getLeftKnee().setY(replayBody.getLeftKnee().getY() - 0.1f * replayBody.getLeftKnee().getY());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		replayBody.getLeftKnee().setY(replayBody.getLeftKnee().getY() + 0.1f * replayBody.getLeftKnee().getY());
		replayBody.getLeftKnee().setY(replayBody.getLeftKnee().getY() + 0.1f * replayBody.getLeftKnee().getY());
	}

	/**
	 * Replays the body model from emfstore
	 * 
	 * @param initCommit 
	 * @throws EmfStoreException
	 */
	public void replay(int version) {
		currentlyReplaying = false;
		PrimaryVersionSpec start = VersioningFactory.eINSTANCE.createPrimaryVersionSpec();
		start.setIdentifier(version);
		IdEObjectCollection collection = ModelFactory.eINSTANCE.createProject();
		collection.addModelElement(replayBody);
		
		List<AbstractOperation> operations;
		try {
	        for (ChangePackage cp : projectSpace.getChanges(start, projectSpace.getBaseVersion())) {
	        	cp.getOperations();
	        	operations = cp.getLeafOperations();
	        	for (AbstractOperation op: operations) {
	        		op.apply(collection);
	        	}
	        	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//	        	for (AbstractOperation o : operations) {
//	        		replayElement(o, collection);
//	        	}
//	        	// pause fo a moment to see changes
//	        	try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//
//				}
	        }
		} catch (EmfStoreException e) {
			e.printStackTrace();
		}
		currentlyReplaying = false;
	}
	
	private void replayElement(AbstractOperation o, IdEObjectCollection collection) {
		if (o instanceof AttributeOperation) {
			AttributeOperation ao = (AttributeOperation) o;

			ao.eResource();
			ao.apply(collection);
		}
	}
	
	private void fillBody() {
		replayBody = BodymodelFactory.eINSTANCE.createBody();
		BodymodelFactory factory=BodymodelFactory.eINSTANCE;
		//create Elements
		Head head=factory.createHead();
		CenterShoulder shoulderCenter = factory.createCenterShoulder();
		LeftShoulder shoulderLeft = factory.createLeftShoulder();
		RightShoulder shoulderRight = factory.createRightShoulder();
		LeftElbow elbowLeft = factory.createLeftElbow();
		RightElbow elbowRight = factory.createRightElbow();
		LeftWrist wristLeft = factory.createLeftWrist();
		RightWrist wristRight = factory.createRightWrist();
		LeftHand handLeft = factory.createLeftHand();
		RightHand handRight = factory.createRightHand();
		Spine spine =factory.createSpine();
		CenterHip hipCenter = factory.createCenterHip();
		LeftHip hipLeft = factory.createLeftHip();
		RightHip hipRight = factory.createRightHip();
		LeftKnee kneeLeft = factory.createLeftKnee();
		RightKnee kneeRight = factory.createRightKnee();
		LeftAnkle ankleLeft = factory.createLeftAnkle();
		RightAnkle ankleRight = factory.createRightAnkle();
		LeftFoot footLeft = factory.createLeftFoot();
		RightFoot footRight = factory.createRightFoot();
		
		//set color
		footLeft.setColor_g(255);
		footRight.setColor_g(255);
		handLeft.setColor_r(255);
		handLeft.setColor_g(0);
		handLeft.setColor_b(0);
		handRight.setColor_r(255);
		head.setColor_b(255);
		
		//add elements to body
		replayBody.setHead(head);
		replayBody.setLeftAnkle(ankleLeft);
		replayBody.setRightAnkle(ankleRight);
		replayBody.setLeftElbow(elbowLeft);
		replayBody.setRightElbow(elbowRight);
		replayBody.setLeftFoot(footLeft);
		replayBody.setRightFoot(footRight);
		replayBody.setLeftHand(handLeft);
		replayBody.setRightHand(handRight);
		replayBody.setCenterHip(hipCenter);
		replayBody.setLeftHip(hipLeft);
		replayBody.setRightHip(hipRight);
		replayBody.setLeftKnee(kneeLeft);
		replayBody.setRightKnee(kneeRight);
		replayBody.setCenterShoulder(shoulderCenter);
		replayBody.setLeftShoulder(shoulderLeft);
		replayBody.setRightShoulder(shoulderRight);
		replayBody.setSpine(spine);
		replayBody.setLeftWrist(wristLeft);
		replayBody.setRightWrist(wristRight);
		
		//create links
		createLink(head, shoulderCenter);
		createLink(shoulderCenter, shoulderLeft);
		createLink(shoulderCenter, shoulderRight);
		createLink(shoulderLeft, elbowLeft);
		createLink(shoulderRight, elbowRight);
		createLink(elbowLeft, wristLeft);
		createLink(elbowRight, wristRight);
		createLink(wristLeft, handLeft);
		createLink(wristRight, handRight);
		createLink(shoulderCenter,spine);
		createLink(spine, hipCenter);
		createLink(hipCenter, hipLeft);
		createLink(hipCenter, hipRight);
		createLink(hipLeft, kneeLeft);
		createLink(hipRight, kneeRight);
		createLink(kneeLeft, ankleLeft);
		createLink(kneeRight, ankleRight);
		createLink(ankleLeft, footLeft);
		createLink(ankleRight, footRight);
	}
	
	private void createLink(PositionedElement source, PositionedElement target) {
		HumanLink link = BodymodelFactory.eINSTANCE.createHumanLink();
		link.setSource(source);
		link.setTarget(target);
		
		source.getOutgoingLinks().add(link);
		target.getIncomingLinks().add(link);
		
		replayBody.getLinks().add(link);
	}
	

}
