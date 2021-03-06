/**
 * 
 */
package org.primitive.model.common;

import java.awt.Color;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.primitive.model.abstraction.ModelObject;
import org.primitive.model.interfaces.IDecomposable;
import org.primitive.model.interfaces.IHasWebElementFrames;
import org.primitive.webdriverencapsulations.Handle;
import org.primitive.webdriverencapsulations.components.bydefault.Interaction;
import org.primitive.webdriverencapsulations.components.overriden.FrameSupport;
import org.primitive.webdriverencapsulations.components.overriden.Ime;
import org.primitive.webdriverencapsulations.components.overriden.PageFactoryWorker;
import org.primitive.webdriverencapsulations.interfaces.ISwitchesToItself;
import org.primitive.webdriverencapsulations.interfaces.ITakesPictureOfItSelf;
import org.primitive.webdriverencapsulations.interfaces.IWebElementHighlighter;

/**
 * @author s.tihomirov It describes simple web page or mobile app context or their fragment
 */
public abstract class FunctionalPart extends ModelObject implements
		IHasWebElementFrames, ITakesPictureOfItSelf, ISwitchesToItself {

	/**
	 * @author s.tihomirov
	 * 
	 */
	@Target(value = ElementType.METHOD)
	@Retention(value = RetentionPolicy.RUNTIME)
	public static @interface InteractiveMethod {

	}

	protected final PageFactoryWorker pageFactoryWorker;
	protected FunctionalPart parent; // parent test object
	protected final FrameSupport frameSupport;
	// Integer specification of a frame that object is placed on.
	private Integer frameToSwitchOnInt = null;
	// String specification of a frame that object is placed on.
	private String frameToSwitchOnStr = null;
	// WebElement specification of a frame that object is placed on
	private Object frameToSwitchOnElem = null;
	// page object is created by specified entity
	protected Application application;
	private IWebElementHighlighter highLighter;
	protected final Interaction interaction;
	protected final Ime ime;

	/** switches to object
	* this method can be overridden
	*/
	@Override
	public synchronized void switchToMe() {
		// firstly we should switch parent browser window on
		if (parent != null) {
			parent.switchToMe();
		} else {
			handle.switchToMe();
		}
		// if this object is placed on some frame
		if (frameToSwitchOnInt != null) { // we should switch to it
			frameSupport.switchTo(frameToSwitchOnInt);
			return;
		}
		if (frameToSwitchOnStr != null) {
			frameSupport.switchTo(frameToSwitchOnStr);
			return;
		}
		if (frameToSwitchOnElem != null) {
			frameSupport.switchTo((WebElement) frameToSwitchOnElem);
			return;
		}
		return;
	}

	@Override
	protected final void addChild(ModelObject child) {
		super.addChild(child);
		FunctionalPart childPart = (FunctionalPart) child;
		childPart.parent = this;
		childPart.application = this.application;
	}

	protected FunctionalPart(Handle handle){
		super(handle);
		pageFactoryWorker = driverEncapsulation.getPageFactoryWorker();
		frameSupport = driverEncapsulation.getFrameSupport();
		highLighter = driverEncapsulation.getHighlighter();
		interaction = driverEncapsulation.getInteraction();
		ime = driverEncapsulation.getIme();
	}

	// constructs from another page object
	protected FunctionalPart(FunctionalPart parent){
		this(parent.handle);
		parent.addChild(this);
	}

	// constructor with specified integer frame value
	protected FunctionalPart(Handle handle, Integer frameIndex) {
		this(handle);
		frameToSwitchOnInt = frameIndex;
	}

	// constructs from another page object
	protected FunctionalPart(FunctionalPart parent, Integer frameIndex){
		this(parent.handle, frameIndex);
		parent.addChild(this);
	}

	// constructor with specified string frame value. pathToFrame can be
	// relative to another frame
	protected FunctionalPart(Handle handle, String pathToFrame) {
		this(handle);
		frameToSwitchOnStr = pathToFrame;
	}

	// constructs from another page object
	protected FunctionalPart(FunctionalPart parent, String pathToFrame){
		this(parent.handle, pathToFrame);
		parent.addChild(this);
	}

	// constructor with specified WebElement frame value.
	protected FunctionalPart(Handle handle, WebElement frameElement) {
		this(handle);
		frameToSwitchOnElem = frameElement;
	}

	// constructs from another page object
	protected FunctionalPart(FunctionalPart parent, WebElement frameElement){
		this(parent.handle, frameElement);
		parent.addChild(this);
	}

	// Methods that you see below can be used for loading of a page object
	// This method loads page object with the list of field decorators
	protected void load(ArrayList<FieldDecorator> decorators) {
		for (FieldDecorator decorator : decorators) { // decorators that are
														// given from outside or
														// generated inside
			pageFactoryWorker.initPageFactory(decorator, this);
		}
	}

	// This method loads page object with the list of ElementLocatorFactory
	// implementations
	protected void load(List<ElementLocatorFactory> factories) {
		for (ElementLocatorFactory factory : factories) { // factory that are
															// given from
															// outside or
															// generated inside
			pageFactoryWorker.initPageFactory(factory, this);
		}
	}

	// The method below simply loads page factory
	protected void load() {
		pageFactoryWorker.initPageFactory(this);
	}

	// - simple constructor
	@Override
	public <T extends IDecomposable> T getPart(Class<T> partClass) {
		Class<?>[] params = new Class[] {FunctionalPart.class};
		Object[] values = new Object[] {this};
		return DefaultApplicationFactory.get(partClass, params,
				values);
	}

	// - with specified frame index
	@Override
	public <T extends IDecomposable> T getPart(Class<T> partClass,
			Integer frameIndex)  {
		Class<?>[] params = new Class[] {FunctionalPart.class, Integer.class};
		Object[] values = new Object[] {this, frameIndex};
		return DefaultApplicationFactory.get(partClass, params,
				values);
	}

	// - with specified path to any frame
	@Override
	public <T extends IDecomposable> T getPart(Class<T> partClass,
			String pathToFrame) {
		Class<?>[] params = new Class[] {FunctionalPart.class,  String.class };
		Object[] values = new Object[] {this, pathToFrame };
		return DefaultApplicationFactory.get(partClass, params,
				values);
	}

	// - with frame that specified as web element
	@Override
	public <T extends IDecomposable> T getPart(Class<T> partClass,
			WebElement frameElement)  {
		Class<?>[] params = new Class[] {FunctionalPart.class, WebElement.class };
		Object[] values = new Object[] {this, frameElement };
		return DefaultApplicationFactory.get(partClass, params,
				values);
	}

	/**
	if handle disappeared
	all objects that are placed on this will be destroyed. I think it
	should work this way
	 */
	@Override
	public void destroy() {
		if (!handle.exists()) {
			handle.destroy();
		}
		super.destroy();
		return;
	}

	/**
	 *  takes screenshots for log messages with SEVERE level
	 */
	@Override
	public void takeAPictureOfASevere(String comment) {
		handle.takeAPictureOfASevere(comment);
	}

	@InteractiveMethod
	public void highlightAsSevere(WebElement element, String comment) {
		highLighter.highlightAsSevere(driverEncapsulation.getWrappedDriver(),
				element, comment);
	}

	@InteractiveMethod
	public void highlightAsSevere(WebElement element, Color highlight,
			String comment) {
		highLighter.highlightAsSevere(driverEncapsulation.getWrappedDriver(),
				element, highlight, comment);
	}

	/**
	 *  takes screenshots for log messages with WARNING level
	 */
	@Override
	public void takeAPictureOfAWarning(String comment) {
		handle.takeAPictureOfAWarning(comment);
	}

	@InteractiveMethod
	public void highlightAsWarning(WebElement element, String comment) {
		highLighter.highlightAsWarning(driverEncapsulation.getWrappedDriver(),
				element, comment);
	}

	@InteractiveMethod
	public void highlightAsWarning(WebElement element, Color highlight,
			String comment) {
		highLighter.highlightAsWarning(driverEncapsulation.getWrappedDriver(),
				element, highlight, comment);
	}

	/**
	 *  takes screenshots for log messages with INFO level
	 */
	@Override
	public void takeAPictureOfAnInfo(String comment) {
		handle.takeAPictureOfAnInfo(comment);
	}

	@InteractiveMethod
	public void highlightAsInfo(WebElement element, String comment) {
		highLighter.highlightAsInfo(driverEncapsulation.getWrappedDriver(),
				element, comment);
	}

	@InteractiveMethod
	public void highlightAsInfo(WebElement element, Color highlight,
			String comment) {
		highLighter.highlightAsInfo(driverEncapsulation.getWrappedDriver(),
				element, highlight, comment);
	}

	/**
	 *  takes screenshots for log messages with FINE level
	 */
	@Override
	public void takeAPictureOfAFine(String comment) {
		handle.takeAPictureOfAFine(comment);
	}

	@InteractiveMethod
	public void highlightAsFine(WebElement element, String comment) {
		highLighter.highlightAsFine(driverEncapsulation.getWrappedDriver(),
				element, comment);
	}

	@InteractiveMethod
	public void highlightAsFine(WebElement element, Color highlight,
			String comment) {
		highLighter.highlightAsFine(driverEncapsulation.getWrappedDriver(),
				element, highlight, comment);
	}
}
