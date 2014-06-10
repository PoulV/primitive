package org.primitive.webdriverencapsulations.eventlisteners;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.primitive.configuration.interfaces.IConfigurable;
import org.primitive.logging.Log;
import org.primitive.webdriverencapsulations.interfaces.IExtendedWindow;
import org.primitive.webdriverencapsulations.interfaces.IHasHandle;
import org.primitive.webdriverencapsulations.interfaces.ITakesPictureOfItSelf;

/**
* @author s.tihomirov
*Implementation of @link{IWindowListener} by default
*/
public class DefaultWindowListener extends DefaultHandleListener implements IWindowListener, IConfigurable{

	public DefaultWindowListener() {
		super();
	}
	
	private void postWindowUrl(IExtendedWindow window)	{
		Log.message("URL is " + window.getCurrentUrl());
	}

	@Override
	public void whenNewHandleIsAppeared(IHasHandle handle) {
		if (toDoScreenShotsOfNewWindows)
		{
			((ITakesPictureOfItSelf) handle).takeAPictureOfAnInfo("The new window");
		}
		
	}

	@Override
	public void beforeWindowIsClosed(IExtendedWindow window) {
		Log.message("Attempt to close window...");
		postWindowUrl(window);
	}

	@Override
	public void whenWindowIsClosed(IExtendedWindow window) {
		Log.message("Not any problem has occurred when window was closed...");					
	}

	@Override
	public void beforeIsSwitchedOn(IHasHandle handle) {
		Log.debug("Attempt to switch window on by handle "+ handle.getHandle());
	}

	@Override
	public void whenIsSwitchedOn(IHasHandle handle) {
		postWindowUrl((IExtendedWindow) handle);			
	}

	@Override
	public void beforeWindowIsMaximized(IExtendedWindow window) {
		Log.message("Attempt to maximize window");	
		postWindowUrl(window);
	}

	@Override
	public void whenWindowIsMaximized(IExtendedWindow window) {
		Log.message("Window has been maximized");			
	}

	@Override
	public void beforeWindowIsRefreshed(IExtendedWindow window) {
		Log.message("Attempt to refresh window");	
		postWindowUrl(window);
	}

	@Override
	public void whenWindowIsRefreshed(IExtendedWindow window) {
		Log.message("Current window has been refreshed");
		postWindowUrl(window);		
	}

	@Override
	public void beforeWindowIsMoved(IExtendedWindow window, Point point) {
		Log.message("Attempt to change window position. X " + Integer.toString(point.getX()) + 
				" Y " + Integer.toString(point.getY()));	
		postWindowUrl(window);
	}

	@Override
	public void whenWindowIsMoved(IExtendedWindow window, Point point) {
		Log.message("Window position has been changed to X " + Integer.toString(point.getX()) + 
				" Y " + Integer.toString(point.getY()));
		postWindowUrl(window);
	}

	@Override
	public void beforeWindowIsResized(IExtendedWindow window, Dimension dimension) {
		Log.message("Attempt to change window size. New height is " + Integer.toString(dimension.getHeight()) + 
				" new width is " + Integer.toString(dimension.getWidth()));
		postWindowUrl(window);
	}

	@Override
	public void whenWindowIsResized(IExtendedWindow window, Dimension dimension) {
		Log.message("Window size has been changed! New height is " + Integer.toString(dimension.getHeight()) + 
				" new width is " + Integer.toString(dimension.getWidth()));
		postWindowUrl(window);			
	}

}
