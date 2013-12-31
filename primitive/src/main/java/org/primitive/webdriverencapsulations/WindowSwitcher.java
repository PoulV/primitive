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
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.primitive.configuration.Configuration.BrowserWindowsTimeOuts;
import org.primitive.exceptions.UnclosedBrowserWindowException;
import org.primitive.interfaces.IDestroyable;
import org.primitive.logging.Log;
import org.primitive.logging.Photographer;
import org.primitive.webdriverencapsulations.webdrivercomponents.AlertHandler;
import org.primitive.webdriverencapsulations.webdrivercomponents.Awaiting;


public final class WindowSwitcher implements IDestroyable
{
	/**
	 * @author s.tihomirov
	 * Fluent waiting of browser window conditions
	 */
	protected static class Fluent implements IDestroyable{
		private WindowSwitcher switcher;
		protected Fluent(WindowSwitcher switcher) {
			this.switcher = switcher;
		}
		
		//is there such window count? It it is true this method returns handle of window 
		private String getWindowHandleByIndex(final WebDriver from, int windowIndex)
		{

			synchronized(switcher)
			{
				Set<String> handles =  from.getWindowHandles();
				if ((handles.size()-1)>=windowIndex)
				{
					Log.message("Browser window that specified by index " + Integer.toString(windowIndex) + " is present");
					return new ArrayList<String>(handles).get(windowIndex);
				}
				else
				{
					return null;
				}	
			}
		}
		
		//fluent waiting of the result. See above
		protected ExpectedCondition<String> suchBrowserhWindowWithIndexIsPresent(final int windowIndex)
		{
			return new ExpectedCondition<String>()
			{ 				
				public String apply(final WebDriver from)
				{
					return getWindowHandleByIndex(from, windowIndex);		
				}
			};
		}
		
		//is browser window closed?
		private Boolean isClosed(final WebDriver from, String handle)
		{
			synchronized (switcher) 
			{
				Set<String> handles = from.getWindowHandles();
				if (!handles.contains(handle))
				{
					Log.message("Browser window has been closed successfully!");
					return true;
				}
				else
				{
					return null;
				}		
			}					
		}
		
		//fluent waiting of the result. See above
		protected ExpectedCondition<Boolean> isClosed(final String closingHandle)
		{
			return new ExpectedCondition<Boolean>()
			{
				public Boolean apply(final WebDriver from)
				{
					return isClosed(from, closingHandle);
				}
			};	
		}
		
		//is here new browser window?
		//returns handle of a new browser window that we have been waiting for specified time
		private String getNewHandle(final WebDriver from, final Set<String> oldHandles)
		{
			synchronized (switcher) 
			{
				String newHandle = null;
				Set<String> newHandles = from.getWindowHandles();
				if (newHandles.size()>oldHandles.size())
				{
					for (String handle:newHandles)
					{
						if (!oldHandles.contains(handle)&(!handle.equals("")))
						{
							newHandle = handle;
							Log.message("New browser window is caught!");
							return newHandle;
						}
					}
				}
				return newHandle;
			}

		}
		
		//fluent waiting of the result. See above
		protected ExpectedCondition<String> newWindowIsAppeared()
		{
			return new ExpectedCondition<String>()
			{ 
				Set<String> oldHandles = switcher.getWindowHandles();
				public String apply(final WebDriver from)
				{
					return getNewHandle(from, oldHandles);
				}
			};
		}
		
		//is here new browser window?
		//returns handle of a new browser window that we have been waiting for specified time
		//new browser window should have defined title. We can specify title in this way:
		//title, title*, *title, *title*
		private String getNewHandle(final WebDriver from, final Set<String> oldHandles, String title)
		{
			synchronized(switcher)
			{
				String containingTitle = title.replace("*", "");
				String newHandle = null;
				Set<String> newHandles = from.getWindowHandles();
				if (newHandles.size()>oldHandles.size())
				{
					for (String handle:newHandles)
					{
						if (!oldHandles.contains(handle))
						{
							from.switchTo().window(handle);
							if (from.getTitle().contains(containingTitle))
							{
								newHandle = handle;
								Log.message("New browser window with title " + title + "is caught!");
								return newHandle;
							}
						}
					}
				}
				return newHandle;
			}
		}	
		
