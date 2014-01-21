package org.primitive.webdriverencapsulations;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
import org.primitive.interfaces.IDestroyable;
import org.primitive.logging.Log;
import org.primitive.logging.Photographer;
import org.primitive.webdriverencapsulations.webdrivercomponents.AlertHandler;
import org.primitive.webdriverencapsulations.webdrivercomponents.Awaiting;
import org.primitive.webdriverencapsulations.webdrivercomponents.FluentWindowConditions;

public final class WindowSwitcher implements IDestroyable {
	protected final WebDriverEncapsulation driverEncapsulation;
	private final static List<WindowSwitcher> swithcerList = Collections
			.synchronizedList(new ArrayList<WindowSwitcher>());
	private WindowTimeOuts windowTimeOuts;
	private Awaiting awaiting;
	private FluentWindowConditions fluent;
	final List<SingleWindow> openedWindows = Collections.synchronizedList(new ArrayList<SingleWindow>());
	private boolean isAlive = true;

	private void changeActiveWindow(String handle)
			throws NoSuchWindowException, UnhandledAlertException {
		Set<String> handles = getWindowHandles();
		if (!handles.contains(handle)) {
			throw new NoSuchWindowException("There is no window with handle "
					+ handle + "!");
		}
		try {
			driverEncapsulation.getWrappedDriver().switchTo().window(handle);
		} catch (UnhandledAlertException | NoSuchWindowException e) {
			throw e;
		}
	}

	/**
	 * returns WindowSwither instance that exists or creates a new instance
	 */
	public static WindowSwitcher get(WebDriverEncapsulation driver) {
		for (WindowSwitcher switcher : swithcerList) {
			if (switcher.driverEncapsulation == driver) {
				return switcher;
			}
		}

		return new WindowSwitcher(driver);
	}

	private WindowSwitcher(WebDriverEncapsulation InitialDriverPerformance) {
		driverEncapsulation = InitialDriverPerformance;
		windowTimeOuts = driverEncapsulation.getWindowTimeOuts();
		awaiting = driverEncapsulation.getAwaiting();
		fluent = new FluentWindowConditions(driverEncapsulation.getWrappedDriver());
		swithcerList.add(this);
	}

