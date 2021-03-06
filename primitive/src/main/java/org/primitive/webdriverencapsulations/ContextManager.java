package org.primitive.webdriverencapsulations;


import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchContextException;
import org.openqa.selenium.TimeoutException;
import org.primitive.configuration.commonhelpers.ContextTimeOuts;
import org.primitive.logging.Log;
import org.primitive.webdriverencapsulations.components.bydefault.AlertHandler;
import org.primitive.webdriverencapsulations.components.bydefault.ComponentFactory;
import org.primitive.webdriverencapsulations.components.bydefault.ContextTool;
import org.primitive.webdriverencapsulations.components.overriden.FluentContextConditions;
import org.primitive.webdriverencapsulations.interfaces.IHasActivity;

public final class ContextManager extends Manager {
	private final FluentContextConditions fluent;
	private final ContextTool contextTool;

	public ContextManager(WebDriverEncapsulation initialDriverEncapsulation) {
		super(initialDriverEncapsulation);
		fluent       = new FluentContextConditions(getWrappedDriver());
		contextTool  = ComponentFactory.getComponent(ContextTool.class, getWrappedDriver());
	}

	private ContextTimeOuts getContextTimeOuts() {
		return driverEncapsulation.configuration.getContextTimeOuts();
	}
	
	@Override
	void changeActive(String context) throws NoSuchContextException {			
		contextTool.context(context);
	}

	@Override
	/**
	 * returns context by it's index
	 */
	String getHandleByIndex(int index) throws NoSuchContextException {
		try {
			Log.debug("Attempt to get context that is specified by index "
					+ Integer.toString(index) + "...");
			ContextTimeOuts timeOuts = getContextTimeOuts();
			long timeOut = getTimeOut(timeOuts.getContextCountTimeOutSec(),
					defaultTime);
			return awaiting.awaitCondition(timeOut, 100, 
					fluent.suchContextWithIndexIsPresent(index));
		} catch (TimeoutException e) {
			throw new NoSuchContextException("Can't find context! Index out of bounds! Specified index is "
							+ Integer.toString(index)
							+ " is more then actual context count", e);
		}
	}

	@Override
	public Set<String> getHandles() {
		return contextTool.getContextHandles();
	}

	@Override
	/**
	 * returns a new context that we have been waiting for time that
	 * specified in configuration
	 */
	String switchToNew() throws NoSuchContextException {
		ContextTimeOuts timeOuts = getContextTimeOuts();
		long timeOut = getTimeOut(
				timeOuts.getNewContextTimeOutSec(),
				defaultTimeForNew);
		return switchToNew(timeOut);
	}

	@Override
	/**
	 * returns a new context that we have been waiting for specified
	 * time
	 */
	String switchToNew(long timeOutInSeconds) throws NoSuchContextException {
		try {
			Log.debug("Waiting a new context for "
					+ Long.toString(timeOutInSeconds) + " seconds.");
			String context = awaiting.awaitCondition(timeOutInSeconds, 100,
					fluent.newContextIsAppeared());
			changeActive(context);
			return context;
		} catch (TimeoutException e) {
			throw new NoSuchContextException(
					"There is no new context! We have been waiting for "
							+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}

	@Override
	/**
	 * returns a new context that we have been waiting for specified
	 * time. new context is predefined.
	 */
	String switchToNew(long timeOutInSeconds, String context)
			throws NoSuchContextException {
		try {
			Log.debug("Waiting a new context '" + context + "' for "
					+ Long.toString(timeOutInSeconds));
			awaiting.awaitCondition(timeOutInSeconds, 100,
					fluent.isContextPresent(context));
			changeActive(context);
			return context;
		} catch (TimeoutException e) {
			throw new NoSuchContextException("There is no new context '"
					+ context + "'! We have been waiting for "
					+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}

	/**
	 * returns a new context that we have been waiting for specified
	 * time. new context is predefined. Time out is specified in configuration
	 */
	@Override
	String switchToNew(String context) {
		ContextTimeOuts timeOuts = getContextTimeOuts();
		long timeOut = getTimeOut(
				timeOuts.getNewContextTimeOutSec(),
				defaultTimeForNew);
		return switchToNew(timeOut, context);
	}

	synchronized String getActivityByHandle(String handle)
			throws NoSuchContextException {
		changeActive(handle);
		return (((IHasActivity) getWrappedDriver()).currentActivity());
	}

	/**
	 * returns context handle by it's index
	 */
	@Override
	public synchronized Handle getByIndex(int index) {
		String handle = this.getHandleByIndex(index);
		SingleContext initedContext = (SingleContext) SingleContext.isInitiated(handle, this);
		if (initedContext != null) {
			return (initedContext);
		}
		return (new SingleContext(handle, this));
	}

	/**
	 * returns context handle by it's name
	 */
	public synchronized Handle getByContextName(String contextName) {
		ContextTimeOuts timeOuts = getContextTimeOuts();
		long timeOut = getTimeOut(timeOuts.getIsContextPresentTimeOut(),
				defaultTime);	
		awaiting.awaitCondition(timeOut, fluent.isContextPresent(contextName));
		contextTool.context(contextName);
		SingleContext initedContext = (SingleContext) SingleContext.isInitiated(contextName, this);
		if (initedContext != null) {
			return (initedContext);
		}
		return (new SingleContext(contextName, this));
	}
	
	/**
	 * returns handle of a new context that we have been waiting for time that
	 * specified in configuration
	 */
	@Override
	public synchronized Handle getNewHandle() {
		return new SingleContext(switchToNew(), this);
	}

	/**
	 * returns handle of a new context that we have been waiting for specified
	 * time
	 */
	@Override
	public synchronized Handle getNewHandle(long timeOutInSeconds) {
		return new SingleContext(switchToNew(timeOutInSeconds), this);
	}

	/**
	 * returns handle of a new context that we have been waiting for specified
	 * time using context name
	 */
	@Override
	public synchronized Handle getNewHandle(long timeOutInSeconds, String contextName) {
		return new SingleContext(switchToNew(timeOutInSeconds, contextName), this);
	}

	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration using context name
	 */
	@Override
	public synchronized Handle getNewHandle(String contextName) {
		return new SingleContext(switchToNew(contextName), this);
	}

	@Override
	public Alert getAlert() throws NoAlertPresentException {
		ContextTimeOuts timeOuts = getContextTimeOuts();
		return ComponentFactory.getComponent(AlertHandler.class,
				getWrappedDriver(), new Class[] {long.class}, new Object[] {getTimeOut(timeOuts.
						getSecsForAwaitinAlertPresent(),
								defaultTime) });
	}
	

}
