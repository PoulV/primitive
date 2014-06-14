package org.primitive.webdriverencapsulations;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.primitive.configuration.commonhelpers.WindowsTimeOuts;
import org.primitive.logging.Log;
import org.primitive.webdriverencapsulations.components.bydefault.AlertHandler;
import org.primitive.webdriverencapsulations.components.bydefault.ComponentFactory;
import org.primitive.webdriverencapsulations.components.overriden.FluentWindowConditions;

public final class WindowManager extends Manager {
	private final FluentWindowConditions fluent;
	void changeActive(String handle)
			throws NoSuchWindowException, UnhandledAlertException {
		Set<String> handles = getHandles();
		if (!handles.contains(handle)) {
			throw new NoSuchWindowException("There is no window with handle "
					+ handle + "!");
		}
		try {
			getWrappedDriver().switchTo().window(handle);
		} catch (UnhandledAlertException | NoSuchWindowException e) {
			throw e;
		}
	}
	
	private WindowsTimeOuts getWindowTimeOuts() {
		return driverEncapsulation.configuration.getBrowserWindowsTimeOuts();
	}

	public WindowManager(WebDriverEncapsulation initialDriverEncapsulation) {
		super(initialDriverEncapsulation);
		fluent = new FluentWindowConditions(getWrappedDriver());
	}

	@Override
	/**
	 * returns window handle by it's index
	 */
	String getHandleByInex(int windowIndex)
			throws NoSuchWindowException {
		try {
			Log.debug("Attempt to get window that is specified by index "
					+ Integer.toString(windowIndex) + "...");
			WindowsTimeOuts timeOuts = getWindowTimeOuts();
			long timeOut = getTimeOut(timeOuts.getWindowCountTimeOutSec(),
					defaultTime);
			return awaiting.awaitCondition(timeOut, 100, 
					fluent.suchWindowWithIndexIsPresent(windowIndex));
		} catch (TimeoutException e) {
			throw new NoSuchWindowException("Can't find window! Index out of bounds! Specified index is "
							+ Integer.toString(windowIndex)
							+ " is more then actual window count", e);
		}
	}