		//fluent waiting of the result. See above
		protected ExpectedCondition<String> newWindowIsAppeared(final String title)
		{
			return new ExpectedCondition<String>()
			{ 
				Set<String> oldHandles = switcher.getWindowHandles();
				public String apply(final WebDriver from)
				{
					return getNewHandle(from, oldHandles, title);
				}
			};
		}
		
		//is here new browser window?
		//returns handle of a new browser window that we have been waiting for specified time
		//new browser window should have page that loads by specified URL
		private String getNewHandle(final WebDriver from, final Set<String> oldHandles, URL url)
		{
			synchronized (switcher) 
			{
				String newHandle = null;
				Set<String> newHandles = from.getWindowHandles();
				if (newHandles.size()>oldHandles.size())
				{
					for (String handle:newHandles)
					{
						if (!oldHandles.contains(handle))
						{
							from.switchTo().window(handle);
							if (from.getCurrentUrl().equals(url.toString()))
							{
								newHandle = handle;
								Log.message("New browser window that loaded by URL " + url.toString() + " is caught!");
								return newHandle;
							}
						}
					}
				}
				return newHandle;	
			}			
		}
		
		//fluent waiting of the result. See above
		protected ExpectedCondition<String> newWindowIsAppeared(final URL url)
		{
			return new ExpectedCondition<String>()
			{ 
				Set<String> oldHandles = switcher.getWindowHandles();
				public String apply(final WebDriver from)
				{
					return getNewHandle(from, oldHandles, url);
				}
			};
		}
		
		@Override
		public void destroy() {
			try 
			{
				this.finalize();
			} 
			catch (Throwable e) {
				Log.warning("There are some problems with finalizing of browser window fluent waiting.", e);
			}
			switcher = null;
			
		}

	}

	protected WebDriverEncapsulation driverEncapsulation;
	private final static List<WindowSwitcher> swithcerList = Collections.synchronizedList(new ArrayList<WindowSwitcher>());
	private WindowTimeOuts windowTimeOuts;
	private Awaiting awaiting;
	private Fluent fluent;
	final List<SingleWindow> openedWindows = Collections.synchronizedList(new ArrayList<SingleWindow>());
		
	private void changeActiveWindow(String handle) throws NoSuchWindowException, UnhandledAlertException
	{
		Log.debug("Attempt to switch browser window on by handle "+handle);
		Set<String> handles = getWindowHandles();
		if (!handles.contains(handle))
		{
			throw new NoSuchWindowException("There is no browser window with handle " + handle + "!");
		}	
		try
		{
			driverEncapsulation.getWrappedDriver().switchTo().window(handle);
		}
		catch (UnhandledAlertException e)
		{
			throw e;
		}
	}
	
	//returns WindowSwither instance that exists or creates a new instance 
	public static WindowSwitcher get(WebDriverEncapsulation driver)
	{
		for (WindowSwitcher switcher: swithcerList)
		{
			if (switcher.driverEncapsulation == driver)
			{
				return switcher;
			}
		}
		
		return new WindowSwitcher(driver);
	}
	
	private WindowSwitcher(WebDriverEncapsulation InitialDriverPerformance)
	{
		driverEncapsulation = InitialDriverPerformance;
		windowTimeOuts   = driverEncapsulation.getWindowTimeOuts();
		awaiting        = driverEncapsulation.getAwaiting();
		fluent          = new Fluent(this);
		swithcerList.add(this);
	}
	
