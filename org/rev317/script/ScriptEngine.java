package org.rev317.script;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import org.parabot.core.Context;
import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.scripts.Script;
import org.rev317.api.events.MessageEvent;
import org.rev317.api.events.listeners.MessageListener;

public class ScriptEngine {
	private static HashMap<Context, ScriptEngine> instances = new HashMap<Context, ScriptEngine>();
	private ArrayList<MouseListener> mouseListeners = new ArrayList<MouseListener>();
	private ArrayList<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
	private ArrayList<MessageListener> messageListeners = new ArrayList<MessageListener>();
	
	private Script script = null;
	
	private ScriptEngine() {
		instances.put(Context.resolve(), this);
	}
	
	public static ScriptEngine getInstance() {
		final ScriptEngine engine = instances.get(Context.resolve());
		if(engine != null) {
			return engine;
		}
		return new ScriptEngine();
	}
	
	public void addMouseListener(MouseListener mouseListener) {
		mouseListeners.add(mouseListener);
	}
	
	public void removeMouseListener(MouseListener mouseListener) {
		mouseListeners.remove(mouseListener);
	}
	
	public void clearMouseListeners() {
		mouseListeners.clear();
	}
	
	public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
		mouseMotionListeners.add(mouseMotionListener);
	}
	
	public void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
		mouseMotionListeners.remove(mouseMotionListener);
	}
	
	public void clearMouseMotionListeners() {
		mouseMotionListeners.clear();
	}
	
	public void addMessageListener(MessageListener messageListener) {
		messageListeners.add(messageListener);
	}
	
	public void removeMessageListener(MessageListener messageListener) {
		messageListeners.remove(messageListener);
	}
	
	public void clearMessageListeners() {
		messageListeners.clear();
	}
	
	public void setScript(final Script script) {
		this.script = script;
	}
	
	public void unload() {
		clearMouseListeners();
		clearMouseMotionListeners();
		clearMessageListeners();
		if(script instanceof Paintable) {
			Context.resolve().removePaintable((Paintable)script);
		}
		this.script = null;
	}
	
	public void init() { 
		if(script == null) {
			throw new RuntimeException("Script is null");
		}
		if(script instanceof MouseListener) {
			addMouseListener((MouseListener)script);
		}
		if(script instanceof MouseMotionListener) {
			addMouseMotionListener((MouseMotionListener)script);
		}
		if(script instanceof MessageListener) {
			addMessageListener((MessageListener)script);
		}
		if(script instanceof Paintable) {
			Context.resolve().addPaintable((Paintable)script); 
		}
	}
	
	public void dispatch(AWTEvent event) {
		if(this.script == null) {
			return;
		}
		if(!(event instanceof MouseEvent)) {
			return;
		}
		final MouseEvent e = (MouseEvent) event;
		for(final MouseListener m : mouseListeners) {
			switch(e.getID()) {
			case MouseEvent.MOUSE_CLICKED:
				m.mouseClicked(e);
				break;
			case MouseEvent.MOUSE_ENTERED:
				m.mouseEntered(e);
				break;
			case MouseEvent.MOUSE_EXITED:
				m.mouseExited(e);
				break;
			case MouseEvent.MOUSE_PRESSED:
				m.mousePressed(e);
				break;
			case MouseEvent.MOUSE_RELEASED:
				m.mouseReleased(e);
			}
		}
		for(final MouseMotionListener m : mouseMotionListeners) {
			switch(e.getID()) {
			case MouseEvent.MOUSE_MOVED:
				m.mouseMoved(e);
				break;
			case MouseEvent.MOUSE_DRAGGED:
				m.mouseDragged(e);
				break;
			}
		}
	}
	
	public void dispatch(MessageEvent event) {
		for(final MessageListener messageListener : messageListeners) {
			messageListener.messageReceived(event);
		}
	}

}
