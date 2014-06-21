package org.primitive.webdriverencapsulations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.primitive.interfaces.IDestroyable;
import org.primitive.logging.Photographer;
import org.primitive.webdriverencapsulations.components.bydefault.AlertHandler;
import org.primitive.webdriverencapsulations.components.bydefault.ComponentFactory;
import org.primitive.webdriverencapsulations.components.overriden.Awaiting;
import org.primitive.webdriverencapsulations.interfaces.IHasHandle;

public abstract class Manager implements IDestroyable {

	final Awaiting awaiting;
	final WebDriverEncapsulation driverEncapsulation;
	boolean isAlive = true;
	private final HandleReceptionist handleReceptionist = new HandleReceptionist();
	private final static List<Manager> managerList = Collections
	.synchronizedList(new ArrayList<Manager>());
	
	final static long defaultTime = 1; // default time we waiting for anything
    final static long defaultTimeForNew = 30; // we will wait
    // appearance of a new handle for 30 seconds by default

	static long getTimeOut(Long possibleTimeOut, long defaultValue) {
		if (possibleTimeOut == null) {
			return defaultValue;
		} else {
			return possibleTimeOut;
		}
	}
    
	Manager(WebDriverEncapsulation initialDriverEncapsulation) {
		driverEncapsulation = initialDriverEncapsulation;
		awaiting = driverEncapsulation.getAwaiting();
		managerList.add(this);
	}

	@Override
	public void destroy(){
		managerList.remove(this);
		isAlive = false;

		List<IHasHandle> toBeDestroyed = handleReceptionist.getInstantiated();
		for (IHasHandle hasHandle : toBeDestroyed) {
			((IDestroyable) hasHandle).destroy();
		}
	}
	
	abstract void changeActive(String handle);
	
	synchronized void takeAPictureOfAFine(String handle,
			String Comment) {
		changeActive(handle);
		Photographer.takeAPictureOfAFine(
				driverEncapsulation.getWrappedDriver(), Comment);
	}

	synchronized void takeAPictureOfAnInfo(String handle,
			String Comment) {
		changeActive(handle);
		Photographer.takeAPictureOfAnInfo(
				driverEncapsulation.getWrappedDriver(), Comment);
	}

	synchronized void takeAPictureOfASevere(String handle,
			String Comment) {
		changeActive(handle);
		Photographer.takeAPictureOfASevere(
				driverEncapsulation.getWrappedDriver(), Comment);
	}

	synchronized void takeAPictureOfAWarning(String handle,
			String Comment) {
		changeActive(handle);
		Photographer.takeAPictureOfAWarning(
				driverEncapsulation.getWrappedDriver(), Comment);
	}

	WebDriverEncapsulation getWebDriverEncapsulation(){
		return driverEncapsulation;
	}

	WebDriver getWrappedDriver() {
		return driverEncapsulation.getWrappedDriver();
	}

	boolean isAlive() {
		return isAlive;
	}

	synchronized void switchTo(String Handle){
		changeActive(Handle);
	}
	abstract String getHandleByIndex(int index);
	abstract  Set<String> getHandles();
	
	abstract String switchToNew();
	abstract String switchToNew(long timeOutInSeconds);
	abstract String switchToNew(long timeOutInSeconds, String stringIdentifier);
	abstract String switchToNew(String stringIdentifier);
	
	public abstract Handle getByIndex(int index);
	public abstract Handle getNewHandle();
	public abstract Handle getNewHandle(long timeOutInSeconds);
	public abstract Handle getNewHandle(long timeOutInSeconds, String stringIdentifier);
	public abstract Handle getNewHandle(String stringIdentifier);

	HandleReceptionist getHandleReceptionist(){
		return handleReceptionist;
	}

	public synchronized Alert getAlert(long timeOut) throws NoAlertPresentException {
		return  ComponentFactory.getComponent(AlertHandler.class,
				getWrappedDriver(), new Class[] {long.class}, new Object[] {timeOut});
	}
	
	public abstract Alert getAlert() throws NoAlertPresentException;
}