	public String getBrowserWindowHandleByInex(int windowIndex) throws NoSuchWindowException
	{
		try
		{
			BrowserWindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
			long timeOut = windowTimeOuts.getTimeOut(timeOuts.getWindowCountTimeOutSec(),windowTimeOuts.defaultTime);
			return awaiting.awaitCondition(timeOut, 100, fluent.suchBrowserhWindowWithIndexIsPresent(windowIndex));
		}
		catch (TimeoutException e)
		{
			throw new NoSuchWindowException("Can't find browser window! Index out of bounds! Specified index is " + Integer.toString(windowIndex) + 
											" is more then actual window count", e);
		}
	}
	
	//returns handle of a new browser window that we have been waiting for specified time
	public String switchToNewBrowserWindow(long timeOutInSeconds) throws NoSuchWindowException
	{	
		try
		{
			Log.message("Waiting the new browser window for " + Long.toString(timeOutInSeconds) + " seconds ...");
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100, fluent.newWindowIsAppeared());
			synchronized (this) {
				changeActiveWindow(newHandle);
			}
			return newHandle;
		}
		catch (TimeoutException e)
		{
			throw new NoSuchWindowException("There is no new browser window! We have been waiting for "	+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}
	
	//returns handle of a new browser window that we have been waiting for time that specified in configuration 
	public String switchToNewBrowserWindow() throws NoSuchWindowException
	{	
		BrowserWindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		long timeOut = windowTimeOuts.getTimeOut(timeOuts.getNewBrowserWindowTimeOutSec(),windowTimeOuts.defaultTimeForNewWindow);
		return switchToNewBrowserWindow(timeOut);
	}	
	
	//returns handle of a new browser window that we have been waiting for specified time
	//new browser window should has defined title. We can specify title in this way:
	//title, title*, *title, *title*
	public String switchToNewBrowserWindow(long timeOutInSeconds, String title) throws NoSuchWindowException
	{	
		try
		{
			Log.message("Waiting the new browser window for " + Long.toString(timeOutInSeconds) + " seconds. New window should have title " + title);
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100, fluent.newWindowIsAppeared(title));
			synchronized (this) {
				changeActiveWindow(newHandle);
			}
			return newHandle;
		}
		catch (TimeoutException e)
		{
			throw new NoSuchWindowException("There is no new browser window with title " + title + 
					" ! We have been waiting for "	+ Long.toString(timeOutInSeconds) + " seconds", e);
		}
	}	
	
	//returns handle of a new browser window that we have been waiting for time that specified in configuration
	//new browser window should has defined title. We can specify title in this way:
	//title, title*, *title, *title*
	public String switchToNewBrowserWindow(String title) throws NoSuchWindowException
	{	
		BrowserWindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		long timeOut = windowTimeOuts.getTimeOut(timeOuts.getNewBrowserWindowTimeOutSec(),windowTimeOuts.defaultTimeForNewWindow);
		return switchToNewBrowserWindow(timeOut, title);
	}
	
	//returns handle of a new browser window that we have been waiting for specified time
	//new browser window should has page that loads by specified URL
	public String switchToNewBrowserWindow(long timeOutInSeconds, URL url) throws NoSuchWindowException
	{	
		try
		{
			Log.message("Waiting the new browser window for " + Long.toString(timeOutInSeconds) + " seconds. New window should have page " +  
					" that loads by specified URL. Url is " + url.toString());
			String newHandle = awaiting.awaitCondition(timeOutInSeconds, 100, fluent.newWindowIsAppeared(url));
			synchronized (this) {
				changeActiveWindow(newHandle);
			}
			return newHandle;
		}
		catch (TimeoutException e)
		{
			throw new NoSuchWindowException("There is no new browser window that loads by " + url.toString() + 
					" ! We have been waiting for "	+ Long.toString(timeOutInSeconds) + " seconds", e);
		}	
	}	
	