	/**
	 * returns window handle by it's index
	 */
	public String getWindowHandleByInex(int windowIndex)
			throws NoSuchWindowException {
		try {
			Log.debug("Attempt to get window that is specified by index "
					+ Integer.toString(windowIndex) + "...");
			WindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
			long timeOut = windowTimeOuts.getTimeOut(timeOuts.getWindowCountTimeOutSec(),
					windowTimeOuts.defaultTime);
			return awaiting.awaitCondition(timeOut, 100, 
					fluent.suchWindowWithIndexIsPresent(windowIndex));
		} catch (TimeoutException e) {
			throw new NoSuchWindowException("Can't find window! Index out of bounds! Specified index is "
							+ Integer.toString(windowIndex)
							+ " is more then actual window count", e);
		}
	}

	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time
	 */
	public synchronized String switchToNewWindow(long timeOutInSeconds)
			throws NoSuchWindowException {
		try {
			Log.debug("Waiting a new window for "
					+ Long.toString(timeOutInSeconds) + " seconds.");
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100,
					fluent.newWindowIsAppeared());
			changeActiveWindow(newHandle);
			return newHandle;
		} catch (TimeoutException e) {
			throw new NoSuchWindowException(
					"There is no new window! We have been waiting for "
							+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}

	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration
	 */
	public String switchToNewWindow() throws NoSuchWindowException {
		WindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		long timeOut = windowTimeOuts.getTimeOut(
				timeOuts.getNewWindowTimeOutSec(),
				windowTimeOuts.defaultTimeForNewWindow);
		return switchToNewWindow(timeOut);
	}

	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time new window should has defined title. We can specify title in this
	 * way: title, title*, *title, *title*, ti*tle and another regular
	 * expressions
	 */
	public synchronized String switchToNewWindow(long timeOutInSeconds, String title)
			throws NoSuchWindowException {
		try {
			Log.debug("Waiting a new window for "
					+ Long.toString(timeOutInSeconds)
					+ " seconds. New window should have title " + title);
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100,
					fluent.newWindowIsAppeared(title));
			changeActiveWindow(newHandle);
			return newHandle;
		} catch (TimeoutException e) {
			throw new NoSuchWindowException(
					"There is no new window with title " + title
							+ " ! We have been waiting for "
							+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}

	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration new window should has defined title. We can
	 * specify title in this way: title, title*, *title, *title*
	 */
	public String switchToNewWindow(String title) throws NoSuchWindowException {
		WindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		long timeOut = windowTimeOuts.getTimeOut(
				timeOuts.getNewWindowTimeOutSec(),
				windowTimeOuts.defaultTimeForNewWindow);
		return switchToNewWindow(timeOut, title);
	}

	/**
	 * returns handle of a new window that we have been waiting for specified
	 * time new window should has page that loads by specified URL
	 */
	public synchronized String switchToNewWindow(long timeOutInSeconds, URL url)
			throws NoSuchWindowException {
		try {
			Log.debug("Waiting a new window for "
					+ Long.toString(timeOutInSeconds)
					+ " seconds. New window should have page "
					+ " that is loaded by specified URL. Url is "
					+ url.toString());
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100,
					fluent.newWindowIsAppeared(url));
			changeActiveWindow(newHandle);
			return newHandle;
		} catch (TimeoutException e) {
			throw new NoSuchWindowException(
					"There is no new window that loads by " + url.toString()
							+ " ! We have been waiting for "
							+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}

	/**
	 * returns handle of a new window that we have been waiting for time that
	 * specified in configuration new window should has page that loads by
	 * specified URL
	 */
	public String switchToNewWindow(URL url) throws NoSuchWindowException {
		WindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		long timeOut = windowTimeOuts.getTimeOut(
				timeOuts.getNewWindowTimeOutSec(),
				windowTimeOuts.defaultTimeForNewWindow);
		return switchToNewWindow(timeOut, url);
	}

	synchronized void switchTo(String Handle)
			throws NoSuchWindowException {
		changeActiveWindow(Handle);
	}

	public synchronized String getWindowURLbyHandle(String handle)
			throws NoSuchWindowException {
		changeActiveWindow(handle);
		return (driverEncapsulation.getWrappedDriver().getCurrentUrl());
	}

	public synchronized String getTitleByHandle(String handle)
			throws NoSuchWindowException {
		changeActiveWindow(handle);
		return (driverEncapsulation.getWrappedDriver().getTitle());
	}

	public synchronized void destroy() {
		swithcerList.remove(this);
		isAlive = false;

		List<SingleWindow> windowsToBeDestroyed = new ArrayList<SingleWindow>();
		windowsToBeDestroyed.addAll(openedWindows);
		for (SingleWindow window : windowsToBeDestroyed) {
			window.destroy();
		}
		// removes all windows that was generated by this switcher
		openedWindows.clear();
	}

	public synchronized void close(String handle)
			throws UnclosedWindowException, NoSuchWindowException,
			UnhandledAlertException, UnreachableBrowserException {
		WindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		long timeOut = windowTimeOuts.getTimeOut(
				timeOuts.getWindowClosingTimeOutSec(),
				windowTimeOuts.defaultTime);

		try {
			changeActiveWindow(handle);
			WebDriver driver = driverEncapsulation.getWrappedDriver();
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
			actualWinCount = getWindowHandles().size();
		} catch (WebDriverException e) { // if all windows are closed
			actualWinCount = 0;
		} finally {
			if (actualWinCount == 0) {
				destroy();
				driverEncapsulation.destroy();
			}
		}
	}

	public Set<String> getWindowHandles() {
		return (driverEncapsulation.getWrappedDriver().getWindowHandles());
	}

	protected synchronized void takeAPictureOfASevere(String handle,
			String Comment) {
		changeActiveWindow(handle);
		Photographer.takeAPictureOfASevere(
				driverEncapsulation.getWrappedDriver(), Comment);
	}

	protected synchronized void takeAPictureOfAWarning(String handle,
			String Comment) {
		changeActiveWindow(handle);
		Photographer.takeAPictureOfAWarning(
				driverEncapsulation.getWrappedDriver(), Comment);
	}

	protected synchronized void takeAPictureOfAnInfo(String handle,
			String Comment) {
		changeActiveWindow(handle);
		Photographer.takeAPictureOfAnInfo(
				driverEncapsulation.getWrappedDriver(), Comment);
	}

	protected synchronized void takeAPictureOfAFine(String handle,
			String Comment) {
		changeActiveWindow(handle);
		Photographer.takeAPictureOfAFine(
				driverEncapsulation.getWrappedDriver(), Comment);
	}

	public synchronized Alert getAlert() throws NoAlertPresentException {
		WindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		return (new AlertHandler(driverEncapsulation.getWrappedDriver(), 
				windowTimeOuts.getTimeOut(timeOuts.getSecsForAwaitinAlertPresent(), windowTimeOuts.defaultTime)));
	}
	
	public synchronized Alert getAlert(long timeOut) throws NoAlertPresentException {
		return (new AlertHandler(driverEncapsulation.getWrappedDriver(), timeOut));
	}

	boolean isAlive() {
		return isAlive;
	}
}