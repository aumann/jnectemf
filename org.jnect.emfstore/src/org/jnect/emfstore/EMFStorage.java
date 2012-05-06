package org.jnect.emfstore;

import java.security.AccessControlException;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.client.model.Usersession;
import org.eclipse.emf.emfstore.client.model.Workspace;
import org.eclipse.emf.emfstore.client.model.WorkspaceManager;
import org.eclipse.emf.emfstore.client.model.util.EMFStoreClientUtil;
import org.eclipse.emf.emfstore.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.common.model.ModelElementId;
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
	private IdEObjectCollection collection;
	
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
		//TODO move?
		//initIds();
		
		PrimaryVersionSpec start = VersioningFactory.eINSTANCE.createPrimaryVersionSpec();
		start.setIdentifier(version);
		
		List<AbstractOperation> operations;
		try {
	        for (ChangePackage cp : projectSpace.getChanges(start, projectSpace.getBaseVersion())) {
	        	operations = cp.getLeafOperations();
	        	for (AbstractOperation o : operations) {
	        		replayElement(o);
	        	}
	        	// pause for a moment to see changes
	        	try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

				}
	        }
		} catch (EmfStoreException e) {
			e.printStackTrace();
		}
	}
	
	private void replayElement(AbstractOperation o) {
//		if (o instanceof AttributeOperation) {
//			AttributeOperation ao = (AttributeOperation) o;
//			ao.apply(collection);
//		}
		
		if (o instanceof AttributeOperation) {
			AttributeOperation ao = (AttributeOperation) o;
			ModelElementId id = ao.getModelElementId();
			EObject element = projectSpace.getProject().getModelElement(id);
			Float newValue = (Float) ao.getNewValue();
			String attribute = ao.getName(); // form: Set attribute-name attribute
			
			if (element instanceof Head) {
				setValue(attribute, replayBody.getHead(), newValue);
			} else if (element instanceof CenterShoulder) {
				setValue(attribute, replayBody.getCenterShoulder(), newValue);
			} else if (element instanceof LeftShoulder) {
				setValue(attribute, replayBody.getLeftShoulder(), newValue);
			} else if (element instanceof RightShoulder) {
				setValue(attribute, replayBody.getRightShoulder(), newValue);
			} else if (element instanceof LeftElbow) {
				setValue(attribute, replayBody.getLeftElbow(), newValue);
			} else if (element instanceof RightElbow) {
				setValue(attribute, replayBody.getRightElbow(), newValue);
			} else if (element instanceof LeftWrist) {
				setValue(attribute, replayBody.getLeftWrist(), newValue);
			} else if (element instanceof RightWrist) {
				setValue(attribute, replayBody.getRightWrist(), newValue);
			} else if (element instanceof LeftHand) {
				setValue(attribute, replayBody.getLeftHand(), newValue);
			} else if (element instanceof RightHand) {
				setValue(attribute, replayBody.getRightHand(), newValue);
			} else if (element instanceof Spine) {
				setValue(attribute, replayBody.getSpine(), newValue);
			} else if (element instanceof CenterHip) {
				setValue(attribute, replayBody.getCenterHip(), newValue);
			} else if (element instanceof LeftHip) {
				setValue(attribute, replayBody.getLeftHip(), newValue);
			} else if (element instanceof RightHip) {
				setValue(attribute, replayBody.getRightHip(), newValue);
			} else if (element instanceof LeftKnee) {
				setValue(attribute, replayBody.getLeftKnee(), newValue);
			} else if (element instanceof RightKnee) {
				setValue(attribute, replayBody.getRightKnee(), newValue);
			} else if (element instanceof LeftAnkle) {
				setValue(attribute, replayBody.getLeftAnkle(), newValue);
			} else if (element instanceof RightAnkle) {
				setValue(attribute, replayBody.getRightAnkle(), newValue);
			} else if (element instanceof LeftFoot) {
				setValue(attribute, replayBody.getLeftFoot(), newValue);
			} else if (element instanceof RightFoot) {
				setValue(attribute, replayBody.getRightFoot(), newValue);
			} 
		}
	}
	
	private void setValue(String attribute, PositionedElement element, Float value) {
		if (attribute.equals("Set x attribute")) {
			element.setX(value);
		} else if (attribute.equals("Set y attribute")) {
			element.setY(value);
		} else if (attribute.equals("Set z attribute")) {
			element.setZ(value);
		}
	}
	
	private void initIds() {
		collection = ModelFactory.eINSTANCE.createProject();
		collection.addModelElement(replayBody);
		
		Project project = projectSpace.getProject();
		
		for (EObject o : project.getAllModelElements()) {
			String elementId = project.getModelElementId(o).getId();
			if (o instanceof Head) {
				collection.getModelElementId(replayBody.getHead()).setId(elementId);
			} else if (o instanceof CenterShoulder) {
				collection.getModelElementId(replayBody.getCenterShoulder()).setId(elementId);
			} else if (o instanceof LeftShoulder) {
				collection.getModelElementId(replayBody.getLeftShoulder()).setId(elementId);
			} else if (o instanceof RightShoulder) {
				collection.getModelElementId(replayBody.getRightShoulder()).setId(elementId);
			} else if (o instanceof LeftElbow) {
				collection.getModelElementId(replayBody.getLeftElbow()).setId(elementId);
			} else if (o instanceof RightElbow) {
				collection.getModelElementId(replayBody.getRightElbow()).setId(elementId);
			} else if (o instanceof LeftWrist) {
				collection.getModelElementId(replayBody.getLeftWrist()).setId(elementId);
			} else if (o instanceof RightWrist) {
				collection.getModelElementId(replayBody.getRightWrist()).setId(elementId);
			} else if (o instanceof LeftHand) {
				collection.getModelElementId(replayBody.getLeftHand()).setId(elementId);
			} else if (o instanceof RightHand) {
				collection.getModelElementId(replayBody.getRightHand()).setId(elementId);
			} else if (o instanceof Spine) {
				collection.getModelElementId(replayBody.getSpine()).setId(elementId);
			} else if (o instanceof CenterHip) {
				collection.getModelElementId(replayBody.getCenterHip()).setId(elementId);
			} else if (o instanceof LeftHip) {
				collection.getModelElementId(replayBody.getLeftHip()).setId(elementId);
			} else if (o instanceof RightHip) {
				collection.getModelElementId(replayBody.getRightHip()).setId(elementId);
			} else if (o instanceof LeftKnee) {
				collection.getModelElementId(replayBody.getLeftKnee()).setId(elementId);
			} else if (o instanceof RightKnee) {
				collection.getModelElementId(replayBody.getRightKnee()).setId(elementId);
			} else if (o instanceof LeftAnkle) {
				collection.getModelElementId(replayBody.getLeftAnkle()).setId(elementId);
			} else if (o instanceof RightAnkle) {
				collection.getModelElementId(replayBody.getRightAnkle()).setId(elementId);
			} else if (o instanceof LeftFoot) {
					collection.getModelElementId(replayBody.getLeftFoot()).setId(elementId);
			} else if (o instanceof RightFoot) {
				collection.getModelElementId(replayBody.getRightFoot()).setId(elementId);
			} else {
				//do nothing
			}
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