	@Override
	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time
	 */
	String switchToNew(long timeOutInSeconds)
			throws NoSuchWindowException {
		try {
			Log.debug("Waiting a new window for "
					+ Long.toString(timeOutInSeconds) + " seconds.");
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100,
					fluent.newWindowIsAppeared());
			changeActive(newHandle);
			return newHandle;
		} catch (TimeoutException e) {
			throw new NoSuchWindowException(
					"There is no new window! We have been waiting for "
							+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}

	@Override
	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration
	 */
	String switchToNew() throws NoSuchWindowException {
		WindowsTimeOuts timeOuts = getWindowTimeOuts();
		long timeOut = getTimeOut(
				timeOuts.getNewWindowTimeOutSec(),
				defaultTimeForNew);
		return switchToNew(timeOut);
	}

	@Override
	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time. new window should has defined title. We can specify title partially
	 * as a regular expression
	 */
	String switchToNew(long timeOutInSeconds, String title)
			throws NoSuchWindowException {
		try {
			Log.debug("Waiting a new window for "
					+ Long.toString(timeOutInSeconds)
					+ " seconds. New window should have title " + title);
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100,
					fluent.newWindowIsAppeared(title));
			changeActive(newHandle);
			return newHandle;
		} catch (TimeoutException e) {
			throw new NoSuchWindowException(
					"There is no new window with title " + title
							+ " ! We have been waiting for "
							+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}

	@Override
	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration. new window should has defined title. We can
	 * specify title partially
	 * as a regular expression
	 */
	String switchToNew(String title) throws NoSuchWindowException {
		WindowsTimeOuts timeOuts = getWindowTimeOuts();
		long timeOut = getTimeOut(
				timeOuts.getNewWindowTimeOutSec(),
				defaultTimeForNew);
		return switchToNew(timeOut, title);
	}

	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time. new window should has page that loads by specified URLs. We can specify it as regular expression list
	 */
	String switchToNew(long timeOutInSeconds, List<String> urls)
			throws NoSuchWindowException {
		try {
			Log.debug("Waiting a new window for "
					+ Long.toString(timeOutInSeconds)
					+ " seconds. New window should have page "
					+ " that is loaded by specified URLs. Urls are "
					+ urls.toString());
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100,
					fluent.newWindowIsAppeared(urls));
			changeActive(newHandle);
			return newHandle;
		} catch (TimeoutException e) {
			throw new NoSuchWindowException(
					"There is no new window that loads by " + urls.toString()
							+ " ! We have been waiting for "
							+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}

	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration. new window should has page that loads by
	 * specified URL. We can specify it as regular expression list
	 */
	String switchToNew(List<String> urls) throws NoSuchWindowException {
		WindowsTimeOuts timeOuts = getWindowTimeOuts();
		long timeOut = getTimeOut(
				timeOuts.getNewWindowTimeOutSec(),
				defaultTimeForNew);
		return switchToNew(timeOut, urls);
	}

	public synchronized String getWindowURLbyHandle(String handle)
			throws NoSuchWindowException {
		changeActive(handle);
		return (getWrappedDriver().getCurrentUrl());
	}

	public synchronized String getTitleByHandle(String handle)
			throws NoSuchWindowException {
		changeActive(handle);
		return (getWrappedDriver().getTitle());
	}

	public synchronized void close(String handle)
			throws UnclosedWindowException, NoSuchWindowException,
			UnhandledAlertException, UnreachableBrowserException {
		WindowsTimeOuts timeOuts = getWindowTimeOuts();
		long timeOut = getTimeOut(
				timeOuts.getWindowClosingTimeOutSec(),
				defaultTime);

		try {
			changeActive(handle);
			WebDriver driver = getWrappedDriver();
			driver.switchTo().window(handle).close();
		} catch (UnhandledAlertException | NoSuchWindowException e) {
			throw e;
		}

		try {
			awaiting.awaitCondition(timeOut, fluent.isClosed(handle));
		} catch (TimeoutException e) {
			throw new UnclosedWindowException("Window hasn't been closed!", e);
		}

		int actualWinCount = 0;
		try {
			actualWinCount = getHandles().size();
		} catch (WebDriverException e) { // if all windows are closed
			actualWinCount = 0;
		} finally {
			if (actualWinCount == 0) {
				destroy();
				driverEncapsulation.destroy();
			}
		}
	}

	public Set<String> getHandles() {
		return (getWrappedDriver().getWindowHandles());
	}

	public synchronized Alert getAlert() throws NoAlertPresentException {
		WindowsTimeOuts timeOuts = getWindowTimeOuts();
		return ComponentFactory.getComponent(AlertHandler.class,
				getWrappedDriver(), new Class[] {long.class}, new Object[] {getTimeOut(timeOuts.
						getSecsForAwaitinAlertPresent(),
								defaultTime) });

	}
	
	public synchronized Alert getAlert(long timeOut) throws NoAlertPresentException {
		return  ComponentFactory.getComponent(AlertHandler.class,
				getWrappedDriver(), new Class[] {long.class}, new Object[] {timeOut});
	}

	/**
	 * returns window handle by it's index
	 */
	@Override
	public synchronized Handle getByInex(int index) {
		String handle = this.getHandleByInex(index);
		SingleWindow initedWindow = (SingleWindow) SingleWindow.isInitiated(handle, this);
		if (initedWindow != null) {
			return (initedWindow);
		}
		return (new SingleWindow(handle, this));
	}

	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration
	 */
	@Override
	public synchronized Handle getNewHandle() {
		return new SingleWindow(switchToNew(), this);
	}

	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time
	 */
	@Override
	public  synchronized Handle getNewHandle(long timeOutInSeconds) {
		return new SingleWindow(switchToNew(timeOutInSeconds), this);
	}

	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time. new window should has defined title. We can specify title partially
	 * as a regular expression
	 */
	@Override
	public  synchronized Handle getNewHandle(long timeOutInSeconds, String title) {
		return new SingleWindow(switchToNew(timeOutInSeconds, title), this);
	}

	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration. new window should has defined title. We can
	 * specify title partially
	 * as a regular expression
	 */
	@Override
	public synchronized Handle getNewHandle(String title) {
		return new SingleWindow(switchToNew(title), this);
	}
	
	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time. new window should has page that loads by specified URLs. We can specify it as regular expression list
	 */
	public synchronized Handle getNewHandle(long timeOutInSeconds, List<String> urls) {
		return new SingleWindow(switchToNew(timeOutInSeconds, urls), this);
	}

	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration. new window should has page that loads by
	 * specified URL. We can specify it as regular expression list
	 */
	public synchronized Handle getNewHandle(List<String> urls) {
		return new SingleWindow(switchToNew(urls), this);
	}
}