	//returns handle of a new browser window that we have been waiting for time that specified in configuration 
	//new browser window should has page that loads by specified URL
	public String switchToNewBrowserWindow(URL url) throws NoSuchWindowException
	{	
		BrowserWindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		long timeOut = windowTimeOuts.getTimeOut(timeOuts.getNewBrowserWindowTimeOutSec(),windowTimeOuts.defaultTimeForNewWindow);
		return switchToNewBrowserWindow(timeOut, url);
	}
	
	
	public synchronized void switchTo(String Handle) throws NoSuchWindowException
	{
		try
		{
			changeActiveWindow(Handle);			
		}
		catch (NoSuchWindowException e)
		{
			throw new NoSuchWindowException("Can't switch to inexisting browser window. Handle of inexisting window is " + Handle, e);
		}			
	}
	
	public synchronized String getWindowURLbyHandle(String handle) throws NoSuchWindowException
	{
		changeActiveWindow(handle);
		return(driverEncapsulation.getWrappedDriver().getCurrentUrl());
	}
	
	public synchronized String getTitleByHandle(String handle)  throws NoSuchWindowException
	{
		changeActiveWindow(handle);
		return (driverEncapsulation.getWrappedDriver().getTitle());
	}
	
	public synchronized void destroy()
	{		
		swithcerList.remove(this);
		fluent.destroy();		
		List<SingleWindow> windowsToBeDestroyed = new ArrayList<SingleWindow>();
		windowsToBeDestroyed.addAll(openedWindows);
		for (SingleWindow window: windowsToBeDestroyed)
		{
			window.destroy();
		}		
		//removes all windows that was generated by this switcher
		openedWindows.clear();		
		try 
		{
			this.finalize();
		} 
		catch (Throwable e) 
		{
			Log.warning("Some problem has occured while instance of window switcher was finalized! " + e.getMessage(),e);
		}
	}
	
	public synchronized void close(String handle) throws UnclosedBrowserWindowException, NoSuchWindowException, UnhandledAlertException, UnreachableBrowserException
	{
		BrowserWindowsTimeOuts timeOuts = windowTimeOuts.getTimeOuts();
		long timeOut = windowTimeOuts.getTimeOut(timeOuts.getWindowClosingTimeOutSec(),windowTimeOuts.defaultTime);
		try
		{
			changeActiveWindow(handle);
			WebDriver driver = driverEncapsulation.getWrappedDriver();	
			driver.switchTo().window(handle).close();
			awaiting.awaitCondition(timeOut, fluent.isClosed(handle));
		}
		catch (UnhandledAlertException|NoSuchWindowException e)
		{
			throw e;
		}
		catch (TimeoutException e) {
			throw new UnclosedBrowserWindowException(e);
		}
	}
	
	public Set<String> getWindowHandles()
	{
		try
		{	
			return(driverEncapsulation.getWrappedDriver().getWindowHandles());		
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	protected synchronized void takeAPictureOfASevere(String handle, String Comment)
	{
		changeActiveWindow(handle);
		Photographer.takeAPictureOfASevere(driverEncapsulation.getWrappedDriver(), Comment);	
	}
	
	protected synchronized void takeAPictureOfAWarning(String handle, String Comment)
	{
		changeActiveWindow(handle);
		Photographer.takeAPictureOfAWarning(driverEncapsulation.getWrappedDriver(), Comment);	
	}
	
	protected synchronized void takeAPictureOfAnInfo(String handle, String Comment)
	{
		changeActiveWindow(handle);
		Photographer.takeAPictureOfAnInfo(driverEncapsulation.getWrappedDriver(), Comment);
	}
	
	protected synchronized void takeAPictureOfAFine(String handle, String Comment)
	{
		changeActiveWindow(handle);
		Photographer.takeAPictureOfAFine(driverEncapsulation.getWrappedDriver(), Comment);	
	}	
	
	public synchronized Alert getAlert(long secsToWait) throws NoAlertPresentException
	{
		return(new AlertHandler(driverEncapsulation.getWrappedDriver(), secsToWait));
	}
	
	public synchronized Alert getAlert() throws NoAlertPresentException
	{
		return(new AlertHandler(driverEncapsulation.getWrappedDriver()));
	}
}
