/*******************************************************************************
 * Copyright (c) 2012 jnect.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugen Neufeld - initial API and implementation
 *******************************************************************************/
package org.jnect.core.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
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
import org.jnect.core.IBodyProvider;
import org.jnect.core.KinectManager;
import org.jnect.core.SpeechListener;
import org.jnect.core.impl.connection.jni.ProxyConnectionManager;
import org.w3c.dom.Document;


public class KinectManagerImpl implements KinectManager, KinectDataHandler {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private static final KinectManager INSTANCE = new KinectManagerImpl();
	
	public static KinectManager getInstance() {
		return INSTANCE;
	}
	
	private ConnectionManager connectionManager;
	
	private SkeletonParser skeletonParser;
	private Body body;
	private Map<SpeechListener, Set<String>> speechWords = new HashMap<SpeechListener, Set<String>>();
	private Map<String, Set<SpeechListener>> filteredSpeechListeners = new HashMap<String, Set<SpeechListener>>();
	private Set<SpeechListener> unfilteredSpeechListeners = new HashSet<SpeechListener>();

	public KinectManagerImpl() {
		// this.connectionManager = new SocketConnectionManager();
		this.connectionManager = new ProxyConnectionManager();
		this.connectionManager.setDataHandler(this);
		body = retrieveBody();
//		body=BodymodelFactory.eINSTANCE.createBody();
//		fillBody();
		this.skeletonParser = new SkeletonParser(body);
	}

	private Body retrieveBody() {
		IConfigurationElement[] bodyConfElements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.jnect.core.bodyprovider");
		Body body;
		if (bodyConfElements.length > 1) {
			throw new IllegalStateException("Only one extension for the body provider allowed currently!");
		} else if (bodyConfElements.length == 0) {
			body = createAndFillBody();
		} else {
			try {
				IBodyProvider bodyProvider = (IBodyProvider) bodyConfElements[0].createExecutableExtension("class");
				body = bodyProvider.getBody();
			} catch (CoreException e) {
				e.printStackTrace();
				body = createAndFillBody();
			}
		}
		
		return body;		
	}

	private Body createAndFillBody() {
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
		Body createdBody = factory.createBody();
		//add elements to body
		createdBody.setHead(head);
		createdBody.setLeftAnkle(ankleLeft);
		createdBody.setRightAnkle(ankleRight);
		createdBody.setLeftElbow(elbowLeft);
		createdBody.setRightElbow(elbowRight);
		createdBody.setLeftFoot(footLeft);
		createdBody.setRightFoot(footRight);
		createdBody.setLeftHand(handLeft);
		createdBody.setRightHand(handRight);
		createdBody.setCenterHip(hipCenter);
		createdBody.setLeftHip(hipLeft);
		createdBody.setRightHip(hipRight);
		createdBody.setLeftKnee(kneeLeft);
		createdBody.setRightKnee(kneeRight);
		createdBody.setCenterShoulder(shoulderCenter);
		createdBody.setLeftShoulder(shoulderLeft);
		createdBody.setRightShoulder(shoulderRight);
		createdBody.setSpine(spine);
		createdBody.setLeftWrist(wristLeft);
		createdBody.setRightWrist(wristRight);
		
		//create links
		createLink(head, shoulderCenter, createdBody);
		createLink(shoulderCenter, shoulderLeft, createdBody);
		createLink(shoulderCenter, shoulderRight, createdBody);
		createLink(shoulderLeft, elbowLeft, createdBody);
		createLink(shoulderRight, elbowRight, createdBody);
		createLink(elbowLeft, wristLeft, createdBody);
		createLink(elbowRight, wristRight, createdBody);
		createLink(wristLeft, handLeft, createdBody);
		createLink(wristRight, handRight, createdBody);
		createLink(shoulderCenter,spine, createdBody);
		createLink(spine, hipCenter, createdBody);
		createLink(hipCenter, hipLeft, createdBody);
		createLink(hipCenter, hipRight, createdBody);
		createLink(hipLeft, kneeLeft, createdBody);
		createLink(hipRight, kneeRight, createdBody);
		createLink(kneeLeft, ankleLeft, createdBody);
		createLink(kneeRight, ankleRight, createdBody);
		createLink(ankleLeft, footLeft, createdBody);
		createLink(ankleRight, footRight, createdBody);
		return createdBody;
	}
	
	private void createLink(PositionedElement source, PositionedElement target, Body body) {
		HumanLink link = BodymodelFactory.eINSTANCE.createHumanLink();
		link.setSource(source);
		link.setTarget(target);
		
		source.getOutgoingLinks().add(link);
		target.getIncomingLinks().add(link);
		
		body.getLinks().add(link);
	}
	
	@Override
	public void startKinect() {
		try {
			this.connectionManager.openConnection();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void stopKinect() {
		try {
			this.connectionManager.closeConnection();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Body getSkeletonModel() {
		return body;
	}

	@Override
	public void startSkeletonTracking() {
		this.skeletonParser.reset();
		this.connectionManager.startSkeletonTracking();
	}

	@Override
	public void stopSkeletonTracking() {
		this.connectionManager.stopSkeletonTracking();
	}

	@Override
	public void addSpeechListener(SpeechListener listener) {
		this.speechWords.put(listener, listener.getWords());
		if (listener.isFiltered()) {
			for (String word : listener.getWords()) {
				if (!this.filteredSpeechListeners.containsKey(word)) {
					this.filteredSpeechListeners.put(word, new HashSet<SpeechListener>());
				}
				this.filteredSpeechListeners.get(word).add(listener);
			}
		} else {
			this.unfilteredSpeechListeners.add(listener);
		}
	}

	@Override
	public void removeSpeechListener(SpeechListener listener) {
		this.speechWords.remove(listener);
		for (String word : listener.getWords()) {
			this.filteredSpeechListeners.get(word).remove(listener);
		}
		this.unfilteredSpeechListeners.remove(listener);
	}

	@Override
	public void startSpeechRecognition() {
		Set<String> words = new HashSet<String>();
		for (Set<String> listenerWords : this.speechWords.values()) {
			words.addAll(listenerWords);
		}

		String[] keywords = words.toArray(new String[words.size()]);
		this.connectionManager.startSpeechRecognition(keywords);
	}

	@Override
	public void stopSpeechRecognition() {
		this.connectionManager.stopSpeechRecognition();
	}

	@Override
	public void handleSkeletonData(Document doc) {
		this.skeletonParser.parseSkeleton(doc);
//		emfStorage.updateBody();
	}

	@Override
	public void handleSpeechData(String word) {
		Set<SpeechListener> listeners = new HashSet<SpeechListener>();
		
		// Add all listeners that want to get notified on every recognized word
		listeners.addAll(this.unfilteredSpeechListeners);
		
		// Add all listeners that only want to be notified on specific words
		if (this.filteredSpeechListeners.containsKey(word)) {
			listeners.addAll(this.filteredSpeechListeners.get(word));
		}
		
		// Notify speech listeners
		for (SpeechListener listener : listeners) {
			listener.notifySpeech(word);
		}
	}

	@Override
	public boolean isStarted() {
		return this.connectionManager.isConnected();
	}

	@Override
	public boolean isSkeletonTrackingStarted() {
		return this.connectionManager.isSkeletonTrackingStarted();
	}

	@Override
	public boolean isSpeechRecognitionStarted() {
		return this.connectionManager.isSpeechRecognitionStarted();
	}
}
